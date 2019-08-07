import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
/*
context.inboxList = [[sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"]
];*/


public Map<String,Object> searchTrash(){
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 10;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 10;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String msg = "";
    EntityListIterator communicationEvent = EntityQuery.use(delegator)
            .select()
            .from("CommunicationEvent")
            .where(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"COM_COMPLETE"))
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != communicationEvent && communicationEvent.getResultsSizeAfterPartialList() > 0){
        totalCount = communicationEvent.getResultsSizeAfterPartialList();
        pageList = communicationEvent.getPartialList(lowIndex, viewSize);
    }
    communicationEvent.close();
    Map map = new HashMap();
    map.put("list",pageList);
    map.put("mag",msg);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    successResult.put("data",map);

    return successResult;
}
