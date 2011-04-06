package com.zyeeda.framework.managers.internal;

import com.zyeeda.framework.managers.DictionaryManager;
import com.zyeeda.framework.entities.Dictionary;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class DefaultDictionaryManager extends DomainEntityManager<Dictionary, String> implements DictionaryManager {

	public DefaultDictionaryManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

}
