import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery

GenericValue userLogin = context.get("userLogin");

DynamicViewEntity dynamicView = DynamicViewEntity.newInstance();
dynamicView.addMemberEntity("workPlan","TblWorkPlan");
dynamicView.addMemberEntity("personWork","TblPersonWork");
dynamicView.addAlias("workPlan","workPlanId");
dynamicView.addAlias("workPlan","title");
dynamicView.addAlias("personWork","personId");
dynamicView.addAlias("personWork","startTime");
dynamicView.addAlias("personWork","completeTime");
dynamicView.addAlias("personWork","jobDescription");
dynamicView.addAlias("personWork","personWorkStatus");

dynamicView.addViewLink("workPlan","personWork",true,UtilMisc.toList(ModelKeyMap.makeKeyMapList("workPlanId")));
List<GenericValue> workPlanList = EntityQuery.use(delegator)
        .from(dynamicView)
        .where(EntityCondition.makeCondition(
            EntityCondition.makeCondition("personId",EntityOperator.EQUALS,userLogin.getString("partyId")),
            EntityJoinOperator.AND,
            EntityCondition.makeCondition("completeTime",EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(UtilDateTime.nowDate().getTime()))
        )).queryList();
context.workPlanList = workPlanList;
