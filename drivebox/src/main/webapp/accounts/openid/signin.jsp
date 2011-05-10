<%@ page import="java.util.Map"%>
<%@ page import="org.openid4java.message.AuthRequest"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
AuthRequest authReq = (AuthRequest) request.getAttribute("message");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>单点登录</title>
    </head>
    <% if (authReq == null) { %>
    <body>
        <h1>当前用户已登录！</h1>
    </body>
    <% } else { 
    	Map<?, ?> params = authReq.getParameterMap(); %>
    <body onload="document.forms['openid-form-redirection'].submit();">
        <h1>正在向单点登录服务器发送请求，请稍候……</h1>
        <p><%= authReq.getOPEndpoint() %></p>
        <form name="openid-form-redirection" action="<%= authReq.getOPEndpoint() %>" method="post" accept-charset="utf-8">
            <% for (Map.Entry<?, ?> entry : params.entrySet()) { %>
            <input type="hidden" name="<%= (String) entry.getKey() %>" value="<%= (String) entry.getValue() %>" />
            <% } %>
        </form>
    </body>
    <% } %>
</html>