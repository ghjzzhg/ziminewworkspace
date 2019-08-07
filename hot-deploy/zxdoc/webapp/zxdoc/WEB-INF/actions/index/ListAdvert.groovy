/**
 * Created by Administrator on 2016/10/12.
 */
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination
List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("hasdelete", EntityOperator.EQUALS, "1"));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("TblAdvert").where(conditions).cache(), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);

