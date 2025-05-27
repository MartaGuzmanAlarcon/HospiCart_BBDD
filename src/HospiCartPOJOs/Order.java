package HospiCartPOJOs;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Exceptions.OrderExceptions;

public class Order implements Serializable {

	private static final long serialVersionUID = 5399184169574043556L;
	private Integer orderId;
	private Client client; // 1 Order has 1 Client 
	private Payment payment;
	private Shipment shipment; // 1 Order has 1 Shipment 
	private List<ProductOrder> productOrders; // 1 Order has many ProductOrders 
	private Date orderDate;
	private OrderStatus status;

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
	 * @throws OrderExceptions if any object received as parameter is null or if the order date is future.
	 */
	public Order(int _orderId, Client _client, Payment _payment, Shipment _shipment, List<ProductOrder> _productOrders, Date _orderDate, OrderStatus _status) throws OrderExceptions{
		super();
		
		//First, I check that the received objects are not null. If any of them is null, I won't be able to create the order and will throw the appropriate exception.
		if(_client == null || _client.getUserId() == null) {
			      //We throw a personalized exception
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_CLIENT);
		}
		if(_payment == null) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_PAYMENT);
		}
		if(_shipment == null) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_SHIPMENT);
		}
		if(_productOrders.isEmpty()) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_PRODUCT_ORDER);
		} else {
			for(int i=0; i<_productOrders.size(); i++) {
				if(_productOrders.get(i) == null || _productOrders.get(i).getProduct().getProductId() == null) {
					throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_PRODUCT_ORDER);
				}
			}
		}
		if(_orderDate.after(Date.valueOf(LocalDate.now()))){
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_DATE_FUTURE);
		}
		if(_status != OrderStatus.ORDERED && _status != OrderStatus.DELIVERED && _status != OrderStatus.CANCELLED) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_STATUS);
		}
		//TODO SHOULD I ALSO CHECK IF THE ORDER DATE IS IN THE PAST? i.e. before 2020 or something like that?
		this.orderId = _orderId;
		this.client = _client;
		this.payment = _payment;
		this.shipment = _shipment;
		this.productOrders = _productOrders;
		this.orderDate = _orderDate;
		this.status = _status;
	}
	
	/**
	 * Constructor of "Order" that receives one parameter per attribute the class has (except for order ID) and initializes them.
	 * @param _client object of the class "Client" that stores the client that made the order.
	 * @param _shipment object of the class "Shipment" that stores the shipment of the order.
	 * @param _productOrders list of objects of the class "ProductOrder" that stores all the product orders associated to the order.
	 * @throws OrderExceptions if any object received as parameter is null.
	 */
	public Order(Client _client, Shipment _shipment, List<ProductOrder> _productOrders) throws OrderExceptions{
		super();
		//First, I check that the received objects are not null. If any of them is null, I won't be able to create the order and will throw the appropriate exception.
		if(_client == null || _client.getUserId() == null) {
			      //We throw a personalized exception
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_CLIENT);
		}
		if(_shipment == null) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_SHIPMENT);
		}
		if(_productOrders.isEmpty()) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_PRODUCT_ORDER);
		} else {
			for(int i=0; i<_productOrders.size(); i++) {
				if(_productOrders.get(i) == null || _productOrders.get(i).getProduct().getProductId() == null) {
					throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_PRODUCT_ORDER);
				}
			}
		}		
		this.client = _client;
		this.shipment = _shipment;
		this.productOrders = _productOrders;
		//I initialize the order date to the present date.
		this.orderDate = Date.valueOf(LocalDate.now());
		//I initialize the status of the order to "PENDING" which is the 'default' status.
		this.status = OrderStatus.PENDING;
	}
	
	

	/**
	 * Constructor of "Order" that receives one parameter per attribute the class has 
	 * (except for order ID, order date, order status and the list of product orders) and initializes them.
	 * @param client object of the class "Client" that stores the client that made the order.
	 * @param shipment shipment object of the class "Shipment" that stores the shipment of the order.
	 * @throws OrderExceptions if any of the parameters is null.
	 */
	public Order(Client client, Shipment shipment) throws OrderExceptions {
		super();
		//First, I check that the received objects are not null. If any of them is null, I won't be able to create the order and will throw the appropriate exception.
		if(client == null || client.getUserId() == null) {
			      //We throw a personalized exception
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_CLIENT);
		}
		if(shipment == null) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_SHIPMENT);
		}
		this.client = client;
		this.shipment = shipment;
		this.productOrders = new ArrayList<ProductOrder>();
		//I initialize the order date to the present date.
		this.orderDate = Date.valueOf(LocalDate.now());
		//I initialize the status of the order to "PENDING" which is the 'default' status.
		this.status = OrderStatus.PENDING;
	}
	
	/**
	 * Constructor of "Order" that only receives a client as parameter. It creates an empty order (cart) for the given client.
	 * @param client object of the class "Client" that stores the client that made the order.
	 * @throws OrderExceptions OrderExceptions if any of the parameters is null.
	 */
	public Order(Client client) throws OrderExceptions {
		// Check that the received objects are not null. If any of them is null, no order will be created and a personalized exception is going to be thrown
	    if (client == null || client.getUserId() == null) {
	        throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_CLIENT);
	    }
	    // Initialize only the attributes of our interest according to the logic of the application 
	    this.client = client;
	    this.productOrders = new ArrayList<>(); // Initialize to an empty list of product orders -> empty cart
	    this.orderDate = Date.valueOf(LocalDate.now()); // Initialize the order date to the present date
	    this.status = OrderStatus.PENDING; // Initialize the status of the order to "PENDING" which is the 'default' status
	    // payment & shipment remain null until we call them in pay() method of DoctorMenu and NurseMenu
	}
	

	// Additional method to use LocalDate objects
	public void setLocalDateDob(LocalDate ldate) throws OrderExceptions {
		if(ldate.isAfter(LocalDate.now())){
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_ORDER_DATE_FUTURE);
		}
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
		return shipment;
	}


	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}



	public List<ProductOrder> getProductOrders() {
		return productOrders;
	}


	public void setProductOrders(List<ProductOrder> productOrders_) {
		this.productOrders = productOrders_;
	}


	public OrderStatus getOrderStatus() {
		return status;
	}


	public void setOrderStatus(OrderStatus status) throws OrderExceptions{
		if(status != OrderStatus.PENDING && status != OrderStatus.ORDERED && status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED) {
			throw new OrderExceptions(OrderExceptions.ErrorTypeOrder.INVALID_STATUS);
		}
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
		return "Order: \t\tOrder ID = " + this.orderId + "\t\tClient = " + this.client + "\t\tPayment = " + this.payment +
				"\t\tShipment = " + this.shipment + "\t\tProduct Orders = " + this.productOrders + "\t\tOrder Date = " + this.orderDate + "\t\tStatus = " + this.status;
	}
}
