package HospiCartApp;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartJDBC.*;
import HospiCartPOJOs.*;
import HospiCartXML.ManagerImplXML;
import Utilities.*;

/**
 * A menu UI for doctors to browse products and manage their shopping cart (an Order).
 */
public class NurseMenu {
	private ProductManagerJDBC productManager;
	private ConnectionManagerJDBC connectionManager;
	private OrderManagerJDBC orderManager;
	private ProductOrderManagerJDBC productOrderManager;
	private ClientManagerJDBC clientManager;
	private Order cart;
	private Client nurse; 
	private ManagerImplXML xmlMan;

	//TODO MAKE "EXIT" BE "LOG OUT" --> IF THE USER EXITS THE PROGRAM WITH AN UNPAID ORDER, WE MUST INCREASE THE PRODUCT STOCK WITH THE UNPAID UNITS
	
	// NOTE: this attributes CAN NOT BE STATIC because they are per-session pieces of state.
	// Making them static would cause that two different doctors logging in consecutively would trample on each other’s doctor/cart

	/**
	 * Construct a NurseMenu for the given doctor and cart, wired up to the shared JDBC connection.
	 * @param cm the JDBC connection manager, which allows this class to be connected to the database
	 * @param nurse the Client representing the currently‐logged‐in doctor
	 */
	public NurseMenu(ConnectionManagerJDBC cm, Client nurse) {
		// Initialize all the attributes 
        this.connectionManager = cm; // Passing one cm is simpler than passing five or six different manager objects, cm includes all of them
        this.productManager = new ProductManagerJDBC(cm);
        this.orderManager = new OrderManagerJDBC(cm);
        this.productOrderManager = new ProductOrderManagerJDBC(cm);
        this.clientManager = new ClientManagerJDBC(cm);
        this.nurse = nurse;
        this.cart = new Order(nurse); // Initialize a brand-new, empty cart. In this way we are assigning an Order to a Client, which is a doctor in this case 
		this.xmlMan = new ManagerImplXML();
	}
	 
