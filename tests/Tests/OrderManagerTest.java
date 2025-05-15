package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
import HospiCartJDBC.PaymentManager;
import HospiCartJDBC.ProductManager;
import HospiCartJDBC.ProductOrderManager;
import HospiCartJDBC.ShipmentManager;
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

class OrderManagerTest {	
	//I define global variables that are going to be needed in several tests
	
		 private static ConnectionManagerJDBC connectionManager;
		 private static PaymentManager paymentManager;
		 private static ShipmentManager shipmentManager;
		 private static ProductOrderManager productOrderManager;
		 private static ProductManager productManager;
		 private static ClientManager clientManager;
		 private static OrderManager orderManager;
		 private static SupplierManager supplierManager;
	    
	    @BeforeAll 
	    static void initAll() throws Exception {
	    	// This annotation is used to signal that the annotated method should be executed before all tests in the current test class.
	        // In our case, this annotation allows us to create tables once.
	        // Initialize the ConnectionManager (creates tables)
	        
	        connectionManager = new ConnectionManagerJDBC();
	        orderManager = new OrderManager(connectionManager);
	        clientManager = new ClientManager(connectionManager);
	        paymentManager = new PaymentManager(connectionManager);
	        shipmentManager = new ShipmentManager(connectionManager);
	        productOrderManager = new ProductOrderManager(connectionManager);
	        productManager = new ProductManager(connectionManager);
	        supplierManager = new SupplierManager(connectionManager);
	        
	    }

