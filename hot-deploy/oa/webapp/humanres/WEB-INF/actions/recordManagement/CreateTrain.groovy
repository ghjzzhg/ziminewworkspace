import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ModelService
GenericValue userLogin = (GenericValue) context.get("userLogin");
partyId = parameters.get("partyId");
trainId = parameters.get("trainId");
url = parameters.get("url");
resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
resultMap = [:];
if (resultList.size()!=0){
    map = [:];
    map = resultList[0];
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblTrain","numName","number","prefix","staffTrain","userLogin",userLogin));
    resultMap.put("number",uniqueNumber.get("number"));
    resultMap.put("groupName",map.get("departmentName"));
    resultMap.put("fullName",map.get("fullName"));
    resultMap.put("workerSn",map.get("workerSn"));
    resultMap.put("occupationName",map.get("occupationName"));
    resultMap.put("partyId",partyId);
}else{
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblTrain","numName","number","prefix","staffTrain","userLogin",userLogin));
    resultMap.put("number",uniqueNumber.get("number"));
}
if(trainId!=null){
    map = delegator.findByPrimaryKey("TblTrain",UtilMisc.toMap("trainId",trainId));
       resultMap.put("trainId",trainId);
       resultMap.put("number",map.get("number"))
       resultMap.put("name",map.get("name"))
       resultMap.put("type",map.get("type"))
       resultMap.put("teacher",map.get("teacher"))
       resultMap.put("money",map.get("money"))
       resultMap.put("date",map.get("date"))
       resultMap.put("address",map.get("address"))
       resultMap.put("content",map.get("content"))
       resultMap.put("remarks",map.get("remarks"))
}
resultMap.put("url",url);
context.resultMap = resultMap;
