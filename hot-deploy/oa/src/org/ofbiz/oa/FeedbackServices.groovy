import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.oa.FeedbackParametersUtil
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp

public Map<String,Object> saveFeedbackBasic(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    Timestamp feedbackTime = new Timestamp(System.currentTimeMillis());//反馈时间
    String feedbackContext = context.get("feedbackContext");//反馈内容
    String permission = context.get("permission");           //查看权限
    String actualFeedbackPerson = context.get("feedbackPerson");//实际反馈人（待反馈人）
    String feedbackPerson = userLogin.get("partyId");              //反馈人
    String feedbackMiddleId = context.get("feedbackMiddleId");//被反馈表id
    String processing = context.get("processing");             //进度
    String type = context.get("feedbackMiddleType");                          //类型
    String logicDelete = "N";                                   //逻辑删除（'N':存在，'Y':已删除）

    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.put("feedbackTime",feedbackTime);
    valueMap.put("feedbackPerson",feedbackPerson);
    valueMap.put("logicDelete",logicDelete);

    if(UtilValidate.isNotEmpty(feedbackContext)){
        valueMap.put("feedbackContext",feedbackContext);
    }
    if(UtilValidate.isNotEmpty(permission)){
        valueMap.put("permission",permission);
    }
    if(UtilValidate.isNotEmpty(actualFeedbackPerson)){
        valueMap.put("actualFeedbackPerson",actualFeedbackPerson);
    }
    if(UtilValidate.isNotEmpty(feedbackMiddleId)){
        valueMap.put("feedbackMiddleId",feedbackMiddleId);
    }
    if(UtilValidate.isNotEmpty(processing)){
        valueMap.put("processing",processing);
    }

    String feedbackId = delegator.getNextSeqId("TblFeedback");
    valueMap.put("feedbackId",feedbackId);
    //反馈
    GenericValue feedback = delegator.makeValidValue("TblFeedback", valueMap);
    feedback.create();
    //反馈中间表
    GenericValue feedbackMiddle = delegator.makeValidValue("TblFeedbackMiddle", UtilMisc.toMap("feedbackMiddleId", feedbackMiddleId,"feedbackId", feedbackId,"feedbackMiddleType",type));
    feedbackMiddle.create();
    successResult.put("msg","反馈成功！");
    return successResult;
}

public Map<String,Object> getFeedback() {
    Map<String,Object> successResult = FastMap.newInstance();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = userLogin.getString("partyId");
    String feedbackMiddleId = context.get("feedbackMiddleId");
    String childFeedbackId = context.get("childFeedbackId");
    String feedbackPerson = context.get("feedbackPerson");
    String type = context.get("feedbackMiddleType");
    /*List<GenericValue> executorList = EntityQuery.use(delegator)
            .select("partyId")
            .from("TblWorkPlanExecutor")
            .where(EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,feedbackMiddleId))
            .queryList();
    GenericValue workPlan = EntityQuery.use(delegator)
            .select("projectLeader","planPerson")
            .from("TblWorkPlan")
            .where(EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,feedbackMiddleId))
            .queryOne();
    if(UtilValidate.isNotEmpty(workPlan)){
        String projectLeader = workPlan.getString("projectLeader");
        String planPerson = workPlan.getString("planPerson");
    }

    String executorStrs = "";
    if(UtilValidate.isNotEmpty(executorList)){
        for(GenericValue value : executorList){
            executorStrs += "'"+ value.getString("partyId") +"'" + ",";
        }
        executorStrs = executorStrs.substring(0,executorStrs.length() - 1);
        executorStrs = "(" + executorStrs + ")";
    }*/





    Map<String,Object> valueMap = FastMap.newInstance();
    List<EntityCondition> conditionList = FastList.newInstance();

    if(UtilValidate.isNotEmpty(childFeedbackId)){
        valueMap.put("showChild","false");//反馈列表不显示对应的子计划
        conditionList.add(EntityCondition.makeCondition("childWorkPlanId",EntityOperator.EQUALS,childFeedbackId));
    }else {
        valueMap.put("showChild","true");//总反馈列表显示对应的子计划
        if (UtilValidate.isNotEmpty(feedbackPerson)) {
            conditionList.add(EntityCondition.makeCondition("feedbackPerson", EntityOperator.EQUALS, feedbackPerson));
        }
    }
    conditionList.add(EntityCondition.makeCondition("feedbackMiddleId", EntityOperator.EQUALS, feedbackMiddleId));

    conditionList.add(EntityCondition.makeCondition("feedbackMiddleType", EntityOperator.EQUALS, type));
    conditionList.add(EntityCondition.makeCondition("logicDelete", EntityOperator.EQUALS, "N"));

    //------------------------------------------
    EntityListIterator eli = null;
    try {
        // 分页参数
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 5;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 5;
        }

        String sortField = (String) context.get("sortField");
        List<String> orderBy = FastList.newInstance();
        if (UtilValidate.isNotEmpty(sortField)) {
            orderBy.add(sortField);
        }else {
            sortField = "feedbackTime";
            orderBy.add(sortField);
        }

        // 计算当前显示页的最小、最大索引(可能会超出实际条数)
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;
        //--------------------------------------------------

        eli = EntityQuery.use(delegator)
                .from("FeedbackMiddleFeedback")
                .where(EntityCondition.makeCondition(conditionList))
                .orderBy("-feedbackTime")
                .cursorScrollInsensitive()
                .fetchSize(highIndex)
                .queryIterator();
        // 获取结果片段
        List<GenericValue> members = eli.getPartialList(lowIndex, viewSize);
        valueMap.put("feedbackList",members);
        // 获取实际总条数
        int memberSize = eli.getResultsSizeAfterPartialList();
            if (highIndex > memberSize) {
            highIndex = memberSize;
        }
        successResult.put("returnValue", valueMap);
        successResult.put("viewIndex", viewIndex);
        successResult.put("viewSize", viewSize);
        successResult.put("highIndex", highIndex);
        successResult.put("lowIndex", lowIndex);
        successResult.put("sortField",sortField);
        successResult.put("totalCount", memberSize);
        successResult.put("feedbackMiddleId", feedbackMiddleId);
        successResult.put("childFeedbackId", childFeedbackId);
        successResult.put("feedbackPerson", feedbackPerson);
        successResult.put("feedbackMiddleType", type);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询工作计划错误");
    } finally {
        if (eli != null) {
            eli.close();
        }
    }
    return successResult;
}