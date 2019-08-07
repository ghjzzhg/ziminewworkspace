package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
import java.text.SimpleDateFormat

public Map<String,Object> searchWorkSchedule(){
    String auto = "auto";
    String cycle_department = "cycle_department";
    String cycle_personal = "cycle_personal";
    String personal = "personal";
    Map<String,Object> valueMap = FastMap.newInstance();
    Map<String,Object> success = ServiceUtil.returnSuccess();
    String type = context.get("type");
    Date startDate = context.get("workStartDate");
    Date endDate = context.get("workEndDate");
    String staffId = context.get("workStaff");
    String departmentId = context.get("workDepartment");
    GenericValue userLogin = context.get("userLogin");
    if(UtilValidate.isEmpty(type)){
        type = "WORK_SCHEDULE_AUTO";
    }

    if("WORK_SCHEDULE_AUTO".equals(type)){ //自动排班
        success = dispatcher.runSync("searchAutoWorkSchedule",UtilMisc.toMap("type",auto,"startDate",startDate,"endDate",endDate,"departmentId",departmentId,"userLogin",userLogin));
        valueMap.put("workScheduleList",success.get("returnValue"));
        valueMap.put("type","WORK_SCHEDULE_AUTO");
        success.put("returnValue",valueMap);
    }
    if("WORK_SCHEDULE_WEEK".equals(type)){ //周期排班
        success = dispatcher.runSync("searchWorkCycleSchedule",UtilMisc.toMap("startDate",startDate,"endDate",endDate,"departmentId",departmentId,"staff",staffId,"userLogin",userLogin));
        valueMap.put("workScheduleList",success.get("returnValue"));
        valueMap.put("type","WORK_SCHEDULE_WEEK");
        success.put("returnValue",valueMap);
    }
    if("WORK_SCHEDULE_PERSONAL".equals(type)){//个人排班
        success = dispatcher.runSync("searchPersonalSchedule",UtilMisc.toMap("startDate",startDate,"endDate",endDate,"departmentId",departmentId,"staff",staffId,"userLogin",userLogin));
        valueMap.put("workScheduleList",success.get("returnValue"));
        valueMap.put("type","WORK_SCHEDULE_PERSONAL");
        success.put("returnValue",valueMap);
    }
    return success;
}
/*
* 查找某天员工的班次
* */
public Map<String,Object> getListOfWorkByStaffAndDate(){
    Map<String,Object> success = ServiceUtil.returnSuccess();
    String staffId = context.get("staffId");
    Date date = context.get("date");
    success.put("returnValue",findListOfWork(staffId,date));
    return success;
}

public GenericValue findListOfWork(String staffId,Date date){
    GenericValue staff = EntityQuery.use(delegator)
            .from("TblStaff")
            .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,staffId))
            .queryOne();
    String departmentId = staff.get("department");
    GenericValue listOfWork = findListOfWork(staffId,departmentId,date,1);
    if(UtilValidate.isEmpty(listOfWork)){
        listOfWork = getlistOfWorkByDepart(departmentId,date);
    }
    if(null == listOfWork ){
        listOfWork = new GenericValue();
    }
    return listOfWork;
}


public GenericValue getParentDepart(String partyIdTo){
    List<EntityCondition> groupConditions = FastList.newInstance();
    groupConditions.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyIdTo));
    groupConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"GROUP_ROLLUP"));
    groupConditions.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.NOT_EQUAL,"DIVISION"));
    GenericValue parentGroup = EntityQuery.use(delegator)
            .select("partyIdFrom","partyIdTo")
            .from("PartyRelationship")
            .where(groupConditions)
            .cache()
            .queryOne();
    return parentGroup;
}
/*
* 查询上级部门班次
* */
public GenericValue getlistOfWorkByDepart(String departmentId,Date date){
    GenericValue parentGroup = getParentDepart(departmentId);
    GenericValue listOfWork = null;
    String parentGroupId = null;
    boolean isEmptyParentGroupId = false;
    if(UtilValidate.isEmpty(parentGroup)){
        isEmptyParentGroupId = true;
    }else {
        parentGroupId = parentGroup.get("partyIdFrom");
        listOfWork  = findListOfWork('',parentGroupId,date,3);
    }
    if(!isEmptyParentGroupId && UtilValidate.isEmpty(listOfWork)){
        listOfWork = getlistOfWorkByDepart(parentGroupId,date);
    }
    if(null == listOfWork){
        listOfWork = new GenericValue();
    }
    return listOfWork;
}

