package org.ofbiz.content.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by galaxypan on 16.8.27.
 */
public class LocalFileManager extends FileTypeAbstractImpl {

    private GenericDelegator delegator;

    private final String filePath = File.separatorChar + "runtime" + File.separatorChar + "tempfiles" + File.separatorChar + "upload" + File.separatorChar;

    private String module = LocalFileManager.class.getName();

    public LocalFileManager(GenericDelegator delegator){
        super(delegator);
        this.delegator = delegator;
    }

    @Override
    public String storeFile(DataFileDescription fileDescription, GenericValue userLogin) {
        LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(null, delegator);
        String ofbizHome = System.getProperty("ofbiz.home");
        String uploadFolder = ofbizHome + filePath;
        File folder = new File(uploadFolder);
        boolean mkdirFlag;
        if(!folder.exists()){
            mkdirFlag = folder.mkdirs();
        }else{
            mkdirFlag = true;
        }
        if(!mkdirFlag){
            try {
                throw new Exception("文件夹未创建");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        try {
            String fileId = fileDescription.getFileId();
            String filePath = uploadFolder + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(fileDescription.getFileName());
            if(UtilValidate.isNotEmpty(fileId)) {
                GenericValue oldFile = EntityQuery.use(delegator).from("DataResource").where("dataResourceId", fileId).queryOne();
                if (oldFile != null) {
                    filePath = oldFile.getString("objectInfo");
                }
            }
            File file = new File(filePath);
            if(fileDescription.isOverwrite() && file.exists()){
                file.delete();
            }
            fileId = FileDataUtil.createFileVersion(userLogin.get("partyId").toString(), filePath, delegator,"LOCAL_FILE" ,fileDescription,dispatcher, userLogin);
            OutputStream out = new FileOutputStream(file);
            out.write(fileDescription.getContent());
            out.close();
            return fileId;
        } catch (Exception e) {
            Debug.logError(e, module);
            throw new RuntimeException("文件存储错误");
        }
    }

    @Override
    public byte[] getFileContent(String dataResourceId) {
        byte[] result = null;
        try {
            GenericValue file = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), true);
            String fileType = file.getString("dataResourceTypeId");
            if(!"LOCAL_FILE".equalsIgnoreCase(fileType)){
                throw new RuntimeException("DataResourceTypeId必须为LOCAL_FILE");
            }

            String filePath = file.getString("objectInfo");
            if(UtilValidate.isNotEmpty(filePath)){
                File targetFile = new File(filePath);
                if(targetFile.exists()){
                    FileInputStream input = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        input = new FileInputStream(targetFile);
                        baos = new ByteArrayOutputStream();
                        IOUtils.copy(input, baos);
                        result = baos.toByteArray();
                    }finally {
                        if(input != null){
                            input.close();
                        }
                        if(baos != null){
                            baos.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError(e, "获取文件内容错误", module);
        }
        return result;
    }

    @Override
    public InputStream getFileAsStream(String dataResourceId) {
        InputStream result = null;
        try {
            GenericValue file = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), true);
            String fileType = file.getString("dataResourceTypeId");
            if(!"LOCAL_FILE".equalsIgnoreCase(fileType)){
                throw new RuntimeException("DataResourceTypeId必须为LOCAL_FILE");
            }

            String filePath = file.getString("objectInfo");
            if(UtilValidate.isNotEmpty(filePath)){
                File targetFile = new File(filePath);
                if(targetFile.exists()){
                    result = new FileInputStream(targetFile);
                }
            }
        } catch (Exception e) {
            Debug.logError(e, "获取文件内容错误", module);
        }
        return result;
    }

    @Override
    public void delRealityFile(String dataResourceId) {
        try {
            GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",dataResourceId)).queryOne();
            if(fileInfo != null){
                new File(fileInfo.get("objectInfo").toString()).delete();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delFileHistoryList(String dataResourceId) {
        try {
            List<GenericValue> historyList = EntityQuery.use(delegator).select().from("TblHistoryFiles").where(UtilMisc.toMap("dataResourceId", dataResourceId)).queryList();
            delegator.removeByAnd("TblHistoryFiles", UtilMisc.toMap("dataResourceId", dataResourceId));
            if(UtilValidate.isNotEmpty(historyList)){
                for(GenericValue fileInfo : historyList){
                    String hisFileId = fileInfo.get("historyFileId").toString();
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",hisFileId)).queryOne();
                    if (UtilValidate.isNotEmpty(genericValue)){
                        String path = genericValue.get("objectInfo").toString();
                        File file = new File(path);
                        file.delete();
                        super.delFileInfo(hisFileId);
                    }
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File searchFileById(String dataResourceId, Boolean realFile) {
        try {
            GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",dataResourceId)).queryOne();
            if(fileInfo != null){
                String path = fileInfo.getString("objectInfo");
                File file =  new File(path);
                if(!realFile){
                    File f = new File(file.getParent() + File.separator + "copy" + File.separator + fileInfo.getString("dataResourceName"));
                    FileDataUtil.copyFromSourceToDestFile(file, f, false);
                    return f;
                }
                return file;
            }
        } catch (GenericEntityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
