import javolution.util.FastList
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery

startDate = parameters.get("startDate");
endDate = parameters.get("endDate");
partyId = parameters.get("partyId");
partyIdForSearch = parameters.get("partyIdForSearch");
changeType = parameters.get("changeType");

int viewIndex = 0;
try {
    viewIndex = Integer.parseInt((String) parameters.get("VIEW_INDEX"));
} catch (Exception e) {
    viewIndex = 0;
}
int totalCount = 0;
int viewSize = 5;
try {
    viewSize = Integer.parseInt((String) parameters.get("VIEW_SIZE"));
} catch (Exception e) {
    viewSize = 5;
}
// 计算当前显示页的最小、最大索引(可能会超出实际条数)
int lowIndex = viewIndex * viewSize + 1;
int highIndex = (viewIndex + 1) * viewSize;

List<EntityCondition> conditionList = FastList.newInstance();
List criteria = new LinkedList();
if (UtilValidate.isNotEmpty(startDate) && !UtilValidate.isNotEmpty(endDate)) {
    startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
    criteria.add(startDateForSql)
    conditionList.add(EntityCondition.makeCondition("changeDate", EntityOperator.GREATER_THAN_EQUAL_TO, criteria));
}
if (UtilValidate.isNotEmpty(endDate) && !UtilValidate.isNotEmpty(startDate)) {
    endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
    criteria.add(endDateForSql)
    conditionList.add(EntityCondition.makeCondition("changeDate", EntityOperator.LESS_THAN_EQUAL_TO, criteria));
}
if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
    startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
    endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
    criteria.add(startDateForSql)
    criteria.add(endDateForSql)
    conditionList.add(EntityCondition.makeCondition("changeDate", EntityOperator.BETWEEN, criteria));
}
if (UtilValidate.isNotEmpty(partyIdForSearch)) {
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdForSearch));
}
if (UtilValidate.isNotEmpty(partyId)) {
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
}
if (UtilValidate.isNotEmpty(changeType)) {
    conditionList.add(EntityCondition.makeCondition("changeType", EntityOperator.EQUALS, changeType));
}
EntityListIterator from = EntityQuery.use(delegator).from("searchTblPostChange").where(conditionList).orderBy("changeDate DESC").queryIterator();
List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
if(null != from && from.getResultsSizeAfterPartialList() > 0){
    totalCount = from.getResultsSizeAfterPartialList();
    pageList = from.getPartialList(lowIndex, viewSize);
}
from.close();
postList = pageList;
context.postList = postList;
context.viewIndex = viewIndex;
context.highIndex = highIndex;
context.totalCount = totalCount;
context.viewSize = viewSize;
context.lowIndex = lowIndex;
context.partyId = partyId;
