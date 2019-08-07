import org.ofbiz.base.util.UtilHttp
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.oa.CheckingUtil

import java.sql.Time
import java.text.SimpleDateFormat

String type = parameters.get("type");
Map<String, Object> ctx = UtilHttp.getParameterMap(request);
int rowCount = UtilHttp.getMultiFormRowCount(ctx);
String month = "08";
String sendId = "";
String staffId = "";
double sendMoney = 0;
Calendar cal = Calendar.getInstance();
cal.set(Calendar.YEAR,cal.get(Calendar.YEAR))
cal.set(Calendar.MONTH, Integer.parseInt(month));
cal.set(Calendar.DAY_OF_MONTH, 1);
cal.add(Calendar.DAY_OF_MONTH, -1);
Date lastDate = cal.getTime();

cal.set(Calendar.DAY_OF_MONTH, 1);
Date firstDate = cal.getTime();
for (int i = 0; i < rowCount; i++) {
    String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
    String year = (String) ctx.get("year" + suffix);
    if(UtilValidate.isEmpty(sendId)){
        sendId = (String) ctx.get("sendId" + suffix);
    }
    String detailId = (String) ctx.get("detailId" + suffix);
    String salary = (String) ctx.get("salary" + suffix);
    String remarks = (String) ctx.get("remarks" + suffix);
    String typeFor = (String) ctx.get("type" + suffix);
    String title = (String) ctx.get("title" + suffix);
    String entryId = (String) ctx.get("entryId" + suffix);
    staffId = (String) ctx.get("staffId" + suffix);
    if(UtilValidate.isNotEmpty(salary)){
        if(typeFor.equals("SEND")){
            sendMoney += Double.parseDouble(salary);
        }else if (typeFor.equals("DISCOUNT")){
            sendMoney -= Double.parseDouble(salary);
        }
    }
    if (title.equals("基本工资")){
        int numberSize = CheckingUtil.getAbsenteeismSize(delegator,dispatcher,firstDate,lastDate,staffId,context.get("userLogin"));
        if (UtilValidate.isEmpty(sendId)){
            sendId = delegator.getNextSeqId("TblSalarySend").toString();
            genericValue = delegator.makeValue("TblSalarySend",UtilMisc.toMap("sendId", sendId))
            genericValue.setString("staffId",staffId);
            genericValue.setString("month",month);
            genericValue.setString("year",year);
            if (type.equals("submit")){
                genericValue.setString("state","未审");
            }else {
                genericValue.setString("state","未发");
            }
            genericValue.create();
        }else {
            genericValue = delegator.findByPrimaryKey("TblSalarySend",UtilMisc.toMap("sendId",sendId));
            if (type.equals("submit")){
                genericValue.setString("state","未审");
            }else {
                genericValue.setString("state","未发");
            }
            genericValue.store();
        }
        List checkingInForLate = delegator.findByAnd("TblCheckingFor",UtilMisc.toMap("staff",staffId,"checkingInMonth",Long.parseLong(month),"checkingInStatus","CHECKING_IN_STATUS_LATE"))
        List checkingInForLeaveEarly = delegator.findByAnd("TblCheckingFor",UtilMisc.toMap("staff",staffId,"checkingInMonth",Long.parseLong(month),"checkingInType","CHECKING_IN_STATUS_EARLY"))
        for(Map checkingMap:checkingInForLate){
            int minutes = checkingMap.get("minutes");
            attendanceList = delegator.findByAnd("TblSalaryOnAttendance",UtilMisc.toMap("type","LATE"));
            for(Map attendanceMap:attendanceList){
                int timeStart = Integer.parseInt(attendanceMap.get("timeRangeStart"));
                int timeEnd = Integer.parseInt(attendanceMap.get("timeRangeEnd"));
                int number = Integer.parseInt(attendanceMap.get("numberRangeEnd"));
                String deductType = attendanceMap.get("deductType");
                int value = Integer.parseInt(attendanceMap.get("value"));
                if (number<attendanceList.size()){
                    if (minutes>=timeStart&&minutes<=timeEnd){
                         if (deductType.equals("PERCENT")){
                             sendMoney -=sendMoney*(value/100);
                         }else if(deductType.equals("MONEY")){
                             sendMoney -=value;
                         }
                    }
                }
            }
        }
        for(Map checkingMap:checkingInForLeaveEarly){
            int minutes = checkingMap.get("minutes");
            attendanceList = delegator.findByAnd("TblSalaryOnAttendance",UtilMisc.toMap("type","LEAVE_EARLY"));
            for(Map attendanceMap:attendanceList){
                int timeStart = Integer.parseInt(attendanceMap.get("timeRangeStart"));
                int timeEnd = Integer.parseInt(attendanceMap.get("timeRangeEnd"));
                int number = Integer.parseInt(attendanceMap.get("numberRangeEnd"));
                String deductType = attendanceMap.get("deductType");
                int value = Integer.parseInt(attendanceMap.get("value"));
                if (number<attendanceList.size()){
                    if (minutes>=timeStart&&minutes<=timeEnd){
                        if (deductType.equals("PERCENT")){
                            sendMoney -=sendMoney*(value/100);
                        }else if(deductType.equals("MONEY")){
                            sendMoney -=value;
                        }
                    }
                }
            }
        }
            attendanceList = delegator.findByAnd("TblSalaryOnAttendance",UtilMisc.toMap("type","ABSENTEEISM"));
            for(Map attendanceMap:attendanceList){
                if(!UtilValidate.isEmpty(attendanceMap.get("numberRangeEnd"))){
                    int number = Integer.parseInt(attendanceMap.get("numberRangeEnd"));
                    String deductType = attendanceMap.get("deductType");
                    int value = Integer.parseInt(attendanceMap.get("value"));
                    if (number<numberSize){
                        for(int j=0;j<numberSize-number;j++){
                            if (deductType.equals("PERCENT")){
                                sendMoney -=sendMoney*(value/100);
                            }else if(deductType.equals("MONEY")){
                                sendMoney -=value;
                            }
                        }
                    }
                }else {
                    String deductType = attendanceMap.get("deductType");
                    int value = Integer.parseInt(attendanceMap.get("value"));
                    for(int j=0;j<numberSize;j++){
                        if (deductType.equals("PERCENT")){
                            sendMoney -=sendMoney*(value/100);
                        }else if(deductType.equals("MONEY")){
                            sendMoney -=value;
                        }
                    }
                }

            }
    }
    if (UtilValidate.isEmpty(detailId)){
        detailId = delegator.getNextSeqId("TblSendDetail").toString();
        genericValue = delegator.makeValue("TblSendDetail",UtilMisc.toMap("detailId",detailId));
        genericValue.setString("sendId",sendId);
        genericValue.setString("entryId",entryId);
        if (title.equals("应发工资")){
            genericValue.setString("salary",sendMoney + "");
        }else {
            genericValue.setString("salary",salary);
        }
        genericValue.setString("remarks",remarks);
        genericValue.create();
    }else {
        genericValue = delegator.findByPrimaryKey("TblSendDetail",UtilMisc.toMap("detailId",detailId));
        genericValue.setString("sendId",sendId);
        genericValue.setString("entryId",entryId);
        if (title.equals("应发工资")){
            genericValue.setString("salary",sendMoney + "");
        }else {
            genericValue.setString("salary",salary);
        }
        genericValue.setString("remarks",remarks);
        genericValue.store();
    }

}
context.data = "保存成功"
public java.sql.Time parseTime(int time){
    String timeFor = "";
    if (time<60){
        timeFor = "00:" + time + ":00";
    }else if (time==60){
        timeFor = "01:00:00";
    }else if(time>60){
        int i  = time/60;
        int j = time%60;
        timeFor = i + ":" + j + ":00";
    }
    SimpleDateFormat   df = new   SimpleDateFormat("HH:mm:ss");
    java.util.Date   Date = df.parse(timeFor);
    java.sql.Time  times=   new   java.sql.Time(Date.getTime());
    return times;
}

