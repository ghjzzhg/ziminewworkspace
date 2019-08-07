package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ServiceUtil

import javax.swing.text.html.parser.Entity
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> findEnums() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String enumTypeId = context.get("enumTypeId");
    listEnums=[];
    enums = from("Enumeration").where(UtilMisc.toMap("enumTypeId", enumTypeId)).orderBy("sequenceId").queryList();
    if(enums){
        listEnums = enums;
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("listEnums",listEnums);
    map.put("size", listEnums.size());
    successResult.put("data",map);
    return successResult;

}

public Map<String, Object> saveNotice() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String noticeId = context.get("noticeId");
    String strSignInPerson = context.get("signInPerson");
    String msg = "";
    String entityName = context.get("noticeDataScope_entity_name");
    String deptOnly = context.get("noticeDataScope_dept_only");
    String deptLike = context.get("noticeDataScope_dept_like");
    String levelOnly = context.get("noticeDataScope_level_only");
    String levelLike = context.get("noticeDataScope_level_like");
    String positionOnly = context.get("noticeDataScope_position_only");
    String positionLike = context.get("noticeDataScope_position_like");
    String dataScopeUser = context.get("noticeDataScope_user");

    if(UtilValidate.isNotEmpty(noticeId)){
        GenericValue updateNotice = delegator.makeValidValue("TblNotice",context);
        delegator.removeByCondition("TblDataScope",EntityCondition.makeCondition(EntityCondition.makeCondition("entityName",EntityOperator.EQUALS,"TblNotice"),
                EntityJoinOperator.AND,EntityCondition.makeCondition("dataId",EntityOperator.EQUALS,noticeId)));
        updateNotice.store();
        GenericValue deleteNotice = delegator.makeValidValue("TblNotice","noticeId",noticeId);
        deleteNotice.removeRelated("TblSignInPerson");
        String[] signInPersonIds = strSignInPerson.split(",");
        for(String str : signInPersonIds){
            GenericValue siPerson = EntityQuery.use(delegator)
                    .from("TblSignInPerson")
                    .where(EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("noticeId",EntityOperator.EQUALS,noticeId),
                    EntityCondition.makeCondition("staffId",str),
                    EntityCondition.makeCondition("signInPersonType","TblNotice")
            )))
                    .queryOne();
            if(UtilValidate.isEmpty(siPerson)){
                String spId = delegator.getNextSeqId("TblSignInPerson");
                GenericValue signInPerson = delegator.makeValidValue("TblSignInPerson",UtilMisc.toMap("singnInPersonId",spId,"noticeId",noticeId,"staffId",str,"hasSignIn","NS_N","signInPersonStatus","NS_NOT_SEE","signInPersonType","TblNotice"));
                signInPerson.create();
            }
        }
        dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName",entityName,"deptOnly",deptOnly,
                "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                "positionLike",positionLike,"userValue",dataScopeUser,"dataId",noticeId,"userLogin",userLogin));
        msg = "更新成功";
    }else {
        noticeId = delegator.getNextSeqId("TblNotice");
        context.put("noticeId",noticeId);
        context.put("releaseTime",new Date(new java.util.Date().getTime()));
        context.put("releasePerson",userLogin.get("partyId"));
        GenericValue notice = delegator.makeValidValue("TblNotice",context);
        notice.create();
        String[] signInPersonIds = strSignInPerson.split(",");
        for(String str : signInPersonIds){
            GenericValue siPerson = EntityQuery.use(delegator)
                    .from("TblSignInPerson")
                    .where(EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("noticeId",EntityOperator.EQUALS,noticeId),
                        EntityCondition.makeCondition("staffId",str),
                        EntityCondition.makeCondition("signInPersonType","TblNotice")
                    )))
                    .queryOne();
            if(UtilValidate.isEmpty(siPerson)){
                String spId = delegator.getNextSeqId("TblSignInPerson");
                GenericValue signInPerson = delegator.makeValidValue("TblSignInPerson",UtilMisc.toMap("singnInPersonId",spId,"noticeId",noticeId,"staffId",str,"hasSignIn","NS_N","signInPersonStatus","NS_NOT_SEE","signInPersonType","TblNotice"));
                signInPerson.create();
            }
        }
        dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName",entityName,"deptOnly",deptOnly,
                "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                "positionLike",positionLike,"userValue",dataScopeUser,"dataId",noticeId,"userLogin",userLogin));
        msg = "发布成功！";

        BaiDuYunPush.setMessageData(noticeId,context,context.get("hasFeedback"),BaiDuYunPush.NOTICE,delegator,BaiDuYunPush.NOTICE_NAME,context.get("content"),context.get("title"));
    }
    successResult.put("msg",msg);
    return successResult;
}

