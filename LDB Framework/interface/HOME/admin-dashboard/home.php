<?php
if (!isset($_SESSION)) {
    session_start();
}
//echo $_COOKIE['AUTH'];
// $data = (object) array("username" => $username, "session_id" => $_currentSessionID);
// $cookieData = (object)  array("user_data" => $data, "expire" => $_expires);
// header('Content-type: application/json');
// echo json_encode($cookieData);
if (isset($_COOKIE['AUTH'])) {
  header('Location: admin-dashboard.php');
  exit;
}else{
  header("Location: http://localhost:80/");
  exit;
}
?>
