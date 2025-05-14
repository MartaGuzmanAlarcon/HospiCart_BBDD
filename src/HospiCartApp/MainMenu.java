package HospiCartApp;

import java.io.IOException;

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

            boolean exit = false;

            while (!exit) {
                IO.println("\n=== Welcome to HospiCart ===");
                IO.println("1. Register as Doctor");
                IO.println("2. Register as Nurse");
                IO.println("3. Register as Supplier");
                IO.println("4. Log In");
                IO.println("0. Exit");

                int input = IO.readInteger();

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
                        exit = true;
                        break;
                    default:
                        IO.println("Invalid option. Please try again.");
                }
            }

        } catch (Exception e) {
            IO.println("Critical system error:");
            e.printStackTrace();
        }
    }

    public static void registerDoctor() throws IOException {
        IO.println("\n=== Doctor Registration ===");
        Client doctor = IO.gatherClientInfo();
        // clientMan.insertClient(doctor);

        User user = IO.createUser(doctor.getEmail());
        userMan.register(user);
        Role doctorRole = userMan.getRole("doctor");
        userMan.assignRole(user, doctorRole);

        IO.println("Doctor registration completed.");
        IO.println("Username: "+user.getEmail());
    }

    public static void registerNurse() throws IOException {
        IO.println("\n=== Nurse Registration ===");
        Client nurse = IO.gatherClientInfo();
        // clientMan.insertClient(nurse);

        User user = IO.createUser(nurse.getEmail());
        userMan.register(user);
        Role nurseRole = userMan.getRole("nurse");
        userMan.assignRole(user, nurseRole);

        IO.println("Nurse registration completed.");
        IO.println("Username: "+user.getEmail());
    }

    public static void registerSupplier() throws IOException {
        IO.println("\n=== Supplier Registration ===");
        Client supplier = IO.gatherClientInfo();
        // clientMan.insertClient(supplier);

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
