import javolution.util.FastList
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

String partyId = context.get("userLogin").get("partyId");
List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
String contractParty = parameters.get("contractParty");
String startDate = parameters.get("startDate");
String dateClose = parameters.get("dateClose");
List<EntityCondition> condList = FastList.newInstance();
condList.add(EntityCondition.makeCondition("createBy", EntityOperator.EQUALS, partyId));
if (UtilValidate.isNotEmpty(contractParty)) {
//       condList.add(EntityCondition.makeCondition("firstPartyName",EntityOperator.EQUALS,firstPartyName));
    condList.add(EntityCondition.makeCondition("secondPartyName", EntityOperator.LIKE, "%"+contractParty+"%"));
}
if (UtilValidate.isNotEmpty(startDate)) {
    condList.add(EntityCondition.makeCondition("startDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(startDate,"yyyy-MM-dd")));
}
if (UtilValidate.isNotEmpty(dateClose)) {
    condList.add(EntityCondition.makeCondition("dateClose", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(dateClose,"yyyy-MM-dd")));
}

UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PartyGroupContract").where( EntityCondition.makeCondition(condList)), parameters);

List<Map<String, Object>> resultList = new ArrayList<>();
for(GenericValue contract: result.data){
    Map<String, Object> map = new HashMap<>();
    map.putAll(contract);
    if(partyId.equals(contract.getString("createBy"))){//编辑权限
        map.put("editable", true);
    } else {
        map.put("editable", false);
    }
    resultList.add(map);
}
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", resultList);
