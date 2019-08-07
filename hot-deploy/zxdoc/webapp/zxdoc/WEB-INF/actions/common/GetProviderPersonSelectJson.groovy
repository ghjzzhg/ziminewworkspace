import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

String name = parameters.q;
String page = parameters.page;
String groupIdOrName = parameters.groupIdOrName;
if (UtilValidate.isEmpty(page)) {
    page = 1;
}

List<EntityCondition> condList = [];
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_IDENTIFIED"));
List<Map<String, Object>> returnList = new ArrayList();
if (UtilValidate.isNotEmpty(groupIdOrName)) {
    condList.add(EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupIdOrName));
}
if (UtilValidate.isNotEmpty(name)) {
    condList.add(EntityCondition.makeCondition("fullName", EntityOperator.LIKE, "%" + name + "%"));
}
condList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
UtilPagination.PaginationResult result = UtilPagination.queryPage(from("SubAccountSimpleInfo").where(condList).cache().distinct(), UtilMisc.toMap("VIEW_INDEX", String.valueOf((Integer.parseInt(page) - 1)), "sortField", "fullName"));
for (GenericValue resultAccount : result.resultList) {
    Map map = new HashMap();
    String partyId = resultAccount.get("partyId");
    String text = resultAccount.get("fullName");
    String partnerType = resultAccount.get("partnerType");
    map.put("id", partyId);
    map.put("text", text + (UtilValidate.isNotEmpty(partnerType) ? " [合伙人]" : ""));
    returnList.add(map);
}
request.setAttribute("data", returnList);
request.setAttribute("totalCount", returnList.size());