package org.ofbiz.oa

import com.sun.xml.internal.bind.v2.TODO
import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityExpr
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ServiceUtil
import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> saveWorkPlan() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    List<String> noticePersonList = context.get("noticePerson");
    List<String> canSeePersonList = context.get("canSeePerson");
    String workPlanId = context.get("workPlanId");
    if (UtilValidate.isNotEmpty(canSeePersonList)) {
        String canSeePerson = "";
        for (String str : canSeePersonList) {
            canSeePerson += str + ",";
        }
        context.put("canSeePerson", canSeePerson.trim().substring(0, canSeePerson.trim().length() - 1));
    }
    context.remove("noticePerson");
    if (UtilValidate.isNotEmpty(noticePersonList)) {
        String noticePerson = "";
        for (String str : noticePersonList) {
            noticePerson += str + ","
        }
        context.put("noticePerson", noticePerson.trim().substring(0, noticePerson.trim().length() - 1));
    }
    String msg = "保存成功";
    if (UtilValidate.isNotEmpty(workPlanId)) {
        //TODO:更新工作计划
        GenericValue workPlan = delegator.makeValidValue("TblWorkPlan", context);
        workPlan.store();
    } else {
        //TODO：保存工作计划
        workPlanId = delegator.getNextSeqId("TblWorkPlan").toString();
        context.put("workPlanId", workPlanId);
        context.put("workPlanStatus",new Double(0));//初始进度（待执行）
        context.put("personId",userLogin.getString("partyId"));
        GenericValue workPlan = delegator.makeValidValue("TblWorkPlan", context);
        workPlan.create();
    }
    successResult.put("data", msg);
    successResult.put("workPlanId",workPlanId);
    return successResult;
}

public Map<String,Object> deleteWorkPlan(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String workPlanId = context.get("workPlanId");
    delegator.removeByCondition("TblChildWorkPlan",EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId));
    delegator.removeByCondition("TblPersonWork",EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId));
    delegator.removeByCondition("TblWorkPlan",EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId));
    successResult.put("msg","删除成功！");
    return successResult;
}

