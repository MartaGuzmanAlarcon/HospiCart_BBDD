package HospiCartPOJOs;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable {

	private static final long serialVersionUID = 5399184169574043556L;
	private Integer orderId;
	private Client client; // 1 Order has 1 Client 
	private Payment payment;
	private Shipment Shipment; // 1 Order has 1 Shipment TODO: CHANGE THE CAPITAL S OF THE NAME OF THE VARIABLE FOR AN s (lower case S)
	private List<ProductOrder> productOrders; // 1 Order has many ProductOrders 
	private Date orderDate;
	private Status status;

	/**
	 * Empty constructor of "Order" in which the list of product orders is created.
	 */
	public Order() {
		super();
		this.productOrders = new ArrayList<ProductOrder>();

	}
	
	/**
	 * Constructor of "Order" that receives one parameter per attribute the class has and initializes them.
	 * @param _orderId integer that stores the ID of the order.
	 * @param _client object of the class "Client" that stores the client that made the order.
	 * @param _payment object of the class "Payment" that stores the payment of the order.
	 * @param _shipment object of the class "Shipment" that stores the shipment of the order.
	 * @param _productOrders list of objects of the class "ProductOrder" that stores all the product orders associated to the order.
	 * @param _orderDate variable of type Date that stores the date in which the order was made.
	 * @param _status variable of the enumerate "Status" that stores the status of the order.
	 */
	public Order(int _orderId, Client _client, Payment _payment, Shipment _shipment, List<ProductOrder> _productOrders, Date _orderDate, Status _status) {
		super();
		this.orderId = _orderId;
		this.client = _client;
		this.payment = _payment;
		this.Shipment = _shipment;
		this.productOrders = _productOrders;
		this.orderDate = _orderDate;

		this.status = _status;
	}
	
	/**
	 * Constructor of "Order" that receives one parameter per attribute the class has (except for order ID) and initializes them.
	 * @param _client object of the class "Client" that stores the client that made the order.
	 * @param _payment object of the class "Payment" that stores the payment of the order.
	 * @param _shipment object of the class "Shipment" that stores the shipment of the order.
	 * @param _productOrders list of objects of the class "ProductOrder" that stores all the product orders associated to the order.
	 */
	public Order(Client _client, Payment _payment, Shipment _shipment, List<ProductOrder> _productOrders) {
		super();
		this.client = _client;
		this.payment = _payment;
		this.Shipment = _shipment;
		this.productOrders = _productOrders;
		//I initialize the order date to the present date.
		this.orderDate = Date.valueOf(LocalDate.now());
		//I initialize the status of the order to "ORDERED" which is the 'default' status.
		this.status = Status.ORDERED;

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



	public List<ProductOrder> getProductOrders() {
		return productOrders;
	}


	public void setProductOrders(List<ProductOrder> productOrders_) {
		this.productOrders = productOrders_;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		//TODO: this method should check if the received status is valid and throw an exception in the case it is not valid.
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
