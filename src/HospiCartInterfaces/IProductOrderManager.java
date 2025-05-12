package HospiCartInterfaces;

import java.sql.SQLException;
import java.util.List;

import HospiCartPOJOs.ProductOrder;

import HospiCartPOJOs.Order;

/**
 * Interface that defines the operations for managing product orders in the HospiCart system.
 * Implementing classes must provide functionality to create, retrieve, update, and delete product orders, as well as manage the products within those product orders.
 */
public interface IProductOrderManager {
	
	/**
	 * Method that receives a product and an order id by parameter and adds the received product to the order that corresponds with the received order id. i.e: the method creates a new
	 * product order with the order and product IDs received as parameter.
	 * @param product_id integer that stores the id of the product that we want to add to an order.
	 * @param order_id integer that stores the id of the order to which we want to add a product.
	 */
	void insertProductOrder(int product_id, int order_id)throws SQLException;
	
	/**
	 * Method that receives an order id and deletes the product orders associated to it.
	 * @param order_id integer that stores the id of an order.
	 */
	void deleteProductOrdersByOrderID(int order_id)throws SQLException;
	
	/**
	 * Method that receives the id of a product and the id of an order as parameter and deletes the product order whose IDs match with the received ones.
	 * @param product_id integer that stores the id of a product.
	 * @param order_id integer that stores the id of an order.
	 */
	void deleteProductOrderByIDs(int product_id, int order_id)throws SQLException;
	
	/**
	 * Method that receives the id of a product and the id of an order and returns the object of Product Order that corresponds with the received IDs.
	 * @param product_id integer that stores the id of a product.
	 * @param order_id integer that stores the id of an order.
	 * @return an object of the class product order whose id matches with the received one as parameter.
	 */
	ProductOrder getProductOrderByIDs(int product_id, int order_id);
	
	/**
	 * Method that receives an order id as parameter and returns a list that contains all the product orders whose order id matches the received one.
	 * @param order_id integer that stores the id of an order.
	 * @return a list that contains objects of "ProductOrder".
	 */
	List<ProductOrder> getProductOrdersByOrderID(int order_id);
	
	/**
	 * Method that receives an order id and returns the total price of the order that corresponds with the receives id.
	 * @param order_id integer that stores the id of the order whose products we wish to obtain.
	 * @return a floating number that stores the total price of the order.
	 */
	double getTotalPriceOfAnOrder(int order_id);
	
	/**
	 * Method that receives a product id and returns a list that contains all the orders that have the product passed as parameter.
	 * @param product_id integer that stores the id of the product of interest.
	 * @return a list containing instances of Order.
	 */
	List<Order> getOrdersWithAProduct(int product_id);
	
	/**
	 * Method that receives a product id and an amount and adds that amount of the specified product to the product's stock quantity.
	 * @param product_id integer that stores the id of a product.
	 * @param amount integer that stores the amount of the product that we want to add to the stock.
	 */
	void addProductToStockQuantity(int product_id, int amount) throws SQLException;
	
	/**
	 * Method that receives a product id and an amount and removes that amount of the specified product from the product's stock.
	 * @param product_id integer that stores a product id.
	 * @param amount integer that stores the amount of the product that we want to remove from the prosuct's stock.
	 */
	public void removeProductFromStockQuantity(int product_id, int amount)throws SQLException;
	
	/**
	 * Method that receives an order id, a product's id and a quantity. The method updates the amount of the received product for the quantity passed by parameter of the order that corresponds with the received order id.
	 * @param product_id integer that stores the id of the product whose quantity we wish to update.
	 * @param order_id integer that stores the id of the order which we wish to modify.
	 * @param amount integer that stores the amount we wish to order of the specified product.
	 */
	void updateProductAmountInAnOrder(int product_id, int order_id, int amount)throws SQLException;
}