public Map<String, Object> searchWorkPlan() {
    TimeZone timeZone = (TimeZone)context.get("timeZone");
    Locale locale = (Locale)context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String selfWorkPlan = context.get("selfWorkPlan");
    GenericValue departmentGenericValue = EntityQuery.use(delegator).select("department")
            .from("TblStaff")
            .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId))
            .queryOne();
    String userLoginDept = " ";
    if(UtilValidate.isNotEmpty(departmentGenericValue)){
        userLoginDept = departmentGenericValue.get("department");
    }

    String title = context.get("title");
    String member = context.get("member");
    String department = context.get("departmentId");
    String paramStartTime = context.get("startTime");
    List criteriaDate = new LinkedList();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String searchStartDate = null;
    java.sql.Date startDate = null;
    java.sql.Date endDate = null;
    if(UtilValidate.isNotEmpty(paramStartTime)){
        startDate = new java.sql.Date(format.parse(paramStartTime).getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(paramStartTime));
        calendar.add(Calendar.MONTH, 1);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (-index));
        endDate = new java.sql.Date(calendar.getTime().getTime());
    }else {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        startDate = new java.sql.Date((c.getTime()).getTime());
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = new java.sql.Date((c.getTime()).getTime());
    }
    criteriaDate.add(startDate);
    criteriaDate.add(endDate);
    String planPerson = context.get("planPerson");
    String planDescription = context.get("planDescription");
    String planType = context.get("planType");
    String workPlanStatus = context.get("workPlanStatus");


    //查看权限
    List<EntityExpr> conditions = FastList.newInstance();
    EntityCondition conditionLimit = null;
            //查询条件
    List<EntityExpr> conditions4 = FastList.newInstance();

    if(UtilValidate.isEmpty(selfWorkPlan)){
        EntityCondition condition1 = EntityCondition.makeCondition("canSeePerson", EntityOperator.LIKE, "%WP_CS_ALL%");

        EntityCondition condition2 = EntityCondition.makeCondition(
                UtilMisc.toList(
                        EntityCondition.makeCondition("canSeePerson", EntityOperator.LIKE, "%WP_CS_DEPARTMENT%"),
                        EntityCondition.makeCondition("departmentId", EntityOperator.EQUALS, userLoginDept),
                )
        );
        EntityCondition condition3 = EntityCondition.makeCondition(
                EntityCondition.makeCondition(
                        UtilMisc.toList(
                                EntityCondition.makeCondition("canSeePerson", EntityOperator.LIKE, "%WP_CS_ONLY_PLAN%"),
                                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
                        )
                ),EntityJoinOperator.OR
                ,EntityCondition.makeCondition("personId", EntityOperator.EQUALS, partyId)
        );

        conditions.add(condition1);
        conditions.add(condition2);
        conditions.add(condition3);
        conditionLimit = EntityCondition.makeCondition(conditions, EntityOperator.OR);


        if (UtilValidate.isNotEmpty(member)) {
            conditions4.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, member));
        }
    }else {
        conditions4.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, selfWorkPlan));
    }


    if (UtilValidate.isNotEmpty(title)) {
        conditions4.add(EntityCondition.makeCondition("title", EntityOperator.LIKE, "%" + title + "%"));
    }
    if (UtilValidate.isNotEmpty(member)) {
        conditions4.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, member));
    }
    if (UtilValidate.isNotEmpty(department)) {
        conditions4.add(EntityCondition.makeCondition("departmentId", EntityOperator.EQUALS, department));
    }
    if (criteriaDate.size() > 0) {
        conditions4.add(EntityCondition.makeCondition(
                EntityCondition.makeCondition("startTime", EntityOperator.BETWEEN, criteriaDate),
                EntityOperator.OR,
                EntityCondition.makeCondition("completeTime", EntityOperator.BETWEEN, criteriaDate)
        ));
    }
    if (UtilValidate.isNotEmpty(planPerson)) {
        conditions4.add(EntityCondition.makeCondition("planPerson", EntityOperator.EQUALS, planPerson));
    }
    if (UtilValidate.isNotEmpty(planDescription)) {
        conditions4.add(EntityCondition.makeCondition("planDescription", EntityOperator.LIKE, "%" + planDescription + "%"));
    }
    if (UtilValidate.isNotEmpty(planType)) {
        conditions4.add(EntityCondition.makeCondition("planType", EntityOperator.EQUALS, planType));
    }
    if (UtilValidate.isNotEmpty(workPlanStatus)) {
        Double statusValue;
        if ("WP_STATUS_BETO".equals(workPlanStatus)){//待执行
            statusValue = 0;
        }else if("WP_STATUS_COMPLETE".equals(workPlanStatus)){//已完成
            statusValue = 100;
        }else if("WP_STATUS_DISCARDED".equals(workPlanStatus)){//作废
            statusValue = -1;
        }
        EntityCondition statusCondition = EntityCondition.makeCondition("workPlanStatus", EntityOperator.EQUALS, statusValue);
        if("WP_STATUS_DOING".equals(workPlanStatus)){//执行中
            statusCondition = EntityCondition.makeCondition("workPlanStatus", EntityOperator.GREATER_THAN, statusValue);
        }
        conditions4.add(statusCondition);
    }else {
        conditions4.add(EntityCondition.makeCondition(
                EntityCondition.makeCondition("workPlanStatus",EntityOperator.GREATER_THAN_EQUAL_TO,new Double(0)),
                EntityJoinOperator.AND,
                EntityCondition.makeCondition("workPlanStatus",EntityOperator.LESS_THAN,new Double(100))
        ));
    }
    EntityCondition conditionSearch = null;
    EntityCondition condition = null;
        conditionSearch = EntityCondition.makeCondition(conditions4, EntityOperator.AND);
        if(UtilValidate.isNotEmpty(conditionLimit)){
            condition = EntityCondition.makeCondition(EntityOperator.AND, conditionLimit,conditionSearch);
        }else {
            condition = conditionSearch;
        }
    EntityListIterator eli = null;
    List<GenericValue> returnValue = null;

    try {
        // 分页参数
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 2;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 2;
        }

        String sortField = (String) context.get("sortField");
        List<String> orderBy = FastList.newInstance();
        if (UtilValidate.isNotEmpty(sortField)) {
            orderBy.add(sortField);
        }else {
            orderBy.add("-startTime");
        }
        // 计算当前显示页的最小、最大索引(可能会超出实际条数)
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;

        //------------------------"wpsDescription"--------------------------
        //查总计划
        eli = EntityQuery.use(delegator)
                .select("workPlanId", "departmentId", "personId","title", "startTime", "completeTime", "noticePerson", "planDescription", "planType"
                , "isPerformance", "importanceDegree", "difficultyDegree", "planPerson", "projectLeader", "canSeePerson", "workPlanStatus",
                "ptDescription", "idDescription", "ddDescription", "isDescription","planPersonName","groupName","feedbackPersonName","feedbackTime")
                .from("WorkPlanJoinExecutor")
                .where(condition)
                .orderBy("-startTime")
                .cursorScrollInsensitive()
                .fetchSize(highIndex)
                .distinct()
                .queryIterator();
        // 获取结果片段
        List<GenericValue> members = eli.getPartialList(lowIndex, viewSize);
        eli.close();

        returnValue = FastList.newInstance(); //输出数据
        //执行人及其子计划
        for (GenericValue genericValue : members) {
            Map<String, Object> valueMap = FastMap.newInstance();//存储总计划及执行人以及子计划
            //总计划
            valueMap.putAll(genericValue);
            if(genericValue.get("departmentId").equals(genericValue.get("planPerson"))){
                valueMap.put("planPersonName",genericValue.get("groupName"));
            }

            /*List<GenericValue> executorList1 = genericValue.getRelated("PersonWorkInfo");*/
            List<GenericValue> executorList = delegator.findByAnd("ExecutorPersonWork",UtilMisc.toMap("workPlanId",genericValue.getString("workPlanId")),null,false);
            BigDecimal personWorkStatus = new BigDecimal(0.0);
            List<Map> executorAndChildPlanList = FastList.newInstance();
            for (GenericValue executor : executorList) {
                Map<String, Object> executorAndChildPlan = FastMap.newInstance();
                List<GenericValue> childWorkPlanList = EntityQuery.use(delegator).from("ChildWorkPlanInfo")
                        .where(EntityCondition.makeCondition(UtilMisc.toMap("workPlanId", genericValue.get("workPlanId"), "operatorId", executor.get("partyId"))))
                        .queryList();
                executorAndChildPlan.putAll(executor);
                executorAndChildPlan.put("childWorkPlanList", childWorkPlanList);
                if(genericValue.getString("projectLeader").equals(executorAndChildPlan.get("partyId"))){
                    executorAndChildPlanList.addFirst(executorAndChildPlan);
                }else{
                    executorAndChildPlanList.add(executorAndChildPlan);
                }
                personWorkStatus = personWorkStatus.add(new BigDecimal(executor.get("personWorkStatus")));
            }
            if(UtilValidate.isNotEmpty(executorList)){
                BigDecimal divisor = new BigDecimal(executorList.size());
                BigDecimal totalPersonWorkStatus = personWorkStatus.divide(divisor,2);
                valueMap.put("totalPersonWorkStatus",totalPersonWorkStatus.doubleValue());

            }
            //执行人及其子计划信息
            valueMap.put("executorAndChildPlanList", executorAndChildPlanList);

            //里程碑
            List<GenericValue> milestoneList = EntityQuery.use(delegator).from("TblMilestone")
                    .where(EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,genericValue.getString("workPlanId")))
                    .orderBy("milestoneTime")
                    .queryList();
            valueMap.put("milestoneList",milestoneList);

            String[] dateStart = genericValue.get("startTime").toString().trim().split(" ")[0].split("-");
            Map<String, String> startTimeMap = FastMap.newInstance();
            startTimeMap.put("year", dateStart[0]);
            startTimeMap.put("month", dateStart[1]);
            if ((dateStart[2].charAt(0).toString().equals("0"))) {
                startTimeMap.put("day", Integer.parseInt(dateStart[2].charAt(1).toString()));
            } else {
                startTimeMap.put("day", Integer.parseInt(dateStart[2]));
            }

            //put分割后的startTime开始时间
            valueMap.put("startTimeSlip", startTimeMap);

            String[] dateEnd = genericValue.get("completeTime").toString().trim().split(" ")[0].split("-");
            Map<String, String> completeTimeMap = FastMap.newInstance();
            completeTimeMap.put("year", dateEnd[0]);
            completeTimeMap.put("month", dateEnd[1]);
            if ((dateEnd[2].charAt(0).toString().equals("0"))) {
                completeTimeMap.put("day", Integer.parseInt(dateEnd[2].charAt(1).toString()));
            } else {
                completeTimeMap.put("day", Integer.parseInt(dateEnd[2]));
            }

            //put分割后的completeTime完成时间
            valueMap.put("completeTimeSlip", completeTimeMap);
            //put查询开始时间
            valueMap.put("searchStartDate",searchStartDate);

            returnValue.add(valueMap);//存入输出List

        }


        //----------------------------------------------

        // 获取实际总条数
        int memberSize = eli.getResultsSizeAfterPartialList();
        if (highIndex > memberSize) {
            highIndex = memberSize;
        }
        successResult.put("workPlanList", returnValue);
        successResult.put("viewIndex", viewIndex);
        successResult.put("viewSize", viewSize);
        successResult.put("highIndex", highIndex);
        successResult.put("lowIndex", lowIndex);
        successResult.put("sortField", sortField);
        successResult.put("totalCount", memberSize);
        successResult.put("title", title);
        successResult.put("member", member);
        successResult.put("departmentId", department);
        successResult.put("planPerson", planPerson);
        successResult.put("planDescription", planDescription);
        successResult.put("planType", planType);
        successResult.put("workPlanStatus", workPlanStatus);

        successResult.put("startTime",UtilDateTime.toDateString(new Date(startDate.getTime()),UtilDateTime.DATE_FORMAT));

        successResult.put("endTime",UtilDateTime.toDateString(new Date(endDate.getTime()),UtilDateTime.DATE_FORMAT));
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