	/**
	 * Method that displays the doctor's menu.
	 */
	public void displayNurseMenu() { 
		Output.println("\n=== Welcome to the Nurse Menu! ===");
		while(true) {
        	Output.println("\nDear nurse, please introduce the number of the operation you wish to perform:");
            Output.println("1. View my cart");
            Output.println("2. Shop");
            Output.println("3. Pay");
            Output.println("4. View my orders");
            Output.println("5. Account settings"); // Includes the UPDATE methods 
            Output.println("0. Go back");

            int choice = InputKB.readInteger();
            
            try {
            	switch (choice) {
                case 1:  
                	viewCart();          
                	break;
                case 2: 
                	Product chosenProduct = chooseProduct();
                	productShoppingMenu(chosenProduct);
                	break;
                case 3: 
                	pay();
                	break; 
                case 4:
                	List<Order> ordersOfDoctor = nurse.getOrders();
                	if(ordersOfDoctor != null ) {
                    	Output.println("\n============================== ORDER RECORD ==============================");
                    	for(int i=0; i<ordersOfDoctor.size(); i++) {
                    		Order order = ordersOfDoctor.get(i);
                    		Output.println("ORER " + (i+1) + ": ");
                    		Output.println("- Order ID: " + order.getOrderId());
                    		Output.println("- Order date: " + order.getOrderDate());
                    		Output.println("- Order Status: " + order.getOrderStatus());
                    		if(order.getOrderStatus() != OrderStatus.CANCELLED) {
	                    		Output.println("- Payment: \tPayment ID: " + order.getPayment().getPaymentId() + "\tPayment method: " + order.getPayment().getPaymentMethod() + "\tTotal paid: " + order.getPayment().getAmount());
	                    		Output.println("- Shipment: \tShipment ID: " + order.getShipment().getShipmentId() + "\tTracking Number: " + order.getShipment().getTrackingNumber());
	                    		Output.println("- Product Orders: ");
	                    		List<ProductOrder> productOrders = order.getProductOrders();
	                    		for(int j = 0; j < productOrders.size(); j++) {
	                    			ProductOrder productOrder = productOrders.get(j);
	                    			Output.println("\t- Product Order " + (j+1) + ": \tProduct: " + productOrder.getProduct().getName() + "\t\tAmount ordered: " + productOrder.getAmount() + "\tPrice: " + productOrder.getTotalPrice());
	                    		}
                    		}
                        }
                    	Output.println("\n\n");
                	} else {
                		Output.println("\nYou have not made any orders with us yet... \nLet's change that!  :)");
                	}

                	break;
                case 5:                
                	accountSettings();
                	break;
                case 0:
                	verifyPendingOrder();
	                Output.println("Redirecting to home page ...");
	                //TODO CALL THE MAIN MENU
	                //System.exit(0); //I close the application
	                break;
                default: 
                	Output.println("Invalid option. Please try agin introducing a valid number.");    
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
	
	/**
	 * Method that offers the user different options once they have selected a product.
	 * @param chosenProduct object of the class Product that stores the product the user chose.
	 */
	public void productShoppingMenu(Product chosenProduct) {
		boolean keepGoing = true;
		while(keepGoing) {
			try {
				Output.println("\nWhat do you want to do with this product?");
		    	Output.println("1. Add to my cart");
		    	Output.println("2. See more details of the product");
		    	Output.println("3. See product as XML");
		    	Output.println("0. Go back");
		    	int option = InputKB.readInteger();
		    	
		    	switch(option) {
		    	case 1:
		        	int amount = chooseAmount(chosenProduct);
		        	if(amount == 0) {
		        		displayNurseMenu();
		        	} else {
		        		addToCart(chosenProduct, amount);
		        	}
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
		    	case 0:
		    		displayNurseMenu();
		    		keepGoing = false;
		    		break;
				default:
					Output.println("The introduced number is invalid. Please try again!");
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
	 * @throws SQLException
	 */
	public void viewCart() throws OrderExceptions, ClientException, SQLException { 
		// Check for a brand new cart that’s never been inserted in the DB: If the user never calls addToCart(), and thus never inserted the Order into the DB, cart.getOrderId() will be null
		// Recap: at this point the object Order has been created in Java thanks to the constructor, but it is not inserted in the DB until addToCart() is called
		if(cart == null || cart.getProductOrders().isEmpty()) {
			Output.println("\nYour cart is empty.");
			return; // Tells Java “stop executing this method right now and go back to the caller”, it is a way of exiting the method
		} 
		
		// Now it's safe to pull the product orders from the DB -> the corresponding manager is used for this purpose 
		List<ProductOrder> productsOrders = cart.getProductOrders();
		
		// Otherwise, show all products 
		Output.println("\nYour current cart items:");
    	// Print column headers
        Output.println(String.format("%-20s \t\t%5s \t\t\t%10s", "Product", "Quantity", "Total"));
        Output.println("----------------------------------------------------------------------");
        for (ProductOrder po : productsOrders) {
            String name = po.getProduct().getName();
            int amount = po.getAmount();
            float totalPrice = po.getTotalPrice();
            // Same formatted row as before
            Output.println(String.format("  - %-18s %5d %10.2f", name, amount, totalPrice));
         } 
        
        Output.println("Press 1 if you want to edit your cart or another number otherwise");
        int choice = InputKB.readInteger();
        if(choice == 1) {
        	editOrder();
        }
	}
	/**
	 * Method that offers the user different options to edit their order.
	 * @throws ClientException
	 * @throws SQLException 
	 */
	private void editOrder() throws ClientException, SQLException{
		boolean keepGoing2 = true;
		while(keepGoing2) {
			Output.println("Press: ");
			Output.println("1. If you want to change the quantity of a product");
			Output.println("2. If you want to delete a product");
			Output.println("3. If you want to continue shopping");
			int choice = InputKB.readInteger();
			boolean keepGoing = true;
			ProductOrder productOrderToEdit = null;
			while(keepGoing) {
				Output.println("Introduce the number of the product you want to edit: ");
				List<ProductOrder> productOrders = cart.getProductOrders();
				for(int i=0; i<productOrders.size(); i++) {
					Output.println("" + (i+1) + ". " + productOrders.get(i).getProduct().getName());
				}
				int option = InputKB.readInteger();
				if(option <= 0 || option >= productOrders.size()+1) {
					Output.println("The introduced number is invalid. Please, try again!");
				} else {
					keepGoing = false;
					productOrderToEdit = productOrders.get(option-1);
				}
			}
			List<ProductOrder> productOrders;
			switch(choice) {
			case 1:
				int currentAmount = productOrderToEdit.getAmount();
				Output.println("Currently you have " + currentAmount + " units of '" + productOrderToEdit.getProduct().getName() + "' ");
				Product product = productOrderToEdit.getProduct();
				//We reset the stock of the product by adding the units the user had ordered (because the received amount will be the total units he she wants to order)
				productManager.updateProductStock(product, currentAmount, true);
				//I call the method that asks the user how many units of the product they want to order
				int totalAmount = chooseAmount(productOrderToEdit.getProduct());
				if(totalAmount == 0) {
					displayNurseMenu();
				}
				Order order = productOrderToEdit.getOrder();				

				//We reset the stock of the product by adding the units the user had ordered (because the received amount will be the total units he she wants to order)
				
				productOrders = cart.getProductOrders();
				int amountOfProductOrders = productOrders.size();
				for(int i=0; i<amountOfProductOrders; i++) {
					if (productOrders.get(i).equals(productOrderToEdit)) {
						ProductOrder productOrder = productOrders.get(i);
						productOrder.setAmount(totalAmount);
						productOrder.setTotalPrice(totalAmount * product.getPrice());
						//I reset the list of product orders associated to the order 
						cart.setProductOrders(productOrders);

						try {
							//I call the method of product order manager that updates the amount of the product in the product order
							productOrderManager.updateProductAmountInAnOrder(product.getProductId(), order.getOrderId(), totalAmount); 
						} catch(RuntimeException rte) {
							System.out.println("ERROR: " + rte);
						}
						Output.println(totalAmount + " x '" + product.getName() + "' added to your cart.");
						Output.println("The product amount was successfully updated in your order!");				
						break;
					}
				}
				keepGoing2 = false;
				displayNurseMenu();
				break;
			case 2:
				productOrders = cart.getProductOrders();
				for(int i=0; i < productOrders.size(); i++) {
					ProductOrder productOrder = productOrders.get(i);
					if (productOrders.get(i).equals(productOrderToEdit)) {
						//I update the stock of the product involved in the product order the user wants to delete
						productManager.updateProductStock(productOrder.getProduct(), productOrder.getAmount(), true);
						//I delete the product order from the database (which automatically increases the stock of the product)
						productOrderManager.deleteProductOrderByIDs(cart.getOrderId(), productOrders.get(i).getProduct().getProductId());
						//I remove the product order from the list of product orders associated to the order
						productOrders.remove(productOrders.get(i));
						//I reset the list of product orders associated to the order
						cart.setProductOrders(productOrders);
						Output.println("The product order was successfully removed from your order!");
						break;
					}
				}
				keepGoing2 = false;
				displayNurseMenu();
				break;
			case 3: 
				keepGoing2 = false;
				chooseProduct();
				break;
			default:
				Output.println("The introduced number is invalid. Please, try again!");
	
			}
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
		List<ProductOrder> productOrders = cart.getProductOrders();
		for(int i=0; i<productOrders.size(); i++) {
			ProductOrder productOrder = productOrders.get(i);
			Product orderedProduct = productOrder.getProduct();
				if(orderedProduct.getProductId() == product.getProductId()) {
					//If the program gets here, it is because the user has already made an order of this product.
					//According to out program logic, a user cannot make 2 product orders of the same product under the same order.
					//Therefore, we will let the user know that he has already ordered this product and ask them if they want to update the amount of units of the product they are buying
				Output.println("\nYou have already ordered " + productOrder.getAmount() + " units of '" + orderedProduct.getName() + "'");
				Output.println("Press 1 if you want to update your order or another number if you want to shop other products!");
				int choice = InputKB.readInteger();
				if(choice == 1) {
					Output.println("\nRedirecting to update order ...");
					editOrder();
					break;
				} else {
					chooseProduct();
					displayNurseMenu();
				}
			}
		}
		//If the chosen product does not crash with any product order of the order, we add the product order to the cart
		// ADD A PRODUCT ORDER TO AN ORDER IN JAVA -> link them both
		ProductOrder productOrder = new ProductOrder(amount, cart, product); // Attach our in-memory Order (cart) to a ProductOrder
		// Add the productOrder to the list of products orders of the cart (order)
		productOrders.add(productOrder);
		cart.setProductOrders(productOrders);
		
		// INSERT THE NEW ROWS OF THE CORRESPONDING TABLES IN THE DB 
		if(cart.getOrderId() == null) { // Insert the Order (cart) in the DB if it does not exist -> the DB will generate its ID with AUTOINCREMENT
			orderManager.insertOrder(cart); 
			productOrderManager.insertProductOrder(productOrder); 
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
	        	//I get all the products of the introduced category and filter them depending on their prescription dependency
	            List<Product> allProductsOfCategory = productManager.getProductsByCategory(category);
	            candidates = new ArrayList<>();
	            for(int i=0; i<allProductsOfCategory.size(); i++) {
	            	if(!allProductsOfCategory.get(i).getNeedPrescription()) {
	            		candidates.add(allProductsOfCategory.get(i));
	            	}
	            }
	            break;
	          case 2:
	        	//I get all the products and filter them depending on their prescription dependency
	        	List<Product> allPrdocuts = productManager.getAllProducts();
	            candidates = new ArrayList<>();
	            for(int i=0; i<allPrdocuts.size(); i++) {
	            	if(!allPrdocuts.get(i).getNeedPrescription()) {
	            		candidates.add(allPrdocuts.get(i));
	            	}
	            }
	            break;
	          case 0:
	        	  displayNurseMenu();
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

	/**
	 * Method that asks the user to introduce the amount they want to add to the cart of the chosen product.
	 * @param product
	 * @return an integer that stores the amount of the chosen product the user want to buy
	 */
	private int chooseAmount(Product product) {
		int amount = 0;
		boolean keepGoing = true;
		while(keepGoing) {
			Output.println("How many units of '" + product.getName() + "' would you like to order?");
			amount = InputKB.readInteger();
			if(amount < 0) {
				Output.println("Quantity must be at least 1 (or 0 if you wish to order no units). Try again:\n");
			} else if(product.getStockQuantity() == 0){
				Output.println("\n'" + product.getName() + "' is out of stock :(");
				Output.println("This product will be restocked shortly, sorry for the inconvenience.");
				amount = 0;
				keepGoing = false;
				break;
			} else if (amount > product.getStockQuantity()) {
				Output.println("\nThere are not enough units of " + product.getName() + " in stock. Please enter an amount equal or smaller than " + product.getStockQuantity() + " ");
			}  else {
				keepGoing = false;
				break;
			}
		}
		return amount; 
	}
	 
	 /**
	  * Method that asks the user to select the category in which they are interested in.
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

	     // Build & persist the shipment
	     Shipment shipment = new Shipment(cart); // Link the user's cart (Order) to the Payment
	     connectionManager.getShipmentManager().insertShipment(shipment);
	     
	     // Ask for payment details
	     Output.println("\n===================== Payment Information =====================");
	     //Output.println("Total to pay: $" + String.format("%.2f", cart.getProductOrders().getTotalPrice())); 
	       // assume you have added a getTotalAmount() helper on Order
	     
	     // Calculate the total price of the Order (cart)
	     float productTotalPrice = 0;
         int orderTotalPrice = 0;
	     List<ProductOrder> productsOrders = productOrderManager.getProductOrdersByOrderID(cart.getOrderId());
	     for (ProductOrder po : productsOrders) {
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
				 //We call the method that either approves or rejects the payment
				 if(approvePayment()) { 
					 //If the payment was processed correctly, we create the payment and the process the order of the user
					 paymentMethod = methods[choice-1];
					 keepGoing = false; //We set the boolean variable to false in order to exit the while loop
					 
				     // Build & persist Payment
				     Payment payment = new Payment();
				     payment.setOrder(cart);
				     payment.setAmount(orderTotalPrice);
				     payment.setPaymentMethod(paymentMethod);
				     payment.setPaymentStatus(PaymentStatus.COMPLETED);
				     connectionManager.getPaymentManager().insertPayment(payment);
				     cart.setPayment(payment);
	
				     // Set the order status into “ORDERED”
				     cart.setOrderStatus(OrderStatus.ORDERED);
				     orderManager.updateOrderStatus(cart.getOrderId(), OrderStatus.ORDERED);
	
				     Output.println("\nPayment received and order placed! Your order ID is " + shipment.getTrackingNumber());
				     List<Order> ordersOfClient = nurse.getOrders();
				     if(ordersOfClient == null) {
				    	 ordersOfClient = new ArrayList<>();
				     }
				     ordersOfClient.add(cart); //We add the new order to the retrieved list of orders of the user
			    	 nurse.setOrders(ordersOfClient); //We reset the list of order of the user to the actualized one
			    	 
				     // Print the shipment details
				     Output.println("\n===================== Shipping Information =====================");	   
				     System.out.println(shipment);
				     cart.setShipment(shipment); 
				     int daysUntilDelivery = calculateDeliveryDate();
				     Output.println("Delivery date: " + LocalDate.now().plusDays(daysUntilDelivery));
				     Output.println("Your order will be delivered in " + daysUntilDelivery + " days! ");
				     Output.println("\n\n\n---------------- <3 THANK YOU FOR BUYING AT HOSPICART <3 ----------------\n\n\n");
				     
			         cart = new Order(nurse); //I "reset" the cart
				 } else {
					 Output.println("\nSomething went wrong! Your payment was not processed correctly ... Please try again");
				 }
			 } else if(choice == 0) {
				 cart.setOrderStatus(OrderStatus.CANCELLED);
				 Output.println("Your order was cancelled.");
				 //We still add the cancelled order to the list of orders of the user because he/she should be able to see it in the summary of all the orders he/she made
			     List<Order> ordersOfClient = nurse.getOrders();
			     if(ordersOfClient == null) {
			    	 ordersOfClient = new ArrayList<>();
			     }
			     ordersOfClient.add(cart);
			     resetStock();
			     nurse.setOrders(ordersOfClient);
		         cart = new Order(nurse); //I "reset" the cart
				 keepGoing = false;
			 }
			 else {
				 Output.println("The introduced number is invalid, please try again.");
			 }
		 }
	 }
	 /**
	  * Method that re-stocks the products included in an order. This method will be used when the user cancels an order or logs out without checking out.
	  */
	 private void resetStock() {
		 List<ProductOrder> productOrders = cart.getProductOrders();
		 for(int i=0; i<productOrders.size(); i++) {
			 ProductOrder productOrder = productOrders.get(i);
			 productManager.updateProductStock(productOrder.getProduct(), productOrder.getAmount(), true);
		 }
	 }
	 
	 /**
	  * Method that generates a random number which will represent the amount of days the delivery of the order will take.
	  * @return an integer that stores the generated random number
	  */
	 private int calculateDeliveryDate() {
		 Random random = new Random();
	     return random.nextInt(30) + 1; // Generates a number between 1 and 30
	 }
	 
	 /**
	  * Method that generates a random number between 0.0 and 1.0 and returns true if the number is smaller than 0.7 or false otherwise.
	  * In this way, there is a 70% chance of the payment being approved and a 30% of not being approved (in real life, this method represents
	  * problems connecting with the bank, or not having enough money in the account, etc.)
	  * @return
	  */
	 private boolean approvePayment() {
	     Random random = new Random();
	        return random.nextDouble() < 0.7; // 70% chance of returning true
	 }

	 
	/**
	* Method that shows the user his/her personal data and asks if he/she wants to change the password or not.
	* @throws SQLException
	* @throws ClientException
	*/
	 public void accountSettings() throws SQLException, ClientException{
	    boolean keepGoing = true;
	    	
	    while(keepGoing) {	    	
	    	while(keepGoing) {
	        	Output.println("\nPress:");
	        	Output.println("1. See my personal information");
	        	Output.println("2. See my personal information as an XML");
	        	Output.println("3. LOG OUT");
	        	Output.println("0. Go back");	        
	        	int option = InputKB.readInteger();
	        	
		    switch(option) {
		    case 1:
		    	System.out.println(nurse);
				Output.println("\nUsername: " + nurse.getEmail());
		    	Output.println("Press 0 if you want to edit your profile or another number otherwise");
		    	int choice = InputKB.readInteger();
		    	if(choice == 0) {
		    		editPersonalInformation();
		    	}
		    	keepGoing = false;
		    	break;
		    case 2:
		    	client2Xml();
		    	break;
		    case 3:
		    	verifyPendingOrder();
		    	//TODO REDIRECT TO THE INITIAL MENU OF THE MAIN MENU
		    	break;
		   	case 0: 
		   		displayNurseMenu();
	    		keepGoing = false;
	    		break;
	    	default:
		    	Output.println("The number introduced is invlaid, please try again.");
		    	break;
		    }
	    }
	    }

	 }
	 /**
	  * Method that offers the user the possibility to change the personal information related to his/her account.
	  * @throws ClientException
	  * @throws SQLException
	  */
	 private void editPersonalInformation() throws ClientException, SQLException{
	    	Output.println("\nPress:");
	    	boolean keepGoing = true;
	    	while(keepGoing) {
	        	Output.println("1. Change my address");
	        	Output.println("2. Change my phone number");
	        	Output.println("3. Change my name");
	        	Output.println("4. Change my surname");
	        	Output.println("0. Go back");	        
	        	int option = InputKB.readInteger();
	        	
			    switch(option) {
			    case 1: 
			    	Output.println("Please introduce your new address: ");
		    		String newAddress = InputKB.readString();
		    		nurse.setAddress(newAddress);
		    		clientManager.updateAddress(nurse.getUserId(), newAddress);	
		    		Output.println("Your address was successfully updated!");
			    	keepGoing = false;
			   		accountSettings(); //I take the supplier back to the supplier menu
			   		break;
			    case 2:
			    	Output.println("Please introduce your new phone number: ");
		    		int newPhoneNumber = InputKB.readInteger();
		    		nurse.setPhoneNumber(newPhoneNumber);
		    		clientManager.updatePhoneNumber(nurse.getUserId(), newPhoneNumber);	
		    		Output.println("Your phone number was successfully updated!");
	
			    	keepGoing = false;
			    	accountSettings(); //I take the supplier back to the supplier menu
			   		break;
			    case 3: 
			    	Output.println("Please introduce your new name: ");
		    		String newName = InputKB.readString();
		    		nurse.setName(newName);
		    		clientManager.updateName(nurse.getUserId(), newName);	
		    		Output.println("Your name was successfully updated!");
			    	keepGoing = false;
			    	accountSettings(); //I take the supplier back to the supplier menu
			   		break;
			    case 4: 
			    	Output.println("Please introduce your new surname: ");
		    		String newSurname = InputKB.readString();
		    		nurse.setSurname(newSurname);
		    		clientManager.updateSurname(nurse.getUserId(), newSurname);	
		    		Output.println("Your surname was successfully updated!");
			    	keepGoing = false;
			    	accountSettings(); //I take the supplier back to the supplier menu
			   		break;
			    case 0: 
			    	keepGoing = false;
			    	accountSettings();
			    	break;
		    	default:
			    	Output.println("The number introduced is invalid, please try again.");
			    	break;
			    }
	    	}
	 }
	 
	 /**
	  * Method that checks if the user has any pending (i.e. unpaid) order and warns him/her that if he logs out without paying for the order, the order will be lost. 
	  * Therefore, the user is asked if he/she still wants to log out or if he/she wants to pay the order.
	  * @throws ClientException
	  * @throws SQLException
	  */
	 private void verifyPendingOrder() throws ClientException, SQLException{
		//THE ORDER IS PENDING AS SOON AS THE USER LOGS IN!!! ADD A CONTROL THAT CHECKS IF THE ORDER HAS ANY ORDER
		 if(!cart.getProductOrders().isEmpty() && cart.getOrderStatus() == OrderStatus.PENDING) {
			 Output.println("Just before you go, beware you have an unpaid order in your cart. If you log out, your order will be dismissed.");
			 Output.println("Press 0 if you want to log out or another number if you want to check out first");
			 int choice = InputKB.readInteger();
			 if(choice != 0) {
				 Output.println("Great! Redirecting to pay....");
				 pay();
			 } else{
				 resetStock();
				 System.out.println("Hope to see you again soon!");
				 //TODO LOG OUT
			 }
		 }
	 }
	 
	 /**
	  * Method that calls the Marshaller method that converts a client to XML format
	  */
	public void client2Xml() {
		//We call the Marshaller method
		xmlMan.client2Xml(nurse);				
		System.out.println("Your client information is in ./xmls/Client.xml");
	}
}