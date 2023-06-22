package filemanager.com.server.auth;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import filemanager.com.server.common.Environments;

/**
 * @author n0bita-windows
 *
 */
public class Authentication {
	public static Dictionary<SocketAddress, String> session = new Hashtable<>();

	/**
	 * 
	 * @param username
	 * @param pass
	 */
	public static void addAccountToDatabase(String username, String pass) {
		String url = Environments.JDBC_URL; // information of the database
		String usernameforsql = Environments.JDBC_USR;
		String passforsql = Environments.JDBC_PWD;
		try {
			Class.forName(Environments.JDBC_DRIVER); // register the driver class

			Connection con = DriverManager.getConnection(url, usernameforsql, passforsql); // create connection with the
			Statement stat1 = con.createStatement(); // create sql statement

			if (findAccInDatabase(username)) {
				System.out.println("This username is already exist, please choose another one");
			} else {
				String query = "insert into account values(\"{0}\", \"{1}\")";
				String statement = MessageFormat.format(query, username, pass); // content of the sql
																				// statement
				stat1.executeUpdate(statement); // execute the statement
				System.out.println("Create new account success");
			}
			con.close(); // close the connection
		} catch (Exception e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param user
	 * @return
	 */
	public static boolean findAccInDatabase(String user) { // find account with given username
		String url = Environments.JDBC_URL; // information of the database
		String usernameforsql = Environments.JDBC_USR;
		String passforsql = Environments.JDBC_PWD;
		String u = null;
		Boolean found = false;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection con = DriverManager.getConnection(url, usernameforsql, passforsql);
			Statement stat = con.createStatement();

			String query = "select * from account where username = \"{0}\"";
			String statement = MessageFormat.format(query, user);

			ResultSet res = stat.executeQuery(statement); // get result from executing the statement

			while (res.next()) {
				u = res.getString("username");
				// System.out.println("found acc:" + u);
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
			Statement stat = con.createStatement();

			String query = "select * from account where username = \"{0}\"";
			String statement = MessageFormat.format(query, username);

			ResultSet res = stat.executeQuery(statement); // get result from executing the statement

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
		Enumeration<String> name = session.elements();

		while (name.hasMoreElements()) {
			String n = name.nextElement();
			System.out.println("USername: " + n);
			if (n.equals(username)) {
				return true;
			}
		}

		return false;

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

	public static void main(String[] args) {
		addAccountToDatabase("trung", "hello world");
	}

	public static boolean channelIsLoging(Object channel) {
		boolean res = false;

		if (session.get(channel) != null) {
			res = true;
		}

		return res;
	}

}
