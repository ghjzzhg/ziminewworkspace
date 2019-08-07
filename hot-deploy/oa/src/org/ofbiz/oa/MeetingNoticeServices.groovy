package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.apache.commons.collections.map.HashedMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> saveMeetingNotice() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String meetingNoticeId = context.get("meetingNoticeId");
    String hasSignIn = context.get("hasSignIn");
    String entityName = context.get("noticeDataScope_entity_name");
    String deptOnly = context.get("noticeDataScope_dept_only");
    String deptLike = context.get("noticeDataScope_dept_like");
    String levelOnly = context.get("noticeDataScope_level_only");
    String levelLike = context.get("noticeDataScope_level_like");
    String positionOnly = context.get("noticeDataScope_position_only");
    String positionLike = context.get("noticeDataScope_position_like");
    String userValue = context.get("noticeDataScope_user");
    if(UtilValidate.isEmpty(hasSignIn)||hasSignIn.equals(CheckingInParametersUtil.PARAMETERS_NO)){
        hasSignIn = CheckingInParametersUtil.PARAMETERS_NO;
    }
    GenericValue userInfo = EntityQuery.use(delegator)
            .from("TblStaff")
            .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLogin.get("partyId")))
            .queryOne();

    if(UtilValidate.isNotEmpty(meetingNoticeId)){
        //TODO 跟新
        GenericValue updateMeetingNotice = delegator.makeValidValue("TblMeetingNotice",context);
        updateMeetingNotice.store();

        //更新与会人员
        Map<String, String> deleteMap = UtilMisc.toMap("signInPersonType", "TblMeetingNotice", "noticeId", meetingNoticeId);
        delegator.removeByAnd("TblSignInPerson", deleteMap);
        String participants = context.get("participants");
        String[] partArray = participants.split(",");
        if(UtilValidate.isNotEmpty(partArray)){
            for(String str : partArray){
                String spId = delegator.getNextSeqId("TblSignInPerson");
                GenericValue createParticipant = delegator.makeValidValue("TblSignInPerson",
                        UtilMisc.toMap("singnInPersonId",spId,"noticeId",meetingNoticeId,"staffId",str,"hasSignIn","NS_N","signInPersonStatus","NS_NOT_SEE","signInPersonType","TblMeetingNotice"));
                createParticipant.create();
            }
        }
        //更新数据范围
        dispatcher.runSync("saveDataScope", UtilMisc.toMap(
                "entityName",entityName,"dataId",meetingNoticeId,"dataAttr","","deptOnly",deptOnly,
                "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                "positionLike",positionLike,"userValue",userValue,"userLogin",userLogin
        ));
    }else {
        if(UtilValidate.isNotEmpty(userInfo)){
            context.put("releaseDepartment",userInfo.get("department"));
        }
        context.put("releaseTime",new Date(new java.util.Date().getTime()));
        meetingNoticeId = delegator.getNextSeqId("TblMeetingNotice");
        context.put("meetingNoticeId",meetingNoticeId);
        context.put("releaseSummary",CheckingInParametersUtil.PARAMETERS_NO);
        GenericValue createMeetingNotice = delegator.makeValidValue("TblMeetingNotice",context);
        createMeetingNotice.create();
        dispatcher.runSync("saveDataScope", UtilMisc.toMap(
                "entityName","TblMeetingNotice","dataId",meetingNoticeId,"dataAttr","","deptOnly",deptOnly,
                "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                "positionLike",positionLike,"userValue",userValue,"userLogin",userLogin
        ));
        String participants = context.get("participants");
        String[] partArray = participants.split(",");
        if(UtilValidate.isNotEmpty(partArray)){
            for(String str : partArray){
                String spId = delegator.getNextSeqId("TblSignInPerson");
                GenericValue createParticipant = delegator.makeValidValue("TblSignInPerson",
                        UtilMisc.toMap("singnInPersonId",spId,"noticeId",meetingNoticeId,"staffId",str,"hasSignIn","NS_N","signInPersonStatus","NS_NOT_SEE","signInPersonType","TblMeetingNotice"));
                createParticipant.create();
            }
        }

        BaiDuYunPush.setMessageData(meetingNoticeId,context,context.get("hasSignIn"),BaiDuYunPush.MEETING_NOTICE,delegator,BaiDuYunPush.MEETING_NOTICE_NAME,context.get("meetingTheme"),context.get("meetingName"))
    }
    successResult.put("msg","发布会议成功！");
    return successResult;
}

