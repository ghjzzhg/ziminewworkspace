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

import com.ckfinder.connector.ServletContextFactory;
import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.PathUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

/**
 * Base class for all command handlers.
 */
public abstract class Command {

	/**
	 * exception caught in fileUpload in debug mode should be thrown to servlet
	 * response.
	 */
	protected Exception exception;
	/**
	 * connector configuration.
	 */
	protected IConfiguration configuration;
	protected String userRole;
	protected String currentFolder;
	protected String type;
	protected Delegator delegator;
	protected LocalDispatcher dispatcher;
	protected String partyId;
	protected String folderId;
	protected String fileId;
	protected String statusId;
	protected GenericValue userLogin;
	protected String disc;
	protected String dataType;

	/**
	 * standard constructor.
	 */
	public Command() {
		configuration = null;
		userRole = null;
		currentFolder = null;
		type = null;
	}

	/**
	 * Runs command. Initialize, sets response and execute command.
	 *运行命令。初始化,设置响应和执行命令。
	 * @param request request
	 * @param response response
	 * @param configuration1 connector configuration
	 * @param params additional execute parameters.
	 * @throws ConnectorException when error occurred.
	 */
	public void runCommand(final HttpServletRequest request,
						   final HttpServletResponse response,
						   final IConfiguration configuration1,
						   final Object... params) throws ConnectorException, GenericEntityException {
		this.initParams(request, configuration1, params);
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) session.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		this.userLogin = userLogin;
		this.partyId = userLogin.getString("partyId");
		try {
			setResponseHeader(response, ServletContextFactory.getServletContext());
			execute(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (ConnectorException e) {
			throw e;
		} catch (IOException e) {
			throw new ConnectorException(
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		} catch (Exception e) {
			throw new ConnectorException(
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		}
	}

	/**
	 * initialize params for command handler.
	 *
	 * @param request request
	 * @param configuration connector configuration
	 * @param params execute additional params.
	 * @throws ConnectorException to handle in error handler.
	 */
	public void initParams(final HttpServletRequest request,
						   final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		disc = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourcePath", "localhost", delegator);
		dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourceType", "localhost", delegator);
		delegator = (Delegator) request.getAttribute("delegator");
		dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		if(UtilValidate.isNotEmpty(request.getParameter("targetId")) && !"undefined".equals(request.getParameter("targetId"))){
			this.folderId = request.getParameter("targetId");
		}else{
			this.folderId = "";
		}
		this.fileId = request.getParameter("fileId");
		String status = request.getParameter("statusId");
		if(null != status && !"".equals(status) && ("all".equals(status) || "view".equals(status))){
			this.statusId = status;
		}else{
			this.statusId = "all";
		}
		if (configuration != null) {
			this.configuration = configuration;
			this.userRole = (String) request.getSession().getAttribute(
					configuration.getUserRoleName());
			getCurrentFolderParam(request);
			if (checkConnector(request) && checkParam(this.currentFolder)) {
				this.currentFolder = PathUtils.escape(this.currentFolder);
				if (!checkHidden()) {
					if ((this.currentFolder == null || this.currentFolder.equals(""))
							|| checkIfCurrFolderExists(request)) {
						this.type = getParameter(request, "type");
					}
				}

			}
		}


	}

	/**
	 * check if connector is enabled and checks authentication.
	 *
	 * @param request current request.
	 * @return true if connector is enabled and user is authenticated
	 * @throws ConnectorException when connector is disabled
	 */
	protected boolean checkConnector(final HttpServletRequest request)
			throws ConnectorException {
		if (!configuration.enabled() || !configuration.checkAuthentication(request)) {
			throw new ConnectorException(
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_CONNECTOR_DISABLED, false);
		}
		return true;
	}

	/**
	 * Checks if current folder exists.
	 *
	 * @param request current request object
	 * @return {@code true} if current folder exists
	 * @throws ConnectorException if current folder doesn't exist
	 */
	protected boolean checkIfCurrFolderExists(final HttpServletRequest request)
			throws ConnectorException {
		String tmpType = getParameter(request, "type");
		String folderId = getParameter(request, "targetId");
		if ( tmpType != null  && folderId != null && !"undefined".equals(folderId)) {
			if( checkIfTypeExists( tmpType )) {
				String newPath = "";
				if(!"/".equals(this.currentFolder) && ("他人分享".equals(tmpType) || "本人分享".equals(tmpType))  && null != folderId){
					GenericValue data = new GenericValue();
					try {
						data = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", folderId)).queryOne();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					String path = data.get("parentObjectInfo").toString() + data.get("dataResourceName").toString();
					newPath = path.replace("\\","/");
				}else{
					newPath = configuration.getTypes().get(tmpType).getPath()
							+ this.currentFolder;
				}
				File currDir = new File(newPath);
				if (!currDir.exists() || !currDir.isDirectory()) {
					throw new ConnectorException(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND,
							false);
				} else {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Checks if type of resource provided as parameter exists.
	 *
	 * @return {@code true} if provided type exists, {@code false} otherwise	 
	 */
	protected boolean checkIfTypeExists( final String type ) {
		ResourceType testType = configuration.getTypes().get( type );
		if( testType == null )
			return false;
		return true;
	}

	/**
	 * checks if current folder is hidden.
	 *
	 * @return false if isn't.
	 * @throws ConnectorException when is hidden
	 */
	protected boolean checkHidden() throws ConnectorException {
		if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
			throw new ConnectorException(
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST,
					false);
		}
		return false;
	}

	/**
	 * executes command and writes to response.
	 *
	 * @param out response output stream
	 * @throws ConnectorException when error occurs
	 */
	public abstract void execute(final OutputStream out)
			throws ConnectorException;

	/**
	 * sets header in response.
	 *
	 * @param response servlet response
	 * @param sc sevletcontext
	 */
	public abstract void setResponseHeader(final HttpServletResponse response,
										   final ServletContext sc);

	/**
	 * check request for security issue.
	 *
	 * @param reqParam request param
	 * @return true if validation passed
	 * @throws ConnectorException if valdation error occurs.
	 */
	protected boolean checkParam(final String reqParam)
			throws ConnectorException {
		if (reqParam == null || reqParam.equals("")) {
			return true;
		}
		if (Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(reqParam).find()) {
			throw new ConnectorException(
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME,
					false);
		}

		return true;
	}

	/**
	 * Gets request param value with correct encoding.
	 *
	 * @param request reqeust
	 * @param paramName request param name
	 * @return param value
	 */
	protected String getParameter(final HttpServletRequest request,
								  final String paramName) {
		if (request.getParameter(paramName) == null) {
			return null;
		}
		return FileUtils.convertFromUriEncoding(
				request.getParameter(paramName), configuration);
	}

	/**
	 * gets current folder request param or sets default value if it's not set.
	 *
	 * @param request request
	 */
	protected void getCurrentFolderParam(final HttpServletRequest request) {
		String currFolder = getParameter(request, "currentFolder");
//		currFolder = currFolder.substring(1,currFolder.length());
//		String[] folders = currFolder.split("/");
//		currFolder = "";
//		if(null != folders){
//			for(String folder : folders){
//				if(null != folder && folder.indexOf("(") >= 0){
//					folder = folder.substring(0,folder.lastIndexOf("("));
//					currFolder += folder + "/";
//				}
//			}
//		}
//		currFolder = "/" + currFolder;
		if (currFolder == null || currFolder.equals("")) {
			this.currentFolder = "/";
		} else {
			this.currentFolder = PathUtils.addSlashToBeginning(PathUtils.addSlashToEnd(currFolder));
		}
	}
}
