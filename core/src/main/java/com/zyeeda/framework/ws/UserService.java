package com.zyeeda.framework.ws;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/users")
public class UserService extends ResourceService {
	
//	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public UserService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent:.*}")
	@Produces("application/json")
	public User createUser(@FormParam("") User user, @PathParam("parent") String parent) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		if (userMgr.findById(user.getId()) != null ) {
			throw new RuntimeException("账号不能重复");
		} else {
			user.setDepartmentName(parent);
			userMgr.persist(user);
			user = userMgr.findById(user.getId());
			userSyncService.persist(user);
			return user;
		}
	}
	
	@DELETE
	@Path("/{id}")
	public void removeUser(@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		userMgr.remove(id);
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public User editUser(@FormParam("") User user, @PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		user.setDeptFullPath(id);
		String uid = user.getDeptFullPath().substring(user.getDeptFullPath().indexOf("=") + 1, 
				user.getDeptFullPath().indexOf(","));
		if (!uid.equals(user.getId())) {
			throw new RuntimeException("不能修改账号");
		} else {
			userMgr.update(user);
			user = userMgr.findById(user.getId());
			userSyncService.update(user);
			return user;
		}
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public User getUserById(@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return userMgr.findById(id.substring(id.indexOf("=") + 1, id.indexOf(",")));
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<UserVo> getUserListByName(@PathParam("name") String name) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return UserService.fillUserListPropertiesToVo(userMgr.findByName(name));
	}
	
	@GET
	@Path("/userList/{deptId}")
	@Produces("application/json")
	public List<UserVo> getUserListByDepartmentId(@PathParam("deptId") String deptId) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(deptId));
	}
	
	@PUT
	@Path("/{id}/update_password")
	@Produces("application/json")
	public User updatePassword(@PathParam("id") String id, @FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword)  throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		User u = userMgr.findById(id.substring(id.indexOf("=") + 1, id.indexOf(",")));
		if (("{MD5}" + oldPassword).equals(u.getPassword())) {
			if (!newPassword.equals(oldPassword)) {
				userMgr.updatePassword(id, newPassword);
			}
		} else {
			throw new RuntimeException("旧密码输入错误");
		}
		return userMgr.findById(id.substring(id.indexOf("=") + 1, id.indexOf(",")));
	}
	
	@PUT
	@Path("/{id}/enable")
	@Produces("application/json")
	public User enable(@PathParam("id") String id, @FormParam("status") Boolean visible)
			throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		userMgr.enable(id);
		userSyncService.enable(id);
		return userMgr.findById(id.substring(id.indexOf("=") + 1, id.indexOf(",")));
	}
	
	@PUT
	@Path("/{id}/unenable")
	@Produces("application/json")
	public User disable(@PathParam("id") String id, @FormParam("status") Boolean visible)
	 		throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		userMgr.disable(id);
		userSyncService.disable(id);
		return userMgr.findById(id.substring(id.indexOf("=") + 1, id.indexOf(",")));
	}
	
	@POST
	@Path("/{id}")
	@Produces("application/json")
	public void uploadPhoto(@Context HttpServletRequest request, @PathParam("id") String id) throws Throwable {
		InputStream in = request.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    byte[] b = new byte[1024];  
	    int len = 0;  
	  
	    while ((len = in.read(b, 0, 1024)) != -1) {  
	        baos.write(b, 0, len);  
	    }  
	    baos.flush();
	  
//	    byte[] bytes = baos.toByteArray();
	    LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		User user = new User();
		user.setId("china");
//		user.setPhoto(bytes);
		
		userMgr.update(user);
	}
	
//	private User setVisible(String id, Boolean visible)  throws UserPersistException {
//		LdapService ldapSvc = this.getLdapService();
//		UserSyncService userSyncService = this.getUserSynchService();
//		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
//		
//		userMgr.setVisible(visible, id);
//		userSyncService.enable(id);
//		return userMgr.findById(id.substring(id.indexOf("=") + 1, id.indexOf(",")));
//	}
	
	public static UserVo fillUserPropertiesToVo(User user) {
		UserVo userVo = new UserVo();

		userVo.setId(user.getId());
		userVo.setType("io");
		userVo.setLabel("<a>" + user.getId() + "<a>");
		userVo.setCheckName(user.getId());
		userVo.setLeaf(true);
		userVo.setUid(user.getId());
		userVo.setDeptFullPath(user.getDeptFullPath());
		userVo.setKind("user");

		return userVo;
	}
	
	public static UserVo fillUserPropertiesToVo(User user, String type) {
		UserVo userVo = new UserVo();

		userVo.setId(user.getId());
		userVo.setType(type);
		userVo.setLabel("<a>" + user.getId() + "<a>");
		userVo.setCheckName(user.getId());
		userVo.setLeaf(true);
		userVo.setUid(user.getId());
		userVo.setDeptFullPath(user.getDeptFullPath());
		userVo.setKind("user");

		return userVo;
	}

	public static List<UserVo> fillUserListPropertiesToVo(List<User> userList) {
		List<UserVo> userVoList = new ArrayList<UserVo>(userList.size());
		UserVo userVo = null;
		for (User user : userList) {
			userVo = UserService.fillUserPropertiesToVo(user);
			userVoList.add(userVo);
		}
		return userVoList;
	}

	public static List<UserVo> fillUserListPropertiesToVo(List<User> userList, String type) {
		List<UserVo> userVoList = new ArrayList<UserVo>(userList.size());
		UserVo userVo = null;
		for (User user : userList) {
			userVo = UserService.fillUserPropertiesToVo(user, type);
			userVoList.add(userVo);
		}
		return userVoList;
	}
	
}
