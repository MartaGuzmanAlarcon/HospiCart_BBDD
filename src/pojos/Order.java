package pojos;

import java.io.Serializable;
import java.time.LocalDateTime; // cambiar mirar ejemplo xq hay q hacwr una transformacion sql!!
import java.util.Objects;

// Implementacion ejemplo
// need getters, setter,attributes and empty constructor
// need equals 
//implent Serializable 
// usar wrapper (Integer,Float...) not primitive types (int,float...)
// las FK implentar en abos lados en JAVA en BBDD solo en el many 
// PERO EL toString solo en un lado !!!
// Inicializar listas en los constructores

public class Order implements Serializable {

	private static final long serialVersionUID = 5399184169574043556L;
	private  Integer orderId;
	private  Integer userId; // FK  Client ESTO NO SE IMPLEMENTA ASI EXCAT Y EN BBDD EN1 SOLO LADO PERO AQUI EN AMBOS
	private LocalDateTime orderDate;
	private Status status;
	public LocalDateTime getOrderDate() {
		return orderDate;
	}
	
	
	// si hubiera lista o algo inicializar!
	
	public Order() {
		super();
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
