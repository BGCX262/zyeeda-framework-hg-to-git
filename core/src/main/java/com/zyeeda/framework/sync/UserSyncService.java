package com.zyeeda.framework.sync;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.service.Service;

public interface UserSyncService extends Service {
	
	public void persist(User user);
	
	public void update(User user);
	
	public void enable(String... ids);
	
	public void disable(String... ids);
	
//	public void setVisible(Boolean visible, String... ids);
}