public GenericValue findListOfWork(String staffId,String departmentId,Date date,int rank){
    String rank_one = "1";
    String rank_two = "2";
    String rank_three = "3";
    String rank_four = "4";
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    List<GenericValue> listOfWorkList = FastList.newInstance();
    List<EntityCondition> conditions = FastList.newInstance();
    conditions.add(EntityCondition.makeCondition(
            EntityCondition.makeCondition("staff",staffId),
            EntityJoinOperator.OR,
            EntityCondition.makeCondition("department",departmentId)));
    conditions.add( EntityCondition.makeCondition("startDate",EntityOperator.LESS_THAN_EQUAL_TO,sqlDate));
    conditions.add( EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN_EQUAL_TO,sqlDate));
    List<GenericValue> workScheduleList = null;
    for(int i = rank; i <= 4; i++){
        workScheduleList = EntityQuery.use(delegator)
                .from("WorkScheduleIndexInfo")
                .where(EntityCondition.makeCondition(EntityCondition.makeCondition(conditions),EntityJoinOperator.AND,EntityCondition.makeCondition("rank",EntityOperator.EQUALS,i.toString())))
                .queryList();
        if(UtilValidate.isNotEmpty(workScheduleList)) break;
    }
    GenericValue listOfWork = null;
    if (UtilValidate.isNotEmpty(workScheduleList)) {
        for (GenericValue value : workScheduleList) {
            String workScheduleId = value.get("dataValue");
            GenericValue workSchedule = null;
            String tableName = value.get("tableName");
            if (tableName.equals("TblPersonalWorkSchedule")) {//个人排班
                workSchedule = delegator.findOne("TblPersonalWorkSchedule", UtilMisc.toMap("personalWorkScheduleId", workScheduleId),false);
                listOfWork = getListOfWork(workSchedule.get("listOfWorkId"));
            } else if (tableName.equals("TblWorkCycleSchedule")) {//周期排班
                Date startDate = value.getDate("startDate");
                Date endDate = value.getDate("endDate");
                workSchedule = delegator.findOne("TblWorkCycleSchedule",UtilMisc.toMap("workCycleScheduleId", workScheduleId),false);
                listOfWork = getListOfWork(getListOfWorkByCycle(date,startDate,endDate,workSchedule));
            } else if (tableName.equals("TblAutoScheduleDepartment")) {//自动排班
                workSchedule = EntityQuery.use(delegator)
                        .from("TblAutoScheduleDepartment")
                        .where(UtilMisc.toList(
                        EntityCondition.makeCondition("autoWorkScheduleId", EntityOperator.EQUALS,workScheduleId),
                        EntityCondition.makeCondition("department", EntityOperator.EQUALS, value.get("department"))
                ))
                        .queryOne();
                Date startDate = value.get("startDate");
                Date endDate = value.get("endDate");
                int workOrder = Integer.parseInt((String)workSchedule.get("scheduleOrder"));
                listOfWork = getListOfWorkByAutoSchedule(workScheduleId, workOrder, date, startDate, endDate);
            }
        }
    }
    return listOfWork;
}



