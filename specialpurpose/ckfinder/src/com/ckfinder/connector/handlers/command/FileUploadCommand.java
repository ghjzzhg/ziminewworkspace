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
import com.ckfinder.connector.data.AfterFileUploadEventArgs;
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.errors.ErrorUtils;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.ImageUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.IOFileUploadException;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
public class FileUploadCommand extends Command implements IPostCommand {

	/**
	 * Uploading file name request.
	 */
	protected String fileName;
	/**
	 * File name after rename.
	 */
	protected String newFileName;
	/**
	 * Function number to call after file upload is completed.
	 */
	protected String ckEditorFuncNum;
	/**
	 * The selected response type to be used after file upload is completed.
	 */
	protected String responseType;
	/**
	 * Function number to call after file upload is completed.
	 */
	protected String ckFinderFuncNum;
	/**
	 * Language (locale) code.
	 */
	private String langCode;
	/**
	 * Flag informing if file was uploaded correctly.
	 */
	protected boolean uploaded;
	/**
	 * Error code number.
	 */
	protected int errorCode;
	/**
	 * Custom error message.
	 */
	protected String customErrorMsg;
	/**
	 * Array containing unsafe characters which can't be used in file name.
	 */
	private static final char[] UNSAFE_FILE_NAME_CHARS = {':', '*', '?', '|', '/'};

	private String partyId;

	private String fileStatus = "";

	private String fileId = "";

	/**
	 * default constructor.
	 */
	public FileUploadCommand() {
		this.errorCode = 0;
		this.fileName = "";
		this.newFileName = "";
		this.type = "";
		this.uploaded = false;
	}

	/**
	 * Executes file upload command.
	 *
	 * @param out the response output stream
	 * @throws ConnectorException when error occurs.
	 */
	@Override
	public void execute(final OutputStream out) throws ConnectorException {
		if (configuration.isDebugMode() && this.exception != null) {
			throw new ConnectorException(this.errorCode, this.exception);
		}
		try {
			String errorMsg = this.errorCode == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE ? "" : ( this.errorCode == Constants.Errors.CKFINDER_CONNECTOR_ERROR_CUSTOM_ERROR ? this.customErrorMsg :
					ErrorUtils.getInstance().getErrorMsgByLangAndCode(this.langCode, this.errorCode, this.configuration));
			errorMsg = errorMsg.replaceAll("%1", Matcher.quoteReplacement(this.newFileName));
			String path = "";

			if (!uploaded) {
				this.newFileName = "";
				this.currentFolder = "";
			} else {
				path = configuration.getTypes().get(this.type).getUrl()
						+ this.currentFolder;
			}

			if (this.responseType != null && this.responseType.equals("txt")) {
				out.write((this.newFileName + "|" + errorMsg).getBytes("UTF-8"));
			} else {
				if (checkFuncNum()) {
					handleOnUploadCompleteCallFuncResponse(out, errorMsg, path);
				} else {
					handleOnUploadCompleteResponse(out, errorMsg);
				}
			}

		} catch (IOException e) {
			throw new ConnectorException(
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
		}

	}

	/**
	 * check if func num is set in request.
	 *
	 * @return true if is.
	 */
	protected boolean checkFuncNum() {
		return this.ckFinderFuncNum != null;
	}

	/**
	 * return response when func num is set.
	 *
	 * @param out response.
	 * @param errorMsg error message
	 * @param path path
	 * @throws IOException when error occurs.
	 */
	protected void handleOnUploadCompleteCallFuncResponse(final OutputStream out,
														  final String errorMsg,
														  final String path)
			throws IOException {
		this.ckFinderFuncNum = this.ckFinderFuncNum.replaceAll(
				"[^\\d]", "");
		out.write("<script type=\"text/javascript\">".getBytes("UTF-8"));
		out.write(("window.parent.CKFinder.tools.callFunction("
				+ this.ckFinderFuncNum + ", '"
				+ path
				+ FileUtils.backupWithBackSlash(this.newFileName, "'")
				+ "', '" + errorMsg + "');").getBytes("UTF-8"));
		out.write("</script>".getBytes("UTF-8"));
	}

	/**
	 *
	 * @param out out put stream
	 * @param errorMsg error message
	 * @throws IOException when error occurs
	 */
	protected void handleOnUploadCompleteResponse(final OutputStream out,
												  final String errorMsg) throws IOException {
		out.write("<script type=\"text/javascript\">".getBytes("UTF-8"));
		out.write("window.parent.OnUploadCompleted(".getBytes("UTF-8"));
		out.write(("\'" + FileUtils.backupWithBackSlash(this.newFileName, "'") + "\'").getBytes("UTF-8"));
		out.write((", \'"
				+ (this.errorCode
				!= Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE ? errorMsg
				: "") + "\'").getBytes("UTF-8"));
		out.write(");".getBytes("UTF-8"));
		out.write("</script>".getBytes("UTF-8"));
	}

	/**
	 * initializing parametrs for command handler.
	 *
	 * @param request request
	 * @param configuration connector configuration.
	 * @param params execute additional params.
	 * @throws ConnectorException when error occurs.
	 */
	@Override
	public void initParams(final HttpServletRequest request,
						   final IConfiguration configuration, final Object... params)
			throws ConnectorException, GenericEntityException {
		super.initParams(request, configuration, params);
		this.ckFinderFuncNum = request.getParameter("CKFinderFuncNum");
		this.ckEditorFuncNum = request.getParameter("CKEditorFuncNum");
		this.fileStatus = request.getParameter("fileStatus");
		this.fileId = request.getParameter("fileId");
		this.responseType = request.getParameter("response_type") != null
				? request.getParameter("response_type") : request.getParameter("responseType");
		this.langCode = request.getParameter("langCode");
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) session.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		this.partyId = userLogin.getString("partyId");
		if (this.errorCode == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
			this.uploaded = uploadFile(request);
		}


	}

