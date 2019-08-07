package index

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

/**
 * 获取合伙人list
 * 改动：修复合伙人联系人和联系号码，之前显示的是主账户的
 * gf
 */
//String groupType = parameters.groupType;
String groupName = parameters.groupName;
List<EntityCondition> conditions = new ArrayList<>();
//先检索出公司类型
List<EntityCondition> condition = new ArrayList<>();
condition.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"));
//changed by galaxypan@2017-08-05:合伙人判断逻辑变更。只需要根据名称即可。
/*if(groupType!=null)
{
    conditions.add(EntityCondition.makeCondition("partnerType",EntityOperator.EQUALS,groupType));
}*/
conditions.add(EntityCondition.makeCondition("groupName",EntityOperator.EQUALS, groupName));
//合伙人类型
conditions.add(EntityCondition.makeCondition("partnerType",EntityOperator.NOT_EQUAL, null));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartyGroupAndUserLoginAndContact").where(conditions).cache(), parameters);
//修复合伙人联系人和联系号码，之前显示的是主账户的
List<GenericValue> list = result.data;
//changed by galaxypan@2017-08-05:合伙人联系方式直接在PartyGroupAndUserLoginAndContact获取
/*List<GenericValue> data = new ArrayList<>();
for(GenericValue group:list)
{
    String partyId = group.get("partyId");
    List<GenericValue> persons = EntityQuery.use(delegator).from("SubAccountAndContact").where("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT","roleTypeId","CASE_ROLE_PARTNER").orderBy("createdStamp").queryList();
    if(UtilValidate.isNotEmpty(persons))
    {
        GenericValue person = persons.get(0);////changed by galaxypan@2017-08-02:合伙人首次注册时创建的人员联系方式
        group.put("fullName",person.get("fullName"));
        group.put("contactNumber",person.get("contactNumber"));
    }
    data.add(group);
}*/
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", list);

