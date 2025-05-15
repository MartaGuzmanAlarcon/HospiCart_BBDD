package Tests;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.ProductOrder;

public class ProductOrderManagerTest {
	@Test
	public void insertProductOrderTest() {
		
	}
	
	/*void deleteProductOrdersByOrderID(int order_id) throws SQLException, OrderExceptions, ClientException;
	
	void deleteProductOrderByIDs(int product_id, int order_id) throws SQLException, OrderExceptions, ClientException;
	
	ProductOrder getProductOrderByIDs(int product_id, int order_id) throws OrderExceptions, ClientException;
	
	List<ProductOrder> getProductOrdersByOrderID(int order_id) throws OrderExceptions, ClientException;
	
	double getTotalPriceOfAnOrder(int order_id);
	
	List<Order> getOrdersWithAProduct(int product_id) throws OrderExceptions, ClientException;
	
	void addProductToStockQuantity(int product_id, int amount) throws SQLException;
	
	public void removeProductFromStockQuantity(int product_id, int amount)throws SQLException;

	void updateProductAmountInAnOrder(int product_id, int order_id, int amount)throws SQLException;
	*/
}
