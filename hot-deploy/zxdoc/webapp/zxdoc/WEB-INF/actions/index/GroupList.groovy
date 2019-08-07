import javolution.util.FastList
import org.apache.commons.collections.map.HashedMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

/**
 * 获取第三方机构信息
 * 改动：增加合伙人链接，需要增加标识 0110
 * 改动：考虑合伙人挂靠的企业实际不存在 0111
 */
String draw = parameters.draw;
String start = parameters.start;
Integer begin = Integer.parseInt(start);
String groupType = parameters.groupType;
String areaCode = parameters.areaCode;
/*List<GenericValue> groupList = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where(UtilMisc.toMap("roleTypeId", groupType)).queryList();*/
List<EntityCondition> condition = new ArrayList<>();
//筛选出合伙人
condition.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, groupType), EntityOperator.OR, EntityCondition.makeCondition("partnerType", EntityOperator.EQUALS, groupType)));
//地区搜索条件
//1.如果只选定了省，则末四位截断应该为0000
if (areaCode != null && areaCode != "") {
    String lastFour = areaCode.substring(2, 6);
    if (lastFour == "0000") {
        condition.add(EntityCondition.makeCondition("area", EntityOperator.LIKE, areaCode.substring(0, 2) + "%"));
    } else {
        //2.如果只选定了市，则末两位为00
        String lastTwo = areaCode.substring(4, 6);
        if (lastTwo == "00") {
            condition.add(EntityCondition.makeCondition("area", EntityOperator.LIKE, areaCode.substring(0, 4) + "%"));
        }
        //3.选定到区
        else {
            condition.add(EntityCondition.makeCondition("area", EntityOperator.EQUALS, areaCode));
        }
    }
}
condition.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
/*UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartyGroupAndUserLoginAndContact").where(condition), parameters);*/
List<GenericValue> groups = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where(condition).orderBy("createdStamp").queryList();
//changed by galaxypan@2017-08-05:页面上合伙人都归属到所挂靠的公司，即同一个公司下属合伙人只显示在一条记录上，通过点击合伙人按钮进入合伙人列表展示。
List<String> partnerHostNames = new ArrayList<>();
Map<String, List<GenericValue>> partnersMap = new HashMap<>();
for (GenericValue group: groups) {
    String partnerType = group.getString("partnerType");
    String companyName = group.getString("groupName");
    if(UtilValidate.isNotEmpty(partnerType)){
        List<GenericValue> partners = partnersMap.get(companyName);
        if(partners == null){
            partners = new ArrayList<>();
            partnersMap.put(companyName, partners);
        }
        partners.add(group);
    }else{
        partnerHostNames.add(companyName);
    }
}

//增加标识，验证是否存在合伙人
List list = new ArrayList();
//repeated用来统计增加到list中的合伙人的groupName
List repeated = new ArrayList();
for (GenericValue group : groups) {
    String partnerType = group.getString("partnerType");
    String companyName = group.getString("groupName");
    if(UtilValidate.isEmpty(partnerType)){//非合伙人
        Map map = new HashedMap();
        map.putAll(group);
        if(partnersMap.containsKey(companyName)){
            map.put("hasPartner", "Y");
            map.put("isPartner", "N");
        }else{
            map.put("hasPartner", "N");
            map.put("isPartner", "N");
        }
        list.add(map);
    }else if(!partnerHostNames.contains(companyName) && !repeated.contains(companyName)){//合伙人挂靠的机构不存在时，仅显示一次
        Map map = new HashedMap();
        repeated.add(companyName);
        map.putAll(group);
        map.put("hasPartner", "Y");
        map.put("isPartner", "Y");
        list.add(map);
    }//如果挂靠的机构已存在则不必单独显示
}
//根据draw获取数据
int end = begin + Integer.parseInt(parameters.length);
List result = list.subList(begin, end > list.size() - 1 ? list.size(): end);

request.setAttribute("draw", draw);
request.setAttribute("recordsTotal", list.size());
request.setAttribute("recordsFiltered", list.size());
request.setAttribute("data", result);

