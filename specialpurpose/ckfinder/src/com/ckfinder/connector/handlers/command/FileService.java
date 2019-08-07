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

import com.ckfinder.connector.configuration.ConfigurationFactory;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.utils.FileUtils;
import javolution.util.FastList;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

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

	private static IConfiguration getIConfigurationByType(Delegator delegator,HttpServletRequest request){
		IConfiguration configuration = null;
		try {
			GenericValue loginMap = (GenericValue) request.getSession().getAttribute("userLogin");
			request.getSession().setAttribute("userLogin",loginMap);
			configuration = ConfigurationFactory.getInstace().getConfiguration(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration;
	}

	/**
	 * 删除登陆人分享数据
	 * @return String
	 */
	public static String delDataResourceRole(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourceType", "localhost", delegator);
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		String absolutePath = configuration.getTypes().get("本人分享").getPath();
		absolutePath = absolutePath.replace("\\","/");
		String dataResourceId = request.getParameter("dataResourceId");
		GenericValue fileModel = searchFile(dataResourceId, delegator);
		String dataResourceName = fileModel.get("dataResourceName").toString();
		String dataResourceTypeId = fileModel.get("dataResourceTypeId").toString();
		Map<String,Object> dataResourceRoleMap = new HashMap<String,Object>();
		Map<String, Object> attrMap = new HashMap<String,Object>();
		dataResourceRoleMap.put("dataResourceId", dataResourceId);
		dataResourceRoleMap.put("partyId", request.getParameter("partyId"));
		dataResourceRoleMap.put("fromDate", request.getParameter("fromDate"));
//		 dataResourceCount = 0;
		try{
			long dataResourceCount = delegator.findCountByCondition("DataResourceRole",
					EntityCondition.makeCondition("dataResourceId",EntityOperator.EQUALS,dataResourceId
					),null,null);
			if(dataResourceCount > 1){
				delegator.removeByAnd("DataResourceRole",dataResourceRoleMap);
			}else if(dataResourceCount == 1){
				delegator.removeByAnd("DataResourceRole",dataResourceRoleMap);
				String sharedPath = absolutePath + "/" + dataResourceName;
				if(dataResourceTypeId.equals(dataType + "_FILE")){
					DeleteFolderCommand.deleteSharedFile(sharedPath);
				}else if(dataResourceTypeId.equals(dataType + "_FOLDER")){
					DeleteFolderCommand.deleteDirectory(sharedPath);
				}
			}
			attrMap.put("msg", "删除成功！");//返回json数据
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}
		request.setAttribute("data",attrMap);
		return "success";
	}

	/**
	 * 增加登陆人分享数据
	 * @return String
	 */
	public static String addShare(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourceType", "localhost", delegator);
		String absolutePath = configuration.getTypes().get("本人分享").getPath();
		absolutePath = absolutePath.replace("\\","/");
		String fileId = request.getParameter("fileId");
		Map<String, Object> attrMap = new HashMap<String,Object>();
		List<Map<String,Object>> dataIdList = new ArrayList<Map<String,Object>>();
		splitAndSaveData(request.getParameter("dataScope_dept_only"), dataIdList,"SCOPE_DEPT_ONLY");
		splitAndSaveData(request.getParameter("dataScope_dept_like"), dataIdList,"SCOPE_DEPT_LIKE");
		splitAndSaveData(request.getParameter("dataScope_user"), dataIdList,"SCOPE_USER");
		GenericValue fileModel = searchFile(fileId, delegator);
		List<GenericValue> sharedFileModel = searchSharedFile(fileId, delegator);
		if(UtilValidate.isNotEmpty(sharedFileModel)){
			saveShare(dataIdList, fileId, delegator,request);
			attrMap.put("msg", "分享成功！");//返回json数据
		}else {
			String oldPath;
			String newPath;
			Boolean msg = true;
			if(fileModel.get("dataResourceTypeId").toString().equals(dataType + "_FILE")){
				oldPath = fileModel.get("parentObjectInfo").toString() + fileModel.get("dataResourceName").toString();
				newPath = absolutePath + "/" + fileModel.get("dataResourceName").toString();
				msg = copyFile(oldPath, newPath);
			}else if(fileModel.get("dataResourceTypeId").toString().equals(dataType + "_FOLDER")){
				oldPath = fileModel.get("parentObjectInfo").toString()+fileModel.get("dataResourceName").toString();
				newPath = absolutePath + "/" + fileModel.get("dataResourceName").toString();
				msg = copyFolder(oldPath, newPath);
			}
			if(msg){
				saveShare(dataIdList, fileId, delegator,request);
				attrMap.put("msg", "分享成功！");//返回json数据
			}else{
				attrMap.put("msg", "分享文件名重复，请重新命名！");//返回json数据
			}
		}
		request.setAttribute("data",attrMap);
		return "success";
	}

	public static String setJson(HttpServletRequest request, HttpServletResponse response, Object object){
		//返回json
		String httpMethod = request.getMethod();
		Writer out;
		try {
			JSON json = JSON.from(object);
			String jsonStr = json.toString();
			if (UtilValidate.isEmpty(jsonStr)) {
				Debug.logError("JSON Object was empty; fatal error!", module);
				return "error";
			}
			if ("GET".equalsIgnoreCase(httpMethod)) {
				Debug.logWarning("for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
						+ "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
						+ "You might need to remove it if you use Ajax GET responses (not recommended)."
						+ "In case, the util.js scrpt is there to help you", module);
				jsonStr = "//" + jsonStr;
			}
			response.setContentType("application/x-json");
			response.setContentLength(jsonStr.getBytes("UTF8").length);
			out = response.getWriter();
			out.write(jsonStr);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, module);
		}
		return "success";
	}

	/**
	 * 保存分享
	 * @param dataIdList List<String>
	 * @param delegator Delegator
	 */
	public static void saveShare(List<Map<String,Object>> dataIdList, String fileId, Delegator delegator,HttpServletRequest request){
		try{
			Date dateTime = new Date();
			IConfiguration configuration  = getIConfigurationByType(delegator, request);
			String absolutePath = configuration.getTypes().get("本人分享").getPath();
			absolutePath = absolutePath.replace("\\","/");
			GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
			java.sql.Timestamp time = new java.sql.Timestamp(dateTime.getTime());
			if(UtilValidate.isNotEmpty(dataIdList)){
				for(Map<String,Object> data : dataIdList){
					Map<String,Object> shareMap = new HashMap<String,Object>();
					shareMap.put("dataResourceId", fileId);
					shareMap.put("partyId", data.get("id").toString());
					shareMap.put("roleType", data.get("type").toString());
					shareMap.put("roleTypeId", "_NA_");
					shareMap.put("fromDate", time);
					shareMap.put("thruDate", time);
					shareMap.put("sharedPath", absolutePath + "/" + genericValue.get("dataResourceName"));
					GenericValue dataResourceRole = delegator.makeValidValue("DataResourceRole", shareMap);
					dataResourceRole.create();
				}
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查找文件相关信息
	 * @param fileId String
	 * @param delegator Delegator
	 * @return GenericValue
	 */
	public static GenericValue searchFile(String fileId,Delegator delegator){
		GenericValue FileModel = null;
		try {
			FileModel = EntityQuery.use(delegator).select()
					.from("DataResource")
					.where(EntityCondition.makeCondition("dataResourceId", fileId))
					.queryOne();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return FileModel;
	}

	/**
	 * 查找分享文件记录
	 * @param fileId String
	 * @param delegator Delegator
	 * @return GenericValue
	 */
	public static List<GenericValue> searchSharedFile(String fileId,Delegator delegator){
		List<GenericValue> sharedFileModel = null;
		try {
			sharedFileModel = EntityQuery.use(delegator).select()
					.from("DataResourceRole")
					.where(EntityCondition.makeCondition("dataResourceId", fileId))
					.queryList();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return sharedFileModel;
	}

	/**
	 * 分隔数据，用于存储
	 * @param dataScope String
	 * @param dataIdList List<String>
	 */
	public static void splitAndSaveData(String dataScope,List<Map<String,Object>> dataIdList,String type){
		String[] executor = null;
		if(null != dataScope && dataScope.length() > 0 && !"null".equals(dataScope)){
			executor = dataScope.split(",");
		}
		if(UtilValidate.isNotEmpty(executor)){
			for(String id :executor){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id",id);
				map.put("type",type);
				dataIdList.add(map);
			}
		}
	}

	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static Boolean copyFile(String oldPath, String newPath) {
		Boolean flag = true;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldFile = new File(oldPath);
			File newFile = new File(newPath);
			if (oldFile.exists() && !newFile.exists()) { //旧文件存在，且新文件不存在
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}else{
				flag = false;
			}
		}
		catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 复制整个文件夹内容
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static Boolean copyFolder(String oldPath, String newPath) {
		Boolean flag = true;
		try {
			File newFolder = new File(newPath);
			if(newFolder.exists()){//如果文件夹存在 则不继续执行
				flag = false;
			}else{
				newFolder.mkdirs(); //如果文件夹不存在 则建立新文件夹
				File a = new File(oldPath);
				String[] file = a.list();
				File temp = null;
				for (int i = 0; i < file.length; i++) {
					if(oldPath.endsWith(File.separator)){
						temp = new File(oldPath+file[i]);
					}
					else{
						temp = new File(oldPath+File.separator+file[i]);
					}

					if(temp.isFile()){
						FileInputStream input = new FileInputStream(temp);
						FileOutputStream output = new FileOutputStream(newPath + "/" + temp.getName());
						byte[] b = new byte[1024 * 5];
						int len;
						while ( (len = input.read(b)) != -1) {
							output.write(b, 0, len);
						}
						output.flush();
						output.close();
						input.close();
					}
					if(temp.isDirectory()){//如果是子文件夹
						copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
					}
				}
			}
		}
		catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	public static String showFileList(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String upFoldePath = request.getParameter("folderPath");
		String partyId = userLogin.getString("partyId");
		String folderType = request.getParameter("folderType");
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		String absolutePath = configuration.getTypes().get(folderType).getPath();
		absolutePath = absolutePath.replace("\\","/");
		if(absolutePath.contains("/ckfinder/ckfinder")){
			absolutePath = absolutePath.replace("/ckfinder/ckfinder","/ckfinder");
		}
		String folderPath = "";
		String fileId = request.getParameter("folderId");

		if(null != fileId && fileId.length() > 0){
			GenericValue fileModel = null;
			try {
				fileModel = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			folderPath = fileModel.get("parentObjectInfo").toString() + fileModel.get("dataResourceName");
		}else if(null != upFoldePath && upFoldePath.length() > 0){
			folderPath = upFoldePath;
		}else{
			folderPath = absolutePath;
		}
		List<GenericValue> fileList = new ArrayList<GenericValue>();
		List<Map<String,Object>> newFileList = new ArrayList<Map<String,Object>>();
		File file = new File(folderPath);
		if(file.exists()){
			String path =file.getAbsolutePath()+"/";
			path = path.replace("\\","/");
			try {
				fileList = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path)).orderBy("-dataResourceTypeId").queryList();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			if(null != fileList && fileList.size() > 0){
				for(GenericValue fileModel: fileList){
					Map<String,Object> map = new HashMap<String,Object>();
					map.putAll(fileModel);
					File subfile = new File(map.get("parentObjectInfo").toString() + map.get("dataResourceName").toString());
					if(subfile.isDirectory()){
						map.put("type","folder");
						map.put("typeName","文件夹");
					}else{
						map.put("type","file");
						map.put("typeName","文件");
					}
					if("协作空间".equals(folderType)){
						Map<String,Object> dataMap = new HashMap<String,Object>();
						dataMap.put("dataId",fileModel.get("dataResourceId"));
						dataMap.put("entityName","DataResource");
						List<GenericValue> partyList = null;
						try {
							partyList = EntityQuery.use(delegator).select().from("TblDataScope").where(dataMap).queryList();
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						if(null != partyList && partyList.size() > 0){
							String idList = "";
							for(GenericValue party : partyList){
								if("SCOPE_USER".equals(party.get("scopeType"))){
									idList = idList + party.get("scopeValue") + ",";
								}else{
									List<GenericValue> personList = null;
									try {
										personList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department", party.get("scopeValue"))).queryList();
									} catch (GenericEntityException e) {
										e.printStackTrace();
									}
									for(GenericValue person : personList){
										idList = idList + person.get("partyId") + ",";
									}
									if("SCOPE_DEPT_LIKE".equals(party.get("scopeType"))){
										Map<String, Object> positionMap = UtilMisc.toMap("partyIdFrom", party.get("scopeValue"), "partyRelationshipTypeId", "GROUP_ROLLUP");
										List<GenericValue> members = null;
										try {
//											members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
											members = EntityQuery.use(delegator).select().from("PartyRelationshipAndDetail").where(positionMap).queryList();
										} catch (GenericEntityException e) {
											e.printStackTrace();
										}
										List data = FastList.newInstance();
										if (UtilValidate.isNotEmpty(members)) {
											for (GenericValue member : members) {
												List<GenericValue> persons = null;
												try {
													persons = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department", member.get("scopeValue"))).queryList();
												} catch (GenericEntityException e) {
													e.printStackTrace();
												}
												for(GenericValue person : persons){
													idList = idList + person.get("partyId") + ",";
												}
											}
										}
									}
								}
							}
							if(idList.indexOf(partyId+",") >= 0){
								newFileList.add(map);
							}
						}
					}else{
						newFileList.add(map);
					}
				}
			}
		}
		String path = "";
		if((null != fileId && fileId.length() > 0) || (upFoldePath != null && !upFoldePath.equals(absolutePath))){
			String filePath = file.getPath().replace("\\","/");
			path = filePath.substring(0,filePath.lastIndexOf("/"));
		}
		Map<String,Object> map = new HashMap<String,Object>();
		path = path.replace("\\","/");
		map.put("returnList",path);
		map.put("fileList",newFileList);
		request.setAttribute("data",map);
		return "success";
	}

	public static String sendFileList(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		String personList = request.getParameter("personIds");
		String fileIdList = request.getParameter("fileIdList");
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		String sendPath = configuration.getTypes().get("发送文件").getPath();
		String receivePath = configuration.getTypes().get("接收文件").getPath();
		File sendPathFile = new File(sendPath);
		File receivePathFile = new File(receivePath);
		String[] files = fileIdList.split(",");
		String[] persons = personList.split(",");
		String sendName = searchPerson(partyId,delegator);
		for(String person : persons){
			String receiceName = searchPerson(person,delegator);
			String newSendPath = (sendPathFile.getPath() + "/" + receiceName).replace("\\","/");
			String newReceivePath = (receivePathFile.getPath().substring(0, receivePathFile.getPath().lastIndexOf(File.separator)) + "/" + person + "/" + sendName).replace("\\","/");
			if(newSendPath.contains("/ckfinder/ckfinder")){
				newSendPath = newSendPath.replace("/ckfinder/ckfinder","/ckfinder");
			}
			if(newReceivePath.contains("/ckfinder/ckfinder")){
				newReceivePath = newReceivePath.replace("/ckfinder/ckfinder","/ckfinder");
			}
			mkdirPath(newSendPath,partyId,delegator);
			mkdirPath(newReceivePath,person,delegator);
			for(String fileId : files){
				GenericValue fileModel = searchFile(fileId,delegator);
				String file = fileModel.get("parentObjectInfo").toString()+fileModel.get("dataResourceName").toString();
				String sendfile = newSendPath + "/" + fileModel.get("dataResourceName").toString();
				String receivefile = newReceivePath + "/" + fileModel.get("dataResourceName").toString();
				File sendFile = new File(sendfile);
				File receiveFile = new File(receivefile);
				int counter = 1;
				File newSendFile;
				File newReceiveFile;
				while (true){
					if(sendFile.exists() || receiveFile.exists()){
						String newSendFileName = FileUtils.getFileNameWithoutExtension(sendFile.getName(), false)
								+ "(" + counter + ")."
								+ FileUtils.getFileExtension(sendFile.getName(), false);
						newSendFile = new File(sendFile.getParent(), newSendFileName);
						newReceiveFile = new File(receiveFile.getParent(), newSendFileName);
						if(newSendFile.exists() || newReceiveFile.exists()){
							counter++;
						}else {
							createFile(newSendFile.getPath(), file);
							createFile(newReceiveFile.getPath(), file);
							saveDataResource(newSendPath,newSendFileName,partyId,delegator);
							saveDataResource(newReceivePath,newSendFileName,person,delegator);
							break;
						}
					}else {
						createFile(sendfile,file);
						createFile(receivefile,file);
						saveDataResource(newSendPath,fileModel.get("dataResourceName").toString(),partyId,delegator);
						saveDataResource(newReceivePath,fileModel.get("dataResourceName").toString(),person,delegator);
						break;
					}
				}
			}
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("message","发送成功");
		request.setAttribute("data", map);
		return "success";
	}

	private  static void mkdirPath(String path,String partyId,Delegator delegator){
		File file = new File(path);
		if(!file.exists()){
			saveFolder(file,partyId,delegator);
			file.mkdirs();
		}
	}

	private  static void saveFolder(File file,String partyId,Delegator delegator){
		String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourceType", "localhost", delegator);
		Map<String,Object> map = new HashMap<String,Object>();
		String folderId = delegator.getNextSeqId("DataResource");//获取主键ID
		map.put("dataResourceId",folderId);
		map.put("dataResourceTypeId",dataType + "_FOLDER");
		String path = file.getPath().replace("\\","/");
		String newPath = path.substring(0,path.lastIndexOf("/")+1);
		newPath = newPath.replace("\\","/");
		map.put("parentObjectInfo",newPath);
		map.put("objectInfo",newPath+file.getName());
		map.put("partyId",partyId);
		map.put("dataResourceName",file.getName());
		GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
		try {
			GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("parentObjectInfo",newPath,"dataResourceName",file.getName())).queryOne();
			if(!UtilValidate.isNotEmpty(genericValue)){
				dataResoure.create();
				String dataScope = delegator.getNextSeqId("TblDataScope");//获取主键ID
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	public static String searchPerson(String partyId,Delegator delegator){
		GenericValue party = null;
		try {
			party = EntityQuery.use(delegator).select()
					.from("Person")
					.where(EntityCondition.makeCondition("partyId", partyId))
					.queryOne();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String fullName = "";
		if(null != party){
			fullName = party.get("fullName").toString();
		}
		return fullName;
	}

	public static void createFile(String file1 ,String file2){
		try {
			FileInputStream inFile = new java.io.FileInputStream(file2);
			FileOutputStream out = new FileOutputStream(file1);
			byte[] bt = new byte[1024];
			int count;
			while ((count = inFile.read(bt)) > 0) {
				out.write(bt, 0, count);
			}
			inFile.close();
			out.close();
		} catch (IOException ex) {
		}
	}

	public static void saveDataResource(String path,String fileName,String partyId,Delegator delegator){
		String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourceType", "localhost", delegator);
		Map<String,Object> map = new HashMap<String,Object>();
		String resourceId = delegator.getNextSeqId("DataResource");//获取主键ID
		map.put("dataResourceId",resourceId);
		map.put("dataResourceTypeId",dataType + "_FILE");
		map.put("dataResourceName",fileName);
		map.put("partyId",partyId);
		path = path.replace("\\","/") + "/";
		map.put("parentObjectInfo",path);
		map.put("objectInfo",path+fileName);
		GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
		try {
			dataResoure.create();
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	public  static String searchDataResourceById(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fileId = request.getParameter("fileId");
		GenericValue fileData = null;
		try {
			fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String filePath = "";
		if(null != filePath){
			filePath = fileData.get("parentObjectInfo").toString().replace("\\","/") + fileData.get("dataResourceName");
			filePath = filePath.substring(filePath.lastIndexOf("specialpurpose/ckfinder/webapp")+"specialpurpose/ckfinder/webapp".length(),filePath.length());
			fileData.put("parentObjectInfo",filePath);
		}
		request.setAttribute("data", fileData);
		return "success";
	}

	public static String searchFileByIds(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String files = request.getParameter("files");
		List<Map<String,Object>> fileList = new ArrayList<Map<String,Object>>();
		String fileIds = "";
		if(null != files && files.length() > 0){
			String[] fileIdList = files.split(",");
			for(String fileId : fileIdList){
				GenericValue data = null;
				try {
					data = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				Map<String,Object> filemap = new HashMap<String,Object>();
				filemap.putAll(data);
				String filePath = filemap.get("parentObjectInfo").toString();
				filePath = filePath.substring(filePath.lastIndexOf("specialpurpose/ckfinder/webapp")+"specialpurpose/ckfinder/webapp".length(),filePath.length());
				filemap.put("parentObjectInfo",filePath+filemap.get("dataResourceName"));
				String type = filemap.get("dataResourceName").toString();
				type = type.substring(type.lastIndexOf(".") + 1,type.length());
				filemap.put("type",type);
				fileList.add(filemap);
				fileIds = fileIds + filemap.get("dataResourceId") + ",";
			}
			fileIds = fileIds.substring(0,fileIds.length() - 1);
			map.put("fileIds",fileIds);
			map.put("fileList",fileList);
			request.setAttribute("data", map);
		}
		return "success";
	}

	public static  String testingFile(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourceType", "localhost", delegator);
		List<EntityExpr> cond = UtilMisc.toList(
				EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType + "_FOLDER"),
				EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType + "_FILE")
		);
		EntityCondition partyCond = EntityCondition.makeCondition(cond, EntityOperator.OR);
		List<GenericValue> fileList;
		try {
			fileList = EntityQuery.use(delegator).from("DataResource").where(partyCond).queryList();
			for(GenericValue genericValue : fileList){
				if(UtilValidate.isNotEmpty(genericValue.get("parentObjectInfo"))){
					String filePath = genericValue.get("parentObjectInfo").toString() + genericValue.get("dataResourceName");
					String fileId = genericValue.get("dataResourceId").toString();
					File file = new File(filePath);
					if(!file.exists()){
						delegator.removeByAnd("TblFileScope", UtilMisc.toMap("dataId", fileId,"entityName", "DataResource"));
						delegator.removeByAnd("TblDataScope", UtilMisc.toMap("dataId", fileId,"entityName", "DataResource"));
						delegator.removeByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", fileId));
						delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileId));
					}
				}
			}
			IConfiguration configuration  = getIConfigurationByType(delegator, request);
			String team = subPath(configuration.getTypes().get("协作空间").getPath());
			String personal = subPath(configuration.getTypes().get("个人文档").getPath());
			String sendPath = subPath(configuration.getTypes().get("发送文件").getPath());
			String receivePath = subPath(configuration.getTypes().get("接收文件").getPath());
			List<File> teamFileList = new ArrayList<File>();
			List<File> personalFileList = new ArrayList<File>();
			List<File> sendPathFileList = new ArrayList<File>();
			List<File> receivePathFileList = new ArrayList<File>();
			getAllFiles(new File(team),teamFileList);
			getAllFiles(new File(personal),personalFileList);
			getAllFiles(new File(sendPath),sendPathFileList);
			getAllFiles(new File(receivePath),receivePathFileList);
			delFile(teamFileList,delegator);
			delFile(personalFileList,delegator);
			delFile(sendPathFileList,delegator);
			delFile(receivePathFileList,delegator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static  void delFile(List<File> fileList,Delegator delegator){
		for(File file : fileList){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("dataResourceName",file.getName());
			map.put("parentObjectInfo",file.getParent().replace("\\","/")+"/");
			try {
				GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
				if(!UtilValidate.isNotEmpty(genericValue)){
					file.delete();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
	}

	private static String subPath(String path){
		if(path.contains("/ckfinder/ckfinder")){
			path = path.replace("/ckfinder/ckfinder","/ckfinder");
		}
		return path;
	}

	public static void getAllFiles(File dir, List<File> fileList)
	{
		File[] files=dir.listFiles();
		if(UtilValidate.isNotEmpty(files)){
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					getAllFiles(file, fileList);
				}
			}
		}
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
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fileName = request.getParameter("fileName");
		String folderPath = request.getParameter("currentFolder");
		String type = request.getParameter("type");
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		String team = subPath(configuration.getTypes().get(type).getPath());
		Map<String, Object> map = new HashMap<>();
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1,fileName.length());
		map.put("dataResourceName",fileName);
		map.put("parentObjectInfo",team + folderPath);
		GenericValue file = EntityQuery.use(delegator).select().from("DataResource").where(map).queryOne();
		if(UtilValidate.isNotEmpty(file)){
			request.setAttribute("fileStatus","1");
			request.setAttribute("fileId", file.get("dataResourceId"));
		}else{
			request.setAttribute("fileStatus","0");
		}
		return "success";
	}

	public static String showPhoto(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			String fileId = request.getParameter("fileId");
			GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
			String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourcePath", "localhost", delegator);
			FileInputStream hFile = new FileInputStream(dataType + fileInfo.get("objectInfo").toString()); // 以byte流的方式打开文件
			int i = hFile.available();
			byte data[] = new byte[i];
			hFile.read(data); // 读数据
			hFile.close();
			response.setContentType("image/*"); // 设置返回的文件类型
			OutputStream toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
			toClient.write(data); // 输出数据
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static String editFileEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String dataType = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourcePath", "localhost", delegator);
		String message = "";
		String fileId = request.getParameter("fileName");
		GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
		if(null != fileData){
			String path = dataType + fileData.get("parentObjectInfo").toString() + fileData.get("dataResourceName").toString();
			File file = new File(path);
			if(!file.exists()){
				message = "文件不存在";
				return "error";
			}
			exportFileToResponse(file, response);
			return "success";
		}else{
			return "error";
		}
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
						message = "关闭文件流错误";
					}
				}
				inputStream.close();
			}
		}else{
			message = "文件不存在";
		}
	}
}
