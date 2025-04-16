package db.pojos;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable {

	private static final long serialVersionUID = 5399184169574043556L;
	private Integer orderId;
	private Client client;
	private Payment payment;
	private Shipment Shipment;
	private List<ProductOrder> productOrders;
	private Date orderDate;
	private Status status;

	// empty constr.
	public Order() {
		super();
		this.productOrders = new ArrayList<ProductOrder>();

	}
	

	// Additional method to use LocalDate objects
	public void setLocalDateDob(LocalDate ldate) {
		this.orderDate = Date.valueOf(ldate);
	}

	// Additional method to use LocalDate objects
	public LocalDate getLocalDateDob() {
		return this.orderDate.toLocalDate();
	}

	// getters and setters

	public Date getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Integer getOrderId() {
		return orderId;
	}


	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}


	public Client getClient() {
		return client;
	}


	public void setClient(Client client) {
		this.client = client;
	}


	public Payment getPayment() {
		return payment;
	}


	public void setPayment(Payment payment) {
		this.payment = payment;
	}


	public Shipment getShipment() {
		return Shipment;
	}


	public void setShipment(Shipment shipment) {
		Shipment = shipment;
	}



	public List<ProductOrder> getProducts() {
		return productOrders;
	}


	public void setProducts(List<ProductOrder> products) {
		this.productOrders = products;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	// equals and hashCode -> orderId
	@Override
	public int hashCode() {
		return Objects.hash(orderId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return orderId == other.orderId;
	}

	// toString
	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", client=" + client + ", payment=" + payment + ", Shipment=" + Shipment
				+ ", productOrders=" + productOrders + ", orderDate=" + orderDate + ", status=" + status + "]";
	}


	

}
