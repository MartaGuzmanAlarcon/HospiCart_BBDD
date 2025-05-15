package HospiCartJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartInterfaces.IShipmentManager;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Shipment;

/**
 * This class is the responsible for handling all the operations related to shipments.
 * This includes creating new shipments, finding shipments by their shipment id, tracking number or order id or deleting shipments.
 * 
 * This class implements the interface "IShipmentManager" and implements all of its methods. 
 */
public class ShipmentManager implements IShipmentManager{

	private Connection c;
    private ConnectionManagerJDBC cm;

    //Constructor
    public ShipmentManager(ConnectionManagerJDBC cm) {
        this.cm = cm;
        this.c = cm.getConnection();
    }
    
    /**
	 * Method that adds new shipments.
	 * @param order object of the class "Order" for which we want to create the shipment.
	 * @return the created shipment.
	 */
	@Override
	/*public void insertShipment(Order order) throws SQLException { //TODO: CHANGE THIS SO IT RECEIVES THE SHIPMENT?

		Shipment shipment = new Shipment(order); //I create the Order object

	       //I initialize the order of the shipment to the received one.
	       shipment.setOrder(order);
	       
	       //I insert the order information that I have up to now
	       String sql = "INSERT INTO shipment (order_id, tracking_number) VALUES (?, ?)";

	       //I create the shipment record and fetch the generated keys (the id of the shipment)
	        try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
	            stmt.setInt(1, order.getOrderId());
	            stmt.setInt(2, shipment.getTrackingNumber());

	            int affectedRows = stmt.executeUpdate();
	            if(affectedRows == 0) {
	                throw new SQLException("Creating shipment failed, no rows affected.");
	            }

	            //Now, I get the generated shipment id
	            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    shipment.setShipmentId(generatedKeys.getInt(1));
	                } else {
	                    throw new SQLException("Creating shipment failed, no ID obtained.");
	                }
	            }
	            c.commit(); //we do this because we disabled the auto-commit in the connection
	        } catch (SQLException e) {
	            //We "rollback" the transaction in case of error.
	            if(c != null){ //We make sure that c is not null as an error would be thrown when trying to roll back over a null object
	                try{
	                    c.rollback();
	                } catch(SQLException ex){
	                    throw new SQLException("Error during rollback: " + ex.getMessage(), ex);
	                }
	            }
	            throw new RuntimeException("Error creating shipment: " + e.getMessage(), e);
	        }
	}*/
	public void insertShipment(Shipment shipment) throws SQLException { //TODO: CHANGE THIS SO IT RECEIVES THE SHIPMENT?
			Order order = shipment.getOrder();
	       
	       //I insert the order information that I have up to now
	       String sql = "INSERT INTO shipment (order_id, tracking_number) VALUES (?, ?)";

	       //I create the shipment record and fetch the generated keys (the id of the shipment)
	        try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
	            stmt.setInt(1, order.getOrderId());
	            stmt.setInt(2, shipment.getTrackingNumber());

	            int affectedRows = stmt.executeUpdate();
	            if(affectedRows == 0) {
	                throw new SQLException("Creating shipment failed, no rows affected.");
	            }

	            //Now, I get the generated shipment id
	            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    shipment.setShipmentId(generatedKeys.getInt(1));
	                } else {
	                    throw new SQLException("Creating shipment failed, no ID obtained.");
	                }
	            }
	            c.commit(); //we do this because we disabled the auto-commit in the connection
	        } catch (SQLException e) {
	            //We "rollback" the transaction in case of error.
	            if(c != null){ //We make sure that c is not null as an error would be thrown when trying to roll back over a null object
	                try{
	                    c.rollback();
	                } catch(SQLException ex){
	                    throw new SQLException("Error during rollback: " + ex.getMessage(), ex);
	                }
	            }
	            throw new RuntimeException("Error creating shipment: " + e.getMessage(), e);
	        }
	}
	
	/**
	 * Method that receives a shipment id and removes the shipment whose id matches with the received one.
	 * @param shipment_id integer that stores the id of the shipment we want to delete.
	 */
	@Override
	public void deleteShipmentByID(int shipment_id) throws OrderExceptions, ClientException{
		Shipment shipment = getShipmentByID(shipment_id);
		
		/*We delete the shipment from the table shipment of the database. However, it is worth saying that we do not delete the order associated to the shipment
		because we think that in some scenarios it may be possible wanting to delete a shipment and assigning a new one to the order.
		*/
		String deleteFromShipment = "DELETE FROM shipment WHERE shipment_id = ?";
    	
    	try (PreparedStatement stmtFromShipment = c.prepareStatement(deleteFromShipment))
    	{
    		//Delete from shipment
    		stmtFromShipment.setInt(1, shipment_id);
    		int rowsAffected = stmtFromShipment.executeUpdate();
    		
    		//We check whether a line was or not affected (is yes, then the order was removed)
    		if (rowsAffected == 0) {
                System.out.println("No shipment found with ID: " + shipment_id);
            } else {
                System.out.println("Shipment with ID " + shipment_id + " deleted successfully.");
                setOrderToNull(shipment);
            }
            c.commit(); //we commit the transaction
    		
    	}catch (SQLException e) {
            try {
                c.rollback(); // Roll back on failure
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Error deleting shipment: " + e.getMessage(), e);
        }
	}

	/**
	 * Method that receives a tracking number and removes the shipment whose tracking number matches with the received one.
	 * @param tracking_number integer that stores the tracking number of the shipment we want to delete.
	 */
	@Override
	public void deleteShipmentByTrackingNumber(int tracking_number) throws OrderExceptions, ClientException{
		Shipment shipment = getShipmentByTrackingNumber(tracking_number);
		
		/*We delete the shipment from the table shipment of the database. However, it is worth saying that we do not delete the order associated to the shipment
		because we think that in some scenarios it may be possible wanting to delete a shipment and assigning a new one to the order.
		*/
		String deleteFromShipment = "DELETE FROM shipment WHERE tracking_number = ?";
    	
    	try (PreparedStatement stmtFromShipment = c.prepareStatement(deleteFromShipment))
    	{
    		//Delete from shipment
    		stmtFromShipment.setInt(1, tracking_number);
    		int rowsAffected = stmtFromShipment.executeUpdate();
    		
    		//We check whether a line was or not affected (is yes, then the order was removed)
    		if (rowsAffected == 0) {
                System.out.println("No shipment found with tracking number: " + tracking_number);
            } else {
                System.out.println("Shipment with tracking number " + tracking_number + " deleted successfully.");
                setOrderToNull(shipment);
            }
            c.commit(); //we commit the transaction
    		
    	}catch (SQLException e) {
            try {
                c.rollback(); // Roll back on failure
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Error deleting shipment: " + e.getMessage(), e);
        }		
	}

	/**
	 * Method that receives an order id and removes the shipment whose order id matches with the received one.
	 * @param order_id integer that stores the order id of the shipment we want to delete.
	 */
	@Override
	public void deleteShipmentByOrderID(int order_id) throws OrderExceptions, ClientException{
		Shipment shipment = getShipmentByOrderID(order_id);
		
		/*We delete the shipment from the table shipment of the database. However, it is worth saying that we do not delete the order associated to the shipment
		because we think that in some scenarios it may be possible wanting to delete a shipment and assigning a new one to the order.
		*/
		String deleteFromShipment = "DELETE FROM shipment WHERE order_id = ?";
    	
    	try (PreparedStatement stmtFromShipment = c.prepareStatement(deleteFromShipment))
    	{
    		//Delete from shipment
    		stmtFromShipment.setInt(1, order_id);
    		int rowsAffected = stmtFromShipment.executeUpdate();
    		
    		//We check whether a line was or not affected (is yes, then the order was removed)
    		if (rowsAffected == 0) {
                System.out.println("No shipment found with order ID : " + order_id);
            } else {
                System.out.println("Shipment with order ID " + order_id + " deleted successfully.");
                setOrderToNull(shipment);
            }
            c.commit(); //we commit the transaction
    		
    	}catch (SQLException e) {
            try {
                c.rollback(); // Roll back on failure
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Error deleting shipment: " + e.getMessage(), e);
        }				
	}

	/**
	 * Method that receives a shipment id as parameter and retrieves the shipment associated with the shipment id.
	 * @param shipment_id integer that stores a shipment ID.
	 * @return the found shipment whose shipment id matches with the received one.
	 */
	@Override
	public Shipment getShipmentByID(int shipment_id) throws OrderExceptions, ClientException {
		Shipment shipment = null;
	    //SQL query
    	String sql = "SELECT * "
    			+ "FROM shipment "
    			+ "WHERE shipment_id = ?";
    	//I create the statement
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, shipment_id);
    		try(ResultSet resultSet = stmt.executeQuery()){
    			if(resultSet.next()) {
    				int order_id = resultSet.getInt("order_id");
    				Order order = cm.getOrderManager().getOrderByID(order_id);
    				
    				shipment = new Shipment(order);
    				shipment.setShipmentId(resultSet.getInt("shipment_id"));
    				//I don't set the tracking number because it has already been done by the constructor of Shipment when I created the shipment object and passed order by parameter.
    				
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving shipment: " + e.getMessage());
            e.printStackTrace();
    	}
        return shipment;
	}

	/**
	 * Method that receives a tracking number as parameter and retrieves the shipment associated with the tracking number.
	 * @param tracking_number integer that stores a tracking number.
	 * @return the found shipment whose tracking number matches with the received one.
	 */
	@Override
	public Shipment getShipmentByTrackingNumber(int tracking_number) throws OrderExceptions, ClientException {
		Shipment shipment = null;
	    //SQL query
    	String sql = "SELECT * "
    			+ "FROM shipment "
    			+ "WHERE tracking_number = ?";
    	//I create the statement
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, tracking_number);
    		
    		try(ResultSet resultSet = stmt.executeQuery()){
    			if(resultSet.next()) {
    				int order_id = resultSet.getInt("order_id");
    				Order order = cm.getOrderManager().getOrderByID(order_id);
    				
    				shipment = new Shipment();
    				shipment.setShipmentId(resultSet.getInt("shipment_id"));
    				shipment.setTrackingNumber(resultSet.getInt("tracking_number")); //I set the tracking number manually
    				shipment.setOrder(order);
    				//TODO what would happen if one constructor of Shipment implemented the other in this case? Because here, the shipment already has an assigned 
    				//shipment number and I don't want to generate another one. Therefore, I only want to use the empty constructor.
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving shipment: " + e.getMessage());
            e.printStackTrace();
    	}
        return shipment;
	}

	/**
	 * Method that receives an order id as parameter and retrieves the shipment associated with the order id.
	 * @param order_id integer that stores the id of an order.
	 * @return the shipment whose order id matches with the received one.
	 */
	@Override
	public Shipment getShipmentByOrderID(int order_id) throws OrderExceptions, ClientException {
		Shipment shipment = null;
	    //SQL query
    	String sql = "SELECT * "
    			+ "FROM shipment "
    			+ "WHERE order_id = ?";
    	//I create the statement
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		
    		try(ResultSet resultSet = stmt.executeQuery()){
    			if(resultSet.next()) {
    				Order order = cm.getOrderManager().getOrderByID(order_id);
    				
    				shipment = new Shipment();
    				shipment.setShipmentId(resultSet.getInt("shipment_id"));
    				shipment.setTrackingNumber(resultSet.getInt("tracking_number"));
    				shipment.setOrder(order);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving shipment: " + e.getMessage());
            e.printStackTrace();
    	}
        return shipment;
	}

	/**
	 * Method that returns a list of shipments that contains them all.
	 * @return a list with objects of "Shipment"
	 */
	@Override
	public List<Shipment> getAllShipments() throws OrderExceptions, ClientException{
		Shipment shipment = null;
    	List<Shipment> shipments = new ArrayList<>();

    	String sql = "SELECT * "
    			+ "FROM shipment AS s";
    	
    	try (PreparedStatement stmt = c.prepareStatement(sql)){
    		try(ResultSet resultSet = stmt.executeQuery()){
                //While loop that iterates through all the result set and retrieves all the shipments.
    			while(resultSet.next()) {
    				shipment = new Shipment();
    				//I create a variable called order id and store the id of the order in it.
    				int order_id = resultSet.getInt("order_id");
    				Order order = cm.getOrderManager().getOrderByID(order_id);
    				
    				shipment.setOrder(order);
    				shipment.setTrackingNumber(resultSet.getInt("tracking_number"));
    				shipment.setShipmentId(resultSet.getInt("shipment_id"));
    				
    				//Finally, I add the created shipment to the list of shipments.
    				shipments.add(shipment);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving shipments: " + e.getMessage());
            e.printStackTrace();
    	}
        return shipments;
	}
	
	//TODO: SEE IF WE NEED THIS METHOD !!!
		/**
		 * Method that receives an object of shipments and sets the order to null. This method is useful for when we delete shipments 
		 * and we want to set the order associated to the deleted shipments to null.
		 * @param shipment object of the class "Shipment".
		 */
		@Override
		public 	void setOrderToNull(Shipment shipment) {
			shipment.setOrder(null);
	    	//TODO see what I do with this method
			//NOT NECESSARY IF WE CHANGE THE TABLE OF THE DB
	    	/*String sql = "UPDATE shipment SET order_id = ? WHERE shipment_id = ?";
	    	
	    	try(PreparedStatement stmt = c.prepareStatement(sql)){
	    		//stmt.setInt(1, ); TODO what should I put here? because I cannot set the order_id to null!!!
	    		stmt.setInt(2, shipment.getShipmentId());
	    		
	    		int rowsUpdated = stmt.executeUpdate();
	    		if (rowsUpdated == 0) {
	                System.out.println("No order found associated with the provided shipment.");
	            } else {
	                System.out.println("The shipment was properly updated.");
	            }
	    		c.commit();
	    	} catch (SQLException e) {
	            try {
	                c.rollback();  // Roll back in case of error
	            } catch (SQLException rollbackEx) {
	                System.err.println("Rollback failed: " + rollbackEx.getMessage());
	            }
	            throw new RuntimeException("Error updating the shipment: " + e.getMessage(), e);
	        }*/
		}
}
