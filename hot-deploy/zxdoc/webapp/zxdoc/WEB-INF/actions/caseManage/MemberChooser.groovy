import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery

DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
dynamicViewEntity.addMemberEntity("P", "Person");
dynamicViewEntity.addMemberEntity("PR", "PartyRelationship");
dynamicViewEntity.addMemberEntity("ULN","UserLogin");
dynamicViewEntity.addAlias("P", "partyId");
dynamicViewEntity.addAlias("P", "fullName");
dynamicViewEntity.addAlias("PR", "partyIdFrom");
dynamicViewEntity.addAlias("PR", "partyRelationshipTypeId");
dynamicViewEntity.addAlias("ULN","enabled");
dynamicViewEntity.addViewLink("PR", "P", true, UtilMisc.toList(new ModelKeyMap("partyIdTo", "partyId")));
dynamicViewEntity.addViewLink("P", "ULN", true, UtilMisc.toList(new ModelKeyMap("partyId", "partyId")));
//参与方所有子账户
List<EntityCondition> conditionList = UtilMisc.toList(
        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ORG_SUB_ACCOUNT"),
        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId),
        EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
EntityListIterator entityListIterator = delegator.findListIteratorByCondition(dynamicViewEntity, EntityCondition.makeCondition(conditionList), null, null, null, null);
Map members = new HashMap();
while(entityListIterator.hasNext()){
    GenericValue genericValue = entityListIterator.next();
    members.put(genericValue.get("partyId"), genericValue.get("fullName"));
}
//参与方已参与此CASE子账户
List<GenericValue> membersIn = delegator.findList("TblCasePartyMember", EntityCondition.makeCondition(UtilMisc.toList(
        EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, parameters.caseId),
        EntityCondition.makeCondition("groupPartyId", EntityOperator.EQUALS, parameters.partyId))),
        UtilMisc.toSet("partyId", "roleTypeId"), null, null, false);

String managerPartyId = "";
for (GenericValue member : membersIn) {
    if("CASE_PERSON_ROLE_MANAGER".equals(member.getString("roleTypeId"))){
        managerPartyId = member.getString("partyId");
        break;
    }
}

//如果是项目经理修改成员，获取对应的主账户
if(!userLogin.partyId.equals(parameters.partyId)){//项目经理管理成员
    //成员参与CASE的角色
    List<Map<String, String>> casePersonRoles = new ArrayList<>();
    List<String> roleNames = new ArrayList();
    context.roleType = casePersonRoles;
    List<GenericValue> standardRoles= delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", "CASE_PERSON_ROLE"), null, false);
    for (GenericValue role: standardRoles) {
        if("CASE_PERSON_ROLE_MANAGER".equals(role.getString("roleTypeId"))){
            continue;//项目经理只能管理组员
        }
        casePersonRoles.add(UtilMisc.toMap("roleTypeId", role.getString("roleTypeId"), "description", role.getString("description")));
        roleNames.add(role.getString("description"));
    }

//查询当前参与方是否有特殊设定的CASE成员角色
    GenericValue groupType = EntityQuery.use(delegator).from("TblCaseParty").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, parameters.partyId),EntityCondition.makeCondition("caseId",EntityOperator.EQUALS, parameters.caseId)).queryOne();
    String groupTypeId = groupType.get("roleTypeId");
    List list = new ArrayList();
    List<GenericValue> memberType = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId",groupTypeId).queryList();
    if(UtilValidate.isNotEmpty(memberType))
    {
        for(GenericValue member:memberType)
        {
            String roleDesc = member.get("description")
            if(!roleNames.contains(roleDesc)){
                list.add(UtilMisc.toMap("roleTypeId", member.get("enumCode"), "description", roleDesc));
            }
        }
    }
    //非合伙人机构项目经理管理成员时，包含下属合伙人
    GenericValue casePartyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", parameters.partyId), true);
    if(UtilValidate.isEmpty(casePartyGroup.getString("partnerType"))){
        List<GenericValue> partnerAccounts = EntityQuery.use(delegator).from("SubAccountSimpleInfo").where(EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, casePartyGroup.getString("groupName")), EntityCondition.makeCondition("partnerType", EntityOperator.NOT_EQUAL, null)).queryList();
        if(UtilValidate.isNotEmpty(partnerAccounts)){
            for (GenericValue account : partnerAccounts) {
                members.put(account.get("partyId"), account.get("fullName"));
            }
        }
    }

}else{//主账户管理成员
    context.roleType = UtilMisc.toList(UtilMisc.toMap("roleTypeId", "CASE_PERSON_ROLE_MANAGER", "description", "项目经理"));
}
if(!parameters.memberId){
    for(GenericValue memberIn: membersIn){
        members.remove(memberIn.get("partyId"));
    }
}else{
    context.roleType = UtilMisc.toList(UtilMisc.toMap("roleTypeId", "CASE_PERSON_ROLE_MANAGER", "description", "项目经理"));
    members.remove(managerPartyId);
}

context.members = members;
context.partyId = parameters.partyId;
context.caseId = parameters.caseId;
context.memberId = parameters.memberId;