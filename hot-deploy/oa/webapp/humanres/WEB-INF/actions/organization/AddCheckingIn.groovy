import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import java.text.SimpleDateFormat
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

GenericValue userLogin = parameters.get("userLogin");
String partyId = userLogin.get("partyId");

String staffId = parameters.get("staffId");
String dateStr = parameters.get("date");



Calendar calendar = Calendar.getInstance();
Date nowDate = new Date();
if(UtilValidate.isNotEmpty(staffId) && UtilValidate.isNotEmpty(dateStr)){
    Date date = format.parse(dateStr);
    nowDate = date;
    partyId = staffId;
}
calendar.setTime(nowDate);
calendar.add(Calendar.DATE,-1);
Date preDate = format.parse(format.format(calendar.getTime()));
calendar.add(Calendar.DATE,+2);
Date nextDate = format.parse(format.format(calendar.getTime()));

Calendar calendar1 = Calendar.getInstance();
int checkingInTime_c_hour=calendar1.get(calendar1.HOUR_OF_DAY);
int checkingInTime_c_minutes=calendar1.get(calendar1.MINUTE);


Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",partyId,"date",nowDate,"userLogin",userLogin));
GenericValue listOfWork = successMap.get("returnValue");
  List<Map<String,Object>> listOfWorkList = FastList.newInstance();
