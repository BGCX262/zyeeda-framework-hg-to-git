package com.zyeeda.drivebox.entities;

import com.zyeeda.framework.entities.Dictionary;

@javax.persistence.Entity
@javax.persistence.DiscriminatorValue("priority")
public class Priority extends Dictionary {

	private static final long serialVersionUID = -6775283130479671135L;

}
