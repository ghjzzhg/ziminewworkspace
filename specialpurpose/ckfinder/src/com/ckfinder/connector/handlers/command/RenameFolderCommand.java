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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.PathUtils;

/**
 * Class to handle
 * <code>RenameFolder</code> command.
 */
public class RenameFolderCommand extends XMLCommand implements IPostCommand {

	private String newFolderName;
	private String newFolderPath;

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
		element.setAttribute("newPath", this.newFolderPath);
		element.setAttribute("newUrl", configuration.getTypes().get(this.type).getUrl() + this.newFolderPath);
		rootElement.appendChild(element);

	}

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

		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(this.type,
				this.currentFolder,
				this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_RENAME)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}

		if (configuration.forceASCII()) {
			this.newFolderName = FileUtils.convertToASCII(this.newFolderName);
		}

		if (FileUtils.checkIfDirIsHidden(this.newFolderName, configuration)
				|| !FileUtils.checkFolderName(this.newFolderName, configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME;
		}

		if (this.currentFolder.equals("/")) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}

		File dir = new File(disc + configuration.getTypes().get(this.type).getPath()
				+ this.currentFolder);
		try {
			if (!dir.isDirectory()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}
			setNewFolder();
			File newDir = new File(disc + configuration.getTypes().get(this.type).getPath()
					+ this.newFolderPath);
			if (newDir.exists()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST;
			}
			//修改有分享记录的文件夹
			List<GenericValue> roleList = EntityQuery.use(delegator).select().from("DataResourceRole").where(UtilMisc.toMap("dataResourceId", folderId)).queryList();
			if(UtilValidate.isNotEmpty(roleList)){
				String oldPath = roleList.get(0).get("sharedPath").toString();
				String newPath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + this.newFolderName;
				File file1 = new File(oldPath);
				File newFile1 = new File(newPath);
				if(newFile1.exists()){
					return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST;
				}
				file1.renameTo(newFile1);
				for(GenericValue role : roleList){
					role.put("sharedPath", newPath);
					role.store();
				}
			}
			//修改分享文件夹中的文件夹名
			String path1 = dir.getPath().replace("\\", "/");
			path1 = path1.substring(path1.indexOf(":") + 1,path1.length());
			String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
			String newPath1 = path1.substring(typePath.length()+1, path1.length()).replace("\\","/");//currentFolder
			String[] paths = newPath1.split("/");
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
				//shared路径
				String newTypePath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/");
				String newAllPath = newTypePath + "/" + newPath1;
				newAllPath = newAllPath.substring(0,newAllPath.length()-1);
				String newFilePath= newAllPath.substring(0,newAllPath.lastIndexOf("/"));
				List<GenericValue> genericValue = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName)).queryList();
				if(UtilValidate.isNotEmpty(genericValue)){
					String oldSharedPath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + newPath1;
					String newSharedPath = newFilePath + "/" + this.newFolderName;
					File file1 = new File(oldSharedPath);
					File newFile1 = new File(newSharedPath);
					if(!newFile1.exists()){
						file1.renameTo(newFile1);
					}
				}
				if(th.length() == newPath1.length()){
					break;
				}
				newPath1 = newPath1.substring(th.length()+1,newPath1.length());
			}
			GenericValue folderList = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", folderId)).distinct().queryOne();
			String oldPath = folderList.get("parentObjectInfo").toString() + folderList.get("dataResourceName")+"/";
			String newPath = folderList.get("parentObjectInfo").toString() + this.newFolderName + "/";
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.LIKE, oldPath+"%"));
			EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
			List<GenericValue> fileList = EntityQuery.use(delegator).select().from("DataResource").where(condition).queryList();
			for(GenericValue fileValue : fileList){
				String objectInfo = fileValue.get("parentObjectInfo").toString();
				String filePath = newPath + objectInfo.substring(oldPath.length(),objectInfo.length());
				fileValue.put("parentObjectInfo",filePath);
				fileValue.put("objectInfo",filePath + fileValue.get("dataResourceName"));
				GenericValue dataResource = delegator.makeValidValue("DataResource", fileValue);
				dataResource.store();
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dataResourceId", folderId);
			map.put("objectInfo",newPath);
			map.put("dataResourceName", this.newFolderName);
			GenericValue dataResource = delegator.makeValidValue("DataResource", map);
			dataResource.store();
			if (dir.renameTo(newDir)) {
				renameThumb();
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
		} catch (SecurityException e) {
			if (configuration.isDebugMode()) {
				throw e;
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return 1;
		}


		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	/**
	 * renames thumb folder.
	 */
	private void renameThumb() {
		File thumbDir = new File(configuration.getThumbsPath()
				+ File.separator
				+ this.type
				+ this.currentFolder);
		File newThumbDir = new File(configuration.getThumbsPath()
				+ File.separator
				+ this.type
				+ this.newFolderPath);
		thumbDir.renameTo(newThumbDir);

	}

	/**
	 * sets new folder name.
	 */
	private void setNewFolder() {
		String tmp1 = this.currentFolder.substring(0,
				this.currentFolder.lastIndexOf('/'));
		this.newFolderPath = tmp1.substring(0,
				tmp1.lastIndexOf('/') + 1).concat(this.newFolderName);
		this.newFolderPath = PathUtils.addSlashToEnd(this.newFolderPath);

	}

	/**
	 * @param request request
	 * @param configuration connector conf
	 * @param params execute additional params.
	 * @throws ConnectorException when error occurs.
	 */
	@Override
	public void initParams(final HttpServletRequest request,
			final IConfiguration configuration,
			final Object... params) throws ConnectorException, GenericEntityException {

		super.initParams(request, configuration);
		this.newFolderName = getParameter(request, "NewFolderName");
		super.folderId = getParameter(request, "folderId");
	}
}
