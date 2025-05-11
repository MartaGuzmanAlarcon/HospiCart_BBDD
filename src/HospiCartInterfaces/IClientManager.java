package HospiCartInterfaces;

import java.util.List;

import HospiCartPOJOs.Client;

/**
 * The interface IClientManager includes all CRUD operations on the Client POJO plus 
 * a few finder helpers.
 */
public interface IClientManager {
	public void insertClient (Client c); // Insert a new client in the database.
	public void deleteClientbyID (Integer id) throws Exception;
	
	public List<Client> getListOfClients();
	public Client getClientByID (Integer c_id); // Look up a client by their primary key.
	public Client getClientByEmail (String c_email) throws Exception; // Find a client by email (useful for login & uniqueness checks).
	boolean isClientInDatabase(String c_email);
	
	// TODO ASK IF NAME, SURNAME AND EMAIL METHODS ARE REDUNDANT 
	public void updateName (Integer id, String name) throws Exception;
	public void updateSurname (Integer id, String surname) throws Exception;
	public void updatePhoneNumber (Integer id, Integer phone_number) throws Exception;
	public void updateAddress (Integer id,String address) throws Exception;
	public void updateEmail(Integer id, String email) throws Exception;
}
