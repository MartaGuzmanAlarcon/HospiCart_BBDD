package HospiCartApp;

import java.io.IOException;


import Exceptions.ClientException;
import Utilities.Encryption;
import Utilities.*;
import HospiCartInterfaces.IClientManager;
import HospiCartInterfaces.IUserManager;
import HospiCartJDBC.ClientManager;
import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJPA.JPAUserManager;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Role;
import HospiCartPOJOs.User;

public class MainMenu {

    private static IUserManager userMan;
    private static IClientManager clientMan;

    public static void main(String[] args) {
        try {
            ConnectionManagerJDBC conMan = new ConnectionManagerJDBC();
            userMan = new JPAUserManager();
            clientMan = new ClientManager(conMan);
            //We declare a boolean variable and set it to false in order to create a while that prints the menu in the screen that runs until 
            //the value of this variable is changes (when the user wants to leave the application) 
            boolean exit = false;
            //The while runs as long as exit is false.
            while (!exit) {
            	//We print a welcome message and the first options of the menu of our application.
            	Output.println("\n=== Welcome to HospiCart ===");
            	Output.println("Dear customer, please introduce the respective number of the operation you wish to perform among the ones shown below:");
            	Output.println("1. Register as Doctor"); //TODO this should only be Register and then, if the user chose this option we should ask him/her to choose between nurse or doctor or supplier.
            	Output.println("2. Register as Nurse");
            	Output.println("3. Register as Supplier");
            	Output.println("4. Log In");
            	Output.println("0. Exit");
                //We create an variable of type integer, which will store the number the customer introduced in the screen.
                int input = InputKB.readInteger();
                //We create a switch which one case per each number the user might have introduced.
                switch (input) {
                    case 1:
                        registerDoctor();
                        break;
                    case 2:
                        registerNurse();
                        break;
                    case 3:
                        registerSupplier();
                        break;
                    case 4:
                        login(conMan);
                        break;
                    case 0:
                        conMan.disconnect();
                        Output.println("Application closed!");
                        //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
                        exit = true;
                        break;
                    default:
                    	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
                }
            }

        } catch (Exception e) {
        	Output.println("Critical system error:"); //TODO
            e.printStackTrace();
        }
    }

    public static void registerDoctor() throws IOException, ClientException {
    	Output.println("\n=== Doctor Registration ===");
        Client doctor = InputKB.getClientFromKB();
        clientMan.insertClient(doctor);

        User user = InputKB.getUserFromKB(doctor.getEmail());
        userMan.register(user);
        Role doctorRole = userMan.getRole("doctor");
        userMan.assignRole(user, doctorRole);

        Output.println("Doctor registration completed.");
        Output.println("Username: "+user.getEmail());
    }

    public static void registerNurse() throws IOException, ClientException {
    	Output.println("\n=== Nurse Registration ===");
        Client nurse = InputKB.getClientFromKB();
        clientMan.insertClient(nurse);

        User user = InputKB.getUserFromKB(nurse.getEmail());
        userMan.register(user);
        Role nurseRole = userMan.getRole("nurse");
        userMan.assignRole(user, nurseRole);

        Output.println("Nurse registration completed.");
        Output.println("Username: "+user.getEmail());
    }

    public static void registerSupplier() throws IOException, ClientException {
    	Output.println("\n=== Supplier Registration ===");
        Client supplier = InputKB.getClientFromKB();
        clientMan.insertClient(supplier);

        User user = InputKB.getUserFromKB(supplier.getEmail());
        userMan.register(user);
        Role supplierRole = userMan.getRole("supplier");
        userMan.assignRole(user, supplierRole);

        Output.println("Supplier registration completed.");
        Output.println("Username: "+user.getEmail());
    }

    public static void login(ConnectionManagerJDBC conMan) throws IOException {
    	Output.println("\n=== Log In ===");

        while (true) {
        	Output.println("Email: ");
            String email = InputKB.readString();
            Output.println("Password: ");
            String password = InputKB.readString();

            User user = userMan.login(email, password);

            if (user != null) {
                String roleName = user.getRole().getName();
                Output.println("Logged in as: " + roleName);

                switch (roleName) {
                    case "doctor":
                        new HospiCartApp.DoctorMenu(conMan, user).displayMenu();
                        break;
                    case "nurse":
                        new HospiCartApp.NurseMenu(conMan, user).displayMenu();
                        break;
                    case "supplier":
                        new HospiCartApp.SupplierMenu(conMan, user).displayMenu();
                        break;
                    default:
                    	Output.println("Unrecognized role.");
                        break;
                }
                break;
            } else {
            	Output.println("Incorrect email or password, or user not registered. Please try again.");
            }
        }
    }
}
