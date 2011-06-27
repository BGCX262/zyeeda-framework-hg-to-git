package com.zyeeda.framework.ws;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.MenuManager;
import com.zyeeda.framework.managers.internal.DefaultMenuManager;
import com.zyeeda.framework.managers.internal.DefaultRoleManager;
import com.zyeeda.framework.viewmodels.MenuVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/menu")
public class MenuService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
		
	public MenuService(@Context ServletContext ctx) {
		super(ctx);
	}

	@GET
	@Path("/")
	@Produces("application/json")
	public List<MenuVo> getMenu()throws XPathExpressionException, IOException {
		String user = this.getSecurityService().getCurrentUser();
		MenuManager menuMgr = new DefaultMenuManager();

		DefaultRoleManager roleMgr = new DefaultRoleManager(this
				.getPersistenceService());
		List<String> rolesAuth = new ArrayList<String>();
		List<MenuVo> listMenu = new ArrayList<MenuVo>();
		List<Role> roles = new ArrayList<Role>();
		System.out.println("*******************" + user);
		roles = roleMgr.getRoleBySubject(user);	
		if(roles.size() == 1) {
			listMenu = menuMgr.getMenuListByPermissionAuth(roles.get(0).getPermissionList());
			return listMenu;
		}
		for(Role role:roles) {
			logger.debug("the value of the dept subject is = {}  ", role.getPermissions());
			for(String permission:role.getPermissionList()){
				if(rolesAuth.size() == 0){
					rolesAuth.add(permission);
					continue;
				}
				if(!(rolesAuth.contains(permission))){
					rolesAuth.add(permission);
				}
				
			}
		}		
		listMenu = menuMgr.getMenuListByPermissionAuth(rolesAuth);
		return listMenu;
	}
}
