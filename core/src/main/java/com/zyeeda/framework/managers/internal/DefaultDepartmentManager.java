package com.zyeeda.framework.managers.internal;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.realm.ldap.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.DepartmentManager;
import com.zyeeda.framework.utils.TreeDeleteControlUtils;
import com.zyeeda.framework.viewmodels.DepartmentVo;
import com.zyeeda.framework.viewmodels.OrganizationNodeVo;
import com.zyeeda.framework.viewmodels.UserVo;

public class DefaultDepartmentManager implements DepartmentManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDepartmentManager.class);
	
	private LdapService ldapSvc;
	
	public DefaultDepartmentManager(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	public DepartmentVo persist(Department dept) throws NamingException {
		LdapContext ctx = null;
		LdapContext parentCtx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			String parent = dept.getParent();
			String dn = String.format("ou=%s", dept.getName());
			logger.debug("the value of the dn and parent is = {}   {}  ", dn, parent);
			
			Attributes attrs = DefaultDepartmentManager.unmarshal(dept);
			parentCtx = (LdapContext) ctx.lookup(parent);
			parentCtx.createSubcontext(dn, attrs);
		
			String id = String.format("%s,%s", dn, parent);
			dept.setId(id);
			DepartmentVo deptVo = this.fillDepartmentPropertiesToVo(dept);
			
			return deptVo;
		} finally {
			LdapUtils.closeContext(parentCtx);
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public void remove(String id) throws NamingException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx = (LdapContext) ctx.lookup(id);
			ctx.setRequestControls(new Control[] { new TreeDeleteControlUtils()});
			ctx.unbind("");
			
			logger.debug("ctx " + ctx.getNameInNamespace() + " deleted");
		} catch(OperationNotSupportedException e) {
			 ctx.setRequestControls(new Control[0]);
	         deleteRecursively(ctx);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	public static void deleteRecursively(LdapContext ctx) throws NamingException {
		NamingEnumeration<Binding> ne = ctx.listBindings("");
		 while (ne.hasMore()) {
		     Binding b = (Binding) ne.next();
		     if (b.getObject() instanceof LdapContext) {
		         deleteRecursively((LdapContext) b.getObject());
		     }
		 }
		 ctx.unbind("");
		 logger.debug("Entry " + ctx.getNameInNamespace() + " deleted");
	}

	@Override
	public DepartmentVo update(Department dept) throws NamingException {
		LdapContext ctx = null;
		try {
//			String name = this.findById(dept.getId()).getLabel().replaceAll("<a>", "");
			String name = this.findById(dept.getId()).getName();
			logger.debug("the value of the name and dept.getName() is = {} {} ", name, dept.getName());
			
			ctx = this.ldapSvc.getLdapContext();
			// 如果名称相同，则可以修改
			if (dept.getName().equals(name)) {
				Attributes attrs = unmarshal(dept);
				String dn = dept.getId();
				ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
			} else {
				// 修改名称会出现异常
				logger.debug("===================error perform==================");
			}
			
			DepartmentVo deptVo = this.fillDepartmentPropertiesToVo(dept);
			
			return deptVo;
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	public Department findById(String id) throws NamingException {
		LdapContext ctx = null;
		Attributes attrs = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			attrs = ctx.getAttributes(id);
			Department dept = marshal(attrs);
			dept.setId(id);
			
//			DepartmentVo deptVo = this.fillDepartmentPropertiesToVo(dept);
			
//			return deptVo;
			return dept;
		} finally {
				LdapUtils.closeContext(ctx);
		}
	}
	
	
	
	@Override
	public List<DepartmentVo> getDepartmentListById(String id) throws NamingException {
		logger.debug("******************the method is getDepartmentListById*******************");
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<DepartmentVo> deptList = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			SearchResult entry = null;
			ne = ctx.search(id, "(ou=*)", this.getOneLevelScopeSearchControls());
			
			deptList = new ArrayList<DepartmentVo>();
			for (; ne.hasMore(); ) {
				entry = ne.next();
				Department dept = new Department();
				Attributes attr = entry.getAttributes();
				String childId = String.format("%s,%s", entry.getName(), id);
				logger.debug("the value of the childId is = {}  ", childId);
				
				dept.setName((String)attr.get("ou").get());
				dept.setDescription((String)attr.get("description").get());
				dept.setId(childId);
				
				DepartmentVo deptVo = this.fillDepartmentPropertiesToVo(dept);
				
				deptList.add(deptVo);
			}
			
			return deptList;
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public List<DepartmentVo> getDepartmentListByName(String name)
			throws NamingException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<DepartmentVo> deptList = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ne = ctx.search("o=广州局", "(ou=*" + name + ")", this.getThreeLevelScopeSearchControls());
			
			SearchResult entry = null; 
			deptList = new ArrayList<DepartmentVo>();
			for (; ne.hasMore(); ) {
				entry = ne.next();
				String childId = String.format("%s,o=%s", entry.getName(), "广州局");
				logger.debug("the value of the dept childId is = {}  ", childId);
				
				Department dept = new Department();
				Attributes attr = entry.getAttributes();
				dept.setName((String)attr.get("ou").get());
				dept.setDescription((String)attr.get("description").get());
				dept.setId(childId);
				
				DepartmentVo deptVo = this.fillDepartmentPropertiesToVo(dept);
				
				deptList.add(deptVo);
			}
			
			return deptList;
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}
	
	public List<OrganizationNodeVo> mergeDepartmentVoAndUserVo(List<DepartmentVo> deptList, List<UserVo> userList) {
		List<OrganizationNodeVo> orgNodeVoList = new ArrayList<OrganizationNodeVo>();
		for (DepartmentVo deptVo: deptList) {
			OrganizationNodeVo orgNodeVo = new OrganizationNodeVo();
			orgNodeVo.setId(deptVo.getId());
			orgNodeVo.setCheckName(deptVo.getCheckName());
			orgNodeVo.setIo(deptVo.getIo());
			orgNodeVo.setLabel(deptVo.getLabel());
			orgNodeVo.setType(deptVo.getType());
			
			orgNodeVoList.add(orgNodeVo);
		}
		
		for (UserVo userVo: userList) {
			OrganizationNodeVo orgNodeVo = new OrganizationNodeVo();
			orgNodeVo.setId(userVo.getId());
			orgNodeVo.setCheckName(userVo.getCheckName());
			orgNodeVo.setIo(userVo.getId());
			orgNodeVo.setLabel(userVo.getLabel());
			orgNodeVo.setType(userVo.getType());
			orgNodeVo.setLeaf(userVo.isLeaf());
			
			orgNodeVoList.add(orgNodeVo);
		}
		
		return orgNodeVoList;
	}
	


	private static Attributes unmarshal(Department dept) {
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "organizationalUnit");
		attrs.put("ou", dept.getName());
		attrs.put("description", dept.getDescription());
		
		return attrs;
	}
	
	private static Department marshal(Attributes attrs) throws NamingException {
		Department dept = new Department();
		
		dept.setName((String) attrs.get("ou").get());
		dept.setDescription((String) attrs.get("description").get());
		
		return dept;
	}
	
	private DepartmentVo fillDepartmentPropertiesToVo(Department dept) {
		DepartmentVo deptVo = new DepartmentVo();
		
		deptVo.setId(dept.getId());
		deptVo.setType("task");
		deptVo.setLabel("<a>" + dept.getName() + "<a>");
		deptVo.setCheckName(dept.getId());
		deptVo.setLeaf(false);
		deptVo.setIo("/rest/depts/" + dept.getId() + "/children");
		logger.debug("******the value of the io is = {} ", deptVo.getIo());
		
		return deptVo;
	}
	
	private SearchControls getOneLevelScopeSearchControls() {
		SearchControls sc = this.getSearchControls();
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);

		return sc;
	}
	
	private SearchControls getThreeLevelScopeSearchControls() {
		SearchControls sc = this.getSearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		return sc;
	}

	private SearchControls getSearchControls() {
		SearchControls sc = new SearchControls();

		return sc;
	}
	
}
