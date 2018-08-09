import java.util.*;
public class LDB_EncryptAccounts extends LinkedDatabaseFramework{

	protected static byte[] encrptedDBUsername;
	protected static byte[] decrptedDBUsername;
	
	protected static byte[] encrptedDBPassword;
	protected static byte[] decrptedDBPassword;
	
	public LDB_EncryptAccounts() {
		
	}
	
	protected static String encryptUsername(String DBUsername) {
		encrptedDBUsername = Base64.getEncoder().encode(DBUsername.getBytes());
		return new String(encrptedDBUsername);
	}
	
	protected static String decryptUsername(String encrptedDBUsername) {
		decrptedDBUsername = Base64.getDecoder().decode(encrptedDBUsername);
		return new String(decrptedDBUsername);
	}
	
	protected static String encryptPassword(String DBPassword) {
		encrptedDBPassword = Base64.getEncoder().encode(DBPassword.getBytes());
		return new String(encrptedDBPassword);
	}
	
	protected static String decryptPassword(String encrptedDBPassword) {
		decrptedDBPassword = Base64.getDecoder().decode(encrptedDBPassword);
		return new String(decrptedDBPassword);
	}
}