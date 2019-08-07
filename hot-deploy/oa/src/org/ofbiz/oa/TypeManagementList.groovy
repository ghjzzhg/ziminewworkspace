package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> AddTypeManagement() {
    String typeManagementListId = context.get("typeManagementListId");
    String typeManagement=context.get("typeManagement");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map map=new HashMap();
    String msg = "添加成功";
    if (UtilValidate.isNotEmpty(typeManagementListId)){
        msg = "更新成功"
        genericValue = delegator.findByPrimaryKey("TblTypeManagementList",UtilMisc.toMap("typeManagementListId",typeManagementListId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }else if (UtilValidate.isNotEmpty(typeManagement)){
        typeManagementListId = delegator.getNextSeqId("TblTypeManagementList").toString();
        genericValue = delegator.makeValidValue("TblTypeManagementList",UtilMisc.toMap("typeManagementListId",typeManagementListId));
        genericValue.setNonPKFields(context);
        genericValue.create();
        map.put("typeManagement", typeManagement);
        map.put("Id",typeManagementListId);
    }else {
        msg="请输入需要添加的类型管理";
    }
    map.put("msg", msg);
    successResult.put("data", map);
    return successResult;
}



