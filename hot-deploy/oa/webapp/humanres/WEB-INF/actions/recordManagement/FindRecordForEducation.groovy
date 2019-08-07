import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
 educationList = null;
if (partyId!=null){
    educationList =  EntityQuery.use(delegator).from("TblEducational").where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId))).queryIterator();
}
context.educationList = educationList;
context.partyId = partyId;
