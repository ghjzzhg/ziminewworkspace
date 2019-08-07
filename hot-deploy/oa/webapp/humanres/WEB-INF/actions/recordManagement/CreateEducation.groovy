import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
educationId = parameters.get("educationId");
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
if(educationId!=null){
    map = delegator.findByPrimaryKey("TblEducational",UtilMisc.toMap("educationId",educationId));
       resultMap.put("educationId",educationId);
       resultMap.put("startDate",map.get("startDate"))
       resultMap.put("endDate",map.get("endDate"))
       resultMap.put("schoolName",map.get("schoolName"))
       resultMap.put("schoolAddress",map.get("schoolAddress"))
       resultMap.put("major",map.get("major"))
       resultMap.put("degree",map.get("degree"))
       resultMap.put("certificate",map.get("certificate"))
       resultMap.put("remarks",map.get("remarks"))
}
context.resultMap = resultMap;
