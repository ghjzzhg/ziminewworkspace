import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

List<Map<String, Object>> relatedTicketList = new ArrayList<>();

context.relatedTickets = relatedTicketList;
//找到当前用户的roleTypeId
String partyId = context.get("userLogin").get("partyId");
List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
List<EntityCondition> conditionList = new ArrayList<>();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_OWNER"));
GenericValue partyRole = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition(conditionList)).queryOne();
if (partyRole != null) {
    String roleTypeId = partyRole.get("roleTypeId");
    //changed by galaxypan@2017-08-07:（合伙人）主账户已有对应的case 角色
    /*if(roleTypeId.equals("CASE_ROLE_PARTNER"))
    {
        GenericValue userType = EntityQuery.use(delegator).from("BasicPartnerInfo").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId),EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%")).queryOne();
        roleTypeId = userType.get("partnerType");
    }*/
    String groupId = partyRole.get("partyId")
    GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where(UtilMisc.toMap("partyId",groupId)).queryOne();
    groupId = partyGroup.get("groupName");
//找到与当前roleTypeId相关的发布信息
//    List<EntityCondition> conditions = new ArrayList<>();
//    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));//机构类型相同
//    conditions.add(EntityCondition.makeCondition("endTime", EntityOperator.GREATER_THAN, new java.sql.Date(new Date().getTime())));
//未过时的信息
//TODO 没用到？ //changed by galaxypan@2017-10-11:未发现引用到此查询结果的地方
//    List<GenericValue> relatedTickets = delegator.findList("RoleTypeTickets", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-startTime"), null, false);
//找到当前roleTypeId的竞选情况
    List condition = new ArrayList();
    condition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
    condition.add(EntityCondition.makeCondition("hasdelete", EntityOperator.EQUALS, null));
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String newDate = format.format(date);
    java.sql.Date sqlDate = new java.sql.Date((format.parse(newDate)).getTime());
    condition.add(EntityCondition.makeCondition("endTime", EntityOperator.GREATER_THAN_EQUAL_TO, sqlDate));
    List<GenericValue> ticketParties = EntityQuery.use(delegator).from("TicketCondition").where(condition).distinct().queryList();
//    List<GenericValue> ticketPartiess = EntityQuery.use(delegator).from("TicketCondition").where(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId),EntityOperator.OR,EntityCondition.makeCondition("partnerType",EntityOperator.EQUALS,roleTypeId))).queryList();
    Map<String, List<GenericValue>> ticketMap = ticketParties.groupBy { x -> x.get("ticketId") }

    for (String key : ticketMap.keySet()) {
        List<GenericValue> list = ticketMap.get(key)
        Map<String, Object> map = new HashMap();
        //过滤不是由机构发布的消息
        for (GenericValue genericValue : list) {
            String ticketId = genericValue.get("ticketId");
            GenericValue aticket = EntityQuery.use(delegator).from("TblTicket").where(UtilMisc.toMap("id",ticketId)).queryOne();
            String ticketPartyId = aticket.getString("ticketPartyId");
            GenericValue party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", ticketPartyId)).queryOne();
            if(UtilValidate.isNotEmpty(party)){
                if(!party.get("partyTypeId").equals("PARTY_GROUP")){
                    continue;
                }
            }else{
                continue;
            }
            List<GenericValue> ticketRoles = new ArrayList<>();
            List<GenericValue> ticket = EntityQuery.use(delegator).from("TblTicketRolesParty").where(UtilMisc.toMap("id",genericValue.get("rolesId"))).queryList();
            ticketRoles = EntityQuery.use(delegator).from("TblTicketRolesParty").where(UtilMisc.toMap("id",genericValue.get("rolesId"),"groupId",groupId)).queryList();
            if(UtilValidate.isNotEmpty(ticketRoles) && ticketRoles.size() > 0 || UtilValidate.isEmpty(ticket)){
                map.put("result", "AVAILABLE");
                map.put("groupName", genericValue.getString("groupName"));
                map.put("ticketContent", genericValue.getString("ticketContent"));
                map.put("startTime", genericValue.getDate("startTime"));
                map.put("endTime", genericValue.getString("endTime"));
                String title = genericValue.getString("title");
//                if(title.length()>=8)
//                {
//                    title = title.substring(0,8) + "...";
//                }
                map.put("title", title);
                map.put("ticketId", genericValue.getString("ticketId"));
                map.put("roleTypeId", roleTypeId);
                map.put("type", genericValue.getString("type"));
                map.put("typeName", genericValue.getString("typeName"));
                if (context.get("userLogin").get("partyId").equals(genericValue.get("partyId"))) {
                    map.putAll(genericValue);
                    if (UtilValidate.isNotEmpty(genericValue.get("joinDate"))) {
                        map.put("result", "SUCCESS");
                    } else {
                        map.put("result", "ONGOING");
                    }
                    break;
                }
                relatedTicketList.add(map);
            }
        }
    }
}