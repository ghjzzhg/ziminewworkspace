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

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.XmlAttribute;
import com.ckfinder.connector.data.XmlElementData;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle
 * <code>GetFolders</code> command.
 */
public class GetFoldersCommand extends XMLCommand {

	/**
	 * list of subdirectories in directory.
	 */
	private List<Map<String,Object>> directories;
	private List<String> directoriesL;

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException, GenericEntityException {

		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createFoldersData(rootElement);
		}
	}

	/**
	 * gets data for response.
	 *
	 * @return 0 if everything went ok or error code otherwise
	 */
	@Override
	protected int getDataForXml() {
		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}

		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(this.type,
				this.currentFolder,
				this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}
		if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}
		try {
			File dir = null;
			List<GenericValue> dataResourceList = new ArrayList<GenericValue>();

			if(null != this.type && ("他人分享".equals(this.type) || "本人分享".equals(this.type)) && "/".equals(this.currentFolder)){
				//先查找文件夹下的文件夹，然后在去数据库中查找对应的ID，如果数据库中不存在则不显示
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				if("他人分享".equals(this.type)){
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, partyId));
				}else{
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
				conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType +"_FOLDER"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				dataResourceList = EntityQuery.use(delegator).select().from("DataResourceRoleShareList").where(condition).distinct().queryList();
				if("他人分享".equals(type)){
					this.setDataResourceList(dataResourceList);
				}
			}else if ( ("他人分享".equals(this.type) || "本人分享".equals(this.type)) && !"/".equals(this.currentFolder)){
				GenericValue folder = new GenericValue();
				if(null != this.folderId){
					folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",this.folderId)).distinct().queryOne();
				}
				FileFilter fileFilter = new FileFilter() {
					public boolean accept(File file) {
						return file.isDirectory();
					}
				};
				dir = new File(folder.get("parentObjectInfo").toString() + folder.get("dataResourceName").toString());
				String path = dir.getPath().replace("\\","/")+"/";
				File[] subDirsList = dir.listFiles(fileFilter);
				if(null != subDirsList){
					for (File file : subDirsList){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataResourceName",file.getName());
						map.put("dataResourceTypeId",dataType +"_FOLDER");
						map.put("parentObjectInfo",path);
						folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
						if(null != folder && folder.size() > 0){
							dataResourceList.add(folder);
						}
					}
				}
			}else{
				String fullCurrentPath = configuration.getTypes().get(this.type).getPath()
						+ this.currentFolder;
				dir = new File(fullCurrentPath);
				String path  = dir.getPath();
				path = path.replace("\\","/") + "/";
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path));
				conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType +"_FOLDER"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				dataResourceList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
			}
			if(null != this.type && "协作空间".equals(this.type)  && "/".equals(this.currentFolder)){
				List<GenericValue> list = new ArrayList<GenericValue>();
				for(GenericValue dataResource : dataResourceList){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("dataId",dataResource.get("dataResourceId"));
					map.put("entityName","DataResource");
					List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblDataScope").where(EntityCondition.makeCondition(map)).queryList();
					String flag = "";
					if(null != partyList && partyList.size() > 0){
						String idList = "";
						for(GenericValue party : partyList){
							if("SCOPE_USER".equals(party.get("scopeType"))){
								idList = idList + party.get("scopeValue") + ",";
								if(this.partyId.equals(party.get("scopeValue"))){
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
									List data = FastList.newInstance();
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
						dataResource.put("statusId",flag);
						if(idList.indexOf(partyId+",") < 0){
							list.add(dataResource);
						}
					}
				}
				for(GenericValue genericValue : list){
					dataResourceList.remove(genericValue);
				}
			}
			if(null != dataResourceList && dataResourceList.size() > 0){
				for(GenericValue dataResource : dataResourceList){
					String newpath = dataResource.get("parentObjectInfo").toString() + dataResource.get("dataResourceName").toString();
					dir = new File(newpath);
					if (!dir.exists()) {
						delFile(dataResource.get("dataResourceId").toString());
						return Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND;
					}
					//在数据库中查找当前登陆人点击type下的所有文件
					directories = FileUtils.findFolderChildrensList(dataResource, true,directories,statusId);
				}
			}else{
				directories = new ArrayList<Map<String,Object>>();
			}
		} catch (SecurityException e) {
			if (configuration.isDebugMode()) {
				throw e;
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		filterListByHiddenAndNotAllowed();
//		Collections.sort(directories);
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	/**
	 * filters list and check if every element is not hidden and have correct
	 * ACL.
	 */
	private void filterListByHiddenAndNotAllowed() {
		List<Map<String,Object>> tmpDirs = new ArrayList<Map<String,Object>>();
		for (Map dir : this.directories) {
			if (AccessControlUtil.getInstance(this.configuration).checkFolderACL(this.type, this.currentFolder + dir,
					this.userRole,
					AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW)
					&& !FileUtils.checkIfDirIsHidden(dir.get("folderName").toString(), this.configuration)) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("folderName", dir.get("folderName"));
				map.put("folderId",dir.get("folderId"));
				map.put("newPath",dir.get("newPath"));
				map.put("statusId",dir.get("statusId"));
				tmpDirs.add(dir);
			}

		}

		this.directories.clear();
		this.directories.addAll(tmpDirs);

	}

	/**
	 * creates folder data node in XML document.
	 *
	 * @param rootElement root element in XML document
	 */
	private void createFoldersData(final Element rootElement) throws GenericEntityException{
		Element element = creator.getDocument().createElement("Folders");
		for (Map<String,Object> dirPath : directories) {
			File dir = new File(dirPath.get("newPath").toString()
					+ dirPath.get("folderName").toString());
			this.folderId = dirPath.get("folderId").toString();
			Integer num = searchFileNumber(dir.getPath(),this.type);
			dir = new File(disc + dirPath.get("newPath").toString()
					+ dirPath.get("folderName").toString());
			if (dir.exists()) {
				XmlElementData xmlElementData = new XmlElementData("Folder");
				xmlElementData.getAttributes().add(new XmlAttribute("name", dirPath.get("folderName").toString()));
				xmlElementData.getAttributes().add(new XmlAttribute("num", num.toString()));
				xmlElementData.getAttributes().add(new XmlAttribute("id", dirPath.get("folderId").toString()));
				xmlElementData.getAttributes().add(new XmlAttribute("statusId", dirPath.get("statusId").toString()));
				xmlElementData.getAttributes().add(new XmlAttribute("hasChildren",
						this.hasChildren(this.currentFolder + dirPath.get("folderName") + "/", dir, configuration, this.type, this.userRole, partyId, delegator, "folder", folderId).toString()));
				String acl = "255";
				if("本人分享".equals(this.type) || "他人分享".equals(this.type)){
					acl = "17";
				}
				if("协作空间".equals(this.type)){
					if("view".equals(dirPath.get("statusId"))){
						acl = "17";
					}else{
						acl = "255";
					}
				}
				if("发送文件".equals(this.type) || "接收文件".equals(this.type)){
					acl = "223";
				}
				xmlElementData.getAttributes().add(new XmlAttribute("acl", acl));
//				xmlElementData.getAttributes().add(new XmlAttribute("acl",String.valueOf(AccessControlUtil.getInstance(configuration).checkACLForRole(this.type,this.currentFolder+ dirPath.get("folderName"),this.userRole))));
				xmlElementData.addToDocument(creator.getDocument(), element);
			}
		}
		rootElement.appendChild(element);
	}

	public Boolean hasChildren(String dirPath, File dir, IConfiguration configuration, String resourceType, String currentUserRole, String partyId, Delegator delegator, String flag, String folderId) throws GenericEntityException {
		if("type".equals(flag)){
			GenericValue folder = new GenericValue();
			if(null != folderId){
				folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",folderId)).distinct().queryOne();
			}
			List<GenericValue> dataResourceList = new ArrayList<GenericValue>();
			if(null != resourceType && ("他人分享".equals(resourceType) || "本人分享".equals(resourceType)) && "/".equals(dirPath)){
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				if("他人分享".equals(resourceType)){
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, partyId));
				}else{
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
				conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType +"_FOLDER"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				dataResourceList = EntityQuery.use(delegator).from("DataResourceRoleShareList").where(condition).distinct().queryList();
				if("他人分享".equals(resourceType)){
					this.setDataResourceList(dataResourceList);
				}
			}else if ( ("他人分享".equals(resourceType) || "本人分享".equals(resourceType)) && !"/".equals(dirPath)){
				FileFilter fileFilter = new FileFilter() {
					public boolean accept(File file) {
						return file.isDirectory();
					}
				};
				dir = new File(folder.get("parentObjectInfo").toString() + folder.get("dataResourceName").toString());
				File[] subDirsList = dir.listFiles(fileFilter);
				if(null != subDirsList){
					for (File file : subDirsList){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataResourceName",file.getName());
						map.put("parentObjectInfo",dir.getPath().replace("\\","/"));
						folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
						dataResourceList.add(folder);
					}
				}
			}else{
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				String path  = dir.getPath();
				path = path.replace("\\","/") + "/";
				conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path));
				conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType +"_FOLDER"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				dataResourceList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
				if("协作空间".equals(resourceType)  && "/".equals(this.currentFolder)){
					List<GenericValue> list = new ArrayList<GenericValue>();
					for(GenericValue dataResource : dataResourceList){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataId",dataResource.get("dataResourceId"));
						map.put("entityName","DataResource");
						List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblDataScope").where(map).queryList();
						if(null != partyList && partyList.size() > 0){
							String idList = "";
							for(GenericValue party : partyList){
								if("SCOPE_USER".equals(party.get("scopeType"))){
									idList = idList + party.get("scopeValue") + ",";
								}else{
									List<GenericValue> personList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department",party.get("scopeValue"))).queryList();
									for(GenericValue person : personList){
										idList = idList + person.get("partyId") + ",";
									}
									if("SCOPE_DEPT_LIKE".equals(party.get("scopeType"))){
										Map positionMap = UtilMisc.toMap("partyIdFrom", party.get("scopeValue"), "partyRelationshipTypeId", "GROUP_ROLLUP");
										List<GenericValue> members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
										List data = FastList.newInstance();
										if (UtilValidate.isNotEmpty(members)) {
											for (GenericValue member : members) {
												List<GenericValue> persons = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department",member.get("scopeValue"))).queryList();
												for(GenericValue person : persons){
													idList = idList + person.get("partyId") + ",";
												}
											}
										}
									}
								}
							}
							if(idList.indexOf(partyId+",") < 0){
								list.add(dataResource);
							}
						}
					}
					for(GenericValue genericValue : list){
						dataResourceList.remove(genericValue);
					}
				}
			}
			if (dataResourceList != null) {
				for (GenericValue file : dataResourceList) {
					String subDirName = file.get("dataResourceName").toString();
					if (!FileUtils.checkIfDirIsHidden(subDirName, configuration)
							&& AccessControlUtil.getInstance(configuration).checkFolderACL(resourceType,
							dirPath + subDirName, currentUserRole, AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW)) {
						return true;
					}
				}
			}
			return false;
		}else{
			List<Map<String,Object>> dataResourceList = new ArrayList<Map<String,Object>>();
			FileFilter fileFilter = new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory();
				}
			};
			File[] subDirsList = dir.listFiles(fileFilter);
			for(File file : subDirsList){
				Map<String,Object> dataResource = new HashMap<String,Object>();
				dataResource.put("dataResourceName", file.getName());
				dataResourceList.add(dataResource);
			}
			if (dataResourceList != null) {
				for (Map<String,Object> file : dataResourceList) {
					String subDirName = file.get("dataResourceName").toString();
					if (!FileUtils.checkIfDirIsHidden(subDirName, configuration)
							&& AccessControlUtil.getInstance(configuration).checkFolderACL(resourceType,
							dirPath + subDirName, currentUserRole, AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public void setDataResourceList (List<GenericValue> dataResourceList) throws GenericEntityException {
		List<GenericValue> removeList = new ArrayList<GenericValue>();
		if(null != dataResourceList && dataResourceList.size() > 0){
			for(GenericValue dataResource : dataResourceList){
				List<GenericValue> dataResourceRoleList = EntityQuery.use(delegator).select().from("DataResourceRole").where(EntityCondition.makeCondition("dataResourceId",dataResource.get("dataResourceId"))).queryList();
				String partys = "";
				for(GenericValue dataResourceRole :dataResourceRoleList){
					GenericValue party = EntityQuery.use(delegator).select().from("Party").where(EntityCondition.makeCondition("partyId", dataResourceRole.get("partyId"))).queryOne();
					if("PERSON".equals(party.get("partyTypeId"))){
						partys = partys + party.get("partyId") + ",";
					}else{
						List<GenericValue> personList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department",party.get("partyId"))).queryList();
						for(GenericValue person :personList){
							partys = partys + person.get("partyId") + ",";
						}
					}
				}
				if(partys.indexOf(partyId+",") < 0){
					removeList.add(dataResource);
				}
			}
			if(null != removeList){
				for(GenericValue genericValue : removeList){
					dataResourceList.remove(genericValue);
				}
			}
		}
	}
}
