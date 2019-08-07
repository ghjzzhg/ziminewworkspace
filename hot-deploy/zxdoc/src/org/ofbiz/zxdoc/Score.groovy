import javolution.util.FastList
import org.apache.commons.net.ntp.TimeStamp
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityExpr
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil
import org.ofbiz.zxdoc.ScoreMessage

import java.sql.Timestamp

public Map<String, Object> addScoreRule() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String ruleName = context.get("ruleName");
    String rulePeriod = context.get("rulePeriod");
    int maxTimes = context.get("maxTimes");
    int score = context.get("score");

    String scoreRuleId = delegator.getNextSeqId("TblScoreRule");
    GenericValue scoreRule = delegator.makeValidValue("TblScoreRule",
            UtilMisc.toMap("ruleName", ruleName, "rulePeriod", rulePeriod, "maxTimes", maxTimes, "score", score, "scoreRuleId", scoreRuleId));
    GenericValue createdScoreRule = delegator.create(scoreRule);
    return successResult;
}

public Map<String, Object> searchScoreRules() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    List<GenericValue> listScoreRule = from("ScoreRuleView").queryList();
    Map map = new HashMap();
    map.put("map", listScoreRule)
    successResult.put("data", map);
    return successResult;
}

public Map<String, Object> editScoreRule() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String scoreRuleId = context.get("scoreRuleId");
    Map<String, Object> listRule = new HashMap();
    if (scoreRuleId != null) {
        listRule = delegator.findByAnd("TblScoreRule", UtilMisc.toMap("scoreRuleId", scoreRuleId), null, false).get(0);
    }
    List<GenericValue> listPeriod = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "SCORE_PERIOD"), null, false);
    List<GenericValue> list1 = new ArrayList<>();
    for (GenericValue a : listPeriod) {
        Map<String, Object> listEnum = new HashMap<>();
        String enumId = a.get("enumId");
        String description = a.get("description");
        listEnum.put("enumId", enumId);
        listEnum.put("description", description);
        list1.add(listEnum);
    }
    Map map = new HashMap();
    map.put("map", listRule);
    map.put("list", list1);
    map.put("rules", delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "SCORE_RULE"), null, false));
    successResult.put("data", map);
    return successResult;
}

public Map<String, Object> saveRule() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String scoreRuleId = context.get("scoreRuleId");
    String ruleName = context.get("ruleName");
    String rulePeriod = context.get("rulePeriod");
    Integer maxTimes = null;
    Integer score = null;
    if (UtilValidate.isNotEmpty(context.get("maxTimes"))) {
        maxTimes = Integer.parseInt(context.get("maxTimes").toString());
    }
    if (UtilValidate.isNotEmpty(context.get("score"))) {
        score = Integer.parseInt(context.get("score").toString());
    }
    GenericValue listRule = delegator.findByPrimaryKey("TblScoreRule", UtilMisc.toMap("scoreRuleId", scoreRuleId));
    listRule.put("ruleName", ruleName);
    listRule.put("rulePeriod", rulePeriod);
    listRule.put("maxTimes", maxTimes);
    listRule.put("score", score);
    listRule.store();
    return successResult;
}

public Map<String, Object> removeScoreRule() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String scoreRuleId = context.get("scoreRuleId");
    delegator.removeByAnd("TblScoreRule", UtilMisc.toMap("scoreRuleId", scoreRuleId));
    return successResult;
}

public Map<String, Object> userScores() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String userName = context.get("userName");
    String account = context.get("account");
    String phoneNumber = context.get("phoneNumber");
