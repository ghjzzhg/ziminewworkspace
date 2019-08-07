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
import com.ckfinder.connector.data.InitCommandEventArgs;
import com.ckfinder.connector.errors.ConnectorException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle
 * <code>Init</code> command.
 */
public class InitCommand extends XMLCommand {

	/**
	 * method from super class - not used in this command.
	 *
	 * @return 0
	 */
	@Override
	protected int getDataForXml() {
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	@Override
	protected void createXMLChildNodes(final int errorNum,
									   final Element rootElement)
			throws ConnectorException {
		try {
			createResouceTypesData(rootElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		createPluginsData(rootElement);
	}

	/**
	 * Creates plugins node in XML.
	 *
	 * @param rootElement root element in XML
	 * @throws ConnectorException when error in event handler occurs.
	 */
	public void createPluginsData(final Element rootElement) throws ConnectorException {
		Element element = creator.getDocument().createElement("PluginsInfo");
		rootElement.appendChild(element);
		InitCommandEventArgs args = new InitCommandEventArgs();
		args.setXml(this.creator);
		args.setRootElement(rootElement);
	}

	/**
	 * Creates plugins node in XML.
	 *
	 * @param rootElement root element in XML
	 * @throws Exception when error occurs
	 */
	private void createResouceTypesData(final Element rootElement) throws Exception {

		boolean isCompany = "CASE_ROLE_OWNER".equals(groupInfo.getString("roleTypeId"));
		Element element = creator.getDocument().createElement("ResourceTypes");
		rootElement.appendChild(element);
//		List<GenericValue> rootFolderList = EntityQuery.use(delegator).select().from("RootFolderList").queryList();
		List<Map<String, Object>> rootFolderList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("folderId",Constants.PRIVATE_FOLDER_ENTRY);
		map.put("folderName","私有文档");
		rootFolderList.add(map);
		GenericValue genericValue = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId", partyId)).queryOne();
		if(UtilValidate.isNotEmpty(genericValue) && genericValue.getString("partyTypeId").equals("PERSON")){
			map = new HashMap<>();
			map.put("folderId",Constants.SHARE_FOLDER_ENTRY);
			map.put("folderName","分享文档");
			rootFolderList.add(map);
		}
		//changed by galaxypan@2017-09-13:CASE文档放在私有文档下面
		if(isCompany){//仅企业账户或其子账号具有CASE文档大类目
			map = new HashMap<>();
			map.put("folderId",Constants.CASE_FOLDER_ENTRY);
			map.put("folderName","CASE文档");
			rootFolderList.add(map);
		}
		for(Map<String, Object> rootFolder : rootFolderList){
			Element childElement = creator.getDocument().
					createElement("ResourceType");
			childElement.setAttribute("folderId", rootFolder.get("folderId").toString());
			childElement.setAttribute("name",  rootFolder.get("folderName").toString());
			element.appendChild(childElement);
		}
	}
}
