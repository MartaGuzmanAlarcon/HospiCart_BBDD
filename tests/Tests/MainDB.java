package Tests;

import db.HospiCartJDBC.ConnectionManagerJDBC;

public class MainDB {

	public static void main(String[] args) {
		ConnectionManagerJDBC dbManager = new ConnectionManagerJDBC();
		dbManager.disconnect();
	}
}
