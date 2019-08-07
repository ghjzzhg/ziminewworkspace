import javolution.util.FastList
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil

public Object borrowRegister(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    GenericValue fixedAssets = delegator.findOne("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId),false);
    request.setAttribute("fixedAssets",fixedAssets);
    return "success";
}
public Object saveBorrowRegister(){
    String key = EntityUtilProperties.getPropertyValue("oa.properties", "activitiFixedKey", "localhost", delegator);
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String applyInformation = request.getParameter("applyInformation");
    java.sql.Date applyDate = UtilDateTime.toSqlDate(request.getParameter("applyDate"),"yyyy-MM-dd");
    String applyPerson = request.getParameter("applyPerson");
    String fixedAssetsLendId = delegator.getNextSeqId("TblFixedAssetsLend");
    delegator.create(delegator.makeValidValue("TblFixedAssetsLend",UtilMisc.toMap("fixedAssetsLendId",fixedAssetsLendId,"fixedAssetsId",fixedAssetsId,"applyInformation",applyInformation,"applyDate",applyDate,"applyPerson",applyPerson,"lendOver","N")));
    delegator.store(delegator.makeValidValue("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId,"lendStatus","FIXED_ASSETS_LEND_STATUS_2")));
    Map<String,Object> dataMap = dispatcher.runSync("startBusinessProcess",UtilMisc.toMap("bizKey",fixedAssetsLendId,"workflowKey", key,"userLogin",userLogin));
    if(UtilValidate.isNotEmpty(dataMap)){
        String taskId = dataMap.get("taskId");
        request.setAttribute("taskId",taskId);
    }
    return "success";
}

public Object borrowAssetsConfirm(){
    DynamicViewEntity dynamicView = DynamicViewEntity.newInstance();
    dynamicView.addMemberEntity("fixedAssets","TblFixedAssets");
    dynamicView.addMemberEntity("fixedAssetsLend","TblFixedAssetsLend");
    dynamicView.addAliasAll("fixedAssets","",null);
    dynamicView.addAliasAll("fixedAssetsLend","",UtilMisc.toList("fixedAssetsId"));
    dynamicView.addViewLink("fixedAssets","fixedAssetsLend",true,UtilMisc.toList(ModelKeyMap.makeKeyMapList("fixedAssetsId")));
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    GenericValue fixedAssets = EntityQuery.use(delegator).from(dynamicView).where(EntityCondition.makeCondition(EntityCondition.makeCondition("fixedAssetsId",EntityOperator.EQUALS,fixedAssetsId),EntityJoinOperator.AND,EntityCondition.makeCondition("lendOver",EntityOperator.EQUALS,"N"))).queryOne();
    request.setAttribute("fixedAssets",fixedAssets);
    String key = EntityUtilProperties.getPropertyValue("oa.properties", "activitiFixedKey", "localhost", delegator);
    Map<String,Object> map = dispatcher.runSync("searchBusinessStatus",UtilMisc.toMap("bizKey",fixedAssets.get("fixedAssetsLendId"),"workflowKey",key,"userLogin",userLogin));
    if("success".equals(map.get("responseMessage"))){
        Map<String,Object> dataMap = map.get("data");
        request.setAttribute("formKey",dataMap.get("formKey"));
        request.setAttribute("taskId",dataMap.get("taskId"));
        request.setAttribute("nextViewType",dataMap.get("nextViewType"));
        request.setAttribute("formId",dataMap.get("formId"));
        request.setAttribute("content",dataMap.get("formContent"));
    }
    return "success";
}
public Object saveBorrowAssetsConfirm(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String fixedAssetsLendId = request.getParameter("fixedAssetsLendId");
    java.sql.Date assetLendDate = UtilDateTime.toSqlDate(request.getParameter("assetLendDate"),"yyyy-MM-dd");
    String assetLendAdvice = request.getParameter("assetLendAdvice");
    String assetLendOutStatus = request.getParameter("assetLendOutStatus");
    String assetLendRemarks = request.getParameter("assetLendRemarks");

    if("Y".equals(assetLendAdvice)){
        delegator.store(delegator.makeValidValue("TblFixedAssetsLend",
                UtilMisc.toMap("fixedAssetsLendId",fixedAssetsLendId,"assetLendDate",assetLendDate,"assetLendAdvice",assetLendAdvice,"assetLendOutStatus",assetLendOutStatus,"assetLendRemarks",assetLendRemarks,"lendOver","N")));
        delegator.store(delegator.makeValidValue("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId,"lendStatus","FIXED_ASSETS_LEND_STATUS_3")));
    }else {
        delegator.store(delegator.makeValidValue("TblFixedAssetsLend",
                UtilMisc.toMap("fixedAssetsLendId",fixedAssetsLendId,"assetLendDate",assetLendDate,"assetLendAdvice",assetLendAdvice,"assetLendOutStatus","FIXED_ASSETS_LEND_OUT_STATUS_3","assetLendRemarks",assetLendRemarks,"lendOver","Y")));
        delegator.store(delegator.makeValidValue("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId,"lendStatus","FIXED_ASSETS_LEND_STATUS_1")));
    }
    return "success";
}
public Object saveReturnAssets(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String fixedAssetsLendId = request.getParameter("fixedAssetsLendId");
    java.sql.Date assetReturnDate = UtilDateTime.toSqlDate(request.getParameter("assetReturnDate"),"yyyy-MM-dd");
    String assetReturnStatus = request.getParameter("assetReturnStatus");
    String assetReturnPerson = request.getParameter("assetReturnPerson");
    String assetReturnRemarks = request.getParameter("assetReturnRemarks");
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String inputPerson = userLogin.get("partyId");


    GenericValue fixedAssetsLendGen = delegator.findOne("TblFixedAssetsLend",UtilMisc.toMap("fixedAssetsLendId",fixedAssetsLendId),false);

    fixedAssetsLendGen.put("assetReturnDate",assetReturnDate);
    fixedAssetsLendGen.put("assetReturnStatus",assetReturnStatus);
    fixedAssetsLendGen.put("assetReturnPerson",assetReturnPerson);
    fixedAssetsLendGen.put("assetReturnRemarks",assetReturnRemarks);
    fixedAssetsLendGen.put("lendOver","Y");
    delegator.store(fixedAssetsLendGen);
    GenericValue assetsGen = delegator.findOne("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId),false);
    int lendCount = assetsGen.getInteger("lendCount");
    assetsGen.put("lendCount",lendCount + 1);
    int lendDayCount = (int)((fixedAssetsLendGen.getDate("assetReturnDate").getTime() - fixedAssetsLendGen.getDate("assetLendDate").getTime())/(24*60*60*1000));
    assetsGen.put("lendDayCount",assetsGen.getInteger("lendDayCount") + lendDayCount);
    assetsGen.put("lendStatus","FIXED_ASSETS_LEND_STATUS_1");
    delegator.store(assetsGen);
    orderMap = delegator.findByPrimaryKey("TblFixedAssetsLend", UtilMisc.toMap("fixedAssetsLendId", fixedAssetsLendId));
    Map assetsUseInfo = new HashMap();
    assetsUseInfoId = delegator.getNextSeqId("TblAssetsUseInfo");
    assetsUseInfo.put("assetsUseInfoId", assetsUseInfoId);
    assetsUseInfo.put("fixedAssetsId", orderMap.get("fixedAssetsId"));
    assetsUseInfo.put("usePerson", orderMap.get("applyPerson"));
    staffMap = delegator.findByPrimaryKey("TblStaff", UtilMisc.toMap("partyId", orderMap.get("applyPerson")));
    if(UtilValidate.isNotEmpty(staffMap.get("department"))){
        assetsUseInfo.put("useDepartment", staffMap.get("department"));
    }
    assetsUseInfo.put("startDate", orderMap.get("assetLendDate"));
    assetsUseInfo.put("endDate", assetReturnDate);
    assetsUseInfo.put("inputPerson", inputPerson);
    assetsUseInfo.put("inputDate", new java.sql.Date(new Date().getTime()));
    delegator.create(delegator.makeValidValue("TblAssetsUseInfo", assetsUseInfo));
    return "success";
}

public Object assetsInfo(){
    DynamicViewEntity dynamicView = DynamicViewEntity.newInstance();
    dynamicView.addMemberEntity("fixedAssets","TblFixedAssets");
    dynamicView.addMemberEntity("fixedAssetsLend","TblFixedAssetsLend");
    dynamicView.addAliasAll("fixedAssets","",null);
    dynamicView.addAliasAll("fixedAssetsLend","",UtilMisc.toList("fixedAssetsId"));
    dynamicView.addViewLink("fixedAssets","fixedAssetsLend",true,UtilMisc.toList(ModelKeyMap.makeKeyMapList("fixedAssetsId")));
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    List<GenericValue> fixedAssets = EntityQuery.use(delegator).from(dynamicView).where(EntityCondition.makeCondition("fixedAssetsId",EntityOperator.EQUALS,fixedAssetsId)).queryList();
    if(UtilValidate.isNotEmpty(fixedAssets.get(0))){
        if(fixedAssets.get(0).get("canLendOut").equals("Y")){
            fixedAssets.get(0).put("canLendOut", "可外借");
        }else {
            fixedAssets.get(0).put("canLendOut", "不可外借");
        }
    }
    request.setAttribute("fixedAssets",fixedAssets.get(0));
    return "success";
}
public Object saveAssetsPartys(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String assetsPartsId = request.getParameter("assetsPartsId");
    String assetsPartsName = request.getParameter("assetsPartsName");
    String assetsPartsStandard = request.getParameter("assetsPartsStandard");
    String assetsPartsMaker = request.getParameter("assetsPartsMaker");
    String assetsPartsUnit = request.getParameter("assetsPartsUnit");
    int assetsPartsCount = Integer.parseInt(request.getParameter("assetsPartsCount"));
    String assetsPartsStatus = request.getParameter("assetsPartsStatus");
    String isNewPlay = request.getParameter("isNewPlay");
    String remarks = request.getParameter("remarks");
    if(UtilValidate.isNotEmpty(assetsPartsId)){
        delegator.store(delegator.makeValidValue("TblFixedAssetsParts",UtilMisc.toMap("assetsPartsId",assetsPartsId ,"fixedAssetsId",fixedAssetsId,"assetsPartsName",assetsPartsName,"assetsPartsStandard",assetsPartsStandard,
                "assetsPartsMaker",assetsPartsMaker,"assetsPartsUnit",assetsPartsUnit,"assetsPartsCount",assetsPartsCount,"assetsPartsStatus",assetsPartsStatus,"isNewPlay",isNewPlay,"remarks",remarks)));
    }else {
        assetsPartsId = delegator.getNextSeqId("TblFixedAssetsParts");
        delegator.create(delegator.makeValidValue("TblFixedAssetsParts",UtilMisc.toMap("assetsPartsId",assetsPartsId ,"fixedAssetsId",fixedAssetsId,"assetsPartsName",assetsPartsName,"assetsPartsStandard",assetsPartsStandard,
                "assetsPartsMaker",assetsPartsMaker,"assetsPartsUnit",assetsPartsUnit,"assetsPartsCount",assetsPartsCount,"assetsPartsStatus",assetsPartsStatus,"isNewPlay",isNewPlay,"remarks",remarks)));
    }

    request.setAttribute("fixedAssetsId",fixedAssetsId);
    return "success";
}
public Object saveUseInfo(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String fixedAssetsCode = request.getParameter("fixedAssetsCode");
    GenericValue userLogin = context.get("userLogin");
    String usePerson = request.getParameter("usePersonForUseInfo");
    String useDepartment = request.getParameter("useDepartmentForUseInfo");
    java.sql.Date startDate = UtilDateTime.toSqlDate(request.getParameter("startDate"),"yyyy-MM-dd");
    java.sql.Date endDate = UtilDateTime.toSqlDate(request.getParameter("endDate"),"yyyy-MM-dd");
    String assetsUseInfoId = request.getParameter("assetsUseInfoId");
    if(UtilValidate.isNotEmpty(assetsUseInfoId)){
        delegator.store(delegator.makeValidValue("TblAssetsUseInfo",UtilMisc.toMap("assetsUseInfoId",assetsUseInfoId,"fixedAssetsId",fixedAssetsId ,"usePerson",usePerson,"useDepartment",useDepartment,"startDate",startDate,"endDate",endDate)));
    }else {
        assetsUseInfoId = delegator.getNextSeqId("TblAssetsUseInfo");
        delegator.create(delegator.makeValidValue("TblAssetsUseInfo",
                UtilMisc.toMap("inputPerson",userLogin.getString("partyId"),"inputDate",new java.sql.Date(new Date().getTime()),"assetsUseInfoId",assetsUseInfoId,"fixedAssetsId",fixedAssetsId ,"usePerson",usePerson,"useDepartment",useDepartment,"startDate",startDate,"endDate",endDate)));
    }
    request.setAttribute("fixedAssetsId",fixedAssetsId);
    request.setAttribute("fixedAssetsCode",fixedAssetsCode);
    return "success";
}
public Object deleteParts(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String assetsPartsId = request.getParameter("assetsPartsId");
    delegator.removeByAnd("TblFixedAssetsParts",UtilMisc.toMap("assetsPartsId",assetsPartsId));
    request.setAttribute("fixedAssetsId",fixedAssetsId);
    return "success";
}
public Object deleteUseInfo(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String fixedAssetsCode = request.getParameter("fixedAssetsCode");
    String assetsUseInfoId = request.getParameter("assetsUseInfoId");
    delegator.removeByAnd("TblAssetsUseInfo",UtilMisc.toMap("assetsUseInfoId",assetsUseInfoId));
    request.setAttribute("fixedAssetsId",fixedAssetsId);
    request.setAttribute("fixedAssetsCode",fixedAssetsCode);
    return "success";
}
public Object deleteRepair(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String assetsRepairId = request.getParameter("assetsRepairId");
    delegator.removeByAnd("TblAssetsRepair",UtilMisc.toMap("assetsRepairId",assetsRepairId));
    request.setAttribute("fixedAssetsId",fixedAssetsId);
    return "success";
}
public Object findUseInfo(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String assetsUseInfoId = request.getParameter("assetsUseInfoId");
    if(UtilValidate.isNotEmpty(assetsUseInfoId)){
        GenericValue assetsUserInfo = delegator.findOne("TblAssetsUseInfo",UtilMisc.toMap("assetsUseInfoId",assetsUseInfoId),false);
        request.setAttribute("assetsUserInfo",assetsUserInfo);
    }
    return "success";
}
public Object findRepair(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String assetsRepairId = request.getParameter("assetsRepairId");
    if(UtilValidate.isNotEmpty(assetsRepairId)){
        GenericValue assetsRepair = delegator.findOne("TblAssetsRepair",UtilMisc.toMap("assetsRepairId",assetsRepairId),false);
        request.setAttribute("assetsRepair",assetsRepair);
    }
    return "success";
}
public Object saveAssetsRepair(){
    GenericValue userLogin = context.get("userLogin");
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    String assetsRepairId = request.getParameter("assetsRepairId");
    String repairPerson = request.getParameter("repairPerson");
    String repairContent = request.getParameter("repairContent");
    String repairDepartment = request.getParameter("repairDepartment");
    java.sql.Date repairDate = UtilDateTime.toSqlDate(request.getParameter("repairDate"),"yyyy-MM-dd");
    if(UtilValidate.isNotEmpty(assetsRepairId)){
        delegator.store(delegator.makeValidValue("TblAssetsRepair",UtilMisc.toMap("assetsRepairId",assetsRepairId,"fixedAssetsId",fixedAssetsId ,"repairPerson",repairPerson,"repairContent",repairContent,"repairDepartment",repairDepartment,"repairDate",repairDate)));
    }else {
        assetsRepairId = delegator.getNextSeqId("TblAssetsRepair");
        delegator.create(delegator.makeValidValue("TblAssetsRepair",UtilMisc.toMap("inputPerson",userLogin.getString("partyId"),"inputDate",new java.sql.Date(new Date().getTime()),"assetsRepairId",assetsRepairId,"fixedAssetsId",fixedAssetsId ,"repairPerson",repairPerson,"repairContent",repairContent,"repairDepartment",repairDepartment,"repairDate",repairDate)));
    }
    request.setAttribute("fixedAssetsId",fixedAssetsId);
    return "success";
}

public Object rejectBorrowAssetsConfirm(){
    String fixedAssetsLendId = request.getParameter("fixedAssetsLendId");
    delegator.removeByAnd("TblFixedAssetsLend", UtilMisc.toMap("fixedAssetsLendId", fixedAssetsLendId));
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    GenericValue fixedAssets = EntityQuery.use(delegator).from("TblFixedAssets").where(UtilMisc.toMap("fixedAssetsId",fixedAssetsId)).queryOne();
    fixedAssets.put("lendStatus","FIXED_ASSETS_LEND_STATUS_1");
    fixedAssets.store();
    return "success";
}
