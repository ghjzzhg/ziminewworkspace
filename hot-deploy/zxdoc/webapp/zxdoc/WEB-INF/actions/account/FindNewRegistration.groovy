import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

List<EntityCondition> conditions = [];
if(UtilValidate.isNotEmpty(parameters.type)){
    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, parameters.type));
}

conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_DISABLED"));

UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartyGroupAndUserLoginAndContact").where(conditions).cache(), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);