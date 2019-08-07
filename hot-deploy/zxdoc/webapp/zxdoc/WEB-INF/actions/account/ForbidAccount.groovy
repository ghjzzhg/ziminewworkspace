package account

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.webapp.control.LoginWorker

import javax.servlet.http.HttpSession

/**
 * 主账户禁用，禁用主账户和子账户
 */
//移除session中的登录账户
LoginWorker.userSessions.remove(parameters.userLoginId);
//主账户禁用
GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId",parameters.userLoginId)).queryOne();
userLogin.setString("enabled", "J");
//清空禁用时间，防止系统恢复该账户
userLogin.setString("disabledDateTime",null);
userLogin.store();
//子账户禁用
String partyId = userLogin.get("partyId");
List<GenericValue> subLogins = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT")).queryList();
if(subLogins!=null && subLogins.size()!=0)
{
    for (GenericValue subLogin:subLogins)
    {
        String subId = subLogin.get("partyIdTo");
        GenericValue sub = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId",subId)).queryOne();
        if (sub!=null) {
            sub.setString("enabled", "J");
            sub.setString("disabledDateTime",null);
            sub.store();
            String subUserLoginId = sub.get("userLoginId");
            LoginWorker.userSessions.remove(subUserLoginId);
        }
    }
}
//禁用合伙人主账户
GenericValue partnerLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId","[" + parameters.userLoginId + "]")).queryOne();
if(partnerLogin!=null) {
    partnerLogin.setString("enabled", "J");
    partnerLogin.setString("disabledDateTime",null);
    partnerLogin.store();
    String partnerLoginId = partnerLogin.get("userLoginId");
    LoginWorker.userSessions.remove(partnerLoginId);
}
 request.setAttribute("data","禁用成功");
