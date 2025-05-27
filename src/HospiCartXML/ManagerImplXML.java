package HospiCartXML;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import HospiCartInterfaces.IXMLManager;
import HospiCartPOJOs.Client;
import Utilities.Output;

public class ManagerImplXML implements IXMLManager {

	@Override
	public void client2Xml(Client c) { // Marshalling method: turns Java objects into XML documents 
		try {
			// Create the JAXBContext 
			JAXBContext jaxbContext = JAXBContext.newInstance(Client.class); // throws a JAXBException
			
			// Create the JAXBMarshaller 
			Marshaller marshaller = jaxbContext.createMarshaller();
			
			// Pretty formatting 
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			// Write to a file 
			File file = new File("./xmls/Client.xml");
			marshaller.marshal(c, file);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Client xml2Client(File xml) { // Unmarshalling methods: turns XML documents into Java objects 
		Client c = null;
		try {
			// Create the JAXBContext 
			JAXBContext jaxbContext = JAXBContext.newInstance(Client.class); // throws a JAXBException
			
			// Create the JAXBUnmarshaller
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			// Create the object by reading from a file 
			c = (Client) unmarshaller.unmarshal(xml);
			
			return c;
						
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return c;
	}

}
