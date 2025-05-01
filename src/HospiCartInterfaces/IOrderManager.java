package HospiCartInterfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Status;


public interface IOrderManager {
	/**
	 * Method that receives the id of the user and creates an order. 
	 * @param client
	 */
	//TODO: this method should establish the order_date as the present one (LOCAL DATE: i think we have to use just DATE, CHECK!) and sets its order id and status to the appropriate ones
	//TODO: order id should be unique values and auto-increment as orders are created.
	Order createOrder(Client client) throws SQLException;
	
	/**
	 * Method that retrieves a specific order whose id matches the one received as parameter.
	 * @param order_id integer that contains the id of the order we wish to obtain.
	 * @return an object of Order
	 */
	//TODO: this method should throw an exception for the cases in which an order with the introduced id does not exist.
	Order getOrderByID(int order_id);
	
	/**
	 * Method that retrieves a list of orders whose buyer's id coincides with the id received as parameter.
	 * @param user_id integer that stores the id of the user whose orders we want to see.
	 * @return a list that contains all the orders associated to the user.
	 */
	//TODO: this method should throw an exception in case there is no user with the received id
	List<Order> getOrdersByUser(int user_id);
	
	/**
	 * Method that retrieves a list containing all the orders that were purchased on the date received as parameter.
	 * @param order_date variable of local date type.
	 * @return the list of orders that were purchased on the date introduced.
	 */
	//TODO: throw an exception if either no orders were purchased on the received date or if the date is invalid.
	List<Order> getOrdersByOrderDate(LocalDate order_date);
	
	/**
	 * Method that receives two dates as parameter, which establish the date range that is of our interest in order to filter the orders and see only the ones that fall within this range.
	 * @param startDate variable of LocalDate type that stores the start date of the range.
	 * @param endDate variable of LocalDate type that stores the end date of the range.
	 * @return a list containing all the orders whose order date is between the range.
	 */
	//TODO: throw an exception if any of the dates is invalid or if there are no orders in the provided range.
	List<Order> getOrdersWithinDateRange(LocalDate startDate, LocalDate endDate);
	
	/**
	 * Method that receives an order id and a status as parameters and updates the status of the order whose id coincides with the received as parameter.
	 * @param order_id integer that stores the id of the order whose status we wish to update.
	 * @param newStatus variable of type Status that store the status we want the order to have.
	 */
	//TODO: this method should throw an exception for the cases in which an order with the introduced id does not exist.
	void updateOrderStatus(int order_id, Status newStatus);
	
	/**
	 * Method that receives an order's id as parameter and deletes it.
	 * @param order_id integer that stores the id of the order we wish to remove.
	 */
	//TODO: this method should throw an exception for the cases in which an order with the introduced id does not exist.
	void deleteOrder(int order_id);
	
	/**
	 * Method that retrieves a list containing all the orders of HospiCart.
	 * @return a list with all the orders.
	 */
	//TODO: throw an exception in case there are no orders.
	List<Order> getAllOrders();
	//TODO ver si es util
	/**
	 * Method that retrieves a list that contains all the orders whose status matches the one received as parameter.
	 * @param status variable of type Status that contains the status we are interested in (in order to see the orders that have this status)
	 * @return a list that contains the orders with the received status.
	 */
	//TODO: should this method throw an exception in case there are no orders with the specified status?
	List<Order> getOrdersByStatus(Status status);
	
	/**
	 * Method that checks whether an order exists with the order id received as parameter or not.
	 * @param order_id integer that stores the id of the order we want to check its existence.
	 * @return true if the order exists.
	 */
	//TODO: throw an exception
	//TODO SEE IF THIS METHOD IS USEFUL
	boolean orderExists(int order_id);
	
	

}
