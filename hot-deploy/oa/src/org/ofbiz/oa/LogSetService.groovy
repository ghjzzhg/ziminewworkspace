package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

public Map<String, Object> saveLogSet() {
    logSetId = context.get("logSetId");
    id = logSetId;
    checkboxForPlan = context.get("checkboxForPlan")
    department = context.get("department")
    planText = context.get("planText")
    checkboxForLog = context.get("checkboxForLog")
    logText = context.get("logText")
    checkboxForInstructions = context.get("checkboxForInstructions")
    instructionsText = context.get("instructionsText")
    try {
        returnMap = EntityQuery.use(delegator)
                .select()
                .from("TblLogSet")
                .where(EntityCondition.makeCondition("department",department))
                .queryFirst();
        if (returnMap == null && id !=null && id != ""){
            id = "";
            logSetId = "";
        } else if (returnMap != null){
            logSetId = returnMap.get("logSetId");
            id = logSetId;
        }

        if (logSetId == "" || logSetId == null) {
            logSetId = delegator.getNextSeqId("TblLogSet").toString();
            genericValue = delegator.makeValue("TblLogSet", UtilMisc.toMap("logSetId", logSetId))
        } else {
            genericValue = delegator.findByPrimaryKey("TblLogSet", UtilMisc.toMap("logSetId", logSetId));
        }
        if (department != null) {
            genericValue.setString("department",department);
        }else {
            genericValue.setString("department","");
        }
        if (checkboxForPlan == "on" && planText != null) {
            genericValue.setString("planValue", planText)
        }else {
            genericValue.setString("planValue", "")
        }
        if (checkboxForLog == "on" && logText != null) {
            genericValue.setString("logValue", logText)
        }else {
            genericValue.setString("logValue", "")
        }
        if (checkboxForInstructions == "on" && instructionsText != null) {
            genericValue.setString("instructionsValue", instructionsText)
        }else {
            genericValue.setString("instructionsValue", "")
        }
        if (id == null || id == "") {
            genericValue.create();
        } else {
            genericValue.store();
        }
    } catch (GenericEntityException e) {
        e.printStackTrace();
    }
    data=[:];
    data.put("data", logSetId);
    return data;
}
public Map<String, Object> savePersonalTemplate() {
    positionId = context.get("positionId");
    template = context.get("template");
    /*userLoginId = context.get("userLogin").get("userLoginId");
    result = delegator.findByAnd("TblWorkLogTemplate", UtilMisc.toMap("positionId",positionId,"userLoginId",userLoginId));*/
    userLoginId = "demooa";
    result = delegator.findByAnd("TblWorkLogTemplate", UtilMisc.toMap("positionId",positionId,"userLoginId",userLoginId));
    String msg = "";
    if (result.size()==0){
        genericValue = delegator.makeValue("TblWorkLogTemplate", UtilMisc.toMap("positionId", positionId));
        genericValue.setString("userLoginId", userLoginId);
        genericValue.setString("template", template);
        genericValue.create();
        msg = "保存成功"
    }else {
        genericValue = delegator.findByPrimaryKey("TblWorkLogTemplate", UtilMisc.toMap("positionId", positionId));
        genericValue.setString("userLoginId", userLoginId);
        genericValue.setString("template", template);
        genericValue.store();
        msg = "修改成功"
    }
    data=[:];
    data.put("data", msg);
    return data;
}
public Map<String, Object> saveTemplate() {
    positionId = context.get("positionId");
    template = context.get("template");
    result = delegator.findByAnd("TblWorkLogTemplate", UtilMisc.toMap("positionId",positionId,"userLoginId","_NA_"));
    String msg = "";
    if (result.size()==0){
        genericValue = delegator.makeValue("TblWorkLogTemplate", UtilMisc.toMap("positionId", positionId));
        genericValue.setString("userLoginId", "_NA_");
        genericValue.setString("template", template);
        genericValue.create();
        msg = "保存成功"
    }else {
        genericValue = delegator.findByPrimaryKey("TblWorkLogTemplate", UtilMisc.toMap("positionId", positionId));
        genericValue.setString("userLoginId", "_NA_");
        genericValue.setString("template", template);
        genericValue.store();
        msg = "修改成功"
    }
    data=[:];
    data.put("data", msg);
    return data;
}
