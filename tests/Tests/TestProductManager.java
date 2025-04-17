package Tests;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import Utilities.Utilities;
import db.HospiCartJDBC.ConnectionManagerJDBC;
import db.HospiCartJDBC.ProductManager;
import db.HospiCartJDBC.SupplierManager;
import db.pojos.*;

public class TestProductManager {
	public static void main(String[] args) {

		ConnectionManagerJDBC cm = new ConnectionManagerJDBC();
		SupplierManager sm = new SupplierManager(cm);
		ProductManager pm = new ProductManager(cm); // establezco conexion

		 sm.insertSuppliersFromCSV("src/Utilities/data/Suppliers.txt");
		// System.out.println("supplies table values inserted");

		 pm.insertProductsFromCSV("src/Utilities/data/Products.txt");

		/*
		 * int testProductId = 3; Product product = pm.getProductById(testProductId);
		 * Supplier supplier= (Supplier) product.getSupplier();
		 * 
		 * 
		 * if (product != null) { System.out.println("Product found:");
		 * System.out.println("ID: " + product.getProductId());
		 * System.out.println("Name: " + product.getName());
		 * System.out.println("Category: " + product.getCategory());
		 * System.out.println("Description: " + product.getDescription());
		 * System.out.println("Price: " + product.getPrice());
		 * System.out.println("Stock: " + product.getStockQuantity());
		 * System.out.println("Company name: " + supplier.getCompanyName());
		 * System.out.println("¿Need prescription?: " + product.getNeedPrescription());
		 * } else { System.out.println("No se encontró ningún producto con ID " +
		 * testProductId); }
		 */

		/*
		 * Manufacturer manufact = Manufacturer.BAYER; List <Product> p =
		 * pm.getProductsByManufacturer(manufact);
		 * System.out.println("Producto encontrado: " + p);
		 */

		/*
		 * Category category = Category.DIAGNOSTIC_TOOLS; // Usa una categoría válida
		 * List<Product> products = pm.getProductsByCategory(category); for (Product
		 * product : products) { System.out.println(product); }
		 */

		/*
		 * List<Product> products = pm.getAllProducts(); for (Product product :
		 * products) { System.out.println(product); }
		 */

		// mas pruebas!
		// Prueba1:
		/*
		 * int testProductId = 3;
		 * 
		 * Product product = pm.getProductById(testProductId);
		 * System.out.println(product);
		 * 
		 * if (product != null) {
		 * 
		 * product.setPrice (Utilities.truncateBigDecimal( new BigDecimal("5.555"),2));
		 * product.setStockQuantity(50);
		 * 
		 * 
		 * boolean updated = pm.updateProduct(product);
		 * System.out.println("¿Actualización exitosa?: " + updated);
		 * System.out.println(product); } else {
		 * System.out.println("No se encontró el producto con ID: " + testProductId); }
		 */

		/*
		 * Prueba : Reducir stock int quantityToReduce = 50; boolean reduceStockResult;
		 * try { reduceStockResult = pm.reduceStock(3, quantityToReduce); } catch
		 * (SQLException e) {
		 * 
		 * e.printStackTrace(); }
		 */

		/*
		 * Prueba : Actualizar stock de producto int newStock = 5; boolean
		 * updateStockResult = pm.increaseStock(3, newStock);
		 * System.out.println("Stock update successful: " + updateStockResult);
		 */

		/*
		 * Prueba 6: Obtener productos con stock bajo por categoría Category category =
		 * Category.MEDICATIONS; // Usa una categoría válida List<Product>
		 * lowStockProducts = pm.getLowStockProductsByCategory(category);
		 * System.out.println("Low stock products in category " + category + ":"); for
		 * (Product p : lowStockProducts) { System.out.println(p); }
		 */

		/*
		 * String productName = "ecg"; List<Product> foundProducts =
		 * pm.searchProductsByName(productName);
		 * 
		 * if (foundProducts.isEmpty()) {
		 * System.out.println("No products found with the name: " + productName); } else
		 * {
		 * 
		 * for (Product p : foundProducts) { System.out.println(p); } }
		 */

		// pm.deleteProduct(1);
	/*	sm.showCompanyNames();
		// Proveedor seleccionado (por ejemplo, ID 1)
		int supplierId = 1;
		Product product = new Product(supplierId, "New Product", Category.MEDICATIONS, "Description",
				new BigDecimal("199.99"), 10, true);

		// Llamamos al método público
		boolean success = pm.addProduct(supplierId, product);
		if (success) {
			System.out.println("Product added successfully!");
		} else {
			System.out.println("Failed to add product.");
		}*/
	}
}
