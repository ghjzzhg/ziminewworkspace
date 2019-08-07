import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ModelService
GenericValue userLogin = (GenericValue) context.get("userLogin");
partyId = parameters.get("partyId");
rewordId = parameters.get("rewordId");
resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
url = parameters.get("url")
resultMap = [:];
if (resultList.size()!=0){
    map = [:];
    map = resultList[0];
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblRewordsPunishment","numName","number","prefix","staffRewordsPunishment","userLogin",userLogin));
    resultMap.put("groupName",map.get("departmentName"));
    resultMap.put("fullName",map.get("fullName"));
    resultMap.put("workerSn",map.get("workerSn"));
    resultMap.put("occupationName",map.get("occupationName"));
    resultMap.put("number",uniqueNumber.get("number"));
    resultMap.put("partyId",partyId);
}else{
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblRewordsPunishment","numName","number","prefix","staffRewordsPunishment","userLogin",userLogin));
    resultMap.put("number",uniqueNumber.get("number"));
}
if(rewordId!=null){
    map = delegator.findByPrimaryKey("TblRewordsPunishment",UtilMisc.toMap("rewordId",rewordId));
    resultMap.put("rewordId",rewordId);
    resultMap.put("number",map.get("number"))
    resultMap.put("name",map.get("name"))
    resultMap.put("type",map.get("type"))
    resultMap.put("level",map.get("level"))
    resultMap.put("money",map.get("money"))
    resultMap.put("date",map.get("date"))
}

resultMap.put("url",url);
context.resultMap = resultMap;
