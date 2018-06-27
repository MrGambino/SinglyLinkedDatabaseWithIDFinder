public class LinkedDatabaseFramework {

	private Object Head;
	private LinkedDatabaseFramework Next;
	private LinkedDatabaseFramework Previous;
	private Object item;
	private int itemCount;
	private String fName;

	// CONSTRUCTORS
	public LinkedDatabaseFramework() {
		Head = null;
		fName = "Name Not Set";
		itemCount = 0;
	}

	public LinkedDatabaseFramework(Object item) {
		Head = null;
		this.item = item;
		itemCount = 0;
	}

	public LinkedDatabaseFramework(Object item, LinkedDatabaseFramework next) {
		this.item = item;
		this.Next = next;
		itemCount++;
	}

	public LinkedDatabaseFramework(Object item, LinkedDatabaseFramework next, LinkedDatabaseFramework prev) {
		this.item = item;
		this.Next = next;
		this.Previous = prev;
	}

	// SETTERS
	public void setItemCount(int newItemCount) {
		itemCount = newItemCount;
	}

	public void setNext(LinkedDatabaseFramework next) {
		this.Next = next;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public void setFullName(String fName) {
		this.fName = fName;
	}

	// GETTERS
	public int getItemCount() {
		return itemCount;
	}

	public Object getHead() {
		return Head;
	}

	public LinkedDatabaseFramework getNext() {
		return Next;
	}

	public LinkedDatabaseFramework getPrevious() {
		return Previous;
	}

	public Object getItem() {
		return item;
	}

	public String getFullName() {
		return fName;
	}
}