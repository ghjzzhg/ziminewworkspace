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
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.errors.DuplicateNameException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.GenericServiceException;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle
 * <code>RenameFolder</code> command.
 */
public class RenameFolderCommand extends XMLCommand implements IPostCommand {

	private String newFolderName;
	private String newFolderPath;
	private Integer errorFlag = 0;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createRenamedFolderNode(rootElement);
		}

	}

	/**
	 * creates XML node for renamed folder.
	 *
	 * @param rootElement XML root element.
	 */
	private void createRenamedFolderNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("RenamedFolder");
		element.setAttribute("newName", this.newFolderName);
		element.setAttribute("errorFlag", errorFlag.toString());
		rootElement.appendChild(element);

	}

	@Override
	protected int getDataForXml() {
		this.newFolderName = this.request.getParameter("newFolderName");
		String fileId = this.request.getParameter("folderId");
		String newfoldeRemark = this.request.getParameter("newfoldeRemark");
		try {
			dispatcher.runSync("renameFolder", UtilMisc.toMap("folderId", fileId, "newFolderName", newFolderName, "newfoldeRemark", newfoldeRemark, "userLogin", userLogin));

		} catch (DuplicateNameException e) {
			errorFlag = Constants.Errors.CKFINDER_FOLDER_REPEAT;
		} catch (GenericServiceException e1) {
			e1.printStackTrace();
			errorFlag = Constants.Errors.CKFINDER_CREATE_FOLDER_ERROR;
		}
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}


}
