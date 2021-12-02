package com.bridgelabz.employeepayroll;
import java.time.LocalDate;
import java.util.ArrayList;
public class EmployeePayrollData {
	public Integer id;
	public String name;
	public Double salary;
	public LocalDate startDate;
	public ArrayList<String> depart=new ArrayList<String>();
	public Boolean isActive;
	public EmployeePayrollData(Integer id,String name,Double salary) {
		this.id=id;
		this.name=name;
		this.salary=salary;
	}
	public EmployeePayrollData(Integer id,String name,Double salary,LocalDate startDate) {
		this(id,name,salary);
		this.startDate=startDate;
	}
	public EmployeePayrollData(Integer id,String name,Double salary,LocalDate startDate,ArrayList depart) {
		this(id,name,salary,startDate);
		this.depart=depart;
	}
	public EmployeePayrollData(Integer id,String name,Double salary,LocalDate startDate,ArrayList depart,Boolean isActive) {
		this(id,name,salary,startDate,depart);
		this.isActive=isActive;
	}
	public StringBuffer printArray() {
		StringBuffer sb=new StringBuffer();
		for(String s:depart) {
			sb.append(s+" ");
		}
		return sb;
	}
	@Override
	public String toString() {
		return "id="+id+", Name='"+name+'\''+", Salary="+salary+" Dept="+printArray();
	}
	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		EmployeePayrollData that=(EmployeePayrollData) obj;
		return id==that.id && Double.compare(that.salary,salary)==0 && name.equals(that.name);
	}
}