/*@description 根据日期获得星期几，得出星期几的班次的id
* @param date 得出星期几
* @workByWeek 周期排班
* @return listOfWorkId 班次id
* */
public String getListOfWorkByCycle(Date date,Date start,Date end,GenericValue workByWeek){
    int order = 1;
    if(UtilValidate.isNotEmpty(workByWeek.get("cycleSize"))){
        long workWeekSize = delegator.findCountByCondition("TblWorkCycleScheduleWorkWeek",EntityCondition.makeCondition("workCycleScheduleId",workByWeek.get("workCycleScheduleId")),null,null);
        order = CheckingUtil.getWorkWeekOrder(date,start,end,(int)workWeekSize,workByWeek.getInteger("cycleSize"),workByWeek.getString("cycleSizeUnit"));
    }
    List<GenericValue> workWeekList = delegator.findByAnd("TblWorkCycleScheduleWorkWeek",UtilMisc.toMap("workCycleScheduleId",workByWeek.get("workCycleScheduleId"),"weekSort",order),null,false);
    GenericValue listOfWorkByWeek = null;
    String listOfWorkId = "";
    if(UtilValidate.isNotEmpty(workWeekList)){
        listOfWorkByWeek = delegator.findOne("TblListOfWorkByWeek",UtilMisc.toMap("listOfWorkByWeekId",workWeekList.get(0).getString("listOfWorkByWeekId")),false);
        int weekDay = getWeekDay(date);
        switch (weekDay){
            case 1 :
                listOfWorkId = listOfWorkByWeek.get("mon");
                break;
            case 2 :
                listOfWorkId = listOfWorkByWeek.get("tue");
                break;
            case 3 :
                listOfWorkId = listOfWorkByWeek.get("wed");
                break;
            case 4 :
                listOfWorkId = listOfWorkByWeek.get("thu");
                break;
            case 5 :
                listOfWorkId = listOfWorkByWeek.get("fri");
                break;
            case 6 :
                listOfWorkId = listOfWorkByWeek.get("sat");
                break;
            case 7 :
                listOfWorkId = listOfWorkByWeek.get("sun");
                break;
            default:
                break;
        }
    }
    return listOfWorkId;
}

/*
*
* */
public Map<String,Object> getListOfWorkInfo(GenericValue listOfWork,Date date){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Map<String,Object> listOfWorkMap = FastMap.newInstance();
    listOfWorkMap.put("listOfWorkId",listOfWork.get("listOfWorkId"));
    listOfWorkMap.put("name",listOfWork.get("name"));
    listOfWorkMap.put("toWorkTime",listOfWork.get("toWorkTime"));
    listOfWorkMap.put("getOffWorkTime",listOfWork.get("getOffWorkTime"));
    listOfWorkMap.put("date",format.format(date));
    return listOfWorkMap;
}
public GenericValue getListOfWork(String id){
    return delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",id),false);
}

public GenericValue getListOfWorkByAutoSchedule(String autoWorkScheduleId,int workOrder,Date date,Date startDate,Date endDate){
    List<GenericValue> autoScheduleWorkOrderList = EntityQuery.use(delegator)
            .from("TblAutoScheduleWorkOrder")
            .where(EntityCondition.makeCondition("autoWorkScheduleId",autoWorkScheduleId))
            .queryList();
    List<GenericValue> autoScheduleDepartmentList = EntityQuery.use(delegator)
            .from("TblAutoScheduleDepartment")
            .where(EntityCondition.makeCondition("autoWorkScheduleId",autoWorkScheduleId))
            .queryList();
    int orderValueSize = autoScheduleWorkOrderList.size();
    int workGroupSize = autoScheduleDepartmentList.size();
    /*int order = getOrder(date,startDate,endDate,orderValueSize,workOrder);*/
    int order = CheckingUtil.getOrder(date,startDate,endDate,workGroupSize,workOrder);
    GenericValue autoScheduleWorkOrder = EntityQuery.use(delegator).from("TblAutoScheduleWorkOrder")
            .where(EntityCondition.makeCondition(
            EntityCondition.makeCondition("autoWorkScheduleId",EntityOperator.EQUALS,autoWorkScheduleId),
            EntityCondition.makeCondition("scheduleOrder",EntityOperator.EQUALS,order)
    ))
            .queryOne();
    String listOfWorkId = "";
    if(UtilValidate.isNotEmpty(autoScheduleWorkOrder)){
        listOfWorkId = autoScheduleWorkOrder.get("listOfWorkId")
    }
    GenericValue listOfWork = getListOfWork(listOfWorkId);
    if(UtilValidate.isEmpty(listOfWork)){
        listOfWork = delegator.makeValidValue("TblListOfWork",UtilMisc.toMap("listOfWorkId",null));
    }
    return listOfWork;
}

public int getWeekDay(Date date){
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
//一周第一天是否为星期天
    boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
//获取周几
    int weekDay = cal.get(Calendar.DAY_OF_WEEK);
//若一周第一天为星期天，则-1
    if (isFirstSunday) {
        weekDay = weekDay - 1;
        if (weekDay == 0) {
            weekDay = 7;
        }
    }
    return weekDay;
}