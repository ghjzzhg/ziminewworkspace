import javolution.util.FastSet
import org.apache.commons.collections.CollectionUtils
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.content.data.DataServices
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination
import org.ofbiz.service.ServiceUtil

import java.text.SimpleDateFormat




//提交问题
public Map<String, Object> handelQuestion() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String questionId;
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String userLoginId = userLogin.get("userLoginId");
    String partyId = userLogin.get("partyId");
    String questionOverview = context.get("questionOverview");
    String isSecret = context.get("isSecret");
    String isAnonymous = context.get("isAnonymous");
//    String targetUser = context.get("targetUser");
    String questionDetail = context.get("questionDetail")==null?" ":context.get("questionDetail");
//    Map<String, String> files = context.get("fileFieldName");
    String questionType1 = context.get("questionType1");
    String questionType2 = context.get("questionType2");
    String isMessage = context.get("isMessage");
    String targetPerson = context.get("targetPerson");
    Integer integral = context.get("integral");
    String files = context.get("fileFieldName");
    questionId = delegator.getNextSeqId("TblQuestion");

    if(integral != null){//尝试先扣除可用积分，不足时阻止悬赏
        dispatcher.runSync("deductUserAvailableScore", UtilMisc.toMap("partyId", partyId, "score", integral, "userLogin", userLogin));
    }

    List<GenericValue> questionTemplates = new ArrayList<>();

    String msg = "提交问题成功！";
    Boolean flag = true;

//   添加questionType
    if (UtilValidate.isNotEmpty(questionType1)) {
        String[] str = questionType1.split("\\, ");//初步分割
        String[] splquestionType;
        //是否多选
        if(str.size()>1) {
            questionType1 = questionType1.substring(1, questionType1.length() - 1);//去掉首尾字符
            splquestionType = questionType1.split("\\, ");
        }
        else {
            splquestionType = questionType1.split("\\, ");
        }
        Integer i;
        String[] isStandard = new String[splquestionType.length];
        try {
            for (i = 0; i < splquestionType.length; i++) {
                Set<String> fieldsToSelect = FastSet.newInstance();
                fieldsToSelect.add("enumId");
                //比较
                EntityCondition mainCond1 = EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, splquestionType[i]);
                //获得List
                List<GenericValue> questionByCond = delegator.findList("Enumeration",mainCond1, fieldsToSelect, null, null, false);
                //判断是否在enumeration表中
                if (questionByCond.size() == 0) {
                    isStandard[i] = new String("N");

                } else {
                    isStandard[i] = new String("Y");
                }
                String id = delegator.getNextSeqId("TblQuestionType");
                Map<String, Object> questionTypeMap = UtilMisc.toMap("id", id, "questionId", questionId, "questionType", splquestionType[i], "isStandard", isStandard[i]);
                GenericValue questionT = delegator.makeValidValue("TblQuestionType", questionTypeMap);
                GenericValue handelQuestionType = delegator.create(questionT);
            }
            if(questionType2!=null) {
                String id = delegator.getNextSeqId("TblQuestionType");
                Map<String, Object> questionTypeMap1 = UtilMisc.toMap("id", id, "questionId", questionId, "questionType", questionType2, "isStandard", "N");
                GenericValue questionT1 = delegator.makeValidValue("TblQuestionType", questionTypeMap1);
                GenericValue handelQuestionType1 = delegator.create(questionT1);
            }
            //    往数据库中增加问题
            String statusId = "QUESTION_STATUS_WAIT_ANSWER";
            Map<String, Object> questionMap = UtilMisc.toMap("questionId", questionId, "questionOverview", questionOverview, "isSecret", isSecret, "isAnonymous", isAnonymous,
                    "questionDetail", questionDetail, "isMessage", isMessage, "userLoginId", userLoginId,
                    "integral", integral, "statusId", statusId,"targetUser",targetPerson);
            GenericValue question = delegator.makeValidValue("TblQuestion", questionMap);
            GenericValue handelQuestion = delegator.create(question);

        }
        catch (GenericEntityException ex) {
            flag = false;
            msg = "提交问题失败！";
        }
    }
    else{
        String id = delegator.getNextSeqId("TblQuestionType");
        Map<String, Object> questionTypeM= UtilMisc.toMap("id", id, "questionId", questionId, "questionType", questionType2, "isStandard","N");
        GenericValue questionT = delegator.makeValidValue("TblQuestionType", questionTypeM);
        GenericValue handelQuestionType = delegator.create(questionT);
        String statusId = "QUESTION_STATUS_WAIT_ANSWER";
        Map<String, Object> questionMap = UtilMisc.toMap("questionId", questionId, "questionOverview", questionOverview, "isSecret", isSecret, "isAnonymous", isAnonymous,
                "questionDetail", questionDetail, "isMessage", isMessage, "userLoginId", userLoginId,
                "integral", integral, "statusId", statusId,"targetUser",targetPerson );
        GenericValue question = delegator.makeValidValue("TblQuestion", questionMap);
        GenericValue handelQuestion = delegator.create(question);
    }
