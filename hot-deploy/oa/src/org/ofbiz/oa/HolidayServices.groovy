package org.ofbiz.oa

import javolution.util.FastList
import net.fortuna.ical4j.model.WeekDay
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.oa.im.EventTO
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat



public Map<String, Object> searchOvertime() {
    Map success = ServiceUtil.returnSuccess();
    Timestamp startTime = context.get("overtimeStartDate");
    Timestamp endTime = context.get("overtimeEndDate");
    String staff = context.get("overtimeStaff");
    String department = context.get("overtimeDepartment");
    String overtimeType = context.get("overTimeType");

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

    List<EntityCondition> conditions = FastList.newInstance();
    if(UtilValidate.isNotEmpty(startTime)&&UtilValidate.isEmpty(endTime)){
        conditions.add(EntityCondition.makeCondition("startTime",EntityOperator.GREATER_THAN_EQUAL_TO,startTime));
    }
    if(UtilValidate.isNotEmpty(endTime)&&UtilValidate.isEmpty(startTime)){
        conditions.add(EntityCondition.makeCondition("endTime",EntityOperator.LESS_THAN_EQUAL_TO,endTime));
    }
    if (UtilValidate.isNotEmpty(endTime) && UtilValidate.isNotEmpty(startTime)) {
        conditions.add(EntityCondition.makeCondition("startTime",EntityOperator.GREATER_THAN_EQUAL_TO,startTime));
        conditions.add(EntityCondition.makeCondition("endTime",EntityOperator.LESS_THAN_EQUAL_TO,endTime));
    }
    if(UtilValidate.isNotEmpty(staff)){
        conditions.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staff));
    }
    if(UtilValidate.isNotEmpty(department)){
        conditions.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if(UtilValidate.isNotEmpty(overtimeType)){
        conditions.add(EntityCondition.makeCondition("overtimeType",EntityOperator.EQUALS,overtimeType));
    }
    EntityListIterator overtimeList = EntityQuery.use(delegator)
            .from("TblOvertime")
            .where(conditions)
            .orderBy("startTime DESC")
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != overtimeList && overtimeList.getResultsSizeAfterPartialList() > 0){
        totalCount = overtimeList.getResultsSizeAfterPartialList();
        pageList = overtimeList.getPartialList(lowIndex, viewSize);
    }
    overtimeList.close();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("overtimeStartDate",context.get("overtimeStartDate"));
    map.put("overtimeEndDate",context.get("overtimeEndDate"));
    map.put("overtimeStaff",staff);
    map.put("overtimeDepartment",department);
    map.put("overtimeType",overtimeType);
    success.put("returnValue",map);
    return success;
}
public Map<String, Object> saveOvertime() {
    Map success = ServiceUtil.returnSuccess();
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    String overtimeId = context.get("overtimeId");
    context.put("startDate", new java.sql.Date(format1.parse(context.get("startTime").toString()).getTime()));
    context.put("endDate", new java.sql.Date(format1.parse(context.get("endTime").toString()).getTime()));
    try {
        if (UtilValidate.isNotEmpty(overtimeId)) {
            GenericValue update = delegator.makeValidValue("TblOvertime",context);
            update.store();
            msg = "加班更新成功！";
        } else {
            overtimeId = delegator.getNextSeqId("TblOvertime");
            context.put("overtimeId", overtimeId);
            context.put("overtimeState","PERSON_ONE");//未审核状态
            GenericValue save = delegator.makeValidValue("TblOvertime",context);
            save.create();
            msg = "加班添加成功！";
        }
    } catch (GenericEntityException e) {
        msg = "服务出错！"
    }
    success.put("returnValue", msg)
    return success;
}
public Map<String, Object> saveAuditOvertime() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String overtimeId = context.get("overtimeId");
    String auditRemarks = context.get("auditRemarks");
    String overtimeState = context.get("overtimeState");
    String msg = "";
    if (UtilValidate.isNotEmpty(overtimeId)){
        Map map = new HashMap();
        map.put("overtimeId", overtimeId);
        map.put("auditStartTime", context.get("auditStartTime"));
        map.put("auditEndTime", context.get("auditEndTime"));
        map.put("auditRemarks", auditRemarks);
        map.put("overtimeState", overtimeState);
        delegator.store(delegator.makeValidValue("TblOvertime",map));
        msg = "审核完成";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
public Map<String, Object> deleteOvertime() {
    Map success = ServiceUtil.returnSuccess();
    String msg = "";
    String overtimeId = context.get("overtimeId");
    if(UtilValidate.isNotEmpty(overtimeId)){
        try {
            GenericValue delete = delegator.makeValidValue("TblOvertime",UtilMisc.toMap("overtimeId",overtimeId));
            delete.remove();
            msg = "删除成功！"
        }catch (GenericEntityException e){
            msg = "删除失败！"
        }
        success.put("returnValue",msg);
    }
    return success;
}

public Map<String, Object> saveHoliday() {
    Map success = ServiceUtil.returnSuccess();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    String holidayId = context.get("holidayId");
    context.put("startDate", new java.sql.Date(format1.parse(context.get("startTime").toString()).getTime()));
    context.put("endDate", new java.sql.Date(format1.parse(context.get("endTime").toString()).getTime()));
    try {
        if (UtilValidate.isNotEmpty(holidayId)) {
            GenericValue updateHoliday = delegator.makeValidValue("TblHoliday",context);
            updateHoliday.store();
            msg = "假期更新成功！";
        } else {
            holidayId = delegator.getNextSeqId("TblHoliday");
            context.put("holidayId",holidayId);
            context.put("pass","Y");
            GenericValue saveHoliday = delegator.makeValidValue("TblHoliday",context);
            saveHoliday.create();
            msg = "假期添加成功！";
        }
    } catch (GenericEntityException e) {
        msg = "服务出错！"
    }
    success.put("returnValue", msg)
    return success;
}
public Map<String, Object> searchHoliday() {
    Map success = ServiceUtil.returnSuccess();
    Timestamp startTime = context.get("holidayStartDate");
    Timestamp endTime = context.get("holidayEndDate");
    String staff = context.get("holidayStaff");
    String department = context.get("holidayDepartment");

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

    List<EntityCondition> conditions = FastList.newInstance();
    if(UtilValidate.isNotEmpty(startTime)&&UtilValidate.isEmpty(endTime)){
        conditions.add(EntityCondition.makeCondition("startTime",EntityOperator.GREATER_THAN_EQUAL_TO,startTime));
    }
    if(UtilValidate.isNotEmpty(endTime)&&UtilValidate.isEmpty(startTime)){
        conditions.add(EntityCondition.makeCondition("endTime",EntityOperator.LESS_THAN_EQUAL_TO,endTime));
    }
    if (UtilValidate.isNotEmpty(endTime) && UtilValidate.isNotEmpty(startTime)) {
        conditions.add(EntityCondition.makeCondition("startTime",EntityOperator.GREATER_THAN_EQUAL_TO,startTime));
        conditions.add(EntityCondition.makeCondition("endTime",EntityOperator.LESS_THAN_EQUAL_TO,endTime));
    }
    if(UtilValidate.isNotEmpty(staff)){
        conditions.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staff));
    }
    if(UtilValidate.isNotEmpty(department)){
        conditions.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    EntityListIterator holidayList = EntityQuery.use(delegator)
            .from("TblHoliday")
            .where(conditions)
            .orderBy("startTime DESC")
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != holidayList && holidayList.getResultsSizeAfterPartialList() > 0){
        totalCount = holidayList.getResultsSizeAfterPartialList();
        pageList = holidayList.getPartialList(lowIndex, viewSize);
    }
    holidayList.close();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("holidayStartDate",context.get("holidayStartDate"));
    map.put("holidayEndDate",context.get("holidayEndDate"));
    map.put("staff",context.get("staff"));
    map.put("department",context.get("department"));
    success.put("returnValue",map);
    return success;
}
public Map<String, Object> deleteHoliday() {
    Map success = ServiceUtil.returnSuccess();
    String msg = "";
    String holidayId = context.get("holidayId");
    if(UtilValidate.isNotEmpty(holidayId)){
        try {
            GenericValue deleteHoliday = delegator.makeValidValue("TblHoliday",UtilMisc.toMap("holidayId",holidayId));
            deleteHoliday.remove();
            msg = "假期删除成功！"
        }catch (GenericEntityException e){

        }
        success.put("returnValue",msg);
    }
    return success;
}