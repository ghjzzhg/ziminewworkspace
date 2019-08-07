package org.ofbiz.oa

import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String,Object> searchWorkReportList(){
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String loginUserId = userLogin.getString("partyId");

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
    List workReportList = new ArrayList();
    String num = context.get("num")==null?"":context.get("num");
    String name = context.get("name")==null?"":context.get("name");
    String party = context.get("party")==null?"":context.get("party");
    String status = context.get("status")==null?"":context.get("status");
    String type = context.get("type")==null?"":context.get("type");
    String process = context.get("process")==null?"":context.get("process");
    String executor = context.get("executor")==null?"":context.get("executor");
    String leader = context.get("leader")==null?"":context.get("leader");
    List conditionList = addConditionList(userLogin,loginUserId, num, name, party, status, type, process, executor, leader);
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    //判断重复报告是否在限定时间范围内，如果不在，表示开始新一轮，将状态初始化，执行人进度清空
    List<GenericValue> updateReportList = EntityQuery.use(delegator).select().from("WorkReportList").where(condition).queryList();
    if(null != updateReportList && updateReportList.size() > 0){
        for(Map updateWorkReport : updateReportList){
            int flag = verifyRepeat(updateWorkReport);
            if(flag == 0){
                String id = updateWorkReport.get("workReportId");
                delegator.removeByAnd("TblPersonProcessing", UtilMisc.toMap("workReportId", id));
                Map map = new HashMap();
                map.put("workReportId",id);
                map.put("status","WORK_REPORT_STATUS_A");
                Date dateTime = new Date();
                Timestamp time = new Timestamp(dateTime.getTime());
                map.put("statusTime",time);
                GenericValue workReport = delegator.makeValidValue("TblWorkReport", map);
                workReport.store();
            }
        }
    }
    EntityListIterator reportTterator = EntityQuery.use(delegator).select().from("WorkReportList").where(condition).queryIterator();
    if(null != reportTterator && reportTterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = reportTterator.getResultsSizeAfterPartialList();
        List<GenericValue> reportList = reportTterator.getPartialList(lowIndex, viewSize);
        reportTterator.close();
        for (Map workReport : reportList) {
            String workReportId = workReport.get("workReportId")
            Map workReportMap = new HashMap();
            workReportMap.putAll(workReport);
            Date startTime = (Date)workReport.get("startTime");
            Date endTime = (Date)workReport.get("endTime");
            String startDate = formatTime(startTime);
            String endDate = formatTime(endTime);
            workReportMap.put("startDate", startDate);
            String reportType = workReportMap.get("reportType");
//            if("WORK_REPORT_TYPE_A".equals(reportType) || "WORK_REPORT_TYPE_B".equals(reportType) || "WORK_REPORT_TYPE_C".equals(reportType)){
                workReportMap.put("endDate", endDate);
//            }else{
//                workReportMap.put("endDate", "长期");
//            }
            Date nowTime = new Date();
            Calendar cal = Calendar.getInstance();
            //通过Date（util类型）来设定日历时间
            cal.setTime(endTime);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date endTimes = cal.getTime();
            if ((nowTime.getTime() > endTimes.getTime()
                    /*&& !"WORK_REPORT_TYPE_D".equals(reportType)
                    && !"WORK_REPORT_TYPE_E".equals(reportType)
                    && !"WORK_REPORT_TYPE_F".equals(reportType)
                    && !"WORK_REPORT_TYPE_G".equals(reportType)*/
            )
                    || "WORK_REPORT_STATUS_B".equals(workReport.get("status"))
                    || "WORK_REPORT_STATUS_C".equals(workReport.get("status"))) {
                workReportMap.put("flag", 0);
            } else {
                workReportMap.put("flag", 1);
            }
            if(nowTime.getTime() < startTime.getTime()){
                workReportMap.put("flag", 2);
            }
            String frequency = workReportMap.get("reportTypeName");
            String frequencyName = frequency.substring(frequency.indexOf("（") + 1, frequency.indexOf("）"));
            workReportMap.put("frequencyName", frequencyName);

            String reportTypeName = frequency.substring(0, frequency.indexOf("（"));
            workReportMap.put("typeName", reportTypeName);
            //将最新反馈内容放置在map中
            String feedbackInfo = "";
            List<GenericValue> feedbacklist = EntityQuery.use(delegator).select().from("WorkReportFeedback").where(EntityCondition.makeCondition("feedbackMiddleId", workReportId)).orderBy("feedbackTime").queryList();
            if (null != feedbacklist && feedbacklist.size() > 0) {
                GenericValue feedback = feedbacklist.get(feedbacklist.size() - 1);
                String feedbackTime = feedback.get("feedbackTime");
                feedbackTime = feedbackTime.substring(0,feedbackTime.lastIndexOf("."));
                feedbackInfo = feedback.get("fullName") + "/" + feedbackTime;
            }
            workReportMap.put("feedbackInfo", feedbackInfo);

            searchExecutionPlan(workReportId,workReportMap,loginUserId,userLogin);
            List<Map<String,Object>> executionPlanList = (List<Map<String,Object>>)workReportMap.get("executionPlanList");
            int submit = 0;
            int noSubmit = 0;
            for(Map<String,Object> executionPlanMap : executionPlanList){
                if("WORK_REPORT_PLAN_C".equals(executionPlanMap.get("processing").toString())){
                    ++submit;
                }else{
                    ++noSubmit;
                }
            }
            workReportMap.put("submit",submit);
            workReportMap.put("noSubmit",noSubmit);
            workReportList.add(workReportMap);
        }
    }
    reportTterator.close();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("num",num);
    viewData.put("name",name);
    viewData.put("party",party);
    viewData.put("status",status);
    viewData.put("type",type);
    viewData.put("process",process);
    viewData.put("executor",executor);
    viewData.put("leader",leader);
    viewData.put("workReportList",workReportList)
    successResult.put("data",viewData);
    return successResult;
}