//添加附件
    if (UtilValidate.isNotEmpty(files)) {
        String[] dataResouceIds = files.split(",");
        for (String dataResourceId : dataResouceIds) {
            String id = delegator.getNextSeqId("TblQuestionAttachment");
            GenericValue questionFile = delegator.makeValue("TblQuestionAttachment");
            questionFile.setString("questionId", questionId);
            questionFile.setString("id", id);
            questionFile.setString("dataResourceId", dataResourceId);
            questionTemplates.add(questionFile);
        }
        delegator.storeAll(questionTemplates);
    }


    if(UtilValidate.isNotEmpty(targetPerson)){
        String author = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne().getString("fullName");
        String msgTitle = author + "邀请您回答提问";
        String titleClickAction = "displayInside('/zxdoc/control/AnswerQuestion?questionId=" + questionId + "')";
        runService("createSiteMsg", UtilMisc.toMap("partyId", targetPerson, "title", msgTitle, "titleClickAction", titleClickAction, "type", "notice"));
    }
    successResult.put("msg", msg);
    return successResult;
}



//保存问题
public Map<String, Object> saveQuestion() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String questionId=context.get("questionId");
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String userLoginId = userLogin.get("userLoginId");
    String partyId = userLogin.get("partyId");
    String questionOverview = context.get("questionOverview");
    String isSecret = context.get("isSecret");
    String isAnonymous = context.get("isAnonymous");
//    String targetUser = context.get("targetUser");
    String questionDetail = context.get("questionDetail");
    String questionType1 = context.get("questionType1");//复选框问题类型
    String questionType2 = context.get("questionType2");//自定义问题类型
    String files = context.get("fileFieldName");
    String isMessage = context.get("isMessage");
