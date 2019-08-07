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
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ckfinder.connector.utils.FileUtils;
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
import com.ckfinder.connector.utils.XMLCreator;

/**
 * Base class to handle XML commands.
 */
public abstract class XMLCommand extends Command {

	/**
	 * util to create XML document.
	 */
	protected XMLCreator creator;

	private String fileBaseUrl = "";

	/**
	 * sets response headers for XML response.
	 *
	 * @param response response
	 * @param sc servlet context
	 */
	public void setResponseHeader(final HttpServletResponse response,
			final ServletContext sc) {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("utf-8");
	}

	/**
	 * executes XML command. Creates XML response and writes it to response
	 * output stream.
	 *
	 * @param out response output stream
	 * @throws ConnectorException to handle in error handler.
	 */
	public void execute(final OutputStream out) throws ConnectorException {
		try {
			createXMLResponse(getDataForXml());
			out.write(creator.getDocumentAsText().getBytes("UTF-8"));
		} catch (ConnectorException e) {
			throw e;
		} catch (IOException e) {
			throw new ConnectorException(Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * abstract method to create XML response in command.
	 * 抽象方法来创建XML响应命令。
	 * @param errorNum error code from method getDataForXml()
	 * @throws ConnectorException to handle in error handler.
	 */
	private void createXMLResponse(final int errorNum) throws ConnectorException, GenericEntityException {
		if (configuration.isDebugMode() && this.exception != null) {
			throw new ConnectorException(this.exception);
		}
		Element rootElement = creator.getDocument().createElement("Connector");
		if (this.type != null && !type.equals("")) {
			rootElement.setAttribute("resourceType", this.type);
		}
		if (mustAddCurrentFolderNode()) {
			createCurrentFolderNode(rootElement);
		}
		creator.addErrorCommandToRoot(rootElement, errorNum, getErrorMsg(errorNum));
		createXMLChildNodes(errorNum, rootElement);
		creator.getDocument().appendChild(rootElement);
	}

	/**
	 * gets error message if needed.
	 *
	 * @param errorNum error code
	 * @return error message
	 */
	protected String getErrorMsg(final int errorNum) {
		return null;
	}

	/**
	 * abstract method to create XML nodes for commands.
	 *
	 * @param errorNum error code
	 * @param rootElement XML root node
	 * @throws ConnectorException to handle in error handler.
	 */
	protected abstract void createXMLChildNodes(final int errorNum,
			final Element rootElement)
			throws ConnectorException, GenericEntityException;

	/**
	 * gets all necessary data to create XML response.
	 *
	 * @return error code {@link com.ckfinder.connector.configuration.Constants.Errors}
	 * or
	 * {@link com.ckfinder.connector.configuration.Constants.Errors#CKFINDER_CONNECTOR_ERROR_NONE}
	 * if no error occurred.
	 */
	protected abstract int getDataForXml() throws GenericEntityException;

	/**
	 * creates
	 * <code>CurrentFolder</code> element.
	 *
	 * @param rootElement XML root node.
	 */
	protected void createCurrentFolderNode(final Element rootElement) {
		Element element = creator.getDocument().createElement("CurrentFolder");
		element.setAttribute("path", this.currentFolder);
		element.setAttribute("url", configuration.getTypes().get(this.type).getUrl()
				+ this.currentFolder);
		element.setAttribute("acl", String.valueOf(AccessControlUtil.getInstance(configuration).checkACLForRole(this.type,
				this.currentFolder, this.userRole)));
		rootElement.appendChild(element);
	}

	@Override
	public void initParams(final HttpServletRequest request,
			final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration, params);
		creator = new XMLCreator();
		creator.createDocument();
	}

	/**
	 * whether
	 * <code>CurrentFolder</code> element should be added to the XML response.
	 *
	 * @return true if must.
	 */
	protected boolean mustAddCurrentFolderNode() {
		if( this.type != null && this.currentFolder != null )
			return true;
		return false;
	}

	public Integer searchFileNumber(String path,String key) throws GenericEntityException {
		return searchFileList(path,key,true).size();
	}

	public List<Map<String,Object>> searchFileList(String typePath,String key,Boolean folderFlag){
		List<Map<String,Object>> files = new ArrayList<Map<String,Object>>();
		try {
			File dir = null;
			List<GenericValue> dataResourceList = new ArrayList<GenericValue>();
			if(("他人分享".equals(key) || "本人分享".equals(key))  && !UtilValidate.isNotEmpty(this.folderId)){
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				if("他人分享".equals(key)){
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, partyId));
				}else{
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
//				conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, "LOCAL_FILE"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				dataResourceList = EntityQuery.use(delegator).select().from("DataResourceRoleShareList").where(condition).distinct().queryList();
			}else if(("他人分享".equals(key) || "本人分享".equals(key)) && UtilValidate.isNotEmpty(this.folderId)){
				GenericValue folder = new GenericValue();
				if(UtilValidate.isNotEmpty(this.folderId) && !"undefined".equals(this.folderId)){
					folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",this.folderId)).distinct().queryOne();
					FileFilter fileFilter = new FileFilter() {
						public boolean accept(File file) {
							return !file.isDirectory();
						}
					};
					dir = new File(disc + folder.get("parentObjectInfo").toString() + folder.get("dataResourceName").toString());
					String path = dir.getPath().replace("\\","/")+"/";
					File[] subDirsList = dir.listFiles(fileFilter);
					if(null != subDirsList){
						for (File file : subDirsList){
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("dataResourceName",file.getName());
//							map.put("dataResourceTypeId","LOCAL_FILE");
							map.put("parentObjectInfo",path);
							folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
							if(null != folder && folder.size() > 0){
								folder.put("statusId","all");
								dataResourceList.add(folder);
							}
						}
					}
				}

			}else{
				GenericValue folder = new GenericValue();
				FileFilter fileFilter = new FileFilter() {
					public boolean accept(File file) {
						return !file.isDirectory();
					}
				};
				String path = typePath.replace("\\","/");
				dir = new File(disc + path);
	//				File[] subDirsList = dir.listFiles(fileFilter);
				File[] subDirsList = dir.listFiles();
				if(this.currentFolder != null){
					fileBaseUrl = configuration.getTypes().get(key).getUrl()
							+ this.currentFolder;
				}else{
					fileBaseUrl = configuration.getTypes().get(key).getUrl() + "/";
				}

				if(null != subDirsList){
					for (File file : subDirsList){
						List conditionList = new ArrayList();
						conditionList.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.EQUALS, file.getName()));
						conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, fileBaseUrl.replace("\\", "/")));
						conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType + "_FOLDER"),
								EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType + "_FILE")), EntityOperator.OR));
						EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
						folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(condition)).distinct().queryOne();

						if(null != folder && folder.size() > 0){
							dataResourceList.add(folder);
						}
					}
				}
			}
			if("他人分享".equals(key)){
				List<GenericValue> dataList = dataResourceList;
				for(int i = 0; i < dataList.size() ; i++){
					GenericValue genericValue = dataList.get(i);
					List<String> partyIds = new ArrayList<String>();
					if(!UtilValidate.isNotEmpty(genericValue.get("statusId")) && !"all".equals(genericValue.get("statusId"))){
						String type = genericValue.get("roleType").toString();
						if("SCOPE_USER".equals(type)){
							partyIds.add(genericValue.get("rolePartyId").toString());
						}else {
							List<GenericValue> personList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department", genericValue.get("partyId"))).queryList();
							for (GenericValue person : personList) {
								partyIds.add(person.get("rolePartyId").toString());
							}
							if ("SCOPE_DEPT_LIKE".equals(type)) {
								Map positionMap = UtilMisc.toMap("partyIdFrom", type, "partyRelationshipTypeId", "GROUP_ROLLUP");
								List<GenericValue> members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
								if (UtilValidate.isNotEmpty(members)) {
									for (GenericValue member : members) {
										List<GenericValue> persons = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department", member.get("scopeValue"))).queryList();
										for (GenericValue person : persons) {
											partyIds.add(person.get("partyId").toString());
										}
									}
								}
							}
						}
						if(!partyIds.contains(partyId)){
							dataResourceList.remove(genericValue);
						}
					}
				}
			}
			if(null != key && "协作空间".equals(key)){
				List<GenericValue> list = new ArrayList<GenericValue>();
				for(GenericValue dataResource : dataResourceList){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("dataId",dataResource.get("dataResourceId"));
					map.put("entityName","DataResource");
					List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblDataScope").where(map).queryList();
					String flag = "";
					if(null != partyList && partyList.size() > 0){
						String idList = "";
						for(GenericValue party : partyList){
							String scopeValue = party.get("scopeType").toString();
							if("SCOPE_USER".equals(scopeValue)){
								idList = idList + party.get("scopeValue") + ",";
								if(idList.contains(partyId + ",")){
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
								if("SCOPE_DEPT_LIKE".equals(scopeValue)){
									Map positionMap = UtilMisc.toMap("partyIdFrom", scopeValue, "partyRelationshipTypeId", "GROUP_ROLLUP");
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
								if(idList.contains(partyId + ",")){
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
							list.add(dataResource);
						}
					}
				}
				for(GenericValue genericValue : list){
					dataResourceList.remove(genericValue);
				}
			}
			List<GenericValue> dataList = dataResourceList;
			dataResourceList = new ArrayList<GenericValue>();
			List<String> dataIdList = new ArrayList<String>();
			for(int i = 0; i< dataList.size(); i++){
				GenericValue genericValue = dataList.get(i);
				if(!dataIdList.contains(genericValue.get("dataResourceId").toString())){
					dataResourceList.add(genericValue);
					dataIdList.add(genericValue.get("dataResourceId").toString());
				}
			}
			if(UtilValidate.isNotEmpty(dataResourceList)){
				for(GenericValue dataResource : dataResourceList){
					File file = new File(disc + dataResource.get("parentObjectInfo").toString() + dataResource.get("dataResourceName"));
					if(!file.exists()){
						delFile(dataResource.get("dataResourctId").toString());
						continue;
					}
					//在数据库中查找当前登陆人点击type下的所有文件
					files = FileUtils.findChildrensList(dataResource, false,files);
				}
			}else{
				files = new ArrayList<Map<String,Object>>();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return files;
	}

	public void delFile(String  fileId){
		try {
			delegator.removeByAnd("TblFileScope", UtilMisc.toMap("dataId", fileId,"entityName", "DataResource"));
			delegator.removeByAnd("TblDataScope", UtilMisc.toMap("dataId", fileId,"entityName", "DataResource"));
			delegator.removeByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", fileId));
			delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	public List<File> showAllFiles(File dir,List<File> fileList) throws Exception{
		File[] fs = dir.listFiles();
		if(null != fs && fs.length > 0) {
			for (File file : fs) {
				fileList.add(file);
				if (file.isDirectory()) {
					showAllFiles(file, fileList);
				}
			}
		}
		return fileList;
	}

	public void deleteFile(final File destFile) throws Exception {
		File dir = destFile;
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		String path = dir.getPath().substring(0, dir.getPath().lastIndexOf(dir.getName())).replace("\\","/");
		conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path));
		conditionList.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.EQUALS, dir.getName()));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
		List<GenericValue> folderList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
		String fileId = folderList.get(0).get("dataResourceId").toString();
		//删除有分享记录的文件
		List<GenericValue> roleList = EntityQuery.use(delegator).select().from("DataResourceRole").where(UtilMisc.toMap("dataResourceId", fileId)).queryList();
		if(UtilValidate.isNotEmpty(roleList)){
			for(GenericValue role : roleList){
				String filePath = role.get("sharedPath").toString();
				File files = new File(disc + filePath);
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
				DeleteFolderCommand.deleteSharedFile(newTypePath + destFile.getName());
			}
			newPath = newPath.substring(th.length()+1,newPath.length());
		}
		delFile(fileId);
	}

	public void deleteFolder(final File destFile) throws Exception {
		File dir = destFile;
		//删除文件夹下有分享记录的文件或者文件夹
		String path1 = dir.getPath().replace("\\", "/");
		List<EntityCondition> conditionList1 = new ArrayList<EntityCondition>();
		conditionList1.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.LIKE, "%" + path1 + "%"));
		EntityConditionList condition1 = EntityCondition.makeCondition(UtilMisc.toList(conditionList1));
		List<GenericValue> list = EntityQuery.use(delegator).select().from("DataResource").where(condition1).distinct().queryList();
		delShareFile(list);
		delShareFolder(path1);
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		String path = dir.getPath().substring(0, dir.getPath().lastIndexOf(dir.getName())).replace("\\","/");
		conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path));
		conditionList.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.EQUALS, dir.getName()));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
		List<GenericValue> folderList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
		removeFlderList(folderList,dir);
	}

	public void removeFlderList(List<GenericValue> folderList,File dir) throws Exception {
		for(GenericValue folder : folderList){
			File file = new File(disc + folder.get("parentObjectInfo").toString()+folder.get("dataResourceName"));
			List<File> files = new ArrayList<File>();
			List<File> fileList = showAllFiles(file, files);
			for(File fi : fileList){
				Map<String,Object> map1 = new HashMap<String, Object>();
				map1.put("dataResourceName",fi.getName());
				String filePath = fi.getPath();
				String newFilePath = filePath.substring(0,filePath.lastIndexOf(File.separator));
				newFilePath = newFilePath.replace("\\","/") + "/";
				map1.put("parentObjectInfo",newFilePath);
				GenericValue fileModel = EntityQuery.use(delegator).select().from("DataResource").where(map1).distinct().queryOne();
				if(null != fileModel){
					delFile(fileModel.get("dataResourceId").toString());
				}
			}
			if(UtilValidate.isNotEmpty(folder)){
				delFile(folder.get("dataResourceId").toString());
			}
			if(null != folder.get("parentObjectInfo") && !folder.get("parentObjectInfo").toString().contains(dir.getPath().toString())){
				folderList.remove(folder);
			}
		}
	}

	public void delShareFile(List<GenericValue> list) throws GenericEntityException {
		for(GenericValue g : list){
			List<GenericValue> roleList = EntityQuery.use(delegator).select()
					.from("DataResourceRole")
					.where(UtilMisc.toMap("dataResourceId", g.get("dataResourceId")))
					.queryList();
			if(UtilValidate.isNotEmpty(roleList)){
				for(GenericValue role : roleList){
					String filePath = role.get("sharedPath").toString();
					if(g.get("dataResourceTypeId").equals(dataType + "_FOLDER")){
						DeleteFolderCommand.deleteDirectory(filePath);
					}else {
						DeleteFolderCommand.deleteSharedFile(filePath);
					}
				}
			}
		}
	}

	public void delShareFolder(String path1) throws GenericEntityException {
		//删除分享文件夹中的文件夹
		String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
		String newPath = path1.substring(typePath.length()+1, path1.length()).replace("\\","/");//currentFolder
		String[] paths = newPath.split("/");
		String partPath = "/";
		searchSubPath(paths,partPath,typePath,newPath);
	}

	public void searchSubPath(String [] paths, String partPath, String typePath, String newPath) throws GenericEntityException {
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
				String newTypePath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + newPath;
				newTypePath = newTypePath.replace("\\","/");
				DeleteFolderCommand.deleteDirectory(newTypePath);
			}
			if(th.length() == newPath.length()){
				break;
			}
			newPath = newPath.substring(th.length()+1,newPath.length());
		}
	}


	/**
	 * Handles overwrite option.
	 *
	 * @param sourceFile source file to move from.
	 * @param destFile destenation file to move to.
	 * @return true if moved correctly
	 * @throws IOException when ioerror occurs
	 */
	public Map handleOverwrite(final File sourceFile, final File destFile)
			throws IOException, GenericEntityException {
		String oldPath = sourceFile.getPath();
		String newPath = destFile.getPath();
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> fileMap = new HashMap<String,Object>();
		String sourcePath = sourceFile.getParent().replace("\\","/")+"/";
		fileMap.put("parentObjectInfo",sourcePath);
		fileMap.put("dataResourceName",sourceFile.getName());
		GenericValue fileValue = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(fileMap)).queryOne();
		if((dataType + "_FOLDER").equals(fileValue.get("dataResourceTypeId"))) {
			//文件夹:先删除(包括数据库,分享部分)，再复制
			try{
				deleteFolder(destFile);
			}catch (Exception e){
				e.printStackTrace();
			}
			DeleteFolderCommand.deleteDirectory(newPath);
			map.put("flag",FileService.copyFolder(oldPath, newPath));
			map.put("newDestFile",destFile);
			map.put("dataResourceTypeId",dataType + "_FOLDER");
		}else {
			//文件:先删除，再复制
			try{
				deleteFile(destFile);
			}catch (Exception e){
				e.printStackTrace();
			}
			File destThumbFile = new File(configuration.getThumbsPath() + File.separator
					+ this.type
					+ this.currentFolder, destFile.getName());
			if(DeleteFolderCommand.deleteSharedFile(newPath)){
				FileUtils.delete(destThumbFile);
			}
			map.put("flag",FileUtils.copyFromSourceToDestFile(sourceFile, destFile, false, configuration));
			map.put("newDestFile",destFile);
			map.put("dataResourceTypeId",dataType + "_FILE");
		}
		return map;
	}

	public void saveDataResource(File file) throws GenericEntityException {
		Map<String,Object> map = new HashMap<String,Object>();
		String resourceId = delegator.getNextSeqId("DataResource");//获取主键ID
		map.put("dataResourceId",resourceId);
		map.put("dataResourceTypeId",dataType + "_FILE");
		map.put("dataResourceName",file.getName());
		map.put("partyId",partyId);
		String newFolder = file.getParent().replace("\\","/") + "/";
		map.put("parentObjectInfo",newFolder);
		map.put("objectInfo",newFolder + file.getName());
		GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
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
	}
}