public Map<String, Object> saveChildPlan() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    String childWorkPlanId = context.get("childWorkPlanId");
    String operatorId = context.get("operatorId");
    String workPlanId = context.get("workPlanId");
    String curPartyId = context.get("curPartyId");
    String personId = context.get("personId");
    context.put("planPerson",userLogin.get("partyId"));
    String msg = "";
    if (UtilValidate.isNotEmpty(childWorkPlanId)) {
        //TODO 跟新子计划
        GenericValue childWorkPlan = delegator.makeValidValue("TblChildWorkPlan",context);
        childWorkPlan.store();
        successResult.put("data",childWorkPlan);
    } else {
        //保存子计划
        childWorkPlanId = delegator.getNextSeqId("TblChildWorkPlan");
        context.put("childWorkPlanId", childWorkPlanId);
        GenericValue childWorkPlan = delegator.makeValidValue("TblChildWorkPlan", context);
        childWorkPlan.create();
        msg = "保存成功！";
        Map<String,Object> condition = FastMap.newInstance();
        condition.put("workPlanId",workPlanId);
        if(UtilValidate.isNotEmpty(curPartyId)){
            condition.put("operatorId",curPartyId);
        }
        List<GenericValue> childWorkPlanList = delegator.findByAnd("TblChildWorkPlan",condition,null,false);
        GenericValue currentExecutor = delegator.findOne("Person",UtilMisc.toMap("partyId",operatorId),false);
        successResult.put("data",UtilMisc.toMap("childWorkPlanList",childWorkPlanList,"currentExecutor",currentExecutor,"curPartyId",curPartyId,"personId",personId));
    }
    return successResult;
}

