package com.zyeeda.framework.managers.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;

import com.zyeeda.framework.entities.Department;
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
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			Attributes attrs = unmarshal(dept);
			String dn = dept.getId();
			ldapTemplate.bind(dn, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	@Override
	public void remove(String dn) throws UserPersistException{
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			ldapTemplate.unbind(dn, true);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	@Override
	public void update(Department dept) throws UserPersistException {
		String dn = dept.getId();
		Attributes attrs = unmarshal(dept);
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
//			if (dn.equals(dept.getParent()) || StringUtils.isBlank(dept.getParent())) {
//				ldapTemplate.modifyAttributes(dn, attrs);
//			} else {
//				String newName = StringUtils.substring(dn, 0, dn.indexOf(",")) + 
//									"," + dept.getParent();
//				if (dn.equals(newName)) {
//					ldapTemplate.modifyAttributes(dn, attrs);
//				} else {
//					ldapTemplate.rename(dn, newName);
//				}
//				dept.setId(newName);
//				updateUserDeptFullPath();
//			}
			ldapTemplate.modifyAttributes(dn, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	public Department findById(String id) throws UserPersistException {
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			Attributes attrs = ldapTemplate.findByDn(id);
			Department dept = LdapDepartmentManager.marshal(attrs);
			dept.setId(id);
			dept.setDeptFullPath(id);
			dept.setParent(LdapTemplate.spiltNameInNamespace(id));
			return dept;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	@Override
	public List<Department> getChildrenById(String dn) throws UserPersistException {
		List<Department> deptList = null;
		LdapTemplate ldapTemplate = null;
		
		try {
			ldapTemplate = this.getLdapTemplate();
			String filter = "";
			if ("root".equals(dn)) {
				dn = "";
				filter = "o=*";
			} else {
				filter = "ou=*";
			}
			SearchControls sc = SearchControlsFactory.getDefaultSearchControls();
			NamingEnumeration<SearchResult> ne = ldapTemplate.getSearchResult(dn,
																			  filter,
																			  sc);
			Map<String, Attributes> map = ldapTemplate.searchResultToMap(ne);
			deptList = new ArrayList<Department>(map.keySet().size());
			for (String key : map.keySet()) {
				Department department = marshal(map.get(key));
				department.setParent(LdapTemplate.spiltNameInNamespace(key));
				department.setDeptFullPath(key);
				deptList.add(department);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	@Override
	public List<Department> findByName(String name) throws UserPersistException {
		return this.findByName("", name);
	}

	public Integer getChildrenCountById(String dn, String filter) throws UserPersistException {
		List<Attributes> attrList = null;
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			SearchControls sc = SearchControlsFactory.getSearchControls(SearchControls.ONELEVEL_SCOPE);
			attrList = ldapTemplate.getResultList(dn,
												  filter,
												  sc);
			return attrList.size();
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	public List<Department> findByName(String parent,
									   String name) 
								  throws UserPersistException {
		List<Department> deptList = null;
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			SearchControls sc = SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> ne = ldapTemplate.getSearchResult(parent,
																			  "(ou=" + name + ")",
																			  sc);
			Map<String, Attributes> map = ldapTemplate.searchResultToMap(ne);
			deptList = new ArrayList<Department>(map.keySet().size());
			for (String key : map.keySet()) {
				Department dept = LdapDepartmentManager.marshal(map.get(key));
				dept.setDeptFullPath(key);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	@Override
	public List<Department> getRootAndSecondLevelDepartment()
								throws UserPersistException {
		List<Department> deptList = null;
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			SearchControls sc = SearchControlsFactory.getDefaultSearchControls();
			NamingEnumeration<SearchResult> ne = ldapTemplate.getSearchResult("",
																			  "(o=广州局)",
																			  sc);
			Map<String, Attributes> map = ldapTemplate.searchResultToMap(ne);
			deptList = new ArrayList<Department>();
			for (String key : map.keySet()) {
				Department dept = LdapDepartmentManager.marshal(map.get(key));
				dept.setDeptFullPath(key);
				deptList.add(dept);
			}
			ne = ldapTemplate.getSearchResult("o=广州局",
							   				  "(ou=*)",
											  sc);
			map = ldapTemplate.searchResultToMap(ne);
			for (String key : map.keySet()) {
				Department dept = LdapDepartmentManager.marshal(map.get(key));
				dept.setDeptFullPath(key);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}

	@Override
	public List<Department> getDepartmentListByUserId(String userId)
										throws UserPersistException {
		List<Department> deptList = null;
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			SearchControls sc = SearchControlsFactory.getDefaultSearchControls();
			NamingEnumeration<SearchResult> ne = ldapTemplate.getSearchResult(userId,
																		      "(|(o=*)(ou=*))",
																			  sc);
			Map<String, Attributes> map = ldapTemplate.searchResultToMap(ne);
			deptList = new ArrayList<Department>(map.keySet().size());
			for (String key : map.keySet()) {
				Department dept = LdapDepartmentManager.marshal(map.get(key));
				dept.setDeptFullPath(key);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}

	@Override
	public List<Department> search(String condition)
							throws UserPersistException {
		List<Department> deptList = null;
		LdapTemplate ldapTemplate = null;
		try {
			ldapTemplate = this.getLdapTemplate();
			SearchControls sc = SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE);
//			List<Attributes> attrList = ldapTemplate.getResultList("",
//									   							   "uid=" + condition,
//									   							   sc);
//			List<User> userList = new ArrayList<User>(attrList.size());
//			for (Attributes attrs : attrList) {
//				User user = LdapUserManager.marshal(attrs);
//				userList.add(user);
//			}
//			if (userList != null && userList.size() > 0) {
//				
//			}
			String filter = "(|(o=*" + condition + "*)(ou=*" + condition + "*" + "))";
			if ("*".equals(condition) || StringUtils.isBlank(condition)) {
				filter = "(|(o=*)(ou=*))";
			}
			NamingEnumeration<SearchResult> ne = ldapTemplate.getSearchResult("",
																			  filter,
																			  sc);
			Map<String, Attributes> map = ldapTemplate.searchResultToMap(ne);
			deptList = new ArrayList<Department>(map.keySet().size());
			for (String key : map.keySet()) {
				Department dept = LdapDepartmentManager.marshal(map.get(key));
				dept.setDeptFullPath(key);
				deptList.add(dept);
			}
			return deptList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}/* catch (ParseException e) {
			throw new UserPersistException(e);
		}*/ finally {
			if (ldapTemplate != null) {
				ldapTemplate.closeLdapContext();
			}
		}
	}
	
	private LdapTemplate getLdapTemplate() throws NamingException {
		return new LdapTemplate(this.ldapSvc.getLdapContext());
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
