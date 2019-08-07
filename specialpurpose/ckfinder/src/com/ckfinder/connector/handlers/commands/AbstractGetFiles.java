package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.ckfinder.connector.handlers.commands.Command.FILEPMS;
import static com.ckfinder.connector.handlers.commands.Command.FOLDERPMS;

/**
 * Created by galaxypan on 2017/9/14.
 */
public abstract class AbstractGetFiles implements GetFilesInterface {

    protected String parentFolderPermissions;
    protected Delegator delegator;
    protected String folderId;
    protected String loginUserPartyId;
    protected String loginUserGroupId;
    protected boolean loginUserIsCompanyUser;

    public AbstractGetFiles(Delegator delegator, String folderId, String loginUserPartyId, String loginUserGroupId, boolean loginUserIsCompanyUser) {
        this.delegator = delegator;
        this.folderId = folderId;
        this.loginUserPartyId = loginUserPartyId;
        this.loginUserGroupId = loginUserGroupId;
        this.loginUserIsCompanyUser = loginUserIsCompanyUser;
    }

    protected void setParentFolderPermissions(String parentFolderPermissions){
        this.parentFolderPermissions = parentFolderPermissions;
    }

    @Override
    public String getParentFolderPermission(){
        return parentFolderPermissions;
    }

    protected abstract List<EntityCondition> searchFolderConditions() throws GenericEntityException;
    protected abstract List<EntityCondition> searchFildConditions();

    protected abstract boolean showFileFolderPath();

    /**
     * 提供特殊的目录入口
     * @return
     */
    protected List<Map<String, Object>> extraFolders(){
        return null;
    }

    /**
     * 显示特殊的文件
     * @return
     */
    protected List<Map<String, Object>> extraFiles(){
        return null;
    }

