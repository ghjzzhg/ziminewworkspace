package account

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

/**
 * Created by Administrator on 2016/11/15.
 */
GenericValue userLogin = context.get("userLogin");
String loginId = userLogin.get("partyId");
//检查是否为子账户
String partyTypeId = EntityQuery.use(delegator).from("Party").where("partyId",loginId).queryOne().get("partyTypeId");
//子账户
if(partyTypeId.equals("PERSON"))
{
    GenericValue relationship = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdTo",loginId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT")).queryOne();
    if(relationship!=null)
    {
        loginId = relationship.get("partyIdFrom");
    }
}
//通过id获取groupName
GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where(UtilMisc.toMap("partyId",loginId)).queryOne();
String groupName = partyGroup.get("groupName");
request.setAttribute("groupName",groupName);
request.setAttribute("groupId",loginId);