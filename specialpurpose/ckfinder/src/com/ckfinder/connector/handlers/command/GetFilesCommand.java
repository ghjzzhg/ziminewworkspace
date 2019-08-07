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
import com.ckfinder.connector.utils.ImageUtils;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapComparator;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.w3c.dom.Element;

import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileFilter;
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
	/**
	 * show thumb post param.
	 */
	private String showThumbs;

	private String partyId;

	/**
	 * initializing parameters for command handler.
	 *命令处理程序的初始化参数。
	 * @param request request
	 * @param configuration connector configuration
	 * @param params execute additional params.
	 * @throws ConnectorException when error occurs
	 */
	@Override
	public void initParams(final HttpServletRequest request,
			final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration);
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) session.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		this.partyId = userLogin.getString("partyId");
		this.showThumbs = request.getParameter("showThumbs");
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
	 *取得文件夹下所有文件
	 * @return 0 if ok, otherwise error code
	 */
	protected int getDataForXml() throws GenericEntityException {
		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}
		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(
				this.type, this.currentFolder, this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_VIEW)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}
		this.files = searchFileList(configuration.getTypes().get(this.type).getPath() + this.currentFolder,this.type,false);
		filterListByHiddenAndNotAllowed();
		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	private void filterListByHiddenAndNotAllowed() throws GenericEntityException {
		List<Map<String,Object>> tmpFiles = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> tmpFolders = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> tmpFileList = new ArrayList<Map<String,Object>>();
		for (Map<String,Object> file : this.files) {
			if (FileUtils.checkFileExtension(file.get("fileName").toString(), this.configuration.getTypes().get(this.type)) == 0
					&& !FileUtils.checkIfFileIsHidden(file.get("fileName").toString(), this.configuration)) {
				Map<String, Object> map = new HashMap<>();
				map.put("fileName",file.get("fileName").toString());
				map.put("fileId",file.get("fileId").toString());
				map.put("newPath",file.get("newPath").toString());
				map.put("statusId",file.get("statusId").toString());
				map.put("fileType",file.get("fileType").toString());
				//map.put("historyFlag",isHaveFileHistory(file.get("fileId").toString()));
				tmpFiles.add(map);
			}
		}
		for(Map<String,Object> file: tmpFiles){
			if((dataType + "_FOLDER").equals(file.get("fileType").toString())){
				tmpFolders.add(file);
			}else{
				tmpFileList.add(file);
			}
		}
		this.files.clear();
		this.files.addAll(tmpFolders);
		this.files.addAll(tmpFileList);
	}

	/**
	 *  检索文件是否存在版本变更历史，如果存在版本变更则需要在页面中展现历史版本按钮
	 * @param fileId 文件ID
	 * @return Boolean 是否存在历史版本标识
	 * @throws GenericEntityException
     */
	private Boolean isHaveFileHistory(String fileId) throws GenericEntityException {
		Boolean flag = false;
		//// TODO: 2016-08-03  搜索文件是否存在历史信息，缺少表信息
		List<GenericValue> genericValues = EntityQuery.use(delegator).select().from("").where(UtilMisc.toMap("",fileId)).queryList();
		if (UtilValidate.isNotEmpty(genericValues)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * creates files data node in reponse XML.
	 *创建数据节点响应XML文件。
	 * @param rootElement root element from XML.
	 */
	private void createFilesData(final Element rootElement) {
		Element element = creator.getDocument().createElement("Files");
		for (Map<String,Object> filePath : files) {
			File file = new File(disc + filePath.get("newPath").toString(), filePath.get("fileName").toString());
			if (file.exists()) {
				XmlElementData elementData = new XmlElementData("File");
				XmlAttribute attribute = new XmlAttribute("name", filePath.get("fileName").toString());
				elementData.getAttributes().add(attribute);
				attribute = new XmlAttribute("date",FileUtils.parseLastModifDate(file));
				elementData.getAttributes().add(attribute);
				attribute = new XmlAttribute("size", getSize(file));
				elementData.getAttributes().add(attribute);
				attribute = new XmlAttribute("id", filePath.get("fileId").toString());
				elementData.getAttributes().add(attribute);
				attribute = new XmlAttribute("statusId", filePath.get("statusId").toString());
				elementData.getAttributes().add(attribute);
				attribute = new XmlAttribute("fileType", filePath.get("fileType").toString());
				elementData.getAttributes().add(attribute);
				attribute = new XmlAttribute("dataType", dataType);
				elementData.getAttributes().add(attribute);
				if (ImageUtils.isImage(file) && isAddThumbsAttr()) {
					String attr = createThumbAttr(file);
					if (!attr.equals("")) {
						attribute = new XmlAttribute("thumb", attr);
						elementData.getAttributes().add(attribute);
					}
				}
				elementData.addToDocument(this.creator.getDocument(), element);
			}
		}
		rootElement.appendChild(element);
	}

	/**
	 * gets thumb attribute value.
	 *
	 * @param file file to check if has thumb.
	 * @return thumb attribute values
	 */
	private String createThumbAttr(final File file) {
		File thumbFile = new File(configuration.getThumbsPath()
				+ File.separator + this.type + this.currentFolder,
				file.getName());
		if (thumbFile.exists()) {
			return file.getName();
		} else if (isShowThumbs()) {
			return "?".concat(file.getName());
		}
		return "";
	}

	/**
	 * get file size.
	 *得到文件大小
	 * @param file file
	 * @return file size
	 */
	private String getSize(final File file) {
		if (file.length() > 0 && file.length() < BYTES) {
			return "1";
		} else {
			return String.valueOf(Math.round(file.length() / BYTES));
		}
	}

	/**
	 * Check if show thumbs or not (add attr to file node with thumb file name).
	 *检查是否显示(添加attr与文件名文件节点)。
	 * @return true if show thumbs
	 */
	private boolean isAddThumbsAttr() {
		return configuration.getThumbsEnabled()
				&& (configuration.getThumbsDirectAccess()
				|| isShowThumbs());
	}

	/**
	 * checks show thumb request attribute.
	 *检查显示请求属性。
	 * @return true if is set.
	 */
	private boolean isShowThumbs() {
		return (this.showThumbs != null && this.showThumbs.toString().equals("1"));
	}
}
