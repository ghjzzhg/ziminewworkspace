package account

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination
import org.ofbiz.service.ServiceUtil

/**
 * Created by Administrator on 2016/11/15.
 */
String name = parameters.q;
String page = parameters.page;
if (UtilValidate.isEmpty(page)) {
    page = 1;
}
List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_IDENTIFIED"));
conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%"));
conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, userLogin.partyId));
List<Map<String, Object>> returnList = new ArrayList();
//非合伙人
//changed by galaxypan@2017-08-05:合伙人一并被查询
//UtilPagination.PaginationResult result = UtilPagination.queryPage(from("BasicGroupInfo").where(conditions), UtilMisc.toMap("VIEW_INDEX", String.valueOf((Integer.parseInt(page) - 1)), "sortField", "groupName"));
//list = result.resultList.collect {[id: it.getString("partyId"), text: it.getString("groupName")]};
List<GenericValue> result = EntityQuery.use(delegator).from("BasicGroupInfo").where(conditions).orderBy("groupName").queryList();
List<String> groupNames = new ArrayList<>();
for (GenericValue resultAccount : result) {
    String partyId = resultAccount.get("partyId");
    String text = resultAccount.get("groupName");
    //changed by galaxypan@2017-08-05:处理合伙人情况:显示机构、机构型合伙人（有独立名称的）。个人合伙人挂靠的机构仅显示一次。在页面上可以再具体选择哪个人员。人员是按机构名称来搜索的。
    String partnerGroupName = resultAccount.get("partnerGroupName");
    text = UtilValidate.isNotEmpty(partnerGroupName) ? partnerGroupName : text;
    if(!groupNames.contains(text)){
        Map map = new HashMap();
        map.put("id", text);
        map.put("text", UtilValidate.isNotEmpty(partnerGroupName) ? partnerGroupName : text);
        returnList.add(map);
    }
}
//合伙人
//changed by galaxypan@2017-08-05:合伙人一并被查询
/*conditions = [];
conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%"));
conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CASE_ROLE_PARTNER"));
conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, userLogin.partyId));
List<GenericValue> partner = EntityQuery.use(delegator).select("partyId", "groupName").from("BasicPartnerInfo").where(conditions).distinct().queryList();
for (GenericValue resultAccount : partner) {
    Map map = new HashMap();
    String partyId = resultAccount.get("partyId");
    String text = resultAccount.get("groupName");
    //检查当前公司是否存在，如果挂靠公司不存在，则不显示
//    List<GenericValue> groupIsExist = EntityQuery.use(delegator).from("groupIsExist").where(EntityCondition.makeCondition("groupName",EntityOperator.EQUALS,text),EntityCondition.makeCondition("roleTypeId",EntityOperator.NOT_EQUAL,"CASE_ROLE_PARTNER"),EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%")).queryList();
//    if(groupIsExist!=null&&groupIsExist.size()!=0)
//    {
        map.put("id", text);
        map.put("text", text);
        returnList.add(map);
//    }
}
// list去重
for (int i = 0; i < returnList.size() - 1; i++) {
    for (int j = returnList.size() - 1; j > i; j--) {
        if (returnList.get(j).get("text").equals(returnList.get(i).get("text"))) {
            returnList.remove(j);
        }
    }
}*/

request.setAttribute("data", returnList);
request.setAttribute("totalCount", returnList.size());