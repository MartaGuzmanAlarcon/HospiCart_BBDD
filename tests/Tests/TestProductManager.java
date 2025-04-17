package Tests;

import java.math.BigDecimal;
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
		int testProductId = 3;

		Product product = pm.getProductById(testProductId);
		System.out.println(product);

		if (product != null) {

			product.setPrice (Utilities.truncateBigDecimal( new BigDecimal("5.555"),2));
			product.setStockQuantity(50);

		
			boolean updated = pm.updateProduct(product);
			System.out.println("¿Actualización exitosa?: " + updated);
			System.out.println(product);
		} else {
			System.out.println("No se encontró el producto con ID: " + testProductId);
		}

		/*
		 * // Prueba 3: Reducir stock int quantityToReduce = 5; boolean
		 * reduceStockResult = pm.reduceStock(testProductId, quantityToReduce);
		 * System.out.println("Stock reduced successfully: " + reduceStockResult);
		 * 
		 * // Prueba 4: Actualizar stock de producto int newStock = 50; boolean
		 * updateStockResult = pm.updateProductStockInDB(testProductId, newStock);
		 * System.out.println("Stock update successful: " + updateStockResult);
		 * 
		 * // Prueba 5: Verificar alerta de bajo stock Product lowStockProduct = new
		 * Product(testProductId, "Low Stock Product", Category.MEDICAL_EQUIPMENT,
		 * "Low stock description", 50, 2, false);
		 * pm.checkLowStockAlert(lowStockProduct); // Si el stock está bajo, debería
		 * mostrar una advertencia
		 * 
		 * // Prueba 6: Obtener productos con stock bajo por categoría Category category
		 * = Category.MEDICAL_EQUIPMENT; // Usa una categoría válida List<Product>
		 * lowStockProducts = pm.getLowStockProductsByCategory(category);
		 * System.out.println("Low stock products in category " + category + ":"); for
		 * (Product p : lowStockProducts) { System.out.println(p); }
		 */

	}
}
