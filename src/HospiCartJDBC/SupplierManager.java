package HospiCartJDBC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import HospiCartPOJOs.Product;
import HospiCartPOJOs.Supplier;

/**
 * The SupplierManager class provides methods for interacting with the
 * supplier-related data in the database. It allows for inserting supplier
 * information from a CSV file and displaying the list of suppliers.
 */

public class SupplierManager {

	private Connection c;
	private ConnectionManagerJDBC cm;
	private ProductManager productManager;

	/**
	 * Constructor that initializes the SupplierManager with the given
	 * ConnectionManager.
	 * 
	 * @param cm The ConnectionManager that will be used to obtain the database
	 *           connection.
	 */
	public SupplierManager(ConnectionManagerJDBC cm) {

		this.cm = cm;
		this.c = cm.getConnection();
		this.productManager = new ProductManager(cm);
	}

	/**
	 * Inserts supplier data into the database from a CSV file. Each row in the CSV
	 * should contain the company name, contact person, and address of the supplier.
	 * The file should not have a header.
	 * 
	 * @param filePath The path to the CSV file containing supplier data.
	 */

//	public void insertSuppliersFromCSV(String filePath) {
//		String line;
//		String csvSplitBy = ",";
//
//		String sql = "INSERT INTO supplier (company_name, contact_person, address) VALUES (?, ?, ?)";
//
//		try (PreparedStatement stmt = c.prepareStatement(sql);
//				BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//
//			// Saltar la cabecera
//			br.readLine();
//
//			while ((line = br.readLine()) != null) {
//				String[] data = line.split(csvSplitBy);
//
//				if (data.length < 3) {
//					System.out.println("Skipping invalid row: " + line);
//					continue;
//				}
//
//				String companyName = data[0].trim(); // .trim(): elimina espacios en blanco
//				String contactPerson = data[1].trim();
//				String address = data[2].trim();
//
//				stmt.setString(1, companyName);
//				stmt.setString(2, contactPerson);
//				stmt.setString(3, address);
//
//				stmt.executeUpdate();
//			}
//
//			c.commit();
//			System.out.println("Suppliers inserted correctly from the CSV.");
//
//		} catch (IOException | SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
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
	public void showCompanyNames() {
		String sql = "SELECT supplier_id, company_name FROM supplier";
		try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			System.out.println("=== Suppliers List ===");
			System.out.printf("%-10s %-30s%n", "ID", "Nombre de la empresa");
			while (rs.next()) {
				int id = rs.getInt("supplier_id");
				String name = rs.getString("company_name");
				System.out.printf("%-10d %-30s%n", id, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
