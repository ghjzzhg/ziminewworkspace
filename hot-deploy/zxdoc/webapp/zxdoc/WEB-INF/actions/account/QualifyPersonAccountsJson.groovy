package account

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

/**
 * Created by rextec on 2016/9/13.
 */
List<GenericValue> condList =[];
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_UNIDENTIFIED"));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartyPersonContactInfo").where(condList).cache(), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);