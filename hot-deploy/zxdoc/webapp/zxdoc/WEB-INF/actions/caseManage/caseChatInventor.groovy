import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

List<GenericValue> caseMemberManagerList = delegator.findByAnd("casePartyMembers", UtilMisc.toMap("caseId", parameters.caseId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"), null, false);
context.caseMemberManagers = caseMemberManagerList;
context.caseId = parameters.caseId;
String partyId = context.get("userLogin").get("partyId");
GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT").queryOne();
partyId = partyRelationship.get("partyIdFrom");
context.roleId = partyId;
