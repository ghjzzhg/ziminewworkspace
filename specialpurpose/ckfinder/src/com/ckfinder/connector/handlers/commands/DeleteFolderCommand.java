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

/**
 * Class to handle
 * <code>DeleteFolder</code> command.
 */
public class DeleteFolderCommand extends XMLCommand implements IPostCommand {

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
	}

	/**
	 * @return error code or 0 if ok. Deletes folder and thumb folder.
	 */
	@Override
	protected int getDataForXml() {
		String folderId = request.getParameter("folderId");
		delFileInfo(folderId,"folder");
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}
}
