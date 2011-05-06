package com.zyeeda.framework.ws;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.zyeeda.framework.ws.base.ResourceService;

@Path("/permission")
public class PermissionService extends ResourceService{
	public PermissionService(@Context ServletContext ctx){
		super(ctx);
	}

}
