package HospiCartJDBC;
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

   /* public ConnectionManager() { // VOLVER e implementar
        this.connect();
        this.clientMan = new ClientManagerImpl(this);
        this.orderMan = new OrderManagerImpl(this);
        this.productMan = new ProductManagerImpl(this);
        this.productOrderMan = new ProductOrderManagerImpl(this);
        this.shipmentMan = new ShipmentManagerImpl(this);
        this.supplierMan = new SupplierManagerImpl(this);
        this.paymentMan = new PaymentManagerImpl(this);
    } */

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.c = DriverManager.getConnection("jdbc:sqlite:your_database.db"); // Update DB path
            this.c.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	
	
}
