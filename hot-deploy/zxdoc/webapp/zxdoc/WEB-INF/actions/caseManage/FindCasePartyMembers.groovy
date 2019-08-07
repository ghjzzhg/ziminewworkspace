import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

String partyTypeId = delegator.findOne("Party", UtilMisc.toMap("partyId", context.get("userLogin").get("partyId")), false).get("partyTypeId");
String partyId = context.get("userLogin").get("partyId");
boolean isManager = false;
boolean isRole = false;
boolean isPerson = false;
if("PARTY_GROUP".equals(partyTypeId)){
    isManager = true;
    isRole = true;
}else if("PERSON".equals(partyTypeId)){
    GenericValue casePartyMember = delegator.findOne("TblCasePartyMember", UtilMisc.toMap("partyId", partyId, "caseId", parameters.caseId), false);
    if(casePartyMember.size() > 0){
        isManager = true
        if(!"CASE_PERSON_ROLE_MANAGER".equals(casePartyMember.get("roleTypeId"))){
            isPerson = true
        }
    }
}
List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT"), null, false);
partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
List<GenericValue> casePartyMemberList = delegator.findByAnd("casePartyMembers", UtilMisc.toMap("caseId", request.getParameter("caseId"),"enabled","Y"), UtilMisc.toList("groupName","personPartyId"), false);
List<Map> casePartyMembers = new ArrayList<>();
for(GenericValue casePartyMember: casePartyMemberList){
    Map map = new HashMap();
    map.putAll(casePartyMember);
    boolean isPersonManager = false;
    //参与方主账户或项目经理具有修改权限
    if(!partyId.equals(casePartyMember.get("groupPartyId")) || !isManager || UtilValidate.isEmpty(casePartyMember.get("fullName"))){
        map.put("editable", false);
    }else {
        GenericValue genericValue1 = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("partyId", map.get("partyId"),"caseId",map.get("caseId"),"roleTypeId", "CASE_PERSON_ROLE_MANAGER")).queryOne();
        if(UtilValidate.isNotEmpty(genericValue1)){
            isPersonManager = true;
        }
        map.put("isPerson",isPerson);
        map.put("isManager", isRole);
        map.put("isPersonManager", isPersonManager);
        map.put("editable", true);
    }
    casePartyMembers.add(map);
}
request.setAttribute("data", casePartyMembers);
