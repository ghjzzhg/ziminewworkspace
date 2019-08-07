import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.UtilPagination

GenericValue userLogin = context.get("userLogin");
Map<String,Object> map = dispatcher.runSync("providerCases", UtilMisc.<String, Object>toMap("userLogin", userLogin, "customerName", parameters.customerName, "pageParam", parameters));
UtilPagination.PaginationResultDatatables result = map.get("pageResult");

request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);