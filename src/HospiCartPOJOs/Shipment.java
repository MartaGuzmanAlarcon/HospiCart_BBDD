package HospiCartPOJOs;

import java.io.Serializable;
import java.util.Objects;

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
	//TODO comment this constructor
	public Shipment(int _shipmentId, Order _order, int _trackingNumber) {
		super();
		this.shipmentId = _shipmentId;
		this.order = _order;
		this.trackingNumber = _trackingNumber;
	}
	
	public Shipment(int _shipmentId, int _trackingNumber) {
		super();
		this.shipmentId = _shipmentId;
		this.trackingNumber = _trackingNumber;
	}
	
	/**
	 * Constructor that initializes the tracking number of Shipment.
	 * @param trackingNumber integer that stores the tracking number of a shipment.
	 */
	public Shipment(int trackingNumber) {
		super();
		this.trackingNumber = trackingNumber;
	}

	/**
	 * Overloaded constructor that calls the method that generates a tracking number. This constructor will only be used when we want to create a new order.
	 * @param order object of Order.
	 */
	public Shipment(Order order) {
		super();
		this.order = order;
		this.trackingNumber = generateUniqueTrackingNumber(order.getOrderId());
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
	 * @param order_id integer that stores the order ID of the order of the shipment, which is used to create the unique tracking number of the order.
	 * @return a 10 character long string that is composed solely of numbers.
	 */
	private Integer generateUniqueTrackingNumber(int order_id) {
		Integer tracking_number = 100000 + order_id;
		return tracking_number;
		
		/* TODO
		 * I CHANGED THE ATTRIBUTE "TRACKING_NUMBER" FRO  STRING TO INTEGER (just like it is in the database) and also the getter and setter. 
		 * The method "generateUniqueTrackingNumber" was implemented using integer values for variables of type Integer but I don't think it is appropriate because it limits our program a lot.
		 * i.e. we would only be able to create 2,147,483,647 shipments (only 10 digits max) --> ASK RODRIGO IF WE CAN USE THIS ANYWAY, UNDERSTANDING ITS LIMITATIONS.
		 */
	}

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
		return "Shipment: \t\tShipment ID = " + this.shipmentId + "\t\tTracking Number = " + this.trackingNumber;
	}

}
