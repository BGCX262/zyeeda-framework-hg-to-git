package com.zyeeda.framework.entities;

import java.io.Serializable;
import java.util.Date;

import com.zyeeda.framework.entities.base.SimpleDomainEntity;

public class User extends SimpleDomainEntity implements Serializable {

	private static final long serialVersionUID = -411862891641683217L;

	private String id;
	private String username;
	private String password;
	private String gender;
	private String position;
	private String degree;
	private String email;
	private String mobile;
	private Date birthday;
	private Date dateOfWork;
	private Boolean status;
	private Boolean postStatus;
	private byte[] photo;
	private String departmentName;
	private String deptFullPath;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getDateOfWork() {
		return dateOfWork;
	}

	public void setDateOfWork(Date dateOfWork) {
		this.dateOfWork = dateOfWork;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Boolean getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(Boolean postStatus) {
		this.postStatus = postStatus;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDeptFullPath() {
		return deptFullPath;
	}

	public void setDeptFullPath(String deptFullPath) {
		this.deptFullPath = deptFullPath;
	}

}