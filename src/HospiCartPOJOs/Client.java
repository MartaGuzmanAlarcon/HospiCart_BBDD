package HospiCartPOJOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client implements Serializable {

	private static final long serialVersionUID = -2672315887844188653L;
	private Integer id;
	private String name;
	private String surname;
	private Integer phoneNumber;
	private String email; // TODO ASK IF THIS IS THE USERNAME 
	private String address;
	private Role role; // "doctor" o "nurse"
	private List<Order> orders; // 1 Client has many Orders 

	public Client() {
		super();
		this.orders = new ArrayList<Order>();
	}
	
	
	public Client(Integer id, String name, String surname, Integer phoneNumber, String email, String address,
			Role role) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		this.role = role;
	}

	public Client(String name, String surname, Integer phoneNumber, String email, String address,
			Role role) {
		super();
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		this.role = role;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
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
		return Objects.equals(id, other.id);
	}

	// toString  --> ORDER PRINT??
	@Override
	public String toString() {
		return "Client [id =" + this.id + ", name =" + this.name + ", surname =" + this.surname +
				", phoneNumber =" + this.phoneNumber + ", email =" + this.email + 
				", address =" + this.address + ", role =" + this.role + "]";
	}

}