/**
 * 拼接查询字段
 * @param userLogin
 * @param partyId
 * @param num
 * @param name
 * @param party
 * @param status
 * @param type
 * @param process
 * @param executor
 * @param leader
 * @return
 */
public List addConditionList(GenericValue userLogin, String partyId, String num, String name, String party, String status, String type, String process, String executor, String leader){
    List conditionList = new ArrayList();
    //查询当前登陆人可查看的工作报告编号
    Map<String,Object> partySuccessMap = dispatcher.runSync("verifyViewPermissions",UtilMisc.toMap("entityName","TblWorkReport","isSelect",false,"partyId",partyId,"userLogin",userLogin));
    Map<String,Object> partyInfo = (HashMap<String,Object>)partySuccessMap.get("data");
    List<Map<String,Object>> entityDataList = new ArrayList<Map<String,Object>>();
    if(null != partyInfo){
        entityDataList = (List<FastMap<String,Object>>)partyInfo.get("entityDataList");
}
    List workList = new ArrayList();
    for(Map<String ,Object> map : entityDataList){
        workList.add(map.get("dataId"));
    }
    //查询项目主管中是否存在当前登陆人
    List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblWorkreportParty").where(UtilMisc.toMap("partyId",partyId)).queryList();
    for(GenericValue partyData : partyList){
        workList.add(partyData.get("workReportId"));
    }
    //发布人
    List<GenericValue> inputPersonList = EntityQuery.use(delegator).select().from("TblWorkReport").where(UtilMisc.toMap("inputPerson",partyId)).queryList();
    for(GenericValue partyData : inputPersonList){
        workList.add(partyData.get("workReportId"));
    }

    List executorId = new ArrayList();
    if(UtilValidate.isNotEmpty(executor)){
        Map<String,Object> executorSuccessMap = dispatcher.runSync("verifyViewPermissions",UtilMisc.toMap("entityName","TblWorkReport","isSelect",false,"partyId",executor,"userLogin",userLogin));
        HashMap<String,Object> executorInfo = (HashMap<String,Object>)executorSuccessMap.get("data");
        List<HashMap<String,Object>> executorEntityDataList = new ArrayList<HashMap<String,Object>>();
        if(null != executorInfo){
            executorEntityDataList = (List<HashMap<String,Object>>)executorInfo.get("entityDataList");
        }

        for(Map<String ,Object> map : executorEntityDataList){
            executorId.add(map.get("dataId"));
        }
    }

    List leaderId = new ArrayList();
    if(UtilValidate.isNotEmpty(leader)){
        List<GenericValue> leaderList = EntityQuery.use(delegator).select().from("TblWorkreportParty").where(UtilMisc.toMap("partyId",leader)).queryList();
        for(GenericValue partyData : leaderList){
            leaderId.add(partyData.get("workReportId"));
        }
    }
    if(UtilValidate.isNotEmpty(executorId) || UtilValidate.isNotEmpty(executor)){
        workList = setSearchWrokId(workList,executorId);
    }
    if(UtilValidate.isNotEmpty(leaderId) || UtilValidate.isNotEmpty(leader)){
        workList = setSearchWrokId(workList,leaderId);
    }
    conditionList.add(EntityCondition.makeCondition("workReportId", EntityOperator.IN, workList));
    if(null != num && num.length() > 0){
        conditionList.add(EntityCondition.makeCondition("reportNumber", EntityOperator.LIKE, "%"+num+"%"));
    }
    if(null != name && name.length() > 0){
        conditionList.add(EntityCondition.makeCondition("reportTitle", EntityOperator.LIKE, "%"+name+"%"));
    }
    if(null != party && party.length() > 0){
        conditionList.add(EntityCondition.makeCondition("inputPerson", EntityOperator.EQUALS, party));
    }
    if(null != status && status.length() > 0){
        conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, status));
    }
    if(null != type && type.length() > 0){
        conditionList.add(EntityCondition.makeCondition("reportType", EntityOperator.EQUALS, type));
    }
    if(null != process && process.length() > 0){
        conditionList.add(EntityCondition.makeCondition("plan", EntityOperator.EQUALS, process));
    }
    return conditionList;
}

public List<String> setSearchWrokId(List<String> oneWorkIdList, List<String> twoWorkIdList){
    List<String> searchWorkId = new ArrayList<String>()
    if(UtilValidate.isNotEmpty(twoWorkIdList) && UtilValidate.isNotEmpty(oneWorkIdList)){
        for(String workId : twoWorkIdList){
            if(oneWorkIdList.contains(workId)){
                searchWorkId.add(workId);
            }
        }
    }
    return searchWorkId;
}

/**
 * 格式化时间
 * @param date
 * @return
 */
public String formatTime(Object date){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String formatDate = "";
    if(date instanceof  Date){
        formatDate = format.format(date);
    }
    return formatDate;
}

