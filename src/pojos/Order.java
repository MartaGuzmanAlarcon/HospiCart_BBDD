package pojos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

// Implementacion ejemplo
// need getters, setter,attributes and empty constructor
// need equals 
//implent Serializable 

public class Order implements Serializable {

	private static final long serialVersionUID = 5399184169574043556L;
	private  int orderId;
	private  int userId; // FK  Client
	private LocalDateTime orderDate;
	private Status status;
	public LocalDateTime getOrderDate() {
		return orderDate;
	}
	
	
	// Getters and Setters
	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}


	// equals and hasCode -> orderId
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


	
}
