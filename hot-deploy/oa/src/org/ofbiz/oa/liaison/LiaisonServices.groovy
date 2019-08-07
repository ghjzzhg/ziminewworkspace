import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp

public Map<String,Object> checkLiaison(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contactListId = context.get("contactListId");
    String workSheetAuditInformationId = context.get("workSheetAuditInforId");
    String content=context.get("content");
    String partyId = context.get("partyId")
    String reviewTheStatus = context.get("reviewTheStatus");
    String msg = "";
    if(reviewTheStatus == "PERSON_TWO"){
        msg = "审核通过！"
    }else if(reviewTheStatus == "PERSON_THREE"){
        msg = "已驳回！"
    }

    GenericValue genericValue = delegator.findByPrimaryKey("TblWorkSheetAuditInfor",UtilMisc.toMap("workSheetAuditInforId",workSheetAuditInformationId));
    genericValue.put("content",content);
    genericValue.put("reviewTheStatus",reviewTheStatus);
    genericValue.put("responseTime",new Timestamp(new Date().getTime()));
    genericValue.store();


    //list
    List<GenericValue> workSheetAuditInformationList = delegator.findByAnd("TblWorkSheetAuditInfor",UtilMisc.toMap("contactListId",contactListId),null,false);
    List<GenericValue> workSheetAuditInformationLists=new ArrayList<>();
    int reviewTheStatusPass=0;//已审核人数
    boolean reviewTheStatusReject=false;//是否有人驳回
    for (int i=0;i<workSheetAuditInformationList.size();i++){
        Map map=new HashMap();
        map.putAll(workSheetAuditInformationList.get(i));
        if (map.get("reviewTheStatus")=="PERSON_TWO"){//已审核
            reviewTheStatusPass++;
        }else if (map.get("reviewTheStatus")=="PERSON_THREE"){//已驳回
            reviewTheStatusReject=true;
        }
        String responseTime=map.get("responseTime");
        if (UtilValidate.isNotEmpty(responseTime)){
            map.put("responseTime",responseTime.substring(0,responseTime.length()-2));
        }
        workSheetAuditInformationLists.add(map);
    }
    GenericValue workContactList = delegator.findByPrimaryKey("TblWorkContactList",UtilMisc.toMap("contactListId",contactListId));
    if (reviewTheStatusReject){
        workContactList.put("reviewTheStatus","LIAISON_STATUS_FOUR");//有人驳回则此方案作废
    }else if (reviewTheStatusPass==workSheetAuditInformationList.size()){
        workContactList.put("reviewTheStatus","LIAISON_STATUS_TWO");//所有人审核通过后，此方案进入已送达阶段
    }
    workContactList.store();
    liaisonMap=delegator.findByPrimaryKey("TblWorkContactList",UtilMisc.toMap("contactListId",contactListId));

    successResult.put("data",UtilMisc.toMap("msg",msg,"workSheetAuditInformationList",workSheetAuditInformationLists,"liaisonMap",liaisonMap));
    return successResult;
}