public int verifyRepeat(Map workReportMap){
    int flag = 0;
    String reportType = workReportMap.get("reportType");
    Timestamp timestamp = (Timestamp)workReportMap.get("statusTime");
    Date statusTime = timestamp;
    Date startTime = new Date();
    Date endTime = new Date();
    if("WORK_REPORT_TYPE_D".equals(reportType)){  //季度
        startTime = getCurrentQuarterStartTime();
        endTime = getCurrentQuarterEndTime();
    }else if("WORK_REPORT_TYPE_E".equals(reportType)){//月
        startTime = getCurrentMonthStartTime();
        endTime = getCurrentMonthEndTime();
    }else if("WORK_REPORT_TYPE_F".equals(reportType)){//周
        startTime = getCurrentWeekDayStartTime();
        endTime = getCurrentWeekDayEndTime();
    }else if("WORK_REPORT_TYPE_G".equals(reportType)){//日
        startTime = getCurrentDayStartTime();
        endTime = getCurrentDayEndTime();
    }else{
        flag = 3;//不是重复报告
    }
    if(statusTime.getTime() > startTime.getTime() && statusTime.getTime() < endTime.getTime() &&  flag != 3){
        flag = 1;//在时间范围指内
    }else if(flag == 3){
        flag = 2
    }else{
        flag = 0;//在时间范围之外
    }
    return flag;
}

/**
 * 获得本天的开始时间，即2012-01-01 00:00:00
 *
 * @return
 */
public static Date getCurrentDayStartTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    Date now = new Date();
    try {
        now = shortSdf.parse(shortSdf.format(now));
    } catch (Exception e) {
        e.printStackTrace();
    }
    return now;
}

/**
 * 获得本天的结束时间，即2012-01-01 23:59:59
 *
 * @return
 */
public static Date getCurrentDayEndTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date now = new Date();
    try {
        now = longSdf.parse(shortSdf.format(now) + " 23:59:59");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return now;
}

/**
 * 获得本周的第一天，周一
 *
 * @return
 */
public static Date getCurrentWeekDayStartTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar c = Calendar.getInstance();
    try {
        int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
        c.add(Calendar.DATE, -weekday);
        c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00"));
    } catch (Exception e) {
        e.printStackTrace();
    }
    return c.getTime();
}

/**
 * 获得本周的最后一天，周日
 *
 * @return
 */
public static Date getCurrentWeekDayEndTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar c = Calendar.getInstance();
    try {
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, 8 - weekday);
        c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59"));
    } catch (Exception e) {
        e.printStackTrace();
    }
    return c.getTime();
}

/**
 * 获得本月的开始时间
 *
 * @return
 */
public static Date getCurrentMonthStartTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    Date now = null;
    try {
        c.set(Calendar.DATE, 1);
        now = shortSdf.parse(shortSdf.format(c.getTime()));
    } catch (Exception e) {
        e.printStackTrace();
    }
    return now;
}

/**
 * 当前月的结束时间
 *
 * @return
 */
