package filemanager.com.server.sql;
 
import java.sql.Connection;
import java.sql.DriverManager;

import filemanager.com.server.common.Environments;
 
public class SQLConnector {
    private static String DB_URL = Environments.JDBC_URL;
    private static String USER_NAME = Environments.JDBC_USR;
    private static String PASSWORD = Environments.JDBC_PWD;
 
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.Driver");
            conn = DriverManager.getConnection(SQLConnector.DB_URL, SQLConnector.USER_NAME, SQLConnector.PASSWORD);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
        }
        return conn;
    }
}