public Map<String,Object> searchMeetingNotice(){

    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String meetingName = context.get("meetingName");
    String meetingTheme = context.get("meetingTheme");
    String meetingStartTime = context.get("startTime");
    String meetingEndTime = context.get("endTime");
    String releaseDepartment = context.get("releaseDepartment");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");
    GenericValue staff = delegator.findOne("TblStaff",UtilMisc.toMap("partyId",partyId),false);
    String department = "";
    String position = "";
    if(UtilValidate.isNotEmpty(staff)){
        department = staff.get("department");
        position = staff.get("position");
    }
    String level = "";//TODO
    //TODO 范围权限
    List<EntityCondition> conditionList = FastList.newInstance();
    if(UtilValidate.isNotEmpty(department)){
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("scopeType",EntityOperator.EQUALS,"SCOPE_DEPT_ONLY"),
                EntityCondition.makeCondition("scopeValue",EntityOperator.EQUALS,department),
        )));
    }
    if(UtilValidate.isNotEmpty(position)){
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("scopeType",EntityOperator.EQUALS,"SCOPE_POSITION_ONLY"),
                EntityCondition.makeCondition("scopeValue",EntityOperator.EQUALS,position),
        )));
    }
    if(UtilValidate.isNotEmpty(level)){
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("scopeType",EntityOperator.EQUALS,"SCOPE_LEVEL_ONLY"),
                EntityCondition.makeCondition("scopeValue",EntityOperator.EQUALS,level),
        )));
    }
    conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(
            EntityCondition.makeCondition("scopeType",EntityOperator.EQUALS,"SCOPE_USER"),
            EntityCondition.makeCondition("scopeValue",EntityOperator.EQUALS,partyId),
    )));
    EntityCondition condition1 = EntityCondition.makeCondition(UtilMisc.toList(
            EntityCondition.makeCondition(conditionList,EntityOperator.OR),
            EntityCondition.makeCondition("entityName","TblMeetingNotice")
    ));
    EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
            condition1,EntityCondition.makeCondition("scopeType",EntityOperator.EQUALS,"")
    ),EntityOperator.OR);
    List<GenericValue> scopeList = EntityQuery.use(delegator)
            .select("dataId")
            .from("TblDataScope")
            .where(condition)
            .queryList();
    List<EntityCondition> conditions = FastList.newInstance();
    //conditions.add(EntityCondition.makeCondition("meetingNoticeId",EntityOperator.IN,scopeList));
    conditions.add(EntityCondition.makeCondition("releaseSummary",EntityOperator.EQUALS,CheckingInParametersUtil.PARAMETERS_NO));
    if(UtilValidate.isNotEmpty(meetingStartTime)&&UtilValidate.isNotEmpty(meetingEndTime)){
        conditions.add(EntityCondition.makeCondition("meetingStartTime",EntityOperator.BETWEEN,UtilMisc.toList(
                UtilDateTime.stringToTimeStamp(meetingStartTime,UtilDateTime.DATE_FORMAT,TimeZone.getDefault(),Locale.getDefault()),
                UtilDateTime.stringToTimeStamp(meetingEndTime,UtilDateTime.DATE_FORMAT,TimeZone.getDefault(),Locale.getDefault()))));
    }
    if(UtilValidate.isNotEmpty(meetingName)){
        conditions.add(
                EntityCondition.makeCondition("meetingName",EntityOperator.LIKE,"%"+meetingName+"%")
        );
    }
    if (UtilValidate.isNotEmpty(meetingTheme)){
        conditions.add(
                EntityCondition.makeCondition("meetingTheme",EntityOperator.LIKE,"%"+meetingTheme+"%")
        );
    }
    if (UtilValidate.isNotEmpty(releaseDepartment)){
        conditions.add(
                EntityCondition.makeCondition("releaseDepartment",EntityOperator.EQUALS,releaseDepartment)
        );
    }
    EntityListIterator meetingNoticeList = null;
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    List<Map> returnValue = FastList.newInstance();
    if(UtilValidate.isNotEmpty(conditions)){
        meetingNoticeList = EntityQuery.use(delegator)
                .from("MeetingNoticeInfo")
                .where(conditions)
                .orderBy("releaseTime DESC")
                .queryIterator();
        if(UtilValidate.isNotEmpty(meetingNoticeList)){
            List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
            if(null != meetingNoticeList && meetingNoticeList.getResultsSizeAfterPartialList() > 0){
                totalCount = meetingNoticeList.getResultsSizeAfterPartialList();
                pageList = meetingNoticeList.getPartialList(lowIndex, viewSize);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for(GenericValue g : pageList){
                Map<String,Object> valueMap = FastMap.newInstance();
                valueMap.putAll(g);
                valueMap.put("meetingStartTime",format.format((java.util.Date)g.get("meetingStartTime")));
                valueMap.put("meetingEndTime",format.format((java.util.Date)g.get("meetingEndTime")));
                list.add(valueMap);
            }
        }
    }
    meetingNoticeList.close()
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",list);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("meetingName",meetingName);
    map.put("meetingTheme",meetingTheme);
    map.put("meetingStartTime",meetingStartTime);
    map.put("meetingEndTime",meetingEndTime);
    map.put("releaseDepartment",releaseDepartment);
    successResult.put("returnValue",map);
    return successResult;
}

