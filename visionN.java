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

public class visionN {
	public static void main(String args []) {
		LiteDataBase db = new LiteDataBase("ResourceManager.sqlite3");
		
		UserPassDialog entrada = new UserPassDialog(db);
		System.out.println("UserAuth: "+ entrada.whichUserAuth().getUserName() +"\n");
		
		new MainFrame(db, entrada.whichUserAuth());
		
	}
}

class UserPassDialog extends JDialog {

	JTextField userName;
	JPasswordField passwd;
	JButton confirm;
	JLabel resultLabel;
	User user;
	LiteDataBase db;
	
	UserPassDialog(LiteDataBase db){
		super((Frame)null, "Log in - User and Password please.", true);
		
		this.db = db;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setLayout(new FlowLayout());
		
		userName = new JTextField(10);
		passwd = new JPasswordField(10);
		confirm = new JButton("Log in");
		resultLabel = new JLabel();
		
		confirm.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(authorizeCheck( userName.getText(), new String(passwd.getPassword())) ){
						dispose();
					}
					else{
						resultLabel.setText("Access Denied");
					}
				}
			}
		);
		
		
		add(userName);
		add(passwd);
		add(confirm);
		add(resultLabel);
		
		setSize(300, 200);
		setVisible(true);
	}
	
	User whichUserAuth(){
		return this.user;
	}
	
	boolean authorizeCheck(String nome, String senha){
		
		this.user = db.findUser(nome);
		
		// Se for encontrado um usuário com esse nome
		if (this.user != null){
		System.out.println("authorizeCheck: Usuário encontrado!\n");
			// Checamos se a senha está certa:
			if  (user.getUserPasswd().equals(senha) ) {
				System.out.println("authorizeCheck: Senha correta!\n");
				return true;
			} else
				System.out.println("authorizeCheck: Senha errada!\n");
		} else 
			System.out.println("authorizeCheck: Usuário não encontrado!\n");
		
	// Não foi possivel autenticar
	return false;
	}
}

class MainFrame extends JFrame {
	
	JTabbedPane tabbedPane;
	
// 	----------------------------------------------------
	JPanel usersPanel;
	
	JLabel userLabel, passwdLabel;
	JTextField userTextField, passwdTextField;
	JCheckBox adminCheckBox;
	JButton registerButton;
	
	JComboBox userComboBox;
	JButton userDeleteButton;
// 	----------------------------------------------------
	JPanel resourcesPanel;
	
	JLabel nameLabel;
	JTextField resourceTextField;
	JButton addButton;
	
	JComboBox resourceComboBox;
	JButton resourceDeleteButton;
// 	----------------------------------------------------
	JPanel allocationsPanel;
	
	JLabel allocationResourceLabel;
	JComboBox allocationResourceComboBox;
	JXDatePicker datePicker;
	JTable allocationTable;
	
	LiteDataBase db;
	User user;
	
	
	MainFrame(LiteDataBase db, User user){
		
		super("Resources Manager - User: " + user.getUserName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		
		this.db = db;
		this.user = user;
		
		tabbedPane = new JTabbedPane();
		add(tabbedPane);
		
		if( user.isAdmin()){
			usersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			resourcesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}		
		allocationsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		
		if( user.isAdmin() ){
			tabbedPane.add("Users", usersPanel);
			tabbedPane.add("Resources", resourcesPanel);
		}
		tabbedPane.add("Allocations", allocationsPanel);

		
		if(user.isAdmin()){
			tabbedPane.setToolTipTextAt(0, "Register new users");
			tabbedPane.setToolTipTextAt(1, "Register new resources");
			tabbedPane.setToolTipTextAt(2, "Make allocations");
		}
		else{
			tabbedPane.setToolTipTextAt(0, "Make allocations");
		}
		
		

		if(user.isAdmin()){

// 			Users Panel components ------------------------------------------------
		
			userTextField = new JTextField(15);
			passwdTextField = new JTextField(15);
			registerButton = new JButton("Register");
			userLabel = new JLabel("User Name:");
			passwdLabel = new JLabel("Password:");
			adminCheckBox = new JCheckBox("Admin");
			userComboBox = new JComboBox();
			userDeleteButton = new JButton("Delete");
			
			usersPanel.add(userLabel);
			usersPanel.add(userTextField);
			usersPanel.add(passwdLabel);
			usersPanel.add(passwdTextField);
			usersPanel.add(adminCheckBox);
			usersPanel.add(registerButton);
		
			usersPanel.add(userComboBox);
			usersPanel.add(userDeleteButton);
			
	// 		Resources Panel components --------------------------------------------

			nameLabel = new JLabel("Resource:");
			resourceTextField = new JTextField(15);
			addButton = new JButton("Add");
			
			resourceComboBox = new JComboBox();
			resourceDeleteButton = new JButton("Delete");
			
			resourcesPanel.add(nameLabel);
			resourcesPanel.add(resourceTextField);
			resourcesPanel.add(addButton);
			resourcesPanel.add(resourceComboBox);
			resourcesPanel.add(resourceDeleteButton);
		
		}
		
// 		Allocation Panel components -------------------------------------------

		allocationResourceLabel = new JLabel("Resource:");
		(allocationResourceComboBox = new JComboBox()).setFocusable(false);
		(datePicker = new JXDatePicker(new java.util.Date())).setFormats("dd/MM/yyyy");
		
		
		allocationTable = new JTable(
			new DefaultTableModel(10, 8){
				public boolean isCellEditable(int row, int column){
					return false;
				}
			}
		);
		
		allocationTable.setFocusable(false);
		allocationTable.setRowSelectionAllowed(false);
		
		allocationsPanel.add(allocationResourceLabel);
		allocationsPanel.add(allocationResourceComboBox);
		allocationsPanel.add(datePicker);
		allocationsPanel.add(allocationTable);
		
		
// 		=============================================================
//		usersPanel events +++++++++++++++++++++++++++++++++++++++++++
		if(user.isAdmin()){


		
			usersPanel.addAncestorListener(
				new AncestorListener(){
// 					event activated when the source (or any ancestor) is set to be Visible
					public void ancestorAdded(AncestorEvent event){
						reloadUsersPanel();
					}
					public void ancestorMoved(AncestorEvent event){}
					public void ancestorRemoved(AncestorEvent event){}
				}
			);
			
			registerButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						registerNewUser();
					}
				}
			);
			
			userDeleteButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						deleteExistingUser();
					}
				}
			);
			
// 			resourcesPanel events ++++++++++++++++++++++++++++++++++++++++++++

			resourcesPanel.addAncestorListener(
				new AncestorListener(){
// 					event activated when the source (or any ancestor) is set to be Visible
					public void ancestorAdded(AncestorEvent event){
						reloadResourcesPanel();
					}
					public void ancestorMoved(AncestorEvent event){}
					public void ancestorRemoved(AncestorEvent event){}
				}
			);			
			
			
			addButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						addNewResource();
					}
				}
			);
			
			resourceDeleteButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						deleteExistingResource();
					}
				}
			);
			
		}
		
// 		allocationsPanel events ++++++++++++++++++++++++++++++++++++++++++++
		
		allocationsPanel.addAncestorListener(
			new AncestorListener(){
// 				event activated when the source (or any ancestor) is set to be Visible
				public void ancestorAdded(AncestorEvent event){
					reloadAllocationsPanel();
				}
				public void ancestorMoved(AncestorEvent event){}
				public void ancestorRemoved(AncestorEvent event){}
			}
		);	
		
		allocationTable.addMouseListener(
			new MouseInputAdapter(){
				public void mouseClicked(MouseEvent e){
					String strAt;
					int row = allocationTable.rowAtPoint(e.getPoint());
					int column = allocationTable.columnAtPoint(e.getPoint());

					strAt = (String)allocationTable.getValueAt(row, column);
					
					allocationTableCellClicked(row, column, strAt);
				}
			}
		);

		datePicker.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					updateAllocationTable();
				}
			}
		);
		
		
// 		allocationResourceComboBox.addActionListener(
// 			new ActionListener(){
// 				public void actionPerformed(ActionEvent e){
// 					if(allocationResourceComboBox.getItemCount() > 0){
// 						JOptionPane.showMessageDialog(MainFrame.this, "Action!");
// 						Porque nao esta adicionando os outros items???????
// 						updateAllocationTable();

