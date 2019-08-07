import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

GenericValue userLogin = (GenericValue) context.get("userLogin");
partyId = parameters.get("partyId");
recordMap = [:];
if (partyId!=null&&partyId!=""){
    recordList = delegator.findByAnd("StaffInformationDetailEdit", UtilMisc.toMap("partyId",partyId));
    recordMap = recordList[0];
}else{
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblStaff","numName","workerSn","prefix","employee","userLogin",userLogin));
    recordMap.put("workerSn",uniqueNumber.get("number"));
}
/*if(UtilValidate.isNotEmpty(Id)){
    recordMap.put("Id",Id);
}*/
context.recordMap = recordMap;
