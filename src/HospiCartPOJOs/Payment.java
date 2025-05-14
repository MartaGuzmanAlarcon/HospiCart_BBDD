package HospiCartPOJOs;

import java.io.Serializable;
import java.util.Objects;

public class Payment implements Serializable {

	private static final long serialVersionUID = 8072301622699680035L;
	private Integer paymentId;
	private Order Order; // 1 Payment has 1 Order 
	private Integer amount;
	private PaymentMethod paymentMethod;
	private PaymentStatus paymentStatus;

	/**
	 * Empty constructor of Payment.
	 */
	public Payment() {
		super();
	}
	
	/**
	 * Constructor that initializes all the parameters of "Payment".
	 * @param paymentId integer that stores the ID of the payment.
	 * @param order object of Order that stores the order to which the payment is related.
	 * @param amount integer that stores the amount of money of the payment.
	 * @param paymentMethod enumerate that represents the way in which the payment was made.
	 * @param paymentStatus enumerate that represents the current status of the payment.
	 */
	public Payment(Integer paymentId, HospiCartPOJOs.Order order, Integer amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
		super();
		this.paymentId = paymentId;
		Order = order;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
	}
	
	/**
	 * Constructor that initializes all the parameters of "Payment" except for its ID (which is assigned by the database; and retrieved from the database when needed)
	 * and the order object.
	 * @param paymentId integer that stores the ID of the payment.
	 * @param amount integer that stores the amount of money of the payment.
	 * @param paymentMethod enumerate that represents the way in which the payment was made.
	 * @param paymentStatus enumerate that represents the current status of the payment.
	 */
	public Payment(Integer paymentId, Integer amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
		super();
		this.paymentId = paymentId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
	}
	
	/**
	 * Constructor that initializes all the parameters of "Payment" except for its ID (which is assigned by the database; and retrieved from the database when needed)
	 * and the order object.
	 * @param amount integer that stores the amount of money of the payment.
	 * @param paymentMethod enumerate that represents the way in which the payment was made.
	 * @param paymentStatus enumerate that represents the current status of the payment.
	 */
	public Payment(Integer amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
		super();
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
	}


	// getters and setters

	public Order getOrder() {
		return Order;
	}

	public void setOrder(Order order) {
		Order = order;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	// hashcode and equals
	@Override
	public int hashCode() {
		return Objects.hash(paymentId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Payment other = (Payment) obj;
		return Objects.equals(paymentId, other.paymentId);
	}
	// toString

	@Override
	public String toString() {
		return "Payment [paymentId=" + this.paymentId + ", amount=" + this.amount + ", paymentMethod=" + 
				this.paymentMethod + ", paymentStatus=" + this.paymentStatus + "]";
	}

}
