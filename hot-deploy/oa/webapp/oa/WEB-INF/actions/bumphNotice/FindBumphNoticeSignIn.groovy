import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

bumphNoticeSignInList = FastList.newInstance();
List<GenericValue> noticeList = parameters.returnValue
if(UtilValidate.isNotEmpty(noticeList)){
    for(Map<String,Object> notice : noticeList){
        List<GenericValue> signInPersonList = delegator.findList("SignInPersonInfo","noticeId",notice.get("noticeId"));
        for (Map<String,Object> sipList : signInPersonList){
            Map valueMap = FastMap.newInstance();
            valueMap.putAll(notice);
            valueMap.putAll(sipList);
            noticeList.add(valueMap);
        }
    }
}
context.bumphNoticeSignInList = bumphNoticeSignInList;


