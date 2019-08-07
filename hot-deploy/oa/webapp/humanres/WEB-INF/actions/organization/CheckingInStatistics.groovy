import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntity
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.oa.CheckingInParametersUtil

import java.text.SimpleDateFormat
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
workerCheckingList =  FastList.newInstance();
String departmentId = parameters.get("department");
GenericValue userLogin = (GenericValue) parameters.get("userLogin");
String dateStr = ((String)parameters.get("date")).substring(0,10);
java.sql.Date date = (new java.sql.Date(format2.parse(dateStr).getTime()));
dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
java.sql.Timestamp startDate = new java.sql.Timestamp(format.parse(dateStr + " 00:00:00").getTime());
java.sql.Timestamp endDate = new java.sql.Timestamp(format.parse(dateStr + " 23:59:59").getTime());
//查询部门下属成员
Map<String,Object> staffSuccessMap = dispatcher.runSync("getAllDepartmentMember",UtilMisc.toMap("partyId",departmentId,"userLogin",userLogin));

List<GenericValue> staffList = staffSuccessMap.get("data");

int totolPerson = staffList.size(); //总人数
int abnormal = 0;     //异常人数
int leave = 0;        //休假
int latePerson = 0;   //迟到人数
int earlyGetOff = 0; //早退人数
int absenteeism = 0; //旷工人数
int rest = 0 //休息人数
int normal = 0; //正常人数
String abnormalArr = "";     //异常人数
String latePersonArr = "";   //迟到人数
String earlyGetOffArr = ""; //早退人数
String absenteeismArr = ""; //旷工人数
String abnormalCause = ""; //异常原因


long departCount = delegator.findCountByCondition("TblHoliday",EntityCondition.makeCondition(
        UtilMisc.toList(
                EntityCondition.makeCondition("department",EntityOperator.EQUALS,departmentId),
                EntityCondition.makeCondition("startDate",EntityOperator.LESS_THAN_EQUAL_TO,date),
                EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN_EQUAL_TO,date)
        )
),null,null);