public Map<String,Object> deleteChildPlan(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String childWorkPlanId = context.get("childWorkPlanId");
    String workPlanId = context.get("workPlanId");
    String curPartyId = context.get("curPartyId");
    String personId = context.get("personId");
    GenericValue genericValue = delegator.makeValidValue("TblChildWorkPlan","childWorkPlanId",childWorkPlanId);
    genericValue.remove();
    List<GenericValue> childWorkPlanList = delegator.findByAnd("TblChildWorkPlan",UtilMisc.toMap("workPlanId",workPlanId),null,false);
    successResult.put("data",UtilMisc.toMap("childWorkPlanList",childWorkPlanList,"curPartyId",curPartyId,"personId",personId));
    return successResult;
}


public Map<String, Object> feedbackWorkPlan() {
    String workPlanId = context.get("workPlanId");//总计划id
    String partyId = context.get("partyId");//执行人id
    Map<String, Object> successResult = findOneById(context);
    Map<String, Object> valueMap = successResult.get("workPlan");
    String childWorkPlanId = context.get("childWorkPlanId");
    //子任务
    GenericValue childWorkPlan = null;
    if(UtilValidate.isNotEmpty(childWorkPlanId)){//null查询总任务反馈，非null查询子任务反馈
        childWorkPlan = EntityQuery.use(delegator)
                .from("ChildWorkPlanInfo")
                .where(EntityCondition.makeCondition("childWorkPlanId",EntityOperator.EQUALS,childWorkPlanId))
                .queryOne();
    }
    if(UtilValidate.isNotEmpty(childWorkPlan)){
        valueMap.put("childWorkPlan",childWorkPlan);
    }

    //任务
    GenericValue executorJobs = EntityQuery.use(delegator)
            .select("jobDescription","startTime","completeTime","personWorkStatus")
            .from("ExecutorPersonWork")
            .where(EntityCondition.makeCondition(UtilMisc.toMap("workPlanId", workPlanId, "partyId", partyId)))
            .queryOne();

    //子任务集合
    List<GenericValue> childWorkPlanOfExecutorList = EntityQuery.use(delegator)
            .select("workPlanId","childWorkPlanId","title","startTime","completeTime","planPerson","lastFeedback","childWorkPlanStatus")
            .from("TblChildWorkPlan")
            .where(EntityCondition.makeCondition(UtilMisc.toMap("workPlanId", workPlanId, "operatorId", partyId)))
            .queryList();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    String nowDate = format.format(date);
    valueMap.put("nowDate", nowDate);
    valueMap.put("executorJobs",executorJobs);
    valueMap.put("childWorkPlanOfExecutorList",childWorkPlanOfExecutorList);
    successResult.put("returnValue", valueMap);
    successResult.remove("workPlan");
    return successResult;
}

/*查询所有反馈*/
public Map<String, Object> findFeedback() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String workPlanId = context.get("workPlanId");
    Map<String, Object> valueMap = FastMap.newInstance();
    Map<String,Object> returnValue = getFeedback(context);
    if(UtilValidate.isNotEmpty(returnValue)){
        successResult.putAll(returnValue);
    }else{
        successResult.put("returnValue",FastMap.newInstance());
    }
    return successResult;
}

