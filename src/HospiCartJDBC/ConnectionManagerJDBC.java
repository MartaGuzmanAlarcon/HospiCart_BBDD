package HospiCartJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManagerJDBC {
	
	private Connection c = null;

	private ClientManagerJDBC clientMan;
	private OrderManagerJDBC orderMan;
	private ProductManagerJDBC productMan;
	private ProductOrderManagerJDBC productOrderMan;
	private ShipmentManagerJDBC shipmentMan;
	private SupplierManagerJDBC supplierMan;
	private PaymentManagerJDBC paymentMan;

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
			c = DriverManager.getConnection("jdbc:sqlite:./db/HospiCartDB.db");
			c.setAutoCommit(false); // We disable the auto commit
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
						CREATE TABLE client (
							id INTEGER PRIMARY KEY AUTOINCREMENT,
							name TEXT NOT NULL,
							surname TEXT NOT NULL,
							email TEXT NOT NULL,
							phone_number INTEGER,
							address TEXT NOT NULL
						);
						""",
				// client_order: we had to put this name in this table because just “order” was a reserved word in SQLite
				"""
						CREATE TABLE client_order ( 
							order_id INTEGER PRIMARY KEY AUTOINCREMENT,
							user_id INTEGER NOT NULL,
							order_date DATE NOT NULL,
							status TEXT NOT NULL
						);
						""",
				// supplier
				"""
						CREATE TABLE supplier (
							supplier_id INTEGER PRIMARY KEY AUTOINCREMENT,
							company_name TEXT NOT NULL,
							contact_number INTEGER NOT NULL,
							address TEXT NOT NULL
						);
						""",
				// product
				"""
						CREATE TABLE product (
							product_id INTEGER PRIMARY KEY AUTOINCREMENT,
							supplier_id INTEGER NOT NULL REFERENCES supplier(supplier_id),
							name TEXT NOT NULL,
							description TEXT,
							category TEXT,
							price NUMERIC(10,2),
							stock_quantity INTEGER,
							need_prescription TEXT
						);
						""",
				// product_order
				"""
						CREATE TABLE product_order (
							order_id INTEGER REFERENCES client_order(order_id) ON DELETE SET NULL,
							product_id INTEGER REFERENCES product(product_id) ON DELETE SET NULL,
							amount INTEGER NOT NULL,
							total_price NUMERIC(10, 2),
							PRIMARY KEY (order_id, product_id)
						);
						""",
				// shipment
				"""
						CREATE TABLE shipment (
							shipment_id INTEGER PRIMARY KEY AUTOINCREMENT,
							order_id INTEGER NOT NULL,
							tracking_number INTEGER NOT NULL,
							FOREIGN KEY (order_id) REFERENCES product_order(order_id)
						);
						""",
				// payment
				"""
						CREATE TABLE payment (
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
			// Check if the exception is because the tables already exist
			if (e.getMessage().contains("already exist")) {
				return;
			}
			System.out.println("Database error.");
			e.printStackTrace();
		}
	}
	
	//TODO see what we do with this
	private void initManagers() {
		this.clientMan = new ClientManagerJDBC(this);
		this.orderMan = new OrderManagerJDBC(this);
		this.productMan = new ProductManagerJDBC(this);
		this.productOrderMan = new ProductOrderManagerJDBC(this);
		this.shipmentMan = new ShipmentManagerJDBC(this);
		this.supplierMan = new SupplierManagerJDBC(this);
		this.paymentMan = new PaymentManagerJDBC(this);
		System.out.println("Managers initialized correctly.");
	}

	public Connection getConnection() {
		return c;
	}

	public ClientManagerJDBC getClientManager() {
		return clientMan;
	}

	public OrderManagerJDBC getOrderManager() {
		return orderMan;
	}

	public ProductManagerJDBC getProductManager() {
		return productMan;
	}

	public ProductOrderManagerJDBC getProductOrderManager() {
		return productOrderMan;
	}

	public ShipmentManagerJDBC getShipmentManager() {
		return shipmentMan;
	}

	public SupplierManagerJDBC getSupplierManager() {
		return supplierMan;
	}

	public PaymentManagerJDBC getPaymentManager() {
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
