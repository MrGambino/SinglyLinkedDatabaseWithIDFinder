import java.util.Random;

public class LinkedDatabase extends LinkedDatabaseFramework {

	public static LinkedDatabaseFramework Head;
	public static LinkedDatabaseFramework next;
	public static LinkedDatabaseFramework prev;
	public static Object item;
	public static int sizeOfCollection;

	public LinkedDatabase() {
		Head = new LinkedDatabaseFramework();
		sizeOfCollection = 0;
	}

	public boolean isEmpty() {
		return Head.getNext() == null && Head.getItemCount() == 0;
	}

	public void addItem(Object item, String fullName) {
		LinkedDatabaseFramework newNode = new LinkedDatabaseFramework(item, null, Head);

		while (newNode.getNext() != null) {
			newNode = newNode.getNext();
		}

		newNode.Next = new LinkedDatabaseFramework(item);
		newNode.setItem(item);
		newNode.setFullName(fullName);
		newNode.setNext(Head);
		Head = newNode;
		sizeOfCollection++;
	}

	public void deleteFirstItem() {
		//
	}

	public void deleteLastItem() {
		// 
	}

	public void deleteItemByID(int value) {
		// Find ID First
	}

	public String printList() {
		String str = "";
		LinkedDatabaseFramework cur = Head.getNext();
		int firstNode = 0;

		while (cur != null) {
			if (firstNode == 0) {
				str += "-----------------------------------------------------------------------------------------------\t\n";
				str += "\t\t# \t\t\tID\t\t\t\tName\n-----------------------------------------------------------------------------------------------\t\n";
				firstNode++;
			}
			if (!(cur == null)) {
				if (cur.getNext() == null) {
					str += "-----------------------------------------------------------------------------------------------";
				} else {
					str +="\t\t" + (firstNode - 1) + "\t\t\t{" + cur.getItem() + "}\t\t\t(" + cur.getFullName() + ")\n";
				}
			}
			firstNode++;
			cur = cur.getNext();
		}
		return str;
	}
	
	public String tableBuilder() {
		LinkedDatabaseFramework cur = Head.getNext();
		int firstNode = 1; 	// Offset Point -> The linked list starts from 1 not 0
							// If the firstNode is 0 the identifier is -1 so to 
							//offset this it has to be 1 
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(String.format("%-20s%-20s%-20s%-20s\n", "Identifier #", "ID", "Full Name", "Memory Location\t\tAddr\t\t\tUnique-ID"));
		sBuilder.append(String.format(
				"==============================================================================================================================\n"));
		while (cur != null) {
			sBuilder.append(String.format("%-20s", (firstNode - 1)));
			sBuilder.append(String.format("%-20s", "{" + cur.getItem() + "}"));
			sBuilder.append(String.format("%-20s", cur.getFullName()));
			sBuilder.append(String.format("%-20s", Integer.toHexString(cur.hashCode())));
			sBuilder.append(String.format("%-20s\n", "\t@"+cur.hashCode()+"\t\tUID# "+LDB_SaveAndPopulateUniqueIDs.generateID()));
			firstNode++;
			cur = cur.getNext();
		}
		return sBuilder.toString();
	}
	
	public String settingsSaver() {
		String str = "";
		LinkedDatabaseFramework cur = Head.getNext();
		
		while (cur != null) {
			str +="Username=" + cur.getFullName() +"\n" + "Password=" + cur.getItem() + "\n\n";
			cur = cur.getNext();
		}
		return str;
	}

	public String findNameByID(Object valueToFind) {
		Object value = Head.getNext().getItem();
		String name = Head.getNext().getFullName();
		LinkedDatabaseFramework cur = Head.getNext();
		while (cur != null) {
			if (valueToFind == value) {
				return name;
			} else {
				try {
					cur = cur.getNext();
					value = (int) cur.getItem();
					name = cur.getFullName();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("\n" + e + " --> NAME NOT FOUND\n");
				}
			}
		}
		return "Name with that ID Not Found";
	}

	public Object findImmeadiateIDByName(Object fname) {
		Object immediateID = Head.getNext().getItem();
		String name = Head.getNext().getFullName();
		LinkedDatabaseFramework cur = Head.getNext();
		while (cur != null) {
			if (fname == name) {
				return immediateID;
			} else {
				try {
					cur = cur.getNext();
					immediateID = (int) cur.getItem();
					name = cur.getFullName();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("\n" + e + " --> ID NOT FOUND\n");
				}
			}
		}
		return -999999999;
	}

	public static int generateID() {
		Random rand = new Random();
		int generatedRandomID = rand.nextInt(999999999) + 1;
		return generatedRandomID;
	}
}