import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Container.*;
import java.awt.event.*;
import java.util.*;
import java.util.zip.*;
import org.jdesktop.swingx.*;

class User {
	String userName;
	long userPasswd;
	boolean admin, authorized;
	
	void setUserName(String userName){
		this.userName = userName;
	}
	
	void setUserPasswd(String userPasswd){
		this.userPasswd = AdlerChecksum.getAdler(userPasswd);
	}
	
	void setAdmin(boolean admin){
		this.admin = admin;
	}
	
	void setAuthorized(boolean authorized){
		this.authorized = authorized;
	}
	
	String getUserName(){
		return userName;
	}
	
	long getUserPasswd(){
		return userPasswd;
	}
	
	boolean getAdmin(){
		return admin;
	}
	
	boolean getAuthorized(){
		return authorized;
	}
}

class AdlerChecksum {

	static private Adler32 checksum;

	static {
		checksum = new Adler32();
	}
	
	static long getAdler(String str){
		checksum.reset();
		checksum.update(str.getBytes());
		return checksum.getValue();
	}
}z

class LiteDataBase {

	private Connection connection;
	private Statement statement;
	ResultSet resultSet;

	static {
		try{
// 			causes the "org.sqlite.JDBC" class to be initialized
			Class.forName("org.sqlite.JDBC");
		}
		catch(Exception e){
			System.out.println("LiteDataBase loader: " + e);
		}
	}


	LiteDataBase(){};
	LiteDataBase(String filename){
		try{
// 			connects to URL "jdbc:sqlite:filename"
			connection = DriverManager.getConnection("jdbc:sqlite:"+filename);
			
// 			creates a Statement object for sending SQL statements
// 			to the database
			statement = connection.createStatement();
		}
		catch(Exception e){
			System.out.println("LiteDataBase constructor: " + e);
		}
	}

	
	void connect(String filename){
	
		try{
			connection = DriverManager.getConnection("jdbc:sqlite:"+filename);
			statement = connection.createStatement();
		}
		catch(Exception e){
			System.out.println("LiteDataBase->connect: " + e);
		}
	
	}
	
	
	
	void query(String query) {
	
// 		executes given SQL statement
// 		executeQuery never returns null
		try{
			System.out.println(query);
			resultSet = statement.executeQuery(query);
		}
		catch(Exception e){
			System.out.println("LiteDataBase->query(String): " + e);
		}
	}
	
	
	void noResultsQuery(String query){
		try{
			System.out.println(query);
			statement.executeUpdate(query);
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Problem!" + e);
			System.out.println("LiteDataBase->noResultsQuery: " + e);
		}		
	}

	
	boolean next(){
		try{
			return resultSet.next();
		}
		catch(Exception e){
			System.out.println("LiteDataBase->next: " + e);
			return false;
		}		
	}
	
	String getString(String columnName){
		try{
			return resultSet.getString(columnName);
		}
		catch(Exception e){
			System.out.println("LiteDataBase->getString: " + e);
			return null;
		}		
	}
	
	long getLong(String columnName){
		try{
			return resultSet.getLong(columnName);
		}
		catch(Exception e){
			System.out.println("LiteDataBase->getLong: " + e);
			return 0;
		}
	}
}
