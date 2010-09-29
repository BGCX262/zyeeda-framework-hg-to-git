package com.zyeeda.framework.entities;

import javax.validation.constraints.NotNull;

@javax.persistence.MappedSuperclass
public class LiteDomainEntity extends DomainEntity {

    private static final long serialVersionUID = -2200108673372668900L;
	
    private String name;
    private String description;
    
    @javax.persistence.Basic
    @javax.persistence.Column(name = "F_NAME")
    @NotNull
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @javax.persistence.Basic
    @javax.persistence.Column(name = "F_DESCRIPTION", length = 2000)
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}