package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Time
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
public Map<String, Object> saveCheckingIn() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化时间
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");// 格式化时间
    Date nowDate = new Date();
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String checkingInTime1 = context.get("checkingInTime");
    String checkingInDate = context.get("checkingInDate");
    java.sql.Time checkingInTime = java.sql.Time.valueOf(checkingInTime1);
    String listOfWorkId = context.get("listOfWorkId");
    GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",listOfWorkId),false);
    String toWorkTime = listOfWork.get("toWorkTime");
    String getOffWorkTime = listOfWork.get("getOffWorkTime");
    int allowTimeToLateTime = listOfWork.get("allowTimeToLateTime"); //允许迟到时间
    int tardyGetOffWorkTime = listOfWork.get("tardyGetOffWorkTime"); //允许早退时间
    String checkingInType = context.get("checkingInType");

    Date toWorkDate = format.parse(checkingInDate + " " + toWorkTime);//上班时间
    Date getOffWorkDate = format.parse(checkingInDate + " " +  getOffWorkTime);//下班时间

    Date checkingInDa = format.parse(checkingInDate + " " +  checkingInTime);//签到（签退）（yyyy-MM-dd hh:MM:ss）

    if ("CHECKING_IN".equals(checkingInType)) {//签到
        if (addMinutes(toWorkDate, allowTimeToLateTime).compareTo(checkingInDa) == 1) {
            context.put("checkingInStatus", CheckingInParametersUtil.CHECKING_IN_STATUS_NORMAL);//正常
        } else {
            context.put("checkingInStatus", CheckingInParametersUtil.CHECKING_IN_STATUS_LATE);//迟到
            context.put("minutes", (int) dateSubtract(checkingInDa, toWorkDate));
        }
    } else if (CheckingInParametersUtil.CHECKING_OUT.equals(checkingInType)) {//签退
        if (addMinutes(getOffWorkDate, -tardyGetOffWorkTime).compareTo(checkingInDa) == 1) {
            context.put("checkingInStatus", CheckingInParametersUtil.CHECKING_IN_STATUS_EARLY);//早退
            context.put("minutes", (int) dateSubtract(getOffWorkDate, checkingInDa));
        } else {
            context.put("checkingInStatus", CheckingInParametersUtil.CHECKING_IN_STATUS_NORMAL);//正常
        }
    }
    context.put("registerDate",new java.sql.Timestamp(nowDate.getTime()));
    context.put("checkingInTime",checkingInTime);
    context.put("weekDay",getWeekDay(checkingInDa));
    String msg = "考勤信息添加成功"
    try {
        GenericValue saveCheckingIn = delegator.makeValidValue("TblCheckingIn",context);
        saveCheckingIn.create();
    } catch (GenericEntityException e) {
        return ServiceUtil.returnError("当日已经签到或签退，请勿重复签到或签退！");
    }
    success.put("returnValue", msg)
    return success;
}
public Map<String, Object> deleteCheckingIn() {
    Map success = ServiceUtil.returnSuccess();
    String msg = "";
    try {
        String checkingInDate = context.get("checkingInDate");
        String staff = context.get("staff");
        String listOfWorkId = context.get("listOfWorkId");
        String type = context.get("type");
        delegator.removeByAnd("TblCheckingIn",UtilMisc.toMap("checkingInDate",checkingInDate,"staff",staff,"listOfWorkId",listOfWorkId,"checkingInType",type));
        msg = "删除成功！";
    }catch (GenericEntityException e){
        return ServiceUtil.returnError("删除失败！")
    }
    success.put("returnValue",msg);
    return success;
}
/*
*
* 考勤统计
* */
public Map<String, Object> checkingInStatistics() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) parameters.get("userLogin");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String department = context.get("department");//部门
    String partyId = context.get("partyId");//个人
    String dayCou = context.get("daySize");//天数
    Integer daySize = 1;
    if(null != dayCou && !"".equals(dayCou)){
        daySize = Integer.parseInt(dayCou);
    }
    Date startDate = format.parse(context.get("startDate").toString());
    List<Map<String,Object>> valueList = FastList.newInstance();
    if(UtilValidate.isNotEmpty(partyId)){
        valueList.add(getStaffCheckingInStatistics(partyId,daySize,startDate,userLogin));
    }else if(UtilValidate.isNotEmpty(department)){
        Map<String,Object> staffSuccessMap = dispatcher.runSync("getAllDepartmentMember",UtilMisc.toMap("partyId",department,"userLogin",userLogin));
        List<GenericValue> staffList = staffSuccessMap.get("data");
        for(Map value : staffList){
            valueList.add(getStaffCheckingInStatistics(value.get("partyId"),daySize,startDate));
        }
    }
    success.put("data",valueList);
    return success;
}
public Map<String,Object> getStaffCheckingInStatistics(String partyId,int daySize,Date startDate, GenericValue userLogin){
    int workHowLongTimeTotoal = 0;
    double workHowLongDayTotoal = 0;
    Integer lateTime = 0;   //迟到时间
    Integer lateDay = 0;   //迟到天数
    Integer earlyGetOffTime = 0; //早退时间
    Integer earlyGetOffDay = 0; //早退天数
    Integer absenteeismTime = 0; //旷工时间
    Integer absenteeismDay = 0; //旷工天数
    Integer holidayTime = 0;
    Integer holidayDay = 0;
    Date indexDate = startDate;
    Date endDate = CheckingUtil.addDays(startDate,daySize);
    List<Date> lateDateList = FastList.newInstance();
    List<Date> earlyDateList = FastList.newInstance();
    List<Date> absenteeismDateList = FastList.newInstance();
    Integer workHowLongTime = 0;
    Integer workHowLongDay = 0;
    while (indexDate.compareTo(endDate) != 1){
        if(UtilValidate.isNotEmpty(partyId)){
            Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",partyId,"date",indexDate,"userLogin",userLogin));
            GenericValue listOfWork = (GenericValue)successMap.get("returnValue");//获得员工班次
            Timestamp start = UtilDateTime.getDayStart(new Timestamp(indexDate.getTime()));
            Timestamp end = UtilDateTime.getDayEnd(new Timestamp(indexDate.getTime()));

            if(UtilValidate.isNotEmpty(listOfWork)){
                String listOfWorkType = listOfWork.get("listOfWorkType");
                if(listOfWorkType.equals("LIST_OF_WORK_TYPE_02") || UtilValidate.isEmpty(listOfWork.get("listOfWorkId"))){//休息班次

                }else {
                    workHowLongTime = listOfWork.getInteger("workHowLongTime");//应出勤时长(小时)
                    workHowLongDay = listOfWork.getInteger("workHowLongDay") - 1;//应出勤折算天数(天)
                    Integer workHowDay = 1;

                    java.sql.Date IndexSqlDate = new java.sql.Date(indexDate.getTime());
                    long holidayCount = delegator.findCountByCondition("TblHoliday", EntityCondition.makeCondition(
                            EntityCondition.makeCondition("startDate",EntityOperator.LESS_THAN_EQUAL_TO,IndexSqlDate),
                            EntityJoinOperator.AND,
                            EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN_EQUAL_TO,IndexSqlDate)
                    ),null,null);

                    if(holidayCount > 0){//请假
                        holidayTime += workHowLongTime;
                        holidayDay += 1;
                        indexDate = CheckingUtil.addDays(indexDate,1);
                        continue;//结束本次循环
                    }
                    List<GenericValue> checkingInList = null;
                    if(listOfWork.get("getOffWorkTime") < listOfWork.get("toWorkTime")){//跨天的班次
                        java.sql.Date date1 = new java.sql.Date(indexDate.getTime());
                        java.sql.Date date2 = date1 + 1;
                        java.sql.Time time = java.sql.Time.valueOf("12:00:00");
                        checkingInList = EntityQuery.use(delegator)
                                .from("TblCheckingIn")
                                .where(EntityCondition.makeCondition(
                                EntityCondition.makeCondition("staff",EntityOperator.EQUALS, partyId),
                                EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS, date1),
                                EntityCondition.makeCondition("checkingInTime",EntityOperator.GREATER_THAN_EQUAL_TO, time),
                                EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS, "CHECKING_IN"),
                                EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                        )).queryList();
                        List<GenericValue> checkingOut = EntityQuery.use(delegator)
                                .from("TblCheckingIn")
                                .where(EntityCondition.makeCondition(
                                EntityCondition.makeCondition("staff",EntityOperator.EQUALS, partyId),
                                EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS, date2),
                                EntityCondition.makeCondition("checkingInTime",EntityOperator.LESS_THAN_EQUAL_TO, time),
                                EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS, "CHECKING_OUT"),
                                EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                        )).queryList();
                        checkingInList.addAll(checkingOut);
                    }else{//不跨天的班次
                        checkingInList = EntityQuery.use(delegator)
                                .from("TblCheckingIn")
                                .where(EntityCondition.makeCondition(
                                EntityCondition.makeCondition("staff",EntityOperator.EQUALS,partyId),
                                EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS,new java.sql.Date(indexDate.getTime())),
                                EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                        )).queryList();
                    }
                    /*List<GenericValue> checkingInList = EntityQuery.use(delegator)
                            .from("TblCheckingIn")
                            .where(EntityCondition.makeCondition(
                            EntityCondition.makeCondition("staff",EntityOperator.EQUALS,partyId),
                            EntityCondition.makeCondition("registerDate",EntityOperator.BETWEEN,UtilMisc.toList(start,end)),
                            EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                    )).queryList();*/

                    if(UtilValidate.isNotEmpty(checkingInList) || checkingInList.size() > 0){
                            for(GenericValue checkingIn : checkingInList){
                                String status = checkingIn.get("checkingInStatus");
                                Integer minutes = checkingIn.getInteger("minutes");
                                if(null == minutes){
                                    minutes = 0;
                                }
                                if (status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                                    lateTime += minutes;
                                    lateDay += 1;
                                    lateDateList.add(indexDate);
                                }else if (status.equals("CHECKING_IN_STATUS_EARLY")) {//早退
                                    earlyGetOffTime += minutes;
                                    earlyGetOffDay += 1;
                                    earlyDateList.add(indexDate);
                                }else if(status.equals("CHECKING_IN_STATUS_NORMAL")){//正常
                                    workHowLongTimeTotoal += workHowLongTime;
                                    workHowLongDayTotoal += workHowDay;
                                }
                        }
                    }else {
                        //旷工
                        absenteeismTime += workHowLongTime;
                        absenteeismDay += 1;
                        absenteeismDateList.add(indexDate);
                    }
                }
            }
        }
        indexDate = CheckingUtil.addDays(indexDate,1);
    }
    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.put("partyId",partyId);//员工id
    valueMap.put("workHowLongDay",workHowLongDay);
    if(lateDay >0){
        valueMap.put("lateMap",UtilMisc.toMap("dateList",lateDateList,"timeCount",lateTime,"dayCount",lateDay));//迟到情况
    }
    if(earlyGetOffDay >0) {
        valueMap.put("earlyMap", UtilMisc.toMap("dateList", earlyDateList, "timeCount", earlyGetOffTime, "dayCount", earlyGetOffDay));//早退
    }
    if((workHowLongDayTotoal - (lateDay + earlyGetOffDay + absenteeismDay + holidayDay)) > 0){
        valueMap.put("normalMap",UtilMisc.toMap("timeCount",workHowLongTimeTotoal-(lateTime + earlyGetOffTime + absenteeismTime + holidayTime)
            ,"dayCount",workHowLongDayTotoal - (lateDay + earlyGetOffDay + absenteeismDay + holidayDay)));//正常
    }
    if(absenteeismDay > 0){
        valueMap.put("absenteeismMap",UtilMisc.toMap("dateList",absenteeismDateList,"timeCount",absenteeismTime,"dayCount",absenteeismDay));//矿工
    }
    if(holidayDay > 0){
    valueMap.put("holidayMap",UtilMisc.toMap("timeCount",holidayTime,"dayCount",holidayDay));//假期
    }
    return valueMap;
}

