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
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.ckfinder.connector.handlers.commands.Command.FILEPMS;
import static com.ckfinder.connector.handlers.commands.Command.FOLDERPMS;

/**
 * 从文件管理模块的共享文档入口进入
 * Created by galaxypan on 2017/9/14.
 */
public class GetFilesFromShareEntry extends AbstractGetFiles {
    public GetFilesFromShareEntry(Delegator delegator, String folderId, String loginUserPartyId, String loginUserGroupId, boolean loginUserIsCompanyUser) {
        super(delegator, folderId, loginUserPartyId, loginUserGroupId, loginUserIsCompanyUser);
    }

    @Override
    protected List<EntityCondition> searchFolderConditions() throws GenericEntityException {
        boolean topFolder = Constants.SHARE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录
        //changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 begin--------------
        //判断父文件夹是否是分享和私人文档,得到文件夹的权限
        if(topFolder) {
            setParentFolderPermissions(FOLDERPMS);
        }else{
            //子目录
            //changed by galaxypan@2017-09-12:共享文件夹中因同一个文件或子目录可能在不同的case下设置共享，权限可能有所不同（暂时未实现细粒度权限），采用“或”方式获取最大权限

            //changed by galaxypan@2017-10-22:共享目录默认权限
            setParentFolderPermissions("011111");
            /*List permissionCons = new ArrayList();
            permissionCons.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, loginUserPartyId), EntityJoinOperator.OR, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, loginUserGroupId)));
            permissionCons.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, folderId));
            permissionCons.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.SHARE_FOLDER_TYPE));
            List<GenericValue> genericValuePms = EntityQuery.use(delegator).select("folderPermissions").from("TblDirectoryStructure").where(permissionCons).queryList();
            int start = 0;
            if (UtilValidate.isNotEmpty(genericValuePms)) {
                for (GenericValue genericValuePm : genericValuePms) {
                    start = start | Integer.parseInt(genericValuePm.getString("folderPermissions"), 2);
                }
            }
            setParentFolderPermissions(StringUtils.leftPad(Integer.toBinaryString(start), 6, "0"));*/
        }
        //如果权限不足或超过6位，则说明权限设置有问题，按默认设置
        if (parentFolderPermissions.length() != 6) {
            setParentFolderPermissions(FOLDERPMS);
        }
        //changed by galaxypan@2017-09-13: -------------获取当前文件夹的权限 end--------------

        List<Map<String, Object>> files = new ArrayList<>();
        String searchFolderId = topFolder ? null : folderId;
        //得到文件夹下所所有文件夹
        List conditionList = new ArrayList();
        //顶级目录需要查询共享给当前用户或其主账户的。非顶级目录则直接查询其所有子文件夹，因为能进入非顶级目录，则证明该文件夹整个共享给了当前用户或其主账号
        if(topFolder){//将共享的文件夹直接显示在最顶级
            conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
            Set<String> searchParty = new HashSet<>();
            searchParty.add(loginUserGroupId);//共享文档查询共享给当前用户或者当前用户的主账号的
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
        }else{//目录下所有实际的文件夹，而不含那些共享记录
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
            conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
        }
        return conditionList;
    }

    @Override
    protected List<EntityCondition> searchFildConditions() {
        boolean topFolder = Constants.PRIVATE_FOLDER_TYPE.equals(folderId);//当前目录是否是顶级目录
        String searchFolderId = topFolder ? null : folderId;
        List conditionList = new ArrayList();
        conditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, searchFolderId));
        //顶级目录需要查询共享给当前用户或其主账户的。非顶级目录则直接查询其所有文件，因为能进入非顶级目录，则证明该文件夹整个共享给了当前用户或其主账号
        if(topFolder){
            conditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, Constants.SHARE_FOLDER_ENTRY));
            Set<String> searchParty = new HashSet<>();
            searchParty.add(loginUserPartyId);
            searchParty.add(loginUserGroupId);//共享文档查询共享给当前用户或者当前用户的主账号的
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
        }else{//目录下所有实际的文件，而不含那些共享记录
        	Set<String> searchParty = new HashSet<>();
        	searchParty.add(loginUserPartyId);
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
            conditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, Constants.SHARE_FOLDER_ENTRY));
        }
        return conditionList;
    }

    @Override
    protected boolean showFileFolderPath() {
        return Constants.SHARE_FOLDER_ENTRY.equals(folderId);
    }
}
