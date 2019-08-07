package ticket

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String partyId = parameters.partyId;
String ticketId = parameters.ticketId;
String roleTypeId = parameters.roleTypeId;
String caseId = parameters.caseId;

//更新人员的竞选信息
GenericValue ticketParty = delegator.findOne("TblTicketParties", UtilMisc.toMap("partyId", partyId, "ticketId", ticketId, "roleTypeId", roleTypeId), false);
ticketParty.put("joinDate", new java.sql.Date(new Date().getTime()));
ticketParty.store();
//更新该ticket竞选结果信息
GenericValue ticketRoles = EntityQuery.use(delegator).from("TblTicketRoles").where(UtilMisc.toMap("ticketId", ticketId, "roleTypeId", roleTypeId)).queryOne();
ticketRoles.put("partyId", partyId);
ticketRoles.store();

GenericValue ticket = delegator.findOne("TblTicket", UtilMisc.toMap("id", ticketId), false);
String author = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne().getString("fullName");
String msgTitle = ticket.getString("title") + " 竞选成功";
if(UtilValidate.isEmpty(caseId)){
    caseId = ticket.getString("caseId");
}
if(UtilValidate.isNotEmpty(caseId)){
    //更新case的party信息
    GenericValue existParty = EntityQuery.use(delegator).from("TblCaseParty").where("roleTypeId", roleTypeId, "caseId", caseId).queryFirst();
    if(existParty != null){
        //已有相同角色参与方，不可重复添加
        throw new RuntimeException("已存在相同角色参与方，不可重复添加");
    }
    GenericValue caseParty = delegator.makeValue("TblCaseParty");
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT"), null, false);
    String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
    caseParty.setString("partyId", groupId);
    if(partyId != groupId){//子账号参与竞选
        caseParty.setString("personId", partyId);
    }
    caseParty.setString("caseId", caseId);
    caseParty.setString("roleTypeId", roleTypeId);
    caseParty.set("joinDate", new java.sql.Date(new Date().getTime()));
    caseParty.create();
    if(partyId != groupId) {//子账号参与竞选
        runService("AddCasePartyMember", UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", caseId, "partyId", partyId, "groupPartyId", groupId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
    }
    msgTitle += "，已创建CASE！";
}

String titleClickAction = "";
runService("createSiteMsg", UtilMisc.toMap("partyId", partyId, "title", msgTitle, "titleClickAction", titleClickAction, "type", "notice"));

request.setAttribute("data", "保存成功");