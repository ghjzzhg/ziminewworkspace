import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.util.EntityQuery

type = parameters.get("type");
perfExamItems = [];
List<EntityCondition> conditionList = FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("state",EntityOperator.EQUALS,"1"));
if(UtilValidate.isNotEmpty(type)){
    conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("parentType", EntityOperator.EQUALS, type),
            EntityCondition.makeCondition("typeId", EntityOperator.EQUALS, type)), EntityOperator.OR));
}
if(type && !"1".equals(type)){
    perfExamItems = EntityQuery.use(delegator).select().from("TblPerfExamItem").where (conditionList).orderBy("parentType", "typeId").queryList();
}else{
    perfExamItems = EntityQuery.use(delegator).select().from("TblPerfExamItem").where(UtilMisc.toMap("state", "1")).orderBy("parentType", "typeId").queryList();
}
context.perfExamItemsList = perfExamItems;

DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
dynamicViewEntity.addMemberEntity("T", "TblPerfExamItem");
dynamicViewEntity.addAlias("T", "count", "itemId", null, null, false, "count");
dynamicViewEntity.addAlias("T", "typeId", "typeId", null, null, true, null);
typeCounts = from(dynamicViewEntity).queryList();
typeCountMap = [:];
if(typeCounts){
    for (GenericValue count : typeCounts) {
        typeCountMap.put(count.get("typeId"), count.get("count"));
    }
}
context.typeCountMap = typeCountMap;
