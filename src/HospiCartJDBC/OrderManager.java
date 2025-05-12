package HospiCartJDBC;

import HospiCartInterfaces.IOrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.ProductOrder;
import HospiCartPOJOs.Shipment;
import HospiCartPOJOs.Status;
import javax.persistence.EntityManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Exceptions.OrderExceptions;

/**
 * This class is the responsible for handling all the operations related to orders.
 * This includes creating new orders, finding orders by their order id, client, date or status, deleting orders, updating existing orders by updating their status.
 * 
 * This class implements the interface "IOrderManager" and implements all of its methods. 
 */

public class OrderManager implements IOrderManager {
    private Connection c;
    private ConnectionManagerJDBC cm;

    //Constructor
    public OrderManager(ConnectionManagerJDBC cm) {
        this.cm = cm;
        this.c = cm.getConnection();
    }

    /**
     * Method that creates a new order with the client it receives as parameter and setting the current date and the default status to the order.
     * @param client object of Client that stores the user who made the order.
     * @throws SQLException if there is a problem with the connection (it is closed or not properly initialized), if there is an error in the SQL query, if there is a mismatch between the data being inserted and the expected one, etc.
     */
    @Override
    public void insertOrder(Client client, Payment payment, Shipment shipment, List<ProductOrder> productOrders) throws SQLException, OrderExceptions{
        Order order = new Order(); //I create the Order object
        
        if(client == null) {
        	//We throw a personalized exception
            throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_CLIENT);
        }
       //I initialize the order fields
       order.setClient(client);
       order.setOrderDate(Date.valueOf(LocalDate.now()));
       order.setStatus(Status.ORDERED);
       order.setPayment(payment);
       order.setShipment(shipment);
       order.setProductOrders(productOrders);

       //I insert the order information that I have up to now
       String sql = "INSERT INTO client_order (user_id, order_date, status) VALUES (?, ?, ?)";

       //I create the order record and fetch, from the database once the order was inserted in it, the generated key (the id of the order which is automatically generated in the database)
        try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        	stmt.setInt(1, client.getUserId());
            stmt.setDate(2, Date.valueOf(LocalDate.now())); // we set current date
            stmt.setString(3, Status.ORDERED.name()); // we set the default status (ordered)
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
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
            //TODO I think I have to create the objects or ProductOrder, Shipment and Payment also.
            //I don't have to close the statement nor the result sets because I used "trys-with resources"
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
    public void deleteOrder(int order_id) {
    	//I create one SQL sequence to delete the order in all the entities that had some kind of relationship with it.
    	//First, I delete it from ProductOrders because of the many to many relationship. Then, I delete it from shipment and payment and finally from the client_order table.
    	String deleteFromProductOrders = "DELETE FROM product_order WHERE order_id = ?";
    	String deleteFromPayment = "DELETE FROM payment WHERE order_id = ?";
    	String deleteFromShipment = "DELETE FROM shipment WHERE order_id = ?";
    	String deleteFromOrder = "DELETE FROM client_order WHERE order_id = ?";
    	
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
                System.out.println("Order with ID " + order_id + " deleted successfully.");
            }
    		//I call the method of Product Order that increases the stock of a product.
    		List<ProductOrder> productOrdersOfOrder = cm.getProductOrderManager().getProductOrdersByOrderID(order_id);
			for(int i = 0; i<productOrdersOfOrder.size(); i++) {
				
				ProductOrder productOrder = productOrdersOfOrder.get(i);
				Product product = productOrder.getProduct();
				cm.getProductOrderManager().addProductToStockQuantity(product.getProductId(), productOrder.getAmount());
			}

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
    
