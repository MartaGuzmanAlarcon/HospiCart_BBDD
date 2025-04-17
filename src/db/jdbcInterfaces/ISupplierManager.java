package db.jdbcInterfaces;

public interface ISupplierManager {

	public void insertSuppliersFromCSV(String filePath);

	public void showCompanyNames();

}
