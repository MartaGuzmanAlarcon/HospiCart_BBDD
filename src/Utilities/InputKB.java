package Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import HospiCartPOJOs.Client;
import HospiCartPOJOs.User;

public class InputKB {
	
	 private static final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
	 
	 /**
	  * Method that asks the user to introduce his/her personal information and creates a client.
	  * @return the created client
	  * @throws IOException if an input/output error occurred.
	  */
	 public static Client getClientFromKB() throws IOException {
         Output.println("Name: ");
         String name = InputKB.readString();
         Output.println("Surname: ");
         String surname = InputKB.readString();
         Output.println("Phone: ");
         Integer phone = InputKB.readInteger();
         Output.println("Address: ");
         String address = InputKB.readString();
         Output.println("Email: ");
         String email = InputKB.readString();
         //TODO THINK IF WE HAVE TO INCLUDE EXCEPTION IN THE CLIENT CONSTRUCTOR
        return new Client(name, surname, phone, email, address);
	    }
	 	/**
	 	 * Method that receives the clent's username (the email) as parameter and asks him/her to introduce the password, which is used to create the user.
	 	 * @param email username introduced by the client.
	 	 * @return the created user
	     * @throws IOException if an input/output error occurred.
	 	 */
	    public static User getUserFromKB(String email) throws IOException {
	    	//We ask the client to introduce the password.
	    	Output.println("Password: ");
	    	String password = InputKB.readString();
	    	String encryptedPassword = Encryption.encryptPasswordMD5(password);
	    	//We create the user and return it
	    	return new User(email, encryptedPassword, email);
	    }
	 

	    /**
	     * Function that reads the integer input  the user introduced
	     * @return an int variable that stores the input introduced by the user
	     */
	    public static int readInteger(){
	        /*The while is a controlled loop that will run until a "return" if found within the code. This will only happen when the
	        input introduced by the user is an int (when the "parse" conversion is accomplished) and this value is returned. On the
	        other hand, if the input introduced is not an integer type, it won't be possible to convert it to an int value, which will
	         throw an exception, causing the while loop to repeat itself again.*/
	        while(true){
	            try{
	                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	                String readString = br.readLine(); //Can throw an IOException
	                if(readString.trim().isEmpty()){
	                    //Trim function removes whitespaces, tabs, etc. Once removed, I check whether it is empty or not.
	                    throw new IllegalArgumentException("ERROR: input cannot be empty or whitespace.");
	                }
	                int integer = Integer.parseInt(readString);
	                return integer;
	            } catch(IOException e){
	                System.out.println("ERROR: there was an error when reading you input, try again!");
	            } catch(IllegalArgumentException iae){
	                System.out.println("ERROR: The type of data you introduced is incorrect. Try again, now introducing an integer!");
	            }
	        }
	    }
	    
	    /**
	     * Function that reads the float input  the user introduced
	     * @return a float variable that stores the input introduced by the user
	     */
	    public static float readFloat(){
	        while(true){
	            try{
	                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	                String readString = br.readLine();
	                if(readString.trim().isEmpty()){
	                    throw new IllegalArgumentException("ERROR: input cannot be empty or whitespace.");
	                }
	                float floatVariable = Float.parseFloat(readString);
	                return floatVariable;
	            } catch(IOException e){
	                System.out.println("ERROR: there was an error when reading you input, try again!");
	            } catch(IllegalArgumentException iae){
	                System.out.println("ERROR: The type of data you introduced is incorrect. Try again, now introducing a floating-point number!");
	            }
	        }
	    }
	    
	    /**
	     * Function that reads the string input  the user introduced
	     * @return a string variable that stores the input introduced by the user
	     */
	    public static String readString(){
	        while(true){
	            try{
	                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	                String readString = br.readLine();
	                if(readString.trim().isEmpty()){
	                    throw new IllegalArgumentException("ERROR: input cannot be empty or whitespace.");
	                }
	                return readString;
	            } catch(IOException e){
	                System.out.println("ERROR: there was an error when reading you input, try again!");
	            } catch(IllegalArgumentException iae){
	                System.out.println("ERROR: The type of data you introduced is incorrect. Try again, now introducing a string!");
	            }
	        }
	    }
	    

	    public static boolean validateEmail(String email) {
	        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
	        return email != null && email.matches(emailPattern);
	    }
	    
	    

}
