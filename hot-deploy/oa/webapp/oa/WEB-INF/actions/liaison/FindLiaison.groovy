import javolution.util.FastList
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String responseTimeStart = parameters.get("responseTimeStart");
String responseTimeEnd = parameters.get("responseTimeEnd");
String title = parameters.get("title");
String contactListType = parameters.get("contactListType");
String reviewTheStatus = parameters.get("reviewTheStatus");

List<EntityCondition> conditionList = FastList.newInstance();
if(UtilValidate.isNotEmpty(responseTimeStart) && UtilValidate.isNotEmpty(responseTimeEnd)){
    conditionList.add(EntityCondition.makeCondition("createdStamp",
            EntityOperator.BETWEEN,UtilMisc.toList(new Timestamp(format.parse(responseTimeStart).getTime(),new Timestamp(format.parse(responseTimeEnd).getTime())))));
}

if(UtilValidate.isNotEmpty(title)){
    conditionList.add(EntityCondition.makeCondition("title",EntityOperator.LIKE,"%" + title + "%"));
}
if(UtilValidate.isNotEmpty(contactListType)){
    conditionList.add(EntityCondition.makeCondition("contactListType",contactListType));
}
if(UtilValidate.isNotEmpty(reviewTheStatus)){
    conditionList.add(EntityCondition.makeCondition("reviewTheStatus",reviewTheStatus));
}

List<GenericValue> workContactList = EntityQuery.use(delegator).from("TblWorkContactList").where(conditionList).queryList();

context.workContactList = workContactList;