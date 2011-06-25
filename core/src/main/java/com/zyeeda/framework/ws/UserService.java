package com.zyeeda.framework.ws;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;
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

import org.apache.commons.lang.StringUtils;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.SearchControlsFactory;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.utils.LdapEncryptUtils;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/users")
public class UserService extends ResourceService {
	
	public UserService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	private static String createUserDn(String parent, String id) {
		return "uid=" + id + "," + parent;
	}
	
	@POST
	@Path("/{parent:.*}")
	@Produces("application/json")
	public User persist(@FormParam("") User user, @PathParam("parent") String parent)
				   											throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<User> userList = userMgr.findByName(user.getId());
		if (userList != null && userList.size() > 0) {
			throw new RuntimeException("账号不能重复");
		} else {
			user.setDepartmentName(parent);
			user.setDeptFullPath(createUserDn(parent, user.getId()));
			userMgr.persist(user);
			user = userMgr.findById(user.getDeptFullPath());
			userSyncService.persist(user);
			return user;
		}
	}
	
	@DELETE
	@Path("/{id}")
	public String remove(@PathParam("id") String id,
						 @PathParam("cascade") Boolean cascade)
					throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		if (cascade != null) {
			userMgr.remove(id);
			return "{success: 'true'}";
		} else {
			Integer count = userMgr.getChildrenCountById(id, "(objectclass=*)");
			if (count > 0) {
				return "{\"success\": \"false\"}";
			} else {
				userMgr.remove(id);
				return "{\"success\": \"true\"}";
			}
		}
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public User update(@FormParam("") User user, @PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		String uid = id.substring(id.indexOf("=") + 1, id.indexOf(","));
		if (!uid.equals(user.getId())) {
			throw new RuntimeException("不能修改账号");
		} else {
			user.setDeptFullPath(id);
			userMgr.update(user);
			user = userMgr.findById(id);
			userSyncService.update(user);
			return user;
		}
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public User findById(@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return userMgr.findById(id);
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
	@Path("/search")
	@Produces("application/json")
	public String search(@FormParam("name") String name) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<User> userList = userMgr.search(name);
		StringBuffer buffer = new StringBuffer("{");
		buffer.append("\"totalRecords\":").append(userList.size())
	      	  .append(",").append("\"startIndex\":").append(0)
	      	  .append(",").append("\"pageSize\":").append(13)
	      	  .append(",").append("\"records\":[");
		for (User user : userList) {
			buffer.append("{\"id\":").append("\"").append(user.getId()).append("\"").append(",")
			      .append("\"username\":").append("\"").append(user.getUsername()).append("\"").append(",")
			      .append("\"mobile\":").append("\"").append(user.getMobile() == null ? "" : user.getMobile()).append("\"").append(",")
			      .append("\"email\":").append("\"").append(user.getEmail() == null ? "" : user.getEmail()).append("\"").append(",")
			      .append("\"status\":").append("\"").append(user.getStatus() == null ? "" : user.getStatus()).append("\"").append(",")
			      .append("\"parent\":").append("\"").append(user.getDepartmentName() == null ? "" : user.getDepartmentName()).append("\"").append(",")
			      .append("\"fullpath\":").append("\"").append(user.getDeptFullPath() == null ? "" : user.getDeptFullPath()).append("\"").append("},");
		}
		if (buffer.lastIndexOf(",") != -1 && userList.size() > 0) {
			buffer.deleteCharAt(buffer.lastIndexOf(","));
		}
		buffer.append("]}");
		
		return buffer.toString();
	}
	
	@GET
	@Path("/userList/{deptId}")
	@Produces("application/json")
	public List<UserVo> getUserListByDepartmentId(@PathParam("deptId") String deptId)
															throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(deptId));
	}
	
	@PUT
	@Path("/{id}/update_password")
	@Produces("application/json")
	public User updatePassword(@PathParam("id") String id,
							   @FormParam("oldPassword") String oldPassword,
							   @FormParam("newPassword") String newPassword)
						  throws UserPersistException,
						  		 UnsupportedEncodingException,
						  		 NoSuchAlgorithmException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		User u = userMgr.findById(id);
		String ldapPw = u.getPassword();
		String inputPw = oldPassword;
		if (LdapEncryptUtils.verifySHA(ldapPw, inputPw)) {
			if (!LdapEncryptUtils.verifySHA(ldapPw, newPassword)) {
				userMgr.updatePassword(id, newPassword);
			}
		} else {
			throw new RuntimeException("旧密码输入错误");
		}
		return userMgr.findById(id);
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
		return userMgr.findById(id);
	}
	
	@PUT
	@Path("/{id}/unenable")
	@Produces("application/json")
	public User disable(@PathParam("id") String id,
						@FormParam("status") Boolean visible)
	 		       throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		userMgr.disable(id);
		userSyncService.disable(id);
		return userMgr.findById(id);
	}
	
	@POST
	@Path("/{id}")
	@Produces("application/json")
	public void uploadPhoto(@Context HttpServletRequest request,
							@PathParam("id") String id)
					   throws Throwable {
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
	
	@GET
	@Path("/current_user_in_dept_all_user")
	@Produces("application/json")
	public List<User> getCurrentUserInDepartmentAllUser() throws UserPersistException {
		String currentUser = this.getSecurityService().getCurrentUser();
		LdapService ldapSvc = this.getLdapService();
		UserManager userManager = new LdapUserManager(ldapSvc);
		SearchControls sc = SearchControlsFactory.getSearchControls(
										SearchControls.SUBTREE_SCOPE);
		List<User> users = userManager.findByName(currentUser, sc);
		User user = null;
		if (users != null && users.size() > 0) {
			user = users.get(0);
			if (user != null && StringUtils.isNotBlank(user.getDepartmentName())) {
				users = userManager.findByDepartmentId(user.getDepartmentName(), sc);
			}
		}
		
		return users;
	}
	
	public static UserVo fillUserPropertiesToVo(User user) {
		UserVo userVo = new UserVo();
		userVo.setId(user.getId());
		userVo.setType("io");
		userVo.setLabel(user.getId() );
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
		userVo.setLabel( user.getId());
		userVo.setCheckName(user.getId());
		userVo.setLeaf(true);
		userVo.setUid(user.getId());
		userVo.setDeptFullPath(user.getDeptFullPath());
		userVo.setKind("user");

		return userVo;
	}

//	public static UserVo fillUserPropertiesToVo(User user, String type) {
//		UserVo userVo = new UserVo();
//
//		userVo.setId(user.getId());
//		userVo.setType(type);
//		userVo.setLabel("<a>" + user.getId() + "<a>");
//		userVo.setCheckName(user.getId());
//		userVo.setLeaf(true);
//		userVo.setUid(user.getId());
//		userVo.setDeptFullPath(user.getDeptFullPath());
//		userVo.setKind("user");
//
//		return userVo;
//	}

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
