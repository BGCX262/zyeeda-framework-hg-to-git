package com.zyeeda.framework.unittest.services;

import java.util.HashMap;
import java.util.Map;

//import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.testng.annotations.Test;

import com.zyeeda.framework.knowledge.AbstractStatefulSessionCommand;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.unittest.TestSuiteBase;

import static org.testng.Assert.*;

@Test
public class KnowledegeServiceTest extends TestSuiteBase {

	@Test(enabled = false)
	public void testSaveSessionInfo() throws Exception {
		KnowledgeService kSvc = getRegistry().getService(KnowledgeService.class);
		
		StartProcessCommand command = new StartProcessCommand();
		Map<String, Object> r1 = kSvc.execute(command);
		long pid = (Long) r1.get("pid");
		
		SignalEventCommand command2 = new SignalEventCommand();
		command2.setProcessId(pid);
		long r2 = kSvc.execute(command2);
		
		assertTrue(r2 == pid);
	}
	
	private class StartProcessCommand extends AbstractStatefulSessionCommand<Map<String, Object>> {

		@Override
		public Map<String, Object> execute(StatefulKnowledgeSession ksession) {
			int sid = ksession.getId();
			ProcessInstance pi = ksession.startProcess("com.zyeeda.system.TestFlow");
			
			org.drools.process.instance.ProcessInstance pi2 = (org.drools.process.instance.ProcessInstance) pi;
			System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" + pi2.getWorkingMemory());
			
			//pi.signalEvent("signal", "goon");
			
			long pid = pi.getId();
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("sid", sid);
			result.put("pid", pid);
			
			return result;
		}
		
	}
	
	private class SignalEventCommand extends AbstractStatefulSessionCommand<Long> {

		private long processId;
		
		public void setProcessId(long processId) {
			this.processId = processId;
		}
		
		@Override
		public Long execute(StatefulKnowledgeSession ksession) {
			ProcessInstance pi = ksession.getProcessInstance(this.processId);
			
			System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" + pi);
			
			org.drools.process.instance.ProcessInstance pi2 = (org.drools.process.instance.ProcessInstance) pi;
			System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" + pi2.getWorkingMemory());
			
			pi.signalEvent("signal", "goon");
			return pi.getId();
		}
		
	}
}