if(UtilValidate.isNotEmpty(listOfWork)){
    listOfWorkList.add(listOfWork);
}
context.listOfWorkList = listOfWorkList;
context.partyId = partyId;
context.checkingInTime_c_hour=checkingInTime_c_hour;
context.checkingInTime_c_minutes=checkingInTime_c_minutes;
/*
getListOfWorkInfoList(listOfWorkList,partyId,preDate);
getListOfWorkInfoList(listOfWorkList,partyId,nowDate);
getListOfWorkInfoList(listOfWorkList,partyId,nextDate);
context.listOfWorkList = listOfWorkList;
context.partyId = partyId;

public void getListOfWorkInfoList(List<Map<String,Object>> listOfWorkList,String staffId,Date date){
    GenericValue listOfWork1 = findWorkScheduleOne(staffId,date);
    if(UtilValidate.isNotEmpty(listOfWork1)){
        listOfWorkList.add(getListOfWorkInfo(listOfWork1,date));
    }
}
public GenericValue findWorkScheduleOne(String staffId,Date date){
    String rank_one = "1";
    String rank_two = "2";
    String rank_three = "3";
    String rank_four = "4";

    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

    List<GenericValue> listOfWorkList = FastList.newInstance();
    GenericValue staff = EntityQuery.use(delegator)
            .from("TblStaff")
            .where(EntityCondition.makeCondition("partyId",staffId))
            .queryOne();
    String departmentId = "";
    if (staff!=null){
         departmentId = staff.get("department");
    }


    List<EntityCondition> conditions = FastList.newInstance();
    conditions.add(EntityCondition.makeCondition(
                                    EntityCondition.makeCondition("staff",staffId),
                                    EntityJoinOperator.OR,
                                    EntityCondition.makeCondition("department",departmentId)));
    conditions.add( EntityCondition.makeCondition("startDate",EntityOperator.LESS_THAN_EQUAL_TO,sqlDate));
    conditions.add( EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN_EQUAL_TO,sqlDate));

    List<GenericValue> workScheduleList = EntityQuery.use(delegator)
            .from("WorkScheduleIndexInfo")
            .where(EntityCondition.makeCondition(EntityCondition.makeCondition(conditions),EntityJoinOperator.AND,EntityCondition.makeCondition("rank",EntityOperator.EQUALS,rank_one)))
            .queryList();
    if(UtilValidate.isEmpty(workScheduleList)){
        workScheduleList = EntityQuery.use(delegator)
                .from("WorkScheduleIndexInfo")
                .where(EntityCondition.makeCondition(EntityCondition.makeCondition(conditions),EntityJoinOperator.AND,EntityCondition.makeCondition("rank",EntityOperator.EQUALS,rank_two)))
                .queryList();
        if(UtilValidate.isEmpty(workScheduleList)){
            workScheduleList = EntityQuery.use(delegator)
                    .from("WorkScheduleIndexInfo")
                    .where(EntityCondition.makeCondition(EntityCondition.makeCondition(conditions),EntityJoinOperator.AND,EntityCondition.makeCondition("rank",EntityOperator.EQUALS,rank_three)))
                    .queryList();
            if(UtilValidate.isEmpty(workScheduleList)){
                workScheduleList = EntityQuery.use(delegator)
                        .from("WorkScheduleIndexInfo")
                        .where(EntityCondition.makeCondition(EntityCondition.makeCondition(conditions),EntityJoinOperator.AND,EntityCondition.makeCondition("rank",EntityOperator.EQUALS,rank_four)))
                        .queryList();
            }
        }
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
                workSchedule = delegator.findOne("TblWorkCycleSchedule",UtilMisc.toMap("workCycleScheduleId", workScheduleId),false);
                listOfWork = getListOfWork(getListOfWorkByCycle(date, workSchedule));
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



*/
/*@description 根据日期获得星期几，得出星期几的班次的id
* @param date 得出星期几
* @workByWeek 周期排班
* @return listOfWorkId 班次id
* *//*

public String getListOfWorkByCycle(Date date,GenericValue workByWeek){
    GenericValue listOfWorkByWeek = delegator.findOne("TblListOfWorkByWeek",UtilMisc.toMap("listOfWorkByWeekId",workByWeek.get("listOfWorkByWeekId")),false);
    int weekDay = getWeekDay(date);
    String listOfWorkId = "";
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
    return listOfWorkId;
}

*/
/*
*
* *//*

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

public int dateSubtract(Date date1,Date date2){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String formatDate1 = format.format(date1);
    String formatDate2 = format.format(date2);
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(format.parse(formatDate1));
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(format.parse(formatDate2));
    long l = cal2.getTimeInMillis() - cal1.getTimeInMillis();
    int days = (Long)(l / (1000 * 60 * 60 * 24)).intValue();
    return days;
}

public GenericValue getListOfWorkByAutoSchedule(String autoWorkScheduleId,int workOrder,Date date,Date startDate,Date endDate){
    List<GenericValue> autoScheduleWorkOrderList = EntityQuery.use(delegator)
            .from("TblAutoScheduleWorkOrder")
            .where(EntityCondition.makeCondition("autoWorkScheduleId",autoWorkScheduleId))
            .queryList();
    int orderValueSize = autoScheduleWorkOrderList.size();
    if(date.after(startDate) && date.before(endDate)){//当前日期在在自动排班日期范围内
        int dateSubtract = dateSubtract(startDate,date) + 1;
        int cycleCount = dateSubtract/orderValueSize;
        int remainder = dateSubtract%orderValueSize;
        int order = 0;
        int startOrder = 1;
        if(cycleCount < 1 || (cycleCount == 1 && remainder == 0)){
            if(workOrder == orderValueSize){
                startOrder = workOrder;
            }
        }else if(cycleCount >= 1 && (cycleCount < orderValueSize || (cycleCount == orderValueSize && remainder == 0))){
            startOrder = workOrder + cycleCount;
            if(startOrder > orderValueSize){
                startOrder = startOrder - orderValueSize;
            }
        }else if (cycleCount >= orderValueSize) {
            int cycleRemainder = cycleCount % orderValueSize;
            if(cycleRemainder == 0){
                startOrder = workOrder;
            }else {
                startOrder = workOrder + (cycleRemainder - 1);
                if(startOrder > orderValueSize) {
                    startOrder = cycleRemainder - 1;
                }
            }
        }
        int orderAdd = 0;
        if(remainder == 0){
            orderAdd = startOrder + (orderValueSize -1);
        }else {
            orderAdd = startOrder + (remainder - 1);
        }
        if(orderAdd > orderValueSize){
            order = remainder - 1;
        }else {
            order = orderAdd;
        }
        GenericValue autoScheduleWorkOrder = EntityQuery.use(delegator).from("TblAutoScheduleWorkOrder")
                                  .where(EntityCondition.makeCondition(
                                        EntityCondition.makeCondition("autoWorkScheduleId",EntityOperator.EQUALS,autoWorkScheduleId),
                                        EntityCondition.makeCondition("scheduleOrder",EntityOperator.EQUALS,order)
                                   ))
                                  .queryOne();
        return getListOfWork(autoScheduleWorkOrder.get("listOfWorkId"));
    }
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
}*/
