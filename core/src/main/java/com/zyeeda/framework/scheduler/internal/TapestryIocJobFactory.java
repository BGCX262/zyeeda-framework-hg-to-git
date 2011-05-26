package com.zyeeda.framework.scheduler.internal;

import org.apache.tapestry5.ioc.ServiceResources;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.utils.IocUtils;

public class TapestryIocJobFactory implements JobFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(TapestryIocJobFactory.class);
	
	private ServiceResources resources;
	
	public TapestryIocJobFactory(ServiceResources resources) {
		this.resources = resources;
	}

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		JobDetail detail = bundle.getJobDetail();
		Class<? extends Job> jobClass = detail.getJobClass();
		logger.debug("Fetch job class {} from Tapestry IoC container", jobClass.getSimpleName());
		return this.resources.getService(IocUtils.getServiceId(jobClass), Job.class);
	}

}
