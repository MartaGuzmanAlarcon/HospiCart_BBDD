package HospiCartJDBC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Exceptions.OrderExceptions;
import HospiCartInterfaces.ISupplierManager;
import HospiCartPOJOs.Manufacturer;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.Supplier;

/**
 * The SupplierManager class provides methods for interacting with the
 * supplier-related data in the database. It allows for inserting supplier
 * information from a CSV file and displaying the list of suppliers.
 */

public class SupplierManagerJDBC implements ISupplierManager{

	private Connection c;
	private ProductManagerJDBC productManager;

	/**
	 * Constructor that initializes the SupplierManager with the given
	 * ConnectionManager.
	 * 
	 * @param cm The ConnectionManager that will be used to obtain the database
	 *           connection.
	 */
	public SupplierManagerJDBC(ConnectionManagerJDBC cm) {
		this.c = cm.getConnection();
		this.productManager = new ProductManagerJDBC(cm);
	}
	
    /**
     * Method that checks if the table supplier already exists and has information in it. 
     * If it does not exist, it calls the method that inserts the suppliers from the CSV file.
     */
    public void insertSupplierIfNotExists(Supplier supplier) throws SQLException {

//    	if(supplier.getSupplierId() == null) {
//    		insertSupplier(supplier);
//    	}
    	// I check if the supplier table already exists
        String checkQuery = "SELECT COUNT(*) FROM supplier WHERE supplier_id = ?";
        try (PreparedStatement stmt = c.prepareStatement(checkQuery)) {
            stmt.setInt(1, supplier.getSupplierId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                
                if (rs.getInt(1) == 0) {
                    // Only insert if supplier doesn't exist
                	insertSupplier(supplier);
                }
            }
        }
    }