//    List<GenericValue> userList = from("TblUserScore").queryList();
//    List<GenericValue> list = new ArrayList<>();
//    for (GenericValue s : userList) {
//        Map map = new HashMap();
//        String userLoginId = s.get("userLoginId");
//        //判断积分用户是机构组织，还是用户。
//        String partyType =delegator.findByAnd("Party",UtilMisc.toMap("partyId",userLoginId),null,false).get(0).get("partyTypeId");
//        if( partyType == "PARTY_GROUP"){
//            String userName=delegator.findByAnd("PartyGroup",UtilMisc.toMap("partyId",userLoginId),null,false).get(0).get("groupName");
//            map.put("userName",userName);
//        }else{
//        String userName = delegator.findByAnd("Person", UtilMisc.toMap("partyId", userLoginId), null, false).get(0).get("fullName");
//        map.put("userName", userName);
//        }
//        String scoreOn = s.get("scoreOn");
//        String scoreOff = s.get("scoreOff");
//        String scoreNow = s.get("scoreNow");
//        map.put("userLoginId",userLoginId);
//        map.put("scoreOn", scoreOn);
//        map.put("scoreOff", scoreOff);
//        map.put("scoreNow", scoreNow);
//        list.add(map);
//    }
    List<GenericValue> list = new ArrayList<>();
    List<EntityCondition> condList = FastList.newInstance();
    if (userName != null) {
        condList.add(EntityCondition.makeCondition("userName", EntityOperator.EQUALS, userName));
    }
    if (account != null) {
        condList.add(EntityCondition.makeCondition("account", EntityOperator.EQUALS, account));
    }
    if (phoneNumber != null) {
        condList.add(EntityCondition.makeCondition("phoneNumber", EntityOperator.EQUALS, phoneNumber));
    }
    list = delegator.findList("UserScoreParty", EntityCondition.makeCondition(condList), null, null, null, false);
    successResult.put("data", list);
    return successResult;
}

public Map<String, Object> editUserScore() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String userLoginId = context.get("userLoginId");
    Map<String, Object> list = delegator.findByAnd("TblUserScore", UtilMisc.toMap("userLoginId", userLoginId), null, false).get(0);
    //修复点开编辑报错的bug：即编辑主账户的信息，找不到fullName。这边的userloginid实际是partyid 161226
    //1.先查询该账户是什么类型
    GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", userLoginId).queryOne();
    String partyTypeId = party.get("partyTypeId");
    String userName = "";
    //2.如果是主账户类型，获取联系人
    if (partyTypeId.equals("PARTY_GROUP")) {
        GenericValue contact = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where("partyId", userLoginId).queryOne();
        userName = contact.get("fullName");
    }
    //3.子账户直接获得联系人
    else {
        GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId",userLoginId).queryOne();
        userName = person.get("fullName");
    }
    Map map = new HashMap();
    map.put("userName", userName);
    map.put("list", list);
    successResult.put("data", map);
    return successResult;
}

public Map<String, Object> saveUserScore() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String userLoginId = context.get("userLoginId");
    Integer scoreOn = context.get("scoreOn");
    Integer scoreOff = context.get("scoreOff");
    Integer scoreNow = context.get("scoreNow");
    GenericValue userScore = delegator.findByPrimaryKey("TblUserScore", UtilMisc.toMap("userLoginId", userLoginId));
    userScore.put("userLoginId", userLoginId);
    userScore.put("scoreOn", scoreOn==null?0:scoreOn);
    userScore.put("scoreOff", scoreOff==null?0:scoreOff);
    userScore.put("scoreNow", scoreNow);
    userScore.put("scoreAvailable", scoreNow);
    userScore.store();
    return successResult;
}
//积分日志
public Map<String, Object> scoreHistory() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String userName = context.get("userName");
    String account = context.get("account");
    Date startTime = context.get("startTime");
    Date endTime = context.get("endTime");
    String scoreRule = context.get("scoreRule");
//    List<GenericValue> Historylist =from("TblScoreHistory").queryList();
//    List<GenericValue> list =new ArrayList<>();
//    for(GenericValue s:Historylist){
//        Map map =new HashMap();
//        String userLoginId =s.get("userLoginId");
//        //判断积分用户是机构组织，还是用户。
//        String partyType =delegator.findByAnd("Party",UtilMisc.toMap("partyId",userLoginId),null,false).get(0).get("partyTypeId");
//        if(partyType=="PARTY_GROUP"){
//            String userName= delegator.findByAnd("PartyGroup",UtilMisc.toMap("partyId",userLoginId),null,false).get(0).get("groupName");
//            map.put("userName",userName);
//        }else{
//            String fullName= delegator.findByAnd("Person",UtilMisc.toMap("partyId",userLoginId),null,false).get(0).get("fullName");
//            map.put("userName",fullName);
//        }
//        String scoreChange =s.get("scoreChange");
//        String rule =s.get("rule");
//        String createdStamp =s.get("createdStamp");
//        map.put("userLoginId",userLoginId);
//        map.put("scoreChange",scoreChange);
//        map.put("rule",rule);
//        map.put("createdStamp",createdStamp);
//        list.add(map);
//    }
    List<GenericValue> list = new ArrayList<>();
    List<EntityCondition> condList = FastList.newInstance();
    if (userName != null) {
        condList.add(EntityCondition.makeCondition("userName", EntityOperator.EQUALS, userName));
    }
    if (account != null) {
        condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, account))
    }
    if (startTime != null) {
    }
    if (endTime != null) {
    }
    if (scoreRule != null) {
        condList.add(EntityCondition.makeCondition("rule", EntityOperator.EQUALS, scoreRule));
    }
    list = delegator.findList("ScoreHistoryParty", EntityCondition.makeCondition(condList), null, null, null, false);