public Map<String,Object> searchNotice(){

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

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String releasePerson = userLogin.get("partyId");
    String title = context.get("title");
    String department = context.get("releaseDepartment");
    String noticeType = context.get("noticeType");
    String releaseTimeStart = context.get("releaseTimeStart");
    String releaseTimeEnd = context.get("releaseTimeEnd");
    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(releaseTimeStart)&&UtilValidate.isNotEmpty(releaseTimeEnd)){
        timeCondition.add(UtilDateTime.toSqlDate(releaseTimeStart,UtilDateTime.DATE_FORMAT));
        timeCondition.add(UtilDateTime.toSqlDate(releaseTimeEnd,UtilDateTime.DATE_FORMAT));
    }
    List<EntityCondition> condition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(title)){
        condition.add(
                EntityCondition.makeCondition("title",EntityOperator.LIKE,"%"+title+"%")
        );
    }
    if (UtilValidate.isNotEmpty(department)){
        condition.add(
                EntityCondition.makeCondition("department",EntityOperator.LIKE,department)
        );
    }
    if (UtilValidate.isNotEmpty(noticeType)){
        condition.add(
                EntityCondition.makeCondition("noticeType",EntityOperator.EQUALS,noticeType)
        );
    }
    if (UtilValidate.isNotEmpty(timeCondition)){
        condition.add(
                EntityCondition.makeCondition("releaseTime",EntityOperator.BETWEEN,timeCondition)
        );
    }
    condition.add(EntityCondition.makeCondition("releasePerson",EntityOperator.EQUALS,releasePerson));

    EntityListIterator noticeList = EntityQuery.use(delegator)
            .from("NoticeInfo")
            .where(condition)
            .orderBy("releaseTime DESC")
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != noticeList && noticeList.getResultsSizeAfterPartialList() > 0){
        totalCount = noticeList.getResultsSizeAfterPartialList();
        pageList = noticeList.getPartialList(lowIndex, viewSize);
    }
    noticeList.close();
    List<Map> notice = FastList.newInstance();
    for(GenericValue g : pageList){
        Map<String,Object> noticeMap = FastMap.newInstance();
        noticeMap.putAll(g);
        String lastEditPersonName = g.get("lastEditPersonName");
        String lastEditTime = "";
        if(UtilValidate.isNotEmpty(g.get("lastEditTime"))){
            lastEditTime = format.format(g.get("lastEditTime"));
        }
        if(UtilValidate.isNotEmpty(lastEditPersonName)&&UtilValidate.isNotEmpty(lastEditTime)){
            noticeMap.put("lastEditPersonAndDate",lastEditPersonName+"/"+lastEditTime);
        }
        notice.add(noticeMap);
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",notice);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("title",title);
    map.put("department",department);
    map.put("noticeType",noticeType);
    map.put("releaseTimeStart",releaseTimeStart);
    map.put("releaseTimeEnd",releaseTimeEnd);
    successResult.put("returnValue",map);
    return successResult;
}
public Map<String,Object> updateSignInPerson(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = userLogin.get("partyId");
    String singnInPersonId = context.get("singnInPersonId");
    String staffId = context.get("staffId");
    String noticeId = context.get("noticeId");
    String signInStatus = context.get("signInStatus");
    String status = context.get("signInPersonStatus");
    String remark = context.get("remark");
    if(UtilValidate.isEmpty(remark)){
        remark = "";
    }
    if(UtilValidate.isNotEmpty(singnInPersonId)){
        Map<String,Object> param = FastMap.newInstance();
        param.put("singnInPersonId",singnInPersonId);
        Timestamp nowDate = new Timestamp(new java.util.Date().getTime());
        if(status != "NS_NOT_SEE"){
            param.put("signInPersonStatus",status);
            param.put("hasSignIn","NS_Y");
            param.put("signInTime",nowDate);
        }else if(signInStatus == "NS_N"){
            param.put("hasSignIn",signInStatus);
            param.put("signInPersonStatus",status);
        }else if(signInStatus == "NS_Y" || !UtilValidate.isNotEmpty(signInStatus)){
            param.put("signInPersonStatus",status);
            param.put("hasSignIn","NS_Y");
            param.put("signInTime",nowDate);
        }
        if(userLoginId!=staffId){
            GenericValue person = EntityQuery.use(delegator)
                    .from("Person")
                    .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLoginId))
                    .queryOne();
            String strRemark = person.get("fullName")+"代反馈；"+remark;
            param.put("remark",strRemark);
        }else {
            param.put("remark",remark);
        }
        GenericValue signInPerson = delegator.makeValidValue("TblSignInPerson",param);
        signInPerson.store();
        GenericValue updateNotice = delegator.makeValidValue("TblNotice",UtilMisc.toMap("noticeId",noticeId,"lastEditPerson",userLoginId,"lastEditTime",new Timestamp(new java.util.Date().getTime())));
        updateNotice.store();
    }
    successResult.put("msg","更新成功！");
    return successResult;
}

