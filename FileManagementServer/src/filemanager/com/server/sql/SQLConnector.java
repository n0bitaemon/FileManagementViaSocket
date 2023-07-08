package filemanager.com.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import filemanager.com.server.common.Environments;

public class SQLConnector {
	private SQLConnector() {}

	/**
	 * Connect to database
	 * @return Connection
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.Driver");
		return DriverManager.getConnection(Environments.JDBC_URL, 
				Environments.JDBC_USR, Environments.JDBC_PWD);
	}
}