<?php
/**
 * Methods for database handling.
 */
class DB extends SQLite3 {
    const DATABASE_NAME = 'db/database_users.db';
    const BCRYPT_COST = 14;
    /**
     * DB class constructor. Initialize method is called, which will create users table if it does
     * not exist already.
     */
    function __construct() {
        $this->open(self::DATABASE_NAME);
        $this->initialize();
    }

    /**
     * Creates the table if it does not exist already.
     */
    protected function initialize() {
        $sql = 'CREATE TABLE IF NOT EXISTS USERS (
                    NAME text NOT NULL,
                    USERNAME text UNIQUE NOT NULL,
                    PASSWORD text NOT NULL
                )';
        $this->exec($sql);
    }

    /**
     * Authenticates the given user with the given password. If the user does not exist, any action
     * is performed. If it exists, its stored password is retrieved, and then password_verify
     * built-in function will check that the supplied password matches the derived one.
     *
     * @param $username The username to authenticate.
     * @param $password The password to authenticate the user.
     * @return True if the password matches for the username, false if not.
     */
    public function authenticateUser($username, $password) {
        if ($this->userExists($username)) {
            $storedPassword = $this->getUsersPassword($username);
            if ($password == $storedPassword) {
                $authenticated = true;
                $_POST[$_SESSION['user_id']] = $username->ID;
            } else {
                $authenticated = false;
            }
        } else {
            $authenticated = false;
        }
        return $authenticated;
    }

    /**
     * Checks if the given users exists in the database.
     *
     * @param $username The username to check if exists.
     * @return True if the users exists, false if not.
     */
    protected function userExists($username) {
        $sql = 'SELECT COUNT(*) AS count
                FROM   USERS
                WHERE  USERNAME = :username';
        $statement = $this->prepare($sql);
        $statement->bindValue(':username', $username);
        $result = $statement->execute();
        $row = $result->fetchArray();
        $exists = ($row['count'] === 1) ? true : false;
        $statement->close();
        return $exists;
    }

    /**
     * Gets given users passwords
     *
     * @param $username The username to get the password of.
     * @return The password of the given user.
     */
    protected function getUsersPassword($username) {
        $sql = 'SELECT PASSWORD
                FROM   USERS
                WHERE  USERNAME = :username';
        $statement = $this->prepare($sql);
        $statement->bindValue(':username', $username);
        $result = $statement->execute();
        $row = $result->fetchArray();
        $password = $row['PASSWORD'];
        $statement->close();
        return $password;
    }

    /**
     * Creates a new user.
     *
     * @param $username The username to create.
     * @param $password The password of the user.
     */
    public function createUser($username, $password) {
        $sql = 'INSERT INTO USERS
                VALUES (:username, :password)';
        $options = array('cost' => self::BCRYPT_COST);
        $derivedPassword = password_hash($password, PASSWORD_BCRYPT, $options);
        $statement = $this->prepare($sql);
        $statement->bindValue(':username', $username);
        $statement->bindValue(':password', $derivedPassword);
        $statement->execute();
        $statement->close();
    }
}
