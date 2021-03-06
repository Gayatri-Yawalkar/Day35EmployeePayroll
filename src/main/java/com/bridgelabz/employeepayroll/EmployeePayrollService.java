package com.bridgelabz.employeepayroll;
import java.time.LocalDate;
import java.util.ArrayList;
//Uc12
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO,FILE_IO,DB_IO,REST_IO
	}
	public List<EmployeePayrollData> employeePayrollList;
	public EmployeePayrollDbService employeePayrollDbService;
	public EmployeePayrollService() {
		employeePayrollDbService=EmployeePayrollDbService.getInstance();
	}
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList=employeePayrollList;
	}
	public void writeEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("\nWriting Employee Payroll to Console\n"+employeePayrollList);
		} else if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
	}
	public void readEmployeePayrollData(Scanner sc) {
		System.out.println("Enter Employee id");
		int id=sc.nextInt();
		System.out.println("Enter Employee Name");
		String name=sc.next();
		System.out.println("Enter Employee Salary");
		double salary=sc.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList=employeePayrollDbService.readData();
			return this.employeePayrollList;
		}
		return null;
	}
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService,int id) {
		if(ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList=employeePayrollDbService.getEmployeePayrollDataUsingDB(id);
			return this.employeePayrollList;
		}
		return null;
	}
	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate,
																	 LocalDate endDate) {
		if(ioService.equals(ioService.DB_IO)) {
			return employeePayrollDbService.getEmployeeDateRange(startDate,endDate);
		}
		return null;
	}
	public Map<String,Double> readAverageSalaryByGender(IOService ioService){
		if(ioService.equals(ioService.DB_IO)) {
			return employeePayrollDbService.getAverageSalaryByGender();
		}
		return null;
	}
	public int updateEmployeeSalary(int id,String name,double salary) {
		int result=employeePayrollDbService.updateSalary(id, salary);
		if(result==0) {
			return result;
		}
		EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
		if(employeePayrollData!=null) {
			employeePayrollData.salary=salary;
		}
		return result;
	}
	public void addEmployeeToPayrollSingleTable(String name, double salary, LocalDate startDate, String gender) {
		employeePayrollList.add(employeePayrollDbService.addEmployeePayrollSingleTable(name,salary,startDate,gender));
	}
	public void addEmployeeToPayrollMultipleTable(String name, double salary, LocalDate startDate, String gender) {
		employeePayrollList.add(employeePayrollDbService.addEmployeePayrollMultipleTable(name,salary,startDate,gender));
	}
	public void addEmployeeToPayrollMultipleTable(String name, double salary, LocalDate startDate, String gender,ArrayList<String> dept) {
		employeePayrollList.add(employeePayrollDbService.addEmployeePayrollMultipleTable(name,salary,startDate,gender,dept));
	}
	private EmployeePayrollData getEmployeePayrollData(String name) {
		EmployeePayrollData employeePayrollData;
		employeePayrollData=this.employeePayrollList.stream()
				.filter(employeePayrollDataItem->employeePayrollDataItem.name.equals(name))
				.findFirst()
				.orElse(null);
		return employeePayrollData;
	}
	public boolean checkEmployeePayrollSyncWithDb(String name) {
		List<EmployeePayrollData> employeePayrollDataList=employeePayrollDbService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
	public void printData(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printdata();
		}
	}
	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}
	public int removeEmployee(int id,String name) {
		int result=employeePayrollDbService.removeEmployee(id);
		this.getEmployeePayrollData(name).isActive=false;
		if(result==1) {
			System.out.println("Employee is successfully Removed");
		}
		return result;
	}
}
