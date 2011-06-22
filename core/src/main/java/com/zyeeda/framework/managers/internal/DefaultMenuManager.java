package com.zyeeda.framework.managers.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.managers.MenuManager;
import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.utils.MenuListComparator;
import com.zyeeda.framework.viewmodels.MenuVo;
import com.zyeeda.framework.viewmodels.PermissionVo;

public class DefaultMenuManager implements MenuManager {

	@SuppressWarnings("unchecked")
	public List<MenuVo> getMenuListByPermissionAuth(List<String> authList)
			throws XPathExpressionException, IOException {
		
		PermissionManager permissionMgr = new DefaultPermissionManager();
		List<MenuVo> listMenu = new ArrayList<MenuVo>();
		Map<String, MenuVo> menuMap = new LinkedHashMap<String, MenuVo>();
		String	root = null;
		for (String auth : authList) { 
			System.out.println("*************" + auth);
			PermissionVo childPermission = permissionMgr.getPermissionByPath(auth);
			MenuVo childMenu = null;
			if (childPermission != null) {
				childMenu = this.convertPermission2Menu(childPermission);
				if (!(menuMap.containsKey(childMenu.getAuth()))) {
					menuMap.put(childMenu.getAuth(), childMenu);
				}else{
					continue;
				}
			} 
			PermissionVo parentPermission = permissionMgr.getParentPermissionByPath(auth);
			if (parentPermission == null) {
					listMenu.add(menuMap.get(childMenu.getAuth()));
					continue;
			}
			if (menuMap.containsKey(parentPermission.getValue())) {
				MenuVo menuKey = menuMap.get(parentPermission.getValue());
				menuKey.getPermissionSet().add(childMenu);
				continue;
			}
			MenuVo parentMenu = this.convertPermission2Menu(parentPermission);
			parentMenu.getPermissionSet().add(childMenu);
			menuMap.put(parentPermission.getValue(), parentMenu);
			while (parentPermission != null) {
				parentMenu = new MenuVo();
			    String authKey = parentPermission.getValue();
				parentPermission = permissionMgr.getParentPermissionByPath(parentPermission.getValue());
					if(parentPermission != null){
						parentMenu.setAuth(parentPermission.getValue());
						parentMenu.setId(parentPermission.getId());
						parentMenu.setName(parentPermission.getName());
						parentMenu.setOrderBy(parentPermission.getOrderBy());
							if (!(menuMap.containsKey(parentMenu.getAuth()))) {
								parentMenu.getPermissionSet().add(menuMap.get(authKey));
								menuMap.put(parentMenu.getAuth(), parentMenu);
							} else {
								MenuVo menuKey = menuMap.get(parentMenu.getAuth());
								menuKey.getPermissionSet().add(menuMap.get(authKey));
								break;
							}
					} else {
						root = authKey;
						listMenu.add(menuMap.get(root));
					} 
			}
		}
		if(listMenu.size() > 0){
			MenuListComparator comparator = new MenuListComparator();
			Collections.sort(listMenu, comparator);
		}
		System.out.println("****************ssss" + listMenu.size());
		return listMenu;
	}
	
	private MenuVo convertPermission2Menu(PermissionVo permission) {
		MenuVo menu = new MenuVo();
		menu.setAuth(permission.getValue());
		menu.setId(permission.getId());
		menu.setName(permission.getName());
		menu.setOrderBy(permission.getOrderBy());
		return menu;
	}
	
}
