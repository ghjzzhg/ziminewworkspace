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


public Map<String, Object> savePersonalWorkSchedule() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin")
    String personalWorkScheduleId = context.get("personalWorkScheduleId");
    String staff = context.get("staff");
    String valueStr = context.get("valueStr");
    String msg = "";
    try {
        if (UtilValidate.isNotEmpty(personalWorkScheduleId)) {
            //TODO 跟新
            String paramStr = valueStr.substring(1);
            String[] paramArray = paramStr.split(",");
            for(String str : paramArray){
                String[] valueArray = str.split("-");
                String listOfWork = valueArray[1];
                java.sql.Date scheduleDate = new java.sql.Date(Long.valueOf(valueArray[0]));
                GenericValue personalWorkSchedule = delegator.makeValidValue("TblPersonalWorkSchedule",
                        UtilMisc.toMap("personalWorkScheduleId",personalWorkScheduleId,"listOfWorkId",listOfWork));
                personalWorkSchedule.store();

                GenericValue scheduleIndex = EntityQuery.use(delegator).select("id")
                        .from("TblWorkScheduleIndex")
                        .where(EntityCondition.makeCondition("dataValue",EntityOperator.EQUALS,personalWorkScheduleId))
                        .queryOne();
                String id = scheduleIndex.get("id");
                Map<String, Object> workScheduleIndexMap = FastMap.newInstance();
                workScheduleIndexMap.put("id", id);
                workScheduleIndexMap.put("staff", staff);
                workScheduleIndexMap.put("startDate",scheduleDate);
                workScheduleIndexMap.put("endDate",scheduleDate);
                workScheduleIndexMap.put("dataValue",personalWorkScheduleId);
                workScheduleIndexMap.put("type","personal");
                GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap);
                workScheduleIndex.store();
            }
            msg = "个人排班更新成功！"
        } else if(UtilValidate.isNotEmpty(valueStr)) {
            String paramStr = valueStr.substring(1);
            String[] paramArray = paramStr.split(",");
            for(String str : paramArray){
                //保存个人排班
                personalWorkScheduleId = delegator.getNextSeqId("TblPersonalWorkSchedule");
                String[] valueArray = str.split("-");//日期-班次（valueArray[0]：日期 ，valueArray[1]：班次）
                java.sql.Date scheduleDate = new java.sql.Date(Long.valueOf(valueArray[0]));
                Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",staff,"date",scheduleDate,"userLogin",userLogin));
                GenericValue listOfWork = (GenericValue)successMap.get("returnValue");//获得当天员工班次
                if(UtilValidate.isNotEmpty(listOfWork) && listOfWork.get("listOfWorkId").toString().equals(valueArray[1])){
                    msg = "个人排班保存成功！";
                }else{
                    List<GenericValue> personalWorkScheduleList = EntityQuery
                            .use(delegator)
                            .from("PersonalWorkScheduleInfo")
                            .where(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "personal")
                                    ,EntityCondition.makeCondition("staff", EntityOperator.EQUALS, staff)
                                    ,EntityCondition.makeCondition("startDate", EntityOperator.EQUALS, scheduleDate)
                                    ,EntityCondition.makeCondition("endDate", EntityOperator.EQUALS, scheduleDate))
                            .queryList();
                    if(UtilValidate.isNotEmpty(personalWorkScheduleList) && !personalWorkScheduleList.get(0).get("listOfWorkId").equals(valueArray[1])){
                        personalWorkScheduleId = personalWorkScheduleList.get(0).get("dataValue");
                        GenericValue personalWorkSchedule = delegator.makeValidValue("TblPersonalWorkSchedule",
                                UtilMisc.toMap("personalWorkScheduleId",personalWorkScheduleId,"listOfWorkId",valueArray[1]));
                        personalWorkSchedule.store();
                        String id = personalWorkScheduleList.get(0).get("id");
                        Map<String, Object> workScheduleIndexMap = FastMap.newInstance();
                        workScheduleIndexMap.put("id", id);
                        workScheduleIndexMap.put("staff", staff);
                        workScheduleIndexMap.put("startDate",scheduleDate);
                        workScheduleIndexMap.put("endDate",scheduleDate);
//                        workScheduleIndexMap.put("dataValue",personalWorkScheduleId);
                        workScheduleIndexMap.put("type","personal");
                        GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap);
                        workScheduleIndex.store();
                        msg = "个人排班保存成功！";
                    }else{
                        GenericValue personalWorkSchedule = delegator.makeValidValue("TblPersonalWorkSchedule",
                                UtilMisc.toMap("personalWorkScheduleId",personalWorkScheduleId,"listOfWorkId",valueArray[1]));
                        personalWorkSchedule.create();
                        //索引表
                        String id = delegator.getNextSeqId("TblWorkScheduleIndex");
                        Map<String, Object> workScheduleIndexMap = FastMap.newInstance();
                        workScheduleIndexMap.put("id", id);
                        workScheduleIndexMap.put("staff", staff);
                        workScheduleIndexMap.put("startDate",scheduleDate);
                        workScheduleIndexMap.put("endDate",scheduleDate);
                        workScheduleIndexMap.put("dataValue",personalWorkScheduleId);
                        workScheduleIndexMap.put("type","personal");
                        GenericValue workScheduleIndex = delegator.makeValidValue("TblWorkScheduleIndex", workScheduleIndexMap);
                        workScheduleIndex.create();
                        msg = "个人排班保存成功！";
                    }
                }
            }
        }
    } catch (GenericEntityException e) {
        msg = "服务出错！"
    }
    success.put("returnValue", msg);
    return success;
}

