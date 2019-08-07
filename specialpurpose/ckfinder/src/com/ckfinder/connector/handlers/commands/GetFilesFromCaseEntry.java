package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.Constants;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import java.util.*;

import static com.ckfinder.connector.handlers.commands.Command.FILEPMS;
import static com.ckfinder.connector.handlers.commands.Command.FOLDERPMS;

/**
 * 从文件管理模块的CASE文档入口进入
 * Created by galaxypan on 2017/9/14.
 */
public class GetFilesFromCaseEntry extends AbstractGetFiles {

    public GetFilesFromCaseEntry(Delegator delegator, String folderId, String loginUserPartyId, String loginUserGroupId, boolean loginUserIsCompanyUser) {
        super(delegator, folderId, loginUserPartyId, loginUserGroupId, loginUserIsCompanyUser);
    }

    @Override
    protected List<EntityCondition> searchFolderConditions() throws GenericEntityException {
        boolean topFolder = Constants.CASE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录

        //changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 begin--------------
        //判断父文件夹是否是分享和私人文档,得到文件夹的权限
        if(topFolder) {
            setParentFolderPermissions(FOLDERPMS);
        }else{
            //子目录
            //从私有文档进入，则权限为私有文档本身的权限
            GenericValue baseFile = EntityQuery.use(delegator).select("folderPermissions").from("TblDirectoryStructure").where(UtilMisc.toMap("folderId", folderId, "folderType", Constants.PRIVATE_FOLDER_TYPE)).queryOne();
            setParentFolderPermissions(baseFile.getString("folderPermissions"));
        }
        //如果权限不足或超过6位，则说明权限设置有问题，按默认设置
        if (parentFolderPermissions.length() != 6) {
            setParentFolderPermissions(FOLDERPMS);
        }
        //changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 end--------------
        
//        System.out.println(">>>>>>>>>>>"+loginUserGroupId);
//        System.out.println("<<<<<<<<<<<"+loginUserPartyId);
        
        if(loginUserGroupId.equals(loginUserPartyId)){
        	List<Map<String, Object>> files = new ArrayList<>();
            String searchFolderId = topFolder ? Constants.CASE_FOLDER_ENTRY : folderId;//CASE文档实际为私有文档类型下的特殊目录
            //得到文件夹下所所有文件夹
            List conditionList = new ArrayList();
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
            conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
            Set<String> searchParty = new HashSet<>();
            searchParty.add(loginUserPartyId);
            
//            System.out.println("loginUserGroupId>>>>>>>>>>>======="+loginUserGroupId+"<<<<<<<<<<<==========");
            if(loginUserIsCompanyUser){//企业子账号与主账号共用文档管理,其他机构主账号、子账号相互独立
                searchParty.add(loginUserGroupId);
            }

            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
            return conditionList;
        }else{
        	//changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 end--------------

            List<Map<String, Object>> files = new ArrayList<>();
            String searchFolderId = topFolder ? Constants.CASE_FOLDER_ENTRY : folderId;//CASE文档实际为私有文档类型下的特殊目录
//            System.out.println("searchFolderId--------"+searchFolderId);
            //得到文件夹下所所有文件夹
            List conditionList = new ArrayList();
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
            conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
            Set<String> searchParty = new HashSet<>();
            searchParty.add(loginUserPartyId);
//            System.out.println("searchParty------111111"+searchParty);
            //1、获取到case_id为：10750、10780---SELECT CASE_ID FROM tbl_case_party_member WHERE PARTY_ID = 10859;
            List<GenericValue> caseList = EntityQuery.use(delegator).select("caseId").from("TblCasePartyMember").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty)).queryList();
//            System.out.println("caseList--------------00000------"+caseList+"======================");
            List<String> caseIds = new ArrayList<>();
            for(GenericValue caseId : caseList){
            	caseIds.add(caseId.getString("caseId"));
            }
//            System.out.println("caseIds--------------11111------"+caseIds+"======================");
//          2、根据获取到的case_id获取title---SELECT FOLDER_ID FROM tbl_case WHERE CASE_ID = 10750 or CASE_ID = 10780;
            List<GenericValue> foldIdList = EntityQuery.use(delegator).select("folderId").from("TblCase").where(EntityCondition.makeCondition("caseId", EntityOperator.IN, caseIds)).queryList();
//            System.out.println("foldIdList--------------00000------"+foldIdList+"======================");
            List<String> folderIds = new ArrayList<>();
            for(GenericValue folderId : foldIdList){
            	folderIds.add(folderId.getString("folderId"));
            }
//            System.out.println("folderIds--------------00000------"+folderIds+"======================");
            
            conditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.IN, folderIds));
            if(loginUserIsCompanyUser){//企业子账号与主账号共用文档管理,其他机构主账号、子账号相互独立
                searchParty.add(loginUserGroupId);
            }
//            3、当tbl_case表中的title和tbl_directory_structure中的folder_name相同
//            SELECT * FROM tbl_directory_structure td WHERE FOLDER_ID IN (SELECT FOLDER_ID FROM tbl_case 
//            WHERE CASE_ID IN (SELECT CASE_ID FROM tbl_case_party_member WHERE PARTY_ID = 10859)) 
//            AND PARENT_FOLDER_ID = 3 AND FOLDER_TYPE = 1 AND PARTY_ID = 10856;
            
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
//            System.out.println("conditionList-----"+conditionList);
            return conditionList;
        }     
    }

    @Override
    protected List<EntityCondition> searchFildConditions() {
        List fileConditionList = new ArrayList();
        List searchParty = new ArrayList();
        searchParty.add(loginUserPartyId);
        if(loginUserIsCompanyUser){//企业子账号与主账号共用文档管理
            searchParty.add(loginUserGroupId);
        }
        boolean topFolder =Constants.CASE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录
        String searchFolderId = topFolder ?  Constants.CASE_FOLDER_ENTRY: folderId;
        fileConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
        fileConditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
        fileConditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, searchFolderId));
        return fileConditionList;
    }

    @Override
    protected boolean showFileFolderPath() {
        return false;
    }
}
