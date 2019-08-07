import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

List<Map<String, Object>> relatedTicketList = new ArrayList<>();

context.relatedTickets = relatedTicketList;
//找到当前用户的roleTypeId
String partyId = context.get("userLogin").get("partyId");
List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
List<EntityCondition> conditionList = new ArrayList<>();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, groupId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_OWNER"));
GenericValue partyRole = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition(conditionList)).queryOne();
if (partyRole != null) {
    String roleTypeId = partyRole.get("roleTypeId");
    String groupName = partyRole.getString("groupName");
    String partnerGroupName = partyRole.getString("partnerGroupName");
    List<String> searchByGroupNames = new ArrayList<>();
    searchByGroupNames.add(groupName);
    //如果是合伙人，除了查询针对合伙机构的信息，还要查询直接指定到合伙人的信息
    if(UtilValidate.isNotEmpty(partnerGroupName)){
        searchByGroupNames.add(partnerGroupName);
    }
//找到与当前roleTypeId相关的发布信息：最新、相同角色类型尚未确定参与方的、未指定参与方或指定参与方为己方的
    List<EntityCondition> conditions = new ArrayList<>();
    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));//机构类型相同
    conditions.add(EntityCondition.makeCondition("endTime", EntityOperator.GREATER_THAN, new java.sql.Date(new Date().getTime())));
    conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null));
    conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("candidatePartyName", EntityOperator.EQUALS, null),
            EntityJoinOperator.OR,
            EntityCondition.makeCondition("candidatePartyName", EntityOperator.IN, searchByGroupNames)));

    EntityQuery ticketQuery = EntityQuery.use(delegator).from("RoleTypeTickets").where(conditions).orderBy("-startTime");
    if(context.maxRows != null){
        ticketQuery = ticketQuery.maxRows(context.maxRows);
    }
    List<GenericValue> latestTickets = ticketQuery.queryList();

    if(UtilValidate.isNotEmpty(latestTickets)){
//找到当前roleTypeId的竞选情况
        List<GenericValue> typeEnums = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId", "CASE_CATEGORY").cache().queryList();
        Map<String, GenericValue> typeEnumsMap = typeEnums.collectEntries{it -> [it.getString("enumId"), it]};

        List<String> ticketIds = latestTickets.collect{it -> it.getString("id")};
        List<GenericValue> campainParties = EntityQuery.use(delegator).from("TblTicketParties").where(EntityCondition.makeCondition("ticketId", EntityOperator.IN, ticketIds)).queryList();
        Map<String, List<GenericValue>> campainPartiesMap = new HashMap<>();
        if (UtilValidate.isNotEmpty(campainParties)) {
            campainPartiesMap = campainParties.groupBy{it -> it.getString("ticketId")};
        }
        for (GenericValue genericValue : latestTickets) {
            Map<String, Object> map = new HashMap();
            map.put("result", "AVAILABLE");
            map.put("groupName", genericValue.getString("groupName"));
            map.put("ticketContent", genericValue.getString("description"));
            map.put("startTime", genericValue.getDate("startTime"));
            map.put("endTime", genericValue.getString("endTime"));
            String title = genericValue.getString("title");
            map.put("title", title);
            map.put("ticketId", genericValue.getString("id"));
            map.put("roleTypeId", roleTypeId);
            map.put("type", genericValue.getString("type"));
            map.put("typeName", typeEnumsMap.get(map.get("type")).getString("description"));
            List<GenericValue> joinedParties = campainPartiesMap.get(map.get("ticketId"));
            if(joinedParties != null && UtilValidate.isNotEmpty(joinedParties)){
                for (GenericValue joinedParty : joinedParties) {
                    if(partyId.equals(joinedParty.getString("partyId"))){
                        if(joinedParty.get("joinDate") != null){
                            map.put("result", "SUCCESS");
                        }else {
                            map.put("result", "ONGOING");
                        }
                        break;
                    }
                }
            }
            relatedTicketList.add(map);
        }
    }
}