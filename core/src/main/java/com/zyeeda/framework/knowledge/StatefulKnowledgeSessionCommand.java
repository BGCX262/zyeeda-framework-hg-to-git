package com.zyeeda.framework.knowledge;

import org.drools.runtime.StatefulKnowledgeSession;

public interface StatefulKnowledgeSessionCommand<T> {

	public T execute(StatefulKnowledgeSession ksession);
	
}
