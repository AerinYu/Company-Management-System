//Program: Company Management System
// Author: Aerin Yupanqui
// Date: 6 December 2019
// DBMS + JDBC

import java.sql.*;
import java.util.*;


public class ManagementSystem{ 
	static int repeat = 0;
	public static void main(String[]args) throws SQLException {
		// make connection to mysql
		Connection conn = null;
		try {
			conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");	
		}
		
		catch(SQLException e) {
			System.out.println("Connection Failure.");
			e.printStackTrace();
			
		}
		Scanner scan = new Scanner(System.in);
		// print menu options
	while(repeat != 1){	
		String number = null;
		System.out.println("TYPE THE NUMBER OF THE FUNCTION YOU WANT TO EXECUTE:"
				+ "\n________________________________________________________"
				+ "\n 1. Create database \n 2. Initialize department and Project tables "
				+ "\n 3. Add an employee \n 4. Add Works_on entry "
				+ "\n 5. Remove an employee \n 6. Remove a Works_on entry "
				+ "\n 7. Assign employee as a department head"
				+ "\n 8. Input a query");
			
	
		number = scan.nextLine();
	 
		switch(number) {
		case "1":
			createDatabase(conn);
			break;
		case "2":
			projAndDepTables (conn);
			break;
		case "3":
			addEmployee(conn);
			break;
		case "4":
			addWorks_On(conn);
			break;
		case "5":
			removeEmployee(conn);
			break;
		case "6":
			removeWorks_On(conn);
			break;
		case "7":
			assignDeptHead(conn);
			break;
		case "8": 
			query(conn);
			break;	
		}

	}

	}
	
	public static void createDatabase (Connection conn) throws SQLException {
		PreparedStatement s = null;
		// Creates a database called "new_company" with tables: department, employee, project, works_on 
		
		String command = "CREATE DATABASE new_company"; 
		s = conn.prepareStatement(command);
			try{
				s.executeUpdate();
				
		Statement stmt = conn.createStatement();
		String sql = "CREATE TABLE new_company.department(Dlocation VARCHAR(30), Dname VARCHAR(15), Dnumber INT(11), Mgr_ssn CHAR(9))";
		stmt.executeUpdate(sql);		
		sql = "CREATE TABLE new_company.employee(Fname VARCHAR(15), Lname VARCHAR(15),Ssn CHAR(9), Address VARCHAR(30), Dno INT(11))"; 
		stmt.executeUpdate(sql);		
		sql = "CREATE TABLE new_company.project(Dnum INT(11), Plocation VARCHAR(15), Pname VARCHAR(15), Pnumber INT(11))";
		stmt.executeUpdate(sql);		
		sql = "CREATE TABLE new_company.works_on(Essn CHAR(9), Pno INT(11), Hours DOUBLE)";
		stmt.executeUpdate(sql);
		sql = "ALTER TABLE new_company.department ADD PRIMARY KEY (Dnumber)";
		stmt.executeUpdate(sql);
		sql = "ALTER TABLE new_company.employee ADD PRIMARY KEY(Ssn)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.project ADD PRIMARY KEY(Pnumber)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.works_on ADD PRIMARY KEY(Essn, Pno)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.department ADD FOREIGN KEY(Mgr_ssn) REFERENCES employee(Ssn)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.employee ADD FOREIGN KEY(Dno) REFERENCES department(Dnumber)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.project ADD FOREIGN KEY(Dnum) REFERENCES department(Dnumber)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.works_on ADD FOREIGN KEY(Pno) REFERENCES project(Pnumber)";
		stmt.execute(sql);
		sql = "ALTER TABLE new_company.works_on ADD FOREIGN KEY(Essn) REFERENCES employee(Ssn)";
		stmt.execute(sql);
		
		System.out.println("DATABASE CREATED. \n");
		}
		catch(SQLException ex){
			//If database already exists, print
			System.out.println("This database already exists. \n");
		}
	}
	
