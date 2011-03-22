package com.zyeeda.drivebox.services;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.googlecode.genericdao.search.Search;
import com.zyeeda.drivebox.entities.Dictionary;
import com.zyeeda.drivebox.managers.DictionaryManager;
import com.zyeeda.drivebox.managers.internal.DefaultDictionaryManager;

public class DictionaryService extends ResourceService {
	
	public DictionaryService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@GET
	@Path("/dict/{type}")
	@Produces("application/json")
	public List<Dictionary> getDictionary(@PathParam("type") String type) {
		DictionaryManager dictMgr = new DefaultDictionaryManager(this.getPersistenceService());
		
		Search search = new Search();
		search.addFilterEqual("type", type);
		List<Dictionary> dicts = dictMgr.search(search);
		return dicts;
	}
	
	@POST
	@Path("/dicts")
	@Produces("application/xml")
	public Dictionary createDictionary(@FormParam("") Dictionary dict) {
		DictionaryManager dictMgr = new DefaultDictionaryManager(this.getPersistenceService());
		dictMgr.persist(dict);
		this.getPersistenceService().getCurrentSession().flush();
		return dictMgr.find(dict.getId());
	}
	
}
