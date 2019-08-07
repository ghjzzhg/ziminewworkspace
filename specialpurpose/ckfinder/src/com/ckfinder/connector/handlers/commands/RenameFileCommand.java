/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.errors.ConnectorException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.FileManagerFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle
 * <code>RenameFile</code> command.
 */
public class RenameFileCommand extends XMLCommand implements IPostCommand {

	private String fileName;
	private String newFileName;
	private Integer errorFlag = 0;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createRenamedFileNode(rootElement);
		}

	}

	/**
	 * create rename file XML node.
	 *
	 * @param rootElement XML root node
	 */
	private void createRenamedFileNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("RenamedFile");
		element.setAttribute("name", this.fileName);
		element.setAttribute("newName", this.newFileName);
		element.setAttribute("errorFlag", this.errorFlag.toString());
		rootElement.appendChild(element);
	}

	/**
	 * gets data for XML and checks all validation.
	 *
	 * @return error code or 0 if it's correct.
	 */
	@Override
	protected int getDataForXml() {
		this.newFileName = this.request.getParameter("newFileName");
		String fileId = this.request.getParameter("fileId");
		GenericValue dataResource = null;
		try {
			dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
			GenericValue fileInfo = EntityQuery.use(delegator).from("TblFileOwnership").where(UtilMisc.toMap("fileId"	, fileId, "fileType", "1")).queryOne();
			if(UtilValidate.isNotEmpty(fileInfo)){
				List conditionList = new ArrayList();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fileInfo.get("partyId")));
				conditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, fileInfo.get("folderId")));
				conditionList.add(EntityCondition.makeCondition("fileName", EntityOperator.EQUALS, newFileName));
				List<GenericValue> fileList = EntityQuery.use(delegator).from("DataResourceFileList").where(conditionList).queryList();
				if(UtilValidate.isNotEmpty(fileList)){
					errorFlag = Constants.Errors.CKFINDER_FOLDER_REPEAT;
				}
			}
			dataResource.put("dataResourceName",newFileName);
			dataResource.store();
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}
}
