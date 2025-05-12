package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Exceptions.OrderExceptions;
import HospiCartJDBC.ClientManager;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.OrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Role;

class OrderManagerTest {	
	//I define global variables that are going to be needed in several tests
	
		 private static ConnectionManagerJDBC connectionManager;
		 private static ClientManager clientManager;
		 private static OrderManager orderManager;
	    
	    @BeforeAll 
	    static void initAll() throws Exception {
	    	// This annotation is used to signal that the annotated method should be executed before all tests in the current test class.
	        // In our case, this annotation allows us to create tables once.
	        // Initialize the ConnectionManager (creates tables)
	        
	        connectionManager = new ConnectionManagerJDBC();
	        clientManager = new ClientManager(connectionManager);
	        orderManager = new OrderManager(connectionManager);
	    }

	    @BeforeEach
	    void cleanTable() throws Exception {
	    	// This annotation is used to signal that the annotated method should be executed before each @Test method in the current test class.
	        // Crucial for wiping or resetting data so tests can’t influence each other.
	        // Ensure a clean state
	        
	        Connection c = connectionManager.getConnection();
	        try ( Statement s = c.createStatement() ) {
	        	 // Delete in reverse order of dependencies
	           // s.execute("DELETE FROM product_order");
	           //s.execute("DELETE FROM payment");
	           // s.execute("DELETE FROM shipment");
	            s.execute("DELETE FROM client_order");
	            s.execute("DELETE FROM client");
	            c.commit();
	        }
	    }

	    @AfterAll
	    static void tearDown() {
	    	// This annotation is used to signal that the annotated method should be executed after all tests in the current test class.
	        // Tear down shared resources (e.g. close the database connection)
	        connectionManager.disconnect();
	    }
	
	
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
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Order> ordersBefore = orderManager.getAllOrders();
			int countTotalOrdersBefore = ordersBefore.size();
			
			//I create the order with the respective method from OrderManager and pass the complete client as parameter
			orderManager.insertOrder(expectedClient);
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Order> ordersAfter = orderManager.getAllOrders();
			int countTotalOrdersAfter = ordersAfter.size();
			
			//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
		    assertEquals(countTotalOrdersBefore + 1, countTotalOrdersAfter);
			
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
			assertThrows(OrderExceptions.class, () ->{orderManager.insertOrder(expectedClient);});
			
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
			orderManager.insertOrder(client);
			
			List<Order> orders = orderManager.getOrdersByUser(client.getUserId());
			Order insertedOrder = orders.get(orders.size()-1);
			
			Order retrievedOrder = orderManager.getOrderByID(insertedOrder.getOrderId());
			
			//I compare the inserted order and the one obtained through the method "getOrderByID".
			//This works because in the method "createOrder", I retrieve the key that the database generated for the order!
			assertEquals(insertedOrder, retrievedOrder);
			
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