public Map<String,Object> getFeedback(Map context) {
    Map<String,Object> successResult = FastMap.newInstance();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = userLogin.getString("partyId");
    String workPlanId = context.get("workPlanId");
    String childWorkPlanId = context.get("childWorkPlanId");
    String partyId = context.get("partyId");


    /*执行人List*/
    List<GenericValue> executorList = EntityQuery.use(delegator)
            .select("personId")
            .from("TblPersonWork")
            .where(EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId))
            .queryList();
    /*项目主管和安排人*/
    GenericValue workPlan = EntityQuery.use(delegator)
            .select("projectLeader","planPerson")
            .from("TblWorkPlan")
            .where(EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId))
            .queryOne();

    String projectLeade = "";
    String planPerson = "";
    if(UtilValidate.isNotEmpty(workPlan)){
        projectLeader = workPlan.getString("projectLeader");
        planPerson = workPlan.getString("planPerson");
    }

    String executorStrs = "";
    if(UtilValidate.isNotEmpty(executorList)){
        for(GenericValue value : executorList){
            executorStrs += "'"+ value.getString("personId") +"'" + ",";
        }
        executorStrs = executorStrs.substring(0,executorStrs.length() - 1);
        executorStrs = "(" + executorStrs + ")";
    }

    Map<String,Object> valueMap = FastMap.newInstance();
    List<EntityCondition> conditionList = FastList.newInstance();
    conditionList.add(
            EntityCondition.makeCondition(
                    EntityJoinOperator.OR,
                    EntityCondition.makeCondition(
                            EntityOperator.OR,
                            EntityCondition.makeCondition("actualFeedbackPerson",EntityOperator.EQUALS,userLoginId),
                            EntityCondition.makeCondition("feedbackPerson",EntityOperator.EQUALS,userLoginId)
                    ),
                    EntityCondition.makeCondition("permission",EntityOperator.EQUALS,FeedbackParametersUtil.ALL_PERSONAL),
                    EntityCondition.makeCondition(
                            EntityCondition.makeCondition("permission",EntityOperator.EQUALS,FeedbackParametersUtil.EXECUTOR),
                            EntityJoinOperator.AND,
                            EntityCondition.makeConditionWhere("'" + userLoginId + "'" + " in" + executorStrs)
                    ),
                    EntityCondition.makeCondition(
                            EntityJoinOperator.AND,
                            EntityCondition.makeCondition("permission",EntityOperator.EQUALS,FeedbackParametersUtil.PERSON_LEADER),
                            EntityCondition.makeCondition(
                                    EntityJoinOperator.OR,
                                    EntityCondition.makeConditionWhere("'" + userLoginId + "'" + "=" + "'" + planPerson + "'"),
                                    EntityCondition.makeConditionWhere("'" + userLoginId + "'" + "=" + "'" + projectLeader + "'")
                            )
                    ),
                    EntityCondition.makeCondition(
                            EntityJoinOperator.AND,
                            EntityCondition.makeCondition("permission",EntityOperator.EQUALS,FeedbackParametersUtil.PLAN_PERSON),
                            EntityCondition.makeConditionWhere("'" + userLoginId + "'" + "=" + "'" + planPerson + "'")
                    ),
                    EntityCondition.makeCondition(
                            EntityJoinOperator.AND,
                            EntityCondition.makeCondition("permission",EntityOperator.EQUALS,FeedbackParametersUtil.PROJECT_LEADER),
                            EntityCondition.makeConditionWhere("'" + userLoginId + "'" + "=" + "'" + projectLeader + "'")
                    )
            )
    );

    if(UtilValidate.isNotEmpty(childWorkPlanId)){
        valueMap.put("showChild","false");//反馈列表不显示对应的子计划
        conditionList.add(EntityCondition.makeCondition("childWorkPlanId",EntityOperator.EQUALS,childWorkPlanId));
    }else {
        valueMap.put("showChild","true");//显示对应的子计划
        if (UtilValidate.isNotEmpty(partyId)) {
            conditionList.add(EntityCondition.makeCondition("feedbackPerson", EntityOperator.EQUALS, partyId));
        }
    }
    conditionList.add(EntityCondition.makeCondition("feedbackMiddleId", EntityOperator.EQUALS, workPlanId));

    conditionList.add(EntityCondition.makeCondition("feedbackMiddleType", EntityOperator.EQUALS, "TblWorkPlan"));
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
        eli.close();
        successResult.put("returnValue", valueMap);
        successResult.put("viewIndex", viewIndex);
        successResult.put("viewSize", viewSize);
        successResult.put("highIndex", highIndex);
        successResult.put("lowIndex", lowIndex);
        successResult.put("sortField",sortField);
        successResult.put("totalCount", memberSize);
        successResult.put("workPlanId", workPlanId);
        successResult.put("childWorkPlanId", childWorkPlanId);
        successResult.put("partyId", partyId);

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

