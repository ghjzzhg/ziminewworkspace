/*
import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery


String workScheduleType = parameters.get("workScheduleType");//type : 个人排班，周期排班，自动排班
String startDate = parameters.get("startDate");
String endDate = parameters.get("endDate");
String staff = parameters.get("staff");
String department = parameters.get("department");
List<Map<String,Object>> valueList = FastList.newInstance();
List<EntityCondition> conditions = FastList.newInstance();
if(UtilValidate.isNotEmpty(startDate)){
    conditions.add(EntityCondition.makeCondition("startDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDate,));
}
if(UtilValidate.isNotEmpty(endDate)){
    conditions.add(EntityCondition.makeCondition("endDate",EntityOperator.LESS_THAN_EQUAL_TO,endDate,));
}

if(workScheduleType == "WORK_SCHEDULE_PERSONAL"){//个人排班
    List<EntityCondition> conditionPer = FastList.newInstance();
    if(UtilValidate.isNotEmpty(staff)){
        conditionPer.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staff));
    }
    if(UtilValidate.isNotEmpty(startDate) && UtilValidate.isEmpty(endDate)){
        conditionPer.add(EntityCondition.makeCondition("scheduleDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDate,));
    } else if(UtilValidate.isNotEmpty(endDate) && UtilValidate.isEmpty(startDate)){
        conditionPer.add(EntityCondition.makeCondition("scheduleDate",EntityOperator.LESS_THAN_EQUAL_TO,endDate,));
    }else if (UtilValidate.isNotEmpty(startDate) && UtilValidate.isNotEmpty(endDate)){
        conditionPer.add(EntityCondition.makeCondition("scheduleDate",EntityOperator.BETWEEN,UtilMisc.toList(startDate,endDate)));
    }
   List<GenericValue> personalWorkSchedule = EntityQuery.use(delegator)
            .from("PersonalWorkScheduleInfo")
            .where(conditionPer)
            .queryList();
    for(GenericValue value : personalWorkSchedule){
        GenericValue listOfWork =  GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",value.get("listOfWorkId")));
        GenericValue staffValue = delegator.findOne("TblStaff",UtilMisc.toMap("partyId",value.get("staff")));
        GenericValue staffDepart = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",staffValue.get("department")));
        Map<String,Object> objectMap = FastMap.newInstance();
        objectMap.put("workScheduleType","个人排班");
        objectMap.put("startDate",value.get("scheduleDate"));
        objectMap.put("endDate",value.get("scheduleDate"));
        objectMap.put("department",staffDepart.get("groupName"));
        objectMap.put("listOfWorkName",listOfWork.get("name"));
        valueList.add(objectMap);
    }

}else if(workScheduleType == "WORK_SCHEDULE_WEEK"){//周期排班
    List<EntityCondition> conditions1 = FastList.newInstance();
    if(UtilValidate.isNotEmpty(department)){
        conditions1.add( EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if(UtilValidate.isNotEmpty(staff)){
        conditions1.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staff));
    }
    EntityCondition condition1 = EntityCondition.makeCondition(EntityJoinOperator.OR,conditions1);
    EntityCondition condition2 = EntityCondition.makeCondition(conditions);
    List<GenericValue> workSchedule = EntityQuery.use(delegator)
            .from("TblWorkSchedule")
            .where(EntityCondition.makeCondition(condition1,EntityJoinOperator.AND,condition2))
            .queryList();
    for(GenericValue value : workSchedule){
        GenericValue listOfWork =  GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",value.get("listOfWorkId")));
        GenericValue staffValue = delegator.findOne("TblStaff",UtilMisc.toMap("partyId",value.get("staff")));
        Map<String,Object> objectMap = FastMap.newInstance();
        objectMap.put("workScheduleType","周期排班");
        objectMap.put("startDate",value.get("startDate"));
        objectMap.put("endDate",value.get("endDate"));
        objectMap.put("department",value.get("department"));
        objectMap.put("staff",staffValue.get("fullName"));
        objectMap.put("listOfWorkName",listOfWork.get("name"));
        valueList.add(objectMap);
    }

}else if(workScheduleType == "WORK_SCHEDULE_AUTO"){//自动排班

    List<GenericValue> autoWorkSchedule =  EntityQuery.use(delegator)
            .from("TblAutoSchedule")
            .where(conditions)
            .queryList();
    for(GenericValue value : autoWorkSchedule){
        List<GenericValue> autoScheduleDepartment = null;
        if(UtilValidate.isNotEmpty(department)){
            autoScheduleDepartment = EntityQuery.use(delegator)
                    .from("TblAutoScheduleDepartment")
                    .where(EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("department",EntityOperator.EQUALS,department),
                        EntityCondition.makeCondition("autoScheduleId",EntityOperator.EQUALS,value.get("autoScheduleId"))
                    )))
                    .queryList();
        }else {
            autoScheduleDepartment = EntityQuery.use(delegator)
                    .from("TblAutoScheduleDepartment")
                    .queryList();
        }
        List<GenericValue> workOrder= EntityQuery.use(delegator)
                .from("TblAutoScheduleWorkOrder")
                .where(EntityCondition.makeCondition("autoScheduleId",EntityOperator.EQUALS,value.get("autoScheduleId")))
                .queryList();

        for(int i = 0; i < autoScheduleDepartment.size(); i++){
            Map<String,Object> autoScheduleDepartMap = FastMap.newInstance();
            GenericValue autoDepart = autoScheduleDepartment.get(i);
            int order = Integer.valueOf(autoDepart.get("scheduleOrder"));
            int index = order;
            for(int j = 0; j < workOrder.size(); j++){
                GenericValue workOrderMap  = workOrder.get(index);
                autoScheduleDepartMap.put("workScheduleType","周期排班");
                autoScheduleDepartMap.put("startDate",value.get("startDate"));
                autoScheduleDepartMap.put("endDate",value.get("endDate"));
                autoScheduleDepartMap.put("department",autoDepart.get("department"));
                GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",workOrderMap.get("listOfWorkId")));
                autoScheduleDepartMap.put("listOfWorkName",listOfWork.get("name"));
                if(index >= workOrder.size()){
                    index = 0;
                }else {
                    index++;
                }
                valueList.add(autoScheduleDepartMap);
            }
        }
    }
}

context.valueList = valueList;*/