	/**
	 * uploads file and saves to file.
	 *
	 * @param request request
	 * @return true if uploaded correctly.
	 */
	private boolean uploadFile(final HttpServletRequest request) {
		if (!AccessControlUtil.getInstance(configuration).checkFolderACL(
				this.type, this.currentFolder, this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_UPLOAD)) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
			return false;
		}
		return fileUpload(request);
	}

	/**
	 *
	 * @param request http request
	 * @return true if uploaded correctly
	 */
	@SuppressWarnings("unchecked")
	private boolean fileUpload(final HttpServletRequest request) {
		try {
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload uploadHandler = new ServletFileUpload(
					fileItemFactory);

			List<FileItem> items = uploadHandler.parseRequest(request);
			for (FileItem item : items) {
				if (!item.isFormField()) {
					String path = configuration.getTypes().get(this.type).getPath()
							+ this.currentFolder;
					path = path.replace("\\", "/");
					this.fileName = getFileItemName(item);
					try {
						if (validateUploadItem(item, path)) {
							boolean flag = saveTemporaryFile(path, item, disc);
							if(flag){
								String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
								String newPath = path.substring(typePath.length()+1, path.length()).replace("\\","/");
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
										String newTypePath = configuration.getTypes().get("本人分享").getPath().replace("\\", "/") + "/" + newPath;
										FileService.copyFile(path + newFileName, newTypePath + newFileName);
									}
									newPath = newPath.substring(th.length()+1,newPath.length());
								}
								return flag;
							}
						}
					} finally {
						item.delete();
					}
				}
			}
			return false;
		} catch (InvalidContentTypeException e) {
			if (configuration.isDebugMode()) {
				this.exception = e;
			}
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_CORRUPT;
			return false;
		} catch (IOFileUploadException e) {
			if (configuration.isDebugMode()) {
				this.exception = e;
			}
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			return false;
		} catch (SizeLimitExceededException e) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG;
			return false;
		} catch (FileSizeLimitExceededException e) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG;
			return false;
		} catch (ConnectorException e) {
			this.errorCode = e.getErrorCode();
			if (this.errorCode == Constants.Errors.CKFINDER_CONNECTOR_ERROR_CUSTOM_ERROR)
				this.customErrorMsg = e.getErrorMsg();
			return false;
		} catch (Exception e) {
			if (configuration.isDebugMode()) {
				this.exception = e;
			}
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			return false;
		}

	}

	public void saveDataResource(String path) throws GenericEntityException {
		Map<String,Object> map = new HashMap<String,Object>();
		String resourceId = delegator.getNextSeqId("DataResource").toString();//获取主键ID
		map.put("dataResourceId",resourceId);
		map.put("dataResourceTypeId",dataType + "_FILE");
		map.put("dataResourceName",newFileName);
		map.put("partyId",partyId);
		String newFolder = path.replace("\\","/") ;
		map.put("parentObjectInfo",newFolder);
		map.put("objectInfo",newFolder+newFileName);
		GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
		dataResoure.create();
		String dataScope = delegator.getNextSeqId("TblDataScope").toString();//获取主键ID
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

	/**
	 * saves temporary file in the correct file path.
	 *
	 * @param path path to save file
	 * @param item file upload item
	 * @return result of saving, true if saved correctly
	 * @throws Exception when error occurs.
	 */
	private boolean saveTemporaryFile(final String path, final FileItem item, final String disc)
			throws Exception {
		File file = new File(disc + path, this.newFileName);
		AfterFileUploadEventArgs args = new AfterFileUploadEventArgs();
		args.setCurrentFolder(this.currentFolder);
		args.setFile(file);
		args.setFileContent(item.get());
		if (!ImageUtils.isImage(file)) {
			item.write(file);
			if (configuration.getEvents() != null) {
				configuration.getEvents().run(EventTypes.AfterFileUpload,
						args, configuration);
			}
			return true;
		} else if (ImageUtils.checkImageSize(item.getInputStream(), this.configuration)
				|| configuration.checkSizeAfterScaling()) {
			ImageUtils.createTmpThumb(item.getInputStream(), file, getFileItemName(item),
					this.configuration);
			if (!configuration.checkSizeAfterScaling()
					|| FileUtils.checkFileSize(configuration.getTypes().get(this.type), file.length())) {
				if (configuration.getEvents() != null) {
					configuration.getEvents().run(EventTypes.AfterFileUpload, args, configuration);
				}
				return true;
			} else {
				file.delete();
				this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG;
				return false;
			}
		} else {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG;
			return false;
		}
	}

	/**
	 * if file exists this method adds (number) to file.
	 *
	 * @param path folder
	 * @param name file name
	 * @return new file name.
	 */
	private String getFinalFileName(final String path, final String name) throws GenericEntityException {
		File file = new File(disc + path, name);
		int number = 0;

		String nameWithoutExtension = FileUtils.getFileNameWithoutExtension(name, false);
		Pattern p = Pattern.compile("^(AUX|COM\\d|CLOCK\\$|CON|NUL|PRN|LPT\\d)$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(nameWithoutExtension);
		boolean protectedName = m.find();
		if(UtilValidate.isNotEmpty(fileStatus) && fileStatus.equals("1")){
			return this.newFileName;
		}else if(UtilValidate.isNotEmpty(fileStatus) && fileStatus.equals("2")){
			createFileVersion();
			return this.newFileName;
		}else{
			while (true) {
				if (file.exists() || protectedName) {
					number++;
					StringBuilder sb = new StringBuilder();
					sb.append(FileUtils.getFileNameWithoutExtension(name, false));
					sb.append("(").append(number).append(").");
					sb.append(FileUtils.getFileExtension(name, false));
					this.newFileName = sb.toString();
					file = new File(path, this.newFileName);
					this.errorCode =
							Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_FILE_RENAMED;
					protectedName = false;
				} else {
					saveDataResource(path);
					return this.newFileName;
				}
			}
		}
	}

	private void createFileVersion(){
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("YYYYMMDDHHmmss");
		String dateTime = format.format(date);
		try {
			GenericValue fileModel = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
			String filePath = fileModel.get("objectInfo").toString();
			File file = new File(disc + filePath);
			File newFile = new File(file.getParent().replace("\\", "/") +  "/" + dateTime + "_" + file.getName());
			file.renameTo(newFile);
			String dataResourceId = delegator.getNextSeqId("DataResource");
			fileModel.set("dataResourceId", dataResourceId);
			fileModel.set("dataResourceName",newFile.getName());
			fileModel.set("objectInfo",newFile.getPath().replace("\\","/"));
			fileModel.set("dataResourceTypeId",dataType + "_VERSION_FILE");
			fileModel.create();
			Map<String, Object> map = new HashMap<>();
			map.put("dataResourceId",fileId);
			map.put("historyFileId",dataResourceId);
			map.put("version",dateTime);
			GenericValue historyFile = delegator.makeValidValue("TblHistoryFiles", map);
			historyFile.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * validates uploaded file.
	 *
	 * @param item uploaded item.
	 * @param path file path
	 * @return true if validation
	 */
	private boolean validateUploadItem(final FileItem item, final String path) {

		if (item.getName() != null && item.getName().length() > 0) {
			this.fileName = getFileItemName(item);
		} else {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_INVALID;
			return false;
		}
		this.newFileName = this.fileName;

		for (char c : UNSAFE_FILE_NAME_CHARS) {
			this.newFileName = this.newFileName.replace(c, '_');
		}

		if (configuration.isDisallowUnsafeCharacters()) {
			this.newFileName = this.newFileName.replace(';', '_');
		}
		if (configuration.forceASCII()) {
			this.newFileName = FileUtils.convertToASCII(this.newFileName);
		}
		if (!this.newFileName.equals(this.fileName)) {
			this.errorCode =
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_INVALID_NAME_RENAMED;
		}


		if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			return false;
		}
		if (!FileUtils.checkFileName(this.newFileName)
				|| FileUtils.checkIfFileIsHidden(this.newFileName,
				configuration)) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME;
			return false;
		}
		final ResourceType resourceType = configuration.getTypes().get(this.type);
		int checkFileExt = FileUtils.checkFileExtension(this.newFileName, resourceType);
		if (checkFileExt == 1) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION;
			return false;
		}
		if (configuration.ckeckDoubleFileExtensions()) {
			this.newFileName = FileUtils.renameFileWithBadExt(resourceType, this.newFileName);
		}

		try {
			File file = new File(path, getFinalFileName(path, this.newFileName));
			if (!(ImageUtils.isImage(file) && configuration.checkSizeAfterScaling())
					&& !FileUtils.checkFileSize(resourceType, item.getSize())) {
				this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG;
				return false;
			}

			if (configuration.getSecureImageUploads() && ImageUtils.isImage(file)
					&& !ImageUtils.checkImageFile(item)) {
				this.errorCode =
						Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_CORRUPT;
				return false;
			}

			if (!FileUtils.checkIfFileIsHtmlFile(file.getName(), configuration)
					&& FileUtils.detectHtml(item)) {
				this.errorCode =
						Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_WRONG_HTML_FILE;
				return false;
			}
		} catch (SecurityException e) {
			if (configuration.isDebugMode()) {
				this.exception = e;
			}
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			return false;
		} catch (IOException e) {
			if (configuration.isDebugMode()) {
				this.exception = e;
			}
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			return false;
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}


		return true;
	}

	/**
	 * set response headers. Not user in this command.
	 *
	 * @param response response
	 * @param sc servlet context
	 */
	@Override
	public void setResponseHeader(final HttpServletResponse response,
								  final ServletContext sc) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
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

	/**
	 * check request for security issue.
	 *
	 * @param reqParam request param
	 * @return true if validation passed
	 * @throws ConnectorException if valdation error occurs.
	 */
	@Override
	protected boolean checkParam(final String reqParam)
			throws ConnectorException {
		if (reqParam == null || reqParam.equals("")) {
			return true;
		}
		if (Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(reqParam).find()) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME;
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkHidden()
			throws ConnectorException {
		if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
			return true;
		}
		return false;
	}

	@Override
	protected boolean checkConnector(final HttpServletRequest request)
			throws ConnectorException {
		if (!configuration.enabled() || !configuration.checkAuthentication(request)) {
			this.errorCode =
					Constants.Errors.CKFINDER_CONNECTOR_ERROR_CONNECTOR_DISABLED;
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkIfCurrFolderExists(final HttpServletRequest request)
			throws ConnectorException {
		String tmpType = getParameter(request, "type");
		if( checkIfTypeExists( tmpType ) ) {
			File currDir = new File(disc + configuration.getTypes().get(tmpType).getPath()
					+ this.currentFolder);
			if (currDir.exists() && currDir.isDirectory()) {
				return true;
			} else {
				this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND;
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean checkIfTypeExists( final String type ) {
		ResourceType testType = configuration.getTypes().get( type );
		if( testType == null ) {
			this.errorCode = Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
			return false;
		}
		return true;
	}
}
