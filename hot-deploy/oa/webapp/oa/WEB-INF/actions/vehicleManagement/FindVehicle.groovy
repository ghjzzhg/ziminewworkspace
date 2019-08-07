import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

List listTodayUseVehicle = new ArrayList();
List occupyTime = new ArrayList();
List occupyTimeForThrough = new ArrayList();
List occupyTimeForArrange = new ArrayList();
GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.get("partyId");
String date = parameters.get("date");
if (!UtilValidate.isNotEmpty(date)){
    date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
}
dateForDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
condition = EntityCondition.makeConditionWhere("'" + date + "'" + EntityOperator.GREATER_THAN_EQUAL_TO  + "tbl_vehicle_order.START_DATE_FOR" + " " + EntityOperator.AND + " "+ "'" + date + "'"  + EntityOperator.LESS_THAN_EQUAL_TO + "tbl_vehicle_order.END_DATE_FOR");
vehicleListByDate = delegator.findList("TblVehicleOrder", condition, null, null, null, true);
if (vehicleListByDate.size()!=0){
    for(Map map:vehicleListByDate){
        String startDateString = map.get("startDateString");
        String endDateString = map.get("endDateString");
        Date startDate = map.get("startDateFor");
        Date endDate = map.get("endDateFor");
        vehicleForOccupyeTimeMap = [:];
        String startTime,endTime;
        if (date.equals(startDateString)&&date.equals(endDateString)){
            String time= map.get("startDate").toString().split(" ")[1];
            String time1= map.get("endDate").toString().split(" ")[1];
            String hour = time.split(":")[0];
            String min = time.split(":")[1];
            String hour1 = time1.split(":")[0];
            String min1 = time1.split(":")[1];
            startTime = timeChange(hour,min);
            endTime = timeChange(hour1,min1);
        }else if(date.equals(startDateString)&&!date.equals(endDateString)){
            String time= map.get("startDate").toString().split(" ")[1];
            String hour = time.split(":")[0];
            String min = time.split(":")[1];
            startTime = timeChange(hour,min);
            endTime = "48";
        }else if(!dateForDate.before(startDate)&&dateForDate.before(endDate)){
            startTime = "0";
            endTime = "48";
        }else if(!date.equals(startDateString)&&date.equals(endDateString)){
            String time1= map.get("endDate").toString().split(" ")[1];
            String hour1 = time1.split(":")[0];
            String min1 = time1.split(":")[1];
            startTime = "0";
            endTime = timeChange(hour1,min1);
        }
        if (map.get("reviewState").equals("PERSON_ONE")){
            vehicleForOccupyeTimeMap.put("vehicleId",map.get("vehicleId"));
            vehicleForOccupyeTimeMap.put("orderId",map.get("orderId"));
            vehicleForOccupyeTimeMap.put("startDate",startTime)
            vehicleForOccupyeTimeMap.put("endDate",endTime)
            occupyTime.add(vehicleForOccupyeTimeMap);
        }else if (map.get("reviewState").equals("PERSON_TWO")){
            vehicleForOccupyeTimeMap.put("vehicleId",map.get("vehicleId"));
            vehicleForOccupyeTimeMap.put("orderId",map.get("orderId"));
            vehicleForOccupyeTimeMap.put("startDate",startTime)
            vehicleForOccupyeTimeMap.put("endDate",endTime)
            occupyTimeForThrough.add(vehicleForOccupyeTimeMap);
        }else if (map.get("reviewState").equals("PERSON_FOUR")){
            vehicleForOccupyeTimeMap.put("vehicleId",map.get("vehicleId"));
            vehicleForOccupyeTimeMap.put("orderId",map.get("orderId"));
            vehicleForOccupyeTimeMap.put("startDate",startTime)
            vehicleForOccupyeTimeMap.put("endDate",endTime)
            occupyTimeForArrange.add(vehicleForOccupyeTimeMap);
        }

    }
}
vehicleList = delegator.findByAnd("TblVehicleManagement",UtilMisc.toMap("logicDelete", "N"));
List<Map> vehicle = new ArrayList<>();
for (GenericValue map:vehicleList){
    String readyState=map.get("readyState");
    Map<String,String> value = null;
    if (readyState == "VEHICLE_NORMAL"){
        value=new HashMap<>();
        value.putAll(map);
        vehicle.add(value);
    }
}
List vehicleShow = new ArrayList();
for(Map map : vehicle){
    SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
    Date d = sim.parse(date.toString());
    int i = ((java.util.Date) map.get("buyDate")).compareTo(d);
    if(i == -1 || i == 0){
        vehicleShow.add(map);
    }
}
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
List conditionList = new ArrayList();
conditionList.add(EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "未审核"));
conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("reviewPerson", EntityOperator.EQUALS, partyId),
                                                 EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partyId)], EntityOperator.OR));
EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
List<Map> listPendingAuditVehicle = EntityQuery.use(delegator).select().from("VehicleOrderDetail").where(condition).orderBy("startDate DESC").queryList();
//listPendingAuditVehicle = delegator.findByAnd("VehicleOrderDetail",UtilMisc.toMap("reviewState","未审核", "reviewPerson", partyId));
if(UtilValidate.isNotEmpty(listPendingAuditVehicle)){
    for(Map map : listPendingAuditVehicle){
        map.put("startDate",format.format(map.get("startDate")));
        map.put("endDate",format.format(map.get("endDate")));
    }
}
List conditionList1 = new ArrayList();
conditionList1.add(EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "已审核"));
conditionList1.add(EntityCondition.makeCondition([EntityCondition.makeCondition("arrangePerson", EntityOperator.EQUALS, partyId),
                                                  EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partyId)], EntityOperator.OR));
EntityConditionList condition1 = EntityCondition.makeCondition(UtilMisc.toList(conditionList1));
List<Map> listPendingArrangedVehicle = EntityQuery.use(delegator).select().from("VehicleOrderDetail").where(condition1).orderBy("startDate DESC").queryList();
//listPendingArrangedVehicle = delegator.findByAnd("VehicleOrderDetail",UtilMisc.toMap("reviewState","已审核", "arrangePerson", partyId));
if(UtilValidate.isNotEmpty(listPendingArrangedVehicle)){
    for(Map map : listPendingArrangedVehicle){
        map.put("startDate",format.format(map.get("startDate")));
        map.put("endDate",format.format(map.get("endDate")));
    }
}
java.sql.Date todayDate = new java.sql.Date(new java.util.Date().getTime());
List conditionList2 = new ArrayList();
conditionList2.add(EntityCondition.makeCondition("startDateFor", EntityOperator.LESS_THAN_EQUAL_TO, todayDate));
conditionList2.add(EntityCondition.makeCondition("endDateFor", EntityOperator.GREATER_THAN_EQUAL_TO, todayDate));
conditionList2.add(EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "已安排"));
EntityConditionList condition2 = EntityCondition.makeCondition(UtilMisc.toList(conditionList2));
List<Map> list2 = EntityQuery.use(delegator).select().from("VehicleOrderDetail").where(condition2).orderBy("startDate DESC").queryList();
if(UtilValidate.isNotEmpty(list2)){
    for(Map map : list2){
        map.put("startDate",format.format(map.get("startDate")));
        map.put("endDate",format.format(map.get("endDate")));
    }
}
//condition = EntityCondition.makeConditionWhere("'" + date + "'" + EntityOperator.GREATER_THAN_EQUAL_TO  + "TVO.START_DATE_FOR" + " " + EntityOperator.AND + " "+ "'" + date + "'"  + EntityOperator.LESS_THAN_EQUAL_TO + "TVO.END_DATE_FOR"+ " " + EntityOperator.AND + " " + "TVO.REVIEW_STATE='已安排'");
//listTodayUseVehicle = delegator.findList("VehicleOrderDetail", condition, null, null, null, true);

context.listPendingAuditVehicle = listPendingAuditVehicle;
context.listPendingArrangedVehicle = listPendingArrangedVehicle;
context.listTodayUseVehicle = list2;
context.vehicle = vehicle;
context.vehicleShow = vehicleShow;
context.occupyTime = occupyTime;
context.occupyTimeForThrough = occupyTimeForThrough;
context.occupyTimeForArrange = occupyTimeForArrange;
context.vehicleList = vehicleList;

public String timeChange(String hour,String min){
    int hourInt = Integer.parseInt(hour);
    int minInt = Integer.parseInt(min);
    double minDouble;
    if(minInt == 0){
        minDouble = 0;
    }else if (minInt <= 30){
        minDouble = 0.5;
    }else if(minInt > 30){
        minDouble = 1;
    }
    String time = (hourInt + minDouble)*2;
    return time;
}