public Map<String, Object> searchPersonalSchedule(){
    Map success = ServiceUtil.returnSuccess();
    Date startDate = context.get("startDate");
    Date endDate = context.get("endDate");
    String staffId = context.get("staff");
    String departmentId = context.get("departmentId");
    try {
        List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "personal")));
        if (UtilValidate.isNotEmpty(startDate)) {
            conditions.add(EntityCondition.makeCondition("startDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(startDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(endDate)) {
            conditions.add(EntityCondition.makeCondition("startDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(endDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
            conditions.add(EntityCondition.makeCondition("startDate",EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(startDate.getTime())));
            conditions.add(EntityCondition.makeCondition("endDate",EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(endDate.getTime())));
        }
        if (UtilValidate.isNotEmpty(staffId)) {
            conditions.add(EntityCondition.makeCondition("staff", EntityOperator.EQUALS, staffId));
        }
        if (UtilValidate.isNotEmpty(departmentId)) {
            conditions.add(EntityCondition.makeCondition("staffDepartment", EntityOperator.EQUALS, departmentId));
        }

        List<Map<String, Object>> valueList = FastList.newInstance();
        List<GenericValue> personalWorkScheduleInfoList = EntityQuery.use(delegator)
                .select("type", "startDate", "dataValue","listOfWorkId","staff")
                .from("PersonalWorkScheduleInfo")
                .where(EntityCondition.makeCondition(conditions))
                .distinct()
                .orderBy("startDate DESC")
                .queryList();
        success.put("returnValue",personalWorkScheduleInfoList);
    }catch (GenericEntityException e){

    }
    return success;
}
public Map<String, Object> deletePersonalWorkSchedule(){
    Map success = ServiceUtil.returnSuccess();
    String personalWorkScheduleId = context.get("personalWorkScheduleId");
    String msg = "";
    try {
        GenericValue scheduleIndex = EntityQuery.use(delegator)
                .from("TblWorkScheduleIndex")
                .where(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "personal"),
                        EntityCondition.makeCondition("dataValue", EntityOperator.EQUALS, personalWorkScheduleId))
                .queryOne();
        scheduleIndex.remove();
        GenericValue personalWorkSchedule = delegator.makeValidValue("TblPersonalWorkSchedule",UtilMisc.toMap("personalWorkScheduleId",personalWorkScheduleId));
        personalWorkSchedule.remove();
        msg = "删除成功！";
    }catch (GenericEntityException e){

    }
    success.put("returnValue",msg);
    return success;
}

public Map<String, Object> getPersonalWorkSchdule() {
    Map success = ServiceUtil.returnSuccess();
    String staffId = context.get("staffId");
    Date startDate = context.get("start");
    Date endDate = context.get("end");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date nowDate = format.parse(format.format(new Date()));
    GenericValue staff = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", staffId), false);
    List<EventTO> eventTOList = new ArrayList<EventTO>();

    if (nowDate.after(startDate) && nowDate.before(endDate)) {
        eventTOList = getEventTOList(staff,nowDate,endDate);
    } else if (nowDate.before(startDate)) {
        eventTOList = getEventTOList(staff, startDate,endDate);
    }
    success.put("returnValue", eventTOList);
    return success;
}

List<EventTO> getEventTOList(GenericValue staff, Date startDate, Date endDate) {
    String staffId = staff.get("partyId");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    List<EventTO> eventTOList = new ArrayList<EventTO>();
    GenericValue listOfWork = null;
    GenericValue personalWorkSchedule = delegator.findByAnd("TblPersonalWorkSchedule", UtilMisc.toMap("staff", staffId))[0];
    String personalWorkScheduleId = personalWorkSchedule==null ? null : personalWorkSchedule.get("personalWorkScheduleId");
    Long item = 100L;
    while (!startDate.after(endDate)) {
        if(UtilValidate.isNotEmpty(personalWorkScheduleId)){
            GenericValue listOfWorkByDate = delegator.findOne("TblListOfWorkByDate", UtilMisc.toMap("personalWorkScheduleId", personalWorkScheduleId, "scheduleDate", startDate));
            listOfWork = listOfWorkByDate.getRelated("TblListOfWork")[0];
        }
        if (listOfWork == null) {//在周期排班查找
            listOfWork = getWorkOrderByWorkSchedule(staff,startDate);
            if (listOfWork == null) {//在自动排班中查找
               // listOfWork = autoWorkScheduleManager.getWorkOrderByWorkSchedule(staff, startDate);
            }
        }
        if (listOfWork != null) {
            EventTO eventTO = new EventTO();
            eventTO.setId(workOrder.getId());
            eventTO.setTitle(workOrder.getName());
            eventTO.setStart(sdf.format(startDate));
            eventTOList.add(eventTO);
        } else {
            EventTO eventTO = new EventTO();
            eventTO.setId(item);
            eventTO.setTitle("aaaaaaa");
            eventTO.setStart(sdf.format(startDate));
            eventTOList.add(eventTO);
        }
        item++;
        startDate = addDays(startDate, 1);
    }

    return eventTOList;
}

public Date addDays(Date date, int i) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, i);
    return calendar.getTime();
}

