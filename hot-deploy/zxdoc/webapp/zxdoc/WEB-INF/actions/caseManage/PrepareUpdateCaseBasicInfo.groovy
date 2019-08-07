import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String partyId = userLogin.get("partyId");

GenericValue caseParty = from("TblCaseParty").where("caseId", parameters.caseId, "partyId", partyId, "roleTypeId", "CASE_ROLE_OWNER").queryOne();
GenericValue casePerson = from("TblCase").where("caseId", parameters.caseId, "createPartyId", partyId).queryOne();
if(caseParty == null && casePerson == null){
    throw new RuntimeException("非企业账户，无权修改CASE信息");
}
GenericValue caseObj = from("TblCase").where("caseId", parameters.caseId).queryOne();
String templateId = caseObj.get("caseTemplate")
context.templateId = templateId;
context.caseData = caseObj;
if(UtilValidate.isNotEmpty(caseObj.getString("caseTemplate"))){
    context.put("roles", from("TblCaseTemplate").where("id", caseObj.getString("caseTemplate")).queryOne().get("roles"));
}else{
    List<GenericValue> casePartyList = EntityQuery.use(delegator).select().from("TblCaseParty").where(UtilMisc.toMap("caseId",parameters.caseId)).queryList();
    StringBuffer roles = new StringBuffer();
    if(UtilValidate.isNotEmpty(casePartyList)){
        if(casePartyList.size() == 1){
            roles.append(casePartyList.get(0).get("roleTypeId"));
        }else{
            roles.append("[");
            for(GenericValue genericValue : casePartyList){
                roles.append(genericValue.get("roleTypeId")).append(",");
            }
            roles.deleteCharAt(roles.length() - 1).append("]");
        }
    }
    context.put("roles", roles);
}
List<GenericValue> rolesData = from("casePartyIdNameDescription").where("caseId", parameters.caseId).queryList();
context.rolesData = rolesData.collectEntries{
    [it.getString("roleTypeId"), [partyId : it.getString("partyId"), groupName: it.getString("groupName")]]
}
context.personsData = rolesData.collectEntries{
    [it.getString("roleTypeId"), [personId : it.getString("personId"), fullName: it.getString("fullName")]]
}

List<GenericValue> oldTimes = from("TblCaseBaseTime").where("caseId", parameters.caseId).queryList();
if(UtilValidate.isNotEmpty(oldTimes)){
    context.oldTimes = oldTimes.collectEntries{b -> [b.getString("baseTimeId"), b.getDate("baseTime")]};
}else{
    context.oldTimes = [];
}

context.baseTimes = from("TblCaseTemplateBaseTime").where("templateId", templateId).cache().orderBy("seq").queryList();