//加载积分的初始值
    List<GenericValue> listRule = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "SCORE_RULE"), null, false);
    Map map = new HashMap();
    map.put("listRule", listRule);
    map.put("list", list)
    successResult.put("data", map);
    return successResult;
}

public Map<ServiceUtil, Object> userScoreHistory() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("userLoginId");
    if(partyId==null)
    {
        partyId = context.get("userLogin").get("partyId");
    }
    /*List<GenericValue> list = delegator.findByAnd("ScoreHistoryParty", UtilMisc.toMap("userLoginId", userLoginId), null, false);
    successResult.put("data", list);*/
    successResult.put("userLoginId",partyId);
    return successResult;
}

enum scoreStatus {
    SCORE_RULE_1,
    SCORE_RULE_2,
    SCORE_RULE_3,
    SCORE_RULE_REWARD,
    SCORE_RULE_ADDANDSUBTRACT //添加扣除
}

enum scorePeriod {
    SCORE_PERIOD_FIVE,//不限
    SCORE_PERIOD_ONE,//一次
    SCORE_PERIOD_TWO//每天
}

public Map<String, Object> processScoreMsg() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    ScoreMessage scoreMessage = context.get("message");
//    GenericValue person = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId",scoreMessage.getScoreTarget())).queryOne();
//    if(person.get("partyTypeId").equals("PERSON")){
    GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", scoreMessage.getScoreTarget())).queryOne();
    if (userScore == null) {
        userScore = delegator.makeValidValue("TblUserScore", userScore);
        userScore.put("userLoginId", scoreMessage.getScoreTarget());
        userScore.put("scoreNow", 0);
        userScore.put("scoreAvailable", 0);
        userScore.create();
    }
//        if(!"SCORE_RULE_REWARD".equals(scoreMessage.getEventName())){
    switch (scoreStatus.valueOf(scoreMessage.getEventName())) {
    //登录积分
        case scoreStatus.SCORE_RULE_1: uploadScore(scoreMessage, userScore); break;
    //咨询积分
        case scoreStatus.SCORE_RULE_2: uploadScore(scoreMessage, userScore); break;
    //注册后赠送积分
        case scoreStatus.SCORE_RULE_REWARD: uploadScore(scoreMessage, userScore); break;
    //上传积分
        case scoreStatus.SCORE_RULE_3: uploadScore(scoreMessage, userScore); break;
    //添加扣除
        case scoreStatus.SCORE_RULE_ADDANDSUBTRACT: addAndSubtractScore(scoreMessage, userScore); break;
        default: break;
    }
//        }else if("SCORE_RULE_REWARD".equals(scoreMessage.getEventName())){ //奖励积分，可没有记录
//            Integer scoreNow = 0;
//            if(userScore != null && UtilValidate.isNotEmpty(userScore.get("scoreNow"))){
//                scoreNow = Integer.parseInt(userScore.get("scoreNow").toString());
//            }
//            scoreNow = scoreNow + scoreMessage.getBizScoreOnTarget();
//            saveScore(scoreMessage.getScoreTarget(), scoreNow, scoreMessage.getBizScoreOnTarget(), null);
//        }
//    }
    return successResult;
}

/**
 * 登录积分
 * @param scoreMessage
 */
