package com.zyeeda.framework.sync;

import com.zyeeda.framework.entities.User;

public interface UserSyncService {
	public void persist(User user);
	public void update(User user);
	public void setVisible(Boolean visible, String ...ids);
}
