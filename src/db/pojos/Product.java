package db.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product implements Serializable {

	private static final long serialVersionUID = 3199455987362996759L;
	private Integer productId;
	private Supplier supplier;
	private List<ProductOrder> productOrders;
	private String name;
	private String description;
	private Category category;
	private Integer price;
	private Integer stockQuantity;
	private Boolean needPrescription;

	public Product() {
		super();
		this.productOrders = new ArrayList<ProductOrder>();

	}

	// getters and setters

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<ProductOrder> getOrders() {
		return productOrders;
	}

	public void setOrders(List<ProductOrder> productOrders) {
		this.productOrders = productOrders;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public Boolean getNeedPrescription() {
		return needPrescription;
	}

	public void setNeedPrescription(Boolean needPrescription) {
		this.needPrescription = needPrescription;
	}

	// equals and hashCode
	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(productId, other.productId);
	}

	// toString
	@Override
	public String toString() {
	    return "-Product-" + "\n" +
	           "  productId: " + productId + "\n" +
	           "  supplier: " + supplier.getCompanyName() + "\n" +
	           "  name: " + name + "\n" +
	           "  description: " + description + "\n" +
	           "  category: " + category + "\n" +
	           "  price: " + price + "\n" +
	           "  stockQuantity: " + stockQuantity + "\n" +
	           "  needPrescription: " + needPrescription + "\n\n";  
	}

}
