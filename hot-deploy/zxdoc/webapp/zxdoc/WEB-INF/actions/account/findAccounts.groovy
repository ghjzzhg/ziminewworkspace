package account

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

/**
 * Created by Administrator on 2016/10/18.
 */
String userLoginId = parameters.userLoginId;
if(userLoginId!=null) {
    //changed by galaxypan@2017-08-03:合伙人主账户信息可以直接搜索出并进行编辑
    /*GenericValue roleType = EntityQuery.use(delegator).from("BasicGroupInfo").where(UtilMisc.toMap("userLoginId","[" + userLoginId + "]")).cache().queryOne();
    List<EntityCondition> conditions = [];
    //合伙人处理
    if(roleType!=null && roleType.get("roleTypeId")!=null)
    {
        userLoginId = "[" + userLoginId + "]";
    }*/
    List<EntityCondition> conditions = [];
    conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
    GenericValue account = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where(conditions).cache().queryOne();
    String partyId = account.get("partyId");
    List<EntityCondition> con = [];
    con.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    List<GenericValue> adds = EntityQuery.use(delegator).from("PostalAddressInfo").where(con).cache().queryList();
    GenericValue add = adds[adds.size() - 1];
    String postalCode = add.get("area");
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
        else if (city != null && area != null && provincial == null) {
            site.append(area.get("geoName")).append("/");
            site.append(city.get("geoName"));
        }
        else if (provincial == null && area != null && city == null) {
            site.append(area.get("geoName"));
        }
        //偏远地区省市区特例
        else if (area != null && city == null && provincial != null) {
            site.append(area.get("geoName")).append("/");
            site.append(provincial.get("geoName"));
        }
    }
    List<GenericValue> emailinfosss = EntityQuery.use(delegator).from("EmailAddressInfo").where(con).cache().queryList();
    GenericValue emailinfo = emailinfosss[emailinfosss.size() - 1];
    context.put("subAccountInfo", account);
    context.put("site", site);
    context.put("address1", add.get("address1"));
    context.put("area", add.get("area"));
    context.put("email", emailinfo.get("infoString"));
    context.put("emailId", emailinfo.get("emailId"));
}
context.put("edit","123");