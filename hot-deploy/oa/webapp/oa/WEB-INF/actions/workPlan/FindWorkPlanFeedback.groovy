import javolution.util.FastList
import javolution.util.FastMap

feedbackType = parameters.get("feedbackType");

workPlan = FastMap.newInstance();
workPlan.put("title","学习新技术尽快适应新课题研发");
workPlan.put("department","研发中心");
workPlan.put("estimate","2015-06-15");
workPlan.put("completeStatus","进行中");
workPlan.put("rank","中");
workPlan.put("difficulty","中");
workPlan.put("planPerson","陆建");
workPlan.put("workPerson","陆建、古丽、张栋、王杰");
workPlan.put("description","任务很重要，计划需谨慎");

if(feedbackType!=null&&feedbackType.equals("childFeedback")){
    childWorkPlan = FastMap.newInstance();
    childWorkPlan.put("department","测试员/研发中心");
    childWorkPlan.put("completeTime","2015-06-12");
    childWorkPlan.put("completeStatus","进行中");
    childWorkPlan.put("workPerson","顾曦");
    childWorkPlan.put("description","尽快完成");
    childWorkPlan.put("title","学习freemark");
    childWorkPlan.put("endFeedbackPerson","王杰（2015-06-5）");
    context.childWorkPlan = childWorkPlan;
}else {
    childWorkPlan = FastMap.newInstance();
    childWorkPlan.put("department","测试员/研发中心");
    childWorkPlan.put("completeTime","2015-06-12");
    childWorkPlan.put("completeStatus","进行中");
    childWorkPlan.put("workPerson","顾曦");
    childWorkPlan.put("description","尽快完成");
    childWorkPlan.put("title","学习freemark");
    childWorkPlan.put("endFeedbackPerson","王杰（2015-06-5）");
    childWorkPlan = FastMap.newInstance();
    childWorkPlan.put("department","测试员/研发中心");
    childWorkPlan.put("completeTime","2015-06-12");
    childWorkPlan.put("completeStatus","进行中");
    childWorkPlan.put("workPerson","顾曦");
    childWorkPlan.put("description","尽快完成");
    childWorkPlan.put("title","学习freemark");
    childWorkPlan.put("endFeedbackPerson","王杰（2015-06-5）");
    childWorkPlan = FastMap.newInstance();
    childWorkPlan.put("department","测试员/研发中心");
    childWorkPlan.put("completeTime","2015-06-12");
    childWorkPlan.put("completeStatus","进行中");
    childWorkPlan.put("workPerson","顾曦");
    childWorkPlan.put("description","尽快完成");
    childWorkPlan.put("title","学习freemark");
    childWorkPlan.put("endFeedbackPerson","王杰（2015-06-5）");
    childWorkPlanList = FastList.newInstance();
    childWorkPlanList.add(childWorkPlan);

    workPlan.put("childList",childWorkPlanList);
}



context.feedbackType = feedbackType;
context.workPlan = workPlan;



