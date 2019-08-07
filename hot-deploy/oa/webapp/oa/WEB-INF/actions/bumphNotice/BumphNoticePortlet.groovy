import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

import java.text.SimpleDateFormat

GenericValue userLogin = (GenericValue)context.get("userLogin");
Map<String,Object> returnNoticeMap = dispatcher.runSync("searchNotice",UtilMisc.toMap("userLogin",userLogin));
FastMap<String,Object> noticeDataMap = (FastMap<String,Object>)returnNoticeMap.get("returnValue");
List<FastMap<String,Object>> noticeDataList = (List<FastMap<String,Object>>)noticeDataMap.get("list");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
String today = format.format(new Date());
//表示今天没有公文数量
Integer noNum = 0;
if(UtilValidate.isNotEmpty(noticeDataList)){
    for(FastMap noticeData : noticeDataList){
        String newDate = format.format(format.parse(noticeData.get("releaseTime").toString()))
        if(newDate.equals(today)){
            noNum++;
        }
    }
}else{
    noticeDataList = new ArrayList<FastMap<String,Object>>()
}
context.notices= noticeDataList;
context.noNum = noNum;