package com.zyeeda.framework.ws;

import java.text.ParseException;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.managers.internal.DefaultUserManager;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/sync")
public class DefaultUserService extends ResourceService {

	public DefaultUserService(@Context ServletContext ctx) {
		super(ctx);
	}

	@POST
	@Path("/persist")
	@Produces("application/json")
	public User createUser(@FormParam("") User user) throws NamingException,
			ParseException {
		DefaultUserManager userMgr = new DefaultUserManager(this
				.getPersistenceService());
		if (userMgr.findById(user.getId()) != null) {
			throw new RuntimeException("账号不能重复");
		} else {
			userMgr.persist(user);
			return userMgr.findById(user.getId());
		}
	}

	@PUT
	@Path("/update")
	@Produces("application/json")
	public User editUser(@FormParam("") User user) throws NamingException,
			ParseException {
		DefaultUserManager userMg = new DefaultUserManager(this
				.getPersistenceService());

		User u = userMg.find(user.getId());
		u.setUsername(user.getUsername());
		u.setEmail(user.getEmail());
		u.setBirthday(user.getBirthday());
		u.setDateOfWork(user.getDateOfWork());
		u.setDegree(user.getDegree());
		u.setDepartmentName(user.getDepartmentName());
		u.setDeptFullPath(user.getDeptFullPath());
		u.setGender(user.getGender());
		u.setPassword(user.getPassword());
		u.setMobile(user.getMobile());
		u.setPosition(user.getPosition());
		u.setPostStatus(user.getPostStatus());
		u.setStatus(user.getStatus());
		userMg.update(u);

		return u;
	}

	@PUT
	@Path("/editPassword")
	public User editPassword(@QueryParam("id") String id,
			@QueryParam("formerlyPassword") String formerlyPassword,
			@QueryParam("nowPasswrd") String nowPasswrd)
			throws NamingException, ParseException {
		DefaultUserManager userMg = new DefaultUserManager(this
				.getPersistenceService());
		User user = userMg.findById(id);
		if (user.getPassword().equals(formerlyPassword)) {
			if (!nowPasswrd.equals(user.getPassword())) {
				userMg.updatePassword(id, nowPasswrd);
			}
		} else {
			throw new RuntimeException("旧密码输入错误");
		}
		return null;
	}

	@PUT
	@Path("/enable")
	@Produces("application/json")
	public User enable(@PathParam("id") String id,
			@FormParam("status") Boolean visible) throws NamingException,
			ParseException {
		return this.setVisible(id, true);
	}

	@PUT
	@Path("/{id}/unenable")
	@Produces("application/json")
	public User unEnable(@PathParam("id") String id,
			@FormParam("status") Boolean visible) throws NamingException,
			ParseException {
		return this.setVisible(id, false);
	}

	@PUT
	@Path("/setVisible")
	@Produces("application/json")
	public User setVisible(@FormParam("id") String id,
			@FormParam("status") Boolean visible) throws NamingException,
			ParseException {

		DefaultUserManager userMg = new DefaultUserManager(this
				.getPersistenceService());
		userMg.setVisible(visible, id);

		return userMg.findById(id);
	}

}
