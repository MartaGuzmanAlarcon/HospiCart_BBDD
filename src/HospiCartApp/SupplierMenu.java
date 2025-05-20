package HospiCartApp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import HospiCartJDBC.ConnectionManagerJDBC;
import HospiCartJDBC.ProductManagerJDBC;
import HospiCartJDBC.SupplierManagerJDBC;
import HospiCartJPA.UserManagerJPA;
import HospiCartPOJOs.Category;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.Supplier;
import HospiCartPOJOs.User;
import Utilities.InputKB;
import Utilities.Output;

public class SupplierMenu {
	private static ConnectionManagerJDBC connectionManager;
	private static SupplierManagerJDBC supplierManager;
	private static ProductManagerJDBC productManager;
	private static Supplier supplier;
	private static User user;
	private static UserManagerJPA userManager;
	
	/**
	 * Constructor of SupplierMenu for the given supplier, wired up to the shared JDBC connection.
	 * @param cm the JDBC connection manager, which allows this class to be connected to the database.
	 * @param supplier the currently‐logged‐in suppler.
	 */
	public SupplierMenu(ConnectionManagerJDBC cm, User supplierUser) {
		// Initialize all the attributes with DoctorMenu. instead of this. because they are static variables 
		SupplierMenu.connectionManager = cm; // Passing one cm is simpler than passing five or six different manager objects, cm includes all of them
		SupplierMenu.productManager = new ProductManagerJDBC(cm);
		SupplierMenu.supplierManager = new SupplierManagerJDBC(cm);
		//SupplierMenu.supplier = supplier;
		user = supplierUser; //TODO SEE IF THIS WORKS
		userManager = new UserManagerJPA();
	}