public static Date getCurrentMonthEndTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar c = Calendar.getInstance();
    Date now = null;
    try {
        c.set(Calendar.DATE, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return now;
}

/**
 * 当前季度的开始时间
 *
 * @return
 */
public static Date getCurrentQuarterStartTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar c = Calendar.getInstance();
    int currentMonth = c.get(Calendar.MONTH) + 1;
    Date now = null;
    try {
        if (currentMonth >= 1 && currentMonth <= 3)
            c.set(Calendar.MONTH, 0);
        else if (currentMonth >= 4 && currentMonth <= 6)
            c.set(Calendar.MONTH, 3);
        else if (currentMonth >= 7 && currentMonth <= 9)
            c.set(Calendar.MONTH, 4);
        else if (currentMonth >= 10 && currentMonth <= 12)
            c.set(Calendar.MONTH, 9);
        c.set(Calendar.DATE, 1);
        now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return now;
}

/**
 * 当前季度的结束时间
 *
 * @return
 */
public static Date getCurrentQuarterEndTime() {
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar c = Calendar.getInstance();
    int currentMonth = c.get(Calendar.MONTH) + 1;
    Date now = null;
    try {
        if (currentMonth >= 1 && currentMonth <= 3) {
            c.set(Calendar.MONTH, 2);
            c.set(Calendar.DATE, 31);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            c.set(Calendar.MONTH, 5);
            c.set(Calendar.DATE, 30);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            c.set(Calendar.MONTH, 8);
            c.set(Calendar.DATE, 30);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
        }
        now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return now;
}

/**
 * 增加或修改工作报告
 * @return
 */
public Map<String, Object> editWorkReport() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "保存成功";
    String workReportId = context.get("workReportId");
    context.put("description",context.get("descriptionHtml"));
    if(null != workReportId && UtilValidate.isNotEmpty(workReportId)){
        GenericValue report = EntityQuery.use(delegator).select().from("TblWorkReport").where(EntityCondition.makeCondition("workReportId",workReportId)).queryOne();
        if(null != report){
            Date dateTime = new Date();
            Timestamp time = new Timestamp(dateTime.getTime());
            String type = report.get("status")
            if(!type.equals(report.get("status").toString())){
                context.put("statusTime",report.get("statusTime"));
            }else{
                context.put("statusTime",time);
            }
            msg = "更新成功";
            GenericValue workReport = delegator.makeValidValue("TblWorkReport", context);
            workReport.store();
            saveWorkreportParty(context,workReportId,"update");
            this.saveExecutor(context,workReportId,"update");
        }
    }else{
        workReportId = delegator.getNextSeqId("TblWorkReport").toString();//获取主键ID
        Date dateTime = new Date();
        Timestamp  time = new Timestamp(dateTime.getTime());
        context.put("statusTime",time);
        context.put("workReportId",workReportId);
        context.put("plan","WORK_REPORT_PLAN_A");
        context.put("status","WORK_REPORT_STATUS_A")
        GenericValue workReport = delegator.makeValidValue("TblWorkReport", context);
        workReport.create();
        saveWorkreportParty(context,workReportId,"save");
        saveExecutor(context,workReportId,"save");
    }
    String fileId = context.get("fileId");
    List<String> newFileList = new ArrayList<String>();
    if(null != fileId && !"".equals(fileId)){
        String[] files = fileId.split(",");
        for(String file : files){
            newFileList.add(file);
        }
    }
    List<String> oldFileList = new ArrayList<String>();
    List<GenericValue> workAccessFile = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", workReportId, "entityName", "TblWorkReport"))).queryList();
    if(null != workAccessFile && workAccessFile.size() > 0){
        for(GenericValue workAccess : workAccessFile){
            oldFileList.add(workAccess.get("accessoryId").toString());
        }
    }
    saveWorkFile(workReportId,oldFileList,newFileList);
    successResult.put("data", UtilMisc.toMap("msg", msg, "workReportId", workReportId));
    return successResult;
}

public void saveWorkFile(String workId,List<String> oldFileList,List<String> newFileList){
    for(String fileId:newFileList) {
        if (!oldFileList.contains(fileId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            String fileScopeId = delegator.getNextSeqId("TblFileScope");
            map.put("fileScopeId", fileScopeId);
            map.put("entityName", "TblWorkReport");
            map.put("dataId", workId);
            map.put("accessoryId", fileId);
            GenericValue accessory = delegator.makeValidValue("TblFileScope", map);
            accessory.create();
        }
    }
    for(String fileid:oldFileList){
        if(!newFileList.contains(fileid)) {
            delegator.removeByAnd("TblFileScope", UtilMisc.toMap("entityName", "TblWorkReport", "dataId", workId, "accessoryId", fileid));
            GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileid)).queryOne();
            if (null != fileData && "ATTACHMENT_FILE".equals(fileData.get("dataResourceTypeId").toString())) {
                File file = new File(fileData.get("objectInfo").toString() + fileData.get("dataResourceName").toString());
                if (file.exists()) {
                    file.delete();
                }
                delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileid));
            }
        }
    }
}

/**
 * 保存项目主管
 * @param testStaffSelect
 * @param workReportId
 * @param flag
 */
public void saveWorkreportParty(Map<String,Object> testStaffSelect,String workReportId,String flag){
    String leaderList = testStaffSelect.get("testStaffSelect");
    String[] leaders = leaderList.split(",");
    if("update".equals(flag)) {
        delegator.removeByAnd("TblWorkreportParty", UtilMisc.toMap("workReportId", workReportId));
    }
    if(null != leaders && leaders.size() > 0){
        for(String partyId : leaders){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("workReportId",workReportId);
            map.put("partyId",partyId);
            GenericValue accessory = delegator.makeValidValue("TblWorkreportParty", map);
            accessory.create();
        }
    }
}

/**
 * 取得执行人数组，用于保存执行人
 * @param map
 * @param workReportId
 */
public void saveExecutor(Map map,String workReportId, String flag){
    String[] dataScope_dept_only = splitString(map.get("dataScope_dept_only").toString());
    String[] dataScope_dept_like = splitString(map.get("dataScope_dept_like").toString());
    String[] dataScope_level_only = splitString(map.get("dataScope_level_only").toString());
    String[] dataScope_level_like = splitString(map.get("dataScope_level_like").toString());
    String[] dataScope_position_only = splitString(map.get("dataScope_position_only").toString());
    String[] dataScope_position_like = splitString(map.get("dataScope_position_like").toString());
    String[] dataScope_user = splitString(map.get("dataScope_user").toString());
    if("update".equals(flag)) {
        Map<String,Object> conditionMap = new HashMap<String,Object>();
        conditionMap.put("dataId",workReportId);
        conditionMap.put("entityName","TblWorkReport");
        delegator.removeByAnd("TblDataScope", conditionMap);
    }
    saveWorkReportExecutor(dataScope_dept_only,workReportId,"SCOPE_DEPT_ONLY");
    saveWorkReportExecutor(dataScope_dept_like,workReportId,"SCOPE_DEPT_LIKE");
    saveWorkReportExecutor(dataScope_level_only,workReportId,"SCOPE_LEVEL_ONLY");
    saveWorkReportExecutor(dataScope_level_like,workReportId,"SCOPE_LEVEL_LIKE");
    saveWorkReportExecutor(dataScope_position_only,workReportId,"SCOPE_POSITION_ONLY");
    saveWorkReportExecutor(dataScope_position_like,workReportId,"SCOPE_POSITION_LIKE");
    saveWorkReportExecutor(dataScope_user,workReportId,"SCOPE_USER");
}

/**
 * 保存执行人
 * @param ids
 * @param workReportId
 * @param type
 */
