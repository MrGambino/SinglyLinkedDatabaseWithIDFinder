import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;
import com.sun.net.httpserver.HttpServer;

/**
 * Using Java WebSocket and Dynamic Web Editor, this server-socket is able to
 * auto-generate unique codes, find user IDs, and remove selected users from the
 * LinkedDatabase Framework (LDB Server-Side).
 * 
 * The Client-Side will send the user credential through an encrypted HTTPS
 * server and the LDB server will match the sent credential with the database
 * credential if they match the server will send back a "PASS" or let the user
 * login else, it will send a "FAIL" or credentials don't match alert.
 * 
 * The LDB database uses modern password hashing. When the user first makes a
 * password the server will automatically assign a random ID and salt (String).
 * Using the password and salt, the server will encrypt and only save the hash
 * of the password {ex. (SHA512(password + salt)).HASH -> DB}. This means even
 * if the server was hacked, the hacker will not be able to get the password
 * easily and they would have a hard time cracking the encrypted hash passwords.
 * 
 * @author Sirak Berhane
 * @version VERSION 1.1.1 REV 45
 */

public class LDBQueries extends LinkedDatabaseFramework{
	// Version INFO
	private static final String VERSION = "Version 1.1.1";
	
	// Login Credentials
	protected LinkedDatabase rootAccounts = new LinkedDatabase();
	protected LinkedDatabase usersAccounts = new LinkedDatabase();
	protected static String DBusername, newUsername, DBUsername_U, newUsername_U;
	protected static String DBpassword, newPassword, DBPassword_U, newPassword_U;
	protected static String newIDString = Integer.toString(LDB_SaveAndPopulateUniqueIDs.generateID());
	protected static String ID, tempID; 
	private static String cmdIDCreated;
	protected static File IDFile;
	
	/* 
	 User-name and Passwords are encrypted by BASE64 and then saved 
		* Current method is not safe as the BASE64 can be reversed 
		* Using HashMap is best way to store login authentication 
		* To safely store passwords in the LDBFrameworks use the following algorithm:
			-> User-name=[HASH_FUNCTION(SHA512(MD5(<User-name> + Salt)))] 
			-> Password=[HASH_FUNCTION(SHA512(MD5(<Password> + Salt)))] 	
	*/
	
	// Encryption Value-Holders (Base64 Encryption)
	private static String encryptSUPER_User, decryptSUPER_User;
	private static String encrypt_User, decrypt_User;
	private static String encryptSUPER_Pass; //decryptSUPER_Pass;
	private static String encrypt_Pass; //decrypt_Pass;
	public  static Integer $databaseSAVED = 0;
	private static Integer $numberOfTerminalRuns;
	private static String tempTR_SU;
	
	// For Reading/Writing Files on the database (SUPER and NON SUPER) tables 
	private static boolean userPassHasBeenDecoded = false;
	private static boolean userPassHasBeenDecoded_$ = false;
	
	// QueryClass-Only Variables
	private static int attemptToLoginFailed = 0;
	private static double loginFailedStartTime = 0;
	private static double loginFailedEndTime = 0;
	private static boolean failedToLogin = false;
	private static boolean uniqueIDFailedToAuth;

	// Command Variables
	protected static String command;
	protected static Scanner scanner, cmdType;

	// New Query Type
	protected static LDBQueries newQueryLDB; // Super User Only
	protected static LDBQueries newQueryLDB_U; // Non SU Only
	
	// Server OFF/ON flag
	protected static boolean forceServerStart = false;

	public LDBQueries(Object username, Object password, boolean superUser) {
		// Super-User
		if (superUser == true) {
			DBusername = (String) username;
			DBpassword = (String) password;
			rootAccounts.addItem(DBpassword, DBusername);
			rootAccounts.addItem(null, "Ignore");
		}

		// Non Super-User
		if (superUser == false) {
			DBUsername_U = (String) username;
			DBPassword_U = (String) password;
			usersAccounts.addItem(DBPassword_U, DBUsername_U);
			rootAccounts.addItem(null, "Ignore");
		}
	}
	
	protected static void hide(File src) throws IOException, InterruptedException {
		Process proc = Runtime.getRuntime().exec("attrib +h" + src.getPath());
		proc.waitFor();
	}
	
	private static void changeUsername(Object newUsername, boolean superUser) {
		if (superUser == true) {
			DBusername = (String) newUsername;
		}

		if (superUser == false) {
			DBUsername_U = (String) newUsername;
		}
	}

