package com.zyeeda.framework.sync.internal;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.ws.base.ResourceService;

public class UserSyncThreadPool extends ResourceService {

	private UserSyncService userSynchService = null;
	public UserSyncThreadPool(ServletContext ctx) {
		super(ctx);
		
		userSynchService = this.getUserSynchService();
	}

	private static int produceTaskSleepTime = 2;
	private static int produceTaskMaxNumber = 9;
	private static final String USER_SYNCH_ADD_TASK = "add";
	private static final String USER_SYNCH_UPDATE_TASK = "update";
	private static final String USER_SYNCH_SET_VISIBLE_TASK = "setVisible";

	public static void main(String[] args) {

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 3, 3,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		for (int i = 1; i <= produceTaskMaxNumber; i++) {
			try {
				String task = "task@ " + i;
				System.out.println("put " + task);
				threadPool.execute(new ThreadPoolTask(task));

				Thread.sleep(produceTaskSleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class ThreadPoolTask implements Runnable, Serializable {
		private static final long serialVersionUID = 0;
		private String taskType;

		ThreadPoolTask(String taskType) {
			this.taskType = taskType;
		}
		
		public Object getTaskType() {
			return this.taskType;
		}

		public void run() {
			if (UserSyncThreadPool.USER_SYNCH_ADD_TASK.equals(this.taskType)) {
				
			} else if (UserSyncThreadPool.USER_SYNCH_UPDATE_TASK.equals(this.taskType)) {
				
			} else if (UserSyncThreadPool.USER_SYNCH_SET_VISIBLE_TASK.equals(this.taskType)) {
				
			}
		}
	}
}