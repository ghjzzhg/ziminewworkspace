package org.ofbiz.content.data;

import org.ofbiz.entity.GenericValue;

import java.io.File;
import java.io.InputStream;

/**
 * Created by galaxypan on 16.8.27.
 */
public interface FileTypeManager {

    /**
     * 实现文件存储
     * @param fileDescription fileDescription
     * @param userLogin userLogin
     * @return DataResourceId
     */
    String storeFile(DataFileDescription fileDescription, GenericValue userLogin);

    /**
     * 实现文件内容获取
     * @param dataResourceId
     * @return
     */
    byte[] getFileContent(String dataResourceId);

    InputStream getFileAsStream(String dataResourceId);

    /**
     * 删除文件
     * @param dataResourceId 文件ID
     */
    void delFile(String dataResourceId);

    /**
     * 当删除文件时，删除历史文件信息
     * @param dataResourceId 文件ID
     */
    void delFileHistoryList(String dataResourceId);


    /**
     * 根据id查找文件
     * 查找出文件信息，用户批量打包下载
     * @param dataResourceId
     * @return
     */
    File searchFileById(String dataResourceId, Boolean realFile);
}
