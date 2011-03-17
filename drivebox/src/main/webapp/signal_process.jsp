<%@ page import="com.zyeeda.framework.knowledge.AbstractStatefulSessionCommand"%>
<%@ page import="org.drools.runtime.StatefulKnowledgeSession" %>
<%@ page import="org.drools.runtime.process.ProcessInstance" %>
<%@ page import="com.zyeeda.framework.FrameworkConstants" %>
<%@ page import="org.apache.tapestry5.ioc.Registry" %>
<%@ page import="com.zyeeda.framework.knowledge.KnowledgeService" %>
<%@ page import="org.drools.common.InternalWorkingMemory" %>
<%@ page import="org.drools.impl.KnowledgeBaseImpl" %>
<%@ page import="org.drools.command.impl.GenericCommand" %>
<%@ page import="org.drools.command.Context" %>
<%@ page import="org.drools.command.impl.KnowledgeCommandContext" %>

<%!
class CustomCommand extends AbstractStatefulSessionCommand<Void> {
	
	private static final long serialVersionUID = 803619017440949193L;
	
	private long processId;
	
	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public Void execute(StatefulKnowledgeSession ksession) {
		
		ksession.signalEvent("signal", "goon", this.processId);
		
		/*ksession.execute(new GenericCommand<Void>() {
			
			public Void execute(Context ctx) {
		        StatefulKnowledgeSession ks = ((KnowledgeCommandContext) ctx).getStatefulKnowledgesession();
		        ProcessInstance pi = ks.getProcessInstance(processId);
		        pi.signalEvent("signal", "goon");
		        
		        return null;
			}
		});*/
		
		return null;
		
		/*System.out.println("*************************************" + ksession);
		//System.out.println("*************************************" + ((InternalWorkingMemory) ksession).getRuleBase());
		ProcessInstance pi = ksession.getProcessInstance(this.processId);
		System.out.println("*************************************" + pi);
		System.out.println("heiwo");
		System.out.println("*************************************" + ksession);
		System.out.println("*************************************" + ksession.getKnowledgeBase());
		System.out.println("*************************************" + ((KnowledgeBaseImpl) ksession.getKnowledgeBase()).ruleBase);
		pi = ksession.getProcessInstance(this.processId);
		
		org.drools.process.instance.ProcessInstance pi2 = (org.drools.process.instance.ProcessInstance)pi;
		if (pi2.getWorkingMemory() == null) {
			System.out.println("hahahaha it 		ksession.execute(new GenericCommand<Void>() {
					
					public Void execute(Context ctx) {
				        StatefulKnowledgeSession ks = ((KnowledgeCommandContext) ctx).getStatefulKnowledgesession();
				        ProcessInstance pi = ks.getProcessInstance(processId);
				        pi.signalEvent("signal", "goon");
				        
				        return null;
					}
				});is null");
		}
		
		pi.signalEvent("signal", "goon");
		
		return "OK";*/
	}
}
%>

<%
String sid = request.getParameter("sid");
String pid = request.getParameter("pid");

CustomCommand command = new CustomCommand();
command.setProcessId(Long.parseLong(pid));
command.setSessionId(Integer.parseInt(sid));

Registry reg = (Registry) application.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
KnowledgeService ksvc = reg.getService(KnowledgeService.class);
ksvc.execute(command);
%>