package com.zyeeda.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.xpath.XPathExpressionException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.managers.MenuManager;
import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.DefaultMenuManager;
import com.zyeeda.framework.managers.internal.DefaultPermissionManager;
import com.zyeeda.framework.managers.internal.DefaultRoleManager;
import com.zyeeda.framework.managers.internal.DefaultUserManager;
import com.zyeeda.framework.viewmodels.MenuAndPermission;
import com.zyeeda.framework.viewmodels.MenuVo;
import com.zyeeda.framework.viewmodels.PermissionVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/menu")
public class MenuService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
	private final static String ROAM_PERMISSION_FILE = "roamPermission.xml";

	public MenuService(@Context ServletContext ctx) {
		super(ctx);
	}

	@GET
	@Path("/")
	@Produces("application/json")
	public MenuAndPermission getMenu(@Context ServletContext ctx)throws XPathExpressionException, IOException, UserPersistException {
		String user = this.getSecurityService().getCurrentUser();
		UserManager userMgr = new DefaultUserManager(this.getPersistenceService());
		User userName = userMgr.findById(user);
		MenuManager menuMgr = new DefaultMenuManager();
		DefaultRoleManager roleMgr = new DefaultRoleManager(this
				.getPersistenceService());
		PermissionManager permissionMgr = new DefaultPermissionManager();
		//Session session = SecurityUtils.getSubject().get
		MenuAndPermission roleWithUserVo = new MenuAndPermission();
		roleWithUserVo.setUserName(userName.getUsername());
		List<MenuVo> listMenu = new ArrayList<MenuVo>();
		List<Role> roles = new ArrayList<Role>();
		roles = roleMgr.getRoleBySubject(user);	
		Set<String> authList = roleMgr.getListAuth(roles);
		Session session = SecurityUtils.getSubject().getSession();
		//Long begin = System.currentTimeMillis();
		for(String auth : authList) {
			PermissionVo permission = permissionMgr.getPermissionByPath(auth, ROAM_PERMISSION_FILE);
			if(permission != null) {
				roleWithUserVo.getListPermission().add(permission);
			}
		}
	//	Long end = System.currentTimeMillis();
//		System.out.println("************roam**************" + (end - begin));
		if(session.getAttribute("auth") == null){
			session.setAttribute("auth", authList);
		}
//		if(ctx.getAttribute("menuListToTree") != null) {//TODO;
//			ctx.setAttribute("menuListToTree", listMenu);
//		}
	    Long begin1 = System.currentTimeMillis();
		if(roles.size() == 1) {
			logger.debug("the value of the dept subject is = {}  ", roles.get(0).getPermissionsList());
			listMenu = menuMgr.getMenuListByPermissionAuth(roles.get(0).getPermissionsList());
			roleWithUserVo.getListMenu().addAll(listMenu);
			return roleWithUserVo;
		}
		Set<String> authMenuSet = roleMgr.getListMenuAuth(roles);
		List<String> menuList = new ArrayList<String>();
		menuList.addAll(authMenuSet);
		listMenu = menuMgr.getMenuListByPermissionAuth(menuList);
		roleWithUserVo.getListMenu().addAll(listMenu);
		Long end1 = System.currentTimeMillis();
		System.out.println("************permission**************" + (end1 - begin1));
		return roleWithUserVo;
	}
}
