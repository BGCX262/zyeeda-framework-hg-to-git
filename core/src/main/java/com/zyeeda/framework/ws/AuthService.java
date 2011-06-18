package com.zyeeda.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.internal.DefaultPermissionManager;
import com.zyeeda.framework.managers.internal.DefaultRoleManager;
import com.zyeeda.framework.viewmodels.AuthVO;
import com.zyeeda.framework.viewmodels.PermissionVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/auth")
public class AuthService extends ResourceService {

	public AuthService(@Context ServletContext ctx) {
		super(ctx);
	}

	@GET
	@Path("/{id}/{role_id}")
	@Produces("application/json")
	public List<AuthVO> getPermissionById(@PathParam("id") String id,
			@PathParam("role_id") String roleId)
			throws XPathExpressionException, IOException {
		RoleManager roleMgr = new DefaultRoleManager(this
				.getPersistenceService());
		PermissionManager permissionMgr = new DefaultPermissionManager();
		List<PermissionVo> list = permissionMgr.findSubPermissionById(id);
		Role role = roleMgr.find(roleId);
		List<AuthVO> authVO = getAuthList(list, roleId, role
				.getPermissionList());
		return authVO;
	}

	public List<AuthVO> getAuthList(List<PermissionVo> list, String roleId,
			List<String> auth) {
		List<AuthVO> authList = new ArrayList<AuthVO>();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				PermissionVo permission = (PermissionVo) list.get(i);
				AuthVO authVO = new AuthVO(); 
				authVO.setId(permission.getId());
				authVO.setLabel("<a>" + permission.getName() + "</a>");
				authVO.setType("task");
				authVO.setTag(permission.getValue());
				// authVO.setValue(permission.getValue());
				for (String roleAuth : auth) {
					// System.out.print(roleAuth.trim().equals(permission.getValue().trim()));
					if (roleAuth.trim().equals(permission.getValue().trim())) {
						authVO.setCheckedAuth(true);
						break;
					} else {
						authVO.setCheckedAuth(false);
					}
				}
				if ("false".equals(permission.getIsHaveIO().toString())) {
					authVO.setIo("/rest/auth/" + permission.getId() + "/"
							+ roleId);
					authVO.setLeaf(false);
				} else {
					authVO.setLeaf(true);
				}
				authList.add(authVO);
			}
		}
		return authList;
	}

}
