package common

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination
import org.ofbiz.entity.util.UtilPagination.PaginationResultDatatables

String name = parameters.name;
String type = parameters.type;
String partner = parameters.partner;
List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_IDENTIFIED"));
if (UtilValidate.isNotEmpty(name)) {
    conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%"));
}

if(UtilValidate.isNotEmpty(type)){
    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, type));
}
if(!Boolean.parseBoolean(partner)){//不含合伙人
    conditions.add(EntityCondition.makeCondition("partnerType", EntityOperator.EQUALS, null));
}

//TODO:合伙人合并显示,暂时全部显示，标注合伙人
PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartyGroupAndUserLoginAndContact").where(conditions), parameters);
if (result != null) {
    request.setAttribute("draw", result.draw);
    request.setAttribute("recordsTotal", result.recordsTotal);
    request.setAttribute("recordsFiltered", result.recordsFiltered);
    request.setAttribute("data", result.data);
}
