package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> saveWorkLog() {
    Map<String,Object> result = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");
    String workLogId = context.get("workLogId");
    String reviewContent = context.get("reviewContent");
    String Id ="";
    Map<String ,Object> map = new HashMap<String ,Object>();
    if(UtilValidate.isNotEmpty(reviewContent)){
        context.put("reviewedBy",userLoginId.getString("partyId"));
        context.put("reviewDate",new java.sql.Date(new Date().getTime()));
    }
    try{
        if(UtilValidate.isNotEmpty(workLogId)){
            GenericValue workLog = delegator.makeValidValue("TblWorkLog",context);
            workLog.store();
            Id = context.get("workLogId");
        }else{
            GenericValue genericValue = EntityQuery.use(delegator).from("TblWorkLog").where(UtilMisc.toMap("workLogDate",context.get("workLogDate"),"partyId",userLoginId.getString("partyId"))).queryOne()
            if(genericValue==null){
                context.put("partyId",userLoginId.getString("partyId"));
                Id =delegator.getNextSeqId("TblWorkLog");
                context.put("workLogId",Id);
                workLog = delegator.makeValidValue("TblWorkLog",context);
                workLog.create();
            }else{
                context.put("workLogId",genericValue.get("workLogId"))
                workLog = delegator.makeValidValue("TblWorkLog",context);
                workLog.store();
                Id = context.get("workLogId");
            }
        }

        map.put("message","日志记录成功！");
        map.put("workLogId",Id);
        map.put("logContent", context.get("logContent"));
        map.put("reviewContent", context.get("reviewContent"));
        result.put("data",map);
    }catch (GenericEntityException e){
        e.printStackTrace();
    }
    return result;
}
public boolean checkDateTime(List<EntityCondition> condition,Timestamp startDate,Timestamp endDate,DynamicViewEntity dynamicView){
    List<GenericValue> sValueList = EntityQuery.use(delegator).from(dynamicView).where(condition).queryList();
    for(GenericValue value : sValueList){
        Timestamp start = value.getTimestamp("scheduleStartDatetime");
        Timestamp end = value.getTimestamp("scheduleEndDatetime");
        int compare1 = startDate.compareTo(end);
        int compare2 = endDate.compareTo(start);
        if((compare1 != 1) && (compare2 != -1)){
            return true;
        }
    }
    return false;
}
/*
* 保存日程
* */
public Map<String, Object> saveSchedule() {
    Map<String,Object> result = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");

    String workLogId = context.get("workLogId");
    String scheduleId = context.get("scheduleId");
    String startDay = context.get("scheduleStartDatetime");
    String start_hour = context.get("scheduleStartDatetime_hour");
    String start_minute = context.get("scheduleStartDatetime_minute");
    String endDay = context.get("scheduleEndDatetime");
    String end_hour = context.get("scheduleEndDatetime_hour");
    String end_minute = context.get("scheduleEndDatetime_minute");
    java.sql.Timestamp scheduleStartDatetime = UtilDateTime.toTimestamp(UtilDateTime.toSqlDate(startDay + " " + start_hour + ":" + start_minute + ":00.000",UtilDateTime.DATE_TIME_FORMAT));
    java.sql.Timestamp scheduleEndDatetime = UtilDateTime.toTimestamp(UtilDateTime.toSqlDate(endDay + " " + end_hour + ":" + end_minute + ":00.000",UtilDateTime.DATE_TIME_FORMAT));


    DynamicViewEntity dynamicView = DynamicViewEntity.newInstance();
    dynamicView.addMemberEntity("workLog","TblWorkLog");
    dynamicView.addMemberEntity("schedule","TblSchedule");
    dynamicView.addAliasAll("workLog","",null);
    dynamicView.addAliasAll("schedule","",null);
    dynamicView.addViewLink("workLog","schedule",false,UtilMisc.toList(ModelKeyMap.makeKeyMapList("workLogId")));
    List<EntityCondition> condition = FastList.newInstance();
    condition.add(EntityCondition.makeCondition("partyId",userLoginId.getString("partyId")));
    condition.add(EntityCondition.makeCondition("workLogDate",UtilDateTime.toSqlDate(startDay,"yyyy-MM-dd")));
    Map<String,Object> valueMap = FastMap.newInstance();
    String msg = "";
    try{
        java.sql.Date workLogDate = UtilDateTime.toSqlDate(startDay,"yyyy-MM-dd");
        if(UtilValidate.isNotEmpty(workLogId)&&!"null".equals(workLogId)){
            context.put("scheduleStartDatetime",scheduleStartDatetime);
            context.put("scheduleEndDatetime",scheduleEndDatetime);
            if(UtilValidate.isNotEmpty(scheduleId)){
                condition.add(EntityCondition.makeCondition("scheduleId",EntityOperator.NOT_EQUAL,scheduleId))
                if(!checkDateTime(condition,scheduleStartDatetime,scheduleEndDatetime,dynamicView)){
                    GenericValue schedule = delegator.makeValidValue("TblSchedule",context);
                    delegator.store(schedule);
                    msg = "更新日程成功";
                }else {
                    msg = "日程时间重叠，请调整日程时间";
                }
            }else {
                if(!checkDateTime(condition,scheduleStartDatetime,scheduleEndDatetime,dynamicView)){
                    scheduleId = delegator.getNextSeqId("TblSchedule");
                    context.put("scheduleId",scheduleId);
                    GenericValue createSchedule = delegator.makeValidValue("TblSchedule",context);
                    delegator.create(createSchedule);
                    msg = "新建日程成功";
                }else {
                    msg = "日程时间重叠，请调整日程时间";
                }
            }
        }else {
            GenericValue genericValue = EntityQuery.use(delegator).from("TblWorkLog").where(UtilMisc.toMap("workLogDate",workLogDate,"partyId",userLoginId.getString("partyId"))).queryOne()
            if(genericValue==null){
                workLogId = delegator.getNextSeqId("TblWorkLog");
                GenericValue workLog = delegator.makeValidValue("TblWorkLog",UtilMisc.toMap("workLogId",workLogId,"workLogDate",workLogDate,"partyId",userLoginId.getString("partyId")));
                delegator.create(workLog);
            }else{
                workLogId = genericValue.get("workLogId");
            }
            context.put("workLogId",workLogId);
            context.put("scheduleStartDatetime",scheduleStartDatetime);
            context.put("scheduleEndDatetime",scheduleEndDatetime);
            if(!checkDateTime(condition,scheduleStartDatetime,scheduleEndDatetime,dynamicView)){
                scheduleId = delegator.getNextSeqId("TblSchedule");
                context.put("scheduleId",scheduleId);
                GenericValue createSchedule = delegator.makeValidValue("TblSchedule",context);
                delegator.create(createSchedule);
                msg = "新建日程成功";
            }else {
                msg = "日程时间重叠，请调整日程时间";
            }

        }
    }catch (Exception e){
        e.printStackTrace();
    }
    List<GenericValue> scheduleList = EntityQuery.use(delegator).from(dynamicView).where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",userLoginId.getString("partyId"),"workLogDate",UtilDateTime.toSqlDate(startDay,"yyyy-MM-dd")))).queryList();
    valueMap.put("scheduleList",scheduleList);
    valueMap.put("msg",msg);
    valueMap.put("workLogId",workLogId);
    valueMap.put("workLogDate",startDay);
    result.put("data",valueMap);
    return result;
}

