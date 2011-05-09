package com.zyeeda.framework.managers.internal;

import java.util.List;

import com.googlecode.genericdao.search.Search;
import com.zyeeda.framework.entities.ActionHistory;
import com.zyeeda.framework.managers.ActionHistoryManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class DefaultActionHistoryManager extends DomainEntityManager<ActionHistory, String>
		implements ActionHistoryManager {

	public DefaultActionHistoryManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}
	
	public ActionHistory findAlive(Long processInsId) {
		Search search = new Search();
		search.addFilterEqual("processInstanceId", processInsId);
		search.addFilterEqual("alive", true);
		return this.searchUnique(search);
	}
	
	public ActionHistory findAlive(Long processInsId, String nodeInsId) {
		Search search = new Search();
		search.addFilterEqual("processInstanceId", processInsId);
		search.addFilterEqual("nodeInstanceId", nodeInsId);
		search.addFilterEqual("alive", true);
		return this.searchUnique(search);
	}
	public List<ActionHistory> findListByProcessId(Long processInsId){
		Search search = new Search();
		search.addFilterEqual("processInstanceId", processInsId);
		return this.search(search);
	}


}