    /**
	 * Method that retrieves a specific order whose id matches the one received as parameter.
	 * @param order_id integer that contains the id of the order we wish to obtain.
	 * @return an object of Order
	 */
    @Override
    public Order getOrderByID(int order_id) throws OrderExceptions{
    	Order order = null;
    
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_id = ?";
    	
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
    				//I throw a personalized exceptions that indicates that was not found an order with the introduced order_id in the database.
    				throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_ID);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving order: " + e.getMessage());
            e.printStackTrace();
    	}
        return order;
    }
    
    /**
	 * Method that retrieves a list of orders whose buyer's id coincides with the id received as parameter.
	 * @param user_id integer that stores the id of the user whose orders we want to see.
	 * @return a list that contains all the orders associated to the user.
	 */
    @Override
    public List<Order> getOrdersByUser(int user_id) {
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
                //While loop that iterates through all the result set and retrieves all the orders.
                while(resultSet.next()) {
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
    		}
    	}catch(SQLException e) {
    		System.err.println("Error retrieving orders from user: " + e.getMessage());
            e.printStackTrace();
    	}
        return ordersOfUser;
    } // TODO: It would make sense creating a personalized exception if the method getClientByID is able to return null (when a client is not found with the provided user_id).
    //WE CAN CREATE A PERSONALIZED EXCEPTION TO BE THROWN IN getUserByID when a user was not found, and re-throw it in this method!!

    /**
	 * Method that retrieves a list containing all the orders that were purchased on the date received as parameter.
	 * @param order_date variable of date type.
	 * @return the list of orders that were purchased on the date introduced.
	 */
    @Override
    public List<Order> getOrdersByOrderDate(Date order_date) {
    	
    	Order order = null;
    	List<Order> ordersWithSpecifiedDate = new ArrayList<>();
    	
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_date = ?";
    	
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
    	}catch(SQLException e) {
    		System.err.println("Error retrieving orders purchased on the specified date: " + e.getMessage());
            e.printStackTrace();
    	}
        return ordersWithSpecifiedDate;
    }

    /**
	 * Method that receives two dates as parameter, which establish the date range that is of our interest in order to filter the orders and see only the ones that fall within this range.
	 * @param startDate variable of Date type that stores the start date of the range.
	 * @param endDate variable of Date type that stores the end date of the range.
	 * @return a list containing all the orders whose order date is between the range.
	 */
    @Override
    public List<Order> getOrdersWithinDateRange(Date startDate, Date endDate) {
    	Order order = null;
    	List<Order> ordersWithinDateRange = new ArrayList<>();
    	
    	String sql = "SELECT o.order_id, o.user_id, o.order_date, o.status AS order_status "
    			+ "FROM client_order AS o "
    			+ "WHERE o.order_date BETWEEN ? AND ?";
    	
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
    	}catch(SQLException e) {
    		System.err.println("Error retrieving orders between the specified date range: " + e.getMessage());
            e.printStackTrace();
    	}
        return ordersWithinDateRange;
    }
    
    
    /**
	 * Method that retrieves a list containing all the orders of HospiCart.
	 * @return a list with all the orders.
	 */
    @Override
    public List<Order> getAllOrders() {
    	Order order = null;
    	List<Order> orders = new ArrayList<>();

    	String sql = "SELECT *"
    			+ "FROM client_order AS o";
    	
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
    	}
        return orders;
    }

    /**
	 * Method that retrieves a list that contains all the orders whose status matches the one received as parameter.
	 * @param status variable of type Status that contains the status we are interested in (in order to see the orders that have this status)
	 * @return a list that contains the orders with the received status.
	 */
    @Override
    public List<Order> getOrdersByStatus(Status status) {
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
    	}catch(SQLException e) {
    		System.err.println("Error retrieving orders purchased on the specified date: " + e.getMessage());
            e.printStackTrace();
    	}
        return ordersWithSpecifiedStatus;    
    }
    
    /**
	 * Method that receives an order id and a status as parameters and updates the status of the order whose id coincides with the received as parameter.
	 * @param order_id integer that stores the id of the order whose status we wish to update.
	 * @param newStatus variable of type Status that store the status we want the order to have.
	 */
    @Override
    public void updateOrderStatus(int order_id, Status newStatus) {
    	Order order = getOrderByID(order_id);
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
