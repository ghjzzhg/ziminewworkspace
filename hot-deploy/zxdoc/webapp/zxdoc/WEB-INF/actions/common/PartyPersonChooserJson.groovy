package common

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination
import org.ofbiz.entity.util.UtilPagination.PaginationResultDatatables

String name = parameters.name;
String type = parameters.type;
List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_IDENTIFIED"));
if (UtilValidate.isNotEmpty(name)) {
    name = "%" + name + "%";
    conditions.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("groupName", EntityOperator.LIKE, name), EntityCondition.makeCondition("fullName", EntityOperator.LIKE, name), EntityCondition.makeCondition("partnerGroupName", EntityOperator.LIKE, name), EntityCondition.makeCondition("userLoginId", EntityOperator.LIKE, name)));
}

if(UtilValidate.isNotEmpty(type)){
    if(type.contains(",")){
        conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toListArray(type.split(","))));
    }else{
        conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, type));
    }

}
PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("SubAccountSimpleInfoWithRole").where(conditions), parameters);
if (result != null) {
    request.setAttribute("draw", result.draw);
    request.setAttribute("recordsTotal", result.recordsTotal);
    request.setAttribute("recordsFiltered", result.recordsFiltered);
    request.setAttribute("data", result.data);
}
