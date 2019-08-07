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
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.event.EventUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
*Author 邓贝贝
*created 2016/1/8 9:44
*function  获取服务器文件管理中的文件信息
*/
public class GetFileListForAndroid{
	public static final String module = GetFileListForAndroid.class.getName();
	public static List<GenericValue> resourceList = null;
	private static final float BYTES = 1024f;

	public static String searchFileList(HttpServletRequest request, HttpServletResponse response){

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		List<Map<String, Object>> list = null;
		String message = LoginWorker.login(request, response);
		if("success".equals(message)){
			String type = request.getParameter("type");
			try{
				if (UtilValidate.isNotEmpty(type)) {
					list = getExplore(request,configuration,delegator);
				} else {
					list = searchXmlType(request);
				}
			}catch (Exception e1){
				e1.printStackTrace();
			}
			return returnJson(request, response, list, module);
		}else {
			return EventUtil.returnError(request, "error");
		}
	}

	//客户端进入文件管理模式
	public static List<Map<String,Object>> searchXmlType(HttpServletRequest request){
		final Delegator delegator = (Delegator)request.getAttribute("delegator");
		IConfiguration configuration  = getIConfigurationByType(delegator, request);
		List<String> list = new ArrayList<String>();
		if (configuration.getDefaultResourceTypes().size() > 0) {
			list = configuration.getDefaultResourceTypes();
		} else {
			list = configuration.getResourceTypesOrder();
		}
		List<Map<String,Object>> listMap = new ArrayList<Map<String, Object>>();
		for (String name : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("downloadPath", "");
			map.put("targetId", "");
			map.put("path", "");
			map.put("fileSize","0");
			map.put("data","");
			listMap.add(map);
		}
		return listMap;
	}

