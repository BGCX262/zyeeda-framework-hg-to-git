package com.zyeeda.framework.ws;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.mongodb.MongoException;
import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.managers.DocumentException;
import com.zyeeda.framework.managers.DocumentManager;
import com.zyeeda.framework.managers.internal.MongoDbDocumentManager;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/docs")
public class DocumentService extends ResourceService {
	
	public DocumentService(@Context ServletContext ctx) {
		super(ctx);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public void upload(MultipartBody body) throws IOException,
			IllegalAccessException, InvocationTargetException,
			DocumentException {
		Document doc = new Document();
		InputStream is = null;
		try {
			DocumentManager documentManager = new MongoDbDocumentManager();
			List<Attachment> attaches = body.getAllAttachments();
			for (Attachment attach : attaches) {
				String key = attach.getContentDisposition()
						.getParameter("name");
				String value = IOUtils.toString(attach.getDataHandler()
						.getInputStream(), "UTF-8");
				if (!"content".equals(key)) {
					BeanUtils.setProperty(doc, key, value);
				} else {
					is = attach.getDataHandler().getInputStream();
					doc.setContent(is);
					MediaType contentType = attach.getContentType();
					doc.setContentType(contentType.toString());
					doc.setSubType(contentType.getType());
					doc.setType(contentType.getSubtype());
				}
			}
			documentManager.persist(doc);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	@GET
	@Path("/{id}/download/{fileName}")
	public MultipartBody download(@PathParam("id") String id,
			@PathParam("fileName") String fileName) throws MongoException,
			IOException, InterruptedException, DocumentException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		if (documentManager != null) {
			InputStream is = null;
			Document doc = null;
			if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(fileName)) {
				doc = documentManager.findByIdAndFileName(id, fileName);
			}
			try {
				is = doc.getContent();
				Attachment attach = new Attachment("test",
						"application/octet-stream", is);
				List<Attachment> attaches = new ArrayList<Attachment>();
				attaches.add(attach);
				return new MultipartBody(attaches, true);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		} else {
			return null;
		}
	}

	@GET
	@Path("/{id}/view/{fileName}")
	public InputStream view(@PathParam("id") String id,
			@PathParam("fileName") String fileName) throws DocumentException,
			IOException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		InputStream is = null;
		try {
			Document doc = null;
			if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(fileName)) {
				doc = documentManager.findByIdAndFileName(id, fileName);
			}
			is = doc.getContent();
			return is;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	@GET
	@Path("/find/{owner}/{foreignId}/{keyword}/{skip}/{limit}")
	@Produces("application/json")
	public List<Document> findByKeywordAndForeignId(
			@PathParam("owner") String owner,
			@PathParam("keyword") String keyword,
			@PathParam("foreignId") String foreignId,
			@PathParam("skip") int skip, @PathParam("limit") int limit)
			throws DocumentException, UnknownHostException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		List<Document> list = documentManager.findByKeyword(owner, keyword,
				foreignId, skip, limit);
		return list;
	}

	@GET
	@Path("/findByKeywordAndForeignId/{foreignId}/{keyword}/{skip}/{limit}")
	@Produces("application/json")
	public List<Document> findByKeywordAndForeignId(
			@PathParam("foreignId") String foreignId,
			@PathParam("keyword") String keyword, @PathParam("skip") int skip,
			@PathParam("limit") int limit) throws DocumentException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		List<Document> list = documentManager.findByKeyword("", keyword,
				foreignId, skip, limit);
		System.out.println(list.size());
		return list;
	}

	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public void editDocument(@FormParam("document") Document document)
			throws UnknownHostException, DocumentException {
		if (document != null) {
			DocumentManager documentManager = new MongoDbDocumentManager();
			documentManager.updateDocument(document);
		}
	}

	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public void removeById(@PathParam("id") String id)
			throws DocumentException, UnknownHostException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		documentManager.removeById(id);
	}

}
