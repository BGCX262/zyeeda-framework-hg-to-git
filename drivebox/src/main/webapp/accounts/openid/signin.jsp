<%@ page import="java.util.Map"%>
<%@ page import="org.openid4java.message.AuthRequest"%>
<%@ page import="com.zyeeda.framework.FrameworkConstants" %>
<%@ page import="org.apache.tapestry5.ioc.Registry" %>
<%@ page import="com.zyeeda.framework.security.SecurityService" %>
<%@ page import="com.zyeeda.framework.security.internal.OpenIdConsumerSecurityServiceProvider" %>
<%@ page import="com.zyeeda.framework.utils.IocUtils" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
AuthRequest authReq = (AuthRequest) request.getAttribute("message");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>单点登录</title>
		<link href="style/login2.css" rel="stylesheet" type="text/css" />
    </head>
    <% if (authReq == null) { %>
		<body>
			<div class="status_frame">
			   <div class="status_box">
				 <div  class="status_body ">
				   <h1 class="islogin_icon"> 该用户已经登录</h1>
				    <%
						Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
						SecurityService securityService = reg.getService(IocUtils.getServiceId(OpenIdConsumerSecurityServiceProvider.class), SecurityService.class);
						String username = securityService.getCurrentUser();
					%>
					<p>登录名：<%=username%></p>
				 </div>
			   </div>
			</div>
		</body>
    <% } else { 
    	Map<?, ?> params = authReq.getParameterMap(); %>
		<body onload="document.forms['openid-form-redirection'].submit();">
			<div class="status_frame">
				<div class="status_box">
					<div  class="status_body ">
						<h1> <img src="img/loading.gif" width="37" height="37" />正在向单点登录服务器发送请求，请稍后</h1>
						<!--<h1>正在向单点登录服务器发送请求，请稍候……</h1>
						<p> <%= authReq.getOPEndpoint() %> </p>-->
						<form name="openid-form-redirection" action="<%= authReq.getOPEndpoint() %>" method="post" accept-charset="utf-8">
							<% for (Map.Entry<?, ?> entry : params.entrySet()) { %>
							<input type="hidden" name="<%= (String) entry.getKey() %>" value="<%= (String) entry.getValue() %>" />
							<% } %>
						</form>
					</div>
				</div> 
			</div>
		</body>
    <% } %>
</html>