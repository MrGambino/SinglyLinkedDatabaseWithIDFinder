import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LDB_SaveAndPopulateUniqueIDs extends LinkedDatabase {
	public static LinkedDatabase LL;
	public static final int numberOfUsers = 10;
	public static int[] IDarray;
	public static PrintWriter writer;
	public static String date;
	private static boolean newNameAdded = false;
	public static ArrayList<Object> LDB_Pass = new ArrayList<>();
	public static ArrayList<Object> LDB_Fail = new ArrayList<>();
	public static ArrayList<Object> LDB_Errors = new ArrayList<>();

	public static void printAllUserAtIndex(int indexValue) {
		LL = new LinkedDatabase();
		IDarray = new int[10];
		for (int i = 0; i < numberOfUsers; i++) {
			IDarray[i] = generateID();
		}

		// Import Default Data into PASS and FAIL List.
		LL.addItem(IDarray[0], "Jane Doe");
		LL.addItem(IDarray[1], "Sarah Law");
		LL.addItem(IDarray[2], "John Doe");
		LL.addItem(IDarray[3], "Harry Pinto");
		LL.addItem(IDarray[4], "Larry Garry");
		LL.addItem(IDarray[5], "John Jo");
		LL.addItem(IDarray[6], "Jo IO");
		LL.addItem(IDarray[7], "Joyner JP");
		LL.addItem(IDarray[8], "Joey L");
		LL.addItem(IDarray[9], "Js.lpo");
		// This is needed as the list has an offset of one item
		// And so to add a new item we need the item it self plus the default next.
		/*
		 * OBJECT [-1] -> {NEW_OBJ}.ADD() ---- (Not visible currently we need to add
		 * another item) OBJECT [0] OBJECT [1]
		 * 
		 * ----------------------------------------------------------------------------
		 * 
		 * OBJECT [-1] -> {OFFSET_NEW_OBJ}.ADD() ---- (Not visible currently we need to
		 * add another item) OBJECT [0] -> {NEW_OBJ}.ADD() ---- (Visible in our list.
		 * Note: Offset OBJ is never visible in the list) OBJECT [1]
		 **/
		LL.addItem(null, null);
		System.out.println(LL.printList());

		// Check Default Values and add the result to arrayList for DEBUG later
		for (int i = 0; i < indexValue; i++) {
			LDB_Pass.add("\n\n\nFINDING NAME WITH THIS ID NUMBER " + "{" + IDarray[i] + "}" + "\t(?)\n");
			LDB_Fail.add("\n\n\nFINDING NAME WITH THIS ID NUMBER " + "{" + IDarray[i] + "}" + "\t(?)\n");
			try {
				if (LL.findNameByID(IDarray[i]) == "Name with that ID Not Found") {
					LDB_Fail.add("[NOT FOUND]" + " --> " + IDarray[i] + "\t(??..)" + "\nEF1: ERROR OCCURED IN SEARCH");
				} else {
					LDB_Pass.add("[FOUND]" + " --> " + "[" + LL.findNameByID(IDarray[i]) + " | ID: " + IDarray[i] + "]"
							+ "\t(!)");
				}
			} catch (Exception e) {
				LDB_Errors.add(e.getMessage() + " --> " + "[NOT FOUND]" + "\t(??..)");
			}
		}
	}

	public static void addNewUserToLDB(int ID, String fullName) {
		do {
			LL.addItem(ID, fullName);
			// Always add an OFFSET or the list won't find the new added item!
			LL.addItem(null, null);
			setNewNameAdded(true);
		} while (!isNewNameAdded());
	}

	public static boolean isNewNameAdded() {
		return newNameAdded;
	}

	public static void setNewNameAdded(boolean newNameAdded) {
		LDB_SaveAndPopulateUniqueIDs.newNameAdded = newNameAdded;
	}
	
	public static String updateCurrentTime() {
		return date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static void generateUniqueIDsAndMakeATextFile(int numberOfIDs, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		LinkedDatabase LL2 = new LinkedDatabase();
		writer = new PrintWriter(fileName, "UTF-8");
		Path path = Paths.get(fileName);
		Double completed = 0.0;
		
		double startTime = System.nanoTime();
		boolean errorHasOccured = false;
		for (int i = 0; i <= numberOfIDs; i++) {
			if (i == 0) {
				writer.println("VERBOSE DEBUG LOGS ~ " + updateCurrentTime() + " ~ " + path + " ~ " + "\n\n" + "Creating 10 Unique IDs ..");
			}
			try {
				LL2.addItem(generateID(), "$addr" + i);
				writer.print(completed / numberOfIDs * 100 + " % Completed! ~ ELAPSED TIME: " + (System.nanoTime() - startTime)/1000000 + " seconds ~ "+ updateCurrentTime() + "\n");
				writer.println(LL2.printList() + "\n\n");
				completed++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				errorHasOccured = true;
				writer.println("ERROR HAS OCCURED WHEN GENERATING UNIQUE IDs" +"\n" +"[ ERROR TYPE ]==>[ " + e + " ]");
			}
		}
		writer.println("");
		if (errorHasOccured) {
			writer.println("GENERATING ID HAS FAILED. Try Again Later!");
		} else {
			writer.print(100 + " % Completed! ~ ELAPSED TIME: " + (System.nanoTime() - startTime)/1000000 + " seconds ~ " + updateCurrentTime() + "\n");
			writer.println("Generating ID has passed, " + numberOfIDs + " unique IDs can be used for validation.");
		}
		writer.println("\n ~ EOF ~");
		writer.close();
		System.out.println(LL2.printList());
		System.out.println("\n" + path.toString() + " -> SUCCESSFULLY CREATED " + numberOfIDs + " RANDOM IDs ~ " + updateCurrentTime());

	}
	
	public static String saveToTextFile(String string, String offset) throws FileNotFoundException, UnsupportedEncodingException {
		String stringName = "save" + offset +".txt";
		try {
			PrintWriter save = new PrintWriter(stringName, "UTF-8");
			save.println();
			save.println(string);
			save.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("[FAILED TO SAVE] -> " + e);	
		}
		return "[SUCCESS] Saved in -> " + stringName;
	}
	
//	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
//		// TODO Auto-generated method stub
//		// Run the token/unique id maker 
//		generateUniqueIDsAndMakeATextFile(25, "uniqueIds25_validation_hourly.txt");
//		// Saving files into Text editors
//		LinkedDatabase LL2 = new LinkedDatabase();
//		LL2.addItem(101030433, "Sirak Berhane");
//		LL2.addItem(generateID(), "Jane Doe");
//		LL2.addItem(generateID(), "Sarah Law");
//		LL2.addItem(generateID(), "John Doe");
//		LL2.addItem(generateID(), "Harry Pinto");
//		LL2.addItem(generateID(), "Larry Garry");
//		LL2.addItem(generateID(), "John Jo");
//		LL2.addItem(generateID(), "Jo IO");
//		LL2.addItem(generateID(), "Joyner JP");
//		LL2.addItem(generateID(), "Joey L");
//		LL2.addItem(generateID(), "Js.lpo");
//		LL2.addItem(generateID(), "Jane Doe");
//		LL2.addItem(generateID(), "Sarah Law");
//		LL2.addItem(generateID(), "John Doe");
//		LL2.addItem(generateID(), "Harry Pinto");
//		LL2.addItem(generateID(), "Larry Garry");
//		LL2.addItem(generateID(), "John Jo");
//		LL2.addItem(generateID(), "Jo IO");
//		LL2.addItem(generateID(), "Joyner JP");
//		LL2.addItem(generateID(), "Joey L");
//		LL2.addItem(generateID(), "Js.lpo");
//		LL2.addItem(null, "fullName");
//		System.out.println(saveToTextFile(LL2.printList(), "0"));
//		// System.out.println(LL2.settingsSaver());
//	}
}