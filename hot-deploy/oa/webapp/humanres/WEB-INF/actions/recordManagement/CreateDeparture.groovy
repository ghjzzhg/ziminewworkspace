import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
isEdit = parameters.get("isEdit");
resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
resultMap = [:];
if (resultList.size()!=0){
    map = [:];
    map = resultList[0];
    resultMap.put("groupName",map.get("departmentName"));
    resultMap.put("fullName",map.get("fullName"));
    resultMap.put("workerSn",map.get("workerSn"));
    resultMap.put("occupationName",map.get("occupationName"));
    resultMap.put("partyId",partyId);
}
if(isEdit=="true"){
    List<GenericValue> list =  EntityQuery.use(delegator).select().from("TblDeparture").where(UtilMisc.toMap("partyId",partyId)).orderBy("departureDate DESC").queryList();
    if (list.size()!=0){
        map = list[0];
        List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", map.get("departureId"), "entityName", "TblDeparture"))).queryList();
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
        resultMap.put("departureId",map.get("departureId"));
        resultMap.put("departureDate",map.get("departureDate"));
        resultMap.put("departureType",map.get("departureType"));
        resultMap.put("departureReason",map.get("departureReason"));
        resultMap.put("transferContent",map.get("transferContent"));
        resultMap.put("remarks",map.get("remarks"));
        resultMap.put("url","DepartureManagement");
    }else {
        resultMap.put("url","DepartureManagement");
    }

}else {
    resultMap.put("url","ListDepartureManagement");
}
context.resultMap = resultMap;



