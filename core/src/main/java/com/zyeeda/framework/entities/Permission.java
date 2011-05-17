package com.zyeeda.framework.entities;


public class Permission {
	private String id;
	private String name;
	private String value;
	private String isHaveIO;
	private int path = 0;
	public int getPath() {
		return path;
	}
	public void setPath(int path) {
		this.path = path;
	}
	private Boolean checked;
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	
	public String getIsHaveIO() {
		return isHaveIO;
	}
	public void setIsHaveIO(String isHaveIO) {
		this.isHaveIO = isHaveIO;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
