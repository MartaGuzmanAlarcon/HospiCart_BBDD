package HospiCartApp;

import java.sql.SQLException;
import java.util.List;


import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartJDBC.*;
import HospiCartPOJOs.*;
import Utilities.*;

/**
 * A menu UI for doctors to browse products and manage their shopping cart (an Order).
 */
public class DoctorMenu {
	private ProductManagerJDBC productManager;
	private ConnectionManagerJDBC connectionManager;
	private OrderManagerJDBC orderManager;
	private ProductOrderManagerJDBC productOrderManager;
	private ClientManagerJDBC clientManager;
	private Order cart;
	private Client doctor; 
	
	// NOTE: this attributes CAN NOT BE STATIC because they are per-session pieces of state.
	// Making them static would cause that two different doctors logging in consecutively would trample on each other’s doctor/cart

	/**
	 * Construct a DoctorMenu for the given doctor and cart, wired up to the shared JDBC connection.
	 * @param cm the JDBC connection manager, which allows this class to be connected to the database
	 * @param doc the Client representing the currently‐logged‐in doctor
	 */
	public DoctorMenu(ConnectionManagerJDBC cm, Client doc) {
		// Initialize all the attributes 
        this.connectionManager = cm; // Passing one cm is simpler than passing five or six different manager objects, cm includes all of them
        this.productManager = new ProductManagerJDBC(cm);
        this.orderManager = new OrderManagerJDBC(cm);
        this.productOrderManager = new ProductOrderManagerJDBC(cm);
        this.clientManager = new ClientManagerJDBC(cm);
        this.doctor = doc;
        this.cart = new Order(doctor); // Initialize a brand-new, empty cart. In this way we are assigning an Order to a Client, which is a doctor in this case 
	}
	
	/**
	 * Method that displays the doctor's menu.
	 */
	public void displayDoctorMenu() { 
		Output.println("\n=== Welcome to the Doctor Menu! ===");
		while(true) {
        	Output.println("Dear doctor, please introduce the number of the operation you wish to perform:");
        	//Output.println("1. Browse products");
            Output.println("1. View your cart");
            Output.println("2. Shop");
            Output.println("3. Pay");
            Output.println("4. Account settings"); // Includes the UPDATE methods 
            //View my accoutn should have a switch with options: MY DATA, MY ORDERS AND LOG OUT.
            	//MY DATA should enable the user to SEE ITS PERSONAL INFORMATION, CHANGE PASSWORD, CHANGE ADDRESS.
            	//MY ORDERS should 
            //should call the update mthods to updae the information of the 
            //Output.println("0. Go back"); //ir a donde se llama al menu de doctor
            Output.println("0. Exit");
            
            int choice = InputKB.readInteger();
            
            try {
            	switch (choice) {
//            	case 1:  
//            		browseProducts();
//            		break;
                case 1:  
                	viewCart();          
                	break;
                case 2: 
                	Product chosenProduct = chooseProduct();
                	int amount = chooseAmount(chosenProduct);
                	addToCart(chosenProduct, amount);
                	break;
                case 3: 
                	pay();
                	break;
                case 4:
                	accountSettings();
                case 0:
	                Output.println("Application closed!");
	                //If the user introduces a 0, then we set the variable "keepGoing" to false so we can exit the switch.
	                System.exit(0); //I close the application
	                break;
                //case 0:  return;
                default: Output.println("Invalid option. Please try agin introducing a valid number.");    
              }
            } catch(ClientException ce) { // It is the Menu's responsibility to catch all exceptions thrown by the methods it calls
	        	System.out.println("ERROR: " + ce);
	        } catch(OrderExceptions oe) {
	        	System.out.println("ERROR: " + oe);
	        } catch(SQLException sqle) {
	        	System.out.println("ERROR: " + sqle);
	        }
			
		}
		
	}
	
