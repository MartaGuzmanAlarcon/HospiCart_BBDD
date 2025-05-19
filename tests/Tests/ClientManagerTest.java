package Tests;
import HospiCartPOJOs.Client;
import org.junit.jupiter.api.*;

import Exceptions.ClientException;
import HospiCartJDBC.ClientManagerJDBC;
import HospiCartJDBC.ConnectionManagerJDBC;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientManagerTest {
	// Define global variables that are going to be needed in several tests
	 private static ConnectionManagerJDBC connectionManager;
	 private static ClientManagerJDBC clientManager;
    
    @BeforeAll 
    static void initAll() throws Exception {
    	// This annotation is used to signal that the annotated method should be executed before all tests in the current test class.
        // In our case, this annotation allows us to create tables once.
        // Initialize the ConnectionManager (creates tables)
        
        connectionManager = new ConnectionManagerJDBC();
        clientManager = new ClientManagerJDBC(connectionManager);
    }

    @BeforeEach
    void cleanTable() throws Exception {
    	// This annotation is used to signal that the annotated method should be executed before each @Test method in the current test class.
        // Crucial for wiping or resetting data so tests can’t influence each other.
        // Ensure a clean state
        
        Connection c = connectionManager.getConnection();
        try ( Statement s = c.createStatement() ) {
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
    void insertAndGetAValidClientTest() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Belen", "Esteban", 656329185, "belenesteban@gmail.com", "Vallekas 3"); 
        	clientManager.insertClient(expectedClient); // throws ClientException
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	Client actualClient = clientManager.getClientByID(idRetrieved); // throws ClientException
        	
        	// Compare both clients
        	assertEquals(expectedClient, actualClient);
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}
    	
    }
    
    @Test
    void insertAndGetClientTest2() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Juan", "Esteban", 656329185, "juanesteban@gmail.com", "Vallekas 3"); 
        	clientManager.insertClient(expectedClient); // throws ClientException
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	Client actualClient = clientManager.getClientByID(idRetrieved);
        	
        	// Compare both clients
        	assertEquals(expectedClient, actualClient);
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}
    	
    }
    
    @Test
    void instertTheSameClientTwiceTest() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client client1 = new Client("Rita", "La Cantaora", 656329185, "ritalacantaora@gmail.com", "Sevilla 88"); 
        	clientManager.insertClient(client1); // throws ClientException
        	
        	// Insert the same client again
        	Client client2 = new Client("Rita", "La Cantaora", 656329185, "ritalacantaora@gmail.com", "Sevilla 88"); 
        	
        	// Check if an exception was thrown 
        	assertThrows( ClientException.class, () -> clientManager.insertClient(client2),
                    "Inserting a duplicate client should throw an exception");
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}
    }
    
    @Test
    void deleteClientWithAValidIDTest() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Maria", "Martinez", 656329185, "mariamartinez@gmail.com", "Boadilla 2"); 
        	clientManager.insertClient(expectedClient);  // throws ClientException 
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	assertNotNull(idRetrieved, "insertClient should assign a database ID");
            assertNotNull(clientManager.getClientByID(idRetrieved),
                "getClientById should return the inserted client before deletion");
        	
        	// Remove the client 
            clientManager.deleteClientbyID(idRetrieved); // throws ClientException
            
            // Check if the client was deleted from the database and no longer exists 
            assertFalse(clientManager.isClientInDatabase(expectedClient.getEmail()),
                    "isClientInDatabase should be false once the client is deleted");
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}
    }
    
    
    @Test
    void deleteClientWithAnInvalidIDTest(){
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client client = new Client("Maria", "Martinez", 656329185, "mariamartinez@gmail.com", "Boadilla 2"); 
        	clientManager.insertClient(client);  // throws ClientException 
        	int existingId = client.getUserId();
        	
        	// Create an ID that we know is invalid (e.g. existingId + 100)
        	int invalidID = existingId + 100;
    		
            assertThrows( ClientException.class, () -> clientManager.deleteClientbyID(invalidID),
                    "Trying to delete a client with an invalid id should throw an exception");
    	} catch (ClientException ce) { 
    		// Use a catch just for the exception that insertClient() throws, because for deleteClientbyID()
    		//the exception is being captured in the assertThrows()
    		ce.printStackTrace();
    	}
    }
    
    
    @Test 
    void getListOfClientsTest() { 
    	try {
    		// Insert several 'incomplete' clients from Java with the constructor of Client that does not admit a user_id
        	Client client1 = new Client("Client", "One", 656329185, "clientone@gmail.com", "Chong Ching 2"); 
        	clientManager.insertClient(client1); // throws ClientException
        	
        	Client client2 = new Client("Client", "Two", 656329185, "clienttwo@gmail.com", "Chong Ching 2"); 
        	clientManager.insertClient(client2); // throws ClientException
        	
        	Client client3 = new Client("Client", "Three", 656329185, "clientthree@gmail.com", "Chong Ching 2"); 
        	clientManager.insertClient(client3); // throws ClientException
        	
        	// Retrieve all the clients 
        	List<Client> clients = clientManager.getListOfClients();
        	
        	// Define parameters to be compared later with assertEquals
        	int expectedNumberOfClients = 3;
        	int actualNumberOfClients = clients.size();
        	
        	assertEquals(expectedNumberOfClients, actualNumberOfClients);
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}   	
    }
    
   
    @Test
    void getClientByAnInvalidIDTest() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client client = new Client("Maria", "Martinez", 656329185, "mariamartinez@gmail.com", "Boadilla 2"); 
        	clientManager.insertClient(client);  // throws ClientException 
        	int existingId = client.getUserId();
        	
        	// Create an ID that we know is invalid (e.g. existingId + 100)
        	int invalidID = existingId + 100;
        	
        	// // Check if an exception was thrown when getting the the client by the id
        	assertThrows(ClientException.class, () -> clientManager.getClientByID(invalidID),
                    "getClientByID on invalid ID should throw ClientException");
	
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}  
    }
     
    @Test 
    void getClientByAValidEmailTest() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Maria", "Martinez", 656329185, "mariamartinez@gmail.com", "Boadilla 2"); 
        	clientManager.insertClient(expectedClient);  // throws ClientException 
        	

            // Retrieve the client from the DB using the email
            Client actualClient = clientManager.getClientByEmail("mariamartinez@gmail.com");

            // Check if the clients are equal (ID too)
            assertEquals(expectedClient, actualClient);
            assertEquals(expectedClient.getUserId(), actualClient.getUserId());
        	
        	
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}  
    }
    
    @Test
    void updateNameTest() { // Useful if a mistake when inserting the name was made 
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Pepa", "La Loca", 656329185, "juanalaloca@gmail.com", "Castilla 1500"); 
        	clientManager.insertClient(expectedClient);  // throws ClientException
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	
        	// Update the client's name 
        	clientManager.updateName(idRetrieved, "Juana");
        	
        	// Go to DB brpwser and check if Juana La Loca appears in the DB -> yes, it does 
        	
        	// TODO DECIDE IF THE UPDATE METHOD ONLY UPDATES THE DB OR IF IT ALSO SETS THE JAVA OBJECT
        	// NOW IT ONLY UPDATES THE DB SO:
        	// assertEquals("Juana", clientToUpdate.getName()); WOULD NOT WORK
        	        	
    	}  catch (ClientException ce) {
    		ce.printStackTrace();
    	}  catch (SQLException sqle) {
    		sqle.printStackTrace();
    	}
    	
    }
    
    @Test
    void updateSurnameTest() { // Useful when a woman's surname changes due to marriage 
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Ana María", "La Loca", 656329185, "anamarialajusticia@gmail.com", "Málaga 23"); 
        	clientManager.insertClient(expectedClient);  // throws ClientException
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	
        	// Update the client's surname
        	clientManager.updateSurname(idRetrieved, "La Justicia");
        	
        	// Go to DB brpwser and check if Ana Maria La Justicia appears in the DB -> yes, it does 
    		
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}  catch (SQLException sqle) {
    		sqle.printStackTrace();
    	}
    }
    
    @Test 
    void updatePhoneNumberTest() {
    	try {
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Paco", "Pozo", 0000, "pacopozo@gmail.com", "Villanueva de la Cañada"); 
        	clientManager.insertClient(expectedClient);  // throws ClientException
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	
        	// Update the client's phone number 
        	clientManager.updatePhoneNumber(idRetrieved, 683764339);
        	
        	// Go to DB brpwser and check if the new phone number appears in the DB -> yes, it does 
        	
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}  catch (SQLException sqle) {
    		sqle.printStackTrace();
    	}
    }
    
    @Test
    void updateAdressTest() {
    	try{
    		// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
        	Client expectedClient = new Client("Juanjo", "Arroyo", 683764339, "juanjoarroyo@gmail.com", "Camino del Real"); 
        	clientManager.insertClient(expectedClient);  // throws ClientException
        	
        	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
        	int idRetrieved = expectedClient.getUserId();
        	
        	// Updates the client's address
        	clientManager.updateAddress(idRetrieved, "Alcalá Meco");
        	
        	// Go to DB brpwser and check if the new address appears in the DB -> yes, it does 
        	
    	} catch (ClientException ce) {
    		ce.printStackTrace();
    	}  catch (SQLException sqle) {
    		sqle.printStackTrace();
    	}
    	
    }
    
    
    
    
    
}
