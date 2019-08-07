package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.service.ModelService

workLogId = parameters.get("workLogId");
GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
returnMap = delegator.findByPrimaryKey("TblWorkLog",UtilMisc.toMap("workLogId", workLogId));
positionMapForPerson = delegator.findByAnd("StaffPositionDetailView", UtilMisc.toMap("memberId", parameters.get("userLogin").get("partyId")));
resultListForLowerOccupation = new ArrayList<>();
resultMapFor = [:];
for (Map map : positionMapForPerson) {
    String positionId = map.get("positionId");
    context.positionId = positionId
    resultList = runService("getLowerOccupations", dispatcher.getDispatchContext().getModelService("getLowerOccupationMembers").makeValid(context, ModelService.IN_PARAM));
    if (resultList != null && resultList.size() > 0) {
        resultListForLowerOccupation = resultList.get("data");
    }
    String template = "";
    String userLoginId = context.get("userLogin").get("userLoginId");
    resultMapForUser = delegator.findByAnd("TblWorkLogTemplate",UtilMisc.toMap("positionId",positionId,"userLoginId",userLoginId));
    if (resultMapForUser.size()==0){
        resultMapForUser = delegator.findByAnd("TblWorkLogTemplate",UtilMisc.toMap("positionId",positionId,"userLoginId","_NA_"));
        if (resultMapForUser.size()!=0){
            template = resultMapForUser[0].get("template");
        }
    }else {
        template = resultMapForUser[0].get("template");
    }
    resultMapFor.put("template",template);
}

context.returnMap = returnMap;
context.resultMapFor = resultMapFor;
