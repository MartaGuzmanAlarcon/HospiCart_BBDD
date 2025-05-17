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
        INVALID_CLIENT, INVALID_ORDER_ID, INVALID_PAYMENT, INVALID_SHIPMENT, INVALID_PRODUCT_ORDER, INVALID_ORDER_DATE_FUTURE, INVALID_STATUS, DELETE_ERROR
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
                return "The client received as paramenter is null, and therefore it is not valid. The order could not be created";
            case INVALID_ORDER_ID:
            	return "The received order ID is not valid.";
            case INVALID_PAYMENT:
            	return "The payment of the order is invalid and the order could not be created.";
            case INVALID_SHIPMENT:
            	return "The shipment is invalid and the order could not be created.";
            case INVALID_PRODUCT_ORDER:
            	return "The list of product orders is empty and, therefore, an order could not be created (because an order must have at least one product order).";
            case INVALID_ORDER_DATE_FUTURE:
            	return "The date of the order is invalid. You can't introduce a future date as the date of the order.";
            case INVALID_STATUS:
            	return "The status is invalid.";
            case DELETE_ERROR:
            	return "The order can no longer be deleted due to its current state.";
            default:
                return "An error has occurred.";
        }
    }
	
	

}
