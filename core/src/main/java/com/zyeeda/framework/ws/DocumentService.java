package com.zyeeda.framework.ws;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.hornetq.api.core.management.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.entities.DocumentListVo;
import com.zyeeda.framework.managers.DocumentException;
import com.zyeeda.framework.managers.DocumentManager;
import com.zyeeda.framework.managers.internal.MongoDbDocumentManager;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/docs")
public class DocumentService extends ResourceService {

	private static final Logger logger = LoggerFactory
			.getLogger(DocumentService.class);
	private static int KB = 1024;
	private static int MB = 1048576;
	private static int GB = 1073741824;

	private static final int OBJECT_ID_LENGTH = 24;
	private static final int DISPLAY_PAGE_NUMBER = 200;

	public DocumentService(@Context ServletContext ctx) {

		super(ctx);
	}

	// 如果附件超标、或者没有附件
	// TODO
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public void upload(MultipartBody body) throws IOException,
			IllegalAccessException, InvocationTargetException,
			DocumentException {

		// ShiroSecurityServiceProvider s = new
		// ShiroSecurityServiceProvider(null, null, null);
		String currentUser = this.getSecurityService().getCurrentUser();// 默认为zhufang在登录

		Document doc = new Document();
		InputStream is = null;
		Date date = new Date();

		String foreignId = "1";
		try {
			DocumentManager documentManager = new MongoDbDocumentManager();
			List<Attachment> attaches = body.getAllAttachments();
			for (Attachment attach : attaches) {
				String key = attach.getContentDisposition()
						.getParameter("name");
				if (!"content".equals(key)) {
					String value = IOUtils.toString(attach.getDataHandler()
							.getInputStream(), "UTF-8");
					logger.debug("Multipart: {} = {}", key, value);
					BeanUtils.setProperty(doc, key, value);
				} else {

					is = attach.getDataHandler().getInputStream();
					logger.debug("filesize = {}", is.available());
					doc.setForeignId(foreignId);
					doc.setContent(is);
					MediaType contentType = attach.getContentType();
					logger.debug("contentType = {} ", contentType.toString());
					doc.setOwner(currentUser); // 拥有人
					doc.setCreator(currentUser); // 创建人
					doc.setCreatedTime(date);// 创建时间
					doc.setLastModifiedTime(date);// 最后修改时间
					doc.setLastModifier(currentUser);// 最后修改人
					doc.setContentType(contentType.toString());
					doc.setSubType(contentType.getSubtype());
					doc.setType(contentType.getType());
				}
			}
			if (StringUtils.isNotBlank(doc.getName())
					&& doc.getContent() != null) {
				documentManager.persist(doc);
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	// @POST
	// @Path("/findFileSize")
	// @Consumes(MediaType.MULTIPART_FORM_DATA)
	// //@Produces(MediaType.APPLICATION_JSON)
	// public int getFileSize (Attachment body) throws IOException {
	// InputStream is= null;
	// int fileSize = 0;
	// try{
	// Attachment attachment = body;
	// is = attachment.getDataHandler().getInputStream();
	// fileSize = is.available();
	// }finally{
	// if(is != null) {
	// is.close();
	// }
	// }
	// return fileSize;
	// }
	// TODO
	// 当文件名 或者 ID不存在的时候出异常 还有view方法
	@GET
	@Path("/{id}/download/{fileName}")
	@Produces("multipart/mixed")
	public MultipartBody download(@PathParam("id") String id,
			@PathParam("fileName") String fileName) throws DocumentException,
			IOException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		InputStream is = null;
		if (id.length() == OBJECT_ID_LENGTH && StringUtils.isNotBlank(fileName)) {
			try {
				Document doc = documentManager
						.findByIdAndFileName(id, fileName);
				logger.debug("view id = {},fileName = {}", id, fileName);
				logger.debug("view contentType = {}", doc.getContentType());
				if (doc != null) {
					is = doc.getContent();
					Attachment attach = new Attachment(id,
							"application/octet-stream", is);
					List<Attachment> attaches = new ArrayList<Attachment>();
					attaches.add(attach);
					return new MultipartBody(attaches, true);
				} else {
					throw new DocumentException("找不到文件");
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		throw new DocumentException("找不到文件");
	}

	@GET
	@Path("/{id}/view/{fileName}")
	@Produces("multipart/mixed")
	public Attachment view(@PathParam("id") String id,
			@PathParam("fileName") String fileName) throws DocumentException,
			IOException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		InputStream is = null;
		if (id.length() == OBJECT_ID_LENGTH && StringUtils.isNotBlank(fileName)) {
			try {
				Document doc = documentManager
						.findByIdAndFileName(id, fileName);
				logger.debug("view id = {},fileName = {}", id, fileName);
				logger.debug("view contentType = {}", doc.getContentType());
				if (doc != null) {
					is = doc.getContent();
					Attachment attach = new Attachment(id, doc.getContentType()
							+ ";charset=GBK", is);
					return attach;
				} else {
					throw new DocumentException("找不到文件");
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		throw new DocumentException("找不到文件");
	}

	@GET
	@Path("/{owner}/{page}")
	@Produces("application/json")
	public List<DocumentListVo> findByOwnerAndForeignId(
			@QueryParam("keyword") String keyword,
			@QueryParam("foreignId") String foreignId,
			@PathParam("owner") String owner, @PathParam("page") int page)
			throws DocumentException {

		int skip = (page - 1) * DISPLAY_PAGE_NUMBER;
		int limit = page * DISPLAY_PAGE_NUMBER;

		DocumentManager documentManager = new MongoDbDocumentManager();
		List<Document> list = null;
		int documentNumber = 0;
		String[] key = keyword.split(",");
		logger.debug("findByOwnerAndForeignId skip={},keyword={}", skip,
				keyword);
		logger.debug("foreignId = {} ,owner = {}", foreignId, owner);
		// 有关键字的查询
		if (StringUtils.isNotBlank(keyword)) {
			logger
					.debug("findByOwnerAndForeignId run findByKeyword have keyword");
			list = documentManager.findByKeyword(owner, key, foreignId, skip,
					DISPLAY_PAGE_NUMBER);
			documentNumber = documentManager.findNumber(owner, foreignId, key);
		} else {
			logger
					.debug("findByOwnerAndForeignId run findByKeyword not keyword");
			list = documentManager.findByKeyword(owner, foreignId, skip,
					DISPLAY_PAGE_NUMBER);
			documentNumber = documentManager.findOwnerNumber(owner, foreignId);
		}
		List<DocumentListVo> documentList = new ArrayList<DocumentListVo>();

		documentList = this.getDocumentListVo(list, documentNumber, page);
		logger
				.debug("++++++++++++ documentList size = {}", documentList
						.size());
		return documentList;
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Document findById(@PathParam("id") String id)
			throws DocumentException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		Document document = documentManager.findById(id);
		return document;
	}

	// TODO
	// 修改出错 给出提示
	// 现在还没有不为空约束，而在底层写入的则为：当传来的参数不为空的时候则去修改，如果为空就不去修改
	@PUT
	@Path("/")
	@Produces("application/json")
	public void editDocument(@FormParam("") Document document)
			throws UnknownHostException, DocumentException {
		String currentUser = this.getSecurityService().getCurrentUser();// 默认为zhufang在登录
		logger.debug("function editDocument editDocument = {}", document
				.getDescription());
		if (document != null) {
			document.setLastModifier(currentUser);
			document.setLastModifiedTime(new Date());
			DocumentManager documentManager = new MongoDbDocumentManager();
			documentManager.updateDocument(document);
		}
	}

	// TODO
	// 如果没有ID给出提示
	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public void removeById(@PathParam("id") String id)
			throws DocumentException, UnknownHostException {
		DocumentManager documentManager = new MongoDbDocumentManager();
		logger.debug("removeId = {}", id);
		documentManager.removeById(id);
	}

	// TODO
	// 如果没有ID给出提示
	@DELETE
	@Path("/allRemove")
	@Produces("application/json")
	public void allRemove(@Parameter(name = "allId") String allId)
			throws DocumentException, UnknownHostException {
		DocumentManager documentManager = new MongoDbDocumentManager();

		String[] id = allId.split(",");
		logger.debug(" allRemove id= {},idsize={}", id, id.length);

		documentManager.allremoveById(id);

	}

	private List<DocumentListVo> getDocumentListVo(List<Document> list,
			int documentNumber, int page) {
		List<DocumentListVo> documentListVo = new ArrayList<DocumentListVo>(
				list.size());
		for (int i = 0; i < list.size(); i++) {
			DocumentListVo docListVo = new DocumentListVo();
			Document document = list.get(i);
			docListVo.setId(document.getId());
			docListVo.setName(document.getName());
			docListVo.setDescription(document.getDescription());
			docListVo.setCreator(document.getCreator());
			docListVo.setCreatedTime(document.getCreatedTime());
			docListVo.setLastModifier(document.getLastModifier());
			docListVo.setLastModifiedTime(document.getLastModifiedTime());
			docListVo.setForeignId(document.getForeignId());
			docListVo.setOwner(document.getOwner());
			docListVo.setFileSize(document.getFileSize());
			docListVo.setFileType(document.getFileType());
			docListVo.setKeyword(document.getKeyword());
			docListVo.setContentType(document.getContentType());
			docListVo.setSubType(document.getSubType());
			docListVo.setType(document.getType());
			docListVo.setCurrentlyPage(page);
			docListVo.setDocumentNumber(documentNumber);

			docListVo.setWeight(document.getWeight());
			String deleteUrl = "/rest/docs/" + document.getId();
			docListVo.setDeleteUrl(deleteUrl);
			String downloadUrl = "/rest/docs/" + document.getId()
					+ "/download/" + document.getName();
			docListVo.setDownloadUrl(downloadUrl);
			String viewUrl = "/rest/docs/" + document.getId() + "/view/"
					+ document.getName() + "";
			docListVo.setViewUrl(viewUrl);
			String viewByIdUrl = "/rest/docs/" + document.getId() + "";
			docListVo.setViewByIdUrl(viewByIdUrl);

			float length = document.getFileSize();
			if (length / KB >= 1 && length / KB < 1024) {
				docListVo.setSize(String.format("%.2f", length / KB) + "KB");
			} else if (length / MB >= 1 && length / MB < 1024) {
				docListVo.setSize(String.format("%.2f", length / MB) + "MB");
			} else if (length / GB >= 1) {
				docListVo.setSize(String.format("%.2f", length / GB) + "GB");
			} else {
				docListVo.setSize(length + "byte");
			}
			documentListVo.add(docListVo);
		}
		return documentListVo;
	}
}