//    Integer integral = context.get("integral");//changed by galaxypan@2017-09-28:修改问题不允许修改悬赏积分
    String targetPerson = context.get("targetPerson");
    String msg = "保存问题成功！";

    //去掉之前的问题类型
    delegator.removeByAnd("TblQuestionType", UtilMisc.toMap("questionId", questionId));
    //添加新的问题类型
    if (questionType1 != null) {
        String[] str = questionType1.split("\\, ");//初步分割
        String[] splquestionType;
        //是否多选
        if(str.size()>1) {
            questionType1 = questionType1.substring(1, questionType1.length() - 1);//去掉首尾字符
            splquestionType = questionType1.split("\\, ");
        }
        else {
            splquestionType = questionType1.split("\\, ");
        }
        Integer i;
        String[] isStandard = new String[splquestionType.length];
        try {
            for (i = 0; i < splquestionType.length; i++) {
                Set<String> fieldsToSelect = FastSet.newInstance();
                fieldsToSelect.add("enumId");
                //比较
                EntityCondition mainCond1 = EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, splquestionType[i]);
                //获得List
                List<GenericValue> questionByCond = delegator.findList("Enumeration",mainCond1, fieldsToSelect, null, null, false);
                //判断是否在enumeration中
                if (questionByCond.size() == 0) {
                    isStandard[i] = new String("N");

                } else {
                    isStandard[i] = new String("Y");
                }
                String id = delegator.getNextSeqId("TblQuestionType");
                Map<String, Object> questionTypeMap = UtilMisc.toMap("id", id, "questionId", questionId, "questionType", splquestionType[i], "isStandard", isStandard[i]);
                GenericValue questionT = delegator.makeValidValue("TblQuestionType", questionTypeMap);
                GenericValue handelQuestionType = delegator.create(questionT);
            }
            if(questionType2!=null) {
                String id = delegator.getNextSeqId("TblQuestionType");
                Map<String, Object> questionTypeMap1 = UtilMisc.toMap("id", id, "questionId", questionId, "questionType", questionType2, "isStandard", "N");
                GenericValue questionT1 = delegator.makeValidValue("TblQuestionType", questionTypeMap1);
                GenericValue handelQuestionType1 = delegator.create(questionT1);
            }
        }
        catch (GenericEntityException ex) {
            msg = "保存问题失败！";
        }
    }
    else {
        String id = delegator.getNextSeqId("TblQuestionType");
        Map<String, Object> questionTypeM = UtilMisc.toMap("id", id, "questionId", questionId, "questionType", questionType2, "isStandard", "N");
        GenericValue questionT = delegator.makeValidValue("TblQuestionType", questionTypeM);
        GenericValue handelQuestionType = delegator.create(questionT);
    }
    //    往数据库中修改问题
    //String statusId = "QUESTION_STATUS_WAIT_ANSWER";
    GenericValue oldQuestion = EntityQuery.use(delegator).from("TblQuestion").where("questionId", questionId).queryOne();
    String oldTargetUser = oldQuestion.getString("targetUser");
    if(UtilValidate.isNotEmpty(targetPerson) && !targetPerson.equals(oldTargetUser)){
        String author = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne().getString("fullName");
        String msgTitle = author + "邀请您回答提问";
        String titleClickAction = "displayInside('/zxdoc/control/AnswerQuestion?questionId=" + questionId + "')";
        runService("createSiteMsg", UtilMisc.toMap("partyId", targetPerson, "title", msgTitle, "titleClickAction", titleClickAction, "type", "notice"));
    }
    Map<String, Object> questionMap = UtilMisc.toMap("questionId", questionId, "questionOverview", questionOverview, "isSecret", isSecret, "isAnonymous", isAnonymous,
            "questionDetail", questionDetail, "isMessage", isMessage, "userLoginId", userLoginId, /*"integral", integral, */"targetUser", targetPerson
            );
    GenericValue question = delegator.makeValidValue("TblQuestion", questionMap);
    delegator.store(question);


    delegator.removeByAnd("TblQuestionAttachment", UtilMisc.toMap("questionId", questionId));

    List<GenericValue> questionTemplates = new ArrayList<>();

    if(UtilValidate.isNotEmpty(files)){
        String[] dataResouceIds = files.split(",");
        for (String dataResourceId : dataResouceIds) {
            String id = delegator.getNextSeqId("TblQuestionAttachment");
            GenericValue questionFile = delegator.makeValue("TblQuestionAttachment");
            questionFile.setString("questionId", questionId);
            questionFile.setString("id", id);
            questionFile.setString("dataResourceId", dataResourceId);
            questionTemplates.add(questionFile);
        }
        delegator.storeAll(questionTemplates);
    }


    successResult.put("msg", msg);
    return successResult;
}


