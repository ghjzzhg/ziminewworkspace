import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

partyId = parameters.get("partyId");
departureMapList = [:];
if (partyId != null && partyId != "") {
    List<GenericValue> departureList = EntityQuery.use(delegator).select().from("TblDepartureDetail").where(EntityCondition.makeCondition("partyId", partyId)).orderBy("departureDate DESC").queryList();
    if (departureList.size() != 0) {
        departureMapList = departureList[0];
    }
} else {
    startDate = parameters.get("startDate")
    endDate = parameters.get("endDate")
    partyIdForSearch = parameters.get("partyIdForSearch")
    departureType = parameters.get("departureType")
    List conditionList = new ArrayList();
    List criteria = new LinkedList();
    if (UtilValidate.isNotEmpty(startDate) && !UtilValidate.isNotEmpty(endDate)) {
        startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
        criteria.add(startDateForSql)
        conditionList.add(EntityCondition.makeCondition("departureDate", EntityOperator.GREATER_THAN_EQUAL_TO, criteria));
    }
    if (UtilValidate.isNotEmpty(endDate) && !UtilValidate.isNotEmpty(startDate)) {
        endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
        criteria.add(endDateForSql)
        conditionList.add(EntityCondition.makeCondition("departureDate", EntityOperator.LESS_THAN_EQUAL_TO, criteria));
    }
    if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
        startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
        endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
        criteria.add(startDateForSql)
        criteria.add(endDateForSql)
        conditionList.add(EntityCondition.makeCondition("departureDate", EntityOperator.BETWEEN, criteria));
    }
    if (UtilValidate.isNotEmpty(partyIdForSearch)) {
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdForSearch));
    }
    if (UtilValidate.isNotEmpty(departureType)) {
        conditionList.add(EntityCondition.makeCondition("departureType", EntityOperator.EQUALS, departureType));
    }
    EntityQuery from = EntityQuery.use(delegator).from("TblDepartureDetail");
    if (UtilValidate.isNotEmpty(conditionList)) {
        EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList), EntityOperator.AND);
        from = from.where(condition);
    }
    departureMapList = from.queryIterator();
}
context.departureList = departureMapList;
context.partyId = partyId;