package com.zyeeda.framework.viewmodels;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "document")
public class DocumentVo {

	private static final long serialVersionUID = 6753120023803767202L;
	
	private String id;
	private String fileName;
	private long fileSize;
	private String fileType;
	private String creator;
	private String createdTime;
	
	private String updateUrl;
	private String viewUrl;
	private String downloadUrl;
	private String deleteUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	public String getUpdateUrl() {
		return updateUrl;
	}
	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	public String getDeleteUrl() {
		return deleteUrl;
	}
	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}
	
	public String getViewUrl() {
		return viewUrl;
	}
	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}
}
