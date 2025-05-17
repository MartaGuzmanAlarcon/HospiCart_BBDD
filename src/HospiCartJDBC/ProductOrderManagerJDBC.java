package HospiCartJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartInterfaces.IProductOrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.ProductOrder;
import HospiCartPOJOs.Shipment;
import HospiCartPOJOs.Status;

/**
 * This class is the responsible for handling all the operations related product orders.
 * This includes finding product orders by their order id, product id or both, deleting product orders, updating existing product orders (changing the amount of a product and 
 * the price or deleting a product) or creating a new product order.
 * 
 * This class implements the interface "IProductOrderManager" and implements all of its methods. 
 */
public class ProductOrderManagerJDBC implements IProductOrderManager{
	
    private Connection c;
	private ConnectionManagerJDBC connectionManager;
	private ProductManagerJDBC productManager;
	//private OrderManager orderManager;
	/**
	 * Constructor
	 * @param cm object of "ConnectionManagerJDBC"
	 */
	public ProductOrderManagerJDBC(ConnectionManagerJDBC cm) {
		this.connectionManager = cm;
		this.c = cm.getConnection();
		this.productManager = new ProductManagerJDBC(cm);
		//this.orderManager = new OrderManager(cm);
		
	}
	
	/**
	 * Method that receives a product and an order id by parameter and adds the received product to the order that corresponds with the received order id. i.e: the method creates a new
	 * product order with the order and product IDs received as parameter.
	 * @param productOrder object of the class ProductOrder that store the product order that we want to insert to the database.
	 * @throws ClientException 
	 */
	@Override
	public void insertProductOrder(ProductOrder productOrder) throws SQLException, ClientException{
		// Retrieve the product and order from the productOrder
		Product product = productOrder.getProduct();
		Order order = productOrder.getOrder();
		
		// Check that product and order exist in the DB
//		if (order.getOrderId() == null) {
//            orderManager.insertOrder(order);
//        }
		
		if (product.getProductId() == null) {
            productManager.insertProduct(product);
        }
		
		// Compute the total price TODO REVISE THIS, WHY CAN WE OBTAIN DIRECTLY THE TOTAL PRICE WITH productOrder.getTotalPrice()?
		int amount = productOrder.getAmount();
		float totalPrice = productOrder.getTotalPrice();
				
		/*if(product.getProductId() == null) {
			pm.insertProduct(product);
		}*/

		//SQL query
		String sql = "INSERT INTO product_order (order_id, product_id, amount, total_price) VALUES (?, ?, ?, ?) ";
		
		// Prepare the statement in the try catch block
		try(PreparedStatement prep = c.prepareStatement(sql)){
			//removeProductFromStockQuantity(productOrder.getProduct().getProductId(), productOrder.getAmount()); //I remove the added product from the stock.
			//TODO is this ok? won't the stock be reduced twice? Because I am updating it here and it is also being updated in "reduceStock" (Marta's function)

			// Bind parameters
			prep.setInt(1, order.getOrderId());
			prep.setInt(2, productOrder.getProduct().getProductId());
			prep.setInt(3, productOrder.getAmount());
			prep.setFloat(4, totalPrice);
			
			// Execute insert
			int rowsAffected = prep.executeUpdate();
			if(rowsAffected > 0 ) {
				productManager.updateProductStock(product, productOrder.getAmount(), false);
			} else {
				throw new RuntimeException("An error occurred when updating the product order in the database.");
			}
			c.commit();
		}catch(SQLException e) {
			System.err.println("Error adding a product order: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that receives an order id and deletes the product orders associated to it. This method functions as a method that clears the product orders
	 * that belong to a specified order.
	 * @param order_id integer that stores the id of an order.
	 */
	@Override
	public void deleteProductOrdersByOrderID(int order_id) throws SQLException, OrderExceptions, ClientException{
		
		//SQL query
		String sql = "DELETE * FROM product_order WHERE order_id = ? ";
		
		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			//I get a list with the product orders associated to the order with the received order id and create a for loop in which, 
			//for each product order, I call the method that adds the products to the stock.
			List<ProductOrder> productOrdersOfOrder = getProductOrdersByOrderID(order_id);
			for(int i = 0; i < productOrdersOfOrder.size(); i++) {
				ProductOrder productOrder = productOrdersOfOrder.get(i);
				Product product = productOrder.getProduct();
				productManager.updateProductStock(product, productOrder.getAmount(), true);
			}
			
			stmt.setInt(1,  order_id);
			
			int rowsAffected = stmt.executeUpdate();
			if(rowsAffected == 0) {
				throw new RuntimeException("An error occurred when updating the product order in the database.");
			}
			//TODO HERE WE ARE NOT TAKING INTO ACCOUNT WHAT HAPPENS IF THE EXCECUTE UPDATE FAILS --> IF IT FAILS, WE WILL HAVE ALREADY MODIFIED THE STOCK
			c.commit();
		}catch(SQLException e) {
			System.err.println("Error deleting product orders by their order ID: " + e.getMessage());
            e.printStackTrace();
		}
	}

	/**
	 * Method that receives the id of a product and the id of an order as parameter and deletes the product order whose IDs match with the received ones.
	 * @param product_id integer that stores the id of a product.
	 * @param order_id integer that stores the id of an order.
	 */
	@Override
	public void deleteProductOrderByIDs(int product_id, int order_id) throws SQLException, OrderExceptions, ClientException{
					
		//SQL query
		String sql = "DELETE * FROM product_order WHERE order_id = ? AND product_id = ? ";
		
		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			ProductOrder productOrder = getProductOrderByIDs(product_id, order_id);
			
			stmt.setInt(1,  order_id);
			stmt.setInt(2, product_id);
			
			int rowsAffected = stmt.executeUpdate();
			
			if(rowsAffected > 0) {
				productManager.updateProductStock(productOrder.getProduct(), productOrder.getAmount(), true);
			} else {
				throw new RuntimeException("An error occurred when updating the product order in the database.");
			}
			c.commit();
		}catch(SQLException e) {
			System.err.println("Error deleting product orders by the product and order ID: " + e.getMessage());
		    e.printStackTrace();
		}
	}
	
	/**
	 * Method that receives the id of a product and the id of an order and returns the object of Product Order that corresponds with the received IDs.
	 * @param product_id integer that stores the id of a product.
	 * @param order_id integer that stores the id of an order.
	 * @return an object of the class product order whose id matches with the received one as parameter.
	 */
	@Override
	public ProductOrder getProductOrderByIDs(int product_id, int order_id) throws OrderExceptions, ClientException {
		ProductOrder productOrder = null;
	    
    	String sql = "SELECT * "
    			+ "FROM product_order AS po "
    			+ "WHERE po.order_id = ? AND po.product_id = ? ";
    	
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		stmt.setInt(2,  product_id);
    		
    		try(ResultSet resultSet = stmt.executeQuery()){
    			
    			if(resultSet.next()) {
    				
    				productOrder = new ProductOrder();
    				productOrder.setAmount(resultSet.getInt("amount"));
    				productOrder.setTotalPrice(resultSet.getFloat("total_price"));
    				
    				Order order = connectionManager.getOrderManager().getOrderByID(order_id);
    				productOrder.setOrder(order);
    				
    				Product product = connectionManager.getProductManager().getProductById(product_id);
    				productOrder.setProduct(product);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving product order: " + e.getMessage());
            e.printStackTrace();
    	}
        return productOrder;
	}

	/**
	 * Method that receives an order id as parameter and returns a list that contains all the product orders whose order id matches the received one.
	 * @param order_id integer that stores the id of an order.
	 * @return a list that contains objects of "ProductOrder".
	 */
	@Override
	public List<ProductOrder> getProductOrdersByOrderID(int order_id) throws OrderExceptions, ClientException {
		ProductOrder productOrder = null;
    	List<ProductOrder> productOrdersOfOrder = new ArrayList<>();
    	
    	String sql = "SELECT * "
    			+ "FROM product_order AS po "
    			+ "WHERE po.order_id = ? ";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		
    		try(ResultSet resultSet = stmt.executeQuery()){
                //While loop that iterates through all the result set and retrieves all the product orders.
    			while(resultSet.next()) {
    				
    				productOrder = new ProductOrder();
    				
    				//I set the fields of the product order object.
    				productOrder = new ProductOrder();
    				productOrder.setAmount(resultSet.getInt("amount"));
    				productOrder.setTotalPrice(resultSet.getFloat("total_price"));
    				
    				// Get the full Order object from OrderManager
    				//Order order = cm.getOrderManager().getOrderByID(order_id);
    				///productOrder.setOrder(order);
    				//TODO THE LINES ABOVE GENERATE STACK OVERFLOW
    				int product_id = resultSet.getInt("product_id");
    				Product product = connectionManager.getProductManager().getProductById(product_id);
    				productOrder.setProduct(product);
    				
    				//Finally, I add the created product order to the list of product orders associated to the received order id.
    				productOrdersOfOrder.add(productOrder);
    			}
    		}
    	}catch(SQLException e) {
    		System.err.println("Error retrieving product orders from an order: " + e.getMessage());
            e.printStackTrace();
    	}
        return productOrdersOfOrder;
	}
	
	/**
	 * Method that receives an order id and returns the total price of the order that corresponds with the receives id.
	 * @param order_id integer that stores the id of the order whose products we wish to obtain.
	 * @return a floating number that stores the total price of the order.
	 */
	@Override
	public double getTotalPriceOfAnOrder(int order_id) {
		//SQL query
		String sql = "SELECT SUM(total_price) FROM product_order WHERE order_id = ? ";
		//This variable will store the retrieved total price of the order.
		Double total_price = 0.0d;
		
		//I create the statement in the try catch block.
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  order_id);
			try(ResultSet rs = stmt.executeQuery()){
				if(rs.next()) {
					total_price = total_price + rs.getDouble(1);
				}
			}
		} catch(SQLException e) {
			System.err.println("Error retrieving the total price of an order: " + e.getMessage());
			e.printStackTrace();
		}
		return total_price;
	}

	/**
	 * Method that receives a product id and returns a list that contains all the orders that have the product passed as parameter.
	 * @param product_id integer that stores the id of the product of interest.
	 * @return a list containing instances of Order.
	 */
	@Override
	public List<Order> getOrdersWithAProduct(int product_id) throws OrderExceptions, ClientException{
		//SQL query
		String sql = "SELECT * FROM product_order WHERE product_id = ?";
		
		//List that will store the orders
		List<Order> ordersWithProduct = new LinkedList<>();
		//I create the statement in the try catch block.
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  product_id);
			
			try(ResultSet rs = stmt.executeQuery()){
                //While loop that iterates through all the result set and retrieves all the orders.
				while(rs.next()) {
					//I store the order id ina variable of type integer.
					int order_id = rs.getInt("order_id");
					//I retrieve the order that corresponds with the obtained order id using the respective method generated in "OrderManager"
					Order order = connectionManager.getOrderManager().getOrderByID(order_id);
					//I add the order to the list of orders.
					ordersWithProduct.add(order);
				}
			}
		} catch(SQLException e) {
			System.err.println("Error retrieving the total price of an order: " + e.getMessage());
			e.printStackTrace();
		}
		return ordersWithProduct;
	}
		
	/**
	 * Method that receives an order id, a product's id and a quantity. The method updates the amount of the received product for the quantity passed by parameter of the order that corresponds with the received order id.
	 * @param product_id integer that stores the id of the product whose quantity we wish to update.
	 * @param order_id integer that stores the id of the order which we wish to modify.
	 * @param product_amount integer that stores the amount we wish to order of the specified product.
	 */
	@Override
	public void updateProductAmountInAnOrder(int product_id, int order_id, int product_amount) throws SQLException, ClientException {
		//I get the product out of its ID
		Product product = connectionManager.getProductManager().getProductById(product_id);
		Float product_price = product.getPrice();
		//I update the total price of the product order by multiplying the price of the product by the amount ordered.
		Float updated_price = ((float) product_price * product_amount);
		
		ProductOrder productOrder = getProductOrderByIDs(product_id, order_id);
		
		//SQL query
		String sql = "UPDATE product_order SET amount = ?, total_price = ? "
				+ "FROM product "
				+ "WHERE order_id = ? AND product_id = ? ";

		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  product_amount);
			stmt.setFloat(2, updated_price);
			stmt.setInt(3,  order_id);
			stmt.setInt(4,  product_id);
							
			int rowsAffected = stmt.executeUpdate();
			if(rowsAffected > 0) {
				if(productOrder.getAmount() > product_amount) {
					//I increase the stock
					int newStock = productOrder.getAmount() - product_amount;
					productManager.updateProductStock(product, newStock, true);
				} else {
					//I decrease the stock
					int newStock = product_amount - productOrder.getAmount();
					productManager.updateProductStock(product, newStock, false);
				}
			} else {
				throw new RuntimeException("An error occurred when updating the product order in the database.");
			}
			c.commit();
		}catch(SQLException e) {
			System.err.println("Error updating the amount of a product in a product order: " + e.getMessage());
			e.printStackTrace();
		}		
	}
}
