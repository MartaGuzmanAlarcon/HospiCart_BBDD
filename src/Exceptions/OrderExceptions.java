package Exceptions;

/**
 *  Class that contains personalized exceptions in order to handle with unexpected scenarios regarding the use of the instances
 *  of the "Order" class.
 */
public class OrderExceptions extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	 /**
     * Enumerate that contains the different types of exceptions we can find when dealing with instances of the class Order.
     */
    public enum ErrorTypeOrder{
        INVALID_CLIENT, INVALID_ORDER_ID
    }

    public ErrorTypeOrder errorTypeOrder;

    /**
     * Constructor that initializes the only attribute of this class, "errorTypeOrder" with a value which is received as parameter.
     * @param errorTypeOrder contains the type of exception, which is determined when creating an instance of 'Order' and encountering diverse unexpected situations.
     */
    public OrderExceptions(ErrorTypeOrder errorTypeOrder) {
        this.errorTypeOrder = errorTypeOrder;
    }
    /**
     * Getter for the attribute of the "OrderExceptions" class which makes the attribute of this class accessible.
     * @return the attribute of the class.
     */
    public ErrorTypeOrder getErrorTypeOrder() {
        return errorTypeOrder;
    }
    /**
     * Method to string which provides the string representation of each exception. It contains a switch case which
     * determines a specific string for each type of exception, which is to be returned in order to know why was the exception thrown.
     * @return a string which explains what was the exception.
     */
    public String toString() {
        switch (getErrorTypeOrder()) {
            case INVALID_CLIENT:
                return "The client received as paramenter is null, and therefore it is not valid.";
            case INVALID_ORDER_ID:
            	return "The receives order ID is not valid.";
            	
            default:
                return "An error has occurred.";
        }
    }
	
	

}