private void uploadScore(ScoreMessage scoreMessage, GenericValue userScore) {
    List<GenericValue> scoreRuleList = EntityQuery.use(delegator).from("TblScoreRule").where(UtilMisc.toMap("ruleName", scoreMessage.getEventName())).orderBy("lastUpdatedStamp DESC").queryList();
    Integer userNowScore = Integer.parseInt(userScore.get("scoreNow").toString());
    String partyId = scoreMessage.getScoreTarget();
    if (UtilValidate.isNotEmpty(scoreRuleList)) {
        GenericValue scoreRule = scoreRuleList.get(0);
        String rulePeriodId = scoreRule.get("rulePeriod");
        List conditionList = new ArrayList();
        //每天
        if (rulePeriodId.equals("SCORE_PERIOD_TWO")) {
            conditionList.add(EntityCondition.makeCondition("createTime", EntityOperator.GREATER_THAN_EQUAL_TO, getStartTime()));
            conditionList.add(EntityCondition.makeCondition("createTime", EntityOperator.LESS_THAN_EQUAL_TO, getEndTime()));
        }
        conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("rule", EntityOperator.EQUALS, scoreMessage.getEventName()));
        List<GenericValue> userScoreValue = EntityQuery.use(delegator).from("TblScoreHistory").where(conditionList).queryList();
        Integer userScoreSize = 0;
        if (UtilValidate.isNotEmpty(userScoreValue)) {
            userScoreSize = userScoreValue.size();
        }
        Boolean flag = true;
        Integer maxTimes = Integer.parseInt(scoreRule.get("maxTimes").toString())
        if (searchHistoryByRule(maxTimes, rulePeriodId, userScoreSize)) {
            Integer scoreOn;
            Integer scoreOff;
            Integer score = Integer.parseInt(scoreRule.get("score").toString());
            userNowScore = userNowScore + score;
            //积分超过上限不允许修改
            if (UtilValidate.isNotEmpty(userScore.get("scoreOn"))) {
                scoreOn = userScore.get("scoreOn").toString();
                flag = userNowScore > scoreOn;
            }
            //积分低于下限不允许修改
            if (UtilValidate.isNotEmpty(userScore.get("scoreOff"))) {
                scoreOff = userScore.get("scoreOff").toString();
                flag = userNowScore < scoreOff;
            }
            //修改积分
            if (flag) {
                saveScore(partyId, userNowScore, score, scoreMessage.getEventName());
            }
        }
    }
}

private boolean searchHistoryByRule(Integer maxTimes, String rulePeriodId, Integer userScoreSize) {
    switch (scorePeriod.valueOf(rulePeriodId)) {
    //不限
        case scorePeriod.SCORE_PERIOD_FIVE: return true; break;
    //一次
        case scorePeriod.SCORE_PERIOD_ONE: return userScoreSize < 1; break;
    //每天
        case scorePeriod.SCORE_PERIOD_TWO: return userScoreSize < maxTimes; break;
        default: break;
    }
}

/**
 * 修改积分
 * @param partyId 被修改的用户名
 * @param userNowScore 用户当前积分
 * @param score 增加或者减少积分
 * @param rule 类型
 */
public void saveScore(String partyId, Integer userNowScore, Integer score, String rule) {
//修改当前积分
    Map<String, Object> scoreMap = new HashMap<>();
    scoreMap.put("userLoginId", partyId);
    scoreMap.put("scoreNow", userNowScore);
    GenericValue userScoreGeneric = delegator.makeValidValue("TblUserScore", scoreMap);
    GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", partyId)).queryOne();
    if (userScore != null) {
        if(score > 0){//changed by galaxypan@2017-09-28:可用积分减少的操作在其他地方执行
            userScoreGeneric.set("scoreAvailable", userScore.getInteger("scoreAvailable") + score);//增加可用积分
        }
        userScoreGeneric.store();
    } else {
        userScoreGeneric.set("scoreAvailable", userNowScore);
        userScoreGeneric.create();
    }
    //将积分信息存到历史表中
    Map<String, Object> scoreHistoryMap = new HashMap<>();
    String scoreHistoryId = delegator.getNextSeqId("TblScoreHistory");
    scoreHistoryMap.put("historyId", scoreHistoryId);
    scoreHistoryMap.put("userLoginId", partyId);
    scoreHistoryMap.put("scoreChange", score);
    scoreHistoryMap.put("createTime", new Timestamp((new Date()).getTime()));
    scoreHistoryMap.put("rule", rule);
    GenericValue scoreHistoryGeneric = delegator.makeValidValue("TblScoreHistory", scoreHistoryMap);
    scoreHistoryGeneric.create();
}

/**
 * 初始化登录账户的积分记录
 */
public Map<String, Object> initUserScore() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", context.get("partyId"))).queryOne();
    if (userScore == null) {
        Map<String, Object> scoreMap = new HashMap<>();
        scoreMap.put("userLoginId", context.get("partyId"));
        scoreMap.put("scoreNow", 0);
        scoreMap.put("scoreAvailable", 0);
        GenericValue userScoreGeneric = delegator.makeValidValue("TblUserScore", scoreMap);
        userScoreGeneric.create();
    }
    return success;
}


/**
 * 减少用户可用积分
 * @param partyId 被修改的用户名
 * @param score 减少积分
 */