// 					}
// 				}
// 			}
// 		);
		
		
		
		
		setVisible(true);
	}

	void reloadUsersPanel(){
		
// 		update combo box
		userComboBox.removeAllItems();
		db.query("SELECT userName FROM UsersTable WHERE userName <> '"+ user.getUserName()+"'");
		while(db.next())
			userComboBox.addItem(db.getString("userName"));
	}
	
	void registerNewUser(){
		
		String newUserName;
		String newPasswd;
		boolean isAdminSet;
		boolean goThrough;
		
		goThrough = true;
		
		newUserName = userTextField.getText();
		newPasswd = passwdTextField.getText();
		
		if(newUserName.equals("")){
			JOptionPane.showMessageDialog(null, "Invalid value for User.");
			goThrough = false;
		}
		else if(newPasswd.equals("")){
			JOptionPane.showMessageDialog(null, "Invalid value for Password.");
			goThrough = false;
		}
		
		if(goThrough){
			if(adminCheckBox.isSelected())
				isAdminSet = true;
			else
				isAdminSet = false;
			
			User newUser = new User(newUserName, newPasswd, isAdminSet);
			db.saveUser(newUser);
			
			reloadUsersPanel();
			
			userTextField.setText("");
			passwdTextField.setText("");
		}
	}
	
	void deleteExistingUser(){
		String toBeDeletedUser;
		
		toBeDeletedUser = (String)userComboBox.getSelectedItem();
		db.deleteUser(db.findUser(toBeDeletedUser));
		reloadUsersPanel();
	}
	
	void reloadResourcesPanel(){
// 		update combo box
		resourceComboBox.removeAllItems();
		db.query("SELECT resourceName FROM ResourcesTable");
		while(db.next())
			resourceComboBox.addItem(db.getString("resourceName"));
	}
	
	void addNewResource(){
	
		String newResourceName;
		boolean goThrough;
		
		goThrough = true;
		
		newResourceName = resourceTextField.getText();

		if(newResourceName.equals("")){
			JOptionPane.showMessageDialog(null, "Invalid value for Resource.");
			goThrough = false;
		} else {
			Resource newResouce = new Resource(newResourceName);
			db.saveResource(newResouce);
		}
		
	reloadResourcesPanel();
	resourceTextField.setText("");
	}
	
	void deleteExistingResource(){
		String toBeDeletedResource;
		
		toBeDeletedResource = (String)resourceComboBox.getSelectedItem();
		db.deleteResource(db.findResource(toBeDeletedResource));
		reloadResourcesPanel();
	}
	
	void reloadAllocationsPanel() {
		
		String str;
		int i;
		
		allocationResourceComboBox.removeAllItems();
		db.query("SELECT resourceName FROM ResourcesTable");
		
		i=0;
		
		while(db.next()){
			allocationResourceComboBox.addItem(str = db.getString("resourceName"));
			System.out.println("addItem(" + str + ")" );
			i++;
		}
		
		updateAllocationTable();
	}
	
	void allocationTableCellClicked(int row, int column, String strAt){
		
// 		JOptionPane.showMessageDialog(this, "Cell ("+ row +","+ column +") was clicked.");
		
		if( (row>=2) && (column>=1) ){
			
// 			column 1 => (day, month, year)
// 			column 2 => (day+1, month, year)
// 			column 3 => (day+2, month, year)...
// 			...
// 			column j => (day+j-1, month, year)
	
// 			row 2 => slot 0
// 			row 3 => slot 1
// 			row 4 => slot 2
// 			...
// 			row i => slot i - 2
			
			
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(datePicker.getDate());

			int refDay = calendar.get(Calendar.DAY_OF_MONTH);
			int refMonth = calendar.get(Calendar.MONTH)+1;
			int refYear = calendar.get(Calendar.YEAR);	
			
			
			if(strAt.equals("") || strAt == null){
				
				
				db.noResultsQuery("INSERT INTO AllocationsTable(userName, resourceName, timeSlot, dateDay, dateMonth, dateYear) VALUES ('"+user.getUserName()+"', '"+allocationResourceComboBox.getSelectedItem()+"', '"+(row-2) + DateClass.getValidDate("', '", (refDay+column-1), "', '", refMonth, "', '", refYear, "')") );
				
				
				updateAllocationTable();
			}
			else if(strAt.equals(user.getUserName())){
			
			
	// 			remove request
				db.noResultsQuery("DELETE FROM AllocationsTable WHERE userName='"+user.getUserName()+"'  AND resourceName='"+allocationResourceComboBox.getSelectedItem()+"' AND timeSlot='"+(row-2)+"' "+ 	DateClass.getValidDate("AND dateDay='",(refDay+column-1),"' AND dateMonth='",refMonth,"' AND dateYear='",refYear,"'") );
				
				

				updateAllocationTable();
			}
		}
	}
	
	
	void dateChose(int newDay, int newMonth, int newYear){
		
		
		for(int i=0; i<10; i++){
			for(int j=0; j<8; j++){
				allocationTable.setValueAt("", i, j);
			}
		}
		
		allocationTable.setValueAt("07:30", 2, 0);
		allocationTable.setValueAt("09:30", 3, 0);
		allocationTable.setValueAt("11:30", 4, 0);
		allocationTable.setValueAt("13:30", 5, 0);
		allocationTable.setValueAt("15:30", 6, 0);
		allocationTable.setValueAt("17:30", 7, 0);
		allocationTable.setValueAt("19:30", 8, 0);
		allocationTable.setValueAt("21:30", 9, 0);
		
		
		for(int i=0; i<7; i++){
			allocationTable.setValueAt(DateClass.getDOW(newDay+i, newMonth, newYear), 0, i+1);
			allocationTable.setValueAt(DateClass.getValidDate(newDay+i, newMonth, newYear), 1, i+1);
		}

		for(int slot=0; slot<9; slot++){
			for(int i=0; i<7; i++){
				db.query("SELECT userName FROM AllocationsTable WHERE timeSlot='"+ slot +"' AND " + DateClass.getValidQuery(newDay+i, newMonth, newYear) + " AND resourceName='" + allocationResourceComboBox.getSelectedItem() + "'");
				
				while(db.next()){
					allocationTable.setValueAt(db.getString("userName"), slot+2, i+1);
				}
			}
		}
		
	}
	
		
	void updateAllocationTable(){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(datePicker.getDate());

		dateChose(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));	
		
	}
	
	
}
