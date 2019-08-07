import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

partyId = parameters.userLogin.partyId;
GenericValue party = from("Party").where("partyId", partyId).queryOne();
String partyType = party.getString("partyTypeId");
context.qualificationDesc = "未知类型";
context.qualificationStatus="请先实名认证";
if("PERSON".equals(partyType)){//子账户认证
    String partyIdFrom =delegator.findByAnd("PartyRelationship",UtilMisc.toMap("partyIdTo",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT")).get(0).get("partyIdFrom");
    GenericValue partyGroup = from("PartyGroup").where("partyId",partyIdFrom).queryOne();
    String groupName= partyGroup.getString("groupName");
    context.qualificationType = "PROVIDER_PERSON";
    context.personInfo=from("Person").where("partyId",partyId).queryFirst();
    context.qualificationDesc=groupName+"子帐户";
}else{//机构认证
    List<GenericValue> roles = from("PartyRole").where("partyId", partyId).queryList();
    List<String> providerRoles = ["CASE_ROLE_ACCOUNTING", "CASE_ROLE_INVESTOR", "CASE_ROLE_STOCK", "CASE_ROLE_LAW"];
    for (GenericValue role : roles) {
        String roleType = role.getString("roleTypeId")
        if("CASE_ROLE_OWNER".equals(roleType)){
            context.qualificationType = "COMPANY";
            context.qualificationDesc = "CASE_ROLE_OWNER";
            break;
        }else if(providerRoles.contains(roleType)){
            context.qualificationType = "PROVIDER";
            context.qualificationDesc = roleType;
            break;
        }
    }

    context.address = from("PartyAndContactMech").where("contactMechTypeId", "POSTAL_ADDRESS", "partyId", partyId).queryFirst();

    if(UtilValidate.isNotEmpty(context.qualificationType)){
        context.qualificationDesc = from("RoleType").where( "roleTypeId", context.qualificationDesc).queryOne().getString("description");
    }

}