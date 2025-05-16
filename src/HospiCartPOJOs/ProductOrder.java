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
	
	/**
	 * Constructor of Product Order that initializes all the parameters, computing the total order price of a product. 
	 * @param amount integer that stores the amount of a product that is contained in the product order.
	 * @param order Order object which contains a list of ProductOrder
	 * @param product Product object 
	 */
	public ProductOrder(int amount, Order order, Product product) {
		super();
		this.amount = amount;
		this.totalPrice = product.getPrice() * amount; // product.getPrice() retrieves the unit product price 
		this.order = order;
		this.product = product;
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
	/*public ProductOrder(int amount, float total_price) {
		super();
		this.amount = amount;
		this.totalPrice = total_price;
	}*/

	// Getters and setters

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float total_price) {
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
		return Objects.hash(order.getOrderId(), product.getProductId());
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
		return Objects.equals(order.getOrderId(), other.order.getOrderId()) && Objects.equals(product.getProductId(), other.product.getProductId());
	}

// toString
	@Override
	public String toString() {
		return "ProductOrder [amount=" + amount + ", total_price=" + totalPrice + ", product=" + product + "]";
	}

}