	private static void changePassword(Object newPassword, boolean superUser) {
		if (superUser == true) {
			DBpassword = (String) newPassword;
		}

		if (superUser == false) {
			DBPassword_U = (String) newPassword;
		}
	}
	
	public static boolean isFailedToLogin() {
		return failedToLogin;
	}

	public static void setFailedToLogin(boolean failedToLogin) {
		LDBQueries.failedToLogin = failedToLogin;
	}

	protected static void welcomeSetUp() throws IOException {
		// Check File for setup
		File configFile = new File("saveUpdatedLDB.txt");
		File configFile2 = new File("saveUpdatedLDB_U.txt");
		File configFile3 = new File("saveLDB_runs.txt");
		IDFile = new File("saveLDB_UniqueID.txt");
		if (configFile.exists() && configFile2.exists() && IDFile.exists() && configFile3.exists()) {
			/*--> (2)*/loginPrompt();
			forceServerStart =  true;
			LDB_Server.server = HttpServer.create();
			LDB_Server.server.createContext("/", new LDB_Server("LDB Framework/interface", false, false));
			LDB_Server.server.bind(new InetSocketAddress("localhost", LDB_Server.port), 100);
			System.out.println("\n" + LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ Server Connection Starting ....");
			LDB_Server.server.start();
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ http://localhost:" + LDB_Server.port + "/" + "\n");
		} else {
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Setup Wizard Starting Up ... ");	
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Setup Request --> ID# " + newIDString + "\n");
			LDB_SaveAndPopulateUniqueIDs.saveToTextFile("ID=" + newIDString, "LDB_UniqueID");
			$numberOfTerminalRuns = 0;
			LDB_SaveAndPopulateUniqueIDs.saveToTextFile("incr=" + $numberOfTerminalRuns, "LDB_runs");
			tempTR_SU = Integer.toString($numberOfTerminalRuns);
			IDFile.setReadOnly();
			processCommand("USERS -A newUser -$ -newSetupWizard -SU");
		}
		// If setup exists -> Skip Set-Up
		// Else -> Do the setup
		// Check these files each time the program runs --> ["saveUpdatedLDB.txt" && "saveUpdatedLDB_U.txt"]
		//		----> If they are deleted or unreadable make a new setup file!
	}

	private static void loginAuthentication(String Client_username, String Client_password) {
		if (Client_username.equals(DBusername) && Integer.toString(LDB_EncryptAccounts.encryptUsername(Client_password).hashCode()).equals(DBpassword)
				&& (loginFailedEndTime - System.currentTimeMillis()) * 60000 <= 0 && uniqueIDFailedToAuth == false) {
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Login Successful!\n");
			setFailedToLogin(false);
			attemptToLoginFailed = 0;
		}
		if (((loginFailedEndTime - loginFailedStartTime) * 60000 >= 15.0)) {
			System.out.println(
					LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Account is locked and there is "
							+ (loginFailedEndTime - System.currentTimeMillis()) / 60000 + " minutes remaining!");
		}
		if (!((Client_username.equals(DBusername) && Integer.toString(LDB_EncryptAccounts.encryptUsername(Client_password).hashCode()).equals(DBpassword)))) {
			setFailedToLogin(true);
			attemptToLoginFailed++;
			if (attemptToLoginFailed == 5 && (loginFailedEndTime - System.currentTimeMillis()) / 60000 <= 0) {
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
						+ " - Local:~ LDB_Framework$ Account is locked for 15 min!");
				loginFailedStartTime = System.currentTimeMillis();
				loginFailedEndTime = loginFailedStartTime + (15.0 * 60000.0);
			} else if (attemptToLoginFailed <= 5) {
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Login Failed! You have "
								+ attemptToLoginFailed + " out of 5 attempts left to login.");
			}
		}
	}

	private static void findIncr() throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner fileScan = new Scanner(new File("saveLDB_runs.txt"));
		int runNums = 0;
			do {
				String input = fileScan.nextLine();
				if (input.length() == 0) {
					runNums++;
				}
				if (input.startsWith("incr=")) {
					tempTR_SU = input.substring(5, input.length());
					$numberOfTerminalRuns = Integer.parseInt(tempTR_SU);
					runNums++;
				}	
			} while (runNums < 2);
		}
	
	private static void findID(boolean saveTo) throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner fileScan = new Scanner(new File("saveLDB_UniqueID.txt"));
		int runNums = 0;
		if (saveTo == true) {
			do {
				String input = fileScan.nextLine();
				if (input.length() == 0) {
					runNums++;
				}
				if (input.startsWith("ID=")) {
					tempID = input.substring(3, input.length());
					runNums++;
				}	
			} while (runNums < 2);
		}
		if (saveTo == false) {
			do {
				String input = fileScan.nextLine();
				if (input.length() == 0) {
					runNums++;
				}
				if (input.startsWith("ID=")) {
					ID = input.substring(3, input.length());
					runNums++;
				}
			} while (runNums < 2);
			IDFile.setExecutable(false, true);
			IDFile.setWritable(false);
		}
	}

	private static void findLoginAuth(boolean superUser) throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner fileScan = new Scanner(new File("saveUpdatedLDB.txt"));
		@SuppressWarnings("resource")
		Scanner fileScan2 = new Scanner(new File("saveUpdatedLDB_U.txt"));
		int runNums = 0;
		int runNums2 = 0;

		if (superUser == true) {
			do {
				String input = fileScan.nextLine();
				if (input.length() == 0) {
					runNums++;
				}
				if (input.startsWith("Username=")) {
					newUsername = input.substring(9, input.length());
					decrypt_User = new String (LDB_EncryptAccounts.decryptPassword(newUsername));
					changeUsername(decrypt_User, true);
					newUsername = decrypt_User;
					userPassHasBeenDecoded_$ = true;
					runNums++;
				}
				if (input.startsWith("Password=")) {
					newPassword = input.substring(9, input.length());
					//decrypt_Pass = new String (LDB_EncryptAccounts.decryptPassword(newPassword));
					//changePassword(decrypt_Pass, true);
					changePassword(newPassword, true);
					//newPassword = decrypt_Pass;
					userPassHasBeenDecoded_$ = true;
					runNums++;
				}
			} while (runNums < 3);
			newQueryLDB = new LDBQueries(newUsername, newPassword, true);
		}

		if (superUser == false) {
			do {
				String input2 = fileScan2.nextLine();
				if (input2.length() == 0) {
					runNums2++;
				}
				if (input2.startsWith("Username=")) {
					newUsername_U = input2.substring(9, input2.length());
					decryptSUPER_User = new String (LDB_EncryptAccounts.decryptPassword(newUsername_U));
					changeUsername(decryptSUPER_User, false);
					newUsername_U = decryptSUPER_User;
					userPassHasBeenDecoded = true;
					runNums2++;
				}
				if (input2.startsWith("Password=")) {
					newPassword_U = input2.substring(9, input2.length());
					//decryptSUPER_Pass = new String (LDB_EncryptAccounts.decryptPassword(newPassword_U));
					//changePassword(decryptSUPER_Pass, false);
					changePassword(newPassword_U , false);
					//newPassword_U = decryptSUPER_Pass;
					userPassHasBeenDecoded = true;
					runNums2++;
				}
			} while (runNums2 < 3);
			newQueryLDB_U = new LDBQueries(newUsername_U, newPassword_U, false);
		}		
	}

	private static void printLoginAuth(boolean superUser) throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner fileScan = new Scanner(new File("saveUpdatedLDB.txt"));
		@SuppressWarnings("resource")
		Scanner fileScan2 = new Scanner(new File("saveUpdatedLDB_U.txt"));
		int runNums = 0;
		int runNums2 = 0;
		if (superUser == true) {
			do {
				String input = fileScan.nextLine();
				if (input.length() == 0) {
					runNums++;
				}
				if (input.startsWith("Username=")) {
					newUsername = input.substring(9, input.length());
					System.out.println("Username=" + newUsername + "\n");
					runNums++;
				}
				if (input.startsWith("Password=")) {
					newPassword = input.substring(9, input.length());
					System.out.println("Password=" + newPassword + "\n");
					runNums++;
				}
			} while (runNums < 3);
		}

		if (superUser == false) {
			do {
				String input2 = fileScan2.nextLine();
				if (input2.length() == 0) {
					runNums2++;
				}
				if (input2.startsWith("Username=")) {
					newUsername_U = input2.substring(9, input2.length());
					System.out.print("Username=" + newUsername_U + "\n");
					runNums2++;
				}
				if (input2.startsWith("Password=")) {
					newPassword_U = input2.substring(9, input2.length());
					System.out.println("Password=" + newPassword_U + "\n");
					runNums2++;
				}
			} while (runNums2 < 3);
		}
	}
	
	private static void preLoginSetup(boolean superUser) throws FileNotFoundException, UnsupportedEncodingException {
		if (superUser == true) {
			try {
				findLoginAuth(true);
				System.out.println("");
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Loading loginAuth for database1 ...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[FAILED TO UPDATE LDB_ADMINS] -> ERROR " + e);
			}
		}

		if (superUser == false) {
			try {
				findLoginAuth(false);
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Loading loginAuth for database2 ...");
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
						+ " - [COMPLETE] Getting LDB Framework Ready ...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[FAILED TO UPDATE LDB_USERS] -> ERROR " + e);
			}

			try {
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
						+ " - Checking uniqueID exists for database1 & database2 ...");
				if (tempID.equals(ID)) {
					System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - [UNIQUE ID FOUND --> " + ID + "] ");
					System.out.println("\n" + LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
							+ " - (!) LinkedDataBase Framework Online - Local (!) \n\n");
				} else {
					System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - (Warning) [UNIQUE ID NOT FOUND] LDB Error ~ ID deleted or unreadable (Warning) |--> Check saveDEBUG.txt for more INFO (!) <--| ");
					LDB_SaveAndPopulateUniqueIDs.saveToTextFile(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - value saved in $tempID = " + tempID + " | " + "value saved in $ID = " + ID, "DEBUG");
					System.out.println("\n" + LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
							+ " - (!) LinkedDataBase Framework Offline (!) \n\n");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void loginPrompt() throws FileNotFoundException, UnsupportedEncodingException {
		findIncr();
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("\tLogin to LinkedDataBase Framework \t| \t" + VERSION +" - Session " + $numberOfTerminalRuns);
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("\t\tHint 1: Do not include spaces! "
				+ "\n\t\tHint 2: You only have 5 tries before it auto locks for 15 minutes!"
				+ "\n\t\tHint 3: Press <Enter> in the command prompt to exit program.");
		System.out.println("-------------------------------------------------------------------------------------");
		findID(true);
		findID(false);
		preLoginSetup(true); // SU Only
		preLoginSetup(false); // NON-SU Only
		defaultLoginPrompt();
		System.out.println("");
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("\tWelcome to LinkedDatabase \t| \t" + VERSION + " - Session " + $numberOfTerminalRuns);
		System.out.println("-------------------------------------------------------------------------------------");
		$numberOfTerminalRuns++;
		LDB_SaveAndPopulateUniqueIDs.saveToTextFile("incr=" + $numberOfTerminalRuns, "LDB_runs");
	}

	private static void defaultLoginPrompt() {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		String username;
		String password;
		do {
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Username: ");
			username = in.next();
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Password: ");
			password = in.next();
			System.out.println("");
			loginAuthentication(username, password);
		} while (failedToLogin == true);
	}

	public static void LDBFrameworkOptions() {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("\tCommands \t| \tDescription");
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("?:- USERS -HELP \t  Prints Command/Description list for user");
		System.out.println("?:- USERS -SU \t\t  Print All user Accounts if Admin");
		System.out.println("?:- USERS -C \t\t  Change existing account in the LDB Framework");
		System.out.println("?:- USERS -A \t\t  Adds a new Account to the LDB Framework");
		System.out.println("?:- USERS -GEN -AUTH \t  Generates a unique ID (DYNAMIC CREATION/DELETION)");
		System.out.println("?:- USERS -SID.AUTH \t  Shows the user's static unique ID (STATIC)");
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("");
	}

	public static void processCommand(String cmd) throws FileNotFoundException, UnsupportedEncodingException {
		if (cmd.equals("help") || cmd.contentEquals("USERS -HELP") || cmd.equals("--help")) {
			System.out.print("\n");
			LDBFrameworkOptions();
		}
		
		if (cmd.equals("USERS -SU")) {
			if (DBusername.equals("Admin") || DBusername.equals("root") || DBusername.equals("rootSU")) {
				System.out.println("\n\t~ Current Authentication Database (ADMIN) ~\n");
				printLoginAuth(true);
				System.out.println(newQueryLDB.rootAccounts.tableBuilder());
			} else {
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ LDB Error - Not super user!");
			}
		}
		
		if(cmd.equals("USERS -A")) {
			cmdType = new Scanner(System.in);
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ [WARNING]: May Corrupt Database Users! Restart to not add any new accounts.\n");
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Add USERNAME: ");
			newUsername_U = cmdType.next();
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Add PASSWORD: ");
			newPassword_U = cmdType.next();
			newQueryLDB.rootAccounts.addItem(newPassword_U,newUsername_U);
			newQueryLDB.rootAccounts.addItem(null,null); // -> Database Offset
			System.out.println();
			processCommand("USERS -SU");
		}

		if (cmd.equals("USERS -C")) {
			if (DBusername.equals("Admin") || DBusername.equals("root") || DBusername.equals("rootSU")) {
				cmdType = new Scanner(System.in);
				boolean esc = false;
				do {
					System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Verify you want to change account settings. ID# " + ID + "\n");
					defaultLoginPrompt();
					System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Username: ");
					newUsername = cmdType.next();
					changeUsername(newUsername, true);
					System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Password: ");
					newPassword = cmdType.next();
					changePassword(newPassword, true);
					System.out.println(
							LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Updating Database ..");
					newQueryLDB = new LDBQueries(newUsername, newPassword, true);
					newQueryLDB.rootAccounts.addItem(newPassword, newUsername);
					newQueryLDB.rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "LDB Updated");
					System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
							+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB.rootAccounts.settingsSaver(), "UpdatedLDB"));
					System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
							+ " - Local:~ LDB_Framework$ Username and Password Successfully Changed! ");
					esc = true;
					System.out.println("");
				} while (esc == false);
			} else {
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ LDB Error - Not super user!");
			}
		}
		// Hidden Only-Available for new Setup Wizard
		if (cmd.equals("USERS -A newUser -$ -newSetupWizard -SU")) {
			cmdType = new Scanner(System.in);
			boolean esc = false;
			do {
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Username [SUPER]: ");
				newUsername_U = cmdType.next();
				encryptSUPER_User = LDB_EncryptAccounts.encryptUsername(newUsername_U);
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Username [NON-SUPER]: ");
				newUsername = cmdType.next();
				encrypt_User = LDB_EncryptAccounts.encryptUsername(newUsername);
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Password [SUPER]: ");
				newPassword_U = cmdType.next();
				encryptSUPER_Pass = Integer.toString(LDB_EncryptAccounts.encryptUsername(newPassword_U).hashCode());
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Password [NON-SUPER]: ");
				newPassword = cmdType.next();
				encrypt_Pass = Integer.toString(LDB_EncryptAccounts.encryptUsername(newPassword).hashCode());
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Updating Database ..");
				newQueryLDB = new LDBQueries(encryptSUPER_User, encryptSUPER_Pass, true);
				newQueryLDB_U = new LDBQueries(encrypt_User, encrypt_Pass, false);
				newQueryLDB_U.usersAccounts.addItem(encrypt_Pass, encrypt_User);
				newQueryLDB.rootAccounts.addItem( encryptSUPER_Pass, encryptSUPER_User);
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
						+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB.rootAccounts.settingsSaver(), "UpdatedLDB"));
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
						+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB_U.usersAccounts.settingsSaver(), "UpdatedLDB_U"));
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
						+ " - Local:~ LDB_Framework$ New Username and Password Successfully Changed! ");
				esc = true;
				System.out.println("");
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
						+ " - Local:~ LDB_Framework$ Restart is needed  to apply new changes to the LDB Framework.");
			} while (esc == false);
		}
		
		if (cmd.equals("USERS -GEN -AUTH")) {
			cmdIDCreated = Integer.toString(LDB_SaveAndPopulateUniqueIDs.generateID());
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
					+ " - Local:~ LDB_Framework$ " + cmdIDCreated);
		}
		
		if (cmd.equals(cmdIDCreated)) {
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime()
					+ " - Local:~ LDB_Framework$ Generated ID Found! You can use this for setting up default passwords for new users." + " Your New Unique ID ---> " + cmdIDCreated + " Your static ID ---> " + ID);
		}
		
		if (cmd.equals("USERS -SID.AUTH")) {
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ " + "Your static ID ---> " + ID);
		}
		
		
		// Note: Server-ONLY commands (port 8000)
		if (cmd.equals("GET SERVER hashCode")) {
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ " + LDB_Server.server.hashCode() + "\n");
		}
		
		if (cmd.equals("GET SERVER assets")) {
			for (LDB_Server.Asset asset: LDB_Server.data.values()) {
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ " + asset.toString() + "\n");
			}
		}
		
		if (cmd.equals("GET SERVER data")) {
			for (String data$: LDB_Server.data.keySet()) {
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ " + data$ + "\n");
			}
		}	
	}

	public static void continousPrompt() throws FileNotFoundException, UnsupportedEncodingException {
		scanner = new Scanner(System.in);
		System.out.print("");
		/*
		 * STEPS IN USED DURING CONTINOUS PROMPT (READ --> PROCESS --> PRINT --> {WHILE (CONDITION == TRUE)} -> READ -> .. ETC)
		 * 
		 * 1) (READ COMMAND LINE)
		 * 2) (PROCESS AND APPLY COMMAND TO DATABASE)
		 * 3) (PRINT RESULT)
		 * 
		 */
		do {
			System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ ");
			command = scanner.nextLine();
			processCommand(command);
		} while (command.length() > 0);
	}
	
	public static void postLoginConfig(boolean superUser) {
		if (superUser == true) {
			try {
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Saving changes made to loginAuth database 1...");
				if (userPassHasBeenDecoded_$ == false) {
					newQueryLDB.rootAccounts.addItem(encryptSUPER_Pass, encryptSUPER_User);
					newQueryLDB.rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
				} 
				if (userPassHasBeenDecoded_$ == true) {
					if ($databaseSAVED == 1) {
						encryptSUPER_User = LDB_EncryptAccounts.encryptUsername(DBusername);
						encryptSUPER_Pass = DBpassword;
						newQueryLDB.rootAccounts.addItem(encryptSUPER_Pass, encryptSUPER_User);
						newQueryLDB.rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
						userPassHasBeenDecoded_$ = false;
					} else {
						encryptSUPER_User = LDB_EncryptAccounts.encryptUsername(DBusername);
						encryptSUPER_Pass = Integer.toString(LDB_EncryptAccounts.encryptUsername(DBpassword).hashCode());
						newQueryLDB.rootAccounts.addItem(encryptSUPER_Pass, encryptSUPER_User);
						newQueryLDB.rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
						userPassHasBeenDecoded_$ = false;
					}	
				}
				
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
						+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB.rootAccounts.settingsSaver(), "UpdatedLDB"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[FAILED TO UPDATE LDB_ADMINS] -> ERROR " + e);
			}
		}

		if (superUser == false) {
			try {
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Saving changes made to loginAuth database 2...");
				if (userPassHasBeenDecoded == false) {
					encrypt_User = LDB_EncryptAccounts.encryptUsername(DBUsername_U);
					newQueryLDB_U.usersAccounts.addItem(encrypt_Pass, encrypt_User);
					newQueryLDB_U.usersAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
				} 
				
				if (userPassHasBeenDecoded == true) {
					if ($databaseSAVED == 1) {
						encrypt_User = LDB_EncryptAccounts.encryptUsername(DBUsername_U);
						encrypt_Pass = DBPassword_U;
						newQueryLDB_U.usersAccounts.addItem(encrypt_Pass, encrypt_User);
						newQueryLDB_U.usersAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
						userPassHasBeenDecoded = false;
					} else {
						encrypt_User = LDB_EncryptAccounts.encryptUsername(DBUsername_U);
						encrypt_Pass = Integer.toString(LDB_EncryptAccounts.encryptUsername(DBPassword_U).hashCode());
						newQueryLDB_U.usersAccounts.addItem(encrypt_Pass, encrypt_User);
						newQueryLDB_U.usersAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
						userPassHasBeenDecoded = false;
					}
				}
				
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
						+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB_U.usersAccounts.settingsSaver(), "UpdatedLDB_U"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[FAILED TO UPDATE LDB_USERS] -> ERROR " + e);
			}
		}
	}

	public static void main(String[] args) throws NoSuchElementException, IOException {
		// if (args.length < 1 || args[0].equals("-help") || args[0].equals("--help")) {
				// System.out.println("Usage: java -jar LDBQueries.jar $webroot [$port]");
				// return;
				// }
		
		/* ::Start::--> (1) */welcomeSetUp(); // Login Setup 
		$databaseSAVED = 1;
		/*--> (3)*/LDBFrameworkOptions();
		/*--> (4)*/continousPrompt();
		/*--> (5A)*/postLoginConfig(true);
		/*--> (5B)*/postLoginConfig(false);
		if (forceServerStart == true) {
			LDB_Server.closeServerConnection();
		}
	}
}