package com.zyeeda.framework.knowledge;

import org.drools.runtime.StatefulKnowledgeSession;

public interface StatefulCommand<T> {

	T execute(StatefulKnowledgeSession ksession) throws Exception;
	
}