public Map<String, Object> findOneById() {
    String childWorkPlanId = context.get("childWorkPlanId");
    GenericValue genericValue = EntityQuery.use(delegator)
            .from("TblChildWorkPlan")
            .where(EntityCondition.makeCondition("childWorkPlanId",EntityOperator.EQUALS,childWorkPlanId))
            .queryOne();
    Map<String, Object> valueMap = findOneById(context).get("workPlan");
    valueMap.put("childWorkPlan",genericValue);
    valueMap.put("curPartyId",partyId);
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    valueMap.put("curPartyId",context.get("curPartyId"));
    successResult.put("workPlan",valueMap);
    return successResult;
}

public Map<String, Object> findOneById(Map context) {
    Map<String, Object> valueMap = FastMap.newInstance();//输出数据
    List<Map> valueChildPlanList = FastList.newInstance();//子计划集合
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Locale locale = (Locale) context.get("locale");
    String partyId = context.get("partyId");
    String workPlanId = context.get("workPlanId");
    GenericValue workPlan = EntityQuery.use(delegator)
            .select("workPlanId", "departmentId","personId", "title", "startTime", "completeTime", "noticePerson", "planDescription", "planType"
            , "isPerformance", "importanceDegree", "difficultyDegree", "planPerson", "projectLeader", "canSeePerson", "workPlanStatus",
            "ptDescription", "idDescription", "ddDescription", "isDescription", "groupName","feedbackTime","feedbackPerson","actualFeedbackPerson")
            .from("WorkPlanJoinExecutor")
            .distinct()
            .where(EntityCondition.makeCondition("workPlanId", workPlanId))
            .queryOne();
    //当前执行人
    GenericValue currentExecutor = EntityQuery.use(delegator).from("StaffInfo").where(EntityCondition.makeCondition("staffId", partyId)).queryOne();
    //当前执行人的工作状况
    GenericValue curPersonWork = EntityQuery.use(delegator).from("TblPersonWork").where(EntityCondition.makeCondition(UtilMisc.toMap("workPlanId",workPlanId,"personId", partyId))).queryOne();
    //安排人
    GenericValue planPerson = EntityQuery.use(delegator).from("StaffInfo").where(EntityCondition.makeCondition("staffId", workPlan.get("planPerson"))).queryOne();
    //所有执行人
    List<GenericValue> executorList = delegator.findByAnd("ExecutorPersonWork",UtilMisc.toMap("workPlanId",workPlanId));
    //里程碑
    List<GenericValue> milestoneList = workPlan.getRelated("TblMilestone");

//子计划
    List<GenericValue> childPlanList = EntityQuery.use(delegator).from("TblChildWorkPlan").where(EntityCondition.makeCondition("workPlanId", workPlan.get("workPlanId"))).queryList();
    for (GenericValue genericValue : childPlanList) {
        Map<String, Object> childPlan = FastMap.newInstance();
        GenericValue childexecutor = delegator.findOne("Person",UtilMisc.toMap("partyId",genericValue.getString("operatorId")),false);
        childPlan.putAll(genericValue);
        childPlan.put("executorId", childexecutor.get("partyId"));
        childPlan.put("executorName", childexecutor.get("fullName"));
        valueChildPlanList.add(childPlan);
    }

    valueMap.put("childPlanList", valueChildPlanList);//子计划
    valueMap.putAll(workPlan);//总计划
    valueMap.put("currentExecutor", currentExecutor);//当前执行人
    valueMap.put("curPersonWork",curPersonWork);     //当前执行人的工作状况
    valueMap.put("executorList", executorList);//所有执行人
    valueMap.put("milestoneList",milestoneList);

    if (UtilValidate.isNotEmpty(planPerson)) {
        valueMap.put("planPersonName", planPerson.get("fullName"));
    } else {
        valueMap.put("planPersonName", workPlan.get("groupName"));
    }

    successResult.put("workPlan", valueMap);
    return successResult;
}

public Map<String, Object> deleteFeedback() {
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    List<String> strFeedbackList = Arrays.asList(context.get("deleteFeedbackId").split(","));
    for(String str : strFeedbackList){
        GenericValue feedback = delegator.makeValidValue("TblFeedback",UtilMisc.toMap("feedbackId",str,"logicDelete","Y"));
        feedback.store();
    }
    successResult.put("msg","反馈删除成功");
    return successResult;
}