//查询问题
public Map<String, Object> searchQuestion() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String userLoginId = ((GenericValue) context.get("userLogin")).get("userLoginId");
    String statusId=context.get("statusId");
    Set<String> fieldsToSelect1 = FastSet.newInstance();
    fieldsToSelect1.add("questionId");
    fieldsToSelect1.add("questionOverview");
    fieldsToSelect1.add("createdStamp");
    fieldsToSelect1.add("questionDetail");
    fieldsToSelect1.add("statusId");
    fieldsToSelect1.add("rejected");
    fieldsToSelect1.add("browseNum");
    List<GenericValue> idOverviewDateDetail;
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
////比较
    EntityCondition mainCond1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
    EntityCondition mainCond2 = EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId);
    if(statusId==null) {
        idOverviewDateDetail = delegator.findList("TblQuestion",mainCond2, fieldsToSelect1,UtilMisc.toList("-createdStamp"), null, false);
    }
    else {
        idOverviewDateDetail = delegator.findList("TblQuestion", EntityCondition.makeCondition(UtilMisc.toList(mainCond1, mainCond2)), fieldsToSelect1,UtilMisc.toList("-createdStamp"), null, false);
    }
    Set<String> fieldsToSelect2 = FastSet.newInstance();
    fieldsToSelect2.add("questionType");
    fieldsToSelect2.add("isStandard");
    List<GenericValue> questionType;
    Map<String, Object> map = new LinkedHashMap<>();
    Map<String, Object> map_description = new LinkedHashMap<>();
    Map<String, Object> map_questionType = new LinkedHashMap<>();
    Map<String, Object> map_answer= new LinkedHashMap<>();
    Set<String> fieldsToSelect = FastSet.newInstance();
    fieldsToSelect.add("description");
    fieldsToSelect.add("enumId");
    EntityCondition mainCond = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "QUESTIONTYPE_STATUS");
    List<GenericValue> description=delegator.findList("Enumeration",mainCond,fieldsToSelect,null,null,false);
    map_description.put("description",description);

    List<GenericValue> answerFroms;
    List<Integer> answernum=new ArrayList<>();
    Set<String> fieldsToSelect3 = FastSet.newInstance();
    fieldsToSelect3.add("answerFrom");
    int count=0;
    for(int i=0;i<idOverviewDateDetail.size();i++)
    {
        EntityCondition mainCond3 = EntityCondition.makeCondition("questionId", EntityOperator.EQUALS, idOverviewDateDetail[i].get("questionId"));
        questionType=delegator.findList("TblQuestionType", mainCond3, fieldsToSelect2, null, null, false);
        map_questionType.put(idOverviewDateDetail.get(i), questionType);
        answerFroms=delegator.findList("TblAnswer",mainCond3,fieldsToSelect3,null,null,false);
        if(answerFroms.size()>0){
            for(int j=0;j<answerFroms.size();j++){
                if(answerFroms[j].get("answerFrom")!=userLoginId){
                    count++;
                }
            }
            answernum.add(count);
            count=0;
        }
        else{
            answernum.add(0);
        }
        map_answer.put(idOverviewDateDetail.get(i),answernum.get(i));
    }
    map.put("description",map_description);
    map.put("questionall",map_questionType);
    map.put("answernum",map_answer);
    successResult.put("result", map);
    return successResult;
}

