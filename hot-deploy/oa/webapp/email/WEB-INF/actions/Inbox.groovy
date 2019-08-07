import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
/*context.inboxList = [[sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "<span class='mail-icon mail-attachment'></span>", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "read", statusIcon:"<span class='mail-icon mail-read'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"],
                     [sender: "系统管理员", title: "系统通知", time: "2015-06-01 10:10:10", attachment: "", status: "unread", statusIcon:"<span class='mail-icon mail-unread'></span>"]
];*/


public Map<String,Object> searchInbox(){
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
    GenericValue userLogin = context.get("userLogin");
    String msg = "";
    EntityListIterator communicationEvent = EntityQuery.use(delegator)
            .select()
            .from("CommunicationEventAndRole")
            .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLogin.getString("partyId")))
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != communicationEvent && communicationEvent.getResultsSizeAfterPartialList() > 0){
        totalCount = communicationEvent.getResultsSizeAfterPartialList();
        pageList = communicationEvent.getPartialList(lowIndex, viewSize);
    }
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    for (Map map1 : pageList){
        Map<String,Object> value = new HashMap<String,Object>();
        GenericValue content = EntityQuery.use(delegator)
                .select()
                .from("CommEventContentAssoc")
                .where(EntityCondition.makeCondition("communicationEventId",EntityOperator.EQUALS,map1.getString("communicationEventId")))
                .queryFirst();
        if (UtilValidate.isNotEmpty(context)){

        }
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

public Map<String ,Object> checkEmail(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String communicationEventId = context.get("communicationEventId");
    Map<String,Object> map = new HashMap();
    if (UtilValidate.isNotEmpty(communicationEventId)){
        GenericValue communicationEvent = EntityQuery.use(delegator)
                .select()
                .from("CommunicationEvent")
                .where(EntityCondition.makeCondition("communicationEventId",EntityOperator.EQUALS,communicationEventId))
                .queryOne();
        map.put("receive",communicationEvent.get("toString"));
        map.put("type",context.get("type"));
        map.putAll(communicationEvent);
        //获取附件
        List<Map<String,Object>> fileList = new ArrayList<Map<String,Object>>();
        List<GenericValue> genericValueList = EntityQuery.use(delegator)
                .select().from("CommEventContentAssoc")
                .where(EntityCondition.makeCondition(UtilMisc.toMap("communicationEventId", communicationEventId)))
                .queryList();
        String fileIds = "";
        if(null != genericValueList && genericValueList.size() > 0){
            for(GenericValue genericValue : genericValueList){
                GenericValue generic = EntityQuery.use(delegator)
                        .select().from("Content")
                        .where(EntityCondition.makeCondition(UtilMisc.toMap("contentId", genericValue.get("contentId"))))
                        .queryOne();
                Map value = new HashMap();
                value.put("fileName",generic.get("contentName"));
                value.put("contentId",generic.get("contentId"));
                fileList .add(value);
            }

            map.put("fileList",fileList);
        }
        successResult.put("data",map);
    }
    return successResult;
}

public Map<String,Object> deleteEmail() {
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String communicationEventIdList = context.get("communicationEventIdList");
    String msg = "";
    if (UtilValidate.isNotEmpty(communicationEventIdList)){
        String[] ids = communicationEventIdList.split(",");
        for (String id : ids){
            GenericValue genericValue = delegator.makeValidValue("CommunicationEvent","communicationEventId",id);
            genericValue.put("statusId", "COM_COMPLETE");
            genericValue.store();
        }
        msg = "操作成功"
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> removeEmail(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String communicationEventIdList = context.get("communicationEventIdList");
    String msg = "操作失败";
    if (UtilValidate.isNotEmpty(communicationEventIdList)){
        String[] ids = communicationEventIdList.split(",");
        for (String id : ids){
            GenericValue genericValue = delegator.makeValidValue("CommunicationEvent","communicationEventId",id);
            genericValue.removeRelated("CommEventContentAssoc");
//            genericValue.removeRelated("CommunicationEventProduct");
            genericValue.removeRelated("CommunicationEventPurpose");
            genericValue.removeRelated("CommunicationEventRole");
            genericValue.removeRelated("PartyNeed");
            genericValue.remove();
        }
        msg = "操作成功"
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}
