import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery


GenericValue userLogin = (GenericValue)context.get("userLogin");
String partyId = userLogin.get("partyId");
String blankCaseSessionKey = parameters.blankCaseSessionKey;
context.blankCaseSessionKey = blankCaseSessionKey;
Map<String, Object> blankCaseData = request.getSession().getAttribute(blankCaseSessionKey);
Map<String, List> caseManagerData = new HashMap<>();
context.caseManagers = caseManagerData;
context.templateId = "";
//仅企业主账号及CASE创建者有权修改
boolean caseOwner = false;
List<GenericValue> caseParties = blankCaseData.get("caseParties");
List<Map<String, Object>> casePartyMembers = blankCaseData.get("casePartyMembers");
List<String> partyIds = new ArrayList<>();
List<String> partyManagerIds = new ArrayList<>();
Map<String, String> partyRolesMap = new HashMap<>();
Map<String, String> partyManagerMap = new HashMap<>();
for (GenericValue caseParty : caseParties) {
    partyIds.add(caseParty.getString("partyId"));
    partyManagerIds.add(caseParty.getString("personId"));
    partyRolesMap.put(caseParty.getString("partyId"), caseParty.getString("roleTypeId"));
    partyManagerMap.put(caseParty.getString("partyId"), caseParty.getString("personId"));
}

List<GenericValue> partyEntities = EntityQuery.use(delegator).from("PartyGroup").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds)).queryList();
List<GenericValue> partyManagerEntities = EntityQuery.use(delegator).from("Person").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyManagerIds)).queryList();
Map<String, GenericValue> partyManagerEntityMap = new HashMap<>();
for (GenericValue partyManager : partyManagerEntities) {
    partyManagerEntityMap.put(partyManager.getString("partyId"), partyManager);
}
for (GenericValue caseManager : partyEntities) {
    String casePartyId = caseManager.getString("partyId");
    String groupName = caseManager.getString("groupName");
    String partnerGroupName = caseManager.getString("partnerGroupName");
    if(UtilValidate.isNotEmpty(partnerGroupName)){
        groupName = partnerGroupName;
    }
    String partyRole = partyRolesMap.get(casePartyId)
    if(partyRole.equals("CASE_ROLE_OWNER") && partyId.equals(casePartyId)){
        caseOwner = true;
    }
    context.caseManagers.put(partyRole, [partyManagerMap.get(casePartyId), partyManagerEntityMap.get(partyManagerMap.get(casePartyId)).getString("fullName"), groupName]);
}
GenericValue caseObj = blankCaseData.get("case");

context.caseData = caseObj;
context.templateId = caseObj.getString("caseTemplate");
if(!caseOwner && !partyId.equals(caseObj.getString("createPartyId"))){
    throw new RuntimeException("无权修改CASE信息");
}

List<GenericValue> oldTimes = blankCaseData.get("caseBaseTimes");
if(UtilValidate.isNotEmpty(oldTimes)){
    context.oldTimes = oldTimes.collectEntries{b -> [b.getString("baseTimeId"), b.getDate("baseTime")]};
}else{
    context.oldTimes = [];
}

StringBuffer roles = new StringBuffer();
if(UtilValidate.isNotEmpty(caseParties)){
    if(caseParties.size() == 1){
        roles.append(caseParties.get(0).get("roleTypeId"));
    }else{
        roles.append("[");
        for(GenericValue genericValue : caseParties){
            roles.append(genericValue.get("roleTypeId")).append(",");
        }
        roles.deleteCharAt(roles.length() - 1).append("]");
    }
}
context.put("roles", roles);

GenericValue party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId",partyId)).queryOne();
if(party.get("partyTypeId").equals("PERSON")){//子账号创建，获取对应主账号信息
    context.userPersonId = partyId;
    GenericValue subAccountInfo = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("partyId", partyId)).cache().queryOne();
    partyId = subAccountInfo.getString("groupId");
}
GenericValue groupInfo = EntityQuery.use(delegator).from("BasicGroupInfo").where(UtilMisc.toMap("partyId", partyId)).cache().queryOne();
context.userRoleType = groupInfo.getString("roleTypeId");
context.userGroupId = partyId;
context.userGroupName = groupInfo.getString("groupName");
context.userPartner = false;

String partnerType = groupInfo.getString("partnerType");
if(UtilValidate.isNotEmpty(partnerType)){//合伙人
    context.userPartner = true;
    String partnerGroupName = groupInfo.getString("partnerGroupName");
    if(UtilValidate.isNotEmpty(partnerGroupName)){
        context.userGroupName = partnerGroupName;
    }
}
//查询用户所在组织的所有子账号，不包括合伙人，因为此处是选择项目经理，如果项目经理为合伙人子账号将造成业务混乱
List<GenericValue> subAccountInfos = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("groupId", partyId)).cache().queryList();
/*if(UtilValidate.isNotEmpty(partnerType)) {//合伙人只查询自己的子账号
    subAccountInfos = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("groupId", partyId)).cache().queryList();
}else{//非合伙人查询本身机构子账号及合伙人子账号
    subAccountInfos = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("groupName", context.userGroupName)).cache().queryList();
}*/
context.persons = subAccountInfos;


