package com.zyeeda.framework.managers.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.entities.Menu;
import com.zyeeda.framework.entities.Permission;
import com.zyeeda.framework.managers.MenuManager;
import com.zyeeda.framework.managers.PermissionManager;

public class MenuManagerImpl implements MenuManager {

	public List<Menu> getMenuListByPermissionAuth(Set<String> authList)
			throws XPathExpressionException, IOException {
		
		PermissionManager permissionMgr = new PermissionManagerImpl();
		List<Menu> listMenu = new ArrayList<Menu>();
		Map<String, Menu> menuMap = new HashMap<String, Menu>();
		String	root = null;
		for (String auth : authList) {
			Permission childPermission = permissionMgr.getPermissionByPath(auth);
			Menu childMenu = null;
			if (childPermission != null) {
				childMenu = this.convertPermission2Menu(childPermission);
				if (!menuMap.containsKey(childMenu.getAuth())) {
					menuMap.put(childMenu.getAuth(), childMenu);
				}
			} 
			Permission parentPermission = permissionMgr.getParentPermissionByPath(auth);
			if (parentPermission == null) {
				listMenu.add(menuMap.get(childMenu.getAuth()));
				continue;
			}
			if (menuMap.containsKey(parentPermission.getValue())) {
				Menu menuKey = menuMap.get(parentPermission.getValue());
				menuKey.getPermissionSet().add(childMenu);
				continue;
			}
			Menu parentMenu = this.convertPermission2Menu(parentPermission);
			parentMenu.getPermissionSet().add(childMenu);
			menuMap.put(parentPermission.getValue(), parentMenu);
			while (parentPermission != null) {
				parentMenu = new Menu();
			    String authKey = parentPermission.getValue();
				parentPermission = permissionMgr.getParentPermissionByPath(parentPermission.getValue());
					if(parentPermission != null){
						parentMenu.setAuth(parentPermission.getValue());
						parentMenu.setId(parentPermission.getId());
						parentMenu.setName(parentPermission.getName());
							if (menuMap.get(parentMenu.getAuth()) == null) {
								menuMap.put(parentMenu.getAuth(), parentMenu);
								parentMenu.getPermissionSet().add(menuMap.get(authKey));
							} else if(menuMap.get(parentMenu.getAuth()) != null) {
								Menu menuKey = menuMap.get(parentMenu.getAuth());
								menuKey.getPermissionSet().add(menuMap.get(authKey));
								break;
							}
					} else {
						root = authKey;
						listMenu.add(menuMap.get(root));
					} 
			}
		}
		return listMenu;
	}
	
	private Menu convertPermission2Menu(Permission permission) {
		Menu menu = new Menu();
		menu.setAuth(permission.getValue());
		menu.setId(permission.getId());
		menu.setName(permission.getName());
		return menu;
	}
}
