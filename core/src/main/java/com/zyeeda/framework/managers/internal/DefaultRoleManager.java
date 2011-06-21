package com.zyeeda.framework.managers.internal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class DefaultRoleManager extends DomainEntityManager<Role, String>
		implements RoleManager {
	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public DefaultRoleManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

	public  List<Role> getRoleBySubject(String subject){
		logger.debug("the value of the dept subject is = {}  ", subject);
		TypedQuery<Role> query = this.em().createNamedQuery("getRolesBySubject", Role.class);
		query.setParameter("subject", subject);
		List<Role> roleList = query.getResultList();
		return roleList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getRoleDistinct(String hql){
		List<Role> list = new ArrayList<Role>();
		TypedQuery<Role> createNativeQuery = (TypedQuery<Role>) this.em().createNativeQuery(hql, Role.class);
		TypedQuery<Role> query = createNativeQuery;
		 list = query.getResultList();
		return list;
	}

	
}
