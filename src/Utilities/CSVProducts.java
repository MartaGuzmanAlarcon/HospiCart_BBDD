package Utilities;

import HospiCartJDBC.*;

public class CSVProducts {
	
	public static void main(String[] args) {
        ConnectionManagerJDBC cm = new ConnectionManagerJDBC();
        ProductManager pm = new ProductManager(cm);
        SupplierManager sm = new SupplierManager(cm);
        
        sm.insertSuppliersFromCSV("Utilities/Suppliers.csv"); 
        
        pm.insertProductsFromCSV("Utilities/Products.csv");
    }

}
