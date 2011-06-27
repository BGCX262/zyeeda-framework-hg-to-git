package com.zyeeda.framework.managers.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.LdapTemplate;
import com.zyeeda.framework.ldap.SearchControlsFactory;
import com.zyeeda.framework.managers.DepartmentManager;
import com.zyeeda.framework.managers.UserPersistException;

public class LdapDepartmentManager implements DepartmentManager {

	private LdapService ldapSvc;
	
	public LdapDepartmentManager(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	public void persist(Department dept) throws UserPersistException {
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			Attributes attrs = unmarshal(dept);
			String dn = dept.getId();
			ldapTemplate.bind(dn, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	@Override
	public void remove(String dn) throws UserPersistException{
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			ldapTemplate.unbind(dn, true);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	@Override
	public void update(Department dept) throws UserPersistException {
		String dn = dept.getId();
		Attributes attrs = unmarshal(dept);
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			ldapTemplate.modifyAttributes(dn, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	public Department findById(String id) throws UserPersistException {
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			Attributes attrs = ldapTemplate.findByDn(id);
			Department dept = LdapDepartmentManager.marshal(attrs);
			dept.setId(id);
			dept.setDeptFullPath(id);
			dept.setParent(id);
			return dept;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	@Override
	public List<Department> getChildrenById(String dn) throws UserPersistException {
		List<Attributes> attrList = null;
		List<Department> deptList = null;
		
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			String filter = "";
			if ("root".equals(dn)) {
				dn = "";
				filter = "o=*";
			} else {
				filter = "ou=*";
			}
			attrList = ldapTemplate.getResultList(dn,
												  filter,
												  SearchControlsFactory.getSearchControls(SearchControls.ONELEVEL_SCOPE));
			deptList = new ArrayList<Department>(attrList.size());
			for (Attributes attr : attrList) {
				Department department = marshal(attr);
				department.setParent(dn);
				department.setDeptFullPath("ou=" + department.getId() + "," + dn);
				deptList.add(department);
			}
			
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	@Override
	public List<Department> findByName(String name) throws UserPersistException {
		return this.findByName("", name);
	}

	public Integer getChildrenCountById(String dn, String filter) throws UserPersistException {
		List<Attributes> attrList = null;
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			attrList = ldapTemplate.getResultList(dn,
												  filter,
												  SearchControlsFactory.getSearchControls(SearchControls.ONELEVEL_SCOPE));
			return attrList.size();
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	public List<Department> findByName(String parent,
									   String name) 
								  throws UserPersistException {
		List<Department> deptList = null;
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			List<Attributes> attrsList = ldapTemplate.getResultList(parent,
									   								"(ou=" + name + ")",
									   								SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE));
			deptList = new ArrayList<Department>(attrsList.size());
			for (Attributes attrs : attrsList) {
				Department dept = LdapDepartmentManager.marshal(attrs);
				
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}
	
	
	
	private static Attributes unmarshal(Department dept) {
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "organizationalUnit");
		if (StringUtils.isNotBlank(dept.getName())) {
			attrs.put("ou", dept.getName());
		}
		if (StringUtils.isNotBlank(dept.getDescription())) {
			attrs.put("description", dept.getDescription());
		}
		return attrs;
	}
	
	private static Department marshal(Attributes attrs) throws NamingException {
		Department dept = new Department();
		
		if (attrs.get("ou") != null) {
			dept.setId((String) attrs.get("ou").get());
			dept.setName((String) attrs.get("ou").get());
		} else if (attrs.get("o") != null) {
			dept.setId((String) attrs.get("o").get());
			dept.setName((String) attrs.get("o").get());
		}
		if (attrs.get("description") != null) {
			dept.setDescription((String) attrs.get("description").get());
		}
		return dept;
	}
	
	private LdapTemplate getLdapTemplate() throws NamingException {
		return new LdapTemplate(this.ldapSvc.getLdapContext());
	}

	@Override
	public List<Department> getRootAndSecondLevelDepartment()
												throws UserPersistException {
		List<Department> deptList = null;
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			List<Attributes> attrsList = ldapTemplate.getResultList("",
									   								"(o=广州局)",
									   								SearchControlsFactory.getDefaultSearchControls());
			
			attrsList.addAll(ldapTemplate.getResultList("o=广州局",
									   					"(ou=*)",
									   					SearchControlsFactory.getDefaultSearchControls()));
			deptList = new ArrayList<Department>(attrsList.size());
			for (Attributes attrs : attrsList) {
				Department dept = LdapDepartmentManager.marshal(attrs);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}

	@Override
	public List<Department> getDepartmentListByUserId(String userId)
										throws UserPersistException {
		List<Department> deptList = null;
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			List<Attributes> attrsList = ldapTemplate.getResultList(userId,
									   							   "(|(o=*)(ou=*))",
									   							   SearchControlsFactory.getDefaultSearchControls());
			deptList = new ArrayList<Department>(attrsList.size());
			for (Attributes attrs : attrsList) {
				Department dept = LdapDepartmentManager.marshal(attrs);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}
	}

	@Override
	public List<Department> search(String condition)
							throws UserPersistException {
		List<Department> deptList = null;
		try {
			LdapTemplate ldapTemplate = this.getLdapTemplate();
			List<Attributes> attrList = ldapTemplate.getResultList("",
									   							   "uid=" + condition,
									                               SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE));
			List<User> userList = new ArrayList<User>(attrList.size());
			for (Attributes attrs : attrList) {
				User user = LdapUserManager.marshal(attrs);
				userList.add(user);
			}
			if (userList != null && userList.size() > 0) {
				
			}
			String filter = "(|(o=*" + condition + "*)(ou=*" + condition + "*" + "))";
			if ("*".equals(condition)) {
				filter = "(|(o=*)(ou=*))";
			}
			List<Attributes> attrsList = ldapTemplate.getResultList("",
																	filter,
									   								SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE));
			deptList = new ArrayList<Department>(attrsList.size());
			for (Attributes attrs : attrsList) {
				Department dept = LdapDepartmentManager.marshal(attrs);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (ParseException e) {
			throw new UserPersistException(e);
		}
	}
	
	
	
	
	/*
	private LdapTemplate getLdapTemplate(LinkedHashMap<String, Boolean> orderBy)
			throws NamingException, IOException {
		LdapContext ctx = this.ldapSvc.getLdapContext();
		if (orderBy != null) {
			for (String key : orderBy.keySet()) {
				ctx.setRequestControls(new Control[] { new SortControl(key,
						orderBy.get(key)) });
			}
		}
		return new LdapTemplate(ctx);
	}
	*/
}
