import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LDBQueries extends LinkedDatabaseFramework {
	// Login Credentials
	protected LinkedDatabase rootAccounts = new LinkedDatabase();
	protected LinkedDatabase usersAccounts = new LinkedDatabase();
	protected static String DBusername, newUsername, DBUsername_U, newUsername_U;
	protected static String DBpassword, newPassword, DBPassword_U, newPassword_U;
	protected static String newIDString = Integer.toString(LDB_SaveAndPopulateUniqueIDs.generateID());
	protected static String ID, tempID;
	protected static File IDFile;
	

	// QueryClass-Only Variables
	private static int attemptToLoginFailed = 0;
	private static double loginFailedStartTime = 0;
	private static double loginFailedEndTime = 0;
	private static boolean failedToLogin = false;
	private static boolean uniqueIDFailedToAuth;

	// Command
	protected static String command;
	protected static Scanner scanner, cmdType;

	// New Query Type
	protected static LDBQueries newQueryLDB; // Super User Only
	protected static LDBQueries newQueryLDB_U; // Non SU Only

	public LDBQueries(Object username, Object password, boolean superUser) {
		// Super-User
		if (superUser == true) {
			DBusername = (String) username;
			DBpassword = (String) password;
			rootAccounts.addItem(DBpassword, DBusername);
			rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
		}

		// Non Super-User
		if (superUser == false) {
			DBUsername_U = (String) username;
			DBPassword_U = (String) password;
			usersAccounts.addItem(DBPassword_U, DBUsername_U);
			usersAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
		}
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
	
	protected static void hide(File src) throws IOException, InterruptedException {
		Process proc = Runtime.getRuntime().exec("attrib +h" + src.getPath());
		proc.waitFor();
	}

	protected static void welcomeSetUp() throws FileNotFoundException, UnsupportedEncodingException {
		// Check File for setup
		File configFile = new File("saveUpdatedLDB.txt");
		File configFile2 = new File("saveUpdatedLDB_U.txt");
		IDFile = new File("saveLDB_UniqueID.txt");
		if (configFile.exists() && configFile2.exists() && IDFile.exists()) {
			/*--> (2)*/loginPrompt();
		} else {
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Setup Wizard Starting Up ... ");	
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Setup Request --> ID# " + newIDString + "\n");
			LDB_SaveAndPopulateUniqueIDs.saveToTextFile("ID=" + newIDString, "LDB_UniqueID");
			IDFile.setReadOnly();
			processCommand("USERS -A newUser -$ -newSetupWizard -SU");
		}
		// If setup exists -> Skip Set-Up
		// Else -> Do the setup
		// Check these files each time the program runs --> ["saveUpdatedLDB.txt" && "saveUpdatedLDB_U.txt"]
		//		----> If they are deleted or unreadable make a new setup file!
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

	private static void loginAuthentication(String Client_username, String Client_password) {
		if (Client_username.equals(DBusername) && Client_password.equals(DBpassword)
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
		if (!((Client_username.equals(DBusername) && Client_password.equals(DBpassword)))) {
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

	public static boolean isFailedToLogin() {
		return failedToLogin;
	}

	public static void setFailedToLogin(boolean failedToLogin) {
		LDBQueries.failedToLogin = failedToLogin;
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
					changeUsername(newUsername, true);
					runNums++;
				}
				if (input.startsWith("Password=")) {
					newPassword = input.substring(9, input.length());
					changePassword(newPassword, true);
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
					changeUsername(newUsername_U, false);
					runNums2++;
				}
				if (input2.startsWith("Password=")) {
					newPassword_U = input2.substring(9, input2.length());
					changePassword(newPassword_U, false);
					runNums2++;
				}
			} while (runNums2 < 3);
		}

		newQueryLDB = new LDBQueries(newUsername, newPassword, true);
		newQueryLDB_U = new LDBQueries(newUsername_U, newPassword_U, false);
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
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("\tLogin to LinkedDataBase Framework \t| \tVersion 0.0.2 - 01");
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
		System.out.println("\tWelcome to LinkedDatabase \t| \tVersion 0.0.2 - 01");
		System.out.println("-------------------------------------------------------------------------------------");
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
		System.out.println("?:- USERS -SU \t\t  Print All user Accounts if Admin");
		System.out.println("?:- USERS -C \t\t  Change existing account in the LDB Framework");
		System.out.println("?:- USERS -A \t\t  Adds a new Account to the LDB Framework");
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("");
	}

	public static void processCommand(String cmd) throws FileNotFoundException, UnsupportedEncodingException {
		if (cmd.equals("USERS -SU")) {
			if (DBusername.equals("Admin") || DBusername.equals("root") || DBusername.equals("rootSU")) {
				System.out.println("\n\t~ Current Authentication Database (ADMIN) ~\n");
				printLoginAuth(true);
				System.out.println(newQueryLDB.rootAccounts.printList());
			} else {
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ LDB Error - Not super user!");
			}
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
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Username: ");
				newUsername = cmdType.next();
				newUsername_U = newUsername;
				changeUsername(newUsername, true);
				changeUsername(newUsername_U, false);
				System.out.print(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ New Password: ");
				newPassword = cmdType.next();
				newPassword_U = newPassword;
				changePassword(newPassword, true);
				changePassword(newPassword_U, false);
				System.out.println(
						LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Local:~ LDB_Framework$ Updating Database ..");
				newQueryLDB = new LDBQueries(newUsername, newPassword, true);
				newQueryLDB_U = new LDBQueries(newUsername_U, newPassword_U, false);
				newQueryLDB.rootAccounts.addItem(newPassword, newUsername);
				newQueryLDB_U.usersAccounts.addItem(newPassword_U, newUsername_U);
				newQueryLDB.rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "LDB & LDB_U Updated!");
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
	}

	public static void continousPrompt() throws FileNotFoundException, UnsupportedEncodingException {
		scanner = new Scanner(System.in);
		System.out.print("");
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
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
						+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB.rootAccounts.settingsSaver(), "UpdatedLDB"));
				newQueryLDB.rootAccounts.addItem(DBpassword, DBusername);
				newQueryLDB.rootAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[FAILED TO UPDATE LDB_ADMINS] -> ERROR " + e);
			}
		}

		if (superUser == false) {
			try {
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - Saving changes made to loginAuth database 2...");
				System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() + " - "
						+ LDB_SaveAndPopulateUniqueIDs.saveToTextFile(newQueryLDB_U.usersAccounts.settingsSaver(), "UpdatedLDB_U"));
				newQueryLDB_U.usersAccounts.addItem(DBPassword_U, DBUsername_U);
				newQueryLDB_U.usersAccounts.addItem(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime(), "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[FAILED TO UPDATE LDB_USERS] -> ERROR " + e);
			}
		}
	}

	public static void main(String[] args)
			throws FileNotFoundException, UnsupportedEncodingException, NoSuchElementException {
		/* ::Start::--> (1) */welcomeSetUp(); // Login Setup
		/*--> (3)*/LDBFrameworkOptions();
		/*--> (4)*/continousPrompt();
		/*--> (5A)*/postLoginConfig(true);
		/*--> (5B)*/postLoginConfig(false);
	}
}