package filemanager.com.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class SQLConnector {
	private SQLConnector() {}

	/**
	 * Connect to database
	 * @return Connection
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Dotenv dotenv = Dotenv.load();
		
		Class.forName(dotenv.get("JDBC_DRIVER"));
		return DriverManager.getConnection(dotenv.get("JDBC_URL"), 
				dotenv.get("JDBC_USR"), dotenv.get("JDBC_PWD"));
	}
}