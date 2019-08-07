package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.errors.DuplicateNameException;
import org.apache.commons.collections.CollectionUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class CkfinderServices {
    public static final String module = CkfinderServices.class.getName();
    /**
     * 查询登陆人分享数据
     *
     * @return
     */
    public static Map<String, Object> showShareInfoList(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }
        int totalCount = 0;
        int viewSize = 5;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 5;
        }
        // 计算当前显示页的最小、最大索引(可能会超出实际条数)
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;
        Map<String, Object> dataResourceMap = new HashMap<String, Object>();
        String fileFlag = (String) context.get("fileFlag");
        String fileId = (String) context.get("fileId");
        EntityListIterator dataResourceIterator;
        List<Map> dataResourceList = new ArrayList<Map>();
        try {
            if ("file".equals(fileFlag)) {
                dataResourceIterator = EntityQuery.use(delegator).select().from("DataResourceFileList").where(UtilMisc.toMap("fileId", fileId, "fileType", "2")).queryIterator();
            } else {
                dataResourceIterator = EntityQuery.use(delegator).select().from("DataResourceFolderList").where(UtilMisc.toMap("folderId", fileId, "folderType", "2")).queryIterator();
            }
            if (null != dataResourceIterator && dataResourceIterator.getResultsSizeAfterPartialList() > 0) {
                totalCount = dataResourceIterator.getResultsSizeAfterPartialList();
                List<GenericValue> dataResources = dataResourceIterator.getPartialList(lowIndex, viewSize);
                for (GenericValue dataResource : dataResources) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    if ("file".equals(fileFlag)) {
                        map.put("fileId", dataResource.getString("fileId"));
                    } else {
                        map.put("fileId", dataResource.getString("folderId"));
                    }
                    String dataPartyId = dataResource.getString("partyId");
                    map.put("partyId", dataPartyId);
                    String partyName;
                    GenericValue party = EntityQuery.use(delegator).select().from("Party").where(EntityCondition.makeCondition("partyId", dataPartyId)).queryOne();
                    if ("PERSON".equals(party.getString("partyTypeId"))) {
                        GenericValue partyGroup = EntityQuery.use(delegator).select().from("Person").where(EntityCondition.makeCondition("partyId", dataPartyId)).queryOne();
                        partyName = partyGroup.getString("fullName");
                    } else {
                        GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(EntityCondition.makeCondition("partyId", dataPartyId)).queryOne();
                        partyName = partyGroup.getString("groupName");

                    }
                    map.put("partyName", partyName);
                    dataResourceList.add(map);
                }
            }
            dataResourceIterator.close();
        }catch (Exception e){
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        dataResourceMap.put("dataResourceList", dataResourceList);
        dataResourceMap.put("viewIndex", viewIndex);
        dataResourceMap.put("highIndex", highIndex);
        dataResourceMap.put("totalCount", totalCount);
        dataResourceMap.put("viewSize", viewSize);
        successResult.put("dataResourceMap", dataResourceMap);
        return successResult;
    }

    /**
     * 设置目录共享
     *
     * @return
     */
    public static Map<String, Object> shareUserFolder(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyIdTo = (String) context.get("partyIdTo");
        String folderPath = (String) context.get("folderPath");
        String permissionStr = (String) context.get("permissionStr");
        if (UtilValidate.isEmpty(permissionStr)) {
            permissionStr = Command.FOLDERPMS;
        }
        String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1, folderPath.length());
        String parentFolderPath = folderPath.substring(0, folderPath.lastIndexOf("/") + 1);
        List conditionList = new ArrayList();
        if (parentFolderPath.equals("/")) {
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, null));
        } else {
            conditionList.add(EntityCondition.makeCondition("folderPath", EntityOperator.EQUALS, "/" + partyIdFrom + parentFolderPath));
        }
        conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
        conditionList.add(EntityCondition.makeCondition("folderName", EntityOperator.EQUALS, folderName));
        GenericValue folderInfo = null;
        try {
            folderInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(conditionList).queryOne();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        List<String> dataIdList = new ArrayList<String>();
        dataIdList.add(partyIdTo);
        String msg = FileService.saveShare(dataIdList, folderInfo.get("folderId").toString(), delegator, "folder", permissionStr);
        successResult.put("msg", msg);
        return successResult;
    }

    /**
     * 删除目录共享
     *
     * @return
     */
    public static Map<String, Object> delShareUserFolder(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyIdTo = (String) context.get("partyIdTo");
        String folderPath = (String) context.get("folderPath");
        String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1, folderPath.length());
        String parentFolderPath = folderPath.substring(0, folderPath.lastIndexOf("/") + 1);
        List conditionList = new ArrayList();
        if (parentFolderPath.equals("/")) {
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, null));
        } else {
            conditionList.add(EntityCondition.makeCondition("folderPath", EntityOperator.EQUALS, "/" + partyIdFrom + parentFolderPath));
        }
        conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
        conditionList.add(EntityCondition.makeCondition("folderName", EntityOperator.EQUALS, folderName));
        GenericValue folderInfo = null;
        try {
            folderInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(conditionList).queryOne();
            delegator.removeByAnd("TblDirectoryStructure", UtilMisc.toMap("folderId", folderInfo.get("folderId"), "partyId", partyIdTo, "folderType", "2"));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }

        successResult.put("msg", "删除成功");
        return successResult;
    }

    /**
     * 系统账户需要调用接口给某个账户添加文件夹
     *
     * @return
     */
    public static Map<String, Object> addUserFolder(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String parentFolderId = (String) context.get("parentFolderId");
        String partyId = (String) context.get("partyId");
        String parentPath;
        if (UtilValidate.isNotEmpty(parentFolderId)) {
            GenericValue parentFolderInfo = null;
            try {
                parentFolderInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId", parentFolderId, "folderType", "1")).queryOne();
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (UtilValidate.isNotEmpty(parentFolderInfo)) {
                if (UtilValidate.isNotEmpty(parentFolderInfo.get("folderPath"))) {
                    parentPath = parentFolderInfo.get("folderPath").toString() + parentFolderInfo.get("folderName") + "/";
                } else {
                    parentPath = "/" + partyId + parentFolderInfo.get("folderName") + "/";
                }
            } else {
                parentPath = "/" + partyId + "/";
            }
        } else {
            parentPath = "/" + partyId + "/";
        }
        String folderName = (String) context.get("folderName");

        //如果是文件夹放置到TblDirectoryStructure
        String id = delegator.getNextSeqId("TblDirectoryStructureId");
        String folderId = delegator.getNextSeqId("TblDirectoryStructure");
        Map<String, Object> folderMap = new HashMap<>();
        folderMap.put("folderId", folderId);
        if (!UtilValidate.isEmpty(parentFolderId)) {
            folderMap.put("parentFolderId", parentFolderId);
        }
        folderMap.put("id", id);
        folderMap.put("folderType", "1");
        folderMap.put("partyId", partyId);
        String permissionStr = (String) context.get("permissionStr");
        folderMap.put("folderPermissions", permissionStr);
        String description = (String) context.get("description");
        folderMap.put("foldeRemarks", description);
        folderMap.put("folderName", folderName);
        folderMap.put("folderPath", parentPath);
        folderMap.put("createPartyId", userLogin.get("partyId"));
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        folderMap.put("createFolderTime", timestamp);
        GenericValue folder = delegator.makeValue("TblDirectoryStructure", folderMap);
        try {
            folder.create();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        successResult.put("folderId", folderId);
        return successResult;
    }

    /**
     * 根据文件名称搜索文件信息
     * @param ctx
     * @param context
     * @return 文件列表
     * @throws GenericEntityException
     */
    public Map<String,Object> searchFileListByName(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = userLogin.getString("partyId");
        String fileName = context.get("fileName").toString();
        String folderId = context.get("folderId").toString();
        String rootFolderId = context.get("rootFolderId").toString();
        List<Map<String,Object>> fileList = new ArrayList<>();
        List<Map<String,Object>> files = new ArrayList<>();
        if(UtilValidate.isNotEmpty(fileName)){
            List conditionList = new ArrayList<>();
            if(!folderId.equals("1") && !folderId.equals("2")){
                conditionList.add(EntityCondition.makeCondition("folderStructure", EntityOperator.LIKE, "%" + folderId + "%" ));
            }
            conditionList.add(EntityCondition.makeCondition("fileName", EntityOperator.LIKE,  "%" + fileName + "%" ));
            //处理私有文档的情况、分享文档查询所有，后处理
            if(rootFolderId.equals("1")){
                conditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, rootFolderId));
                conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
            }

            EntityCondition cond1 = EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1");
            EntityCondition cond2 = EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, null);
            conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2), EntityOperator.OR));

            List<GenericValue> genericValues = EntityQuery.use(delegator).from("DataResourceFileList").where(conditionList).distinct().queryList();
            if(UtilValidate.isNotEmpty(genericValues)){
                if(rootFolderId.equals("2")){
                    for(GenericValue fileInfo : genericValues){
                        String fileType = fileInfo.get("fileType").toString();
                        String filePartyId = fileInfo.get("partyId").toString();
                        if(fileType.equals("2") && filePartyId.equals(partyId)){
                            fileList.add(fileInfo);
                        }else{
                            //得到文件目录文件id列表
                            if(UtilValidate.isNotEmpty(fileInfo.get("folderStructure"))){
                                String filePathIds = fileInfo.get("folderStructure").toString();
                                String[] filePathList = filePathIds.split(";");
                                //循环文件id判断文件是否在分享文件中是否归属当前登陆人
                                for(String foldersId : filePathList){
                                    GenericValue folderInfo = EntityQuery.use(delegator).from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",foldersId,"folderType","2","partyId",partyId)).queryOne();
                                    if(UtilValidate.isNotEmpty(folderInfo)){
                                        fileList.add(fileInfo);
                                    }
                                }
                            }
                        }
                    }
                }else{
                    fileList.addAll(genericValues);
                }
            }
        }
        //路径处理
        for(Map<String,Object> map : fileList){
            if(rootFolderId.equals("2")){
                if(UtilValidate.isNotEmpty(map.get("folderType"))){
                    String folderType = map.get("folderType").toString();
                    if(folderType.equals("2")){
                        continue;
                    }
                }
            }
            GenericValue party = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",map.get("createPartyId"))).queryOne();
            String fullName = map.get("createFullName") != null ? map.get("createFullName").toString() : "";
            if(party.get("partyTypeId").equals("PARTY_GROUP")){
                GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",map.get("createPartyId"))).queryOne();
                fullName = partyGroup.get("groupName").toString();
            }
            map.put("createFullName",fullName);
            Map<String,Object> fileMap = new HashMap<>();
            fileMap.putAll(map);
            StringBuffer filePath = new StringBuffer();
            if(rootFolderId.equals("1")){
                filePath.append("私有文档/");
            }else{
                filePath.append("分享文档/");
            }
            if(UtilValidate.isNotEmpty( map.get("folderStructure"))){
                String path = map.get("folderStructure").toString();
                String[] filePathList = path.split(";");
                for(String id : filePathList){
                    GenericValue folderInfo = EntityQuery.use(delegator).from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",id,"folderType","1")).queryOne();
                    filePath.append(folderInfo.get("folderName") + "/");
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            fileMap.put("createTime", format.format(fileMap.get("createFileTime")));
            fileMap.put("filePath", filePath);
            //标识，用于区分是资料库，还是文档管理
            fileMap.put("isLibrary","N");
            files.add(fileMap);
        }
        successResult.put("data", files);
        return successResult;
    }

    /**
     * 更新目录名称
     * @param ctx
     * @param context
     * @return 文件列表
     * @throws GenericEntityException
     */
    public Map<String,Object> renameFolder(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String newFolderName = context.get("newFolderName").toString();
        String newfoldeRemark = context.get("newfoldeRemark").toString();
        String folderId = context.get("folderId").toString();
        try {
            GenericValue oriFolderInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",folderId,"folderType", "1")).queryOne();
            List<GenericValue> sharedFolders = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",folderId,"folderType", "2")).queryList();
            List list = new ArrayList();
            list.add(EntityCondition.makeCondition("folderName", EntityOperator.EQUALS, newFolderName));
            list.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, oriFolderInfo.get("parentFolderId")));
            list.add(EntityCondition.makeCondition("folderId", EntityOperator.NOT_EQUAL, folderId));
            list.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
            List<GenericValue> filesList = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(list).queryList();
            if(UtilValidate.isNotEmpty(filesList)){//同级目录下重名判断
                throw new DuplicateNameException();
            }
            String path = oriFolderInfo.get("folderPath").toString() + oriFolderInfo.get("folderName") + "/";
            String newPath = oriFolderInfo.get("folderPath").toString() + newFolderName;
            List conditionList = new ArrayList();
            conditionList.add(EntityCondition.makeCondition("folderPath", EntityOperator.LIKE, path+"%"));
            EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
            List<GenericValue> folderList = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(condition).queryList();
            path = path.substring(0,path.length() - 1);
            //更新子目录中的路径信息
            for(GenericValue folder : folderList){
                String fileSubPath = folder.get("folderPath").toString().replace(path, newPath);
                folder.put("folderPath", fileSubPath);
//                folder.put("foldeRemarks", newfoldeRemark);//changed by galaxypan@2017-10-06:子目录备注不用更新
                folder.store();
            }

            //更新目录下文件的路径信息
            List fileConditionList = new ArrayList();
            fileConditionList.add(EntityCondition.makeCondition("objectInfo", EntityOperator.LIKE, path+"%"));
            EntityConditionList filecondition = EntityCondition.makeCondition(UtilMisc.toList(fileConditionList));
            List<GenericValue> fileList = EntityQuery.use(delegator).select().from("DataResource").where(filecondition).queryList();
            for(GenericValue folder : fileList){
                String fileSubPath = folder.get("objectInfo").toString().replace(path, newPath);
                folder.put("objectInfo", fileSubPath);
                folder.store();
            }
            //更新所选目录的名称
            oriFolderInfo.put("foldeRemarks", newfoldeRemark);
            oriFolderInfo.put("folderName", newFolderName);
            oriFolderInfo.store();

            //更新共享目录的信息
            if(CollectionUtils.isNotEmpty(sharedFolders)){
                for (GenericValue sharedFolder : sharedFolders) {
                    sharedFolder.set("folderName", newFolderName);
                    sharedFolder.set("foldeRemarks", newfoldeRemark);
                }
                delegator.storeAll(sharedFolders);
            }
        } catch (GenericEntityException e) {
            throw e;
        }
        return successResult;
    }
}