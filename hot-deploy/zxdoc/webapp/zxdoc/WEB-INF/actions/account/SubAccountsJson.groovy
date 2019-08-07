import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.get("userLogin").get("partyId")));
//conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_PARTNER"));
conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("SubAccountAndContact").where(conditions).cache(), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);