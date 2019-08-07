package org.ofbiz.oa
import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil



public Map<String, Object> saveWorkSchedule(){
    Map success = ServiceUtil.returnSuccess();
    List<String> listOfWorkByWeekIdList = context.get("listOfWorkByWeekId");
    String workCycleScheduleId = context.get("workCycleScheduleId");
    Date startDate = context.get("startDate");
    Date endDate = context.get("endDate");
    String staff = context.get("staff");
    String department = context.get("department");
    String msg = "周期排班成功！"
    List<EntityCondition> condition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(staff)){
        condition.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staff));
    }else {
        condition.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }

    try{
        List<GenericValue> wcScheduleWorkWeekList = FastList.newInstance();
        int sortNum = 1;
        if(UtilValidate.isNotEmpty(workCycleScheduleId)){
            condition.add(EntityCondition.makeCondition("dataValue",EntityOperator.NOT_EQUAL,workCycleScheduleId));
            //TODO 跟新
            delegator.removeByCondition("TblWorkCycleScheduleWorkWeek",EntityCondition.makeCondition("workCycleScheduleId",workCycleScheduleId));
            if(checkDateTime(condition,success)){
               return success;
            }
            for(String str : listOfWorkByWeekIdList){
                wcScheduleWorkWeekList.add(delegator.makeValidValue("TblWorkCycleScheduleWorkWeek",UtilMisc.toMap("workCycleScheduleId",workCycleScheduleId,"listOfWorkByWeekId",str,"weekSort",sortNum)));
                sortNum ++;
            }
            delegator.storeAll(wcScheduleWorkWeekList);

            GenericValue updateWorkSchedule = delegator.makeValidValue("TblWorkCycleSchedule",context);
            updateWorkSchedule.store();
            GenericValue scheduleIndex = delegator.findByAnd("TblWorkScheduleIndex",UtilMisc.toMap("dataValue",workCycleScheduleId),null,false).get(0);
            String id = scheduleIndex.get("id");
            //注册索引
            if(UtilValidate.isNotEmpty(department)){
                Map<String, Object> workScheduleIndexMap2 = FastMap.newInstance();
                workScheduleIndexMap2.put("id", id);
                workScheduleIndexMap2.put("department", department);
                workScheduleIndexMap2.put("startDate", new java.sql.Date(startDate.getTime()));
                workScheduleIndexMap2.put("endDate", new java.sql.Date(endDate.getTime()));
                workScheduleIndexMap2.put("dataValue", workCycleScheduleId);
                workScheduleIndexMap2.put("type", "cycle_department");
                GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap2);
                workScheduleIndex.store();

            }else {
                Map<String,Object> workScheduleIndexMap = FastMap.newInstance();
                workScheduleIndexMap.put("id",id);
                workScheduleIndexMap.put("staff",staff);
                workScheduleIndexMap.put("startDate",new java.sql.Date(startDate.getTime()));
                workScheduleIndexMap.put("endDate",new java.sql.Date(endDate.getTime()));
                workScheduleIndexMap.put("dataValue",workCycleScheduleId);
                workScheduleIndexMap.put("type","cycle_personal");
                GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex",workScheduleIndexMap);
                workScheduleIndex.store();
            }
            msg = "周期排班更新成功!"
        }else{
            if(checkDateTime(condition,success)){
                return success;
            }
            //保存周期排班
            workCycleScheduleId = delegator.getNextSeqId("TblWorkCycleSchedule");
            context.put("workCycleScheduleId",workCycleScheduleId);
            GenericValue workCycleSchedule = delegator.makeValidValue("TblWorkCycleSchedule",context);
            workCycleSchedule.create();

            for(String str : listOfWorkByWeekIdList){
                wcScheduleWorkWeekList.add(delegator.makeValidValue("TblWorkCycleScheduleWorkWeek",UtilMisc.toMap("workCycleScheduleId",workCycleScheduleId,"listOfWorkByWeekId",str,"weekSort",sortNum)));
                sortNum ++;
            }
            delegator.storeAll(wcScheduleWorkWeekList);

            //注册索引
            String id = delegator.getNextSeqId("TblWorkScheduleIndex");
            if(UtilValidate.isNotEmpty(department)){
                Map<String, Object> workScheduleIndexMap2 = FastMap.newInstance();
                workScheduleIndexMap2.put("id", id);
                workScheduleIndexMap2.put("department", department);
                workScheduleIndexMap2.put("startDate", new java.sql.Date(startDate.getTime()));
                workScheduleIndexMap2.put("endDate", new java.sql.Date(endDate.getTime()));
                workScheduleIndexMap2.put("dataValue", workCycleScheduleId);
                workScheduleIndexMap2.put("type", "cycle_department");
                GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap2);
                workScheduleIndex.create();
            }else {
                Map<String,Object> workScheduleIndexMap = FastMap.newInstance();
                workScheduleIndexMap.put("id",id);
                workScheduleIndexMap.put("staff",staff);
                workScheduleIndexMap.put("startDate",new java.sql.Date(startDate.getTime()));
                workScheduleIndexMap.put("endDate",new java.sql.Date(endDate.getTime()));
                workScheduleIndexMap.put("dataValue",workCycleScheduleId);
                workScheduleIndexMap.put("type","cycle_personal");
                GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex",workScheduleIndexMap);
                workScheduleIndex.create();
            }

        }
    }catch (GenericEntityException e){
        msg = "服务出错！"
    }
    success.put("returnValue",msg)
    return success;
}

