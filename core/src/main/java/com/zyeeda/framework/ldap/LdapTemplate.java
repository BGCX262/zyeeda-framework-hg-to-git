package com.zyeeda.framework.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.drools.lang.DRLParser.string_list_return;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.SearchControlsFactory;
import com.zyeeda.framework.managers.UserPersistException;

/**
 * Ldap Common operate class
 * @author gary.wang
 * @since 1.5
 * @version 1.6
 * 
 */
public class LdapTemplate{


	private LdapService ldapSvc;

	public LdapTemplate(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	/**
	 * Create subcontext 
	 * @param dn the node's dn value
	 * @param attrs the node's attributes
	 * @throws UserPersistException exception
	 */ 
	public void create(String dn,Attributes attrs) throws UserPersistException{
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx.bind(dn, null, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}finally {
			LdapUtils.closeContext(ctx);
		}
		
	}
	
	/**
	 * Update the subcontext'attributes values by it's dn
	 * @param dn the node's dn value
	 * @param attrs the node's attributes 
	 * @throws UserPersistException exception
	 */
//	public void rebind(String dn, Attributes attrs) throws UserPersistException {
//		LdapContext ctx = null;
//		try {
//			ctx = this.ldapSvc.getLdapContext();
//			ctx.rebind(dn, null, attrs);
//		} catch (NamingException e) {
//			throw new UserPersistException(e);
//		} finally {
//			LdapUtils.closeContext(ctx);
//		}
//	}
	
	/**
	 * Delete subcontext by dn not cascade
	 * when the node has children ,it will be fail
	 * @param dn DN
	 * @throws UserPersistException exception
	 */
//	public void delete(String dn) throws Throwable {
//		this.delete(dn, false);
//	}
	
	
	/**
	 * Delete subcontext by dn
	 * @param dn dn DN
	 * @param cascade cascade true:cascade delete,false:no cascade
	 * @throws UserPersistException exception
	 */
//	public void delete(String dn,boolean cascade) throws UserPersistException{
//		LdapContext ctx = null;
//		try {
//			ctx = this.ldapSvc.getLdapContext();
//			delete(ctx,dn,cascade);
//		} catch (NamingException e) {
//			throw new UserPersistException(e);
//		}finally {
//			LdapUtils.closeContext(ctx);
//		}
//	}
	
	
	/**
	 * Delete subcontext by dn
	 * @param ctx LdapContext
	 * @param dn DN
	 * @param cascade true:cascade delete,false:no cascade
	 * @throws UserPersistException exception
	 */
//	private void delete(LdapContext ctx,String dn,boolean cascade) throws UserPersistException{
//		try {
//			if(!cascade){
//				ctx.unbind(dn);
//			}else{
//				NamingEnumeration<Binding> enumeration = ctx.listBindings(dn);
//				while (enumeration.hasMore()) {
//					Binding binding = (Binding) enumeration.next();
//					delete(ctx, stringSubString(binding.getNameInNamespace(), ctx.getNameInNamespace()),cascade);
//				}
//				ctx.unbind(dn);
//			}
//		} catch (NamingException e) {
//			throw new UserPersistException(e);
//		} 
//	}
	
	/**
	 * Find node Attributes by DN
	 * @param dn DN that full path 
	 * @return Attributes
	 * @throws UserPersistException exception 
	 */
//	public Attributes findByPrimaryKey(String dn) throws UserPersistException{
//		LdapContext ctx = null;
//		try {
//			ctx = this.ldapSvc.getLdapContext();
//			return ctx.getAttributes(dn);
//		} catch (NamingException e) {
//			return null;
//		} finally {
//			LdapUtils.closeContext(ctx);
//		}
//	}

	
	/**
	 * Find all children that the node has,except children node has children.
	 * @param ctx LdapContext 
	 * @param dn DN
	 * @return children'DN set
	 * @throws UserPersistException exception
	 */
//	public Map<String, Attributes> findAll(String key,String filter) throws UserPersistException{
//		NamingEnumeration<SearchResult> ne = 
//			this.search(key, filter, SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE));
//		return this.searchResultToMapValue(ne);
//	}
	
	/**
	 * Search NamingEnumeration<SearchResult> by name and filter
	 * @param name dn,eg: uid=1,dc=x,dc=cctv,dc=com ;the name is dc=x,dc=cctv,dc=com; filter is uid=?
	 * @param filter filter condition eg:uid=*,uid=1
	 * @param controls search scope
	 * @return  NamingEnumeration
	 */
//	protected NamingEnumeration<SearchResult> search(String name,String filter,SearchControls controls){
//		LdapContext ctx = null;
//		try {
//			ctx = this.ldapSvc.getLdapContext();
//			return ctx.search(name, filter, controls);
//		} catch (NamingException e) {
//			return null;
//		} finally {
//			LdapUtils.closeContext(ctx);
//		}
//	}
	

	/**
	 * SearchResult to Map value 
	 * @param ne NamingEnumeration<SearchResult> 
	 * @return Map<String, Attributes> 
	 * @throws UserPersistException exception
	 */
//	private Map<String, Attributes> searchResultToMapValue(NamingEnumeration<SearchResult> ne) throws UserPersistException{
//		Map<String, Attributes> result = null;
//		try{
//			if(ne != null){
//				SearchResult entry = null;
//				result = new HashMap<String, Attributes>();
//				while(ne.hasMore()){
//					entry = ne.next();
//					Attributes attrs = entry.getAttributes();
//					result.put(entry.getNameInNamespace(),attrs);
//				}
//			}
//		}catch (NamingException e) {
//			throw new UserPersistException(e);
//		}finally {
//			try{
//				if(ne != null){
//					ne.close();
//				}
//			}catch(NamingException ee){
//				throw new UserPersistException(ee);
//			}
//		}
//		return result;
//	}
	
//	/**
//	 * Big String sub Small String ,and return a remaining string
//	 * @param big Big String
//	 * @param small Small String
//	 * @return remaining string 
//	 * @throws UserPersistException exception
//	 */
//	private static String stringSubString(String big,String small) throws UserPersistException{
//		String result = null;
//		try{
//			result = big.substring(0,big.indexOf(small)-1);
//		}catch(Exception e){
//			throw new UserPersistException(e);
//		}
//		return result;
//	}
	
	public void rebind(String dn, Attributes attrs) throws UserPersistException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx.rebind(dn, null, attrs);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	public void unbind(String dn) throws NamingException {
		this.unbind(dn, false);
	}
	
	private void unbind(String dn, boolean cascade) throws NamingException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			if (!cascade){
				ctx.unbind(dn);
			} else {
				NamingEnumeration<Binding> enumeration = ctx.listBindings(dn);
				while (enumeration.hasMore()) {
					Binding binding = (Binding) enumeration.next();
					unbind(binding.getNameInNamespace(),cascade);
				}
				ctx.unbind(dn);
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public Attributes findByDn(String dn) throws Throwable{
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			return ctx.getAttributes(dn);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	public QueryResult<Attributes> search(String name,
            								     Attributes matchingAttributes,
            								     String[] attributesToReturn)
								     	  throws NamingException {
		LdapContext ctx = this.ldapSvc.getLdapContext();
		NamingEnumeration<SearchResult> ne = ctx.search(name, matchingAttributes, attributesToReturn);
		return this.namingEnumeration2QueryResult(ne);
	}
	
	public QueryResult<Attributes> search(String name,
            						Attributes matchingAttributes)
     						 throws NamingException {
		LdapContext ctx = this.ldapSvc.getLdapContext();
		NamingEnumeration<SearchResult> ne = ctx.search(name, matchingAttributes);
		return this.namingEnumeration2QueryResult(ne);
	}
	
	public QueryResult<Attributes> search(String name,
            						String filter,
            						SearchControls cons)
     						 throws NamingException {
		LdapContext ctx = this.ldapSvc.getLdapContext();
		NamingEnumeration<SearchResult> ne = ctx.search(name, filter, cons);
		return this.namingEnumeration2QueryResult(ne);
	}
	
	public QueryResult<Attributes> search(String name,
										  String filterExpr,
										  Object[] filterArgs,
										  SearchControls cons)
								   throws NamingException {
		LdapContext ctx = this.ldapSvc.getLdapContext();
		NamingEnumeration<SearchResult> ne = ctx.search(name, filterExpr, filterArgs, cons);
		return this.namingEnumeration2QueryResult(ne);
	}
	
	private QueryResult<Attributes> namingEnumeration2QueryResult(
											NamingEnumeration<SearchResult> ne)
									throws NamingException {
		if (ne == null) {
			return null;
		}
		QueryResult<Attributes> qr = new QueryResult<Attributes>();
		List<Attributes> attrList = new ArrayList<Attributes>();
		Long totalRecords = 0l;
		while (ne.hasMore()) {
			totalRecords ++;
			Attributes atrrs = ne.next().getAttributes();
			if (atrrs != null) {
				attrList.add(atrrs);
			}
		}
		qr.setResultList(attrList);
		qr.setTotalRecords(totalRecords);
		
		return qr;
	}
}
