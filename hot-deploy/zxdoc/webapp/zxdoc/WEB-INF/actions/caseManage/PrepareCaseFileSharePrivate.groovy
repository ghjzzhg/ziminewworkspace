import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.get("userLogin").get("partyId")));
conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("SubAccountAndContact").where(conditions).cache(), parameters);
context.dataList = result.data;

