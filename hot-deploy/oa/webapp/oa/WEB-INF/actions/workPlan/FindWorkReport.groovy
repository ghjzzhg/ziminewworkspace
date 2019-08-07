import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil


Locale locale = (Locale) context.get("locale");
Map<String, Object> successResult = ServiceUtil.returnSuccess();
GenericValue userLogin = (GenericValue) context.get("userLogin");

workReportList = FastList.newInstance();
workReportMap = FastMap.newInstance();
List<GenericValue> reports = EntityQuery.use(delegator).select().from("WorkReportList").queryList();
for(GenericValue workReport : reports){
        workReportMap.put("workTime",workReport.get("startTime").toString()+" "+workReport.get("endTime").toString());
        GenericValue reportTypeName = EntityQuery.use(delegator).select().from("Enumeration").where(EntityCondition.makeCondition("enumId",workReport.get("reportType"))).queryOne();
        workReportMap.put("startTime",workReport.get("startTime"));
        workReportMap.put("inputPerson",workReport.get("inputPerson"));
        workReportMap.put("reportType",reportTypeName.get("description"));
        workReportMap.put("reportNumber",workReport.get("reportNumber"));
        workReportMap.put("reportTitle",workReport.get("reportTitle"));
        workReportMap.put("workReportId",workReport.get("workReportId"));
        workReportMap.put("firstName",workReport.get("firstName"));
        workReportMap.put("endTime",workReport.get("endTime"));
        workReportMap.put("request",workReport.get("request"));
        GenericValue reportPlanName = EntityQuery.use(delegator).select().from("Enumeration").where(EntityCondition.makeCondition("enumId",workReport.get("plan"))).queryOne();
        GenericValue reportStatusName = EntityQuery.use(delegator).select().from("Enumeration").where(EntityCondition.makeCondition("enumId",workReport.get("status"))).queryOne();
        workReportMap.put("plan",reportPlanName.get("description"));
        workReportMap.put("status",reportStatusName.get("description"));
        //todo将最新反馈内容放置在map中
        String feedbackInfo = "";
        List<GenericValue> feedbacklist = EntityQuery.use(delegator).select().from("WorkReportFeedback").where(EntityCondition.makeCondition("feedbackMiddleId",workReport.get("workReportId"))).orderBy("feedbackTime").queryList();
        if(null != feedbacklist && feedbacklist.size() > 0){
                GenericValue feedback = feedbacklist.get(feedbacklist.size()-1);
                feedbackInfo = feedback.get("firstName") + "/" + feedback.get("feedbackTime");
        }
        workReportMap.put("feedbackInfo",feedbackInfo);
        workReportList.add(workReportMap);
}
context.workReportList = workReportList;


