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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.w3c.dom.Element;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to handle
 * <code>FileUpload</code> command.
 */
public class FileUploadCommand extends XMLCommand {

	/**
	 * Uploading file name request.
	 */
	protected String fileName;
	/**
	 * File name after rename.
	 */
	protected String newFileName;
	/**
	 * Flag informing if file was uploaded correctly.
	 */
	protected boolean uploaded;
	/**
	 * Error code number.
	 */
	protected int errorCode;
	/**
	 * Array containing unsafe characters which can't be used in file name.
	 */
	private static final char[] UNSAFE_FILE_NAME_CHARS = {':', '*', '?', '|', '/'};

	private String fileStatus = "";

	private String fileId = "";

	/**
	 * default constructor.
	 */
	public FileUploadCommand() {
		this.errorCode = 0;
		this.fileName = "";
		this.newFileName = "";
		this.uploaded = false;
	}

	@Override
	protected void createXMLChildNodes(int errorNum, Element rootElement) throws ConnectorException, GenericEntityException {
		Element element = creator.getDocument().createElement("Files");
		XmlElementData xmlElementData = new XmlElementData("file");
		String fileSize = EntityUtilProperties.getPropertyValue("content.properties", "fileSize", "localhost", delegator);
		xmlElementData.getAttributes().add(new XmlAttribute("fileId", fileId));
		xmlElementData.getAttributes().add(new XmlAttribute("fileName", newFileName));
		xmlElementData.addToDocument(creator.getDocument(), element);
		rootElement.appendChild(element);
	}

	@Override
	protected int getDataForXml() throws GenericEntityException {
		this.fileId = request.getParameter("fileId");
		this.fileStatus = request.getParameter("fileStatus");
		fileUpload(request);
		return errorCode;
	}

	/**
	 *
	 * @param request http request
	 * @return true if uploaded correctly
	 */
	@SuppressWarnings("unchecked")
	private boolean fileUpload(final HttpServletRequest request) {
		String currentFolder = request.getParameter("currentFolder");
		String rootFolder = request.getParameter("rootFolderId");
		String folderStructure = request.getParameter("folderStructure");
		String folderId = "";
		if(UtilValidate.isNotEmpty(currentFolder)){
			folderId = currentFolder;
		}else{
			folderId = rootFolder;
		}
		if(UtilValidate.isNotEmpty(this.otherModules)){
			rootFolder = "1";
			if(currentFolder.equals("2")){
				folderId = "1";
			}
		}
		try {
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload uploadHandler = new ServletFileUpload(
					fileItemFactory);
			List<FileItem> items = uploadHandler.parseRequest(request);
			for (FileItem item : items) {
				if (!item.isFormField()) {
					this.fileName = getFileItemName(item);
					if(fileName == null || fileName.trim().equals("")){
						continue;
					}

					//防止外模块修改文件所属的partyId
					GenericValue fileInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap
							("folderType","1","folderId",folderId)).queryOne();
					String filePartyId = this.partyId;
					if(UtilValidate.isNotEmpty(fileInfo)){
						filePartyId = fileInfo.get("partyId").toString();
					}
					boolean overwrite = true;
					if(UtilValidate.isNotEmpty(fileStatus) && fileStatus.equals("2")){
						overwrite = false;
					}
					String fileSize = EntityUtilProperties.getPropertyValue("content.properties", "fileSize", "localhost", delegator);
					if(item.getSize() > Integer.parseInt(fileSize) * 1024 * 1024 ){
						errorCode = Constants.Errors.CKFINDER_FILE_SIZE;
						break;
					}
					String dataResourceId = DataServices.storeAllDataResourceInMap(delegator,this.fileName,item.getSize(),item.getContentType(),item.get
							(),userLogin,fileId,overwrite);
					this.fileId = dataResourceId;
					this.newFileName = this.fileName;
					if(UtilValidate.isEmpty(fileStatus)) {//新文件或新版本
						saveFileInfo("", this.fileName, folderId, rootFolder,"file", "", filePartyId, dataResourceId,folderStructure);
					}
				}
			}
		}catch (Exception e) {
			errorCode  = Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
		}
		return false;
	}

	/**
	 * save if uploaded file item name is full file path not only file name.
	 *
	 * @param item file upload item
	 * @return file name of uploaded item
	 */
	private String getFileItemName(final FileItem item) {
		Pattern p = Pattern.compile("[^\\\\/]+$");
		Matcher m = p.matcher(item.getName());

		return (m.find()) ? m.group(0) : "";
	}

}
