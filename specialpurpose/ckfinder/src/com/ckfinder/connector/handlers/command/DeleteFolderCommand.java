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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;

/**
 * Class to handle
 * <code>DeleteFolder</code> command.
 */
public class DeleteFolderCommand extends XMLCommand implements IPostCommand {

	@Override
	protected void createXMLChildNodes(final int errorNum, final Element rootElement)
			throws ConnectorException {
	}

	/**
	 * @return error code or 0 if ok. Deletes folder and thumb folder.
	 */
	@Override
	protected int getDataForXml() {

		if (!checkIfTypeExists(this.type)) {
			this.type = null;
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
		}

		if (!AccessControlUtil.getInstance(this.configuration).checkFolderACL(this.type,
				this.currentFolder,
				this.userRole,
				AccessControlUtil.CKFINDER_CONNECTOR_ACL_FOLDER_DELETE)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
		}
		if (this.currentFolder.equals("/")) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}

		if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
			return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
		}

		String dirpath = configuration.getTypes().get(this.type).getPath()
				+ this.currentFolder;

		File adir = new File(disc + dirpath);
		File dir = new File(dirpath);

		try {
			if (!adir.exists() || !adir.isDirectory()) {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND;
			}
			File thumbDir = new File(configuration.getThumbsPath() + File.separator + this.type + this.currentFolder);
			String path1 = dir.getPath().replace("\\", "/");
			//删除文件夹下有分享记录的文件或者文件夹
			List<EntityCondition> conditionList1 = new ArrayList<EntityCondition>();
			conditionList1.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.LIKE, "%" + path1 + "%"));
			EntityConditionList condition1 = EntityCondition.makeCondition(UtilMisc.toList(conditionList1));
			List<GenericValue> list = EntityQuery.use(delegator).select().from("DataResource").where(condition1).distinct().queryList();
			delShareFile(list);
			//删除分享文件夹中的文件夹
			String typePath = configuration.getTypes().get(this.type).getPath().replace("\\", "/");
			String newPath = path1.substring(typePath.length()+1, path1.length()).replace("\\","/");//currentFolder
			String[] paths = newPath.split("/");
			String partPath = "/";
			searchSubPath(paths,partPath,typePath,newPath);
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			String path = dir.getPath().substring(0, dir.getPath().lastIndexOf(dir.getName())).replace("\\","/");
			conditionList.add(EntityCondition.makeCondition("parentObjectInfo", EntityOperator.EQUALS, path));
			conditionList.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.EQUALS, dir.getName()));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
			List<GenericValue> folderList = EntityQuery.use(delegator).select().from("DataResource").where(condition).distinct().queryList();
			removeFlderList(folderList,dir);
			if (FileUtils.delete(adir)) {
				FileUtils.delete(thumbDir);
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
		} catch (SecurityException e) {
			if (configuration.isDebugMode()) {
				throw e;
			} else {
				return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
	}

	/**
	 * 删除单个文件
	 * @param   sPath    被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteSharedFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * @param   sPath 被删除目录的文件路径
	 * @return  目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		//如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		//如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		//删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			//删除子文件
			if (files[i].isFile()) {
				flag = deleteSharedFile(files[i].getAbsolutePath());
				if (!flag) break;
			} //删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
		//删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
}
