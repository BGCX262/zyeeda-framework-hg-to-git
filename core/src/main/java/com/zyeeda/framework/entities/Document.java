package com.zyeeda.framework.entities;

import java.io.InputStream;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.zyeeda.framework.entities.base.SimpleDomainEntity;

@Entity
@Table(name = "ZDA_SYS_DOCUMENTS")
@XmlRootElement(name = "document")
public class Document extends SimpleDomainEntity {

	private static final long serialVersionUID = -5913731949268189623L;
	
	private String foreignId;
	private int weight;
	private String owner;
	private String creator;


	private Date createdTime;
    private String lastModifier;
    private Date lastModifiedTime;
    private long fileSize;
	
    public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	private String fileType; 
    private String keyword;
    private InputStream content;
    private String contentType;
    


	private String subType;
    private String type;
    
    
    
    
    public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}



	public Document() {
    	Date now = new Date();
    	this.createdTime = now;
    	this.lastModifiedTime = now;
    }
    
	public String getForeignId() {
		return foreignId;
	}
	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getLastModifier() {
		return lastModifier;
	}
	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	

	public String getKeyword() {
		return this.keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Transient
	public InputStream getContent() {
		return content;
	}
	
	public void setContent(InputStream content) {
		this.content = content;
	}
    
    public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