	    @BeforeEach
	    void cleanTable() throws Exception {
	    	// This annotation is used to signal that the annotated method should be executed before each @Test method in the current test class.
	        // Crucial for wiping or resetting data so tests can’t influence each other.
	        // Ensure a clean state
	        
	        Connection c = connectionManager.getConnection();
	        try ( Statement s = c.createStatement() ) {
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
	    //TODO: delete the methods of the end.
	    //TODO: think about the exceptions and only use the ones we need.
	
	@Test
	/**
	 * Test that checks if the method "insertOrder" of "OrderManager" properly works.
	 */
	void insertOrderTest(){
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(123456);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
		
		ProductOrder productOrder1 = new ProductOrder(4, 200.0f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 90.0f, product2);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
		
		try {
			//I call the method that inserts clients after checking if the client was already inserted in the database.
			//I insert the client in the client table of the database, which automatically assigns an ID to the incomplete client I created before.
			clientManager.insertClient(expectedClient);
			//I insert the created supplier in the database, method in which the products will also be inserted.
			supplierManager.insertSupplier(supplier);
			
			//Now, I create the order
			Order order = new Order(expectedClient, payment, shipment, productOrders);
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Order> ordersBefore = orderManager.getAllOrders();
			int countTotalOrdersBefore = ordersBefore.size();
			
			//I create the order with the respective method from OrderManager and pass the complete client as parameter
			orderManager.insertOrder(order);
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Order> ordersAfter = orderManager.getAllOrders();
			int countTotalOrdersAfter = ordersAfter.size();

			//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
		    assertEquals(countTotalOrdersBefore + 1, countTotalOrdersAfter);
			
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if the method "deleteOrder" works as desired.
	 */
	void deleteOrderTest() {		
		Client client = new Client("Robert", "Williams", 346667855, "robwilliams@gmail.com", "Calle de Paraguay 20");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(123451);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
				
		//I create the supplier.
		Supplier supplier = new Supplier(products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		ProductOrder productOrder1 = new ProductOrder(4, 20.0f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 1800.0f, product2);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
		
		try {
			//I call the method that inserts clients after checking if the client was already inserted in the database.
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			//I insert the created supplier in the database, method in which the products will also be inserted.
			supplierManager.insertSupplier(supplier);

			//Now, I create the order
			Order order = new Order(client, payment, shipment, productOrders);
			
			//I create the order with the respective method from OrderManager and pass the complete client as parameter
			orderManager.insertOrder(order);
			//After inserting the order, payment, shipment and the product orders will already be inserted in the database.
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Order> ordersBefore = orderManager.getAllOrders();
			int countTotalOrdersBefore = ordersBefore.size();
			
			//I call the method that deletes the order from the database
			orderManager.deleteOrder(order.getOrderId());
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Order> ordersAfter = orderManager.getAllOrders();
			int countTotalOrdersAfter = ordersAfter.size();

			//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
		    assertEquals(countTotalOrdersBefore - 1, countTotalOrdersAfter);
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
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
		Client client = new Client("Serena", "Williams", 346667865, "serewilliams@gmail.com", "Calle de Uruguay 50");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(123437);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
				
		//I create the supplier.
		Supplier supplier = new Supplier(products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		ProductOrder productOrder1 = new ProductOrder(4, 20.0f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 1800.0f, product2);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
		
		try {
			//I call the method that inserts clients after checking if the client was already inserted in the database.
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			//I insert the created supplier in the database, method in which the products will also be inserted.
			supplierManager.insertSupplier(supplier);

			//Now, I create the order
			Order order = new Order(client, payment, shipment, productOrders);
			//I create the order with the respective method from OrderManager and pass the complete client as parameter
			orderManager.insertOrder(order);
			
			Order retrievedOrder = orderManager.getOrderByID(order.getOrderId());
			
			//I compare the inserted order and the one obtained through the method "getOrderByID".
			//This works because in the method "createOrder", I retrieve the key that the database generated for the order!
			assertEquals(order, retrievedOrder);
			
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) { //SEE IF WE NEED THIS CATCH
			e.printStackTrace();
		}
	}
	//TODO: THIS TEST DOES NOT WORK, I DON'T KNOW WHY
	@Test
	void getOrderByUserTest() {
		Client client = new Client("Bobby", "Brown", 343367865, "bobby@gmail.com", "Calle de Ambar 40");
		
		//I create a payment, a shipment and 2 products.
		Payment payment1 = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment2 = new Payment(8, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment1 = new Shipment(127452);
		Shipment shipment2 = new Shipment(123452);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.0f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 1800.0f, product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders1 = new ArrayList<ProductOrder>();
		List<ProductOrder> productOrders2 = new ArrayList<ProductOrder>();
		productOrders1.add(productOrder1);
		productOrders2.add(productOrder2);
		
		try {
			//I call the method that inserts clients after checking if the client was already inserted in the database.
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			//I insert the created supplier in the database, method in which the products will also be inserted.
			supplierManager.insertSupplier(supplier);
			
			//Now, I create the orders
			Order order1 = new Order(client, payment1, shipment1, productOrders1);
			Order order2 = new Order(client, payment2, shipment2, productOrders2);
			
			//I create an order with the created client to make sure he/she has at least one order associated to him/her.
			orderManager.insertOrder(order1);
			orderManager.insertOrder(order2);
			int amountOfOrdersOfUser = 2;
			
			List<Order> orders = orderManager.getOrdersByUser(client.getUserId());
			int realAmountOfOrdersOfUser = orders.size();
						
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersOfUser, realAmountOfOrdersOfUser);
			
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	/**
	 * Test that checks if "getOrdersByOrderDate" works as desired when there is only one order made in the date we are interested in (present date).
	 */
	void getOneOrderByOrderDateTest() {
		Client client = new Client("Rodrigo", "De Paul", 543367865, "rodri@gmail.com", "Calle de Argentina 80");
		
		//I create a payment, a shipment and 2 products.
		Payment payment1 = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(127497);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.0f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 1800.0f, product2);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);

		
		try {
			//I call the method that inserts clients after checking if the client was already inserted in the database.
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			//I insert the created supplier in the database, method in which the products will also be inserted.
			supplierManager.insertSupplier(supplier);
			
			//Now, I create the orders
			Order order1 = new Order(client, payment1, shipment, productOrders);
			
			//I create an order with the created client to make sure he/she has at least one order associated to him/her.
			orderManager.insertOrder(order1);
			int amountOfOrdersMadeToday = 1;
			
			List<Order> orders = orderManager.getOrdersByOrderDate(Date.valueOf(LocalDate.now()));
			int realAmountOfOrdersMadeToday = orders.size();
			
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMadeToday, realAmountOfOrdersMadeToday);
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if "getOrdersByOrderDate" works as desired when there are several orders made in the date we are interested in (present date).
	 */
	void getSeveralOrdersByOrderDateTest() {
		Client client = new Client("Rodrigo", "De Paul", 543367865, "rodri@gmail.com", "Calle de Argentina 80");
		
		//I create a payment, a shipment and 2 products.
		Payment payment1 = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment2 = new Payment(6, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment3 = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment1 = new Shipment(127457);
		Shipment shipment2 = new Shipment(327457);
		Shipment shipment3 = new Shipment(427457);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.4f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		ProductOrder productOrder3 = new ProductOrder(4, 18.6f, product2);

		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
		
		try {
			//I call the method that inserts clients after checking if the client was already inserted in the database.
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			supplierManager.insertSupplier(supplier);
			
			//Now, I create the orders
			Order order1 = new Order(client, payment1, shipment1, productOrders);
			Order order2 = new Order(client, payment2, shipment2, productOrders);
			Order order3 = new Order(client, payment3, shipment3, productOrders);

			//I create an order with the created client to make sure he/she has at least one order associated to him/her.
			orderManager.insertOrder(order1);
			orderManager.insertOrder(order2);
			orderManager.insertOrder(order3);
			int amountOfOrdersMadeToday = 3;
						
			List<Order> orders = orderManager.getOrdersByOrderDate(Date.valueOf(LocalDate.now()));
			int realAmountOfOrdersMadeToday = orders.size();
					
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMadeToday, realAmountOfOrdersMadeToday);
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if "getOrdersByOrderDate" works as desired when there are 0 orders made in the date we are interested in (present date).
	 */
	void getZeroOrderByOrderDateTest() {
		Client client = new Client("Rodrigo", "De Paul", 543367865, "rodri@gmail.com", "Calle de Argentina 80");
		
		try {
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);

			int amountOfOrdersMadeToday = 0;
			
			List<Order> orders = orderManager.getOrdersByOrderDate(Date.valueOf(LocalDate.now()));
			int realAmountOfOrdersMadeToday = orders.size();
						
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMadeToday, realAmountOfOrdersMadeToday);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if "getOrdersWithinDateRange" works as desired when there is only one order made within the dates we are interested in (a past date and the present date).
	 */
	void getOneOrderWithinDateRangeTest() {
		Client client = new Client("Nahuel", "Molina", 543667865, "nahuel@gmail.com", "Calle de Tucuman 80");
		
		//I create a payment, a shipment and 2 products.
		Payment payment1 = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(127457);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.4f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
		
		try {
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			productManager.insertProduct(product1);
			productManager.insertProduct(product2);
			shipmentManager.insertShipment(shipment);
			
			Order order = new Order(client, payment1, shipment, productOrders);

			//I create an order with the created client to make sure he/she has at least one order associated to him/her.
			orderManager.insertOrder(order);

			int amountOfOrdersMadeInDateRange = 1;
			
			List<Order> orders = orderManager.getOrdersWithinDateRange(Date.valueOf(LocalDate.of(2025, 05, 11)), Date.valueOf(LocalDate.now()));
			int realAmountOfOrdersMadeInDateRange = orders.size();
				
			//I insert the payment and product orders after the order has been created because it is dependent on the order
			payment1.setOrder(order);
			paymentManager.insertPayment(payment1);
			
			productOrder1.setOrder(order);
			productOrder2.setOrder(order);
			productOrderManager.insertProductOrder(productOrder1);
			productOrderManager.insertProductOrder(productOrder2);
			
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMadeInDateRange, realAmountOfOrdersMadeInDateRange);
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if "getOrdersWithinDateRange" works as desired when there are several orders made within the dates we are interested in (a past date and the present date).
	 */
	void getSeveralOrdersWithinDateRangeTest() {
		Client client1 = new Client("Rodrigo", "De Paul", 543367865, "rodri@gmail.com", "Calle de Argentina 80");
		Client client = new Client("Nahuel", "Molina", 543667865, "nahuel@gmail.com", "Calle de Tucuman 80");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment1 = new Payment(3, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(127457);
		Shipment shipment1 = new Shipment(137457);
		Product product1 = new Product("Gloves", Category.DISPOSABLES, "Latex blue gloves", 5.1f, 320, false);
		Product product2 = new Product("Masks", Category.DISPOSABLES, "Pink Masks", 3.1f, 220, false);
								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.4f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
	
		try {
			//I insert the clients in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			clientManager.insertClient(client1);
			productManager.insertProduct(product1);
			productManager.insertProduct(product2);
			shipmentManager.insertShipment(shipment);
			shipmentManager.insertShipment(shipment1);

			Order order = new Order(client, payment, shipment, productOrders);
			Order order1 = new Order(client, payment1, shipment, productOrders);


			//I create an order with the created client to make sure he/she has at least one order associated to him/her.
			orderManager.insertOrder(order);
			orderManager.insertOrder(order1);

			int amountOfOrdersMadeInDateRange = 2;
			
			List<Order> orders = orderManager.getOrdersWithinDateRange(Date.valueOf(LocalDate.of(2025, 05, 11)), Date.valueOf(LocalDate.now()));
			int realAmountOfOrdersMadeInDateRange = orders.size();
					
			//I insert payment and product orders after the order has been created because it is dependent on the order
			payment.setOrder(order);
			payment1.setOrder(order1);
			paymentManager.insertPayment(payment);
			paymentManager.insertPayment(payment1);
			
			productOrder1.setOrder(order1);
			productOrder2.setOrder(order);
			productOrderManager.insertProductOrder(productOrder1);
			productOrderManager.insertProductOrder(productOrder2);
			
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMadeInDateRange, realAmountOfOrdersMadeInDateRange);
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if "getOrdersWithinDateRange" works as desired when there are zero orders made within the dates we are interested in (a past date and the present date).
	 */
	void getZeroOrdersWithinDateRangeTest() {
		Client client1 = new Client("Rodrigo", "De Paul", 543367865, "rodri@gmail.com", "Calle de Argentina 80");
		Client client = new Client("Nahuel", "Molina", 543667865, "nahuel@gmail.com", "Calle de Tucuman 80");
		
		try {
			//I insert the client in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			clientManager.insertClient(client1);

			int amountOfOrdersMadeInDateRange = 0;
			
			List<Order> orders = orderManager.getOrdersWithinDateRange(Date.valueOf(LocalDate.of(2025, 05, 11)), Date.valueOf(LocalDate.now()));
			int realAmountOfOrdersMadeInDateRange = orders.size();
						
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMadeInDateRange, realAmountOfOrdersMadeInDateRange);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Test that checks if "getOrdersWithinDateRange" works as desired when there are several orders made within the dates we are interested in (a past date and the present date).
	 */
	void getAllOrdersTest() {
		Client client1 = new Client("Rodrigo", "De Paul", 543367865, "rodri@gmail.com", "Calle de Argentina 80");
		Client client = new Client("Nahuel", "Molina", 543667865, "nahuel@gmail.com", "Calle de Tucuman 80");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(7, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment1 = new Payment(3, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment2 = new Payment(2, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Payment payment3 = new Payment(4, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(127457);
		Shipment shipment2 = new Shipment(127757);
		Shipment shipment3 = new Shipment(123457);
		Shipment shipment1 = new Shipment(127557);
		Product product1 = new Product("Gloves", Category.DISPOSABLES, "Latex blue gloves", 5.1f, 320, false);
		Product product2 = new Product("Masks", Category.DISPOSABLES, "Pink Masks", 3.1f, 220, false);
								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.4f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		ProductOrder productOrder3 = new ProductOrder(2, 18.6f, product2);
		ProductOrder productOrder = new ProductOrder(9, 18.6f, product1);


		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
	
		
		try {
			//I insert the clients in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			clientManager.insertClient(client1);
			productManager.insertProduct(product1);
			productManager.insertProduct(product2);
			shipmentManager.insertShipment(shipment);
			shipmentManager.insertShipment(shipment1);
			shipmentManager.insertShipment(shipment2);
			shipmentManager.insertShipment(shipment3);

			Order order = new Order(client, payment, shipment, productOrders);
			Order order1 = new Order(client, payment1, shipment, productOrders);
			Order order2 = new Order(client, payment2, shipment, productOrders);
			Order order3 = new Order(client, payment3, shipment, productOrders);

			orderManager.insertOrder(order);
			orderManager.insertOrder(order1);
			orderManager.insertOrder(order2);
			orderManager.insertOrder(order3);

			int amountOfOrdersMade = 4;
			
			List<Order> orders = orderManager.getAllOrders();
			int realAmountOfOrdersMade = orders.size();
			
			//I insert the payment and product orders after the order has been created because it is dependent on the order
			payment.setOrder(order);
			payment1.setOrder(order1);
			payment2.setOrder(order2);
			payment3.setOrder(order3);
			paymentManager.insertPayment(payment);
			paymentManager.insertPayment(payment1);
			paymentManager.insertPayment(payment2);
			paymentManager.insertPayment(payment3);
			
			productOrder1.setOrder(order1);
			productOrder2.setOrder(order2);
			productOrder3.setOrder(order3);
			productOrder3.setOrder(order);

			productOrderManager.insertProductOrder(productOrder1);
			productOrderManager.insertProductOrder(productOrder2);
			productOrderManager.insertProductOrder(productOrder3);
			productOrderManager.insertProductOrder(productOrder);
			
			//I compare the amount of orders the user has and the amount that I expected (according to the amount of orders I introduced in the database).
			assertEquals(amountOfOrdersMade, realAmountOfOrdersMade);
		} catch(SQLException e) {
			System.out.println("ERROR: " + e);
		} catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
		} catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		} catch (Exception e) {
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
	
	 /*
	  * /*
	 * @Test
	/**
	 * Test that checks the method "createOrder" of "OrderManager" when the client passed as parameter to the method is null.
	 */
	//TODO CORRECT THIS TEST --> ADD EXCPTIONS IN  THE CONSTRUCTOR!! -->THIS TEST SHOULD GO IN "OrderTest"
	/*void insertOrderWithNullClientTest(){
		//I create a client and set it to null.
		Client expectedClient = null;
		
		
		try {
			//I call the method I want to check and pass the null client as parameter. I expect the method to throw an exception and check if it really does by using the "assertThrows".
			//assertThrows(OrderExceptions.class, () ->{orderManager.insertOrder(expectedClient);});
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	  */
}