public Map<String, Object> deleteSchedule() {
    Map<String,Object> result = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");
    String workLogId = context.get("workLogId");
    String scheduleId = context.get("scheduleId");
    String workLogDate = context.get("workLogDate");
    delegator.removeByAnd("TblSchedule",UtilMisc.toMap("scheduleId",scheduleId));

    DynamicViewEntity dynamicView = DynamicViewEntity.newInstance();
    dynamicView.addMemberEntity("workLog","TblWorkLog");
    dynamicView.addMemberEntity("schedule","TblSchedule");
    dynamicView.addAliasAll("workLog","",null);
    dynamicView.addAliasAll("schedule","",null);
    dynamicView.addViewLink("workLog","schedule",false,UtilMisc.toList(ModelKeyMap.makeKeyMapList("workLogId")));
    Map<String,Object> valueMap = FastMap.newInstance();
    List<GenericValue> scheduleList = EntityQuery.use(delegator).from(dynamicView).where(EntityCondition.makeCondition(UtilMisc.toMap("partyId",userLoginId.getString("partyId"),"workLogDate",UtilDateTime.toSqlDate(workLogDate,"yyyy-MM-dd")))).queryList();
    valueMap.put("scheduleList",scheduleList);
    valueMap.put("msg","删除成功");
    valueMap.put("workLogId",workLogId);
    valueMap.put("workLogDate",workLogDate);
    result.put("data",valueMap);
    return result;
}

public Map<String, Object> saveLeadInstructions() {
    workLogId = context.get("workLogId");
    Map<String,Object> result = ServiceUtil.returnSuccess();
    GenericValue genericValue = null;
    date = context.get("workDate");
    Timestamp workDate = Timestamp.valueOf(date);
    Date dateForChange = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = df.format(dateForChange);
    reviewDate = Timestamp.valueOf(dateString+" 00:00:00.0");
    try{
        workLogId = delegator.getNextSeqId("TblWorkLog").toString();
        genericValue = delegator.makeValue("TblWorkLog",UtilMisc.toMap("workLogId", workLogId))
        genericValue.setString("logTitle",context.get("logTitle"))
        genericValue.setString("logContent",context.get("logContent"))
        genericValue.setString("planTitle",context.get("planTitle"))
        genericValue.setString("planContent",context.get("planContent"))
        genericValue.setString("reviewContent",context.get("reviewContent"))
        genericValue.setString("reviewedBy",context.get("reviewedBy"))
        genericValue.setString("partyId",context.get("partyId"))
        genericValue.set("workDate",workDate)
        genericValue.set("reviewDate",reviewDate)
            genericValue.store();
    }catch (GenericEntityException e){
        e.printStackTrace();
    }
    result.put("data",workLogId)
    return result;
}
public Map<String ,Object> saveAttentionSubordinate(){
    String partyIdFrom = context.get("userLogin").get("partyId");
    String partyIdTo = context.get("partyId")
    String ifAttentionButton = context.get("ifAttentionButton")
    if (ifAttentionButton=="关注"){
        try{
            genericValue = delegator.makeValue("TblFocusUnderling",UtilMisc.toMap("partyIdFrom", partyIdFrom));
            genericValue.setString("partyIdTo",partyIdTo)
            genericValue.create();
            msg = "关注成功"
        }catch (GenericEntityException e){
            e.printStackTrace();
            msg = "关注失败"
        }
    }else if("取消关注"){
        try{
            genericValue = delegator.removeByAnd("TblFocusUnderling",UtilMisc.toMap("partyIdFrom", partyIdFrom,"partyIdTo",partyIdTo));
            msg = "取消关注成功"
        }catch (GenericEntityException e){
            e.printStackTrace();
            msg = "取消关注失败"
        }
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    result.put("data",msg)
    return result;
}