	/**
	 * Inserts supplier data into the database from a CSV file. Each row in the CSV
	 * should contain the company name, contact number, and address of the supplier.
	 * The file should not have a header.
	 * 
	 * @param filePath The path to the CSV file containing supplier data.
	 */
	@Override
	public void insertSuppliersFromCSV(String filePath) {
		String line;
		String csvSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			br.readLine();
			List<Supplier> suppliers = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				String[] data = line.split(csvSplitBy);

				if (data.length < 4) {
					System.out.println("Skipping invalid row: " + line);
					continue;
				}
				Integer supplierId = Integer.parseInt(data[0].trim());
				Manufacturer companyName = Manufacturer.valueOf(data[1].trim());
				Integer contactNumber = Integer.parseInt(data[2].trim());
				String address = data[3].trim();
				
				Supplier supplier = new Supplier(supplierId, companyName, contactNumber, address);
				suppliers.add(supplier);

			}
	        for (Supplier supplier : suppliers) {
	            insertSupplierIfNotExists(supplier);
	        }
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that inserts the supplier received as parameter into the database.
	 * @param filePath The path to the CSV file containing supplier data.
	 */
	@Override
	public void insertSupplier(Supplier supplier) throws SQLException {
       
       //I insert the order information that I have up to now
       String sql = "INSERT INTO supplier (company_name, contact_number, address) VALUES (?, ?, ?) ";

       //I create the shipment record and fetch the generated keys (the id of the shipment)
        try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getCompanyName().name());
            stmt.setInt(2, supplier.getContactNumber());
            stmt.setString(3, supplier.getAddress());


            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("Creating supplier failed, no rows affected.");
            }

            //Now, I get the generated shipment id
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setSupplierId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating supplier failed, no ID obtained.");
                }
            }
            //I obtain the list of products associated to the supplier and check if the products have been inserted into the database.
            List<Product> products = supplier.getProducts();
            //TODO BEFORE DOING THIS I SHOULD USE THE METHOD THAT RETRIEVES ALL THE PRODUCTS AND ASSIGN THEM TO THE SUPPLIER
            if(products == null || products.isEmpty()) {
            	products = productManager.getProductsBySupplier(supplier.getSupplierId());
            }
            for(int i=0; i<products.size(); i++) {
	           	//If the ID of the products is null, it is because they have not been inserted into the database.
	          	if(products.get(i).getProductId() == null) {
	          		//Therefore, I set the inserted supplier as the supplier of the product and I insert the product in the database.
	           		products.get(i).setSupplier(supplier);
	           		productManager.insertProduct(products.get(i));
	           	}
	        }
            c.commit(); //we do this because we disabled the auto-commit in the connection
        } catch (SQLException e) {
            //We "rollback" the transaction in case of error.
            if(c != null){ //We make sure that c is not null as an error would be thrown when trying to roll back over a null object
                try{
                    c.rollback();
                } catch(SQLException ex){
                    throw new SQLException("Error during rollback: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Error creating supplier: " + e.getMessage(), e);
        }
	}

	/**
	 * Displays the list of suppliers with their IDs and company names. 
	 */
	@Override
	public void showCompanyNames() {
		String sql = "SELECT supplier_id, company_name FROM supplier";
		try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			System.out.println("=== Suppliers List ===");
			System.out.printf("%-10s %-30s%n", "ID", "Name of the company");
			while (rs.next()) {
				int id = rs.getInt("supplier_id");
				String name = rs.getString("company_name");
				System.out.printf("%-10d %-30s%n", id, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<Supplier> getAllSuppliers() {
		List<Supplier> suppliers = new ArrayList<>();
		String sql = "SELECT * "
				+ "FROM supplier ";
		try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase())); // Convertir
				supplier.setContactNumber(rs.getInt("contact_number"));
				supplier.setAddress(rs.getString("address"));

				suppliers.add(supplier);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return suppliers;
	}
	
	/**
	 * Method that returns an array list that contains the names of the companies that supply the application.
	 * @return
	 */
	@Override
	public List<String> getCompanyNames(){
		ArrayList<String> companyNames = new ArrayList<>();
		String sql = "SELECT company_name FROM supplier";
		try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				String name = rs.getString("company_name");
				companyNames.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return companyNames;
	}
	
	   /**
		 * Method that retrieves the supplier whose ID coincides with the received as parameter.
		 * @param supplier_id integer that stores the id of the supplier company.
		 * @return the retrieved supplier.
		 */
	    @Override
	    public Supplier getSupplierByID(int supplier_id) throws SQLException{
	    	Supplier supplier = null;
	    	
	    	String sql = "SELECT * "
	    			+ "FROM supplier "
	    			+ "WHERE supplier_id = ? ";
	    	
	    	try(PreparedStatement stmt = c.prepareStatement(sql)){
	    		stmt.setInt(1, supplier_id);
	    		try(ResultSet resultSet = stmt.executeQuery()){
	                //While loop that iterates through all the result set and retrieves all the orders.
	                while(resultSet.next()) {	                	
	    				supplier = new Supplier();
	    				//I create a variable called order id and store the id of the order in it.
	    				int supplierID = resultSet.getInt("supplier_id");
	    				//I set the fields of the order object.
	    				supplier.setSupplierId(supplierID);
	    				supplier.setContactNumber(resultSet.getInt("contact_number"));
	    				supplier.setCompanyName(Manufacturer.valueOf(resultSet.getString("company_name")));
	    				supplier.setAddress(resultSet.getString("address"));   
	    			}
	    		}
	    	} catch(SQLException e) {
	    		System.err.println("Error retrieving supplier from supplier ID: " + e.getMessage());
	            e.printStackTrace();
	    	} catch(OrderExceptions oe) {
	    		System.out.println("ERROR: " + oe);
	    	}
	        return supplier;
	    }
	
	   /**
		 * Method that retrieves the supplier whose company name coincides with the received as parameter.
		 * @param companyName string that stores the company name of the supplier company.
		 * @return the retrieved supplier.
		 */
	    @Override
	    public Supplier getSupplierByCompanyName(String companyName){
	    	Supplier supplier = null;
	    	
	    	String sql = "SELECT * "
	    			+ "FROM supplier "
	    			+ "WHERE company_name = ? ";
	    	
	    	try(PreparedStatement stmt = c.prepareStatement(sql)){
	    		stmt.setString(1, companyName);
	    		try(ResultSet resultSet = stmt.executeQuery()){
	                //While loop that iterates through all the result set and retrieves all the orders.
	                while(resultSet.next()) {	                	
	    				supplier = new Supplier();
	    				//I create a variable called order id and store the id of the order in it.
	    				int supplierID = resultSet.getInt("supplier_id");
	    				//I set the fields of the order object.
	    				supplier.setSupplierId(supplierID);
	    				supplier.setContactNumber(resultSet.getInt("contact_number"));
	    				supplier.setCompanyName(Manufacturer.valueOf(resultSet.getString("company_name")));
	    				supplier.setAddress(resultSet.getString("address"));   
	    			}
	    		}
	    	} catch(SQLException e) {
	    		System.err.println("Error retrieving supplier from company name: " + e.getMessage());
	            e.printStackTrace();
	    	} catch(OrderExceptions oe) {
	    		System.out.println("ERROR: " + oe);
	    	}
	        return supplier;
	    }
	    
	    /**
		 * Method that receives a supplier id and an address as parameters and updates the address of the supplier whose id coincides with the received as parameter.
		 * @param supplier_id integer that stores the id of the supplier whose contact number we wish to update.
		 * @param address variable of type string that stores the address we want the supplier to have.
	     * @throws SQLException 
		 */
	    @Override
	    public void updateSupplierAddress(int supplier_id, String newAddress) throws SQLException {
	    	Supplier supplier = getSupplierByID(supplier_id);
	    	//The new status' validity is checked in the setter method of status in Order.
	    	supplier.setAddress(newAddress);
	    	
	    	String sql = "UPDATE supplier SET address = ? WHERE supplier_id = ?";
	    	
	    	try(PreparedStatement stmt = c.prepareStatement(sql)){
	    		stmt.setString(1, newAddress);
	    		stmt.setInt(2, supplier_id);
	    		
	    		int rowsUpdated = stmt.executeUpdate();
	    		if (rowsUpdated == 0) {
	                System.out.println("No supplier found with ID: " + supplier_id);
	            } else {
	                System.out.println("Supplier ID " + supplier_id + " updated to address: " + newAddress);
	            }
	    		c.commit();
	    	} catch (SQLException e) {
	            try {
	                c.rollback();  // Roll back in case of error
	            } catch (SQLException rollbackEx) {
	                System.err.println("Rollback failed: " + rollbackEx.getMessage());
	            }
	            throw new RuntimeException("Error updating supplier's address: " + e.getMessage(), e);
	        }
	    }
	    
	    /**
		 * Method that receives a supplier id and a contact number as parameters and updates the contact number of the supplier whose id coincides with the received as parameter.
		 * @param supplier_id integer that stores the id of the supplier whose contact number we wish to update.
		 * @param contactNumber variable of type integer that stores the contact number we want the supplier to have.
	     * @throws SQLException 
		 */
	    @Override
	    public void updateSupplierContactNumber(int supplier_id, int contactNumber) throws SQLException {
	    	Supplier supplier = getSupplierByID(supplier_id);
	    	//The new status' validity is checked in the setter method of status in Order.
	    	supplier.setContactNumber(contactNumber);
	    	
	    	String sql = "UPDATE supplier SET contact_number = ? WHERE supplier_id = ?";
	    	
	    	try(PreparedStatement stmt = c.prepareStatement(sql)){
	    		stmt.setInt(1, contactNumber);
	    		stmt.setInt(2, supplier_id);
	    		
	    		int rowsUpdated = stmt.executeUpdate();
	    		if (rowsUpdated == 0) {
	                System.out.println("No supplier found with ID: " + supplier_id);
	            } else {
	                System.out.println("Supplier ID " + supplier_id + " updated to contact number: " + contactNumber);
	            }
	    		c.commit();
	    	} catch (SQLException e) {
	            try {
	                c.rollback();  // Roll back in case of error
	            } catch (SQLException rollbackEx) {
	                System.err.println("Rollback failed: " + rollbackEx.getMessage());
	            }
	            throw new RuntimeException("Error updating supplier's contact number: " + e.getMessage(), e);
	        }
	    }	    
}
