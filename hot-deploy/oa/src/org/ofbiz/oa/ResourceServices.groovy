package org.ofbiz.oa

import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

//查询固定资产与其他资源list
public Map<String, Object> findResource(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    resourceListForNew = delegator.findByAnd("TblResourceManagement",UtilMisc.toMap("resourceUseState","USING"));
    resourceListForFixedAssets = delegator.findByAnd("TblFixedAssets",UtilMisc.toMap("reserveStatus","FIXED_ASSETS_RESERVE_STATUS_1"));
    List<Map> resourceList = new ArrayList<>();
    if(UtilValidate.isNotEmpty(resourceListForNew)){
        for(Map map : resourceListForNew){
            Map map1 = new HashMap();
            map1.put("resourceId", "3_" + map.get("resourceId"));
            map1.put("resourceName", "[其他资源]" + map.get("resourceName"));
            map1.put("createDate", map.get("createDate"));
            resourceList.add(map1);
        }
    }
    if(UtilValidate.isNotEmpty(resourceListForFixedAssets)){
        for(Map map : resourceListForFixedAssets){
            Map map1 = new HashMap();
            map1.put("resourceId", "1_" + map.get("fixedAssetsId"));
            map1.put("resourceName", "[固定资产]" + map.get("fixedAssetsName"));
            map1.put("createDate", map.get("inputDate"));
            resourceList.add(map1);
        }
    }
    Map map = new HashMap();
    map.put("resource", resourceList);
    successResult.put("dataResource", map);
    return successResult;
}

