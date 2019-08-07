import javolution.util.FastList
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
startDate = parameters.get("startDate");
endDate = parameters.get("endDate");
gender = parameters.get("gender");
String workerSn1 = parameters.get("workerSn");
String fullName1 = parameters.get("fullName");
String jobState1 = parameters.get("jobState");

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

context.departmentId = partyId;
if (partyId != null) {
    resultList = runService("getAllLowerDepartments", dispatcher.getDispatchContext().getModelService("getAllLowerDepartments").makeValid(context, ModelService.IN_PARAM));
    departmentIdList = resultList.get("data");
    condition = EntityCondition.makeCondition("department", EntityOperator.IN, departmentIdList)
    staffList = EntityQuery.use(delegator).from("StaffInformationDetailView").where(condition).queryIterator();
} else {
    List<EntityCondition> conditionList = FastList.newInstance();
    List criteria = new LinkedList();
    if (UtilValidate.isNotEmpty(startDate) && !UtilValidate.isNotEmpty(endDate)) {
        startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
        criteria.add(startDateForSql )
        conditionList.add(EntityCondition.makeCondition("workDate", EntityOperator.GREATER_THAN_EQUAL_TO, criteria));
    }
    if (UtilValidate.isNotEmpty(endDate) && !UtilValidate.isNotEmpty(startDate)) {
        endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
        criteria.add(endDateForSql)
        conditionList.add(EntityCondition.makeCondition("workDate", EntityOperator.LESS_THAN_EQUAL_TO, criteria));
    }
    if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
        startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
        endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
        criteria.add(startDateForSql)
        criteria.add(endDateForSql)
        conditionList.add(EntityCondition.makeCondition("workDate", EntityOperator.BETWEEN, criteria));
    }

    if (UtilValidate.isNotEmpty(gender)) {
        conditionList.add(EntityCondition.makeCondition("gender", EntityOperator.EQUALS, gender));
    }
    if (UtilValidate.isNotEmpty(workerSn1)) {
        String workerSn= workerSn1.trim();
        conditionList.add(EntityCondition.makeCondition("workerSn", EntityOperator.LIKE, "%" + workerSn + "%"));
    }
    if (UtilValidate.isNotEmpty(fullName1)) {
        String fullName= fullName1.trim();
        conditionList.add(EntityCondition.makeCondition("fullName", EntityOperator.LIKE, "%" + fullName + "%"));
    }
    if (UtilValidate.isNotEmpty(jobState1)){
        String jobState = jobState1.trim();
        conditionList.add(EntityCondition.makeCondition("jobState", EntityOperator.EQUALS, jobState));
    }

    EntityListIterator from = EntityQuery.use(delegator).from("StaffInformationDetailView").where(conditionList).orderBy("workDate DESC").queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != from && from.getResultsSizeAfterPartialList() > 0){
        totalCount = from.getResultsSizeAfterPartialList();
        pageList = from.getPartialList(lowIndex, viewSize);
    }
    staffList = pageList;
}
context.recordList = staffList;
context.viewIndex = viewIndex;
context.highIndex = highIndex;
context.totalCount = totalCount;
context.viewSize = viewSize;
context.lowIndex = lowIndex;
context.oaAdminPermission = security.hasEntityPermission("OA","_ADMIN", session);


