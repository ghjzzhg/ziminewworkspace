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
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle
 * <code>CreateFolder</code> command.
 */
public class CreateFolderCommand extends XMLCommand implements IPostCommand {

	/**
	 * new folder name request param.
	 */
	private String newFolderName;
	private Integer errorFlag = 0;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createNewFolderElement(rootElement);
		}
	}

	/**
	 * creates current folder XML node.
	 *
	 * @param rootElement XML root element.
	 */
	private void createNewFolderElement(final Element rootElement) {
		Element element = creator.getDocument().createElement("NewFolder");
		element.setAttribute("name", this.newFolderName);
		element.setAttribute("id", this.folderId);
		element.setAttribute("errorFlag", errorFlag.toString());
		rootElement.appendChild(element);
	}

	/**
	 * gets data for xml. Not used in this handler.
	 *
	 * @return always 0
	 */
	@Override
	protected int getDataForXml() throws GenericEntityException {
		try {
			this.newFolderName = this.request.getParameter("NewFolderName");
			checkParam(newFolderName);
		} catch (ConnectorException e) {
			return e.getErrorCode();
		}
		String currentFolder = request.getParameter("currentFolder");
		String rootId = request.getParameter("rootFolderId");
		String foldeRemarks = request.getParameter("foldeRemarks");
		String rootFolder = rootId;
		if(!UtilValidate.isNotEmpty(currentFolder)){
			currentFolder = rootId;
		}
		if(UtilValidate.isNotEmpty(this.otherModules)){
			rootFolder = "1";
			if(currentFolder.equals("2")){
				currentFolder = "1";
			}
		}
		String folderInfo = getFolderPath(currentFolder, "folder");
		//防止外模块修改文件所属的partyId
		GenericValue fileInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderType","1","folderId",currentFolder)).queryOne();
		String filePartyId = this.partyId;
		if(UtilValidate.isNotEmpty(fileInfo)){
			filePartyId = fileInfo.get("partyId").toString();
		}
		String flag = saveFileInfo(folderInfo, newFolderName, currentFolder, rootFolder,"folder", foldeRemarks,filePartyId, "","");
		if(flag.equals("repeatError")){
			errorFlag = Constants.Errors.CKFINDER_FOLDER_REPEAT;
		}
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}
}
