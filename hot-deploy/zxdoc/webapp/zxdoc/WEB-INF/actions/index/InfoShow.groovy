package index

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * 获取个人信息
 */
//获取登录的用户
GenericValue userLogin = context.get("userLogin");
//查看该账号为主账号还是子账号
String type = EntityQuery.use(delegator).from("Party").select("partyTypeId").where(UtilMisc.toMap("partyId",userLogin.get("partyId"))).cache().queryOne().get("partyTypeId");
String postalCode;
StringBuffer site = new StringBuffer();
//子账户
if(type.equals("PERSON"))
{
    GenericValue sub =  EntityQuery.use(delegator).from("SubAccountAndInformation").where(UtilMisc.toMap("partyId",userLogin.get("partyId"))).cache().queryOne();
    context.put("AccountType","person");
    context.put("SubInfo",sub);
    context.put("email",sub.get("infoString"));
    context.put("emailId",sub.get("emailId"));
    postalCode = sub.get("area");
}
//主账号
else
{
    List<GenericValue> subList =  EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where(UtilMisc.toMap("partyId",userLogin.get("partyId"))).distinct().queryList();
    if(UtilValidate.isNotEmpty(subList)){
        GenericValue sub = subList.get(subList.size()-1);
        context.put("AccountType","group");
        context.put("SubInfo",sub);
        postalCode = sub.get("area");
        List<GenericValue> useremail =  EntityQuery.use(delegator).from("EmailAddressInfo").where(UtilMisc.toMap("partyId",userLogin.get("partyId"))).cache().queryList();
        GenericValue emails = useremail[useremail.size()-1];
        context.put("email",emails.get("infoString"));
        context.put("emailId",emails.get("emailId"));
    }
}
//省市区
if(postalCode != null) {
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
    //偏远地区省市区特例
    else if (area != null && city == null && provincial != null) {
        site.append(area.get("geoName")).append("/");
        site.append(provincial.get("geoName"));
    }
    context.put("site",site);
}