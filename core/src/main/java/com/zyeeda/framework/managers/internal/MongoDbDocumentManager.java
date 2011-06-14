package com.zyeeda.framework.managers.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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
import com.mongodb.DBObject;
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

	// mongo 没有关闭
	// TODO
	private DBCollection getCollection(String addr, int port, String test)
			throws UnknownHostException, MongoException {
		DBCollection collection = null;
		Mongo mongo = new Mongo(addr, port);
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
		logger.debug("setData Document.creator= {}", document.getCreator());
		gridFSFile.put("filename", document.getName());// 数据库自带filename
		gridFSFile.put("description", document.getDescription());
		gridFSFile.put("creator", document.getCreator());
		gridFSFile.put("uploadDate", document.getCreatedTime());// 数据库自带uploadDate
		gridFSFile.put("lastModifier", document.getLastModifier());
		gridFSFile.put("lastModifiedTime", document.getLastModifiedTime());
		gridFSFile.put("foreignId", document.getForeignId());
		gridFSFile.put("weight", document.getWeight());
		gridFSFile.put("owner", document.getOwner());
		gridFSFile.put("fileType", document.getFileType());// 数据库自带fileType
		gridFSFile.put("keyword", document.getKeyword());
		gridFSFile.put("subType", document.getSubType());
		gridFSFile.put("type", document.getType());
		gridFSFile.put("contentType", document.getContentType());// 数据库自带contentType
		gridFSFile.save();
	}

	
	// 集合的查
	private List<Document> find(Map<String, Object> map,
			Map<String, Object> map1, String foreignId, int skip, int limit)
			throws DocumentException, MongoException {
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			BasicDBObject basicDBObject = new BasicDBObject();
			if (StringUtils.isNotBlank(foreignId)) {
				basicDBObject.put("foreignId", foreignId);
			}
			if (map != null) {
				basicDBObject.put((String) map.get("condition"), map
						.get("value"));
			}
			if (map1 != null) {
				DBObject in = new BasicDBObject("$in", map1.get("value"));
				basicDBObject.put((String) map1.get("condition"), in);
			}
			BasicDBObject condition = new BasicDBObject();
			condition.put("lastModifiedTime", -1);// 排序的元素 根据时间从大到小
			DBCursor cursor = gridFS.getFileList(basicDBObject).sort(condition)
					.skip(skip).limit(limit);
			List<Document> documentList = new ArrayList<Document>(cursor
					.count());
			while (cursor.hasNext()) {
				Document document = new Document();
				GridFSDBFile gridFSDBFile = (GridFSDBFile) cursor.next();
				document.setId(gridFSDBFile.getId().toString());
				document.setName(gridFSDBFile.getFilename());
				document.setDescription((String) gridFSDBFile
						.get("description"));
				document.setCreator((String) gridFSDBFile.get("creator"));
				document.setFileType((String) gridFSDBFile.get("fileType"));
				document.setFileSize(gridFSDBFile.getLength());
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
				document.setCreatedTime(gridFSDBFile.getUploadDate());
				document.setOwner((String) gridFSDBFile.get("owner"));
				document.setFileType((String) gridFSDBFile.get("fileType"));
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
	public List<Document> findByKeyword(String owner, String[] keyword,
			String foreignId, int skip, int limit) throws DocumentException {
		try {
			Map<String, Object> map = null;
			Map<String, Object> map1 = null;
			if (StringUtils.isNotBlank(owner)) {
				map = new HashMap<String, Object>();
				map.put("condition", "owner");
				map.put("value", owner);
			}
			map1 = new HashMap<String, Object>();
			map1.put("condition", "keyword");
			map1.put("value", keyword);

			return this.find(map, map1, foreignId, skip, limit);
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

	// 批量删除
	@Override
	public void allremoveById(String[] id) throws DocumentException {
		logger.debug("remove by id = {}", id);
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			for (int i = 0; i < id.length; i++) {
				logger.debug("removeId = {}", id[i]);
				gridFS.remove(new ObjectId(id[i]));
			}
		} catch (MongoException e) {
			throw new DocumentException(e);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateDocument(Document document) throws DocumentException {
		try {
			logger.debug("updateDocument keyword={}", document.getKeyword());
			logger.debug("updateDocument name={}", document.getName());
			logger.debug("updateDocument desc={}", document.getDescription());
			Mongo mongo = new Mongo(addr, port);
			DBCollection collection = mongo.getDB(DB).getCollection("fs.files");
			BasicDBObject updateObject = new BasicDBObject();
			if (StringUtils.isNotBlank(document.getDescription())) {
				updateObject.put("description", document.getDescription());
			}
			if (StringUtils.isNotBlank(document.getKeyword())) {
				updateObject.put("keyword", document.getKeyword());
			}
			if (StringUtils.isNotBlank(document.getName())) {
				updateObject.put("filename", document.getName());
			}
			if (StringUtils.isNotBlank(document.getLastModifier())) {
				updateObject.put("lastModifier", document.getLastModifier());
			}
			if(document.getLastModifiedTime()!=null){
				updateObject.put("lastModifiedTime", document.getLastModifiedTime());
			}
			
			BasicDBObject basic = new BasicDBObject();
			basic.put("$set", updateObject);
			collection.update(new BasicDBObject("_id", new ObjectId(document
					.getId())), basic, false, true);
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
			if (gridFSDbFile != null) {
				Document document = new Document();
				document.setId(gridFSDbFile.getId().toString());
				document.setName(gridFSDbFile.getFilename());
				document.setDescription((String) gridFSDbFile
						.get("description"));
				document.setCreator((String) gridFSDbFile.get("creator"));
				document.setCreatedTime((Date) gridFSDbFile.get("createdTime"));
				document.setLastModifier((String) gridFSDbFile
						.get("lastModifier"));
				document.setLastModifiedTime((Date) gridFSDbFile
						.get("lastModifiedTime"));
				document.setForeignId((String) gridFSDbFile.get("foreignId"));
				Object weightObj = gridFSDbFile.get("weight");
				if (weightObj != null) {
					document
							.setWeight(Integer.parseInt((weightObj.toString())));
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
			} else {
				return null;
			}

		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		} catch (MongoException e) {
			throw new DocumentException(e);

		}
	}

//	@Override
//	public List<Document> findByKeyword(String owner, int skip, int limit)
//			throws DocumentException {
//		Map<String, Object> map = null;
//		Map<String, Object> map1 = null;
//		if (StringUtils.isNotBlank(owner)) {
//			map = new HashMap<String, Object>();
//			map.put("condition", "owner");
//			map.put("value", owner);
//		}
//		return this.find(map, map1, null, skip, limit);
//	}

	@Override
	public List<Document> findByKeyword(String owner, String foreignId,
			int skip, int limit) throws DocumentException {
		Map<String, Object> map = null;
		Map<String, Object> map1 = null;
		if (StringUtils.isNotBlank(owner)) {
			map = new HashMap<String, Object>();
			map.put("condition", "owner");
			map.put("value", owner);
		}
		return this.find(map, map1, foreignId, skip, limit);
	}

	@Override
	public int findNumber(String owner, String foreignId, String[] keyword)
			throws DocumentException {
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			BasicDBObject basicDBObject = new BasicDBObject();
			if (StringUtils.isNotBlank(foreignId)) {
				basicDBObject.put("foreignId", foreignId);
			}
			if (StringUtils.isNotBlank(owner)) {
				basicDBObject.put("owner", owner);
			}
			DBObject in = new BasicDBObject("$in", keyword);
			basicDBObject.put("keyword", in);
			return gridFS.find(basicDBObject).size();
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		}
	}

	@Override
	public int findNumber(String foreignId, String[] keyword)
			throws DocumentException {
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			BasicDBObject basicDBObject = new BasicDBObject();
			if (StringUtils.isNotBlank(foreignId)) {
				basicDBObject.put("foreignId", foreignId);
			}
			if (keyword.length > 1) {
				DBObject in = new BasicDBObject("$in", keyword);
				basicDBObject.put("keyword", in);
			}
			return gridFS.find(basicDBObject).size();
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		}
	}

	@Override
	public int findOwnerNumber(String owner, String foreignId)
			throws DocumentException {
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			BasicDBObject basicDBObject = new BasicDBObject();
			if (StringUtils.isNotBlank(foreignId)) {
				basicDBObject.put("foreignId", foreignId);
			}
			if (StringUtils.isNotBlank(owner)) {
				basicDBObject.put("owner", owner);
			}
			return gridFS.find(basicDBObject).size();
		} catch (UnknownHostException e) {
			throw new DocumentException(e);
		}
	}

	@Override
	public Document findById(String id) throws DocumentException {
		try {
			DBCollection collection = this.getCollection(addr, port, DB);
			GridFS gridFS = new GridFS(collection.getDB());
			GridFSDBFile gridFSDbFile = gridFS.find(new ObjectId(id));
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
			// document.setFileSize(gridFSDbFile.getLength()); // 系统默认的
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
