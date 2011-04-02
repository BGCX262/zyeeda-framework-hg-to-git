package com.zyeeda.framework.entities.base;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.zyeeda.framework.entities.ActionLog;

@javax.persistence.MappedSuperclass
public class FlowDomainEntity extends RevisionDomainEntity  {

	private static final long serialVersionUID = 7994729611645833909L;

	private List<ActionLog> actionLogs = new ArrayList<ActionLog>();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public List<ActionLog> getActionLogs() {
		return actionLogs;
	}

	public void setActionLogs(List<ActionLog> actionLogs) {
		this.actionLogs = actionLogs;
	}
	
}
