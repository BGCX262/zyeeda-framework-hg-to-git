package com.zyeeda.framework.entities;

import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Table(name = "ZDA_SYS_DOCUMENTVOS")
@XmlRootElement(name = "document")
public class DocumentListVo extends DocumentVo {

	private int currentlyPage;
	private int documentNumber;
	
	
	public int getDocumentNumber() {
		return documentNumber;
	}
	public int getCurrentlyPage() {
		return currentlyPage;
	}
	public void setCurrentlyPage(int currentlyPage) {
		this.currentlyPage = currentlyPage;
	}
	public void setDocumentNumber(int documentNumber) {
		this.documentNumber = documentNumber;
	}
}
