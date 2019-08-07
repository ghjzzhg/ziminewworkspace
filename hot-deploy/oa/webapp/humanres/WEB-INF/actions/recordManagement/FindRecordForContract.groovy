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
contractType = parameters.get("contractType")
contractNumber = parameters.get("contractNumber")
contractName = parameters.get("contractName")
List conditionList = new ArrayList();
List criteria = new LinkedList();
if (UtilValidate.isNotEmpty(startDate) && !UtilValidate.isNotEmpty(endDate)) {
    startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
    criteria.add(startDateForSql)
    conditionList.add(EntityCondition.makeCondition("signDate", EntityOperator.GREATER_THAN_EQUAL_TO, criteria));
}
if (UtilValidate.isNotEmpty(endDate) && !UtilValidate.isNotEmpty(startDate)) {
    endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
    criteria.add(endDateForSql)
    conditionList.add(EntityCondition.makeCondition("signDate", EntityOperator.LESS_THAN_EQUAL_TO, criteria));
}
if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
    startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
    endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
    criteria.add(startDateForSql)
    criteria.add(endDateForSql)
    conditionList.add(EntityCondition.makeCondition("signDate", EntityOperator.BETWEEN, criteria));
}
if (UtilValidate.isNotEmpty(partyIdForSearch)) {
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdForSearch));
}
if (UtilValidate.isNotEmpty(partyId)) {
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
}
if (UtilValidate.isNotEmpty(contractType)) {
    conditionList.add(EntityCondition.makeCondition("contractType", EntityOperator.EQUALS, contractType));
}
if (UtilValidate.isNotEmpty(contractNumber)) {
    conditionList.add(EntityCondition.makeCondition("contractNumber", EntityOperator.LIKE, "%" + contractNumber + "%"));
}
if (UtilValidate.isNotEmpty(contractName)) {
    conditionList.add(EntityCondition.makeCondition("contractName", EntityOperator.LIKE, "%" + contractName + "%"));
}
conditionList.add(EntityCondition.makeCondition("contractStatus", EntityOperator.EQUALS,"CONTRACT_STATUS_A"));
EntityQuery from = EntityQuery.use(delegator).from("TblContractDetail");
if (UtilValidate.isNotEmpty(conditionList)) {
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList), EntityOperator.AND);
    from = from.where(condition).orderBy("signDate DESC");
}
contractList = from.queryIterator();
context.contractList = contractList;
context.partyId = partyId;

