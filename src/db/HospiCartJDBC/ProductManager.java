package db.HospiCartJDBC;

import db.jdbcInterfaces.IProductManager;
import db.pojos.Category;
import db.pojos.Manufacturer;
import db.pojos.Product;
import db.pojos.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Utilities.Utilities;

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

	// Threshold limits per category, used for stock control
	public static final int LIMIT_MEDICATION = 20;
	public static final int LIMIT_MEDICAL_EQUIPMENT = 10;
	public static final int LIMIT_SURGICAL_SUPPLIES = 15;
	public static final int LIMIT_DIAGNOSTIC_TOOLS = 10;
	public static final int LIMIT_DISPOSABLES = 50;
	public static final int LIMIT_PERSONAL_CARE = 30;
	public static final int LIMIT_LAB_SUPPLIES = 10;
	public static final int LIMIT_IMAGING = 3;
	public static final int LIMIT_OFFICE_SUPPLIES = 100;

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
	 * The CSV file should have the following columns: product_id, supplier_id,
	 * name, category, description, price, stock_quantity, and need_prescription.
	 * 
	 * @param filePath The path to the CSV file containing the product data.
	 */
	public void insertProductsFromCSV(String filePath) {

		String line;
		String csvSplitBy = ",";

		String sql = "INSERT INTO product (supplier_id, name, category, description, price, stock_quantity, need_prescription) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = c.prepareStatement(sql);
				BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			// Saltar la cabecera
			br.readLine();

			while ((line = br.readLine()) != null) {
				String[] data = line.split(csvSplitBy);

				stmt.setInt(1, Integer.parseInt(data[0])); // supplier_id
				stmt.setString(2, data[1]); // name
				stmt.setString(3, Category.valueOf(data[2].toUpperCase()).name()); // category
				stmt.setString(4, data[3]); // description
				stmt.setInt(5, Integer.parseInt(data[4])); // price
				stmt.setInt(6, Integer.parseInt(data[5])); // stock_quantity
				stmt.setBoolean(7, Boolean.parseBoolean(data[6])); // need_prescription

				stmt.executeUpdate();
			}

			c.commit();
			System.out.println("Products inserted correctly from the CSV.");

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves a product by its ID, including supplier information (company name).
	 *
	 * @param id The ID of the product to retrieve.
	 * @return the Product if found.
	 */
	@Override
	public Product getProductById(int id) {
		String sql = "SELECT p.product_id, p.name AS product_name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription, s.supplier_id, s.company_name "
				+ "FROM product p " + "JOIN supplier s ON p.supplier_id = s.supplier_id " + "WHERE p.product_id = ?";

		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Product product = new Product();

				// Obtener los datos del producto
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("product_name"));
				product.setCategory(Category.valueOf(rs.getString("category").toUpperCase()));
				product.setDescription(rs.getString("description"));
				product.setPrice(Utilities.truncateBigDecimal(rs.getBigDecimal("price"), 2));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));

				// Crear el objeto Supplier y asignarlo al producto
				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase()));

				product.setSupplier(supplier); // Asociar el Supplier al Product

				return product;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves all products from the database.
	 * 
	 * @return List of all products.
	 */
	@Override
	public List<Product> getAllProducts() {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT p.product_id, p.name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription, s.supplier_id, s.company_name "
				+ "FROM product p " + "JOIN supplier s ON p.supplier_id = s.supplier_id";
		try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Product product = new Product();
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
				product.setCategory(Category.valueOf(rs.getString("category").toUpperCase())); // Convertir a enum
																								// Category
				product.setDescription(rs.getString("description"));
				product.setPrice(Utilities.truncateBigDecimal(rs.getBigDecimal("price"), 2));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));

				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase())); // Convertir
																											// a enum
																											// Manufacturer
				product.setSupplier(supplier);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	/**
	 * Retrieves products by category.
	 * 
	 * @param category
	 * @return List of products of the specified category.
	 */
	@Override
	public List<Product> getProductsByCategory(Category category) {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT p.product_id, p.name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription, s.supplier_id, s.company_name "
				+ "FROM product p " + "JOIN supplier s ON p.supplier_id = s.supplier_id " + "WHERE p.category = ?";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setString(1, category.name()); // Convierte el enum a String (su nombre)
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
				product.setCategory(Category.valueOf(rs.getString("category").toUpperCase()));
				product.setDescription(rs.getString("description"));
				product.setPrice(Utilities.truncateBigDecimal(rs.getBigDecimal("price"), 2));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));

				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase()));
				product.setSupplier(supplier);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	/**
	 * Retrieves products by manufacturer.
	 * 
	 * @param manufacturer The name of the manufacturer.
	 * @return List of products from the specified manufacturer.
	 */
	@Override
	public List<Product> getProductsByManufacturer(Manufacturer manufacturer) {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT * FROM product p JOIN supplier s ON p.supplier_id = s.supplier_id WHERE s.company_name = ?";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setString(1, manufacturer.name()); // paso a String para buscar en la db
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
				product.setCategory(Category.valueOf(rs.getString("category").toUpperCase()));
				product.setDescription(rs.getString("description"));
				product.setPrice(Utilities.truncateBigDecimal(rs.getBigDecimal("price"), 2));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));

				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase()));
				product.setSupplier(supplier);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	/**
	 * Searches for products by name.
	 * 
	 * @param name The name to search for.
	 * @return List of products that match the name.
	 */
	@Override
	public List<Product> searchProductsByName(String name) {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT p.product_id, p.name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription, "
				+ "s.supplier_id, s.company_name " + "FROM product p "
				+ "JOIN supplier s ON p.supplier_id = s.supplier_id " + "WHERE p.name LIKE ?";

		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setString(1, "%" + name + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
				product.setCategory(Category.valueOf(rs.getString("category").toUpperCase()));
				product.setDescription(rs.getString("description"));
				product.setPrice(Utilities.truncateBigDecimal(rs.getBigDecimal("price"), 2));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));

				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase()));
				product.setSupplier(supplier);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	/**
	 * Updates an existing product in the database.
	 * 
	 * @param product The product to be updated.
	 * @return true if the product was updated, false otherwise.
	 */
	@Override
	public boolean updateProduct(Product product) {
		String sql = "UPDATE product SET name = ?, category = ?, description = ?, price = ?, stock_quantity = ?, need_prescription = ? WHERE product_id = ?";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setString(1, product.getName());
			stmt.setString(2, product.getCategory().name()); // Convertir el enum Category a String
			stmt.setString(3, product.getDescription());
			stmt.setBigDecimal(4, Utilities.truncateBigDecimal(product.getPrice(), 2));
			stmt.setInt(5, product.getStockQuantity());
			stmt.setBoolean(6, product.getNeedPrescription());
			stmt.setInt(7, product.getProductId());

			int rowsAffected = stmt.executeUpdate();
			if (!c.getAutoCommit()) {
				c.commit();
			}
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Reduces the stock of a product by a given quantity after a purchase. If the
	 * stock goes below the defined threshold, a low stock warning is triggered.
	 *
	 * @param productId the ID of the product
	 * @param quantity  the quantity to reduce
	 * @return true if the stock was successfully updated, false otherwise
	 * @throws SQLException
	 */
	public boolean reduceStock(int productId, int quantity) throws SQLException {
		Product product = getProductById(productId);
		System.out.println("product" + product);

		if (product == null) {
			System.out.println("Product not found.");
			return false;
		}

		if (product.getStockQuantity() < quantity) {
			System.out.println("Insufficient stock for the requested purchase.");
			return false;
		}

		int upDateStock = product.getStockQuantity() - quantity;
		System.out.println("upDateStock" + upDateStock);
		product.setStockQuantity(upDateStock);

		if (updateProductStockInDB(productId, upDateStock)) {
			c.commit();
			checkLowStockAlert(product);
			return true;
		}

		return false;
	}

	/**
	 * Increases the stock quantity of a given product by the specified amount.
	 *
	 * @param productId     ID of the product whose stock will be increased
	 * @param quantityToAdd Amount of stock to add
	 * @return true if the stock was successfully updated and committed; false
	 *         otherwise
	 */
	public boolean increaseStock(int productId, int quantityToAdd) {
		Product product = getProductById(productId);

		if (product == null) {
			System.out.println("Product not found.");
			return false;
		}

		int updatedStock = product.getStockQuantity() + quantityToAdd;
		product.setStockQuantity(updatedStock);

		try {
			if (updateProductStockInDB(productId, updatedStock)) {
				c.commit(); // Solo commit
				System.out.println("Stock increased successfully.");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Updates the stock of a product in the database.
	 *
	 * @param productId the ID of the product
	 * @param newStock  the new stock quantity to set
	 * @return true if the update was successful, false otherwise
	 */
	@Override
	public boolean updateProductStockInDB(int productId, int newStock) {
		String sql = "UPDATE product SET stock_quantity = ? WHERE product_id = ?";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setInt(1, newStock);
			stmt.setInt(2, productId);
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks whether the stock of a product is below its threshold. If so, prints a
	 * warning message.
	 *
	 * @param product the product to check
	 */
	public void checkLowStockAlert(Product product) {
		int threshold = getThresholdForCategory(product.getCategory());

		if (product.getStockQuantity() < threshold) {
			System.out.println("Warning: Product \"" + product.getName() + "\" is running low on stock.");
		}
	}

	/**
	 * Returns the stock threshold for a given product category.
	 *
	 * @param category the product category
	 * @return the threshold value associated with that category
	 */
	public int getThresholdForCategory(Category category) {
		switch (category) {
		case MEDICATIONS:
			return LIMIT_MEDICATION;
		case MEDICAL_EQUIPMENT:
			return LIMIT_MEDICAL_EQUIPMENT;
		case SURGICAL_SUPPLIES:
			return LIMIT_SURGICAL_SUPPLIES;
		case DIAGNOSTIC_TOOLS:
			return LIMIT_DIAGNOSTIC_TOOLS;
		case DISPOSABLES:
			return LIMIT_DISPOSABLES;
		case PERSONAL_CARE:
			return LIMIT_PERSONAL_CARE;
		case LAB_SUPPLIES:
			return LIMIT_LAB_SUPPLIES;
		case IMAGING:
			return LIMIT_IMAGING;
		case OFFICE_SUPPLIES:
			return LIMIT_OFFICE_SUPPLIES;
		default:
			return 10;
		}
	}

	/**
	 * Retrieves all products of a specific category that are below the stock
	 * threshold.
	 *
	 * @param category the category to filter by
	 * @return a list of products in that category with stock below the threshold
	 */
	@Override
	public List<Product> getLowStockProductsByCategory(Category category) {
		int threshold = getThresholdForCategory(category);
		List<Product> products = new ArrayList<>();
		String sql = "SELECT p.product_id, p.name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription, "
				+ "s.supplier_id, s.company_name " + "FROM product p "
				+ "JOIN supplier s ON p.supplier_id = s.supplier_id " + "WHERE p.stock_quantity < ? AND p.category = ?";

		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setInt(1, threshold);
			stmt.setString(2, category.name()); // Enum name used as string
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				// Creating a Product object
				Product product = new Product();
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
				product.setCategory(Category.valueOf(rs.getString("category")));
				product.setDescription(rs.getString("description"));
				product.setPrice(Utilities.truncateBigDecimal(rs.getBigDecimal("price"), 2));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));

				// Creating and setting Supplier object
				Supplier supplier = new Supplier();
				supplier.setSupplierId(rs.getInt("supplier_id"));
				supplier.setCompanyName(Manufacturer.valueOf(rs.getString("company_name").toUpperCase())); // Convert
																											// to
																											// enum
				product.setSupplier(supplier);

				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return products;
	}

	/**
	 * Deletes a product from the database.
	 * 
	 * @param id The ID of the product to be deleted.
	 * @return true if the product was deleted, false otherwise.
	 */
	@Override
	public boolean deleteProduct(int id) {
		String sql = "DELETE FROM product WHERE product_id = ?";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setInt(1, id);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				c.commit(); 
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
