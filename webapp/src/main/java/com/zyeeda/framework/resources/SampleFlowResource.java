package com.zyeeda.framework.resources;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.tapestry5.ioc.Registry;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.knowledge.StatefulKnowledgeSessionCommand;

@Path("sample")
public class SampleFlowResource {

	@GET
	@Produces("text/plain")
	public String start(@Context ServletContext context) throws Exception {
		Registry reg = (Registry) context.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		KnowledgeService ksvc = reg.getService(KnowledgeService.class);
		
		return ksvc.execute(new StatefulKnowledgeSessionCommand<String>() {

			private static final long serialVersionUID = 803619017440949193L;

			@Override
			public String execute(StatefulKnowledgeSession ksession) {
				ksession.startProcess("com.zyeeda.system.TestFlow");
				return "OK";
			}
		});	
	}
}
