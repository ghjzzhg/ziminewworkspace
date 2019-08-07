import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

String customerName = parameters.get("SearchCustomerName");
String area = parameters.get("SearchArea");
String customerLevel = parameters.get("SearchCustomerLevel");
String customerStatus = parameters.get("SearchCustomerStatus");
String userLoginId = ((GenericValue) context.get("userLogin")).get("userLoginId");

List<EntityCondition> condList = FastList.newInstance();
if (UtilValidate.isNotEmpty(customerName)) {
    condList.add(EntityCondition.makeCondition("customerName", EntityOperator.LIKE, "%"+customerName+"%"));
}
if (UtilValidate.isNotEmpty(area)) {
    //对于区域进行判断
    String lastf = area.substring(2,6);
    if(lastf.equals("0000"))
    {

        String first2 = area.substring(0,2);
        condList.add(EntityCondition.makeCondition("area", EntityOperator.LIKE, first2+"%"));
    }else
    {
        String lastt = area.substring(4,6);
        if(lastt.equals("00"))
        {

            String first4 = area.substring(0,4);
            condList.add(EntityCondition.makeCondition("area", EntityOperator.LIKE, first4+"%"));
        }else
        {
            condList.add(EntityCondition.makeCondition("area", EntityOperator.EQUALS, area));
        }
    }
}
if (UtilValidate.isNotEmpty(customerLevel)) {
    condList.add(EntityCondition.makeCondition("customerLevel", EntityOperator.EQUALS, customerLevel));
}
if (UtilValidate.isNotEmpty(customerStatus)) {
    condList.add(EntityCondition.makeCondition("customerStatus", EntityOperator.EQUALS, customerStatus));
}
condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
condList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "Y"));
//condList.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"ORG_LINK_CONTACT"));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartnerSearchInfo").where(EntityCondition.makeCondition(condList)).distinct(), parameters);
List<GenericValue> partnerList = new ArrayList<>();
for (GenericValue partner:result.data)
{
    Map map = new HashMap();
    map.put("area",partner.get("area"));
    /*map.put("descriptionLevel",partner.get("descriptionLevel"));*/
    map.put("address1",partner.get("address1"));
    map.put("customerLevel",partner.get("customerLevel"));
    map.put("customerName",partner.get("customerName"));
    map.put("customerStatus",partner.get("customerStatus"));
    map.put("userLoginId",partner.get("userLoginId"));
//    map.put("partyRelationshipTypeId",partner.get("partyRelationshipTypeId"));
    /*map.put("descriptionStatus",partner.get("descriptionStatus"));*/

    map.put("infoString",partner.get("infoString"));
    map.put("id",partner.get("id"));
    map.put("partyId",partner.get("partyId"));
    map.put("remarks",partner.get("remarks"));
    map.put("status",partner.get("status"));
    //通过level和status获取相关描述，该修改修复页面错误数据的展示，根本原因在于视图配置问题
    String level = partner.get("customerLevel");
    if(level!=null&&level!=""){
        GenericValue levelEnum = EntityQuery.use(delegator).from("Enumeration").where("enumId",level,"enumTypeId","CUSTOMER_LEVEL").queryOne();
        map.put("descriptionLevel",levelEnum.get("description"));
    }else
    {
        map.put("descriptionLevel","");
    }
    String status = partner.get("customerStatus");
    if(status!=null&&status!=""){
        GenericValue levelEnum = EntityQuery.use(delegator).from("Enumeration").where("enumId",status,"enumTypeId","CUSTOMER_STATUS").queryOne();
        map.put("descriptionStatus",levelEnum.get("description"));
    }else
    {
        map.put("descriptionStatus","");
    }
    //地区处理
    String postalCode = partner.get("area");
    StringBuffer site = new StringBuffer();
    if(UtilValidate.isNotEmpty(postalCode)){
        GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",postalCode)).queryOne()
        String cityCode =  postalCode.substring(0,4) + "00";
        GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",cityCode)).queryOne()
        String areaCode =  postalCode.substring(0,2) + "0000";
        GenericValue areas = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",areaCode)).queryOne()
        //处理只选择了省或者只选择了省、市
        if(postalCode.substring(2,6)=="0000")
        {
            site.append(areas.get("geoName"));
        }
        else if (postalCode.substring(4,6)=="00")
        {
            site.append(areas.get("geoName")).append("/");
            site.append(city.get("geoName"));
        }else {
            if (areas != null && city != null && provincial != null) {
                site.append(areas.get("geoName")).append("/");
                site.append(city.get("geoName")).append("/");
                site.append(provincial.get("geoName"));
            }
            else if (city != null && areas != null && provincial == null) {
                site.append(areas.get("geoName")).append("/");
                site.append(city.get("geoName"));
            }
            else if (provincial == null && areas != null && city == null) {
                site.append(areas.get("geoName"));
            }
            //偏远地区省市区特例
            else if (areas != null && city == null && provincial != null) {
                site.append(areas.get("geoName")).append("/");
                site.append(provincial.get("geoName"));
            }
        }
    }
    map.put("geoName",site);
    partnerList.add(map);
}
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", partnerList);
