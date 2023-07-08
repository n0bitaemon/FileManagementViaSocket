package filemanager.com.server.auth;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.sql.SQLConnector;

/**
 * this class contain the methods used in log in, log out and register command, include:
 * methods work with database (store information of user's account)
 * methods work ưith session (store the log in status)
 *  
 * @author Cuong
 *
 */
public class Authentication {

	private static final Logger LOGGER = LogManager.getLogger(Authentication.class);

	public static final Dictionary<SocketAddress, String> session = new Hashtable<>();

	/**
	 * 
	 * insert username and password of new account to the database
	 * 
	 * @param username
	 * @param pass
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public static void addAccountToDatabase(String username, String pass) throws SQLException, ClassNotFoundException {
		try (Connection conn = SQLConnector.getConnection()) {
			String query = "insert into account values(?, ?)";
			try (PreparedStatement stat = conn.prepareStatement(query)){
				stat.setString(1, username);
				stat.setString(2, pass);

				stat.executeUpdate();
				LOGGER.info("Created username {}", username);
			}
		}
	}

	/**
	 * find account in database with given username
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public static boolean isUsernameInDb(String username) throws SQLException, ClassNotFoundException {
		try (Connection conn = SQLConnector.getConnection()) {
			String dbUsername = null;
			boolean isUsernameInDb = false;

			String query = "select * from account where username =?";
			try(PreparedStatement stat = conn.prepareStatement(query)){
				stat.setString(1, username);
	
				ResultSet res = stat.executeQuery();
	
				while (res.next()) {
					dbUsername = res.getString("username");
				}
	
				if (dbUsername != null) {
					isUsernameInDb = true;
				}
	
				return isUsernameInDb;
			}
		}
	}

	/**
	 * check if the password provided by user is match ưith the password in database
	 * given by username
	 * 
	 * @param username
	 * @param pass
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public static boolean isValidPassword(String username, String pass) throws SQLException, ClassNotFoundException {
		try (Connection conn = SQLConnector.getConnection()) {
			String dbPass = null;
			boolean isValidPass = false;

			String query = "select * from account where username =?";
			try(PreparedStatement stat = conn.prepareStatement(query)){
				stat.setString(1, username);

				ResultSet res = stat.executeQuery();

				while (res.next()) {
					dbPass = res.getString("password");
				}

				if (pass.equals(dbPass)) {
					isValidPass = true;
				}
				return isValidPass;
			}
			
		}
	}

	/**
	 * check if this account is being used in any address
	 * 
	 * @param username
	 * @return
	 */
	public static boolean accIsLoging(String username) {
		if (username == null) {
			return false;
		}

		boolean res = false;
		Enumeration<String> sessionUsers = session.elements();

		while (sessionUsers.hasMoreElements()) {
			String user = sessionUsers.nextElement();
			if (user.equals(username)) {
				res = true;
			}
		}
		return res;
	}

	/**
	 * find the address where an account is being used
	 * 
	 * @param username
	 * @return
	 */
	public static SocketAddress accOfChannel(String username) {
		SocketAddress channel = null;
		Enumeration<SocketAddress> sessionRemoteAddrs = session.keys();

		while (sessionRemoteAddrs.hasMoreElements()) {
			SocketAddress remoteAddr = sessionRemoteAddrs.nextElement();
			if (session.get(remoteAddr).equals(username)) {
				channel = remoteAddr;
			}
		}
		return channel;
	}

	/**
	 * check if there's an account being used in this channel
	 * 
	 * @param channel
	 * @return
	 */
	public static boolean channelIsLoging(SocketAddress channel) {
		boolean res = false;

		if (session.get(channel) != null) {
			res = true;
		}

		return res;
	}

	private Authentication() {
	}
}