public Map<String,Object> meetingNoticeInfo(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String meetingNoticeId = context.get("meetingNoticeId");

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    GenericValue meetingNotice = EntityQuery.use(delegator)
            .from("MeetingNoticeInfo")
            .where(EntityCondition.makeCondition("meetingNoticeId",meetingNoticeId))
            .queryOne();
    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.putAll(meetingNotice);
    valueMap.put("meetingStartTime",format.format((java.util.Date)meetingNotice.get("meetingStartTime")));
    valueMap.put("meetingEndTime",format.format((java.util.Date)meetingNotice.get("meetingEndTime")));

    //TODO 与会人员 发布范围
    successResult.put("returnValue",valueMap);
    return successResult;
}

public Map<String,Object> saveFeedbackOfMeetingNotice(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String meetingNoticeId = context.get("meetingNoticeId");
    String feedbackContext = context.get("feedbackContext1");

    dispatcher.runSync("saveFeedbackBasic",UtilMisc.toMap("userLogin",userLogin,"feedbackMiddleId",meetingNoticeId,"feedbackContext",feedbackContext,"feedbackMiddleType","TblMeetingNotice"));
    successResult.put("msg","反馈成功！");
    return successResult;
}
public Map<String,Object> saveMeetingNoticeSummary(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map<String,Object> data = FastMap.newInstance();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String meetingNoticeId = context.get("meetingNoticeId");
    String summaryId = context.get("summaryId");
    String participants = context.get("participants");

    String entityName = context.get("meetingSummaryDataScope_entity_name");
    String deptOnly = context.get("meetingSummaryDataScope_dept_only");
    String deptLike = context.get("meetingSummaryDataScope_dept_like");
    String levelOnly = context.get("meetingSummaryDataScope_level_only");
    String levelLike = context.get("meetingSummaryDataScope_level_like");
    String positionOnly = context.get("meetingSummaryDataScope_position_only");
    String positionLike = context.get("meetingSummaryDataScope_position_like");
    String dataScopeUser = context.get("meetingSummaryDataScope_user");

    if(UtilValidate.isNotEmpty(meetingNoticeId)){
        context.put("releaseSummary",CheckingInParametersUtil.PARAMETERS_YES);
        if(UtilValidate.isNotEmpty(summaryId)){
            //TODO 跟新
            context.put("lastEditPerson",userLogin.get("partyId"));
            context.put("lastEditTime",new Timestamp(new java.util.Date().getTime()));
            context.put("meetingStartTime", context.get("meetingStartTime1"));
            context.put("meetingEndTime", context.get("meetingEndTime1"));
            GenericValue updateMeetingNotice = delegator.makeValidValue("TblMeetingNotice",context);
            updateMeetingNotice.store();
            GenericValue updateSummary = delegator.makeValidValue("TblMeetingSummary",context);
            updateSummary.store();

            //更新与会人员
            Map<String, String> deleteMap = UtilMisc.toMap("signInPersonType", "TblMeetingNotice", "noticeId", meetingNoticeId);
            delegator.removeByAnd("TblSignInPerson", deleteMap);
            String[] partArray = participants.split(",");
            if(UtilValidate.isNotEmpty(partArray)){
                for(String str : partArray){
                    String spId = delegator.getNextSeqId("TblSignInPerson");
                    GenericValue createParticipant = delegator.makeValidValue("TblSignInPerson",
                            UtilMisc.toMap("singnInPersonId",spId,"noticeId",meetingNoticeId,"staffId",str,"hasSignIn","NS_N","signInPersonStatus","NS_NOT_SEE","signInPersonType","TblMeetingNotice"));
                    createParticipant.create();
                }
            }
            //更新数据范围
            dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName",entityName,"deptOnly",deptOnly,
                    "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                    "positionLike",positionLike,"userValue",dataScopeUser,"dataId",summaryId,"userLogin",userLogin));
            data.put("type","update");
            data.put("msg","会议纪要更新成功！");
        }else {
            context.put("lastEditPerson",userLogin.get("partyId"));
            context.put("lastEditTime",new Timestamp(new java.util.Date().getTime()));
            context.put("meetingStartTime", context.get("meetingStartTime1"));
            context.put("meetingEndTime", context.get("meetingEndTime1"));
            GenericValue updateMeetingNotice = delegator.makeValidValue("TblMeetingNotice",context);
            updateMeetingNotice.store();
            context.put("summaryId",meetingNoticeId);
            context.put("releasePerson",userLogin.getString("partyId"));
            context.put("releaseSummaryTime",UtilDateTime.nowTimestamp());
            GenericValue createSummary = delegator.makeValidValue("TblMeetingSummary",context);
            createSummary.create();
            Map<String, String> deleteMap = UtilMisc.toMap("signInPersonType", "TblMeetingNotice", "noticeId", meetingNoticeId);
            delegator.removeByAnd("TblSignInPerson", deleteMap);
            String[] partArray = participants.split(",");
            if(UtilValidate.isNotEmpty(partArray)){
                for(String str : partArray){
                    String spId = delegator.getNextSeqId("TblSignInPerson");
                    GenericValue createParticipant = delegator.makeValidValue("TblSignInPerson",
                            UtilMisc.toMap("singnInPersonId",spId,"noticeId",meetingNoticeId,"staffId",str,"hasSignIn","NS_N","signInPersonStatus","NS_NOT_SEE","signInPersonType","TblMeetingNotice"));
                    createParticipant.create();
                }
            }
            dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName",entityName,"deptOnly",deptOnly,
                    "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                    "positionLike",positionLike,"userValue",dataScopeUser,"dataId",meetingNoticeId,"userLogin",userLogin));
            data.put("type","create");
            data.put("msg","会议纪要发布成功！");
            BaiDuYunPush.setMessageData(meetingNoticeId,context,context.get("hasSignIn"),BaiDuYunPush.MEETING_SUMMARY,delegator,BaiDuYunPush.MEETING_SUMMARY_NAME,context.get("meetingTheme"),context.get("meetingName"));
        }
    }
    successResult.put("data",data);
    return successResult;
}

