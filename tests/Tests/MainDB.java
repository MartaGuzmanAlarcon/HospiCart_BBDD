package Tests;

import HospiCartJDBC.ConnectionManagerJDBC;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import HospiCartJDBC.ConnectionManagerJDBC;

public class MainDB {

	    @Test
	    void testConnectAndDisconnect() {
	        ConnectionManagerJDBC dbManager = new ConnectionManagerJDBC();
	    	dbManager.disconnect();
	    }
	}


