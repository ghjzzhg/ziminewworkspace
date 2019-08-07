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
package com.ckfinder.connector.handlers.command;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.errors.ConnectorException;
import net.wimpi.pim.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle
 * <code>GetFolders</code> command.
 */
public class GetFolderAttrCommand extends XMLCommand {

	/**
	 * list of subdirectories in directory.
	 */
	private List<Map<String,Object>> directories;
	private List<String> directoriesL;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException, GenericEntityException {

		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createFolderAttrData(rootElement);
		}
	}

	/**
	 * gets data for response.
	 *
	 * @return 0 if everything went ok or error code otherwise
	 */
	@Override
	protected int getDataForXml() {
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}


	/**
	 * creates folder data node in XML document.
	 *
	 * @param rootElement root element in XML document
	 */
	private void createFolderAttrData(final Element rootElement) throws GenericEntityException{
		String folderName = currentFolder.equals("/") ? type : currentFolder;
		String[] split = StringUtil.split(currentFolder, "/");
		if(null != split && split.length > 0){
			folderName = split[split.length - 1];
		}
		String fullCurrentPath = configuration.getTypes().get(this.type).getPath()
				+ this.currentFolder;
		String s = fullCurrentPath.substring(fullCurrentPath.length() - 1, fullCurrentPath.length());
		if("/".equals(s)){
			fullCurrentPath = fullCurrentPath.substring(0,fullCurrentPath.length() - 1);
		}
//		String num =  "[" + searchFileNumber(fullCurrentPath.replace("\\","/"),type) + "]";
		String num =  searchFileNumber(fullCurrentPath.replace("\\","/"),type) + "";
		Element element = creator.getDocument().createElement("FolderAttr");
		element.setAttribute("num", num);
		element.setAttribute("newName", folderName);
		rootElement.appendChild(element);
	}


}
