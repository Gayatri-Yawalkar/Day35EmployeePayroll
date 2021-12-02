package com.bridgelabz.employeepayroll;
//Uc1
import java.sql.DriverManager;
import java.util.Enumeration;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
public class DbConnection {
	public static void main(String[] args) {
		String jdbcUrl="jdbc:mysql://localhost:3306/payrollservice?useSSL=false";
		String userName="root";
		String password="root";
		Connection con;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded");
		} catch(ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find driver in class path");
		}
		listDrivers();
		try {
			System.out.println("Connecting to database:"+jdbcUrl);
			con=(Connection) DriverManager.getConnection(jdbcUrl, userName, password);
			System.out.println("Coonection is successfull");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private static void listDrivers() {
		Enumeration<java.sql.Driver> driverList=DriverManager.getDrivers();
		while(driverList.hasMoreElements()) {
			Driver driverClass=(Driver)driverList.nextElement();
			System.out.println("   "+driverClass.getClass().getName());
		}
	}
}
