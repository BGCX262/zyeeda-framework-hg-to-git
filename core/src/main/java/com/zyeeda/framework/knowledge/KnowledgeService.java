package com.zyeeda.framework.knowledge;

import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.task.service.TaskService;

import com.zyeeda.framework.service.Service;

public interface KnowledgeService extends Service {

	public KnowledgeBase getKnowledgeBase();
	
	public TaskService getTaskService();
	
	public <T> T execute(StatefulKnowledgeSessionCommand<T> command) throws Exception;
	
}
