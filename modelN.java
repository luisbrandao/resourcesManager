import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.Container.*;
import java.awt.event.*;
import java.util.*;
import java.util.zip.*;
import org.jdesktop.swingx.*;

class User {
	private String userName;
	private String userPasswd;
	private boolean admin;
	
	public void setUserName(String userName)		{ this.userName = userName; }
	public void setUserPasswd(String userPasswd)	{ this.userPasswd = userPasswd; }
	public void setAdmin(boolean admin)		 	{ this.admin = admin; }
	
	public String getUserName()	{ return this.userName; }
	public String getUserPasswd()	{ return this.userPasswd; }
	public boolean isAdmin()		{ return this.admin; }
	
	public boolean checkPasswd (String senha) {
		// senha = AdlerChecksum.getAdler(senha);
		
		if (this.userPasswd == senha)
			return true;
		else
			return false;
	}
		
	// Construtores:
	User(){};
	User(String userName, String userPasswd){
		this.userName = userName;
		this.userPasswd = userPasswd; 
		this.admin = false ;
	}
	User(String userName, String userPasswd, boolean admin){
		this.userName = userName;
		this.userPasswd = userPasswd; 
		this.admin = admin;
	}
}

class Resource {
	private String resourceName;
	private String resourceDescr;
	
	public void setResourceName(String resourceName)	{ this.resourceName = resourceName; }
	public void setResourceDescr(String resourceDescr)	{ this.resourceDescr = resourceDescr; }
	public String getResourceName()	{ return this.resourceName; }
	public String getResourceDescr()	{ return this.resourceDescr; }
	
	// Construtores:
	Resource(){};
	Resource(String resourceName){
		this.resourceName = resourceName;
		this.resourceDescr = "";
	}
	Resource(String resourceName, String resourceDescr){
		this.resourceName = resourceName;
		this.resourceDescr = resourceDescr;
	}
}

class Allocation {
	private String userName;
	private String resourceName;
	private int timeSlot, dateDay, dateMonth, dateYear;
	
	public void setUserName(String userName)		{ this.userName = userName; }
	public void setResourceName(String resourceName){ this.resourceName = resourceName; }
	
