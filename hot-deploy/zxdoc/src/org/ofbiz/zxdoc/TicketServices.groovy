import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

public Map<String, Object> saveTicket() {
    Map<String,Object> groupIds = context.get("groupName");
    //Map<String,Object> groupInfoList = subGroupNames(groupIds);
    Map<String, Object> success = ServiceUtil.returnSuccess();
    //创建ticket
    String ticketId = delegator.getNextSeqId("TblTicket");
    String partyId = context.get("userLogin").get("partyId");
//    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId), null, false);
//    partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;

//    //发布信息时必须包含创建该信息的机构
//
//    List<EntityCondition> conditionList = new ArrayList<>();
//    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
//    conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
//    String thisPartyRoleTypeId = EntityQuery.use(delegator).from("PartyRole").where(EntityCondition.makeCondition(conditionList)).queryOne().get("roleTypeId");

    String title = context.get("title");
    Date endTime = context.get("endTime");
    String type = context.get("type");
    List<String> roles = context.get("roles");
    String ticketContent = context.get("ticketContent");
    GenericValue ticket = delegator.makeValue("TblTicket");
    String TemplateId = context.get("TemplateId");
    ticket.setString("id", ticketId);
    ticket.setString("title", title);
    ticket.setString("ticketPartyId", partyId);
    ticket.set("startTime", new java.sql.Date(new Date().getTime()));
    ticket.set("endTime", new java.sql.Date(endTime.getTime()));
    ticket.setString("description", ticketContent==null?" ":ticketContent);
    ticket.setString("type", type);
    ticket.setString("templateId", TemplateId);
    delegator.create(ticket);
    //创建参与方
    List<GenericValue> ticketParties = new ArrayList<>();
    List<GenericValue> ticketPartyParties = new ArrayList<>();
//    if(!roles.contains(thisPartyRoleTypeId)){
//        roles.add(thisPartyRoleTypeId);
//    }
    for (String role : roles) {
        GenericValue ticketParty = delegator.makeValue("TblTicketRoles");
        String id = delegator.getNextSeqId("TblTicketRoles");
        ticketParty.setString("id", id);
        ticketParty.setString("ticketId", ticketId);
        ticketParty.setString("roleTypeId", role);
        if("CASE_ROLE_OWNER".equals(role)){//当前只有企业能发布信息，因此直接把当前企业加入到ticketRole中
            ticketParty.setString("partyId", partyId);
        }
        if(UtilValidate.isNotEmpty(groupIds)) {
            String groupList = groupIds.get(role);
            if (UtilValidate.isNotEmpty(groupList)) {
                String[] groups = groupList.split(",");
                for (String groupId : groups) {
                    GenericValue ticketRoleParty = delegator.makeValue("TblTicketRolesParty");
                    ticketRoleParty.setString("id", id);
                    ticketRoleParty.setString("groupId", groupId);
                    ticketPartyParties.add(ticketRoleParty);
                }

            }
        }/*
        if("CASE_ROLE_PARTNER".equals(role)){
            List<String> groupNamePariner = context.get("groupNamePariner");
            for(String groupName : groupNamePariner){
                GenericValue ticketRoleParty = delegator.makeValue("TblTicketRolesParty");
                ticketRoleParty.setString("id", id);
                ticketRoleParty.setString("groupId", groupName);
                ticketPartyParties.add(ticketRoleParty);
            }
        }*/
        ticketParties.add(ticketParty);
    }
    delegator.storeAll(ticketParties);
    delegator.storeAll(ticketPartyParties);

    success.put("data", "发布成功");
    return success;
}

private Map<String,Object> subGroupNames(List<String> groupIds){
    Map<String ,Object> groupMap = new HashMap<>();
    for(String groupId : groupIds){
        GenericValue groupInfo = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where(UtilMisc.toMap("partyId",groupId)).queryOne();
        String roleTypeId = groupInfo.get("roleTypeId");
        if(UtilValidate.isNotEmpty(groupMap.get(roleTypeId))){
            String groupIdList = groupMap.get(roleTypeId);
            groupMap.put(roleTypeId,groupIdList + "," + groupId);
        }else{
            groupMap.put(roleTypeId , groupId);
        }
    }
    return groupMap;
}

public Map<String, Object> runForTicket() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String partyId = context.get("userLogin").get("partyId");
//    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId), null, false);
//    partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
    String reason = context.get("reason");
    String ticketId = context.get("ticketId");
    String roleTypeId = context.get("roleTypeId");
    GenericValue ticketParty = delegator.makeValue("TblTicketParties");
    ticketParty.setString("ticketId", ticketId);
    ticketParty.setString("partyId", partyId);
    ticketParty.setString("roleTypeId", roleTypeId);
    ticketParty.set("runForTime", new java.sql.Date(new Date().getTime()));
    ticketParty.setString("reason", reason);
    delegator.create(ticketParty);
    success.put("data", "提交成功");
    return success;
}

