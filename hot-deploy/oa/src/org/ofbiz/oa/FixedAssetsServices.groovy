package org.ofbiz.oa

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil
public Map<String, Object> removeAssets() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String assetsId = context.get("assetsId");
    delegator.removeByAnd("FixedAssets", UtilMisc.toMap("assetsId",assetsId));
    return successResult;
}
public Map<String, Object> saveFixedAssets() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String fixedAssetsId = context.get("fixedAssetsId");
    String fixedAssetsCode1 = context.get("fixedAssetsCode1");
    String msg = "保存";
    try {
        if(UtilValidate.isNotEmpty(fixedAssetsId)){
            msg = "更新";
            delegator.store(delegator.makeValidValue("TblFixedAssets",context));
        }else{
            context.put("lendCount",0);
            context.put("lendDayCount",0);
            context.put("inputPerson",userLogin.getString("partyId"));
            context.put("inputDate",new java.sql.Date(new Date().getTime()));
            fixedAssetsId = delegator.getNextSeqId("TblFixedAssets");//获得主键
            context.put("fixedAssetsId",fixedAssetsId);
            context.put("fixedAssetsCode",fixedAssetsCode1);
            context.remove("fixedAssetsCode1");
            delegator.create(delegator.makeValidValue("TblFixedAssets",context));
        }
    }catch (Exception e){
        return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                msg+"失败",
                UtilMisc.toMap("errMessage", e.getMessage()), locale));
    }
    return successResult;
}
public Map<String, Object> lookupUserDepartment() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(partyId)){
        List<GenericValue> resultList = delegator.findByAnd("StaffDetail",UtilMisc.toMap("partyId",partyId));
        if (resultList.size()!=0){
            map = resultList.get(0);
        }
    }
    successResult.put("data",map);
    return successResult;
}
public Map<String, Object> deleteFixedAssets(){
    String fixedAssetsId = context.get("fixedAssetsId");
    genericValue = delegator.findByPrimaryKey("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId));
    if(genericValue.get("lendStatus").equals("FIXED_ASSETS_LEND_STATUS_3")){
        return ServiceUtil.returnError("资产正在使用，无法删除");
    }else {
        delegator.removeByAnd("TblFixedAssetsParts", UtilMisc.toMap("fixedAssetsId", fixedAssetsId));
        delegator.removeByAnd("TblAssetsUseInfo", UtilMisc.toMap("fixedAssetsId", fixedAssetsId));
        delegator.removeByAnd("TblAssetsRepair", UtilMisc.toMap("fixedAssetsId", fixedAssetsId));
        order = delegator.findByAnd("TblResourceOrder", UtilMisc.toMap("resourceId", fixedAssetsId, "source", "1"));
        if (UtilValidate.isNotEmpty(order)) {
            for (GenericValue g : order) {
                Map map1 = new HashMap();
                map1.put("orderId", g.get("orderId"));
                map1.put("reviewState", "PERSON_THREE");//已驳回
                delegator.store(delegator.makeValidValue("TblResourceOrder", map1));
            }
        }
        delegator.removeByAnd("TblFixedAssetsLend", UtilMisc.toMap("fixedAssetsId", fixedAssetsId));
        delegator.removeByAnd("TblFixedAssets", UtilMisc.toMap("fixedAssetsId", fixedAssetsId));
        request.setAttribute("msg", "删除成功！");
        return "success";
    }
}