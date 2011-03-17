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
		ProcessInstance pi = ksession.startProcess("com.zyeeda.system.TaskFlow");
		return "session id = " + ksession.getId() + " | process id = " + pi.getId();
	}
}
%>

<%
Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);

PersistenceService pSvc = reg.getService("hibernate-persistence-service-provider", PersistenceService.class);
EntityManagerFactory emf = pSvc.getSessionFactory();
EntityManager em = emf.createEntityManager();
EntityManager em2 = emf.createEntityManager();
System.out.println(em);
System.out.println(em2);

final TransactionService txSvc = reg.getService(TransactionService.class);
UserTransaction tx1 = txSvc.getTransaction();
UserTransaction tx2 = txSvc.getTransaction();
System.out.println(tx1.hashCode());
System.out.println(tx2.hashCode());

new Thread(new Runnable() {
	public void run() {
		try {
			UserTransaction tx3 = txSvc.getTransaction();
			UserTransaction tx4 = txSvc.getTransaction();
			System.out.println(tx3.hashCode());
			System.out.println(tx4.hashCode());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}).start();

KnowledgeService ksvc = reg.getService(KnowledgeService.class);
String result = ksvc.execute(new CustomCommand());
out.print(result);
%>