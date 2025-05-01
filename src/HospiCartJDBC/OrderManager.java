package HospiCartJDBC;

import HospiCartInterfaces.IOrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Status;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderManager implements IOrderManager {
    private Connection c;
    private ConnectionManagerJDBC cm; //TODO delete this? it is not used

    public OrderManager(ConnectionManagerJDBC cm) {
        this.cm = cm;
        this.c = cm.getConnection();
    }

    /**
     * Method that creates a new order with the client it receives as parameter and setting the current date and the default status to the order.
     * @param client object of Client that stores the user who made the order.
     * @throws SQLException if there is a problem with the connection (it is closed or not properly initialised), if there is an error in the SQL query, if there is a mismatch between the data being inserted and the expected one, etc.
     */
    @Override
    public Order createOrder(Client client) throws SQLException{
        Order order = new Order(); //I create the Order object

       //I initialize the order fields
       order.setClient(client);
       order.setOrderDate(Date.valueOf(LocalDate.now()));
       order.setStatus(Status.ORDERED);

       //I insert the order information that I have up to now
       String sql = "INSERT INTO client_order (user_id, order_date, status) VALUES (?, ?, ?)";

       //I create the order record and fetch the generated key (the id of the order)
        try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, client.getUserId());
            stmt.setDate(2, Date.valueOf(LocalDate.now())); // we set current date
            stmt.setString(3, Status.ORDERED.name()); // we set the default status (ordered)

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            //Now, I get the generated order id
            try(var generatedKeys = stmt.getGeneratedKeys()) {
                //We use var because it enables the compiler to infer the type of the variable from the initialization (in this case, var represents a result set)
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
            stmt.close();
            c.commit(); //we do this because we disabled the autocommit in the connection
        } catch (SQLException e) {
            //We "rollback" the transaction in case of error.
            if(c != null){ //We make sure that c is not null as an error would be thrown when trying to rollback over a null object
                try{
                    c.rollback();
                } catch(SQLException ex){
                    throw new SQLException("Error during rollback: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
        return order;

    }

    @Override
    public Order getOrderByID(int order_id) {
    	Order order = null;
    	/*
    	String sql = "SELECT o.order_id, o. "
    	try {
    		Statement
    	}
    	*/
        return order;
    }

    @Override
    public List<Order> getOrdersByUser(int user_id) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByOrderDate(LocalDate order_date) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersWithinDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public void updateOrderStatus(int order_id, Status newStatus) {

    }

    @Override
    public void deleteOrder(int order_id) {

    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByStatus(Status status) {
        return List.of();
    }

    @Override
    public boolean orderExists(int order_id) {
        return false;
    }
}
