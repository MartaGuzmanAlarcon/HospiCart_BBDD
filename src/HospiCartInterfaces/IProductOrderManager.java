package HospiCartInterfaces;

import java.util.List;

import HospiCartPOJOs.ProductOrder;

public interface IProductOrderManager {

	boolean addProductOrder(ProductOrder productOrder); // Agregar un producto a mi pedido

	List<ProductOrder> getProductOrdersByOrderId(int orderId); // Ver productos de un pedido

	ProductOrder getProductOrderById(int productOrderId); // Ver un producto específico

	boolean cancelProductOrder(int productOrderId); // Borrar un producto
	
	//Actualizar cantidad del pedido
	boolean updateProductOrderQuantity(int productOrderId, int quantity);
}
