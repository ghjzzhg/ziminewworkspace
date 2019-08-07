import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

GenericValue userLogin = (GenericValue)context.get("userLogin");
String loginUserPartyId = userLogin.partyId;
GenericValue userParty = from("Party").where("partyId", loginUserPartyId).queryOne();

//查询文件路径
Map<String,Object> casesMap = dispatcher.runSync("providerCases", UtilMisc.<String, Object>toMap("userLogin", userLogin));
List<Map<String,Object>> caseDataMap = casesMap.get("data");
List<String> shareFolderIdList = new ArrayList<>();
List<String> shareFolderPathList = new ArrayList<>();
for(Map<String,Object> caseMap : caseDataMap) {
    String caseId = caseMap.get("caseId");
    GenericValue companyRole = from("casePartyIdNameDescription").where("caseId", caseId, "roleTypeId", "CASE_ROLE_OWNER").queryOne();
    if(companyRole == null){
        continue;
    }
    String partyGroupId = loginUserPartyId;
    if("PERSON".equalsIgnoreCase(userParty.getString("partyTypeId"))){
        //子账户
        GenericValue relation = from("PartyRelationship").where(UtilMisc.toMap("partyIdTo", loginUserPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT")).queryOne();
        partyGroupId = relation.getString("partyIdFrom");
    }
    GenericValue currentUserRole = from("casePartyIdNameDescription").where("caseId", caseId, "partyId", partyGroupId).queryOne();
    if(currentUserRole == null){
        continue;
    }
    String roleType = currentUserRole.getString("roleTypeId");//参与角色

    GenericValue caseEntity = from("TblCase").where("caseId", caseId).queryOne();
    String caseCategory = caseEntity.getString("caseCategory");

    String fileSharePartyId = companyRole.getString("partyId");
    List<GenericValue> folders = EntityQuery.use(delegator).from("TblCaseCategoryFolder").where(EntityCondition.makeCondition("caseCategory", EntityOperator.EQUALS, caseCategory), EntityCondition.makeCondition("roles", EntityOperator.LIKE, "%" + roleType + "%")).queryList();
    for (GenericValue folder : folders) {
        List conditionList = new ArrayList();
        String folderPath = folder.getString("folderPath");
        String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1, folderPath.length());
        String parentFolderPath = folderPath.substring(0,folderPath.lastIndexOf("/")+1);
        if(parentFolderPath.equals("/")){
            conditionList.add(EntityCondition.makeCondition("parentFolderId", EntityOperator.EQUALS, null));
        }else{
            conditionList.add(EntityCondition.makeCondition("folderPath", EntityOperator.EQUALS, "/" + fileSharePartyId +parentFolderPath));
        }
        conditionList.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fileSharePartyId));
        conditionList.add(EntityCondition.makeCondition("folderName", EntityOperator.EQUALS, folderName));
        GenericValue folderInfo = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(conditionList).queryOne();
        if(!shareFolderIdList.contains(folderInfo.get("folderId"))){
            shareFolderIdList.add(folderInfo.get("folderId"));
            shareFolderPathList.add(folderInfo.get("folderPath") + folderInfo.get("folderName").toString())
        }
    }
}
List list = new ArrayList();
list.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, "1"));
EntityCondition cond1 = EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1");
EntityCondition cond2 = EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, null);
EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2), EntityOperator.OR);
list.add(cond)
List conList = new ArrayList();
for(String path : shareFolderPathList){
    EntityCondition con = EntityCondition.makeCondition("folderPath", EntityOperator.EQUALS, path);
    conList.add(con);
}
conList.add(EntityCondition.makeCondition("folderId", EntityOperator.IN, shareFolderIdList));
EntityCondition con1 = EntityCondition.makeCondition(conList, EntityOperator.OR);
list.add(con1)
EntityListIterator fileValue = EntityQuery.use(delegator).select().from("DataResourceFileList").where(list).orderBy("createFileTime DESC").queryIterator();
List<GenericValue> fileList = fileValue.getPartialList(0,5);
fileValue.close();
List<Map<String, Object>> fileMapList = new ArrayList<>();
for(GenericValue file : fileList){
    Map<String, Object> map = new HashMap<>();
    map.putAll(file);
    GenericValue folder = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",map.get("folderId"),"folderType","1")).queryOne();
    GenericValue party = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",folder.get("partyId"))).queryOne();
    GenericValue fileParty = EntityQuery.use(delegator).select().from("Party").where(UtilMisc.toMap("partyId",file.get("createPartyId"))).queryOne();
    String fullName;
    if(party.get("partyTypeId").equals("PARTY_GROUP")){
        GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",map.get("partyId"))).queryOne();
        fullName = partyGroup.get("groupName").toString();
    }else{
        GenericValue partyGroup = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",map.get("partyId"))).queryOne();
        fullName = partyGroup.get("fullName").toString();
    }
    String fileFullName;
    if(fileParty.get("partyTypeId").equals("PARTY_GROUP")){
        GenericValue partyGroup = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",map.get("partyId"))).queryOne();
        fileFullName = partyGroup.get("groupName").toString();
    }else{
        fileFullName = file.get("fullName").toString();
    }
    String allFileName = map.fileName;
    String fileName = allFileName;
    if(fileName.length() > 15){
        fileName = fileName.substring(0, 15) + "...";
    }
    map.put("allFileName",allFileName)
    map.put("fileName",fileName)
    map.put("fullName",fileFullName)
    map.put("ownershipName",fullName)
    List<GenericValue> fileHistory = EntityQuery.use(delegator).select().from("TblHistoryFiles").where(UtilMisc.toMap("dataResourceId",file.get("fileId").toString())).queryList();
    String fileHidtoryFlag = "0";
    if(UtilValidate.isNotEmpty(fileHistory)){
        fileHidtoryFlag = "1";
    }
    map.put("fileHidtoryFlag",fileHidtoryFlag);
    fileMapList.add(map);
}
    context.put("fileList",fileMapList)


