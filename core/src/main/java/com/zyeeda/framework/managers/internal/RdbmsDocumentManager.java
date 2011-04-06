package com.zyeeda.framework.managers.internal;

import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.managers.DocumentManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class RdbmsDocumentManager extends DomainEntityManager<Document, String> implements DocumentManager {

	public RdbmsDocumentManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

}
