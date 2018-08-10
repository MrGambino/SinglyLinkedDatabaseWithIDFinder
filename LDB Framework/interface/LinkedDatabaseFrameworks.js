// Combination of LinkedDatabaseFramework.java + LinkedDatabase.java
class LinkedDatabaseFramework {
  constructor() {
    this.head = null;
    this.tail = null;
    this.count = 0;
  }

  // SETTERS --> Public value setters
  set setNextNode(Next){
    this.tail = Next;
  }

  set setItemID(itemID){
    this.itemID = itemID;
  }

  set setFullName(fullName){
    this.fullName = fullName;
  }

  // GETTERS --> Public value finders
  get getHeadNode() {
    return this.head;
  }

  get getNextNode() {
    return this.head.tail;
  }

  get getItemID(){
    return this.head.itemID;
  }

  get getFullName() {
    return this.head.fullName;
  }

  get getCount(){
    return this.count;
  }

  generateID() {
    return Math.floor((Math.random() * 999999999) + 1);
  }

  addLast(itemID, fullName) {
    // Create a new Node
    const node = {
      itemID: itemID,
      fullName: fullName,
      Next: null
    }

    if(this.count === 0) {
      // If this is the first Node, assign it to head
      this.head = node;
    } else {
      // If not the first node, link it to the last node
      this.tail.next = node;
    }

    this.next = node;

    this.count++;
  }

  addFirst(itemID, fullName) {
    // Create a new Node
    const node = {
      itemID: itemID,
      fullName: fullName,
      next: null
    }

    // Save the first Node
    const temp = this.head;

    // Point head to the new Node
    this.head = node;

    // Add the rest of node behind the new first Node
    this.head.next = temp;

    this.count++;

    if(this.count === 1) {
      // If first node,
      // point tail to it as well
      this.tail = this.head;
    }
  }

  removeFirst(itemID) {
    if(this.count > 0) {
      // The head should point to the second element
      this.head = this.head.next;

      this.count--;

      if(this.count === 0) {
        // If list empty, set tail to null
        this.tail = null;
      }
    }
  }

  removeLast(itemID) {
    if(this.count > 0) {
      if(this.count === 1) {
        this.head = null;
        this.tail = null;
      } else {
        // Find the Node right before the last Node
        let current = this.head;
        while(current.next !== this.tail) {
          current = current.next;
        }

        current.next = null;
        this.tail = current;
      }
      this.count--;
    }
  }
}

// Date -> Format Maker
function formatDate(date) {
  var monthNames = [
    "January", "February", "March",
    "April", "May", "June", "July",
    "August", "September", "October",
    "November", "December"];

  var day = date.getDate();
  var monthIndex = date.getMonth();
  var year = date.getFullYear();

  return day + ' ' + monthNames[monthIndex] + ' ' + year;
}
