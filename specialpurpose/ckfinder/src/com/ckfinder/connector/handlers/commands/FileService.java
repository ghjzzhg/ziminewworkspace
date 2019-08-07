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

import com.ckfinder.connector.configuration.ConfigurationFactory;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.utils.FileUtils;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.FileManagerFactory;
import org.ofbiz.content.data.FileTypeManager;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import sun.security.util.Cache;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class FileService {
	public static final String module = FileService.class.getName();

	private static Set<String> supportImgType = new HashSet<String>();

	static {
		supportImgType.add("jpg");
		supportImgType.add("png");
		supportImgType.add("jpeg");
		supportImgType.add("gif");
		supportImgType.add("bmp");
	}
	/**
	 * 文件历史信息
	 * @param request request
	 * @param  response response
	 * @return 成功失败
	 * @throws GenericEntityException
     */
	public static String showFileHistoryList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		String fileId = request.getParameter("fileId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> fileList = EntityQuery.use(delegator).select().from("HistoryFileList").where(UtilMisc.toMap("dataResourceId",fileId)).queryList();
		request.setAttribute("fileList",fileList);
		return "success";
	}

	/**
	 * 检查文件是否已经存在
	 * @param request request
	 * @param response response
	 * @return 成功失败
	 * @throws GenericEntityException 数据库错误信息
     */
	public static String checkFileStatus(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fileName = request.getParameter("fileName");
		String currentFolderId = request.getParameter("currentFolder");
		String rootId = request.getParameter("rootFolderId");
		String otherModules = request.getParameter("otherModules");
		String folderId = "";
		if(UtilValidate.isNotEmpty(currentFolderId)){
			folderId = currentFolderId;
		}else{
			folderId = rootId;
		}
		if(UtilValidate.isNotEmpty(otherModules)){
			String fileSharePartyId = request.getParameter("fileSharePartyId");
			if(UtilValidate.isNotEmpty(fileSharePartyId)){
				partyId = fileSharePartyId;
			}
			folderId = currentFolderId;
		}
		GenericValue folder = getFolder(folderId, "folder", delegator, partyId);
		fileName = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1, fileName.length());
		List fileconditionList = new ArrayList();
		if(folder != null){
			fileconditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, folder.get("folderId")));
			fileconditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
		}else{
			fileconditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, null));
			fileconditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, null));
			fileconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		}
		fileconditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, "1"));
		fileconditionList.add(EntityCondition.makeCondition("fileName", EntityOperator.EQUALS, fileName));
		List<GenericValue> fileList = EntityQuery.use(delegator).select().from("DataResourceFileList").where(fileconditionList).queryList();
		if(UtilValidate.isNotEmpty(fileList)){
			String dataResourceId = fileList.get(0).get("fileId").toString();
			request.setAttribute("fileStatus","1");
			request.setAttribute("fileId", dataResourceId);
		}else{
			request.setAttribute("fileStatus","0");
		}
		return "success";
	}

	public static String showPhoto(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			String fileId = request.getParameter("fileId");
			String storeType = UtilProperties.getPropertyValue("content.properties", "content.store.type");
			FileTypeManager fileManager = FileManagerFactory.getFileManager(storeType, (GenericDelegator)delegator);
			GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
			if(dataResource != null){
				fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
			}
			File file = fileManager.searchFileById(fileId,false);
			if(UtilValidate.isNotEmpty(file) && file.exists()){
				FileInputStream hFile = new FileInputStream(file); // 以byte流的方式打开文件
				int i = hFile.available();
				byte data[] = new byte[i];
				hFile.read(data); // 读数据
				hFile.close();
				response.setContentType("image/*"); // 设置返回的文件类型
				OutputStream toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
				toClient.write(data); // 输出数据
				toClient.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static GenericValue getFolder(String fileId, String fileFalg, Delegator de,String partyId){
		GenericValue fileInfo = null;
		try {
			if(fileFalg.equals("file")){
				fileInfo = EntityQuery.use(de).select().from("TblFileOwnership").where(UtilMisc.toMap("fileType","1","fileId",fileId)).queryOne();
				GenericValue folder = EntityQuery.use(de).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderType","1","folderId",fileInfo.get("folderId"))).queryOne();
				return folder;
			}else{
				fileInfo = EntityQuery.use(de).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderType","1","folderId",fileId)).queryOne();
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return fileInfo;
	}

	public static String editFileEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String dataType = EntityUtilProperties.getPropertyValue("content.properties", "content.store.type", "localhost", delegator);
		String fileId = request.getParameter("fileName");
		String storeType = UtilProperties.getPropertyValue("content.properties", "content.store.type");
		FileTypeManager fileManager = FileManagerFactory.getFileManager(storeType, (GenericDelegator)delegator);
		GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
		if(dataResource != null){
			fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
		}
		File file = fileManager.searchFileById(fileId, false);
		if(null != file){
			if(!file.exists()){
			}
			exportFileToResponse(file, response);
			return "success";
		}else{
			return "error";
		}
	}

	/**
	 * 保存分享
	 * @param dataIdList List<String>
	 * @param delegator Delegator
	 */
	public static String saveShare(List<String> dataIdList, String fileId, Delegator delegator,String fileFlag, String permissionStr){
		String msg = "分享成功！";
		try{
			Date dateTime = new Date();
			java.sql.Timestamp time = new java.sql.Timestamp(dateTime.getTime());
			if(UtilValidate.isNotEmpty(dataIdList)){
				if(fileFlag.equals("file")){
					for(String toPartyId : dataIdList){
						Map<String,Object> shareMap = new HashMap<>();
						shareMap.put("fileId", fileId);
						shareMap.put("partyId", toPartyId);
						GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblFileOwnership").where(shareMap).queryOne();
						if(UtilValidate.isNotEmpty(dataResourceRole)){
							return "分享人已存在，请检查！";
						}
					}
					for(String toPartyId : dataIdList){
						Map<String,Object> shareMap = new HashMap<>();
						shareMap.put("fileId", fileId);
						shareMap.put("partyId", toPartyId);
						shareMap.put("fileType", "2");
						GenericValue dataResourceRole = delegator.makeValidValue("TblFileOwnership", shareMap);
						dataResourceRole.create();
					}
				} else {
					for(String toPartyId : dataIdList){
						Map<String,Object> shareMap = new HashMap<>();
						shareMap.put("folderId", fileId);
						shareMap.put("folderType", "2");
						shareMap.put("partyId", toPartyId);
						GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(shareMap).queryOne();
						if(UtilValidate.isNotEmpty(dataResourceRole)){
							return "分享人已存在，请检查！";
						}
					}
					for(String toPartyId : dataIdList){
						GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",fileId,"folderType","1")).queryOne();
						dataResourceRole.put("folderId", fileId);
						dataResourceRole.put("folderType", "2");
						dataResourceRole.put("folderPermissions", permissionStr);
						dataResourceRole.put("partyId", toPartyId);
						dataResourceRole.create();
					}
				}
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return msg;
	}

	public static void exportFileToResponse(File file, HttpServletResponse response) throws IOException {
		String message = "";
		if(file.exists()){
			InputStream inputStream = new FileInputStream(file);
			response.setHeader("Content-Length", String.valueOf(file.length()));
			String extension = FilenameUtils.getExtension(file.getName());
			if(supportImgType.contains(extension)){
				response.setContentType("image/" + extension);
			}else{
				if("pdf".equals(extension)){
					response.setContentType("application/pdf");
				}else if("xls".equals(extension)){
					response.setContentType("application/vnd.ms-excel");
				}else if("doc".equals(extension)){
					response.setContentType("application/rtf");
				}
			}
			ServletOutputStream sos = null;
			try{
				sos = response.getOutputStream();
				IOUtils.copy(inputStream, sos);
			}finally {
				if (sos != null) {
					try {
						sos.flush();
						sos.close();
					} catch (IOException e) {
					}
				}
				inputStream.close();
			}
		}else{
		}
	}

	public static String addFilePath(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List list = new ArrayList();
			List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("folderStructure", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("folderStructure", EntityOperator.EQUALS, ""));
			list.add(EntityCondition.makeCondition(exprs, EntityOperator.OR));
			list.add(EntityCondition.makeCondition("folderId", EntityOperator.NOT_EQUAL, null));
			List<GenericValue> genericValues = EntityQuery.use(delegator).from("TblFileOwnership").where(list).queryList();
			for(GenericValue genericValue : genericValues){
				if(UtilValidate.isNotEmpty(genericValue.get("folderId"))){
					String rootFolder = genericValue.get("folderId").toString();
					StringBuffer folderIds = new StringBuffer();
					folderIds.append(rootFolder).append(";");
					String folder = addFolderPath(rootFolder,delegator,folderIds);
					genericValue.set("folderStructure",folder);
				}
			}
			delegator.storeAll(genericValues);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String addFolderPath(String folderId, Delegator delegator, StringBuffer folderIds){
		String folderList = "";
		try {
			GenericValue folderInfo = EntityQuery.use(delegator).from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",folderId,"folderType","1")).queryOne();
			if(UtilValidate.isNotEmpty(folderInfo.get("parentFolderId"))){
				String parentFolderId = folderInfo.get("parentFolderId").toString();
				if(UtilValidate.isNotEmpty(folderIds)){
					folderIds.append(parentFolderId).append(";");
				}
				addFolderPath(parentFolderId, delegator,folderIds);
			}
			String[] folderIdList = folderIds.toString().split(";");
			for(int i = folderIdList.length-1; i >= 0; i--){
				folderList += folderIdList[i] + ";";
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return folderList;
	}
}
