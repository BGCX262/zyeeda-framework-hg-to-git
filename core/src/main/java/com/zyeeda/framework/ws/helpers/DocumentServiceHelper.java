package com.zyeeda.framework.ws.helpers;

import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.viewmodels.DocumentVo;

public class DocumentServiceHelper {

	public static DocumentVo document2Vo(Document doc) {
		DocumentVo vo = new DocumentVo();
		
		vo.setId(doc.getId());
		vo.setFileName(doc.getName());
		vo.setCreator(doc.getCreator());
		vo.setCreatedTime(doc.getCreatedTime());
		vo.setFileType(doc.getFileType());
		vo.setFileSize(doc.getFileSize());
		
		vo.setDeleteUrl(String.format("/rest/docs/%s", doc.getId()));
		vo.setDownloadUrl(String.format("/rest/docs/%s/download/%s", doc.getId(), doc.getName()));
		vo.setViewUrl(String.format("/rest/docs/%s/view/%s", doc.getId(), doc.getName()));
		
		return vo;
	}
	
}
