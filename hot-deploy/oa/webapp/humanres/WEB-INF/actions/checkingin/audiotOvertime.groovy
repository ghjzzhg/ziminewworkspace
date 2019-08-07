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

String overtimeId = parameters.get("overtimeId");
GenericValue value = delegator.findOne("TblOvertime", UtilMisc.toMap("overtimeId",overtimeId), false);
if(UtilValidate.isNotEmpty(value)){
    List<Map<String,Object>> overtimeList = FastList.newInstance();
    Map<String,Object> overtime = FastMap.newInstance();
    overtime.putAll(value);
    String staffId = value.get("staff");
    String departmentId = value.get("department");
    String auditor = value.get("auditor");
    if(UtilValidate.isNotEmpty(staffId)){
        overtime.put("type","个人");
        GenericValue staff = delegator.findOne("Person",UtilMisc.toMap("partyId",staffId),false);
        overtime.put("fullName",staff.get("fullName"));
    }else if(UtilValidate.isNotEmpty(departmentId)){
        overtime.put("type","组织");
        GenericValue department = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",departmentId),false);
        overtime.put("groupName",department.get("groupName"));
    }
    GenericValue auditorMap = delegator.findOne("Person",UtilMisc.toMap("partyId",auditor),false);
    overtime.put("auditorName",auditorMap.get("fullName"));
    context.overtime = overtime;
}
