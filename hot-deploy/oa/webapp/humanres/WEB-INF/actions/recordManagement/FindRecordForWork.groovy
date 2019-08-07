import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
 workList = null;
if (partyId!=null){
    workList =  EntityQuery.use(delegator).from("TblWorkExperience").where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId))).queryIterator();
}
context.workList = workList;
context.partyId = partyId;
