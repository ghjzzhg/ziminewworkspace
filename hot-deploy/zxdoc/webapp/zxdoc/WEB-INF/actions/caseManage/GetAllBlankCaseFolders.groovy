import com.fasterxml.jackson.databind.ObjectMapper
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String blankCaseSessionKey = parameters.blankCaseSessionKey;
Map<String, Object> blankCaseData = request.getSession().getAttribute(blankCaseSessionKey);

List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", userLogin.partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : userLogin.partyId;

GenericValue groupInfo = from("BasicGroupInfo").where("partyId", groupId).queryOne()
boolean isCompany = groupInfo != null && "CASE_ROLE_OWNER".equalsIgnoreCase(groupInfo.getString("roleTypeId"));
context.isCompany = isCompany;
Map<String, List<String>> existingFolders = new HashMap<>();

Map<String, String> providerRoleMap = new HashMap<>();

List<GenericValue> caseParties = blankCaseData.get("caseParties");
if(UtilValidate.isNotEmpty(caseParties)){
    for (GenericValue caseParty : caseParties) {
        providerRoleMap.put(caseParty.getString("partyId"), caseParty.getString("roleTypeId"));
    }
}

ObjectMapper objectMapper = new ObjectMapper();
context.existingFolders = objectMapper.writer().writeValueAsString(existingFolders);

List<GenericValue> caseRoles = caseParties;
List<Map> roleType = new ArrayList<>();
if(caseRoles!=null)
{
    for (GenericValue caseRole:caseRoles)
    {
        Map roleMap = new HashMap();
        String type = caseRole.get("roleTypeId");
        //changed by galaxypan@2017-08-03:合伙人不再单独一个CASE ROLE
        /*if(type.equals("CASE_ROLE_PARTNER"))
        {
            String partyId = caseRole.get("partyId");
            type = EntityQuery.use(delegator).from("PartyRole").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"),EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).queryOne().get("roleTypeId");
            if(type.equals("CASE_ROLE_PARTNER")){
                type = EntityQuery.use(delegator).from("TblPartnerType").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).queryOne().get("roleTypeId");
            }
        }*/
        String description = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",type).queryOne().get("description");
        roleMap.put("roleTypeId",type);
        roleMap.put("description",description);
        roleType.add(roleMap);
    }
    context.roleTypeList = roleType;
}

//移除企业
for(int i = 0; i < context.roleTypeList.size(); i ++){
    if("CASE_ROLE_OWNER".equalsIgnoreCase(context.roleTypeList[i].get("roleTypeId"))){
        context.roleTypeList.remove(i);
        break;
    }
}
