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
 * 从文件管理模块之外的模块的进入共享文档
 * Created by galaxypan on 2017/9/14.
 */
public class GetFilesFromModuleShareEntry extends AbstractGetFiles {
    protected String moduleType;//模块类型标识，例如case
    protected String moduleId;//该模块内对应的业务记录id，例如caseId
    protected String ownerPartyId;//共享文件夹所属party

    public GetFilesFromModuleShareEntry(Delegator delegator, String folderId, String loginUserPartyId, String loginUserGroupId, boolean loginUserIsCompanyUser, String moduleType, String moduleId, String ownerPartyId) {
        super(delegator, folderId, loginUserPartyId, loginUserGroupId, loginUserIsCompanyUser);
        this.moduleType = moduleType;
        this.moduleId = moduleId;
        this.ownerPartyId = ownerPartyId;
    }

    @Override
    protected List<EntityCondition> searchFolderConditions() throws GenericEntityException {
        boolean topFolder = Constants.SHARE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录
        String searchFolderId = topFolder ? null : folderId;
        //得到文件夹下所所有文件夹
        List conditionList = new ArrayList();
        //顶级目录需要查询共享给当前用户或其主账户的。非顶级目录则直接查询其所有子文件夹，因为能进入非顶级目录，则证明该文件夹整个共享给了当前用户或其主账号
        if(topFolder){//将共享的文件夹直接显示在最顶级
            setParentFolderPermissions(FOLDERPMS);

            conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.SHARE_FOLDER_TYPE));
            Set<String> searchParty = new HashSet<>();
            searchParty.add(loginUserPartyId);
            searchParty.add(loginUserGroupId);//共享文档查询共享给当前用户或者当前用户的主账号的
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
            //仅限在所在业务模块内共享的文件夹
            if(UtilValidate.isNotEmpty(moduleType)){
                conditionList.add(EntityCondition.makeCondition("shareFromModule", EntityOperator.EQUALS, moduleType));
                conditionList.add(EntityCondition.makeCondition("shareFromModuleId", EntityOperator.EQUALS, moduleId));
            }
            //限定为指定的某个账户共享出来的文件夹
            conditionList.add(EntityCondition.makeCondition("folderPath", EntityOperator.LIKE, "/" + ownerPartyId + "/%"));

        }else{//目录下所有实际的文件夹，而不含那些共享记录
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, searchFolderId));
            conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));

            //子目录
            ////changed by galaxypan@2017-09-15:非文件管理模块中共享子目录严格按照该父级目录共享时设置的权限
            List permissionCons = new ArrayList();
            permissionCons.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, loginUserPartyId), EntityJoinOperator.OR, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, loginUserGroupId)));
            if (UtilValidate.isNotEmpty(moduleType)) {
                permissionCons.add(EntityCondition.makeCondition("shareFromModule", EntityOperator.EQUALS, moduleType));
                permissionCons.add(EntityCondition.makeCondition("shareFromModuleId", EntityOperator.EQUALS, moduleId));
            }
            permissionCons.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, folderId));
            permissionCons.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, Constants.SHARE_FOLDER_TYPE));
            List<GenericValue> genericValuePms = EntityQuery.use(delegator).select("folderPermissions").from("TblDirectoryStructure").where(permissionCons).queryList();
            int start = 0;
            if (UtilValidate.isNotEmpty(genericValuePms)) {
                for (GenericValue genericValuePm : genericValuePms) {
                    start = start | Integer.parseInt(genericValuePm.getString("folderPermissions"), 2);
                }
                setParentFolderPermissions(StringUtils.leftPad(Integer.toBinaryString(start), 6, "0"));
            }else{//没有直接共享子文件夹时，采用共享默认权限
                setParentFolderPermissions("000111");
            }

        }
        return conditionList;
    }

    @Override
    protected List<EntityCondition> searchFildConditions() {
        boolean topFolder = Constants.SHARE_FOLDER_ENTRY.equals(folderId);//当前目录是否是顶级目录
        String searchFolderId = topFolder ? null : folderId;
        //得到文件夹下所所有文件夹
        List conditionList = new ArrayList();
        //顶级目录需要查询共享给当前用户或其主账户的。非顶级目录则直接查询其所有文件，因为能进入非顶级目录，则证明该文件夹整个共享给了当前用户或其主账号
        if(topFolder){
            conditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, Constants.SHARE_FOLDER_TYPE));
            Set<String> searchParty = new HashSet<>();
            searchParty.add(loginUserPartyId);
            searchParty.add(loginUserGroupId);//共享文档查询共享给当前用户或者当前用户的主账号的
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, searchParty));
            //仅限在所在业务模块内共享的文件夹
            conditionList.add(EntityCondition.makeCondition("shareFromModule", EntityOperator.EQUALS, moduleType));
            conditionList.add(EntityCondition.makeCondition("shareFromModuleId", EntityOperator.EQUALS, moduleId));
        }else{//目录下所有实际的文件，而不含那些共享记录
            conditionList.add(EntityCondition.makeCondition("folderId", EntityOperator.EQUALS, searchFolderId));
            conditionList.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, Constants.PRIVATE_FOLDER_TYPE));
        }
        return conditionList;
    }

    @Override
    protected boolean showFileFolderPath() {
        return Constants.SHARE_FOLDER_ENTRY.equals(folderId);
    }
}
