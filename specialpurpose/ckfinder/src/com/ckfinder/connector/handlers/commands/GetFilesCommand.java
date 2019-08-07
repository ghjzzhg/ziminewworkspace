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

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.XmlAttribute;
import com.ckfinder.connector.data.XmlElementData;
import com.ckfinder.connector.errors.ConnectorException;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to handle
 * <code>GetFiles</code> command.
 */
public class GetFilesCommand extends XMLCommand {

	/**
	 * number of bytes in kilobyte.
	 */
	private static final float BYTES = 1024f;
	/**
	 * list of files.
	 */
	private List<Map<String,Object>> files;
	/**
	 * temporary field to keep full path.
	 */
	private String fullCurrentPath;

	private String parentFolderPermissions;

	/**
	 * initializing parameters for command handler.
	 *命令处理程序的初始化参数。
	 * @param request request
	 * @param configuration connector configuration
	 * @param params execute additional params.
	 * @throws ConnectorException when error occurs
	 */
	public void initParams(final HttpServletRequest request,
						   final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration);
	}

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createFilesData(rootElement);
		}
	}

	/**
	 * gets data to XML response.
	 * 场景1：从文件管理模块进入：进入私有文档；进入共享文档。进入子目录
	 * 场景2：从其它模块进入：所属人进入私有文档；非所属人进入共享文档。进入子目录
	 * 企业子账户与主账号文件管理统一为同一套数据，即子账户操作的文件实际为主账号的文件
	 * 共享目录查询共享给当前子账号或其主账号的（如果有module限制的要根据moduleType与moduleId过滤）
	 * 机构账户与子账户文件管理相互独立
	 * 取得文件夹下所有文件
	 * 需得到文件或者文件夹的权限点
	 * 1表示有权限，0表示无权限
	 * 一共六种111111，第一位：分享；第二位：重命名；第三位：删除；第四位：新增文件夹；第五位：新增文件，第六位：下载
	 * @return 0 if ok, otherwise error code
	 */
	protected int getDataForXml() throws GenericEntityException {
		String folderId = this.request.getParameter("folderId");
		String folderEntry = this.request.getParameter("defaultFolderId");//1 私有 或者 2 共享 3为CASE文档
		String otherModules = this.request.getParameter("otherModules");

		boolean fromOtherModule = UtilValidate.isNotEmpty(otherModules);//非文件管理模块进入
		boolean fromShareFolder = Constants.SHARE_FOLDER_ENTRY.equals(folderEntry);//从共享文档入口进入
		String loginUserGroupId = groupInfo.getString("partyId");

		GetFilesInterface getFileHandler = null;
		if(fromOtherModule){//文件管理模块进入
			if(fromShareFolder){
				getFileHandler = new GetFilesFromModuleShareEntry(delegator, folderId, loginUserPartyId, loginUserGroupId, isCompany, moduleType, moduleId, partyId);
			}else{
				getFileHandler = new GetFilesFromModulePrivateEntry(delegator, folderId, loginUserPartyId, loginUserGroupId, isCompany);
			}
		}else{
			if(Constants.CASE_FOLDER_ENTRY.equals(folderEntry)){
				getFileHandler = new GetFilesFromCaseEntry(delegator, folderId, loginUserPartyId, loginUserGroupId, isCompany);
			}else if(Constants.PRIVATE_FOLDER_ENTRY.equals(folderEntry)){
				getFileHandler = new GetFilesFromPrivateEntry(delegator, folderId, loginUserPartyId, loginUserGroupId, isCompany);
			}else{
				getFileHandler = new GetFilesFromShareEntry(delegator, folderId, loginUserPartyId, loginUserGroupId, isCompany);
			}
		}

		this.files = getFileHandler.getFiles();
		this.parentFolderPermissions = getFileHandler.getParentFolderPermission();
		//外模块筛选文件夹信息，只有当是根目录时才筛选
		/*if(UtilValidate.isNotEmpty(otherModules) && this.files.size() > 0 && folderEntry.equals("2") && folderId.equals("2")){
			List<Map<String , Object>> FileList = getOtherModulesFile(partyId);
			this.files.clear();
			if(FileList.size() > 0){
				this.files = FileList;
			}else{
				this.files = new ArrayList<>();
			}
		}*/
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	/**
	 * 得到名称列表
	 * @return
	 */
	public String getFileNameList(){
		String fileNameList = "";
		for(Map<String,Object> map : this.files){
			fileNameList += map.get("fileId");
		}
		return fileNameList;
	}

	/**
	 * 得到外模块所需的文件信息
	 * @param inputPartyId 外模块传入的用户ID
	 * @return 文件信息
     */
	public List<Map<String, Object>> getOtherModulesFile(String inputPartyId){
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		String userLoginId = userLogin.get("partyId").toString();
		List fileL = new ArrayList();//待传出的文件列表
		String filePathList = this.request.getParameter("filePathList");
		try {
			if(UtilValidate.isNotEmpty(filePathList) && !filePathList.equals("/")){
				String[] filePaths = filePathList.split(",");
				String filePath = "/" + inputPartyId;
				//遍历外模块传入的文件路径
				for(String filep : filePaths){
					String newFile = filePath + "/";
					String name = filep.substring(filep.lastIndexOf("/") + 1, filep.length());
					newFile += filep.substring(0, filep.lastIndexOf("/"));
					GenericValue genericValue = null;
					String fileflag = "";
					//区分文件夹和文件，查询的表不同
					if(name.indexOf(".") > 0){
						GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("objectInfo",newFile,"dataResourceName",name)).orderBy("createdDate DESC").queryOne();
						if(UtilValidate.isNotEmpty(fileInfo)){
							List list = new ArrayList();
							list.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, null));
							list.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, "1"));
							list.add(EntityCondition.makeCondition("fileId", EntityOperator.EQUALS, fileInfo.get("dataResourceId")));
							genericValue = EntityQuery.use(delegator).select().from("DataResourceFileList").where(list).queryOne();
						}
						fileflag = "fileId";
					}else{
						genericValue = EntityQuery.use(delegator).select().from("DataResourceFolderList").where(UtilMisc.toMap("folderPath",newFile,"folderName",name,"folderType","1")).queryOne();
						fileflag = "folderId";
					}
					String fullName = "管理员";
					//将查询出来的文件id进行筛选
					if(UtilValidate.isNotEmpty(genericValue)){
						Map<String, Object> map = new HashMap<>();
						if(fileflag.equals("folderId")){
							GenericValue shareGenericValue = EntityQuery.use(delegator).select().from("DataResourceFolderList").where(UtilMisc.toMap("partyId",userLoginId,"folderId",genericValue.get("folderId"),"folderType","2")).queryOne();
							if(UtilValidate.isNotEmpty(shareGenericValue) || userLoginId.equals(partyId)){
								map.put("fileId",genericValue.get("folderId"));
								map.put("fileName",genericValue.get("folderName"));
								map.put("fileType","folder");
								map.put("partyId",genericValue.get("partyId"));
								map.put("remarks",genericValue.get("foldeRemarks") != null ? genericValue.get("foldeRemarks") : "");
								if(userLoginId.equals(partyId)){
									map.put("filePermissions",genericValue.get("folderPermissions"));
								}else{
									map.put("filePermissions",shareGenericValue.get("folderPermissions"));
								}
								GenericValue folderInfo = EntityQuery.use(delegator).select().from("DataResourceFolderList").where(UtilMisc.toMap("folderId",genericValue.get("folderId") ,"folderType","1")).queryOne();
								map.put("remarks",folderInfo.get("foldeRemarks") != null ? folderInfo.get("foldeRemarks") : "");

								GenericValue party = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",folderInfo.get("createPartyId"))).queryOne();
								fullName = folderInfo.get("createFullName") != null ? folderInfo.get("createFullName").toString() : "管理员";
								if(party.get("partyTypeId").equals("PARTY_GROUP")){
									GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",folderInfo.get("createPartyId"))).queryOne();
									if(UtilValidate.isNotEmpty(partyGroup)){
										fullName = partyGroup.get("groupName").toString();
									}
								}
								String fileFullName = "";
								GenericValue partyInfo = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",folderInfo.get("partyId"))).queryOne();
								if(partyInfo.get("partyTypeId").equals("PARTY_GROUP")){
									GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",folderInfo.get("partyId"))).queryOne();
									fileFullName = partyGroup.get("groupName").toString();
								}else{
									GenericValue person = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",folderInfo.get("partyId"))).queryOne();
									if(UtilValidate.isNotEmpty(person.get("fullName"))){
										fileFullName = person.get("fullName").toString();
									}
								}
								map.put("fullName",fileFullName);
								map.put("createFullName",fullName);
								map.put("fileVersion", "");
								map.put("createTime", format.format(folderInfo.get("createFolderTime")));
								fileL.add(map);
							}
						}else{
							List list = new ArrayList();
							list.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, null));
							list.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, "2"));
							list.add(EntityCondition.makeCondition("fileId", EntityOperator.EQUALS, genericValue.get("fileId")));
							list.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginId));
							GenericValue shareGenericValue = EntityQuery.use(delegator).select().from("DataResourceFileList").where(list).queryOne();
							if(UtilValidate.isNotEmpty(shareGenericValue) || userLoginId.equals(partyId)) {
								map.put("fileId", genericValue.get("fileId"));
								map.put("fileName", genericValue.get("fileName"));
								map.put("fileType", "file");
								map.put("partyId", genericValue.get("partyId"));
								map.put("remarks", "");
								if(userLoginId.equals(partyId)){
									map.put("filePermissions",genericValue.get("folderPermissions"));
								}else{
									map.put("filePermissions",shareGenericValue.get("folderPermissions"));
								}
								list.clear();
								list.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, null));
								list.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, '1'));
								list.add(EntityCondition.makeCondition("fileId", EntityOperator.EQUALS, genericValue.get("fileId")));
								GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResourceFileList").where(list).queryOne();
								GenericValue party = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",fileInfo.get("createPartyId"))).queryOne();
								fullName = fileInfo.get("createFullName") != null ? fileInfo.get("createFullName").toString() : "管理员";
								if(party.get("partyTypeId").equals("PARTY_GROUP")){
									GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",fileInfo.get("createPartyId"))).queryOne();
									if(UtilValidate.isNotEmpty(partyGroup)){
										fullName = partyGroup.get("groupName").toString();
									}
								}
								String fileFullName = "";
								GenericValue partyInfo = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",fileInfo.get("partyId"))).queryOne();
								if(partyInfo.get("partyTypeId").equals("PARTY_GROUP")){
									GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",fileInfo.get("partyId"))).queryOne();
									fileFullName = partyGroup.get("groupName").toString();
								}else{
									GenericValue person = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",fileInfo.get("partyId"))).queryOne();
									if(UtilValidate.isNotEmpty(person.get("fullName"))){
										fileFullName = person.get("fullName").toString();
									}
								}
								map.put("fullName",fileFullName);
								map.put("createFullName",fullName);
								map.put("fileVersion",fileInfo.get("fileVersion") != null ? fileInfo.get("fileVersion") : "");
								map.put("remarks","");
								map.put("createTime", format.format(fileInfo.get("createFileTime")));
								fileL.add(map);
							}
						}
					}
				}
			}else{
				this.files = new ArrayList<>();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return fileL;
	}

	/**
	 * creates files data node in reponse XML.
	 *创建数据节点响应XML文件。
	 * @param rootElement root element from XML.
	 */
	private void createFilesData(final Element rootElement) {
		Element element = creator.getDocument().createElement("Files");

		XmlElementData folderData = new XmlElementData("folder");
		XmlAttribute parent = new XmlAttribute("parentFolderPermissions", parentFolderPermissions);
		folderData.getAttributes().add(parent);
		folderData.addToDocument(this.creator.getDocument(), element);
		if(UtilValidate.isNotEmpty(this.files)){
			for (Map<String,Object> filePath : files) {
				try {
					XmlElementData elementData = new XmlElementData("File");
					XmlAttribute attribute = new XmlAttribute("fileName", filePath.get("fileName").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("fileId", filePath.get("fileId").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("fileType", filePath.get("fileType").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("fullName", filePath.get("fullName").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("filePermissions", filePath.get("filePermissions").toString());
					elementData.getAttributes().add(attribute);
					List<GenericValue> fileHistory = EntityQuery.use(delegator).select().from("TblHistoryFiles").where(UtilMisc.toMap("dataResourceId",filePath.get("fileId").toString())).queryList();
					String fileHidtoryFlag = "0";
					if(UtilValidate.isNotEmpty(fileHistory) && fileHistory.size() > 0){
						fileHidtoryFlag = "1";
					}
					attribute = new XmlAttribute("fileHistory", fileHidtoryFlag);
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("partyId", filePath.get("partyId").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("remarks", filePath.get("remarks").toString());
					elementData.getAttributes().add(attribute);

					attribute = new XmlAttribute("createFullName",  filePath.get("createFullName").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("fileVersion", filePath.get("fileVersion").toString());
					elementData.getAttributes().add(attribute);
					attribute = new XmlAttribute("createTime", filePath.get("createTime").toString());
					elementData.getAttributes().add(attribute);
					elementData.addToDocument(this.creator.getDocument(), element);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		rootElement.appendChild(element);
	}
}
