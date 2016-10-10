<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Log In</title>
        <link rel="stylesheet" href="css/normalize.css">
        <link href='http://fonts.googleapis.com/css?family=Nunito:400,300' rel='stylesheet' type='text/css'>
        <link rel="stylesheet" href="css/style.css">
    </head>
    <body>

      <form action="login" method="post">
      
        <h1>Sign Up</h1>
        
          <legend><span class="number"></span>Your basic info</legend>
          
          <label for="name">UserName:</label>
          <input type="text" id="name" name="userName">
          
          <label for="password">Password:</label>
          <input type="password" id="password" name="password">
          
        <input type="submit" value="Log In">
        <h2>If not registered, register <a href="http://localhost:8086/DirectApkDownloaderSelenium/rest/openSignUp">here</a></h2>
      </form>
      
    </body>
</html>