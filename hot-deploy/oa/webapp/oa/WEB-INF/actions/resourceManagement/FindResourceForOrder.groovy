import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

import java.text.SimpleDateFormat

List occupyTime = new ArrayList();
List occupyTimeForThrough = new ArrayList();
List occupyTimeForArrange = new ArrayList();
List resourceListAll = data1.resource;
List resourceListShow = new ArrayList();
String date = parameters.get("date");
if (!UtilValidate.isNotEmpty(date)){
    date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
}
for(Map map : resourceListAll){
    SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
    Date d = sim.parse(date.toString());
    int i = ((java.util.Date) map.get("createDate")).compareTo(d);
    if(i == -1 || i == 0){
        resourceListShow.add(map);
    }
}
dateForDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
condition = EntityCondition.makeConditionWhere("'" + date + "'" + EntityOperator.GREATER_THAN_EQUAL_TO  + "tbl_resource_order.START_DATE_FOR" + " " + EntityOperator.AND + " "+ "'" + date + "'"  + EntityOperator.LESS_THAN_EQUAL_TO + "tbl_resource_order.END_DATE_FOR");
resourceListByDate = delegator.findList("TblResourceOrder", condition, null, null, null, true);
if (resourceListByDate.size()!=0){
    for(Map map:resourceListByDate){
        String startDateString = map.get("startDateString");
        String endDateString = map.get("endDateString");
        Date startDate = map.get("startDateFor");
        Date endDate = map.get("endDateFor");
        resourceForOccupyeTimeMap = [:];
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
        if (map.get("reviewState").equals("PERSON_ONE")){//未审核
            if(map.get("source").equals("1")){
                resourceForOccupyeTimeMap.put("resourceId","1_" + map.get("resourceId"));
            }
            if(map.get("source").equals("3")){
                resourceForOccupyeTimeMap.put("resourceId","3_" + map.get("resourceId"));
            }
            resourceForOccupyeTimeMap.put("orderId",map.get("orderId"));
            resourceForOccupyeTimeMap.put("startDate",startTime)
            resourceForOccupyeTimeMap.put("endDate",endTime)
            occupyTime.add(resourceForOccupyeTimeMap);
        }else if (map.get("reviewState").equals("PERSON_TWO")){//已审核
            if(map.get("source").equals("1")){
                resourceForOccupyeTimeMap.put("resourceId","1_" + map.get("resourceId"));
            }
            if(map.get("source").equals("3")){
                resourceForOccupyeTimeMap.put("resourceId","3_" + map.get("resourceId"));
            }
            resourceForOccupyeTimeMap.put("orderId",map.get("orderId"));
            resourceForOccupyeTimeMap.put("startDate",startTime)
            resourceForOccupyeTimeMap.put("endDate",endTime)
            occupyTimeForThrough.add(resourceForOccupyeTimeMap);
        }else if (map.get("reviewState").equals("PERSON_FOUR")){//已安排
            if(map.get("source").equals("1")){
                resourceForOccupyeTimeMap.put("resourceId","1_" + map.get("resourceId"));
            }
            if(map.get("source").equals("3")){
                resourceForOccupyeTimeMap.put("resourceId","3_" + map.get("resourceId"));
            }
            resourceForOccupyeTimeMap.put("orderId",map.get("orderId"));
            resourceForOccupyeTimeMap.put("startDate",startTime)
            resourceForOccupyeTimeMap.put("endDate",endTime)
            occupyTimeForArrange.add(resourceForOccupyeTimeMap);
        }
    }
}
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
context.occupyTimeForThrough = occupyTimeForThrough;
context.occupyTimeForArrange = occupyTimeForArrange;
context.occupyTime = occupyTime;
context.resourceListShow = resourceListShow;
