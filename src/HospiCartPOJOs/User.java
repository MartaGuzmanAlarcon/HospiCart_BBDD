package HospiCartPOJOs;

import javax.persistence.*;

import Utilities.Encryption;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "users") // creara la tabla users

public class User implements Serializable {
	private static final long serialVersionUID = -3381207443009484208L;

	@Id  // marca el campo como PK -> id es PK
	@GeneratedValue(generator = "users") // We define the name of the generator
	@TableGenerator(name = "users", table = "sqlite_sequence",
		pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "users") // TODO ASK
	// "name = "users"" --> In the table generator, we define the name of the generator.
	// "table = "sqlite_sequence"" --> we specify which table stores the sequence numbers (sqlite_sequence, which is a special table in SQLite that tracks the auto-increment values)
	// "pkColumnName = "name"" --> Then we define the name of the column in the sequence table that contains entity names.
	// "valueColumnName = "seq"" --> name of the column that contains the current sequence value.
	//"pkColumnValue = "users"" --> this is the value stored in the pkColumnName column for this entity. It identifies which row in the sequence table belongs to the User entity
	private Integer id;
	@Column(unique = true) // username tiene q ser unique
	private String email;  //The email is both the email of the client and the username of the user!
	// EL EMAIL ME DETERMINA EN ESTE CASO EL ROLE TODO what does this mean??????
	//TODO I THINK WE HAVE TO DELETE USERNAME BECAUSE THE EMAIL IS THE USERNAME
	//private String username;
	private String password;
	@ManyToOne(fetch = FetchType.EAGER) //relacion desde la clase actual -> con la q relaciono, es decir user(n) --> role(1)
	@JoinColumn(name = "roleId") // FK: la FK va en el side d los many -> user --> roleId para conectarlo con esa tabla
	private Role role;
	
	public User() {
		super();
	}

//	public User(String username, String password, String email) {
//		super();
//		//this.username = username; //TODO DELETE THIS IF WE REMOVE THE ATTRIBUTE USERNAME
//		this.password = password;
//		this.email = email;
//	}
	public User(String email, String password) {
		super();
		this.password = password;
		this.email = email;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
//TODO REMOVE THESE 2 METHODS IF WE REMOVE THE ATTRIBUTE USERNAME
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		String encriptedPassword = Encryption.encryptPasswordMD5(password);
		this.password = encriptedPassword;
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
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", password=" + password + ", email=" + email + ", role="
				+ role + "]";
	}
	//TODO I REMOVED A FRAGMENT FROM THE TO STRING BECAUSE I THINK WE ARE REMOVING THE ATTRIBUTE USERNAME: ", username=" + username +
	
}
