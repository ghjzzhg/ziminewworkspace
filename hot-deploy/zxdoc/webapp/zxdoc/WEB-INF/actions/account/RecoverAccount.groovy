package account

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * 主账户恢复
 */
//主账户恢复
GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId",parameters.userLoginId)).queryOne();
userLogin.setString("enabled", "Y");
userLogin.set("disabledDateTime", null);
userLogin.set("successiveFailedLogins", 0L);
userLogin.store();
//子账户恢复
String partyId = userLogin.get("partyId");
List<GenericValue> subLogins = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT")).queryList();
if(subLogins!=null && subLogins.size()!=0)
{
    for (GenericValue subLogin:subLogins)
    {
        String subId = subLogin.get("partyIdTo");
        GenericValue sub = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId",subId)).queryOne();
        sub.setString("enabled", "Y");
        sub.set("disabledDateTime", null);
        sub.set("successiveFailedLogins", 0L);
        sub.store();
    }
}
//恢复合伙人主账户
GenericValue partnerLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId","[" + parameters.userLoginId + "]")).queryOne();
if(partnerLogin!=null) {
    partnerLogin.setString("enabled", "Y");
    partnerLogin.set("disabledDateTime", null);
    partnerLogin.set("successiveFailedLogins", 0L);
    partnerLogin.store();
}
request.setAttribute("data","恢复成功");