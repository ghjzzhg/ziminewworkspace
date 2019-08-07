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
import org.ofbiz.entity.GenericEntityException;
import org.w3c.dom.Element;

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

		Element element = creator.getDocument().createElement("FolderAttr");
		element.setAttribute("num", "");
		rootElement.appendChild(element);
	}


}
