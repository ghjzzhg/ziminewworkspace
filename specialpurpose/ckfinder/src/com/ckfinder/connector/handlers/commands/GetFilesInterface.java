package com.ckfinder.connector.handlers.commands;

import org.ofbiz.entity.GenericEntityException;

import java.util.List;
import java.util.Map;

/**
 * Created by galaxypan on 2017/9/14.
 */
public interface GetFilesInterface {

    List<Map<String,Object>> getFiles() throws GenericEntityException;
    String getParentFolderPermission();
}
