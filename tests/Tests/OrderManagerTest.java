package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import HospiCartJDBC.ClientManager;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.OrderManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Role;

class OrderManagerTest {
	
	@Test
	void createOrderTest(){
		//I create the connection manager and use it to create an instance of ProductOrder
		ConnectionManagerJDBC cm = new ConnectionManagerJDBC();
		OrderManager om = new OrderManager(cm);
		ClientManager clientM = new ClientManager(cm);
		
		//I create a client and an order and call the method that I want to test.
		Client client1 = new Client(1, "Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30", Role.DOCTOR);
		try {
			clientM.insertClient(client1);
			Order order = om.createOrder(client1);
			//I store the clients name to check if the order was properly created or not
			Client client = order.getClient();
			int clientID = client.getUserId();
			
			assertEquals(clientID, client1.getUserId());
			
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		}
	}
}
