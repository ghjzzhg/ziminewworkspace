import javolution.util.FastMap
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition

import java.text.SimpleDateFormat

String summaryId = parameters.get("summaryId");
GenericValue userLogin = context.get("userLogin");

GenericValue meetingSummary = from("MeetingSummaryNotice")
        .where(EntityCondition.makeCondition("summaryId",summaryId))
        .queryOne();
Map<String,Object> valueMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(meetingSummary)){
    valueMap.putAll(meetingSummary);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    valueMap.put("meetingStartTime",format.format((java.util.Date)meetingSummary.get("meetingStartTime")));
    valueMap.put("meetingEndTime",format.format((java.util.Date)meetingSummary.get("meetingEndTime")));
    valueMap.put("releaseSummaryTime",format.format((java.util.Date)meetingSummary.get("releaseSummaryTime")));
}
context.valueMap = valueMap;



