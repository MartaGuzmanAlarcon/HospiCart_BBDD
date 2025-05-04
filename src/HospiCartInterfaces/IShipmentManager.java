package HospiCartInterfaces;

import java.sql.SQLException;
import java.util.List;

import HospiCartPOJOs.Order;
import HospiCartPOJOs.Shipment;

/**
 * Interface that defines the operations for managing shipments in the HospiCart system.
 * Implementing classes must provide functionality to create, retrieve and delete shipments.
 */
public interface IShipmentManager {
	
	/**
	 * Method that adds new shipments.
	 * @param order object of the class "Order" for which we want to create the shipment.
	 * @return the created shipment.
	 */
	Shipment createShipment(Order order) throws SQLException;
	
	/**
	 * Method that receives a shipment id as parameter and retrieves the shipment associated with the shipment id.
	 * @param shipment_id integer that stores a shipment ID.
	 * @return the found shipment whose shipment id matches with the received one.
	 */
	Shipment getShipmentByID(int shipment_id);

	/**
	 * Method that receives a tracking number as parameter and retrieves the shipment associated with the tracking number.
	 * @param tracking_number integer that stores a tracking number.
	 * @return the found shipment whose tracking number matches with the received one.
	 */
	Shipment getShipmentByTrackingNumber(int tracking_number);
	
	/**
	 * Method that receives an order id as parameter and retrieves the shipment associated with the order id.
	 * @param order_id integer that stores the id of an order.
	 * @return the shipment whose order id matches with the received one.
	 */
	Shipment getShipmentByOrderID(int order_id);
	
	/**
	 * Method that receives a shipment id and removes the shipment whose id matches with the received one.
	 * @param shipment_id integer that stores the id of the shipment we want to delete.
	 */
	void deleteShipmentByID(int shipment_id);
	
	/**
	 * Method that receives a tracking number and removes the shipment whose tracking number matches with the received one.
	 * @param tracking_number integer that stores the tracking number of the shipment we want to delete.
	 */
	void deleteShipmentByTrackingNumber(int tracking_number);
	
	/**
	 * Method that receives an order id and removes the shipment whose order id matches with the received one.
	 * @param order_id integer that stores the order id of the shipment we want to delete.
	 */
	void deleteShipmentByOrderID(int order_id);
	
	/**
	 * Method that receives an object of shipments and sets the order to null. This method is useful for when we delete shipments 
	 * and we want to set the order associated to the deleted shipments to null.
	 * @param shipment object of the class "Shipment".
	 */
	void setOrderToNull(Shipment shipment);
	
	/**
	 * Method that returns a list of shipments that contains them all.
	 * @return a list with objects of "Shipment"
	 */
	List<Shipment> getAllShipments();
}