//查询问题
public Map<String, Object> searchInvitedQuestion() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String partyId = ((GenericValue) context.get("userLogin")).get("partyId");
    String statusId=context.get("statusId");
    Set<String> fieldsToSelect1 = FastSet.newInstance();
    fieldsToSelect1.add("questionId");
    fieldsToSelect1.add("questionOverview");
    fieldsToSelect1.add("createdStamp");
    fieldsToSelect1.add("questionDetail");
    fieldsToSelect1.add("statusId");
    fieldsToSelect1.add("rejected");
    fieldsToSelect1.add("browseNum");
    List<GenericValue> idOverviewDateDetail;
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
////比较
    EntityCondition mainCond1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
    EntityCondition mainCond2 = EntityCondition.makeCondition("targetUser", EntityOperator.EQUALS, partyId);
    if(statusId==null) {
        idOverviewDateDetail = delegator.findList("TblQuestion",mainCond2, fieldsToSelect1,UtilMisc.toList("-createdStamp"), null, false);
    }
    else {
        idOverviewDateDetail = delegator.findList("TblQuestion", EntityCondition.makeCondition(UtilMisc.toList(mainCond1, mainCond2)), fieldsToSelect1,UtilMisc.toList("-createdStamp"), null, false);
    }
    Set<String> fieldsToSelect2 = FastSet.newInstance();
    fieldsToSelect2.add("questionType");
    fieldsToSelect2.add("isStandard");
    List<GenericValue> questionType;
    Map<String, Object> map = new LinkedHashMap<>();
    Map<String, Object> map_description = new LinkedHashMap<>();
    Map<String, Object> map_questionType = new LinkedHashMap<>();
    Map<String, Object> map_answer= new LinkedHashMap<>();
    Set<String> fieldsToSelect = FastSet.newInstance();
    fieldsToSelect.add("description");
    fieldsToSelect.add("enumId");
    EntityCondition mainCond = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "QUESTIONTYPE_STATUS");
    List<GenericValue> description=delegator.findList("Enumeration",mainCond,fieldsToSelect,null,null,false);
    map_description.put("description",description);

    List<GenericValue> answerFroms;
    List<Integer> answernum=new ArrayList<>();
    Set<String> fieldsToSelect3 = FastSet.newInstance();
    fieldsToSelect3.add("answerFrom");
    int count=0;
    for(int i=0;i<idOverviewDateDetail.size();i++)
    {
        EntityCondition mainCond3 = EntityCondition.makeCondition("questionId", EntityOperator.EQUALS, idOverviewDateDetail[i].get("questionId"));
        GenericValue questionRecord = EntityQuery.use(delegator).from("TblQuestion").where(mainCond3).queryOne();
        questionType=delegator.findList("TblQuestionType", mainCond3, fieldsToSelect2, null, null, false);
        map_questionType.put(idOverviewDateDetail.get(i), questionType);
        answerFroms=delegator.findList("TblAnswer",mainCond3,fieldsToSelect3,null,null,false);
        if(answerFroms.size()>0){
            for(int j=0;j<answerFroms.size();j++){
                GenericValue answerRecord = answerFroms[j];
                if(answerRecord.getString("answerFrom") != questionRecord.getString("userLoginId")){
                    count++;
                }
            }
            answernum.add(count);
            count=0;
        }
        else{
            answernum.add(0);
        }
        map_answer.put(idOverviewDateDetail.get(i),answernum.get(i));
    }
    map.put("description",map_description);
    map.put("questionall",map_questionType);
    map.put("answernum",map_answer);
    successResult.put("result", map);
    return successResult;
}

//更新问题
public Map<String, Object> editQuestion() {
    Map<String,Object> successResult=ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String questionId=context.get("questionId");
    GenericValue question;
    question=delegator.findOne("TblQuestion",UtilMisc.toMap("questionId",questionId),false);
    List<GenericValue> questionType;
    questionType=delegator.findByAnd("TblQuestionType",UtilMisc.toMap("questionId",questionId),null,false);

    String dataResourceId = "";
    List<GenericValue> files = delegator.findByAnd("TblQuestionAttachment",UtilMisc.toMap("questionId",questionId),null,false);
    if(UtilValidate.isNotEmpty(files)){
        List<String> ids = new ArrayList<>();
        for (GenericValue file : files) {
            ids.add(file.getString("dataResourceId"));
        }
        dataResourceId = ids.join(",");
    }
    Set<String> fieldsToSelect = FastSet.newInstance();
    fieldsToSelect.add("description");
    fieldsToSelect.add("enumId");
    EntityCondition mainCond1 = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "QUESTIONTYPE_STATUS");
    List<GenericValue> description=delegator.findList("Enumeration",mainCond1,fieldsToSelect,null,null,false);
    Map<String, Object> map = new HashMap<String,Object>();

    //获取咨询对象信息
    if(question!=null) {
        String targetUser = question.get("targetUser");
        if(targetUser!=null&&targetUser!="")
        {
            GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where("partyId",targetUser).distinct().queryOne();
            if(person!=null)
            {
                map.put("groupName",person.get("groupName"));
                map.put("partyId",person.get("partyId"));
                map.put("fullName",person.get("fullName"));
            }
        }
    }
    map.put("question",question);
    map.put("dataResourceId",dataResourceId);
    map.put("questionType",questionType);
    map.put("description",description);

    int userScoreNo = 0;
    //查找用户积分
    GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", userLogin.getString("partyId"))).queryOne();
    if(userScore != null){
        userScoreNo = Integer.parseInt(userScore.get("scoreAvailable").toString());
    }

    map.put("maxScore",userScoreNo);
    successResult.put("result",map);
    return successResult;
}

