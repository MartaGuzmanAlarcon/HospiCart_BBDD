package db.HospiCartJDBC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.*;

/**
 * The SupplierManager class provides methods for interacting with the
 * supplier-related data in the database. It allows for inserting supplier
 * information from a CSV file and displaying the list of suppliers.
 */

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

	/**
	 * Inserts supplier data into the database from a CSV file. Each row in the CSV
	 * should contain the company name, contact person, and address of the supplier.
	 * The file should not have a header.
	 * 
	 * @param filePath The path to the CSV file containing supplier data.
	 */

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
