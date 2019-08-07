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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.conversion.BooleanConverters;
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

	@Override
	protected int getDataForXml() {
		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}

		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(
				this.type,
				this.currentFolder,
				this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_RENAME
				| AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_DELETE
				| AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_UPLOAD)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}

		try {
			return moveFiles();
		} catch (Exception e) {
			this.exception = e;
		}
		//this code should never be reached
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNKNOWN;

	}

	/**
	 * move files.
	 *
	 * @return error code.
	 * @throws IOException when io error in debug mode occurs
	 */
	private int moveFiles() throws IOException {
		this.filesMoved = 0;
		this.addMoveNode = false;
		for (FilePostParam file : files) {

			if (!FileUtils.checkFileName(file.getName())) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}
			if (Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(
					file.getFolder()).find()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (configuration.getTypes().get(file.getType()) == null) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (file.getFolder() == null || file.getFolder().equals("")) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}
			if (FileUtils.checkFileExtension(file.getName(),
					this.configuration.getTypes().get(this.type)) == 1) {
				creator.appendErrorNodeChild(
						Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION,
						file.getName(), file.getFolder(), file.getType());
				continue;
			}

			if (!this.type.equals(file.getType())) {
				if (FileUtils.checkFileExtension(file.getName(),
						this.configuration.getTypes().get(file.getType())) == 1) {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION,
							file.getName(), file.getFolder(), file.getType());
					continue;
				}
			}

			if (FileUtils.checkIfFileIsHidden(file.getName(),
					this.configuration)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (FileUtils.checkIfDirIsHidden(file.getFolder(), this.configuration)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (!AccessControlUtil.getInstance(this.configuration).checkFolderACL(file.getType(), file.getFolder(), this.userRole,
					AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_VIEW)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;

			}
			File sourceFile = new File(configuration.getTypes().get(file.getType()).getPath()
					+ file.getFolder(), file.getName());
			String destFilePath = configuration.getTypes().get(this.type).getPath() + this.currentFolder;
			File destFile = new File(destFilePath, file.getName());

			File sourceThumb = new File(configuration.getThumbsPath() + File.separator + file.getType()
					+ file.getFolder() + file.getName());
			try {
				if (!sourceFile.exists() || (!sourceFile.isFile() && !sourceFile.isDirectory())) {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND,
							file.getName(), file.getFolder(), file.getType());
					continue;
				}
				if (!this.type.equals(file.getType())) {
					Long maxSize = configuration.getTypes().get(this.type).getMaxSize();
					if (maxSize != null && maxSize < sourceFile.length()) {
						creator.appendErrorNodeChild(
								Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG,
								file.getName(), file.getFolder(), file.getType());
						continue;
					}
				}
				if (sourceFile.equals(destFile)) {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_SOURCE_AND_TARGET_PATH_EQUAL,
							file.getName(), file.getFolder(), file.getType());
					continue;
				} else if (destFile.exists()) {//目标文件存在时:自动覆盖or重命名
					if (file.getOptions() != null && file.getOptions().indexOf("overwrite") != -1) {//自动覆盖
						Map map = handleOverwrite(sourceFile, destFile);
						if (!Boolean.parseBoolean(map.get("flag").toString())) {//覆盖失败
							creator.appendErrorNodeChild(
									Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
									file.getName(), file.getFolder(), file.getType());
							continue;
						} else {//覆盖成功
							this.filesMoved++;
							FileUtils.delete(sourceThumb);
						}
						//保存到DataResource表
						reSaveDataResource(sourceFile, destFile, map);
						//保存到分享目录
						String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
						String paths = destFile.getPath();
						String anewPath = destFile.getPath().substring(typePath.length()+1, destFile.getPath().length()).replace("\\","/");
						String[] pathList = anewPath.split("/");
						String partPath = "/";
						for(String th : pathList){
							if(!"".equals(th) && !"/".equals(th) && !th.contains(".")){
								partPath += th+"/";
							}else{
								continue;
							}
							String allPath = typePath + partPath;
							allPath = allPath.substring(0,allPath.length()-1);
							String filePath= allPath.substring(0,allPath.lastIndexOf("/"));
							String fileName = allPath.substring(allPath.lastIndexOf("/")+1,allPath.length());
							List<GenericValue> shareList = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName)).queryList();
							if(UtilValidate.isNotEmpty(shareList)){
								String newTypePath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + anewPath;
								if((dataType + "_FILE").equals(map.get("dataResourceTypeId"))){
									FileService.copyFile(paths, newTypePath);
								}else{
									FileService.copyFolder(paths, newTypePath);
								}
							}
							if(th.length() == anewPath.length()){
								break;
							}
							anewPath = anewPath.substring(th.length()+1,anewPath.length());
						}
					} else if (file.getOptions() != null && file.getOptions().indexOf("autorename") != -1) {//自动重命名
						Map map = handleAutoRename(sourceFile, destFile);
						if (!(Boolean.parseBoolean(map.get("flag").toString()))) {
							creator.appendErrorNodeChild(
									Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
									file.getName(), file.getFolder(),
									file.getType());
							continue;
						} else {
							this.filesMoved++;
							FileUtils.delete(sourceThumb);
						}
						//保存到DataResource表
						reSaveDataResource(sourceFile, destFile, map);
						// 如果destFile属于分享内容，自动添加到分享目录
						File newDestFile = (File)map.get("newDestFile");
						String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
						String paths = newDestFile.getPath();
						String anewPath = newDestFile.getPath().substring(typePath.length()+1, newDestFile.getPath().length()).replace("\\","/");
						String[] pathList = anewPath.split("/");
						String partPath = "/";
						for(String th : pathList){
							if(!"".equals(th) && !"/".equals(th) && !th.contains(".")){
								partPath += th+"/";
							}else{
								continue;
							}
							String allPath = typePath + partPath;
							allPath = allPath.substring(0,allPath.length()-1);
							String filePath= allPath.substring(0,allPath.lastIndexOf("/"));
							String fileName = allPath.substring(allPath.lastIndexOf("/")+1,allPath.length());
							List<GenericValue> shareList = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName)).queryList();
							if(UtilValidate.isNotEmpty(shareList)){
								String newTypePath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + anewPath;
								if((dataType + "_FILE").equals(map.get("dataResourceTypeId"))){
									FileService.copyFile(paths, newTypePath);
								}else{
									FileService.copyFolder(paths, newTypePath);
								}
							}
							if(th.length() == anewPath.length()){
								break;
							}
							anewPath = anewPath.substring(th.length()+1,anewPath.length());
						}
						//todo 删除原文件内容
					} else {
						creator.appendErrorNodeChild(
								Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST,
								file.getName(), file.getFolder(), file.getType());
						continue;
					}
				} else {//目标文件不存在时
					String source = sourceFile.getPath().replace("\\","/")+"/";
					if(!source.equals(destFilePath)) {
						this.filesMoved++;
						Map<String,Object> fileMap = new HashMap<String,Object>();
						String sourcePath = sourceFile.getParent().replace("\\","/")+"/";
						fileMap.put("parentObjectInfo",sourcePath);
						String fileName = sourceFile.getName();
						if(filesMoved > 1){
							fileName = fileName.substring(0,fileName.lastIndexOf(".")) + "(" + filesMoved + ")" + fileName.substring(fileName.lastIndexOf("."),fileName.length());
						}
						fileMap.put("dataResourceName",fileName);
						GenericValue fileValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(fileMap)).queryOne();
						if((dataType + "_FOLDER").equals(fileValue.get("dataResourceTypeId"))){
							moveDataResource(sourceFile,destFile,fileValue);
							moveFolder(sourceFile.getPath(),destFile.getPath());
							//删除分享文件夹中的文件夹
							String path1 = sourceFile.getPath().replace("\\", "/");
							String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
							String newPath = path1.substring(typePath.length()+1, path1.length()).replace("\\","/");//currentFolder
							String[] paths = newPath.split("/");
							String partPath = "/";
							searchSubPath(paths,partPath,typePath,newPath);
						}else{
							if (FileUtils.copyFromSourceToDestFile(sourceFile, destFile,
							true, configuration)) {
								//删除分享文件夹中的文件
								String path1 = sourceFile.getPath().replace("\\", "/");
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
									String fileName3 = allPath.substring(allPath.lastIndexOf("/")+1,allPath.length());
									List<GenericValue> genericValue = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName3)).queryList();
									if(th.length() == newPath.length()){
										break;
									}
									if(UtilValidate.isNotEmpty(genericValue)){
										String newTypePath = configuration.getTypes().get("本人分享").getPath() + "/" + newPath;
										DeleteFolderCommand.deleteSharedFile(newTypePath);
									}
									newPath = newPath.substring(th.length()+1,newPath.length());
								}
								saveDataResource(sourceFile,destFile,false);
								moveThumb(file);
							}
						}
						//自动保存到新的分享目录
						String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
						String paths = destFile.getPath();
						String anewPath = destFile.getPath().substring(typePath.length()+1, destFile.getPath().length()).replace("\\","/");
						String[] pathList = anewPath.split("/");
						String partPath = "/";
						for(String th : pathList){
							if(!"".equals(th) && !"/".equals(th) && !th.contains(".")){
								partPath += th+"/";
							}else{
								continue;
							}
							String allPath = typePath + partPath;
							allPath = allPath.substring(0,allPath.length()-1);
							String filePath= allPath.substring(0,allPath.lastIndexOf("/"));
							String fileName1 = allPath.substring(allPath.lastIndexOf("/")+1,allPath.length());
							List<GenericValue> shareList = EntityQuery.use(delegator).select().from("ResourceRoleShareList").where(UtilMisc.toMap("parentObjectInfo", filePath+"/", "dataResourceName", fileName1)).queryList();
							if(UtilValidate.isNotEmpty(shareList)){
								String newTypePath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + anewPath;
								if((dataType + "_FILE").equals(fileValue.get("dataResourceTypeId"))){
									FileService.copyFile(paths, newTypePath);
								}else{
									FileService.copyFolder(paths, newTypePath);
								}
							}
							if(th.length() == anewPath.length()){
								break;
							}
							anewPath = anewPath.substring(th.length()+1,anewPath.length());
						}
					}else{
						creator.appendErrorNodeChild(
								Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
								file.getName(), file.getFolder(), file.getType());
						continue;
					}
				}
			} catch (SecurityException e) {
				if (configuration.isDebugMode()) {
					throw e;
				} else {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
							file.getName(), file.getFolder(), file.getType());
					continue;
				}
			} catch (IOException e) {
				if (configuration.isDebugMode()) {
					throw e;
				} else {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
							file.getName(), file.getFolder(), file.getType());
					continue;
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}

		}
		this.addMoveNode = true;
		if (creator.hasErrors()) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_MOVE_FAILED;
		} else {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
		}
	}

	public void moveDataResource(File sourceFile,File destFile,GenericValue genericValue) throws GenericEntityException {
		String sourceParentPath = sourceFile.getParent().replace("\\","/")+"/";
		String sourcePath = sourceFile.getPath().replace("\\","/")+"/";
		List conditionList = new ArrayList();
		conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.LIKE, sourceParentPath + sourceFile.getName() + "/" +"%"));
		EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
		List<GenericValue> fileList = EntityQuery.use(delegator).select().from("DataResource").where(condition).queryList();
		String newPath = destFile.getPath().replace("\\","/")+"/";
		for(GenericValue fileValue : fileList){
			String objectInfo = fileValue.get("parentObjectInfo").toString();
			String filePath = newPath + objectInfo.substring(sourcePath.length(),objectInfo.length());
			fileValue.put("dataResourceId",fileValue.get("dataResourceId"));
			fileValue.put("parentObjectInfo",filePath);
			fileValue.put("objectInfo",filePath+fileValue.get("DataResourceName"));
			GenericValue dataResource = delegator.makeValidValue("DataResource", fileValue);
			dataResource.store();
			saveFile(fileValue,partyId);
		}
		String path = destFile.getParent().replace("\\","/")+"/";
		genericValue.put("parentObjectInfo",path);
		genericValue.put("objectInfo",path+genericValue.get("dataResourceName"));
		genericValue.store();
		saveFile(genericValue,partyId);

	}

	public void saveFile(GenericValue genericValue,String partyId){
		try {
			List<GenericValue>  genericValue1 = EntityQuery.use(delegator).select().from("TblDataScope").where(UtilMisc.toMap("dataId",genericValue.get("dataResourceId"),"scopeType","SCOPE_USER","scopeValue",partyId,"scopeType","SCOPE_USER","entityName","DataResource")).queryList();
			if(UtilValidate.isNotEmpty(genericValue1) && genericValue1.size() > 0){
				String dataScope = delegator.getNextSeqId("TblDataScope");//获取主键ID
				Map<String,Object> dataScpoceMap = new HashMap<String,Object>();
				dataScpoceMap.put("scopeId",dataScope);
				dataScpoceMap.put("dataId",genericValue.get("dataResourceId"));
				dataScpoceMap.put("dataAttr","all");
				dataScpoceMap.put("scopeType","SCOPE_USER");
				dataScpoceMap.put("scopeValue",partyId);
				dataScpoceMap.put("entityName","DataResource");
				GenericValue executor = delegator.makeValidValue("TblDataScope", dataScpoceMap);
				executor.create();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	public void saveDataResource(File sourceFile,File destFile,Boolean flag) throws GenericEntityException {
		Map<String,Object> fileMap = new HashMap<String,Object>();
		String sourcePath = sourceFile.getParent().replace("\\","/")+"/";
		fileMap.put("parentObjectInfo",sourcePath);
		fileMap.put("dataResourceName",sourceFile.getName());
		GenericValue fileValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(fileMap)).queryOne();
		if(null != fileValue && fileValue.size() > 0){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("dataResourceTypeId",fileValue.get("dataResourceTypeId"));
			map.put("partyId",partyId);
			String newFolder = destFile.getParent().replace("\\","/")+"/" ;
			map.put("parentObjectInfo",newFolder);
			map.put("dataResourceName",destFile.getName());
			map.put("objectInfo",newFolder + destFile.getName());
			String id;
			if(flag) {
				id = delegator.getNextSeqId("DataResource");
				map.put("dataResourceId",id);
			}else{
				id = fileValue.get("dataResourceId").toString();
				map.put("dataResourceId",id);
			}
			GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
			if(flag) {
				dataResoure.create();
			}else{
				dataResoure.store();
			}
			String dataScope = delegator.getNextSeqId("TblDataScope");//获取主键ID
			Map<String,Object> dataScpoceMap = new HashMap<String,Object>();
			dataScpoceMap.put("scopeId",dataScope);
			dataScpoceMap.put("dataId",id);
			dataScpoceMap.put("dataAttr","all");
			dataScpoceMap.put("scopeType","SCOPE_USER");
			dataScpoceMap.put("scopeValue",partyId);
			dataScpoceMap.put("entityName","DataResource");
			GenericValue executor = delegator.makeValidValue("TblDataScope", dataScpoceMap);
			executor.create();
		}
	}

	/**
	 * Handles autorename option.
	 *
	 * @param sourceFile source file to move from.
	 * @param destFile destenation file to move to.
	 * @return true if moved correctly
	 * @throws IOException when ioerror occurs
	 */
	private Map handleAutoRename(final File sourceFile, final File destFile)
			throws IOException, GenericEntityException {
		int counter = 1;
		File newDestFile;
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> fileMap = new HashMap<String,Object>();
		String sourcePath = sourceFile.getParent().replace("\\","/")+"/";
		fileMap.put("parentObjectInfo",sourcePath);
		fileMap.put("dataResourceName",sourceFile.getName());
		GenericValue fileValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(fileMap)).queryOne();
		while (true){
			if((dataType + "_FOLDER").equals(fileValue.get("dataResourceTypeId"))) {//文件夹重命名
				String newFileName = destFile.getName() + "(" + counter + ")";
				newDestFile = new File(destFile.getParent() , newFileName);
				if (!newDestFile.exists()) {
					String oldPath = sourceFile.getPath();
					String newPath = newDestFile.getPath();
					map.put("flag",FileService.copyFolder(oldPath, newPath));
					map.put("newDestFile",newDestFile);
					map.put("dataResourceTypeId",dataType + "_FOLDER");
					return map;
				} else {
					counter++;
				}
			}else {
				//文件重命名
				String newFileName = FileUtils.getFileNameWithoutExtension(destFile.getName(), false)
						+ "(" + counter + ")."
						+ FileUtils.getFileExtension(destFile.getName(), false);
				newDestFile = new File(destFile.getParent(), newFileName);
				if (!newDestFile.exists()) {
					// can't be in one if=, becouse when error in
					// copy file occurs then it will be infinity loop
					map.put("flag",FileUtils.copyFromSourceToDestFile(sourceFile, newDestFile, true, configuration));
					map.put("newDestFile",newDestFile);
					map.put("dataResourceTypeId",dataType + "_FILE");
					return map;
				} else {
					counter++;
				}
			}

		}
	}


	private void reSaveDataResource(File sourceFile, File destFile, Map map) throws GenericEntityException {
		File file1 = (File)map.get("newDestFile");
		String sourcePath = sourceFile.getParent().replace("\\","/")+"/";
		Map<String,Object> fileMap = new HashMap<String,Object>();
		fileMap.put("parentObjectInfo",sourcePath);
		fileMap.put("dataResourceName",destFile.getName());
		GenericValue fileValue = EntityQuery.use(delegator).select()
				.from("DataResource")
				.where(EntityCondition.makeCondition(fileMap))
				.queryOne();
		if(UtilValidate.isNotEmpty(fileValue)) {
			if((dataType + "_FOLDER").equals(fileValue.get("dataResourceTypeId"))){
				fileValue.put("dataResourceName", file1.getName());
				moveDataResource(sourceFile, file1, fileValue);
				delFolder(sourceFile.getPath());
				//删除分享文件夹中的文件夹
				String path1 = sourceFile.getPath().replace("\\", "/");
				delShareFolder(path1);
			}else{
				//删除分享文件夹中的文件
				String path1 = sourceFile.getPath().replace("\\", "/");
				delShareFolder(path1);
				saveDataResource(sourceFile,file1,false);
			}
		}

	}

	public void copySaveFile(File sourceFile,File destFile,GenericValue genericValue) throws GenericEntityException {
		FileUtils.copyFolder(sourceFile.getPath(), destFile.getPath());
		String sourceParentPath = sourceFile.getParent().replace("\\","/")+"/";
		String sourcePath = sourceFile.getPath().replace("\\","/")+"/";
		List conditionList = new ArrayList();
		conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.LIKE, sourceParentPath + sourceFile.getName() + "/" +"%"));
		EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
		List<GenericValue> fileList = EntityQuery.use(delegator).select().from("DataResource").where(condition).queryList();
		String newPath = destFile.getPath().replace("\\","/")+"/";
		for(GenericValue fileValue : fileList){
			String resourceId = delegator.getNextSeqId("DataResource");//获取主键ID
			String objectInfo = fileValue.get("parentObjectInfo").toString();
			fileValue.put("dataResourceId",resourceId);
			String filePath = newPath + objectInfo.substring(sourcePath.length(),objectInfo.length());
			fileValue.put("parentObjectInfo",filePath);
			fileValue.put("objectInfo",filePath+fileValue.get("dataResourceName"));
			GenericValue dataResource = delegator.makeValidValue("DataResource", fileValue);
			dataResource.create();
		}
		String resourceId = delegator.getNextSeqId("DataResource");//获取主键ID
		String path = destFile.getParent().replace("\\","/")+"/";
		genericValue.put("parentObjectInfo",path);
		genericValue.put("objectInfo",path+genericValue.get("dataResourceName"));
		genericValue.put("dataResourceId", resourceId);
		genericValue.create();
	}

	/**
	 * move thumb file.
	 *
	 * @param file file to move.
	 * @throws IOException when ioerror occurs
	 */
	private void moveThumb(final FilePostParam file) throws IOException {
		File sourceThumbFile = new File(configuration.getThumbsPath()
				+ File.separator + file.getType()
				+ file.getFolder() + file.getName());
		File destThumbFile = new File(configuration.getThumbsPath()
				+ File.separator + this.type
				+ this.currentFolder
				+ file.getName());

		FileUtils.copyFromSourceToDestFile(sourceThumbFile, destThumbFile,
				true, configuration);

	}

	private void moveFolder(String oldPath,String  newPath){
		FileUtils.copyFolder(oldPath,  newPath);
		delFolder(oldPath);
	}

	/**
	 *  删除文件夹
	 *  @return  boolean
	 */
	public  void  delFolder(String  folderPath)  {
		try  {
			delAllFile(folderPath);  //删除完里面所有内容
			String  filePath  =  folderPath;
			filePath  =  filePath.toString();
			java.io.File  myFilePath  =  new  java.io.File(filePath);
			myFilePath.delete();  //删除空文件夹

		}
		catch  (Exception  e)  {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}

	}

	/**
	 *  删除文件夹里面的所有文件
	 *  @param  path  String  文件夹路径  如  c:/fqf
	 */
	public  void  delAllFile(String  path)  {
		File  file  =  new  File(path);
		if  (!file.exists())  {
			return;
		}
		if  (!file.isDirectory())  {
			return;
		}
		String[]  tempList  =  file.list();
		File  temp  =  null;
		for  (int  i  =  0;  i  <  tempList.length;  i++)  {
			if  (path.endsWith(File.separator))  {
				temp  =  new  File(path  +  tempList[i]);
			}
			else  {
				temp  =  new  File(path  +  File.separator  +  tempList[i]);
			}
			if  (temp.isFile())  {
				temp.delete();
			}
			if  (temp.isDirectory())  {
				delAllFile(path+"/"+  tempList[i]);//先删除文件夹里面的文件
				delFolder(path+"/"+  tempList[i]);//再删除空文件夹
			}
		}
	}

	@Override
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
				file.setName(getParameter(request, paramName));
				String folder = getParameter(request, "files[" + i + "][folder]");
//				if(!"/".equals(folder)){
//					file.setFolder(folder.substring(0,folder.lastIndexOf("("))+"/");
//				}else{
					file.setFolder(folder);
//				}
				file.setOptions(getParameter(request, "files[" + i
						+ "][options]"));
				file.setType(getParameter(request, "files[" + i + "][type]"));
				files.add(file);
			} else {
				break;
			}
			i++;
		}
	}
}
