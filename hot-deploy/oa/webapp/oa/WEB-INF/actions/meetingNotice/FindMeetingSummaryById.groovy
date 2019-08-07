import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

String summaryId = parameters.get("summaryId");

if(UtilValidate.isNotEmpty(summaryId)){
    GenericValue meetingNoticeMap = from("MeetingSummaryNotice")
            .where(EntityCondition.makeCondition("summaryId",EntityOperator.EQUALS,summaryId))
            .queryOne();
    //absencePersonList
    context.meetingNoticeMap = meetingNoticeMap;
    context.hasWorkPlan = "N";
    context.isCreate = "N";
}else {
    context.isCreate = "Y";
}

