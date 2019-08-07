import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.config.model.Datasource
import org.ofbiz.entity.config.model.EntityConfig
import org.ofbiz.entity.datasource.GenericHelperInfo
import org.ofbiz.entity.jdbc.ConnectionFactoryLoader
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * 查询绩效考核记录
 * @return
 */
public Map<String, Object> searchPerfExam(){
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    String partyId = userLoginId.getString("partyId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;
    int yearNow = Calendar.getInstance().get(Calendar.YEAR);
    int monthNow = Calendar.getInstance().get(Calendar.MONTH);
    int monthNows =monthNow+1;
    Long year ;
    Long month;
    String name = context.get("nameForSearch");
    String department = context.get("departmentForSearch");
    String post = context.get("postForSearch");
    Long evaluateYear = context.get("evaluateYear");
    Long evaluateMonth = context.get("evaluateMonth");
    if(UtilValidate.isNotEmpty(evaluateYear)){
        year = evaluateYear;
    }else{
        year = Calendar.getInstance().get(Calendar.YEAR);
    }
    if(UtilValidate.isNotEmpty(evaluateMonth)){
        month = evaluateMonth ;
    }else{
        month = Calendar.getInstance().get(Calendar.MONTH);
        if(month==0){
            month = 12;
            year =year-1;
        }
    }
    List<EntityCondition> conditionList = FastList.newInstance();
    conditionList.add(EntityCondition.makeCondition("planState",EntityOperator.EQUALS,"1"));
    if(UtilValidate.isNotEmpty(name)){
        conditionList.add(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,name));
    }
    if(UtilValidate.isNotEmpty(department)){
        conditionList.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if(UtilValidate.isNotEmpty(post)){
        conditionList.add(EntityCondition.makeCondition("post",EntityOperator.EQUALS,post));
    }
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<GenericValue> perfExamList = new ArrayList<>();
    EntityListIterator perfExamPersonIterator = EntityQuery.use(delegator).select().from("PerfExamPersonDetail").where(condition).queryIterator();
    if(null != perfExamPersonIterator && perfExamPersonIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = perfExamPersonIterator.getResultsSizeAfterPartialList();
        List<GenericValue> list = perfExamPersonIterator.getPartialList(lowIndex, viewSize);
        Iterator<GenericValue> it = list.iterator();
        perfExamPersonIterator.close();
        while (it.hasNext()) {
            GenericValue  perfExamPerson =it.next();
            String seasonMonth = getSeasonMonth(monthNows);
            String [] newSeasonMonth = seasonMonth.split(",");
            String semiannualMonth = getSemiannualMonth(monthNows);
            String [] newSemiannualMonth = semiannualMonth.split(",");
            java.sql.Date startDate = perfExamPerson.get("startDate");
            java.util.Date date = new java.util.Date(startDate.getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            selectedYear = cal.get(Calendar.YEAR);
            selectedMonth = cal.get(Calendar.MONTH);
            Map map = new HashMap();
            String planId = perfExamPerson.get("planId");
            String perfExamCycle = perfExamPerson.get("perfExamCycle");
            GenericValue perfExamCycleMap = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumId",perfExamCycle)).queryOne();
            String evaluator = perfExamPerson.get("evaluator");//考评人
            String reviewer = perfExamPerson.get("reviewer");//初审人
            String approver = perfExamPerson.get("approver");//终审人
            String finalizer = perfExamPerson.get("finalizer");//归档人
            if(perfExamCycle.equals("PERF_EXAM_CYCLE_1")){//月份考核
                Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                map.putAll(perfExamPerson);
                map.putAll(maps);
            }else if(perfExamCycle.equals("PERF_EXAM_CYCLE_2")){//季度考核
                List arrayList = new ArrayList();
                for(int i=0;i<newSeasonMonth.length;i++)
                {
                    String months = newSeasonMonth[i];
                    long m = Long.parseLong(months);
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",m)).queryOne();
                    if(UtilValidate.isNotEmpty(genericValue)){
                        arrayList.add(genericValue);
                    }
                }
                if(arrayList.size()>0){
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",month)).queryOne();
                    if(UtilValidate.isNotEmpty(genericValue)){
                        Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                        map.putAll(perfExamPerson);
                        map.putAll(maps);
                    }
                }else{
                    Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                    map.putAll(perfExamPerson);
                    map.putAll(maps);
                }
            }else if(perfExamCycle.equals("PERF_EXAM_CYCLE_3")){//半年考核
                List arrayList = new ArrayList();
                for(int i=0;i<newSemiannualMonth.length;i++)
                {
                    String months = newSemiannualMonth[i];
                    long m = Long.parseLong(months);
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",m)).queryOne();
                    if(UtilValidate.isNotEmpty(genericValue)){
                        arrayList.add(genericValue);
                    }
                }
                if(arrayList.size()>0){
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",month)).queryOne();
                    if(UtilValidate.isNotEmpty(genericValue)){
                        Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                        map.putAll(perfExamPerson);
                        map.putAll(maps);
                    }
                }else{
                    Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                    map.putAll(perfExamPerson);
                    map.putAll(maps);
                }
            }else if(perfExamCycle.equals("PERF_EXAM_CYCLE_4")){//年度考核
                List arrayList = new ArrayList();
                for(int i=1;i<=12;i++)
                {
                    long m = i;
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",m)).queryOne();
                    if(UtilValidate.isNotEmpty(genericValue)){
                        arrayList.add(genericValue);
                    }
                }
                if(arrayList.size()>0){
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",month)).queryOne();
                    if(UtilValidate.isNotEmpty(genericValue)){
                        Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                        map.putAll(perfExamPerson);
                        map.putAll(maps);
                    }
                }else{
                    Map maps =powerMap(planId,evaluator,reviewer,approver,finalizer,year,month,partyId);
                    map.putAll(perfExamPerson);
                    map.putAll(maps);
                }
            }
            if(selectedYear<yearNow){
                if(UtilValidate.isNotEmpty(map)){
                    map.put("perfExamCycle",perfExamCycleMap.get("description"));
                    perfExamList.add(map);
                }
            }else if(selectedYear==yearNow){
                if(selectedMonth<monthNow){
                    if(UtilValidate.isNotEmpty(map)){
                        map.put("perfExamCycle",perfExamCycleMap.get("description"));
                        perfExamList.add(map);
                    }
                }
            }
        }
    }
    perfExamPersonIterator.close();

    Map statisticsMap = statistics(year,month);
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("statisticsMap",statisticsMap);
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("name",name);
    viewData.put("department",department);
    viewData.put("post",post);
    viewData.put("evaluateYear",year);
    viewData.put("evaluateMonth",month);
    viewData.put("perfExamList",perfExamList);
    successResult.put("data",viewData);
    return successResult;
}
/**
 *
 * @param planId
 * @param evaluator
 * @param reviewer
 * @param approver
 * @param finalizer
 * @param year
 * @param month
 * @param partyId
 * @return
 */
public Map powerMap(String planId,String evaluator,String reviewer,String approver,String finalizer,Long year,Long month,String partyId) {
    Map map = new HashMap();
    String examId =""
    String evaluatePower="";
    String reviewePower="";
    String approvePower="";
    String finalizePower="";
    String evaluateDeletePower="";
    String revieweDeletePower="";
    String approveDeletePower="";
    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",month)).queryOne();
    if(genericValue==null){
        if(partyId.equals(evaluator)==true){
            evaluatePower="1";//1表示有权限
        }
    }else{
        examId = genericValue.get("examId");
        String state = genericValue.get("state");
        GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"resultType","PERF_EXAM_TYPE_1")).queryOne();
        GenericValue gv1 = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"resultType","PERF_EXAM_TYPE_2")).queryOne();
        GenericValue gv2 = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"resultType","PERF_EXAM_TYPE_3")).queryOne();
        GenericValue gv3 = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"resultType","PERF_EXAM_TYPE_4")).queryOne();
        if(state == null){
            if(partyId.equals(evaluator)==true){
                evaluatePower="1";
            }
        }else{
            map.put("evaluatorScore",gv.get("totalScore"));
            map.put("evaluateDate",gv.get("resultDate"));
            if(state.equals("PERF_EXAM_STATE_1")){
                if(partyId.equals(evaluator)==true){
                    evaluateDeletePower = "1";
                }
                if(partyId.equals(reviewer)==true){
                    reviewePower = "1";
                }
            }
            if(state.equals("PERF_EXAM_STATE_2")){
                map.put("reviewerScore",gv1.get("totalScore"));
                map.put("reviewerDate",gv1.get("resultDate"));
                if(partyId.equals(reviewer)==true){
                    revieweDeletePower="1";
                }
                if(partyId.equals(approver)==true){
                    approvePower="1";
                }
            }
            if(state.equals("PERF_EXAM_STATE_3")){
                if(partyId.equals(evaluator)==true){
                    evaluateDeletePower = "1";
                }
                if(partyId.equals(reviewer)==true){
                    revieweDeletePower="1";
                }
            }
            if(state.equals("PERF_EXAM_STATE_4")) {
                map.put("reviewerScore", gv1.get("totalScore"));
                map.put("reviewerDate", gv1.get("resultDate"));
                map.put("approverScore", gv2.get("totalScore"));
                map.put("approverDate", gv2.get("resultDate"));
                if (partyId.equals(finalizer) == true) {
                    finalizePower = "1";
                }
                if (partyId.equals(approver) == true) {
                    approveDeletePower = "1";
                }
            }
            if(state.equals("PERF_EXAM_STATE_5")) {
                map.put("reviewerScore", gv1.get("totalScore"));
                map.put("reviewerDate", gv1.get("resultDate"));
                if(partyId.equals(reviewer)==true){
                    revieweDeletePower="1";
                }
                if (partyId.equals(approver) == true) {
                    approveDeletePower = "1";
                }
            }
            if(state.equals("PERF_EXAM_STATE_6")) {
                map.put("reviewerScore", gv1.get("totalScore"));
                map.put("reviewerDate", gv1.get("resultDate"));
                map.put("approverScore", gv2.get("totalScore"));
                map.put("approverDate", gv2.get("resultDate"));
                map.put("finalizerDate",gv3.get("resultDate"));
            }
        }
    }
    map.put("examId",examId);
    map.put("evaluatePower",evaluatePower);
    map.put("reviewePower",reviewePower);
    map.put("approvePower",approvePower);
    map.put("finalizePower",finalizePower);
    map.put("evaluateDeletePower",evaluateDeletePower);
    map.put("revieweDeletePower",revieweDeletePower);
    map.put("approveDeletePower",approveDeletePower);
    map.put("evaluateYear",year);
    map.put("evaluateMonth",month);
    return map;
}

