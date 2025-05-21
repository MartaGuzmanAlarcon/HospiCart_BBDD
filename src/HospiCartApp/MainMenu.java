package HospiCartApp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import Exceptions.ClientException;
import Utilities.*;
import HospiCartJDBC.ClientManagerJDBC;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.ProductManagerJDBC;
import HospiCartJDBC.SupplierManagerJDBC;
import HospiCartJPA.UserManagerJPA;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.User;

public class MainMenu {

    private static UserManagerJPA userManager;
    private static ClientManagerJDBC clientManager;
    private static ConnectionManagerJDBC connectionManager;
    private static ProductManagerJDBC productManager;
    private static SupplierManagerJDBC supplierManager;
    //I create this variable in order to keep track of the activity of the user (i.e. if he /she has already logged in or not)
    private static boolean loggedIn = false; //TODO create a log out method that receives this variable and logs the user out only if the variable is true.


    public static void main(String[] args) {
        try {
            connectionManager = new ConnectionManagerJDBC();
            userManager = new UserManagerJPA();
            clientManager = new ClientManagerJDBC(connectionManager);
            productManager = new ProductManagerJDBC(connectionManager);
            supplierManager = new SupplierManagerJDBC(connectionManager);
            
            //TODO ADD CHECK TO INSERT THE SUPPLIER AND PRODUCTS
            setApplication(); //I call the method that prepares the application by inserting the products we sell and the supplier.
            
            //We declare a boolean variable and set it to false in order to create a while that prints the menu in the screen that runs until 
            //the value of this variable is changes (when the user wants to leave the application) 
            boolean exit = false;
        	//We print a welcome message and the first options of the menu of our application.
        	Output.println("\n========== Welcome to HospiCart ==========");
            //The while runs as long as exit is false.
            while (!exit) {
            	Output.println("Dear customer, please introduce the respective number of the operation you wish to perform among the ones shown below:");
            	Output.println("1. Register"); 
            	Output.println("2. Log In");
            	Output.println("0. Exit");
                //We create an variable of type integer, which will store the number the customer introduced in the screen.
                int input = InputKB.readInteger();
                //We create a switch which one case per each number the user might have introduced.
                switch (input) {
                    case 1:
                        register();
                        exit = true;
                        break;
                    case 2:
                        login();
                        exit = true;
                        break;
                    case 0:
                        closeConnections();
                        Output.println("Application closed!");
                        //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
                        exit = true;
                        break;
                    default:
                    	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
                    	break;
                }
            }
        } catch (IOException ioe) {
        	Output.println("ERROR: " + ioe);
        	ioe.printStackTrace();
        } catch (Exception e) {
        	Output.println("Critical system error:"); //TODO
            e.printStackTrace();
        } finally {
        	closeConnections();
        }
    }
    /**
     * Method to properly close all database connections
     */
    private static void closeConnections() {
        try {
            if (connectionManager != null) {
                connectionManager.disconnect();
            }
            if (userManager != null) {
                userManager.close();
            }
        } catch (Exception e) {
            System.out.println("Error closing connections: " + e.getMessage());
        }
    }

    /**
     * This method sets the "HospiCart" application by calling the method of Product that inserts all the products contained in a CSV file in the folder "Utilities".
     */
    public static void setApplication() {
    	if(!loggedIn) {
	    	try {
	            // Make sure connection is valid before proceeding
	            Connection c = connectionManager.getConnection();
	            if (c == null || c.isClosed()) {
	                // Reestablish connection if needed
	                connectionManager = new ConnectionManagerJDBC();
	                // Recreate product manager with new connection
	                productManager = new ProductManagerJDBC(connectionManager);
	            }
	            String filePathSuppliers = "src/Utilities/data/Suppliers.txt";
	            supplierManager.insertSuppliersFromCSV(filePathSuppliers);           
	            String filePathProducts = "src/Utilities/data/Products.txt";
	            productManager.insertProductsFromCSV(filePathProducts);
	        } catch (Exception e) {
	            System.out.println("Error setting up application: " + e.getMessage());
	            e.printStackTrace();
	        }
    	}
    }
    
