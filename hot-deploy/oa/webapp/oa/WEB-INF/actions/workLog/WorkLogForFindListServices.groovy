package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator

GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
List resultList = new ArrayList();
partyId = parameters.get("userLogin").get("partyId");
partyIdForFind = parameters.get("partyId");
if (partyIdForFind==null){
    resultList = delegator.findByAnd("TblWorkLog", UtilMisc.toMap("partyId", partyId));
}else {
    resultList = delegator.findByAnd("TblWorkLog", UtilMisc.toMap("partyId", partyIdForFind));
}

List resultListForClone = new ArrayList();
for(Map<String,Object> map : resultList){
    mapForClone = (Map)map.clone();
    String s = mapForClone.get("workLogDate").toString();
    mapForClone.put("workLogDate",s);
    resultListForClone.add(mapForClone);
}
if (partyIdForFind==null){
    partyIdForFind="";
}
context.returnList = resultListForClone;
context.partyId = partyIdForFind;
