package com.zyeeda.framework.managers.internal;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

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
	
	
	public List<ActionHistory> findListByProcessCreator(String name){
		Search search = new Search();
		search.addFilterEqual("creator", name);
		return this.search(search);
	}
//	public List<Long> findListByProcessCreator(String name){
//		String sql = "select distinct f_process_ins_id  FROM ZDA_SYS_ACTION_HISTORY where f_creator = ?";
//		TypedQuery<Object[]> createNativeQuery = (TypedQuery<Object[]>) this.em().createNativeQuery(sql);
//		TypedQuery<Object[]> query = createNativeQuery;
//		query.setParameter(1, name);
//		List<Object[]> list = query.getResultList();
//		List<Long> listLong = new ArrayList<Long>();
//		Long instanceId = null;
//		for (Object[] objs : list) {
//			instanceId = (Long) objs[0] == null ? 0 : (Long) objs[0];
//			listLong.add(instanceId);
//		}
		
	//	return null;
	//}
}
