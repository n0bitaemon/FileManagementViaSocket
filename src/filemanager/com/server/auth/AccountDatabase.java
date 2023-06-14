package filemanager.com.server.auth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import filemanager.com.server.sql.SQLConnector;

public class AccountDatabase {
	public static void main(String[] args) {
		try {
			Account account = AccountDatabase.select("n0bita", "trietsuper");
			System.out.println(account.getUsername());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Account select(String username, String password) throws SQLException {
		Connection conn = SQLConnector.getConnection();
		Statement stat = conn.createStatement();
		
		String queryString = "select * from account where username=\"{0}\" and password=\"{1}\"";
		String query = MessageFormat.format(queryString, username);
		
		ResultSet res = stat.executeQuery(query);

		Account account = null;
		while(res.next()) {
			account = new Account();
			account.setUsername(res.getString("username"));
			account.setPassword(res.getString("password"));
		}
		
		return account;
	}
	
	public static boolean checkUserExist(String username) {
		return false;
	}
	
	public static void insert(String username, String password) {
		
	}
	
	public static void update(String password) {
		
	}
	
	public static void delete(String username) {
		
	}
	
}
