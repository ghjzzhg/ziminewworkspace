import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
workId = parameters.get("workId");
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
if(workId!=null){
    map = delegator.findByPrimaryKey("TblWorkExperience",UtilMisc.toMap("workId",workId));
       resultMap.put("workId",workId);
       resultMap.put("startDate",map.get("startDate"))
       resultMap.put("endDate",map.get("endDate"))
       resultMap.put("companyName",map.get("companyName"))
       resultMap.put("companyProperty",map.get("companyProperty"))
       resultMap.put("address",map.get("address"))
       resultMap.put("post",map.get("post"))
       resultMap.put("workContent",map.get("workContent"))
       resultMap.put("phoneNumber",map.get("phoneNumber"))
       resultMap.put("remarks",map.get("remarks"))
}
context.resultMap = resultMap;
