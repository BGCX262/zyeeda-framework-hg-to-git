<%@ page import="com.zyeeda.framework.FrameworkConstants" %>
<%@ page import="org.apache.tapestry5.ioc.Registry" %>
<%@ page import="com.zyeeda.framework.persistence.PersistenceService" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="com.zyeeda.framework.entities.Role" %>

<%
Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
PersistenceService svc = reg.getService("hibernate-persistence-service-provider", PersistenceService.class);
EntityManager em = svc.getCurrentSession();

Role role = em.find(Role.class, "1");
role.getSubjects().add("aeinstein");

em.persist(role);
%>