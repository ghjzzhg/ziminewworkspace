package common

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

String name = parameters.q;
String type = parameters.type;
String page = parameters.page;
if (UtilValidate.isEmpty(page)) {
    page = 1;
}
if (UtilValidate.isNotEmpty(name)) {
    List<EntityCondition> conditions = [];
    conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_IDENTIFIED"));
    conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%"));

    List<String> providerRoles = ["CASE_ROLE_ACCOUNTING", "CASE_ROLE_INVESTOR", "CASE_ROLE_STOCK", "CASE_ROLE_LAW","CASE_ROLE_OWNER"];
    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, providerRoles));
    List<Map<String, Object>> returnList = new ArrayList();
    //非合伙人
    UtilPagination.PaginationResult result = UtilPagination.queryPage(from("BasicGroupInfo").where(conditions), UtilMisc.toMap("VIEW_INDEX", String.valueOf((Integer.parseInt(page) - 1)), "sortField", "groupName"));
    //list = result.resultList.collect {[id: it.getString("partyId"), text: it.getString("groupName")]};
    for (GenericValue resultAccount : result.resultList) {
        Map map = new HashMap();
        String partyId = resultAccount.get("partyId");
        String text = resultAccount.get("groupName");
        map.put("id", partyId);
        map.put("text", text);
        returnList.add(map);
    }
    // list去重
    for (int i = 0; i < returnList.size() - 1; i++) {
        for (int j = returnList.size() - 1; j > i; j--) {
            if (returnList.get(j).get("text").equals(returnList.get(i).get("text"))) {
                returnList.remove(j);
            }
        }
    }
    request.setAttribute("data", returnList);
    request.setAttribute("totalCount", returnList.size());
}

