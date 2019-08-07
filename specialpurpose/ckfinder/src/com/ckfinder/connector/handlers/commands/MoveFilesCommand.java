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

import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.FilePostParam;
import com.ckfinder.connector.errors.ConnectorException;
import org.ofbiz.entity.GenericEntityException;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle
 * <code>MoveFiles</code> command.
 */
public class MoveFilesCommand extends XMLCommand implements IPostCommand {

	private List<FilePostParam> files;
	private int filesMoved;
	private int movedAll;
	private boolean addMoveNode;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (creator.hasErrors()) {
			Element errorsNode = creator.getDocument().createElement("Errors");
			creator.addErrors(errorsNode);
			rootElement.appendChild(errorsNode);
		}

		if (addMoveNode) {
			createMoveFielsNode(rootElement);
		}
	}

	@Override
	protected int getDataForXml() throws GenericEntityException {
		return 0;
	}

	/**
	 * creates move file XML node.
	 *
	 * @param rootElement XML root element.
	 */
	private void createMoveFielsNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("MoveFiles");
		element.setAttribute("moved", String.valueOf(this.filesMoved));
		element.setAttribute("movedTotal",
				String.valueOf(this.movedAll + this.filesMoved));
		rootElement.appendChild(element);
	}

	/**
	 * move files.
	 *
	 * @return error code.
	 * @throws IOException when io error in debug mode occurs
	 */
	private int moveFiles() throws IOException {
		return 1;
	}

	public void initParams(final HttpServletRequest request,
			final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration);
		this.files = new ArrayList<FilePostParam>();
		this.movedAll = (request.getParameter("moved") != null) ? Integer.valueOf(request.getParameter("moved")) : 0;

		getFilesListFromRequest(request);

	}

	/**
	 * get file list to copy from request.
	 *
	 * @param request request
	 */
	private void getFilesListFromRequest(final HttpServletRequest request) {
		int i = 0;
		while (true) {
			String paramName = "files[" + i + "][name]";
			if (request.getParameter(paramName) != null) {
				FilePostParam file = new FilePostParam();
				file.setName(request.getParameter(paramName));
				String folder = request.getParameter("files[" + i + "][folder]");
//				if(!"/".equals(folder)){
//					file.setFolder(folder.substring(0,folder.lastIndexOf("("))+"/");
//				}else{
					file.setFolder(folder);
//				}
				file.setOptions(request.getParameter("files[" + i
						+ "][options]"));
				file.setType(request.getParameter("files[" + i + "][type]"));
				files.add(file);
			} else {
				break;
			}
			i++;
		}
	}
}
