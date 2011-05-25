package com.zyeeda.framework.managers.internal;

import java.text.ParseException;
import java.util.List;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class DefaultUserManager extends DomainEntityManager<User, String>
		implements UserManager {

	public DefaultUserManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultUserManager.class);

	@Override
	public List<User> findByDepartmentId(String id) throws NamingException {
		return null;
	}

	@Override
	public User findById(String id) throws NamingException, ParseException {
		return super.find(id);
	}

	@Override
	public List<User> findByName(String name) throws NamingException {
		User user = new User();
		user.setUsername(name);
		return null;
	}

	@Override
	public void persist(User user) throws NamingException {
		super.persist(user);

	}

	@Override
	public void remove(String id) throws NamingException {
		super.removeById(id);

	}

	@Override
	public void setVisible(Boolean visible, String... ids) {
		for (String id : ids) {
			super.em()
					.createQuery(
							"update com.zyeeda.framework.entities.User o set o.status = ?1 where o.id = ?2")
					.setParameter(1, visible).setParameter(2, id)
					.executeUpdate();
		}
	}

	@Override
	public void update(User user) throws NamingException, ParseException {
		super._merge(user);
	}

	@Override
	public void updatePassword(String id, String password)
			throws NamingException, ParseException {
		User user = new User();
		user.setId(id);
		user.setPassword(password);

		super._merge(user);
	}

}
