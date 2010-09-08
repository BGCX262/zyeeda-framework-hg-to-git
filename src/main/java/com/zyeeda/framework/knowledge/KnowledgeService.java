package com.zyeeda.framework.knowledge;

import com.zyeeda.framework.service.Service;

public interface KnowledgeService<T> extends Service {

	public T getKnowledgeBase();
	
}
