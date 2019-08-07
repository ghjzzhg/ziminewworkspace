import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
 familyList = null;
if (partyId!=null){
    familyList =  EntityQuery.use(delegator).from("TblFamilyInformation").where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId))).queryIterator();
}
context.familyList = familyList;
context.partyId = partyId;
