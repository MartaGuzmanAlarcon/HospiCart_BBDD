package HospiCartJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartInterfaces.IPaymentManager;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.PaymentMethod;
import HospiCartPOJOs.PaymentStatus;
import HospiCartPOJOs.Shipment;
import Utilities.Utilities;

public class PaymentManagerJDBC implements IPaymentManager {
	private ConnectionManagerJDBC manager;
	// private OrderManager orderManager;

    public PaymentManagerJDBC(ConnectionManagerJDBC m) {
        this.manager = m;
    }


    /**
     * This method inserts a new payment record (row) into the database.
     * @param p the Payment to insert.
     */
	@Override
	public void insertPayment(Payment p) throws SQLException{
		String sql = "INSERT INTO payment (order_id, amount, payment_method, payment_status) " + " VALUES (?,?,?,?) ";
		try {
			PreparedStatement prep = manager.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS); 
			 prep.setInt(1, p.getOrder().getOrderId()); // The 1 binds to the first "?"
	         prep.setInt(2, p.getAmount()); // The 2 binds to the second "?", etc.
	         prep.setString(3, p.getPaymentMethod().name()); // name() Returns the name of this enum constant, exactly as declared in its enum declaration
	         prep.setString(4, p.getPaymentStatus().name()); // The 4 binds to the fourth "?"
	         
	         // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
	         int affectedRows = prep.executeUpdate();
	            if(affectedRows == 0) {
	                throw new SQLException("Creating payment failed, no rows affected.");
	            }

	            //Now, I get the generated shipment id
	            try(ResultSet generatedKeys = prep.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    p.setPaymentId(generatedKeys.getInt(1));
	                } else {
	                    throw new SQLException("Creating payment failed, no ID obtained.");
	                }
	            }
	         prep.close();
	         manager.getConnection().commit();
		//} catch(Exception e){ // All the previous methods throw an exception that is generally caught here in the form of Exception 
			//e.printStackTrace(); // To print where the error comes from
		}catch (SQLException e) {
            //We "rollback" the transaction in case of error.
            if(manager.getConnection() != null){ //We make sure that c is not null as an error would be thrown when trying to roll back over a null object
                try{
                	manager.getConnection().rollback();
                } catch(SQLException ex){
                    throw new SQLException("Error during rollback: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Error creating shipment: " + e.getMessage(), e);
        }
	}

	/**
     * This method deletes the payment with the given id.
     * @param paymentId unique identifier of the payment.
     * @throws Exception if no such payment exists or a database error occurs.
     */
	@Override
	public void deletePaymentById(Integer paymentId) throws Exception {
		try {
			 String sql = "DELETE FROM payment WHERE payment_id = ?";
			 PreparedStatement prep = manager.getConnection().prepareStatement(sql); 
			 
			 prep.setInt(1, paymentId); // The 1 binds to the first and unique "?"
			 
			 int rows = prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
             if (rows == 0) {
            	 throw new Exception("No payment found with id " + paymentId);
            	 } 
		} catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
		
	}

	/**
     * This method retrieves all payments from the database.
     * @return a List of Payment objects; never null but possibly empty if there are no records.
     */
	@Override
	public List<Payment> getListOfPayments() {
		List<Payment> payments = new ArrayList<>();
		try {
			Statement stmt = manager.getConnection().createStatement();
			String sql = "SELECT * FROM payment";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) { // next() Moves the cursor forward one row from its current position
				Integer paymentId = rs.getInt("payment_id");
				// TODO Order order = orderManager.getOrderById(order_id); -> ABRIL HAS TO IMPLEMENT THIS EXACT METHOD FOR ME 
				Integer amount = rs.getInt("amount");
				PaymentMethod paymentMethod = PaymentMethod.valueOf(rs.getString("payment_method")); // valueOf() is a function from Enum
				PaymentStatus paymentStatus = PaymentStatus.valueOf(rs.getString("payment_status")); // valueOf() is a function from Enum
				
				// IMPLEMENT THIS ONCE THE CLASS ORDER IS FINISHED
				// Payment payment = new Payment(paymentId, order, amount, paymentMethod, paymentStatus);
				// payments.add(payment);
			}
			
			rs.close();
			stmt.close();
			
		} catch(Exception e){ // All the previous methods throw an exception that is generally caught here in the form of Exception 
			e.printStackTrace(); // To print where the error comes from
		}
		
		return payments;
	}

	/**
	 * This method retrieves a single payment by its id.
	 * @param p_id the unique identifier of the payment to fetch.
	 * @return the matching Payment, or null if no such payment exists.
	 */
	@Override
	public Payment getPaymentById(Integer p_id) {
		Payment payment = null;
		
		try {
			Statement stmt = manager.getConnection().createStatement();
			String sql = "SELECT * FROM payment WHERE payment_id = " + p_id;
			ResultSet rs = stmt.executeQuery(sql);
			
			Integer paymentId = rs.getInt("payment_id");
			// TODO Order order = orderManager.getOrderById(order_id); -> ABRIL HAS TO IMPLEMENT THIS EXACT METHOD FOR ME 
			Integer amount = rs.getInt("amount");
			PaymentMethod paymentMethod = PaymentMethod.valueOf(rs.getString("payment_method")); // valueOf() is a function from Enum
			PaymentStatus paymentStatus = PaymentStatus.valueOf(rs.getString("payment_status")); // valueOf() is a function from Enum
			
			// IMPLEMENT THIS ONCE THE CLASS ORDER IS FINISHED
			// payment = new Payment(paymentId, order, amount, paymentMethod, paymentStatus);
			
			rs.close();
			stmt.close();
			
		}catch(Exception e){ // All the previous methods throw an exception that is generally caught here in the form of Exception 
			e.printStackTrace(); // To print where the error comes from.
		}
		return payment;
	}

	/**
	 * This method updates the amount on an existing payment.
	 * @param paymentId the unique identifier of the payment to update.
	 * @param amount the new amount value.
	 * @throws Exception if no payment exists with that ID or a database error occurs.
	 */
	@Override
	public void updateAmount(Integer paymentId, Integer amount) throws Exception {
		try {
			String sql = "UPDATE payment SET amount = ? WHERE payment_id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);
			
			prep.setInt(1, amount); // The 1 binds to the first "?"
            prep.setInt(2, paymentId); // The 2 binds to the second "?"
            int rows = prep.executeUpdate();
            if (rows == 0) {
                throw new Exception("No payment found with id " + paymentId);
            } 
		} catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
		
	}

	/**
	 * This method updates the payment method on an existing payment.
	 * @param paymentId the unique identifier of the payment to update.
	 * @param method the new PaymentMethod to set.
	 * @throws Exception if no payment exists with that ID or a database error occurs.
	 */
	@Override
	public void updatePaymentMethod(Integer paymentId, PaymentMethod method) throws Exception {
		try {
			 String sql = "UPDATE payment SET payment_method = ? WHERE payment_id = ?";
			 PreparedStatement prep = manager.getConnection().prepareStatement(sql);
			 
			 prep.setString(1, method.name()); // name() Returns the name of this enum constant, exactly as declared in its enum declaration
             prep.setInt(2, paymentId);
             int rows = prep.executeUpdate();
             if (rows == 0) {
            	 throw new Exception("No payment found with id " + paymentId);
             }
		} catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
		
	}

	/**
	 * This method updates the payment status on an existing payment.
	 * @param paymentId the unique identifier of the payment to update.
	 * @param status the new PaymentStatus to set.
	 * @throws Exception if no payment exists with that ID or a database error occurs.
	 */
	@Override
	public void updatePaymentStatus(Integer paymentId, PaymentStatus status) throws Exception {
		try {
			String sql = "UPDATE payment SET payment_status = ? WHERE payment_id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);
			
			prep.setString(1, status.name()); // name() Returns the name of this enum constant, exactly as declared in its enum declaration
            prep.setInt(2, paymentId);
            int rows = prep.executeUpdate();
            if (rows == 0) {
                throw new Exception("No payment found with id " + paymentId);
            }
		} catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
		
	}

	/**
	 * Method that receives an order id by parameter and retrieves the payment that is associated to the order of the received order id.
	 * @param order_id integer that stores the id of the order whose payment we want to get.
	 */
	@Override
	public Payment getPaymentByOrderId(int order_id) throws ClientException, OrderExceptions{
		Payment payment = null;
	    //SQL query
    	String sql = "SELECT * "
    			+ "FROM payment "
    			+ "WHERE order_id = ? ";
    	//I create the statement
    	try (PreparedStatement stmt = manager.getConnection().prepareStatement(sql)){
    		stmt.setInt(1, order_id);
    		
    		try(ResultSet resultSet = stmt.executeQuery()){
    			if(resultSet.next()) {
    				//Order order = manager.getOrderManager().getOrderByID(order_id);
    				//the line above produces a STACK OVERFLOW BECAUSE ORDER HAS ALSO THE MANAGER OF PAYMENT
    				//I set the fields of the payment object.
    				payment = new Payment();
    				payment.setPaymentId(resultSet.getInt("payment_id"));
    				//payment.setOrder(order);
    				payment.setAmount(resultSet.getInt("amount"));
    				payment.setPaymentMethod(PaymentMethod.valueOf(resultSet.getString("payment_method")));
    				payment.setPaymentStatus(PaymentStatus.valueOf(resultSet.getString("payment_status")));
    			}
    			else {
    				throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_ID);
    			}
    		}
    	} catch(SQLException e) {
    		System.err.println("Error retrieving the payment: " + e.getMessage());
            e.printStackTrace();
    	}
        return payment;
	}
}
