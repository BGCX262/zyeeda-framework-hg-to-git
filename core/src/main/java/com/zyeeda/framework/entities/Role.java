package com.zyeeda.framework.entities;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.Column;
import javax.persistence.Basic;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.CollectionUtils;

import com.zyeeda.framework.entities.base.SimpleDomainEntity;

@Entity
@Table(name = "SYS_ROLE")
@NamedNativeQuery(
		name = "getRolesBySubject",
		query = "SELECT r.F_ID, r.F_NAME, r.F_DESCRIPTION, r.F_SCOPE_TYPE, r.F_SCOPE_ID, r.F_PERMISSIONS "
			+ "FROM SYS_ROLE r "
			+ "LEFT JOIN SYS_SUBJECT s ON s.F_ROLE_ID = r.F_ID "
			+ "WHERE s.F_SUBJECT = :subject",
		resultClass = Role.class
)
@NamedQuery(
		name = "getRoles",
		query = "SELECT r FROM Role r"
)
public class Role extends SimpleDomainEntity {

	private static final long serialVersionUID = 1665902703034523260L;
	private static final char PERMISSION_SEPARATOR = ';';
	
	private Set<String> subjects;
	private String permissions;
	private String scopeType;
	private String scopeId;
	
	@ElementCollection
	@CollectionTable(name="SYS_SUBJECT", joinColumns=@JoinColumn(name="F_ROLE_ID"))
	@Column(name="F_SUBJECT")
	public Set<String> getSubjects() {
		return this.subjects;
	}
	public void setSubjects(Set<String> subjects) {
		this.subjects = subjects;
	}
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "F_PERMISSIONS", length = 4000)
	public String getPermissions() {
		return this.permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	
	@Transient
	public Set<String> getPermissionSet() {
		String permissions = this.getPermissions();
		String[] permissionArray = StringUtils.split(permissions, PERMISSION_SEPARATOR);
		return CollectionUtils.asSet(permissionArray);
	}
	
	@Basic
	@Column(name = "F_SCOPE_TYPE")
	public String getScopeType() {
		return this.scopeType;
	}
	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}
	
	@Basic
	@Column(name = "F_SCOPE_ID")
	public String getScopeId() {
		return this.scopeId;
	}
	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}

}
