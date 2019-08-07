package org.ofbiz.oa

import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery

GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
GenericValue userLogin = context.get("userLogin");
String workLogId = parameters.get("workLogId");
String workLogDate = parameters.get("workLogDate");
List<GenericValue> scheduleList;
GenericValue workLog = null;
if(UtilValidate.isNotEmpty(workLogId)){
    scheduleList = delegator.findByAnd("TblSchedule",UtilMisc.toMap("workLogId",workLogId),null,false);
    workLog = EntityQuery.use(delegator).select().from("TblWorkLog").where(UtilMisc.toMap("workLogId",workLogId)).queryOne();
}else{
    List<GenericValue> workLogList = delegator.findByAnd("TblWorkLog",UtilMisc.toMap("partyId",userLogin.getString("partyId"),"workLogDate",UtilDateTime.toSqlDate(workLogDate,"yyyy-MM-dd")),null,false);
    if(UtilValidate.isNotEmpty(workLogList)){
        workLog = workLogList.get(0);
    }
    if(UtilValidate.isNotEmpty(workLog)){
        scheduleList = delegator.findByAnd("TblSchedule",UtilMisc.toMap("workLogId",workLog.get("workLogId")),null,false);
    }else{
        DynamicViewEntity dynamicView = DynamicViewEntity.newInstance();
        dynamicView.addMemberEntity("workLog","TblWorkLog");
        dynamicView.addMemberEntity("schedule","TblSchedule")
        dynamicView.addAliasAll("workLog","",null);
        dynamicView.addAliasAll("schedule","",null);
        dynamicView.addViewLink("workLog","schedule",true,UtilMisc.toList(ModelKeyMap.makeKeyMapList("workLogId")));
        scheduleList = EntityQuery.use(delegator).from(dynamicView).where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",userLogin.getString("partyId"),"workLogDate",UtilDateTime.toSqlDate(workLogDate,"yyyy-MM-dd")))).queryList();
    }
}

GenericValue map = EntityQuery.use(delegator).from("TblStaff").where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",userLogin.getString("partyId")))).queryOne();
String template = "";
if(UtilValidate.isNotEmpty(map)){
    String positionId = "Company,"+map.get("post");
    String userLoginId = context.get("userLogin").get("userLoginId");
    List<Map> resultMapForUser = delegator.findByAnd("TblWorkLogTemplate",UtilMisc.toMap("positionId",positionId),null,false);
    if (resultMapForUser.size()!=0){
        template = resultMapForUser[0].get("template");
        }
}

//List<GenericValue> logSet = delegator.findAll("TblLogSet",false);
GenericValue department = EntityQuery.use(delegator)
        .select("department")
        .from("TblStaff")
        .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.get("userLogin").get("partyId")))
        .queryOne();
List<GenericValue> logSet = EntityQuery.use(delegator)
.select()
.from("TblLogSet")
.where(EntityCondition.makeCondition("department",department.get("department")))
.queryList();
Date nowDate = new Date(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()).getTime());
Date logLimiteDate;
Date scheduleLimiteDate;
if(UtilValidate.isNotEmpty(logSet)){
    String logValue = logSet.get(0).getString("logValue");
    String planValue = logSet.get(0).getString("planValue");
    if(UtilValidate.isNotEmpty(logValue)){
        logLimiteDate = CheckingUtil.addDays(new Date(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()).getTime()),Integer.parseInt(logValue));
    }else {
        logLimiteDate = nowDate;
    }
    if(UtilValidate.isNotEmpty(planValue)){
        scheduleLimiteDate = CheckingUtil.addDays(new Date(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()).getTime()),-Integer.parseInt(planValue));
    }else {
        scheduleLimiteDate = nowDate;
    }
}else {
    logLimiteDate = nowDate;
    scheduleLimiteDate = nowDate;
}

context.canLog = logLimiteDate.compareTo(new Date(UtilDateTime.toSqlDate(workLogDate,"yyyy-MM-dd").getTime())) != -1 ? true : false;
context.canSchedule = scheduleLimiteDate.compareTo(new Date(UtilDateTime.toSqlDate(workLogDate,"yyyy-MM-dd").getTime())) != 1 ? true : false;
context.template = template;
context.scheduleList = scheduleList;
context.workLog = workLog;
context.workLogDate=workLogDate;