    @Override
    public List<Map<String, Object>> getFiles() throws GenericEntityException {
        List<Map<String, Object>> files = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        List<EntityCondition> searchConditions = searchFolderConditions();
        System.out.println("searchConditions:"+searchConditions);
        List<GenericValue> folderList = EntityQuery.use(delegator).select("folderId", "folderName", "folderPermissions", "parentFolderId").from("DataResourceFolderList").where(searchConditions).orderBy("folderName").orderBy("createFolderTime DESC").distinct().queryList();
        List<String> folderIds = new ArrayList<>();
        List<String> folderPartyIds = new ArrayList<>();
        for(GenericValue folder : folderList){
            folderIds.add(folder.getString("folderId"));
            if(showFileFolderPath()) {
                folderIds.add(folder.getString("parentFolderId"));
            }
        }
        System.out.println("folderList:"+folderList);
        Map<String, GenericValue> folderMap = new HashMap<>();
        System.out.println("folderIds:"+folderIds);
        List<GenericValue> folderEntities = EntityQuery.use(delegator).select("folderId", "folderPath", "folderName", "partyId", "createPartyId", "createFolderTime", "foldeRemarks").from("TblDirectoryStructure").where(EntityCondition.makeCondition("folderId", EntityOperator.IN, folderIds), EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1")).queryList();
        if(UtilValidate.isEmpty(folderEntities)){
        	folderEntities = EntityQuery.use(delegator).select("folderId", "folderPath", "folderName", "partyId", "createPartyId", "createFolderTime", "foldeRemarks").from("TblDirectoryStructure").where(EntityCondition.makeCondition("folderId", EntityOperator.IN, folderIds), EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "2")).queryList();
        }
        System.out.println("folderEntities:"+folderEntities);
        for (GenericValue folder : folderEntities) {
            folderMap.put(folder.getString("folderId"), folder);
            folderPartyIds.add(folder.getString("partyId"));
            folderPartyIds.add(folder.getString("createPartyId"));
        }
        System.out.println("folderMap:"+folderMap);

        List<GenericValue> folderParties = EntityQuery.use(delegator).from("PartyNameInfo").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, folderPartyIds)).queryList();
        Map<String, String> partyNameMap = new HashMap<>();
        for (GenericValue folderParty : folderParties) {
            String partyId = folderParty.getString("partyId");
            String fullName = folderParty.getString("fullName");
            String groupName = folderParty.getString("groupName");
            partyNameMap.put(partyId, UtilValidate.isEmpty(fullName) ? groupName : fullName);
        }

        //遍历文件夹和文件信息，使文件夹和文件在同已页面中显示
        for(GenericValue folder : folderList){
            String fullName = "管理员";
            Map<String, Object> map = new HashMap<>();
            map.put("fileId",folder.get("folderId"));
            String folderName = folder.getString("folderName");
            map.put("fileName", folderName);
            map.put("fileType","folder");
            map.put("filePermissions",folder.get("folderPermissions") != null ? folder.get("folderPermissions") : FILEPMS);
            String folderaId = folder.get("folderId").toString();
            if(showFileFolderPath()){
                GenericValue parentFolder = folderMap.get(folder.get("parentFolderId"));
                if (parentFolder != null) {
                    String folderPath = parentFolder.getString("folderPath");
                    if(UtilValidate.isNotEmpty(folderPath)){
                        folderPath = folderPath.substring(1);
                        folderName = folderPath.substring(folderPath.indexOf("/")) + parentFolder.getString("folderName") + "/" + folderName;
                        map.put("fileName", folderName);
                    }
                }
            }
            //得到文件所属人的信息
            GenericValue folderInfo = folderMap.get(folderaId);
            if(folderInfo != null){
                map.put("partyId",folderInfo.get("partyId"));
                 fullName = partyNameMap.get(folderInfo.getString("createPartyId"));
                String fileFullName = partyNameMap.get(folderInfo.getString("partyId"));

                map.put("fullName",fileFullName == null ? "" : fileFullName);
                map.put("createFullName",fullName == null ? "" : fullName);
                map.put("remarks",folderInfo.get("foldeRemarks") != null ? folderInfo.get("foldeRemarks") : "");
                map.put("fileVersion", "");
                map.put("createTime", format.format(folderInfo.get("createFolderTime")));
                files.add(map);
            }
        }
        List<Map<String, Object>> extraFolders = extraFolders();
        if(CollectionUtils.isNotEmpty(extraFolders)){
            files.addAll(extraFolders);
        }
        System.out.println("searchFildConditions():"+searchFildConditions());
        List<GenericValue> fileList = EntityQuery.use(delegator).select().from("DataResourceFileList").where(searchFildConditions()).orderBy("fileName").orderBy("createFileTime DESC").queryList();
        System.out.println("fileList:"+fileList);
        List<String> fileIds = new ArrayList<>();
        List<String> filePartyIds = new ArrayList<>();
        for (GenericValue file : fileList) {
            fileIds.add(file.getString("fileId"));
        }
        Map<String, GenericValue> fileMap = new HashMap<>();
        List<GenericValue> fileEntities = EntityQuery.use(delegator).select().from("TblFileOwnership").where(EntityCondition.makeCondition("fileId", EntityOperator.IN, fileIds), EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, "1")).queryList();

        for (GenericValue file : fileEntities) {
            fileMap.put(file.getString("fileId"), file);
            filePartyIds.add(file.getString("partyId"));
            filePartyIds.add(file.getString("createPartyId"));
        }

        List<GenericValue> fileParties = EntityQuery.use(delegator).from("PartyNameInfo").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, filePartyIds)).queryList();
        partyNameMap = new HashMap<>();
        for (GenericValue fileParty : fileParties) {
            String partyId = fileParty.getString("partyId");
            String fullName = fileParty.getString("fullName");
            String groupName = fileParty.getString("groupName");
            partyNameMap.put(partyId, UtilValidate.isEmpty(fullName) ? groupName : fullName);
        }

        for(GenericValue file : fileList){
            String fullName = "管理员";
            String fileFullName = "";
            Map<String, Object> map = new HashMap<>();
            map.put("fileId",file.get("fileId"));
            String fileName = file.getString("fileName");
            if(showFileFolderPath()){
                String folderPath = file.getString("folderPath");
                if(UtilValidate.isNotEmpty(folderPath)){
                    folderPath = folderPath.substring(1);
                    fileName = folderPath.substring(folderPath.indexOf("/")) + file.getString("folderName") + "/" + fileName;
                }
            }
            map.put("fileName",fileName);
            map.put("fileType","file");
            map.put("filePermissions",file.get("filePermissions") != null ? file.get("filePermissions") : FILEPMS);
            GenericValue fileInfo = fileMap.get(file.getString("fileId"));
            if(fileInfo != null){
                map.put("partyId",fileInfo.get("partyId"));

                fullName = partyNameMap.get(fileInfo.getString("createPartyId"));
                fileFullName = partyNameMap.get(fileInfo.getString("partyId"));

                map.put("fullName",fileFullName == null ? "" : fileFullName);
                map.put("createFullName",fullName == null ? "" : fullName);
                map.put("fileVersion",fileInfo.get("fileVersion") != null ? fileInfo.get("fileVersion") : "");
                map.put("remarks","");
                map.put("createTime", format.format(fileInfo.get("createFileTime")));
                files.add(map);
            }
        }
        List<Map<String, Object>> extraFiles = extraFiles();
        if(CollectionUtils.isNotEmpty(extraFiles)){
            files.addAll(extraFiles);
        }

        Collections.sort(files, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String fullName1 = (String) o1.get("fullName");
                String fileName1 = (String) o1.get("fileName");
                String fullName2 = (String) o2.get("fullName");
                String fileName2 = (String) o2.get("fileName");
                int fullNameResult = fullName1.compareTo(fullName2);
                return fullNameResult == 0 ? fileName1.compareTo(fileName2) : fullNameResult;
            }
        });
        return files;
    }

    protected String getFilePartyName(String partyId) throws GenericEntityException {
        GenericValue partyInfo = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId", partyId)).queryOne();
        if(partyInfo.get("partyTypeId").equals("PARTY_GROUP")){
            GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",partyId)).queryOne();
            return partyGroup.get("groupName").toString();
        }else{
            GenericValue person = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",partyId)).queryOne();
            if(UtilValidate.isNotEmpty(person.get("fullName"))){
                return person.get("fullName").toString();
            }
        }
        return "";
    }
}
