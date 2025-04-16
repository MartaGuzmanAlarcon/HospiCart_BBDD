package db.jdbcInterfaces;

import db.pojos.Product;
import java.util.List;

public interface IProductManager {

	Product getProductById(int id); // Obtener producto por ID

	/*List<Product> getAllProducts(); // Obtener todos los productos

	boolean addProduct(Product product); // Agregar un nuevo producto

	boolean updateProduct(Product product); // Actualizar producto

	boolean deleteProduct(int id); // Eliminar un producto
	
	//Buscar por nombre, tipo (medicamento, material, etc.) o fabricante
	List<Product> searchProductsByName(String name);
	List<Product> getProductsByType(String type); 
	List<Product> getProductsByManufacturer(String manufacturer);

	// Ver productos con bajo stock
	List<Product> getLowStockProducts(int threshold); */
}