public void saveWorkReportExecutor(String[] ids,String workReportId,String type){
    if(null != ids && ids.size() > 0){
        for(String id : ids){
            if(null != id && !"null".equals(id) && !"".equals(id)){
                String scopeId = delegator.getNextSeqId("TblDataScope").toString();//获取主键ID
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("scopeId",scopeId);
                map.put("dataId",workReportId);
                map.put("dataAttr","view");
                map.put("scopeType",type);
                map.put("scopeValue",id);
                map.put("entityName","TblWorkreport")
                GenericValue executor = delegator.makeValidValue("TblDataScope", map);
                executor.create();
            }
        }
    }
}

/**
 * 分隔执行人，用于存储
 * @param dataScope
 * @return
 */
public String[] splitString(String dataScope){
    String[] executor = null;
    if(null != dataScope && dataScope.length() > 0 && !"null".equals(dataScope)){
        executor = dataScope.split(",");
    }
    return executor;
}

/**
 * 根据ID查找工作报告数据：编辑页面
 * @return
 */
public Map<String,Object> searchWorkReportById(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String workReportId = parameters.get("workReportId");
    String partyId = userLogin.getString("partyId");
    Map<String ,Object> workReportMap = new HashMap<String,Object>();
    //判断是否有报告编号：编辑页面
    if(null != workReportId && UtilValidate.isNotEmpty(workReportId)){
        //根据ID查找报告
        workReport = EntityQuery.use(delegator).select().from("WorkReportList").where(EntityCondition.makeCondition("workReportId",workReportId)).queryOne();
        workReportMap.putAll(workReport);
        //分离反馈通知人
        Date startTime = (Date)workReportMap.get("startTime");
        Date endTime = (Date)workReportMap.get("endTime");
        String startDate = formatTime(startTime);
        String endDate = formatTime(endTime);
        workReportMap.put("startDate", startDate);
        String reportType = workReportMap.get("reportType");
        if("WORK_REPORT_TYPE_A".equals(reportType) || "WORK_REPORT_TYPE_B".equals(reportType) || "WORK_REPORT_TYPE_C".equals(reportType)){
            workReportMap.put("endDate", endDate);
        }else{
            workReportMap.put("endDate", "长期");
        }
        String noticePerson = workReportMap.get("noticePerson");
        String isPerson = "";
        String isLeader = "";
        if(null != noticePerson && !"".equals(noticePerson)){
            isPerson = noticePerson.substring(0,noticePerson.indexOf(","));
            isLeader = noticePerson.substring(noticePerson.indexOf(",")+1,noticePerson.length());
        }
        workReportMap.put("isPerson",isPerson);
        workReportMap.put("isLeader",isLeader);
        //查找项目主管
        Map map = searchLeaderListById(workReportId);
        workReportMap.put("leaderNames", map.get("leaderNames"));
        workReportMap.put("leaderIds", map.get("leaderIds"));
        //查找执行人
        List<GenericValue> scopeList = EntityQuery.use(delegator).select().from("TblDataScope").where(UtilMisc.toMap("dataId",workReportId)).queryList();
        String allName = ""
        String partyIdList = "";
        if(UtilValidate.isNotEmpty(scopeList)){
            for (GenericValue scope : scopeList){
                partyIdList += scope.get("scopeValue").toString();
                String scopeType = scope.get("scopeType");
                if(scopeType.equals("SCOPE_DEPT_ONLY") || scopeType.equals("SCOPE_DEPT_LIKE")){
                    GenericValue group = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",scope.get("scopeValue"))).queryOne();
                    allName += group.get("groupName").toString() + " ";
                }else if(scopeType.equals("SCOPE_USER")){
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",scope.get("scopeValue"))).queryOne();
                    allName += genericValue.get("fullName").toString() + " ";
                }else{
                    List<GenericValue> entitys = EntityQuery.use(delegator).select().from("PersonByRoleId").where(UtilMisc.toMap("roleTypeId",scope.get("scopeValue"))).queryList();
                    allName += entitys.get("fullName").toString() + " ";
                }
            }
        }
        workReportMap.put("allName",allName);
        workReportMap.put("partyIdList",partyIdList)
        //查找反馈记录，状态
        Map conditionMap = new HashMap();
        conditionMap.put("feedbackMiddleId",workReportId);
        conditionMap.put("feedbackPerson",partyId);
        List<GenericValue> feedbacklist = EntityQuery.use(delegator).select().from("WorkReportFeedback").where(EntityCondition.makeCondition(conditionMap)).orderBy("feedbackTime").queryList();
        List<GenericValue> feedbacklist1 = EntityQuery.use(delegator).select().from("WorkReportFeedback").where(UtilMisc.toMap("feedbackMiddleId", workReportId,"processing","WORK_REPORT_PLAN_C")).orderBy("feedbackTime").queryList();
        if(null != feedbacklist && feedbacklist.size() > 0){
            GenericValue feedback = feedbacklist.get(feedbacklist.size()-1);
            typeId = feedback.get("processing");
        }else{
            typeId = "WORK_REPORT_PLAN_A";
        }
        workReportMap.put("processingTypeId",typeId);
        GenericValue personProcessingMap =  EntityQuery.use(delegator).select().from("TblPersonProcessing").where(UtilMisc.toMap("workReportId", workReportId,"reportPerson",partyId)).queryOne();
        if(UtilValidate.isNotEmpty(personProcessingMap)){
            workReportMap.put("personProcessing",personProcessingMap.get("processing"));
        }else{
            workReportMap.put("personProcessing","WORK_REPORT_PLAN_A");
        }
        String viewType = context.get("viewType");
        Map<String,Object> partySuccessMap = dispatcher.runSync("verifyViewPermissions",UtilMisc.toMap("entityName","TblWorkReport","entityId",workReportId,"isSelect",true,"partyId",partyId,"userLogin",userLogin));
        FastMap<String,Object> partyInfo = (FastMap<String,Object>)partySuccessMap.get("data");
        List<String> tempList = new ArrayList<String>();
        if(null != partyInfo){
            List<FastMap<String,Object>> entityDataList = (List<FastMap<String,Object>>)partyInfo.get("entityDataList");
            if(null != entityDataList && entityDataList.size() > 0) {
                for(Map<String, Object> maps : entityDataList){
                    List<Map<String, Object>> person = (List<Map<String, Object>>)maps.get("partyList");
                    for(Map<String, Object> personMap : person){
                        tempList.add(personMap.get("partyId"));
                    }
                }
            }
        }
        if(UtilValidate.isNotEmpty(tempList)){
            if(tempList.contains(partyId))
            {
                workReportMap.put("workReportJurisdiction","0");
            } else {
                workReportMap.put("workReportJurisdiction","1");
            }
        }
        if(null != viewType && "edit".equals(viewType)){
            searchFeedback(workReportId,workReportMap.get("inputPerson").toString(),workReportMap,context);
        }else{
            //查询执行人进度
            searchExecutionPlan(workReportId,workReportMap,partyId,userLogin);
        }

            List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", workReportId, "entityName", "TblWorkReport"))).queryList();
        String fileIds = "";
        if(null != genericValueList && genericValueList.size() > 0){
            for(GenericValue genericValue : genericValueList){
                fileIds = fileIds + genericValue.get("accessoryId") + ",";
            }
        }
        if(!"".equals(fileIds)){
        fileIds = fileIds.substring(0,fileIds.length() - 1);
            context.put("files",fileIds);
            Map data = runService("searchFileByIds", dispatcher.getDispatchContext().getModelService("searchFileByIds").makeValid(context, ModelService.IN_PARAM));
            Map filemap = (Map)data.get("data");
            workReportMap.put("fileIds",filemap.get("fileIds"));
            workReportMap.put("fileList",filemap.get("fileList"));
        }

    }
    successResult.put("workReportMap", workReportMap);
    return successResult;
}