//回复问题
public Map<String, Object> handelAnswer() {
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.getString("partyId");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String answerTo = context.get("answerTo");
    String questionId = context.get("questionId");
    String userLoginId = ((GenericValue) context.get("userLogin")).get("userLoginId");
    String answerContent = context.get("answerContent");

    answerId = delegator.getNextSeqId("TblAnswer");
    String isAdopt = "N";
    Map<String, Object> answerMap = UtilMisc.toMap("answerId", answerId, "questionId", questionId, "answerFrom", userLoginId, "answerContent", answerContent, "isAdopt", isAdopt, "answerTo", answerTo);
    GenericValue answer = delegator.makeValidValue("TblAnswer", answerMap);
    GenericValue handelQuestion = delegator.create(answer);
    List<GenericValue> answerreturn;
    answerreturn = delegator.findByAnd("TblAnswer",UtilMisc.toMap("answerId",answerId), null,false);

    //changed by galaxypan@2017-09-28:自己回复自己的问题不能获得积分
    GenericValue question = EntityQuery.use(delegator).from("TblQuestion").where("questionId", questionId).queryOne();
    if(!userLogin.getString("userLoginId").equalsIgnoreCase(question.getString("userLoginId"))){
        dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreTarget",partyId,"eventName","SCORE_RULE_2"));
    }
    successResult.put("data", answerreturn);
    return successResult;
}

//采纳问题
public Map<String, Object> getAnswer() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String answerId = context.get("answerId");
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String msg = "问题已解决";
    try {
        List<GenericValue> answer;
////比较
        EntityCondition mainCond1 = EntityCondition.makeCondition("answerId", EntityOperator.EQUALS, answerId);
        answer = delegator.findList("TblAnswer",mainCond1, null, null, null, false);
        for(int i=0;i<answer.size();i++) {
            GenericValue answeredQuestion = EntityQuery.use(delegator).from("TblQuestion").where("questionId", answer[i].get("questionId")).queryOne();
            answeredQuestion.set("statusId", "QUESTION_STATUS_COMPLETE");
            answeredQuestion.store();
            //悬赏积分
            Integer integral = answeredQuestion.getInteger("integral");
            if(integral != null && integral > 0){
                String scoreSource = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", answeredQuestion.getString("userLoginId")).queryOne().getString("partyId");
                String scoreTarget = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", answer[i].getString("answerFrom")).queryOne().getString("partyId");
                dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreSource",scoreSource,"scoreTarget",scoreTarget, "bizScoreOnSource", 0 - integral, "bizScoreOnTarget", integral, "eventName", "SCORE_RULE_ADDANDSUBTRACT"));
            }
            GenericValue getAnswer2=delegator.makeValue("TblAnswer", UtilMisc.toMap("answerId", answerId, "isAdopt", "Y"));
            delegator.store(getAnswer2);
        }
    }
    catch (GenericEntityException ex) {
        msg = "解决失败";
    }
    successResult.put("msg", msg);
    return successResult;
}

