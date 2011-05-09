package com.zyeeda.framework.managers.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.managers.DocumentException;
import com.zyeeda.framework.managers.DocumentManager;

public class MongoDbDocumentManager implements DocumentManager {

	private static final Logger logger = LoggerFactory
			.getLogger(MongoDbDocumentManager.class);
	private static String addr = "localhost";
	private static int port = 27017;
	private static String DB = "test";

	private DBCollection getCollection(String addr, int port, String test)
			throws UnknownHostException, MongoException {
		DBCollection collection = null;
		Mongo mongo = null;

		mongo = new Mongo(addr, port);
		collection = mongo.getDB(DB).getCollection("test");
		return collection;

	}

	@Override
	public void persist(Document document) throws IOException,
			DocumentException {
		InputStream is = null;
		try {
			is = document.getContent();
			DBCollection collection = this.getCollection(addr, port, DB);

			GridFS gridFS = new GridFS(collection.getDB());
			GridFSFile gridFSFile = gridFS.createFile(is);
			this.setData(gridFSFile, document);
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		} catch (MongoException e) {
			throw new DocumentException(e);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public void setData(GridFSFile gridFSFile, Document document) {
		gridFSFile.put("filename", document.getName());
		gridFSFile.put("description", document.getDescription());
		gridFSFile.put("creator", document.getCreator());
		gridFSFile.put("lastModifier", document.getLastModifier());
		gridFSFile.put("lastModifiedTime", document.getLastModifiedTime());
		gridFSFile.put("foreignId", document.getForeignId());
		gridFSFile.put("weight", document.getWeight());
		gridFSFile.put("owner", document.getOwner());
		gridFSFile.put("fileType", document.getFileType());
		gridFSFile.put("keyword", document.getKeyword());
		gridFSFile.put("subType", document.getSubType());
		gridFSFile.put("type", document.getType());
		gridFSFile.put("contentType", document.getContentType());
		gridFSFile.save();
	}

	private List<Document> find(Map<String, Object> map, String keyword, String foreignId,
			int skip, int limit) throws DocumentException, MongoException {
		try {
			Document document = new Document();
			DBCollection collection = getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			BasicDBObject basicDBObject = new BasicDBObject();
			if (StringUtils.isNotBlank(foreignId)) {
				basicDBObject.put("foreignId", foreignId);
			}
			if (map != null) {
				basicDBObject.put((String) map.get("condition"), map
						.get("value"));

			}
			if (StringUtils.isNotBlank(keyword)) {
				basicDBObject.put("keyword", keyword);
			}

			DBCursor cursor = gridFS.getFileList(basicDBObject).skip(skip)
					.limit(limit);
			List<Document> documentList = new ArrayList<Document>(cursor
					.count());
			while (cursor.hasNext()) {
				GridFSDBFile gridFSDBFile = (GridFSDBFile) cursor.next();
				document.setId(gridFSDBFile.getId().toString());
				document.setName(gridFSDBFile.getFilename());
				document.setDescription((String) gridFSDBFile
						.get("description"));
				document.setCreator((String) gridFSDBFile.get("creator"));
				document.setCreatedTime(gridFSDBFile.getUploadDate());
				document.setLastModifier((String) gridFSDBFile
						.get("lastModifier"));
				document.setLastModifiedTime((Date) gridFSDBFile
						.get("lastModifiedTime"));
				document.setForeignId((String) gridFSDBFile.get("foreignId"));
				Object obj = gridFSDBFile.get("weight");
				if (obj != null) {
					document.setWeight(Integer.parseInt(obj.toString()));
				}
				document.setOwner((String) gridFSDBFile.get("owner"));
				document.setFileSize(gridFSDBFile.getLength()); // 系统默认的
				document.setType((String) gridFSDBFile.get("fileType"));
				document.setKeyword((String) gridFSDBFile.get("keyword"));
				document.setContentType(gridFSDBFile.getContentType());
				document.setSubType((String) gridFSDBFile.get("subType"));
				document.setType((String) gridFSDBFile.get("type"));
				documentList.add(document);
			}
			return documentList;
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		}
	}

	@Override
	public List<Document> findByKeyword(String owner, String keyword,
			String foreignId, int skip, int limit) throws DocumentException {
		try {
			Map<String, Object> map = null;
			if (StringUtils.isNotBlank(owner)) {
				map = new HashMap<String, Object>();
				map.put("condition", "owner");
				map.put("value", owner);
			}
			return this.find(map, keyword, foreignId, skip, limit);
		} catch (MongoException e) {
			throw new DocumentException(e);
		}
	}

	@Override
	public void removeById(String id) throws DocumentException {
		logger.debug("remove by id = {}", id);
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			gridFS.remove(new ObjectId(id));
		} catch (MongoException e) {
			throw new DocumentException(e);
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void updateDocument(Document document) throws DocumentException {
		try {
			Mongo mongo = new Mongo(addr,port);
			DBCollection collection = mongo.getDB(DB).getCollection("fs.files");
			collection.update(new BasicDBObject("_id", new ObjectId(document
					.getId())), new BasicDBObject("$set", new BasicDBObject(
					"description", document.getDescription())), false, true);
		} catch (MongoException e) {
			throw new DocumentException(e);
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		}
	}

	@Override
	public Document findByIdAndFileName(String id, String fileName)
			throws DocumentException {
		logger.debug("find by id [{}] and file name [{}]", id, fileName);
		DBCollection collection;
		try {
			collection = this.getCollection(addr, port, DB);

			GridFS gridFS = new GridFS(collection.getDB());
			BasicDBObject object = new BasicDBObject();
			object.put("filename", fileName);
			object.put("_id", new ObjectId(id));
			GridFSDBFile gridFSDbFile = gridFS.findOne(object);
			Document document = new Document();
			document.setId(gridFSDbFile.getId().toString());
			document.setName(gridFSDbFile.getFilename());
			document.setDescription((String) gridFSDbFile.get("description"));
			document.setCreator((String) gridFSDbFile.get("creator"));
			document.setCreatedTime((Date) gridFSDbFile.get("createdTime"));
			document.setLastModifier((String) gridFSDbFile.get("lastModifier"));
			document.setLastModifiedTime((Date) gridFSDbFile
					.get("lastModifiedTime"));
			document.setForeignId((String) gridFSDbFile.get("foreignId"));
			Object weightObj = gridFSDbFile.get("weight");
			if (weightObj != null) {
				document.setWeight(Integer.parseInt((weightObj.toString())));
			}
			document.setOwner((String) gridFSDbFile.get("owner"));
			document.setFileSize(gridFSDbFile.getLength()); // 系统默认的
			document.setFileType((String) gridFSDbFile.get("fileType"));
			document.setKeyword((String) gridFSDbFile.get("keyword"));
			document.setContent(gridFSDbFile.getInputStream());
			document.setContentType(gridFSDbFile.getContentType());
			document.setSubType((String) gridFSDbFile.get("subType"));
			document.setType((String) gridFSDbFile.get("type"));
			return document;
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		} catch (MongoException e) {
			throw new DocumentException(e);

		}
	}

}
