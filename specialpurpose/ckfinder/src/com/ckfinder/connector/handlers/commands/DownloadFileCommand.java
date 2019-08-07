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

import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.content.data.FileManagerFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * Class to handle
 * <code>DownloadFile</code> command.
 */
public class DownloadFileCommand extends Command {

	/**
	 * File to download.
	 */
	private File file;
	/**
	 * filename request param.
	 */
	private String fileName;
	private Object format;
	private String fileId;

	/**
	 * executes the download file command. Writes file to response.
	 *
	 * @param out output stream
	 * @throws ConnectorException when something went wrong during reading file.
	 */
	@Override
	public void execute(final OutputStream out) throws ConnectorException {
		try {
			FileUtils.printFileContentToResponse(file, out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Sets response headers.
	 *
	 * @param response response
	 * @param sc       servlet context
	 */
	@Override
	public void setResponseHeader(final HttpServletResponse response,
								  final ServletContext sc) {
		try {
			String fileId = request.getParameter("fileId");
			if(fileId.indexOf(",") > 0){
				String[] fileIds = fileId.split(",");
				List<File> fileList = new ArrayList<>();
				for(String id : fileIds){
					GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",id)).queryOne();
					if(dataResource != null){
						fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
					}
					File file = fileManager.searchFileById(id, false);
					if(file != null){
						fileList.add(file);
					}
				}
				String ofbizHome = System.getProperty("ofbiz.home");
				String uploadFolder = ofbizHome + File.separatorChar + "runtime" + File.separatorChar + "tempfiles" + File.separatorChar + "downloadzip";
				File folderPath = new File(uploadFolder);
				if(!folderPath.exists()){
					folderPath.mkdirs();
				}
				Date date = new Date();
				this.fileName = date.getTime() + ".zip";
				String zipPath = uploadFolder + File.separatorChar + this.fileName;
				this.file = new File(zipPath);
				zipOutput(fileList,zipPath);
				this.fileName = URLEncoder.encode(this.fileName, "UTF-8");
			}else{
				GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
				if(dataResource != null){
					fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
					this.file = fileManager.searchFileById(fileId, true);
					this.fileName = dataResource.get("dataResourceName").toString();
					this.fileName = URLEncoder.encode(this.fileName, "UTF-8");
				}
			}
		} catch (IOException | GenericEntityException e) {
			e.printStackTrace();
		}
		String mimetype = sc.getMimeType(fileName);
		response.setCharacterEncoding("utf-8");
		if (this.format != null && this.format.equals("text")) {
			response.setContentType("text/plain; charset=utf-8");
		} else {
			if (mimetype != null) {
				response.setContentType(mimetype);
			} else {
				response.setContentType("application/octet-stream");
			}
			if (file != null) {
				response.setContentLength((int) file.length());
			}

			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ this.fileName + "\"");

		}
		response.setHeader("Cache-Control", "cache, must-revalidate");
		response.setHeader("Pragma", "public");
		response.setHeader("Expires", "0");
	}

	public void zipOutput(List<File> fileList, String zipPath){
		// 要被压缩的文件夹
		File zipFile = new File(zipPath);
		try {
			InputStream input = null;
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
			// zip的名称为
			zipOut.setComment(zipFile.getName());
			for (File fileInfo : fileList) {
				input = new FileInputStream(fileInfo);
				zipOut.putNextEntry(new ZipEntry(fileInfo.getName()));
				int temp = 0;
				while ((temp = input.read()) != -1) {
					zipOut.write(temp);
				}
				input.close();
				fileInfo.delete();
			}
			zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
