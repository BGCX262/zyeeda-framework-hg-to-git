package com.zyeeda.framework.persistence.internal;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.ejb.Ejb3Configuration;
import org.slf4j.Logger;

import com.zyeeda.framework.ioc.DroolsTask;
import com.zyeeda.framework.persistence.PersistenceService;

@ServiceId("DroolsTaskPersistenceServiceProvider")
@Marker(DroolsTask.class)
public class DroolsTaskPersistenceServiceProvider extends AbstractPersistenceServiceProvider
		implements PersistenceService {

	private final static String DROOLS_TASK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME = "Drools-Task-Entity-Classes";
	
	protected DroolsTaskPersistenceServiceProvider(Logger logger,
			RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
	}
	
	@Override
	public void start() throws Exception {
		Ejb3Configuration config = new Ejb3Configuration().configure("org.drools.task");
		this.addMappingClasses(config, DROOLS_TASK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME);
		this.setSessionFactory(config.buildEntityManagerFactory());
	}

}
