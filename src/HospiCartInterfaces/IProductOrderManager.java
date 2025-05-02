package HospiCartInterfaces;

import java.util.List;

import HospiCartPOJOs.ProductOrder;

import HospiCartPOJOs.Product;
import HospiCartPOJOs.Order;


import java.util.List;

public interface IProductOrderManager {

	//boolean addProductOrder(ProductOrder productOrder); // Agregar un producto a mi pedido
	
	
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
	 * Method that receives an order id and deletes the product orders associated to it.
	 * @param order_id integer that stores the id of an order.
	 */
	void deleteProductOrdersByOrderID(int order_id);
	
	/**
	 * Method that receives the id of a product order as parameter and deletes the product order whose id matches the received one.
	 * @param po_id integer that stores the id of a product order.
	 */
	/**
	 * Method that receives the id of a product and the id of an order as parameter and deletes the product order whose IDs match with the received ones.
	 * @param product_id integer that stores the id of a product.
	 * @param order_id integer that stores the id of an order.
	 */
	void deleteProductOrderByIDs(int product_id, int order_id);
	
	/**
	 * Method that receives a product and an order id by parameter and adds the received product to the order that corresponds with the received order id.
	 * @param product_id integer that stores the id of the product that we want to add to an order.
	 * @param order_id integer that stores the id of the order to which we want to add a product.
	 */
	void addProductToAnOrder(int product_id, int order_id);
	
	/**
	 * Method that receives a product and an order id by parameter and removed the received product to the order that corresponds with the received order id.
	 * @param product_id integer that stores the id of the product that we want to remove from an order.
	 * @param order_id integer that stores the id of the order from which we want to remove a product.
	 */
	void removeProductFromAnOrder(int product_id, int order_id);
	
	/**
	 * Method that receives an order id by parameter and removes all the products of the order that corresponds with the received id.
	 * @param order_id integer that stores the id of the order whose products we wish to remove.
	 */
	void clearOrder(int order_id);
	
	/**
	 * Method that receives an order id, a product's id and a quantity. The method updates the amount of the received product for the quantity passed by parameter of the order that corresponds with the received order id.
	 * @param product_id integer that stores the id of the product whose quantity we wish to update.
	 * @param order_id integer that stores the id of the order which we wish to modify.
	 * @param amount integer that stores the amount we wish to order of the specified product.
	 */
	void updateProductAmountInAnOrder(int product_id, int order_id, int amount);
	
	/**
	 * Method that receives an order id and returns a list with all the products contained in that order.
	 * @param order_id integer that stores the id of the order whose products we wish to obtain.
	 * @return a list with the products of the order.
	 */
	List<Product> getProductsFromOrder(int order_id);
	
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
}