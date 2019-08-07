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
import com.ckfinder.connector.data.FilePostParam;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import sun.security.util.Cache;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class used to handle
 * <code>DeleteFiles</code> command.
 */
public class DeleteFilesCommand extends XMLCommand implements IPostCommand {

	private List<FilePostParam> files;
	private int filesDeleted;
	private boolean addDeleteNode;

	@Override
	protected void createXMLChildNodes(int errorNum, Element rootElement) throws ConnectorException {
		if (creator.hasErrors()) {
			Element errorsNode = creator.getDocument().createElement("Errors");
			creator.addErrors(errorsNode);
			rootElement.appendChild(errorsNode);
		}

		if (this.addDeleteNode) {
			createDeleteFielsNode(rootElement);
		}
	}

	/**
	 * Adds delete file node in XML.
	 *
	 * @param rootElement - root element in XML response.
	 */
	private void createDeleteFielsNode(Element rootElement) {
		Element element = creator.getDocument().createElement("DeleteFiles");
		element.setAttribute("deleted", String.valueOf(this.filesDeleted));
		rootElement.appendChild(element);
	}

	/**
	 * Prepares data for XML response.
	 *
	 * @return error code or 0 if action ended with success.
	 */
	@Override
	protected int getDataForXml() {

		this.filesDeleted = 0;

		this.addDeleteNode = false;

		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}

		for (FilePostParam fileItem : this.files) {
			if (!FileUtils.checkFileName(fileItem.getName())) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (configuration.getTypes().get(fileItem.getType()) == null) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (fileItem.getFolder() == null || fileItem.getFolder().equals("")
					|| Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(
					fileItem.getFolder()).find()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (FileUtils.checkIfDirIsHidden(fileItem.getFolder(), this.configuration)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (FileUtils.checkIfFileIsHidden(fileItem.getName(), this.configuration)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (FileUtils.checkFileExtension(fileItem.getName(), this.configuration.getTypes().get(fileItem.getType())) == 1) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;

			}

			if (!AccessControlUtil.getInstance(this.configuration).checkFolderACL(fileItem.getType(), fileItem.getFolder(), this.userRole,
					AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_DELETE)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
			}

			File file = new File(disc + configuration.getTypes().get(fileItem.getType()).getPath() + fileItem.getFolder(), fileItem.getName());

			try {
				this.addDeleteNode = true;
				if (!file.exists()) {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND,
							fileItem.getName(), fileItem.getFolder(), fileItem.getType());
					continue;
				}

				if (FileUtils.delete(file)) {
					File thumbFile = new File(configuration.getThumbsPath()
							+ File.separator + fileItem.getType() + this.currentFolder, fileItem.getName());
					this.filesDeleted++;

					try {
						FileUtils.delete(thumbFile);
					} catch (Exception exp) {
						// No errors if we are not able to delete the thumb.
					}
				} else { //If access is denied, report error and try to delete rest of files.
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
							fileItem.getName(), fileItem.getFolder(), fileItem.getType());
				}
			} catch (SecurityException e) {
				if (configuration.isDebugMode()) {
					throw e;
				} else {
					return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
				}
			}
		}
		if (creator.hasErrors()) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_DELETE_FAILED;
		} else {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
		}
	}

	/**
	 * Initializes parameters for command handler.
	 *
	 * @param request - request object.
	 * @param configuration - connector configuration object.
	 * @param params - additional parameters.
	 * @throws ConnectorException when error occurs.
	 */
	@Override
	public void initParams(HttpServletRequest request,
			IConfiguration configuration,
			Object... params) throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration);
		this.files = new ArrayList<FilePostParam>();
		getFilesListFromRequest(request);
	}

	/**
	 * Gets list of files from request.
	 *
	 * @param request - request object.
	 */
	private void getFilesListFromRequest(HttpServletRequest request) throws GenericEntityException, ConnectorException {
		int i = 0;
		String paramName = "files[" + i + "][name]";
		while (request.getParameter(paramName) != null) {
			FilePostParam file = new FilePostParam();
			file.setName(getParameter(request, paramName));
			String folder = getParameter(request, "files[" + i + "][folder]");
//			if(!"/".equals(folder)){
//				file.setFolder(folder.substring(0,folder.lastIndexOf("("))+"/");
//			}else{
				file.setFolder(folder);
//			}
			file.setOptions(getParameter(request, "files[" + i + "][options]"));
			file.setType(getParameter(request, "files[" + i + "][type]"));
			String fileId = getParameter(request, "files[" + i + "][fileId]");
			GenericValue fileModel = FileService.searchFile(fileId, delegator);
			List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(UtilMisc.toMap("accessoryId",fileId)).queryList();
			if(UtilValidate.isNotEmpty(genericValueList)){
				throw new ConnectorException(Constants.Errors.CKFINDER_CONNECTOR_ERROR_DELETE_FILE);
			}
			String newFileName = fileModel.get("dataResourceName").toString();
			//删除有分享记录的文件
			List<GenericValue> roleList = EntityQuery.use(delegator).select().from("DataResourceRole").where(UtilMisc.toMap("dataResourceId", fileId)).queryList();
			if(UtilValidate.isNotEmpty(roleList)){
				for(GenericValue role : roleList){
					String filePath = role.get("sharedPath").toString();
					File files = new File(filePath);
					FileUtils.deleteFile(files);
				}
			}
			//删除分享文件夹中的文件
			String path1 = configuration.getTypes().get(this.type).getPath()
					+ this.currentFolder;
			String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
			String newPath = path1.substring(typePath.length()+1, path1.length()).replace("\\","/");
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
					String newTypePath = configuration.getTypes().get("本人分享").getPath() + "/" + newPath;
					deleteSharedFile(newTypePath + newFileName);
				}
				newPath = newPath.substring(th.length()+1,newPath.length());
			}
			delegator.removeByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", fileId));
			GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",fileId)).distinct().queryOne();
			if(null != genericValue && genericValue.get("dataResourceTypeId").equals(dataType + "_FOLDER")){
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				String path = genericValue.get("parentObjectInfo").toString() + genericValue.get("dataResourceName").toString() + "/";
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, path + "%"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				List<GenericValue> list = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
				for(GenericValue genericValue1 : list){
					delFile(genericValue1.get("dataResourceId").toString());
				}
			}
			delFile(fileId);
			this.files.add(file);
			paramName = "files[" + (++i) + "][name]";
		}
	}

	/**
	 * 删除单个文件
	 * @param   sPath 被删除目录的文件路径
	 */
	public void deleteSharedFile(String sPath) {
		File file = new File(sPath);
//		路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}
}
