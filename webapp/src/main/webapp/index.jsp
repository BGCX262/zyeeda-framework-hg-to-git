<%@page import="org.apache.shiro.SecurityUtils" %>

Hello <%= SecurityUtils.getSubject().getPrincipal() %>!