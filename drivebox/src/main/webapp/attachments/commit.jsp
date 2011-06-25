<%@ page import="org.apache.tapestry5.ioc.Registry" %>
<%@ page import="com.zyeeda.framework.FrameworkConstants" %>
<%@ page import="com.zyeeda.framework.nosql.MongoDbService" %>
<%@ page import="com.zyeeda.framework.managers.DocumentManager" %>
<%@ page import="com.zyeeda.framework.managers.internal.MongoDbDocumentManager" %>

<%
String foreignId = request.getParameter("foreignId");

Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
MongoDbService mongoSvc = reg.getService(MongoDbService.class);
DocumentManager docMgr = new MongoDbDocumentManager(mongoSvc);

System.out.println("count = " + docMgr.countBySuffixes("tangrui", foreignId, "doc", "docx"));

docMgr.replaceForeignId(foreignId, "tangrui");

out.print("OK");
%>