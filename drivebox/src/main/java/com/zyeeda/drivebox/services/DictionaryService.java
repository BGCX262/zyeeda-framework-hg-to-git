package com.zyeeda.drivebox.services;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.tapestry5.ioc.Registry;

import com.zyeeda.drivebox.entities.Priority;
import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.entities.Dictionary;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.HibernatePersistenceServiceProvider;
import com.zyeeda.framework.utils.IocUtils;

public class DictionaryService {

	@GET
	@Path("/dict/{name}")
	public Dictionary getDictionary(@PathParam("name") String name) {
		throw new RuntimeException(name);
	}
	
	@POST
	@Path("/dicts")
	public Dictionary createDictionary(@Context ServletContext ctx, @FormParam("") Dictionary dict) {
		Registry reg = (Registry) ctx.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		PersistenceService persistenceSvc = reg.getService(IocUtils.getServiceId(HibernatePersistenceServiceProvider.class), PersistenceService.class);
		EntityManager session = persistenceSvc.openSession();
		Priority p = new Priority();
		p.setName(dict.getName());
		p.setValue(dict.getValue());
		session.persist(p);
		session.flush();
		return session.find(Dictionary.class, p.getId());
	}
}
