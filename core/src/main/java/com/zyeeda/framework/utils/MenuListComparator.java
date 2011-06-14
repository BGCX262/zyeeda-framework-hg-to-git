package com.zyeeda.framework.utils;

import java.util.Comparator;

import com.zyeeda.framework.viewmodels.MenuVo;
import com.zyeeda.framework.viewmodels.PermissionVo;

public class MenuListComparator implements Comparator {
	

	@Override
	public int compare(Object o1, Object o2) {
		MenuVo menu = (MenuVo)o1;
		MenuVo menu1 = (MenuVo)o2;
		String menuOrder1 = menu.getOrderBy();
		String menuOrder2 = menu.getOrderBy();
		int flag = 0;
		if(menuOrder1 != null && menuOrder2 != null){
			flag = menu.getOrderBy().compareTo(menu1.getOrderBy());
		}
		return flag;
	}

	
}
