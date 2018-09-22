<?php
session_start();
require_once('database-super.php');
/**
 * Checks if the given parameters are set. If one of the specified parameters is not set,
 * die() is called.
 *
 * @param $parameters The parameters to check.
 */
function checkPOSTParametersOrDie($parameters) {
    foreach ($parameters as $parameter) {
        isset($_POST[$parameter]) || die("'$parameter' parameter must be set by POST method.");
    }
}

checkPOSTParametersOrDie(['username', 'password']);
$username = $_POST['username'];
$password = $_POST['password'];
$db = new DB();
$authenticated = $db->authenticateUser($username, $password);
if ($authenticated) {
    $time = time();
    $_currentSessionID = session_id();
    $_expires = $time + 3600;
    setcookie('AUTH', $_currentSessionID, $_expires);
    header('Location: /admin-dashboard/home.php');
    exit;
} else {
    $response = "Incorrect credentials or user does not exist.";
    header('Location: index.html');
    exit;
}
echo $response;
?>