public Map<String, Object> saveResource() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String resourceId = context.get("resourceId");
    String msg = "保存资源成功";
    if (UtilValidate.isNotEmpty(resourceId)){
        msg = "更新资源成功"
        genericValue = delegator.findByPrimaryKey("TblResourceManagement",UtilMisc.toMap("resourceId",resourceId));
        genericValue.setNonPKFields(context);
        genericValue.store();
    }else {
        resourceId = delegator.getNextSeqId("TblResourceManagement").toString();
        context.put("createDate",new java.sql.Date(new Date().getTime()));
        genericValue = delegator.makeValidValue("TblResourceManagement",UtilMisc.toMap("resourceId",resourceId));
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
public Map<String, Object> deleteResource() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String resourceId = context.get("resourceId");
    String msg = "删除资源成功";
    try {
        delegator.removeByAnd("TblResourceManagement", UtilMisc.toMap("resourceId", resourceId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
public Map<String, Object> saveOrderResource() {
    String orderId = context.get("orderId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String dateStringStart = context.get("startDate");
    String dateStringEnd = context.get("endDate");
    String string = context.get("resourceId");
    String source = string.substring(0,1);
    String resourceId = string.substring(2);
    String msg = "";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    List list = new ArrayList();
    List<GenericValue> resourceOrderList = delegator.findAll("TblResourceOrder",false);
    for(GenericValue map : resourceOrderList){
        if((map.get("reviewState") != "PERSON_THREE")
                && (map.get("source").toString().equals(source))
                && (map.get("resourceId").toString().equals(resourceId))){//过滤预约
            list.add(map);
        }
    }
    for (GenericValue map : list){
        Timestamp dateStart = map.getTimestamp("startDate");
        Timestamp dateEnd = map.getTimestamp("endDate");

        int compare1 = dateStringEndTimestamp.compareTo(dateStart);
        int compare2 = dateStringStartTimestamp.compareTo(dateEnd);
        if((compare1 == 1 || compare1 == 0) && (compare2 == -1 || compare2 == 0)){
            msg ="所选时间与其他预约有冲突，请重新预约!";
            flag=true;
        }
    }
    if (!flag){
        msg = "预约资源提交成功";
        if (UtilValidate.isNotEmpty(orderId)){
            msg = "更新预约资源成功"
            genericValue = delegator.findByPrimaryKey("TblResourceOrder",UtilMisc.toMap("orderId",orderId));
            genericValue.setNonPKFields(context);
            genericValue.store();
        }else {
            Date startUtilDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateString);
            Date endUtilDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateString);
            java.sql.Date startSqlDate = new java.sql.Date(startUtilDate.getTime());
            java.sql.Date endSqlDate = new java.sql.Date(endUtilDate.getTime());
            orderId = delegator.getNextSeqId("TblResourceOrder").toString();
            genericValue = delegator.makeValidValue("TblResourceOrder",UtilMisc.toMap("orderId",orderId));
            genericValue.setNonPKFields(context);
            genericValue.set("source", source);//资源来源
            genericValue.set("resourceId", resourceId);//资源
            genericValue.set("startDateString",startDateString);
            genericValue.set("startDateFor",startSqlDate);
            genericValue.set("endDateString",endDateString);
            genericValue.set("endDateFor",endSqlDate);
            genericValue.set("reviewState","PERSON_ONE");//未审核状态
            genericValue.create();
        }
        BaiDuYunPush.setMessageData(orderId,genericValue,"2",BaiDuYunPush.RESOURCE,delegator,BaiDuYunPush.RESOURCE_APPROVE,genericValue.get("useReason"),"资源审批标题");
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));

    return successResult;
}
public Map<String, Object> saveAuditResourceOrder() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String orderId = context.get("orderId");
    String auditDate = context.get("auditDate");
    String auditRemarks = context.get("auditRemarks");
    String reviewState = context.get("reviewState");
    String auditPerson = userLogin.get("partyId");
    String msg = "";
    if (UtilValidate.isNotEmpty(orderId)){
        Map map = new HashMap();
        map.put("orderId", orderId);
        map.put("auditDate", context.get("auditDate"));
        map.put("auditRemarks", auditRemarks);
        map.put("reviewState", reviewState);
        delegator.store(delegator.makeValidValue("TblResourceOrder",map));
        msg = "审核完成";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
public Map<String, Object> saveArrangeResourceOrder() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String orderId = context.get("orderId");
    String arrangeRemarks = context.get("arrangeRemarks");
    String reviewState = context.get("reviewState");
    String arrangePerson = userLogin.get("partyId");
    String msg = "";
    if (UtilValidate.isNotEmpty(orderId)){
        Map map = new HashMap();
        map.put("orderId", orderId);
        map.put("arrangeDate", context.get("arrangeDate"));
        map.put("arrangeRemarks", arrangeRemarks);
        map.put("reviewState", reviewState);
//        map.put("arrangePerson", arrangePerson);
        delegator.store(delegator.makeValidValue("TblResourceOrder",map));
        msg = "安排成功";
        orderMap = delegator.findByPrimaryKey("TblResourceOrder", UtilMisc.toMap("orderId", orderId));
        if(orderMap.get("source").equals("1")){
            Map assetsUseInfo = new HashMap();
            assetsUseInfoId = delegator.getNextSeqId("TblAssetsUseInfo");
            assetsUseInfo.put("assetsUseInfoId", assetsUseInfoId);
            assetsUseInfo.put("fixedAssetsId", orderMap.get("resourceId"));
            assetsUseInfo.put("usePerson", orderMap.get("orderPerson"));
            assetsUseInfo.put("useDepartment", orderMap.get("orderDepartment"));
            assetsUseInfo.put("startDate", orderMap.get("startDateFor"));
            assetsUseInfo.put("endDate", orderMap.get("endDateFor"));
            assetsUseInfo.put("inputPerson", arrangePerson);
            assetsUseInfo.put("inputDate", new java.sql.Date(new Date().getTime()));
            delegator.create(delegator.makeValidValue("TblAssetsUseInfo", assetsUseInfo));
        }
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;

}

public Map<String, Object> findResourceList(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    String partyId = userLogin.get("partyId");
    Map map1 = new HashMap();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //未审核状态的资源预约
    List conditionList = new ArrayList();
    conditionList.add(EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "未审核"));
    conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("auditPersonType", EntityOperator.EQUALS, partyId),
                                                      EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partyId)], EntityOperator.OR));
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<Map> list = EntityQuery.use(delegator).select().from("ResourceOrderDetail").orderBy("startDate DESC").where(condition).queryList();
    transferList(list);
    //已审核状态的资源预约
    List conditionList1 = new ArrayList();
    conditionList1.add(EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "已审核"));
    conditionList1.add(EntityCondition.makeCondition([EntityCondition.makeCondition("arrangePersonType", EntityOperator.EQUALS, partyId),
                                                      EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partyId)], EntityOperator.OR));
    EntityConditionList condition1 = EntityCondition.makeCondition(UtilMisc.toList(conditionList1));
    List<Map> list1 = EntityQuery.use(delegator).select().from("ResourceOrderDetail").orderBy("startDate DESC").where(condition1).queryList();
    transferList(list1);
    //今日资源使用情况
    java.sql.Date todayDate = new java.sql.Date(new java.util.Date().getTime());
    todayForSql = UtilDateTime.toSqlDate(todayDate.toString(), "yyyy-MM-dd");
    List conditionList2 = new ArrayList();
    conditionList2.add(EntityCondition.makeCondition("startDateFor", EntityOperator.LESS_THAN_EQUAL_TO, todayDate));
    conditionList2.add(EntityCondition.makeCondition("endDateFor", EntityOperator.GREATER_THAN_EQUAL_TO, todayDate));
    conditionList2.add(EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "已安排"));
    conditionList2.add(EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partyId));
    EntityConditionList condition2 = EntityCondition.makeCondition(UtilMisc.toList(conditionList2));
    List<Map> list2 = EntityQuery.use(delegator).select().from("ResourceOrderDetail").orderBy("startDate DESC").where(condition2).queryList();
    transferList(list2);
    Map<String,Object> resource = dispatcher.runSync("findResource",UtilMisc.toMap("userLogin",userLogin));
    map1.put("resource", resource.get("dataResource"));
    map1.put("listPendingAuditResource", list);
    map1.put("ListPendingArrangedResource", list1);
    map1.put("ListTodayUseResource", list2);
    successResult.put("dataResourceList", map1);
    return successResult;
}
public List<Map> transferList(List<Map> list){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if(UtilValidate.isNotEmpty(list)){
        for(Map map : list){
            //判断预约资源的来源
            if(map.get("source").toString().equals("3")){
                resourceMap = delegator.findByPrimaryKey("TblResourceManagement",UtilMisc.toMap("resourceId", map.get("resourceId")));
                map.put("resourceId", "[其他资源]" + resourceMap.get("resourceName"));
            }
            if(map.get("source").toString().equals("1")){
                fixedAssetsMap = delegator.findByPrimaryKey("TblFixedAssets",UtilMisc.toMap("fixedAssetsId", map.get("resourceId")));
                map.put("resourceId", "[固定资产]" + fixedAssetsMap.get("fixedAssetsName"));
            }
            map.put("startDate",format.format(map.get("startDate")));
            map.put("endDate",format.format(map.get("endDate")));
        }
    }
    return list;
}


public Map<String, Object> findResourceById(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Map map = delegator.findByPrimaryKey("ResourceOrderDetail",UtilMisc.toMap("orderId",orderId));//根据id查找预约资源详情
    //判断预约资源的来源
    if(map.get("source").toString().equals("3")){
        resourceMap = delegator.findByPrimaryKey("TblResourceManagement",UtilMisc.toMap("resourceId", map.get("resourceId")));
        map.put("source", "[其他资源]" + resourceMap.get("resourceName"));
    } else if (map.get("source").toString().equals("1")){
        fixedAssetsMap = delegator.findByPrimaryKey("TblFixedAssets",UtilMisc.toMap("fixedAssetsId", map.get("resourceId")));
        map.put("source", "[固定资产]" + fixedAssetsMap.get("fixedAssetsName"));
    }
    map.put("startDate",format.format(map.get("startDate")));
    map.put("endDate",format.format(map.get("endDate")));
    if(UtilValidate.isNotEmpty(map.get("auditDate"))){
        map.put("auditDate",format.format(map.get("auditDate")));
    }
    if(UtilValidate.isNotEmpty(map.get("arrangeDate"))){
        map.put("arrangeDate",format.format(map.get("arrangeDate")));
    }
    successResult.put("data", map);
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