/**
 * 查询执行人进度和最新反馈
 * @param workReportId
 * @return
 */
public void searchExecutionPlan(String workReportId,Map<String,Object> ePlanMap,String party,GenericValue userLogin){
    Map<String,Object> partySuccessMap = dispatcher.runSync("verifyViewPermissions",UtilMisc.toMap("entityName","TblWorkReport","entityId",workReportId,"isSelect",true,"partyId",party,"userLogin",userLogin));
    FastMap<String,Object> partyInfo = (FastMap<String,Object>)partySuccessMap.get("data");
    List<Map<String,Object>> personIdList = new ArrayList<Map<String,Object>>()
    if(null != partyInfo){
        List<FastMap<String,Object>> entityDataList = (List<FastMap<String,Object>>)partyInfo.get("entityDataList");
        if(null != entityDataList && entityDataList.size() > 0) {
            for(Map<String, Object> map : entityDataList){
                List<Map<String, Object>> person = (List<Map<String, Object>>)map.get("partyList");
                for(Map<String, Object> personMap : person){
                    personIdList.add(personMap);
                }
//                personIdList.add(map.get("partyList"));
            }
//            personIdList = (List<Map<String, Object>>) entityDataList.get(0).get("partyList");
        }
    }
    List<Map<String,Object>> executionList = new ArrayList<Map<String,Object>>();
    if(null != personIdList && personIdList.size() > 0){
        for(Map<String,Object> person : personIdList){
            Map<String,Object> map = new HashMap<String,Object>();
            Map<String,Object> executionMap = new HashMap<String,Object>();
            String partyId = person.get("partyId");
            map.get("workReportId",workReportId);
            map.get("reportPerson",person.get("partyId"));
            GenericValue executionPlans = EntityQuery.use(delegator).select().from("PersonProcessList").where(map).queryOne();
            if(null == executionPlans || executionPlans.size() <= 0){
                executionMap.put("workReportId",workReportId);
                executionMap.put("reportPerson",partyId);
                executionMap.put("fullName",person.get("fullName"));
                executionMap.put("processing","WORK_REPORT_PLAN_A");
                executionMap.put("description","未提交");
            }else{
                executionMap.putAll(executionPlans);
            }
            Map conditionMap = new HashMap();
            conditionMap.put("feedbackMiddleId",workReportId);
            conditionMap.put("feedbackPerson",partyId);
            List<GenericValue> feedbacklist = EntityQuery.use(delegator).select().from("WorkReportFeedback").where(EntityCondition.makeCondition(conditionMap)).orderBy("feedbackTime").queryList();
            String personAndTime;
            String feedbackInfo;
            if(null != feedbacklist && feedbacklist.size() > 0){
                GenericValue feedback = feedbacklist.get(feedbacklist.size()-1);
                personAndTime = feedback.get("fullName") + "/" + feedback.get("feedbackTime");
                feedbackInfo = feedback.get("feedbackContext");
                if(feedbackInfo.length() > 6){
                    feedbackInfo = feedbackInfo.substring(0,6) + "...";
                }
            }else{
                personAndTime = "-";
                feedbackInfo = "-";
            }
            executionMap.put("personAndTime",personAndTime);
            executionMap.put("feedbackInfo",feedbackInfo);
            executionMap.put("workReportId",workReportId)
            executionList.add(executionMap);

        }
    }
    ePlanMap.put("executionPlanList",executionList);
    ePlanMap.put("workReportId",workReportId);
}

