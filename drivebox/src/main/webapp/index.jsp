<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.apache.tapestry5.ioc.Registry" %>

<%@page import="com.zyeeda.framework.FrameworkConstants" %>
<%@page import="com.zyeeda.framework.template.TemplateService" %>

<%
Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
TemplateService tplSvc = reg.getService(TemplateService.class);
tplSvc.paint("/index.ftl", out);
%>
