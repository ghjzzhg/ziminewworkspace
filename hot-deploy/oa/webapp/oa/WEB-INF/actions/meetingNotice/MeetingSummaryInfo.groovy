import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator

import java.text.SimpleDateFormat

Map<String,Object> map = parameters.get("returnValue");
List<GenericValue> returnValue = map.get("list");
List<Map> meetingSummaryList = FastList.newInstance();
if(UtilValidate.isNotEmpty(returnValue)){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    for(GenericValue g : returnValue){
        Map<String,Object> valueMap = FastMap.newInstance();
        List<GenericValue> participants = from("SignInPersonInfo")
                .where(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("noticeId",EntityOperator.EQUALS,g.get("meetingNoticeId")),EntityCondition.makeCondition("signInPersonType",EntityOperator.EQUALS,"TblMeetingNotice"))))
                .queryList();
        String participantsNames = "";
        if(UtilValidate.isNotEmpty(participants)){
            for(GenericValue pg:participants){
                participantsNames += ","+pg.get("fullName");
            }
        }
        valueMap.putAll(g);
        if(participantsNames.length() > 1){
            participantsNames = participantsNames.substring(1);
        }
        valueMap.put("participantsNames",participantsNames);
        GenericValue releasePerson = from("Person")
                .where(EntityCondition.makeCondition("partyId",g.get("releasePerson")))
                .queryOne();
        if(UtilValidate.isNotEmpty(releasePerson)){
            valueMap.put("releasePersonName",releasePerson.get("fullName"));
        }
        EntityListIterator eli = from("FeedbackMiddleFeedback")
                .where(EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("feedbackMiddleId",EntityOperator.EQUALS,g.get("meetingNoticeId")),
                    EntityCondition.makeCondition("feedbackMiddleType",EntityOperator.EQUALS,"TblMeetingNotice")
                )))
                .orderBy("-feedbackTime")
                .queryIterator();
        GenericValue feedbackMiddle = eli.getAt(0);
        if(UtilValidate.isNotEmpty(feedbackMiddle)){
            valueMap.put("lastFeedbackPerson",feedbackMiddle.get("feedbackPersonName"));
            valueMap.put("lastFeedbackTime",format.format((Date)feedbackMiddle.get("feedbackTime")));
        }
        Long nowDate = new Date().getTime();
        Long meetingStartTime = ((Date)g.get("meetingStartTime")).getTime();
        String hasMeetingStart = "会议待召开";
        if(meetingStartTime<=nowDate){
            hasMeetingStart = "会议已召开";
        }
        valueMap.put("hasMeetingStart",hasMeetingStart);
        String hasReleaseSummary = "纪要未上传";
        if(UtilValidate.isNotEmpty(g.get("summaryId"))){
            hasReleaseSummary = "纪要已上传";
        }
        GenericValue lastEditPerson = from("Person")
                .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,g.get("lastEditPerson")))
                .queryOne();
        if(UtilValidate.isNotEmpty(lastEditPerson)){
            valueMap.put("lastEditPersonName",lastEditPerson.get("fullName"));
            valueMap.put("lastEditTime",format.format(g.get("lastEditTime")));
        }
        valueMap.put("hasReleaseSummary",hasReleaseSummary);
        valueMap.put("meetingStartTime",format.format(g.get("meetingStartTime")));
        valueMap.put("meetingEndTime",format.format(g.get("meetingEndTime")));
        meetingSummaryList.add(valueMap);
    }
    context.meetingSummaryList = meetingSummaryList;
}




