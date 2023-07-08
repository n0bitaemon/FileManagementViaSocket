package filemanager.com.server.common;

import org.jasypt.properties.EncryptableProperties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Environments {
	
	public static final boolean DEBUG_MODE = true;

	public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String JDBC_URL = "jdbc:mysql://localhost:3306/account";
	public static final String JDBC_USR = "root";
	public static final String JDBC_PWD = "@Cuong3009@!#";
	
	private Environments() {
		}
	
	/*StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setPassword("some password"); 
	EncryptableProperties props = new EncryptableProperties(encryptor);
	props.load(new FileInputStream("C:\\Users\\Admin\\Desktop\\Properties File Pr1.txt"));

	JDBC_DRIVER = props.getProperty("driver");
	JDBC_URL = props.getProperty("url");		
	JDBC_USR = props.getProperty("username");
	JDBC_PWD = props.getProperty("password");*/
}
