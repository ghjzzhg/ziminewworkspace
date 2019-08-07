import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.sql.Date
import java.text.SimpleDateFormat

List listTodayUseResource = new ArrayList();
List occupyTimeForThrough = new ArrayList();

resourceList = delegator.findAll("ResourceDetail",false);
resourceListFor = delegator.findByAnd("TblResourceManagement",UtilMisc.toMap("resourceUseState","USING"));
listPendingAuditResource = delegator.findByAnd("ResourceOrderDetail",UtilMisc.toMap("reviewState","未审核"));
listPendingArrangedResource = delegator.findByAnd("ResourceOrderDetail",UtilMisc.toMap("reviewState","未安排"));


GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.get("partyId");
Map defaultMap = new HashMap();
List<GenericValue> resultList = delegator.findByAnd("StaffDetail",UtilMisc.toMap("partyId",partyId));
if (resultList.size()!=0){
    Map<String,Object> map = resultList.get(0);
//    defaultMap.put("groupName",map.get("departmentName"));
    defaultMap.put("orderDepartment",map.get("departmentId"));
    defaultMap.put("orderPerson",partyId);
}


context.listPendingAuditResource = listPendingAuditResource;
context.listPendingArrangedResource = listPendingArrangedResource;
context.listTodayUseResource = listTodayUseResource;
context.resource = resourceListFor;
context.occupyTimeForThrough = occupyTimeForThrough;
context.resourceList = resourceList;
context.defaultMap = defaultMap;