package db.pojos;

import java.io.Serializable;
import java.util.Objects;

public class ProductOrder implements Serializable {

	private static final long serialVersionUID = 1794207772823018743L;
	private Integer productOrderID;
	private Integer amount;
	private Float total_price;
	private Order order;
	private Product product;

	public ProductOrder() {
		super();
	}

	// Getters and setters
	public Integer getOrderProductID() {
		return productOrderID;
	}

	public void setOrderProductID(Integer orderProductID) {
		this.productOrderID = orderProductID;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Float getTotal_price() {
		return total_price;
	}

	public void setTotal_price(Float total_price) {
		this.total_price = total_price;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	// equals and hashCode
	@Override
	public int hashCode() {
		return Objects.hash(productOrderID);
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
		return Objects.equals(productOrderID, other.productOrderID);
	}

// toString
	@Override
	public String toString() {
		return "ProductOrder [productOrderID=" + productOrderID + ", amount=" + amount + ", total_price=" + total_price
				+ ", product=" + product + "]";
	}

}
