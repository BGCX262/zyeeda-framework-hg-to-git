package com.zyeeda.drivebox.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.zyeeda.framework.entities.Dictionary;

public class DictionaryService {

	@GET
	@Path("/dict/{name}")
	public List<Dictionary> getDictionary(String name) {
		return null;
	}
	
	@POST
	@Path("/dicts")
	public Dictionary createDictionary(Dictionary dict) {
		return null;
	}
}