public Map<String,Object> searchMeetingNoticeSummary(){

    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String meetingName = context.get("meetingName");
    String meetingTheme = context.get("meetingTheme");
    java.util.Date meetingStartTime = (java.util.Date)context.get("startTime");
    java.util.Date meetingEndTime = (java.util.Date)context.get("endTime");
    String releaseDepartment = context.get("releaseDepartment");
    List<EntityCondition> conditions = FastList.newInstance();
    if(UtilValidate.isNotEmpty(meetingName)){
        conditions.add(EntityCondition.makeCondition("meetingName",EntityOperator.LIKE,"%"+meetingName+"%"));
    }
    if(UtilValidate.isNotEmpty(meetingTheme)){
        conditions.add(EntityCondition.makeCondition("meetingTheme",EntityOperator.LIKE,"%"+meetingTheme+"%"));
    }
    if(UtilValidate.isNotEmpty(meetingStartTime)&&UtilValidate.isNotEmpty(meetingEndTime)){
        conditions.add(EntityCondition.makeCondition("meetingStartTime",EntityOperator.BETWEEN,UtilMisc.toList(new Timestamp(meetingStartTime.getTime()),
                new Timestamp(meetingEndTime.getTime()))));
    }
    if(UtilValidate.isNotEmpty(releaseDepartment)){
        conditions.add(EntityCondition.makeCondition("releaseDepartment",EntityOperator.EQUALS,releaseDepartment));
    }
    EntityListIterator meetingSummaryList = null;
    if(UtilValidate.isNotEmpty(conditions)){
        meetingSummaryList = EntityQuery.use(delegator)
                .from("MeetingSummaryNotice")
                .orderBy("-meetingStartTime")
                .where(EntityCondition.makeCondition(conditions))
                .queryIterator();
    }else {
        meetingSummaryList = EntityQuery.use(delegator)
                .from("MeetingSummaryNotice")
                .orderBy("-meetingStartTime")
                .queryIterator();
    }
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != meetingSummaryList && meetingSummaryList.getResultsSizeAfterPartialList() > 0){
        totalCount = meetingSummaryList.getResultsSizeAfterPartialList();
        pageList = meetingSummaryList.getPartialList(lowIndex, viewSize);
    }
    meetingSummaryList.close();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("meetingName",meetingName);
    map.put("meetingTheme",meetingTheme);
    map.put("meetingStartTime",meetingStartTime);
    map.put("meetingEndTime",meetingEndTime);
    map.put("releaseDepartment",releaseDepartment);
    successResult.put("returnValue",map);
    return successResult;
}