	public void displaySupplierMenu() {
		// TODO Auto-generated method stub
		Output.println("\n=== Welcome to the Supplier Menu! ===");
		String chosenCompany = outputCompanyNames();
		try {
			supplier = supplierManager.getSupplierByCompanyName(chosenCompany);
			setProductsToSupplier(supplier);
			Output.println("Logged in as '" + supplier.getCompanyName()+"', welcome back!");
			displaySupplierMenuOptions();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void displaySupplierMenuOptions() throws SQLException {
		Output.println("Dear supplier, please introduce the number of the operation you wish to perform:");
		boolean keepGoing = true;
		while(keepGoing) {
			Output.println("1. View my personal data.");
			Output.println("2. View the company's data.");
			Output.println("3. Manage products.");
			Output.println("4. Manage orders.");
			Output.println("0. Exit.");
			
			int option = InputKB.readInteger();
			switch(option) {
			case 1:
				viewPersonalData();
				keepGoing = false;
				break;
			case 2:
				viewCompanyData();
				keepGoing = false;
				break;
			case 3:
				manageProducts();
				keepGoing = false;
				break;
			case 4:
				//TODO CREATE THIS METHOD manageOrders();
				keepGoing = false;
				break;
			case 0:
                closeConnections();
                Output.println("Application closed!");
                //If the user introduces a 0, then we set the variable "keepGoing" to false so we can exit the switch.
                keepGoing = false;
                break;
            default:
            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
            	break;				
			}
		}
	}
	
    /**
     * Method to properly close all database connections
     */
    private void closeConnections() {
        try {
            if (connectionManager != null) {
                connectionManager.disconnect();
            }
        } catch (Exception e) {
            System.out.println("Error closing connections: " + e.getMessage());
        }
    }
	
	/**
	 * Method that gets all the company names of the suppliers and outputs them to the user.
	 * @return the chosen number.
	 */
	private String outputCompanyNames(){
		List<String> companyNames = supplierManager.getCompanyNames();
		boolean keepGoing = true;
		int option = 0;
		
		while(keepGoing) {
			Output.println("Introduce the number that corresponds to the name of your company:");
			if(!companyNames.isEmpty()) {
				for(int i=1; i< companyNames.size()+1; i++) {
					Output.println(" "+i+". "+ companyNames.get(i));
				}
				option = InputKB.readInteger();
				if(option > 0 && option <= companyNames.size()) {
					keepGoing = false;
				}
			}
		}
		return companyNames.get(option);
	}
	
    /**
     * Method that receives a supplier and retrieves the products that this company supplier. Then, sets the list of products to the supplier company.
     */
    private void setProductsToSupplier(Supplier supplier) {
    	List<Product> productsOfSupplier = productManager.getProductsByManufacturer(supplier.getCompanyName());
    	supplier.setProducts(productsOfSupplier);
    }
    
    /**
     * Method that shows the user his/her personal data and asks if he/she wants to change the password or not.
     */
    private void viewPersonalData() throws SQLException {
    	Output.println("Username: " + user.getEmail());
    	Output.println("Password: " + user.getPassword());
    	
    	Output.println("Do you want to change your password? Press 1 if yes or 0 if you want to go back.");
    	int option = InputKB.readInteger();
    	boolean keepGoing = true;
    	
    	while(keepGoing) {
	    	switch(option) {
	    	case 1: 
	    		resetPassword();
	    		keepGoing = false;
	    		break;
	    	case 0: 
	    		displaySupplierMenuOptions();
	    		keepGoing = false;
	    		break;
	    	}
    	}
    }
    
    /**
     * Method that resets the password of a user.
     * @throws IOException if the method register throws an exception of this type.
     */
    private void resetPassword() throws SQLException{
    	Output.println("HospiCart's got you!");
    	boolean reintroducePassword = true;
    	
        while(reintroducePassword) {
        	//I ask the user to introduce and confirm the new password and I check that they are in fact equal.
	  		Output.println("Please introduce your new password: ");
	        String newPassword = InputKB.readString();
	        Output.println("Confirm new password: ");
	        String newPasswordConfirmed = InputKB.readString();
	        //If the introduced passwords are equal
	        if(newPassword.equals(newPasswordConfirmed)) {
	               userManager.updatePassword(user.getEmail(), newPasswordConfirmed);
	               //I call the setter method for password, which encrypts it and sets it as the password of the user.
	               user.setPassword(newPasswordConfirmed);
	               Output.println("Password successfully reset!");
	               displaySupplierMenuOptions(); //I take the supplier back to the supplier menu
	        }
	        else {
	               //I tell the user that the password do not match and ask him/her what does he/she want to do.
	               Output.println("The introduced passwords do not match!");
	               Output.println("Introduce: ");
	               Output.println("1. If you want to try again.");
	               Output.println("2. If you want to go back.");
	               Output.println("0. If you want to exit.");
	               int option2 = InputKB.readInteger();
	               switch(option2) {
		        			case 1:
		        				//If the user wants to keep trying, I set the try again variable to true and just exit the switch case.
		        				reintroducePassword = true;
		        				break;
		        			case 2:
		        				reintroducePassword = false;
		        				break;
		        			case 0:
		        				//If the user wants to exit, I disconnect the application.
		        				closeConnections();
			                    Output.println("Application closed! Hope to see you again soon!");
		        				reintroducePassword = false;
		        				break; //TODO VERIFY IF IT WORKS CORRECTLY!!
			                default:
			                	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
			                	break;	
	            	}
	         }
       }
    }
    
    /**
     * Method that shows the user his/her personal data and asks if he/she wants to change the password or not.
     */
    private void viewCompanyData() throws SQLException{
    	System.out.println(supplier);
    	
    	Output.println("Press:");

    	boolean keepGoing = true;
    	
    	while(keepGoing) {
        	Output.println("1. If you want to change the company's address");
        	Output.println("2. If you want to change the company's contact number");
        	Output.println("0. If you want to go back");
        	
        	int option = InputKB.readInteger();
        	
	    	switch(option) {
	    	case 1: 
	    		Output.println("Please introduce the new address of the company: ");
	    		String newAddress = InputKB.readString();
	    		supplier.setAddress(newAddress);
	    		supplierManager.updateSupplierAddress(supplier.getSupplierId(), newAddress);
	    		keepGoing = false;
	    		break;
	    	case 2: 
	    		Output.println("Please introduce the new contact number of the company: ");
	    		int newContactNumber = InputKB.readInteger();
	    		supplier.setContactNumber(newContactNumber);
	    		supplierManager.updateSupplierContactNumber(supplier.getSupplierId(), newContactNumber);
	    		keepGoing = false;
	    		break;
	    	case 0: 
	    		displaySupplierMenuOptions();
	    		keepGoing = false;
	    		break;
            default:
            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
            	break;	
	    	}
    	}
    }
    /**
     * Method that outputs the menu regarding the administration of products and redirects the application according to the user's choice.
     * @throws SQLException
     */
    private void manageProducts() throws SQLException {
    	Output.println("Please introduce the number of operation you wish to perform regarding your products: ");
    	boolean keepGoing = true;
    	while(keepGoing) {
    		Output.println("1. View my products");
    		Output.println("2. Add a new product");
    		Output.println("3. Delete a product");
    		Output.println("4. Manage my products' stock");
    		Output.println("0. Back");
    		int option = InputKB.readInteger();
    		switch(option) {
    		case 1: 
    			int amountProductsOfSupplier = supplier.getProducts().size();
    			for(int i = 0; i < amountProductsOfSupplier; i++) {
    				Product product = supplier.getProducts().get(i);
    				Output.println("Product: \t\tProduct ID = " + product.getProductId() + "\t\tProduct Name = " + product.getName() + "\t\tDescription = " + product.getDescription()
    				+ "\t\tCategory = " + product.getCategory() + "\t\tPrice = " + product.getPrice() + "\t\tAmount in stock = " + product.getStockQuantity() + "\t\tNeeds prescription = " + product.getNeedPrescription());
    			}
    			keepGoing = false;
    			displaySupplierMenuOptions();
    			break;
    		case 2: 
    			createNewProduct();
    			keepGoing = false;
    			displaySupplierMenuOptions();
    			break;
    		case 3:
    			Output.println("Intoduce the ID of the product you want to delete among the following ones: ");
    			int productID = getPrdocutIDs();
    			productManager.deleteProduct(productID);
    			keepGoing = false;
    			displaySupplierMenuOptions();
    			break;
    		case 4:
    			manageProductsStock();
    			keepGoing = false;
    			displaySupplierMenuOptions();
    			break;
            default:
            	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
            	break;	
    		}
    	}
    }
    /**
     * Method that asks the user to introduce the required data to create a product and inserts it in the database.
     * @throws SQLException
     */
    private void createNewProduct() throws SQLException {
    	//I ask the user to introduce all the data needed in order to add a new product to the database.
		Output.println("Introduce the name of the new new product: ");
		String name = InputKB.readString();
		Output.println("Introduce the description of the new product: ");
		String description = InputKB.readString();
		Output.println("Introduce the number of the category of the new product out of the following ones: ");
		Category category = getCategoryOfPrdocuts();
		Output.println("Introduce the price of the new product: ");
		float price = InputKB.readFloat();
		Output.println("Introduce the amount in stock of the new product: ");
		int amountInStock = InputKB.readInteger();
		Output.println("Does the new product need prescription? (introduce true/false): ");
		String introducedString = InputKB.readString();
		boolean needPrescription = Boolean.parseBoolean(introducedString);
		Product newProduct = new Product(name, category, description, price, amountInStock, needPrescription);
		//I set the supplier of the product
		newProduct.setSupplier(supplier);
		//I call the method that inserts the products in the database
		productManager.insertProduct(newProduct);
    }
    
	 /**
	  * Method that shows the user the categories of the products.
	  * @return a variable of type Category.
	  */
	 private Category getCategoryOfPrdocuts() {
		 Category[] categories = Category.values();
		 while(true) {
			 for(int i = 0; i < categories.length; i++) {
				 Output.println(" " + i + ". " + " " + categories[i]);
			 }
			 int choice = InputKB.readInteger();
			 if(choice > -1 && choice < categories.length) {
				 return categories[choice];
			 } else {
				 Output.println("The introduced number is invalid, please try again.");
			 }
		 }
	 }
	 
	 /**
	  * Method that shows the user the IDs of the products the supplier has associated to it.
	  * @return a variable of type integer that stores the ID of the product the user wants to delete from the database.
	  */
	 private Integer getPrdocutIDs() {
		 List<Integer> productIDs = null;
		 for(int i=0; i< supplier.getProducts().size(); i++) {
			 productIDs.add(supplier.getProducts().get(i).getProductId());
		 }
		 while(true) {
			 for(int i = 0; i < productIDs.size(); i++) {
				 Output.println(" " + i + ". " + " " + productIDs.get(i));
			 }
			 int choice = InputKB.readInteger();
			 if(choice > -1 && choice < productIDs.size()) {
				 return productIDs.get(choice);
			 } else {
				 Output.println("The introduced number is invalid, please try again.");
			 }
		 }
	 }	 
	 
	 private void manageProductsStock() throws SQLException {
		 Output.println("Introduce the umber of the operation you wish to perform regarding the stock of your products: ");
		 Output.println("1. Check and update products with low stock");
		 Output.println("2. Update all products' stock");
		 Output.println("0. Go back");
		 int option = InputKB.readInteger();
		 List<Product> productsWithLowStock = new ArrayList<>();
		 switch(option) {
		 case 1:
			 for(int i=0; i<supplier.getProducts().size(); i++) {
				 Product product = supplier.getProducts().get(i);
				 boolean runningOutOfStock = productManager.checkLowStockAlert(product);
				 if(runningOutOfStock) {
					 productsWithLowStock.add(product);
				 }
			 }
			 boolean restocking = true;
			 while(restocking) {
				 Output.println("Do you wish to re-stock these products? Press 1 for YES and 0 for NO");
				 int wantsToRestock = InputKB.readInteger();
				 if(wantsToRestock == 1) {
					 restockProducts(productsWithLowStock);
					 restocking = false;
				 } else if(wantsToRestock == 0) {
					 restocking = false;
					 manageProducts();
				 } else {
			         Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
				 }
			 }
			 break;
		 case 2:
			 List<Product> allProductsOfSupplier = supplier.getProducts();
			 restockProducts(allProductsOfSupplier);
			 restocking = false;
			 break;
         default:
         	Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
         	break;
		 }
	 }
	 
	 /**
	  * Method that re-stocks the products that have low stock
	  * @param productsWithLowStock
	 * @throws SQLException 
	  */
	 private void restockProducts(List<Product> productsWithLowStock) throws SQLException {
		 boolean keepRestocking = true;
		 while(keepRestocking) {
			 Output.println("Intoduce the number of the product you want to restock: ");
			 for(int i=0; i<productsWithLowStock.size(); i++) {
				 Product product = productsWithLowStock.get(i);
				 int currentStock = product.getStockQuantity();
				 Output.println("Currently you have " + currentStock + " items of product with ID " + product.getProductId());
				 Output.println("Introduce the amount you want to increase the stock of product with ID " + product.getProductId() + " by: ");
				 int increaseStockBy = InputKB.readInteger();
				 if(increaseStockBy >= 0) {
					 //I set the new stock of the product
					 product.setStockQuantity(currentStock + increaseStockBy);
					 //I call the method that updates the stock of the product in the database
					 productManager.updateProductStock(product, currentStock+increaseStockBy, true);
					 Output.println("Do you want to re-stock another product with low stock? Introduce 1 for YES and 0 for NO");
					 int choice = InputKB.readInteger();
					 switch(choice) {
					 case 1: 
						 break;
					 case 2:
						 keepRestocking = false;
						 manageProductsStock();
						 break;
			         default:
			            Output.println("The number introduced is invalid. Please try again introducing a number that corresponds with one operation of the shown below: ");
			            break;						
			         }
				 }
			 }
		 }
	 }
	 
	 
	 
}
