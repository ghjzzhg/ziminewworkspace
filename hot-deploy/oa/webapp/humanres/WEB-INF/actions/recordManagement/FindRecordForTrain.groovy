import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

partyId = parameters.get("partyId");
startDate = parameters.get("startDate")
endDate = parameters.get("endDate")
partyIdForSearch = parameters.get("partyIdForSearch")
type = parameters.get("type")
String name1 = parameters.get("name")
String number1 = parameters.get("number")
List conditionList = new ArrayList();
List criteria = new LinkedList();
if (UtilValidate.isNotEmpty(startDate) && !UtilValidate.isNotEmpty(endDate)) {
    startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
    criteria.add(startDateForSql)
    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO, criteria));
}
if (UtilValidate.isNotEmpty(endDate) && !UtilValidate.isNotEmpty(startDate)) {
    endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
    criteria.add(endDateForSql)
    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO, criteria));
}
if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
    startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
    endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
    criteria.add(startDateForSql)
    criteria.add(endDateForSql)
    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.BETWEEN, criteria));
}
if (UtilValidate.isNotEmpty(partyIdForSearch)) {
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdForSearch));
}
if (UtilValidate.isNotEmpty(partyId)) {
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
}
if(UtilValidate.isNotEmpty(type)) {
    conditionList.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, type));
}
if (UtilValidate.isNotEmpty(number1)) {
    String number= number1.trim();
    conditionList.add(EntityCondition.makeCondition("number", EntityOperator.LIKE, "%" + number + "%"));
}
if (UtilValidate.isNotEmpty(name1)) {
    String name= name1.trim();
    conditionList.add(EntityCondition.makeCondition("name", EntityOperator.LIKE, "%" + name + "%"));
}
EntityQuery from = EntityQuery.use(delegator).from("TblTrainDetail");
if (UtilValidate.isNotEmpty(conditionList)) {
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList), EntityOperator.AND);
    from = from.where(condition).orderBy("date DESC");
}
trainList = from.queryIterator();
context.trainList = trainList;
context.partyId = partyId;

