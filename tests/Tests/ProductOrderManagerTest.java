package Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartJDBC.ClientManager;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.OrderManager;
import HospiCartJDBC.ProductManager;
import HospiCartJDBC.ProductOrderManager;
import HospiCartJDBC.SupplierManager;
import HospiCartPOJOs.Category;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Manufacturer;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.PaymentMethod;
import HospiCartPOJOs.PaymentStatus;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.ProductOrder;
import HospiCartPOJOs.Shipment;
import HospiCartPOJOs.Supplier;

public class ProductOrderManagerTest {
	// Define global variables that are going to be needed in several tests
	private static ConnectionManagerJDBC connectionManager;
	private static ClientManager clientManager;
	private static OrderManager orderManager;
	private static SupplierManager supplierManager;
	private static ProductManager productManager;
	private static ProductOrderManager productOrderManager;
	
	@BeforeAll
	static void initAll() {
		// This annotation is used to signal that the annotated method should be executed before all tests in the current test class.
        // In our case, this annotation allows us to create tables once.
        // Initialize the ConnectionManager (creates tables)
		connectionManager = new ConnectionManagerJDBC();
		clientManager = new ClientManager(connectionManager);
		supplierManager = new SupplierManager(connectionManager);
        productManager = new ProductManager(connectionManager);
        orderManager = new OrderManager(connectionManager);
        productOrderManager = new ProductOrderManager(connectionManager);
    }
	
	 @BeforeEach
	    void cleanTables() throws Exception {
		 // This annotation is used to signal that the annotated method should be executed before each @Test method in the current test class.
	     // Crucial for wiping or resetting data so tests can’t influence each other.
	     // Ensure a clean state
		 
		 Connection c = connectionManager.getConnection();
	        try (Statement s = c.createStatement() ) {
	        	// Delete in reverse order of dependencies
	        	s.execute("DELETE FROM supplier");
	         	s.execute("DELETE FROM product");
	         	s.execute("DELETE FROM product_order");
	            s.execute("DELETE FROM payment");
	            s.execute("DELETE FROM shipment");
	            s.execute("DELETE FROM client");
	            s.execute("DELETE FROM client_order");
	            c.commit();
	        }
	    }
	 
	 @AfterAll
     static void tearDown() {
    	 // This annotation is used to signal that the annotated method should be executed after all tests in the current test class.
         // Tear down shared resources (e.g. close the database connection)
         connectionManager.disconnect();
        }
	
	 // TODO REVISE THIS TEST, IT DOESN'T WORK YET 
	@Test
	public void insertProductOrderTest() {
		/* PIPELINE OF INSTRUCTIONS
		 * Because of our database’s foreign‐key constraints:
		 * Order depend on Client -> so we need to create and insert a client before creating and inserting the order 
		 * Product depend on Supplier -> so we need to create and insert a supplier before creating and inserting the product
		 */
		try {
			// Create and insert an 'incomplete' client from Java
			Client expectedClient = new Client("Belen", "Esteban", 656329185, "belenesteban@gmail.com", "Vallekas 3"); 
        	clientManager.insertClient(expectedClient); // throws ClientException
        	
			// Create and insert a supplier 
        		// To create a suppler, I have to create a payment, a shipment and 2 products.
    		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
    		Shipment shipment = new Shipment(123456);
    		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
    		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
    		
    			//I create a list of products and add the products I created above.
    		List<Product> products = new ArrayList<Product>();
    		products.add(product1);
    		products.add(product2);
    		
			Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34"); 
			supplierManager.insertSupplier(supplier); // throws SQLException
			
			// Create and insert the order 
			//Now, I create the order
			Order order = new Order(expectedClient, payment, shipment);
			orderManager.insertOrder(order);
			
			// Create and insert the product order 
			int amount = 5;
			ProductOrder expectedProductOrder1 = new ProductOrder(amount, order, product1);
			productOrderManager.insertProductOrder(expectedProductOrder1);
			
			// Check the product order 
			List<ProductOrder> lines = productOrderManager.getProductOrdersByOrderID(order.getOrderId());
			ProductOrder actualProductOrder1 = lines.get(0);
			
			assertEquals(1, lines.size(), "One line‐item should be present");

			assertEquals(expectedProductOrder1, actualProductOrder1);
			
			// Verify stock was reduced
		} catch (ClientException ce) {
    		ce.printStackTrace();
    	} catch (SQLException sqle) {
    		sqle.printStackTrace();
    	}
	}
	
	/*void deleteProductOrdersByOrderID(int order_id) throws SQLException, OrderExceptions, ClientException;
	
	void deleteProductOrderByIDs(int product_id, int order_id) throws SQLException, OrderExceptions, ClientException;
	
	ProductOrder getProductOrderByIDs(int product_id, int order_id) throws OrderExceptions, ClientException;
	
	List<ProductOrder> getProductOrdersByOrderID(int order_id) throws OrderExceptions, ClientException;
	
	double getTotalPriceOfAnOrder(int order_id);
	
	List<Order> getOrdersWithAProduct(int product_id) throws OrderExceptions, ClientException;
	
	void addProductToStockQuantity(int product_id, int amount) throws SQLException;
	
	public void removeProductFromStockQuantity(int product_id, int amount)throws SQLException;

	void updateProductAmountInAnOrder(int product_id, int order_id, int amount)throws SQLException;
	*/
}
