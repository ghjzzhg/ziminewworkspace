import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

GenericValue userLogin = (GenericValue)context.get("userLogin");
Map<String,Object> returnMeetingNoticeMap = dispatcher.runSync("searchMeetingNotice",UtilMisc.toMap("userLogin",userLogin));
FastMap<String,Object> meetingNoticeMap = (FastMap<String,Object>)returnMeetingNoticeMap.get("returnValue");
List<FastMap<String,Object>> meetingNoticeDataList = (List<FastMap<String,Object>>)meetingNoticeMap.get("list");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
Integer noNum = 0;
List<Map<String,Object>> meetingNoticeList = new ArrayList<Map<String,Object>>();
if(UtilValidate.isNotEmpty(meetingNoticeDataList)){
    String today = format.format(new Date());
    for(FastMap meetingNotice : meetingNoticeDataList){
        Map<String,Object> map = new HashMap<String,Object>();
        String meetingStartTime = meetingNotice.get("meetingStartTime");
        map.putAll(meetingNotice);
        String meetingStartDate = format.format(format.parse(meetingStartTime))
        map.put("meetingStartTime",meetingStartDate);
        if(meetingStartDate.equals(today)){
            noNum++;
        }
        meetingNoticeList.add(map);
    }
}
context.notices= meetingNoticeList;
context.noNum = noNum;