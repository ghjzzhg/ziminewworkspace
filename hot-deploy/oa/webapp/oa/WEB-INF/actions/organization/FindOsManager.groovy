import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.entity.condition.EntityCondition

suppliesName = parameters.get("suppliesName");
category = parameters.get("category");
repertoryWarning = parameters.get("repertoryWarning");

conditionMap = FastMap.newInstance();
if(suppliesName!=null&& !"".equals(suppliesName)){
    conditionMap.put("suppliesName",suppliesName)
    context.suppliesName = suppliesName;
}
if(category!=null&& !"".equals(category)){
    conditionMap.put("category",category)
    context.category = category;
}
if(repertoryWarning!=null&& !"".equals(repertoryWarning)){
    conditionMap.put("repertoryWarning",repertoryWarning)
    context.repertoryWarning = repertoryWarning;
}

osManagers = delegator.findByAnd("OfficeSuppliesManagement",conditionMap);
EntityCondition


listosManagers =  FastList.newInstance();
if(osManagers!=null && osManagers.size()>0){
    for (int i=0;i<osManagers.size();i++){
        osManagerMap = [:];
        osManagerList = osManagers.get(i);
        osManagerMap.put("amount",osManagerList.get("amount"));
        osManagerMap.put("lastUpdatedStamp",osManagerList.get("lastUpdatedStamp"));
        osManagerMap.put("category",osManagerList.get("category"));
        osManagerMap.put("createdTxStamp",osManagerList.get("createdTxStamp"));
        osManagerMap.put("standard",osManagerList.get("standard"));
        osManagerMap.put("createdStamp",osManagerList.get("createdStamp"));
        osManagerMap.put("remark",osManagerList.get("remark"));
        osManagerMap.put("suppliesName",osManagerList.get("suppliesName"));
        osManagerMap.put("osManagementId",osManagerList.get("osManagementId"));
        osManagerMap.put("repertoryAmount",osManagerList.get("repertoryAmount"));
        osManagerMap.put("repertoryWarning",osManagerList.get("repertoryWarning"));
        listosManagers.add(osManagerMap);
    }
}

context.listosManagers = listosManagers;

