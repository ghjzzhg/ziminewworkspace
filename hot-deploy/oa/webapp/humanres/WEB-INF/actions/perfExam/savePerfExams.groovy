import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.ServiceUtil

import java.sql.Date


public Map<String, Object> savePerfExams() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");
    String examId = context.get("examId")
    String type = context.get("type");
    String msg = "";
    Map map1 = new HashMap();//用来存放TblPerfExamAdvice的数据
    map1.put("previousAdvice", context.get("previousAdvice"));
    map1.put("nextAdvice", context.get("nextAdvice"));
    Map map2 = new HashMap();//用来存放TblPerfExamResult的数据
    map2.put("type", type);
    String time = context.get("date_i18n");
    if(UtilValidate.isNotEmpty(time )) {
        java.sql.Date date = Date.valueOf(time);
        map2.put("date", date);
    }
    if(UtilValidate.isNotEmpty(context.get("addScore"))) {
        map2.put("addScore", new BigDecimal(context.get("addScore")));
    }
    if(UtilValidate.isNotEmpty(context.get("planScore"))) {
        map2.put("planScore", new BigDecimal(context.get("planScore")));
    }
    if(UtilValidate.isNotEmpty(context.get("totalScore"))) {
        map2.put("totalScore", new BigDecimal(context.get("totalScore")));
    }
    map2.put("result", context.get("result"));
    String state = context.get("state")
    map2.put("state",state);//1表示审核通过，0表示驳回
    map2.put("remark", context.get("remark"));
    try {
        if(UtilValidate.isEmpty(examId)) {
            String newExamId = delegator.getNextSeqId("TblPerfExam").toString();//获取主键ID
            Map map = new HashMap();
            map.put("examId", newExamId);
            map.put("planId", context.get("planId"));
            map.put("department", context.get("department"));
            map.put("evaluateYear", context.get("evaluateYear"));
            map.put("evaluateMonth", context.get("evaluateMonth"));
            delegator.create(delegator.makeValidValue("TblPerfExam", map));
            map1.put("examId", newExamId);
            delegator.create(delegator.makeValidValue("TblPerfExamAdvice", map1));
            map2.put("examId", newExamId);
            delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
            List scoreList = context.get("scoreListMap");
            if(scoreList.size()!=0){
                Map scoreMap = scoreList[0];
                Iterator<Map.Entry<String, String>> it = scoreMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    String itemId = entry.getKey();
                    String score = entry.getValue();
                    GenericValue itemScore = delegator.makeValue("TblPerfExamScore", "examId", newExamId, "itemId", itemId, "type", type, "score", new BigDecimal(score));
                    itemScore.create();
                }
            }
            msg = "保存成功"
        }else {
            if(UtilValidate.isNotEmpty(type)){
                if(type.equals("PERF_EXAM_TYPE_1")){
                    map1.put("examId", examId);
                    delegator.create(delegator.makeValidValue("TblPerfExamAdvice", map1));
                    map2.put("examId", examId);
                    delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
                    List scoreList = context.get("scoreListMap");
                    if(scoreList.size()!=0) {
                        Map scoreMap = scoreList[0];
                        Iterator<Map.Entry<String, String>> it = scoreMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, String> entry = it.next();
                            String itemId = entry.getKey();
                            String score = entry.getValue();
                            GenericValue itemScore = delegator.makeValue("TblPerfExamScore", "examId", examId, "itemId", itemId, "type", type, "score", new BigDecimal(score));
                            itemScore.create();
                        }
                    }
                    msg = "保存考评信息成功"
                }else if(type.equals("PERF_EXAM_TYPE_2")||type.equals("PERF_EXAM_TYPE_3")){
                    if(state.equals("0")){
                        Map map3 = new HashMap();
                        map3.put("state",state);
                        map3.put("type",type);
                        map3.put("examId", examId);
                        delegator.create(delegator.makeValidValue("TblPerfExamResult", map3));
                    }else{
                        GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"type",type)).queryOne();
                        map2.put("examId", examId);
                        if(UtilValidate.isNotEmpty(gv)){
                            delegator.store(delegator.makeValidValue("TblPerfExamResult",map2));
                        }else{
                            delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
                        }
                        List scoreList = context.get("scoreListMap");
                        if(scoreList.size()!=0) {
                            Map scoreMap = scoreList[0];
                            Iterator<Map.Entry<String, String>> it = scoreMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<String, String> entry = it.next();
                                String itemId = entry.getKey();
                                String score = entry.getValue();
                                GenericValue itemScore = delegator.makeValue("TblPerfExamScore", "examId", examId, "itemId", itemId, "type", type, "score", new BigDecimal(score));
                                itemScore.create();
                            }
                        }


                    }
                    msg = "保存审核信息成功"
                }else{
                    Map map3 = new HashMap();
                    java.util.Date date1 = new java.util.Date();
                    map3.put("date",new java.sql.Date(date1.getTime()));
                    map3.put("state","1");
                    map3.put("type",type);
                    map3.put("examId", examId);
                    delegator.create(delegator.makeValidValue("TblPerfExamResult", map3));
                    msg = "归档成功"
                }


            }
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


public Map<String,Object> deletePerfExam(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String examId = context.get("examId");
    String type = context.get("type");
    if(UtilValidate.isNotEmpty(examId)&&UtilValidate.isNotEmpty(type)){
        delegator.removeByAnd("TblPerfExamScore",UtilMisc.toMap("examId",examId,"type",type));
        delegator.removeByAnd("TblPerfExamResult",UtilMisc.toMap("examId",examId,"type",type));
        if(type.equals("PERF_EXAM_TYPE_1")){
            delegator.removeByAnd("TblPerfExamAdvice",UtilMisc.toMap("examId",examId));
        }

    }
    successResult.put("msg","删除成功！");
    return successResult;
}

