package com.zyeeda.framework.ws;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import javax.naming.NamingException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.utils.MD5;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/users")
public class UserService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public UserService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent:.*}")
	@Produces("application/json")
	public User createUser(@FormParam("") User user, @PathParam("parent") String parent) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		user.setDepartmentName(parent);
		return userMgr.persist(user);
	}
	
	@DELETE
	@Path("/{id}")
	public void removeUser(@PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		userMgr.remove(id);
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public User editUser(@FormParam("") User user, @PathParam("id") String id) throws NamingException, ParseException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		user.setDeptFullPath(id);
		return userMgr.update(user);
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public User getUserById(@PathParam("id") String id) throws NamingException, ParseException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		return userMgr.findById(id);
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<UserVo> getUserListByName(@PathParam("name") String name) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		return userMgr.getUserListByName(name);
	}
	
	@GET
	@Path("/userList/{deptId}")
	@Produces("application/json")
	public List<UserVo> getUserListByDepartmentId(@PathParam("deptId") String deptId) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		return userMgr.getUserListByDepartmentId(deptId);
	}
	
	@PUT
	@Path("/{id}/update_password")
	@Produces("application/json")
	public void updatePassword(@PathParam("id") String id, @FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword) throws NamingException, ParseException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		User u = userMgr.findById(id);
		if (("{MD5}" + oldPassword).equals(u.getPassword())) {
			if (!newPassword.equals(oldPassword)) {
				userMgr.updatePassword(id, newPassword);
			}
		} else {
			throw new RuntimeException("旧密码输入错误");
		}
	}
	
	@PUT
	@Path("/{id}/enable")
	@Produces("application/json")
	public void enable(@PathParam("id") String id, @FormParam("status") Boolean visible)
			throws NamingException, ParseException {
		this.setVisible(id, true);
	}
	
	@PUT
	@Path("/{id}/unenable")
	@Produces("application/json")
	public void unenable(@PathParam("id") String id, @FormParam("status") Boolean visible)
			throws NamingException, ParseException {
		this.setVisible(id, false);
	}
	
	private void setVisible(String id, Boolean visible) throws NamingException, ParseException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		userMgr.setVisible(visible, id);
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
	  
	    byte[] bytes = baos.toByteArray();
	    LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		User user = new User();
		user.setId("china");
//		user.setPhoto(bytes);
		
		userMgr.update(user);
	}
	
	
}