	public void browseProducts() { 
		Output.println("Press:");
		Output.println("1. If you want to browse products by category");
		Output.println("2. If you want to browse all products");
		Output.println("3. If you want to go back");
		int choice2 = InputKB.readInteger();
		switch(choice2) {
		case 1:
			//We call the method that asks the user to introduce a category and returns the category.
			Category category = getCategoryElection();
			browseProductsByCategory(category);
			break;
		case 2:
    		browseAllProducts(); 
    		break;
		case 3: 
			displayDoctorMenu();
			return; 
		}   
	}
	
	
//	public void resetPassword() {
//		//I ask the user to introduce and confirm the new password and I check that they are in fact equal.
//		Output.println("Please introduce your new password: ");
//        String newPassword = InputKB.readString();
//        Output.println("Confirm new password: ");
//        String newPasswordConfirmed = InputKB.readString();
//        //If the introduced passwords are equal
//        if(newPassword.equals(newPasswordConfirmed)) {
//        	userManager.updatePassword(user.getEmail(), newPasswordConfirmed);
//        	//I call the setter method for password, which encrypts it and sets it as the password of the user.
//        	//TODO DO I HAVE TO ENCRYPT THE PASSWORD IN THE SETTER???
//        	user.setPassword(newPasswordConfirmed);
//        	Output.println("Password successfully reset!");
//        	Output.println("Redirecting to login...");
//        	login();
//        }
//        else {
//        	//I tell the user that the password do not match and ask him/her what does he/she want to do.
//        	Output.println("The introduced passwords do not match!");
//        	Output.println("Introduce: ");
//    		Output.println("1. If you want to try again.");
//    		Output.println("2. If you want to go back.");
//    		Output.println("0. If you want to exit.");
//    		int option2 = InputKB.readInteger();
//    		switch(option2) {
//    			case 1:
//    				//If the user wants to keep trying, I set the try again variable to true and just exit the switch case.
//    				reintroducePassword = true;
//    				break;
//    			case 2:
//    				keepGoing = true;
//    				reintroducePassword = false;
//    				tryAgain = true;
//    				break;
//    			case 0:
//    				//If the user wants to exit, I disconnect the application.
//    				closeConnections();
//                    Output.println("Application closed! Hope to see you again soon!");
//    				keepGoing = false;
//    				reintroducePassword = false;
//    				break; //TODO VERIFY IF IT WORKS CORRECTLY!!
//                default:
//                	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
//                	tryAgain = false;
//                	break;	
//    		}
//        }
//	}
	
	/**
	 * Method that allows the doctor to see all the product orders within its cart.
	 * If the cart is empty either because no cart was inserted in the database or because the list of product orders is empty, the method exits.
	 * @throws OrderExceptions
	 * @throws ClientException
	 */
	public void viewCart() throws OrderExceptions, ClientException { 
		// Check for a brand new cart that’s never been inserted in the DB: If the user never calls addToCart(), and thus never inserted the Order into the DB, cart.getOrderId() will be null
		// Recap: at this point the object Order has been created in Java thanks to the constructor, but it is not inserted in the DB until addToCart() is called
//		if(cart.getOrderId() == null) {
		if(cart.getProductOrders().isEmpty()) {
			Output.println("Your cart is empty.");
			return; // Tells Java “stop executing this method right now and go back to the caller”, it is a way of exiting the method
		} 
		
		// Now it's safe to pull the product orders from the DB -> the corresponding manager is used for this purpose 
		//List<ProductOrder> productsOrders = productOrderManager.getProductOrdersByOrderID(cart.getOrderId());
		List<ProductOrder> productsOrders = cart.getProductOrders();
		
		// Check if there really zero items:
		if (productsOrders.isEmpty()) {
            Output.println("Your cart is empty.");
            return; // To exit the method 
		} 
		
		// Otherwise, show all products 
		Output.println("Your current cart items:");
    	// Print column headers
        Output.println(String.format("%-20s %5s %10s", "Product", "Quantity", "Total"));
        Output.println("----------------------------------------");
        for (ProductOrder po : productsOrders) {
            String name = po.getProduct().getName();
            int amount = po.getAmount();
            float totalPrice = po.getTotalPrice();
            // Same formatted row as before
            Output.println(String.format("  - %-18s %5d %10.2f", name, amount, totalPrice));
         } 
	 }
	
	/**
	 * Method that adds a product to the client's cart (an order).
	 * This method performs the addition of the product both in Java and in the database.
	 * @param product
	 * @param amount
	 * @throws SQLException
	 * @throws ClientException
	 */
	public void addToCart(Product product, int amount) throws SQLException, ClientException {
		// ADD A PRODUCT ORDER TO AN ORDER IN JAVA -> link them both
		ProductOrder productOrder = new ProductOrder(amount, cart, product); // Attach our in-memory Order (cart) to a ProductOrder
		// Add the productOrder to the list of products orders of the cart (order)
		List<ProductOrder> productOrders = cart.getProductOrders(); // Retrieve the product orders of the cart (order)
		productOrders.add(productOrder);
		
		// INSERT THE NEW ROWS OF THE CORRESPONDING TABLES IN THE DB 
		if(cart.getOrderId() == null) { // Insert the Order (cart) in the DB if it does not exist -> the DB will generate its ID with AUTOINCREMENT
			orderManager.insertOrder(cart); 
		} else { // If the order exists in the DB, insert the product order in the DB 
			productOrderManager.insertProductOrder(productOrder);
		}
		
		 Output.println(amount + " x '" + product.getName() + "' added to your cart.");
	}
	
	
//	private Product chooseProduct() {
//		// Retrieve all the products from the DB, which are loaded in setApplication() method once we run the MainMenu 
//		List<Product> products = productManager.getAllProducts();
//		
//		// Show all the products numbered
//		Output.println("\nPlease choose a product:");
//	    for (int i = 0; i < products.size(); i++) {
//	        Product p = products.get(i);
//	        Output.println(String.format("%2d) %s  [%s]  $%.2f", 
//	            i+1, p.getName(), p.getCategory(), p.getPrice()));
//	    }
//	    
//	    // Read until the user introduces a valid option 
//	    while (true) {
//	        int option = InputKB.readInteger();
//	        if (option >= 0 && option < products.size()) {
//	        	Product chosedProduct = products.get(option -1);
//	    	    return chosedProduct;
//	        } else {
//	        	Output.println("Invalid selection. Please enter a number between 1 and " + (products.size()));
//	        }
//	    }
//	    
//	}
	
