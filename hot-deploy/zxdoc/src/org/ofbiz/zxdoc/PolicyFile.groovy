package org.ofbiz.zxdoc

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.content.data.DataServices
import org.ofbiz.content.data.FileManagerFactory
import org.ofbiz.content.data.FileTypeManager
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.minilang.method.entityops.EntityCondition
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp

/**
 * 根据一级分类查询二级分类
 * @return
 */
public Map<String, Object> searchPolicyTypeTwo(){
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String oneTypeId = context.get("oneTypeId");
    List<GenericValue> twoTypeList = EntityQuery.use(delegator).from("Enumeration").where(UtilMisc.toMap("enumTypeId", oneTypeId)).queryList();
    success.put("data", twoTypeList);
    return success;
}

public Map<String, Object> savePolicyFile(){
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String msg = "上传成功";
    String size = context.get("_uploadedFile_size");
    String fileSize = EntityUtilProperties.getPropertyValue("content.properties", "fileSize", "localhost", delegator);
    if(size != null && Integer.valueOf(size) < Integer.parseInt(fileSize) * 1024 * 1024 ) {
        String policyType = context.get("addPolicySearchTwo");
        if(UtilValidate.isEmpty(policyType)){
            policyType = context.get("addPolicySearchOne");
        }
        if(UtilValidate.isNotEmpty(policyType)){
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            List<String> dataResouceIds = DataServices.storeAllDataResourceInMap(dispatcher, delegator as GenericDelegator, userLogin, context, true);
            if(UtilValidate.isNotEmpty(dataResouceIds)){
                String fileId = dataResouceIds.get(0);
                Map<String, Object> policyMap = new HashMap<>();
                policyMap.put("id", fileId);
                policyMap.put("category", policyType);
                policyMap.put("file", fileId.substring(0, fileId.lastIndexOf("-")));
                policyMap.put("publishDate", new java.sql.Date((new Date()).getTime()));
                GenericValue genericValue = delegator.makeValidValue("TblPolicyFiles", policyMap);
                genericValue.create();
            }
        }else{
            msg = "请选择分类！";
        }
    }else{
        msg = "文件大小请限制在" + fileSize + "兆之内";
    }
    success.put("msg", msg);
    return success;
}

public Map<String, Object> delPolicyFile(){
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String msg = "删除成功";
    String fileId = context.get("fileId");
    GenericValue policyFile = EntityQuery.use(delegator).select().from("TblPolicyFiles").where(UtilMisc.toMap("id",fileId)).queryOne();
    if( policyFile != null){
        delegator.removeByAnd("TblPolicyFiles", UtilMisc.toMap("id", fileId));
        GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId",fileId)).queryOne();policyFile
        FileTypeManager fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator)delegator);
        fileManager.delFile(fileId);
    }else{
        msg = "政策文件已删除，请刷新";
    }
    success.put("msg", msg);
    return success;
}