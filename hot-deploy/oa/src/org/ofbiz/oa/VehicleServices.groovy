package org.ofbiz.oa

import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> saveVehicle() {
    String vehicleId = context.get("vehicleId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String logicDelete = "N";//初始状态未删除
    context.put("logicDelete", logicDelete);
    String msg = "保存车辆成功";
    if (UtilValidate.isNotEmpty(vehicleId)){
        msg = "更新车辆成功"
        genericValue = delegator.findByPrimaryKey("TblVehicleManagement",UtilMisc.toMap("vehicleId",vehicleId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }else {
        vehicleId = delegator.getNextSeqId("TblVehicleManagement").toString();
        genericValue = delegator.makeValidValue("TblVehicleManagement",UtilMisc.toMap("vehicleId",vehicleId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String, Object> saveVehicleCost() {
    String costId = context.get("costId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "添加车辆费用成功";
    if (UtilValidate.isNotEmpty(costId)){
        msg = "更新车辆费用成功"
        genericValue = delegator.findByPrimaryKey("TblVehicleCost",UtilMisc.toMap("costId",costId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }else {
        costId = delegator.getNextSeqId("TblVehicleCost").toString();
        genericValue = delegator.makeValidValue("TblVehicleCost",UtilMisc.toMap("costId",costId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
/**
 * 修改车辆费用金额和备注
 * @return
 */
public Map<String, Object> saveVehicleByCostId() {
    String costId = context.get("costId");
    String cost = context.get("cost");
    String remarks = context.get("remarks");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "修改车辆费用失败";
    if (UtilValidate.isNotEmpty(costId)){
        msg = "修改车辆费用成功"
        genericValue = delegator.findByPrimaryKey("TblVehicleCost",UtilMisc.toMap("costId",costId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String, Object> deleteVehicle() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String vehicleId = context.get("vehicleId");
    String logicDelete = "Y";//逻辑删除
    context.put("logicDelete", logicDelete);
    String msg = "";
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(vehicleId)){
        GenericValue NoticeTemplate = delegator.makeValidValue("TblVehicleManagement","vehicleId",vehicleId);
        NoticeTemplate.setNonPKFields(context);
        NoticeTemplate.store();
        //系统自动驳回预约
        vehicleOrder = delegator.findByAnd("TblVehicleOrder",UtilMisc.toMap("vehicleId", vehicleId));
        if(UtilValidate.isNotEmpty(vehicleOrder)){
            for(GenericValue g : vehicleOrder){
                Map map1 = new HashMap();
                map1.put("orderId", g.get("orderId"));
                map1.put("reviewState", "PERSON_THREE");
                delegator.store(delegator.makeValidValue("TblVehicleOrder",map1));
            }
        }
        msg = "删除成功";
    }
    map.put("msg", msg);
    successResult.put("data",map);
    return successResult;
}
/**
 * 删除指定车辆的指定费用
 * @return
 */
public Map<String, Object> deleteVehicleByCostId() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String vehicleId = context.get("costId");
    String msg = "删除费用成功";
    try {
        delegator.removeByAnd("TblVehicleCost", UtilMisc.toMap("costId", costId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String, Object> saveOrderVehicle() {
    String orderId = context.get("orderId");
    String reviewResult = context.get("reviewResult");
    String vehicleId = context.get("vehicleId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStringStart=context.get("startDate");
    String dateStringEnd=context.get("endDate")
    String msg = "";
    Timestamp dateStringStartTimestamp = UtilDateTime.toTimestamp(format.parse(dateStringStart.substring(0,dateStringStart.indexOf("."))));
    Timestamp dateStringEndTimestamp = UtilDateTime.toTimestamp(format.parse(dateStringStart.substring(0,dateStringEnd.indexOf("."))));

    String startDateString,endDateString;
    if (UtilValidate.isNotEmpty(dateStringStart)){
         startDateString = dateStringStart.substring(0,dateStringStart.indexOf(" "));
    }
    if(UtilValidate.isNotEmpty(dateStringEnd)){
         endDateString = dateStringEnd.substring(0,dateStringEnd.indexOf(" "));
    }
    boolean flag=false;//所选时间是否与之前的预约有冲突
    List<GenericValue> vehicleOrderList=delegator.findAll("TblVehicleOrder",false);
    for (GenericValue map:vehicleOrderList){
        if((map.get("vehicleId").toString().equals(vehicleId)) && (map.get("reviewState") != "PERSON_THREE")){
            Timestamp dateStart=map.getTimestamp("startDate");
            Timestamp dateEnd=map.getTimestamp("endDate");

            int compare1 = dateStringEndTimestamp.compareTo(dateStart);
            int compare2 = dateStringStartTimestamp.compareTo(dateEnd);
            if((compare1 == 1 || compare1 == 0) && (compare2 == -1 || compare2 == 0)){
                msg ="所选时间与其他预约有冲突，请重新预约!";
                flag=true;
            }
        }
    }

    if (!flag){
        msg = "预约车辆提交成功";
        if (UtilValidate.isNotEmpty(orderId)){
            genericValue = delegator.findByPrimaryKey("TblVehicleOrder",UtilMisc.toMap("orderId",orderId));
            if (UtilValidate.isNotEmpty(reviewResult)){
                genericValue.set("reviewState","未安排")
                genericValue.set("reviewResult",context.get("reviewResult"))
                genericValue.set("reviewSituation",context.get("reviewSituation"))
                msg = "审核成功"
            }else {
                genericValue.setNonPKFields(context);
                msg = "更新预约车辆提交成功"
            }
            genericValue.store();
        }else {
            Date startUtilDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateString);
            Date endUtilDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateString);
            java.sql.Date startSqlDate = new java.sql.Date(startUtilDate.getTime());
            java.sql.Date endSqlDate = new java.sql.Date(endUtilDate.getTime());
            orderId = delegator.getNextSeqId("TblVehicleOrder").toString();
            genericValue = delegator.makeValidValue("TblVehicleOrder",UtilMisc.toMap("orderId",orderId));
            genericValue.setNonPKFields(context);
            genericValue.set("startDateString",startDateString);
            genericValue.set("startDateFor",startSqlDate);
            genericValue.set("endDateString",endDateString);
            genericValue.set("endDateFor",endSqlDate);
            genericValue.set("reviewState","PERSON_ONE");
            genericValue.create();
        }

        BaiDuYunPush.setMessageData(orderId,genericValue,"1",BaiDuYunPush.VEHICLE,delegator,BaiDuYunPush.VEHICLE_APPROVE,genericValue.get("destination"),"车辆审批标题")
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

/**
 * 审核车辆预约信息
 * @return
 */
public Map<String, Object> saveAuditVehicleOrder() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String orderId = context.get("orderId");
    String reviewRemarks = context.get("reviewRemarks");
    String reviewState = context.get("reviewState");
    String msg = "";
    if (UtilValidate.isNotEmpty(orderId)){
        Map map = new HashMap();
        map.put("orderId", orderId);
        map.put("reviewDate", context.get("reviewDate"));
        map.put("reviewRemarks", reviewRemarks);
        map.put("reviewState", reviewState);
        delegator.store(delegator.makeValidValue("TblVehicleOrder",map));
        msg = "审核完成";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

/**
 * 车辆资源安排
 * @return
 */
public Map<String, Object> saveArrangeVehicleOrder() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String orderId = context.get("orderId");
    String arrangeRemarks = context.get("arrangeRemarks");
    String reviewState = context.get("reviewState");
    String msg = "";
    if (UtilValidate.isNotEmpty(orderId)){
        Map map = new HashMap();
        map.put("orderId", orderId);
        map.put("arrangeDate", context.get("arrangeDate"));
        map.put("arrangeRemarks", arrangeRemarks);
        map.put("reviewState", reviewState);
        delegator.store(delegator.makeValidValue("TblVehicleOrder",map));
        msg = "安排成功";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String, Object> saveLeadInstructions() {
    workLogId = context.get("workLogId");
    Map<String,Object> result = SeicrveUtil.returnSuccess();
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
    String partyIdFrom = context.get("userLogin").get("partyId");
    String partyIdTo = context.get("partyId")
    String ifAttentionButton = context.get("ifAttentionButton")
    if (ifAttentionButton=="关注"){
        try{
            genericValue = delegator.makeValue("TblFocusUnderling",UtilMisc.toMap("partyIdFrom", partyIdFrom));
            genericValue.setString("partyIdTo",partyIdTo)
            genericValue.create();
            msg = "关注成功"
        }catch (GenericEntityException e){
            e.printStackTrace();
            msg = "关注失败"
        }
    }else if("取消关注"){
        try{
            genericValue = delegator.removeByAnd("TblFocusUnderling",UtilMisc.toMap("partyIdFrom", partyIdFrom,"partyIdTo",partyIdTo));
            msg = "取消关注成功"
        }catch (GenericEntityException e){
            e.printStackTrace();
            msg = "取消关注失败"
        }
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    result.put("data",msg)
    return result;
}