/**
 *某月所在的季度有哪些月份
 * @param monthNow
 * @return
 */
public String getSeasonMonth(int monthNow) {
    String seasonMonth="";
    if(monthNow>=1&&monthNow<=3){
        seasonMonth="1,2,3";
    }else if(monthNow>=4&&monthNow<=6){
        seasonMonth="4,5,6";
    }else if(monthNow>=7&&monthNow<=9){
        seasonMonth="7,8,9";
    }else if(monthNow>=10&&monthNow<=12){
        seasonMonth="10,11,12";
    }
    return seasonMonth;
}
/**
 *某月所在的半年有哪些月份
 * @param monthNow
 * @return
 */
public String getSemiannualMonth(int monthNow) {
    String semiannualMonth="";
    if(monthNow>=1&&monthNow<=6){
        semiannualMonth="1,2,3,4,5,6";
    }else if(monthNow>=7&&monthNow<=12){
        semiannualMonth="7,8,9,10,11,12";
    }
    return semiannualMonth;
}
/**
 * 统计
 * @param evaluateYear
 * @param evaluateMonth
 * @return
 */
public Map statistics(Long evaluateYear,Long evaluateMonth){
    int yearNow = Calendar.getInstance().get(Calendar.YEAR);
    int monthNow = Calendar.getInstance().get(Calendar.MONTH);
    int notEvaluateCount = 0;//未评分的个数
    int evaluateCount = 0;//已经评分的个数
    int revieweCount = 0;//已经初审的个数
    int approveCount = 0;//已经终审的个数
    int finalizeCount = 0;//已经归档的个数
    int CCount1 = 0;//C-等级的个数
    int CCount = 0;//C等级的个数
    int BCount = 0;//B等级的个数
    int BCount1 = 0;//B+等级的个数
    int ACount = 0;//A等级的个数
    int ACount1 = 0;//A+等级的个数
    List list = EntityQuery.use(delegator).select().from("TblPerfExamPerson").where(UtilMisc.toMap("planState","1","perfExamCycle","PERF_EXAM_CYCLE_1")).queryList();
    if(list.size() > 0) {
        Iterator<GenericValue> it = list.iterator();
        while (it.hasNext()) {
            GenericValue  perfExamPerson =it.next();
            String planId = perfExamPerson.get("planId");
            String examId =""
            if(UtilValidate.isNotEmpty(evaluateYear)){
                year = evaluateYear;
            }else{
                year = Calendar.getInstance().get(Calendar.YEAR);
            }
            if(UtilValidate.isNotEmpty(evaluateMonth)){
                month = evaluateMonth ;
            }else{
                month = Calendar.getInstance().get(Calendar.MONTH);
            }
            java.sql.Date startDate = perfExamPerson.get("startDate");
            java.util.Date date = new java.util.Date(startDate.getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            selectedYear = cal.get(Calendar.YEAR);
            selectedMonth = cal.get(Calendar.MONTH);
            if(selectedYear<yearNow){
                GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",month)).queryOne();
                if(genericValue==null){
                    notEvaluateCount = notEvaluateCount+1;
                }else{
                    examId = genericValue.get("examId");
                    List CCountList1 =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","C-")).queryList();
                    CCount1=CCount1+CCountList1.size();
                    List CCountList =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","C")).queryList();
                    CCount=CCount+CCountList.size();
                    List BCountList1 =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","B+")).queryList();
                    BCount1=BCount1+BCountList1.size();
                    List BCountList =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","B")).queryList();
                    BCount=BCount+BCountList.size();
                    List ACountList1 =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","A+")).queryList();
                    ACount1=ACount1+ACountList1.size();
                    List ACountList =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","A")).queryList();
                    ACount=ACount+ACountList.size();
                    String state = genericValue.get("state");
                    if(state == null){
                        notEvaluateCount = notEvaluateCount+1;
                    }else{
                        if(state.equals("PERF_EXAM_STATE_1")){
                            evaluateCount = evaluateCount+1;
                        }
                        if(state.equals("PERF_EXAM_STATE_2")){
                            revieweCount = revieweCount+1;
                        }
                        if(state.equals("PERF_EXAM_STATE_3")){
                            evaluateCount = evaluateCount+1;
                        }
                        if(state.equals("PERF_EXAM_STATE_4")) {
                            approveCount = approveCount+1;
                        }
                        if(state.equals("PERF_EXAM_STATE_5")) {
                            revieweCount = revieweCount+1;
                        }
                        if(state.equals("PERF_EXAM_STATE_6")) {
                            finalizeCount = finalizeCount+1;
                        }
                    }
                }
            }else if(selectedYear==yearNow){
                if(selectedMonth<monthNow){
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("planId",planId,"evaluateYear",year,"evaluateMonth",month)).queryOne();
                    if(genericValue==null){
                        notEvaluateCount = notEvaluateCount+1;
                    }else{
                        examId = genericValue.get("examId");
                        List CCountList1 =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","C-")).queryList();
                        CCount1=CCount1+CCountList1.size();
                        List CCountList =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","C")).queryList();
                        CCount=CCount+CCountList.size();
                        List BCountList1 =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","B+")).queryList();
                        BCount1=BCount1+BCountList1.size();
                        List BCountList =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","B")).queryList();
                        BCount=BCount+BCountList.size();
                        List ACountList1 =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","A+")).queryList();
                        ACount1=ACount1+ACountList1.size();
                        List ACountList =EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId",examId,"result","A")).queryList();
                        ACount=ACount+ACountList.size();
                        String state = genericValue.get("state");
                        if(state == null){
                            notEvaluateCount = notEvaluateCount+1;
                        }else{
                            if(state.equals("PERF_EXAM_STATE_1")){
                                evaluateCount = evaluateCount+1;
                            }
                            if(state.equals("PERF_EXAM_STATE_2")){
                                revieweCount = revieweCount+1;
                            }
                            if(state.equals("PERF_EXAM_STATE_3")){
                                evaluateCount = evaluateCount+1;
                            }
                            if(state.equals("PERF_EXAM_STATE_4")) {
                                approveCount = approveCount+1;
                            }
                            if(state.equals("PERF_EXAM_STATE_5")) {
                                revieweCount = revieweCount+1;
                            }
                            if(state.equals("PERF_EXAM_STATE_6")) {
                                finalizeCount = finalizeCount+1;
                            }
                        }
                    }
                }
            }
        }
    }
    Map statisticsMap = new HashMap();
    statisticsMap.put("notEvaluateCount",notEvaluateCount);
    statisticsMap.put("evaluateCount",evaluateCount);
    statisticsMap.put("revieweCount",revieweCount);
    statisticsMap.put("approveCount",approveCount);
    statisticsMap.put("finalizeCount",finalizeCount);
    statisticsMap.put("CCount1",CCount1);
    statisticsMap.put("CCount",CCount);
    statisticsMap.put("BCount",BCount);
    statisticsMap.put("BCount1",BCount1);
    statisticsMap.put("ACount",ACount);
    statisticsMap.put("ACount1",ACount1);
    return statisticsMap;
}

