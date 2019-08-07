import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

GenericValue userLogin = context.get("userLogin");
Map<String,Object> map = dispatcher.runSync("providerCases", UtilMisc.<String, Object>toMap("userLogin", userLogin));
List<Map<String,Object>> caseList = map.get("data");


if(UtilValidate.isNotEmpty(caseList)){
    if(caseList.size() >= 5){
        caseList = caseList.subList(0,5);
    }
    for(Map<String,Object> caseMap : caseList){
//        String title = caseMap.get("title").toString();
//        if(title.length() > 20){
//            caseMap.put("subCaseTitle", title.substring(0,19) + "...");
//        }
        String startDate  = caseMap.get("startDate");

        if(UtilValidate.isNotEmpty(caseMap.get("dueDate"))){
            String dueDate = caseMap.get("dueDate");

            String timeSlot = startDate + "~" + dueDate;
            long nowtime = new Date().getTime();
            long lasttime = new SimpleDateFormat("yyyy-MM-dd").parse(dueDate).getTime();
            long starttime = new SimpleDateFormat("yyyy-MM-dd").parse(startDate).getTime();
            long ts1 = lasttime - nowtime;
            long days=ts1/(1000*60*60*24);
            //总时间
            long ts2 = starttime - lasttime;
            long alldays=ts2/(1000*60*60*24);
             if(days < 0){
                caseMap.put("dataStatus","0");//过期
            }else{
                 if(alldays != 0){
                     double ts3 = days/alldays;
                     if(ts3 >= 0.1){
                         caseMap.put("dataStatus","1");//正常
                         caseMap.put("timeSlot",timeSlot);//如果正常显示时间段
                     }else{
                         caseMap.put("dataStatus", "2");//即将到期
                     }
                 }else{
                     caseMap.put("dataStatus", "2");//即将到期
                 }
            }
        }else{
            caseMap.put("dataStatus","1");//正常
            String timeSlot;
            if(UtilValidate.isNotEmpty(caseMap.get("completeDate"))){
                timeSlot = startDate + "~" + caseMap.get("completeDate");
            }else{
                timeSlot = startDate + "~";
            }

            caseMap.put("timeSlot",timeSlot);//如果正常显示时间段
        }
    }
}
context.put("caseList", caseList);