public Map<String,Object> deleteMeetingNoticeSummary(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String summaryId = context.get("summaryId");
    GenericValue summary = delegator.findOne("TblMeetingSummary",UtilMisc.toMap("summaryId",summaryId),false);
    summary.remove();
    summary.removeRelated("TblMeetingNotice");
    Map<String, String> deleteMap = UtilMisc.toMap("signInPersonType", "TblMeetingNotice", "noticeId", summaryId);
    delegator.removeByAnd("TblSignInPerson", deleteMap);
    successResult.put("msg","会议纪要删除成功！");
    return successResult;
}

public Map<String,Object> deleteMeetingNotice(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String meetingNoticeId = context.get("meetingNoticeId");
    GenericValue meetingNotice = delegator.findOne("TblMeetingNotice",UtilMisc.toMap("meetingNoticeId",meetingNoticeId),false);
    meetingNotice.remove();
    Map<String, String> deleteMap = UtilMisc.toMap("signInPersonType", "TblMeetingNotice", "noticeId", meetingNoticeId);
    delegator.removeByAnd("TblSignInPerson", deleteMap);
    successResult.put("msg","会议通知删除成功！");
    return successResult;
}

public Map<String,Object> showPersonList(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String summaryId = context.get("summaryId");
    GenericValue summary = delegator.findOne("TblMeetingSummary",UtilMisc.toMap("summaryId",summaryId), false);
    Map map = new HashMap();
    List absentPersonList = new ArrayList();
    List latePersonList = new ArrayList();
    List personList = new ArrayList();
    if(UtilValidate.isNotEmpty(summary.get("absentPerson"))) {
        String allAbsentPerson = summary.get("absentPerson").toString();
        String[] absentPersonArray = allAbsentPerson.split(",");
        for (String staffId : absentPersonArray) {
            List<GenericValue> resultList = delegator.findByAnd("StaffInfo", UtilMisc.toMap("staffId", staffId));
            Map resultMap = resultList.get(0);
            absentPersonList.add(resultMap);
        }
    }
    if(UtilValidate.isNotEmpty(summary.get("latePerson"))) {
        String allLatePerson = summary.get("latePerson").toString();
        String[] latePersonArray = allLatePerson.split(",");
        for (String staffId : latePersonArray) {
            List<GenericValue> resultList1 = delegator.findByAnd("StaffInfo", UtilMisc.toMap("staffId", staffId));
            Map resultMap1 = resultList1.get(0);
            latePersonList.add(resultMap1);
        }
    }
    personList.add(absentPersonList);
    personList.add(latePersonList);
    //查找执行人
//    String allName = searchExecutor(summaryId, map);
//    workReportMap.put("allName",allName);
    successResult.put("personList", personList);
    return successResult;
}