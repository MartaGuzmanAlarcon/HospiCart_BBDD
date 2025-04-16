package db.HospiCartJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManagerJDBC {
	private Connection c = null;

	private ClientManager clientMan;
	private OrderManager orderMan;
	private ProductManager productMan;
	private ProductOrderManager productOrderMan;
	private ShipmentManager shipmentMan;
	private SupplierManager supplierMan;
	private PaymentManager paymentMan;

	public ConnectionManagerJDBC() {
		try {
			connect();
			createTables();
			initManagers();
		} catch (SQLException e) {
			System.err.println("Error initializing ConnectionManagerJDBC: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/HospiCartDB.db");
			c.setAutoCommit(false); // ← desactiva el auto-commit
			c.createStatement().execute("PRAGMA foreign_keys=ON");
			System.out.println("Connection established.");
		} catch (ClassNotFoundException cnfE) {
			System.out.println("Error: The database libraries were not loaded.");
			cnfE.printStackTrace();
		} catch (SQLException sqlE) {
			System.out.println("Error with the database.");
			sqlE.printStackTrace();
		}
	}

	private void createTables() throws SQLException {
		String[] tableStatements = {
				// client
				"""
						CREATE TABLE IF NOT EXISTS client (
							user_id INTEGER PRIMARY KEY AUTOINCREMENT,
							name TEXT NOT NULL,
							email TEXT NOT NULL,
							role TEXT NOT NULL,
							phoneNumber INTEGER,
							address TEXT NOT NULL
						);
						""",
				// client_order
				"""
						CREATE TABLE IF NOT EXISTS client_order (
							order_id INTEGER PRIMARY KEY AUTOINCREMENT,
							user_id INTEGER NOT NULL,
							order_date DATE NOT NULL,
							status TEXT NOT NULL
						);
						""",
				// supplier
				"""
						CREATE TABLE IF NOT EXISTS supplier (
							supplier_id INTEGER PRIMARY KEY AUTOINCREMENT,
							company_name TEXT NOT NULL,
							contact_person INTEGER NOT NULL,
							address TEXT NOT NULL
						);
						""",
				// product
				"""
						CREATE TABLE IF NOT EXISTS product (
							product_id INTEGER PRIMARY KEY AUTOINCREMENT,
							supplier_id INTEGER NOT NULL REFERENCES supplier(supplier_id),
							name TEXT NOT NULL,
							description TEXT,
							category TEXT,
							price REAL,
							stock_quantity INTEGER,
							need_prescription TEXT
						);
						""",
				// product_order
				"""
						CREATE TABLE IF NOT EXISTS product_order (
							order_id INTEGER REFERENCES client_order(order_id) ON DELETE SET NULL,
							product_id INTEGER REFERENCES product(product_id) ON DELETE SET NULL,
							amount INTEGER NOT NULL,
							total_price INTEGER NOT NULL,
							PRIMARY KEY (order_id, product_id)
						);
						""",
				// shipment
				"""
						CREATE TABLE IF NOT EXISTS shipment (
							shipment_id INTEGER PRIMARY KEY AUTOINCREMENT,
							order_id INTEGER NOT NULL,
							tracking_number INTEGER NOT NULL,
							FOREIGN KEY (order_id) REFERENCES product_order(order_id)
						);
						""",
				// payment
				"""
						CREATE TABLE IF NOT EXISTS payment (
							payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
							order_id INTEGER REFERENCES client_order(order_id) ON DELETE SET NULL,
							amount INTEGER NOT NULL,
							payment_method TEXT,
							payment_status TEXT
						);
						""" };

		try (Statement stmt = c.createStatement()) {
			for (String sql : tableStatements) {
				stmt.executeUpdate(sql);
			}
			c.commit();
			System.out.println("Tables created correctly.");
		} catch (SQLException e) {
			c.rollback();
			System.err.println("Error creating tables: " + e.getMessage());
			throw e;
		}
	}

	private void initManagers() {
		// this.clientMan = new ClientManager(this);
		// this.orderMan = new OrderManager(this);
		this.productMan = new ProductManager(this);
		this.productOrderMan = new ProductOrderManager(this);
		// this.shipmentMan = new ShipmentManager(this);
		// this.supplierMan = new SupplierManager(this);
		// this.paymentMan = new PaymentManager(this);
		System.out.println("Managers initialized correctly.");
	}

	public Connection getConnection() {
		return c;
	}

	public ClientManager getClientManager() {
		return clientMan;
	}

	public OrderManager getOrderManager() {
		return orderMan;
	}

	public ProductManager getProductManager() {
		return productMan;
	}

	public ProductOrderManager getProductOrderManager() {
		return productOrderMan;
	}

	public ShipmentManager getShipmentManager() {
		return shipmentMan;
	}

	public SupplierManager getSupplierManager() {
		return supplierMan;
	}

	public PaymentManager getPaymentManager() {
		return paymentMan;
	}

	public void disconnect() {
		try {
			if (c != null && !c.isClosed()) {
				c.close();
				System.out.println("Connection closed correctly.");
			}
		} catch (SQLException e) {
			System.err.println("Error closing the connection: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
