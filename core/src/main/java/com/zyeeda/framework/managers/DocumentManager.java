package com.zyeeda.framework.managers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import com.zyeeda.framework.entities.Document;

public interface DocumentManager {

	// 创建
	public void persist(Document doc) throws DocumentException, IOException;

	// 根据ID名字去查询
	public Document findByIdAndFileName(String id, String fileName)
			throws DocumentException;

	// 根据拥有人关键字数据类别去查询
	public List<Document> findByKeyword(String owner, String keyword,
			String foreignId, int skip, int limit) throws DocumentException;

	public void updateDocument(Document document) throws UnknownHostException,
			DocumentException;

	public void removeById(String id) throws UnknownHostException,
			DocumentException;

}
