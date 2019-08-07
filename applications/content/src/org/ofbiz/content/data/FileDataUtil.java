package org.ofbiz.content.data;

import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by REXTEC-15-2 on 2016-09-08.
 */
public class FileDataUtil {
    public static String createFileVersion(String partyId, String newFilePath, GenericDelegator delegator, String fileType, DataFileDescription fileDescription,LocalDispatcher dispatcher, GenericValue userLogin) throws Exception {
        String fileId = fileDescription.getFileId();
        String fileName = fileDescription.getFileName();
        if(fileName.length() > 50){
            fileName = fileName.substring(0, 30) + "." + FilenameUtils.getExtension(fileName);
            fileDescription.setFileName(fileName);
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYYMMddHHmmss");
        String dateTime = format.format(date);
        if(fileDescription.getFileSize() <= 0){
            fileDescription.setFileType("text/plain");
        }
        //将原来的文件复制修改为版本文件
        GenericValue fileModel = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();
        if(fileModel == null){
            Map<String, Object> serviceResult = dispatcher.runSync("createDataResource", UtilMisc.toMap("userLogin", userLogin, "dataResourceTypeId", fileType, "dataResourceName", fileDescription.getFileName(), "mimeTypeId", fileDescription.getFileType(), "objectInfo", newFilePath, "resourceSize", fileDescription.getFileSize()));
            if(UtilValidate.isNotEmpty(fileDescription.getReadOnly()) && fileDescription.getReadOnly()){
                GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",serviceResult.get("dataResourceId"))).queryOne();
                String filerId = serviceResult.get("dataResourceId") + "-r";
                genericValue.put("isPublic", "Y");
                genericValue.put("dataResourceId", filerId);
                genericValue.create();
                return filerId;
            }
            return (String) serviceResult.get("dataResourceId");
        } else if(!fileDescription.isOverwrite()) {
            //查询文件归属表信息
            GenericValue fileOwnership = EntityQuery.use(delegator).select().from("TblFileOwnership").where(UtilMisc.toMap("fileId", fileId, "fileType", "1")).queryOne();
            String fileVersion = "";
            if(fileOwnership == null){
                fileVersion = dateTime;
            }else{
                fileVersion = fileOwnership.get("fileVersion").toString();
            }
            String filePath = fileModel.get("objectInfo").toString();
            File file = new File(filePath);
            String parentPath = "";
            if(UtilValidate.isNotEmpty(file.getParent())){
                parentPath = file.getParent().replace("\\", "/")  + "/";
            }
            File newFile = new File(parentPath + fileVersion + "_" + file.getName());
            String newFileName = fileVersion + "_" + fileModel.get("dataResourceName");
            file.renameTo(newFile);
            String dataResourceId = delegator.getNextSeqId("DataResource");
            fileModel.put("dataResourceId", dataResourceId);
            fileModel.put("dataResourceName", newFileName);
            fileModel.put("objectInfo", newFile.getAbsolutePath());
            fileModel.put("dataResourceTypeId", fileType);
            fileModel.create();
            //fileOwnership为空，说明不是文档管理中的文件，不需要修改TblFileOwnership表中的数据
            if(fileOwnership != null) {
                //修改当前版本信息
                fileOwnership.put("fileVersion", dateTime);
                fileOwnership.put("createFileTime", new java.sql.Timestamp(date.getTime()));
                fileOwnership.put("createPartyId", partyId);
                fileOwnership.store();
            }
            //在历史表中记录文件信息
            Map<String, Object> map = new HashMap<>();
            map.put("dataResourceId", fileId);
            map.put("historyFileId", dataResourceId);
            map.put("version", fileVersion);
            GenericValue historyFile = delegator.makeValidValue("TblHistoryFiles", map);
            historyFile.create();
            fileDescription.setOverwrite(true);
        }
        return fileId;
    }

    public static boolean copyFromSourceToDestFile(final File sourceFile,
                                                   final File destFile,
                                                   final boolean move)
            throws IOException {
        createPath(destFile, true);
        InputStream in = new FileInputStream(sourceFile);
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        if (move) {
            sourceFile.delete();
        }
        return true;
    }

    public static void createPath(final File file, final boolean asFile) throws IOException {
        String path = file.getAbsolutePath();
        String dirPath;
        if (asFile) {
            dirPath = path.substring(0, path.lastIndexOf(File.separator));
        } else {
            dirPath = path;
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (asFile) {
            file.createNewFile();
        }
    }
}
