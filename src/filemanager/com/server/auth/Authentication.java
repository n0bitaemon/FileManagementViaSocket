package filemanager.com.server.auth;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.common.Environments;

public class Authentication {
	private static final Logger LOGGER = LogManager.getLogger(Authentication.class);

	public static Dictionary<SocketAddress, String> session = new Hashtable<>();

	public static void addAccountToDatabase(String username, String pass) {
		String url = Environments.JDBC_URL; // information of the database
		String usernameforsql = Environments.JDBC_USR	;
		String passforsql = Environments.JDBC_PWD;
		try {
			Class.forName(Environments.JDBC_DRIVER); // register the driver class

			Connection con = DriverManager.getConnection(url, usernameforsql, passforsql); // create connection with the

			String query = "insert into account values(?, ?)";
			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, username);
			stat.setString(2, pass);
			
			stat.executeUpdate(); // execute the statement
			LOGGER.info("Created username {}", username);
			con.close(); // close the connection
		} catch (Exception e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
		}
	}

	public static boolean findAccInDatabase(String username) { // find account with given username
		String url = Environments.JDBC_URL; // information of the database
		String usernameforsql = Environments.JDBC_USR;
		String passforsql = Environments.JDBC_PWD;
		String u = null;
		Boolean found = false;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection con = DriverManager.getConnection(url, usernameforsql, passforsql);

			String query = "select * from account where username =?";
			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1,  username);

			ResultSet res = stat.executeQuery(); // get result from executing the statement

			while (res.next()) {
				u = res.getString("username");
			}
			con.close();
		} catch (Exception e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
		}

		if (u != null) {
			found = true;
		}

		return found;
	}

	public static boolean checkPass(String username, String pass) {
		String url = Environments.JDBC_URL; // information of the database
		String usernameforsql = Environments.JDBC_USR;
		String passforsql = Environments.JDBC_PWD;
		String p = null;
		Boolean check = false;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection con = DriverManager.getConnection(url, usernameforsql, passforsql);
			
			String query = "select * from account where username =?";
			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, username);
			
			ResultSet res = stat.executeQuery(); // get result from executing the statement

			while (res.next()) {
				p = res.getString("password");
			}
			con.close();
		} catch (Exception e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
		}
		if (pass.equals(p)) {
			check = true;
		}
		return check;
	}

	public static boolean accIsLoging(String username) {
		/*
		 * Enumeration<Object> keys = Authentication.loging.keys();
		 * 
		 * while (keys.hasMoreElements()) { Object k = keys.nextElement();
		 * System.out.println("key: " + k + ", value: " + Authentication.loging.get(k));
		 * }
		 */
		
		
		Boolean res = false;
		Enumeration<String> name = session.elements();

		while (name.hasMoreElements()) {
			Object n = name.nextElement();
			if (n.equals(username)) {
				res = true;
			}
		}

		return res;

	}

	// Check for status of an account
	public static Object accOfChannel(String username) {
		Object channel = null;
		Enumeration<SocketAddress> keys = session.keys();

		while (keys.hasMoreElements()) {
			Object k = keys.nextElement();
			if (session.get(k).equals(username)) {
				channel = k;
			}
		}
		return channel;
	}

	public static boolean channelIsLoging(SocketAddress channel) {
		boolean res = false;

		if (session.get(channel) != null) {
			res = true;
		}

		return res;
	}
}