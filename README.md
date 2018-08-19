[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://docs.oracle.com/javase/8/docs/api/)
[![forthebadge](https://forthebadge.com/images/badges/made-with-javascript.svg)](https://www.javascript.com/)
[![forthebadge](https://forthebadge.com/images/badges/uses-html.svg)](https://en.wikipedia.org/wiki/HTML)
[![forthebadge](https://forthebadge.com/images/badges/uses-css.svg)](https://en.wikipedia.org/wiki/Cascading_Style_Sheets)

# LinkedDatabase Frameworks (SERVER)
Using Java WebSocket and Dynamic Web Editor, this server-socket is able to auto-generate, find user IDs, and remove selected users from the LDB Framework (Server-Side). 

The Client-Side will send the user credential through an encrypted HTTPS server and the LDB server will match the sent credential with the database credential if they match the server will send back a "PASS" or let the user login else, it will send a "FAIL" or credentials don't match alert.

The LDB database uses modern password hashing. When the user first makes a password the server will automatically assign a random ID and salt (String). Using the password and salt, the server will encrypt and only save the hash of the password {ex. (SHA512(password + salt)).HASH -> DB}. This means even if the server was hacked, the hacker will not be able to get the password easily and they would have a hard time cracking the encrypted hash passwords. 

# Features 
* Create, Edit, and Delete Accounts (Database)
* Locks for 15 min if password is wrong
* Bash-like UI with unique commands 

# Server-Side Screenshots (BETA)


### Home Page

![alt text](https://github.com/MrGambino/SinglyLinkedDatabaseWithIDFinder/blob/master/Screen%20Shot%202018-07-25%20at%2010.41.45%20AM.png) 

### Locks Account for 15 minutes after 5 tries 

![alt text](https://github.com/MrGambino/SinglyLinkedDatabaseWithIDFinder/blob/master/Screen%20Shot%202018-07-25%20at%2010.42.52%20AM.png) 

### Bash-like Commands 

![alt text](https://github.com/MrGambino/SinglyLinkedDatabaseWithIDFinder/blob/master/Screen%20Shot%202018-07-25%20at%2010.44.53%20AM.png) 


# Future Implementations 

* Admin + Multi-User Control (Parallel Pipeline)
* Branch/Guest Login (Limiting User to only basic commands)
* Automated Login (Admin-Only)
* Network Handling - Admin vs. User website Re-directing (Admin-Only)
* Virtual/Nano LDB Framework (Admin + User)
* Implement YubiKey Two-factor Authenticator + NFC for IOS/Android 
