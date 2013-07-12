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
		
		
		db.findUser("admin");
		db.findUser("Luis");
	}
}
