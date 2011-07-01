package com.zyeeda.framework.managers.internal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

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
		search.addFilterEqual("nodeType", "StateNode");
		search.addSortDesc("createdTime");
		return this.search(search);
	}
	
	
	public List<ActionHistory> findListByProcessCreator(String name){
		Search search = new Search();
		search.addFilterEqual("creator", name);
		return this.search(search);
	}
	
	public List<Long> findListByProcessCreator1(String name){
		String sql = "select distinct f_process_ins_id  FROM ZDA_SYS_ACTION_HISTORY where f_creator = ?";
		Query query  =  this.em().createNativeQuery(sql);
		query.setParameter(1, name);
		List<Long> longList = new ArrayList<Long>();
		List<Object[]> list = query.getResultList();
		for(int i = 0; i < list.size(); i ++) {
			Object[] obj = list.get(i);
			String processId = (String)obj[0];
			Long longId = Long.parseLong(processId);
			longList.add(longId);
		}
		return longList;
	}
}
