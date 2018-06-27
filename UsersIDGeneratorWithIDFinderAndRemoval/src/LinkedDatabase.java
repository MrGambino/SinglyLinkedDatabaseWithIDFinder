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
		LinkedDatabaseFramework newNode = new LinkedDatabaseFramework();
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
		//
	}

	public String printList() {
		String str = "";
		LinkedDatabaseFramework cur = Head.getNext();
		int firstNode = 0;

		while (cur != null) {
			if (firstNode == 0) {
				str += "[LAST]->";
				firstNode++;
			}
			if (!(cur.getNext() == null)) {
				str += "[" + cur.getItem() + "]" + "->";
			} else {
				if (cur.getNext() == null) {
					str += "[" + "FIRST" + "]";
				}else {
				str += "[" + cur.getItem() + "]";
				}
			}
			cur = cur.getNext();
		}

		return str;
	}

	public String findNameByID(int valueToFind) {
		int value = (int) Head.getNext().getItem();
		String name = Head.getNext().getFullName();
		LinkedDatabaseFramework cur = Head.getNext();

		while (cur != null) {
			if (valueToFind == value) {
				return name;
			} else {
				cur = cur.getNext();
				value = (int) cur.getItem();
				name = cur.getFullName();
			}
		}
		return "Name with that ID Not Found";
	}

	public static int generateID() {
		Random rand = new Random();
		int generatedRandomID = rand.nextInt(999999999) + 1;
		return generatedRandomID;
	}
}