	private Product chooseProduct() {
	    while (true) {
	        // Display the options to the user
	        Output.println("\nChoose how to browse products:");
	        Output.println(" 1. By category");
	        Output.println(" 2. Show all");
	        Output.println(" 0. Cancel");
	        int browseOption = InputKB.readInteger();
	        
	        List<Product> candidates;
	        switch (browseOption) {
	          case 1:
	            Category category = getCategoryElection();
	            candidates = productManager.getProductsByCategory(category);
	            break;
	          case 2:
	            candidates = productManager.getAllProducts();
	            break;
	          case 0:
	            return null;  // Exit 
	          default:
	            Output.println("Invalid option, try again.");
	            continue;
	        }
	        
	        // Show the candidates products numbered 
	        Output.println("\nChoose a product by number:");
	        for (int i = 0; i < candidates.size(); i++) {
	            Product p = candidates.get(i);
	            Output.println(String.format(" %2d) %s [%s] $%.2f",
	                i + 1, p.getName(), p.getCategory(), p.getPrice()));
	        }
	        Output.println(" 0. Go back");
	        
	        // Read and validate their choice  
	        int choice = InputKB.readInteger();
	        if (choice == 0) {
	            continue;  // back to browse mode
	        }
	        if (choice < 1 || choice > candidates.size()) {
	            Output.println("That’s not a valid product number. Try again.");
	            continue; 
	        }
	        
	        // 4) Return the picked one
	        Product chosedProduct = candidates.get(choice -1);
	        return chosedProduct;
	    }
	}


	private int chooseAmount(Product product) {
		Output.println("How many units of '" + product.getName() + "' would you like to add?");
		while(true) {
			int amount = InputKB.readInteger();
			if(amount <= 0) {
				Output.println("Quantity must be at least 1. Try again:");
			} else if (amount > product.getStockQuantity()) {
				Output.println("Threre are no more units of " + product.getName() + " in stock. Please enter a smaller amount.");
			} else {
				return amount;
			}
		}
	    
	    
	}
	
	/**
	 * Method that calls the get all products method of product manager and prints all the products contained in the database.
	 */
	 public void browseAllProducts() {
		 List<Product> products = productManager.getAllProducts();
		 for(int i = 0; i < products.size(); i++) {
			 System.out.println(products.get(i));
		 }
	 } 
	 
	 /**
	  * Method that asks the user to select the category in which he/she is interested in.
	  * @return a variable of type Category.
	  */
	 private static Category getCategoryElection() {
		 Category[] categories = Category.values();
		 while(true) {
			 Output.println("Introduce the number of the category of products you are interested in: ");
			 for(int i = 0; i < categories.length; i++) {
				 Output.println(" " + i + ". " + " " + categories[i]);
			 }
			 int choice = InputKB.readInteger();
			 if(choice > -1 && choice < categories.length) {
				 return categories[choice];
			 } else {
				 Output.println("The introduced number is invalid, please try again.");
			 }
		 }
	 }
	 
	 /**
	 * Method that calls the get products by category method of product manager and prints all the products of the specified category contained in the database.
	  * @param category variable of type category.
	  */
	 public void browseProductsByCategory(Category category) {
		 List<Product> productsByCategory = productManager.getProductsByCategory(category);
		 for(int i = 0; i < productsByCategory.size(); i++) {
			 System.out.println(productsByCategory.get(i));
		 }
	 }
	 
