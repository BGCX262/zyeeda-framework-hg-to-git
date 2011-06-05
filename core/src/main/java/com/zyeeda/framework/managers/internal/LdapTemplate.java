package com.zyeeda.framework.managers.internal;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.apache.shiro.realm.ldap.LdapUtils;
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
	public void persist(String dn, Attributes attrs) throws UserPersistException{
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx.createSubcontext(dn, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
		
	}
	
	/**
	 * Update the subcontext'attributes values by it's dn
	 * @param dn the node's dn value
	 * @param attrs the node's attributes 
	 * @throws UserPersistException exception
	 */
	public void update(String dn, Attributes attrs) throws UserPersistException {
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	/**
	 * Remove the subcontext by it's dn
	 * when it has sub node ,the operate will be fail 
	 * @param dn the node's dn value
	 * @throws UserPersistException exception
	 */
	public void remove(String dn) throws UserPersistException {
		this.remove(dn, false);
	}
	
	/**
	 * Remove the subcontext cascade by it's dn 
	 * @param dn the node's dn value
	 * @param cascade true:cascade delete nodes,false:no cascade
	 * @throws UserPersistException exception
	 */
	public void remove(String dn, boolean cascade) throws UserPersistException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			if(cascade){
				if(dn==null || dn.indexOf(",")==-1){
					return ;
				}
				int suffix = dn.indexOf(",");
				NamingEnumeration<SearchResult> ne = this.search(ctx, dn, dn.substring(0,suffix), 
						SearchControlsFactory.getSearchControls(SearchControls.ONELEVEL_SCOPE));
					if(ne != null){
						SearchResult entry = null;
						while(ne.hasMore()){
							entry = ne.next();
							this.remove(entry.getNameInNamespace(),cascade);
						}
						ctx.destroySubcontext(dn);
					}
			}else{
				ctx.destroySubcontext(dn);
			}
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	/**
	 * Find node Attributes by DN
	 * @param dn DN that full path 
	 * @return Attributes
	 * @throws UserPersistException exception 
	 */
	public Attributes findByDn(String dn) throws UserPersistException{
		Map<String, Attributes> map = this.findChildrenByDn(dn);
		if(map == null){
			return null;
		}else{
			return (map.get(dn)==null?null:(Attributes)map.get(dn));
		}
	}
	
	/**
	 * Find all children that the node has,except children node has children.
	 * @param ctx LdapContext 
	 * @param dn DN
	 * @return children'DN set
	 * @throws UserPersistException exception
	 */
	public Map<String, Attributes> findChildrenByDn(String dn) throws UserPersistException {
		if(dn==null || dn.trim().equals("") || dn.indexOf(",")==-1){
			return null;
		}
		int first = dn.indexOf(",");
		String cn = dn.substring(0,first);
		String dc = dn.substring(first);
		NamingEnumeration<SearchResult> ne = this.search(dc, cn, SearchControlsFactory.getSearchControls(SearchControls.ONELEVEL_SCOPE));
		return this.searchResultToMapValue(ne);
	}
	

	/**
	 * SearchResult to Map value 
	 * @param ne NamingEnumeration<SearchResult> 
	 * @return Map<String, Attributes> 
	 * @throws UserPersistException exception
	 */
	public Map<String, Attributes> searchResultToMapValue(NamingEnumeration<SearchResult> ne) throws UserPersistException{
		Map<String, Attributes> result = null;
		try{
			if(ne != null){
				SearchResult entry = null;
				result = new HashMap<String, Attributes>();
				while(ne.hasMore()){
					entry = ne.next();
					Attributes attrs = entry.getAttributes();
					result.put(entry.getNameInNamespace(),attrs);
				}
			}
		}catch (NamingException e) {
			throw new UserPersistException(e);
		}
		return result;
	}

	/**
	 * Search NamingEnumeration<SearchResult> by name and filter
	 * @param name dn,eg: uid=1,dc=x,dc=cctv,dc=com ;the name is dc=x,dc=cctv,dc=com; filter is uid=?
	 * @param filter filter condition eg:uid=*,uid=1
	 * @param controls search scope
	 * @return  NamingEnumeration
	 */
	public NamingEnumeration<SearchResult> search(String name,String filter,SearchControls controls){
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			return ctx.search(name, filter, controls);
		} catch (NamingException e) {
			return null;
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	/**
	 * Search NamingEnumeration<SearchResult> by name and filter
	 * @param name dn,eg: uid=1,dc=x,dc=cctv,dc=com ;the name is dc=x,dc=cctv,dc=com; filter is uid=?
	 * @param filter filter condition eg:uid=*,uid=1
	 * @param controls search scope
	 * @return  NamingEnumeration
	 */
	public NamingEnumeration<SearchResult> search(LdapContext ctx,String name,String filter,SearchControls controls){
		try {
			return ctx.search(name, filter, controls);
		} catch (NamingException e) {
			return null;
		}
	}
	
	/**
	 * Delete subcontext by dn not cascade
	 * when the node has children ,it will be fail
	 * @param dn DN
	 * @throws UserPersistException exception
	 */
	public void delete(String dn) throws UserPersistException{
		this.delete(dn,false);
	}
	
	/**
	 * Delete subcontext by dn
	 * @param dn dn DN
	 * @param cascade cascade true:cascade delete,false:no cascade
	 * @throws UserPersistException exception
	 */
	public void delete(String dn,boolean cascade) throws UserPersistException{
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			delete(ctx,dn,cascade);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		}finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	/**
	 * Delete subcontext by dn
	 * @param ctx LdapContext
	 * @param dn DN
	 * @param cascade true:cascade delete,false:no cascade
	 * @throws UserPersistException exception
	 */
	public void delete(LdapContext ctx, String dn, boolean cascade) throws UserPersistException{
		try {
			if(!cascade){
				ctx.unbind(dn);
			}else{
				NamingEnumeration<Binding> enumeration = ctx.listBindings(dn);
				while (enumeration.hasMore()) {
					Binding binding = (Binding) enumeration.next();
					delete(ctx, binding.getNameInNamespace(), cascade);
				}
				ctx.unbind(dn);
			}
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} 
	}
}
