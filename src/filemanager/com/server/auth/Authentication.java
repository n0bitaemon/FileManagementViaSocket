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

public class Authentication {
	private Authentication() {}
	
	private static final Logger LOGGER = LogManager.getLogger(Authentication.class);

	public static Dictionary<SocketAddress, String> session = new Hashtable<>();

	public static void addAccountToDatabase(String username, String pass) throws SQLException {
		try(Connection conn = SQLConnector.getConnection()){
			String query = "insert into account values(?, ?)";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setString(1, username);
			stat.setString(2, pass);
			
			stat.executeUpdate();
			LOGGER.info("Created username {}", username);
		}
	}

	// find account with given username
	public static boolean isUsernameInDb(String username) throws SQLException {
		try(Connection conn = SQLConnector.getConnection()){
			String dbUsername = null;
			boolean isUsernameInDb = false;
			
			String query = "select * from account where username =?";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setString(1,  username);

			ResultSet res = stat.executeQuery(); // get result from executing the statement

			while (res.next()) {
				dbUsername = res.getString("username");
			}
			
			if (dbUsername != null) {
				isUsernameInDb = true;
			}

			return isUsernameInDb;
		}
	}

	public static boolean isValidPassword(String username, String pass) throws SQLException {
		try(Connection conn = SQLConnector.getConnection()){
			String dbPass = null;
			boolean isValidPass = false;
			
			String query = "select * from account where username =?";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setString(1, username);
			
			ResultSet res = stat.executeQuery(); // get result from executing the statement

			while (res.next()) {
				dbPass = res.getString("password");
			}
			
			if (pass.equals(dbPass)) {
				isValidPass = true;
			}
			return isValidPass;
		}
	}

	public static boolean accIsLoging(String username) {
		if(username == null) {
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

	public static boolean channelIsLoging(SocketAddress channel) {
		boolean res = false;

		if (session.get(channel) != null) {
			res = true;
		}

		return res;
	}
}