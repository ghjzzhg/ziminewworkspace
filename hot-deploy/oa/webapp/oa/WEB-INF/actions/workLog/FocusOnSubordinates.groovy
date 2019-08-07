import org.ofbiz.base.util.UtilMisc
import org.ofbiz.service.ModelService


positionMapForPerson = delegator.findByAnd("StaffPositionDetailView", UtilMisc.toMap("memberId", parameters.get("userLogin").get("partyId")))
resultMap = new ArrayList<>();
underlingList = new ArrayList<>();
for (Map map : positionMapForPerson) {
    String positionId = map.get("positionId");
   /* String positionId = "Company 10024";*/

    context.positionId = positionId
    resultList = runService("getLowerOccupationMembers", dispatcher.getDispatchContext().getModelService("getLowerOccupationMembers").makeValid(context, ModelService.IN_PARAM));
    if (resultList != null && resultList.size() > 0) {
        resultMap = resultList.get("data");
        attentionList = delegator.findByAnd("TblFocusUnderling", UtilMisc.toMap("partyIdFrom", parameters.get("userLogin").get("partyId")))
        for (Map map1 : resultMap) {
            underlingMap = [:];
            underlingMap.put("partyId", map1.get("partyId"))
            underlingMap.put("employeeName", map1.get("employeeName"))
            underlingMap.put("post", map1.get("post"))
            underlingMap.put("ifAttention", "false");
            underlingMap.put("ifAttentionButton", "关注");
            underlingMap.put("attentionState", "未关注");
            for (Map map2 : attentionList) {
                if (map1.get("partyId") == map2.get("partyIdTo")) {
                    underlingMap.put("ifAttention", "true");
                    underlingMap.put("ifAttentionButton", "取消关注");
                    underlingMap.put("attentionState", "已关注");
                    break;
                }
            }
            underlingList.add(underlingMap);
        }
    }
}
context.underlingList = underlingList;