package com.zyeeda.framework.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.managers.DocumentManager;
import com.zyeeda.framework.managers.internal.RdbmsDocumentManager;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/docs")
public class DocumentService extends ResourceService {

	public DocumentService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_XML)
	public void upload(@FormParam("") Document doc, @Multipart("file1") InputStream is) throws IOException, SQLException {
		OutputStream os = null;
		try {
			os = doc.getContent().setBinaryStream(0);
			IOUtils.copy(is, os);
			os.flush();
			
			DocumentManager docMgr = new RdbmsDocumentManager(this.getPersistenceService());
			docMgr.persist(doc);
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
		
		System.out.println("name = " + doc.getName());
		System.out.println("desc = " + doc.getDescription());
	}

}
