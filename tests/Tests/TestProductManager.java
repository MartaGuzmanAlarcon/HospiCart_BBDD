package Tests;


import db.HospiCartJDBC.ConnectionManagerJDBC;
import db.HospiCartJDBC.ProductManager;
import db.pojos.Product;


public class TestProductManager {
	public static void main(String[] args) {
    
        ConnectionManagerJDBC cm = new ConnectionManagerJDBC();
        ProductManager pm = new ProductManager(cm); // establezco conexion 

        pm.insertProductsFromCSV("src/Utilities/Products.txt");


        int testProductId = 1; 
        Product product = pm.getProductById(testProductId);

       
        if (product != null) {
            System.out.println("Product found:");
            System.out.println("ID: " + product.getProductId());
            System.out.println("Name: " + product.getName());
            System.out.println("Category: " + product.getCategory());
            System.out.println("Description: " + product.getDescription());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Stock: " + product.getStockQuantity());
            System.out.println("¿Need prescrption?: " + product.getNeedPrescription());
        } else {
            System.out.println("No se encontró ningún producto con ID " + testProductId);
        }
    }
}
