package HospiCartInterfaces;

import java.sql.SQLException;
import java.util.List;

import HospiCartPOJOs.Supplier;

public interface ISupplierManager {

	public void insertSuppliersFromCSV(String filePath);
	
	public void insertSupplier(Supplier supplier) throws SQLException;

	public void showCompanyNames();
	
    public Supplier getSupplierByID(int supplier_id) throws SQLException;


    public Supplier getSupplierByCompanyName(String companyName) throws SQLException;
    
	public List<String> getCompanyNames();
	
    public void updateSupplierAddress(int supplier_id, String newAddress) throws SQLException;
    
    public void updateSupplierContactNumber(int supplier_id, int contactNumber) throws SQLException;
}
