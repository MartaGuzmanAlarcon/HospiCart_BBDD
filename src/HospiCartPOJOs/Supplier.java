package HospiCartPOJOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Supplier implements Serializable {
	
	private static final long serialVersionUID = 3452839978524351013L;
	private Integer supplierId;
	private List<Product> products;
    private Manufacturer companyName;
    private String contactPerson;
    private String address;
    
    
	public Supplier() {
		super();
		this.products = new ArrayList<Product>();
	}
	
	public Supplier(Manufacturer m) {
	 this.companyName=m;
	}
	
	public Supplier(int _supplierId, List<Product> _products, Manufacturer _companyName, String _contactPerson, String _address) {
		super();
		this.supplierId = _supplierId;
		this.products = _products;
		this.companyName = _companyName;
		this.contactPerson = _contactPerson;
		this.address = _address;
	}

	// getters and setters
	public Integer getSupplierId() {
		return supplierId;
	}


	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}



	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Manufacturer getCompanyName() {
		return companyName;
	}


	public void setCompanyName(Manufacturer companyName) {
		this.companyName = companyName;
	}


	public String getContactPerson() {
		return contactPerson;
	}


	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}

	// hashCode and equals
	@Override
	public int hashCode() {
		return Objects.hash(supplierId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supplier other = (Supplier) obj;
		return Objects.equals(supplierId, other.supplierId);
	}
	//toString
	@Override
	public String toString() {
		return "Supplier [supplierId=" + supplierId + ", companyName=" + companyName + ", contactPerson="
				+ contactPerson + ", address=" + address + "]";
	}
    


    
    

}
