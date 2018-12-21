package Project;

import java.sql.*;
import java.util.*;

/*Author:  Richa Patel*/

public class P3 {
	public static void main(String[] args) throws Exception {
		// Load and register a JDBC driver
		try {
			// Load the driver (registers itself)
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception E) {
			System.err.println("Unable to load driver.");
			E.printStackTrace();
		}
		try {
			// Connect to the database
			Connection conn1;
			String dbUrl = "jdbc:mysql://csdb.cs.iastate.edu:3306/db363yshen";
			String user = "dbu363yshen";
			String password = "BfeO1299";
			conn1 = DriverManager.getConnection(dbUrl, user, password);
			System.out.println("*** Connected to the database ***");

			// Create Statement and ResultSet variables to use throughout the project
			Statement statement = conn1.createStatement();
			ResultSet rs;
			
			// A
			System.out.println("*** Part A output is showed below ***");
			rs = statement.executeQuery("select g.Name, f.Salary "
					+ "from Instructor f, Person g "
					+ "where f.InstructorID = g.ID");
			
			String Name;
			int Salary;
			int totalSalary = 0;
			
			while(rs.next()){
				Name = rs.getString(1);
				Salary = rs.getInt(2);
				System.out.println(Name+" "+Salary);
				totalSalary += Salary;	
			}
			System.out.println("Total Salary of all faculty: "+totalSalary);
			
			// B
			String Create = "create table MeritList( "
					+ "ID char (9) not null, "
					+ "Classification char (10), "
					+ "MentorID char (9) not null, "
					+ "GPA double);";
			
			statement.executeUpdate(Create);
			
			rs = statement.executeQuery("select e.StudentID, e.Classification, e.MentorID, e.GPA "
					+ "from Student e "
					+ "where e.GPA >= "
					+ "(select p.GPA "
					+ "from Student p "
					+ "order by p.GPA desc "
					+ "limit 1 offset 19) "
					+ "order by e.GPA desc");
			
			String insert = "insert into MeritList (ID, Classification, MentorID, GPA) "
					+ "values(?,?,?,?)";
			
			PreparedStatement stmt3 = conn1.prepareStatement(insert);

			while(rs.next()){
				stmt3.setString(1, rs.getString(1));
				stmt3.setString(2, rs.getString(2));
				stmt3.setString(3, rs.getString(3));
				stmt3.setDouble(4, rs.getDouble(4));
				stmt3.executeUpdate();
			}
			
			// C
			System.out.println("*** Part C Output is showed below***");
			rs = statement.executeQuery("select * "
					+ "from MeritList m "
					+ "order by m.GPA");
			
			String ID;
			String Classification;
			String MentorID;
			double GPA;
			
			while(rs.next()){
				ID = rs.getString(1);
				Classification = rs.getString(2);
				MentorID = rs.getString(3);
				GPA = rs.getDouble(4);
				System.out.println(ID+" "+Classification+" "+MentorID+" "+GPA);
			}
			
			// D
			String update = "update Instructor " +
					"set Salary=? " +
					"where InstructorID = ?";
			
			stmt3 = conn1.prepareStatement(update);
			
			rs = statement.executeQuery("select t.MentorID, t.Classification, w.Salary "
					+ "from MeritList t, Instructor w "
					+ "where t.MentorID = w.InstructorID "
					+ "group by t.Classification, t.MentorID");
			
			ArrayList<String> Mentor = new ArrayList<String>();
			ArrayList<String> year = new ArrayList<String>();
			ArrayList<Double> newSalary = new ArrayList<Double>();
			String Ment = "";
			String yea = "";
			Double sal = 0.0;
			boolean find = false;
			
			while(rs.next())
			{
				Ment = rs.getString(1);
				yea = rs.getString(2);
				sal = rs.getDouble(3);
				if(yea.equals("Freshman"))
				{
					Mentor.add(Ment);
					year.add(yea);
					newSalary.add(sal * 1.04);
				}else if(yea.equals("Junior"))
				{
					for(int i=0; i<Mentor.size(); i++)
					{
						if(Mentor.get(i).equals(Ment) && year.get(i).equals("Freshman"))
						{
							year.set(i, yea);
							newSalary.set(i, sal * 1.06);
							find = true;
						}
					}
					if(find==false){
						Mentor.add(Ment);
						year.add(yea);
						newSalary.add(sal * 1.06);
					}
					find=false;
				}else if(yea.equals("Sophomore"))
				{
					for(int i=0; i<Mentor.size(); i++)
					{
						if(Mentor.get(i).equals(Ment) && (year.get(i).equals("Freshman") || year.get(i).equals("Junior")))
						{
							year.set(i, yea);
							newSalary.set(i, sal * 1.08);
							find=true;
						}
					}
					if(find==false){
						Mentor.add(Ment);
						year.add(yea);
						newSalary.add(sal * 1.08);
					}
					find=false;
				}else
				{
					for(int i=0; i<Mentor.size(); i++)
					{
						if(Mentor.get(i).equals(Ment))
						{
							year.set(i, yea);
							newSalary.set(i, Math.round(sal * 1.10 *100)/100.0);
							find=true;
						}
					}
					if(find==false){
						Mentor.add(Ment);
						year.add(yea);
						newSalary.add(sal * 1.10);
					}
					find=false;
				}
			}
		
			for(int i=0; i<Mentor.size(); i++){
				stmt3.setDouble(1, newSalary.get(i));
				stmt3.setString(2, Mentor.get(i));
				stmt3.executeUpdate();
			}

			
			// E
			System.out.println("*** Part E output below ***");
			rs = statement.executeQuery("select g.Name, f.Salary "
					+ "from Instructor f, Person g "
					+ "where f.InstructorID = g.ID");
			
			totalSalary = 0;
			
			while(rs.next()){
				Name = rs.getString(1);
				Salary = rs.getInt(2);
				System.out.println(Name+" "+Salary);
				totalSalary += Salary;	
			}
			System.out.println("Total Salary of all faculty: "+totalSalary);
			
			// F
			statement.close();
			rs.close();
			stmt3.close();
			conn1.close();

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}
}
