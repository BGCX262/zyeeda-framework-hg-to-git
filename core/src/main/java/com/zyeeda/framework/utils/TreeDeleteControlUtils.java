package com.zyeeda.framework.utils;

import javax.naming.ldap.Control;

public class TreeDeleteControlUtils implements Control {

	private static final long serialVersionUID = 1L;

	@Override
	public byte[] getEncodedValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return "1.2.840.113556.1.4.805";
	}

	@Override
	public boolean isCritical() {
		// TODO Auto-generated method stub
		return Control.CRITICAL;
	}
	

}
