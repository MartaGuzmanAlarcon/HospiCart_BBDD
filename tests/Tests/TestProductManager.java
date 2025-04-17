package Tests;

import java.util.List;

import db.HospiCartJDBC.ConnectionManagerJDBC;
import db.HospiCartJDBC.ProductManager;
import db.HospiCartJDBC.SupplierManager;
import db.pojos.*;

public class TestProductManager {
	public static void main(String[] args) {

		ConnectionManagerJDBC cm = new ConnectionManagerJDBC();
		SupplierManager sm = new SupplierManager(cm);
		ProductManager pm = new ProductManager(cm); // establezco conexion

		// sm.insertSuppliersFromCSV("src/Utilities/data/Suppliers.txt");
		// System.out.println("supplies table values inserted");

		// pm.insertProductsFromCSV("src/Utilities/data/Products.txt");

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

		/*List<Product> products = pm.getAllProducts();
		for (Product product : products) {
			System.out.println(product);
		} */

	}
}
