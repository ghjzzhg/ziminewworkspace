package org.ofbiz.oa

import com.ibm.icu.util.GenderInfo
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

public Map searchLookupEmployee(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;
    String name = context.get("name")==null?"":context.get("name");
    String fullName = context.get("fullName")==null?"":context.get("fullName");
    String groupId = context.get("lookUpGroupName")==null?"":context.get("lookUpGroupName");
    String occupationId = context.get("lookUpOccupation")==null?"":context.get("lookUpOccupation");
    String groupName = context.get("showlookUpGroupName")==null?"":context.get("showlookUpGroupName");
    String occupationName = context.get("showlookUpOccupation")==null?"":context.get("showlookUpOccupation");
    List conditionList = new ArrayList();
    if(UtilValidate.isNotEmpty(fullName.trim())){
        conditionList.add(EntityCondition.makeCondition("fullName", EntityOperator.LIKE, "%"+fullName.trim()+"%"));
    }
    if(UtilValidate.isNotEmpty(groupId.trim())){
        conditionList.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS,groupId.trim()));
    }else if(UtilValidate.isNotEmpty(groupName.trim())){
        conditionList.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%"+groupName.trim()+"%"));
    }
    if(UtilValidate.isNotEmpty(occupationId.trim())){
        conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, occupationId.trim()));
    }else if(UtilValidate.isNotEmpty(occupationName.trim())){
        conditionList.add(EntityCondition.makeCondition("postName", EntityOperator.LIKE, "%"+occupationName.trim()+"%"));
    }
    EntityListIterator staff = EntityQuery.use(delegator).select().from("StaffInformationDetailView").where(conditionList).queryIterator();
    totalCount = staff.getResultsSizeAfterPartialList();
    List<GenericValue>  staffList = staff.getPartialList(lowIndex, viewSize);
    staff.close();
    for(GenericValue genericValue : staffList){
        String group = genericValue.get("groupName");
        String occ = genericValue.get("postName");
        String jobState = genericValue.get("jobState");
        if(!UtilValidate.isNotEmpty(group) || "Not Applicable".equals(group)){
            genericValue.put("groupName","");
        }
        if(!UtilValidate.isNotEmpty(occ) || "Not Applicable".equals(occ) || "Employee".equals(occ)){
            genericValue.put("postName","");
        }
    }
    Map map =  new HashMap();
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("name",name);
    map.put("employeeList",staffList);
    successResult.put("data",map);
    return successResult;
}