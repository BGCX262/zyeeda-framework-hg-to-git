package com.zyeeda.framework.managers;

import java.util.List;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.zyeeda.framework.entities.ActionHistory;

public interface ActionHistoryManager extends GenericDAO<ActionHistory, String> {

	public ActionHistory findAlive(Long processInsId);
	
	public ActionHistory findAlive(Long processInsId, String nodeInsId);

	public List<ActionHistory> findListByProcessId(Long parseLong);
	
	public List<ActionHistory> findListByProcessCreator(String name);
	
	
}
