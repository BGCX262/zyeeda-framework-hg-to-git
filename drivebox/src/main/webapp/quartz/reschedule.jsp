<%@ page import="com.zyeeda.framework.utils.IocUtils" %>
<%@ page import="com.zyeeda.framework.scheduler.SchedulerService" %>
<%@ page import="org.apache.tapestry5.ioc.Registry" %>
<%@ page import="org.quartz.TriggerKey" %>
<%@ page import="org.quartz.Trigger" %>
<%@ page import="org.quartz.Scheduler" %>
<%@ page import="org.quartz.TriggerBuilder" %>
<%@ page import="org.quartz.SimpleScheduleBuilder" %>
<%@ page import="org.quartz.impl.matchers.GroupMatcher" %>

<%
Registry registry = IocUtils.getRegistry(application);
SchedulerService<?> schedulerSvc = registry.getService(SchedulerService.class);
Scheduler scheduler = (Scheduler) schedulerSvc.getScheduler();

Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey("test-trigger", "test-trigger-group"));
System.out.println(scheduler.getTriggerKeys(GroupMatcher.groupEquals("test-trigger-group")));
if (oldTrigger != null) {
	TriggerBuilder builder = oldTrigger.getTriggerBuilder();
	Trigger newTrigger = builder.withSchedule(
			SimpleScheduleBuilder.simpleSchedule()
			.withRepeatCount(3)
			.withIntervalInSeconds(1)).startNow().build();
	
	scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
} else {
	out.print("old trigger is null");
}
%>