public Map<String, Object> createCaseByTicket() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String ticketId = context.get("ticketId");
    String caseId = delegator.getNextSeqId("TblCase");
    GenericValue userLogin = context.get("userLogin");
    GenericValue caseFromTicket = delegator.makeValue("TblCase");

    GenericValue ticketBasicInfo = delegator.findOne("TblTicket", UtilMisc.toMap("id", ticketId), false);
    //找该类型版本号最新的模板
    List<EntityCondition> conditionList = new ArrayList<>();
    conditionList.add(EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"));
    conditionList.add(EntityCondition.makeCondition("deleted", EntityOperator.EQUALS, null));
    conditionList.add(EntityCondition.makeCondition("id", EntityOperator.EQUALS, ticketBasicInfo.getString("templateId")));
    GenericValue caseTemplate = null;
    List<GenericValue> caseTemplateList = delegator.findList("TblCaseTemplate", EntityCondition.makeCondition(conditionList), null, UtilMisc.toList("-version"), null, false);
    if(caseTemplateList.size() > 0){
        caseTemplate = caseTemplateList.get(0);
        //填写case基本信息,创建case
        caseFromTicket.setString("caseId", caseId);
        if(ticketBasicInfo.getString("type")!="other") {
            caseFromTicket.setString("caseCategory", ticketBasicInfo.getString("type"));
        }
        caseFromTicket.setString("caseTemplate", caseTemplate.getString("id"));
        caseFromTicket.setString("ticket", ticketBasicInfo.getString("id"));
        String caseTitle = ticketBasicInfo.getString("title")
        caseFromTicket.setString("title", caseTitle);
        caseFromTicket.setString("summary", ticketBasicInfo.getString("description"));
        caseFromTicket.set("startDate", ticketBasicInfo.get("startTime"));
//        caseFromTicket.set("completeDate", ticketBasicInfo.get("endTime"));
        caseFromTicket.set("partyId", ticketBasicInfo.get("ticketPartyId"));
        caseFromTicket.set("createPartyId", userLogin.get("partyId"));

        //创建CASE文档目录
        Map<String, Object> caseFolderResult = runService("createCaseRootFolder", UtilMisc.toMap("caseTitle", caseTitle, "ownerPartyId", userLogin.get("partyId"), "userLogin", userLogin));
        String caseFolderId = caseFolderResult.get("folderId");
        caseFromTicket.setString("folderId", caseFolderId);

        delegator.create(caseFromTicket);

        //将caseId更新至ticket
        GenericValue ticket = delegator.findOne("TblTicket", UtilMisc.toMap("id", ticketId), false);
        ticket.put("caseId", caseId);
        ticket.store();
        //更新case参与方
        List<GenericValue> ticketRoles = delegator.findByAnd("TblTicketRoles", UtilMisc.toMap("ticketId", ticketId), null, false);
        List<GenericValue> casePartyList = new ArrayList<>();
        List<String> joinedPartyIds = new ArrayList<>();
        for(GenericValue ticketRole: ticketRoles){
            String partyId = ticketRole.getString("partyId");
            if(UtilValidate.isNotEmpty(partyId)){
                joinedPartyIds.add(partyId);
                GenericValue genericValue = EntityQuery.use(delegator).from("Party").where("partyId",partyId).queryOne();
                String personId = "";
                if(genericValue.get("partyTypeId").equals("PERSON")){
                    GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId",partyId)).queryOne();
                    personId = partyId;
                    partyId = person.get("groupId");
                }
    //            List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    //            String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
                GenericValue caseParty = delegator.makeValue("TblCaseParty");
                if(UtilValidate.isNotEmpty(partyId)){//TODO 如果为空则以后竞选成功后将其加入case
                    caseParty.setString("partyId", partyId);
                    caseParty.setString("caseId", caseId);
                    caseParty.setString("roleTypeId", ticketRole.getString("roleTypeId"));
                    caseParty.set("joinDate", new java.sql.Date(new Date().getTime()));
                    caseParty.set("personId", personId);
                    casePartyList.add(caseParty);
                    if(UtilValidate.isNotEmpty(personId)){
                        runService("AddCasePartyMember", UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true,"caseId",caseId, "partyId", personId, "groupPartyId", partyId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
                    }
                }
            }
        }
        delegator.storeAll(casePartyList);

        //通知参与方
        List<GenericValue> ticketCandidates = delegator.findByAnd("TblTicketParties", UtilMisc.toMap("ticketId", ticketId), null, false);
        for (GenericValue candidate : ticketCandidates) {
            String candidateId = candidate.getString("partyId");
            String msgTitle = caseTitle + " 已创建CASE！";
            if(!joinedPartyIds.contains(candidateId)){
                msgTitle = caseTitle + " 竞选失败";
            }
            String titleClickAction = "";
            runService("createSiteMsg", UtilMisc.toMap("partyId", candidateId, "title", msgTitle, "titleClickAction", titleClickAction, "type", "notice"));
        }
        //copy from CaseService.groovy line 461 - line 477
        //创建大步骤
        List<GenericValue> templateGroups = EntityQuery.use(delegator).from("TblCaseTemplateNodeGroup").where("templateId", caseTemplate.getString("id")).queryList();
        List<GenericValue> toBeStored = new ArrayList<>();
        for (GenericValue templateGroup : templateGroups) {
            GenericValue node = delegator.makeValue("TblCaseProgressGroup");
            String id = delegator.getNextSeqId("TblCaseProgressGroup");
            node.setString("id", id);
            node.setString("caseId", caseId);
            node.setString("name", templateGroup.getString("name"));
            node.setString("templateGroupId", templateGroup.getString("id"));;
            node.set("seq", templateGroup.getInteger("seq"));
            toBeStored.add(node);
        }
        delegator.storeAll(toBeStored);

        //创建具体任务
        runService("saveCaseProgressFromTemplate", UtilMisc.toMap("userLogin", context.get("userLogin"), "caseId", caseId, "templateId", caseTemplate.getString("id")));
        success.put("data", "创建成功")
    } else {
        success.put("data", "无相关模板，创建失败，请联系管理员添加模板");
    }
    return success;
}


public Map<String, Object> deleteCaseByTicket()
{
    Map success = ServiceUtil.returnSuccess();
    String ticketId = context.get("ticketId");
    GenericValue caseTicket = EntityQuery.use(delegator).from("TblTicket").where("id",ticketId).queryOne();
    //Y表示删除
    caseTicket.put("hasdelete","Y");
    caseTicket.store();
    success.put("data","归档成功");
    return success;
}