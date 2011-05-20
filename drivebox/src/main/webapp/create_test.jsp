<%@ page import="java.util.Map"%>
<%@ page import="org.openid4java.message.AuthRequest"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>test</title>
    </head>
    <body>
    <form action="http://localhost:8080/rest/users/ou=dept2,o=广州局" method="post">
    	请输入你的名称:<input type="text" name="commonName"/><br/>
    	uId:<input type="text" name ="uid"/><br/>
    	surname:<input type="text" name="surname"/><br/>
    	userPassword:<input type="text" name="userPassword"/>
    	<input type="submit" value="提交"/>
    </form>
    </body>
</html>