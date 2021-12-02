package com.bridgelabz.employeepayroll;
//Uc12
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class EmployeePayrollDbService {
	private static EmployeePayrollDbService employeePayrollDbService;
	private PreparedStatement employeePayrollDataStatement;
	private EmployeePayrollDbService() {
	}
	public static EmployeePayrollDbService getInstance() {
		if(employeePayrollDbService==null) {
			employeePayrollDbService=new EmployeePayrollDbService();
		}
		return employeePayrollDbService;
	}
	public List<EmployeePayrollData> readData(){
		String sql="SELECT * FROM employeepayroll;";
		return this.getEmployeePayrollDataUsingDB(sql);
	}
	public List<EmployeePayrollData> getEmployeePayrollData(String name){
		List<EmployeePayrollData> employeePayrollList=null;
		if(this.employeePayrollDataStatement==null) {
			this.prepareStatementForEmployeeData();
		}
		try {
			employeePayrollDataStatement.setString(1,name);
			ResultSet resultSet=employeePayrollDataStatement.executeQuery();
			employeePayrollList=this.getEmployeePayrollData(resultSet);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		try (Connection connection=this.getConnection();){
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(sql);
			employeePayrollList=this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	public List<EmployeePayrollData> getEmployeeDateRange(LocalDate startDate, LocalDate endDate) {
		String sql=String.format("SELECT * FROM employeepayroll WHERE start BETWEEN '%s' AND '%s';",
								  Date.valueOf(startDate),Date.valueOf(endDate));
		return this.getEmployeePayrollDataUsingDB(sql);
	}
	public Map<String,Double> getAverageSalaryByGender(){
		String sql="SELECT gender,AVG(salary) FROM employeepayroll GROUP BY gender;";
		Map<String,Double> genderToAverageSalaryMap=new HashMap<>();
		try (Connection connection=this.getConnection();){
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(sql);
			String gender;
			Double salary;
			while(resultSet.next()) {
				gender=resultSet.getString("gender");
				salary=resultSet.getDouble("AVG(salary)");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return genderToAverageSalaryMap;
	}
	private Connection getConnection() throws SQLException {
		String jdbcUrl="jdbc:mysql://localhost:3306/payrollservice?useSSL=false";
		String userName="root";
		String password="root";
		Connection con;
		System.out.println("Connecting to database:"+jdbcUrl);
		con=DriverManager.getConnection(jdbcUrl, userName, password);
		System.out.println("Connection is successfull");
		return con;
	}
	
	public List<EmployeePayrollData> getEmployeePayrollDataUsingDB(int id) {
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		Connection connection=null;
		ResultSet resultSet = null,resultSet2 = null;
		try {
			connection=this.getConnection();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		try {
			String sql="select * from employeepayroll where id="+id+";";
			Statement statement=connection.createStatement();
			resultSet=statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sql2="select department.dept_name "
					+ "from employeepayroll join emp_dept_relation "
					+ "on employeepayroll.id=emp_dept_relation.emp_id "
					+ "join department on department.dept_id=emp_dept_relation.dept_id;";
			Statement statement=connection.createStatement();
			resultSet2=statement.executeQuery(sql2);
			employeePayrollList=this.getEmployeePayrollData(resultSet,resultSet2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		int id;
		String name;
		double salary;
		LocalDate startDate;
		try {
			while(resultSet.next()) {
				id=resultSet.getInt("id");
				name=resultSet.getString("name");
				salary=resultSet.getDouble("salary");
				startDate=resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name,salary,startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet,ResultSet resultSet2) {
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		int id = 0;
		String name = null;
		double salary = 0;
		LocalDate startDate = null;
		ArrayList<String> dept=new ArrayList<String>();
		try {
			while(resultSet.next()) {
				id=resultSet.getInt("id");
				name=resultSet.getString("name");
				salary=resultSet.getDouble("salary");
				startDate=resultSet.getDate("start").toLocalDate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			while(resultSet2.next()) {
				dept.add(resultSet2.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		employeePayrollList.add(new EmployeePayrollData(id, name,salary,startDate,dept));
		return employeePayrollList;
	}
	private void prepareStatementForEmployeeData() {
		try {
			Connection connection=this.getConnection();
			String sql="SELECT * FROM employeepayroll WHERE name=?";
			employeePayrollDataStatement=connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int updateSalary(int id,double salary) {
		int rowAffected=-1;
		Connection connection=null;
		try {
			connection=this.getConnection();
			connection.setAutoCommit(false);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		try(Statement statement=connection.createStatement()) {
			String sql=String.format("update employeepayroll set salary=%.2f where id='%s';",salary,id);
			rowAffected=statement.executeUpdate(sql);
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return rowAffected;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try(Statement statement=connection.createStatement()) {
			double deductions=salary*0.2;
			double taxablePay=salary-deductions;
			double tax=taxablePay*0.1;
			double netPay=salary-tax;
			String sql=String.format("update payroll_details set basic_pay=%s,deductions=%.2f,taxable_pay=%s,tax=%s,net_pay=%s where employee_id='%s';",salary,deductions,taxablePay,tax,netPay,id);
			rowAffected=statement.executeUpdate(sql);
			if(rowAffected==1) {
				System.out.println("Update Success");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return rowAffected;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return rowAffected;
	}
	public EmployeePayrollData addEmployeePayrollSingleTable(String name, double salary, LocalDate startDate, String gender) {
		int employeeId=-1;
		EmployeePayrollData employeePayrollData=null;
		String sql=String.format("INSERT INTO employeepayroll(name,gender,salary,start)"+
				                  "VALUES('%s','%s','%s','%s');",name,gender,salary,Date.valueOf(startDate));
		try(Connection connection=this.getConnection()) {
			Statement statement=connection.createStatement();
			int rowAffected=statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
			if(rowAffected==1) {
				ResultSet resultSet=statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId=resultSet.getInt(1);
				}
			}
			employeePayrollData=new EmployeePayrollData(employeeId,name,salary,startDate);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}
	public EmployeePayrollData addEmployeePayrollMultipleTable(String name, double salary, LocalDate startDate,
			                                                   String gender) {
		int employeeId=-1;
		Connection connection=null;
		EmployeePayrollData employeePayrollData=null;
		try {
			connection=this.getConnection();
			connection.setAutoCommit(false);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		try(Statement statement=connection.createStatement()) {
			String sql=String.format("INSERT INTO employeepayroll(name,gender,salary,start)"+
	                "VALUES('%s','%s','%s','%s');",name,gender,salary,Date.valueOf(startDate));
			int rowAffected=statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
			if(rowAffected==1) {
				ResultSet resultSet=statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId=resultSet.getInt(1);
				}
			}
			employeePayrollData=new EmployeePayrollData(employeeId,name,salary,startDate);
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try(Statement statement=connection.createStatement()) {
			double deductions=salary*0.2;
			double taxablePay=salary-deductions;
			double tax=taxablePay*0.1;
			double netPay=salary-tax;
			String sql=String.format("INSERT INTO payroll_details"+
									 "(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES"+
									 "(%s,%s,%s,%s,%s,%s);",employeeId,salary,deductions,taxablePay,tax,netPay);
			int rowAffected=statement.executeUpdate(sql);
			if(rowAffected==1) {
				employeePayrollData=new EmployeePayrollData(employeeId,name,salary,startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}
	public EmployeePayrollData addEmployeePayrollMultipleTable(String name, double salary, LocalDate startDate,
            					String gender,ArrayList<String> dept) {
		int employeeId=-1;
		Connection connection=null;
		EmployeePayrollData employeePayrollData=null;
		try {
			connection=this.getConnection();
			connection.setAutoCommit(false);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		try(Statement statement=connection.createStatement()) {
			String sql=String.format("INSERT INTO employeepayroll(name,gender,salary,start)"+
					"VALUES('%s','%s','%s','%s');",name,gender,salary,Date.valueOf(startDate));
			int rowAffected=statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
			if(rowAffected==1) {
				ResultSet resultSet=statement.getGeneratedKeys();
				if(resultSet.next()) {
					employeeId=resultSet.getInt(1);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try(Statement statement=connection.createStatement()) {
			double deductions=salary*0.2;
			double taxablePay=salary-deductions;
			double tax=taxablePay*0.1;
			double netPay=salary-tax;
			String sql=String.format("INSERT INTO payroll_details"+
					"(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES"+
					"(%s,%s,%s,%s,%s,%s);",employeeId,salary,deductions,taxablePay,tax,netPay);
			int rowAffected=statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
		int deptId=0;
		int rowAffected=0;
			for(String deptName:dept) {
				try(Statement statement=connection.createStatement()) {
					String sql=String.format("select dept_id from department where dept_name='%s';",deptName);
					ResultSet resultSet=statement.executeQuery(sql);
					if(resultSet.next()) {
						deptId=resultSet.getInt("dept_id");
						System.out.println(deptId);
					}
					String sql2=String.format("INSERT INTO emp_dept_relation"+
							"(emp_id,dept_id) VALUES"+
							"(%s,%s);",employeeId,deptId);
					rowAffected=statement.executeUpdate(sql2);
				} catch(SQLException e) {
					e.printStackTrace();
					try {
						connection.rollback();
						return employeePayrollData;
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
			if(rowAffected==1) {
				employeePayrollData=new EmployeePayrollData(employeeId,name,salary,startDate,dept,true);
			}
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if(connection!=null) {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		return employeePayrollData;
	}
	public int removeEmployee(int id) {
		String sql=String.format("update employeepayroll set is_active=%s where id='%s';",0,id);
		try (Connection connection=this.getConnection();){
			Statement statement=connection.createStatement();
			return statement.executeUpdate(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
