package HospiCartApp;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartJDBC.ClientManagerJDBC;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.OrderManagerJDBC;
import HospiCartJDBC.ProductManagerJDBC;
import HospiCartJDBC.SupplierManagerJDBC;
//import HospiCartJPA.UserManagerJPA;
import HospiCartPOJOs.Category;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.OrderStatus;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.ProductOrder;
import HospiCartPOJOs.Supplier;
import HospiCartPOJOs.User;
import HospiCartXML.ManagerImplXML;
import Utilities.InputKB;
import Utilities.Output;

public class SupplierMenu {
	private ConnectionManagerJDBC connectionManager;
	private SupplierManagerJDBC supplierManager;
	private OrderManagerJDBC orderManager;
	private ProductManagerJDBC productManager;
	//private UserManagerJPA userManager;
	private ManagerImplXML xmlMan; 
	private ClientManagerJDBC clientManager;
	
	private Supplier supplier;
	private User user;
	private Client importedClient; // We store the imported client from XML in a global variable to use it in several methods 
	
	/**
	 * Constructor of SupplierMenu for the given supplier, wired up to the shared JDBC connection.
	 * @param cm the JDBC connection manager, which allows this class to be connected to the database.
	 * @param supplier the currently‐logged‐in suppler.
	 */
	public SupplierMenu(ConnectionManagerJDBC cm, User supplierUser) {
		// Initialize all the attributes 
		this.connectionManager = cm; // Passing one cm is simpler than passing five or six different manager objects, cm includes all of them
		this.productManager = new ProductManagerJDBC(cm);
		this.supplierManager = new SupplierManagerJDBC(cm);
		this.orderManager = new OrderManagerJDBC(cm);
		this.clientManager = new ClientManagerJDBC(cm);
		this.xmlMan = new ManagerImplXML();
		this.user = supplierUser; //TODO SEE IF THIS WORKS
		//userManager = new UserManagerJPA();
	}

	public void displaySupplierMenu() {
		// TODO Auto-generated method stub
		Output.println("\n\n=== Welcome to the Supplier Menu! ===");
		String chosenCompany = outputCompanyNames();
			supplier = supplierManager.getSupplierByCompanyName(chosenCompany);
			setProductsToSupplier(supplier);
			Output.println("Logged in as '" + supplier.getCompanyName()+"', welcome back!");
			displaySupplierMenuOptions();
	}
	
