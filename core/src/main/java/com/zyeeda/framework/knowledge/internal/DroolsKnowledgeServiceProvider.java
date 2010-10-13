package com.zyeeda.framework.knowledge.internal;

import javax.persistence.EntityManagerFactory;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.workitem.wsht.WSHumanTaskHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.task.service.TaskServer;
import org.drools.task.service.TaskService;
import org.drools.task.service.mina.MinaTaskServer;
import org.slf4j.Logger;

import com.zyeeda.framework.ioc.DroolsTask;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.knowledge.StatefulCommand;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.transaction.TransactionService;

@ServiceId("DroolsKnowledgeServiceProvider")
@Marker(Primary.class)
public class DroolsKnowledgeServiceProvider extends AbstractService implements KnowledgeService {
	
	//private static final String SERVICE_PROVIDER_NAME = "drools-knowledge-service-provider";
	
	private final PersistenceService defaultPersistenceSvc;
	private final PersistenceService droolsTaskPersistenceSvc;
	private final TransactionService txSvc;
	
	private KnowledgeBase kbase;
	private TaskService taskService;
	private TaskServer taskServer;
	
	public DroolsKnowledgeServiceProvider(
			@DroolsTask PersistenceService droolsTaskPersistenceSvc,
			@Primary PersistenceService defaultPersistenceSvc,
			@Primary TransactionService txSvc,
			Logger logger, RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
		
		this.defaultPersistenceSvc = defaultPersistenceSvc;
		this.droolsTaskPersistenceSvc = droolsTaskPersistenceSvc;
		this.txSvc = txSvc;
	}
	
	@Override
	public void start() throws Exception {
		// add knowledge changeset
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("META-INF/drools-changeset.xml"), ResourceType.CHANGE_SET);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			if (this.getLogger().isErrorEnabled()) {
				for (KnowledgeBuilderError error: errors) {
					this.getLogger().error(error.toString());
				}
			}
			
			throw new IllegalArgumentException("Could not parse knowledge changeset.");
		}

		this.kbase = kbuilder.newKnowledgeBase();
		this.kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		// start task server
		EntityManagerFactory emf = this.droolsTaskPersistenceSvc.getSessionFactory();
		this.taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		this.taskServer = new MinaTaskServer(this.taskService);
		Thread taskServerThread = new Thread(this.taskServer, "DroolsTaskServer");
		taskServerThread.start();
	}
	
	@Override
	public void stop() throws Exception {
		if (this.taskServer != null) {
			this.taskServer.stop();
		}
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.kbase;
	}
	
	@Override
	public TaskService getTaskService() {
		return this.taskService;
	}
	
	@Override
	public <T> T execute(StatefulCommand<T> command) throws Exception {
		StatefulKnowledgeSession ksession = null;
		KnowledgeRuntimeLogger logger = null;
		
		try {
			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.defaultPersistenceSvc.getSessionFactory());
			env.set(EnvironmentName.TRANSACTION, this.txSvc.getTransaction());
			
			ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
			//ksession = this.kbase.newStatefulKnowledgeSession();
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
			logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			return command.execute(ksession);
		} finally {
			if (logger != null) {
				logger.close();
			}
			if (ksession != null) {
				ksession.dispose();
			}
		}
	}

}
