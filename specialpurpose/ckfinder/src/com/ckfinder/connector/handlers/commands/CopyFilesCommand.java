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
import com.ckfinder.connector.data.FilePostParam;
import com.ckfinder.connector.errors.ConnectorException;
import org.ofbiz.entity.GenericEntityException;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Class to handle
 * <code>CopyFiles</code> command.
 */
public class CopyFilesCommand extends XMLCommand implements IPostCommand {


	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (creator.hasErrors()) {
			Element errorsNode = creator.getDocument().createElement("Errors");
			creator.addErrors(errorsNode);
			rootElement.appendChild(errorsNode);
		}
		createCopyFielsNode(rootElement);
	}

	/**
	 * creates copy file XML node.
	 *
	 * @param rootElement XML root node.
	 */
	private void createCopyFielsNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("CopyFiles");
		element.setAttribute("copied", "");
		element.setAttribute("copiedTotal", "");
		rootElement.appendChild(element);
	}

	@Override
	protected int getDataForXml() {
		try {
			return copyFiles();
		} catch (Exception e) {
			this.exception = e;
		}
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNKNOWN;

	}

	/**
	 * copy files from request.
	 *
	 * @return error code
	 * @throws IOException when ioexception in debug mode occurs
	 */
	private int copyFiles() throws IOException, GenericEntityException {
		return 0;
	}


	/**
	 * Get list of files from request.
	 *
	 * @param request - request object.
	 */
	private void getFilesListFromRequest(final HttpServletRequest request) {
		int i = 0;
		String paramName = "files[" + i + "][name]";
		while (request.getParameter(paramName) != null) {
			FilePostParam file = new FilePostParam();
			file.setName(request.getParameter(paramName));
			String folder = request.getParameter("files[" + i + "][folder]");
			paramName = "files[" + (++i) + "][name]";
		}
	}
}
