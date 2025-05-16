package HospiCartApp;

import java.io.IOException;

import Exceptions.ClientException;
import Utilities.Encryption;
import Utilities.IO;
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
                IO.println("\n=== Welcome to HospiCart ===");
                IO.println("Dear customer, please introduce the respective number of the operation you wish to perform among the ones shown below:");
                IO.println("1. Register as Doctor"); //TODO this should only be Register and then, if the user chose this option we should ask him/her to choose between nurse or doctor or supplier.
                IO.println("2. Register as Nurse");
                IO.println("3. Register as Supplier");
                IO.println("4. Log In");
                IO.println("0. Exit");
                //We create an variable of type integer, which will store the number the customer introduced in the screen.
                int input = IO.readInteger();
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
                        IO.println("Application closed!");
                        //If the user introduces a 0, then we set the variable "exit" to true so we can exit the switch.
                        exit = true;
                        break;
                    default:
                        IO.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
                }
            }

        } catch (Exception e) {
            IO.println("Critical system error:"); //TODO
            e.printStackTrace();
        }
    }

    public static void registerDoctor() throws IOException, ClientException {
        IO.println("\n=== Doctor Registration ===");
        Client doctor = IO.gatherClientInfo();
        clientMan.insertClient(doctor);

        User user = IO.createUser(doctor.getEmail());
        userMan.register(user);
        Role doctorRole = userMan.getRole("doctor");
        userMan.assignRole(user, doctorRole);

        IO.println("Doctor registration completed.");
        IO.println("Username: "+user.getEmail());
    }

    public static void registerNurse() throws IOException, ClientException {
        IO.println("\n=== Nurse Registration ===");
        Client nurse = IO.gatherClientInfo();
        clientMan.insertClient(nurse);

        User user = IO.createUser(nurse.getEmail());
        userMan.register(user);
        Role nurseRole = userMan.getRole("nurse");
        userMan.assignRole(user, nurseRole);

        IO.println("Nurse registration completed.");
        IO.println("Username: "+user.getEmail());
    }

    public static void registerSupplier() throws IOException, ClientException {
        IO.println("\n=== Supplier Registration ===");
        Client supplier = IO.gatherClientInfo();
        clientMan.insertClient(supplier);

        User user = IO.createUser(supplier.getEmail());
        userMan.register(user);
        Role supplierRole = userMan.getRole("supplier");
        userMan.assignRole(user, supplierRole);

        IO.println("Supplier registration completed.");
        IO.println("Username: "+user.getEmail());
    }

    public static void login(ConnectionManagerJDBC conMan) throws IOException {
        IO.println("\n=== Log In ===");

        while (true) {
            IO.println("Email: ");
            String email = IO.readString();
            IO.println("Password: ");
            String password = IO.readString();

            User user = userMan.login(email, password);

            if (user != null) {
                String roleName = user.getRole().getName();
                IO.println("Logged in as: " + roleName);

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
                        IO.println("Unrecognized role.");
                        break;
                }
                break;
            } else {
                IO.println("Incorrect email or password, or user not registered. Please try again.");
            }
        }
    }
}