public GenericValue getWorkOrderByWorkSchedule(GenericValue staff, Date date) {
    String staffId = staff.get("partyId");
    GenericValue listOfWork = null;
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);//"1"星期日 "2"星期一 。。。
    WeekDay weekDay = WeekDay.getDay(dayOfWeek);
    java.sql.Date conDate = new java.sql.Date(date.getTime());
    List<EntityCondition> conditionList = FastList.newInstance();
    conditionList.add(EntityCondition.makeCondition("staff", EntityOperator.EQUALS, staffId));
    conditionList.add(EntityCondition.makeCondition("startDate", EntityOperator.LESS_THAN_EQUAL_TO, conDate));
    conditionList.add(EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN_EQUAL_TO, conDate));
    List<GenericValue> personalWorkScheduleList = delegator.find("TblWorkSchedule", EntityCondition.makeCondition(conditionList),null,null,null,null).findAll();
    listOfWork = getListOfWork(weekDay, personalWorkScheduleList);

    if (listOfWork == null) {//TODO 如果没有针对个人的周期排班，试图从其所属班组、部门、条线等获取相关的周期排班
        /* Boolean hasWorkSchedule = null;
         Department staffDepartment = staff.getWorkGroup();
         if(staffDepartment == null){
             staffDepartment = staff.getDepartment();
             if(staffDepartment == null){
                 staffDepartment = staff.getLineGroup();
             }
         }

         List<WorkSchedule> departmentWorkScheduleList = getAll();
         Iterator<WorkSchedule> iterator = departmentWorkScheduleList.iterator();
         while (iterator.hasNext()){
             WorkSchedule workSchedule = iterator.next();
             Date startDate = workSchedule.getStartDate();
             Date endDate = workSchedule.getEndDate();
             if(!date.before(startDate) && !date.after(endDate)){
                 Department d = workSchedule.getDepartment();
                 //判断该排班是否针对员工所属班组/部门/条线，遍历部门不同层级
                 hasWorkSchedule = hasWorkSchedule(staffDepartment , d);
                 if(!hasWorkSchedule){
                     iterator.remove();
                 }
             }
         }
         listOfWork = getListOfWork(weekDay,departmentWorkScheduleList);*/

    }
    return listOfWork;
}
//根据星期几和班制可以确定该员工某一天具体的班次
private GenericValue getListOfWork(WeekDay weekDay, List<GenericValue> workScheduleList) {
    GenericValue listOfWork = null;
    for (GenericValue workSchedule : workScheduleList) {
        GenericValue listOfWorkByWeek = workSchedule.getRelated("TblListOfWorkByWeek")[0];
        String listOfWorkId = null;
        if (weekDay.day == WeekDay.SU) {
            listOfWorkId = listOfWorkByWeek.get("sun");
        }
        if (weekDay.day == WeekDay.MO) {
            listOfWorkId = listOfWorkByWeek.get("mon");
        }
        if (weekDay.day == WeekDay.TU) {
            listOfWorkId = listOfWorkByWeek.get("tue");
        }
        if (weekDay.day == WeekDay.WE) {
            listOfWorkId = listOfWorkByWeek.get("wed");
        }
        if (weekDay.day == WeekDay.TH) {
            listOfWorkId = listOfWorkByWeek.get("thu");
        }
        if (weekDay.day == WeekDay.FR) {
            listOfWorkId = listOfWorkByWeek.get("fri");
        }
        if (weekDay.day == WeekDay.SA) {
            listOfWorkId = listOfWorkByWeek.get("sat");
        }
        listOfWork = delegator.findOne("TblListOfWork", UtilMisc.toMap("listOfWorkId", listOfWorkId), false);
    }
    return listOfWork;
}

