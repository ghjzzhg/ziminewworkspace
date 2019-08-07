import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

String meetingNoticeId = parameters.get("meetingNoticeId");
Map<String,Object> meetingNoticeMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(meetingNoticeId)){
    GenericValue meetingNMap = from("TblMeetingNotice")
            .where(EntityCondition.makeCondition("meetingNoticeId",EntityOperator.EQUALS,meetingNoticeId))
            .queryOne();
    GenericValue releaseDepartment = from("PartyGroup")
            .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,meetingNMap.get("releaseDepartment")))
            .queryOne();
    meetingNoticeMap.putAll(meetingNMap);
    if(UtilValidate.isNotEmpty(releaseDepartment)){
        meetingNoticeMap.put("groupName",releaseDepartment.get("groupName"));
    }
    context.meetingNoticeMap = meetingNoticeMap;
}

Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblMeetingNotice","prefix","meetingNotice","numName","meetingNoticeNumber","userLogin",userLogin));
context.number = uniqueNumber.get("number");