	//客户端在文件管理状态下，进行文件操作
	public static List<Map<String, Object>> getExplore(HttpServletRequest request, IConfiguration configuration, Delegator delegator) throws GenericEntityException {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			File dir = null;
			List<GenericValue> dataResourceList = new ArrayList<GenericValue>();

			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			String partyId = userLogin.getString("partyId");
			String folderId = request.getParameter("targetId");
			String searchFileName = "";
			if (request.getParameter("searchFileName") != null){
				searchFileName = request.getParameter("searchFileName");
			}

			String type = request.getParameter("type");
			String currentFolder = request.getParameter("currentFolder");

			String path = configuration.getTypes().get(type).getPath() + currentFolder;
			if (path.contains("ckfinder\\ckfinder")) {
				path = path.replace("ckfinder\\ckfinder", "ckfinder");
			}
			path = path.replace("\\","/");
			//设置客户端显示文件的相对路径
			StringBuffer  filePath = new StringBuffer(configuration.getTypes().get(type).getUrl());
			String[] currentFolders = currentFolder.split("/");
			if (!(currentFolders.length == 1)){
				filePath.append(currentFolder);
			}
			//文件夹“他人分享”，和“本人分享”跟目录下的操作规则。
			if(("他人分享".equals(type) || "本人分享".equals(type)) && "/".equals(currentFolder)){
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				if("他人分享".equals(type)){
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, partyId));
					conditionList.add(EntityCondition.makeCondition("dataResourceName",EntityOperator.LIKE,"%" + searchFileName + "%"));
				}else{
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
				//conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, "LOCAL_FILE"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				if("/".equals(currentFolder)){
					dataResourceList = EntityQuery.use(delegator).select().from("DataResourceRoleShareList").where(condition).distinct().queryList();
				}else{
					dataResourceList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
				}
				//根据文件名查找文件
				if(!searchFileName.equals("") && null != dataResourceList && dataResourceList.size() > 0){
					resourceList = new ArrayList<GenericValue>();
					dataResourceList = searchFileByName(dataResourceList,searchFileName, delegator);
				}
			//文件夹“协作空间”、“本人分享”子文件夹的操作规则
			}else if(("他人分享".equals(type) || "本人分享".equals(type)) && !"/".equals(currentFolder)){
				GenericValue folder = new GenericValue();
				if(null != folderId){
					folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",folderId)).distinct().queryOne();
				}

				dir = new File(folder.get("parentObjectInfo").toString() + folder.get("dataResourceName").toString());
				String pathShare = dir.getPath().replace("\\","/")+"/";
				File[] subDirsList = dir.listFiles();
				if(null != subDirsList){
					for (File file : subDirsList){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataResourceName",file.getName());
						map.put("parentObjectInfo",pathShare);
						folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
						if(null != folder && folder.size() > 0){
							dataResourceList.add(folder);
						}
					}
				}
				//根据文件名查找文件
				if(!searchFileName.equals("") && null != dataResourceList && dataResourceList.size() > 0){
					resourceList = new ArrayList<GenericValue>();
					dataResourceList = searchFileByName(dataResourceList,searchFileName, delegator);
				}
			}else{
				//文件夹“个人文档”、“发送文件”、“接收文件”的操作规则
				dir = new File(path);
				GenericValue folder = new GenericValue();
				File[] subDirsList = dir.listFiles();
				if(null != subDirsList){
					for (File file : subDirsList){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataResourceName",file.getName());
//						if(!"个人文档".equals(type) && !"发送文件".equals(type) && !"接收文件".equals(type)){
//							map.put("dataResourceTypeId","LOCAL_FILE");
//						}
						map.put("parentObjectInfo",path);
						folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
						if(null != folder && folder.size() > 0 && !"协作空间".equals(type)){
							dataResourceList.add(folder);
							Map<String,Object> fileMap = new HashMap<String,Object>();
							//判断是文件夹还是文件
							if (folder.get("dataResourceTypeId").equals("LOCAL_FOLDER")){
								fileMap.put("isFolder", true);
								fileMap.put("fileSize",getSize(file));
								fileMap.put("data",FileUtils.parseLastModifDate(file));
							} else {
								fileMap.put("isFolder", false);
								fileMap.put("fileSize",getSize(file));
								fileMap.put("data",FileUtils.parseLastModifDate(file));
							}
							fileMap.put("path",filePath);
							fileMap.put("downloadPath",folder.get("parentObjectInfo"));
							fileMap.put("targetId", folder.get("dataResourceId"));
							fileMap.put("name",folder.get("dataResourceName"));

							list.add(fileMap);
						} else if (null != folder && folder.size() > 0 && "协作空间".equals(type)) {
							dataResourceList.add(folder);
						}
					}
				}


			//文件夹"协作空间"的操作规则
			if(null != type && "协作空间".equals(type) && "/".equals(currentFolder)){
				List<GenericValue> list1 = new ArrayList<GenericValue>();
				for(GenericValue dataResource : dataResourceList){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("dataId",dataResource.get("dataResourceId"));
					map.put("entityName","DataResource");
					List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblDataScope").where(map).queryList();
					String flag = "";
					if(null != partyList && partyList.size() > 0){
						String idList = "";
						for(GenericValue party : partyList){
							if("SCOPE_USER".equals(party.get("scopeType"))){
								idList = idList + party.get("scopeValue") + ",";
								if(partyId.equals(party.get("scopeValue"))){
									if("".equals(flag)){
										flag = party.get("dataAttr").toString();
									}else{
										if("view".equals(flag)){
											flag = party.get("dataAttr").toString();
										}
									}
								}
							}else{
								List<GenericValue> personList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department",party.get("scopeValue"))).queryList();
								for(GenericValue person : personList){
									idList = idList + person.get("partyId") + ",";
								}
								if("SCOPE_DEPT_LIKE".equals(party.get("scopeType"))){
									Map positionMap = UtilMisc.toMap("partyIdFrom", party.get("scopeValue"), "partyRelationshipTypeId", "GROUP_ROLLUP");
									List<GenericValue> members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
									if (UtilValidate.isNotEmpty(members)) {
										for (GenericValue member : members) {
											List<GenericValue> persons = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department",member.get("scopeValue"))).queryList();
											for(GenericValue person : persons){
												idList = idList + person.get("partyId") + ",";
											}
										}
									}
								}
								if(idList.indexOf(partyId+",") > 0){
									if("".equals(flag)){
										flag = party.get("dataAttr").toString();
									}else{
										if("view".equals(flag)){
											flag = party.get("dataAttr").toString();
										}
									}
								}
							}
						}
						if("".equals(flag)){
							flag = "view";
						}
						dataResource.put("statusId",flag);
						if(!idList.contains(partyId + ",")){
							list1.add(dataResource);
						}
					}
				}
				for(GenericValue genericValue : list1){
					dataResourceList.remove(genericValue);
				}
			}
				//根据文件名查找文件
				if(!searchFileName.equals("") && null != dataResourceList && dataResourceList.size() > 0){
					resourceList = new ArrayList<GenericValue>();
					dataResourceList = searchFileByName(dataResourceList,searchFileName, delegator);
					list.clear();
				} else if (!searchFileName.equals("")){
					dataResourceList.clear();
				}
			}


			//将从文件夹中获取的数据存放起来返回给客户端
			if(null != dataResourceList && dataResourceList.size() > 0){
				list.clear();
				for(GenericValue dataResource : dataResourceList){
					Map<String,Object> fileMap = new HashMap<String,Object>();
					//判断是文件夹还是文件
					if (dataResource.get("dataResourceTypeId").equals("LOCAL_FOLDER")){
						fileMap.put("isFolder", true);
						File file = new File((String)dataResource.get("parentObjectInfo"));
						fileMap.put("fileSize","0");
						fileMap.put("data",FileUtils.parseLastModifDate(file));
					} else {
						fileMap.put("isFolder", false);
						File file = new File((String)dataResource.get("parentObjectInfo"),(String)dataResource.get("dataResourceName"));
						fileMap.put("fileSize",getSize(file));
						fileMap.put("data",FileUtils.parseLastModifDate(file));
					}
					fileMap.put("path",filePath);
					fileMap.put("downloadPath",dataResource.get("parentObjectInfo"));
					fileMap.put("targetId", dataResource.get("dataResourceId"));
					fileMap.put("name",dataResource.get("dataResourceName"));

					list.add(fileMap);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return list;
	}

	/*
	*Author 邓贝贝
	*created 2016/1/15 10:19
	*function  根据文件名查找文件
	*/

	private static List<GenericValue> searchFileByName(List<GenericValue> dataResourceList, String searchFileName, Delegator delegator){

		List<GenericValue> list = new ArrayList<GenericValue>();
		GenericValue folder = new GenericValue();
		for(GenericValue dataResource : dataResourceList){
			Map<String,Object> fileMap = new HashMap<String,Object>();
			//判断是文件夹还是文件
			if (dataResource.get("dataResourceTypeId").equals("LOCAL_FOLDER")){

				File dir = new File(dataResource.get("parentObjectInfo").toString() + dataResource.get("dataResourceName").toString());
				String pathShare = dir.getPath().replace("\\","/")+"/";
				File[] subDirsList = dir.listFiles();
				if(null != subDirsList){
					for (File file : subDirsList){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataResourceName",file.getName());
						map.put("parentObjectInfo",pathShare);
						try {
							folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						if(null != folder && folder.size() > 0){
							list.add(folder);
						}
					}
				}
				//继续查找
				searchFileByName(list,searchFileName,delegator);
			} else {
				if (dataResource.get("dataResourceName").toString().contains(searchFileName)){
					resourceList.add(dataResource);
				}
		}

		}
		return resourceList;
	}


	private static IConfiguration getIConfigurationByType(Delegator delegator,HttpServletRequest request){
		IConfiguration configuration = null;
		try {
			Map loginMap = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", request.getParameter("USERNAME")));
			request.getSession().setAttribute("userLogin",loginMap);
			configuration = ConfigurationFactory.getInstace().getConfiguration(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration;
	}

	/*
	*Author 邓贝贝
	*created 2016/1/21 9:16
	*function   获取文件大小
	*/
	public static String getSize(File file) {
		if (file.length() > 0 && file.length() < BYTES) {
			return "1";
		} else {
			return String.valueOf(Math.round(file.length() / BYTES));
		}
	}

	//转换为json类型数据
	public static String returnJson(HttpServletRequest request, HttpServletResponse response, Object toJson,String module) {
		//转译成json
		String httpMethod = request.getMethod();
		Writer out = null;
		try {
			JSON json = JSON.from(toJson);
			String jsonStr = json.toString();
			if (jsonStr == null) {
				Debug.logError("JSON Object was empty; fatal error!", module);
				return EventUtil.returnError(request, "error");
			}
			if ("GET".equalsIgnoreCase(httpMethod)) {
				Debug.logWarning("for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
						+ "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
						+ "You might need to remove it if you use Ajax GET responses (not recommended)."
						+ "In case, the util.js scrpt is there to help you", module);
			}
			response.setContentType("application/x-json");
			response.setContentLength(jsonStr.getBytes("UTF8").length);
			out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			return EventUtil.returnSuccess(request,"true");
		} catch (IOException e) {
			Debug.logError(e, module);
			return EventUtil.returnError(request, "error");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
