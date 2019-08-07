import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery

import java.sql.Date
import java.text.SimpleDateFormat


Map returnValue = (Map)parameters.get("returnValue");
List<GenericValue> valueList = (List<GenericValue>)returnValue.get("list");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
if(UtilValidate.isNotEmpty(valueList)){
    List<Map<String,Object>> overtimeList = FastList.newInstance();
    for(value in valueList){
        Map<String,Object> objectMap = FastMap.newInstance();
        objectMap.putAll(value);
        String staffId = value.get("staff");
        String departmentId = value.get("department");
        String auditor = value.get("auditor");
        if(UtilValidate.isNotEmpty(staffId)){
            objectMap.put("type","个人");
            GenericValue staff = delegator.findOne("Person",UtilMisc.toMap("partyId",staffId),false);
            objectMap.put("fullName",staff.get("fullName"));
        }else if(UtilValidate.isNotEmpty(departmentId)){
            objectMap.put("type","组织");
            GenericValue department = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",departmentId),false);
            objectMap.put("groupName",department.get("groupName"));
        }
        GenericValue auditorMap = delegator.findOne("Person",UtilMisc.toMap("partyId",auditor),false);
        objectMap.put("auditorName",auditorMap.get("fullName"));
        objectMap.put("startDate", format.format(objectMap.get("startTime")));
        objectMap.put("endDate", format.format(objectMap.get("endTime")));
        overtimeList.add(objectMap);
    }
    context.overtimeList = overtimeList;
}
