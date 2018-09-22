<html>
<script>
  function checkFirstVisit() {
    if(sessionStorage.getItem("is_reloaded")) {
      alert('Logging OFF..');
      <?php
            setcookie('AUTH', '', 1, '/');
            setcookie('AUTH', '', 1, '/admin-dashboard');
            if (!(isset($_COOKIE['AUTH']))){
                header('Location: home.php');
                exit;
              }
        ?>
        alert('Logged out --> Session Expired!');
      }
  }
</script>
<html>
