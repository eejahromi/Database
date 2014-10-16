import java.sql.*; 
import java.util.*;
import java.io.*;

public class Database{
	public  static void main(String [] args){
	
	// Array List for inputs
	ArrayList array = new ArrayList(); 
	Scanner scan = new Scanner(System.in);
	Random numberGenerator = new Random();
		
	String url = "jdbc:oracle:thin:@cslabdb:1525:cfedb";	
	
	Statement stmt;
	Connection con;
	
	// Queries for part 1, part 3 and part 4
		
	String query = "SELECT ENAME, SAL FROM empbb02 WHERE POS!='gm' AND POS!='coach'";
	String query3 = "ALTER TABLE infobb02 add Wife varchar2(15)";
	// if I want to list the name for part 3 (wife names)
	String query3b = "SELECT FNAME FROM infobb02";
	String query4 = "ALTER TABLE infobb02 RENAME COLUMN Wife to \"Significant Other\"";
	try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
	} catch( Exception e){
		System.out.println("Sorry driver could not be loaded" + e);
		} //end catch
		
	try{
		con = DriverManager.getConnection(url,"st80","secret");
		stmt = con.createStatement();
			
		
	/**********	part 1	**********************************************************/	
			
		ResultSet rs = stmt.executeQuery(query);


		while(rs.next()){
			String names = rs.getString("ename");
			String salaries = rs.getString("sal");
				
			array.add(names);
			array.add(salaries);
			System.out.println(names + " " + salaries);								
				
		} //end while
		
		
		System.out.println("\nPlease enter a player's name:");
		String name = scan.nextLine();
		//check for name
		validateName(array,name);
		System.out.println("Made it!");
		int low = 20000;
		int high = 25000;
		
		// generating a random number within the range that Dr.Eckberg pays!!!
		int newSalary = numberGenerator.nextInt(high - low) + low;				
		
		PreparedStatement salaryUpdate = con.prepareStatement("UPDATE "
		+ "empbb02 SET SAL=? WHERE ENAME=?");
			salaryUpdate.setInt(1,newSalary);
			salaryUpdate.setString(2,name);	
			
		System.out.println(salaryUpdate.executeUpdate());	
		salaryUpdate.close();			
	
	/*****	part 2	************************************************/
		
		System.out.println("Please enter a number between 1 and 15:");
		int n = scan.nextInt();
		while(n > 15 && n < 1){
			System.out.println("Invalid number.Try again:");
			n = scan.nextInt();
			}
		CallableStatement cstmtf = conn.prepareCall("{? = call "
			+ "salaryRank(?)}");
			
		cstmtf.setInt(2,n);
		cstmtf.registerOutParameter(1,Types.VARCHAR);
		cstmtf.execute();
		String salaryName = cstmtf.getString(1);
		System.out.println(salaryName);	
	
	
		cstmtf.close();
	
	/*****	part 3	*************************************************/
	// adding a new column names Wife and adding user-input names to the column
		stmt.executeUpdate(query3);
		ResultSet rs2 = stmt.executeQuery(query3b);
		String [] listOfNames = new String[15];
		int counter = 0;
		while(rs2.next()){
			String names = rs2.getString("fname");
			listOfNames[counter] = names;
			counter++;
		}
		
		String [] wifeNames = new String[15];
		// use a for loop here to iterate through players names
		for(int i=0;i<listOfNames.length;i++){
		System.out.println("Please enter a name for " + listOfNames[i] + "'s wife:");
		wifeNames[i] = scan.nextLine();
		
		}
		// inserting the user-input wife names
		for(int i=0;i<wifeNames.length;i++){
		PreparedStatement names = con.prepareStatement("UPDATE infobb02 "
		+ "SET Wife=? where fname=?");
			names.setString(1,wifeNames[i]);
			names.setString(2,listOfNames[i]);
			
			names.executeUpdate();
		}
		
		
		
	/*****	part 4	*************************************************/
	// change the column name to Significant Other
		stmt.executeUpdate(query4);
	
	
	
	/*****	part 5	*************************************************/
	
		CallableStatement cstmt = conn.prepareCall("{CALL getBattingAvg(?,?,?)}");
		
		System.out.println("Please enter an integer for the batting average:");
		int average = scan.nextInt();
		cstmt.setInt(1,average);
		cstmt.registerOutParameter(2,Types.NUMERIC,0);
		cstmt.registerOutParameter(3,Types.VARCHAR);
		cstmt.executeUpdate();
		int batterAvg = cstmt.getInt(2);
		String batterName = cstmt.getString(3);
		System.out.println(batterName + batterAvg);
	
	// closing statements and connections 
	
	cstmt.close();
	names.close();
	stmt.close();
	con.close();
		
	} catch(SQLException e) {
		System.err.println("OOPS " + e.getMessage());
		} //end catch
	} //end main
	
	// validating the name before sending the query to the databse
	public static void validateName(ArrayList array,String name){
		Scanner scan2 = new Scanner(System.in);
		while(array.contains(name.toLowerCase()) == false){
			System.out.println("Invalid name.\nPlease enter a name from the above list:");
			name = scan2.nextLine();
					
		}
	
	} //end validateName
	
	
} //end class
