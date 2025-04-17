package db.jdbcInterfaces;

import db.pojos.Category;
import db.pojos.Manufacturer;
import db.pojos.Product;
import java.util.List;

public interface IProductManager {

	Product getProductById(int id); // Obtener producto por ID

	List<Product> getAllProducts(); // Obtener todos los productos

	List<Product> searchProductsByName(String name);

	List<Product> getProductsByCategory(Category type);

	List<Product> getProductsByManufacturer(Manufacturer manufacturer);

	List<Product> getLowStockProductsByCategory(Category category);

	boolean addProduct(int supplierId, Product product); // Agregar un nuevo producto

	boolean updateProduct(Product product); // Actualizar producto

	boolean updateProductStockInDB(int productId, int newStock);

	boolean deleteProduct(int id); // Eliminar un producto

}