public Map<String,Object> getStaffCheckingInStatisticsNew(String partyId,int daySize,Date startDate, GenericValue userLogin){
    int workHowLongTimeTotal = 0;
    double workHowLongDayTotal = 0;
    Integer lateTime = 0;   //迟到时间
    Integer lateDay = 0;   //迟到天数
    Integer earlyGetOffTime = 0; //早退时间
    Integer earlyGetOffDay = 0; //早退天数
    Integer absenteeismTime = 0; //旷工时间
    Integer absenteeismDay = 0; //旷工天数
    double holidayTime = 0;//请假时间
    double holidayDay = 0;//请假天数
    Date indexDate = startDate;
    Date endDate = CheckingUtil.addDays(startDate,daySize);
    List<Date> lateDateList = FastList.newInstance();
    List<Date> earlyDateList = FastList.newInstance();
    List<Date> absenteeismDateList = FastList.newInstance();
    List<Date> checkingInListTotal = FastList.newInstance();//签到情况汇总
    Integer workHowLongTime = 0;//应出勤时长(小时)
    Integer workHowLongDay = 0;//应出勤折算天数(天)
    while (indexDate.compareTo(endDate) != 1){
        if(UtilValidate.isNotEmpty(partyId)){
            Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",partyId,"date",indexDate,"userLogin",userLogin));
            GenericValue listOfWork = (GenericValue)successMap.get("returnValue");//获得当天员工班次
            Timestamp start = UtilDateTime.getDayStart(new Timestamp(indexDate.getTime()));
            Timestamp end = UtilDateTime.getDayEnd(new Timestamp(indexDate.getTime()));

            if(UtilValidate.isNotEmpty(listOfWork)){
                String listOfWorkType = listOfWork.get("listOfWorkType");
                if(listOfWorkType.equals("LIST_OF_WORK_TYPE_02")){//休息班次

                }else {
                    workHowLongTime = listOfWork.getInteger("workHowLongTime");//应出勤时长(小时)
                    workHowLongDay = listOfWork.getInteger("workHowLongDay") - 1;//应出勤折算天数(天)
                    Integer workHowDay = 1;

                    java.sql.Date IndexSqlDate = new java.sql.Date(indexDate.getTime());
                    GenericValue staff = EntityQuery.use(delegator).from("TblStaff").where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)).queryOne();
                    String directDepart = (String) staff.get("department");
//                    long holidayCount = delegator.findCountByCondition("TblHoliday", EntityCondition.makeCondition(
//                            EntityCondition.makeCondition(
//                                    EntityCondition.makeCondition("staff",EntityOperator.EQUALS,partyId),
//                                    EntityOperator.OR,
//                                    EntityCondition.makeCondition("department",EntityOperator.EQUALS,directDepart)
//                            ),EntityOperator.AND,
//                            EntityCondition.makeCondition(
//                                    UtilMisc.toList(
//                                            EntityCondition.makeCondition("startDate",EntityOperator.LESS_THAN_EQUAL_TO,IndexSqlDate),
//                                            EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN_EQUAL_TO,IndexSqlDate)
//                                    )
//                            )
//                    ),null,null);
                    List<GenericValue> holidayList = EntityQuery.use(delegator)
                            .from("TblHoliday")
                            .where(EntityCondition.makeCondition(
                            EntityCondition.makeCondition(
                                    EntityCondition.makeCondition("staff", EntityOperator.EQUALS, partyId),
                                    EntityOperator.OR,
                                    EntityCondition.makeCondition("department", EntityOperator.EQUALS, directDepart)), EntityOperator.AND,
                            EntityCondition.makeCondition(
                                    EntityCondition.makeCondition("startDate", EntityOperator.LESS_THAN_EQUAL_TO, IndexSqlDate),
                                    EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN_EQUAL_TO, IndexSqlDate)
                            )))
                            .orderBy("lastUpdatedStamp DESC")
                            .queryList();

                    if(holidayList.size() > 0){//请假
                        GenericValue holiday = holidayList.get(0);
                        if(!holiday.get("startDate").equals(IndexSqlDate) && !holiday.get("endDate").equals(IndexSqlDate)){//按一天算
                            holidayTime += workHowLongTime;
                            holidayDay += 1;
                            indexDate = CheckingUtil.addDays(indexDate,1);
                            continue;//按一天算则结束本次循环
                        }else{//按半天算
                            holidayTime += workHowLongTime/2;
                            holidayDay += 0.5;
                        }
                    }
                    List<GenericValue> checkingInList = EntityQuery.use(delegator)
                            .from("TblCheckingIn")
                            .where(EntityCondition.makeCondition(
                            EntityCondition.makeCondition("staff",EntityOperator.EQUALS,partyId),
                            EntityCondition.makeCondition("registerDate",EntityOperator.BETWEEN,UtilMisc.toList(start,end)),
                            EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                    )).queryList();

                    if(!(holidayList.size() > 0)){//正常情况(无请假)下签到情况
                        if(UtilValidate.isNotEmpty(checkingInList) || checkingInList.size() > 0){
                            for(GenericValue checkingIn : checkingInList){
                                String status = checkingIn.get("checkingInStatus");
                                Integer minutes = checkingIn.getInteger("minutes");
                                if(null == minutes){
                                    minutes = 0;
                                }
                                if (status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                                    lateTime += minutes;
                                    lateDay += 1;
                                    lateDateList.add(indexDate);
                                }else if (status.equals("CHECKING_IN_STATUS_EARLY")) {//早退
                                    earlyGetOffTime += minutes;
                                    earlyGetOffDay += 1;
                                    earlyDateList.add(indexDate);
                                }else if(status.equals("CHECKING_IN_STATUS_NORMAL")){//正常
                                    workHowLongTimeTotal += workHowLongTime;
                                    workHowLongDayTotal += workHowDay;
                                }
                            }
                        }else {//旷工
                            absenteeismTime += workHowLongTime;
                            absenteeismDay += 1;
                            absenteeismDateList.add(indexDate);
                        }

                    }else{

                    }
                }
            }
        }
        indexDate = CheckingUtil.addDays(indexDate,1);
    }
    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.put("partyId",partyId);//员工id
    valueMap.put("workHowLongDay",workHowLongDay);
    if(lateDay >0){
        valueMap.put("lateMap",UtilMisc.toMap("dateList",lateDateList,"timeCount",lateTime,"dayCount",lateDay));//迟到情况
    }
    if(earlyGetOffDay >0) {
        valueMap.put("earlyMap", UtilMisc.toMap("dateList", earlyDateList, "timeCount", earlyGetOffTime, "dayCount", earlyGetOffDay));//早退
    }
    if((workHowLongDayTotal - (lateDay + earlyGetOffDay + absenteeismDay + holidayDay)) > 0){
        valueMap.put("normalMap",UtilMisc.toMap("timeCount",workHowLongTimeTotal-(lateTime + earlyGetOffTime + absenteeismTime + holidayTime)
                ,"dayCount",workHowLongDayTotal - (lateDay + earlyGetOffDay + absenteeismDay + holidayDay)));//正常
    }
    if(absenteeismDay > 0){
        valueMap.put("absenteeismMap",UtilMisc.toMap("dateList",absenteeismDateList,"timeCount",absenteeismTime,"dayCount",absenteeismDay));//矿工
    }
    if(holidayDay > 0){
        valueMap.put("holidayMap",UtilMisc.toMap("timeCount",holidayTime,"dayCount",holidayDay));//假期
    }
    return valueMap;

}

public Map<String, Object> deleteHoliday() {
    Map success = ServiceUtil.returnSuccess();
    String holidayId = context.get("holidayId");
    if(UtilValidate.isNotEmpty(holidayId)){
        GenericValue deleteHoliday = delegator.makeValidValue("TblHoliday",UtilMisc.toMap("holidayId",holidayId));
        deleteHoliday.remove();
    }
    return success;
}


public long dateSubtract(Date date1,Date date2){
    SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");// 格式化时间
    String date1Str = format.format(date1);
    String date2Str = format.format(date2);
    long result = 0L;
    try {
        result = (format.parse(date1Str).getTime() - format.parse(date2Str)
                .getTime())/60000;
        // 除以1000得到秒，相应的60000得到分，3600000得到小时
    } catch (ParseException e) {
        e.printStackTrace();
    }
    return result;
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

public Date addDays(Date date, int i) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, i);
    return calendar.getTime();
}
public Date addMinutes(Date date, int i) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE,i);
    return calendar.getTime();
}

