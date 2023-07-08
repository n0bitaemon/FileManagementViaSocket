package filemanager.com.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;

import filemanager.com.server.common.Environments;

public class SQLConnector {
	private SQLConnector() {}

	/**
	 * Connect to database
	 * @return Connection
	 */
	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.Driver");
			return DriverManager.getConnection(Environments.JDBC_URL, Environments.JDBC_USR, Environments.JDBC_PWD);
		} catch (Exception ex) {
			System.out.println("connect failure!");
			ex.printStackTrace();
		}
		return null;
	}
}