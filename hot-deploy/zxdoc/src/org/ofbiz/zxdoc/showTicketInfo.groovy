package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/12/23.
 */
String caseId = parameters.caseId;
if(caseId!=null){
    GenericValue ticket = EntityQuery.use(delegator).from("TblTicket").where("caseId",caseId).queryOne();
    List list = new ArrayList();
    if(ticket!=null)
    {
        context.title= ticket.get("title");
        context.startTime = ticket.get("startTime");
        context.endTime = ticket.get("endTime");
        context.description  = ticket.get("description")==null?" ":ticket.get("description");
        String type = ticket.get("type");
        if(type.equals("other"))
        {
            context.type = "企业自建模板";
        }else
        {
            GenericValue ticketType = EntityQuery.use(delegator).from("Enumeration").where(EntityCondition.makeCondition("enumId",EntityOperator.EQUALS,type),EntityCondition.makeCondition("enumTypeId",EntityOperator.LIKE,"CASE_CATEGORY")).queryOne();
            if(ticketType!=null){
                context.type = ticketType.get("description");
            }
        }
        String templateId = ticket.get("templateId");
        GenericValue template = EntityQuery.use(delegator).from("TblCaseTemplate").where("id",templateId).queryOne();
        if(template!=null)
        {
            context.version = template.get("templateName");
        }
        List<GenericValue> groups = EntityQuery.use(delegator).from("TblTicketRoles").where("ticketId",ticket.get("id")).queryList();
        List<Map> members = new ArrayList<>();
        for (GenericValue group:groups)
        {
            Map map = new HashMap();
            String roleTypeId = group.get("roleTypeId");
            String id = group.get("id");
            GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId),EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"CASE_ROLE")).queryOne();
            String description = roleType.get("description");
            String groupName = "";
            if(roleTypeId=="CASE_ROLE_OWNER")
            {
                String partyId = group.get("partyId");
                GenericValue tblgroup = EntityQuery.use(delegator).from("PartyGroup").where("partyId",partyId).queryOne();
                groupName = tblgroup.get("groupName");
            }else
            {
                List<GenericValue> member = EntityQuery.use(delegator).from("TblTicketRolesParty").where("id",id).queryList();
                if(member!=null&&member.size()!=0) {
                    for (int i = 0; i < member.size(); i++) {
                        if(i==0)
                        {
                            groupName = member[i].get("groupId");
                        }else
                        {
                            groupName += "," + member[i].get("groupId");
                        }
                    }
                }
            }
            map.put("type",description);
            map.put("groupName",groupName==""?"所有":groupName);
            members.add(map);
        }
        context.member = members;
    }
}