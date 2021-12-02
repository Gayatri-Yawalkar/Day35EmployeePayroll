package com.bridgelabz.junit;
//Uc11
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.bridgelabz.employeepayroll.EmployeePayrollData;
import com.bridgelabz.employeepayroll.EmployeePayrollDbService;
import com.bridgelabz.employeepayroll.EmployeePayrollService;
import com.bridgelabz.employeepayroll.EmployeePayrollService.IOService;
public class EmployeePayrollServiceDbTest {
	@Test
	public void givenEmployeePayrollDb_whenRetrived_shouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		System.out.println(employeePayrollData);
		assertEquals(9,employeePayrollData.size());
	}
	@Test
	public void givenNewSalaryForEmployee_whenUpdated_shouldSyncWithDb() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary(1,"Gayatri",50002.00);
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDb("Gayatri");
		assertTrue(result);
	}
	@Test
	public void givenDateRange_whenRetrived_shouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate=LocalDate.of(2020,04,01);
		LocalDate endDate=LocalDate.now();
		List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollForDateRange(IOService.DB_IO,startDate,endDate);
		assertEquals(3,employeePayrollData.size());
	}
	@Test
	public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperValue() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String,Double> averageSalryByGender=employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		assertTrue(averageSalryByGender.get("F").equals(30001.00) && averageSalryByGender.get("M").equals(20000.00));
	}
	@Test
	public void givenNewEmployee_whenAdded_shouldSyncWithDb() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayrollSingleTable("Samiksha",5000.0,LocalDate.now(),"F");
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDb("Samiksha");
		assertTrue(result);
	}
	@Test
	public void givenNewEmployee_whenAdded_shouldAddedToTableAndSyncWithDb() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayrollMultipleTable("Shyam",12000.0,LocalDate.now(),"M");
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDb("Shyam");
		assertTrue(result);
	}
	@Test
	public void givenEmployeePayrollDb_whenSpecificRecordRetrived_shouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(IOService.DB_IO,1);
		System.out.println(employeePayrollData);
		assertEquals(1,employeePayrollData.size());
	}
	@Test
	public void givenNewSalaryForSpecificEmployee_whenUpdated_shouldSyncWithDb() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary(8,"Shyam",50002.00);
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDb("Shyam");
		assertTrue(result);
	}
	@Test
	public void givenNewEmployeeWithDept_whenAdded_shouldSyncWithDb() {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		ArrayList<String> department=new ArrayList<>();
		department.add("Sales");
		employeePayrollService.addEmployeeToPayrollMultipleTable("Samiksha",5000.0,LocalDate.now(),"F",department);
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDb("Samiksha");
		assertTrue(result);
	}
}
