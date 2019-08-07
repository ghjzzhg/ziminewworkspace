import org.ofbiz.base.util.UtilMisc

partyId = parameters.get("partyId");
staffList = delegator.findByAnd("StaffInformationDetailView", UtilMisc.toMap("partyId",partyId));
employeeRecordMap = [:];
if (staffList.size()!=0){
    employeeRecordMap = staffList[0];
}
context.employeeRecordMap = employeeRecordMap;
context.partyId = partyId;