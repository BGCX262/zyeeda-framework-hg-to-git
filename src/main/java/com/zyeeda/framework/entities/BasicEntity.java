package com.zyeeda.framework.entities;

import java.util.Date;

public class BasicEntity extends Entity {

	private String name;
    private String description;
    private String owner;
    private String creator;
    private Date creationTime;
    private String lastModifier;
    private Date lastModificationTime;
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOwner() {
    	return this.owner;
    }
    public void setOwner(String owner) {
    	this.owner = owner;
    }
    
    public String getCreator() {
        return this.creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public Date getCreationTime() {
        return this.creationTime;
    }
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
    
    public String getLastModifier() {
        return this.lastModifier;
    }
    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }
    
    public Date getLastModificationTime() {
        return this.lastModificationTime;
    }
    public void setLastModificationTime(Date lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }
	
}
