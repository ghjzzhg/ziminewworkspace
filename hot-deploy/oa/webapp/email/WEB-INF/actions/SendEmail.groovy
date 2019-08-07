import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp


public Map<String ,Object> sendEmail(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String msg = "";
    GenericValue userLogin = (GenericValue)parameters.get("userLogin");
    String type = context.get("type");
    Map result = dispatcher.runSync("findPartyFromEmailAddress",
            UtilMisc.toMap("address", context.get("toString"), "userLogin", userLogin))
    GenericValue contactMech = EntityQuery.use(delegator)
            .select()
            .from("PartyContactMech")
            .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLogin.get("partyId")))
            .queryFirst();
    String contactMechIdFrom = contactMech.get("contactMechId");
    String contactMechIdTo = result.get("contactMechId");


    if (UtilValidate.isNotEmpty(type) && type.equals("send")){
        String communicationEventId = delegator.getNextSeqId("CommunicationEvent");
        context.put("communicationEventId",communicationEventId);
        context.put("statusId","COM_IN_PROGRESS");
        context.put("contentMimeTypeId","text/html");
        context.put("communicationEventTypeId","EMAIL_COMMUNICATION");
        context.put("contactMechTypeId","EMAIL_ADDRESS");
        context.put("datetimeStarted",new Timestamp(System.currentTimeMillis()));
        context.put("roleTypeIdFrom","ORIGINATOR");
        context.put("roleTypeIdTo","ADDRESSEE");
        context.put("contactMechIdFrom",contactMechIdFrom);//TODO:此处根据当前登录人的partyId,获取对应的contachMechId
        context.put("contactMechIdTo",contactMechIdTo);//TODO:获取对应的contachMechId
        GenericValue genericValue = delegator.makeValidValue("CommunicationEvent",context);
        genericValue.create();
        msg = sendEventAsMail(communicationEventId);
    } else if (UtilValidate.isNotEmpty(type) && type.equals("save")){
        String communicationEventId = delegator.getNextSeqId("CommunicationEvent");
        context.put("communicationEventId",communicationEventId);
        context.put("statusId","COM_PENDING");//待定状态
        context.put("contentMimeTypeId","text/html");
        context.put("communicationEventTypeId","EMAIL_COMMUNICATION");
        context.put("contactMechTypeId","EMAIL_ADDRESS");
        context.put("datetimeStarted",new Timestamp(System.currentTimeMillis()));
        context.put("roleTypeIdFrom","ORIGINATOR");
        context.put("roleTypeIdTo","ADDRESSEE");
        context.put("contactMechIdFrom",contactMechIdFrom);//TODO;此处根据当前登录人的partyId,获取对应的contachMechId
        context.put("contactMechIdTo",contactMechIdTo);//TODO;获取对应的contachMechId
        GenericValue genericValue = delegator.makeValidValue("CommunicationEvent",context);
        genericValue.create();
        msg = "操作成功";
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}


public String sendEventAsMail(String communicationEventId){
    String msg = "";
    //附件上传
    String fileId = context.get("fileId");
    List<String> newFileList = new ArrayList<String>();
    if(null != fileId && !"".equals(fileId)){
        String[] files = fileId.split(",");
        for(String file : files){
            newFileList.add(file);
        }
    }
    List<String> oldFileList = new ArrayList<String>();
    List<GenericValue> workAccessFile = EntityQuery.use(delegator)
            .select()
            .from("TblFileScope")
            .where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", communicationEventId, "entityName", "CommEventContentAssoc")))
            .queryList();
    if(null != workAccessFile && workAccessFile.size() > 0){
        for(GenericValue workAccess : workAccessFile){
            oldFileList.add(workAccess.get("accessoryId").toString());
        }
    }
    saveWorkFile(communicationEventId,oldFileList,newFileList);

    for (String id : newFileList){
        Map contentValue = new HashMap();
        contentValue.put("contentId",id);
        contentValue.put("contentTypeId","DOCUMENT");
        contentValue.put("dataResourceId",id);
        GenericValue content = delegator.makeValidValue("Content",contentValue);
        content.create();
        Map contextMap = new HashMap();
        contextMap.put("contentId",id);
        contextMap.put("communicationEventId",communicationEventId);
        contextMap.put("fromDate",new Timestamp(System.currentTimeMillis()));
        GenericValue commEventContentAssoc = delegator.makeValidValue("CommEventContentAssoc",contextMap);
        commEventContentAssoc.create();
    }
    msg = "操作成功";
    try{
        dispatcher.runSync("sendCommEventAsEmail", UtilMisc.toMap("communicationEventId",communicationEventId,"userLogin",userLogin));
    }catch (Exception e){
        msg = "操作失败";
    }
    return mag;
}

/**
 * 保存附件
 * @param workId
 * @param oldFileList
 * @param newFileList
 */
public void saveWorkFile(String workId,List<String> oldFileList,List<String> newFileList){
    for(String fileId:newFileList) {
        if (!oldFileList.contains(fileId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            String fileScopeId = delegator.getNextSeqId("TblFileScope");
            map.put("fileScopeId", fileScopeId);
            map.put("entityName", "CommEventContentAssoc");
            map.put("dataId", workId);
            map.put("accessoryId", fileId);
            GenericValue accessory = delegator.makeValidValue("TblFileScope", map);
            accessory.create();
        }
    }
    for(String fileid:oldFileList){
        if(!newFileList.contains(fileid)) {
            delegator.removeByAnd("TblFileScope", UtilMisc.toMap("entityName", "CommEventContentAssoc", "dataId", workId, "accessoryId", fileid));
            GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileid)).queryOne();
            if (null != fileData && "ATTACHMENT_FILE".equals(fileData.get("dataResourceTypeId").toString())) {
                File file = new File(fileData.get("objectInfo").toString() + fileData.get("dataResourceName").toString());
                if (file.exists()) {
                    file.delete();
                }
                delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileid));
            }
        }
    }
}