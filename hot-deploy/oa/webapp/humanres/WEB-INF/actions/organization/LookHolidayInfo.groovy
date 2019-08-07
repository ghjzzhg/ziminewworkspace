import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

String holidayId = parameters.get("holidayId");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
if(UtilValidate.isNotEmpty(holidayId)){
    lookHolidayInfoMap =  FastMap.newInstance();
    GenericValue holiday = delegator.findOne("TblHoliday",UtilMisc.toMap("holidayId",holidayId),false);
    lookHolidayInfoMap.put("holidayId",holidayId);
    lookHolidayInfoMap.put("startDate",format.format(holiday.get("startTime")));
    lookHolidayInfoMap.put("endDate",format.format(holiday.get("endTime")));
    String staffId = holiday.get("staff");
    String departmentId = holiday.get("department");
    lookHolidayInfoMap.put("staff",staffId);
    lookHolidayInfoMap.put("department",departmentId);
    if(UtilValidate.isNotEmpty(staffId)){
        lookHolidayInfoMap.put("type","个人");
        GenericValue staff = delegator.findOne("Person",UtilMisc.toMap("partyId",staffId),false);
        lookHolidayInfoMap.put("fullName",staff.get("fullName"));
    }else if(UtilValidate.isNotEmpty(departmentId)){
        lookHolidayInfoMap.put("type","组织");
        GenericValue department = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",departmentId),false);
        lookHolidayInfoMap.put("groupName",department.get("groupName"));
    }
    lookHolidayInfoMap.put("description",holiday.get("description"));
    context.lookHolidayInfoMap = lookHolidayInfoMap;
}

