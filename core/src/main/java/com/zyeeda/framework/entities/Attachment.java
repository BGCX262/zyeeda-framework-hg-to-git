package com.zyeeda.framework.entities;

import java.sql.Blob;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.zyeeda.framework.entities.base.RevisionDomainEntity;

@Entity
@Table(name = "ZDA_SYS_ATTACHMENTS")
public class Attachment extends RevisionDomainEntity {

	private static final long serialVersionUID = -5913731949268189623L;
	
	private String foreignId;
	private int weight;
	private String owner;
	private Blob content;
	
	@Basic
	@Column(name = "F_FOREIGN_ID")
	public String getForeignId() {
		return this.foreignId;
	}
	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}
	
	@Basic
	@Column(name = "F_WEIGHT")
	public int getWeight() {
		return this.weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@Basic
	@Column(name = "F_OWNER")
	public String getOwner() {
		return this.owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "F_CONTENT")
	public Blob getContent() {
		return this.content;
	}
	public void setContent(Blob content) {
		this.content = content;
	}

}
