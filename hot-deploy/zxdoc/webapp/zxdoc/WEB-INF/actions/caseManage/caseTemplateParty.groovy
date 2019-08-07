import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery


GenericValue userLogin = (GenericValue)context.get("userLogin");
String partyId = userLogin.get("partyId");
String caseId = parameters.caseId;
List<GenericValue> caseManagers = null;
Map<String, List> caseManagerData = new HashMap<>();
context.caseManagers = caseManagerData;
context.templateId = parameters.templateId;
if(UtilValidate.isNotEmpty(caseId)){
    //仅企业主账号及CASE创建者有权修改
    boolean caseOwner = false;
    caseManagers = EntityQuery.use(delegator).from("CaseManagerInfo").where(UtilMisc.toMap("caseId",caseId)).cache().queryList();
    for (GenericValue caseManager : caseManagers) {
        String casePartyId = caseManager.getString("groupId");
        String groupName = caseManager.getString("groupName");
        String partnerGroupName = caseManager.getString("partnerGroupName");
        if(UtilValidate.isNotEmpty(partnerGroupName)){
            groupName = partnerGroupName;
        }
        String partyRole = caseManager.getString("roleTypeId")
        if(partyRole.equals("CASE_ROLE_OWNER") && partyId.equals(casePartyId)){
            caseOwner = true;
        }
        context.caseManagers.put(partyRole, [caseManager.getString("managerPartyId"), caseManager.getString("managerName"), groupName]);
    }
    GenericValue caseObj = from("TblCase").where("caseId", caseId).queryOne();

    context.caseData = caseObj;
    context.templateId = caseObj.getString("caseTemplate");
    if(!caseOwner && !partyId.equals(caseObj.getString("createPartyId"))){
        throw new RuntimeException("无权修改CASE信息");
    }

    List<GenericValue> oldTimes = from("TblCaseBaseTime").where("caseId", caseId).cache().queryList();
    if(UtilValidate.isNotEmpty(oldTimes)){
        context.oldTimes = oldTimes.collectEntries{b -> [b.getString("baseTimeId"), b.getDate("baseTime")]};
    }else{
        context.oldTimes = [];
    }
}

if(UtilValidate.isNotEmpty(context.templateId)){//有模板的，新增或修改时均已模板设定的参与方为基准
    context.put("roles", delegator.findOne("TblCaseTemplate", UtilMisc.toMap("id", context.templateId), false).get("roles"));
    context.baseTimes = from("TblCaseTemplateBaseTime").where("templateId", context.templateId).cache().orderBy("seq").queryList();
}else if(UtilValidate.isNotEmpty(caseId)){
    StringBuffer roles = new StringBuffer();
    if(UtilValidate.isNotEmpty(caseManagers)){
        if(caseManagers.size() == 1){
            roles.append(caseManagers.get(0).get("roleTypeId"));
        }else{
            roles.append("[");
            for(GenericValue genericValue : caseManagers){
                roles.append(genericValue.get("roleTypeId")).append(",");
            }
            roles.deleteCharAt(roles.length() - 1).append("]");
        }
    }
    context.put("roles", roles);
}
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
//新增时默认为创建人所在机构/企业
if(UtilValidate.isEmpty(caseId)){
    context.caseManagers.put(context.userRoleType, ["", "", context.userGroupName]);
}
//查询用户所在组织的所有子账号，不包括合伙人，因为此处是选择项目经理，如果项目经理为合伙人子账号将造成业务混乱
List<GenericValue> subAccountInfos = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("groupId", partyId)).cache().queryList();
/*if(UtilValidate.isNotEmpty(partnerType)) {//合伙人只查询自己的子账号
    subAccountInfos = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("groupId", partyId)).cache().queryList();
}else{//非合伙人查询本身机构子账号及合伙人子账号
    subAccountInfos = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(UtilMisc.toMap("groupName", context.userGroupName)).cache().queryList();
}*/
context.persons = subAccountInfos;
context.blankCaseSessionKey = UUID.randomUUID().toString();


