<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils"%>
<%
	SecurityUtils.getSubject().logout();
/* 	out.println("已登出"); */
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>该用户已退出</title>
		<link href="style/login2.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div class="status_frame">
		  <div class="status_box">
			 <div  class="status_body ">
				   <h1 class="islogout"> 该用户已退出</h1>
				   <p>您已经安全退出本系统</p>
			 </div>
		  </div>
		</div>
	</body>
</html>