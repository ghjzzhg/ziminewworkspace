import org.ofbiz.base.util.UtilMisc

id = parameters.get("id");
perfExamPerson = [:];
Map map= new HashMap();
if(id){
    perfExamPerson = delegator.findOne("TblPerfExamPerson", UtilMisc.toMap("planId", id), true);
    String partyId=perfExamPerson.get("staff");
    staffList = delegator.findByAnd("changeDPL", UtilMisc.toMap("partyId",partyId));
    String department="";
    String position="";
    String level="";
    if (staffList.size()!=0){
        employeeRecordMap = staffList[0];
        department=employeeRecordMap.get("department");
        position=employeeRecordMap.get("position");
        level=employeeRecordMap.get("level");
    }
    map.putAll(perfExamPerson);
    map.put("department",department);
    map.put("position",position);
    map.put("level",level);
}

context.resultMap =map;