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
 * <code>CopyFiles</code> command.
 */
public class CopyFilesCommand extends XMLCommand implements IPostCommand {

	private List<FilePostParam> files;
	private int filesCopied;
	private int copiedAll;
	private boolean addCopyNode;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (creator.hasErrors()) {
			Element errorsNode = creator.getDocument().createElement("Errors");
			creator.addErrors(errorsNode);
			rootElement.appendChild(errorsNode);
		}


		if (addCopyNode) {
			createCopyFielsNode(rootElement);
		}

	}

	/**
	 * creates copy file XML node.
	 *
	 * @param rootElement XML root node.
	 */
	private void createCopyFielsNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("CopyFiles");
		element.setAttribute("copied", String.valueOf(this.filesCopied));
		element.setAttribute("copiedTotal", String.valueOf(this.copiedAll
				+ this.filesCopied));
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
			return copyFiles();
		} catch (Exception e) {
			this.exception = e;
		}
		//this code should never be reached
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNKNOWN;

	}

	/**
	 * copy files from request.
	 *
	 * @return error code
	 * @throws IOException when ioexception in debug mode occurs
	 */
	private int copyFiles() throws IOException, GenericEntityException {
		this.filesCopied = 0;
		this.addCopyNode = false;
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
			// check #4 (extension) - when moving to another resource type,
			//double check extension
			if (!this.type.equals(file.getType())) {
				if (FileUtils.checkFileExtension(file.getName(),
						this.configuration.getTypes().get(file.getType())) == 1) {
					creator.appendErrorNodeChild(
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION,
							file.getName(), file.getFolder(), file.getType());
					continue;


				}
			}
			if (FileUtils.checkIfDirIsHidden(file.getFolder(), this.configuration)) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			}

			if (FileUtils.checkIfFileIsHidden(file.getName(), this.configuration)) {
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
				} else if (destFile.exists()) {
					//目标文件或文件夹存在时的重名处理：自动改名or覆盖
					if (file.getOptions() != null && file.getOptions().indexOf("overwrite") != -1) {//覆盖
						Map map = handleOverwrite(sourceFile, destFile);
						if (!Boolean.parseBoolean(map.get("flag").toString())) {//覆盖失败
							creator.appendErrorNodeChild(
									Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
									file.getName(), file.getFolder(), file.getType());
							continue;
						} else {//覆盖成功
							this.filesCopied++;
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
						if (!Boolean.parseBoolean(map.get("flag").toString())) {
							creator.appendErrorNodeChild(
									Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
									file.getName(), file.getFolder(), file.getType());
							continue;
						} else {
							this.filesCopied++;
						}
						//保存到DataResource表
						reSaveDataResource(sourceFile, destFile, map);
						// 如果属于分享内容，自动添加到分享目录
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

					} else {
						creator.appendErrorNodeChild(
								Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST,
								file.getName(), file.getFolder(), file.getType());
						continue;
					}
				} else {
					String source = sourceFile.getParent().replace("\\","/")+"/";
					if(!source.equals(destFilePath)) {
						this.filesCopied++;
						Map<String,Object> fileMap = new HashMap<String,Object>();
						String sourcePath = sourceFile.getParent().replace("\\","/")+"/";
						fileMap.put("parentObjectInfo",sourcePath);
						fileMap.put("dataResourceName",sourceFile.getName());
						GenericValue fileValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(fileMap)).queryOne();
						if(null != fileValue){
							if((dataType + "_FOLDER").equals(fileValue.get("dataResourceTypeId"))){//复制文件夹
								copySaveFile(sourceFile,destFile,fileValue);
							}else{
								if (FileUtils.copyFromSourceToDestFile(sourceFile, destFile,
										false, configuration)) {//复制文件
									saveDataResource(destFile);
									copyThumb(file);
								}else{
									creator.appendErrorNodeChild(
											Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
											file.getName(), file.getFolder(), file.getType());
									continue;
								}
							}
							String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
							String paths = destFile.getPath();
							String anewPath = destFile.getPath().substring(typePath.length()+1, destFile.getPath().length()).replace("\\","/");
							String[] pathList = anewPath.split("/");
							String partPath = "/";

						}else{
							creator.appendErrorNodeChild(
									Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND,
									file.getName(), file.getFolder(), file.getType());
							continue;
						}
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
			}
		}
		this.addCopyNode = true;
		if (creator.hasErrors()) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_COPY_FAILED;
		} else {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
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
			String dataScope = delegator.getNextSeqId("TblDataScope");//获取主键ID
			Map<String,Object> dataScpoceMap = new HashMap<String,Object>();
			dataScpoceMap.put("scopeId",dataScope);
			dataScpoceMap.put("dataId",resourceId);
			dataScpoceMap.put("dataAttr","all");
			dataScpoceMap.put("scopeType","SCOPE_USER");
			dataScpoceMap.put("scopeValue",partyId);
			dataScpoceMap.put("entityName","DataResource");
			GenericValue executor = delegator.makeValidValue("TblDataScope", dataScpoceMap);
			executor.create();
		}
		String resourceId = delegator.getNextSeqId("DataResource");//获取主键ID
		String path = destFile.getParent().replace("\\","/")+"/";
		genericValue.put("parentObjectInfo",path);
		genericValue.put("dataResourceId", resourceId);
		genericValue.put("objectInfo",path+genericValue.get("dataResourceName"));
		genericValue.create();
		String dataScope = delegator.getNextSeqId("TblDataScope");//获取主键ID
		Map<String,Object> dataScpoceMap = new HashMap<String,Object>();
		dataScpoceMap.put("scopeId",dataScope);
		dataScpoceMap.put("dataId",resourceId);
		dataScpoceMap.put("dataAttr","all");
		dataScpoceMap.put("scopeType","SCOPE_USER");
		dataScpoceMap.put("scopeValue",partyId);
		dataScpoceMap.put("entityName","DataResource");
		GenericValue executor = delegator.makeValidValue("TblDataScope", dataScpoceMap);
		executor.create();
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
				copySaveFile(sourceFile,file1,fileValue);
			}else{
				saveDataResource(file1);
			}
		}

	}

	/**
	 * Handles autorename option.
	 *
	 * @param sourceFile source file to copy from.
	 * @param destFile destenation file to copy to.
	 * @return true if copied correctly
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
					map.put("flag",FileUtils.copyFromSourceToDestFile(sourceFile, newDestFile, false, configuration));
					map.put("newDestFile",newDestFile);
					map.put("dataResourceTypeId",dataType + "_FILE");
					return map;
				} else {
					counter++;
				}
			}

		}
	}

	/**
	 * copy thumb file.
	 *
	 * @param file file to copy.
	 * @throws IOException when ioerror occurs
	 */
	private void copyThumb(final FilePostParam file) throws IOException {
		File sourceThumbFile = new File(configuration.getThumbsPath() + File.separator
				+ file.getType()
				+ file.getFolder(), file.getName());
		File destThumbFile = new File(configuration.getThumbsPath() + File.separator
				+ this.type
				+ this.currentFolder, file.getName());

		if (sourceThumbFile.isFile() && sourceThumbFile.exists()) {
			FileUtils.copyFromSourceToDestFile(sourceThumbFile, destThumbFile,
					false, configuration);
		}

	}

	@Override
	public void initParams(final HttpServletRequest request,
						   final IConfiguration configuration,
						   final Object... params) throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration);
		this.files = new ArrayList<FilePostParam>();
		this.copiedAll = (request.getParameter("copied") != null) ? Integer.valueOf(request.getParameter("copied")) : 0;

		getFilesListFromRequest(request);

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
			file.setName(getParameter(request, paramName));
			String folder = getParameter(request, "files[" + i + "][folder]");
//			if(!"/".equals(folder)){
//				file.setFolder(folder.substring(0,folder.lastIndexOf("("))+"/");
//			}else{
			file.setFolder(folder);
//			}
			file.setOptions(getParameter(request, "files[" + i + "][options]"));
			file.setType(getParameter(request, "files[" + i + "][type]"));
			this.files.add(file);
			paramName = "files[" + (++i) + "][name]";
		}
	}
}