public Map<String, Object> deductUserAvailableScore() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    Integer score = context.get("score");
    GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", partyId)).queryOne();
    if (userScore != null) {
        int result = userScore.getInteger("scoreAvailable") - Math.abs(score)
        if(result < 0){
            throw new RuntimeException("用户可用积分不足!");
        }
        userScore.set("scoreAvailable", result);//减少可用积分
        userScore.store();
    }else{
        Debug.logError("用户" + partyId + " 无积分记录");
    }
    return success;
}

/**
 * 赠送积分
 * @param partyIdFrom 赠送人
 * @param partyIdTo 接收人
 * @param score 分值
 */
public Map<String, Object> deliverScore() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String scoreSource = context.get("userLogin").get("partyId");
    String scoreTarget = context.get("partyIdTo");
    Integer score = context.get("score");
    if(score < 0){
        throw new RuntimeException("赠送积分不允许负值");
    }
    //changed by galaxypan@2017-09-28:先扣除可用积分，不足时会抛异常阻止赠送积分
    Map<String, Object> result = dispatcher.runSync("deductUserAvailableScore", UtilMisc.toMap("partyId", scoreSource, "score", score, "userLogin", context.get("userLogin")));
    dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreSource",scoreSource,"scoreTarget",scoreTarget, "bizScoreOnSource", 0 - score, "bizScoreOnTarget", score, "eventName", "SCORE_RULE_ADDANDSUBTRACT"));

    return success;
}

/**
 * 每天的开始时间
 * @return
 */
private static Timestamp getStartTime() {
    Calendar todayStart = Calendar.getInstance();
    todayStart.set(Calendar.HOUR, 0);
    todayStart.set(Calendar.MINUTE, 0);
    todayStart.set(Calendar.SECOND, 0);
    todayStart.set(Calendar.MILLISECOND, 0);
    return new Timestamp(todayStart.getTime().getTime());
}

/**
 * 每天的结束时间
 * @return
 */
private static Timestamp getEndTime() {
    Calendar todayEnd = Calendar.getInstance();
    todayEnd.set(Calendar.HOUR, 23);
    todayEnd.set(Calendar.MINUTE, 59);
    todayEnd.set(Calendar.SECOND, 59);
    todayEnd.set(Calendar.MILLISECOND, 999);
    return new Timestamp(todayEnd.getTime().getTime());
}

/**
 *
 * @param scoreMessage
 * @param userScore
 */
private void addAndSubtractScore(ScoreMessage scoreMessage, GenericValue userScore) {
    if (UtilValidate.isNotEmpty(scoreMessage.getScoreSource())) {
        GenericValue sourceScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", scoreMessage.getScoreSource())).queryOne();
        GenericValue targetScore = userScore
        Integer sourceOldScore = sourceScore.get("scoreNow");
        Integer targetOldScore = targetScore.get("scoreNow");
        Integer sourceNowScore = sourceOldScore + scoreMessage.getBizScoreOnSource();
        if(sourceNowScore < 0){//积分不够减
            throw new RuntimeException("积分不足!");
        }
        Integer targetNowScore = targetOldScore + scoreMessage.getBizScoreOnTarget();
        saveScore(scoreMessage.getScoreSource(), sourceNowScore, scoreMessage.getBizScoreOnSource(), scoreMessage.getEventName());
        saveScore(scoreMessage.getScoreTarget(), targetNowScore, scoreMessage.getBizScoreOnTarget(), scoreMessage.getEventName());
    }
}

public Map<String, Object> sendScoreMessage() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    ScoreMessage scoreMessage = new ScoreMessage();
    scoreMessage.setEventName(context.get("eventName"));
    scoreMessage.setScoreSource(context.get("scoreSource"));
    scoreMessage.setScoreTarget(context.get("scoreTarget"));
    Integer sourceScore = context.get("bizScoreOnSource")
    if (sourceScore != null) {
        scoreMessage.setBizScoreOnSource(sourceScore);
    }
    Integer targetScore = context.get("bizScoreOnTarget")
    if (targetScore != null) {
        scoreMessage.setBizScoreOnTarget(targetScore);
    }
    successResult = dispatcher.runSync("sendScoreMessage", UtilMisc.toMap("message", scoreMessage, "userLogin", from("UserLogin").where("userLoginId", "system").queryOne()));
    return successResult;
}

public Map<String, Object> sendScoreMessageService() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    successResult = dispatcher.runSync("processScoreMsg", UtilMisc.toMap("message", context.get("message"), "userLogin", from("UserLogin").where("userLoginId", "system").queryOne()));
    return successResult;
}