/**
 * 寻找绩效考核的记录
 * @return
 */
public Map<String,Object> findPerfExam(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String examId = context.get("examId");
    String planId = context.get("planId");
    String type = context.get("type");
    Long year = context.get("evaluateYear");
    Long month = context.get("evaluateMonth");
    Map map = new HashMap();
    String perfExamType="";
    if(UtilValidate.isNotEmpty(planId)) {
        GenericValue genericValue = EntityQuery.use(delegator).select().from("PerfExamPersonDetail").where(UtilMisc.toMap("planId", planId)).queryOne();
        perfExamType = genericValue.get("perfExamType");
        if (perfExamType && !"1".equals(perfExamType)) {
            perfExamItems = from("TblPerfExamItem").where(EntityCondition.makeCondition
                    (UtilMisc.toList(EntityCondition.makeCondition("parentType", EntityOperator.EQUALS, perfExamType), EntityCondition.makeCondition("typeId", EntityOperator.EQUALS, perfExamType)), EntityOperator.OR)).
                    orderBy("parentType", "typeId").queryList();
            map.put("perfExamItemsList",perfExamItems);
        } else {
            perfExamItems = from("TblPerfExamItem").orderBy("parentType", "typeId").queryList();
            map.put("perfExamItemsList",perfExamItems);
        }
        DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        dynamicViewEntity.addMemberEntity("T", "TblPerfExamItem");
        dynamicViewEntity.addAlias("T", "count", "itemId", null, null, false, "count");
        dynamicViewEntity.addAlias("T", "typeId", "typeId", null, null, true, null);
        typeCounts = from(dynamicViewEntity).queryList();
        typeCountMap = [:];
        if(typeCounts){
            for (GenericValue count : typeCounts) {
                typeCountMap.put(count.get("typeId"), count.get("count"));
            }
        }
        map.put("typeCountMap",typeCountMap);
        map.put("perfExamType",genericValue.get("description"));
        if(type.equals("PERF_EXAM_TYPE_1")){
            map.put("evaluator",genericValue.get("evaluatorName"));
        }
        if(type.equals("PERF_EXAM_TYPE_2")){
            map.put("reviewer",genericValue.get("reviewerName"));
        }
        if(type.equals("PERF_EXAM_TYPE_3")){
            map.put("approver",genericValue.get("approverName"));
        }
        map.put("examId",examId);
        map.put("planId",planId);
        map.put("type",type);
        map.put("fullName",genericValue.get("fullName"));
        map.put("workerSn",genericValue.get("workerSn"));
        map.put("departmentName",genericValue.get("departmentName"));
        map.put("occupationName",genericValue.get("occupationName"));
        map.put("description",genericValue.get("description"));

    }
    if(UtilValidate.isNotEmpty(examId)) {
        GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("examId", examId)).queryOne();
        String department = genericValue.get("department");
        if(UtilValidate.isNotEmpty(department)) {
            GenericValue departmentMap = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId", department)).queryOne();
            map.put("department",departmentMap.get("groupName"));
        }
        map.put("evaluateYear",genericValue.get("evaluateYear"));
        map.put("evaluateMonth",genericValue.get("evaluateMonth"));
        if(UtilValidate.isNotEmpty(type) && !type.equals("PERF_EXAM_TYPE_1")) {
            GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", "PERF_EXAM_TYPE_1")).queryOne();
            map.put("evaluateAddScore",gv.get("addScore"));
            map.put("evaluatePlanScore",gv.get("planScore"));
            GenericValue gv1 = EntityQuery.use(delegator).select().from("TblPerfExamAdvice").where(UtilMisc.toMap("examId", examId)).queryOne();
            map.put("previousAdvice",gv1.get("previousAdvice"));
            map.put("nextAdvice",gv1.get("nextAdvice"));
            if(type.equals("PERF_EXAM_TYPE_2")){
                GenericHelperInfo helperInfo = delegator.getGroupHelperInfo("org.ofbiz");
                Datasource datasourceInfo = EntityConfig.getDatasource(helperInfo.getHelperBaseName());
                // 获得数据库的连接
                Connection connection = ConnectionFactoryLoader.getInstance().getConnection(helperInfo, datasourceInfo.getInlineJdbc());
                String sql ="SELECT a.*, b.evaluateScore FROM (SELECT ITEM_ID,TYPE_ID,PARENT_TYPE,TITLE,SCORE,SCORE1,SCORE2,SCORE3,SCORE4 " +
                        "FROM TBL_PERF_EXAM_ITEM WHERE((PARENT_TYPE = ? OR TYPE_ID = ? ))ORDER BY PARENT_TYPE ASC,TYPE_ID ASC) a " +
                        "LEFT JOIN (SELECT s.ITEM_ID,s.SCORE AS evaluateScore FROM tbl_perf_exam_score s WHERE EXAM_ID = ? AND " +
                        " SCORE_TYPE = ?) b ON a.ITEM_ID = b.ITEM_ID";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1,perfExamType);
                statement.setString(2,perfExamType);
                statement.setString(3,examId);
                statement.setString(4,"PERF_EXAM_TYPE_1");
                ResultSet rs = statement.executeQuery();
                List list =new ArrayList();
                while(rs.next()){
                    Map map1 = new HashMap();
                    map1.put("itemId",rs.getString("ITEM_ID"));
                    map1.put("title",rs.getString("TITLE"));
                    map1.put("typeId",rs.getString("TYPE_ID"));
                    map1.put("score",rs.getString("SCORE"));
                    map1.put("score1",rs.getString("SCORE1"));
                    map1.put("score2",rs.getString("SCORE2"));
                    map1.put("score3",rs.getString("SCORE3"));
                    map1.put("score4",rs.getString("SCORE4"));
                    map1.put("evaluateScore",rs.getString("evaluateScore"));
                    list.add(map1);
                }
                map.put("perfExamItemsList",list);
            }
            if(type.equals("PERF_EXAM_TYPE_3")){
                GenericValue gv2 = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", "PERF_EXAM_TYPE_2")).queryOne();
                map.put("reviewerAddScore",gv2.get("addScore"));
                map.put("reviewerPlanScore",gv2.get("planScore"));
                map.put("reviewerRemark",gv2.get("remark"));
                GenericHelperInfo helperInfo = delegator.getGroupHelperInfo("org.ofbiz");
                Datasource datasourceInfo = EntityConfig.getDatasource(helperInfo.getHelperBaseName());
                // 获得数据库的连接
                Connection connection = ConnectionFactoryLoader.getInstance().getConnection(helperInfo, datasourceInfo.getInlineJdbc());
                String sql = "SELECT c.*,d.reviewerScore FROM(SELECT a.*, b.evaluateScore FROM (SELECT ITEM_ID,TYPE_ID,PARENT_TYPE,TITLE,SCORE,SCORE1,SCORE2,SCORE3,SCORE4 " +
                        "FROM TBL_PERF_EXAM_ITEM WHERE ((PARENT_TYPE = ? OR TYPE_ID = ? ))ORDER BY PARENT_TYPE ASC,TYPE_ID ASC) a LEFT JOIN " +
                        "(SELECT ITEM_ID,SCORE AS evaluateScore FROM tbl_perf_exam_score s WHERE EXAM_ID = ? AND SCORE_TYPE = ?) b ON a.ITEM_ID = b.ITEM_ID)c " +
                        "LEFT JOIN(SELECT ITEM_ID,SCORE AS reviewerScore FROM tbl_perf_exam_score  WHERE EXAM_ID = ?  AND SCORE_TYPE = ?) d ON c.ITEM_ID = d.ITEM_ID";

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1,perfExamType);
                statement.setString(2,perfExamType);
                statement.setString(3,examId);
                statement.setString(4,"PERF_EXAM_TYPE_1");
                statement.setString(5,examId);
                statement.setString(6,"PERF_EXAM_TYPE_2");
                ResultSet rs = statement.executeQuery();
                List list =new ArrayList();
                while(rs.next()){
                    Map map1 = new HashMap();
                    map1.put("itemId",rs.getString("ITEM_ID"));
                    map1.put("title",rs.getString("TITLE"));
                    map1.put("typeId",rs.getString("TYPE_ID"));
                    map1.put("score",rs.getString("SCORE"));
                    map1.put("score1",rs.getString("SCORE1"));
                    map1.put("score2",rs.getString("SCORE2"));
                    map1.put("score3",rs.getString("SCORE3"));
                    map1.put("score4",rs.getString("SCORE4"));
                    map1.put("evaluateScore",rs.getString("evaluateScore"));
                    map1.put("reviewerScore",rs.getString("reviewerScore"));
                    list.add(map1);
                }
                map.put("perfExamItemsList",list);
            }

        }

    }else{
        map.put("evaluateYear",year);
        map.put("evaluateMonth",month);
    }
    map.put("type",type);
    successResult.put("perfExamMap",map);
    return successResult;
}

