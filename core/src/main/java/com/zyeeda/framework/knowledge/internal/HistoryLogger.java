package com.zyeeda.framework.knowledge.internal;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowLogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.event.KnowledgeRuntimeEventManager;

import com.zyeeda.framework.entities.ActionHistory;
import com.zyeeda.framework.entities.ProcessHistory;
import com.zyeeda.framework.managers.ActionHistoryManager;
import com.zyeeda.framework.managers.ProcessHistoryManager;
import com.zyeeda.framework.managers.internal.DefaultActionHistoryManager;
import com.zyeeda.framework.managers.internal.DefaultProcessHistoryManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class HistoryLogger extends WorkingMemoryLogger {
	
	private PersistenceService persistenceSvc;
	
	private ProcessHistoryManager pHisMgr;
	private ActionHistoryManager aHisMgr;
	
	public HistoryLogger(WorkingMemory workingMemory, PersistenceService persistenceSvc) {
		super(workingMemory);
		
		this.persistenceSvc = persistenceSvc;
		this.init();
	}
	
	public HistoryLogger(KnowledgeRuntimeEventManager session, PersistenceService persistenceSvc) {
		super(session);
		
		this.persistenceSvc = persistenceSvc;
		this.init();
	}
	
	private void init() {
		this.pHisMgr = new DefaultProcessHistoryManager(persistenceSvc);
		this.aHisMgr = new DefaultActionHistoryManager(persistenceSvc);
	}
	
	@Override
	public void logEventCreated(LogEvent logEvent) {
		switch (logEvent.getType()) {
			case LogEvent.BEFORE_RULEFLOW_CREATED: {
				RuleFlowLogEvent event = (RuleFlowLogEvent) logEvent;
				
				ProcessHistory history = new ProcessHistory();
				history.setProcessId(event.getProcessId());
				history.setName(event.getProcessName());
				history.setProcessInstanceId(event.getProcessInstanceId());
				this.pHisMgr.persist(history);
				break;
			}
			case LogEvent.AFTER_RULEFLOW_COMPLETED: {
				RuleFlowLogEvent event = (RuleFlowLogEvent) logEvent;
				
				ProcessHistory history = this.pHisMgr.findByProcessInstanceId(event.getProcessInstanceId());
				history.setEnded(true);
				this.pHisMgr.save(history);
				break;
			}
			case LogEvent.BEFORE_RULEFLOW_NODE_TRIGGERED: {
				RuleFlowNodeLogEvent event = (RuleFlowNodeLogEvent) logEvent;
				
				ProcessHistory proHist = this.pHisMgr.findByProcessInstanceId(event.getProcessInstanceId());
				proHist.setCurrentState(event.getNodeName());
				this.pHisMgr.save(proHist);
				
				ActionHistory actHist = new ActionHistory();
				actHist.setProcessId(event.getProcessId());
				actHist.setProcessName(event.getProcessName());
				actHist.setProcessInstanceId(event.getProcessInstanceId());
				actHist.setNodeId(event.getNodeId());
				actHist.setNodeInstanceId(Long.parseLong(event.getNodeInstanceId()));
				actHist.setName(event.getNodeName());
				this.aHisMgr.persist(actHist);
				break;
			}
			case LogEvent.BEFORE_RULEFLOW_NODE_EXITED: {
				RuleFlowNodeLogEvent event = (RuleFlowNodeLogEvent) logEvent;
				
				ProcessHistory proHist = this.pHisMgr.findByProcessInstanceId(event.getProcessInstanceId());
				this.pHisMgr.save(proHist);
				
				ActionHistory actHist = aHisMgr.findAlive(event.getProcessInstanceId(), Long.parseLong(event.getNodeInstanceId()));
				actHist.setAlive(false);
				this.aHisMgr.save(actHist);
				break;
			}
		default:
			// ignore
		}
		
		this.persistenceSvc.getCurrentSession().flush();
	}
	
}