public Map<String,Object> deleteNotice(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String noticeId = context.get("noticeId");
    if(UtilValidate.isNotEmpty(noticeId)){
        GenericValue deleteNotice = delegator.makeValidValue("TblNotice","noticeId",noticeId);
        deleteNotice.removeRelated("TblSignInPerson");
        deleteNotice.remove();
    }
    successResult.put("msg","删除成功！");
    return successResult;
}

public Map<String,Object> saveNoticeFeedback(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String noticeId = context.get("noticeId");
    String feedbackContext = context.get("feedbackContext");

    dispatcher.runSync("saveFeedbackBasic",UtilMisc.toMap("userLogin",userLogin,"feedbackMiddleId",noticeId,"feedbackContext",feedbackContext,"feedbackMiddleType","TblNotice"));
    successResult.put("msg","反馈成功！");
    return successResult;
}

public Map<String,Object> findNoticeFeedback(){
    Map<String,Object> successResult = FastMap.newInstance();
    String noticeId = context.get("noticeId");
    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.put("showChild",false);
    List<EntityCondition> conditionList = FastList.newInstance();

    conditionList.add(EntityCondition.makeCondition("feedbackMiddleId", EntityOperator.EQUALS, noticeId));
    conditionList.add(EntityCondition.makeCondition("feedbackMiddleType", EntityOperator.EQUALS, "TblNotice"));
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
                .orderBy(orderBy)
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
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询工作计划错误");
    } finally {
        if (eli != null) {
            eli.close();
        }
    }
    //------------------------------------------
    return successResult;
}

