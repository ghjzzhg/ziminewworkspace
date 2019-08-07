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
import com.ckfinder.connector.configuration.Events.EventTypes;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.InitCommandEventArgs;
import com.ckfinder.connector.data.PluginInfo;
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.PathUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	 * chars taken to license key.
	 */
	private static final int[] LICENSE_CHARS = {11, 0, 8, 12, 26, 2, 3, 25, 1};
	private static final int LICENSE_CHAR_NR = 5;
	private static final int LICENSE_KEY_LENGTH = 34;
	private String type;

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
		if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			createConnectorData(rootElement);
			try {
				createResouceTypesData(rootElement);
			} catch (Exception e) {
				if (configuration.isDebugMode()) {
					throw new ConnectorException(e);
				}
			}
			createPluginsData(rootElement);
		}
	}

	/**
	 * Creates connector node in XML.
	 *
	 * @param rootElement root element in XML
	 */
	private void createConnectorData(final Element rootElement) {
		// connector info
		Element element = creator.getDocument().createElement("ConnectorInfo");
		element.setAttribute("enabled", String.valueOf(configuration.enabled()));
		element.setAttribute("s", getLicenseName());
		element.setAttribute("c",
				createLicenseKey(configuration.getLicenseKey()));
		element.setAttribute("thumbsEnabled", String.valueOf(
				configuration.getThumbsEnabled()));
		element.setAttribute("uploadCheckImages", configuration.checkSizeAfterScaling() ? "false" : "true");
		if (configuration.getThumbsEnabled()) {
			element.setAttribute("thumbsUrl", configuration.getThumbsURL());
			element.setAttribute("thumbsDirectAccess", String.valueOf(
					configuration.getThumbsDirectAccess()));
			element.setAttribute("thumbsWidth", String.valueOf(configuration.getMaxThumbWidth()));
			element.setAttribute("thumbsHeight", String.valueOf(configuration.getMaxThumbHeight()));
		}
		element.setAttribute("imgWidth", String.valueOf(configuration.getImgWidth()));
		element.setAttribute("imgHeight", String.valueOf(configuration.getImgHeight()));
		if (configuration.getPlugins().size() > 0) {
			element.setAttribute("plugins", getPlugins());
		}
		rootElement.appendChild(element);
	}

	/**
	 * gets plugins names.
	 *
	 * @return plugins names.
	 */
	private String getPlugins() {
		StringBuilder sb = new StringBuilder();
		boolean first = false;
		for (PluginInfo item : configuration.getPlugins()) {
			if (item.isEnabled() && !item.isInternal()) {
				if (first) {
					sb.append(",");
				}
				sb.append(item.getName());
				first = true;
			}
		}
		return sb.toString();
	}

	/**
	 * checks license key.
	 *
	 * @return license name if key is ok, or empty string if not.
	 */
	private String getLicenseName() {
		if (validateLicenseKey(configuration.getLicenseKey())) {
			int index = Constants.CKFINDER_CHARS.indexOf(configuration.getLicenseKey().charAt(0))
					% LICENSE_CHAR_NR;
			if (index == 1 || index == 4) {
				return configuration.getLicenseName();
			}
		}
		return "";
	}

	/**
	 * Creates license key from key in configuration.
	 *
	 * @param licenseKey license key from configuration
	 * @return hashed license key
	 */
	private String createLicenseKey(final String licenseKey) {
		if (validateLicenseKey(licenseKey)) {
			StringBuilder sb = new StringBuilder();
			for (int i : LICENSE_CHARS) {
				sb.append(licenseKey.charAt(i));
			}
			return sb.toString();
		}
		return "";
	}

	/**
	 * validates license key lenght.
	 *
	 * @param licenseKey config license key
	 * @return true if has correnct length
	 */
	private boolean validateLicenseKey(final String licenseKey) {
		return licenseKey != null && licenseKey.length() == LICENSE_KEY_LENGTH;
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
		if (configuration.getEvents() != null) {
			configuration.getEvents().run(EventTypes.InitCommand, args, configuration);
		}

	}

	/**
	 * Creates plugins node in XML.
	 *
	 * @param rootElement root element in XML
	 * @throws Exception when error occurs
	 */
	private void createResouceTypesData(final Element rootElement) throws Exception {
		//resurcetypes
		Element element = creator.getDocument().createElement("ResourceTypes");
		rootElement.appendChild(element);

		List<String> types = null;
		if (super.type != null && !super.type.equals("")) {
			types = new ArrayList<String>();
			types.add(super.type);
		} else {
			types = getTypes();
		}

		for (String key : types) {
			ResourceType resourceType = configuration.getTypes().get(key);
			if (((this.type == null || this.type.equals(key)) && resourceType != null)
					&& AccessControlUtil.getInstance(this.configuration).checkFolderACL(key, "/", this.userRole,
					AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW)) {
				String fullCurrentPath = configuration.getTypes().get(key).getPath();
				Integer num = searchFileNumber(fullCurrentPath,key);
				Element childElement = creator.getDocument().
						createElement("ResourceType");
				childElement.setAttribute("name", resourceType.getName());
				childElement.setAttribute("num", num.toString());
				childElement.setAttribute("acl", String.valueOf(
						AccessControlUtil.getInstance(configuration).checkACLForRole(key, "/", this.userRole)));
				childElement.setAttribute("hash", randomHash(
						resourceType.getPath()));
				childElement.setAttribute(
						"allowedExtensions",
						resourceType.getAllowedExtensions());
				childElement.setAttribute(
						"deniedExtensions",
						resourceType.getDeniedExtensions());
				childElement.setAttribute("url", resourceType.getUrl() + "/");
				Long maxSize = resourceType.getMaxSize();
				childElement.setAttribute("maxSize", (maxSize != null && maxSize > 0) ? maxSize.toString() : "0");
				childElement.setAttribute("hasChildren",
						this.hasChildren("/", new File(PathUtils.escape(resourceType.getPath())),
								configuration, resourceType.getName(), this.userRole, partyId, delegator, "type", folderId).toString());
				element.appendChild(childElement);
			}
		}
	}

	/**
	 * gets list of types names.
	 *
	 * @return list of types names.
	 */
	private List<String> getTypes() {
		if (configuration.getDefaultResourceTypes().size() > 0) {
			return configuration.getDefaultResourceTypes();
		} else {
			return configuration.getResourceTypesOrder();
		}
	}

	/**
	 * Gets hash for folders in XML response to avoid cached responses.
	 *
	 * @param folder folder
	 * @return hash value
	 * @throws Exception when error occurs and debug mode is on
	 */
	private String randomHash(final String folder) throws Exception {

		try {
			MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
			algorithm.reset();
			try {
				algorithm.update(folder.getBytes("UTF8"));
			} catch (UnsupportedEncodingException e) {
				if (configuration.isDebugMode()) {
					throw e;
				}
				algorithm.update(folder.getBytes());
			}
			byte[] messageDigest = algorithm.digest();

			StringBuilder hexString = new StringBuilder();

			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
			}
			return hexString.substring(0, 15);
		} catch (NoSuchAlgorithmException e) {
			if (configuration.isDebugMode()) {
				throw e;
			}
			return "";
		}
	}

	public Boolean hasChildren(String dirPath, File dir, IConfiguration configuration, String resourceType, String currentUserRole, String partyId, Delegator delegator, String flag, String folderId) throws GenericEntityException {
		if("type".equals(flag)){
			GenericValue folder = new GenericValue();
			if(null != folderId){
				folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", folderId)).distinct().queryOne();
			}
			GetFoldersCommand getFoldersCommand = new GetFoldersCommand();
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
						String path = file.getPath().substring(0,file.getPath().length()-0);
						String fileName = path.substring(path.lastIndexOf("/") + 1,path.length());
						String filePath = path.substring(0,path.lastIndexOf("/"));
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataResourceName",fileName);
						map.put("parentObjectInfo",filePath);
						folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
						dataResourceList.add(folder);
					}
				}
				if("他人分享".equals(resourceType)){
					this.setDataResourceList(dataResourceList);
				}
			}else{
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				String path  = dir.getPath();
				path = path.replace("\\","/") + "/";
				conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path));
				conditionList.add(EntityCondition.makeCondition("dataResourceTypeId", EntityOperator.EQUALS, dataType +"_FOLDER"));
				EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
				dataResourceList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
				if("协作空间".equals(resourceType)){
					for(int i = 0; i<dataResourceList.size() ; i++){
						GenericValue dataResource = dataResourceList.get(i);
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("dataId",dataResource.get("dataResourceId"));
						map.put("entityName","DataResource");
						List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblDataScope").where(map).queryList();
						if(null != partyList && partyList.size() > 0){
							String idList = "";
							for(GenericValue party : partyList){
								String scopeType = party.get("scopeType").toString();
								if("SCOPE_USER".equals(scopeType)){
									idList = idList + party.get("scopeValue") + ",";
								}else{
									List<GenericValue> personList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(EntityCondition.makeCondition("department",party.get("scopeValue"))).queryList();
									for(GenericValue person : personList){
										idList = idList + person.get("partyId") + ",";
									}
									if("SCOPE_DEPT_LIKE".equals(scopeType)){
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
								}
							}
							if(!idList.contains(partyId + ",")){
								dataResourceList.remove(dataResource);
							}
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(dataResourceList)) {
				for (GenericValue file : dataResourceList) {
					String subDirName = file.get("dataResourceName").toString();
					Boolean checkFolder = AccessControlUtil.getInstance(configuration).checkFolderACL(resourceType,dirPath + subDirName, currentUserRole, AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW);
					if (!FileUtils.checkIfDirIsHidden(subDirName, configuration) && checkFolder) {
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

	@Override
	protected boolean mustAddCurrentFolderNode() {
		return false;
	}

	@Override
	protected void getCurrentFolderParam(final HttpServletRequest request) {
		this.currentFolder = null;
	}
}
