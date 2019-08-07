import org.ofbiz.entity.util.UtilPagination

UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("ScoreRuleView"), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);