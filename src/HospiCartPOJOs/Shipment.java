package HospiCartPOJOs;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class Shipment implements Serializable {

	private static final long serialVersionUID = 3815867575863770546L;
	private Integer shipmentId;
	private Order order;
	private Integer trackingNumber;
	private static Integer shipments_counter = 0;

	/**
	 * Constructor of shipment. This constructor will be used for all the cases except for creating a new order.
	 */
	public Shipment() {
		super();
	}

	/**
	 * Overloaded constructor that calls the method that generates a tracking number. This constructor will only be used when we want to create a new order.
	 * @param order object of Order.
	 */
	public Shipment(Order order) {
		super();
		this.order = order;
		this.trackingNumber = generateUniqueTrackingNumber();
	}
	
	// getters and setters
	public Integer getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Integer shipmentId) {
		this.shipmentId = shipmentId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Integer getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(Integer trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	/**
	 * Method that generates a unique tracking number for every shipment object that is created.
	 * @return a 10 character long string that is composed solely of numbers.
	 */
	private Integer generateUniqueTrackingNumber() {
		//The number is 10 characters long because is the limit for Integer variables (the tracking_number is an Integer in the database)
		//String tracking_number = String.format("%010d", shipments_counter);
		Integer tracking_number = shipments_counter++;
		return tracking_number;
		
		/* TODO
		 * I CHANGED THE ATTRIBUTE "TRACKING_NUMBER" FRO  STRING TO INTEGER (just like it is in the database) and also the getter and setter. 
		 * The method "generateUniqueTrackingNumber" was implemented using integer values for variables of type Integer but I don't think it is appropriate because it limits our program a lot.
		 * i.e. we would only be able to create 2,147,483,647 shipments (only 10 digits max) --> ASK RODRIGO IF WE CAN USE THIS ANYWAY, UNDERSTANDING ITS LIMITATIONS.
		 */
	}
	//TODO OVERLOAD CONSTRUCTORS??? one should call the other?

	// hashCode and equals
	@Override
	public int hashCode() {
		return Objects.hash(shipmentId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shipment other = (Shipment) obj;
		return Objects.equals(shipmentId, other.shipmentId);
	}

	// toString
	@Override
	public String toString() {
		return "Shipment [shipmentId=" + shipmentId + ", trackingNumber=" + trackingNumber + "]";
	}

}
