package HospiCartJDBC;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import HospiCartInterfaces.IProductOrderManager;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.ProductOrder;


public class ProductOrderManager implements IProductOrderManager{
	
    private Connection c;
	private ConnectionManagerJDBC cm;

	/**
	 * Constructor
	 * @param cm object of "ConnectionManagerJDBC"
	 */
	public ProductOrderManager(ConnectionManagerJDBC cm) {
		this.cm = cm;
		this.c = cm.getConnection();
	}

	/**
	 * Method that receives the id of a product and the id of an order and returns the object of Product Order that corresponds with the received IDs.
	 * @param product_id integer that stores the id of a product.
	 * @param order_id integer that stores the id of an order.
	 * @return an object of the class product order whose id matches with the received one as parameter.
	 */
	@Override
	public ProductOrder getProductOrderByIDs(int product_id, int order_id) {
		ProductOrder productOrder = null;
	    
    	String sql = "SELECT *"
    			+ "FROM product_order AS po"
    			+ "WHERE po.order_id = ? AND po.product_id = ?";
    	
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		stmt.setInt(2,  product_id);
    		
    		try(var resultSet = stmt.executeQuery()){
    			
    			if(resultSet.next()) {
    				
    				productOrder = new ProductOrder();
    				productOrder.setAmount(resultSet.getInt("amount"));
    				productOrder.setTotal_price(resultSet.getFloat("total_price"));
    				
    				Order order = cm.getOrderManager().getOrderByID(order_id);
    				productOrder.setOrder(order);
    				
    				Product product = cm.getProductManager().getProductById(product_id);
    				productOrder.setProduct(product);
    				
    				/*I set the id of the product order manually because it is not a fiel in the database. In order to ensure that it is unique, I set it to be equal 
    				 * to the sum of the order id and its product (which have both unique values)
    				 */
    				productOrder.setOrderProductID(product_id+order_id);
    			}
    			stmt.close();
    			resultSet.close();
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
	public List<ProductOrder> getProductOrdersByOrderID(int order_id) {
		ProductOrder productOrder = null;
    	List<ProductOrder> productOrdersOfOrder = new ArrayList<>();
    	
    	String sql = "SELECT *"
    			+ "FROM product_order AS po"
    			+ "WHERE po.order_id = ?";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		
    		try(var resultSet = stmt.executeQuery()){
    			
    			if(resultSet.next()) {
    				
    				productOrder = new ProductOrder();
    				
    				//I set the fields of the product order object.
    				productOrder = new ProductOrder();
    				productOrder.setAmount(resultSet.getInt("amount"));
    				productOrder.setTotal_price(resultSet.getFloat("total_price"));
    				
    				// Get the full Order object from OrderManager
    				Order order = cm.getOrderManager().getOrderByID(order_id);
    				productOrder.setOrder(order);
    				
    				int product_id = resultSet.getInt("product_id");
    				Product product = cm.getProductManager().getProductById(product_id);
    				productOrder.setProduct(product);
    				
    				/*I set the id of the product order manually because it is not a fiel in the database. In order to ensure that it is unique, I set it to be equal 
    				 * to the sum of the order id and its product (which have both unique values)
    				 */
    				productOrder.setOrderProductID(product_id+order_id);
    				
    				//Finally, I add the created product order to the list of product orders associated to the received order id.
    				productOrdersOfOrder.add(productOrder);
    			}
    			stmt.close();
    			resultSet.close();
    		}
    	}catch(SQLException e) {
    		System.err.println("Error retrieving product orders from an order: " + e.getMessage());
            e.printStackTrace();
    	}
        return productOrdersOfOrder;
		
	}

	/**
	 * Method that receives an order id and deletes the product orders associated to it. This method functions as a method that clears the product orders
	 * that belong to a specified order.
	 * @param order_id integer that stores the id of an order.
	 */
	@Override
	public void deleteProductOrdersByOrderID(int order_id) {
		//SQL query
		String sql = "DELETE * FROM product_order WHERE order_id = ?";
		
		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  order_id);
			
			stmt.executeUpdate();
			stmt.close();
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
	public void deleteProductOrderByIDs(int product_id, int order_id) {
		//SQL query
		String sql = "DELETE * FROM product_order WHERE order_id = ? AND product_id = ?";
		
		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  order_id);
			stmt.setInt(2, product_id);
			
			stmt.executeUpdate();
			stmt.close();
		}catch(SQLException e) {
			System.err.println("Error deleting product orders by the product and order ID: " + e.getMessage());
		    e.printStackTrace();
		}
	}

	/**
	 * Method that receives a product and an order id by parameter and adds the received product to the order that corresponds with the received order id. i.e: the method creates a new
	 * product order with the order and product IDs received as parameter.
	 * @param product_id integer that stores the id of the product that we want to add to an order.
	 * @param order_id integer that stores the id of the order to which we want to add a product.
	 */
	@Override
	public void createProductOrder(int product_id, int order_id){
		//SQL query
		String sql = "INSERT INTO product_order (order_id, product_id, amount, total_price) VALUES (?, ?, 1, (SELECT price FROM product WHERE product_id = ?))";
		
		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1, order_id);
			stmt.setInt(2, product_id);
			stmt.setInt(3, product_id);
					
			stmt.executeUpdate();
			stmt.close();
		}catch(SQLException e) {
			System.err.println("Error adding a product order: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that receives an order id, a product's id and a quantity. The method updates the amount of the received product for the quantity passed by parameter of the order that corresponds with the received order id.
	 * @param product_id integer that stores the id of the product whose quantity we wish to update.
	 * @param order_id integer that stores the id of the order which we wish to modify.
	 * @param product_amount integer that stores the amount we wish to order of the specified product.
	 */
	@Override
	public void updateProductAmountInAnOrder(int product_id, int order_id, int product_amount) {
		//I get the product out of its ID
		Product product = cm.getProductManager().getProductById(product_id);
		BigDecimal product_price = product.getPrice();
		//I update the total price of the product order by multiplying the price of the product by the amount ordered.
		BigDecimal updated_price = product_price.multiply(BigDecimal.valueOf(product_amount));
		
		//SQL query
		String sql = "UPDATE product_order SET amount = ?, total_price = ?"
				+ "FROM product"
				+ "WHERE order_id = ? AND product_id = ?";

		//I create the statement in the try catch block
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  product_amount);
			stmt.setBigDecimal(2, updated_price);
			stmt.setInt(3,  order_id);
			stmt.setInt(4,  product_id);
							
			stmt.executeUpdate();
			stmt.close();
		}catch(SQLException e) {
			System.err.println("Error updating the amount of a product in a product order: " + e.getMessage());
			e.printStackTrace();
		}		
	}

	/**
	 * Method that receives an order id and returns the total price of the order that corresponds with the receives id.
	 * @param order_id integer that stores the id of the order whose products we wish to obtain.
	 * @return a floating number that stores the total price of the order.
	 */
	@Override
	public double getTotalPriceOfAnOrder(int order_id) {
		//SQL query
		String sql = "SELECT SUM(total_price) FROM product_order WHERE order_id = ?";
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
			stmt.close();
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
	public List<Order> getOrdersWithAProduct(int product_id) {
		//SQL query
		String sql = "SELECT * FROM product_order WHERE product_id = ?";
		
		//List that will store the orders
		List<Order> ordersWithProduct = new LinkedList<>();
		//I create the statement in the try catch block.
		try(PreparedStatement stmt = c.prepareStatement(sql)){
			stmt.setInt(1,  product_id);
			
			try(ResultSet rs = stmt.executeQuery()){
				if(rs.next()) {
					//I store the order id ina variable of type integer.
					int order_id = rs.getInt("order_id");
					//I retrieve the order that corresponds with the obtained order id using the respective method generated in "OrderManager"
					Order order = cm.getOrderManager().getOrderByID(order_id);
					//I add the order to the list of orders.
					ordersWithProduct.add(order);
				}
				stmt.close();
				rs.close();
			}
		} catch(SQLException e) {
			System.err.println("Error retrieving the total price of an order: " + e.getMessage());
			e.printStackTrace();
		}
		return ordersWithProduct;
	}
}