//根据问题类型查询问题
public Map<String, Object> searchTypeQuestion() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String questionType=context.get("questionType");
    String userLoginId=((GenericValue) context.get("userLogin")).get("userLoginId");
    String loginId = ((GenericValue) context.get("userLogin")).get("partyId");

    Integer page = context.get("page");//分页
    if(page == null){
        page = 1;
    }

    String pageSize = "15";//默认20条每页

    String filterParty = context.get("filterParty");//是否是portlet的查询，如果是需要限制条数
    if(UtilValidate.isNotEmpty(filterParty)){
        pageSize = "8";
    }
    List<EntityCondition> conditions = new ArrayList<>();
    EntityCondition relatedCon = EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("targetUser",EntityOperator.EQUALS,loginId),EntityOperator.OR,EntityCondition.makeCondition("targetUser",EntityOperator.EQUALS,null)),EntityOperator.OR,EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId));
    conditions.add(relatedCon);
    conditions.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"QUESTION_STATUS_WAIT_ANSWER"));
    if(UtilValidate.isNotEmpty(questionType)){
        conditions.add(EntityCondition.makeCondition("questionType", EntityOperator.EQUALS, questionType));
    }

    List<Map> resultRows = new ArrayList<>();
    List<String> questionIds = new ArrayList<>();
    int totalCount = 0;
    UtilPagination.PaginationResult result = UtilPagination.queryPage(from(UtilValidate.isNotEmpty(questionType) ? "QuestionWithType" : "TblQuestion").where(conditions), UtilMisc.toMap("VIEW_INDEX", String.valueOf((page - 1)), "VIEW_SIZE", pageSize, "sortField", "-createdStamp"));
    if (result != null) {
        totalCount = result.getTotalCount();
        List<GenericValue> resultData = result.getResultList();
        for (GenericValue row : resultData) {
            questionIds.add(row.getString("questionId"));
        }

        List<GenericValue> answers = EntityQuery.use(delegator).from("TblAnswer").where(EntityCondition.makeCondition("questionId", EntityOperator.IN, questionIds)).queryList();
        Map<String, List<GenericValue>> answerMap = answers.groupBy {x -> x.getString("questionId")};

        List<GenericValue> questionTypes = EntityQuery.use(delegator).from("QuestionWithType").where(EntityCondition.makeCondition("questionId", EntityOperator.IN, questionIds)).queryList();
        Map<String, List<GenericValue>> questionTypeMap = questionTypes.groupBy {x -> x.getString("questionId")};

        for (GenericValue rowData : resultData) {
            Map row = new HashMap();
            row.putAll(rowData);
            String questionAuthor = rowData.getString("userLoginId");
            List<GenericValue> answerData = answerMap.get(rowData.getString("questionId"));
            if(UtilValidate.isEmpty(answerData)){
                row.put("answerNum", 0);
            }else{
                row.put("answerNum",answerData.count {x -> !"questionAuthor".equals(x.getString("answerFrom"))});
            }
            List<GenericValue> typeData = questionTypeMap.get(rowData.getString("questionId"));
            if(UtilValidate.isEmpty(typeData)){
                row.put("types", new ArrayList(0));
            }else{
                row.put("types",typeData);
            }
            resultRows.add(row);
        }
    }
    int totalPage = totalCount == 0 ? 0 : (((totalCount - 1) / Integer.parseInt(pageSize)) + 1);

    Map dataMap = new HashMap();
    dataMap.put("result", resultRows);
    dataMap.put("page", page);
    dataMap.put("totalPage", totalPage);
    dataMap.put("isPage",context.get("isPage")==null?"N":context.get("isPage"));
    successResult.put("data", dataMap);
    return successResult;
}

