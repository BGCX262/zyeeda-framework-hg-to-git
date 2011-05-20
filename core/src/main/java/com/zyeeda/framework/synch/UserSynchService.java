package com.zyeeda.framework.synch;

import com.zyeeda.framework.entities.User;

public interface UserSynchService {
	public void persist(User user);
	public void update(User user);
	public void setVisible(Boolean visible, String ...ids);
}