	Allocation(){};
	Allocation(String userName, String resourceName, int timeSlot, int dateDay, int dateMonth, int dateYear ){
		this.userName = userName;
		this.resourceName = resourceName; 
		this.timeSlot = timeSlot;
		this.dateDay = dateDay;
		this.dateMonth = dateMonth;
		this.dateYear = dateYear;
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
			System.out.print("The database: "+dbName+" already exists!\nOpening....\n");
			
			
			try { // Tenta conectar no arquivo existente
				this.connection = DriverManager.getConnection("jdbc:sqlite:"+dbName);
				this.statement = connection.createStatement();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		} else {
			System.out.print("The database "+dbName+" do not exists\nCreating one...\n");
			
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
						"(Id INTEGER PRIMARY KEY NOT NULL," +
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

	public User findUser(String nome){
		try {
			query = "SELECT * FROM UsersTable WHERE userName='"+ nome +"'";
			resultSet = statement.executeQuery(query);
			
			if (resultSet.next()) {
				String userName = resultSet.getString("userName");
				String userPasswd = resultSet.getString("userPasswd");
				boolean admin = resultSet.getBoolean("isAdmin");
				
				User novo = new User(userName, userPasswd, admin);
				return novo;
			} else {
				System.out.println("ERROR: User: "+ nome +" dont exist!\n");
			}
		} catch(SQLException sqlex){ System.out.println("Unable to perform search the user: " + nome +"\n WHY: " + sqlex) ; }
	return null;
	}
	
	public Resource findResource(String nome){
		try {
			query = "SELECT * FROM ResourcesTable WHERE resourceName='"+ nome +"'";
			resultSet = statement.executeQuery(query);
			
			if (resultSet.next()) {
				String resourceName = resultSet.getString("resourceName");
				String resourceDescr = resultSet.getString("resourceDescr");
				
				Resource novo = new Resource(resourceName, resourceDescr);
				return novo;
			} else {
				System.out.println("ERROR: Resource: "+ nome +" dont exist!\n");
			}
		} catch(SQLException sqlex){ System.out.println("Unable to perform search the resource: " + nome +"\n WHY: " + sqlex) ; }
	return null;
	}

	public List UserList() {
		int i = 0;
		List list = new ArrayList();
		String userName = "";
		String userPasswd = "";
		boolean admin = false;
		
		try {
			query = "SELECT * FROM UsersTable";
			resultSet = statement.executeQuery(query);
			
			while (resultSet.next())
				userName = resultSet.getString("userName");
				userPasswd = resultSet.getString("userPasswd");
				admin = resultSet.getBoolean("isAdmin");
				
				list.add(new User(userName, userPasswd, admin));
				i++;
		} catch(SQLException sqlex){ System.out.println("Unable to list the users" + sqlex) ; }
	return list;
	}
	
	public void saveUser(User usuario) {
		
		// Testa a existencia do usuariao no banco:
		if ( (this.findUser( usuario.getUserName())) != null ) {
			try { // Iremos updatear um usuário:
				query = "UPDATE UsersTable SET " +
						" userPasswd='"+ usuario.getUserPasswd() +"'"+
						" isAdmin='"+ usuario.isAdmin() +"'" +
						" WHERE userName='"+ usuario.getUserName() + "'" ;
				statement.executeUpdate(query);
			} catch(SQLException sqlex){ System.out.println("ERROR: Unable to update user: "+ usuario.getUserName()+"\n ERROR: "+ sqlex) ; }
		} else {
			try { // Adicionar um novo usuário
				query = "INSERT INTO UsersTable " +
						" (userName,userPasswd,isAdmin)" +
						" VALUES ('"+usuario.getUserName()+"', '"+usuario.getUserPasswd()+"', '"+usuario.isAdmin()+"')" ;
				statement.executeUpdate(query);
			} catch(SQLException sqlex){ System.out.println("ERROR: Unable to Insert user: "+ usuario.getUserName()+"\n ERROR: "+ sqlex) ; }
		}
	}
	
	public void saveResource(Resource recurso) {
		
		// Testa a existencia do usuariao no banco:
		if ( (this.findResource( recurso.getResourceName() )) != null ) {
			try { // Iremos updatear um Recurso:
				query = "UPDATE ResourcesTable SET " +
						" resourceDescr='"+ recurso.getResourceDescr() +"'"+
						" WHERE resourceName='"+ recurso.getResourceName() + "'" ;
				statement.executeUpdate(query);
			} catch(SQLException sqlex){ System.out.println("ERROR: Unable to update Resource: "+ recurso.getResourceName()+"\n ERROR: "+ sqlex) ; }
		} else {
			try { // Adicionar um novo usuário
				query = "INSERT INTO ResourcesTable " +
						" (resourceName,resourceDescr)" +
						" VALUES ('"+recurso.getResourceName()+"', '"+recurso.getResourceDescr()+"')" ;
				statement.executeUpdate(query);
			} catch(SQLException sqlex){ System.out.println("ERROR: Unable to insert Resource: "+ recurso.getResourceName()+"\n ERROR: "+ sqlex) ; }
		}
	}
	
	public void deleteUser(User usuario) {
		
		// Testa a existencia do usuariao no banco:
		if ( (this.findUser( usuario.getUserName())) != null ) {
			try { // Iremos updatear um usuário:
				query = "DELETE FROM UsersTable " +
						" WHERE userName='"+ usuario.getUserName() + "'" ;
				statement.executeUpdate(query);
			} catch(SQLException sqlex){ System.out.println("ERROR: Unable to remove user: "+ usuario.getUserName()+"\nERROR: "+ sqlex) ; }
		}
		System.out.println("Deleted user: "+ usuario.getUserName()+"\n");
	}
	
	public void deleteResource(Resource recurso) {
		
		// Testa a existencia do recurso no banco:
		if ( (this.findResource( recurso.getResourceName())) != null ) {
			try { // Iremos updatear um usuário:
				query = "DELETE FROM ResourcesTable " +
						" WHERE resourceName='"+ recurso.getResourceName() + "'" ;
				statement.executeUpdate(query);
			} catch(SQLException sqlex){ System.out.println("ERROR: Unable to remove resource: "+ recurso.getResourceName()+"\nERROR: "+ sqlex) ; }
		}
		System.out.println("Deleted resource: "+ recurso.getResourceName()+"\n");
	}
	
	void query(String query) {
	
// 		executes given SQL statement
// 		executeQuery never returns null
		try{
			System.out.println(query);
			this.resultSet = statement.executeQuery(query);
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
