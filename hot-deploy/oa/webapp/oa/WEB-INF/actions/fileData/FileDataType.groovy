import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

public Map<String, Object> saveFileType() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String parentId = context.get("Id");
    String fileTypeId = context.get("fileTypeId");
    String msg = "";
    try {
        if(UtilValidate.isNotEmpty(fileTypeId)){

            Map map = new HashMap();
            map.put("fileTypeId", fileTypeId);
            map.put("parentId", parentId);
            map.put("typeName", context.get("sonTypeName"));
            delegator.store(delegator.makeValidValue("TblFileType", map));
            msg = "更新成功";
        }else{
            String newFileTypeId = delegator.getNextSeqId("TblFileType").toString();//获取主键ID
            Map map = new HashMap();
            map.put("fileTypeId", newFileTypeId);
            map.put("parentId", parentId);
            map.put("typeName", context.get("sonTypeName"));
            map.put("state", "1");//1表示已生效
            delegator.create(delegator.makeValidValue("TblFileType", map));
            msg = "保存成功"
            }
        }catch (Exception e ) {
            msg = "保存失败"
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    msg,
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        successResult.put("data", UtilMisc.toMap("msg", msg));
        return successResult;
}
public Map<String,Object> deleteFileType(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String fileTypeId = context.get("fileTypeId");
    if(UtilValidate.isNotEmpty(fileTypeId)){
        Map map = new HashMap();
        map.put("fileTypeId", fileTypeId);
        map.put("state", "0");//0表示已删除
        delegator.store(delegator.makeValidValue("TblFileType", map));
        delegator.storeByCondition("TblFileType",UtilMisc.toMap("state", "0"),EntityCondition.makeCondition(UtilMisc.toMap("parentId",fileTypeId)))
    }
    successResult.put("msg","删除成功！");
    return successResult;
}
public Map<String,Object> modifyFileType(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String fileTypeId = context.get("fileTypeId");
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(fileTypeId)){
        GenericValue genericValue = delegator.findOne("TblFileType",UtilMisc.toMap("fileTypeId", fileTypeId),false);
        String parentId = genericValue.get("parentId");
        String sonTypeName = genericValue.get("typeName");
        GenericValue gv = delegator.findOne("TblFileType",UtilMisc.toMap("fileTypeId", parentId),false);
        String parentTypeName = gv.get("typeName");
        map.put("fileTypeId",fileTypeId);
        map.put("parentId",parentId);
        map.put("parentTypeName",parentTypeName);
        map.put("sonTypeName",sonTypeName);
    }
    successResult.put("fileTypeMap",map);
    return successResult;
}

public Map<String,Object> getFileDataItemTypes(){
    List<Map<String,Object>> fileDataTypes = new ArrayList<Map<String,Object>>()
    List<GenericValue> fileTypes = EntityQuery.use(delegator).select().from("TblFileType").where(EntityCondition.makeCondition("state",EntityOperator.EQUALS,"1")).queryList();
    for (GenericValue fileType : fileTypes) {
        Map<String,Object> map = new HashMap<String,Object>()
        map.putAll(fileType)
        String fileTypeId = fileType.get("fileTypeId").toString();
        String typeName = fileType.get("typeName").toString();
        String parentId = fileType.get("parentId").toString();
        map.put("id",fileTypeId);
        map.put("code",fileTypeId);
        map.put("pId",parentId);
        map.put("name",typeName);
        fileDataTypes.add(map);
    }
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    successResult.put("data",fileDataTypes);
    return successResult;
}
public Map<String,Object> searchSonTypeName(){
    String parentId = context.get("fileTypeId");
    List<GenericValue> fileTypes = EntityQuery.use(delegator).select().from("TblFileType").where(EntityCondition.makeCondition(UtilMisc.toMap("parentId",parentId,"state","1"))).queryList();
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map map = new HashMap();
    map.put("list",fileTypes)
    successResult.put("data",map);
    return successResult;
}