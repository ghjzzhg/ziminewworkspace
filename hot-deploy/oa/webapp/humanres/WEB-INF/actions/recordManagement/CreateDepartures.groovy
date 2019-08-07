import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

departureId = parameters.get("departureId");
GenericValue map =  EntityQuery.use(delegator).select().from("TblDeparture").where(UtilMisc.toMap("departureId",departureId)).queryOne();
String partyId =map.get("partyId");
resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
Map resultMap =new HashMap();
resultMap.putAll(map);
if (resultList.size()!=0){
    maps = [:];
    maps = resultList[0];
    resultMap.put("groupName",maps.get("departmentName"));
    resultMap.put("fullName",maps.get("fullName"));
    resultMap.put("workerSn",maps.get("workerSn"));
    resultMap.put("occupationName",maps.get("occupationName"));
    resultMap.put("partyId",partyId);
}
List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", departureId, "entityName", "TblDeparture"))).queryList();
String fileId = "";
if(null != genericValueList && genericValueList.size() > 0){
    for(GenericValue genericValue : genericValueList){
        fileId = fileId + genericValue.get("accessoryId") + ",";
    }
}
if(!"".equals(fileId)){
    fileId = fileId.substring(0,fileId.length() - 1);
    context.put("files",fileId);
    Map data = runService("searchFileByIds", dispatcher.getDispatchContext().getModelService("searchFileByIds").makeValid(context, ModelService.IN_PARAM));
    Map filemap = data.get("data");
    resultMap.put("fileId",filemap.get("fileIds"));
    resultMap.put("fileList",filemap.get("fileList"));
}
resultMap.put("url","ListDepartureManagement");
context.resultMap = resultMap;