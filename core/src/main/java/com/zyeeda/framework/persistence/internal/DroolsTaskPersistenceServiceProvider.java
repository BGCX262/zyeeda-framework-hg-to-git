package com.zyeeda.framework.persistence.internal;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.ejb.Ejb3Configuration;
import org.slf4j.Logger;

import com.zyeeda.framework.ioc.annotations.DroolsTask;
import com.zyeeda.framework.persistence.PersistenceService;

@ServiceId("drools-task-persistence-service-provider")
@Marker(DroolsTask.class)
public class DroolsTaskPersistenceServiceProvider extends AbstractPersistenceServiceProvider
		implements PersistenceService {

	private final static String PERSISTENCE_UNIT_NAME = "drools-task";
	//private final static String DROOLS_TASK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME = "Drools-Task-Entity-Classes";
	
	public DroolsTaskPersistenceServiceProvider(Logger logger,
			RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
	}
	
	@Override
	public void start() throws Exception {
		Ejb3Configuration config = new Ejb3Configuration().configure(PERSISTENCE_UNIT_NAME, null);
		//this.addMappingClasses(config, DROOLS_TASK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME);
		this.setSessionFactory(config.buildEntityManagerFactory());
	}

}
