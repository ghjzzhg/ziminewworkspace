import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

department = parameters.get("department");

departmentIds = [];
lowerDepartments = [];
if(department){
    departmentIds.push(department);
    result = runService("getLowerDepartments", [departmentId: department]);
    lowerDepartments = result.get("data");
}
if(lowerDepartments){
    for (Map dep : lowerDepartments) {
        departmentIds.push(dep.get("partyId"));
    }
}
List list= new ArrayList();
if(departmentIds.size() > 0){
    perfExamPersons = delegator.findList("PerfExamDepartmentView", EntityCondition.makeCondition("departmentId", EntityOperator.IN, departmentIds), null, null, null, false);
}else{
    perfExamPersons = EntityQuery.use(delegator).select().from("TblPerfExamPerson").where(UtilMisc.toMap("planState","1")).queryList();
    Iterator<GenericValue> it = perfExamPersons.iterator();
    while (it.hasNext()) {
        GenericValue  genericValue =it.next();
        Map map= new HashMap();
        map.putAll(genericValue);
        String partyId =genericValue.get("staff");
        List<GenericValue> resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
        String groupName = "";
        String occupationName = "";
        if (resultList.size()!=0){
            Map<String,Object> resultMap = resultList.get(0);
            groupName = resultMap.get("departmentName");
            occupationName = resultMap.get("occupationName");
        }
        map.put("department",groupName);
        map.put("post",occupationName);
        list.add(map);
    }
}
context.perfExamPersonsList = list;