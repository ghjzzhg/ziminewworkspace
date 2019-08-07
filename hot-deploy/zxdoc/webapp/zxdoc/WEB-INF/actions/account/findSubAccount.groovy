package account

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/12/7.
 */
String userLoginId = parameters.userLoginId;
if(userLoginId!=null) {
    List<EntityCondition> conditions = [];
    conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
    GenericValue account = EntityQuery.use(delegator).from("SubAccountAndContact").where(conditions).cache().queryOne();
    String postalCode = account.get("postalCode");
    StringBuffer site = new StringBuffer();
    if(UtilValidate.isNotEmpty(postalCode)){
        GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", postalCode)).queryOne()
        String cityCode = postalCode.substring(0, 4) + "00";
        GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", cityCode)).queryOne()
        String areaCode = postalCode.substring(0, 2) + "0000";
        GenericValue area = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", areaCode)).queryOne()
        if (area != null && city != null && provincial != null) {
            site.append(area.get("geoName")).append("/");
            site.append(city.get("geoName")).append("/");
            site.append(provincial.get("geoName"));
        }
        if (city != null && area != null && provincial == null) {
            site.append(area.get("geoName")).append("/");
            site.append(city.get("geoName"));
        }
        if (provincial == null && area != null && city == null) {
            site.append(area.get("geoName"));
        }
    }
    List<EntityCondition> con = [];
    con.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, account.get("partyId")));
    List<GenericValue> emailinfosss = EntityQuery.use(delegator).from("EmailAddressInfo").where(con).cache().queryList();
    GenericValue emailinfo = emailinfosss[emailinfosss.size() - 1];
    context.put("subAccountInfo", account);
    context.put("site", site);
    context.put("address1", account.get("address1"));
    context.put("area", account.get("postalCode"));
    context.put("email", emailinfo.get("infoString"));
    context.put("emailId", emailinfo.get("emailId"));
    context.put("partyIdFrom",account.get("partyIdFrom"));
    context.put("groupName",account.get("groupName"));
}
context.put("edit","123");