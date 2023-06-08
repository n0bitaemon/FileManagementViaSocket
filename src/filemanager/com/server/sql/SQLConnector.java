package filemanager.com.server.sql;
 
import java.sql.Connection;
import java.sql.DriverManager;
 
public class SQLConnector {
    private static String DB_URL = "jdbc:mysql://localhost:3306/filemanager";
    private static String USER_NAME = "n0bita";
    private static String PASSWORD = "trietsuper";
 
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(SQLConnector.DB_URL, SQLConnector.USER_NAME, SQLConnector.PASSWORD);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
        }
        return conn;
    }
}