package com.zyeeda.framework.entities;

import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Table(name = "ZDA_SYS_DOCUMENTVOS")
@XmlRootElement(name = "document")
public class DocumentVo extends Document{

	private String viewByIdUrl;
	private String updateUrl;
	private String viewUrl;
	private String downloadUrl;
	private String deleteUrl;
	private String uplodTime;
	private String size;

	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getUplodTime() {
		return uplodTime;
	}
	public void setUplodTime(String uplodTime) {
		this.uplodTime = uplodTime;
	}
	public String getViewByIdUrl() {
		return viewByIdUrl;
	}
	public void setViewByIdUrl(String viewByIdUrl) {
		this.viewByIdUrl = viewByIdUrl;
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