public Map<String,Object> searchSignInRecord(){

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
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String department = context.get("department");
    Date releaseTimeStart = context.get("releaseTimeStart");
    Date releaseTimeEnd = context.get("releaseTimeEnd");
    String title = context.get("title");
    String noticeType = context.get("noticeType");
    String feedbackDepartment = context.get("feedbackDepartment");
    String hasSignIn = context.get("hasSignIn");
    String status = context.get("status");
    String entityName = "TblNotice";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<EntityCondition> condition1 = FastList.newInstance();
    condition1.add(EntityCondition.makeCondition("staffId",EntityOperator.EQUALS, partyId));
    condition1.add(EntityCondition.makeCondition("signInPersonType",EntityOperator.EQUALS, entityName));
    if(UtilValidate.isNotEmpty(hasSignIn)){
        condition1.add(EntityCondition.makeCondition("hasSignIn",EntityOperator.EQUALS,hasSignIn));
    }
    if(UtilValidate.isNotEmpty(status)){
        condition1.add(EntityCondition.makeCondition("signInPersonStatus",EntityOperator.EQUALS,status));
    }
    if(UtilValidate.isNotEmpty(feedbackDepartment)){
        condition1.add(EntityCondition.makeCondition("departmentId",EntityOperator.EQUALS,feedbackDepartment));
    }
    EntityCondition conditions1 = EntityCondition.makeCondition(condition1);
    List<GenericValue> signList = EntityQuery.use(delegator).select()
            .from("SignInPersonInfo")
            .where(conditions1)
            .queryList();
    List noticeIds = new ArrayList();
    for(GenericValue map : signList){
        noticeIds.add(map.get("noticeId"));
    }
    List<EntityCondition> conditions = FastList.newInstance();
    if((UtilValidate.isNotEmpty(feedbackDepartment)) || (UtilValidate.isNotEmpty(hasSignIn)) || (UtilValidate.isNotEmpty(status))){
        conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noticeId", EntityOperator.IN, noticeIds)));
    }else{
        conditions.add(EntityCondition.makeCondition([EntityCondition.makeCondition("noticeId", EntityOperator.IN, noticeIds),
                                                      EntityCondition.makeCondition("releasePerson", EntityOperator.EQUALS, partyId)], EntityOperator.OR));
    }
    if(UtilValidate.isNotEmpty(department)){
        conditions.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if(UtilValidate.isNotEmpty(title)){
        conditions.add(EntityCondition.makeCondition("title",EntityOperator.LIKE,"%"+title+"%"));
    }
    if(UtilValidate.isNotEmpty(noticeType)){
        conditions.add(EntityCondition.makeCondition("noticeType",EntityOperator.EQUALS,noticeType));
    }
    if(UtilValidate.isNotEmpty(releaseTimeStart)&&UtilValidate.isNotEmpty(releaseTimeEnd)){
        conditions.add(EntityCondition.makeCondition("releaseTime",EntityOperator.BETWEEN,UtilMisc.toList(releaseTimeStart,releaseTimeEnd)));
    }
    EntityCondition condition = null;
    EntityListIterator signInRecordList = null;
    if (UtilValidate.isNotEmpty(conditions)){
        condition = EntityCondition.makeCondition(conditions);
        signInRecordList = EntityQuery.use(delegator)
                .from("NoticeInfo")
                .where(condition)
                .orderBy("releaseTime DESC")
                .queryIterator();
    }else {
        signInRecordList = EntityQuery.use(delegator)
                .from("NoticeInfo")
                .orderBy("releaseTime DESC")
                .queryIterator();
    }
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    List<Map<String,Object>> pageList1 = new ArrayList<Map<String,Object>>();
    if(null != signInRecordList && signInRecordList.getResultsSizeAfterPartialList() > 0){
        totalCount = signInRecordList.getResultsSizeAfterPartialList();
        pageList = signInRecordList.getPartialList(lowIndex, viewSize);

        for(GenericValue genericValue : pageList){
            Map<String,Object> map1 = new HashMap<String,Object>();
            map1.putAll(genericValue);
            long signInCount = delegator.findCountByCondition("SignInRecord",
                    EntityCondition.makeCondition(UtilMisc.toMap("hasSignIn", "NS_Y", "signInPersonType", entityName, "noticeId", map1.get("noticeId"))
                    ),null,null);//已签收人数
            long noSignInCount = delegator.findCountByCondition("SignInRecord",
                    EntityCondition.makeCondition(UtilMisc.toMap("hasSignIn", "NS_N", "signInPersonType", entityName, "noticeId", map1.get("noticeId"))
                    ),null,null);//未签收人数
            long noSeeCount = delegator.findCountByCondition("SignInRecord",
                    EntityCondition.makeCondition(UtilMisc.toMap("signInPersonStatus", "NS_NOT_SEE", "signInPersonType", entityName, "noticeId", map1.get("noticeId"))
                    ),null,null);//未浏览人数
            long seeCount = delegator.findCountByCondition("SignInRecord",
                    EntityCondition.makeCondition(UtilMisc.toMap("signInPersonStatus", "NS_TOUNDERSTAND", "signInPersonType", entityName, "noticeId", map1.get("noticeId"))
                    ),null,null);//浏览中人数
            long understandCount = delegator.findCountByCondition("SignInRecord",
                    EntityCondition.makeCondition(UtilMisc.toMap("signInPersonStatus", "NS_UNDERSTAND", "signInPersonType", entityName, "noticeId", map1.get("noticeId"))
                    ),null,null);//已熟悉人数
            map1.put("signInCount", signInCount);
            map1.put("noSignInCount", noSignInCount);
            map1.put("noSeeCount", noSeeCount);
            map1.put("seeCount", seeCount);
            map1.put("understandCount", understandCount);
//            GenericValue signMap = EntityQuery.use(delegator).select().from("SignInRecord")
//                    .where(UtilMisc.toMap("staffId", partyId, "signInPersonType", entityName, "noticeId", map1.get("noticeId")))
//                    .queryOne();
//            if(null != signMap){
//                if(null != signMap.get("signInTime")){
//                    map1.put("signInTime",format.format(signMap.get("signInTime")));
//                }
//                map1.put("hasSignInDesc", signMap.get("hasSignInDesc"));
//                map1.put("stautsDesc", signMap.get("stautsDesc"));
//                map1.put("departmentName", signMap.get("departmentName"));
//                map1.put("fullName", signMap.get("fullName"));
//                map1.put("positionDesc", signMap.get("positionDesc"));
//            }
            pageList1.add(map1);
        }
    }
    signInRecordList.close();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList1);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("department",department);
    map.put("releaseTimeStart",releaseTimeStart);
    map.put("releaseTimeEnd",releaseTimeEnd);
    map.put("title",title);
    map.put("noticeType",noticeType);
    map.put("feedbackDepartment",feedbackDepartment);
    map.put("hasSignIn",hasSignIn);
    map.put("status",status);
    successResult.put("returnValue",map);
    return successResult;
}
public Map<String, Object> saveNoticeTemplate() {
    String noticeTemplateId = context.get("noticeTemplateId");
    String noticeTemplate = context.get("noticeTemplate");
    String noticeTemplateName = context.get("noticeTemplateName");
    String logicDelete = "N";//初始状态未删除
    context.put("logicDelete", logicDelete);
    GenericValue userLogin = context.get("userLogin");
    java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime());
    java.sql.Timestamp currentTime = new java.sql.Timestamp(currentDate.getTime());
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map map = new HashMap();
    String msg = "添加成功";
    if (UtilValidate.isNotEmpty(noticeTemplateId)){
        msg = "更新成功";
        genericValue = delegator.findByPrimaryKey("TblNoticeTemplate",UtilMisc.toMap("noticeTemplateId",noticeTemplateId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        map.put("noticeTemplateName", noticeTemplateName);
        map.put("noticeTemplateId", noticeTemplateId);
    }else if (UtilValidate.isNotEmpty(noticeTemplate)){
        noticeTemplateId = delegator.getNextSeqId("TblNoticeTemplate");
        context.put("noticeTemplateId", noticeTemplateId);
        context.put("createTime", currentTime);
        context.put("createPerson", userLogin.get("partyId"));
        genericValue = delegator.makeValidValue("TblNoticeTemplate",context);
        genericValue.create();
        map.put("noticeTemplateName", noticeTemplateName);
        map.put("noticeTemplateId", noticeTemplateId);
    }else {
        msg="请输入需要添加的模板";
    }
    map.put("msg", msg);
    successResult.put("data", map);
    return successResult;
}

public Map<String, Object> changeNoticeTemplate(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String noticeTemplateId = context.get("noticeTemplateId");
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(noticeTemplateId)){
        noticeTemplate = delegator.findByPrimaryKey("TblNoticeTemplate", UtilMisc.toMap("noticeTemplateId", noticeTemplateId));
        map.putAll(noticeTemplate);
    }
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> deleteNoticeTemplate(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String noticeTemplateId = context.get("noticeTemplateId");
    String logicDelete = "Y";
    context.put("logicDelete", logicDelete);
    String msg = "";
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(noticeTemplateId)){
        GenericValue NoticeTemplate = delegator.makeValidValue("TblNoticeTemplate","noticeTemplateId",noticeTemplateId);
        NoticeTemplate.setNonPKFields(context);
        NoticeTemplate.store();
        map.put("noticeTemplateId", noticeTemplateId);
        msg = "删除成功";
    }
    map.put("msg", msg);
    successResult.put("data",map);
    return successResult;
}