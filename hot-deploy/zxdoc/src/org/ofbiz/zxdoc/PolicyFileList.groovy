package org.ofbiz.zxdoc

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

String fileName = parameters.get("fileName");
String policySearchOne = parameters.get("policySearchOne");
String policySearchTwo = parameters.get("policySearchTwo");
List list = new ArrayList();
if(UtilValidate.isNotEmpty(fileName)){
    list.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.LIKE, "%" + fileName + "%"));
}
if(UtilValidate.isNotEmpty(policySearchOne)){
    list.add(EntityCondition.makeCondition("category", EntityOperator.LIKE, "%" + policySearchOne + "%"));
}
if(UtilValidate.isNotEmpty(policySearchTwo)){
    list.add(EntityCondition.makeCondition("category", EntityOperator.EQUALS, policySearchTwo));
}
//查询政策文件信息，根据创建时间排序
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PolicyFileList").where(list).orderBy("createdDate DESC"), parameters);
//如果政策文件信息中没有二级类别，另做处理
for(GenericValue genericValue : result.data){
    if(genericValue.get("enumTypeId").equals("POLICY_CATEGORY")){
        genericValue.put("policyTypeOne",genericValue.get("policyTypeTwo"));
        genericValue.put("policyTypeTwo","");
    }
}
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);
