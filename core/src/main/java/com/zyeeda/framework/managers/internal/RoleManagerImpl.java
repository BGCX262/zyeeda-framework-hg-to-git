package com.zyeeda.framework.managers.internal;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class RoleManagerImpl extends DomainEntityManager<Role, String>
		implements RoleManager {


	public RoleManagerImpl(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

	
}
