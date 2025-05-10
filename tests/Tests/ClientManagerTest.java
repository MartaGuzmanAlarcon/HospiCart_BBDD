package Tests;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Role;
import org.junit.jupiter.api.*;

import HospiCartJDBC.ClientManager;
import HospiCartJDBC.ConnectionManagerJDBC;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// TODO REVISE THIS WHOLE CLASS AND FINISH IT 

public class ClientManagerTest {
	// Define global variables that are going to be needed in several tests
	ConnectionManagerJDBC connectionManager = new ConnectionManagerJDBC();
    ClientManager clientManager = new ClientManager(connectionManager);

    // TODO REVIEW THIS ANNOTATIONS AND MAKE THEM WORK!
    /*
    @BeforeAll 
    void initAll() throws Exception {
    	// This annotation is used to signal that the annotated method should be executed before all tests in the current test class.
        // In our case, this annotation allows us to create tables once.
        // Initialize the ConnectionManager (creates tables)
        connectionManager = new ConnectionManagerJDBC();
        clientManager = new ClientManager(connectionManager);
    }

    @BeforeEach
    void cleanTable() throws Exception {
    	// This annotation is used to signal that the annotated method should be executed before each @Test method in the current test class.
        // Crucial for wiping or resetting data so tests can’t influence each other.
        // Ensure a clean state
        try (Connection c = connectionManager.getConnection();
             Statement s = c.createStatement()) {
            s.execute("DELETE FROM client");
            c.commit();
        }
    }

    @AfterAll
    void tearDown() {
    	// This annotation is used to signal that the annotated method should be executed after all tests in the current test class.
        // Tear down shared resources (e.g. close the database connection)
        connectionManager.disconnect();
    }
    
    */
    
    
    @Test
    void insertClientTest() {
    	// Insert an 'incomplete' client from Java with the constructor of Client that does not admit a user_id
    	Client expectedClient = new Client("Belen", "Esteban", 656329185, "belenesteban@gmail.com", "Vallekas 3", Role.DOCTOR); 
    	clientManager.insertClient(expectedClient);
    	
    	// Retrieve the client id from the database once it has been assigned with AUTOINCREMENT
    	int idRetrieved = expectedClient.getUserId();
    	Client actualClient = clientManager.getClientById(idRetrieved);
    	
    	// Compare both clients
    	assertEquals(expectedClient, actualClient);
    	
    }
}
