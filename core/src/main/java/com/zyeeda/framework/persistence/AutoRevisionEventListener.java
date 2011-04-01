package com.zyeeda.framework.persistence;

import java.util.Date;

import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import com.zyeeda.framework.entities.base.RevisionDomainEntity;
import com.zyeeda.framework.security.SecurityService;

public class AutoRevisionEventListener implements PreInsertEventListener, PreUpdateEventListener {

	private static final long serialVersionUID = 3017978089669707604L;

	private SecurityService<?> securitySvc;
	
	public AutoRevisionEventListener(SecurityService<?> securitySvc) {
		this.securitySvc = securitySvc;
	}
	
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		Object e = event.getEntity();
		if (e instanceof RevisionDomainEntity) {
			RevisionDomainEntity rev = (RevisionDomainEntity) e;
			Date now = new Date();
			rev.setCreator(this.securitySvc.getCurrentUser());
			rev.setCreatedTime(now);
			rev.setLastModifier(this.securitySvc.getCurrentUser());
			rev.setLastModifiedTime(now);
			return true;
		}
		return false;
	}
	
	private boolean setLastRevisionInfo(Object e) {
		if (e instanceof RevisionDomainEntity) {
			RevisionDomainEntity rev = (RevisionDomainEntity) e;
			rev.setLastModifier(this.securitySvc.getCurrentUser());
			rev.setLastModifiedTime(new Date());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		Object e = event.getEntity();
		return this.setLastRevisionInfo(e);
	}

}
