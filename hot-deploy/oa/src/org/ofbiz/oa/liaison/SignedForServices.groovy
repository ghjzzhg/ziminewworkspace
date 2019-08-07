import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String,Object> signedLiaison(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contactListId = context.get("contactListId");
    String workSheetSignForInformationId = context.get("workSheetSignForId");
    String content=context.get("content");
    String reviewTheStatus = context.get("reviewTheStatus");
    String msg = "";
    if(reviewTheStatus == "SIGN_FOR_YES"){
        msg = "签收成功！"
    }
  /*  SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    new Timestamp.getTime()*/
    GenericValue genericValue = delegator.findByPrimaryKey("TblWorkSheetSignForInfor",UtilMisc.toMap("workSheetSignForId",workSheetSignForInformationId));
    genericValue.put("SignForContent",content);
    genericValue.put("reviewTheStatus",reviewTheStatus);
    genericValue.put("SignforTime",new Timestamp(new Date().getTime()));
    genericValue.store();


    //list
    List<GenericValue> workSheetSignForInformationList = delegator.findByAnd("TblWorkSheetSignForInfor",UtilMisc.toMap("contactListId",contactListId),null,false);
    List<GenericValue> workSheetSignForInformationLists=new ArrayList<>();
    int reviewTheStatusSign=0;//已签收人数
    for (int i=0;i<workSheetSignForInformationList.size();i++){
        Map map=new HashMap();
        map.putAll(workSheetSignForInformationList.get(i));
        if (map.get("reviewTheStatus")=="SIGN_FOR_YES"){//已签收
            reviewTheStatusSign++;
        }
        String SignforTime=map.get("SignforTime");
        if (UtilValidate.isNotEmpty(SignforTime)){
            map.put("SignforTime",SignforTime.substring(0,SignforTime.length()-2));
        }
        workSheetSignForInformationLists.add(map);
    }
    GenericValue workContactList = delegator.findByPrimaryKey("TblWorkContactList",UtilMisc.toMap("contactListId",contactListId));
    if (reviewTheStatusSign==workSheetSignForInformationList.size()){
        workContactList.put("reviewTheStatus","LIAISON_STATUS_THREE");//所有人签收后，此方案进入'进行中'阶段
    }
    workContactList.store();
    liaisonMap=delegator.findByPrimaryKey("TblWorkContactList",UtilMisc.toMap("contactListId",contactListId));

    successResult.put("data",UtilMisc.toMap("msg",msg,"workSheetSignForInformationList",workSheetSignForInformationLists,"liaisonMap",liaisonMap));
    return successResult;
}
