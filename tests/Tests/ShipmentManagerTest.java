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

public class ShipmentManagerTest {
	//I define global variables that are going to be needed in several tests
	
	 private static ConnectionManagerJDBC connectionManager;
	 private static ShipmentManager shipmentManager;
	 private static OrderManager orderManager;
	 private static ClientManager clientManager;
	 private static SupplierManager supplierManager;

   
   @BeforeAll 
   static void initAll() throws Exception {
   	// This annotation is used to signal that the annotated method should be executed before all tests in the current test class.
       // In our case, this annotation allows us to create tables once.
       // Initialize the ConnectionManager (creates tables)
       
       connectionManager = new ConnectionManagerJDBC();
       shipmentManager = new ShipmentManager(connectionManager);
       orderManager = new OrderManager(connectionManager);
       clientManager = new ClientManager(connectionManager);
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
           s.execute("DELETE FROM client");
           s.execute("DELETE FROM client_order");
           s.execute("DELETE FROM shipment");
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
   public void insertShipmentTest() {
	   Shipment shipment = new Shipment(123456);
	   
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
		
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
			
			//I call the method that returns a list that contains all the shipments made and store this amount as an integer.
			List <Shipment> shipmentBefore = shipmentManager.getAllShipments();
			int countTotalShipmentsBefore = shipmentBefore.size();
			
			//I call the method "insertOrder" from orderManager which is the one that calls the method "insertShipment" of "ShipmentManager"
			orderManager.insertOrder(order);
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Shipment> shipmentsAfter = shipmentManager.getAllShipments();
			int countTotalShipmentsAfter = shipmentsAfter.size();

			//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
		    assertEquals(countTotalShipmentsBefore + 1, countTotalShipmentsAfter);
	   } catch(SQLException e) {
			System.out.println("ERROR: " + e);
	   } catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
		}
   }
	
	@Test
	public void deleteShipmentByIDTest() {
		   Shipment shipment = new Shipment(123456);
		   
			//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
			Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
			
			//I create a payment, a shipment and 2 products.
			Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
			Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
			Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
			
			//I create a list of products and add the products I created above.
			List<Product> products = new ArrayList<Product>();
			products.add(product1);
			products.add(product2);
			
			//I create the supplier.
			Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
			
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
				orderManager.insertOrder(order);
				
				//I call the method that returns a list that contains all the shipments made and store this amount as an integer.
				List <Shipment> shipmentBefore = shipmentManager.getAllShipments();
				int countTotalShipmentsBefore = shipmentBefore.size();
				
				//I call the method that deletes a shipment.
				shipmentManager.deleteShipmentByID(shipment.getShipmentId());
				
				//I call the method that returns a list that contains all the orders made and store this amount on an integer.
				List <Shipment> shipmentsAfter = shipmentManager.getAllShipments();
				int countTotalShipmentsAfter = shipmentsAfter.size();

				//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
			    assertEquals(countTotalShipmentsBefore - 1, countTotalShipmentsAfter);
		   } catch(SQLException e) {
				System.out.println("ERROR: " + e);
		   } catch(ClientException ce) {
				System.out.println("ERROR: " + ce);
		   } catch(OrderExceptions oe) {
				System.out.println("ERROR: " + oe);
		   }
	}

	@Test
	public void deleteShipmentByTrackingNumber() {
		Shipment shipment = new Shipment(123456);
		   
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
		
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
			orderManager.insertOrder(order);
			
			//I call the method that returns a list that contains all the shipments made and store this amount as an integer.
			List <Shipment> shipmentBefore = shipmentManager.getAllShipments();
			int countTotalShipmentsBefore = shipmentBefore.size();
			
			//I call the method that deletes a shipment.
			shipmentManager.deleteShipmentByTrackingNumber(shipment.getTrackingNumber());
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Shipment> shipmentsAfter = shipmentManager.getAllShipments();
			int countTotalShipmentsAfter = shipmentsAfter.size();

			//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
		    assertEquals(countTotalShipmentsBefore - 1, countTotalShipmentsAfter);
	   } catch(SQLException e) {
			System.out.println("ERROR: " + e);
	   } catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
	   } catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
	   }
	}

	@Test
	public void deleteShipmentByOrderIDTest() {
		Shipment shipment = new Shipment(123456);
		   
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
		
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
			orderManager.insertOrder(order);
			
			//I call the method that returns a list that contains all the shipments made and store this amount as an integer.
			List <Shipment> shipmentBefore = shipmentManager.getAllShipments();
			int countTotalShipmentsBefore = shipmentBefore.size();
			
			//I call the method that deletes a shipment.
			shipmentManager.deleteShipmentByOrderID(order.getOrderId());
			
			//I call the method that returns a list that contains all the orders made and store this amount on an integer.
			List <Shipment> shipmentsAfter = shipmentManager.getAllShipments();
			int countTotalShipmentsAfter = shipmentsAfter.size();

			//I check that the list that contains all the orders has been increased by 1 (which ensures that the order was properly created)
		    assertEquals(countTotalShipmentsBefore - 1, countTotalShipmentsAfter);
	   } catch(SQLException e) {
			System.out.println("ERROR: " + e);
	   } catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
	   } catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
	   }		
	}
	
	@Test
	public void getShipmentByIDTest() {
		Shipment shipment = new Shipment(123456);
		   
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
		
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
			orderManager.insertOrder(order);
			
			Shipment retrievedShipment = shipmentManager.getShipmentByID(shipment.getShipmentId());
			
			assertEquals(shipment, retrievedShipment);
			
	   } catch(SQLException e) {
			System.out.println("ERROR: " + e);
	   } catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
	   } catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
	   }			
	}
	
	@Test
	public void getShipmentByTrackingNumberTest() {
		Shipment shipment = new Shipment(123456);
		   
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
		
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
			orderManager.insertOrder(order);
			
			Shipment retrievedShipment = shipmentManager.getShipmentByTrackingNumber(shipment.getTrackingNumber());
			
			assertEquals(shipment, retrievedShipment);
			
	   } catch(SQLException e) {
			System.out.println("ERROR: " + e);
	   } catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
	   } catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
	   }			
	}
	
	@Test
	public void getShipmentByOrderIDTest() {
		Shipment shipment = new Shipment(123456);
		   
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
		
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Product product1 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);
		Product product2 = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		Supplier supplier = new Supplier(products, Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
		
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
			orderManager.insertOrder(order);
			
			Shipment retrievedShipment = shipmentManager.getShipmentByOrderID(order.getOrderId());
			
			assertEquals(shipment, retrievedShipment);
			
	   } catch(SQLException e) {
			System.out.println("ERROR: " + e);
	   } catch(ClientException ce) {
			System.out.println("ERROR: " + ce);
	   } catch(OrderExceptions oe) {
			System.out.println("ERROR: " + oe);
	   }		
	}
	
	@Test
	public void getAllShipmentsTest() {
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
		int amountOfShipments = 4;

		Product product = new Product("Mask", Category.DISPOSABLES, "Mask for surgical protection", 15.0f, 1000, false);
		Product product1 = new Product("Insulin Syringe", Category.DISPOSABLES, "Syringe for insulin administration", 5.0f, 1000, false);
		Product product2 = new Product("ECG Monitor", Category.DIAGNOSTIC_TOOLS, "Portable ECG machine", 300.0f, 50, true);
		Product product3 = new Product("Paracetamol", Category.MEDICATIONS, "Analgesic", 50.0f, 200, true);

								
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product);
		products.add(product1);
		products.add(product2);
		products.add(product3);
								
		//I create the supplier.
		Supplier supplier = new Supplier(1, products ,Manufacturer.THERMO_FISHER, 1234, "Calle de Lisboa 34");
								
		ProductOrder productOrder1 = new ProductOrder(4, 20.0f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 1800.0f, product2);
		ProductOrder productOrder3 = new ProductOrder(4, 200.0f, product3);
		ProductOrder productOrder = new ProductOrder(9, 18.6f, product1);

		productOrder.setProduct(product);
		productOrder1.setProduct(product1);
		productOrder2.setProduct(product2);
		productOrder3.setProduct(product3);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);
		productOrders.add(productOrder3);
		productOrders.add(productOrder);
		
		try {
			//I insert the clients in the client table of the database, which automatically assigns an id to the incomplete client I created before.
			clientManager.insertClient(client);
			clientManager.insertClient(client1);
			supplierManager.insertSupplier(supplier);

			Order order = new Order(client, payment, shipment, productOrders);
			Order order1 = new Order(client, payment1, shipment1, productOrders);
			Order order2 = new Order(client, payment2, shipment2, productOrders);
			Order order3 = new Order(client, payment3, shipment3, productOrders);

			orderManager.insertOrder(order);
			orderManager.insertOrder(order1);
			orderManager.insertOrder(order2);
			orderManager.insertOrder(order3);
			
			List<Shipment> shipments = shipmentManager.getAllShipments();
			int realAmountOfShipments = shipments.size();
			
			//I compare the amount of shipments the user has and the amount that I expected (according to the amount of shipments I introduced in the database).
			assertEquals(amountOfShipments, realAmountOfShipments);
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
}
