package HospiCartApp;

import java.io.IOException;
import Exceptions.ClientException;
import Utilities.Encryption;
import Utilities.*;
import HospiCartJDBC.ClientManagerJDBC;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJPA.UserManagerJPA;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Role;
import HospiCartPOJOs.User;

public class MainMenu {

    private static UserManagerJPA userManager;
    private static ClientManagerJDBC clientManager;
    private static ConnectionManagerJDBC connectionManager;


    public static void main(String[] args) {
        try {
            connectionManager = new ConnectionManagerJDBC();
            userManager = new UserManagerJPA();
            clientManager = new ClientManagerJDBC(connectionManager);
            //We declare a boolean variable and set it to false in order to create a while that prints the menu in the screen that runs until 
            //the value of this variable is changes (when the user wants to leave the application) 
            boolean exit = false;
        	//We print a welcome message and the first options of the menu of our application.
        	Output.println("\n=== Welcome to HospiCart ===");
        	Output.println("Dear customer, please introduce the respective number of the operation you wish to perform among the ones shown below:");
        	Output.println("1. Register"); //TODO this should only be Register and then, if the user chose this option we should ask him/her to choose between nurse or doctor or supplier.
        	Output.println("2. Log In");
        	Output.println("0. Exit");
            //We create an variable of type integer, which will store the number the customer introduced in the screen.
            int input = InputKB.readInteger();
            //The while runs as long as exit is false.
            while (!exit) {
                //We create a switch which one case per each number the user might have introduced.
                switch (input) {
                    case 1:
                        register();
                        break;
                    case 2:
                        login();
                        break;
                    case 0:
                        connectionManager.disconnect();
                        Output.println("Application closed!");
                        //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
                        exit = true;
                        break;
                    default:
                    	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
                    	break;
                }
            }
        } catch (Exception e) {
        	Output.println("Critical system error:"); //TODO
            e.printStackTrace();
        }
    }
    
    /**
     * Method that asks the user to select the type of registration that suits the most according to their profession.
     * @throws IOException if any input/output error occurs.
     */
    public static void register() throws IOException{
    	Output.println("=== Registration ===");
    	Output.println("Dear customer, please introduce the respective number of the operation you wish to perform among the ones shown below:");
    	Output.println("1. Register as a doctor");
    	Output.println("2. Register as a nurse");
    	Output.println("3. Register as a supplier");
    	Output.println("0. Exit");
    	
    	//We create an variable of type integer, which will store the number the customer introduced in the screen.
        int input = InputKB.readInteger();
        //We create a switch which one case per each number the user might have introduced.
        boolean keepGoing = true;
        while(keepGoing) {
	        try {
	        	switch (input) {
		            case 1:
		                registerDoctor();
		                login();
		                break;
		            case 2:
		                registerNurse();
		                login();
		                break;
		            case 3:
		                registerSupplier();
		                login();
		                break;
		            case 0:
		                connectionManager.disconnect();
		                Output.println("Application closed! Hope to see you again soon!");
		                //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
		                keepGoing = false;
		                break;
		            default:
		            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
		            	break;
		        }
	        } catch(ClientException ce) {
	        	System.out.println("ERROR: " + ce);
	        }
        }
    }

    /**
     * Method that registers a user as a doctor.
     * @throws IOException if some input/ output error was produced.
     * @throws ClientException if the method "insertClient" throws an exception of this type.
     */
    public static void registerDoctor() throws IOException, ClientException {
    	Output.println("\n=== Doctor Registration ===");
        //We call the method that request the user to introduce information and retrieved the created client
    	Client doctor = InputKB.getClientFromKB();
    	String introducedEmail = doctor.getEmail();
    	
    	if(clientManager.isClientInDatabase(introducedEmail)) {
        	System.out.println("The introduced email already has an account. Redirecting to Log In ...");
        	login();
    	} else {
    		//We insert the client in the database
            clientManager.insertClient(doctor);
            //We call the method that creates a user with the email of the client as the username and a password he/she introduces by keyboard.
            User user = InputKB.getUserFromKB(doctor.getEmail());
            //We call the method of "JPAUserManager" that creates a user.
            userManager.register(user);
            
            Role doctorRole = userManager.getRole("doctor");
            userManager.assignRole(user, doctorRole);
            
            Output.println("Doctor registration completed! We are glad you are here! Welcome to the 'Hospicart' family.");
            Output.println("Username: " + user.getEmail());
    	}
    }
    
