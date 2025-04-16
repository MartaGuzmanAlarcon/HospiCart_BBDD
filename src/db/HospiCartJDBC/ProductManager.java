package db.HospiCartJDBC;

import db.jdbcInterfaces.IProductManager;
import db.pojos.Product;
import db.pojos.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that manages the operations related to products in the database.
 * Implements the IProductManager interface.
 */

public class ProductManager implements IProductManager {

	private Connection c;
	private ConnectionManagerJDBC cm;

	/**
	 * Constructor that initializes the ProductManager with the given
	 * ConnectionManager.
	 * 
	 * @param cm The ConnectionManager that will be used to obtain the database
	 *           connection.
	 */
	public ProductManager(ConnectionManagerJDBC cm) {

		this.cm = cm; 
		this.c = cm.getConnection();
	}

	 /**
     * Inserts products from a CSV file into the 'product' table in the database.
     * The CSV file should have the following columns: product_id, supplier_id, name, category, description, price, stock_quantity, and need_prescription.
     * @param filePath The path to the CSV file containing the product data.
     */
	public void insertProductsFromCSV(String filePath) {

	    String line;
	    String csvSplitBy = ",";

	    // SQL para insertar productos, omitiendo product_id y supplier_id
	    String sql = "INSERT INTO product (name, category, description, price, stock_quantity, need_prescription) VALUES (?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement stmt = c.prepareStatement(sql);
	            BufferedReader br = new BufferedReader(new FileReader(filePath))) {

	        // Saltar la cabecera
	        br.readLine();

	        // Leer cada línea del archivo CSV
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(csvSplitBy);

	            // Asignar los valores a la consulta SQL
	            stmt.setString(1, data[0]);  // name
	            stmt.setString(2, data[1]);  // category
	            stmt.setString(3, data[2]);  // description
	            stmt.setInt(4, Integer.parseInt(data[3]));  // price
	            stmt.setInt(5, Integer.parseInt(data[4]));  // stock_quantity
	            stmt.setBoolean(6, Boolean.parseBoolean(data[5]));  // need_prescription

	            // Ejecutar la inserción
	            stmt.executeUpdate();
	        }

	        // Realizar commit para confirmar los cambios
	        c.commit();
	        System.out.println("Products inserted correctly from the CSV.");

	    } catch (IOException | SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public Product getProductById(int id) {
	    String sql = "SELECT p.product_id, p.name AS product_name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription, s.supplier_id, s.company_name "
	               + "FROM product p "
	               + "JOIN supplier s ON p.supplier_id = s.supplier_id "
	               + "WHERE p.product_id = ?";

	    try (PreparedStatement stmt = c.prepareStatement(sql)) {
	        stmt.setInt(1, id);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            Product product = new Product();

	            // Obtener los datos del producto
	            product.setProductId(rs.getInt("product_id"));
	            product.setName(rs.getString("product_name"));
	            product.setCategory(rs.getString("category"));
	            product.setDescription(rs.getString("description"));
	            product.setPrice(rs.getInt("price"));
	            product.setStockQuantity(rs.getInt("stock_quantity"));
	            product.setNeedPrescription(rs.getBoolean("need_prescription"));

	            // Crear el objeto Supplier y asignarlo al producto
	            Supplier supplier = new Supplier();
	            supplier.setSupplierId(rs.getInt("supplier_id"));
	            supplier.setCompanyName(rs.getString("company_name"));

	            product.setSupplier(supplier); // Asociar el Supplier al Product

	            return product;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}
