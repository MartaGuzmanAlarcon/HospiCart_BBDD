package HospiCartInterfaces;

import java.sql.SQLException;
import java.util.List;

import HospiCartPOJOs.Category;
import HospiCartPOJOs.Manufacturer;
import HospiCartPOJOs.Product;

public interface IProductManager {

	//boolean insertProduct(int supplierId, Product product) throws SQLException; // Agregar un nuevo producto
	boolean insertProduct(Product product) throws SQLException; // Agregar un nuevo producto
	
	boolean deleteProduct(int id); // Eliminar un producto
	
	Product getProductById(int id); // Obtener producto por ID

	List<Product> getAllProducts(); // Obtener todos los productos

	List<Product> getProductsByName(String name);

	List<Product> getProductsByCategory(Category type);

	List<Product> getProductsByManufacturer(Manufacturer manufacturer);

	List<Product> getLowStockProductsByCategory(Category category);

	boolean updateProduct(Product product); // Actualizar producto

	boolean updateProductStockInDB(int productId, int newStock);
}
