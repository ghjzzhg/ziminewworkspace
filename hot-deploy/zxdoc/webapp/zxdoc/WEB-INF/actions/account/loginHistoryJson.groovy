package account

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

/**
 * Created by Administrator on 2016/11/30.
 */
List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null))
conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, ""))
EntityQuery.use(delegator).from("loginHistoryJosn").where(conditions).queryList();
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("loginHistoryJosn").where(conditions).cache(), parameters);
List<GenericValue> data = result.data;
List list = new ArrayList();
for (GenericValue user:data)
{
    Map map = new HashMap();
    String partyId = user.get("partyId");
    GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId",partyId).queryOne();
    String type = party.get("partyTypeId");
    if(type.equals("PARTY_GROUP"))
    {
        String fullName = EntityQuery.use(delegator).from("PartyGroup").where("partyId",partyId).queryOne().get("groupName");
        map.put("fullName",fullName);
    }else
    {
        map.put("fullName",user.get("fullName"));
    }
    map.put("clientIpAddress",user.get("clientIpAddress"));
    map.put("fromDate",user.get("fromDate"));
    map.put("userLoginId",user.get("userLoginId"));
    list.add(map);
}
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", list);
