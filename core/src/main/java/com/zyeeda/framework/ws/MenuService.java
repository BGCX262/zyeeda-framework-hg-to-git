package com.zyeeda.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Menu;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.MenuManager;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.MenuManagerImpl;
import com.zyeeda.framework.managers.internal.RoleManagerImpl;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/menu")
public class MenuService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);
		
	public MenuService(@Context ServletContext ctx) {
		super(ctx);
	}

	@GET
	@Path("/")
	@Produces("application/json")
	public List<Menu> getMenu()throws XPathExpressionException, IOException {
		String user = this.getSecurityService().getCurrentUser();
		MenuManager menuMgr = new MenuManagerImpl();
		RoleManagerImpl roleMgr = new RoleManagerImpl(this
				.getPersistenceService());
		Set<String> rolesAuth = new HashSet<String>();
		List<Role> roles = new ArrayList<Role>();
		roles = roleMgr.getRoleBySubject(user);
		for(Role role:roles){
			logger.debug("the value of the dept subject is = {}  ", role.getPermissions());
			for(String permission:role.getPermissionSet()){
				if(rolesAuth.size()>0){
					for(String haveAuth:rolesAuth){
						if(!(permission.equals(haveAuth))){
							rolesAuth.add(permission);
						}
					}
				} else {
					rolesAuth.add(permission);
				}
			}
		}
		//Set<String> permissionSet = role.getPermissionSet();
		List<Menu> listMenu = new ArrayList<Menu>();
		listMenu = menuMgr.getMenuListByPermissionAuth(rolesAuth);
		return listMenu;
	}


}