	public void displaySupplierMenuOptions() {
		Output.println("\n\nDear supplier, please introduce the number of the operation you wish to perform:");
		boolean keepGoing = true;
		while(keepGoing) {
			Output.println("1. View my personal data");
			Output.println("2. View the company's data");
			Output.println("3. Manage products");
			Output.println("4. Manage orders");
			Output.println("5. Import a Client profile (Doctor/Nurse) from an XML");
			Output.println("6. View imported client's profile"); 
			Output.println("0. Exit");
			try {	
				int option = InputKB.readInteger();
				switch(option) {
				case 1:
					viewPersonalData();
					keepGoing = false;
					break;
				case 2:
					viewCompanyData();
					keepGoing = false;
					break;
				case 3:
					manageProducts();
					keepGoing = false;
					break;
				case 4:
					manageOrders();
					keepGoing = false;
					break;
				case 5:
					importClientFromXml();
					break;
				case 6:
					viewClientsProfile();
					break;
				case 0:
					Output.println("Redirecting to home page ...");
					return; // Returning from this method sends control back to MainMenu
	            default:
	            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
	            	break;				
				}
			} catch(ClientException ce) {
				System.out.println("ERROR: " + ce);
			} catch (OrderExceptions oe) {
				System.out.println("ERROR: " + oe);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
	}
	
    /**
     * Method to properly close all database connections
     */
    private void closeConnections() {
        try {
            if (connectionManager != null) {
                connectionManager.disconnect();
            }
        } catch (Exception e) {
            System.out.println("Error closing connections: " + e.getMessage());
        }
    }
	
	/**
	 * Method that gets all the company names of the suppliers and outputs them to the user.
	 * @return the chosen number.
	 */
	private String outputCompanyNames(){
		List<String> companyNames = supplierManager.getCompanyNames();
		boolean keepGoing = true;
		int option = 0;
		
		while(keepGoing) {
			Output.println("\nIntroduce the number that corresponds to the name of your company:");
			if(!companyNames.isEmpty()) {
				for(int i=1; i< companyNames.size()+1; i++) {
					Output.println(" "+(i)+". "+ companyNames.get(i-1));
				}
				option = InputKB.readInteger();
				if(option > 0 && option <= companyNames.size()) {
					keepGoing = false;
				}
				//TODO ADD AN ELSE THAT PRINTS AN ERROR MESSAGE TO THE USER SAYING INVALID NUMBER 
			}
		}
		return companyNames.get(option-1);
	}
	
    /**
     * Method that receives a supplier and retrieves the products that this company supplier. Then, sets the list of products to the supplier company.
     */
    private void setProductsToSupplier(Supplier supplier) {
    	List<Product> productsOfSupplier = productManager.getProductsByManufacturer(supplier.getCompanyName());
    	supplier.setProducts(productsOfSupplier);
    }
    
    /**
     * Method that shows the user their personal data.
     */
    private void viewPersonalData() {
    	Output.println("\nUsername: " + user.getEmail());
    	System.out.println("Press 0 if you want to LOG OUT or another number otherwise");
    	int choice = InputKB.readInteger();
    	if(choice == 0) {
			 System.out.println("Hope to see you again soon!");
    		//TODO REDIRECT TO THE HOME PAGE AND LOG OUT THE USER
    	} else {
    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
    	}
    }

    
    /**
     * Method that shows the user his/her personal data and asks if he/she wants to change the password or not.
     */
    private void viewCompanyData() throws SQLException{
    	System.out.println(supplier);
    	
    	Output.println("\nPress:");

    	boolean keepGoing = true;
    	
    	while(keepGoing) {
        	Output.println("1. Change the company's address");
        	Output.println("2. Change the company's contact number");
        	Output.println("3. To go back");
        	Output.println("0. Go back");
        	
        	int option = InputKB.readInteger();
        	
	    	switch(option) {
	    	case 1: 
	    		Output.println("Please introduce the new address of the company: ");
	    		String newAddress = InputKB.readString();
	    		supplier.setAddress(newAddress);
	    		supplierManager.updateSupplierAddress(supplier.getSupplierId(), newAddress);
	    		keepGoing = false;
	    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
	    		break;
	    	case 2: 
	    		Output.println("Please introduce the new contact number of the company: ");
	    		int newContactNumber = InputKB.readInteger();
	    		supplier.setContactNumber(newContactNumber);
	    		supplierManager.updateSupplierContactNumber(supplier.getSupplierId(), newContactNumber);
	    		keepGoing = false;
	    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
	    		break;
	    	case 0: 
	    		displaySupplierMenuOptions();
	    		keepGoing = false;
	    		break;
            default:
            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
            	break;	
	    	}
    	}
    }
    /**
     * Method that outputs the menu regarding the administration of products and redirects the application according to the user's choice.
     * @throws SQLException
     */
    private void manageProducts() throws SQLException {
    	Output.println("\nPlease introduce the number of operation you wish to perform regarding your products: ");
    	boolean keepGoing = true;
    	while(keepGoing) {
    		Output.println("1. View my products");
    		Output.println("2. Add a new product");
    		Output.println("3. Delete a product");
    		Output.println("4. Manage my products' stock");
        	Output.println("5. If you want to exit");
    		Output.println("0. Go back");
    		int option = InputKB.readInteger();
    		switch(option) {
    		case 1: 
    			int amountProductsOfSupplier = supplier.getProducts().size();
    			for(int i = 0; i < amountProductsOfSupplier; i++) {
    				Product product = supplier.getProducts().get(i);
    				Output.println("Product: \tProduct ID = " + product.getProductId() + "\tProduct Name = " + product.getName() + "\tDescription = " + product.getDescription()
    				+ "\tCategory = " + product.getCategory() + "\tPrice = " + product.getPrice() + "\tAmount in stock = " + product.getStockQuantity() + "\tNeeds prescription = " + product.getNeedPrescription());
    			}
    			if(amountProductsOfSupplier == 0) {
    				Output.println("You do not have any products :(. Do not hesitate to add one!");
    			}
    			keepGoing = false;
	    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
    			break;
    		case 2: 
    			createNewProduct();
    			keepGoing = false;
	    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
    			break;
    		case 3:
    			Output.println("\nIntoduce the ID of the product you want to delete among the following ones: ");
    			int productID = getPrdocutIDs();
    			//I obtain the list of products and the index in whih the product we want to delete is.
    			List<Product> productsOfSupplier = supplier.getProducts();
    			int index = -1;
    			for(int i=0; i<productsOfSupplier.size(); i++) {
    				if(productsOfSupplier.get(i).getProductId() == productID) {
    					index = i;
    				}
    			}
    			//We delete the product both from the list of the supplier and from the database
    			productsOfSupplier.remove(index);
    			productManager.deleteProduct(productID);
    			//I obtain the list of products of the supplier and add the new product.
    			keepGoing = false;
	    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
    			break;
    		case 4:
    			manageProductsStock();
    			keepGoing = false;
	    		displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
    			break;
	    	case 5:
                closeConnections();
                Output.println("Application closed!");
                //If the user introduces a 0, then we set the variable "keepGoing" to false so we can exit the switch.
                System.exit(0);
                break;
	    	case 0:
    			keepGoing = false;
	    		displaySupplierMenuOptions();
	    		break;
            default:
            	Output.println("\nThe number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
            	break;	
    		}
    	}
    }
    /**
     * Method that asks the user to introduce the required data to create a product and inserts it in the database.
     * @throws SQLException
     */
    private void createNewProduct() throws SQLException {
    	//I ask the user to introduce all the data needed in order to add a new product to the database.
		Output.println("\nIntroduce the name of the new new product: ");
		String name = InputKB.readString();
		Output.println("Introduce the description of the new product: ");
		String description = InputKB.readString();
		Output.println("Introduce the number of the category of the new product out of the following ones: ");
		Category category = getCategoryOfPrdocuts();
		Output.println("Introduce the price of the new product: ");
		float price = InputKB.readFloat();
		Output.println("Introduce the amount in stock of the new product: ");
		int amountInStock = InputKB.readInteger();
		Output.println("Does the new product need prescription? (introduce true/false): ");
		String introducedString = InputKB.readString();
		boolean needPrescription = Boolean.parseBoolean(introducedString);
		Product newProduct = new Product(name, category, description, price, amountInStock, needPrescription);
		//I set the supplier of the product
		newProduct.setSupplier(supplier);
		//I call the method that inserts the products in the database
		productManager.insertProduct(newProduct);
		Output.println("\nThe product '" + newProduct.getName() + "' was successfully added!");
		//I obtain the list of products of the supplier and add the new product.
		List<Product> productsOfSupplier = supplier.getProducts();
		productsOfSupplier.add(newProduct);

    }
    
	 /**
	  * Method that shows the user the categories of the products.
	  * @return a variable of type Category.
	  */
	 private Category getCategoryOfPrdocuts() {
		 Category[] categories = Category.values();
		 while(true) {
			 for(int i = 0; i < categories.length; i++) {
				 Output.println(" " + i + ". " + " " + categories[i]);
			 }
			 int choice = InputKB.readInteger();
			 if(choice > -1 && choice < categories.length) {
				 return categories[choice];
			 } else {
				 Output.println("\nThe introduced number is invalid, please try again.");
			 }
		 }
	 }
	 
	 /**
	  * Method that shows the user the IDs of the products the supplier has associated to it.
	  * @return a variable of type integer that stores the ID of the product the user wants to delete from the database.
	  */
	 private Integer getPrdocutIDs() {
		 List<Integer> productIDs = new ArrayList<>();
		 for(int i=0; i< supplier.getProducts().size(); i++) {
			 productIDs.add(supplier.getProducts().get(i).getProductId());
		 }
		 while(true) {
			 for(int i = 0; i < productIDs.size(); i++) {
				 Output.println(" "+ i + ".  " + productIDs.get(i));
			 }
			 int choice = InputKB.readInteger();
			 if(choice > -1 && choice < productIDs.size()) {
				 return productIDs.get(choice);
			 } else {
				 Output.println("\nThe introduced number is invalid, please try again.");
			 }
		 }
	 }	 
	 
	 private void manageProductsStock() throws SQLException {
		 Output.println("\nIntroduce the umber of the operation you wish to perform regarding the stock of your products: ");
		 boolean keepGoing = true;
		 boolean restocking = true;
		 //TODO see the return statements
		 while(keepGoing) {
			 Output.println("1. Check and update products with low stock");
			 Output.println("2. Update all products' stock");
	     	 Output.println("3. If you want to exit");
			 Output.println("0. Go back");
			 int option = InputKB.readInteger();
			 List<Product> productsWithLowStock = new ArrayList<>();
			 switch(option) {
			 case 1:
				 for(int i=0; i<supplier.getProducts().size(); i++) {
					 Product product = supplier.getProducts().get(i);
					 boolean runningOutOfStock = productManager.checkLowStockAlert(product);
					 if(runningOutOfStock) {
						 productsWithLowStock.add(product);
					 }
				 }
				 if(productsWithLowStock.isEmpty()) {
					 Output.println("None of your products have low stock. Redirecting ...");
					 manageProducts();
					 keepGoing = false;
				 } else {
					 while(restocking) {
						 Output.println("Do you wish to re-stock these products? Press 1 for YES and 0 for NO");
						 int wantsToRestock = InputKB.readInteger();
						 if(wantsToRestock == 1) {
							 restockProducts(productsWithLowStock);
							 restocking = false;
						 } else if(wantsToRestock == 0) {
							 restocking = false;
							 manageProducts();
						 } else {
					         Output.println("\nThe number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
						 }
					 }
				 }
				 break;
			 case 2:
				 List<Product> allProductsOfSupplier = supplier.getProducts();
				 restockProducts(allProductsOfSupplier);
				 restocking = false;
				 break;
		    	case 3:
	                closeConnections();
	                Output.println("Application closed!");
	                //If the user introduces a 0, then we set the variable "keepGoing" to false so we can exit the switch.
	                System.exit(0);
	                break; 	 
         default:
         	Output.println("\nThe number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
         	break;
		 }
		 }
	 }
	 
	 /**
	  * Method that re-stocks the products that have low stock
	  * @param productsWithLowStock
	 * @throws SQLException 
	  */
	 private void restockProducts(List<Product> productsWithLowStock) throws SQLException {
		 boolean keepRestocking = true;
		 while(keepRestocking) {
			 Output.println("Intoduce the number of the product you want to restock: ");
			 for(int i=0; i<productsWithLowStock.size(); i++) {
				 Product product = productsWithLowStock.get(i);
				 int currentStock = product.getStockQuantity();
				 Output.println("Currently you have " + currentStock + " items of product with ID " + product.getProductId());
				 Output.println("Introduce the amount you want to increase the stock of product with ID " + product.getProductId() + " by: ");
				 int increaseStockBy = InputKB.readInteger();
				 if(increaseStockBy >= 0) {
					 //I set the new stock of the product
					 product.setStockQuantity(currentStock + increaseStockBy);
					 //I call the method that updates the stock of the product in the database
					 productManager.updateProductStock(product, currentStock+increaseStockBy, true);
					 Output.println("Do you want to re-stock another product with low stock? Introduce 1 for YES and 0 for NO");
					 int choice = InputKB.readInteger();
					 switch(choice) {
					 case 1: 
						 break;
					 case 2:
						 keepRestocking = false;
						 manageProductsStock();
						 break;
			         default:
			            Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
			            break;						
			         }
				 }
			 }
		 }
	 }
	 
	 private void manageOrders() throws OrderExceptions, ClientException  {
		 //This method should enable the supplier to: see all the orders, see the ones that have a specific state, 
		 Output.println("\nIntroduce the number of the operation you wish to perform regarding the administration of the orders:");
		 boolean keepGoing = true;
		 while(keepGoing) {
			 Output.println("1. See orders by state");
			 Output.println("2. See all orders");
	         Output.println("3. If you want to exit");
	         //TODO AN EXCEPTION IS THROWN BECAUSE THE ORDER MANAGER IS NULL
	         //ADD AN IF THAT SAYS IF THE SUPPLIER DOES NOT HAVE ANY ORDER!!!
			 
			 int option = InputKB.readInteger();
			 
			 switch(option) {
			 case 1:
				 Output.println("Introduce the order state you are interested in among the following ones: ");
				 OrderStatus statusChosen = getOrderStatusOfOrder();
				 List<Order> ordersWithProvidedStatus = seeOrdersByCategory(statusChosen);
				 //TODO CHECK IF THIS IF WORKS
				 if(ordersWithProvidedStatus.size() >= 1) {
					 //I print the result obtained
					 Output.println("Orders with status '" + statusChosen + "': ");
					 for(int i=0; i<ordersWithProvidedStatus.size(); i++) {
						 Order order = ordersWithProvidedStatus.get(i);
						 System.out.println(order);
					 }
				 } else {
					 System.out.println("Unfortunately, you do not have any orders with the status you selected");
				 }
				 keepGoing = false;
				 displaySupplierMenuOptions();
				 break;
			 case 2:
				 List<Order> allOrders = orderManager.getAllOrders();
				 //I print the result obtained
				 Output.println("Orders: ");
				 for(int i=0; i<allOrders.size(); i++) {
					 Order order = allOrders.get(i);
					 System.out.println(order);
				 }
				 keepGoing = false;
				 displaySupplierMenuOptions();
				 break;
	         default:
		            Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
		            break;	
			 }
		 } 
	 } 
	 
	 /**
	  * Method that shows the user the status of the orders.
	  * @return a variable of type Order Status.
	  */
	 private OrderStatus getOrderStatusOfOrder() {
		 OrderStatus[] status = OrderStatus.values();
		 while(true) {
			 for(int i = 0; i < status.length; i++) {
				 Output.println(" " + i + ". " + " " + status[i]);
			 }
			 int choice = InputKB.readInteger();
			 if(choice > -1 && choice < status.length) {
				 return status[choice];
			 } else {
				 Output.println("The introduced number is invalid, please try again.");
			 }
		 }
	 }
	 
	 private List<Order> seeOrdersByCategory(OrderStatus chosenStatus) throws OrderExceptions, ClientException {
		//I call the method from order manager that returns a list of all the orders whose order status matches the one received as parameter
		 List<Order> ordersWithProvidedStatus = orderManager.getOrdersByStatus(chosenStatus);
		 return ordersWithProvidedStatus;
	 }
	 
	 
	 public boolean approveOrder(Order order) {
		 List<ProductOrder> productOrders = order.getProductOrders();
		 for(int i=0; i<productOrders.size(); i++) {
			 ProductOrder productOrder = productOrders.get(i);
			 if(productOrder.getProduct().getStockQuantity() < 0) {
				 //If there is no sufficient stock of the product the user is tying to buy, then we return false in order to state that the order is not approved by the supplier.
				 return false;
			 }
		 }
		 return true;
	 }
	 
	 
	 /**
	  * Method that retrieves a client from XML file and turns it into a Java object.
	  */
	 public void importClientFromXml() { 
	 // Create the path for the XML file. For simplicity, we used the same path that client2xml
	 File file = new File("./xmls/Client.xml");
	
	 if(!file.exists()) { // TODO WE SHOULD CHECK ALSO IF THE FILE IS EMPTY
		Output.println("No XML found at " + file.getAbsolutePath());
	 }
	
	 // Call the JAXB unmarshaller to get a Client object
	 Client clientFromXML = xmlMan.xml2Client(file);
	
	 // Check that the Client has been imported correctly
	 if(clientFromXML == null) {
		Output.println("Failed to import Client from XML");
	 }
	
	 // NOTE: we use 2 instances of Client (clientFromXML and clientInDB) to avoid having 2 clients with the same email in the DB, which would happen if we import directly  
	 //clientFromXML without checking if it already was in the DB
	 
	 try { // We assume that the client is already in the DB
		Client clientInDB = clientManager.getClientByEmail(clientFromXML.getEmail()); // throws ClientException if no client exists for the given email
		clientManager.updateName(clientInDB.getUserId(), clientFromXML.getName());
        clientManager.updateSurname(clientInDB.getUserId(), clientFromXML.getSurname());
        clientManager.updatePhoneNumber(clientInDB.getUserId(), clientFromXML.getPhoneNumber());
        clientManager.updateAddress(clientInDB.getUserId(), clientFromXML.getAddress());
        Output.println("Existing client (" + clientFromXML.getEmail() + ") was imported");
        
        // Keep track of the imported client 
        this.importedClient = clientInDB; // Initialize the attribute to the correctly imported client, which is also in the DB
	
	 } catch(ClientException ce) {  // If the client imported was not inserted in the DB, we insert it here in the catch clause (we have tried to use an if-else within the try, but it didn't work)
		 try {
			 // Insert the new client
			 clientManager.insertClient(clientFromXML); 
			 // Retrieve the client from the DB and save it in a Java object -> now clientInDB has an ID assigned by the DB
			 Client clientInDB = clientManager.getClientByEmail(clientFromXML.getEmail()); 
			 // Initialize the attribute to the correctly imported Java object, which is also in the DB
			 this.importedClient = clientInDB; 
			 Output.println("New client (" + clientFromXML.getEmail() + ") was imported and inserted in the database");
		 } catch (ClientException e) {
			e.printStackTrace();
		}	
	} catch (SQLException e) {
		e.printStackTrace();
		}	
	}
	 
	 
	 
	 /**
	  * Method that allows the supplier to view the imported client's profile.
	  */
	 public void viewClientsProfile() {
		 if (this.importedClient == null) {
			 Output.println("No client has been imported yet. Please import a client first by selecting option 5");
	         return; // Returns to where the method was called
	         }
		 // Print the imported client's profile 
		 Output.println("\n=== Imported Client Profile ===");
		 Output.println("Name:       " + importedClient.getName() + " " + importedClient.getSurname());
		 Output.println("Email:      " + importedClient.getEmail());
		 Output.println("Phone:      " + importedClient.getPhoneNumber());
		 Output.println("Address:    " + importedClient.getAddress());
		 Output.println("User ID:    " + importedClient.getUserId());
		 Output.println("==============================\n");
	    }
	 
}