public boolean checkDateTime(List<EntityCondition> condition,Map success){
    List<GenericValue> workScheduleList = EntityQuery.use(delegator).from("TblWorkScheduleIndex").where(condition).queryList();
    for(GenericValue value : workScheduleList){
        Date start = value.getDate("startDate");
        Date end = value.getDate("endDate");
        int compare1 = startDate.compareTo(end);
        int compare2 = endDate.compareTo(start);
        if((compare1 == -1 || compare1 == 0) && (compare2 == 1 || compare2 == 0)){
            success.put("returnValue","周期排班时间重叠，请调整时间后保存");
            return  true;
        }
    }
    return false;
}
public Map<String, Object> deleteWorkCycleSchedule(){
    Map success = ServiceUtil.returnSuccess();
    String workCycleScheduleId = context.get("workCycleScheduleId");
    String msg = "";
    try {
        delegator.removeByCondition("TblWorkScheduleIndex",EntityCondition.makeCondition(
                EntityCondition.makeCondition(
                        EntityCondition.makeCondition("type", EntityOperator.EQUALS, "cycle_department"),
                        EntityOperator.OR,
                        EntityCondition.makeCondition("type", EntityOperator.EQUALS, "cycle_personal")),EntityOperator.AND,
                EntityCondition.makeCondition(
                        EntityCondition.makeCondition("dataValue", EntityOperator.EQUALS, workCycleScheduleId)
                )));
        delegator.removeByCondition("TblWorkCycleScheduleWorkWeek",EntityCondition.makeCondition(UtilMisc.toMap("workCycleScheduleId",workCycleScheduleId)));
        delegator.removeByCondition("TblWorkCycleSchedule",EntityCondition.makeCondition(UtilMisc.toMap("workCycleScheduleId",workCycleScheduleId)));
        msg = "删除成功！"
    }catch (GenericEntityException e){
        return ServiceUtil.returnError("删除失败！");
    }
    return ServiceUtil.returnSuccess("删除成功！");
}
public Map<String, Object> searchWorkCycleSchedule(){
    Map success = ServiceUtil.returnSuccess();
   /* String type = context.get("type");*/
    Date startDate = context.get("startDate");
    Date endDate = context.get("endDate");
    String staffId = context.get("staff");
    String departmentId = context.get("departmentId");
    try {
        List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "cycle_department"),EntityJoinOperator.OR,EntityCondition.makeCondition("type", EntityOperator.EQUALS, "cycle_personal")));
        if (UtilValidate.isNotEmpty(startDate)) {
            conditions.add(EntityCondition.makeCondition("startDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(startDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(endDate)) {
            conditions.add(EntityCondition.makeCondition("endDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(endDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
            conditions.add(EntityCondition.makeCondition("startDate",EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(startDate.getTime())));
            conditions.add(EntityCondition.makeCondition("endDate",EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(endDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(staffId)) {
            conditions.add(EntityCondition.makeCondition("staff", EntityOperator.EQUALS, staffId));
        }
        if (UtilValidate.isNotEmpty(departmentId)) {
            conditions.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, departmentId));
        }


        List<GenericValue> workCycleScheduleInfoList = EntityQuery.use(delegator)
                .select("cycleSize","cycleSizeUnit","type", "startDate", "endDate", "dataValue","staff","department")
                .from("WorkCycleScheduleInfo")
                .where(EntityCondition.makeCondition(conditions))
                .distinct()
                .orderBy("startDate DESC")
                .queryList();
        List<Map<String, Object>> valueList = FastList.newInstance();
        for(GenericValue genericValue : workCycleScheduleInfoList){
            Map<String,Object> valueMap = FastMap.newInstance();
            valueMap.putAll(genericValue);
            List<GenericValue> wcsWorkWeekList = delegator.findByAnd("TblWorkCycleScheduleWorkWeek",UtilMisc.toMap("workCycleScheduleId",genericValue.getString("dataValue")),null,false);
            String listOfWorkByWeekNames = "";
            for(GenericValue wcsWorkWeek : wcsWorkWeekList){
                GenericValue listOfWorkByWeek = delegator.findOne("TblListOfWorkByWeek",UtilMisc.toMap("listOfWorkByWeekId",wcsWorkWeek.getString("listOfWorkByWeekId")),false);
                listOfWorkByWeekNames += listOfWorkByWeek.getString("listOfWorkByWeekName") + "、";
            }
            valueMap.put("listOfWorkByWeekNames",listOfWorkByWeekNames);
            valueList.add(valueMap);
        }
        success.put("returnValue",valueList);
    }catch (GenericEntityException e){

    }
    return success;
}
