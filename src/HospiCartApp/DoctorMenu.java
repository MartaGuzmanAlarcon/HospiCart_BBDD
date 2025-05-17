package HospiCartApp;

import java.util.List;

import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.ProductManagerJDBC;
import HospiCartPOJOs.Category;
import HospiCartPOJOs.Product;
import Utilities.Output;

public abstract class DoctorMenu {
	
	 private static ProductManagerJDBC productManager;
	 private static ConnectionManagerJDBC connectionManager;

	public static void displayMenu() {
		// TODO Auto-generated method stub
		
	}
	
	 public static void viewCart() {
		 //Order order = 
		 //order.getProductOrders()
	 }
	 
	 public static void browseAllProducts() {
		 List<Product> products = productManager.getAllProducts();
		 for(int i = 0; i < products.size(); i++) {
			 System.out.println(products.get(i));
		 }
	 } 
	 
	 public static void browseProductsByCategory(Category category) {
		 // 
	 }
	

}
