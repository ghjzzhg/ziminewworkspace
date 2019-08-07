import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

String workGroups = parameters.get("groupValues");
if(UtilValidate.isNotEmpty(workGroups)){
    String[] workGroupAttr = workGroups.split(",");
    List<Map<String,Object>> groupList = FastList.newInstance();
    for(int i = 0 ; i < workGroupAttr.length; i++){
        Map<String,Object> valueMap = FastMap.newInstance();
        GenericValue genericValue = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",workGroupAttr[i]),false);
        valueMap.put("partyId",genericValue.get("partyId"));
        valueMap.put("groupName",genericValue.get("groupName"));
        valueMap.put("order",i + 1);
        groupList.add(valueMap);
    }
    context.groupList = groupList;
    context.groupListSize = groupList.size();
}