    /**
     * Method that asks the user to select the type of registration that suits the most according to their profession.
     * @throws IOException if any input/output error occurs.
     */
    public static void register() throws IOException{
    	Output.println("\n========== Registration ==========");
    	
        //We create a switch which one case per each number the user might have introduced.
        boolean keepGoing = true;
        while(keepGoing) {
        	Output.println("Dear customer, please introduce the respective number of the operation you wish to perform among the ones shown below:");
        	Output.println("1. Register as a doctor");
        	Output.println("2. Register as a nurse");
        	Output.println("3. Register as a supplier");
        	Output.println("0. Exit");
        	
        	//We create an variable of type integer, which will store the number the customer introduced in the screen.
            int input = InputKB.readInteger();
	        try {
	        	switch (input) {
		            case 1:
		                registerDoctor();
		                login();
		                keepGoing = false;
		                break;
		            case 2:
		                registerNurse();
		                login();
		                keepGoing = false;
		                break;
		            case 3:
		                registerSupplier();
		                login();
		                keepGoing = false;
		                break;
		            case 0:
		            	closeConnections();
		                Output.println("Application closed! Hope to see you again soon!");
		                //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
		                keepGoing = false;
		                System.exit(0); //We include this line to terminate the running application
		                break;
		            default:
		            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
		            	break;
		        }
	        } catch(ClientException ce) {
	        	System.out.println("ERROR: " + ce);
	        }
	        catch(SQLException sqle) {
	        	System.out.println("ERROR: " + sqle);
	        }
        }
    }

    /**
     * Method that registers a user as a doctor.
     * @throws IOException if some input/ output error was produced.
     * @throws ClientException if the method "insertClient" throws an exception of this type.
     */
    public static void registerDoctor() throws IOException, ClientException, SQLException {
    	Output.println("\n========== Doctor Registration ==========");
        //We call the method that request the user to introduce information and retrieved the created client
    	Client doctor = InputKB.getClientFromKB();
    	String introducedEmail = doctor.getEmail();
    	
    	if(clientManager.isClientInDatabase(introducedEmail)) {
        	System.out.println("The introduced email already has an account. Redirecting to 'Log In' ...");
        	login();
    	} else {
    		//We insert the client in the database
            clientManager.insertClient(doctor);
            //We call the method that creates a user with the email of the client as the username and a password he/she introduces by keyboard.
            User user = InputKB.getUserFromKB(doctor.getEmail());
            //We call the method of "JPAUserManager" that creates a user.
            userManager.register(user, 1);
            
            Output.println("\nRegistration completed! We are glad you are here! \n\nWelcome to the 'Hospicart' family <3\n");
            Output.println("Username: " + user.getEmail());
    	}
    }
    
    /**
     * Method that registers a user as a nurse.
     * @throws IOException if some input/ output error was produced.
     * @throws ClientException if the method "insertClient" throws an exception of this type.
     */
    public static void registerNurse() throws IOException, ClientException, SQLException {
    	Output.println("\n========== Nurse Registration ==========");
        //We call the method that request the user to introduce information and retrieved the created client
        Client nurse = InputKB.getClientFromKB();
    	String introducedEmail = nurse.getEmail();

    	if(clientManager.isClientInDatabase(introducedEmail)) {
        	System.out.println("The introduced email already has an account. Redirecting to 'Log In' ...");
        	login();
    	} else {
    		//We insert the client in the database
            clientManager.insertClient(nurse);
            //We call the method that creates a user with the email of the client as the username and a password he/she introduces by keyboard.
            User user = InputKB.getUserFromKB(nurse.getEmail());
            //We call the method of "JPAUserManager" that creates a user.
            userManager.register(user, 2);
                   
            Output.println("\nRegistration completed! We are glad you are here! \n\nWelcome to the 'Hospicart' family <3\n");
            Output.println("Username: " + user.getEmail());
    	}
    }  
    /**
     * Method that registers a user as a supplier.
     * @throws IOException if some input/ output error was produced.
     * @throws ClientException if the method "insertClient" throws an exception of this type.
     */
    public static void registerSupplier() throws IOException, ClientException, SQLException {
    	Output.println("\n========== Supplier Registration ==========");
        Client supplier = InputKB.getClientFromKB();
    	String introducedEmail = supplier.getEmail();

    	if(clientManager.isClientInDatabase(introducedEmail)) {
        	System.out.println("The introduced email already has an account. Redirecting to 'Log In' ...");
        	login();
    	} else {
    		//We insert the client in the database
            clientManager.insertClient(supplier);
            //We call the method that creates a user with the email of the client as the username and a password he/she introduces by keyboard.
            User user = InputKB.getUserFromKB(supplier.getEmail());
            //We call the method of "JPAUserManager" that creates a user.
            userManager.register(user, 3);
              
            Output.println("Registration completed! We are glad you are here! \n\nWelcome to the 'Hospicart' family <3\n");
            Output.println("Username: " + user.getEmail());
    	}
    }
    
