<%@ page import="com.zyeeda.framework.knowledge.AbstractStatefulSessionCommand"%>
<%@ page import="org.drools.runtime.StatefulKnowledgeSession" %>
<%@ page import="org.drools.runtime.process.ProcessInstance" %>
<%@ page import="com.zyeeda.framework.FrameworkConstants" %>
<%@ page import="org.apache.tapestry5.ioc.Registry" %>
<%@ page import="com.zyeeda.framework.knowledge.KnowledgeService" %>
<%@ page import="com.zyeeda.framework.transaction.TransactionService" %>
<%@ page import="javax.transaction.UserTransaction" %>
<%@ page import="com.zyeeda.framework.persistence.PersistenceService" %>
<%@ page import="javax.persistence.EntityManagerFactory" %>
<%@ page import="javax.persistence.EntityManager" %>

<%!
class CustomCommand extends AbstractStatefulSessionCommand<String> {
	private static final long serialVersionUID = 803619017440949193L;

	public String execute(StatefulKnowledgeSession ksession) {
		//ProcessInstance pi = ksession.startProcess("com.zyeeda.system.TestFlow");
		ProcessInstance pi = ksession.startProcess("com.zyeeda.system.TestFlow");
		return "session id = " + ksession.getId() + " | process id = " + pi.getId();
	}
}
%>

<%
String sid = request.getParameter("sid");
String pid = request.getParameter("pid");

CustomCommand command = new CustomCommand();

Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
KnowledgeService ksvc = reg.getService(KnowledgeService.class);
out.print(ksvc.execute(command));
%>