	public static void projAndDepTables (Connection conn) throws SQLException{
		// initializes the project and department tables. 
		String sql = "SELECT * FROM new_company.project, new_company.department";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next()) {
			
			sql = "INSERT INTO new_company.department(Dlocation, Dname, Dnumber, Mgr_ssn) "
					+ "VALUES(0,'Headquarts',1,null),(0,'Administration',2,null),(0,'Research',3,null)";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
			sql = "INSERT INTO new_company.project" + 
				"(Dnum, Plocation, Pname, Pnumber)" + 
				"VALUES(1,'Bellaire','ProductX',10), (2,'Sugarland','ProductY',30), (3,'Housten','Reorganization',20)";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			System.out.println("TABLES HAVE BEEN INITIALIZED. \n ");
		}
		else {
			rs.beforeFirst();
			System.out.println("The tables are already initialized. \n");
		}
 	}
	
	public static void addEmployee (Connection conn) throws SQLException{
		//allows user to add an employee into the database
		Scanner scan = new Scanner(System.in);
		PreparedStatement s = null;
		String command = "insert into new_company.employee values (?,?,?,?,?)";
		s = conn.prepareStatement(command);
		System.out.println("First name of employee: ");
		String fName = scan.nextLine();
		s.setString(1, fName);
		System.out.println("Last name of employee: ");
		String lName = scan.nextLine();
		s.setString(2, lName);
		System.out.println("Employee ssn:");
		String ssn = scan.nextLine();
		s.setString(3, ssn);
		System.out.println("Employee address:");
		String address = scan.nextLine();
		s.setString(4, address);
		System.out.println("Employee department number:");
		String depNum = scan.nextLine();
		s.setString(5, depNum);
		try {
		s.executeUpdate();
		} 
		// If the employee already exists (ssn), gives the user the option to either cancel the operation or 
		// update the existing employee's information
		catch(SQLIntegrityConstraintViolationException repeat) {
			System.out.println("This employee is already in the system. '\n'"
					+ "Press 1 to cancel. '\n'"
					+ "Press 2 to update existing employee's information. ");
			int r = scan.nextInt();
			if (r == 1) System.out.println("Operation cancelled.");
			else if (r ==2) {
			//Updates employee	
				command = "UPDATE new_company.employee SET Fname = ?, Lname = ?, Ssn = ?, Address = ?, Dno = ? WHERE Ssn = ?";
				s = conn.prepareStatement(command);
				s.setString(1, fName);
				s.setString(2, lName);
				s.setString(3, ssn);
				s.setString(4, address);
				s.setString(5, depNum);
				s.setString(6, ssn);
				s.executeUpdate();
				
			}
			
			else System.out.println("This employee is already in the system. '\t'"
					+ "Press 1 to cancel. '\t'"
					+ "Press 2 to update existing employee's information. ");
		}
		finally {
			System.out.println("COMPLETE. \n");
			repeat = 0;
		}
	}
	
	public static void addWorks_On(Connection conn) throws SQLException{
		//Allows user to add a works_on entry
		Scanner scan = new Scanner(System.in);
		PreparedStatement s = null;
		String command = "insert into new_company.works_on values (?,?,?)";
		s = conn.prepareStatement(command);
		System.out.println("Enter employee ssn: ");
		String Essn = scan.nextLine();
		s.setString(1, Essn);
		System.out.println("Enter project number: ");
		String Pno = scan.nextLine();
		s.setString(2, Pno);
		System.out.println("Enter number of hours: ");
		String hours = scan.nextLine();
		s.setString(3, hours);
		
		try{
			s.executeUpdate();
			System.out.println("COMPLETE. \n");
		}	
		//catches any errors and prints the getMessage
		catch(Exception e){
			System.out.println("There is an error. Please correct mistake and try again. ");
			System.out.println(e.getMessage() +"\n");
		}
	}
	
	public static void removeEmployee(Connection conn) throws SQLException{
		//Allows user to delete an employee by ssn 
		Scanner scan = new Scanner(System.in);
		PreparedStatement s = null;
		String command = "delete from new_company.employee where ssn = ?";
		s = conn.prepareStatement(command);
		System.out.println("Enter employee ssn: ");
		String ssn = scan.nextLine();
		s.setString(1, ssn);
		try { 
		s.executeUpdate();
		System.out.println("COMPLETE. \n");
		}
		//Catches any errors and prints getMessage
		catch(Exception e) {
			System.out.println("There is an error. Please correct mistake and try again. ");
			System.out.println(e.getMessage() + "\n");
			
		}
	}

	
	public static void removeWorks_On (Connection conn) throws SQLException{
		//Allows user to delete a works_on entry
		Scanner scan = new Scanner(System.in);
		PreparedStatement s = null;
		String command = "delete from new_company.works_on where essn = ? and pno = ? ";
		s = conn.prepareStatement(command);
		System.out.println("Enter employee ssn: ");
		String Essn = scan.nextLine();
		s.setString(1, Essn);
		System.out.println("Enter project number: ");
		String Pno = scan.nextLine();
		s.setString(2, Pno);
	try {
		s.executeUpdate();
		System.out.println("COMPLETE. \n");
	}
		
	catch(Exception e) {
		//catches any errors and prints getMessage
		System.out.println("There is an error. Please correct mistake and try again. ");
		System.out.println(e.getMessage() + "\n");
	}
	
	 }	
	
	public static void assignDeptHead(Connection conn) throws SQLException{
		// Allows user to make an employee into a manager
		Scanner scan = new Scanner(System.in);
		PreparedStatement s = null;
		String command = "UPDATE new_company.department SET Mgr_ssn = ? WHERE Dnumber  = ?";
		s = conn.prepareStatement(command);
		System.out.println("Enter ssn of employee to be assigned as manager: ");
		String ssn = scan.nextLine();
		s.setString(1, ssn);
		System.out.println("Enter department number to be assigned: ");
		String dNum = scan.nextLine();
		s.setString(2, dNum);
		//executes a query to see if the ssn matches an existing manager's ssn (checks if employee is already a manager)
			PreparedStatement ps = null;
			String query = "SELECT new_company.department.Mgr_ssn FROM new_company.department WHERE department.Mgr_ssn = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, ssn);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			//If employee is not a manager, execute update
			if (!rs.next()) {
				s.executeUpdate();
				System.out.println("COMPLETE. \n");
			}
			//If manager is already a manager, print.
			else {
				rs.beforeFirst();
				System.out.println("This employee is already a manager. \n");
			}
	}
	
	public static void query(Connection conn) throws SQLException{
		//Allows user to submit an sql query
		Scanner scan = new Scanner(System.in);
		Statement stmt = null;
		System.out.println("Please type query: ");
		String query = scan.nextLine();	
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData();
		//prints results
		int colNum = rsmd.getColumnCount();
		   while (rs.next()) {
		       for (int i = 1; i <= colNum; i++) {
		           String column = rs.getString(i);
		           System.out.print(column + "\n \n"); 
		       }
		   }
	}
	

	
}

