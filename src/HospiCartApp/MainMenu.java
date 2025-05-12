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
                IO.println("3. Log In");
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
                        login(conMan);
                        break;
                    case 0:
                        conMan.disconnect();
                        IO.println("Connection closed. Goodbye!");
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
        IO.println("Name: ");
        String name = IO.readString();
        IO.println("Surname: ");
        String surname = IO.readString();
        IO.println("Phone: ");
        Integer phone = IO.readInteger();
        IO.println("Address: ");
        String address = IO.readString();
        IO.println("Email: ");
        String email = IO.readString();
        IO.println("Password: ");
        String password = IO.readString();
        String encryptedPassword = Encryption.encryptPasswordMD5(password);


        Client doctor = new Client(name, surname, phone, address, email);
        clientMan.insertClient(doctor);

        User user = new User(email, encryptedPassword, email);
        userMan.register(user);
        Role doctorRole = userMan.getRole("doctor");
        userMan.assignRole(user, doctorRole);

        IO.println("Doctor registration completed.");
    }

    public static void registerNurse() throws IOException {
        IO.println("\n=== Nurse Registration ===");
        IO.println("Name: ");
        String name = IO.readString();
        IO.println("Surname: ");
        String surname = IO.readString();
        IO.println("Phone: ");
        Integer phone = IO.readInteger();
        IO.println("Address: ");
        String address = IO.readString();
        IO.println("Email: ");
        String email = IO.readString();
        IO.println("Password: ");
        String password = IO.readString();
        String encryptedPassword = Encryption.encryptPasswordMD5(password);


        Client nurse = new Client(name, surname, phone, address, email);
        clientMan.insertClient(nurse);

        User user = new User(email, encryptedPassword, email);
        userMan.register(user);
        Role nurseRole = userMan.getRole("nurse");
        userMan.assignRole(user, nurseRole);

        IO.println("Nurse registration completed.");
    }

    public static void login(ConnectionManagerJDBC conMan) throws IOException {
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
                    case "admin":
                        new HospiCartApp.AdminMenu(conMan, user).displayMenu();
                        break;
                    default:
                        IO.println("Unrecognized role.");
                        break;
                }
                break;
            } else {
                IO.println("Incorrect email or password. Please try again.");
            }
        }
    }
}
