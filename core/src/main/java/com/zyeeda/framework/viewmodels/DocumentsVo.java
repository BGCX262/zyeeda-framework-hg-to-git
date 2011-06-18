package com.zyeeda.framework.viewmodels;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "documentListView")
public class DocumentsVo {

	private int currentPage;
	private int totalPage;
	private int totalCount;
	private String keyword;
	private List<DocumentVo> docs;
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public List<DocumentVo> getDocs() {
		return docs;
	}
	public void setDocs(List<DocumentVo> docs) {
		this.docs = docs;
	}
	
	
}
