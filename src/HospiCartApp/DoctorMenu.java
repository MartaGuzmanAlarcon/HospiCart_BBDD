package HospiCartApp;

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
	private static ProductManagerJDBC productManager;
	private static ConnectionManagerJDBC connectionManager;
	private static OrderManagerJDBC orderManager;
	private static ProductOrderManagerJDBC productOrderManager;
	private static ClientManagerJDBC clientManager;
	private static Order cart;
	private static Client doctor; 

	/**
	 * Construct a DoctorMenu for the given doctor and cart, wired up to the shared JDBC connection.
	 * @param cm the JDBC connection manager, which allows this class to be connected to the database
	 * @param doc the Client representing the currently‐logged‐in doctor
	 * @param cart the Order object being used as this doctor’s “shopping cart”
	 */
	public DoctorMenu(ConnectionManagerJDBC cm, Client doc, Order cart) {
		// Initialize all the attributes with DoctorMenu. instead of this. because they are static variables 
        DoctorMenu.connectionManager   = cm; // Passing one cm is simpler than passing five or six different manager objects, cm includes all of them
        DoctorMenu.productManager      = new ProductManagerJDBC(cm);
        DoctorMenu.orderManager        = new OrderManagerJDBC(cm);
        DoctorMenu.productOrderManager = new ProductOrderManagerJDBC(cm);
        DoctorMenu.clientManager       = new ClientManagerJDBC(cm);
        DoctorMenu.doctor = doc;
        DoctorMenu.cart = cart;
	}
	
	/**
	 * Method that displays the doctor's menu.
	 */
	public void displayDoctorMenu() { 
		while(true) {
			Output.println("\n=== Welcome to the Doctor Menu! ===");
        	Output.println("Dear doctor, please introduce the number of the operation you wish to perform:");
        	Output.println("1. Browse products");
            Output.println("2. View your cart");
            Output.println("3. View my account");
            //View my accoutn should have a switch with options: MY DATA, MY ORDERS AND LOG OUT.
            	//MY DATA should enable the user to SEE ITS PERSONAL INFORMATION, CHANGE PASSWORD, CHANGE ADDRESS.
            	//MY ORDERS should 
            //should call the update mthods to updae the information of the 
            Output.println("0. Go back"); //ir a donde se llama al menu de doctor
            
            int choice = InputKB.readInteger();
            
            try {
            	switch (choice) {
            	case 1:  
            		browseProducts();
            		break;
                case 2:  viewCart();          break;
                case 0:  return;
                default: Output.println("Invalid option. Please try agin introducing a valid number.");    
              }
            } catch(ClientException ce) { // It is the Menu's responsibility to catch all exceptions thrown by the methods it calls
	        	System.out.println("ERROR: " + ce);
	        } catch(OrderExceptions oe) {
	        	System.out.println("ERROR: " + oe);
	        }
			
		}
		
	}
	
	public void browseProducts() {
		Output.println("Press:");
		Output.println("1. If you want to browse products by category.");
		Output.println("2. If you want to browse all products.");
		Output.println("3. If you want to go back.");
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
	 * @throws OrderExceptions
	 * @throws ClientException
	 */
	public void viewCart() throws OrderExceptions, ClientException {
		 List<ProductOrder> productsOrders = productOrderManager.getProductOrdersByOrderID(cart.getOrderId());
		 
		 if (productsOrders.isEmpty()) {
	            Output.println("Your cart is empty.");
	     } else {
	    	 Output.println("Your current cart items:");
	    	 // Print column headers
	         Output.println(String.format("%-20s %5s %10s", "Product", "Quantity", "Total"));
	         Output.println("----------------------------------------");
	         for (ProductOrder po : productsOrders) {
	             String name  = po.getProduct().getName();
	             int    amount   = po.getAmount();
	             float  totalPrice = po.getTotalPrice();
	             // Same formatted row as before
	             Output.println(String.format("  - %-18s %5d %10.2f", name, amount, totalPrice));
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
	 public static void browseProductsByCategory(Category category) {
		 List<Product> productsByCategory = productManager.getProductsByCategory(category);
		 for(int i = 0; i < productsByCategory.size(); i++) {
			 System.out.println(productsByCategory.get(i));
		 }
	 }
}
