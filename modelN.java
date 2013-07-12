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
	private String userName;
	private long userPasswd;
	private boolean admin, authorized;
	
	public void setUserName(String userName)		{ this.userName = userName; }
	public void setUserPasswd(String userPasswd)	{ this.userPasswd = userPasswd; }
	public void setAdmin(boolean admin)		 	{ this.admin = admin; }
	public void setAuthorized(boolean authorized)	{ this.authorized = authorized; }
	
	public String getUserName()	{ return userName; }
	public long getUserPasswd()	{ return userPasswd; }
	public boolean getAdmin()		{ return admin; }
	public boolean getAuthorized()	{ return authorized; }
}

class Resource {
	private String resourceName;
	private String resourceDescr;
	
	public void setResourceName(String resourceName)	{ this.resourceName = resourceName; }
	public void setResourceDescr(String resourceDescr)	{ this.resourceDescr = resourceDescr; }
	public String getResourceName()	{ return this.resourceName; }
	public String getResourceDescr()	{ return this.resourceDescr; }
}

class Allocation {
	private String userName;
	private String resourceName;
	private integer timeSlot, dateDay, dateMonth, dateYear;
	
	public void setUserName(String userName)		{ this.userName = userName; }
	public void setResourceName(String resourceName){ this.resourceName = resourceName; }
}

class Acesso {
	public static void main(String args []) {
		LiteDataBase db = new LiteDataBase("alfa.sqlite3");
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
}

class LiteDataBase {
	static { // Inicia o driver de acesso ao banco.
		try{ Class.forName("org.sqlite.JDBC"); }
		catch(Exception e) { System.out.println("ERROR: LiteDataBase loader: " + e); }
	}
	
	private File file;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private String query;
	
	LiteDataBase(){};
	LiteDataBase(String dbName){
		
		this.file = new File (dbName);
		
		if(file.exists()) {
			System.out.print("This database name already exists\n");
			
			
			try { // Tenta conectar no arquivo existente
				this.connection = DriverManager.getConnection("jdbc:sqlite:"+dbName);
				this.statement = connection.createStatement();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		} else {
			System.out.print("This database name do not exists\n");
			
			try { // Prepara a conexão para o banco ser criado.
				this.connection = DriverManager.getConnection("jdbc:sqlite:"+dbName);
				this.statement = connection.createStatement();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
			
			// Cria um banco de dados novo
			createDB();
		}
	}
	private void createDB() {
		String query;
		
		try { // Cria a tabela de usuários
			query = "CREATE TABLE UsersTable " +
						"(userName VARCHAR(255) PRIMARY KEY NOT NULL," +
						" userPasswd VARCHAR(255) NOT NULL," +
						" isAdmin BOOLEAN NOT NULL)"; 
			statement.executeUpdate(query);
		}catch(SQLException sqlex) { System.out.println("ERROR: Unable do create table UsersTable"+ sqlex) ; }
			
		try { // Cria a tabela de recursos
			query = "CREATE TABLE ResourcesTable " +
						"(resourceName VARCHAR(255) PRIMARY KEY NOT NULL," +
						" resourceDescr VARCHAR(255))" ;
			statement.executeUpdate(query);
		}catch(SQLException sqlex) { System.out.println("ERROR: Unable do create table ResourcesTable"+ sqlex) ; }
			
		try { // Cria a tabela de agendamentos
			query = "CREATE TABLE AllocationsTable " +
						"(ID INT PRIMARY KEY NOT NULL," +
						" userName VARCHAR(255) NOT NULL," +
						" resourceName VARCHAR(255) NOT NULL," +
						" timeSlot INT NOT NULL," +
						" dateDay INT NOT NULL," +
						" dateMonth INT NOT NULL," +
						" dateYear INT NOT NULL," +
						" confirmed BOOLEAN NOT NULL)"; 
			statement.executeUpdate(query);
		}catch(SQLException sqlex) { System.out.println("ERROR: Unable do create table AllocationsTable"+ sqlex) ; }
	
		try { // Cria o administrador.
		
		// Seta a senha padrão do administrador
		// adminPasswd = AdlerChecksum.getAdler(adminPasswd);
		// String adminPasswd = "luis" ;
		
		query = "INSERT INTO UsersTable(userName, userPasswd, isAdmin) VALUES ('admin','admin','1')";
		statement.executeUpdate(query);
		} catch(SQLException sqlex){ System.out.println("ERROR: Unable do create admin user "+ sqlex) ; }
	}
}
