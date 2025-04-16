package db.HospiCartJDBC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.*;

public class SupplierManager {
	

	private Connection c;
	private ConnectionManagerJDBC cm;

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
	}

	
	public void insertSuppliersFromCSV(String filePath) {
	    String line;
	    String csvSplitBy = ",";

	    String sql = "INSERT INTO supplier (company_name, contact_person, address) VALUES (?, ?, ?)";

	    try (PreparedStatement stmt = c.prepareStatement(sql);
	         BufferedReader br = new BufferedReader(new FileReader(filePath))) {

	        // Saltar la cabecera
	        br.readLine();

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(csvSplitBy);

	            if (data.length < 3) {
	                System.out.println("Skipping invalid row: " + line);
	                continue;
	            }

	            String companyName = data[0].trim(); // .trim(): elimina espacios en blanco
	            String contactPerson = data[1].trim();
	            String address = data[2].trim();

	            stmt.setString(1, companyName);
	            stmt.setString(2, contactPerson);
	            stmt.setString(3, address);

	            stmt.executeUpdate();
	        }

	        c.commit();
	        System.out.println("Suppliers inserted correctly from the CSV.");

	    } catch (IOException | SQLException e) {
	        e.printStackTrace();
	    }
	}

}
