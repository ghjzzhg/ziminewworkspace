import javolution.util.FastList
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String startDate = parameters.get("startDate");
String endDate = parameters.get("endDate");
String checkingInType = parameters.get("checkingInType");
String status = parameters.get("checkingInStatus");//正常、迟到、早退
String staffId = parameters.get("staff");
String weekDay =  parameters.get("weekDay");

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

List<EntityCondition> conditions = FastList.newInstance();

if(UtilValidate.isNotEmpty(startDate)){
    java.sql.Date startDateSql = new java.sql.Date(format.parse(startDate.substring(0,10)).getTime());
    conditions.add(EntityCondition.makeCondition("checkingInDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDateSql));
}
if(UtilValidate.isNotEmpty(endDate)){
    java.sql.Date endDateSql = new java.sql.Date(format.parse(endDate.substring(0,10)).getTime());
    conditions.add(EntityCondition.makeCondition("checkingInDate",EntityOperator.LESS_THAN_EQUAL_TO,endDateSql));
}
if(UtilValidate.isNotEmpty(checkingInType)){//签到（签退）
    conditions.add(EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS,checkingInType));
}
if(UtilValidate.isNotEmpty(status)){//正常、迟到、早退
    conditions.add(EntityCondition.makeCondition("checkingInStatus",EntityOperator.EQUALS,status));
}
if(UtilValidate.isNotEmpty(staffId)){//考勤员工
    conditions.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staffId));
}
if(UtilValidate.isNotEmpty(weekDay)){//考勤星期
    conditions.add(EntityCondition.makeCondition("weekDay",EntityOperator.EQUALS,Integer.parseInt(weekDay)));
}
EntityListIterator checkingInList = null;
if(conditions.size() > 0){
    checkingInList = EntityQuery.use(delegator)
            .from("TblCheckingFor")
            .where(conditions)
            .orderBy("registerDate DESC")
            .queryIterator();
}else {
    checkingInList = EntityQuery.use(delegator)
            .from("TblCheckingFor")
            .orderBy("registerDate DESC")
            .queryIterator();
}
List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
if(null != checkingInList && checkingInList.getResultsSizeAfterPartialList() > 0){
    totalCount = checkingInList.getResultsSizeAfterPartialList();
    pageList = checkingInList.getPartialList(lowIndex, viewSize);
}
checkingInList.close();
context.checkingInList = pageList;
context.viewIndex = viewIndex;
context.highIndex = highIndex;
context.totalCount = totalCount;
context.viewSize = viewSize;
context.lowIndex = lowIndex;