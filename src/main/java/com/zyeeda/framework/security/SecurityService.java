package com.zyeeda.framework.security;

import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.service.Service;


public interface SecurityService<T> extends Service {

	public T getSecurityManager();
	
	public RoleManager getRoleManager();

}
