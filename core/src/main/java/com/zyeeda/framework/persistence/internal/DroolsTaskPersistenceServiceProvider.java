package com.zyeeda.framework.persistence.internal;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.ejb.Ejb3Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.ioc.annotations.DroolsTaskPersistence;
import com.zyeeda.framework.persistence.PersistenceService;

@ServiceId("drools-task-persistence-service")
@Marker(DroolsTaskPersistence.class)
public class DroolsTaskPersistenceServiceProvider extends AbstractPersistenceServiceProvider
		implements PersistenceService {
	
	private final static Logger logger = LoggerFactory.getLogger(DroolsTaskPersistenceServiceProvider.class);

	private final static String PERSISTENCE_UNIT_NAME = "drools-task";
	//private final static String DROOLS_TASK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME = "Drools-Task-Entity-Classes";
	
	public DroolsTaskPersistenceServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
	}
	
	@Override
	public void start() throws Exception {
		Ejb3Configuration config = new Ejb3Configuration().configure(PERSISTENCE_UNIT_NAME, null);
		//this.addMappingClasses(config, DROOLS_TASK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME);
		this.setSessionFactory(config.buildEntityManagerFactory());
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
