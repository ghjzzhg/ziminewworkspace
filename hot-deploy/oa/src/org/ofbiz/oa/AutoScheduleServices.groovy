package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import net.fortuna.ical4j.model.WeekDay
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.oa.im.EventTO
import org.ofbiz.service.ServiceUtil
import java.text.ParseException
import java.text.SimpleDateFormat


public Map<String, Object> saveAutoSchedule() {
    Map success = ServiceUtil.returnSuccess();
    String autoWorkScheduleId = context.get("autoWorkScheduleId");
    GenericValue userLogin = context.get("userLogin");
    Date startDate = context.get("startDate");
    Date endDate = context.get("endDate");
    String workGroups = context.get("workGroups");
    String workOrders = context.get("workOrders");
    String msg = "";
    try {
        if (UtilValidate.isNotEmpty(autoWorkScheduleId) && !" ".equals(autoWorkScheduleId)) {
            //TODO 跟新
            delegator.removeByCondition("TblDataScope",EntityCondition.makeCondition(EntityCondition.makeCondition("entityName",EntityOperator.EQUALS,"TblAutoWorkSchedule"),
                    EntityJoinOperator.AND,EntityCondition.makeCondition("dataId",EntityOperator.EQUALS,autoWorkScheduleId)));
            List<GenericValue> scheduleIndex = delegator.findByAnd("TblWorkScheduleIndex",UtilMisc.toMap("dataValue",autoWorkScheduleId));
            for(GenericValue value : scheduleIndex){
                value.remove();
            }
            List<GenericValue> autoScheduleDepartment = delegator.findByAnd("TblAutoScheduleDepartment",UtilMisc.toMap("autoWorkScheduleId",autoWorkScheduleId));
            for(GenericValue value : autoScheduleDepartment){
                value.remove();
            }
            List<GenericValue> autoScheduleWorkOrderDel = delegator.findByAnd("TblAutoScheduleWorkOrder",UtilMisc.toMap("autoWorkScheduleId",autoWorkScheduleId));
            for(GenericValue value : autoScheduleWorkOrderDel){
                value.remove();
            }
            String[] workGroupArr = workGroups.split(",");
            String[] workOrderArr = workOrders.split(",");
            if(UtilValidate.isNotEmpty(workGroups) && UtilValidate.isNotEmpty(workOrders) && workGroupArr.size() >= workOrderArr.size()){
                int depOrder = 1;
                for(String depId : workGroupArr){
                    String autoScheduleDepId = delegator.getNextSeqId("TblAutoScheduleDepartment");
                    GenericValue saveAutoScheduleDep = delegator.makeValidValue("TblAutoScheduleDepartment",
                            UtilMisc.toMap("autoScheduleDepId",autoScheduleDepId,"autoWorkScheduleId",autoWorkScheduleId,"department",depId,"scheduleOrder",depOrder));
                    saveAutoScheduleDep.create();
                    depOrder++;
                    //排班索引表
                    String id = delegator.getNextSeqId("TblWorkScheduleIndex");
                    Map<String, Object> workScheduleIndexMap = FastMap.newInstance();
                    workScheduleIndexMap.put("id", id);
                    workScheduleIndexMap.put("department", depId);
                    workScheduleIndexMap.put("startDate", new java.sql.Date(startDate.getTime()));
                    workScheduleIndexMap.put("endDate", new java.sql.Date(endDate.getTime()));
                    workScheduleIndexMap.put("dataValue", autoWorkScheduleId);
                    workScheduleIndexMap.put("type","auto");//排班类型
                    GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap);
                    workScheduleIndex.create();
                }
                int lowOrder = 1;
                for(String listOfWorkId : workOrderArr){
                    String autoShceduleWorkOrderId = delegator.getNextSeqId("TblAutoScheduleWorkOrder");
                    GenericValue autoScheduleWorkOrder = delegator.makeValidValue("TblAutoScheduleWorkOrder",
                            UtilMisc.toMap("autoShceduleWorkOrderId",autoShceduleWorkOrderId,"autoWorkScheduleId",autoWorkScheduleId,"listOfWorkId",listOfWorkId,"scheduleOrder",lowOrder));
                    autoScheduleWorkOrder.create();
                    lowOrder++;
                }
                dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName","TblAutoWorkSchedule","deptOnly",workGroups,
                        "deptLike","","levelOnly","","levelLike","","positionOnly","",
                        "positionLike","","userValue","","dataId",autoWorkScheduleId,"userLogin",userLogin));
            }
            msg = "更新成功！";

        } else {
            autoWorkScheduleId = delegator.getNextSeqId("TblAutoWorkSchedule");
            context.put("autoWorkScheduleId",autoWorkScheduleId);
            GenericValue saveAutoSchedule = delegator.makeValidValue("TblAutoWorkSchedule",
                    UtilMisc.toMap("autoWorkScheduleId",autoWorkScheduleId));
            saveAutoSchedule.create();
            String[] workGroupArr = workGroups.split(",");
            String[] workOrderArr = workOrders.split(",");
            if(UtilValidate.isNotEmpty(workGroups) && UtilValidate.isNotEmpty(workOrders) && workGroupArr.size() >= workOrderArr.size()){
                int depOrder = 1;
                for(String depId : workGroupArr){
                    String autoScheduleDepId = delegator.getNextSeqId("TblAutoScheduleDepartment");
                    GenericValue saveAutoScheduleDep = delegator.makeValidValue("TblAutoScheduleDepartment",
                            UtilMisc.toMap("autoScheduleDepId",autoScheduleDepId,"autoWorkScheduleId",autoWorkScheduleId,"department",depId,"scheduleOrder",depOrder));
                    saveAutoScheduleDep.create();
                    depOrder++;
                    //排班索引表
                    String id = delegator.getNextSeqId("TblWorkScheduleIndex");
                    Map<String, Object> workScheduleIndexMap = FastMap.newInstance();
                    workScheduleIndexMap.put("id", id);
                    workScheduleIndexMap.put("department", depId);
                    workScheduleIndexMap.put("startDate", new java.sql.Date(startDate.getTime()));
                    workScheduleIndexMap.put("endDate", new java.sql.Date(endDate.getTime()));
                    workScheduleIndexMap.put("dataValue", autoWorkScheduleId);
                    workScheduleIndexMap.put("type","auto");//排班类型
                    GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap);
                    workScheduleIndex.create();
                }
                int lowOrder = 1;
                for(String listOfWorkId : workOrderArr){
                    String autoShceduleWorkOrderId = delegator.getNextSeqId("TblAutoScheduleWorkOrder");
                    GenericValue autoScheduleWorkOrder = delegator.makeValidValue("TblAutoScheduleWorkOrder",
                            UtilMisc.toMap("autoShceduleWorkOrderId",autoShceduleWorkOrderId,"autoWorkScheduleId",autoWorkScheduleId,"listOfWorkId",listOfWorkId,"scheduleOrder",lowOrder));
                    autoScheduleWorkOrder.create();
                    lowOrder++;
                }
            }
            dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName","TblAutoWorkSchedule","deptOnly",workGroups,
                    "deptLike","","levelOnly","","levelLike","","positionOnly","",
                    "positionLike","","userValue","","dataId",autoWorkScheduleId,"userLogin",userLogin));
            msg = "自动排班成功！";
        }
    } catch (GenericEntityException e) {
        msg = "服务出错！"
    }
    success.put("returnValue", msg)
    return success;
}

