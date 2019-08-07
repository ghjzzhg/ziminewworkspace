import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
GenericValue userLogin = (GenericValue) context.get("userLogin");
partyId = parameters.get("partyId");
Map map = parameters.get("data");
contractId = parameters.get("contractId");
String url = parameters.get("url");
List<GenericValue> resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
Map<String,Object> resultMap = new HashMap<String,Object>();
if (resultList.size()!=0){
    map = [:];
    map = resultList[0];
    resultMap.put("groupName",map.get("departmentName"));
    resultMap.put("fullName",map.get("fullName"));
    resultMap.put("workerSn",map.get("workerSn"));
    resultMap.put("occupationName",map.get("occupationName"));
    resultMap.put("partyId",partyId);
}
if(contractId!=null){
    map = delegator.findByPrimaryKey("TblContract",UtilMisc.toMap("contractId",contractId));
    resultMap.put("contractId",contractId);
    resultMap.put("contractNumber",map.get("contractNumber"))
    resultMap.put("contractName",map.get("contractName"))
    resultMap.put("contractType",map.get("contractType"))
    resultMap.put("startDate",map.get("startDate"))
    resultMap.put("endDate",map.get("endDate"))
    resultMap.put("salary",map.get("salary"))
    resultMap.put("signDate",map.get("signDate"))
    resultMap.put("content",map.get("content"))
    resultMap.put("remarks",map.get("remarks"))
}
Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblContract","numName","contractNumber","prefix","staffContract","userLogin",userLogin));
context.number = uniqueNumber.get("number");
resultMap.put("url",url);
context.resultMap = resultMap;