	 /**
	  * Completes the current cart: collects shipment + payment, persists both,
	  * and finally moves the order to ORDERED status.
	  */
	 public void pay() throws SQLException, ClientException, OrderExceptions {
	     // Check if there's something to pay for
	     if (cart.getOrderId() == null) {
	         Output.println("Your cart is empty. Add some products before trying to pay!");
	         return;
	     }

	     // Ask for shipment details
	     Output.println("\n======= Shipping Information =======");
	   

	     // Build & persist Shipment
	     Shipment shipment = new Shipment(cart); // Link the user's cart (Order) to the Payment
	     connectionManager.getShipmentManager().insertShipment(shipment);
	     System.out.println(shipment);

	     // Ask for payment details
	     Output.println("\n======= Payment Information =======");
	     //Output.println("Total to pay: $" + String.format("%.2f", cart.getProductOrders().getTotalPrice())); 
	       // assume you have added a getTotalAmount() helper on Order
	     
	     // Calculate the total price of the Order (cart)
	     float productTotalPrice = 0;
         int orderTotalPrice = 0;
	     List<ProductOrder> productsOrders = productOrderManager.getProductOrdersByOrderID(cart.getOrderId());
	     for (ProductOrder po : productsOrders) {
	            String name = po.getProduct().getName();
	            productTotalPrice = po.getTotalPrice();
	            orderTotalPrice = (int) (orderTotalPrice + productTotalPrice);
	         } 
	     
	     //Output.println("Enter payment amount:");
	     //int  amount = InputKB.readInteger();
	     Output.println("Enter payment method (e.g. CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER):");
	     String method = InputKB.readString().toUpperCase();

	     // Build & persist Payment
	     Payment payment = new Payment();
	     payment.setOrder(cart);
	     payment.setAmount(orderTotalPrice);
	     payment.setPaymentMethod(PaymentMethod.valueOf(method));
	     payment.setPaymentStatus(PaymentStatus.COMPLETED);
	     connectionManager.getPaymentManager().insertPayment(payment);

	     // Set the order status into “ORDERED”
	     cart.setStatus(OrderStatus.ORDERED);
	     orderManager.updateOrderStatus(cart.getOrderId(), OrderStatus.ORDERED);

	     Output.println("\nPayment received and order placed! Your order ID is " + shipment.getTrackingNumber());
	 }

		/**
		* Method that shows the user his/her personal data and asks if he/she wants to change the password or not.
		*/
		 public void accountSettings() throws SQLException {

			Output.println("\nUsername: " + doctor.getEmail());

	    	Output.println("\nPress:");
		    boolean keepGoing = true;
		    	
		    while(keepGoing) {	    	
		    	while(keepGoing) {
		        	Output.println("1. If you want to change your address");
		        	Output.println("2. If you want to change your phone number");
		        	Output.println("3. If you want to change your name");
		        	Output.println("4. If you want to change your surname");
		        	Output.println("5. If you want to see your personal information");
		        	//Output.println("6. If you want to log out");
		        	Output.println("0. If you want to go back");	        
		        	int option = InputKB.readInteger();
		        	
			    switch(option) {
			    case 1: 
			    	Output.println("Please introduce your new address: ");
		    		String newAddress = InputKB.readString();
		    		doctor.setAddress(newAddress);
		    		clientManager.updateAddress(doctor.getUserId(), newAddress);	
		    		Output.println("Your address was successfully updated!");
			    	keepGoing = false;
			    	displayDoctorMenu(); //I take the supplier back to the supplier menu
			   		break;
			    case 2:
			    	Output.println("Please introduce your new phone number: ");
		    		int newPhoneNumber = InputKB.readInteger();
		    		doctor.setPhoneNumber(newPhoneNumber);
		    		clientManager.updatePhoneNumber(doctor.getUserId(), newPhoneNumber);	
		    		Output.println("Your phone number was successfully updated!");

			    	keepGoing = false;
			    	displayDoctorMenu(); //I take the supplier back to the supplier menu
			   		break;
			    case 3: 
			    	Output.println("Please introduce your new name: ");
		    		String newName = InputKB.readString();
		    		doctor.setName(newName);
		    		clientManager.updateName(doctor.getUserId(), newName);	
		    		Output.println("Your name was successfully updated!");
			    	keepGoing = false;
			    	displayDoctorMenu(); //I take the supplier back to the supplier menu
			   		break;
			    case 4: 
			    	Output.println("Please introduce your new surname: ");
		    		String newSurname = InputKB.readString();
		    		doctor.setSurname(newSurname);
		    		clientManager.updateSurname(doctor.getUserId(), newSurname);	
		    		Output.println("Your surname was successfully updated!");
			    	keepGoing = false;
			    	displayDoctorMenu(); //I take the supplier back to the supplier menu
			   		break;
			    case 5:
			    	System.out.println(doctor);

			   	case 0: 
			   		displayDoctorMenu();
		    		keepGoing = false;
		    		break;
		    	default:
			    	Output.println("The number introduced is invlaid, please try again.");
			    	break;
			    }
		    	}
		    }
		 }
	 
	 // TODO METODO PAY QUE ASIGNE UN SHIPMENT UNA VEZ QUE EL PAGO SE HA PROCESADO
	 // SETSHIPMENT DE ORDER PARA QUE ACTUALICE EL SHIPMENT EN JAVA 
	 // DESPUÉS -> CREAR UPDATESHIPMENTID EN ORDER PARA ACTUALIZAR EL SHIPMENT EN LA DB 
}
