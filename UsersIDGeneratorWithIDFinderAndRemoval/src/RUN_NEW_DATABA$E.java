public class RUN_NEW_DATABA$E extends LinkedDatabase {
	public static LinkedDatabase LL;
	public static final int numberOfUsers = 10;

	public static void printAllUserAtIndex(int indexValue) {
		LL = new LinkedDatabase();

		int[] IDarray = new int[10];
		for (int i = 0; i < numberOfUsers; i++) {
			IDarray[i] = generateID();
		}

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
		LL.addItem(000000000, "No Name Set --> List Offset");
		System.out.println(LL.printList());

		for (int i = 0; i < indexValue; i++) {
			System.out.println("\n\n\nFINDING NAME WITH THIS ID NUMBER " + "{" + IDarray[i] + "}" + "\t(?)\n");

			try {
				if (LL.findNameByID(IDarray[i]) == "Name with that ID Not Found") {
					System.out.println("[NOT FOUND]" + " --> " + IDarray[i] + "\t(??..)");
					System.out.println("EF1: ERROR OCCURED IN SEARCH");
				} else {
					System.out.println("[FOUND]" + " --> " + "[" + LL.findNameByID(IDarray[i]) + " | ID: "
							+ IDarray[i] + "]" + "\t(!)");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage() + " --> " + "[NOT FOUND]" + "\t(??..)");
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		printAllUserAtIndex(10);
	}
}