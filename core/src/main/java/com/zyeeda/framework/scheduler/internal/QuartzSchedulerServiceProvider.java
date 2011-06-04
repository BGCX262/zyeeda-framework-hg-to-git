package com.zyeeda.framework.scheduler.internal;

import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.zyeeda.framework.scheduler.SchedulerService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("quartz-scheduler-serivce")
@Marker(Primary.class)
public class QuartzSchedulerServiceProvider extends AbstractService implements SchedulerService<Scheduler> {

	private ServiceResources resources;
	private SchedulerFactory schedulerFac;
	private Scheduler scheduler;
	
	public QuartzSchedulerServiceProvider(
			ServiceResources resources,
			RegistryShutdownHub shutdownHub) throws SchedulerException {
		super(shutdownHub);
		
		this.resources = resources;
		this.init();
	}
	
	private void init() throws SchedulerException {
		this.schedulerFac = new StdSchedulerFactory();
		this.scheduler = this.schedulerFac.getScheduler();
		this.scheduler.setJobFactory(new TapestryIocJobFactory(this.resources));
	}
	
	@Override
	public void start() throws SchedulerException {
		this.scheduler.start();
	}
	
	@Override
	public void stop() throws SchedulerException {
		this.scheduler.shutdown();
	}

	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}

}