    /**
     * Method that, depending on the 
     * @param connectionManager
     * @throws IOException
     */
    public static void login() throws IOException {
    	if(!loggedIn) {
	    	Output.println("\n========== Log In ==========");
	    	boolean keepGoing = true;
	        while (keepGoing) {
	        	//We ask the user for its username (email) and password
	        	Output.println("Email: ");
	            String email = InputKB.readString();
	            Output.println("Password: ");
	            String password = InputKB.readString();
	            //We call the method that retrieves the user whose data corresponds with the introduced username and password
	            User user = userManager.getUser(email, password);

	            //We verify that the user retrieved is not null
	            if (user != null) {
	            	// We obtain the role of the user.
	                String roleName = user.getRole().getName();
	                Output.println("It is nice to see you again, dear " + roleName + "!");
	                loggedIn = true;
	                try {
	                	// Retrieve the matching client for this user -> THIS IS THE REASON WHY IT'S VERY IMPORTANT THAT CLIENT AND ROLE SHARE A COMMON ATTRIBUTE
		                Client loggedInClient = clientManager.getClientByEmail(user.getEmail()); // throws ClientException
		                
		                switch (roleName) {
		                case "doctor":
		                	DoctorMenu doctorMenu = new DoctorMenu(connectionManager, loggedInClient); 
	                        doctorMenu.displayDoctorMenu();
	                        break;
	                    case "nurse":
	                        NurseMenu.displayMenu();
	                        break;
	                    case "supplier":
	                    	SupplierMenu supplierMenu = new SupplierMenu(connectionManager, user); 
	                    	supplierMenu.displaySupplierMenu();	                       
	                    	break;
	                    default:
	                    	Output.println("Unrecognized role.");
	                        break;
	                }
		                
	                } catch(ClientException ce) {
        	        	System.out.println("ERROR: " + ce);
        	        }
	                break;
	            } else {
	            	//If the method log in retrieves a null user, we print an error message
	            	Output.println("\nSomething went wrong! \nIncorrect email, incorrect password or user not registered.");
	            	boolean askAgain = true;
	            	while(askAgain) {
		            	Output.println("Select the number of the operation you wish to perform: ");
		            	Output.println("1. Try again");
		            	Output.println("2. Register");
		            	Output.println("3. Reset password");
		            	Output.println("0. Close the application");
		            	
		                int input = InputKB.readInteger();
		
		                switch (input) {
		                case 1:
		                	//If the user introduced a 1, we exit the switch case but remain in the while and let him/her keep trying to sign in.
		                	askAgain = false;
		                	break;
		                case 2:
		                	//If the user introduced a 2, we go back to the method that registers users.
		                	register();
		                	keepGoing = false;
		                	askAgain = false;
		                	break;
		                case 3:
		                	resetPassword();
		                	askAgain = false;
		                	keepGoing = false;
		                	break;
		                case 0:
		                	closeConnections();
		                    Output.println("Application closed! Hope to see you again soon!");
		                    //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
		                    System.exit(0);
		                    break;
		                default:
		                	Output.println("\nThe number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
		                	break;
		                }
	            	}
	            }
	        }
    	} else {
    		Output.println("\nYou have already logged in!");
    	}
    }
    /**
     * Method that resets the password of a user.
     * @throws IOException if the method register throws an exception of this type.
     */
    public static void resetPassword() throws IOException{
    	//First, I ask print some messages
    	Output.println("\nDid you forget your password?");
    	Output.println("No worries! HospiCart's got you!");
    	//I declare two boolean variables that will be useful in order to make the method work
    	boolean keepGoing = true;
    	boolean tryAgain = true;
    	boolean reintroducePassword = true;
    	//I create an object of user and set it to null.
    	User user = null;
    	while(keepGoing) {
    		//I ask the client to introduce his/her email only if the user object is null or if the user wants to keep trying
    		if(tryAgain && user == null) {
    			Output.println("\nPlease introduce your email: ");
    			String email = InputKB.readString();
    			user = userManager.getUserByEmail(email);
    		}
    		//If the user is null it is because a user with the introduced email was not found. Therefore, I give him/her several options and act depending on the chosen one.
        	if(user == null) {
        		Output.println("\nThe introduced email does not have an account asociated to it.");
        		Output.println("Introduce: ");
        		Output.println("1. If you want to try again");
        		Output.println("2. If you want to register");
        		Output.println("3. If you want to go back");
        		Output.println("0. If you want to exit");
        		int option = InputKB.readInteger();
        		switch(option) {
        			case 1:
        				//If the user wants to keep trying, I set the try again variable to true and just exit the switch case.
        				tryAgain = true;
        				break;
        			case 2:
        				//If the user wants to register, I call the register method.
        				register();
        				keepGoing = false;
        				break;
        			case 3:
        				//If the user want to go back, I call the log in method.
        				login();
        				keepGoing = false;
        				break;
        			case 0:
        				//If the user wants to exit, I disconnect the application.
        				closeConnections();
	                    Output.println("Application closed! Hope to see you again soon!");
	                    System.exit(0); //I close the program
	                    break;
	                default:
	                	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
	                	tryAgain = false;
	                	break;	
        		}
        	} else { //If the use is not null it is because he/she has an account associated to it.
        		while(reintroducePassword) {
	        		//I ask the user to introduce and confirm the new password and I check that they are in fact equal.
	        		Output.println("Please introduce your new password: ");
	                String newPassword = InputKB.readString();
	                Output.println("Confirm new password: ");
	                String newPasswordConfirmed = InputKB.readString();
	                //If the introduced passwords are equal
	                if(newPassword.equals(newPasswordConfirmed)) {
	                	userManager.updatePassword(user.getEmail(), newPasswordConfirmed, false);
	                	//I call the setter method for password, which encrypts it and sets it as the password of the user.
	                	//TODO DO I HAVE TO ENCRYPT THE PASSWORD IN THE SETTER???
	                	user.setPassword(newPasswordConfirmed);
	                	Output.println("Password successfully reset!");
	                	Output.println("\nRedirecting to 'Log In'...");
	                	login();
	                }
	                else {
	                	//I tell the user that the password do not match and ask him/her what does he/she want to do.
	                	Output.println("\nThe introduced passwords do not match!");
	                	//TODO ADD A WHILE
	                	boolean askAgain = true;
	                	Output.println("Introduce: ");
	                	while (askAgain) {
		            		Output.println("1. If you want to try again");
		            		Output.println("2. If you want to go back");
		            		Output.println("0. If you want to exit");
		            		int option2 = InputKB.readInteger();
		            		switch(option2) {
			        			case 1:
			        				//If the user wants to keep trying, I set the try again variable to true and just exit the switch case.
			        				reintroducePassword = true;
			        				askAgain = false;
			        				break;
			        			case 2:
			        				keepGoing = false;
			        				reintroducePassword = false;
			        				tryAgain = true;
			        				askAgain = false;
			        				login(); //I call the login method 
			        				break;
			        			case 0:
			        				//If the user wants to exit, I disconnect the application.
			        				closeConnections();
				                    Output.println("Application closed! Hope to see you again soon!");
				                    System.exit(0); //I close the program
				                    break; //TODO VERIFY IF IT WORKS CORRECTLY!!
				                default:
				                	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
				                	tryAgain = false;
				                	break;	
		            		}
	                	}
	                }
        		}
        	}
    	}  
    }
}
