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
@XmlRootElement(name = "Client")
public class Client implements Serializable {

	private static final long serialVersionUID = -2672315887844188653L;
	
	@XmlAttribute(name = "Client ID")
	private Integer id;
	@XmlElement(name = "Name")
	private String name;
	@XmlElement(name = "Surame")
	private String surname;
	@XmlElement(name = "Phone Number")
	private Integer phoneNumber;
	@XmlElement(name = "Email")
	private String email;
	@XmlElement(name = "Address")
	private String address;
	//private Role role; // "doctor" o "nurse" TODO see if we have to delete this!
	@XmlTransient
	private List<Order> orders; // 1 Client has many Orders 
	
	/**
	 * Parameter-less constructor of Client in which the list of orders is created.
	 */
	public Client() {
		super();
		this.orders = new ArrayList<Order>();
	}
	
	/**
	 * Constructor that receives all the parameters.
	 * @param id
	 * @param name
	 * @param surname
	 * @param phoneNumber
	 * @param email
	 * @param address
	 * @param role
	 */
	public Client(Integer id, String name, String surname, Integer phoneNumber, String email, String address) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		//this.role = role;
	}

	/**
	 * Constructor that receives all the parameters except the ID.
	 * @param name
	 * @param surname
	 * @param phoneNumber
	 * @param email
	 * @param address
	 * @param role
	 */
	public Client(String name, String surname, Integer phoneNumber, String email, String address) {
		super();
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		
	}


	// Getters and Setters
	public Integer getUserId() {
		return id;
	}

	public void setUserId(Integer userId) {
		this.id = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Integer getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Integer phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	// equals and hashCode
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		return id == other.id;
	}

	// toString  --> ORDER PRINT??
	@Override
	public String toString() {
		return "Client: \t\tClient ID = " + this.id + "\t\tName = " + this.name + "\t\tSurname = " + this.surname +
				"\t\tPhone Number = " + this.phoneNumber + "\t\tEmail = " + this.email + "\t\tAddress = " + this.address;
	}

}
