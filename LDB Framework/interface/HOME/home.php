<?php
session_start();

if (isset($_SESSION['auth'])) {
  header('Location: ./admin-dashboard/admin-dashboard.html');
  exit;
} else {
    header("Location: http://localhost:80/");
}
?>
