import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

String contactListId=parameters.get("contactListId");
GenericValue tblWorkContactList = delegator.findByPrimaryKey("TblWorkContactList", UtilMisc.toMap("contactListId", contactListId));
Map<String,Object> workContactListMap = FastMap.newInstance();
workContactListMap.putAll(tblWorkContactList);
String mainPersonNameString = "";
String copyPersonNameString = "";
String mainPerson = tblWorkContactList.get("mainPerson");
String copyPerson = tblWorkContactList.get("copyPerson");
if (null != mainPerson && !"".equals(mainPerson)) {
    String[] partyIds = mainPerson.split(",");
    for (int i = 0; i < partyIds.length; i++) {
        String partyId = partyIds[i];
        personMap = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        String mainPersonName = personMap.get("fullName");
        mainPersonNameString = mainPersonNameString + mainPersonName + " ";
    }
}
workContactListMap.put("mainPersonNameString",mainPersonNameString);
if (null != copyPerson && !"".equals(copyPerson)) {
    String[] copyPartyIds = copyPerson.split(",");
    for (int i = 0; i < copyPartyIds.length; i++) {
        String copyPartyId = copyPartyIds[i];
        personMap = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", copyPartyId));
        String copyPersonName = personMap.get("fullName");
        copyPersonNameString = copyPersonNameString + copyPersonName + " ";
    }
}
if (copyPersonNameString==""){
    copyPersonNameString="无";
}
workContactListMap.put("copyPersonNameString",copyPersonNameString);



TblTypeManagementListMap=delegator.findByPrimaryKey("TblTypeManagementList",UtilMisc.toMap("typeManagementListId",tblWorkContactList.get("contactListType")));
String contactListType=TblTypeManagementListMap.get("typeManagement");
workContactListMap.put("contactListType",contactListType);

/**
 *
 */
List<GenericValue> workSheetAuditInformationList=delegator.findByAnd("TblWorkSheetAuditInfor",UtilMisc.toMap("contactListId",contactListId));
List<GenericValue> workSheetAuditInformationLists=new ArrayList<>();
for (Map map:workSheetAuditInformationList){
    Map workSheetAuditInformationMap=new HashMap();
    workSheetAuditInformationMap.putAll(map);
    String responseTime=map.get("responseTime");
    if (UtilValidate.isNotEmpty(responseTime)) {
        workSheetAuditInformationMap.put("responseTime", responseTime.substring(0, responseTime.length() - 2));
    }
    workSheetAuditInformationLists.add(workSheetAuditInformationMap);
}

List<GenericValue> workSheetSignForInformationList=delegator.findByAnd("TblWorkSheetSignForInfor",UtilMisc.toMap("contactListId",contactListId));
List<GenericValue> workSheetSignForInformationLists=new ArrayList<>();
for (Map map:workSheetSignForInformationList){
    Map workSheetSignForInformationMap=new HashMap();
    workSheetSignForInformationMap.putAll(map);
    String responseTime=map.get("SignforTime");
    if (UtilValidate.isNotEmpty(responseTime)) {
        workSheetSignForInformationMap.put("SignforTime", responseTime.substring(0, responseTime.length() - 2));
    }
    workSheetSignForInformationLists.add(workSheetSignForInformationMap);
}

/*
List<GenericValue> replyInformationList=delegator.findByAnd("TblWorkSheetReplyInformation",UtilMisc.toMap("contactListId",contactListId));
List<GenericValue> replyInformationLists=new ArrayList<>();
for (Map map:replyInformationList){
    Map replyInformationMap=new HashMap();
    replyInformationMap.putAll(map);
    String replyTime=map.get("replyTime");
    if (UtilValidate.isNotEmpty(replyTime)) {
        replyInformationMap.put("replyTime", replyTime.substring(0, replyTime.length() - 2));
    }
    replyInformationLists.add(replyInformationMap);
}*/

context.liaisonMap = workContactListMap;
context.workSheetAuditInformationList=workSheetAuditInformationLists;
context.workSheetSignForInformationList=workSheetSignForInformationLists;
/*context.replyInformationList=replyInformationLists;*/