public GenericValue getWorkOrderByAutoWorkSchedule(GenericValue staff, Date date) {
    GenericValue listOfWork = null;
    GenericValue workGroup = staff.getRelated("PartyGroup")[0];
    if (workGroup != null) {
        List<GenericValue> autoWorkScheduleList = FastList.newInstance();
        List<GenericValue> autoScheduleDepartmentList = delegator.findByAnd("TblAutoScheduleDepartment", UtilMisc.toMap("department", workGroup.get("partyId")));
        if (autoScheduleDepartmentList != null && autoScheduleDepartmentList.size() > 0) {
            for (GenericValue autoScheduleDepartment : autoScheduleDepartmentList) {
                GenericValue autoWorkSchedule = autoScheduleDepartment.getRelated("TblAutoWorkSchedule")[0];
                autoWorkScheduleList.add(autoWorkSchedule);
            }
            /*Collections.sort(autoWorkScheduleList,new Comparator<GenericValue>() {
                @Override
                public int compare(GenericValue aw1, GenericValue aw2) {
                    return aw2.getGmtModify().compareTo(aw1.getGmtModify());
                }
            });*/
            for (GenericValue autoWorkSchedule : autoWorkScheduleList) {
                Date startDate = autoWorkSchedule.get("startDate");
                Date endDate = autoWorkSchedule.get("endDate");
                if (!date.before(startDate) && !date.after(endDate)) {
                    listOfWork = eventTo(autoWorkSchedule, date, workGroup);
                    break;
                }
            }
        }
    }

    return listOfWork;
}
//根据排班、日期和该员工所在班组确定该日期的班次（自动排版仅仅针对班组）
public GenericValue eventTo(GenericValue autoWorkSchedule, Date date, GenericValue department) {
    GenericValue listOfWork = null;
    Long newDepartmentDateLong = date.getTime();//将时间转换成毫秒数来比较 date是具体的某一天
    Long startDateLong = autoWorkSchedule.getStartDate().getTime();
    int timeDifference = (int) ((newDepartmentDateLong - startDateLong) / MILLI_SECONDS_ONE_DAY);   //所给时间距离该排班开始日期的天数
    List<GenericValue> autoScheduleDepartmentList = autoWorkSchedule.getAutoScheduleDepartments();
    int order = 0;
    for (GenericValue autoScheduleDepartment : autoScheduleDepartmentList) {
        if (department.getId().equals(autoScheduleDepartment.getDepartment().getId())) {
            order = autoScheduleDepartment.getOrder();
            break;
        }
    }
    int arithmetic = ((timeDifference % 4) + order) % 4;
    if (arithmetic != 0) {
        List<GenericValue> autoScheduleWorkOrderList = autoWorkSchedule.getAutoScheduleWorkOrders();
        listOfWork = autoScheduleWorkOrderList.get(arithmetic - 1).getWorkOrder();   //返回listOfWork
    }
    return listOfWork;
}