package com.zyeeda.framework.managers;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.zyeeda.framework.entities.ActionHistory;

public interface ActionHistoryManager extends GenericDAO<ActionHistory, String> {

	public ActionHistory findAlive(Long processInsId);
	
	public ActionHistory findAlive(Long processInsId, Long nodeInsId);
	
}