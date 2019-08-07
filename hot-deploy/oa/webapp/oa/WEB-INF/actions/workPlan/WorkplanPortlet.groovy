import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

GenericValue userLogin = (GenericValue)context.get("userLogin");
Map<String,Object> returnWorkPlanMap = dispatcher.runSync("searchWorkPlan",UtilMisc.toMap("userLogin",userLogin),);
List<FastMap<String,Object>> workPlanDataList = (List<FastMap<String,Object>>)returnWorkPlanMap.get("workPlanList");

SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
Integer noNum = 0;
List<Map<String,Object>> workPlanList = new ArrayList<Map<String,Object>>();
if(UtilValidate.isNotEmpty(workPlanDataList)){
    String today = format.format(new Date());
    for(FastMap workPlan : workPlanDataList){
        Map<String,Object> map = new HashMap<String,Object>();
        String startTime = workPlan.get("startTime");
        map.putAll(workPlan);
        String startDate = format.format(format.parse(startTime));
        map.put("startTime",startDate);
        if(startDate.equals(today)){
            noNum++;
        }
        workPlanList.add(map);
    }
}
context.notices= workPlanList;
context.noNum = noNum;
