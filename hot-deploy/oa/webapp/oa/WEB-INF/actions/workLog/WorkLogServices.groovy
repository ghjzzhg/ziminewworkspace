package org.ofbiz.oa

import org.apache.catalina.connector.Request
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

import javax.servlet.http.HttpServletRequest
import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> saveWorkLog() {
    userLoginId = context.get("userLogin");
    String partyId = userLoginId.get("partyId");
    workLogId = context.get("workLogId");
    id = workLogId;
    GenericValue genericValue = null;
    date = context.get("workDate");
    dateTime = date + " 00:00:00.0"
    Timestamp workDate = Timestamp.valueOf(dateTime);
    try{
        if (workLogId == ""||workLogId == null){
            workLogId = delegator.getNextSeqId("TblWorkLog").toString();
            genericValue = delegator.makeValue("TblWorkLog",UtilMisc.toMap("workLogId", workLogId))
        }else {
            genericValue = delegator.findByPrimaryKey("TblWorkLog",UtilMisc.toMap("workLogId", workLogId));
        }
        genericValue.setString("logTitle",context.get("logTitle"))
        genericValue.setString("logContent",context.get("logContent"))
        genericValue.setString("planTitle",context.get("planTitle"))
        genericValue.setString("planContent",context.get("planContent"))
        genericValue.setString("partyId",partyId)
        genericValue.set("workDate",workDate)
        if (id==null||id==""){
            genericValue.create();
        }else {
            genericValue.store();
        }
    }catch (GenericEntityException e){
        e.printStackTrace();
    }
    result.put("data",workLogId)
    return result;
}
public Map<String, Object> saveLeadInstructions() {
    workLogId = context.get("workLogId");
    Map<String,Object> result = ServiceUtil.returnSuccess();
    GenericValue genericValue = null;
    date = context.get("workDate");
    Timestamp workDate = Timestamp.valueOf(date);
    Date dateForChange = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = df.format(dateForChange);
    reviewDate = Timestamp.valueOf(dateString+" 00:00:00.0");
    try{
        workLogId = delegator.getNextSeqId("TblWorkLog").toString();
        genericValue = delegator.makeValue("TblWorkLog",UtilMisc.toMap("workLogId", workLogId))
        genericValue.setString("logTitle",context.get("logTitle"))
        genericValue.setString("logContent",context.get("logContent"))
        genericValue.setString("planTitle",context.get("planTitle"))
        genericValue.setString("planContent",context.get("planContent"))
        genericValue.setString("reviewContent",context.get("reviewContent"))
        genericValue.setString("reviewedBy",context.get("reviewedBy"))
        genericValue.setString("partyId",context.get("partyId"))
        genericValue.set("workDate",workDate)
        genericValue.set("reviewDate",reviewDate)
            genericValue.store();
    }catch (GenericEntityException e){
        e.printStackTrace();
    }
    result.put("data",workLogId)
    return result;
}
public Map<String ,Object> saveAttentionSubordinate(){
    String partyIdForm = context.get("userLogin").get("partyId");
    String partyIdTo = context.get("partyId")
    String ifAttentionButton = context.get("ifAttentionButton")
    if (ifAttentionButton=="关注"){
        try{
            genericValue = delegator.findByPrimaryKey("TblFocusUnderling",UtilMisc.toMap("partyIdForm", partyIdForm));
            genericValue.setString("partyIdTo",partyIdTo)
            genericValue.create();
        }catch (GenericEntityException e){
            e.printStackTrace();
        }
    }else if("取消关注"){
        genericValue = delegator.findByAnd("TblFocusUnderling",UtilMisc.toMap("partyIdForm", partyIdForm,"partyIdTo",partyIdTo));
    }

    return null;
}