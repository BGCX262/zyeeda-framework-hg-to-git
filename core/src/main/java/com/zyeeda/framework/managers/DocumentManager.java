package com.zyeeda.framework.managers;

import java.util.List;

import com.zyeeda.framework.entities.Document;

public interface DocumentManager {
	
	public void persist(Document doc);
	
	public Document findById(String id, boolean includeContent);
	
	public Document findByIdAndFileName(String id, String fileName);
	
	public void removeById(String id);
	
	public long countByForeignId(String foreignId);
	
	public void replaceForeignId(String oldForeignId, String newForeignId);
	
	public List<Document> findByForeignId(String foreignId);
	
	public long countByIsTemp();
	
	public void eraseTemp();
	
	public long countBySuffixes(String foreignId, String tempForeignId, String... suffixes);
	
	
	
	
	
	
	/*
	public int findNumber(String owner,String foreignId,String[] keyword) throws DocumentException;
	
	public int findNumber(String foreignId, String[] keyword) throws DocumentException;
	public int findOwnerNumber(String owner,String foreignId) throws DocumentException;
	
	
	
	

	// 根据ID名字去查询
	

	// 根据拥有人关键字数据类别去查询
	public List<Document> findByKeyword(String owner, String[] keyword,
			String foreignId, int skip, int limit) throws DocumentException;

	
	//public List<Document> findByKeyword(String owner,  int skip, int limit) throws DocumentException;
	
	public List<Document> findByKeyword(String owner,String foreignId,  int skip, int limit) throws DocumentException;
	
	
	public void updateDocument(Document document) throws UnknownHostException,
			DocumentException;

	

	public void allremoveById(String[] id) throws DocumentException;

	*/
}
