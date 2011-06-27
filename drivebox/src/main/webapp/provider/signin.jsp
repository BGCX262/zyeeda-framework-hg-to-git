<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.openid4java.message.ParameterList"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>单点登录</title>
		<link href="style/login.css" rel="stylesheet" type="text/css" />
    </head>
	 
	<%
    boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
    ParameterList params = (ParameterList) session.getAttribute("params");
    if (authenticated) {
    	if (params == null) { // 直接访问登录界面 
	%>
		<body class="logined">
    		<!--<h1>当前用户已登录！</h1>-->
		</body>
    <%  }
    } else {
    	if (params != null) {
    		String realm = params.hasParameter("openid.realm") ? params.getParameterValue("openid.realm") : null;
            String returnTo = params.hasParameter("openid.return_to") ? params.getParameterValue("openid.return_to") : null;
            String claimedId = params.hasParameter("openid.claimed_id") ? params.getParameterValue("openid.claimed_id") : null;
            String identity = params.hasParameter("openid.identity") ? params.getParameterValue("openid.identity") : null;
            String site = (realm == null ? returnTo : realm);  %>
			
		<body class="unlogin">	
			<div class="login_box">
				<div class="login_top"></div>
				<div class="login_body">
					<form class="login_form " method="post">
						<div class="clear">
							<!--<strong>ClaimedID:</strong><%= claimedId%><br />
							<strong>Identity:</strong><%= identity %><br />-->
							<em>站点：</em>
							<span class="login_label"><%= site %></span>
						</div>
			<% } %>
						<div class="tips">
							<div class="tip_wrong">
								<span>
									密码提示密码提示，填写错误示，填
								</span>
							</div>
						</div>
						<div class="clear">
							<em>
								用户名：
							</em>
							<input name="username" type="text" class="input_user"/><!--onmouseover="this.className='input_user_hover'"
							onmouseout="this.className='input_user'"-->
						</div>
						<div class="clear">
							<em>
								密 码：
							</em>
							<input name="password" type="password" class="input_password"/><!-- onmouseover="this.className='input_password_hover'"
							onmouseout="this.className='input_password'" -->
						</div>
						<div class="forget">
							<em></em>
							<div class="forget_content ">
								&nbsp; &nbsp;
								<label>
									<input type="checkbox" name="checkbox" id="checkbox" style="margin:0"/>
									记住登录信息
								</label>
								<!--<a href="#">
									<img src="images/Help-Circle.jpg" border="0" />
									忘记密码
								</a>-->
							</div>
						</div>
						<div class="login_bt">
							<em></em>
							<input type="submit" class="" value=" " />
						</div>
						<div class="clear"></div>
					</form>
				</div>
				<div class="login_bottom"></div>
			</div>
		</body>
    <% } %>
</html>