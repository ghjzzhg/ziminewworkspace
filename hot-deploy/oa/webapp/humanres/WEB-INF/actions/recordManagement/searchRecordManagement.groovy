import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

public Map<String,Object> searchRecordManagement(){
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    String partyId = context.get("partyId");
    String startDate = context.get("startDate")
    String endDate = context.get("endDate")
    String gender = context.get("gender")
    String workerSn = context.get("workerSn")
    String  fullName = context.get("fullName")
    context.departmentId = partyId;
    EntityListIterator staffList = null;
    if (partyId != null) {
        resultList = runService("getAllLowerDepartments", dispatcher.getDispatchContext().getModelService("getAllLowerDepartments").makeValid(context, ModelService.IN_PARAM));
        departmentIdList = resultList.get("data");
        condition = EntityCondition.makeCondition("department", EntityOperator.IN, departmentIdList)
        staffList = EntityQuery.use(delegator).from("StaffInformationDetailView").where(condition).queryIterator();
    } else {
        List conditionList = new ArrayList();
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
        if (UtilValidate.isNotEmpty(workerSn)) {
            conditionList.add(EntityCondition.makeCondition("workerSn", EntityOperator.LIKE, "%" + workerSn + "%"));
        }
        if (UtilValidate.isNotEmpty(fullName)) {
            conditionList.add(EntityCondition.makeCondition("fullName", EntityOperator.LIKE, "%" + fullName + "%"));
        }

        EntityQuery from = EntityQuery.use(delegator).from("StaffInformationDetailView");
        if (UtilValidate.isNotEmpty(conditionList)) {
            EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList), EntityOperator.AND);
            from = from.where(condition);
        }
        list = delegator.findList("StaffInformationDetailView",EntityCondition.makeCondition(UtilMisc.toList(conditionList), EntityOperator.AND),null,null,null,false)
        staffList = from.queryIterator();
    }
    List<GenericValue> recordList = new ArrayList();
    if(null != staffList && staffList.getResultsSizeAfterPartialList() > 0){
        totalCount = staffList.getResultsSizeAfterPartialList();
        recordList = staffList.getPartialList(lowIndex, viewSize);
    }
    staffList.close();
    Map<String,Object> result = ServiceUtil.returnSuccess();
    Map<String,Object> resultMap = new HashMap<String,Object>();
    resultMap.put("recordList",recordList);
    resultMap.put("partyId",partyId);
    resultMap.put("viewIndex",viewIndex);
    resultMap.put("highIndex",highIndex);
    resultMap.put("totalCount",totalCount);
    resultMap.put("viewSize",viewSize);
    resultMap.put("lowIndex",lowIndex);
    result.put("data",resultMap);
    return result;
}