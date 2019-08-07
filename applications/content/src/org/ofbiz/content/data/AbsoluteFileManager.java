package org.ofbiz.content.data;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;

import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by galaxypan on 16.8.27.
 */
public class AbsoluteFileManager extends FileTypeAbstractImpl {

    private GenericDelegator delegator;

    private String module = AbsoluteFileManager.class.getName();

    public AbsoluteFileManager(GenericDelegator delegator) {
        super(delegator);
        this.delegator = delegator;
    }

    @Override
    public String storeFile(DataFileDescription fileDescription, GenericValue userLogin) {
        LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(null, delegator);
        String fileHome = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);

        File folder = new File(fileHome);
        boolean mkdirFlag;
        if (!folder.exists()) {
            mkdirFlag = folder.mkdirs();
        } else {
            mkdirFlag = true;
        }
        if (!mkdirFlag) {
            try {
                throw new Exception("文件夹未创建");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        try {
            String fileId = fileDescription.getFileId();
            String filePath = fileHome + File.separatorChar + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(fileDescription.getFileName());
            if (UtilValidate.isNotEmpty(fileId)) {
                GenericValue oldFile = EntityQuery.use(delegator).from("DataResource").where("dataResourceId", fileId).queryOne();
                if (oldFile != null) {
                    filePath = fileHome + File.separatorChar + oldFile.getString("objectInfo");
                }
            }
            File file = new File(filePath);
            fileId = FileDataUtil.createFileVersion(userLogin.get("partyId").toString(), file.getName(), delegator, "ZXDOC_FILE", fileDescription, dispatcher, userLogin);
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
            String fileHome = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);
            if (!fileHome.endsWith(File.separator)) {
                fileHome += File.separator;
            }
            GenericValue file = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), true);
            String fileType = file.getString("dataResourceTypeId");
            if (!"ZXDOC_FILE".equalsIgnoreCase(fileType)) {
                throw new RuntimeException("DataResourceTypeId必须为ZXDOC_FILE");
            }

            String filePath = file.getString("objectInfo");
            if (UtilValidate.isNotEmpty(filePath)) {
                File targetFile = new File(fileHome + filePath);
                if (targetFile.exists()) {
                    FileInputStream input = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        input = new FileInputStream(targetFile);
                        baos = new ByteArrayOutputStream();
                        IOUtils.copy(input, baos);
                        result = baos.toByteArray();
                    } finally {
                        if (input != null) {
                            input.close();
                        }
                        if (baos != null) {
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
            String fileHome = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);
            if (!fileHome.endsWith(File.separator)) {
                fileHome += File.separator;
            }
            GenericValue file = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), true);
            String fileType = file.getString("dataResourceTypeId");
            if (!"ZXDOC_FILE".equalsIgnoreCase(fileType)) {
                throw new RuntimeException("DataResourceTypeId必须为ZXDOC_FILE");
            }

            String filePath = file.getString("objectInfo");
            if (UtilValidate.isNotEmpty(filePath)) {
                File targetFile = new File(fileHome + filePath);
                if (targetFile.exists()) {
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
            String fileHome = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);
            if (!fileHome.endsWith(File.separator)) {
                fileHome += File.separator;
            }
            GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId", dataResourceId)).queryOne();
            if (fileInfo != null) {
                new File(fileHome + fileInfo.get("objectInfo")).delete();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delFileHistoryList(String fileId) {
        try {
            String fileHome = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);
            if (!fileHome.endsWith(File.separator)) {
                fileHome += File.separator;
            }
            List<GenericValue> historyList = EntityQuery.use(delegator).select().from("TblHistoryFiles").where(UtilMisc.toMap("dataResourceId", fileId)).queryList();
            delegator.removeByAnd("TblHistoryFiles", UtilMisc.toMap("dataResourceId", fileId));
            if (UtilValidate.isNotEmpty(historyList)) {
                for (GenericValue fileInfo : historyList) {
                    String hisFileId = fileInfo.get("historyFileId").toString();
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId", hisFileId)).queryOne();
                    String path = fileHome + genericValue.get("objectInfo");
                    File file = new File(path);
                    file.delete();
                    super.delFileInfo(hisFileId);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File searchFileById(String dataResourceId, Boolean realFile) {
        String fileHome = EntityUtilProperties.getPropertyValue("content.properties", "dataResourcePath", "localhost", delegator);
        if (!fileHome.endsWith(File.separator)) {
            fileHome += File.separator;
        }
        try {
            GenericValue fileInfo = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId", dataResourceId)).queryOne();
            if (fileInfo != null) {
                File file = new File(fileHome + fileInfo.get("objectInfo").toString());
                if (!realFile) {
                    File f = new File(fileHome + "copy" + File.separator + fileInfo.getString("dataResourceName"));
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
