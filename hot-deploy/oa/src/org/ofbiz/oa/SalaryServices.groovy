package org.ofbiz.oa

import freemarker.template.Configuration
import freemarker.template.Template
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntity
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.config.model.Datasource
import org.ofbiz.entity.config.model.EntityConfig
import org.ofbiz.entity.datasource.GenericHelperInfo
import org.ofbiz.entity.jdbc.ConnectionFactoryLoader
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

public Map<String, Object> saveSalaryEntry() {
    String entryId = context.get("entryId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "保存条目成功";
    String relativeEntry = context.get("relativeEntry");
    List<String> list = new ArrayList<String>();
    while (UtilValidate.isNotEmpty(relativeEntry)) {
        list.add(relativeEntry);
        genericValue = delegator.findByPrimaryKey("TblSalaryEntry", UtilMisc.toMap("entryId", relativeEntry));
        relativeEntry = genericValue.get("relativeEntry");
        if (list.contains(relativeEntry)) {
            msg = "FAILED";
            break;
        }
    }
    if (!msg.equals("FAILED")) {
        if (UtilValidate.isNotEmpty(entryId)) {
            msg = "更新条目成功"
            genericValue = delegator.findByPrimaryKey("TblSalaryEntry", UtilMisc.toMap("entryId", entryId));
            genericValue.setNonPKFields(context);
            genericValue.store();
        } else {
            entryId = delegator.getNextSeqId("TblSalaryEntry").toString();
            genericValue = delegator.makeValidValue("TblSalaryEntry", UtilMisc.toMap("entryId", entryId));
            genericValue.setNonPKFields(context);
            genericValue.create();
        }
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String, Object> saveSalaryBillMould() {
    String mouldId = context.get("mouldId");
//    String mouldName = context.get("mouldName");
//    String useState = "USED";
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.getString("partyId");
    java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime());
    java.sql.Timestamp currentTime = new java.sql.Timestamp(currentDate.getTime());
    String msg = "保存工资条模板成功";
    if (UtilValidate.isNotEmpty(mouldId)){
        msg = "更新工资条模板成功";
        genericValue = delegator.findByPrimaryKey("TblSalaryBillMould",UtilMisc.toMap("mouldId",mouldId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }else {
        mouldId = delegator.getNextSeqId("TblSalaryBillMould").toString();
        context.put("mouldId", mouldId);
        context.put("createdTime", currentTime);
        context.put("createdPerson", partyId);
        List<GenericValue> salaryMouldList = delegator.findAll("TblSalaryBillMould",false);
        if(UtilValidate.isNotEmpty(salaryMouldList)){
            context.put("useState", "USED");
        }else{
            context.put("useState", "USING");
        }
        genericValue = delegator.makeValidValue("TblSalaryBillMould",context);
        genericValue.create();
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
public Map<String, Object> deleteSalaryTemplate(){
    Map success = ServiceUtil.returnSuccess();
    String mouldId = context.get("mouldId");
    String msg = "删除模板成功";
    genericValue = delegator.findByPrimaryKey("TblSalaryBillMould",UtilMisc.toMap("mouldId",mouldId));
    if(genericValue.get("useState").equals("USING")){
        return ServiceUtil.returnError("当前模板正在使用，无法删除");
    }else{
        GenericValue deleteSalaryTemplate = delegator.makeValidValue("TblSalaryBillMould",UtilMisc.toMap("mouldId",mouldId));
        deleteSalaryTemplate.remove();
        success.put("returnValue",msg);
        return success;
    }
}
public Map<String, Object> changeTemplateState(){
    Map success = ServiceUtil.returnSuccess();
    String mouldId = context.get("mouldId");
    String msg = "使用模板成功";
    try{
        usingTemplate = delegator.findByAnd("TblSalaryBillMould",UtilMisc.toMap("useState","USING"));
        if(UtilValidate.isNotEmpty(usingTemplate)){
            usingTemplate.get(0).put("useState","USED");
            usingTemplate.get(0).store();
        }
        genericValue = delegator.findByPrimaryKey("TblSalaryBillMould",UtilMisc.toMap("mouldId",mouldId));
        genericValue.put("useState","USING");
        genericValue.store();
    }catch (GenericEntityException e){
        return ServiceUtil.returnError("请刷新页面重新操作");
    }
    success.put("returnValue",msg);
    return success;
}

public Map<String, Object> saveAttendance() {
    String attendanceId = context.get("attendanceId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "保存考勤规则成功";
    if (UtilValidate.isNotEmpty(attendanceId)){
        msg = "更新考勤规则成功";
        genericValue = delegator.findByPrimaryKey("TblSalaryOnAttendance",UtilMisc.toMap("attendanceId",attendanceId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }else {
        Map<String,Object> map = new HashMap<String,Object>();
        entryId = delegator.getNextSeqId("TblSalaryEntry").toString();
        map.put("entryId",entryId);
        map.put("title",context.get("title"));
        map.put("type","DISCOUNT");
        map.put("relativeEntry","");
        map.put("amount","");
        map.put("remarks",context.get("title"));
        map.put("allUseEntry","1");
        map.put("systemType","1");//1表示添加规则时添加的薪资条目
        GenericValue entryValue = delegator.makeValidValue("TblSalaryEntry",map);
        entryValue.create();

        context.put("entryId",entryId);
        attendanceId = delegator.getNextSeqId("TblSalaryOnAttendance").toString();
        context.put("systemType",entryId);
        genericValue = delegator.makeValidValue("TblSalaryOnAttendance",UtilMisc.toMap("attendanceId",attendanceId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
public Map<String, Object> createSalarySend() {
    String staffId = context.get("staffId");
    String date = context.get("workDate");
    int month = Integer.parseInt(date.split("-")[1]);
    for(month;month<=12;month++){
        sendId = delegator.getNextSeqId("TblSalarySend").toString();
        genericValue = delegator.makeValidValue("TblSalarySend",UtilMisc.toMap("sendId",sendId));
        genericValue.setString("staffId",staffId);
        genericValue.setString("state","SEND_TYPE_NOTSEND");
        genericValue.setString("month", month<10?"0"+month:""+month);
        genericValue.setString("year", date.split("-")[0]);
        genericValue.create();
        saveDetail(sendId);
    }
}

private void saveDetail(String id){
    salaryEntryList = delegator.findByAnd("TblSalaryEntry",UtilMisc.toMap("allUseEntry","1"));
    for(Map<String, Object> entry:salaryEntryList){
        detailId = delegator.getNextSeqId("TblSendDetail").toString();
        genericValue = delegator.makeValidValue("TblSendDetail",UtilMisc.toMap("detailId",detailId));
        genericValue.setString("entryId",entry.get("entryId").toString());
        genericValue.setString("sendId",id);
        genericValue.create();
    }
}
public Map<String, Object> saveLeadInstructions() {
    workLogId = context.get("workLogId");
    Map<String,Object> result = ServiceUtil.returnSuccess();
    date = context.get("workDate");
    Timestamp workDate = Timestamp.valueOf(date);
    Date dateForChange = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = df.format(dateForChange);
    reviewDate = Timestamp.valueOf(dateString+" 00:00:00.0");
    try{
        workLogId = delegator.getNextSeqId("TblWorkLog").toString();
        GenericValue genericValue = delegator.makeValue("TblWorkLog",UtilMisc.toMap("workLogId", workLogId))
        genericValue.setString("logTitle",context.get("logTitle").toString());
        genericValue.setString("logContent",context.get("logContent").toString());
        genericValue.setString("planTitle",context.get("planTitle").toString());
        genericValue.setString("planContent",context.get("planContent").toString());
        genericValue.setString("reviewContent",context.get("reviewContent").toString());
        genericValue.setString("reviewedBy",context.get("reviewedBy").toString());
        genericValue.setString("partyId",context.get("partyId").toString());
        genericValue.set("workDate",workDate)
        genericValue.set("reviewDate",reviewDate)
        genericValue.store();
    }catch (Exception e){
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
        }catch (Exception e){
            e.printStackTrace();
            msg = "关注失败"
        }
    }else if("取消关注"){
        try{
            genericValue = delegator.removeByAnd("TblFocusUnderling",UtilMisc.toMap("partyIdFrom", partyIdFrom,"partyIdTo",partyIdTo));
            msg = "取消关注成功"
        }catch (Exception e){
            e.printStackTrace();
            msg = "取消关注失败"
        }
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    result.put("data",msg)
    return result;
}

public Map<String, Object> salarySuccess() {
    String salary_success = context.get("salary_success");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    successResult.put("data",salary_success);
    return successResult;
}

public Map<String,Object>  searchSalaryInfo(){
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String loginId = userLogin.getString("partyId");
    String salaryId = context.get("salaryId");
    String partyId = context.get("partyId");//员工ID
    List<GenericValue> resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
    Map<String,Object> resultMap = new HashMap<String,Object>();
    if (resultList.size()!=0){
        Map<String,Object> map = resultList.get(0);
        resultMap.put("groupName",map.get("departmentName"));//部门
        resultMap.put("fullName",map.get("fullName"));//姓名
        resultMap.put("workerSn",map.get("workerSn"));//工号
        resultMap.put("occupationName",map.get("occupationName"));//岗位
        resultMap.put("partyId",partyId);//用户Id
    }
    GenericValue party = EntityQuery.use(delegator).select()
            .from("Person")
            .where(EntityCondition.makeCondition("partyId",loginId))
            .queryOne();
    resultMap.put("loginName",party.get("fullName"));
    resultMap.put("inputId",loginId);
    List<GenericValue> salaryItemsList1 = delegator.findAll("SalaryEntryDetail",false);
    List<Map<String,Object>> salaryItemsList = new ArrayList<Map<String,Object>>();
    DecimalFormat format = new DecimalFormat("0.00");
    for(GenericValue salaryItem : salaryItemsList1){
        Map<String,Object> map = new HashMap<String,Object>();
        if("10001".equals(salaryItem.get("entryId"))){
            List<GenericValue> salaryValueList = EntityQuery.use(delegator).select().from("TblContract").where(UtilMisc.toMap("partyId",partyId,"contractType","LABOR_CONTRACT")).orderBy("endDate DESC").queryList();
            if(null != salaryValueList && salaryValueList.size() > 0){
                GenericValue salaryValue = salaryValueList.get(0);
                if(salaryValue.get("salary")!=null){
                     salaryItem.put("amount",format.format(Double.parseDouble(salaryValue.get("salary").toString())));
                    map.put("oldAmount",format.format(Double.parseDouble(salaryValue.get("salary").toString())));
                }else {
                    salaryItem.put("amount",format.format(Double.parseDouble("0")));
                    map.put("oldAmount",format.format(Double.parseDouble("0")));
                }
            }
        }else{
            List<GenericValue> genericValueList = new ArrayList<GenericValue>();
            salaryItem.put("amount",format.format(searchEntryPay(null ,salaryItem,genericValueList,null)));
            map.put("oldAmount",format.format(searchEntryPay(null ,salaryItem,genericValueList,null)));
        }
        map.putAll(salaryItem);
        List<GenericValue> salaryList = EntityQuery.use(delegator)
                .select()
                .from("TblSalaryAdjustment")
                .where(EntityCondition.makeCondition("partyId",partyId))
                .orderBy("createdStamp")
                .queryList();
        int intSalary = -1;
        for(GenericValue genericValue : salaryList){
            if (intSalary >= 0 && genericValue.get("salaryId").toString().equals(salaryId)){
                List<GenericValue> salaryInfoList = EntityQuery.use(delegator)
                        .select()
                        .from("TblSalaryInfoHistory")
                        .where(EntityCondition.makeCondition("salaryId",salaryList.get(intSalary).get("salaryId")))
                        .queryList();
                for (GenericValue genericValue1 : salaryInfoList){
                    if (map.get("entryId").equals(genericValue1.get("entryId")))
                        map.put("oldAmount",format.format(Double.parseDouble(genericValue1.get("newAmount").toString())));
                }
            }
            intSalary = intSalary + 1;
        }
        salaryItemsList.add(map);
    }
    List<GenericValue> salaryHistoryList = new ArrayList<GenericValue>();
    if(null != salaryId && salaryId.length() > 0){
        GenericValue salaryInfo = EntityQuery.use(delegator).select().from("TblSalaryAdjustment").where(EntityCondition.makeCondition("salaryId",salaryId)).queryOne();
        salaryHistoryList = EntityQuery.use(delegator).select().from("TblSalaryInfoHistory").where(EntityCondition.makeCondition("salaryId",salaryId)).queryList();
        resultMap.put("remarks",salaryInfo.get("remarks"));
        resultMap.put("adjustmentTime",salaryInfo.get("adjustmentTime"));
        resultMap.put("endTime",salaryInfo.get("endTime"));
        resultMap.put("startTime",salaryInfo.get("startTime"));
        resultMap.put("salaryId",salaryInfo.get("salaryId"));
    }
    resultMap.put("salaryItemsList",salaryItemsList);
    resultMap.put("salaryHistoryList",salaryHistoryList);
    Map<String,Object> result = ServiceUtil.returnSuccess();
    result.put("data",resultMap);
    return result;
}

/**
 * 递归查询最上级薪资条目
 * @param newSalaryItem
 * @param salaryItem
 * @param genericValueList
 * @param idList
 * @return
 */
public Double searchEntryPay(GenericValue newSalaryItem,GenericValue salaryItem,List<GenericValue> genericValueList,List<String> idList){
    String entryId = salaryItem.get("entryId").toString();
    String newEntryType = "";
    if(null != newSalaryItem){
        newEntryType = newSalaryItem.get("relativeEntry");
    }else{
        newEntryType = salaryItem.get("relativeEntry");
    }

    if(null == idList){
        idList = new ArrayList<String>();
        idList.add(entryId);
    }
    Double salary = 0;
    if(null != newEntryType && !"".equals(newEntryType)){
        GenericValue parentSalaryItem = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(UtilMisc.toMap("entryId",newEntryType)).queryOne();
        idList.add(parentSalaryItem.get("entryId").toString());
        if(UtilValidate.isNotEmpty(parentSalaryItem.get("relativeEntry"))){
            newSalaryItem = parentSalaryItem;
            genericValueList.add(parentSalaryItem);
            searchEntryPay(newSalaryItem,salaryItem,genericValueList,idList);
        }else{
            newSalaryItem = parentSalaryItem;
            return setPay(salaryItem.get("entryId").toString(),newSalaryItem,idList);
        }
    }else{
        if(null != salaryItem.get("amount")){
            salary = Double.parseDouble(salaryItem.get("amount").toString());
        }
    }
    return salary;
}

/**
 * 递归设置薪资条目金额
 * @param entryId
 * @param genericValue
 * @param idList
 * @return
 */
public Double setPay(String entryId ,GenericValue genericValue, List<String> idList){
    Double salaryPay = Double.parseDouble(genericValue.get("amount").toString());
    List conditionList = new ArrayList();
    conditionList.add(EntityCondition.makeCondition("entryId", EntityOperator.IN, idList));
    conditionList.add(EntityCondition.makeCondition("relativeEntry", EntityOperator.EQUALS, genericValue.get("entryId").toString()));
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    GenericValue entryValue = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(condition).queryOne();
    salaryPay = salaryPay * (Double.parseDouble(entryValue.get("amount"))/100);
    if(!entryId.equals(entryValue.get("entryId"))){
        setPay(entryId,entryValue,idList);
    }
    return salaryPay;
}

public Map<String,Object> saveSalary(){
    String message = "";
    String salaryInfo = context.get("urlValue");
    String startTime = context.get("startTime");
    String adjustmentTime = context.get("adjustmentTime");
    context.put("startTime",this.setTimestamp(startTime));
    context.put("adjustmentTime",this.setTimestamp(adjustmentTime));
    Date date = new Date();
    Timestamp timestamp = new Timestamp(date.getTime());
    context.put("inputTime",timestamp);
    if(null != salaryInfo && salaryInfo.length() > 0){
        String id;
        String uploadSalaryId = context.get("salaryId");
        if(null != uploadSalaryId && uploadSalaryId.length() > 0){
            GenericValue salaryAdjustment = delegator.makeValidValue("TblSalaryAdjustment",context);
            salaryAdjustment.store();
            id = uploadSalaryId;
            delegator.removeByCondition("TblSalaryInfoHistory",EntityCondition.makeCondition("salaryId",uploadSalaryId));
        }else{
            String salaryId = delegator.getNextSeqId("TblSalaryAdjustment").toString();//获取主键ID
            context.put("salaryId",salaryId);
            GenericValue salaryAdjustment = delegator.makeValidValue("TblSalaryAdjustment",context);
            salaryAdjustment.create();
            id = salaryId;
        }
        String[] salaryInfos = salaryInfo.split(",");
        for(String salary : salaryInfos){
            String entryId = salary.substring(0,salary.indexOf(":"));
            String amout = salary.substring(salary.indexOf(":") + 1,salary.indexOf("-"));
            String status = salary.substring(salary.indexOf("-")+1,salary.length());
            if("0".equals(status)){
                continue;
            }else{
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("salaryId",id);
                map.put("entryId",entryId);
                map.put("oldAmount","");
                if(amout != null && !"".equals(amout)){
                    map.put("newAmount",amout);
                }else{
                    map.put("newAmount","");
                }
                GenericValue salaryInfoHistory = delegator.makeValidValue("TblSalaryInfoHistory",map);
                salaryInfoHistory.create();
            }
        }
        message = "保存成功";
    }else{
        message = "保存失败";
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    Map<String,Object> resultMap = new HashMap<String,Object>();
    resultMap.put("message",message);
    result.put("data",resultMap)
    return result;
}

public Timestamp setTimestamp(String date){
    DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = fmt.parse(date);
    return new Timestamp(startDate.getTime());
}

public Map<String,Object> SalaryManagementList(){
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

    String partyId = context.get("partyId");
    EntityListIterator salaryIterator = EntityQuery.use(delegator).select().from("TblSalaryAdjustment").where(EntityCondition.makeCondition("partyId",partyId)).orderBy("inputTime DESC").queryIterator();
    List<Map<String,Object>> salaryMapList = new ArrayList<Map<String,Object>>();
    if(null != salaryIterator && salaryIterator.getResultsSizeAfterPartialList() > 0){
        totalCount = salaryIterator.getResultsSizeAfterPartialList();
        List<GenericValue> salaryList = salaryIterator.getPartialList(lowIndex, viewSize);
        for(GenericValue genericValue : salaryList){
            Map<String,Object> map = new HashMap<String,Object>();
            map.putAll(genericValue);
            String startTime = map.get("startTime");
            String adjustmentTime = map.get("adjustmentTime");
            map.put("startTime",startTime.substring(0,startTime.indexOf(" ")));
            map.put("adjustmentTime",adjustmentTime.substring(0,adjustmentTime.indexOf(" ")));
            String salaryInfoId = map.get("salaryId")
            List<GenericValue> salaryInfoList = EntityQuery.use(delegator).select().from("TblSalaryInfoHistory").where(EntityCondition.makeCondition("salaryId",salaryInfoId)).queryList();
            String basePay = "";
            for(GenericValue salaryInfo : salaryInfoList){
                if(salaryInfo.get("entryId").equals("10001")){
                    basePay = salaryInfo.get("newAmount");
                    break;
                }else{
                    basePay = "";
                }
            }
            map.put("basePay",basePay);
            map.put("partyId",partyId);
            salaryMapList.add(map);
        }
    }
    salaryIterator.close();
    Map<String,Object> result = ServiceUtil.returnSuccess();
    Map<String,Object> resultMap = new HashMap<String,Object>();
    resultMap.put("salaryMapList",salaryMapList);
    resultMap.put("partyId",partyId);
    resultMap.put("viewIndex",viewIndex);
    resultMap.put("highIndex",highIndex);
    resultMap.put("totalCount",totalCount);
    resultMap.put("viewSize",viewSize);
    resultMap.put("lowIndex",lowIndex);
    result.put("data",resultMap);
    return result;
}

public Map<String,Object> removeSalaryById(){
    String salaryId = context.get("salaryId");
    String message;
    if(null != salaryId && !"".equals(salaryId)){
        delegator.removeByCondition("TblSalaryInfoHistory",EntityCondition.makeCondition("salaryId",salaryId));
        delegator.removeByCondition("TblSalaryAdjustment",EntityCondition.makeCondition("salaryId",salaryId));
        message = "删除成功";
    }else{
        message = "删除失败";
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    Map<String,Object> resultMap = new HashMap<String,Object>();
    resultMap.put("message",message);
    result.put("data",resultMap);
    return result;
}

/**
 * 查询薪资发放列表
 * @return
 */
public Map<String,Object> searchSalaryPayOffList() {
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 10;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 10;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    String y = context.get("year");
    Integer year;
    //用于查询到下月为止的数据
    Calendar calendar = Calendar.getInstance();
    Integer month = calendar.get(Calendar.MONTH) + 1;
    if (null != y && !y.equals("") && !y.equals(calendar.get(Calendar.YEAR).toString())) {
        year = Integer.parseInt(y);
        month = 12;
    }else{
        //默认为当前年份
        year = calendar.get(Calendar.YEAR);
    }
    String partyIdFor = context.get("partyId");
    String department = context.get("department");
    String position = context.get("position");
    String appointMonth = context.get("month");
    String type = context.get("type");
    String salaryStatus = context.get("salaryStatus");
    if(UtilValidate.isNotEmpty(position)){
        position = position.substring(position.indexOf(",") + 1,position.length());
    }
    List<GenericValue> salaryPayOffList = new ArrayList<GenericValue>()
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Map<String,Object> numMap = new HashMap<String,Object>();
    Map<String,Object> numMonthMap = new HashMap<String,Object>();
    numMap.put("disapprove",0);
    numMap.put("approve",0);
    numMap.put("notSend",0);
    numMap.put("notExamine",0);
    numMap.put("sent",0);
    //得到当月最后一天。用于去离职表中查询出离职的人员ID
    Calendar cale = Calendar.getInstance();

    cale.set(Calendar.YEAR,Integer.parseInt(year.toString()))
    if(null != appointMonth && !"null".equals(appointMonth)) {
        cale.set(Calendar.MONTH, Integer.parseInt(appointMonth));
        cale.set(Calendar.DAY_OF_MONTH, 1);
        cale.add(Calendar.DAY_OF_MONTH, -1);
        month = Integer.parseInt(appointMonth.toString());
    }else{
        cale.add(Calendar.MONTH, month);
        cale.set(Calendar.DAY_OF_MONTH, 0);
    }
    Date date1 = sdf.parse(sdf.format(cale.getTime()));
    java.sql.Date timestamp = new java.sql.Date(date1.getTime());

    List<GenericValue> staffList =  EntityQuery.use(delegator).select().from("TblStaff").where(UtilMisc.toMap("jobState","DEPARTURE")).queryList()
    List<String> partyIdList = new ArrayList<String>();
    for(GenericValue genericValue : staffList){
        partyIdList.add(genericValue.get("partyId").toString());
    }

    GenericHelperInfo helperInfo = delegator.getGroupHelperInfo("org.ofbiz");
    Datasource datasourceInfo = EntityConfig.getDatasource(helperInfo.getHelperBaseName());
    // 获得数据库的连接
    Connection connection = ConnectionFactoryLoader.getInstance().getConnection(helperInfo, datasourceInfo.getInlineJdbc());
    String sql = "SELECT * FROM (SELECT TS.PARTY_ID,TS.WORKER_SN,TS.DEPARTMENT AS TS_DEPARTMENT,TS.GENDER," +
            " TS.WORK_DATE AS TS_WORK_DATE,TS.BIRTH_DAY,ERTB.DESCRIPTION,TS.AGE,TS.PLACE_OF_ORIGIN,ERTA.DESCRIPTION AS ERTA_DESCRIPTION," +
            " ERT.DESCRIPTION AS ERT_DESCRIPTION,TS.FAMILY_ADDRESS ,TS.PHONE_NUMBER AS TS_PHONE_NUMBER,ENUG.DESCRIPTION AS POST_NAME," +
            " ENUDE.DESCRIPTION AS ENUDE_DESCRIPTION,ENUDI.DESCRIPTION AS ENUDI_DESCRIPTION,PG.GROUP_NAME,PERSON.FIRST_NAME,PERSON.LAST_NAME," +
            " PERSON.FULL_NAME,PERSON.BIRTH_DATE,PERSON.MARITAL_STATUS,PERSON.CARD_ID,PR.ROLE_TYPE_ID,RT.DESCRIPTION AS GENDER_NAME,TF.WORK_DATE as TF_WORK_DATE" +
            " FROM TBL_STAFF TS INNER JOIN PERSON PERSON ON TS.PARTY_ID = PERSON.PARTY_ID INNER JOIN PARTY_ROLE PR ON TS.PARTY_ID = PR.PARTY_ID" +
            " INNER JOIN ROLE_TYPE RT ON PR.ROLE_TYPE_ID = RT.ROLE_TYPE_ID LEFT OUTER JOIN PARTY_GROUP PG ON TS.DEPARTMENT = PG.PARTY_ID" +
            " LEFT OUTER JOIN ENUMERATION ENUG ON TS.GENDER = ENUG.ENUM_ID LEFT OUTER JOIN ENUMERATION ENUDI ON TS.DIPLOMA = ENUDI.ENUM_ID" +
            " LEFT OUTER JOIN ENUMERATION ENUDE ON TS.DEGREE = ENUDE.ENUM_ID LEFT OUTER JOIN ENUMERATION ERT ON TS.DOMICILE_TYPE = ERT.ENUM_ID" +
            " LEFT OUTER JOIN ENUMERATION ERTA ON TS.POLITICAL_STATUS = ERTA.ENUM_ID LEFT OUTER JOIN ENUMERATION ERTB ON TS.IF_MARRIED = ERTB.ENUM_ID" +
            " LEFT OUTER JOIN TBL_STAFF TF ON TS.PARTY_ID = TF.PARTY_ID GROUP BY TS.WORKER_SN) SD "+
            " LEFT JOIN (select SEND_ID,YEAR,MONTH,STAFF_ID,STATE,SEND_STATUS from tbl_salary_send where YEAR = '"+year+"' and MONTH = '"+month+"') AS SS ON SD.PARTY_ID = SS.STAFF_ID" +
            " where TF_WORK_DATE <= '" + sdf.format(new java.sql.Date(timestamp.getTime())) + "'";

    if(UtilValidate.isNotEmpty(year)){
        Date endTime = getYearLast(year);
        sql += " and TS_WORK_DATE  <= '"+sdf.format(endTime)+"'";
    }
    if(UtilValidate.isNotEmpty(partyIdFor)){
        sql += " and party_Id  = '" + partyIdFor + "'";
    }
    if(UtilValidate.isNotEmpty(department)){
        sql += " and TS_DEPARTMENT  = '" + department + "'";
    }
    if(UtilValidate.isNotEmpty(position)){
        sql += " and ROLE_TYPE_ID  = '" + position + "'";
    }
    if(UtilValidate.isNotEmpty(partyIdList.size()) && partyIdList.size() > 0){
        String partyIds = "";
        for(String partyId : partyIdList){
            partyIds += "'" + partyId + "',";
        }
        partyIds = partyIds.substring(0,partyIds.length() -1);
        sql += "and party_Id  NOT IN (" + partyIds + ")";
    }
    if(UtilValidate.isNotEmpty(type)){
        if("SEND_TYPE_NOTSEND".equals(type)){
            sql += "and (STATE = '" + type + "' OR STATE IS NULL)";
        }else{
            sql += "and STATE = '" + type + "'";
        }
    }
    if(UtilValidate.isNotEmpty(salaryStatus)){
        sql += " and (SEND_STATUS = '" + salaryStatus + "'";
        if("1".equals(salaryStatus)){
            sql += " or SEND_STATUS is null)";
        }else{
            sql += ")"
        }
    }
    sql += " and TF_WORK_DATE is not null";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    List<Map<String,Object>> reportAllList = setDataInfo(resultSet,totalCount);
    String lsql = sql + " LIMIT " + viewIndex + "," + viewSize;
    PreparedStatement statementList = connection.prepareStatement(lsql);
    ResultSet resultSetList = statementList.executeQuery();
    List<Map<String,Object>> reportList = setDataInfo(resultSetList,null);

    for(Map<String,Object> map : reportAllList){
        List<GenericValue> adjustmentList = EntityQuery.use(delegator).select().from("TblSalaryAdjustment").where(EntityCondition.makeCondition("partyId",map.get("partyId"))).orderBy("startTime DESC").queryList();
        GenericValue sendValue = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("staffId",map.get("partyId"),"month",month.toString(),"year",year.toString())).queryOne();
        if(null != adjustmentList && adjustmentList.size() > 0) {
            GenericValue adjustment = adjustmentList.get(0);
            if (!UtilValidate.isNotEmpty(sendValue) || !UtilValidate.isNotEmpty(sendValue.get("state")) || "SEND_TYPE_NOTSEND".equals(sendValue.get("state"))) {
                Calendar a = Calendar.getInstance();
                a.set(Calendar.YEAR, year);
                a.set(Calendar.MONTH, month - 1);
                a.set(Calendar.DATE, 1);
                a.roll(Calendar.DATE, -1);
                Integer maxDate = a.get(Calendar.DATE);
                getAttendanceInfoForStaff(null, map.get("partyId").toString(), maxDate.toString(), year.toString(), month.toString(), adjustment.get("salaryId").toString(), userLogin);
            }
        }
    }

    //查询人员列表
    setSendInfo(reportList,numMap,year,salaryPayOffList,false,month,numMonthMap,appointMonth);
    //查询当月薪资发放统计数量，因为查询人员具有分页功能，统计出来的只是当前页的数量，统计不准确，所以新增一个专门查询统计数量
    setSendInfo(reportAllList,numMap,year,salaryPayOffList,true,month,numMonthMap,appointMonth);
    List<GenericValue> staffValueList = EntityQuery.use(delegator).select().from("TblStaff").where(EntityCondition.makeCondition("workDate", EntityOperator.NOT_EQUAL, null)).orderBy("workDate").queryList();
    Calendar cal = Calendar.getInstance();
    Integer nowYear = cal.get(Calendar.YEAR);
    String startDate = nowYear;
    if(null != staffValueList && staffValueList.size() > 0){
    GenericEntity staffEntity = staffValueList.get(0);
    String workDate = staffEntity.get("workDate").toString();
        startDate = workDate.substring(0,workDate.indexOf("-"));
    }
    //页面中只显示最早入职员工年份到到今年的年份列表
    List<Map<String ,Object>> dateList = new ArrayList<Map<String ,Object>>();
    for(Integer i = Integer.parseInt(startDate); i <= nowYear; i++){
        Map<String ,Object> yearMap = new HashMap<String, Object>();
        yearMap.put("yearValue",i.toString());
        dateList.add(yearMap);
    }
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("salaryPayOffList",salaryPayOffList);
    viewData.put("year",year.toString());
    viewData.put("partyId",partyIdFor);
    viewData.put("department",department);
    viewData.put("position",position);
    viewData.put("nowYear",nowYear.toString());
    viewData.put("month",month.toString());
    viewData.put("dateList",dateList);
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("lowIndex",lowIndex);
    viewData.put("sent",numMap.get("sent"));
    viewData.put("numMonthMap",numMonthMap);
    viewData.put("approve",numMap.get("approve"));
    viewData.put("notExamine",numMap.get("notExamine"));
    viewData.put("notSend",numMap.get("notSend"));
    viewData.put("disapprove",numMap.get("disapprove"));
    successResult.put("data",viewData);
    return successResult;
}

public List<Map<String,Object>> setDataInfo(ResultSet resultSetList,Integer totalCount){
    List salaryUpperValue = new ArrayList();
    while (resultSetList.next()){
        if(null != totalCount){
            totalCount++;
        }
        Map map = new HashMap();
        map.put("PARTY_ID",resultSetList.getString("PARTY_ID"));
        map.put("WORKER_SN",resultSetList.getString("WORKER_SN"));
        map.put("TS_DEPARTMENT",resultSetList.getString("TS_DEPARTMENT"));
        map.put("GENDER",resultSetList.getString("GENDER"));
        map.put("TS_WORK_DATE",resultSetList.getString("TS_WORK_DATE"));
        map.put("POST_NAME",resultSetList.getString("POST_NAME"));
        map.put("ENUDE_DESCRIPTION",resultSetList.getString("ENUDE_DESCRIPTION"));
        map.put("ENUDI_DESCRIPTION",resultSetList.getString("ENUDI_DESCRIPTION"));
        map.put("GROUP_NAME",resultSetList.getString("GROUP_NAME"));
        map.put("FULL_NAME",resultSetList.getString("FULL_NAME"));
        map.put("BIRTH_DATE",resultSetList.getString("BIRTH_DATE"));
        map.put("CARD_ID",resultSetList.getString("CARD_ID"));
        map.put("ROLE_TYPE_ID",resultSetList.getString("ROLE_TYPE_ID"));
        map.put("GENDER_NAME",resultSetList.getString("GENDER_NAME"));
        map.put("TF_WORK_DATE",resultSetList.getString("TF_WORK_DATE"));
        salaryUpperValue.add(map);
    }
    return keyLowerCase(salaryUpperValue);
}

/**
 * 将从数据库查询出来的数据库Map中的key去下划线转换成小写并且下划线后一字母大写
 * @param listValue
 * @return
 */
public List<Map<String,Object>> keyLowerCase(List<Map<String,Object>> listValue){
    List newList = new ArrayList();
    for(Map<String,Object> map : listValue){
        Set set = map.entrySet();
        Iterator mapInterator = set.iterator();
        Map newMap = new HashMap();
        String key = "";
        String value = "";
        String lowerKey = "";
        while(mapInterator.hasNext()){
            Map.Entry<String, String> entry1=(Map.Entry<String, String>)mapInterator.next();
            key = entry1.getKey();
            value = map.get(key).toString();
            lowerKey = key.toLowerCase();
            for(int i = 0; i< lowerKey.length();i++){
                char fd = lowerKey.charAt(i);
                if('_' == fd){
                    try{
                        String testLowerValue = lowerKey.substring(i+2,lowerKey.length());
                        if("".equals(testLowerValue)){
                            String subLowerValue = lowerKey.substring(i+1,i+2);
                            lowerKey = lowerKey.substring(0,i) + subLowerValue.toUpperCase();
                        }else{
                            String subLowerValue = lowerKey.substring(i+1,i+2);
                            lowerKey = lowerKey.substring(0,i) + subLowerValue.toUpperCase() + lowerKey.substring(i+2,lowerKey.length());
                        }
                    }catch(Exception e){
                        lowerKey = lowerKey.substring(0,i);
                    }
                }
            }
            newMap.put(lowerKey, value);
        }
        newList.add(newMap);
    }
    return newList;
}

/**
 * 查询发放人员，统计
 * @param companyStaffList
 * @param numMap
 * @param year
 * @param salaryPayOffList
 * @param aFlag
 */
public void setSendInfo(List<GenericValue> companyStaffList,Map numMap,Integer year,List<GenericValue> salaryPayOffList,Boolean aFlag,Integer month,Map numMonthMap,String appointMonth){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date newDate = new Date();
    if(null != companyStaffList && companyStaffList.size() > 0){
        for(Map staff :companyStaffList){
            String partyId = staff.get("partyId");
            //查看员工信息，查找入职日期
            String startTime  = staff.get("tfWorkDate");
            Date startDate = sdf.parse(startTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(startDate);
            Integer startYear = calendar1.get(Calendar.YEAR);
            Integer startMonth = calendar1.get(Calendar.MONTH)+1;
            if(startYear < year){
                startMonth = 1;
            }
            if(null != appointMonth && appointMonth.length() > 0){
                startMonth = Integer.parseInt(appointMonth);
            }
            Map payOffMap = new HashMap();
            payOffMap.put("groupName",staff.get("groupName"));
            payOffMap.put("name",staff.get("fullName"));
            payOffMap.put("workerSn",staff.get("workerSn"));
            payOffMap.put("genderName",staff.get("genderName"));
            payOffMap.put("postName",staff.get("postName"));
            payOffMap.put("staffId",partyId);
            for(Integer i = startMonth;i <= month; i++){
                GenericValue send = EntityQuery.use(delegator).select().from("selectTblSalarySend").where(UtilMisc.toMap("staffId",partyId,"year",year.toString(),"month", i.toString())).queryOne();
                putInfo(i.toString(),payOffMap,numMap,send,partyId,year,aFlag,numMonthMap);
            }
            if(!aFlag){
                salaryPayOffList.add(payOffMap);
            }
        }
    }
}

/**
 * 统计各个状态的数量及保存各个状态的html代码，以便在页面中显示
 * @param moth
 * @param payOffMap
 * @param numMap
 * @param send
 * @param flag
 * @param partyId
 * @param year
 * @param month
 * @param aFlag
 */
public Map<String,Object> putInfo(String moth,Map payOffMap,Map numMap,Map send,String partyId,Integer year,Boolean aFlag,Map numMonthMap){
    if(null != send){
        String state = send.get("state");
        if("SEND_TYPE_SEND".equals(state)){//已发
            if(aFlag){
                Integer num = Integer.parseInt(numMap.get("sent").toString());
                numMap.put("sent",++num);
            }
            if(!aFlag){
                Integer num = 0;
                if(null != numMonthMap.get("sent" + moth) && !"".equals(numMonthMap.get("sent" + moth).toString())){
                    num = Integer.parseInt(numMonthMap.get("sent" + moth).toString());
                }
                numMonthMap.put("sent" + moth,++num);
                payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ','+send.get("sendId")+','+send.get("state") +',' + send.get("paySalary") + '" type="checkbox">');
                payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle">'+send.get("stateName")+'(' + send.get("paySalary") +')</span>');
            }
        }else if("SEND_TYPE_APPROVE".equals(state)){//已审
            if(aFlag){
                Integer num = Integer.parseInt(numMap.get("approve").toString());
                numMap.put("approve",++num);
            }
            if(!aFlag){
                Integer num = 0;
                if(null != numMonthMap.get("approve" + moth) && !"".equals(numMonthMap.get("approve" + moth).toString())){
                    num = Integer.parseInt(numMonthMap.get("approve" + moth).toString());
                }
                numMonthMap.put("approve" + moth,++num);
                payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ','+send.get("sendId")+','+send.get("state") +',' + send.get("paySalary") + '" type="checkbox">');
                payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle" onclick="$.salary.submitPersonSalaryItemsFor(\'' + partyId + '\',\'' + year + '\',\'' + moth + '\',\''+send.get("sendId") +'\',\''+send.get("state") +'\',\''+send.get("paySalary") +'\')" >'+send.get("stateName")+'(' + send.get("paySalary") + ')</a>');
            }
        }else if("SEND_TYPE_NOTEXAMINE".equals(state)){//未审
            if(aFlag){
                Integer num = Integer.parseInt(numMap.get("notExamine").toString());
                numMap.put("notExamine",++num);
            }
            if(!aFlag){
                Integer num = 0;
                if(null != numMonthMap.get("notExamine" + moth) && !"".equals(numMonthMap.get("notExamine" + moth).toString())){
                    num = Integer.parseInt(numMonthMap.get("notExamine" + moth).toString());
                }
                numMonthMap.put("notExamine" + moth,++num);
                payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ','+send.get("sendId")+','+send.get("state") +',' + send.get("paySalary") + '" type="checkbox">');
                payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle" onclick="$.salary.submitPersonSalaryItemsFor(\'' + partyId + '\',\'' + year + '\',\'' + moth + '\',\''+send.get("sendId") +'\',\''+send.get("state") +'\',\''+send.get("paySalary") +'\')" >'+send.get("stateName")+'(' + send.get("paySalary") + ')</a>');
            }
        }else if("SEND_TYPE_NOTSEND".equals(state)){//未发
            if(aFlag){
                Integer num = Integer.parseInt(numMap.get("notSend").toString());
                numMap.put("notSend",++num);
            }
            if(!aFlag){
                Integer num = 0;
                if(null != numMonthMap.get("notSend" + moth) && !"".equals(numMonthMap.get("notSend" + moth).toString())){
                    num = Integer.parseInt(numMonthMap.get("notSend" + moth).toString());
                }
                numMonthMap.put("notSend" + moth,++num);
                payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ','+send.get("sendId")+','+send.get("state") +',' + send.get("paySalary") + '" type="checkbox">');
                payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle" onclick="$.salary.submitPersonSalaryItemsFor(\'' + partyId + '\',\'' + year + '\',\'' + moth + '\',\''+send.get("sendId") +'\',\''+send.get("state") +'\',\''+send.get("paySalary") +'\')" >'+send.get("stateName")+'</a>');
            }
        }else if("SEND_TYPE_DISAPPROVE".equals(state)){//未通过
            if(aFlag){
                Integer num = Integer.parseInt(numMap.get("disapprove").toString());
                numMap.put("disapprove",++num);
            }
            if(!aFlag){
                Integer num = 0;
                if(null != numMonthMap.get("disapprove" + moth) && !"".equals(numMonthMap.get("disapprove" + moth).toString())){
                    num = Integer.parseInt(numMonthMap.get("disapprove" + moth).toString());
                }
                numMonthMap.put("disapprove" + moth,++num);
                payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ','+send.get("sendId")+','+send.get("state") +',' + send.get("paySalary") + '" type="checkbox">');
                payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle" onclick="$.salary.submitPersonSalaryItemsFor(\'' + partyId + '\',\'' + year + '\',\'' + moth + '\',\''+send.get("sendId") +'\',\''+send.get("state") +'\',\''+send.get("paySalary") +'\')" >'+send.get("stateName")+'(' + send.get("paySalary") + ')</a>');
            }
        }else{//未发
            if(aFlag){
                Integer num = Integer.parseInt(numMap.get("notSend").toString());
                numMap.put("notSend",++num);
            }
            if(!aFlag){
                Integer num = 0;
                if(null != numMonthMap.get("notSend" + moth) && !"".equals(numMonthMap.get("notSend" + moth).toString())){
                    num = Integer.parseInt(numMonthMap.get("notSend" + moth).toString());
                }
                numMonthMap.put("notSend" + moth,++num);
                payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ',null,'+send.get("state") +',' + send.get("paySalary") + '" type="checkbox">');
                payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle" onclick="$.salary.submitPersonSalaryItemsFor(\'' + partyId + '\',\'' + year + '\',\'' + moth + '\',null,\''+send.get("state") +'\'),\''+send.get("paySalary") +'\'">' + "未发" + '</a>');
            }
        }
    }else{//未发
        if(aFlag){
            Integer num = Integer.parseInt(numMap.get("notSend").toString());
            numMap.put("notSend",++num);
        }
        if(!aFlag){
            Integer num = 0;
            if(null != numMonthMap.get("notSend" + moth) && !"".equals(numMonthMap.get("notSend" + moth).toString())){
                num = Integer.parseInt(numMonthMap.get("notSend" + moth).toString());
            }
            numMonthMap.put("notSend" + moth,++num);
            List<GenericValue> adjustmentList = EntityQuery.use(delegator).select().from("TblSalaryAdjustment").where(EntityCondition.makeCondition("partyId",partyId)).orderBy("endTime DESC").queryList();
            String state = "";
            String name ="";
            if(null != adjustmentList && adjustmentList.size() > 0){
                GenericValue adjustment = adjustmentList.get(0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, Integer.parseInt(moth));
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                String startTime = adjustment.get("startTime").toString();
                Date startDate = sdf.parse(startTime);
                Date newDate = sdf.parse(sdf.format(cal.getTime()));
                if(newDate.getTime() >= startDate.getTime()){
                    state = "SEND_TYPE_NOTSEND";
                    name = "未发";
                }else{
                    state = "";
                    name = "未发异常（薪资配置过期）";
                }
            }else{
                state = "";
                name = "未发异常（薪资未配置）";
            }
            payOffMap.put("check"+moth,'<input name="month_' + moth + '_'+partyId+'" value="' + partyId + ',' + year + ',' + moth + ',null,'+state+',null" type="checkbox">');
            payOffMap.put("moneh" + moth,'<a class="hyperLinkStyle" onclick="$.salary.submitPersonSalaryItemsFor(\'' + partyId + '\',\'' + year + '\',\'' + moth + '\',null,\'' + state + '\',null)">' + name + '</a>');
        }
    }
}

/**
 * 点击状态，进入工资发放界面
 * @return
 */
public Map<String,Object> submitPersonSalary(){
    GenericValue userLogin = (GenericValue)parameters.get("userLogin");
    DecimalFormat format = new DecimalFormat("0.00");
    String partyId = context.get("partyId");
    String flag = context.get("flag");//用于判断验证是否过期，两个js方法都指向这个方法
    String year = context.get("year");
    String month = context.get("month");
    String sendId = context.get("sendId");
    String status= context.get("status");
    String paySalary= context.get("paySalary");
    if(null == status || "".equals(status)){
        status = "SEND_TYPE_NOTSEND";
    }
    String message = "success";
    List<Map<String,Object>> salaryRealityList = new ArrayList<Map<String,Object>>();
    List<GenericValue> adjustmentList = EntityQuery.use(delegator).select().from("TblSalaryAdjustment").where(EntityCondition.makeCondition("partyId",partyId)).orderBy("startTime DESC").queryList();
    if(null != adjustmentList && adjustmentList.size() > 0){
        GenericValue adjustment = adjustmentList.get(0);
        if("SEND_TYPE_NOTSEND".equals(status)){
            Calendar a = Calendar.getInstance();
            a.set(Calendar.YEAR,Integer.parseInt(year));
            a.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            a.set(Calendar.DATE, 1);
            a.roll(Calendar.DATE, -1);
            Integer maxDate = a.get(Calendar.DATE);
            sendId = getAttendanceInfoForStaff(null,partyId,maxDate.toString(),year,month,adjustment.get("salaryId"),userLogin);
        }
        if(!UtilValidate.isNotEmpty(sendId)){
            GenericValue sendGeneric = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("staffId",partyId,"year",year,"month",month)).queryOne();
            if(null != sendGeneric){
                sendId = sendGeneric.get("sendId");
            }
        }
        List <GenericValue> salaryList = EntityQuery.use(delegator).select().from("selectSalaryByPartyId").where(UtilMisc.toMap("sendId",sendId)).queryList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR,Integer.parseInt(year))
        cal.set(Calendar.MONTH, Integer.parseInt(month));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String startTime = adjustment.get("startTime").toString();
        Date startDate = sdf.parse(startTime);
        Date newDate = sdf.parse(sdf.format(cal.getTime()));
        if(newDate.getTime() >= startDate.getTime()){
            if("history".equals(flag)){
                List<String> idList = new ArrayList<String>();
                List<GenericValue> salaryHistoryList = EntityQuery.use(delegator).select().from("selectSalaryInfoByPartyId").where(UtilMisc.toMap("salaryId",adjustment.get("salaryId"))).queryList();
                if(null != salaryList && salaryList.size() > 0){
                    for(GenericValue salaryValue : salaryList){
                        idList.add(salaryValue.get("entryId").toString());
                        Map map = new HashMap();
                        map.putAll(salaryValue);
                        for(GenericValue salryHistroy : salaryHistoryList){
                            if(map.get("entryId").toString().equals(salryHistroy.get("entryId").toString())){
                                Double newAmount = Double.parseDouble(map.get("newAmount").toString());
                                Double oldAmount = Double.parseDouble(salryHistroy.get("newAmount").toString());
                                if(newAmount != oldAmount){
                                    map.put("oldAmount",format.format(oldAmount));
                                    map.put("newAmount",format.format(newAmount));
                                }
                            }
                        }
                        salaryRealityList.add(map);
                    }
                    for(GenericValue salryHistroy : salaryHistoryList){
                        Map map = new HashMap();
                        map.putAll(salryHistroy);
                        if(!idList.contains(salryHistroy.get("entryId"))){
                            map.put("oldAmount","");
                            salaryRealityList.add(map);
                        }
                    }
                }else{
                    List<Map<String,Object>> salaryHisList = new ArrayList<Map<String,Object>>();
                    for(GenericValue genericValue : salaryHistoryList){
                        Map map = new HashMap();
                        map.putAll(genericValue);
                        map.put("oldAmount","");
                        salaryHisList.add(map);
                    }
                    salaryRealityList.addAll(salaryHisList);
                }
            }
            message = "success";
        }
    }else{
        message = "请先在员工档案中设置该员工的薪资条目！";
    }
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("message",message);
    map.put("year",year);
    map.put("month",month);
    map.put("partyId",partyId);
    map.put("sendId",sendId);
    map.put("status",status);
    map.put("paySalary",paySalary);
    map.put("salaryHistoryList",salaryRealityList)
    successResult.put("data",map);
    return successResult;
}

/**
 * 保存发送薪资的各个状态
 */
public Map<String,Object> saveSendDetail(){
    DecimalFormat format = new DecimalFormat("0.00");
    String entryInfo = context.get("entryInfo");
    String status = context.get("status");
    String sendId = context.get("sendId");
    String partyId = context.get("partyId");
    String year = context.get("year");
    String month = context.get("month");
    Map map = new HashMap();
    map.put("state",status);
    Double salary = 0;
    //判断是否修改为其他状态，如果是未发状态则需增加薪资条目
    GenericValue genericValue1 = EntityQuery.use(delegator).from("TblSalarySend").where(UtilMisc.toMap("year",year,"month",month,"staffId",partyId)).queryOne();
    if((null != sendId && !"null".equals(sendId) && !"".equals(sendId)) || UtilValidate.isNotEmpty(genericValue1)){
        if(UtilValidate.isNotEmpty(genericValue1)) {
            sendId = genericValue1.get("sendId");
        }
        GenericValue salarySendInfo = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("sendId",sendId)).queryOne();
        //除保存草稿和提交审核外，其他不需要保存薪资条目只，改变状态
        if(null != entryInfo){
            if(null != status && "SEND_TYPE_NOTSEND".equals(status)){
                String[] entrys = entryInfo.split(",");
                List lists = new ArrayList();
                for(String s : entrys){
                    lists.add(s);
                }
                delegator.removeByAnd("TblSendSalaryInfo",UtilMisc.toMap("sendId",sendId));
                for(String entry : entrys){
                    String entryId = entry.substring(0,entry.indexOf(":"));
                        String amount = entry.substring(entry.indexOf(":") + 1,entry.indexOf("-"));
                        GenericValue entryModel = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(UtilMisc.toMap("entryId",entryId)).queryOne();
                        String type = entryModel.get("type").toString();
                        String entryType = entryModel.get("relativeEntry");
                        if(null != entryType && !"".equals(entryType)){
                            if("SEND".equals(type)){
                                salary = salary + (salary * Double.parseDouble(amount));
                            }else if("DISCOUNT".equals(type)){
                                salary = salary - (salary * Double.parseDouble(amount));
                            }
                        }else{
                            if("SEND".equals(type)){
                                salary = salary + Double.parseDouble(amount);
                            }else if("DISCOUNT".equals(type)){
                                salary = salary - Double.parseDouble(amount);
                            }
                        }

                        Map detailMap = new HashMap();
                        detailMap.put("entryId",entryId);
                        detailMap.put("sendId",sendId);
                        detailMap.put("amount",format.format(Double.parseDouble(amount)));
                        String id = delegator.getNextSeqId("TblSendSalaryInfo");
                        detailMap.put("id",id);
                        GenericValue detail = delegator.makeValidValue("TblSendSalaryInfo",detailMap);
                        detail.create();
                    }
//                }
            }
            if(salary < 0 ){
                salary = 0;
            }
            salarySendInfo.put("paySalary",format.format(salary));
        }
        String condition = salarySendInfo.get("sendStatus");
        if("SEND_TYPE_NOTSEND".equals(status) && (null == condition || "".equals(condition))){
            List<GenericValue> list = EntityQuery.use(delegator).select().from("selectSalaryByPartyId").where(UtilMisc.toMap("sendId",sendId)).queryList();
            if(null != list){
                String flag = "1";
                for(GenericValue genericValue : list){
                    String type = genericValue.get("type");
                    String entryId = genericValue.get("entryId");
                    if(!"10001".equals(entryId) && ("SEND".equals(type) || "DISCOUNT".equals(type))){
                        flag = "0";
                        break;
                    }
                }
                salarySendInfo.put("sendStatus",flag);
            }
        }
        salarySendInfo.put("state",status);
        salarySendInfo.store();
    }else{
        sendId = delegator.getNextSeqId("TblSalarySend");
        map.put("sendId",sendId);
        map.put("staffId",partyId);
        map.put("year",year);
        map.put("month",month);

        String[] entrys = entryInfo.split(",");
        List lists = new ArrayList();
        for(String s : entrys){
            lists.add(s);
        }
        for(String list : lists){
            String entryId = list.substring(0,list.indexOf(":"));
            String amount = list.substring(list.indexOf(":") + 1,list.indexOf("-"));
            if("10001".equals(entryId)){
                salary = Double.parseDouble(amount);
                break;
            }
        }
        List<GenericValue> list = EntityQuery.use(delegator).select().from("selectSalaryByPartyId").where(UtilMisc.toMap("sendId",sendId)).queryList();
        if(null != list){
            String flag = "1";
            for(GenericValue genericValue : list){
                String type = genericValue.get("type");
                String entryId = genericValue.get("entryId");
                if(!"10001".equals(entryId) && ("SEND".equals(type) || "DISCOUNT".equals(type))){
                    flag = "0";
                    break;
                }
            }
            map.put("sendStatus",flag);
        }
        map.put("paySalary",format.format(salary));
        GenericValue salarySend = delegator.makeValidValue("TblSalarySend",map);
        salarySend.create();

        for(String entry : lists){
            String entryId = entry.substring(0,entry.indexOf(":"));
            String amount = entry.substring(entry.indexOf(":") + 1,entry.indexOf("-"));
            GenericValue entryModel = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(UtilMisc.toMap("entryId",entryId)).queryOne();
            String type = entryModel.get("type").toString();
            String entryType = entryModel.get("relativeEntry");
            if(null != entryType && !"".equals(entryType)){
                if("SEND".equals(type) && !"10001".equals(entryId)){
                    salary = salary + (salary * Double.parseDouble(amount));
                }else if("DISCOUNT".equals(type)){
                    salary = salary - (salary * Double.parseDouble(amount));
                }
            }else{
                if("SEND".equals(type) && !"10001".equals(entryId)){
                    salary = salary + Double.parseDouble(amount);
                }else if("DISCOUNT".equals(type)){
                    salary = salary - Double.parseDouble(amount);
                }
            }
            Map detailMap = new HashMap();
            detailMap.put("entryId",entryId);
            detailMap.put("sendId",sendId);
            detailMap.put("amount",format.format(Double.parseDouble(amount)));
            String id = delegator.getNextSeqId("TblSendSalaryInfo");
            detailMap.put("id",id);
            GenericValue detail = delegator.makeValidValue("TblSendSalaryInfo",detailMap);
            detail.create();
        }
        salarySend.put("paySalary",format.format(salary))
        salarySend.store();
    }
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map dataMap = new HashMap();
    dataMap.put("message","保存成功");
    dataMap.put("sendId",sendId);
    successResult.put("data",dataMap);
    return successResult;
}

/**
 * 获取某年第一天日期
 * @param year 年份
 * @return Date
 */
public Date getYearFirst(int year){
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(Calendar.YEAR, year);
    Date currYearFirst = calendar.getTime();
    return currYearFirst;
}

/**
 * 获取某年最后一天日期
 * @param year 年份
 * @return Date
 */
public Date getYearLast(int year){
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(Calendar.YEAR, year);
    calendar.roll(Calendar.DAY_OF_YEAR, -1);
    Date currYearLast = calendar.getTime();
    return currYearLast;
}

/**
 *计算当天人员的考勤情况
 *
 * @param department 部门
 * @param partyId 个人
 * @param daySize 天数
 * @param startDate 开始时间
 *
 */
public String getAttendanceInfoForStaff(String department,String partyId,String daySize,String year,String month,salaryId,GenericValue userLogin){
    String startDate = year + "-" + month + "-01";
    String sendId = "";
    //得到员工一个月的考勤信息
    Map<String,Object> staffSuccessMap = dispatcher.runSync("checkingInStatistics",UtilMisc.toMap("department",department,"partyId",partyId,"daySize",daySize,"startDate",startDate,"userLogin",userLogin));
    FastMap<String,Object> stafInfo = (FastMap<String,Object>)staffSuccessMap.get("data");
    if(!UtilValidate.isNotEmpty(stafInfo)){
        stafInfo = new HashMap<String,Object>();
    }
    //迟到情况
    FastMap<String,Object> lateMap = (FastMap<String,Object>)stafInfo.get("lateMap");
    //早退
    FastMap<String,Object> earlyMap = (FastMap<String,Object>)stafInfo.get("earlyMap");
    //正常
    FastMap<String,Object> normalMap = (FastMap<String,Object>)stafInfo.get("normalMap");
    //旷工
    FastMap<String,Object> absenteeismMap = (FastMap<String,Object>)stafInfo.get("absenteeismMap");
    //假期
    FastMap<String,Object> holidayMap = (FastMap<String,Object>)stafInfo.get("holidayMap");
    List<GenericValue> attendanceList = EntityQuery.use(delegator).select().from("TblSalaryOnAttendance").queryList();

    if(null != attendanceList && attendanceList.size() > 0
            && (null != lateMap && lateMap.size() > 0)
            || (null != earlyMap && earlyMap.size() > 0)
            || (null != absenteeismMap && absenteeismMap.size() > 0)) {
        Double basePay = 0;
        //考勤规则
        attendanceList = EntityQuery.use(delegator).select().from("TblSalaryOnAttendance").queryList();
        //查询调整后的基本工资
        List<GenericValue> salaryHistoryList = EntityQuery.use(delegator).select().from("selectSalaryInfoByPartyId").where(UtilMisc.toMap("salaryId",salaryId)).queryList();

        for(GenericValue salaryHistroy : salaryHistoryList){
            if("10001".equals(salaryHistroy.get("entryId"))){
                basePay = Double.parseDouble(salaryHistroy.get("newAmount").toString());
            }
        }
        for (GenericValue attendanceValue : attendanceList) {
            Double penalty = 0;//迟到金额
            String type = attendanceValue.get("type");//处罚类型
            String deductType = attendanceValue.get("deductType");//扣除方法
            String entryId = attendanceValue.get("entryId");
            String startTime = attendanceValue.get("timeRangeStart");
            String endTime = attendanceValue.get("timeRangeEnd");
            String startCount = attendanceValue.get("numberRangeStart");
            String endCount = attendanceValue.get("numberRangeEnd");
            String deductValue = attendanceValue.get("value");//扣除金额或者扣除的百分比
            String workDate = stafInfo.get("workHowLongDay");
            if("LATE".equals(type) || "LEAVE_EARLY".equals(type)) {    //迟到早退
                //迟到时间
                Integer lateTime;
                Integer lateCount;
                String realityType;
                if (UtilValidate.isNotEmpty(lateMap)) {
                    lateTime = Integer.parseInt(lateMap.get("timeCount").toString());
                    lateCount = Integer.parseInt(lateMap.get("dayCount").toString());
                    realityType = "LATE";
                }else if(UtilValidate.isNotEmpty(earlyMap)){
                    lateTime = Integer.parseInt(earlyMap.get("timeCount").toString());
                    lateCount = Integer.parseInt(earlyMap.get("dayCount").toString());
                    realityType ="LEAVE_EARLY";
                }else{
                    continue;
                }
                if(Integer.parseInt(startTime) >= lateTime
                        && lateTime < Integer.parseInt(endTime)
                        && Integer.parseInt(startCount) >= lateCount
                        && lateCount < Integer.parseInt(endCount) && type.equals(realityType)){
                    if(deductType.equals("PERCENT")){
                        penalty = basePay * (Integer.parseInt(deductValue)/100);
                    }else{
                        penalty = Double.parseDouble(deductValue);
                    }
                }
            }else if("ABSENTEEISM".equals(type)){   //旷工
                Integer absenteeismCount = Integer.parseInt(absenteeismMap.get("dayCount").toString());
                if(absenteeismCount <= Integer.parseInt(workDate)){
                    Integer neglectWork  = basePay * (absenteeismCount/Integer.parseInt(workDate));
                    if(Integer.parseInt(startCount) >= absenteeismCount
                            && absenteeismCount < Integer.parseInt(endCount) ){
                        if(deductType.equals("PERCENT")){
                            penalty = basePay * (Double.parseDouble(deductValue)/100);
                        }else{
                            penalty = basePay - Double.parseDouble(deductValue);
                        }
                    }
                    penalty = neglectWork + penalty;//旷工罚金应为扣除当天薪资和处罚
                }else{
                    penalty = basePay;
                }
            }
            if(penalty > 0){
                DecimalFormat format = new DecimalFormat("0.00");
                Double penaltyNum = Double.parseDouble(format.format(penalty));

                if(UtilValidate.isNotEmpty(sendId)){
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("sendId",sendId)).queryOne();
                    Double oldPay = Double.parseDouble(genericValue.get("paySalary").toString());
                    Double newPay = oldPay - penaltyNum;
                    Double pay;
                    if(newPay >= 0){
                        pay = newPay;
                    }else{
                        pay = 0;
                    }
                    genericValue.put("paySalary",format.format(pay));
                    genericValue.store();
                }else{
                    Map<String ,Object>  sendMap = new HashMap<String,Object>();
                    sendMap.put("staffId",partyId);
                    sendMap.put("month",month);
                    sendMap.put("year",year);
                    sendMap.put("state","SEND_TYPE_NOTSEND");
                    sendMap.put("sendStatus","0");
                    GenericValue value = EntityQuery.use(delegator).select().from("TblSalarySend").where(sendMap).queryOne();
                    Double pay = basePay - penaltyNum;
                    sendMap.put("paySalary",format.format(pay));
                    if(UtilValidate.isNotEmpty(value)){
                        value.put("paySalary",format.format(pay))
                        value.store();
                        sendId = value.get("sendId");
                    }else{
                        sendId = delegator.getNextSeqId("TblSalarySend");
                        sendMap.put("sendId",sendId);
                        GenericValue salarySend = delegator.makeValidValue("TblSalarySend",sendMap);
                        salarySend.create()
                    }

                }

                Map<String,Object> map = new HashMap<String,Object>();

                map.put("entryId",entryId);
                map.put("sendId",sendId);
                GenericValue value = EntityQuery.use(delegator).select().from("TblSendSalaryInfo").where(map).queryOne();
                if(UtilValidate.isNotEmpty(value)){
                    value.put("amount", penaltyNum.toString())
                    value.store();
                }else{
                    String id = delegator.getNextSeqId("TblSendSalaryInfo");
                    map.put("id",id);
                    map.put("amount", penaltyNum.toString());
                    GenericValue salaryInfoHistory = delegator.makeValidValue("TblSendSalaryInfo",map);
                    salaryInfoHistory.create();
                }

            }
        }
        return sendId;
    }
}

public Map<String,Object> batchSetSalaryInfo(){
    GenericValue userLogin = (GenericValue)parameters.get("userLogin");
    String partyId = context.get("partyId");
    String year = context.get("year");
    String month = context.get("month");
    String paySalary = context.get("paySalary");
    String status = context.get("status");
    String sendId = context.get("sendId");
    if("SEND_TYPE_NOTSEND".equals(status) || "".equals(status)){
        FastMap<String,Object> submitSalary = dispatcher.runSync("submitPersonSalary",UtilMisc.toMap("partyId",partyId,"sendId",sendId,"status","","year",year,"month",month,"flag","no","paySalary",paySalary,"userLogin",userLogin));
        FastMap<String,Object> submitInfo = (FastMap<String,Object>)submitSalary.get("data");
        sendId = submitInfo.get("sendId");
    }
    FastMap<String,Object> personSalary = dispatcher.runSync("submitPersonSalary",UtilMisc.toMap("partyId",partyId,"sendId",sendId,"status",status,"year",year,"month",month,"flag","history","paySalary",paySalary,"userLogin",userLogin));
    FastMap<String,Object> stafInfo = (FastMap<String,Object>)personSalary.get("data");
    List<Map<String,Object>> list = stafInfo.get("salaryHistoryList");
    String entryInfo = "";
    for(Map<String,Object> map : list){
        entryInfo += map.get("entryId") + ":" + map.get("newAmount") + "-" + map.get("id") + ",";
    }
    entryInfo = entryInfo.substring(0,entryInfo.length() - 1);
    String newStatus = "";
    String flag = "1";
    String message = "保存成功";
    Map<String,Object> sendDetail = new HashMap<String,Object>()
    if("SEND_TYPE_NOTSEND".equals(status)){
        newStatus = "SEND_TYPE_NOTEXAMINE";
        dispatcher.runSync("saveSendDetail",UtilMisc.toMap("partyId",partyId,"sendId",sendId,"status","SEND_TYPE_NOTSEND","year",year,"month",month,"entryInfo",entryInfo,"userLogin",userLogin));
    }else if("SEND_TYPE_NOTEXAMINE".equals(status) || "SEND_TYPE_DISAPPROVE".equals(status)){
        newStatus = "SEND_TYPE_APPROVE";
    }else if("SEND_TYPE_APPROVE".equals(status)){
        newStatus = "SEND_TYPE_SEND";
    }else{
        flag = "2";
        message = "状态异常"
    }
    GenericValue salary = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("staffId",partyId,"year",year,"month",month)).queryOne();
    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("sendId",sendId)).queryOne();
    Map salarySendMap = new HashMap();
    if(null != salary && salary.size() > 0){
        salarySendMap.putAll(salary);
    }else{
        salarySendMap.putAll(genericValue);
    }
    salarySendMap.put("state",newStatus);
    GenericValue tblSalary = delegator.makeValidValue("TblSalarySend", salarySendMap);
    tblSalary.store();
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("flag",flag)
    data.put("message",message);
    successResult.put("data",data);
    return successResult;
}

public Map<String,Object> printSalaryBill(){
    String sendIdList = parameters.get("sendIdList");
    List freeMarkerList = new ArrayList();
    if(null != sendIdList){
        sendIdList = sendIdList.substring(0,sendIdList.length() - 1);
        String[] sendIds = sendIdList.split(",");
        for(String sendId : sendIds){
            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSalarySend").where(UtilMisc.toMap("sendId",sendId)).queryOne();
            GenericValue party = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",genericValue.get("staffId"))).queryOne();
            StringWriter stringWriter = printSalaryBill(genericValue.get("staffId").toString(),genericValue,genericValue.get("month").toString(),genericValue.get("year").toString());
            freeMarkerList.add(stringWriter.toString());
        }
    }
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    successResult.put("freeMarkerList",freeMarkerList);
    return successResult;
}

public StringWriter printSalaryBill(String staffId,GenericValue sendValue,String month,String year){
    String sendId = sendValue.get("sendId");
    monthFor = Long.valueOf(month);
    yearFor = Long.valueOf(year);
    checkingList = delegator.findByAnd("TblCheckingFor",UtilMisc.toMap("staff",staffId,"checkingInMonth",monthFor,"checkingInYear",yearFor))
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date newDate = format.parse(year + "-" + month + "-01");
    Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",staffId,"date",newDate,"userLogin",userLogin));
    GenericValue listOfWork = (GenericValue)successMap.get("returnValue");//获得员工班次
    Integer workHowLongDay = 0;
    if(null != listOfWork && listOfWork.size() > 0){
        workHowLongDay = listOfWork.getInteger("workHowLongDay") - 1;//应出勤折算天数(天)
    }
    Map root = new HashMap();
    GenericValue personMap = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",staffId))[0];
    List <GenericValue> personSalaryItems = delegator.findByAnd("TblSendSalaryInfo",UtilMisc.toMap("sendId",sendId));
    root.put("user_name",personMap.get("fullName")==null?"无":personMap.get("fullName"));
    root.put("user_dept",personMap.get("departmentName")==null?"无":personMap.get("departmentName"));
    root.put("user_code",personMap.get("workerSn")==null?"无":personMap.get("workerSn"));
    root.put("user_position",personMap.get("occupationName")==null?"无":personMap.get("occupationName"));
    root.put("s_year",year);
    root.put("s_month",month);
    root.put("actualDays",workHowLongDay);
    root.put("attendanceDays",checkingList.size()/2);
    root.put("actualSalary", sendValue.get("paySalary"));

    for(Map map1:personSalaryItems){
        if (map1.get("entryId").equals("10001")){
            root.put("salary",map1.get("amount")==null?"":map1.get("amount"));
        }
    }

    List<GenericValue> entryList = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(UtilMisc.toMap("systemType",null)).queryList();
    for(GenericValue entry : entryList){
        String entryId = entry.get("entryId");
        if("10001".equals(entryId)){
            continue;
        }
        String value = "";
        for(GenericValue salaryEntry : personSalaryItems){
            String saEntryId = salaryEntry.get("entryId");
            if(entryId.equals(saEntryId)){
                value = salaryEntry.get("amount");
            }
        }
        root.put(entry.get("amount").toString(),value==""?"无":entry.get("amount").toString())
    }
    salaryMouldList = delegator.findAll("TblSalaryBillMould",false);
    GenericValue salaryMould = EntityQuery.use(delegator).select().from("TblSalaryBillMould").where(UtilMisc.toMap("useState","USING")).queryOne();
    if(!UtilValidate.isNotEmpty(salaryMould)){
        writer = new StringWriter();
        writer.write("请在工资条模版中选择需使用的模版！");
        return  writer;
    }
    String freeMarker =  salaryMould.get("mouldContent");
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new StringTemplateLoader(freeMarker));
    cfg.setDefaultEncoding("GB-2312");
    Template template = cfg.getTemplate("");
    StringWriter writer = new StringWriter();
    try {
        template.process(root, writer);
    }catch (Exception e){
        writer = new StringWriter();
        writer.write("模版错误")
    }
    return  writer;
}
