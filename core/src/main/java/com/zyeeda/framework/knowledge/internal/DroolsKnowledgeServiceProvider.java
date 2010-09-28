package com.zyeeda.framework.knowledge.internal;

import javax.persistence.EntityManagerFactory;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.task.service.TaskService;
import org.drools.task.service.mina.MinaTaskServer;
import org.slf4j.Logger;

import com.zyeeda.framework.ioc.DroolsTask;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("DroolsKnowledgeServiceProvider")
@Marker(Primary.class)
public class DroolsKnowledgeServiceProvider extends AbstractService implements KnowledgeService {
	
	private final PersistenceService droolsTaskPersistenceSvc;
	
	private KnowledgeBase kbase;
	private TaskService taskService;
	private Thread taskServerThread;
	
	public DroolsKnowledgeServiceProvider(
			@DroolsTask PersistenceService droolsTaskPersistenceSvc,
			Logger logger, RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
		
		this.droolsTaskPersistenceSvc = droolsTaskPersistenceSvc;
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("drools-changeset.xml"), ResourceType.CHANGE_SET);
		if (kbuilder.hasErrors()) {
			throw new RuntimeException(kbuilder.getErrors().toString());
		}
		this.kbase = kbuilder.newKnowledgeBase();
	}
	
	@Override
	public void start() throws Exception {
		EntityManagerFactory emf = this.droolsTaskPersistenceSvc.getSessionFactory();
		this.taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		MinaTaskServer server = new MinaTaskServer(this.taskService);
		this.taskServerThread = new Thread(server);
		this.taskServerThread.start();
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.kbase;
	}
	
	@Override
	public TaskService getTaskService() {
		return this.taskService;
	}

}