    /**
     * Method that registers a user as a nurse.
     * @throws IOException if some input/ output error was produced.
     * @throws ClientException if the method "insertClient" throws an exception of this type.
     */
    public static void registerNurse() throws IOException, ClientException {
    	Output.println("\n=== Nurse Registration ===");
        //We call the method that request the user to introduce information and retrieved the created client
        Client nurse = InputKB.getClientFromKB();
    	String introducedEmail = nurse.getEmail();

    	if(clientManager.isClientInDatabase(introducedEmail)) {
        	System.out.println("The introduced email already has an account. Redirecting to Log In ...");
        	login();
    	} else {
    		//We insert the client in the database
            clientManager.insertClient(nurse);
            //We call the method that creates a user with the email of the client as the username and a password he/she introduces by keyboard.
            User user = InputKB.getUserFromKB(nurse.getEmail());
            //We call the method of "JPAUserManager" that creates a user.
            userManager.register(user);
            
            Role nurseRole = userManager.getRole("nurse");
            userManager.assignRole(user, nurseRole);
            
            Output.println("Nurse registration completed! We are glad you are here! Welcome to the 'Hospicart' family.");
            Output.println("Username: " + user.getEmail());
    	}
    }  
    /**
     * Method that registers a user as a supplier.
     * @throws IOException if some input/ output error was produced.
     * @throws ClientException if the method "insertClient" throws an exception of this type.
     */
    public static void registerSupplier() throws IOException, ClientException {
    	Output.println("\n=== Supplier Registration ===");
        Client supplier = InputKB.getClientFromKB();
    	String introducedEmail = supplier.getEmail();

    	if(clientManager.isClientInDatabase(introducedEmail)) {
        	System.out.println("The introduced email already has an account. Redirecting to Log In ...");
        	login();
    	} else {
    		//We insert the client in the database
            clientManager.insertClient(supplier);
            //We call the method that creates a user with the email of the client as the username and a password he/she introduces by keyboard.
            User user = InputKB.getUserFromKB(supplier.getEmail());
            //We call the method of "JPAUserManager" that creates a user.
            userManager.register(user);
            
            Role supplierRole = userManager.getRole("supplier");
            userManager.assignRole(user, supplierRole);
            
            Output.println("Supplier registration completed! We are glad you are here! Welcome to the 'Hospicart' family.");
            Output.println("Username: " + user.getEmail());
    	}
    }
    
    /**
     * Method that, depending on the 
     * @param connectionManager
     * @throws IOException
     */
    public static void login() throws IOException {
    	Output.println("\n=== Log In ===");
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
            	//We obtain the role of the user.
                String roleName = user.getRole().getName();
                Output.println("It is nice to see you again, dear " + roleName + "!");

                switch (roleName) {
                    case "doctor":
                        DoctorMenu.displayMenu();
                        break;
                    case "nurse":
                        NurseMenu.displayMenu();
                        break;
                    case "supplier":
                        SupplierMenu.displayMenu();
                        break;
                    default:
                    	Output.println("Unrecognized role.");
                        break;
                }
                break;
            } else {
            	//If the method log in retrieves a null user, we print an error message
            	Output.println("Incorrect email or password, or user not registered. Please try again.");
            	Output.println("Select the number of the operation you wish to perform: ");
            	Output.println("1. Try again.");
            	Output.println("2. Register.");
            	Output.println("0. Close the application.");
            	
                int input = InputKB.readInteger();

                switch (input) {
                case 1:
                	//If the user introduced a 1, we exit the switch case but remain in the while and let him/her keep trying to sign in.
                	break;
                case 2:
                	//If the user introduced a 2, we go back to the method that registers users.
                	register();
                	break;
                case 0:
                    connectionManager.disconnect();
                    Output.println("Application closed! Hope to see you again soon!");
                    //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
                    keepGoing = false;
                    break;
                default:
                	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
                	break;
                }
            }
        }
    }
}
