package HospiCartPOJOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Product")
public class Product implements Serializable {

	private static final long serialVersionUID = 3199455987362996759L;
	
	@XmlAttribute(name = "ID")
	private Integer productId;
	@XmlElement(name = "Supplier")
	private Supplier supplier;
	@XmlTransient
	private List<ProductOrder> productOrders; // 1 Product has many ProductOrders 
	@XmlElement(name = "Name")
	private String name;
	@XmlElement(name = "Description")
	private String description;
	@XmlElement(name = "Category")
	private Category category;
	@XmlElement(name = "Price")
	private Float price; 
	//We set required to false in order to state that this attribute is not mandatory but optional
	@XmlElement(name = "Stock_Quantity", required = false)
	private Integer stockQuantity;
	@XmlAttribute(name = "Need_Prescription")
	private Boolean needPrescription;

	/**
	 * Parameter-less constructor of Client in which the list of orders is created.
	 */
	public Product() {
		super();
		this.productOrders = new ArrayList<ProductOrder>();

	}

	public Product(int productId, String name, Category category, String description, Float price, int stockQuantity, boolean needPrescription) {
        this.productId = productId;          
        this.name = name;                       
        this.category = category;                
        this.description = description;          
        this.price = price;                      
        this.stockQuantity = stockQuantity;     
        this.needPrescription = needPrescription; 
    }
	//TODO: THINK IF WE HAVE TO INITIALISE THE LIST OF PRODUCT ORDERS
	public Product(String name, Category category, String description, Float price, int stockQuantity, boolean needPrescription) {
       super();
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

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
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
		return productId == other.productId;
	}

	// toString
	@Override
	public String toString() {
		//TODO we are not printing the needPrescription nor the amount in stock (because it is not relevant to the user)
//	           "  - Amount in stock: " + stockQuantity + "\n" +
//	           "  - Need prescription: " + needPrescription + "\n\n";  
		return "Product: \tProduct ID = " + this.productId + "\tProduct Name = " + this.name + "\t\tPrice = " + this.price + "\tCategory = " + this.category 
				+ "\t\tSupplier = " + this.supplier.getCompanyName()  + "\t\t\tDescription = " + this.description;
	}
}