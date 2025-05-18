package HospiCartJDBC;

import HospiCartInterfaces.IOrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.ProductOrder;
import HospiCartPOJOs.Shipment;
import HospiCartPOJOs.Status;
//import javax.persistence.EntityManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;

/**
 * This class is the responsible for handling all the operations related to orders.
 * This includes creating new orders, finding orders by their order id, client, date or status, deleting orders, updating existing orders by updating their status.
 * 
 * This class implements the interface "IOrderManager" and implements all of its methods. 
 */

public class OrderManagerJDBC implements IOrderManager {
    private Connection c;
    private ConnectionManagerJDBC cm;
    private ProductOrderManagerJDBC productOrderManager;
    private ShipmentManagerJDBC shipmentManager;
    private PaymentManagerJDBC paymentManager;


	/**
	 * Constructor of order manager.
	 * @param cm object of "ConnectionManagerJDBC"
	 */
    public OrderManagerJDBC(ConnectionManagerJDBC cm) {
        this.cm = cm;
        this.c = cm.getConnection();
        productOrderManager = new ProductOrderManagerJDBC(cm);
        shipmentManager = new ShipmentManagerJDBC(cm);
        paymentManager = new PaymentManagerJDBC(cm);
    }

    /**
     * Method that creates a new order with the client it receives as parameter and setting the current date and the default status to the order.
     * @param order object of Order that stores the order that we want to inert to the database.
     * @throws SQLException if there is a problem with the connection (it is closed or not properly initialized), if there is an error in the SQL query, if there is a mismatch between the data being inserted and the expected one, etc.
     * @throws ClientException if the method insert product order throws an exception of this type, we re-throw it here.
     */
    @Override
      public void insertOrder(Order order) throws SQLException, ClientException{
       //I initialize the order fields and check if they are valid or if I need to throw an exception
       Date orderDate = order.getOrderDate();
       Status status = order.getStatus();
       Client client = order.getClient();

       //I insert the order information that I have up to now
       String sql = "INSERT INTO client_order (user_id, order_date, status) VALUES (?, ?, ?) ";

       //I create the order record and fetch, from the database once the order was inserted in it, the generated key (the id of the order which is automatically generated in the database)
        try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        	stmt.setInt(1, client.getUserId());
            stmt.setDate(2, orderDate);
            stmt.setString(3, status.name());
            //I execute the INSERT operation making use of the method "executeUpdate" and obtain the amount of rows that were changed when executing the query.
            int affectedRows = stmt.executeUpdate();
            //If no rows were affected, then it means that the operation failed and I throw an SQL exception specifying what happened.
            if(affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            //Now, I get the generated primary key of order (the order id, which is assigned by the database), making use of the method "getGeneratedKeys"
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Inserting order failed, no ID obtained.");
                }
            }
            Payment payment = order.getPayment();
            Shipment shipment = order.getShipment();
            
            payment.setOrder(order);
            if(payment.getPaymentId() == null) {
            	paymentManager.insertPayment(payment);
            }
            
            shipment.setOrder(order);
            if(shipment.getShipmentId() == null) {
            	shipmentManager.insertShipment(shipment);
            }
            
            List<ProductOrder> productOrders = order.getProductOrders();
            for(int i=0; i<productOrders.size(); i++) {
                ProductOrder productOrder = productOrders.get(i);
                //I check if the product order has an assigned order and I only set the order and insert the productOrder into the database to those that don't have it set.
                if(productOrder.getOrder() == null) {
                    productOrder.setOrder(order);
                	productOrderManager.insertProductOrder(productOrder);
                }
            }
            //I print success messages
            System.out.println("\nThe order with ID " + order.getOrderId() + " was properly inserted in the database.");
            System.out.println("\n- Payment ID of order with ID " + order.getOrderId() + ": " + payment.getPaymentId());
            System.out.println("\n- Shipment ID of order with ID " + order.getOrderId() + ": " + shipment.getShipmentId());
            System.out.println("\n- Tracking number of order with ID " + order.getOrderId() + ": " + shipment.getTrackingNumber());