public List<String> setIds(String[] ids,List<String> personIdList){
    Set idList = new HashSet();
    if(null != ids && ids.size() > 0){
        for(String id : ids){
            idList.add(id);
        }
    }
    if(null != idList && idList.size() > 0){
        for(String personid :idList){
            personIdList.add(personid);
        }
    }
    return personIdList;
}

/**
 * 根据工作报告ID查找项目主管
 * @param workReportId
 * @return
 */
public Map<String,Object> searchLeaderListById(String workReportId){
    Map<String,Object> workReportMap = new HashMap<String,Object>();
    List<GenericValue>  leaderList = EntityQuery.use(delegator).select().from("LeaderByWorkId").where(EntityCondition.makeCondition("workReportId",workReportId)).queryList();
    String leaderNames ="";
    String leaderIds = "";
    if(null != leaderList && leaderList.size() > 0){
        for (GenericValue leader : leaderList){
            leaderNames = leaderNames + " " + leader.get("fullName").toString();
            leaderIds = leaderIds + "," +  leader.get("partyId").toString();
        }
        leaderNames = leaderNames.substring(leaderNames.indexOf(",")+1 ,leaderNames.length());
        leaderIds = leaderIds.substring(leaderIds.indexOf(",")+1 ,leaderIds.length());
    }
    workReportMap.put("leaderNames", leaderNames);
    workReportMap.put("leaderIds", leaderIds);
    return  workReportMap;
}

/**
 * 查找执行人ID
 * @param workRepoerId
 * @return
 */
public List<GenericValue> searchExecutorList(String workRepoerId,String flag){
    Map<String,Object> conditionMap = new HashMap<String,Object>();
    conditionMap.put("dataId",workRepoerId);
    conditionMap.put("entityName","TblWorkReport");
    if(null != flag && flag.length() > 0){
        conditionMap.put("scopeType",flag);
    }
    List<GenericValue> executorList = EntityQuery.use(delegator).select().from("TblDataScope").where(EntityCondition.makeCondition(conditionMap)).queryList();
    return executorList;
}


/**
 * 增加个人反馈信息，进度
 * @return
 */
