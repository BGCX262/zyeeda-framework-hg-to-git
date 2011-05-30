package com.zyeeda.framework.ws;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.genericdao.search.Search;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.RoleManagerImpl;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/roles")
public class RoleService extends ResourceService {
	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public RoleService(ServletContext ctx) {
		super(ctx);
	}

	
	public List<Role> getRoleBySubject(String subject){
		EntityManager session = (EntityManager) this.getPersistenceService();// persistenceSvc.openSession();
		List<Role> roleList = session.createNamedQuery("getRolesBySubject", Role.class).getResultList();
		Search search = new Search();
		search.addFilterEqual("subject", subject);
		return roleList;
	}
	
	
	@GET
	@Path("/")
	@Produces("application/json")
	public List<Role> getRoles() {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		return roleMgr.findAll();
	}

	@POST
	@Path("/")
	@Produces("application/json")
	public Role creatRole(@FormParam("") Role role) {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		String name = role.getName();
		Search search = new Search();
		search.addFilterEqual("name", name);
		List<Role> list = roleMgr.search(search);
		if (list.size() > 0) {
			return null;
		}else{
			roleMgr.persist(role);
			this.getPersistenceService().getCurrentSession().flush();
		}
		return roleMgr.find(role.getId());
	}

	@PUT
	@Path("/")
	@Produces("application/json")
	public Role editRole(@FormParam("") Role role) {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		Role setRole=roleMgr.find(role.getId());
		Role newRole=roleMgr.save(setRole);
		this.getPersistenceService().getCurrentSession().flush();
		return newRole;
	}

	@POST
	@Path("/{id}/assign_user/{names}")
	@Produces("application/json")
	public void assignRoleUser(@PathParam("id") String id,
			@PathParam("names") String names) {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		Role role = roleMgr.find(id);
		if(StringUtils.isNotBlank(names)){
			String[] usersName = names.split(",");
			for (int i = 0; i < usersName.length; i++) {
				role.getSubjects().add(usersName[i]);
			}
		}
	}

	@PUT
	@Path("/{id}/assignAuth/{auth}")
	@Produces("application/json")
	public Role assignRoleAuth(@PathParam("id") String id,
			@PathParam("auth") String auth) {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		Role role = roleMgr.find(id);
		role.setPermissions(auth);
		Role newRole=roleMgr.merge(role);
		return newRole;
	}
}
