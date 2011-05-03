<%@ page import="java.util.Map"%>
<%@ page import="org.openid4java.message.AuthRequest"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
AuthRequest authReq = (AuthRequest) request.getAttribute("message");
Map<?, ?> params = authReq.getParameterMap();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>OpenID HTML Form Redirection</title>
    </head>
    <body onload="document.forms['openid-form-redirection'].submit();">
        <h1>Redirect to OpenID Provider</h1>
        <p><%= authReq.getOPEndpoint() %></p>
        <form name="openid-form-redirection" action="<%= authReq.getOPEndpoint() %>" method="post" accept-charset="utf-8">
            <% for (Map.Entry<?, ?> entry : params.entrySet()) { %>
            <input type="hidden" name="<%= (String) entry.getKey() %>" value="<%= (String) entry.getValue() %>" />
            <% } %>
        </form>
    </body>
</html>