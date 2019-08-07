import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

//查找子账户信息
if(UtilValidate.isNotEmpty(parameters.partyId)){
    GenericValue subAccountInfo = delegator.findByAnd("SubAccountAndInformation", UtilMisc.toMap("partyId", parameters.partyId), null, false).get(0);
    //context.subAccountInfo = subAccountInfo;
    String postalCode = subAccountInfo.getString("area");
    StringBuffer site = new StringBuffer();
    if(UtilValidate.isNotEmpty(postalCode)){
        GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",postalCode)).queryOne()
        String cityCode =  postalCode.substring(0,4) + "00";
        GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",cityCode)).queryOne()
        String areaCode =  postalCode.substring(0,2) + "0000";
        GenericValue area = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",areaCode)).queryOne()
        //省市区
        if(area != null && city!= null && provincial!=null){
            site.append(area.get("geoName")).append("/");
            site.append(city.get("geoName")).append("/");
            site.append(provincial.get("geoName"));
        }
        else if(city != null && area !=null && provincial==null){
            site.append(area.get("geoName")).append("/");
            site.append(city.get("geoName"));
        }
        else if(provincial == null && area != null && city == null){
            site.append(area.get("geoName"));
        }
        //偏远地区省市区特例
        else if (area != null && city == null && provincial != null) {
            site.append(area.get("geoName")).append("/");
            site.append(provincial.get("geoName"));
        }
        context.put("site",site);
    }
    context.put("subAccountInfo",subAccountInfo);

}
context.put("123","123");