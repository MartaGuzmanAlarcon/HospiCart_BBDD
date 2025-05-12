package HospiCartPOJOs;

import javax.persistence.*;


import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "users") // creara la tabla users

public class User implements Serializable {

	
	private static final long serialVersionUID = -3381207443009484208L;

	
	@Id  // marca el campo como PK -> id es PK
	@GeneratedValue(generator = "users") // genera automáticamente el id
	@TableGenerator(name = "users", table = "sqlite_sequence",
		pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "users") // TODO ASK
	private Integer id;
	@Column(unique = true) // username tiene q ser unique
	private String username;
	private String password;
	private String email; // EL EMAIL ME DETERMINA EN ESTE CASO EL ROLE 
	@ManyToOne(fetch = FetchType.EAGER) //relacion desde la clase actual -> con la q relaciono, es decir user(n) --> role(1)
	@JoinColumn(name = "roleId") // FK: la FK va en el side d los many -> user --> roleId para conectarlo con esa tabla
	private Role role;
	
	public User() {
		super();
	}

	public User(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + ", role="
				+ role + "]";
	}
	
	
}
