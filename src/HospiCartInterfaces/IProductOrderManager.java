package HospiCartInterfaces;

import java.util.List;

import HospiCartPOJOs.ProductOrder;

import HospiCartPOJOs.Product;
import HospiCartPOJOs.Order;


import java.util.List;

public interface IProductOrderManager {

	//boolean addProductOrder(ProductOrder productOrder); // Agregar un producto a mi pedido

	//List<ProductOrder> getProductOrdersByOrderId(int orderId); // Ver productos de un pedido

	//ProductOrder getProductOrderById(int productOrderId); // Ver un producto específico

	//boolean cancelProductOrder(int productOrderId); // Borrar un producto
	//TODO this method should delete the product order. How would this work? If I were to delete a product order, I would have to delete the order associated to it and what should I do with its products?
	
	//Actualizar cantidad del pedido
	boolean updateProductOrderQuantity(int productOrderId, int quantity);
	/**
	 * Method that receives a product and an order id by parameter and adds the received product to the order that corresponds with the received order id.
	 * @param product_id instance of the Product class that we want to add to an order.
	 * @param order_id integer that stores the id of the order to which we want to add a product.
	 */
	void addProductToAnOrder(int product_id, int order_id);
	
	/**
	 * Method that receives a product and an order id by parameter and removed the received product to the order that corresponds with the received order id.
	 * @param product_id instance of the Product class that we want to remove from an order.
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
	 * @return an integer that stores the total price of the order.
	 */
	int getTotalPriceOfAnOrder(int order_id);
	
	/**
	 * Method that receives a product id and returns a list that contains all the orders that have the product passed as parameter.
	 * @param product_id integer that stores the id of the product of interest.
	 * @return a list containing instances of Order.
	 */
	List<Order> getOrdersWithAProduct(int product_id);
	
	
	//GET PRODUCT ORDERS FROM ORDER ID
}