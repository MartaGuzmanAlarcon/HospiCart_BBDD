package HospiCartPOJOs;

import java.io.Serializable;
import java.util.Objects;

public class Shipment implements Serializable {

	private static final long serialVersionUID = 3815867575863770546L;
	private Integer shipmentId;
	private Order order;
	private String trackingNumber;

	public Shipment() {
		super();
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

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
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
		return "Shipment [shipmentId=" + shipmentId + ", trackingNumber=" + trackingNumber + "]";
	}

}
