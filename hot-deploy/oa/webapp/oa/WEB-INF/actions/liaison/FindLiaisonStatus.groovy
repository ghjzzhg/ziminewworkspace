import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

String enumTypeId = parameters.get("enumTypeId");

List<GenericValue> LiaisonStatusList = EntityQuery.use(delegator)
        .select()
        .from("Enumeration")
        .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId))
        .orderBy("sequenceId")
        .queryList();
for (GenericValue genericValue : LiaisonStatusList) {
    if (genericValue.get("enumId").equals("LIAISON_STATUS_FOUR")){
        LiaisonStatusList.remove(genericValue);//½«ÒÑ×÷·Ï×´Ì¬É¾³ý
    }
}
request.setAttribute("LiaisonStatusList",LiaisonStatusList);