public Map<String, Object> deleteautoSchedule() {
    Map<String,Object> success = ServiceUtil.returnSuccess();
    String autoWorkScheduleId = context.get("autoWorkScheduleId");
    String msg = "";
    try {
        List<GenericValue> scheduleIndex = delegator.findByAnd("TblWorkScheduleIndex",UtilMisc.toMap("dataValue",autoWorkScheduleId, "type", "auto"));
        for(GenericValue value : scheduleIndex){
            value.remove();
        }
        List<GenericValue> autoScheduleDepartment = delegator.findByAnd("TblAutoScheduleDepartment",UtilMisc.toMap("autoWorkScheduleId",autoWorkScheduleId));
        for(GenericValue value : autoScheduleDepartment){
            value.remove();
        }
        List<GenericValue> autoScheduleWorkOrder = delegator.findByAnd("TblAutoScheduleWorkOrder",UtilMisc.toMap("autoWorkScheduleId",autoWorkScheduleId));
        for(GenericValue value : autoScheduleWorkOrder){
            value.remove();
        }
        msg = "删除成功！"
    }catch (GenericEntityException e){

    }
    success.put("returnValue",msg)
    return success;
}
public Map<String,Object> searchAutoWorkSchedule(){
    Map<String,Object> success = ServiceUtil.returnSuccess();
    String type = context.get("type");
    Date startDate = context.get("startDate");
    Date endDate = context.get("endDate");
    String staffId = context.get("staff");
    String departmentId = context.get("departmentId");
    try{
        List<EntityCondition> conditions = FastList.newInstance();
        if(UtilValidate.isNotEmpty(type)){
            conditions.add(EntityCondition.makeCondition("type",EntityOperator.EQUALS,type));
        }
        if(UtilValidate.isNotEmpty(startDate)){
            conditions.add(EntityCondition.makeCondition("startDate",EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(startDate.getTime())));
        }
        if(UtilValidate.isNotEmpty(endDate)){
            conditions.add(EntityCondition.makeCondition("endDate",EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(endDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
            conditions.add(EntityCondition.makeCondition("startDate",EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(startDate.getTime())));
            conditions.add(EntityCondition.makeCondition("endDate",EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(endDate.getTime())));
        }
        if(UtilValidate.isNotEmpty(staffId)){
            conditions.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staffId));
        }
        if(UtilValidate.isNotEmpty(departmentId)){
            conditions.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,departmentId));
        }
        List<Map<String,Object>> valueList = FastList.newInstance();
        List<GenericValue> workScheduleIndexList = null;
        if(conditions.size() > 0){
            workScheduleIndexList = EntityQuery.use(delegator)
                    .select("type","startDate","endDate","dataValue")
                    .from("TblWorkScheduleIndex")
                    .where(EntityCondition.makeCondition(conditions))
                    .distinct()
                    .orderBy("startDate DESC")
                    .queryList();
        }else {
            workScheduleIndexList = EntityQuery.use(delegator)
                    .select("type","startDate","endDate","dataValue")
                    .from("TblWorkScheduleIndex")
                    .orderBy("startDate DESC")
                    .queryList();
        }

        for(GenericValue value : workScheduleIndexList){
            Map<String,Object> valueMap = FastMap.newInstance();
            valueMap.putAll(value);
            String autoWorkScheduleId = value.get("dataValue");
            List<GenericValue> departmentList = EntityQuery.use(delegator)
                    .select("department")
                    .from("TblAutoScheduleDepartment")
                    .where(EntityCondition.makeCondition("autoWorkScheduleId",EntityOperator.EQUALS,autoWorkScheduleId))
                    .orderBy("scheduleOrder")
                    .queryList();
            String departStr = "";
            String groupNameStr = "";
            for(GenericValue depart : departmentList){
                GenericValue genericValue = EntityQuery.use(delegator)
                        .select("groupName")
                        .from("PartyGroup")
                        .where(EntityCondition.makeCondition("partyId",depart.get("department")))
                        .queryOne();
                departStr += depart.get("department") + ",";
                groupNameStr += genericValue.get("groupName") + ",";
            }
            List<GenericValue> listOfWorkList = EntityQuery.use(delegator)
                    .select("listOfWorkId")
                    .from("TblAutoScheduleWorkOrder")
                    .where(EntityCondition.makeCondition("autoWorkScheduleId",EntityOperator.EQUALS,autoWorkScheduleId))
                    .orderBy("scheduleOrder")
                    .queryList();
            String listOfWorkStr = "";
            String listOfWorkName = "";
            for(GenericValue listOfWork : listOfWorkList){
                GenericValue genericValue = EntityQuery.use(delegator)
                        .select("listOfWorkName")
                        .from("TblListOfWork")
                        .where(EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId")))
                        .queryOne();
                if(UtilValidate.isNotEmpty(genericValue)){
                    listOfWorkName += genericValue.get("listOfWorkName") + ",";
                    listOfWorkStr += listOfWork.get("listOfWorkId") + ",";
                }
            }
            valueMap.put("dataValue",value.get("dataValue"));
            valueMap.put("department",departStr);
            valueMap.put("groupName",groupNameStr);
            valueMap.put("typeDesc","自动排班");
            valueMap.put("listOfWork",listOfWorkStr);
            valueMap.put("listOfWorkName",listOfWorkName);
            valueList.add(valueMap);
        }
        success.put("returnValue",valueList);
    }catch (GenericEntityException e){

    }
    return success;
}


delegator.findOne("TblStaff",UtilMisc)