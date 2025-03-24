package pojos;

import java.io.Serializable;
import java.util.Objects;

public class ProductOrder implements Serializable {

	private static final long serialVersionUID = 7095409871191391322L;
	private Integer orderIdProductId; // PK compuesta de orderId y productId
	private Product product;
	private Order order;
	private Integer amount;
	private Integer totalPrice;

	public ProductOrder() {
		super();
	}

	// getters and setters
	public Integer getOrderIdProductId() {
		return orderIdProductId;
	}

	public void setOrderIdProductId(Integer orderIdProductId) {
		this.orderIdProductId = orderIdProductId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

// hashcode and equals 

	@Override
	public int hashCode() {
		return Objects.hash(orderIdProductId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductOrder other = (ProductOrder) obj;
		return Objects.equals(orderIdProductId, other.orderIdProductId);
	}

	@Override
	public String toString() {
		return "ProductOrder [orderIdProductId=" + orderIdProductId + ", product=" + product + ", amount=" + amount
				+ ", totalPrice=" + totalPrice + "]";
	}
	
	

}
