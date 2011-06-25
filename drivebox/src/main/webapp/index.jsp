<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    
    <head>
		<title>
            用户组织管理系统
        </title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta http-equiv="pragma" content="no-cache"/>
		<meta http-equiv="cache-control" content="no-cache"/>
		<meta http-equiv="expires" content="0"/>
		<meta http-equiv="keywords" content="ehvgz"/>
		<meta http-equiv="description" content="China South Power Grid Guang Zhou High Voltage User Organization Structure Management Project."/>
		<link rel="shortcut icon" href="favicon.ico" />
		
		<style>
			.loading {
				border:1px solid #8d8d8d;
				margin:auto;
				background:#d7d9e0;
				width:240px;
				height:80px;
				left:50%;  
                top:50%;
				margin-left:-120px;  
				margin-top:-40px; 
				position:absolute;  
			}
			.loading span {
				display:block;
				font-size:14px;
				font-weight:bold;
				color:#4a4a4a;
				padding-left:50px;
				line-height:65px;
				margin:5px;
				height:65px;
				background-image:url(public/img/loading.gif);
				background-position:9px ;
				background-repeat:no-repeat;
				background-color: #FFFFFF;
				border:#cecece 1px solid;
			}
		</style>
		<div id="loading" class="loading"><span>正在为您加载，请稍候</span></div>
		<script type="text/javascript">
		//<![CDATA[
			var loadDocument = document.getElementById("loading");
			loadDocument.style.display = "block"; //显示
			
			var systemLoginUser = "<%=SecurityUtils.getSubject().getPrincipal() %>";//"admin";
		//]]>
		</script>
        
        <link rel="stylesheet" type="text/css" href="public/style/css.css" />
        <link rel="stylesheet" type="text/css" href="public/style/style.css" />
        <link rel="stylesheet" type="text/css" href="public/style/table.css" />
        <link rel="stylesheet" type="text/css" href="public/style/form.css" />
        <link rel="stylesheet" type="text/css" href="public/style/yui-override.css" />
        <link rel="stylesheet" type="text/css" href="public/style/zui-messagebox.css" />
        <link rel="stylesheet" type="text/css" href="public/style/exceptionpage.css" />
		<link type="text/css" rel="stylesheet" href="public/lib/yui3-gallery/build/gallery-aui-skin-base/css/gallery-aui-skin-base.css"/>
		<link type="text/css" rel="stylesheet" href="public/lib/yui3-gallery/build/gallery-aui-skin-classic/css/gallery-aui-skin-classic.css"/>
		<link type="text/css" rel="stylesheet" href="public/lib/yui3-gallery/build/gallery-aui-tree-view/assets/skins/sam/gallery-aui-tree-view.css"/>
		<link type="text/css" rel="stylesheet" href="public/lib/yui3-gallery/build/gallery-calendar/assert/skin.css"/>
		<link type="text/css" rel="stylesheet" href="public/lib/yui3-gallery/build/gallerycss-xarno-skins/gallerycss-xarno-skins.css"/>

		<!--自定义样式-->
        <link rel="stylesheet" type="text/css" href="static/style/style.css" />
        <link rel="stylesheet" type="text/css" href="static/style/department.css" />
        <link rel="stylesheet" type="text/css" href="static/style/user.css" />
        <link rel="stylesheet" type="text/css" href="static/style/tree.css" />
		
        <script type="text/javascript" src="public/lib/yui3/build/yui/yui-min.js"></script>
        <script type="text/javascript" src="public/lib/mustache/mustache.js"></script>
		<script type="text/javascript" src="public/lib/zui/build/zui-common/zui-common.js"></script>
		<script type="text/javascript" src="public/lib/zui/build/zui-messagebox/zui-messagebox.js"></script>
		<script type="text/javascript" src="public/lib/zui/build/zui-tabview/zui-tabview.js"></script>
		
		<!--自定义javascript-->
        <script type="text/javascript" src="static/lib/zyeeda/common/jquery-1.6.1.min.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/common/init.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/common/utils.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/common/tree.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/deparment/department_action.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/deparment/department_detail.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/user/user_action.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/user/user_detail.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/search/searchData.js"></script>
        <script type="text/javascript" src="static/lib/zyeeda/common/main.js"></script>
    </head>
    
    <body class="yui-skin-sam yui3-skin-xarno yui3-skin-xarno-growl" ><!--onbeforeunload="ZDA.listenFormsChange('user_form', 'department_form');"-->
		<div class="box" style="display:none">
			<div class="top">
				<div class="top1">
					<div class="top2"></div>
				</div>
				<div class="logo">
					<div class="logo-bj"></div>
					<div class="welcome">
						<!-- <a href="#">
							<span class="pic">
								<img src="public/img/welcome_06.png" alt="a" width="21" height="24" border="0"
								/>
							</span>
							欢迎您:张三(站长)
						</a> -->
						<a href="#" class="returnToMain">
							<span class="pic">
								<img src="public/img/home.png" alt="a" width="22" height="24" border="0"/>
							</span>
							返回首页
						</a>
						<a href="accounts/openid/signout.jsp">
							<span class="pic">
								<img src="public/img/welcome_10.png" alt="a" width="23" height="24" border="0"/>
							</span>
							安全退出
						</a>
					</div>
				</div>
				<div class="top-bj2"></div>
			</div>
			<div class="content">
				<div class="c-left">
					<div class="warp_l">
						<div class="left_tree_top">
							<img src="static/img/user-left-title_03.jpg" alt="user" />
						</div>
						<div class="left_tree_content">
							<div class="left_tree_content2">
								<div class="user">
									<img src="static/img/user_08.jpg" alt="user" width="37" height="40" align="absmiddle"/>
										欢迎您：<%=SecurityUtils.getSubject().getPrincipal() %>
										<br />
										(站长)
								</div>
								<div class="left_tree" id="left_tree_menu" style="white-space:nowrap; "></div>
								<div style="clear:both"></div>
							</div>
						</div>
					</div>
				</div>   <!-- <div id="Layer1"><img src="public/img/aa_19.png" alt="a" width="13" height="101" /></div> -->
				<div class="c-right">
					<div class="main-operation-zone"> 
					</div>
				</div>
				<div style="clear:both">
				</div>
			</div>
			<div class="box-down0">
				<div class="box-down2">
				</div>
				南方电网 版权所有Copyright Notice 2011 All rights reserved.
			</div>
		</div>
	</body>
</html>