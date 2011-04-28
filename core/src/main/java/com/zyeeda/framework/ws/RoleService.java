package com.zyeeda.framework.ws;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.lang.StringUtils;

import com.googlecode.genericdao.search.Search;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.internal.RoleManagerImpl;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/roles")
public class RoleService extends ResourceService {

	public RoleService(ServletContext ctx) {
		super(ctx);
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
		}
		roleMgr.persist(role);
		this.getPersistenceService().getCurrentSession().flush();
		return role;
	}

	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public Role editRole(@FormParam("") Role role, String id) {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		role.setId(id);
		roleMgr.save(role);
		this.getPersistenceService().getCurrentSession().flush();
		return role;
	}

	@PUT
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
	@Path("/{id}/assignAuth/{path}")
	@Produces("application/json")
	public void assignRoleAuth(@PathParam("id") String id,
			@PathParam("path") String path) {
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		Role role = roleMgr.find(id);
		role.setPermissions(path);
	}

}