public Map<String, Object> saveFeedback() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String childWorkPlanId = context.get("childWorkPlanId");
    String partyId = userLogin.get("partyId");//登录人id(反馈人)
    context.put("actualFeedbackPerson", partyId);
    String feedbackPerson = context.get("feedbackPerson");//被待反馈人id
    Double personWorkStatus = context.get("personWorkStatus");//个人进度
    String workPlanId = context.get("workPlanId");//工作计划id
    Timestamp feedbackTime = new Timestamp(System.currentTimeMillis());//反馈时间
    context.put("feedbackTime", feedbackTime);
    String feedbackId = delegator.getNextSeqId("TblFeedback");
    context.put("feedbackId", feedbackId);
    context.put("logicDelete","N");
    //反馈
    GenericValue feedback = delegator.makeValidValue("TblFeedback", context);
    feedback.create();
    //反馈中间表
    GenericValue feedbackMiddle = delegator.makeValidValue("TblFeedbackMiddle", UtilMisc.toMap("feedbackMiddleId", workPlanId, "childFeedbackId",childWorkPlanId,"feedbackId", feedbackId, "feedbackMiddleType", "TblWorkPlan"));
    feedbackMiddle.create();
    //跟新工作计划最后反馈
    GenericValue workPlan = delegator.makeValidValue("TblWorkPlan","workPlanId",workPlanId,"lastFeedback",feedbackId);
    workPlan.store();
    //子计划反馈，跟新子计划最后反馈
    if(UtilValidate.isNotEmpty(childWorkPlanId)){
        GenericValue childWorkPlan = delegator.makeValidValue("TblChildWorkPlan","childWorkPlanId",childWorkPlanId,"lastFeedback",feedbackId,"childWorkPlanStatus",personWorkStatus);
        childWorkPlan.store();
    }else{
        //查询工作进度是否存在
        GenericValue personWorkFrom = EntityQuery.use(delegator)
                .from("TblPersonWork")
                .where(EntityCondition.makeCondition(EntityCondition.makeCondition("workPlanId", EntityOperator.EQUALS, workPlanId), EntityOperator.AND,
                EntityCondition.makeCondition("personId", EntityOperator.EQUALS, feedbackPerson)))
                .queryOne();
        if (UtilValidate.isNotEmpty(personWorkFrom)) {
            //TODO 跟新工作进度
            personWorkFrom.put("personWorkStatus",personWorkStatus);
            personWorkFrom.put("lastFeedback",feedbackId);
            personWorkFrom.store();
        } else {
            //保存工作进度
            Timestamp actualStartTime = new Timestamp(System.currentTimeMillis());
            String personWorkId = delegator.getNextSeqId("TblPersonWork");
            GenericValue personWork = delegator.makeValidValue("TblPersonWork",
                    UtilMisc.toMap("personWorkId", personWorkId, "workPlanId", workPlanId, "personId", feedbackPerson, "actualStartTime", actualStartTime, "personWorkStatus", personWorkStatus,"lastFeedback",feedbackId,"isInvalid","N"));
            personWork.create();
        }
    }
    updateWorkPlanStatus(workPlanId);
    successResult.put("msg", "反馈成功！");
    return successResult;
}

public void updateWorkPlanStatus(String workPlanId){
    //更新工作计划状态
    EntityListIterator eliPer = EntityQuery.use(delegator)
            .from("TblPersonWork")
            .where(EntityCondition.makeCondition(
            UtilMisc.toList(
                    EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId),
                    EntityCondition.makeCondition("isInvalid",EntityOperator.EQUALS,"N")
            )
    )).queryIterator();
    List<GenericValue> personWorkList = eliPer.findAll();
    int perCount = personWorkList.size();
    eliPer.close();
    double completeSum = 0.0;
    for(GenericValue value : personWorkList){
        completeSum += value.getDouble("personWorkStatus");
    }
    BigDecimal bg = new BigDecimal(completeSum/(perCount*100)*100);
    double workPlanStatus = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    GenericValue updateWorkPlan = delegator.makeValidValue("TblWorkPlan",UtilMisc.toMap("workPlanId",workPlanId,"workPlanStatus",workPlanStatus));
    updateWorkPlan.store();
}

