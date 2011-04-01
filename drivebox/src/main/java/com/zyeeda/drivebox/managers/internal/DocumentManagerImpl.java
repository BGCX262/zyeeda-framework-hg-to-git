package com.zyeeda.drivebox.managers.internal;

import com.zyeeda.drivebox.entities.Document;
import com.zyeeda.drivebox.managers.DocumentManager;
import com.zyeeda.framework.managers.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class DocumentManagerImpl extends DomainEntityManager<Document, String> implements DocumentManager {

	public DocumentManagerImpl(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

}
