package index

/**
 * Created by Administrator on 2016/10/17.
 */

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String patryId = parameters.partyId;
GenericValue partyGroup = EntityQuery.use(delegator).from("CompanyInfo").where(UtilMisc.toMap("partyId",patryId)).queryOne()
String provincialCode = partyGroup.get("area");
if(UtilValidate.isNotEmpty(provincialCode)){
    GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",provincialCode)).queryOne()
    String cityCode =  provincialCode.substring(0,4) + "00";
    GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",cityCode)).queryOne()
    String areaCode =  provincialCode.substring(0,2) + "0000";
    GenericValue area = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",areaCode)).queryOne()
    StringBuffer site = new StringBuffer();
    if(area != null && city!=null && provincial!=null){
        site.append(area.get("geoName")).append("/");
        site.append(city.get("geoName")).append("/");
        site.append(provincial.get("geoName"));
    }
    if(area != null && city!=null && provincial==null){
        site.append(area.get("geoName")).append("/");
        site.append(city.get("geoName"));
    }
    if(area != null && city==null && provincial==null){
        site.append(area.get("geoName"));
    }
    partyGroup.put("area",site)
}
Map<String, Object> map = new HashMap<>();
map.putAll(partyGroup);
String groupName = map.get("groupName");
//if(groupName.length() > 15){
//    map.put("subGroupName",groupName.substring(0,14) + "...");
//}
context.put("partyGroup", map)