public Map<String,Object> addReportFeedback(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String workReportId = parameters.get("workReportId");
    String partyId = userLogin.getString("partyId");
    //增加报告进度
    String processId = delegator.getNextSeqId("TblPersonProcessing").toString();//获取主键ID
    Map<String,Object> processMap = new HashMap<String,Object>();
    Map<String,Object> feedbackMap = new HashMap<String,Object>();
    String type = context.get("feedtype");
    String processing = context.get("processing");
    if(null != type && !"FEEDBACK_TYPE_A".equals(type)){
        processType = processing;
    }else{
        processType = context.get("processingTypeId");
    }
    feedbackMap.put("processing",processType);
    processMap.put("processing",processType);
    Map conditionMap = new  HashMap();
    conditionMap.put("workReportId",workReportId);
    conditionMap.put("reportPerson",partyId);
    GenericValue personProcessingModel = EntityQuery.use(delegator).select().from("TblPersonProcessing").where(EntityCondition.makeCondition(conditionMap)).queryOne();
    if(null != personProcessingModel && personProcessingModel.size() > 0){
        Map process = new  HashMap();
        process.put("id",personProcessingModel.get("id").toString());
        process.put("processing",processType);
        GenericValue workReport = delegator.makeValidValue("TblPersonProcessing", process);
        workReport.store();
    }else{
        processMap.put("workReportId",workReportId);
        processMap.put("reportPerson",partyId);
        processMap.put("id",processId);
        GenericValue personProcessing = delegator.makeValidValue("TblPersonProcessing", processMap);
        personProcessing.create();
    }

    //增加反馈信息
    String feedbackId = delegator.getNextSeqId("TblFeedback").toString();//获取主键ID
    feedbackMap.put("feedbackId",feedbackId);
    feedbackMap.put("feedbackTime",context.get("feedbackTime"));
    feedbackMap.put("feedbackPerson",partyId);
    feedbackMap.put("actualFeedbackPerson",partyId);
    feedbackMap.put("feedbackContext",context.get("template"));
    feedbackMap.put("permission",context.get("permission"));
    feedbackMap.put("feedbackType",context.get("feedtype"));

    //增加报告和反馈关联表信息
    GenericValue feedback = delegator.makeValidValue("TblFeedback", feedbackMap);
    feedback.create();
    Map<String,Object> feedbackMiddleMap = new HashMap<String,Object>();
    feedbackMiddleMap.put("feedbackMiddleId",workReportId);
    feedbackMiddleMap.put("feedbackId",feedbackId);
    feedbackMiddleMap.put("feedbackMiddleType","Tbl_Work_Report");
    GenericValue feedbackMiddle = delegator.makeValidValue("TblFeedbackMiddle", feedbackMiddleMap);
    feedbackMiddle.create();
    String msg = "保存成功";
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

/**
 * 删除工作报告
 * 删除项目主管、个人报告进度，反馈信息，执行人，文件
 * @return
 */
public Map<String,Object> delWorkReport(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String workReportId = parameters.get("workReportId");
    //根据报告ID删除项目主管
    delegator.removeByAnd("TblWorkreportParty",UtilMisc.toMap("workReportId",workReportId));
    //根据报告ID删除附件
    List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId",workReportId, "entityName", "TblWorkReport"))).queryList();
    delegator.removeByAnd("TblFileScope",UtilMisc.toMap("dataId",workReportId, "entityName", "TblWorkReport"));
    if(UtilValidate.isNotEmpty(genericValueList)){
        for(GenericValue genericValue : genericValueList){
            GenericValue data = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",genericValue.get("accessoryId"))).queryOne();
            if("ATTACHMENT_FILE".equals(data.get("dataResourceTypeId").toString())) {
                File file = new File(data.get("objectInfo").toString() + data.get("dataResourceName").toString());
                if (file.exists()) {
                    file.delete();
                }
                delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", data.get("dataResourceId")));
            }
        }
    }
    //根据报告ID删除个人报告进度
    delegator.removeByAnd("TblPersonProcessing",UtilMisc.toMap("workReportId",workReportId));
    //根据报告ID和表名查询出反馈主键ID
    List list = new ArrayList();
    list.add(EntityCondition.makeCondition("feedbackMiddleId", EntityOperator.EQUALS, workReportId));
    list.add(EntityCondition.makeCondition("feedbackMiddleType", EntityOperator.EQUALS, "Tbl_Work_Report"));
    EntityConditionList FeedbackMiddleCondition = EntityCondition.makeCondition(UtilMisc.toList(list));
    List<GenericValue> feedbackMiddleList = delegator.findList("TblFeedbackMiddle", FeedbackMiddleCondition, null, null, null, false);
    List conditionList = new ArrayList();
    List feedbackIds = new ArrayList();
    for(GenericValue feedbackMiddle : feedbackMiddleList){
        feedbackIds.add(feedbackMiddle.get("feedbackId"));
    }
    //根据主键列表删除反馈关联表信息
    delegator.removeByAnd("TblFeedbackMiddle",UtilMisc.toMap("feedbackMiddleId",workReportId,"feedbackMiddleType","Tbl_Work_Report"));
    conditionList.add(EntityCondition.makeCondition("feedbackId", EntityOperator.IN, feedbackIds));
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    workLogMapList = delegator.removeByCondition("TblFeedback",condition);
    //删除执行人
    Map<String,Object> conditionMap = new HashMap<String,Object>();
    conditionMap.put("dataId",workReportId);
    conditionMap.put("entityName","TblWorkReport");
    delegator.removeByAnd("TblDataScope", conditionMap);
    //删除报告信息
    delegator.removeByAnd("TblWorkReport",UtilMisc.toMap("workReportId",workReportId));
    String msg = "删除成功";
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String,Object> searchFeedbackList(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map<String ,Object> workReportMap = new HashMap<String,Object>();
    String workReportId = context.get("workReportId");
    GenericValue workReport = EntityQuery.use(delegator).select().from("WorkReportList").where(EntityCondition.makeCondition("workReportId",workReportId)).queryOne();
    searchFeedback(workReportId,workReport.get("inputPerson").toString(),workReportMap,context);
    successResult.put("workReportMap",  workReportMap);
    return successResult;
}


/**
 * 查询反馈信息
 * @param workReportId
 * @return
 */
public void searchFeedback(String workReportId,String planPerson, Map workReportMap,Map context){
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.getString("partyId");
    Map leaderMap =  searchLeaderListById(workReportId);
    int viewIndex;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;
    String leaderIds = leaderMap.get("leaderIds");
    String personId = context.get("personId");
    Map conditionMap = new HashMap();
    conditionMap.put("feedbackMiddleId",workReportId);
    if(null != personId && personId.length() > 0){
        conditionMap.put("feedbackPerson",personId);
    }
    List<Map<String,Object>> feedbacklist = new ArrayList<Map<String,Object>>();
    List list = new ArrayList();
    if(null != workReportId && workReportId.length() > 0){
    EntityListIterator feedbacks =  EntityQuery.use(delegator).select()
                .from("WorkReportFeedback")
                .where(conditionMap)
                .cursorScrollInsensitive()
                .fetchSize(highIndex)
                .queryIterator();
        totalCount = feedbacks.getResultsSizeAfterPartialList();
        feedbacklist = feedbacks.getPartialList(lowIndex, viewSize);
        feedbacks.close();
        for(Map<String,Object> map :feedbacklist){
            String leaderList = "";
            if(!"FEEDBACK_PARTY_C".equals(map.get("permission"))){
                if("FEEDBACK_PARTY_A".equals(map.get("permission"))){
                    leaderList = leaderIds
                }else if("FEEDBACK_PARTY_B".equals(map.get("permission"))){
                    leaderList = leaderList + "," + planPerson;
                }
                leaderList = leaderList + "," + map.get("feedbackPerson");
                if(leaderList.indexOf(partyId) < 0){
                    list.add(map);

                }
            }
        }

    }
    for(Map map : list){
        feedbacklist.remove(map);
    }

    workReportMap.put("feedback",feedbacklist);
    workReportMap.put("viewIndex",viewIndex);
    workReportMap.put("highIndex",highIndex);
    workReportMap.put("totalCount",totalCount);
    workReportMap.put("viewSize",viewSize);
    workReportMap.put("workReportId",workReportId);
}