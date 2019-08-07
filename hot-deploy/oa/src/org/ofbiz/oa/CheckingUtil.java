package org.ofbiz.oa;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lxx on 2015/8/19.
 */
public class CheckingUtil {
    public static SimpleDateFormat format;
    public static String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static void format(String formatStr) {
        format = new SimpleDateFormat(formatStr);
    }
    public static int dateSubtract(Date date1,Date date2){
        format(YEAR_MONTH_DAY);
        String formatDate1 = format.format(date1);
        String formatDate2 = format.format(date2);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        try {
            cal1.setTime(format.parse(formatDate1));
            cal2.setTime(format.parse(formatDate2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        int days = (int)(l / (1000 * 60 * 60 * 24));
        return days;
    }

    public static int getOrder(Date date,Date startDate,Date endDate,int workgroupSize,int departmentOrder){
        int order = 0;
        if(date.after(addDays(startDate,-1)) && date.before(addDays(endDate,1))){//当前日期在在自动排班日期范围内
            int dateSubtract = dateSubtract(startDate,date) + 1;
            int cycleCount = dateSubtract/workgroupSize;
            int remainder = dateSubtract%workgroupSize;
            int startOrder = departmentOrder;
            int orderAdd = 0;
            if(remainder == 0){
                orderAdd = startOrder + (workgroupSize -1);
            }else {
                orderAdd = startOrder + (remainder - 1);
            }
            order = orderAdd;
            if(orderAdd > workgroupSize){
                order = orderAdd - workgroupSize;
            }
        }
        return order;
    }
    private static String CYCLE_SIZE_UNIT_WEEK = "CST_WEEK";
    private static String CYCLE_SIZE_UNIT_MONTH = "CST_MONTH";
    /*
    * 班制序号
    * */
    public static int getWorkWeekOrder(Date date,Date startDate,Date endDate,int workWeekSize,int cycleSize,String cycleSizeUnit){
        int order = 1;
        if(date.after(addDays(startDate,-1)) && date.before(addDays(endDate,1))){//当前日期在在自动排班日期范围内
            if(CYCLE_SIZE_UNIT_WEEK.equals(cycleSizeUnit)){
                int dateSubtract = dateSubtract(startDate,date) + 1;//当前日期所在周期排班时间范围上的序号
                int cycleCount = dateSubtract/7;
                int remainder = dateSubtract%7;
                if(remainder != 0){
                    cycleCount ++;
                }
                order = cycleCount <= workWeekSize? cycleCount: (cycleCount%workWeekSize == 0 ? workWeekSize : cycleCount%workWeekSize);
            }else if(CYCLE_SIZE_UNIT_MONTH.equals(cycleSizeUnit)){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                int startMonth = calendar.get(Calendar.MONTH) + 1;
                calendar.setTime(date);
                int dateMonth = calendar.get(Calendar.MONTH) + 1;
                int monthSubstract = dateMonth - startMonth + 1;
                order = monthSubstract <= workWeekSize ? monthSubstract : monthSubstract%workWeekSize;
            }
        }
        return order;
    }

    public static Date addDays(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }
    public static int getAbsenteeismSize(Delegator delegator,LocalDispatcher dispatcher,Date startDate,Date endDate,String id,GenericValue userLogin){
        Date indexDate = startDate;
        int absenteeismSize = 0;
        while(!indexDate.after(endDate)){
            Map<String,Object> successMap = null;
            try {
                successMap = dispatcher.runSync("getListOfWorkByStaffAndDate", UtilMisc.toMap("staffId", id, "date", indexDate, "userLogin", userLogin));
                GenericValue listOfWork = (GenericValue) successMap.get("returnValue");
                if(UtilValidate.isNotEmpty(listOfWork)){
                    String listOfWorkType = (String) listOfWork.get("listOfWorkType");
                    if(!listOfWorkType.equals("LIST_OF_WORK_TYPE_02")){//休息班次
                        List<GenericValue> checkingInList = EntityQuery.use(delegator)
                                .from("TblCheckingIn")
                                .where(EntityCondition.makeCondition(
                                        EntityCondition.makeCondition("staff", EntityOperator.EQUALS, id),
                                        EntityCondition.makeCondition("registerDate", EntityOperator.BETWEEN, UtilMisc.toList(startDate, endDate)),
                                        EntityCondition.makeCondition("listOfWorkId", EntityOperator.EQUALS, listOfWork.get("listOfWorkId"))
                                )).queryList();
                        if(checkingInList.isEmpty() || checkingInList.size() == 0){
                            absenteeismSize ++;
                        }
                    }
                }
                indexDate = addDays(indexDate,1);
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return absenteeismSize;
    }
}

