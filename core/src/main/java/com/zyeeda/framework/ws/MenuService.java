package com.zyeeda.framework.ws;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.entities.Menu;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.MenuManager;
import com.zyeeda.framework.managers.internal.MenuManagerImpl;
import com.zyeeda.framework.managers.internal.RoleManagerImpl;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/menu")
public class MenuService extends ResourceService {

	public MenuService(@Context ServletContext ctx) {
		super(ctx);
	}

	@GET
	@Path("/{roleId}")
	@Produces("application/json")
	public List<Menu> getMenu(@PathParam("roleId") String id)
			throws XPathExpressionException, IOException {
		List<Menu> listMenu = null;
		MenuManager menuMgr = new MenuManagerImpl();
		RoleManagerImpl roleMgr = new RoleManagerImpl(this
				.getPersistenceService());
		Role role = roleMgr.find(id);
		Set<String> auth = role.getPermissionSet();
		return listMenu;
	}

	

}
