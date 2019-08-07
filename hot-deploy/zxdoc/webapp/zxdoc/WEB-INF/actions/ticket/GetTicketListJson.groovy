package ticket

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

/**
 * Created by REXTEC-15-3 on 2016/9/10.
 */
String partyId = context.get("userLogin").get("partyId");
//List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId,"partyIdTo", partyId), null, false);
//partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("TblTicket").where(EntityCondition.makeCondition("ticketPartyId", EntityOperator.EQUALS, partyId),EntityCondition.makeCondition("hasdelete", EntityOperator.EQUALS, null)).orderBy(UtilMisc.toList("-createdStamp")).cache(), parameters);
List<GenericValue> ticketList = result.data;
List<Map> tickets = new ArrayList<>();
for(GenericValue ticket: ticketList){
    Map<String, Object> ticketMap = new HashMap<>();
    ticketMap.putAll(ticket);
    String ticketId = ticket.get("id");
    List<Map> ticketRoleList = new ArrayList<>();
    List<GenericValue> ticketRoles = delegator.findByAnd("RoleDescriptionPartyName", UtilMisc.toMap("ticketId", ticketId), null, false);
    //查询该ticket所有的竞选记录
    List<GenericValue> ticketParties = delegator.findByAnd("TblTicketParties", UtilMisc.toMap("ticketId", ticketId), null, false);
    Map<String, List<GenericValue>> ticketPartyMap = ticketParties.groupBy {x -> x.get("roleTypeId")}
    int successNum = 0;
    for(GenericValue ticketRole: ticketRoles){
        Map map = new HashMap();
        String roleTypeId = ticketRole.get("roleTypeId");//参与类型id
        String description = ticketRole.get("description");//参与类型名称
        String rolePartyId = ticketRole.get("partyId");//已成功竞选的参与方id
        String groupName = ticketRole.get("groupName");//已成功竞选的参与方id
        List<GenericValue> ticketRolePartyList = ticketPartyMap.get(roleTypeId);
        map.put("roleTypeId", roleTypeId);
        map.put("description", description);
        if(UtilValidate.isNotEmpty(rolePartyId)){
            map.put("status", "SUCCESS");
            successNum ++;
        }else if(UtilValidate.isNotEmpty(ticketRolePartyList)){
            map.put("status", "ONGOING");
        }else {
            map.put("status", "NONE")
        }
        map.put("groupName", groupName);
        ticketRoleList.add(map);
    }
    if(ticketRoles.size() > 0 && successNum == ticketRoles.size()){
        ticketMap.put("ticketStatus", "complete");
    }else {
        ticketMap.put("ticketStatus", "unComplete");
    }
    ticketMap.put("ticketRoleList", ticketRoleList);
    tickets.add(ticketMap);
}
request.setAttribute("data", tickets);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
