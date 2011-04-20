package com.zyeeda.framework.managers.internal;

import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.managers.DocumentManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class MongoDbDocumentManager extends DomainEntityManager<Document, String>
		implements DocumentManager {

	public MongoDbDocumentManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

}
