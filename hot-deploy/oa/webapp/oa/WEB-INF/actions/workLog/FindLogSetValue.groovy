package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService

GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
department = EntityQuery.use(delegator)
        .select("department")
        .from("TblStaff")
        .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.get("userLogin").get("partyId")))
        .queryOne();
//returnMap = delegator.findAll("TblLogSet",false);
returnMap = EntityQuery.use(delegator)
.select()
.from("TblLogSet")
.where(EntityCondition.makeCondition("department",department.get("department")))
.queryList();
resultMap = [:];
if (returnMap.size()!=0&&returnMap!=null){
    resultMap = returnMap[0];
}
resultListForLogin = new ArrayList<>();
/*positionMapForPerson = delegator.findByAnd("StaffPositionDetailView", UtilMisc.toMap("memberId", parameters.get("userLogin").get("partyId")))*/
/*positionMapForPerson = delegator.findByAnd("StaffPositionDetailView", UtilMisc.toMap("memberId", "demooa"))*/
positionMapForPerson = delegator.findByAnd("RoleType",UtilMisc.toMap("parentTypeId", "JOB_POSITION"));
resultListForLowerOccupation = new ArrayList<>();
for (Map map : positionMapForPerson) {
    String roleTypeId = map.get("roleTypeId");
    String positionId = "Company,"+roleTypeId;
    String template;
    Map maps=EntityQuery.use(delegator).from("TblWorkLogTemplate").where(UtilMisc.toMap("positionId",positionId)).queryOne();
    if(UtilValidate.isNotEmpty(maps)){
        template=maps.get("template")
    }else{
        template="";
    }
    resultMapFor = [:];
    resultMapFor.put("position",map.get("description"));
    resultMapFor.put("positionId",positionId);
    resultMapFor.put("template",template);
    resultListForLogin.add(resultMapFor);
    /*
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
     resultMapForUser = delegator.findByAnd("TblWorkLogTemplate",UtilMisc.toMap("positionId",positionId));
        if (resultMapForUser.size()!=0){
            template = resultMapForUser[0].get("template");
        }
    }else {
        template = resultMapForUser[0].get("template");
    }
    resultMapFor = [:];
    resultMapFor.put("position",map.get("description"));
    resultMapFor.put("positionId",map.get("positionId"));
    resultMapFor.put("departmentName",map.get("groupName"));
    resultMapFor.put("template",template);
    resultListForLogin.add(resultMapFor);*/
}
resultListForTemplate = new ArrayList<>();

for(Map map:resultListForLowerOccupation){
    String positionId = map.get("positionId");
    resultMapForTemplate = delegator.findByAnd("TblWorkLogTemplate",UtilMisc.toMap("positionId",positionId));
    String template = "";
    if(resultMapForTemplate.size()!=0){
         template = resultMapForTemplate[0].get("template");
        if (template==null){
            template = "";
        }
    }
    resultMapFor = [:];
    resultMapFor.put("position",map.get("description"));
    resultMapFor.put("positionId",map.get("positionId"));
    resultMapFor.put("departmentName",map.get("departmentName"));
    resultMapFor.put("template",template);
    resultListForTemplate.add(resultMapFor);
}
context.resultMap = resultMap;
context.department = department;
context.resultListForTemplate = resultListForTemplate;
context.resultListForLogin = resultListForLogin;

