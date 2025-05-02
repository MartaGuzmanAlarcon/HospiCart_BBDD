package HospiCartJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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
    		System.err.println("Error retrieving orders from user: " + e.getMessage());
            e.printStackTrace();
    	}
        return productOrdersOfOrder;
		
	}

	/**
	 * Method that receives an order id and deletes the product orders associated to it.
	 * @param order_id integer that stores the id of an order.
	 */
	@Override
	public void deleteProductOrdersByOrderID(int order_id) {
		
	}

	@Override
	public void deleteProductOrderByIDs(int product_id, int order_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addProductToAnOrder(int product_id, int order_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeProductFromAnOrder(int product_id, int order_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearOrder(int order_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProductAmountInAnOrder(int product_id, int order_id, int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Product> getProductsFromOrder(int order_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTotalPriceOfAnOrder(int order_id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Order> getOrdersWithAProduct(int product_id) {
		// TODO Auto-generated method stub
		return null;
	}
}
