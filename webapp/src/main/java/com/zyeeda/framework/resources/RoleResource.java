package com.zyeeda.framework.resources;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.tapestry5.ioc.Registry;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.aop.Transactional;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.HibernatePersistenceServiceProvider;
import com.zyeeda.framework.vos.Roles;
import com.zyeeda.framework.utils.IocUtils;

@Path("/hello")
public class RoleResource {

	@GET
	@Produces("application/json")
	@Transactional
	public Roles getRoles(@Context ServletContext context) {
		Registry reg = (Registry) context.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		//PersistenceService persistenceSvc = reg.getService(PersistenceService.class);
		PersistenceService persistenceSvc = reg.getService(IocUtils.getServiceId(HibernatePersistenceServiceProvider.class), PersistenceService.class);
		EntityManager session = persistenceSvc.openSession();
		List<Role> roleList = session.createNamedQuery("getRoles", Role.class).getResultList();
		Roles roles = new Roles();
		roles.setRole(roleList);
		return roles;
	}
	
}
