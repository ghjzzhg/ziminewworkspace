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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;

/**
 * Class to handle
 * <code>CreateFolder</code> command.
 */
public class CreateFolderCommand extends XMLCommand implements IPostCommand {

	/**
	 * new folder name request param.
	 */
	private String newFolderName;

	private String partyId;

	private String folderId;

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
		rootElement.appendChild(element);

	}

	/**
	 * gets data for xml. Not used in this handler.
	 *
	 * @return always 0
	 */
	@Override
	protected int getDataForXml() {

		try {
			checkParam(newFolderName);
		} catch (ConnectorException e) {
			return e.getErrorCode();
		}

		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}

		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(
				this.type, this.currentFolder, this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_CREATE)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}

		if (configuration.forceASCII()) {
			this.newFolderName = FileUtils.convertToASCII(this.newFolderName);
		}

		if (!FileUtils.checkFolderName(this.newFolderName, configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME;
		}
		if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}
		if (FileUtils.checkIfDirIsHidden(newFolderName, configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME;
		}

		try {
			if (createFolder()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
			}

		} catch (SecurityException e) {
			if (configuration.isDebugMode()) {
				throw e;
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
		} catch (ConnectorException e) {
			return e.getErrorCode();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * creates folder. throws Exception when security problem occurs or folder
	 * already exists
	 *
	 * @return true if folder is created correctly
	 * @throws ConnectorException when error occurs or dir exists
	 */
	private boolean createFolder() throws ConnectorException, GenericEntityException {
		File dir = new File(disc + configuration.getTypes().get(this.type).getPath()
				+ currentFolder + newFolderName);
		if (dir.exists()) {
			throw new ConnectorException(Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST);
		} else {
			String path = dir.getPath();
			boolean flag = dir.mkdir();
			String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
			String newPath = path.substring(typePath.length()+1, path.length()).replace("\\","/");
			String[] paths = newPath.split("/");
			String partPath = "/";
			for(String th : paths){
				if(!"".equals(th) && !"/".equals(th)){
					partPath += th+"/";
				}else{
					continue;
				}
				String allPath = typePath + partPath;
				allPath = allPath.substring(0,allPath.length()-1);
				String filePath= allPath.substring(0,allPath.lastIndexOf("/"));
				String fileName = allPath.substring(allPath.lastIndexOf("/")+1,allPath.length());
				List<GenericValue> genericValue = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName)).queryList();
				if(UtilValidate.isNotEmpty(genericValue)){
					String newTypePath = configuration.getTypes().get("本人分享").getPath()+ "/" +newPath;
					File file = new File(disc + newTypePath);
					file.mkdirs();
				}
				if(th.length() == newPath.length()){
					break;
				}
				newPath = newPath.substring(th.length()+1,newPath.length());
			}
			saveDataResource(configuration.getTypes().get(this.type).getPath()
					+ currentFolder + newFolderName);
			return flag;
		}
	}

	public void saveDataResource(String path) throws GenericEntityException {
		Map<String,Object> map = new HashMap<String,Object>();
		folderId = delegator.getNextSeqId("DataResource").toString();//获取主键ID
		map.put("dataResourceId",folderId);
		map.put("dataResourceTypeId",dataType + "_FOLDER");
		String newPath = path.substring(0, path.lastIndexOf("/") + 1);
		newPath = newPath.replace("\\","/");
		map.put("parentObjectInfo",newPath);
		map.put("objectInfo",newPath+newFolderName+"/");
		map.put("partyId",partyId);
		map.put("dataResourceName",newFolderName);
		GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
		dataResoure.create();
		String dataScope = delegator.getNextSeqId("TblDataScope").toString();//获取主键ID
		Map<String,Object> dataScpoceMap = new HashMap<String,Object>();
		dataScpoceMap.put("scopeId",dataScope);
		dataScpoceMap.put("dataId",folderId);
		dataScpoceMap.put("dataAttr","all");
		dataScpoceMap.put("scopeType","SCOPE_USER");
		dataScpoceMap.put("scopeValue",partyId);
		dataScpoceMap.put("entityName","DataResource");
		GenericValue executor = delegator.makeValidValue("TblDataScope", dataScpoceMap);
		executor.create();
	}

	@Override
	public void initParams(final HttpServletRequest request,
						   final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration, params);
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) session.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		this.partyId = userLogin.getString("partyId");
		this.newFolderName = getParameter(request, "NewFolderName");
	}
}
