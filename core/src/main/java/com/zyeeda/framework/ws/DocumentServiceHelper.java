package com.zyeeda.framework.ws;

import java.util.ArrayList;
import java.util.List;

import com.zyeeda.framework.entities.Document;
import com.zyeeda.framework.utils.DatetimeUtils;
import com.zyeeda.framework.viewmodels.DocumentVo;
import com.zyeeda.framework.viewmodels.DocumentsVo;

public class DocumentServiceHelper {

	public static DocumentVo document2Vo(Document doc) {
		DocumentVo vo = new DocumentVo();
		
		vo.setId(doc.getId());
		vo.setFileName(doc.getName());
		vo.setCreator(doc.getCreator());
		vo.setCreatedTime(DatetimeUtils.formatDatetime(doc.getCreatedTime()));
		vo.setFileType(doc.getFileType());
		vo.setFileSize(doc.getFileSize());
		
		vo.setDeleteUrl(String.format("/rs/docs/%s", doc.getId()));
		vo.setDownloadUrl(String.format("/rs/docs/%s/download/%s", doc.getId(), doc.getName()));
		vo.setViewUrl(String.format("/rs/docs/%s/view/%s", doc.getId(), doc.getName()));
		
		return vo;
	}
	
	public static DocumentsVo documentList2Vo(List<Document> docs) {
		DocumentsVo docsVo = new DocumentsVo();
		docsVo.setTotalRecords(docs.size());
		docsVo.setStartIndex(0);
		
		List<DocumentVo> docVos = new ArrayList<DocumentVo>(docs.size());
		for (Document doc : docs) {
			docVos.add(document2Vo(doc));
		}
		docsVo.setRecords(docVos);
		return docsVo;
	}
	
}