            // I don't have to close the statement nor the result sets because I used "trys-with resources"
            c.commit(); //we do this because we disabled the auto-commit in the connection
        } catch (SQLException e) {
            //We roll back the transaction in case of error.
            if(c != null){ //We make sure that c is not null as an error would be thrown when trying to roll back over a null object
                try{
                    c.rollback();
                } catch(SQLException ex){
                    throw new SQLException("Error during rollback: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }
    
    /**
	 * Method that receives an order's id as parameter and deletes it.
	 * @param order_id integer that stores the id of the order we wish to remove.
	 */
    @Override
    public void deleteOrder(int order_id)  throws ClientException, OrderExceptions{
    	Order order = getOrderByID(order_id);
    	//After obtaining the order that the user wants to delete, I check if the status of the order is "ORDERED", as it is the only scenario in which an order can be removed.
    	if(order.getStatus() != Status.ORDERED) {
    		throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.DELETE_ERROR);
    	} else {
	    	//I create one SQL sequence to delete the order in all the entities that had some kind of relationship with it.
	    	//First, I delete it from ProductOrders because of the many to many relationship. Then, I delete it from shipment and payment and finally from the client_order table.
	    	String deleteFromProductOrders = "DELETE FROM product_order WHERE order_id = ? ";
	    	String deleteFromPayment = "DELETE FROM payment WHERE order_id = ? ";
	    	String deleteFromShipment = "DELETE FROM shipment WHERE order_id = ? ";
	    	String deleteFromOrder = "DELETE FROM client_order WHERE order_id = ? ";
	    	
	    	try (
	    			//I create one prepared statement per each SQL sequence created.
	    			PreparedStatement stmtProductOrders = c.prepareStatement(deleteFromProductOrders);
	    			PreparedStatement stmtPayment = c.prepareStatement(deleteFromPayment);
	    			PreparedStatement stmtFromShipment = c.prepareStatement(deleteFromShipment);
	    			PreparedStatement stmtOrder = c.prepareStatement(deleteFromOrder);
	    	){
	    		//Delete from product orders
	    		stmtProductOrders.setInt(1, order_id);
	    		stmtProductOrders.executeUpdate();
	    		
	    		//Delete from payment
	    		stmtPayment.setInt(1, order_id);
	    		stmtPayment.executeUpdate();
	
	    		//Delete from shipment
	    		stmtFromShipment.setInt(1, order_id);
	    		stmtFromShipment.executeUpdate();
	    		
	    		//Delete from client_order
	    		stmtOrder.setInt(1, order_id);
	    		int rowsAffected = stmtOrder.executeUpdate();
	    		
	    		//We check whether a line was or not affected (is yes, then the order was removed)
	    		if (rowsAffected == 0) {
	                System.out.println("No order found with ID: " + order_id);
	            } else {
	                System.out.println("\nOrder with ID " + order_id + " deleted successfully.");
	            }
	    		//I call the method of Product Order that deletes the product orders associated to the received order id.
	    		productOrderManager.deleteProductOrdersByOrderID(order_id);
	
	            c.commit(); //we commit the transaction
	    		
	    	}catch (SQLException e) {
	            try {
	                c.rollback(); // Roll back on failure
	            } catch (SQLException rollbackEx) {
	                System.err.println("Rollback failed: " + rollbackEx.getMessage());
	            }
	            throw new RuntimeException("Error deleting order: " + e.getMessage(), e);
	        }
    	}
    }
    
    /**
	 * Method that retrieves a specific order whose id matches the one received as parameter.
	 * @param order_id integer that contains the id of the order we wish to obtain.
	 * @return an object of Order
	 */
    @Override
    public Order getOrderByID(int order_id) throws OrderExceptions, ClientException{
    	Order order = null;
    
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_id = ? ";
    	
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		try(ResultSet resultSet = stmt.executeQuery()){
    			if(resultSet.next()) {
    				order = new Order();
    				order.setOrderId(resultSet.getInt("order_id"));
    				order.setOrderDate(resultSet.getDate("order_date"));
    				order.setStatus(Status.valueOf(resultSet.getString("order_status")));
    				
    				Payment payment = cm.getPaymentManager().getPaymentByOrderId(order_id);
    				order.setPayment(payment);
    				
    				int user_id = resultSet.getInt("user_id");
    				Client client = cm.getClientManager().getClientByID(user_id);
    				order.setClient(client);
    				
    				Shipment shipment = cm.getShipmentManager().getShipmentByOrderID(order_id);
    				order.setShipment(shipment);
    				
    				List<ProductOrder> productOrders = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
    				order.setProductOrders(productOrders);
    				
    			} else {
    				//I throw a personalized exception that indicates that it was not found an order with the introduced order_id in the database.
    				throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_ID);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving order: " + e.getMessage());
            e.printStackTrace();
    	} catch(OrderExceptions oe) {
    		System.out.println("ERROR: " + oe);
    	} 
        return order;
    }
    
    /**
	 * Method that retrieves a list of orders whose buyer's id coincides with the id received as parameter.
	 * @param user_id integer that stores the id of the user whose orders we want to see.
	 * @return a list that contains all the orders associated to the user.
	 */
    @Override
    public List<Order> getOrdersByUser(int user_id) throws ClientException{
    	Order order = null;
    	List<Order> ordersOfUser = new ArrayList<>();
    	
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.user_id = ?";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, user_id);
    		try(ResultSet resultSet = stmt.executeQuery()){
    			// Get the full Client object from ClientManager
                Client client = cm.getClientManager().getClientByID(user_id);
                
                //I create this variable to check if the result set has rows or not, case in which a personalized exception will be thrown.
                boolean hasRows = false;
                //While loop that iterates through all the result set and retrieves all the orders.
                while(resultSet.next()) {
                	hasRows = true;
                	
    				order = new Order();
    				//I create a variable called order id and store the id of the order in it.
    				int order_id = resultSet.getInt("order_id");
    				//I set the fields of the order object.
    				order.setOrderId(order_id);
    				order.setOrderDate(resultSet.getDate("order_date"));
    				order.setStatus(Status.valueOf(resultSet.getString("order_status")));
    				order.setClient(client);   
    				
    				//I call the methods of Payment, Shipment and ProductOrders and add the fields with the found information. For this, I used the order id.
    				Payment payment = cm.getPaymentManager().getPaymentByOrderId(order_id);
    				order.setPayment(payment);
    				
    				Shipment shipment = cm.getShipmentManager().getShipmentByOrderID(order_id);
    				order.setShipment(shipment);
    				
    				List<ProductOrder> productOrders = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
    				order.setProductOrders(productOrders);
    				
    				//Finally, I add the created order to the list of orders the user made.
    				ordersOfUser.add(order);
    			}
                if(!hasRows) {
                	//If the result set is empty, I throw a personalized exception that indicates that it was not found a user with the introduced user_id in the database.
    				throw new ClientException(ClientException.ErrorTypeClient.INVALID_CLIENT_ID);                
                }
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving orders from user: " + e.getMessage());
            e.printStackTrace();
    	} catch(OrderExceptions oe) {
    		System.out.println("ERROR: " + oe);
    	}
        return ordersOfUser;
    }
    
    /**
	 * Method that retrieves a list containing all the orders that were purchased on the date received as parameter.
	 * @param order_date variable of date type.
	 * @return the list of orders that were purchased on the date introduced.
	 * @throws OrderExceptions a personalized exception is thrown if the received date is invalid (future date)
	 */
    @Override
    public List<Order> getOrdersByOrderDate(Date order_date) throws ClientException, OrderExceptions {
    	if(order_date.after(Date.valueOf(LocalDate.now()))){
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_DATE_FUTURE);
		}
    	Order order = null;
    	List<Order> ordersWithSpecifiedDate = new ArrayList<>();
    	
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_date = ? ";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setDate(1, order_date);
    		try(ResultSet resultSet = stmt.executeQuery()){
    			//While loop that runs through all the result set and retrieves all the orders.
    			while(resultSet.next()) {
    				order = new Order();
    				//I create a variable called order id and store the id of the order in it.
    				int order_id = resultSet.getInt("order_id");
    				//I set the fields of the order object.
    				order.setOrderId(order_id);
    				order.setOrderDate(order_date);
    				order.setStatus(Status.valueOf(resultSet.getString("order_status")));
    				
    				//I call the methods of Payment, Shipment and ProductOrders and add the fields with the found information. For this, I used the order id.
    				Client client = cm.getClientManager().getClientByID(resultSet.getInt("user_id"));
    				order.setClient(client);
    				
    				Payment payment = cm.getPaymentManager().getPaymentByOrderId(order_id); 
    				order.setPayment(payment);
    				
    				Shipment shipment = cm.getShipmentManager().getShipmentByOrderID(order_id);
    				order.setShipment(shipment);
    				
    				List<ProductOrder> productOrders = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
    				order.setProductOrders(productOrders);
    				
    				//Finally, I add the created order to the list of orders the user made.
    				ordersWithSpecifiedDate.add(order);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving orders purchased on the specified date: " + e.getMessage());
            e.printStackTrace();
    	} catch(OrderExceptions oe) {
    		System.out.println("ERROR: " + oe);
    	}
        return ordersWithSpecifiedDate;
    }

    /**
	 * Method that receives two dates as parameter, which establish the date range that is of our interest in order to filter the orders and see only the ones that fall within this range.
	 * @param startDate variable of Date type that stores the start date of the range.
	 * @param endDate variable of Date type that stores the end date of the range.
	 * @return a list containing all the orders whose order date is between the range.
	 * @throws OrderExceptions a personalized exception is thrown if any of the received dates is invalid (future dates)
	 */
    @Override
    public List<Order> getOrdersWithinDateRange(Date startDate, Date endDate) throws ClientException, OrderExceptions {
    	if(startDate.after(Date.valueOf(LocalDate.now()))){
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_DATE_FUTURE);
		}
    	if(endDate.after(Date.valueOf(LocalDate.now()))){
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_DATE_FUTURE);
		}
    	
    	Order order = null;
    	List<Order> ordersWithinDateRange = new ArrayList<>();
    	
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_date BETWEEN ? AND ? ";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setDate(1, startDate);
    		stmt.setDate(2, endDate);

    		try(ResultSet resultSet = stmt.executeQuery()){
    			
    			while(resultSet.next()) {
    				order = new Order();
    				//I create a variable called order id and store the id of the order in it.
    				int order_id = resultSet.getInt("order_id");
    				//I set the fields of the order object.
    				order.setOrderId(order_id);
    				order.setOrderDate(resultSet.getDate("order_date"));
    				order.setStatus(Status.valueOf(resultSet.getString("order_status")));
    				
    				//I call the methods of Payment, Shipment and ProductOrders and add the fields with the found information. For this, I used the order id.
    				Client client = cm.getClientManager().getClientByID(resultSet.getInt("user_id"));
    				order.setClient(client);
    				
    				Payment payment = cm.getPaymentManager().getPaymentByOrderId(order_id);
    				order.setPayment(payment);
    				
    				Shipment shipment = cm.getShipmentManager().getShipmentByOrderID(order_id);
    				order.setShipment(shipment);
    				
    				List<ProductOrder> productOrders = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
    				order.setProductOrders(productOrders);
    				
    				//Finally, I add the created order to the list of orders the user made.
    				ordersWithinDateRange.add(order);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving orders between the specified date range: " + e.getMessage());
            e.printStackTrace();
    	} catch(OrderExceptions oe) {
    		System.out.println("ERROR: " + oe);
    	}
        return ordersWithinDateRange;
    }
    
    
    /**
	 * Method that retrieves a list containing all the orders of HospiCart.
	 * @return a list with all the orders.
	 */
    @Override
    public List<Order> getAllOrders() throws ClientException {
    	Order order = null;
    	List<Order> orders = new ArrayList<>();

    	String sql = "SELECT * "
    			+ "FROM client_order AS o ";
    	
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		try(ResultSet resultSet = stmt.executeQuery()){
    			while(resultSet.next()) {
    				order = new Order();
    				//I create a variable called order id and store the id of the order in it.
    				int order_id = resultSet.getInt("order_id");
    				order.setOrderId(order_id);
    				order.setOrderDate(resultSet.getDate("order_date"));
    				order.setStatus(Status.valueOf(resultSet.getString("status")));
    				
    				Payment payment = cm.getPaymentManager().getPaymentByOrderId(order_id);
    				order.setPayment(payment);
    				
    				int user_id = resultSet.getInt("user_id");
    				Client client = cm.getClientManager().getClientByID(user_id);
    				order.setClient(client);
    				
    				Shipment shipment = cm.getShipmentManager().getShipmentByOrderID(order_id);
    				order.setShipment(shipment);
    				
    				List<ProductOrder> productOrders = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
    				order.setProductOrders(productOrders);
    				
    				//Finally, I add the created order to the list of orders the user made.
    				orders.add(order);
    				
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving orders: " + e.getMessage());
            e.printStackTrace();
    	} catch(OrderExceptions oe) {
    		System.out.println("ERROR: " + oe);
    	}
        return orders;
    }

    /**
	 * Method that retrieves a list that contains all the orders whose status matches the one received as parameter.
	 * @param status variable of type Status that contains the status we are interested in (in order to see the orders that have this status)
	 * @return a list that contains the orders with the received status.
	 * @throws OrderExceptions if the received status is invalid.
	 */
    @Override
    public List<Order> getOrdersByStatus(Status status) throws ClientException, OrderExceptions{
    	if(status != Status.ORDERED && status != Status.DELIVERED && status != Status.CANCELLED) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_STATUS);
		}
    	
    	Order order = null;
    	List<Order> ordersWithSpecifiedStatus = new ArrayList<>();
    	
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_status = ?";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setString(1, status.name());
    		try(ResultSet resultSet = stmt.executeQuery()){
    			
    			while(resultSet.next()) {
    				order = new Order();
    				//I create a variable called order id and store the id of the order in it.
    				int order_id = resultSet.getInt("order_id");
    				//I set the fields of the order object.
    				order.setOrderId(order_id);
    				order.setOrderDate(resultSet.getDate("order_date"));
    				order.setStatus(Status.valueOf(resultSet.getString("order_status")));
    				
    				//I call the methods of Payment, Shipment and ProductOrders and add the fields with the found information. For this, I used the order id.
    				Client client = cm.getClientManager().getClientByID(resultSet.getInt("user_id"));
    				order.setClient(client);
    				
    				Payment payment = cm.getPaymentManager().getPaymentByOrderId(order_id);
    				order.setPayment(payment);
    				
    				Shipment shipment = cm.getShipmentManager().getShipmentByOrderID(order_id);
    				order.setShipment(shipment);
    				
    				List<ProductOrder> productOrders = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
    				order.setProductOrders(productOrders);
    				
    				//Finally, I add the created order to the list of orders the user made.
    				ordersWithSpecifiedStatus.add(order);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving orders purchased on the specified date: " + e.getMessage());
            e.printStackTrace();
    	} catch(OrderExceptions oe) {
    		System.out.println("ERROR: " + oe);
    	}
        return ordersWithSpecifiedStatus;    
    }
    
    /**
	 * Method that receives an order id and a status as parameters and updates the status of the order whose id coincides with the received as parameter.
	 * @param order_id integer that stores the id of the order whose status we wish to update.
	 * @param newStatus variable of type Status that store the status we want the order to have.
	 * @throws OrderExceptions if the received new status is invalid, which produces the setter method of status to throw an OrderException and this method to re-throw it. 
	 */
    @Override
    public void updateOrderStatus(int order_id, Status newStatus) throws OrderExceptions, ClientException{
    	Order order = getOrderByID(order_id);
    	//The new status' validity is checked in the setter method of status in Order.
    	order.setStatus(newStatus);
    	
    	String sql = "UPDATE client_order SET status = ? WHERE order_id = ?";
    	
    	try(PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setString(1, newStatus.name());
    		stmt.setInt(2, order_id);
    		
    		int rowsUpdated = stmt.executeUpdate();
    		if (rowsUpdated == 0) {
                System.out.println("No order found with ID: " + order_id);
            } else {
                System.out.println("Order ID " + order_id + " updated to status: " + newStatus);
            }
    		c.commit();
    	} catch (SQLException e) {
            try {
                c.rollback();  // Roll back in case of error
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Error updating order status: " + e.getMessage(), e);
        }
    }
}
