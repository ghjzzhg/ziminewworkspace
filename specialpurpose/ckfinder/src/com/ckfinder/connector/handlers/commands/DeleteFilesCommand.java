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
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

/**
 * Class used to handle
 * <code>DeleteFiles</code> command.
 */
public class DeleteFilesCommand extends XMLCommand implements IPostCommand {

	@Override
	protected void createXMLChildNodes(int errorNum, Element rootElement) throws ConnectorException {
		if (creator.hasErrors()) {
			Element errorsNode = creator.getDocument().createElement("Errors");
			creator.addErrors(errorsNode);
			rootElement.appendChild(errorsNode);
		}
		createDeleteFielsNode(rootElement);
	}

	/**
	 * Adds delete file node in XML.
	 *
	 * @param rootElement - root element in XML response.
	 */
	private void createDeleteFielsNode(Element rootElement) {
			Element element = creator.getDocument().createElement("DeleteFiles");
		element.setAttribute("deleted","");
		rootElement.appendChild(element);
	}

	/**
	 * Prepares data for XML response.
	 *
	 * @return error code or 0 if action ended with success.
	 */
	@Override
	protected int getDataForXml() {
		getFilesListFromRequest(request);
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	/**
	 * Gets list of files from request.
	 *
	 * @param request - request object.
	 */
	private void getFilesListFromRequest(HttpServletRequest request){
		int i = 0;
		String paramName = "files[" + i + "][fileId]";
		while (request.getParameter(paramName) != null) {
			String fileId = request.getParameter("files[" + i + "][fileId]");
			delFileInfo(fileId,"file");
			paramName = "files[" + (++i) + "][fileId]";
		}
	}
}
