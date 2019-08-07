package account

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/15.
 */
String groupId = parameters.groupIdOrName;

List<Map<String, Object>> returnList = new ArrayList();
if (UtilValidate.isNotEmpty(groupId)) {
    List<GenericValue> result = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where("groupId", groupId, "statusId", "PARTY_IDENTIFIED").queryList();
    for (GenericValue resultAccount : result) {
        Map map = new HashMap();
        String partyId = resultAccount.get("partyId");
        String text = resultAccount.get("fullName");
        map.put("id", partyId);
        map.put("text", text);
        returnList.add(map);
    }
}
request.setAttribute("data", returnList);
request.setAttribute("totalCount", returnList.size());