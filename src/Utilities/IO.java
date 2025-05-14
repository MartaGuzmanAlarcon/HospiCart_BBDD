package Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import HospiCartPOJOs.Client;
import HospiCartPOJOs.User;

public class IO {
	
	 private static final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
	 
	 
	 public static Client gatherClientInfo() throws IOException {
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

	        return new Client(name, surname, phone, address, email);
	    }

	    public static User createUser(String email) throws IOException {
	        IO.println("Password: ");
	        String password = IO.readString();
	        String encryptedPassword = Encryption.encryptPasswordMD5(password);
	        return new User(email, encryptedPassword, email);
	    }
	 

	    public static int readInteger() {
	        int num = 0;
	        boolean ok = false;
	        do {
	            System.out.println("Please enter a number: ");

	            try {
	                num = Integer.parseInt(r.readLine());
	                if (num < 0) {
	                    ok = false;
	                    System.out.println("You didn't type a valid number.");
	                } else {
	                    ok = true;
	                }
	            } catch (IOException e) {
	                e.getMessage();
	            } catch (NumberFormatException nfe) {
	                System.out.println("You didn't type a valid number.");
	            }
	        } while (!ok);

	        return num;
	    }


	    public static String readString() {
	        String text = null;
	        boolean ok = false;
	        do {
	            try {
	                text = r.readLine();
	                if (text != null && !text.isBlank()) {
	                    ok = true;
	                } else {
	                    System.out.println("Empty input. Please try again:");
	                }
	            } catch (IOException e) {
	                System.out.println("Input error.");
	            }
	        } while (!ok);
	        return text;
	    }

	    public static void println(String message) {
	        System.out.println(message);
	    }

	    public static boolean validateEmail(String email) {
	        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
	        return email != null && email.matches(emailPattern);
	    }

}