public Map<String,Object> saveGrade(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String workPlanId = context.get("workPlanId");
    String personWorkId = context.get("personWorkId");
    String personId = context.get("personId");
    Double status = context.get("personWorkStatus");
    String performanceScore = context.get("performanceScore");
    String completeTime = context.get("actualCompleteTime");
    SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    Date acDate = format.parse(completeTime);
    Timestamp actualCompleteTime = new Timestamp(acDate.getTime());
    String performanceRemark = context.get("performanceRemark");

    /*GenericValue genericValue = EntityQuery.use(delegator)
            .from("TblPersonWork")
            .where(EntityCondition.makeCondition(
            UtilMisc.toList(
                    EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId),
                    EntityCondition.makeCondition("personId",EntityOperator.EQUALS,personId)
            )))
            .queryOne();*/
    if(UtilValidate.isNotEmpty(personWorkId)){
        //跟新
        if(status == 100){
            context.put("personWorkStatus",new Double(100));
            context.put("performanceScore",performanceScore);
            context.put("actualCompleteTime",actualCompleteTime);
        }else {
            context.remove("performanceScore");
            context.remove("actualCompleteTime");
        }
        GenericValue personWork = delegator.makeValidValue("TblPersonWork",context);
        personWork.store();
    }else {
        //新建
        personWorkId = delegator.getNextSeqId("TblPersonWork");
        Map<String,Object> personWorkMap = FastMap.newInstance();
        personWorkMap.put("performanceRemark",performanceRemark);

        personWorkMap.put("personWorkId",personWorkId);
        personWorkMap.put("workPlanId",workPlanId);
        personWorkMap.put("personId",personId);
        personWorkMap.put("isInvalid","N");
        personWorkMap.put("actualStartTime",new Timestamp(System.currentTimeMillis()));
        if(status == 100){
            personWorkMap.put("performanceScore",performanceScore);
            personWorkMap.put("actualCompleteTime",actualCompleteTime);
            personWorkMap.put("personWorkStatus",status);
        }else if(status == -1){
            personWorkMap.put("personWorkStatus",status);
        }else {
            personWorkMap.put("personWorkStatus",new Double(0));
        }
        GenericValue personWork = delegator.makeValidValue("TblPersonWork",personWorkMap);
        personWork.create();
    }
    long personWorkCount = delegator.findCountByCondition("TblPersonWork",EntityCondition.makeCondition(
            UtilMisc.toList(
                    EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId),
                    EntityCondition.makeCondition("isInvalid",EntityOperator.EQUALS,"N"),
                    EntityCondition.makeCondition("personWorkStatus",EntityOperator.EQUALS,new Double(100)),
                    EntityCondition.makeCondition("performanceScore",EntityOperator.NOT_EQUAL,null)
            )),null,null);
    long workPlanExecutorCount = delegator.findCountByCondition("TblPersonWork",EntityCondition.makeCondition("workPlanId",EntityOperator.EQUALS,workPlanId),null,null);
    if(workPlanExecutorCount==personWorkCount){
        GenericValue workPlan = delegator.makeValidValue("TblWorkPlan","workPlanId",workPlanId,"workPlanStatus",new Double(100));
        workPlan.store();
    }
    updateWorkPlanStatus(workPlanId);
    successResult.put("msg","评分成功！");
    return successResult;
}

public Map<String,Object> deleteMilestone(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String milestoneId = context.get("milestoneId");
    String workPlanId = context.get("workPlanId");
    Map condition = FastMap.newInstance();
    if (UtilValidate.isNotEmpty(workPlanId)) {
        condition.put("workPlanId", workPlanId);
    } else {
        condition.put("milestoneId", milestoneId);
    }
    int index = delegator.removeByAnd("TblMilestone", condition);
    if (index > 0) {
        successResult.put("returnValue", "里程碑刪除成功！");
    }
    return successResult;
}
/*
* 任务分配
* */
public Map<String, Object> saveWorkPlanJobs() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String workPlanId = context.get("workPlanId");
    String personWorkId = context.get("personWorkId");
    String personId = context.get("personId");
    java.sql.Date startTime = new java.sql.Date((format.parse(context.get("startTime").toString())).getTime());
    java.sql.Date completeTime = new java.sql.Date((format.parse(context.get("completeTime").toString())).getTime());
    String jobDescription = context.get("jobDescription");
    GenericValue personWork = EntityQuery.use(delegator).from("TblPersonWork").where(EntityCondition.makeCondition(UtilMisc.toMap("workPlanId",workPlanId,"personId",personId))).queryOne();
    if(UtilValidate.isNotEmpty(personWork)){
        personWork.put("startTime",startTime);
        personWork.put("completeTime",completeTime);
        personWork.put("jobDescription",jobDescription);
        personWork.store();
        msg = "任务跟新成功！";
    }else {
        String id = delegator.getNextSeqId("TblPersonWork");
        GenericValue updatePersonWork = delegator.makeValidValue("TblPersonWork",UtilMisc.toMap(
                "personWorkId",id,"workPlanId",workPlanId,"personId",personId,"startTime",startTime,"completeTime",completeTime,"isInvalid","N","personWorkStatus",new Double(0),"jobDescription",jobDescription));
        updatePersonWork.create();
        msg = "任务分配成功！";
    }


    /*String msg = "";
    if(UtilValidate.isNotEmpty(personWorkId)){
        GenericValue personWork = delegator.makeValidValue("TblPersonWork",context);
        personWork.store();
        msg = "任务跟新成功！";
    }else {
        personWorkId = delegator.getNextSeqId("TblPersonWork");
        context.put("personWorkId",personWorkId);
        context.put("isInvalid","N");
        context.put("personWorkStatus",new Double(0));
        GenericValue personWork = delegator.makeValidValue("TblPersonWork",context);
        personWork.create();
    }*/
    successResult.put("returnValue",msg);
    return successResult;
}