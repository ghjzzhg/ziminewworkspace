import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

String contractId = parameters.contractId;
List<GenericValue> relatedCaseList = new ArrayList<>();
String partyId = context.get("userLogin").get("partyId");
List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
List<EntityCondition> conditionList = new ArrayList<>();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
//实现合同和case的多对多，而不是之前的一对多
/*conditionList.add(EntityCondition.makeCondition("contract", EntityOperator.EQUALS, null));*/
conditionList.add(EntityCondition.makeCondition("deleted", EntityOperator.EQUALS, null));
conditionList.add(EntityCondition.makeCondition("archiveDate", EntityOperator.EQUALS, null));
relatedCaseList = EntityQuery.use(delegator).from("ContractCase").where(conditionList).queryList();
if (contractId != null) {
    conditionList = [];
    conditionList.add(EntityCondition.makeCondition("contractId", EntityOperator.EQUALS, contractId));
    List<GenericValue> hasRelatedCaseList = delegator.findList("TblCaseRelatedContract", EntityCondition.makeCondition(conditionList), null, null, null, false);
    List relatedContractList = new ArrayList();
    List relatedContractIdList = new ArrayList();
    for (GenericValue relatedContract : hasRelatedCaseList) {
        String caseId = relatedContract.get("caseId");
        caseId = caseId.substring(0, 1) == " " ? caseId.substring(1, caseId.length()) : caseId;
        GenericValue caseRelated = EntityQuery.use(delegator).use(delegator).from("TblCase").where("caseId", caseId).queryOne();
        if (caseRelated != null) {
            relatedContractList.add(caseRelated);
            String caseRealtedId = caseRelated.get("caseId");
            relatedContractIdList.add(caseRealtedId);
        }
    }
    List caseList = new ArrayList();
    for (GenericValue realtedCase : relatedCaseList) {
        String relatedId = realtedCase.get("caseId");
        if (!relatedContractIdList.contains(relatedId)) {
            caseList.add(realtedCase);
        }
    }
    context.relatedCaseList = caseList;
    context.relatedContractList = relatedContractList;
} else {
    //新建合同，甲方信息
    String firstPartyId = userLogin.partyId;
    String partyType = delegator.findByAnd("Party", UtilMisc.toMap("partyId", firstPartyId), null, false).get(0).get("partyTypeId");
    if (partyType == "PERSON") {
        firstPartyId = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false).get(0).get("partyIdFrom");
    }

    GenericValue firstPartyGroup = delegator.findByAnd("PartyGroup", UtilMisc.toMap("partyId", firstPartyId), null, false).get(0)
    firstPartyName = firstPartyGroup.get("groupName");
    String partnerName = firstPartyGroup.getString("partnerGroupName")
    if(UtilValidate.isNotEmpty(partnerName)){
        firstPartyName = partnerName;
    }
    context.data = ["firstPartyId": firstPartyId, "firstPartyName": firstPartyName];
    context.relatedCaseList = relatedCaseList;
    context.relatedContractList = [];
}