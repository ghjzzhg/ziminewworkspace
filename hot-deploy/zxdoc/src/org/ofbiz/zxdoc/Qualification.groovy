package org.ofbiz.zxdoc

import javolution.util.FastSet
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.content.data.DataServices
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil

/**
 * Created by rextec on 2016/9/10.
 */
public Map<String, Object> submitAuthentication(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    String contactMechId = context.get("contactMechId");
    String regNumber=context.get("regNumber");
    String infoString = context.get("infoString");
    String paAddress1 = context.get("paAddress1");
    Map<String,String>regPhoto= context.get("regPhoto");
    String bizScope = context.get("bizScope");
//存储认证信息
    GenericValue partnerAuth= delegator.makeValidValue("TblAuthentication",UtilMisc.toMap("partyId",partyId,"regNumber",regNumber,"bizScope",bizScope));
    delegator.create(partnerAuth);
    //存储地址
    if(UtilValidate.isNotEmpty(contactMechId)){
        GenericValue address = delegator.findByPrimaryKey("PostalAddress",UtilMisc.toMap("contactMechId",contactMechId));
        address.put("address1",paAddress1);
        address.store();
    }
    //创建网站地址
    dispatcher.runSync("createPartyContactMech",UtilMisc.toMap("partyId",partyId,"contactMechTypeId","WEB_ADDRESS","infoString",infoString,"userLogin",userLogin));
//存储上传附件
    List<String> dataResouceIds = DataServices.storeAllDataResourceInMap(dispatcher,delegator,userLogin,regPhoto);
    List<GenericValue> attachmentList =new ArrayList<>();
    for(String dataResourceId : dataResouceIds){
        String id = delegator.getNextSeqId("TblAuthenticationAttachment");
        GenericValue attachment =delegator.makeValue("TblAuthenticationAttachment");
        attachment.setString("id",id);
        attachment.setString("partyId",partyId);
        attachment.setString("dataResourceId",dataResourceId);
        attachmentList.add(attachment);
    }
    delegator.storeAll(attachmentList);
    return successResult;
}
//显示机构账户信息
public Map<String,Object> viewQualification(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    if(UtilValidate.isEmpty(partyId)){
        partyId = ((GenericValue) context.get("userLogin")).get("partyId");
    }
    //获取机构类型
    GenericValue type = EntityQuery.use(delegator).from("BasicUnEnabledGroupInfo").where("partyId",partyId).queryOne();
    String comType = type.get("roleTypeId");
    String partnerType = type.get("partnerType");//合伙人业务角色
    String partnerCategory = type.get("partnerCategory");//合伙人类型
    if(UtilValidate.isEmpty(partnerType) || "G".equalsIgnoreCase(partnerCategory)) {//changed by galaxypan@2017-08-03:非合伙人或机构类型的合伙人
        List<GenericValue> qulificationInfos = EntityQuery.use(delegator).from("QualificationInfo").where("partyId", partyId).queryList();
        Map map = new HashMap();
        if (UtilValidate.isNotEmpty(qulificationInfos)) {
            map.put("info", qulificationInfos.get(0));
            map.put("attachements", qulificationInfos);
        } else {
            map.put("info", new HashMap());
        }
        //通过机构的partyId查找角色信息
        List<GenericValue> roles = from("PartyRole").where("partyId", partyId).queryList();
        List<String> providerRoles = ["CASE_ROLE_ACCOUNTING", "CASE_ROLE_INVESTOR", "CASE_ROLE_STOCK", "CASE_ROLE_LAW"];
        String qualificationDesc;
        for (GenericValue role : roles) {
            String roleType = role.getString("roleTypeId")
            if ("CASE_ROLE_OWNER".equals(roleType)) {
                qualificationDesc = "CASE_ROLE_OWNER";
                break;
            } else if (providerRoles.contains(roleType)) {
                qualificationDesc = roleType;
                break;
            }
        }
        String description = from("RoleType").where("roleTypeId", qualificationDesc).queryOne().getString("description");
        map.put("description", description);
        //添加提交实名认证过程的状态
        GenericValue party = from("Party").where("partyId", partyId).queryOne();
        String statusId = party.getString("statusId");
        String qualificationStatus;
        if (statusId.equals("PARTY_UNIDENTIFIED")) {
            qualificationStatus = "正在审核认证";
        } else if (statusId.equals("PARTY_IDENTIFIED")) {
            qualificationStatus = "审核成功";
        }
        map.put("qualificationStatus", qualificationStatus);
        successResult.put("data", map);
        successResult.put("type","NoPartner");
    }else
    {//个体类型的合伙人
        GenericValue targetParty = from("Party").where("partyId",partyId).queryOne();
        String targetPartyType = targetParty.getString("partyTypeId");
        String personId = partyId;
        String groupId = partyId;
        if("PARTY_GROUP".equalsIgnoreCase(targetPartyType)){
            GenericValue relationship = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_CONTACT")).queryOne();
            personId = relationship.get("partyIdTo");
        }else{
            GenericValue relationship = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdTo",partyId,"partyRelationshipTypeId","ORG_CONTACT")).queryOne();
            groupId = relationship.get("partyIdFrom");
        }
        if(UtilValidate.isEmpty(personId)){
            personId = ((GenericValue)context.get("userLogin")).get("partyId");
        }
        List<GenericValue> PersonQualificationInfos = EntityQuery.use(delegator).from("PersonQualificationInfo").where("partyId",personId).queryList();
        Map map= new HashMap();
        if(UtilValidate.isNotEmpty(PersonQualificationInfos)){
            map.put("info",PersonQualificationInfos.get(0));
            map.put("attachements", PersonQualificationInfos);
        }else{
            map.put("info", new HashMap());
        }
        //添加提交实名认证过程的状态
        GenericValue party = from("Party").where("partyId",personId).queryOne();
        String statusId = party.getString("statusId");
        String qualificationStatus;
        if(statusId.equals("PARTY_UNIDENTIFIED")){
            qualificationStatus = "正在审核认证";
        }else if(statusId.equals("PARTY_IDENTIFIED")){
            qualificationStatus = "审核成功";
        }

        GenericValue partner = EntityQuery.use(delegator).from("PartyGroup").where(UtilMisc.toMap("partyId",groupId)).queryOne();
        String partnertype = partner.get("partnerType");
        if(partnertype.equals("CASE_ROLE_ACCOUNTING"))
        {
            partnertype = "会所合伙人";
        }else if(partnertype.equals("CASE_ROLE_INVESTOR"))
        {
            partnertype = "其他机构合伙人";
        }else if(partnertype.equals("CASE_ROLE_LAW"))
        {
            partnertype = "律所合伙人";
        }else if(partnertype.equals("CASE_ROLE_OWNER"))
        {
            partnertype = "企业合伙人";
        }else if(partnertype.equals("CASE_ROLE_STOCK"))
        {
            partnertype = "劵商合伙人";
        }
        map.put("qualificationStatus",qualificationStatus);
        successResult.put("data",map);
        successResult.put("type",partnertype);
    }
    return successResult;

}
//判断是否已经提交认证材料
public Map<String , Object> ifSubmitQualification(){
    String partyId = ((GenericValue) context.get("userLogin")).get("partyId");
    GenericValue party = from("Party").where("partyId",partyId).queryOne();
    String partyType = party.getString("partyTypeId");
    String code;
    if(partyType.equals("PARTY_GROUP")){
        List qualification =delegator.findByAnd("TblAuthentication",UtilMisc.toMap("partyId",partyId));
        if(UtilValidate.isNotEmpty(qualification)){
            code = "hasQualification";
        }else {
            code = "isNotQualification";
        }
    }else if(partyType.equals("PERSON")){
        List qualificationPerson =delegator.findByAnd("TblPersonalAuthentication",UtilMisc.toMap("partyId",partyId));
        if(UtilValidate.isNotEmpty(qualificationPerson)){
            code = "hasPersonQualification";
        }else {
            code = "isNotQualification";
        }
    }
    Map<String, Object> result = ServiceUtil.returnMessage(code, null);
    return result;
}
//删除机构帐号的实名认证信息
public Map<String, Object> deleteQualification(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String userLoginId = context.get("userLoginId");
    String[] ids = userLoginId.split(",");
    for(String id :ids){
        String partyId = delegator.findByAnd("UserLogin",UtilMisc.toMap("userLoginId",id)).get(0).get("partyId");
        Set<String> fieldsToSelect = FastSet.newInstance();
        fieldsToSelect.add("contactMechId");
        List<GenericValue>contactMechIds = delegator.findList("PartyContactMech",EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId),fieldsToSelect,null,null,false);
        List<String> list = new ArrayList<>();
        for (GenericValue a : contactMechIds){
            String contactMechId = a.get("contactMechId");
            list.add(contactMechId);
        }
        //找到id对应的contactMechId并且contactMechTypeId是等于WEB_ADDRESS的记录
        List<GenericValue> contactMechWeb =delegator.findList("ContactMech",EntityCondition.makeCondition(EntityCondition.makeCondition("contactMechId",EntityOperator.IN,list),EntityOperator.AND,EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"WEB_ADDRESS")),null,null,null,false);
        if(UtilValidate.isNotEmpty(contactMechWeb)){
            String webContactMechId = contactMechWeb.get(0).get("contactMechId");
            delegator.removeByAnd("PartyContactMech",UtilMisc.toMap("contactMechId",webContactMechId));
            delegator.removeByAnd("ContactMech",UtilMisc.toMap("contactMechId",webContactMechId));
        }
        delegator.removeByAnd("TblAuthenticationAttachment",UtilMisc.toMap("partyId",partyId));
        delegator.removeByAnd("TblAuthentication",UtilMisc.toMap("partyId",partyId));
    }
    //TODO:删除dataresource记录
    return successResult;
}
//保存子帐号的实名认证的材料
public Map<String, Object>submitPersonQualification(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId= context.get("partyId");
    String fullName = context.get("fullName");
    String idCard = context.get("idCard");
    String qualifiNum = context.get("qualifiNum");
    Map<String,String>regPhoto= context.get("regPhoto");
    GenericValue TblPersonalAuthentication = delegator.makeValidValue("TblPersonalAuthentication",UtilMisc.toMap("partyId",partyId,"idCard",idCard,"qualifiNum",qualifiNum));
    delegator.create(TblPersonalAuthentication);
    GenericValue personInfo = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",partyId));
    personInfo.put("fullName",fullName);
    personInfo.store();
    List<String> dataResouceIds = DataServices.storeAllDataResourceInMap(dispatcher,delegator,userLogin,regPhoto);
    List<GenericValue> listPersonQualifi = new ArrayList<>();
    for(String dataResourceId :dataResouceIds){
        GenericValue TblPersonalAuthenticationAtta =delegator.makeValidValue("TblPersonalAuthenticationAtta",UtilMisc.toMap("partyId",partyId,"dataResourceId",dataResourceId));
        listPersonQualifi.add(TblPersonalAuthenticationAtta);
    }
    delegator.storeAll(listPersonQualifi);
    return successResult;
}
//显示子帐号的实名认证信息
public Map<String, Object>viewQualificationPerson(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String personId = context.get("personId");
    if(UtilValidate.isEmpty(personId)){
        personId = ((GenericValue)context.get("userLogin")).get("partyId");
    }
    List<GenericValue> PersonQualificationInfos = EntityQuery.use(delegator).from("PersonQualificationInfo").where("partyId",personId).queryList();
    Map map= new HashMap();
    if(UtilValidate.isNotEmpty(PersonQualificationInfos)){
        map.put("info",PersonQualificationInfos.get(0));
    }else{
        map.put("info", new HashMap());
    }
    //添加提交实名认证过程的状态
    GenericValue party = from("Party").where("partyId",personId).queryOne();
    String statusId = party.getString("statusId");
    String qualificationStatus;
    if(statusId.equals("PARTY_UNIDENTIFIED")){
        qualificationStatus = "正在审核认证";
    }else if(statusId.equals("PARTY_IDENTIFIED")){
        qualificationStatus = "审核成功";
    }
    map.put("qualificationStatus",qualificationStatus);
    successResult.put("data",map);
    return successResult;
}
//删除子帐号的实名认证信息
public Map<String, Object>deletePersonQualify(){
    Map<String, Object> successResult =ServiceUtil.returnSuccess();
    String personId= context.get("personId");
    String[] ids = personId.split(",");
    for (String id :ids){
        delegator.removeByAnd("TblPersonalAuthenticationAtta",UtilMisc.toMap("partyId",id));
        delegator.removeByAnd("TblPersonalAuthentication",UtilMisc.toMap("partyId",id));
    }
    return successResult;
}
//审核通过子帐号认证
public Map<String,Object>passPersonQualification(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin =(GenericValue) context.get("userLogin");
    String personId = context.get("personId");
    String[] ids = personId.split(",");
    List<GenericValue> partyList = EntityQuery.use(delegator).select().from("Party").where(EntityCondition.makeCondition("partyId",EntityOperator.IN,Arrays.asList(ids))).queryList();
    for (GenericValue party : partyList){
        party.put("statusId","PARTY_IDENTIFIED");
        delegator.store(party);
    }
    dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreTarget",personId,"eventName","SCORE_RULE_REWARD"));
    return successResult;
}