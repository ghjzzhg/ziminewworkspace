package account

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/25.
 */
List<GenericValue> persons = EntityQuery.use(delegator).from("Person").where(EntityCondition.makeCondition("openFireJid",EntityOperator.NOT_EQUAL,null)).queryList();
for (GenericValue person: persons)
{
    String openFireJid = person.get("openFireJid");
    String partyId = person.get("partyId");
    GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId",partyId).queryOne();
    String userLoginId = userLogin.get("userLoginId");
    String login = openFireJid.split("@")[0];
    if(login.equals(userLoginId))
    {
        person.put("openFireJid",partyId+"@"+openFireJid.split("@")[1]);
        person.store();
        Map maps = new HashMap();
        Map data = new HashMap();
        maps.put("fullName", person.get("fullName"));
        maps.put("userLoginId", partyId);
        GenericValue relation = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT").queryOne();
        maps.put("partyGroupId", relation.get("partyIdFrom"));
        maps.put("result", true);
        data.put("data",maps);
        runService("createUserInOF",data);
    }
}