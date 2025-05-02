package HospiCartJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import HospiCartInterfaces.IClientManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Role;



public class ClientManager implements IClientManager{
	private ConnectionManagerJDBC manager;

	public ClientManager(ConnectionManagerJDBC m) {
		this.manager = m;
	}
	
	/**
	 * This method responsible for INSERTing a new Client in the database using Prepared Statements.
	 * This method does NOT provide any methods to input from the keyboard. 
	 * It only manages the insertion into the database.
	 * @param c the Client object
	 */
	@Override
	public void insertClient(Client c) {
		try {
			String sql = "INSERT INTO client (name, surname, phone_number, email, address, role)" + "VALUES (?,?,?,?,?,?)"; // 6 "?" corresponding to 6 expressions in the SQL sentence
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);
			
			prep.setString(1, c.getName()); // The 1 binds to the first "?". NOTICE THAT IT STARTS FROM 1, NOT 0
			prep.setString(2, c.getSurname());
			prep.setInt(3, c.getPhoneNumber());
			prep.setString(4, c.getEmail());
			prep.setString(5, c.getAddress());
			prep.setString(6, c.getRole().name()); // .name() Returns the name of this enum constant, exactly as declared in its enum declaration

			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)

		} catch (Exception e) {
			e.printStackTrace(); // To print where the error comes from
		}
	}
	
	/**
	 * This method deletes the Client with the id passed by parameter.
	 * @param  id the unique identifier of the client to delete.
	 * @throws Exception if no client exists with the given ID, or if a database error occurs
	 */
	@Override
	public void deleteClientbyId(Integer id) throws Exception {
		try {
			String sql = "DELETE FROM client WHERE id=?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);

			prep.setInt(1, id); // The 1 binds to the first and unique "?"

			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method retrieves all Client records (rows) from the database.
	 * @return a List of Client objects; never null but may be empty if there are no records.
	 */
	@Override
	public List<Client> getListOfClients() {
		List<Client> clients = new ArrayList<Client>();
		try {

			Statement stmt = manager.getConnection().createStatement();
			String sql = "SELECT * FROM client";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Integer id = rs.getInt("id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				Integer phoneNumber = rs.getInt("phone_number");
				String email = rs.getString("email");
				String address = rs.getString("address");
				Role role = Role.valueOf(rs.getString("role")); // valueOf() is a function from Enum, not a method.

				Client client = new Client(id, name, surname, phoneNumber, email, address, role);
				clients.add(client);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return clients;
	}

	/**
	 * This method retrieves a single Client by its id.
	 * @param c_id the unique identifier of the client to retrieve.
	 * @return the matching Client object, or null if no such client exists.
	 */
	@Override
	public Client getClientById(Integer c_id) {
		Client client = null;

		try {
			Statement stmt = manager.getConnection().createStatement();
			String sql = "SELECT * FROM client WHERE id=" + c_id;
			ResultSet rs = stmt.executeQuery(sql);

			Integer id = rs.getInt("id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			Integer phoneNumber = rs.getInt("phone_number");
			String email = rs.getString("email");
			String address = rs.getString("address");
			Role role = Role.valueOf(rs.getString("role")); // valueOf() is a function from Enum, not a method.

			client = new Client(id, name, surname, phoneNumber, email, address, role);

			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return client;
	}

	/**
	 * This method looks up a Client by their unique email address.
	 * @param c_email the email address to search for.
	 * @return the matching Client object.
	 * @throws Exception if no client is found or if a database error occurs.
	 */
	@Override
	public Client getClientByEmail(String c_email) throws Exception {
		Client client = null;

		try {
			Statement stmt = manager.getConnection().createStatement();
			String sql = "SELECT * FROM client WHERE email = '" + c_email + "'";

			ResultSet rs = stmt.executeQuery(sql);
			Integer id = rs.getInt("id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			Integer phoneNumber = rs.getInt("phone_number");
			String email = rs.getString("email");
			String address = rs.getString("address");
			Role role = Role.valueOf(rs.getString("role")); // valueOf() is a function from Enum, not a method.

			client = new Client(id, name, surname, phoneNumber, email, address, role);

			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return client;
	}

	/**
	 * This method updates the name field of the Client with the given ID.
	 * @param id the unique identifier of the client to update.
	 * @param name the new name to assign.
	 * @throws Exception if no client exists with the given ID, or if a database error occurs.
	 */
	@Override
	public void updateName(Integer id, String name) throws Exception {
		try {
			String sql = "UPDATE client SET name = ? WHERE id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);

			prep.setString(1, name); // The 1 binds to the first "?"
			prep.setInt(2, id); // The 2 binds to the second "?"
			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the surname field of the Client with the given ID.
	 * @param id the unique identifier of the client to update.
	 * @param surname the new surname to assign.
	 * @throws Exception if no client exists with the given ID, or if a database error occurs.
	 */
	@Override
	public void updateSurname(Integer id, String surname) throws Exception {
		try {
			String sql = "UPDATE client SET surname = ? WHERE id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);

			prep.setString(1, surname); // The 1 binds to the first "?"
			prep.setInt(2, id); // The 2 binds to the second "?"
			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the phone number field of the Client with the given ID.
	 * @param id the unique identifier of the client to update.
	 * @param phoneNumber the new phone number to assign.
	 * @throws Exception if no client exists with the given ID, or if a database error occurs.
	 */
	@Override
	public void updatePhoneNumber(Integer id, Integer phoneNumber) throws Exception {
		try {
			String sql = "UPDATE client SET phone_number = ? WHERE id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);

			prep.setInt(1, phoneNumber); // The 1 binds to the first "?"
			prep.setInt(2, id); // The 2 binds to the second "?"
			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the address field of the Client with the given ID.
	 * @param id the unique identifier of the client to update.
	 * @param address the new address to assign.
	 * @throws Exception if no client exists with the given ID, or if a database error occurs.
	 */
	@Override
	public void updateAddress(Integer id, String address) throws Exception {
		try {
			String sql = "UPDATE client SET address = ? WHERE id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);

			prep.setString(1, address); // The 1 binds to the first "?"
			prep.setInt(2, id); // The 2 binds to the second "?"
			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * This method updates the email field of the Client with the given ID.
	 * @param id the unique identifier of the client to update.
	 * @param email the new email address to assign.
	 * @throws Exception if no client exists with the given ID, or if a database error occurs.
	 */
	@Override
	public void updateEmail(Integer id, String email) throws Exception {
		try {
			String sql = "UPDATE client SET email = ? WHERE id = ?";
			PreparedStatement prep = manager.getConnection().prepareStatement(sql);

			prep.setString(1, email); // The 1 binds to the first "?"
			prep.setInt(2, id); // The 2 binds to the second "?"
			prep.executeUpdate(); // Executes the SQL statement in this PreparedStatement object, which must be an SQL DML statement; or an SQL DDL statement (which returns nothing)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