//点击问题标题后
public Map<String, Object> findQuestionAnswer() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String questionId=context.get("questionId");
    GenericValue userLogin = context.get("userLogin");
    Map<String,Object> userMap = new HashMap<>();
    userMap.put("userLogin",userLogin);
    Set<String> fieldsToSelect1 = FastSet.newInstance();
    fieldsToSelect1.add("questionOverview");
    fieldsToSelect1.add("createdStamp");
    fieldsToSelect1.add("questionDetail");
    fieldsToSelect1.add("questionId");
    fieldsToSelect1.add("userLoginId");
    fieldsToSelect1.add("isAnonymous");
    fieldsToSelect1.add("rejected");
    fieldsToSelect1.add("browseNum");
    fieldsToSelect1.add("statusId");
    List<GenericValue> idOverviewDateDetail;
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
////比较
    EntityCondition mainCond1 = EntityCondition.makeCondition("questionId", EntityOperator.EQUALS, questionId);
    idOverviewDateDetail = delegator.findList("TblQuestion",mainCond1, fieldsToSelect1, UtilMisc.toList("-createdStamp"), null, false);

    List<GenericValue> answer = delegator.findByAnd("TblAnswer",UtilMisc.toMap("questionId",questionId), null,false);
    Map<String,Object> answerMap = new HashMap<>();
    for(GenericValue map : answer){
        if(map.get("isAdopt").equals("Y")){
            answerMap.putAll(map);
        }
    }

    Set<String> fieldsToSelect2 = FastSet.newInstance();
    fieldsToSelect2.add("questionType");
    fieldsToSelect2.add("isStandard");
    Map<String, Object> map_questionall = new LinkedHashMap<>();
    for(int i=0;i<idOverviewDateDetail.size();i++) {
        GenericValue genericValue = idOverviewDateDetail.get(i);
        if(UtilValidate.isNotEmpty(genericValue.get("browseNum"))){
            genericValue.put("browseNum", Integer.parseInt(genericValue.getString("browseNum")) + 1);
        }else{
            genericValue.put("browseNum", 1);
        }
        genericValue.store();
        EntityCondition mainCond2 = EntityCondition.makeCondition("questionId", EntityOperator.EQUALS, questionId);
        List<GenericValue> questionType = delegator.findList("TblQuestionType", mainCond2, fieldsToSelect2, null, null, false);
        map_questionall.put(idOverviewDateDetail.get(i), questionType);
    }
    Set<String> fieldsToSelect = FastSet.newInstance();
    fieldsToSelect.add("description");
    fieldsToSelect.add("enumId");
    EntityCondition mainCond = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "QUESTIONTYPE_STATUS");
    List<GenericValue> description=delegator.findList("Enumeration",mainCond,fieldsToSelect,null,null,false);
    //查询提问附件
    List<GenericValue> fileList = EntityQuery.use(delegator).from("QuestionDateResource").where(UtilMisc.toMap("questionId",questionId)).queryList();
    Map map =new LinkedHashMap();
    map.put("result",map_questionall);
    map.put("fileList",fileList);
    map.put("answerMap",answerMap);
    map.put("content",answer);
    map.put("description",description);
    map.put("userMap",userMap);
    successResult.put("data", map);
    return successResult;
}

//改状态
public Map<String, Object> changeQuestionStatus() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String questionId = context.get("questionId");
    String statusId=context.get("statusId");
    String rejected=context.get("rejected");
    String msg = "成功";
    try {
        GenericValue changeStatus=delegator.makeValue("TblQuestion", UtilMisc.toMap("questionId", questionId, "statusId", statusId));//改为状态
        delegator.store(changeStatus);
        if(statusId=="QUESTION_STATUS_REJECTED"){
            GenericValue changerejected=delegator.makeValue("TblQuestion", UtilMisc.toMap("questionId", questionId, "rejected", rejected));//改为状态
            delegator.store(changerejected);
        }
    }
    catch (GenericEntityException ex) {
        msg = "失败";
    }
    successResult.put("msg", msg);
    return successResult;
}