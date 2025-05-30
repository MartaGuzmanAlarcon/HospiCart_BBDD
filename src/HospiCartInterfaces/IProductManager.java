package HospiCartInterfaces;

import java.sql.SQLException;
import java.util.List;

import HospiCartPOJOs.Category;
import HospiCartPOJOs.Manufacturer;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.Supplier;

public interface IProductManager {

    public void insertProductsIfNotExists(Product product) throws SQLException;
    
	public void insertProductsFromCSV(String filePath, List<Supplier> suppliers);

	boolean insertProduct(Product product) throws SQLException; // Agregar un nuevo producto
	
	boolean deleteProduct(int id); // Eliminar un producto
	
	Product getProductById(int id); // Obtener producto por ID

	List<Product> getAllProducts(); // Obtener todos los productos

	List<Product> getProductsByName(String name);

	List<Product> getProductsByCategory(Category type);

	List<Product> getProductsByManufacturer(Manufacturer manufacturer);

	List<Product> getLowStockProductsByCategory(Category category);
	
	public List<Product> getProductsBySupplier(int supplierId);

	boolean updateProduct(Product product); // Actualizar producto

	void updateProductStock(Product product, int amount, boolean increasing);
}
