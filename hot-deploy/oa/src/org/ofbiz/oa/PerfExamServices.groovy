import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

public Map<String, Object> savePerfExam(){
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();

    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();

    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String examId = context.get("examId");
    String totalscore = context.get("totalscore");//根据状态存入初审总分、终审总分
    Map itemScores = context.get("itemScores");
    String previousAdvice = context.get("previousAdvice");
    String previousAdviceFiles = context.get("previousAdviceFiles");
    String nextAdvice = context.get("nextAdvice");
    String nextAdviceFiles = context.get("nextAdviceFiles");
    String submitType = context.get("submitType");
    if(UtilValidate.isNotEmpty(examId)){
        GenericValue exam = delegator.findOne("TblPerfExam", UtilMisc.toMap("examId", examId), false);
        if("review".equals(submitType)){
            exam.set("evaluateStatus", "PERF_EXAM_PENDING_APPROVE");
            exam.set("reviewer", userLogin.get("partyId"));
        }else if("approve".equals(submitType)){
            exam.set("evaluateStatus", "PERF_EXAM_PENDING_RECORD");
            exam.set("score2", new BigDecimal(totalscore));
            exam.set("approver", userLogin.get("partyId"));
        }else if("record".equals(submitType)){
            exam.set("evaluateStatus", "PERF_EXAM_RECORD");
            exam.set("finalizer", userLogin.get("partyId"));
        }

        //TODO:更新分数及建议
    }else{
        GenericValue exam = delegator.makeValidValue("TblPerfExam", context);
        examId = delegator.getNextSeqId("TblPerfExam")
        exam.set("examId", examId);
        exam.set("score1", new BigDecimal(totalscore));
        exam.set("evaluateStatus", "PERF_EXAM_PENDING_REVIEW")
        exam.create();
        Iterator ite = itemScores.keySet().iterator();
        while (ite.hasNext()){
            String itemId = ite.next();
            GenericValue itemScore = delegator.makeValue("TblPerfExamScore", "examId", examId, "itemId", itemId, "score", new BigDecimal(itemScores.get(itemId)));
            itemScore.create();
        }

        if (UtilValidate.isNotEmpty(nextAdvice) || UtilValidate.isNotEmpty(previousAdvice)) {
            GenericValue advices = delegator.makeValue("TblPerfExamAdvice", "examId", examId, "previousAdvice", previousAdvice, "nextAdvice", nextAdvice);
            advices.create();
        }
        List<GenericValue> fileStore = FastList.newInstance();
        if(UtilValidate.isNotEmpty(previousAdviceFiles)){
            String[] files = previousAdviceFiles.split(",");
            for(int i = 0; i < files.length; i ++){
                fileStore.add(delegator.makeValue("TblPerfExamAdviceFiles", "fileId", delegator.getNextSeqId("TblPerfExamAdviceFiles"), "examId", examId, "type", "PREVIOUS", "dataResourceId", files[i]));
            }
        }
        if(UtilValidate.isNotEmpty(nextAdviceFiles)){
            String[] files = nextAdviceFiles.split(",");
            for(int i = 0; i < files.length; i ++){
                fileStore.add(delegator.makeValue("TblPerfExamAdviceFiles", "fileId", delegator.getNextSeqId("TblPerfExamAdviceFiles"), "examId", examId, "type", "NEXT", "dataResourceId", files[i]));
            }
        }
        if(fileStore.size() > 0){
            delegator.storeAll(fileStore);
        }
    }

    successResult.put("data", UtilMisc.toMap("examId", examId));

    return successResult;
}
public Map<String,Object> changeDPL(){
    String partyId = context.get("staff");
    staffList = delegator.findByAnd("changeDPL", UtilMisc.toMap("partyId",partyId));
    employeeRecordMap = [:];
    if (staffList.size()!=0){
        employeeRecordMap = staffList[0];
    }
    /*context.employeeRecordMap = employeeRecordMap;
    context.partyId = partyId;*/
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map map = new HashMap();
    map.putAll(employeeRecordMap);
    successResult.put("data",map);
    return successResult;
}
public Map<String,Object> deletePerfExamPerson(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String planId = context.get("planId");
    if(UtilValidate.isNotEmpty(planId)){
        Map map = new HashMap();
        map.put("planId", planId);
        map.put("planState", "0");//0表示已删除
        delegator.store(delegator.makeValidValue("TblPerfExamPerson", map));
    }
    successResult.put("msg","删除成功！");
    return successResult;
}


public Map<String, Object> savePerfExamPerson() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");
    String planId = context.get("planId");
    String msg = "";
    try {
        if(UtilValidate.isNotEmpty(planId)){
            delegator.store(delegator.makeValidValue("TblPerfExamPerson",context));
            msg = "更新成功";
        }else{
            String newPlanId = delegator.getNextSeqId("TblPerfExamPerson").toString();//获取主键ID
            context.put("planId",newPlanId)
            delegator.create(delegator.makeValidValue("TblPerfExamPerson",context));
            msg="保存成功"
        }
    }catch (Exception e){
        msg="保存失败"
        return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                msg,
                UtilMisc.toMap("errMessage", e.getMessage()), locale));
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
