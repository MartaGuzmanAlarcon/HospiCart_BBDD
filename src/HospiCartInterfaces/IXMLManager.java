package HospiCartInterfaces;
import java.io.File;
import HospiCartPOJOs.Client;
import HospiCartPOJOs.Product;

public interface IXMLManager {
	public void client2Xml(Client c);
	
	public Client xml2Client(File xml);
	
	public void product2Xml(Product p);
	
	public Product xml2Product(File xml);
	
}
