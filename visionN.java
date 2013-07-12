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
		
	
	User user = new User();
	
	new UserPassDialog(db,user);
	}
}


class UserPassDialog extends JDialog {

	JTextField userName;
	JPasswordField passwd;
	JButton confirm;
	JLabel resultLabel;
	LiteDataBase db;
	User user;
	
	UserPassDialog(LiteDataBase db, User user){
		super((Frame)null, "Log in - User and Password please.", true);
		
		this.db = db;
		this.user=user;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setLayout(new FlowLayout());
		
		userName = new JTextField(10);
		passwd = new JPasswordField(10);
		confirm = new JButton("Log in");
		resultLabel = new JLabel();
		
		confirm.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(authorizeCheck( userName.getText(), new String(passwd.getPassword()) ) ){
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
	
	boolean authorizeCheck(String nome, String senha){
		
		user = db.findUser(nome);
		
		// Se for encontrado um usuário com esse nome
		if (user != null){
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




