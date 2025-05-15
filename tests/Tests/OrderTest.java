package Tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import Exceptions.OrderExceptions;
import HospiCartPOJOs.Category;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Manufacturer;
import HospiCartPOJOs.Order;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.PaymentMethod;
import HospiCartPOJOs.PaymentStatus;
import HospiCartPOJOs.Product;
import HospiCartPOJOs.ProductOrder;
import HospiCartPOJOs.Shipment;
import HospiCartPOJOs.Supplier;

public class OrderTest {
	@Test
	public void constructorOrderInvalidCLientTest() {
		//I create an 'incomplete' client with the constructor of Client that does not admit a user_id.
		Client expectedClient = new Client("Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
				
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(123456);
		Product product1 = new Product("Gloves", Category.DISPOSABLES, "Latex pink gloves", 2.1f, 300, false);
		Product product2 = new Product("Masks", Category.DISPOSABLES, "Blue Masks", 3.1f, 200, false);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		ProductOrder productOrder1 = new ProductOrder(4, 6.1f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);			
		
        assertThrows(OrderExceptions.class, () -> new Order(expectedClient, payment, shipment, productOrders));
	}
	
	@Test
	public void constructorOrderInvalidPaymentTest() {
		//I create a client with an ID so that the client is valid in order to create the order.
		Client expectedClient = new Client(1, "Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
				
		//I create a payment, a shipment and 2 products.
		Payment payment = new Payment(5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(123456);
		Product product1 = new Product("Gloves", Category.DISPOSABLES, "Latex pink gloves", 2.1f, 300, false);
		Product product2 = new Product("Masks", Category.DISPOSABLES, "Blue Masks", 3.1f, 200, false);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		ProductOrder productOrder1 = new ProductOrder(4, 6.1f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);			
		
        assertThrows(OrderExceptions.class, () -> new Order(expectedClient, payment, shipment, productOrders));
	}
	
	@Test
	public void constructorOrderInvalidShipmentTest() {
		//I create a client with an ID so that the client is valid in order to create the order.
		Client expectedClient = new Client(1, "Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
				
		//I create a payment, a shipment and 2 products.
		//I create a payment with a payment ID so that the payment is valid to create the order.
		Payment payment = new Payment(1, 5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		Shipment shipment = new Shipment(123456);
		Product product1 = new Product("Gloves", Category.DISPOSABLES, "Latex pink gloves", 2.1f, 300, false);
		Product product2 = new Product("Masks", Category.DISPOSABLES, "Blue Masks",3.1f, 200, false);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		ProductOrder productOrder1 = new ProductOrder(4, 6.1f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);			
		
        assertThrows(OrderExceptions.class, () -> new Order(expectedClient, payment, shipment, productOrders));
	}
	
	@Test
	public void constructorOrderInvalidProductTest() {
		//I create a client with an ID so that the client is valid in order to create the order.
		Client expectedClient = new Client(1, "Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
				
		//I create a payment, a shipment and 2 products.
		//I create a payment with a payment ID so that the payment is valid to create the order.
		Payment payment = new Payment(1, 5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		//I create a shipment with a shipment ID so that the shipment is valid to create the order.
		Shipment shipment = new Shipment(1, 123456);
		Product product1 = new Product("Gloves", Category.DISPOSABLES, "Latex pink gloves", 2.1f, 300, false);
		Product product2 = new Product("Masks", Category.DISPOSABLES, "Blue Masks", 3.1f, 200, false);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		ProductOrder productOrder1 = new ProductOrder(4, 6.1f, product1);
		ProductOrder productOrder2 = new ProductOrder(6, 18.6f, product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
		productOrders.add(productOrder1);
		productOrders.add(productOrder2);			
		
        assertThrows(OrderExceptions.class, () -> new Order(expectedClient, payment, shipment, productOrders));
	}
	
	@Test
	public void constructorOrderInvalidProductOrderTest() {
		//I create a client with an ID so that the client is valid in order to create the order.
		Client expectedClient = new Client(1, "Julian", "Alvarez", 346667865, "julialvarez@gmail.com", "Calle de la Princesa 30");
				
		//I create a payment, a shipment and 2 products.
		//I create a payment with a payment ID so that the payment is valid to create the order.
		Payment payment = new Payment(1, 5, PaymentMethod.BANK_TRANSFER, PaymentStatus.COMPLETED);
		//I create a shipment with a shipment ID so that the shipment is valid to create the order.
		Shipment shipment = new Shipment(1, 123456);
		Product product1 = new Product(1, "Gloves", Category.DISPOSABLES, "Latex pink gloves", 2.1f, 300, false);
		Product product2 = new Product(2, "Masks", Category.DISPOSABLES, "Blue Masks", 3.1f, 200, false);
				
		//I create a list of products and add the products I created above.
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		//I create the supplier.
		new Supplier(1, products ,Manufacturer.THERMO_FISHER, "Fabio Lopez", "Calle de Lisboa 34");
				
		new ProductOrder(4, 6.1f, product1);
		new ProductOrder(6, 18.6f, product2);
		//I create the list of product orders and add the product orders to the list.
		List<ProductOrder> productOrders = new ArrayList<ProductOrder>();	
		
        assertThrows(OrderExceptions.class, () -> new Order(expectedClient, payment, shipment, productOrders));
	}
}
