package com.zyeeda.drivebox.managers.internal;

import com.zyeeda.drivebox.entities.Dictionary;
import com.zyeeda.drivebox.managers.DictionaryManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class DefaultDictionaryManager extends DefaultManager<Dictionary, String> implements DictionaryManager {

	public DefaultDictionaryManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

}
