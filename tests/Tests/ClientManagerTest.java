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

public class ClientManagerTest {
	private static ConnectionManagerJDBC testConnMgr;
    private ClientManager mgr;

    @BeforeAll
    static void initDatabase() throws Exception {
        // Use your existing no‐arg constructor:
        testConnMgr = new ConnectionManagerJDBC();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Get the ClientManager that was initialized inside ConnectionManagerJDBC:
        mgr = testConnMgr.getClientManager();

        // Clean out the table so each test starts fresh:
        try (Connection c = testConnMgr.getConnection();
             Statement s = c.createStatement()) {
            s.execute("DELETE FROM client;");
            c.commit();
        }
    }

    @AfterAll
    static void tearDown() {
        // Close the DB file when done
        testConnMgr.disconnect();
    }

    @Test
    void testInsertAndGetById() throws Exception {
        Client c = new Client(null,
                              "Alice",
                              "Smith",
                              123456789,
                              "alice@example.com",
                              "123 Oak St",
                              Role.DOCTOR);
        mgr.insertClient(c);
        assertNotNull(c.getUserId(), "ID should be generated on insert");

        Client fetched = mgr.getClientById(c.getUserId());
        assertEquals(c, fetched, "Fetched client must equal the one inserted");
    }
}