/**
 * 保存绩效考核
 * @return
 */
public Map<String, Object> savePerfExam() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");
    String examId = context.get("examId")
    String type = context.get("type");
    String msg = "";
    Map map1 = new HashMap();//用来存放TblPerfExamAdvice的数据
    map1.put("previousAdvice", context.get("previousAdvice"));
    map1.put("nextAdvice", context.get("nextAdvice"));
    Map map2 = new HashMap();//用来存放TblPerfExamResult的数据
    map2.put("resultType", type);
    String time = context.get("date_i18n");
    if(UtilValidate.isNotEmpty(time )) {
        java.sql.Date date = Date.valueOf(time);
        map2.put("resultDate", date);
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
    String state = context.get("state");
    map2.put("result", context.get("result"));
    map2.put("remark", context.get("remark"));
    List scoreList = context.get("scoreListMap");
    try {
        if(UtilValidate.isEmpty(examId)) {
            String newExamId = delegator.getNextSeqId("TblPerfExam").toString();//获取主键ID
            Map map = new HashMap();
            map.put("examId", newExamId);
            map.put("planId", context.get("planId"));
            map.put("department", context.get("department"));
            map.put("evaluateYear", context.get("evaluateYear"));
            map.put("evaluateMonth", context.get("evaluateMonth"));
            map.put("state", "PERF_EXAM_STATE_1");
            delegator.create(delegator.makeValidValue("TblPerfExam", map));
            map1.put("examId", newExamId);
            delegator.create(delegator.makeValidValue("TblPerfExamAdvice", map1));
            map2.put("examId", newExamId);
            delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
            savePerfExamScore(scoreList,newExamId,type);
            msg = "保存成功"
        }else {
            if(UtilValidate.isNotEmpty(type)){
                if(type.equals("PERF_EXAM_TYPE_1")){
                    Map map = new HashMap();
                    map.put("examId", examId);
                    map.put("state", "PERF_EXAM_STATE_1");
                    delegator.store(delegator.makeValidValue("TblPerfExam",map));
                    map1.put("examId", examId);
                    delegator.create(delegator.makeValidValue("TblPerfExamAdvice", map1));
                    map2.put("examId", examId);
                    delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
                    savePerfExamScore(scoreList,examId,type);
                    msg = "保存考评信息成功"
                }else if(type.equals("PERF_EXAM_TYPE_2")){
                    Map map = new HashMap();
                    map.put("examId", examId);
                    map.put("state", state);
                    delegator.store(delegator.makeValidValue("TblPerfExam",map));
                    if(state.equals("PERF_EXAM_STATE_2")) {
                        GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", type)).queryOne();
                        map2.put("examId", examId);
                        if (UtilValidate.isNotEmpty(gv)) {
                            delegator.store(delegator.makeValidValue("TblPerfExamResult", map2));
                        } else {
                            delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
                        }
                        savePerfExamScore(scoreList,examId,type);
                        msg = "初审通过!";
                    }else{
                        GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", type)).queryOne();
                        Map map3 = new HashMap();
                        java.util.Date date1 = new java.util.Date();
                        map3.put("date",new java.sql.Date(date1.getTime()));
                        map3.put("resultType",type);
                        map3.put("examId", examId);
                        map3.put("remark", context.get("remark"));
                        if (UtilValidate.isNotEmpty(gv)) {
                            delegator.store(delegator.makeValidValue("TblPerfExamResult", map3));
                        } else {
                            delegator.create(delegator.makeValidValue("TblPerfExamResult", map3));
                        }
                        msg = "初审驳回!";
                    }
                }else if(type.equals("PERF_EXAM_TYPE_3")){
                    Map map = new HashMap();
                    map.put("examId", examId);
                    map.put("state", state);
                    delegator.store(delegator.makeValidValue("TblPerfExam",map));
                    if(state.equals("PERF_EXAM_STATE_4")) {
                        GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", type)).queryOne();
                        map2.put("examId", examId);
                        if (UtilValidate.isNotEmpty(gv)) {
                            delegator.store(delegator.makeValidValue("TblPerfExamResult", map2));
                        } else {
                            delegator.create(delegator.makeValidValue("TblPerfExamResult", map2));
                        }
                        savePerfExamScore(scoreList,examId,type);
                        msg = "终审通过!";
                    }else{
                        GenericValue gv = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", type)).queryOne();
                        Map map3 = new HashMap();
                        java.util.Date date1 = new java.util.Date();
                        map3.put("date",new java.sql.Date(date1.getTime()));
                        map3.put("resultType",type);
                        map3.put("examId", examId);
                        map3.put("remark", context.get("remark"));
                        if (UtilValidate.isNotEmpty(gv)) {
                            delegator.store(delegator.makeValidValue("TblPerfExamResult", map3));
                        } else {
                            delegator.create(delegator.makeValidValue("TblPerfExamResult", map3));
                        }
                        msg = "终审驳回!";
                    }
                }else if(type.equals("PERF_EXAM_TYPE_4")){
                    Map map = new HashMap();
                    map.put("examId", examId);
                    map.put("state", "PERF_EXAM_STATE_6");
                    delegator.store(delegator.makeValidValue("TblPerfExam",map));
                    Map map3 = new HashMap();
                    java.util.Date date1 = new java.util.Date();
                    map3.put("resultDate",new java.sql.Date(date1.getTime()));
                    map3.put("resultType",type);
                    map3.put("examId", examId);
                    delegator.create(delegator.makeValidValue("TblPerfExamResult", map3));
                    msg = "归档成功!";
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
/**
 * 保存绩效考核项目评分
 * @param scoreList
 * @param examId
 * @param type
 */
public void savePerfExamScore(List scoreList,String examId,String type){
    if(scoreList.size()!=0) {
        Map scoreMap = scoreList[0];
        Iterator<Map.Entry<String, String>> it = scoreMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String itemId = entry.getKey();
            String score = entry.getValue();
            GenericValue itemScore = delegator.makeValue("TblPerfExamScore", "examId", examId, "itemId", itemId, "scoreType", type, "score", new BigDecimal(score));
            itemScore.create();
        }
    }
}
/**
 * 删除相应的绩效考核
 * @return
 */
public Map<String,Object> deletePerfExam(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String examId = context.get("examId");
    String type = context.get("type");
    if(UtilValidate.isNotEmpty(examId)&&UtilValidate.isNotEmpty(type)){
        delegator.removeByAnd("TblPerfExamScore",UtilMisc.toMap("examId",examId,"scoreType",type));
        delegator.removeByAnd("TblPerfExamResult",UtilMisc.toMap("examId",examId,"resultType",type));
        if(type.equals("PERF_EXAM_TYPE_1")){
            delegator.removeByAnd("TblPerfExamAdvice",UtilMisc.toMap("examId",examId));
            Map map = new HashMap();
            map.put("examId", examId);
            map.put("state", null);
            delegator.store(delegator.makeValidValue("TblPerfExam",map));
        }
        if(type.equals("PERF_EXAM_TYPE_2")){
            Map map = new HashMap();
            map.put("examId", examId);
            map.put("state", "PERF_EXAM_STATE_1");
            delegator.store(delegator.makeValidValue("TblPerfExam",map));
        }
        if(type.equals("PERF_EXAM_TYPE_3")){
            Map map = new HashMap();
            map.put("examId", examId);
            map.put("state", "PERF_EXAM_STATE_2");
            delegator.store(delegator.makeValidValue("TblPerfExam",map));
        }
    }
    successResult.put("msg","删除成功！");
    return successResult;
}
/**
 * 根据年份改变月份
 * @return
 */
public Map<String,Object> changeEvaluateMonth(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    months = [];
    String y =context.get("year");
    int year = Integer.parseInt(y);
    int month = 0 ;
    int yearNow = Calendar.getInstance().get(Calendar.YEAR);
    int monthNow = Calendar.getInstance().get(Calendar.MONTH);
    int selectedYear=0;
    int selectedMonth=0;
    perfExamPersons = EntityQuery.use(delegator).select().from("TblPerfExamPerson").where(UtilMisc.toMap("planState","1")).orderBy("startDate ASC").queryList();
    if(perfExamPersons.size()!=0){
        GenericValue map =perfExamPersons[0];
        java.sql.Date startDate = map.get("startDate");
        java.util.Date date = new java.util.Date(startDate.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        selectedYear = cal.get(Calendar.YEAR);
        selectedMonth = cal.get(Calendar.MONTH);
    }
    if(year==yearNow){
        while (month<monthNow){
            month = month+1;
            months.push([value: month, label: month + '月']);
        }
    }else if(year==selectedYear) {
        month = selectedMonth;
        while (month<12){
            month = month+1;
            months.push([value: month, label: month + '月']);
        }
    }else{
            while (month<12){
                month = month+1;
                months.push([value: month, label: month + '月']);
            }
    }
    Map map = new HashMap();
    map.put("list",months);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> showPerfExam(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String examId = context.get("examId");
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(examId)){
        GenericValue genericValue = EntityQuery.use(delegator).select().from("TblPerfExam").where(UtilMisc.toMap("examId", examId)).queryOne();
        map.putAll(genericValue);
        String planId = genericValue.get("planId");
        GenericValue gv = EntityQuery.use(delegator).select().from("PerfExamPersonDetail").where(UtilMisc.toMap("planId", planId)).queryOne();
        String perfExamType = gv.get("perfExamType");
        map.putAll(gv);
        DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        dynamicViewEntity.addMemberEntity("T", "TblPerfExamItem");
        dynamicViewEntity.addAlias("T", "count", "itemId", null, null, false, "count");
        dynamicViewEntity.addAlias("T", "typeId", "typeId", null, null, true, null);
        typeCounts = from(dynamicViewEntity).queryList();
        typeCountMap = [:];
        if(typeCounts){
            for (GenericValue count : typeCounts) {
                typeCountMap.put(count.get("typeId"), count.get("count"));
            }
        }
        map.put("evaluator",gv.get("evaluatorName"));
        map.put("reviewer",gv.get("reviewerName"));
        map.put("approver",gv.get("approverName"));
        map.put("finalizer",gv.get("finalizerName"));
        map.put("typeCountMap",typeCountMap);
        map.put("fullName",gv.get("fullName"));
        map.put("workerSn",gv.get("workerSn"));
        map.put("departmentName",gv.get("departmentName"));
        map.put("occupationName",gv.get("occupationName"));
        map.put("perfExamType",gv.get("description"));
        Map evaluaterMap = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", "PERF_EXAM_TYPE_1")).queryOne();
        if(UtilValidate.isNotEmpty(evaluaterMap)){
            map.put("evaluaterAddScore",evaluaterMap.get("addScore"));
            map.put("evaluaterPlanScore",evaluaterMap.get("planScore"));
            map.put("evaluaterTotalScore",evaluaterMap.get("totalScore"));
            map.put("evaluaterResult",evaluaterMap.get("result"));
            map.put("evaluaterDate",evaluaterMap.get("resultDate"));
        }
        Map reviewerMap = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", "PERF_EXAM_TYPE_2")).queryOne();
        if(UtilValidate.isNotEmpty(reviewerMap)){
            map.put("reviewerAddScore",reviewerMap.get("addScore"));
            map.put("reviewerPlanScore",reviewerMap.get("planScore"));
            map.put("reviewerTotalScore",reviewerMap.get("totalScore"));
            map.put("reviewerResult",reviewerMap.get("result"));
            map.put("reviewerRemark",reviewerMap.get("remark"));
            map.put("reviewerDate",reviewerMap.get("resultDate"));
        }
        Map approverMap = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", "PERF_EXAM_TYPE_3")).queryOne();
        if(UtilValidate.isNotEmpty(approverMap)){
            map.put("approverAddScore",approverMap.get("addScore"));
            map.put("approverPlanScore",approverMap.get("planScore"));
            map.put("approverTotalScore",approverMap.get("totalScore"));
            map.put("approverResult",approverMap.get("result"));
            map.put("approverRemark",approverMap.get("remark"));
            map.put("approverDate",approverMap.get("resultDate"));
        }
        Map finalizerMap = EntityQuery.use(delegator).select().from("TblPerfExamResult").where(UtilMisc.toMap("examId", examId, "resultType", "PERF_EXAM_TYPE_4")).queryOne();
        if(UtilValidate.isNotEmpty(finalizerMap)){
            map.put("finalizerDate",finalizerMap.get("resultDate"));
        }
        GenericValue gv2 = EntityQuery.use(delegator).select().from("TblPerfExamAdvice").where(UtilMisc.toMap("examId", examId)).queryOne();
        if(UtilValidate.isNotEmpty(gv2)){
            map.put("previousAdvice",gv2.get("previousAdvice"));
            map.put("nextAdvice",gv2.get("nextAdvice"));
        }
        GenericHelperInfo helperInfo = delegator.getGroupHelperInfo("org.ofbiz");
        Datasource datasourceInfo = EntityConfig.getDatasource(helperInfo.getHelperBaseName());
        // 获得数据库的连接
        Connection connection = ConnectionFactoryLoader.getInstance().getConnection(helperInfo, datasourceInfo.getInlineJdbc());
        String sql = "SELECT e.*, f.approverScore FROM ( SELECT c.*, d.reviewerScore FROM( SELECT a.*, b.evaluateScore " +
                " FROM(SELECT ITEM_ID,TYPE_ID,PARENT_TYPE,TITLE,SCORE,SCORE1,SCORE2,SCORE3,SCORE4 FROM TBL_PERF_EXAM_ITEM " +
                " WHERE ((PARENT_TYPE = ? OR TYPE_ID = ? )) ORDER BY PARENT_TYPE ASC,TYPE_ID ASC) a " +
                " LEFT JOIN (SELECT ITEM_ID,SCORE AS evaluateScore FROM tbl_perf_exam_score s WHERE EXAM_ID = ? " +
                " AND SCORE_TYPE = ? ) b ON a.ITEM_ID = b.ITEM_ID) c LEFT JOIN (SELECT ITEM_ID,SCORE AS reviewerScore " +
                " FROM tbl_perf_exam_score WHERE EXAM_ID = ? AND SCORE_TYPE = ? ) d ON c.ITEM_ID = d.ITEM_ID) e " +
                " LEFT JOIN (SELECT ITEM_ID,SCORE AS approverScore FROM tbl_perf_exam_score WHERE EXAM_ID = ? " +
                "AND SCORE_TYPE = ? ) f ON e.ITEM_ID = f.ITEM_ID";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,perfExamType);
        statement.setString(2,perfExamType);
        statement.setString(3,examId);
        statement.setString(4,"PERF_EXAM_TYPE_1");
        statement.setString(5,examId);
        statement.setString(6,"PERF_EXAM_TYPE_2");
        statement.setString(7,examId);
        statement.setString(8,"PERF_EXAM_TYPE_3");
        ResultSet rs = statement.executeQuery();
        List list =new ArrayList();
        int typeTotalScore =25;
        while(rs.next()){
            Map map1 = new HashMap();
            map1.put("itemId",rs.getString("ITEM_ID"));
            map1.put("title",rs.getString("TITLE"));
            map1.put("typeId",rs.getString("TYPE_ID"));
            String score = rs.getString("SCORE");
            int s = Integer.parseInt(score);
            typeTotalScore=typeTotalScore+s;
            map1.put("score",score);
            map1.put("score1",rs.getString("SCORE1"));
            map1.put("score2",rs.getString("SCORE2"));
            map1.put("score3",rs.getString("SCORE3"));
            map1.put("score4",rs.getString("SCORE4"));
            map1.put("evaluateScore",rs.getString("evaluateScore"));
            map1.put("reviewerScore",rs.getString("reviewerScore"));
            map1.put("approverScore",rs.getString("approverScore"));
            list.add(map1);
        }
        map.put("perfExamItemsList",list);
        map.put("typeTotalScore",typeTotalScore);
    }
    successResult.put("perfExamMap",map);
    return successResult;
}


public Map<String, Object> savePerfExamItem() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String type = context.get("type");
    String itemId = context.get("itemId");
    context.put("typeId",type);
    context.put("state", "1");
    String msg = "";
    try {
        if(UtilValidate.isNotEmpty(itemId)){
            delegator.store(delegator.makeValidValue("TblPerfExamItem",context));
            msg = "更新成功";
        }else{
            String newItemId = delegator.getNextSeqId("TblPerfExamItem").toString();//获取主键ID
            context.put("itemId",newItemId);
            delegator.create(delegator.makeValidValue("TblPerfExamItem",context));
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


public Map<String, Object> deletePerfExamItem() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String itemId = parameters.get("itemId");
    GenericValue genericValue = delegator.findByPrimaryKey("TblPerfExamItem", UtilMisc.toMap("itemId", itemId));
    genericValue.put("state", "0");//0表示已作废，1表示正常状态
    genericValue.store();
    msg = "作废成功！"
    successResult.put("data",msg);
    return successResult;
}