if(departCount > 0){
    leave = staffList.size();
    return;
}
if(UtilValidate.isNotEmpty(staffList)){
    for(Map<String,Object> value : staffList){
        String staffId = value.get("partyId");
        GenericValue staff = delegator.findByPrimaryKey("TblStaff",UtilMisc.toMap("partyId",staffId));
        String directDepart = staff.get("department");
        long count = delegator.findCountByCondition("TblHoliday",EntityCondition.makeCondition(
                EntityCondition.makeCondition(
                        EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staffId),
                        EntityOperator.OR,
                        EntityCondition.makeCondition("department",EntityOperator.EQUALS,directDepart)
                ),EntityOperator.EQUALS.AND,
                EntityCondition.makeCondition(
                        UtilMisc.toList(
                                EntityCondition.makeCondition("startDate",EntityOperator.LESS_THAN_EQUAL_TO,date),
                                EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN_EQUAL_TO,date)
                        )
                )
        ),null,null);
        if(count>0){
            leave ++;
            continue;
        }

        Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",staffId,"date",date,"userLogin",userLogin));
        GenericValue listOfWork = successMap.get("returnValue");//获得员工班次

        if(UtilValidate.isNotEmpty(listOfWork)){
            String listOfWorkId = listOfWork.getString("listOfWorkId");
            if (UtilValidate.isEmpty(listOfWorkId)){//班次为null,休息
                rest ++;
                continue;
            }
            String listOfWorkType = listOfWork.get("listOfWorkType");
            if(listOfWorkType.equals("LIST_OF_WORK_TYPE_02")){//休息班次
                rest ++;
            }else {
                List<GenericValue> checkingInList = null;
                if(listOfWork.get("getOffWorkTime") < listOfWork.get("toWorkTime")){//跨天的班次
                    java.sql.Date date1 = date;
                    java.sql.Date date2 = date + 1;
                    java.sql.Time time = java.sql.Time.valueOf("12:00:00");
                    checkingInList = EntityQuery.use(delegator)
                            .from("TblCheckingIn")
                            .where(EntityCondition.makeCondition(
                            EntityCondition.makeCondition("staff",EntityOperator.EQUALS, staffId),
                            EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS, date1),
                            EntityCondition.makeCondition("checkingInTime",EntityOperator.GREATER_THAN_EQUAL_TO, time),
                            EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS, "CHECKING_IN"),
                            EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                    )).queryList();
                    List<GenericValue> checkingOut = EntityQuery.use(delegator)
                            .from("TblCheckingIn")
                            .where(EntityCondition.makeCondition(
                            EntityCondition.makeCondition("staff",EntityOperator.EQUALS, staffId),
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
                            EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staffId),
                            EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS,date),
                            EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                    )).queryList();
                }

                if(UtilValidate.isNotEmpty(checkingInList) || checkingInList.size() > 0){
                    int checkingInSize = checkingInList.size();
                    if(checkingInSize == 1){
                        String type = checkingInList.get(0).getString("checkingInType");
                        String status = checkingInList.get(0).get("checkingInStatus");
                        /* StringCause  = CheckingInParametersUtil.CHECKING_IN.equals(type) ? "未签退" : "未签到";*/
                        abnormalCause += CheckingInParametersUtil.CHECKING_IN.equals(type) ? CheckingInParametersUtil.NO_CHECKING_OUT + "," : CheckingInParametersUtil.NO_CHECKING_IN + ",";
                        if (status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                            latePerson ++;  //迟到+1
                            latePersonArr += staffId +",";
                            isLate = true;
                        }else if (status.equals("CHECKING_IN_STATUS_EARLY")) {//早退
                            earlyGetOff ++; //早退+1
                            earlyGetOffArr += staffId +",";
                            isEarly = true;
                        }
                        abnormalArr += staffId + ",";
                        abnormal ++;
                    }else if (checkingInSize == 2){
                        boolean isLate = false;
                        boolean isEarly = false;
                        for(GenericValue checkingIn : checkingInList){
                            String type = checkingIn.getString("checkingInType");
                            String status = checkingIn.get("checkingInStatus");
                            if (status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                                latePerson ++;  //迟到+1
                                latePersonArr += staffId +",";
                                isLate = true;
                            }else if (status.equals("CHECKING_IN_STATUS_EARLY")) {//早退
                                earlyGetOff ++; //早退+1
                                earlyGetOffArr += staffId +",";
                                isEarly = true;
                            }
                        }
                        if(!isLate && !isEarly){
                            normal ++; //正常上班+1
                        }
                    }
                }else {
                    String toWorkTime = listOfWork.getString("toWorkTime");
                    SimpleDateFormat format1 = new SimpleDateFormat("MM/DD/YYYY");
                    Date nowDate = UtilDateTime.nowDate();
                    Date toWorkDate = UtilDateTime.toDate(format1.format(nowDate),toWorkTime);

                    String getOffWorkTime = listOfWork.getString("getOffWorkTime");
                    Date getOffWorkDate = UtilDateTime.toDate(format1.format(nowDate),getOffWorkTime);

                    if(date.compareTo(format2.parse(format2.format(nowDate))) == 0 && toWorkDate.compareTo(nowDate) < 0 && getOffWorkDate.compareTo(nowDate) > 0){
                        abnormalCause += CheckingInParametersUtil.NO_CHECKING_IN + ",";
                        abnormal ++;
                        abnormalArr += staffId + ",";
                        continue;
                    } else if(date.compareTo(format2.parse(format2.format(nowDate))) == 0 && getOffWorkDate.compareTo(nowDate) < 0){
                        abnormalCause += CheckingInParametersUtil.NO_CHECKING_OUT + ",";
                        abnormal ++;
                        abnormalArr += staffId + ",";
                        continue;
                    }

                    absenteeism++; //旷工+1
                    absenteeismArr += staffId +",";
                }
            }
        }
    }
    Map<String,Object> abnormalMap = FastMap.newInstance();
    abnormalMap.put("abnormal", abnormal);
    abnormalMap.put("abnormalArr",abnormalArr);
    abnormalMap.put("abnormalCause",abnormalCause);
    Map<String,Object> latePersonMap = FastMap.newInstance();
    latePersonMap.put("latePerson",latePerson);
    latePersonMap.put("latePersonArr",latePersonArr);
    Map<String,Object> earlyGetOffMap = FastMap.newInstance();
    earlyGetOffMap.put("earlyGetOff",earlyGetOff);
    earlyGetOffMap.put("earlyGetOffArr",earlyGetOffArr);
    Map<String,Object> absenteeismMap = FastMap.newInstance();
    absenteeismMap.put("absenteeism",absenteeism);
    absenteeismMap.put("absenteeismArr",absenteeismArr);

    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.put("totolPerson",totolPerson);
    valueMap.put("abnormal", abnormalMap);
    valueMap.put("latePerson",latePersonMap);
    valueMap.put("earlyGetOff",earlyGetOffMap);
    valueMap.put("absenteeism",absenteeismMap);
    valueMap.put("rest",rest);
    valueMap.put("leave",leave);
    valueMap.put("normal",normal);
    valueMap.put("date",date);
    context.valueMap = valueMap;
}
context.date = date;