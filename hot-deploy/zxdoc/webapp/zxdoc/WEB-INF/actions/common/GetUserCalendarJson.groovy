import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

import java.sql.Date
import java.sql.Timestamp

String includeAll = parameters.includeAll;
if(UtilValidate.isEmpty(includeAll)){
    includeAll = false;
}

List<EntityCondition> conditions = [];
conditions.add(EntityCondition.makeCondition("owner", EntityOperator.EQUALS, userLogin.partyId));
/*if(UtilValidate.isEmpty(includeAll) || "false".equals(includeAll)){
    conditions.add(EntityCondition.makeCondition("completed", EntityOperator.EQUALS, "N"));
}*/

String start = parameters.start;
String end = parameters.end;

//conditions.add(EntityCondition.makeCondition("start", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(UtilDateTime.toSqlDate(start, "yyyy-MM-dd").getTime())));
Date endDate = UtilDateTime.toSqlDate(end, "yyyy-MM-dd");
Calendar cal = Calendar.getInstance();
cal.setTime(endDate);
cal.add(Calendar.DAY_OF_MONTH, 1);
//conditions.add(EntityCondition.makeCondition("end", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(cal.getTimeInMillis())));
List<GenericValue> calendars = from("TblCalendarEvent").where(conditions).queryList();
List list = new ArrayList();
for (GenericValue calendar:calendars)
{
    Map map = new HashMap();
    map.put("id",calendar.get("id"));
    map.put("owner",calendar.get("owner"));
    map.put("title",calendar.get("title"));
    map.put("content",calendar.get("content"));
    String startTime = calendar.get("start");
    map.put("start",startTime);
    //这里的截断是解决时间跨度显示不全的问题
    String endTime = calendar.get("end");
    map.put("end",endTime.substring(0,10) + " 09:00:00");
    map.put("completed",calendar.get("completed"));
    if(calendar.get("completed").equals("Y"))
    {
        map.put("backgroundColor","green");
    }else
    {
        map.put("backgroundColor","red");
    }
    list.add(map);
}
request.setAttribute("data", list);
