package HospiCartApp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartJDBC.*;
import HospiCartPOJOs.*;
import HospiCartXML.ManagerImplXML;
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
	private ManagerImplXML xmlMan; 

	
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
		this.xmlMan = new ManagerImplXML();
	}

	
	/**
	 * Method that displays the doctor's menu.
	 */
	public void displayDoctorMenu() { 
		Output.println("\n=== Welcome to the Doctor Menu! ===");
		while(true) {
        	Output.println("\nDear doctor, please introduce the number of the operation you wish to perform:");
        	//Output.println("1. Browse products");
            Output.println("1. View your cart");
            Output.println("2. Shop");
            Output.println("3. Pay");
            Output.println("4. View my orders");
            Output.println("5. Account settings"); // Includes the UPDATE methods 
            //View my account should have a switch with options: MY DATA, MY ORDERS AND LOG OUT.
            	//MY DATA should enable the user to SEE ITS PERSONAL INFORMATION, CHANGE PASSWORD, CHANGE ADDRESS.
            	//MY ORDERS should 
            //should call the update methods to update the information of the 
            //Output.println("0. Go back"); //ir a donde se llama al menu de doctor
            Output.println("0. Exit");
            
            int choice = InputKB.readInteger();
            
            try {
            	switch (choice) {
                case 1:  
                	viewCart();          
                	break;
                case 2: 
                	boolean keepShopping = true;
                	while(keepShopping) {
	                	Product chosenProduct = chooseProduct();
	                	if(chosenProduct != null) {
	                		productShoppingMenu(chosenProduct);
	                	}
	                	//We ask the user if he/she want to keep shopping
	                	Output.println("Tap 0 to finish your shopping, or any other number if you have more items to add to your cart. ;)");
	                	int option = InputKB.readInteger();
	                	if(option == 0) {
	                		keepShopping = false;
	                		break;
	                	}
                	}
                	break;
                case 3: 
                	pay();
                	break;
                case 4: //TODO EDIT THE TO STRING!! or include outputs to print the orders of the user in a nice way
                	List<Order> ordersOfDoctor = doctor.getOrders();
                	if(ordersOfDoctor != null ) {
                    	Output.println("\n======== ORDER RECORD ========");
                    	for(int i=0; i<ordersOfDoctor.size(); i++) {
                    		System.out.println(ordersOfDoctor.get(i));
                    	}
                	} else {
                		Output.println("\nYou have not made any orders with us yet... \nLet's change that!  :)");
                	}

                	break;
                case 5:
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
	
	public void productShoppingMenu(Product chosenProduct) {
		boolean keepGoing = true;
		while(keepGoing) {
			try {
				Output.println("\nWhat do you want to do with this product?");
		    	Output.println("1. Add to my cart");
		    	Output.println("2. See more details of the product");
		    	Output.println("3. See product as XML");
		    	//TODO THINK IF WE HAVE TO INCLUDE A GO BACK
		    	int option = InputKB.readInteger();
		    	
		    	switch(option) {
		    	case 1:
		        	int amount = chooseAmount(chosenProduct);
		        	addToCart(chosenProduct, amount);
		        	keepGoing = false;
		        	break;
		    	case 2:
		    		System.out.println(chosenProduct);
		        	keepGoing = false;
		    		break;
		    	case 3:
		    		//We call the Marshaller method
					xmlMan.product2Xml(chosenProduct);				
					System.out.println("\nYour product information is in ./xmls/Product.xml");
		        	keepGoing = false;
					break;
				default:
					Output.println("The introduced number is invalid. Please try again!");
					break;
		    	}
			} catch(ClientException ce) {
	        	System.out.println("ERROR: " + ce);
	    	} catch(SQLException sqle) {
	        	System.out.println("ERROR: " + sqle);
	        }
		}
	}
	
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
		if(cart == null || cart.getProductOrders().isEmpty()) {
			Output.println("\nYour cart is empty.");
			return; // Tells Java “stop executing this method right now and go back to the caller”, it is a way of exiting the method
		} 
		
		// Now it's safe to pull the product orders from the DB -> the corresponding manager is used for this purpose 
		//List<ProductOrder> productsOrders = productOrderManager.getProductOrdersByOrderID(cart.getOrderId());
		List<ProductOrder> productsOrders = cart.getProductOrders();
		
		// Check if there really zero items:
		if (productsOrders.isEmpty()) {
            Output.println("\nYour cart is empty.");
            return; // To exit the method 
		} 
		
		// Otherwise, show all products 
		Output.println("\nYour current cart items:");
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
	
	private Product chooseProduct() {
	    while (true) {
	        // Display the options to the user
	        Output.println("\nChoose how to browse products:");
	        Output.println(" 1. By category");
	        Output.println(" 2. Show all");
	        Output.println(" 0. Go back");
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
		    boolean keepShopping = true;
		    //We create a while loop that will iterate until the user selects stop shopping
		    while(keepShopping) {
		        // Show the candidates products numbered 
		        Output.println("\nChoose a product by number:");
		        for (int i = 0; i < candidates.size(); i++) {
		            Product p = candidates.get(i);
		            Output.println(String.format(" %2d) %s [%s] $%.2f",
		                i + 1, p.getName(), p.getCategory(), p.getPrice()));
		        }
		        Output.println("  0) Stop shopping");
		        
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
		        Product chosenProduct = candidates.get(choice -1);
		        return chosenProduct;
	        }
	        return null;
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
	     if (cart == null || cart.getOrderId() == null) {
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
	     
		 PaymentMethod[] methods = PaymentMethod.values();
		 PaymentMethod paymentMethod = null;
		 boolean keepGoing = true;
		 while(keepGoing) {
		     Output.println("Enter the number of the payment method you want to select:");
			 for(int i = 0; i < methods.length; i++) {
				 Output.println(" " + (i+1) + ". " + " " + methods[i]);
			 }
			 Output.println(" 0. CANCEL ORDER");
			 int choice = InputKB.readInteger();
			 if(choice > 0 && choice < methods.length+1) {
				 paymentMethod = methods[choice-1];
				 keepGoing = false;
				 
			     // Build & persist Payment
			     Payment payment = new Payment();
			     payment.setOrder(cart);
			     payment.setAmount(orderTotalPrice);
			     payment.setPaymentMethod(paymentMethod);
			     payment.setPaymentStatus(PaymentStatus.COMPLETED);
			     connectionManager.getPaymentManager().insertPayment(payment);

			     // Set the order status into “ORDERED”
			     cart.setOrderStatus(OrderStatus.ORDERED);
			     orderManager.updateOrderStatus(cart.getOrderId(), OrderStatus.ORDERED);

			     Output.println("\nPayment received and order placed! Your order ID is " + shipment.getTrackingNumber());
			     List<Order> ordersOfClient = doctor.getOrders();
			     if(ordersOfClient == null) {
			    	 ordersOfClient = new ArrayList<>();
			     }
			     ordersOfClient.add(cart);
		    	 doctor.setOrders(ordersOfClient);
		         cart = new Order(doctor); //I "reset" the cart
				 //TODO I THINK WE ARE MISSING SETTING THE CART TO NULL
			     //TODO ARE WE ADDING THIS ORDER TO THE LIST OF ORDERS OF THE CLIENT?
			 } else if(choice == 0) {
				 cart.setOrderStatus(OrderStatus.CANCELLED);
				 Output.println("Your order was cancelled.");
				 //We still add the cancelled order to the list of orders of the user because he/she should be able to see it in the summary of all the orders he/she made
			     List<Order> ordersOfClient = doctor.getOrders();
			     if(ordersOfClient == null) {
			    	 ordersOfClient = new ArrayList<>();
			     }
			     ordersOfClient.add(cart);
		    	 doctor.setOrders(ordersOfClient);
		         cart = new Order(doctor); //I "reset" the cart
				 keepGoing = false;
			 }
			 else {
				 Output.println("The introduced number is invalid, please try again.");
			 }
		 }
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
		        	Output.println("6. If you want to see your personal information as an XML");

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
			    case 6:
			    	client2Xml();
			    	break;
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
		
		public void client2Xml() {
			//We call the Marshaller method
			xmlMan.client2Xml(doctor);				
			System.out.println("Your client information is in ./xmls/Client.xml");
		}
	 
	 // TODO METODO PAY QUE ASIGNE UN SHIPMENT UNA VEZ QUE EL PAGO SE HA PROCESADO
	 // SETSHIPMENT DE ORDER PARA QUE ACTUALICE EL SHIPMENT EN JAVA 
	 // DESPUÉS -> CREAR UPDATESHIPMENTID EN ORDER PARA ACTUALIZAR EL SHIPMENT EN LA DB 
}
