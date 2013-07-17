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


class DateClass {
	
	static String getValidDate(int day, int month, int year){
		
		Calendar calendar = new GregorianCalendar();
		
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.YEAR, year);
		
		return calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
	}
	
	static String getValidDate(String beforeDay, int day, String beforeMonth, int month, String beforeYear, int year, String endStr){
		
		Calendar calendar = new GregorianCalendar();
		
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.YEAR, year);
		
		return beforeDay + calendar.get(Calendar.DAY_OF_MONTH) + beforeMonth + (calendar.get(Calendar.MONTH)+1) + beforeYear + calendar.get(Calendar.YEAR) + endStr;
	}
	
	
	static String getValidQuery(int day, int month, int year){
	
		Calendar calendar = new GregorianCalendar();
		
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.YEAR, year);
		
		return "dateDay='" + calendar.get(Calendar.DAY_OF_MONTH) + "' AND dateMonth='" + (calendar.get(Calendar.MONTH)+1) + "' AND dateYear='" + calendar.get(Calendar.YEAR) + "'";
	}
	
	
	
// 	get Day Of Week
	static String getDOW(int day, int month, int year){
	
		Calendar calendar = new GregorianCalendar();
		
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.YEAR, year);
		
		switch(calendar.get(Calendar.DAY_OF_WEEK)){
			case Calendar.MONDAY 	: return "Mon";
			case Calendar.TUESDAY 	: return "Tue";
			case Calendar.WEDNESDAY	: return "Wed";
			case Calendar.THURSDAY	: return "Thu";
			case Calendar.FRIDAY	: return "Fri";
			case Calendar.SATURDAY	: return "Sat";
			case Calendar.SUNDAY	: return "Sun";
			default			: return null;
		}
	}
	
}

