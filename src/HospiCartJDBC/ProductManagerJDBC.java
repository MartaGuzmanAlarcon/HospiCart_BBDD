package HospiCartJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import HospiCartInterfaces.IProductManager;
import HospiCartPOJOs.Category;
import HospiCartPOJOs.Manufacturer;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.Supplier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that manages the operations related to products in the database.
 * Implements the IProductManager interface.
 */

public class ProductManagerJDBC implements IProductManager {

	private Connection c;

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
	public ProductManagerJDBC(ConnectionManagerJDBC cm) {
		this.c = cm.getConnection();
	}

    /**
     * Method that checks if the table products already exists and has information in it. 
     * If it does not exist, it calls the method that inserts the products from the CSV file.
     */
    public void insertProductsIfNotExists(Product product) throws SQLException {
    	//I check if the product has already been inserted or not by looking at its product id.
    	if(product.getProductId() == null) {
        	insertProduct(product);
    	}
    }
	
	/**
	 * Inserts products from a CSV file into the 'product' table in the database.
	 * The CSV file should have the following columns: product_id, supplier_id,
	 * name, category, description, price, stock_quantity, and need_prescription.
	 * 
	 * @param filePath The path to the CSV file containing the product data.
	 * @param suppliers list that contains objects of supplier
	 */
	public void insertProductsFromCSV(String filePath, List<Supplier> suppliers){
		String line;
		String csvSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			br.readLine();
			List<Product> products = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				String[] data = line.split(csvSplitBy);

				if (data.length != 7) {
					System.out.println("Skipping invalid row: " + line);
					continue;
				}
				int supplier_id = Integer.parseInt(data[0].trim()); // supplier_id
				String product_name = data[1].trim(); // name
				Category category = Category.valueOf(data[2].trim().toUpperCase()); // category
				String description = data[3].trim(); // description
				Float price = Float.parseFloat(data[4].trim()); // price
				int stockQuantity = Integer.parseInt(data[5].trim()); // stock_quantity
				boolean need_prescription = Boolean.parseBoolean(data[6].trim()); // need_prescription

				Product product = new Product(product_name, category, description, price, stockQuantity, need_prescription);

				for(int i=0; i<suppliers.size(); i++) {
					if(supplier_id == suppliers.get(i).getSupplierId()) {
						product.setSupplier(suppliers.get(i));
						break;
					}
				}
				
				products.add(product);
			}
	        for (Product product : products) {
	        	insertProductsIfNotExists(product);
	        }
	        //TODO REMOVE SQLException
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a new product to the database.
	 * 
	 * @param supplierId The supplier's ID for the product.
	 * @param product    The product to be added.
	 * @return true if product was added, false otherwise.
	 */
	@Override
	public boolean insertProduct(Product product) throws SQLException{
		Supplier supplier = product.getSupplier();
		
		String sql = "INSERT INTO product (supplier_id, name, category, description, price, stock_quantity, need_prescription) VALUES (?, ?, ?, ?, ?, ?, ?) ";

		try (PreparedStatement stmt = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			
			// Set parameters for the SQL query
			stmt.setInt(1, supplier.getSupplierId()); // Using the supplierId.
			stmt.setString(2, product.getName());
			stmt.setString(3, product.getCategory().toString()); // Assuming product.getCategory() returns an Enum
			stmt.setString(4, product.getDescription());
			stmt.setFloat(5, product.getPrice()); // Assuming price comes from the																		// Product object
			stmt.setInt(6, product.getStockQuantity());
			stmt.setBoolean(7, product.getNeedPrescription());

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				//Now, I get the generated primary key of order (the order id, which is assigned by the database), making use of the method "getGeneratedKeys"
	            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    product.setProductId(generatedKeys.getInt(1));
	                } else {
	                    throw new SQLException("Inserting product failed, no ID obtained.");
	                }
				c.commit();
				return true;
	            }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
				product.setPrice(rs.getFloat("price"));
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
				product.setPrice(rs.getFloat("price"));
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
				product.setPrice(rs.getFloat("price"));
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
				product.setPrice(rs.getFloat("price"));
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
	public List<Product> getProductsByName(String name) {
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
				product.setPrice(rs.getFloat("price"));
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
	
	@Override
	public List<Product> getProductsBySupplier(int supplierId) {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT p.product_id, p.name, p.category, p.description, p.price, p.stock_quantity, p.need_prescription " + "FROM product p "
			    + "WHERE p.supplier_id = ? ";

		try (PreparedStatement stmt = c.prepareStatement(sql)) {
    		stmt.setInt(1, supplierId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setProductId(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
				product.setCategory(Category.valueOf(rs.getString("category").toUpperCase()));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getFloat("price"));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				product.setNeedPrescription(rs.getBoolean("need_prescription"));
				
				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
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
				product.setPrice(rs.getFloat("price"));
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
			stmt.setString(2, product.getCategory().name());
			stmt.setString(3, product.getDescription());
			stmt.setFloat(4, product.getPrice());
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
	 * Updates the stock of a product in the database.
	 *
	 * @param productId the ID of the product
	 * @param newStock  the new stock quantity to set
	 * @return true if the update was successful, false otherwise
	 */
	@Override
	public void updateProductStock(Product product, int amount, boolean increasing) {
		int newStock = 0;
		//If the variable increasing is true it is because I have to increase the stock quantity of the product. This can happen when a product order is deleted.
		if(increasing) {
			newStock = product.getStockQuantity() + amount;
		} else { //If increasing is false, it is because we have inserted a new product order in the database and we have to decrease the stock of the product.
			newStock = product.getStockQuantity() - amount;
		}
		
		String sql = "UPDATE product SET stock_quantity = ? WHERE product_id = ?";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
			stmt.setInt(1, newStock);
			stmt.setInt(2, product.getProductId());
			product.setStockQuantity(newStock);
			stmt.executeUpdate();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Checks whether the stock of a product is below its threshold and adds it to a list that contains the products that have the products with low stock.
	 *
	 * @param product the product to check
	 * @return true if the product is running out of stock or false otherwise.
	 */
	public boolean checkLowStockAlert(Product product) {
		int threshold = getThresholdForCategory(product.getCategory());

		if (product.getStockQuantity() < threshold) {
			System.out.println("Warning: Product \"" + product.getName() + "\" with ID " + product.getProductId()+" is running low on stock.");
			return true;
		}
		return false;
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
}
