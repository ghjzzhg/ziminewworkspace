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
import com.ckfinder.connector.utils.XMLCreator;
import org.ofbiz.base.lang.json.JSONObject;
import org.ofbiz.base.lang.json.XML;
import org.ofbiz.entity.GenericEntityException;
import org.w3c.dom.Element;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Base class to handle XML commands.
 */
public abstract class XMLCommand extends Command {

	/**
	 * util to create XML document.
	 */
	protected XMLCreator creator;

	/**
	 * sets response headers for XML response.
	 *
	 * @param response response
	 * @param sc servlet context
	 */
	public void setResponseHeader(final HttpServletResponse response,
			final ServletContext sc) {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("utf-8");
	}

	/**
	 * executes XML command. Creates XML response and writes it to response
	 * output stream.
	 *
	 * @param out response output stream
	 * @throws ConnectorException to handle in error handler.
	 */
	public void execute(final OutputStream out) throws ConnectorException {
		creator = new XMLCreator();
		creator.createDocument();
		try {
			createXMLResponse(getDataForXml());
			out.write(creator.getDocumentAsText().getBytes("UTF-8"));
		} catch (ConnectorException e) {
			throw e;
		} catch (IOException e) {
			throw new ConnectorException(Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * executes XML command. Creates XML response and writes it to response
	 * output stream.
	 *
	 * @param out response output stream
	 * @throws ConnectorException to handle in error handler.
	 */
	public void executeJson(final OutputStream out) throws ConnectorException {
		creator = new XMLCreator();
		creator.createDocument();
		try {
			createXMLResponse(getDataForXml());
			JSONObject xmlJSONObj = XML.toJSONObject(creator.getDocumentAsText());
			String jsonPrettyPrintString = xmlJSONObj.toString(4);
			out.write(jsonPrettyPrintString.getBytes("UTF-8"));
		} catch (ConnectorException e) {
			throw e;
		} catch (IOException e) {
			throw new ConnectorException(Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}
	/**
	 * executes XML command. Creates XML response and writes it to response
	 * output stream.
	 *
	 * @param out response output stream
	 * @throws ConnectorException to handle in error handler.
	 */
	public void executeFineUpload(final OutputStream out) throws ConnectorException {
		creator = new XMLCreator();
		creator.createDocument();
		try {
			getDataForXml();
			out.write("{\"success\": true}".getBytes("UTF-8"));
		} catch (IOException e) {
			throw new ConnectorException(Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * abstract method to create XML response in command.
	 * 抽象方法来创建XML响应命令。
	 * @param errorNum error code from method getDataForXml()
	 * @throws ConnectorException to handle in error handler.
	 */
	private void createXMLResponse(final int errorNum) throws ConnectorException, GenericEntityException {
		Element rootElement = creator.getDocument().createElement("Connector");
		creator.addErrorCommandToRoot(rootElement, errorNum, getErrorMsg(errorNum));
		createXMLChildNodes(errorNum, rootElement);
		creator.getDocument().appendChild(rootElement);
	}

	/**
	 * gets error message if needed.
	 *
	 * @param errorNum error code
	 * @return error message
	 */
	protected String getErrorMsg(final int errorNum) {
		return null;
	}

	/**
	 * abstract method to create XML nodes for commands.
	 *
	 * @param errorNum error code
	 * @param rootElement XML root node
	 * @throws ConnectorException to handle in error handler.
	 */
	protected abstract void createXMLChildNodes(final int errorNum,
			final Element rootElement)
			throws ConnectorException, GenericEntityException;

	/**
	 * gets all necessary data to create XML response.
	 *
	 * @return error code {@link Constants.Errors}
	 * or
	 * {@link Constants.Errors#CKFINDER_CONNECTOR_ERROR_NONE}
	 * if no error occurred.
	 */
	protected abstract int getDataForXml() throws GenericEntityException;
}
