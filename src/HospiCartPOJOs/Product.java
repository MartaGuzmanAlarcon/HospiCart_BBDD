package HospiCartPOJOs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product implements Serializable {

	private static final long serialVersionUID = 3199455987362996759L;
	private Integer productId;
	private Supplier supplier;
	private List<ProductOrder> productOrders; // 1 Product has many ProductOrders 
	private String name;
	private String description;
	private Category category;
	private BigDecimal price; 
	private Integer stockQuantity;
	private Boolean needPrescription;

	public Product() {
		super();
		this.productOrders = new ArrayList<ProductOrder>();

	}

	public Product(int productId, String name, Category category, String description, BigDecimal price, int stockQuantity, boolean needPrescription) {
        this.productId = productId;          
        this.name = name;                       
        this.category = category;                
        this.description = description;          
        this.price = price;                      
        this.stockQuantity = stockQuantity;     
        this.needPrescription = needPrescription; 
    }
	//TODO: THINK IF WE HAVE TO INITIALISE THE LIST OF PRODUCT ORDERS
	public Product(String name, Category category, String description, BigDecimal price, int stockQuantity, boolean needPrescription) {
        this.name = name;                       
        this.category = category;                
        this.description = description;          
        this.price = price;                      
        this.stockQuantity = stockQuantity;     
        this.needPrescription = needPrescription; 
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
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
