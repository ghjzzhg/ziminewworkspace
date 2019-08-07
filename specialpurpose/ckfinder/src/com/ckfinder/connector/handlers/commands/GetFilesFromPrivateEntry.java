package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.Constants;
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

import static com.ckfinder.connector.handlers.commands.Command.*;

/**
 * 从文件管理模块的私有文档入口进入
 * Created by galaxypan on 2017/9/14.
 */
public class GetFilesFromPrivateEntry extends AbstractGetFiles {

    public GetFilesFromPrivateEntry(Delegator delegator, String folderId, String loginUserPartyId, String loginUserGroupId, boolean loginUserIsCompanyUser) {
        super(delegator, folderId, loginUserPartyId, loginUserGroupId, loginUserIsCompanyUser);
    }

    @Override
    protected List<EntityCondition> searchFolderConditions() throws GenericEntityException {
        boolean topFolder =Constants.PRIVATE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录

        //changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 begin--------------
        //判断父文件夹是否是分享和私人文档,得到文件夹的权限
        if(topFolder) {
            setParentFolderPermissions(FILEPMS);
        }else{
            //子目录
            //从私有文档进入，则权限为私有文档本身的权限
            GenericValue baseFile = EntityQuery.use(delegator).select("folderPermissions").from("TblDirectoryStructure").where(UtilMisc.toMap("folderId", folderId, "folderType", Constants.PRIVATE_FOLDER_ENTRY)).queryOne();
            setParentFolderPermissions(baseFile.getString("folderPermissions"));
        }
        //如果权限不足或超过6位，则说明权限设置有问题，按默认设置
        if (parentFolderPermissions.length() != 6) {
            setParentFolderPermissions(FILEPMS);
        }
        //changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 end--------------

        List<Map<String, Object>> files = new ArrayList<>();
        String searchFolderId = topFolder ? null : folderId;
        //得到文件夹下所所有文件夹
        List conditionList = new ArrayList();
        conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
        conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
        Set<String> searchParty = new HashSet<>();
        searchParty.add(loginUserPartyId);
//        if(loginUserIsCompanyUser){//企业子账号与主账号共用文档管理,其他机构主账号、子账号相互独立
//            searchParty.add(loginUserGroupId);
//        }
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
        return conditionList;
    }

    @Override
    protected List<EntityCondition> searchFildConditions() {
        List fileConditionList = new ArrayList();
        List searchParty = new ArrayList();
        searchParty.add(loginUserPartyId);
        if(loginUserIsCompanyUser){//企业子账号与主账号共用文档管理
            searchParty.add(loginUserGroupId);
        }
        boolean topFolder =Constants.PRIVATE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录
        String searchFolderId = topFolder ? null : folderId;
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
