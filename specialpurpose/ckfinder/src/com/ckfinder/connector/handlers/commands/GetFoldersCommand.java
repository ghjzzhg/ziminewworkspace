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
import com.ckfinder.connector.data.XmlAttribute;
import com.ckfinder.connector.data.XmlElementData;
import com.ckfinder.connector.errors.ConnectorException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

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
	private List<Map<String,Object>> directories = new ArrayList<>();
	private List<String> directoriesL;
	private String parentFolderId;

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
	protected int getDataForXml() throws GenericEntityException {
		String folderId = this.request.getParameter("folderId");
		parentFolderId = folderId;
		String folderEntry = this.request.getParameter("defaultFolderId");//1 私有 或者 2 共享 3为CASE文档
		boolean fromShareFolder = Constants.SHARE_FOLDER_ENTRY.equals(folderEntry);//从共享文档入口进入

		List conditionList = new ArrayList();
		List searchParty = new ArrayList();
		searchParty.add(loginUserPartyId);
		if(isCompany){//企业子账号与主账号共用文档管理
			String loginUserGroupId = groupInfo.getString("partyId");
			searchParty.add(loginUserGroupId);
		}

		boolean topFolder = Constants.PRIVATE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录
		String searchFolderId = Constants.PRIVATE_FOLDER_ENTRY.equals(folderId) ? null : folderId;
		conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
		conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
		List<GenericValue> folderList = EntityQuery.use(delegator).from("DataResourceFolderList").where(conditionList).orderBy("folderName").orderBy("createFolderTime DESC").queryList();
		//遍历文件夹和文件信息，使文件夹和文件在同已页面中显示
		for(GenericValue folder : folderList) {
			Map<String, Object> map = new HashMap<>();
			map.put("folderId", folder.get("folderId"));
			String folderName = folder.getString("folderName");
			map.put("folderName", folderName);
			directories.add(map);
		}
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}


	/**
	 * creates folder data node in XML document.
	 *
	 * @param rootElement root element in XML document
	 */
	private void createFoldersData(final Element rootElement) throws GenericEntityException{
		Element element = creator.getDocument().createElement("Folders");
		for (Map<String,Object> dirPath : directories) {
			XmlElementData xmlElementData = new XmlElementData("Folder");
			xmlElementData.getAttributes().add(new XmlAttribute("name", dirPath.get("folderName").toString()));
			xmlElementData.getAttributes().add(new XmlAttribute("id", dirPath.get("folderId").toString()));
			xmlElementData.getAttributes().add(new XmlAttribute("parent", parentFolderId));
			xmlElementData.addToDocument(creator.getDocument(), element);
		}
		rootElement.appendChild(element);
	}
}
