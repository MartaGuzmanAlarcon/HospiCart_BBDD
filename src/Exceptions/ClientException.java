package Exceptions;

public class ClientException extends Exception {
	// Define the values that an error can adopt 
	public enum ErrorTypeClient{
		INVALID_CLIENT_ID, CLIENT_ALREADY_EXISTS;
	}
	
	// Attributes 
	public ErrorTypeClient errorTypeClient;
	
	// Constructor 
	public ClientException(ErrorTypeClient errorTypeClient) {
		super();
		this.errorTypeClient = errorTypeClient;
	}
	
	@Override
    public String toString() {
		switch(errorTypeClient) {
		case INVALID_CLIENT_ID : {
			return "Invalid client ID, no client was found";
		}
		case CLIENT_ALREADY_EXISTS : {
			return "The client already exists";
		}
		default :
			return "An error has occurred.";
		}
	}
	

}
