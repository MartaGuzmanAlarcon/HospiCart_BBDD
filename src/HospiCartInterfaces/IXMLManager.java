package HospiCartInterfaces;
import java.io.File;
import HospiCartPOJOs.Client;

public interface IXMLManager {
	public void client2Xml(Client c);
	
	public Client xml2Client(File xml);
	
}
