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

import com.ckfinder.connector.ServletContextFactory;
import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.errors.ConnectorException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.FileManagerFactory;
import org.ofbiz.content.data.FileTypeManager;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import sun.security.util.Cache;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	protected String userRole;
	protected Delegator delegator;
	protected LocalDispatcher dispatcher;
	protected String partyId;//文件、文件夹所属人partyId
	protected String loginUserPartyId;
	protected String folderId;
	protected String fileId;
	protected GenericValue userLogin;
	protected GenericValue groupInfo;
	protected String dataType;
	protected HttpServletRequest request;
	protected String disc;
	protected String otherModules;
	protected String moduleType;
	protected String moduleId;
	//第一位：分享；第二位：重命名；第三位：删除；第四位：新增文件夹；第五位：新增文件，第六位：下载。
	protected static final String FOLDERPMS = "000001";
	protected static final String FILEPMS = "111111";
	protected ConnectorException e;
	protected FileTypeManager fileManager;
	protected boolean isCompany;

	/**
	 * standard constructor.
	 */
	public Command() {
		userRole = null;
	}

	/**
	 * Runs command. Initialize, sets response and execute command.
	 *运行命令。初始化,设置响应和执行命令。
	 * @param request request
	 * @param response response
	 * @param params additional execute parameters.
	 * @throws ConnectorException when error occurred.
	 */
	public void runCommand(final HttpServletRequest request,
						   final HttpServletResponse response,
						   final Object... params) throws ConnectorException, GenericEntityException {
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		this.userLogin = userLogin;
		this.loginUserPartyId = userLogin.getString("partyId");
		this.initParams(request,params);
		if(params.length > 0){
			e = (ConnectorException) params[0];
		}
		this.partyId = userLogin.getString("partyId");
		otherModules = request.getParameter("otherModules");
		if(UtilValidate.isNotEmpty(otherModules)){
			String fileSharePartyId = request.getParameter("fileSharePartyId");
			if(UtilValidate.isNotEmpty(fileSharePartyId)){
				this.partyId = fileSharePartyId;
			}
		}
		try {
			setResponseHeader(response, ServletContextFactory.getServletContext());

			String jsonResponse = request.getParameter("jsonResponse");
			String fineupload = request.getParameter("fineupload");
			if(UtilValidate.isNotEmpty(fineupload)){
				response.setContentType("application/x-json");
				executeFineUpload(response.getOutputStream());
			}else if(UtilValidate.isNotEmpty(jsonResponse)){
				response.setContentType("application/x-json");
				executeJson(response.getOutputStream());
			}else{
				execute(response.getOutputStream());
			}
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (ConnectorException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * initialize params for command handler.
	 *
	 * @param request request
	 * @param params execute additional params.
	 * @throws ConnectorException to handle in error handler.
	 */
	public void initParams(final HttpServletRequest request,
						   final Object... params)
			throws ConnectorException, GenericEntityException {
		delegator = (Delegator) request.getAttribute("delegator");
		dataType = EntityUtilProperties.getPropertyValue("content.properties", "content.store.type", "localhost", delegator);
		disc = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);
		dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		this.request = request;
		this.fileId = request.getParameter("fileId");
		String storeType = UtilProperties.getPropertyValue("content.properties", "content.store.type");
		fileManager = FileManagerFactory.getFileManager(storeType, (GenericDelegator)delegator);

		moduleId = this.request.getParameter("moduleId");
		moduleType = this.request.getParameter("moduleType");
		String groupId = "";
		if("case".equals(moduleType)){//case模块，上级根据具体case中参与的情况获取（考虑到合伙人）
			GenericValue casePartyMember = delegator.findOne("TblCasePartyMember", false, UtilMisc.toMap("caseId", moduleId, "partyId", loginUserPartyId));
			if(casePartyMember != null){
				groupId = casePartyMember.getString("groupPartyId");
			}else{
				groupId = loginUserPartyId;
			}
		}else{
			List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", loginUserPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
			groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).getString("partyIdFrom") : loginUserPartyId;
		}

		groupInfo = EntityQuery.use(delegator).from("BasicGroupInfo").where("partyId", groupId).queryOne();
		isCompany = "CASE_ROLE_OWNER".equals(groupInfo.getString("roleTypeId"));
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
	 * executes command and writes to response.
	 *
	 * @param out response output stream
	 * @throws ConnectorException when error occurs
	 */
	public void executeJson(final OutputStream out)
			throws ConnectorException{}
	/**
	 * executes command and writes to response.
	 *
	 * @param out response output stream
	 * @throws ConnectorException when error occurs
	 */
	public void executeFineUpload(final OutputStream out)
			throws ConnectorException{}

	/**
	 * sets header in response.
	 *
	 * @param response servlet response
	 * @param sc sevletcontext
	 */
	public abstract void setResponseHeader(final HttpServletResponse response,
										   final ServletContext sc) throws IOException;

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
	 * 保存文件或文件夹信息
	 * @param filePath 路径
	 * @param fileName 文件或这文件夹名称
	 * @param rootId 归属类型ID
	 * @param currentFolder 归属文件夹ID
     * @param fileFlag 是否文件夹表示
     */
	protected String  saveFileInfo(String filePath, String fileName, String currentFolder, String rootId, String fileFlag, String remarks, String partyId, String fileId,String folderStructure){
		try {
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateTime = format.format(date);
			//判断是文件文件夹还是文件
			if(fileFlag.equals("file")){
				//如果是文件放置到TblFileOwnership表中
				Map<String, Object> fileMap = new HashMap<>();
				String id = delegator.getNextSeqId("TblFileOwnershipId");
				fileMap.put("id", id);
				fileMap.put("fileId", fileId);
				if(!currentFolder.equals("1") && !currentFolder.equals("2")){
					fileMap.put("folderId", currentFolder);
				}
				fileMap.put("folderStructure", folderStructure);
				fileMap.put("partyId", partyId);
				fileMap.put("fileType", "1");
				fileMap.put("filePermissions", FILEPMS);
				fileMap.put("createPartyId", userLogin.get("partyId"));
				fileMap.put("createFileTime", timestamp);
				fileMap.put("fileVersion", dateTime);
				GenericValue file = delegator.makeValue("TblFileOwnership",fileMap);
				file.create();
				//如果是文件夹下有文件放置到TblFileOwnership表中
				List<GenericValue> tblFileOwnershipList = EntityQuery.use(delegator).from("TblFileOwnership").where(UtilMisc.toMap("fileId", fileId,"folderId", file.getString("folderId"),"partyId", userLogin.getString("partyId"))).queryList();
				if(UtilValidate.isEmpty(tblFileOwnershipList)){
					Map<String, Object> fileMap2 = new HashMap<>();
					String id2 = delegator.getNextSeqId("TblFileOwnership");
					fileMap2.put("id", id2);
					fileMap2.put("fileId", fileId);
					fileMap2.put("folderId", currentFolder);
					fileMap2.put("partyId", userLogin.getString("partyId"));
					fileMap2.put("fileType", "2");
					fileMap2.put("filePermissions", file.getString("filePermissions"));
					fileMap2.put("createPartyId", userLogin.get("partyId"));
					fileMap2.put("folderStructure", file.getString("folderStructure"));
					fileMap2.put("createFileTime", timestamp);
					fileMap2.put("fileVersion", dateTime);
					GenericValue file2 = delegator.makeValue("TblFileOwnership",fileMap2);
					file2.create();
				}
			}else{
				//如果是文件夹放置到TblDirectoryStructure
				String id = delegator.getNextSeqId("TblDirectoryStructureId");
				fileId = delegator.getNextSeqId("TblDirectoryStructure");
				Map<String, Object> folderMap = new HashMap<>();
				if(!currentFolder.equals("1") && !currentFolder.equals("2")){
					folderMap.put("parentFolderId", currentFolder);
				}
				folderMap.put("id", id);
				folderMap.put("folderType", "1");
				folderMap.put("folderName",fileName);
				folderMap.put("folderPath",filePath);
				List<GenericValue> folderList = EntityQuery.use(delegator).from("TblDirectoryStructure").where(folderMap).queryList();
				if(UtilValidate.isNotEmpty(folderList)){
					return "repeatError";
				}
				folderMap.put("foldeRemarks",remarks);
				folderMap.put("folderId", fileId);
				folderMap.put("partyId", partyId);
				folderMap.put("folderPermissions", FILEPMS);
				folderMap.put("createPartyId", userLogin.get("partyId"));
				folderMap.put("createFolderTime", timestamp);
				GenericValue folder = delegator.makeValue("TblDirectoryStructure",folderMap);
				folder.create();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return fileId;
	}

	/**
	 * 删除文件或者文件夹信息
	 * @param fileId 文件ID
	 * @param fileFlag 文件或者文件夹标识
     */
	protected void delFileInfo(String fileId, String fileFlag){
		try {
			if ("file".equals(fileFlag)) {
				GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
				if(dataResource != null){
					fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
					fileManager.delFile(fileId);
				}
			} else {
				//删除文件夹相关
				delFolder(fileId, fileId);
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 递归删除文件夹和其文件夹下的文件
	 * @param fileId 递归的文件夹ID
	 * @param folderId 初始的文件夹ID
     */
	private void delFolder(String fileId,String folderId){
		try {
			//查找文件夹下是否存在文件夹，如果存在，则递归查询文件夹下是否还存在直到最后一层。
			List<GenericValue> folderList = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("parentFolderId",fileId)).queryList();
			if(UtilValidate.isNotEmpty(folderList) && folderList.size() > 0){
				for(GenericValue genericValue : folderList){
					delFolder(genericValue.get("folderId").toString(),folderId);
				}
			}else{
				//查找文件夹下最后一层的文件夹下，是否存在文件，如果存在，则删除文件信息和当前这一层文件夹的信息，接着递归查询初始文件夹另外的文件夹信息。
				List<GenericValue> fileList = EntityQuery.use(delegator).select().from("TblFileOwnership").where(UtilMisc.toMap("folderId",fileId)).queryList();
				if(UtilValidate.isNotEmpty(fileList)){
					for(GenericValue genericValue : fileList){
						//删除文件相关
						String file = genericValue.get("fileId").toString();
						GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",file)).queryOne();
						if(dataResource != null) {
							fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator) delegator);
//							fileManager.delFileHistoryList(file);
							fileManager.delFile(file);
						}else{
							//在最后查找不到文件和文件夹信息后，删除初始文件夹信息并结束递归
							delegator.removeByAnd("TblFileOwnership", UtilMisc.toMap("fileId", file));
						}
					}
					delFolder(folderId,folderId);
				}else{
					//在最后查找不到文件和文件夹信息后，删除初始文件夹信息并结束递归
					delegator.removeByAnd("TblDirectoryStructure", UtilMisc.toMap("parentFolderId", fileId));
					delegator.removeByAnd("TblDirectoryStructure", UtilMisc.toMap("folderId", fileId));
//					delegator.removeByAnd("TblFileOwnership", UtilMisc.toMap("folderId", fileId));
					if(!fileId.equals(folderId)){
						delFolder(folderId,folderId);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	public String getFolderPath(String fileId, String fileFalg){
		String path = "";
		GenericValue fileInfo = null;
		try {
			if(fileFalg.equals("file")){
				fileInfo = EntityQuery.use(delegator).select().from("TblFileOwnership").where(UtilMisc.toMap("fileType","1","fileId",fileId)).queryOne();
				GenericValue folder = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderType","1","folderId",fileInfo.get("folderId"))).queryOne();
				return folder.get("folderPath").toString() + folder.get("folderName") + "/";
			}else{
				fileInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderType","1","folderId",fileId)).queryOne();
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(fileInfo)){
			if(UtilValidate.isNotEmpty(fileInfo.get("folderPath").toString())){
				path =  fileInfo.get("folderPath").toString() + fileInfo.get("folderName").toString() + "/";
			}else{
				path = "/" + partyId + "/" + fileInfo.get("folderName").toString() + "/";
			}

		}else{
			path = "/" + partyId + "/";
		}
		return path;
	}

}
