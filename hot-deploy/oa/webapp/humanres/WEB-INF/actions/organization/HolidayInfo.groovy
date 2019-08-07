import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

Map returnValue = (Map)parameters.get("returnValue");
List<GenericValue> valueList = (List<GenericValue>)returnValue.get("list");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
if(UtilValidate.isNotEmpty(valueList)){
    List<Map<String,Object>> holidayInfoList = FastList.newInstance();
    for(value in valueList){
        Map<String,Object> objectMap = FastMap.newInstance();
        objectMap.putAll(value);
        String staffId = value.get("staff");
        String departmentId = value.get("department");

        if(UtilValidate.isNotEmpty(staffId)){
            objectMap.put("type","个人");
            GenericValue staff = delegator.findOne("Person",UtilMisc.toMap("partyId",staffId),false);
            objectMap.put("fullName",staff.get("fullName"));
        }else if(UtilValidate.isNotEmpty(departmentId)){
            objectMap.put("type","组织");
            GenericValue department = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",departmentId),false);
            objectMap.put("groupName",department.get("groupName"));
        }
        objectMap.put("startDate", format.format(objectMap.get("startTime")));
        objectMap.put("endDate", format.format(objectMap.get("endTime")));
        holidayInfoList.add(objectMap);
    }
    context.holidayInfoList = holidayInfoList;
}
