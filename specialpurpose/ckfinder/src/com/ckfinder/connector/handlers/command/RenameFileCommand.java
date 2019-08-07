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

/**
 * Class to handle
 * <code>RenameFile</code> command.
 */
public class RenameFileCommand extends XMLCommand implements IPostCommand {

	private String fileName;
	private String newFileName;
	private boolean renamed;
	private boolean addRenameNode;

	@Override
	protected void createXMLChildNodes(final int errorNum,
			final Element rootElement) throws ConnectorException {
		if (this.addRenameNode) {
			createRenamedFileNode(rootElement);
		}


	}

	/**
	 * create rename file XML node.
	 *
	 * @param rootElement XML root node
	 */
	private void createRenamedFileNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("RenamedFile");
		element.setAttribute("name", this.fileName);
		if (renamed) {
			element.setAttribute("newName", this.newFileName);
		}
		rootElement.appendChild(element);
	}

	/**
	 * gets data for XML and checks all validation.
	 *
	 * @return error code or 0 if it's correct.
	 */
	@Override
	protected int getDataForXml() {

		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}

		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(
				this.type, this.currentFolder, this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_RENAME)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}

		if (configuration.forceASCII()) {
			this.newFileName = FileUtils.convertToASCII(this.newFileName);
		}

		if (this.fileName != null && !this.fileName.equals("")
				&& this.newFileName != null && !this.newFileName.equals("")) {
			this.addRenameNode = true;
		}

		int checkFileExt = FileUtils.checkFileExtension(this.newFileName,
				this.configuration.getTypes().get(this.type));
		if (checkFileExt == 1) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION;
		}
		if (configuration.ckeckDoubleFileExtensions()) {
			this.newFileName = FileUtils.renameFileWithBadExt(this.configuration.getTypes().get(this.type), this.newFileName);
		}

		if (!FileUtils.checkFileName(this.fileName)
				|| FileUtils.checkIfFileIsHidden(this.fileName,
				configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}

		if (!FileUtils.checkFileName(this.newFileName, configuration)
				|| FileUtils.checkIfFileIsHidden(this.newFileName,
				configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME;
		}

		if (FileUtils.checkFileExtension(this.fileName,
				this.configuration.getTypes().get(this.type)) == 1) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}

		String dirPath = configuration.getTypes().get(this.type).getPath()
				+ this.currentFolder;

		File file = new File(disc + dirPath, this.fileName);
		File newFile = new File(disc + dirPath, this.newFileName);
		File dir = new File(disc + dirPath);

		try {
			if (!file.exists()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND;
			}

			if (newFile.exists()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST;
			}else {
				//修改有分享记录的文件名
				List<GenericValue> roleList = EntityQuery.use(delegator).select().from("DataResourceRole").where(UtilMisc.toMap("dataResourceId", fileId)).queryList();
				if(UtilValidate.isNotEmpty(roleList)){
					String oldPath = roleList.get(0).get("sharedPath").toString();
					String newPath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + this.newFileName;
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
				//修改分享文件夹中的文件名
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
					List<GenericValue> genericValue1 = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName)).queryList();
					if(UtilValidate.isNotEmpty(genericValue1)){
						String newTypePath = configuration.getTypes().get("本人分享").getPath() + "/" + newPath;
						File file1 = new File(newTypePath + this.fileName);
						File newFile1 = new File(newTypePath + this.newFileName);
						file1.renameTo(newFile1);
					}
					newPath = newPath.substring(th.length()+1,newPath.length());
				}
				Map map = new HashMap();
				map.put("dataResourceId",fileId);
				map.put("objectInfo",dirPath + this.newFileName);
				map.put("dataResourceName",this.newFileName);
				GenericValue dataResource = delegator.makeValidValue("DataResource", map);
				dataResource.store();
			}

			if (!dir.canWrite() || !file.canWrite()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
			this.renamed = file.renameTo(newFile);
			if (this.renamed) {
				renameThumb();
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
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


	}

	/**
	 * rename thumb file.
	 */
	private void renameThumb() {
		File thumbFile = new File(configuration.getThumbsPath()
				+ File.separator + this.type + this.currentFolder,
				this.fileName);
		File newThumbFile = new File(configuration.getThumbsPath()
				+ File.separator + this.type + this.currentFolder,
				this.newFileName);
		thumbFile.renameTo(newThumbFile);

	}

	@Override
	public void initParams(final HttpServletRequest request,
			final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration);
		this.fileName = getParameter(request, "fileName");
		this.newFileName = getParameter(request, "newFileName");
		super.fileId = getParameter(request, "fileId");
		GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).distinct().queryOne();
		if(genericValue.get("dataResourceTypeId").equals(dataType + "_FOLDER")){
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			String path = genericValue.get("parentObjectInfo").toString() + genericValue.get("dataResourceName").toString() + "/";
			conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.LIKE, path + "%"));
			EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
			List<GenericValue> list = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
			for(GenericValue genericValue1 : list){
				String oldPath = genericValue1.get("parentObjectInfo").toString();
				String newPath = genericValue.get("parentObjectInfo").toString() + this.newFileName + "/" + oldPath.substring(path.length(),oldPath.length());
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("dataResourceId",genericValue1.get("dataResourceId"));
				map.put("parentObjectInfo",newPath);
				map.put("objectInfo",newPath + map.get("dataResourceName"));
				GenericValue dataResource = delegator.makeValidValue("DataResource", map);
				dataResource.store();
			}
		}
//		//修改有分享记录的文件名
//		List<GenericValue> roleList = EntityQuery.use(delegator).select().from("DataResourceRole").where(UtilMisc.toMap("dataResourceId", fileId)).queryList();
//		if(UtilValidate.isNotEmpty(roleList)){
//			for(GenericValue role : roleList){
//				String oldPath = role.get("sharedPath").toString();
//				String newPath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + this.newFileName;
//				File file = new File(oldPath);
//				File newFile = new File(newPath);
//				file.renameTo(newFile);
//				role.put("sharedPath", newPath);
//				role.store();
//			}
//		}
//		//修改分享文件夹中的文件名
//		String path1 = configuration.getTypes().get(this.type).getPath()
//				+ this.currentFolder;
//		String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
//		String newPath = path1.substring(typePath.length()+1, path1.length()).replace("\\","/");
//		String[] paths = newPath.split("/");
//		String partPath = "/";
//		for(String th : paths){
//			if(!"".equals(th) && !"/".equals(th)){
//				partPath += th+"/";
//			}else{
//				continue;
//			}
//			String allPath = typePath + partPath;
//			allPath = allPath.substring(0,allPath.length()-1);
//			String filePath= allPath.substring(0,allPath.lastIndexOf("/"));
//			String fileName = allPath.substring(allPath.lastIndexOf("/")+1,allPath.length());
//			List<GenericValue> genericValue1 = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("objectInfo", filePath+"/", "dataResourceName", fileName)).queryList();
//			if(UtilValidate.isNotEmpty(genericValue1)){
//				String newTypePath = configuration.getTypes().get("本人分享").getPath() + "/" + newPath;
////				deleteSharedFile(newTypePath + newFileName);
//			}
////			newPath = newPath.substring(th.length()+1,newPath.length());
//		}
	}
}
