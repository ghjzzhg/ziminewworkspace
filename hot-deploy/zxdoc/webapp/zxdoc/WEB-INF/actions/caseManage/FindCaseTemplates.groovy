import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination
import org.ofbiz.entity.condition.EntityCondition

String templateName = parameters.templateName;
List<EntityCondition> conditions = new ArrayList<>();
conditions.add(EntityCondition.makeCondition
        (EntityCondition.makeCondition("deleted", EntityOperator.NOT_EQUAL, "Y"),
                EntityOperator.OR,
                EntityCondition.makeCondition("deleted", EntityOperator.EQUALS, null)));

conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null));
if(UtilValidate.isNotEmpty(templateName)){
   conditions.add(EntityCondition.makeCondition("templateName", EntityOperator.LIKE, "%" + templateName + "%"));
}

UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("CaseTemplateList").where
   (conditions).cache(), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);
