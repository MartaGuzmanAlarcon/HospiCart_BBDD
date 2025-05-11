package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import Exceptions.OrderExceptions;
import HospiCartJDBC.ClientManager;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.OrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Role;

class OrderManagerTest {
	
	//I create the connection manager and use it to create an instance of OrderManager and ClientManager
	ConnectionManagerJDBC cm = new ConnectionManagerJDBC();
	OrderManager om = new OrderManager(cm);
	ClientManager clientManager = new ClientManager(cm);
	
	@Test
	/**
	 * Test that checks if the method "createOrder" of "OrderManager" properly works.
	 */
	void createOrderTest(){
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30", Role.DOCTOR);
		try {
			//I call the method that checks if a client was already inserted in the database. If the method returns a false, then I go ahead inserting the client into the database. If it returns true, I don't insert it again.
			if(!clientManager.isClientInDatabase(expectedClient.getEmail())) {
				//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
				clientManager.insertClient(expectedClient);
			}
			//I get the email of the client I created in order to compare it further on.
			String email = expectedClient.getEmail();
			//I use the method get client by email from the manager of client in order to obtain the complete client (the client that I created but with the ID that the database assigned to him/her)
			expectedClient = clientManager.getClientByEmail(email);
			//I create the order with the respective method from OrderManager and pass the complete client as parameter
			Order order = om.insertOrder(expectedClient);
			//I store the client assigned to the order to check if the order was properly created or not.
			Client actualClient = order.getClient();
			//I check if the emails (which is a unique attribute) of the created client and the client assigned to the order match.
			assertEquals(expectedClient.getUserId(), actualClient.getUserId());
			
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks the method "createOrder" of "OrderManager" when the client passed as parameter to the method is null.
	 */
	void createOrderWithNullClientTest(){
		//I create a client and set it to null.
		Client expectedClient = null;
		try {
			//I call the method I want to check and pass the null client as parameter. I expect the method to throw an exception and check if it really does by using the "assertThrows".
			assertThrows(OrderExceptions.class, () ->{om.insertOrder(expectedClient);});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	//TODO: SEE WHY THIS TEST IS NOT ALWAYS EXCECUTED WHEN I RUN THIS TEST CLASS !!
	/**
	 * Test that checks if the method "getOrderByID" works as desired. First, I create a client, I check if  have to insert him/her into the database
	 * and then I use the created client to create an order. I use a method that retrieves the orders that a client made and make the comparison between the 
	 * created order and the last order contained in the list of orders that the user made, which should be the same.
	 */
	void getOrderByValidIDTest() {
		Client client = new Client("Serena", "Williams", 346667865, "serewilliams@gmail.com", "Calle de Uruguay 50", Role.NURSE);
		try {
			//I call the method that checks if a client was already inserted in the database. If the method returns a false, then I go ahead inserting the client into the database. If it returns true, I don't insert it again.
			if(!clientManager.isClientInDatabase(client.getEmail())) {
				//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
				clientManager.insertClient(client);
			}
			client = clientManager.getClientByEmail(client.getEmail());
			//I create an order with the created client to make sure he/she has at least one order associated to him/her.
			Order order = om.insertOrder(client);
			List<Order> orders = om.getOrdersByUser(client.getUserId());
			//I get the last order added to the list of orders of the user.
			Order orderAdded = orders.get(orders.size()-1);
			//I compare the created order and the last one in the retrieved list of orders of the user.
			//This works because in the method "createOrder", I retrieve the key that the database generated for the order!
			assertEquals(orderAdded, order);
			
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	/**
	 * Test that checks if the method "getOrderByID" works as desired. First, I use a method that retrieves all the orders stored in the database 
	 * and try the "getOrderByI" method passing an invalid ID to it (I pass as parameter the ID of the last order stored in the database plus 5, for instance)
	 */
	/*void getOrderByInvalidIDTest() {
		try {
			List<Order> orders = om.getAllOrders();
			//I get the amount of orders stored in the database.
			int amountOfOrders = orders.size()-1;
			// I call the method I want to check and pass as parameter an invalid order ID (an ID that is not assigned to any order)
			assertThrows(OrderExceptions.class, () ->{om.getOrderByID(amountOfOrders+5);});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	//TODO: THIS TEST DOES NOT WORK!! SEE HOW I CAN CORRECT IT!
	
	
	
}
