package HospiCartPOJOs;

import java.io.Serializable;
import java.util.Objects;

public class ProductOrder implements Serializable {

	private static final long serialVersionUID = 1794207772823018743L;
	private Integer amount;
	private Float totalPrice; //TODO THIS SHOULD ALSO BE A BIG DECIMAL !!! CCHECK HOW IT SHOULD BE IN THE DATABASE
	private Order order; // 1 ProductOrder has 1 Order 
	private Product product; // 1 ProductOrder has 1 Product 

	/**
	 * Empty constructor of ProductOrder.
	 */
	public ProductOrder() {
		super();
	}
	
	public ProductOrder(int amount, float total_price, Order _order, Product _product) {
		super();
		this.amount = amount;
		this.totalPrice = total_price;
		this.order = _order;
		this.product = _product;
	}
	
	public ProductOrder(int amount, float total_price, Product _product) {
		super();
		this.amount = amount;
		this.totalPrice = total_price;
		this.product = _product;
	}
	
	/**
	 * Constructor of Product Order that initializes the amount of the product order and its total price.
	 * @param amount integer that stores the amount of a product that is contained in the product order.
	 * @param total_price floating number that stores the total price of the product order.
	 */
	public ProductOrder(int amount, float total_price) {
		super();
		this.amount = amount;
		this.totalPrice = total_price;
	}

	// Getters and setters

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Float getTotal_price() {
		return totalPrice;
	}

	public void setTotal_price(Float total_price) {
		this.totalPrice = total_price;
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

	@Override
	public int hashCode() {
		return Objects.hash(order, product);
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
		return Objects.equals(order, other.order) && Objects.equals(product, other.product);
	}

// toString
	@Override
	public String toString() {
		return "ProductOrder [amount=" + amount + ", total_price=" + totalPrice + ", product=" + product + "]";
	}

}
