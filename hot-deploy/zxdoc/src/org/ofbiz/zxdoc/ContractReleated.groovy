package org.ofbiz.zxdoc

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.text.DateFormat
import java.text.SimpleDateFormat

//创建合同与修改的判断
public Map<String, Object> addContract() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String files = context.get("fileFieldName");
    String firstPartyId = context.get("firstPartyName");
    GenericValue userLogin = context.get("userLogin");
    //时间判断
    String startDate = context.get("startDate");
    String dateClose = context.get("dateClose");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    try {
        Date dt1 = df.parse(startDate);
        Date dt2 = df.parse(dateClose);
        if (dt1.getTime() > dt2.getTime()) {
            successResult.put("msg", "开始时间不能大于结束时间");
            return successResult;
        }
    } catch (Exception exception) {
        exception.printStackTrace();
    }

    String partyId = context.get("userLogin").get("partyId");
    //如果当前是子账号操作，需要找到对应的主账号
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
    if (UtilValidate.isEmpty(context.get("contractId"))) {
        List<String> relatedCaseName = new ArrayList<>();
        String contractId = delegator.getNextSeqId("TblContract");
        String relatedCaseId = context.get("relatedCase");
        if (UtilValidate.isNotEmpty(relatedCaseId)) {
            relatedCaseName = createContractCaseLinks(relatedCaseId, contractId);
        }
        context.put("contractId", contractId);
        String charge = context.get("isCharge");
        if (charge != null) {
            charge = "Y";
        } else {
            charge = "N";
        }
        context.put("isCharge", charge);
        context.put("createBy", partyId);
        //这里的关联case存放的是case名称，如果需要关联case，去往case表进行关联，这也是造成case只能关联一个合同的原因,已经修改
        context.put("relateCase", relatedCaseName.toString().replace("[", "").replace("]", ""));
        GenericValue contract = delegator.makeValidValue("TblContract", context);
        delegator.create(contract);
        List<String> dataResouceIds = new ArrayList<>();
        if(UtilValidate.isNotEmpty(files)){
            dataResouceIds = UtilMisc.toListArray(files.split(","));
        }
        List<GenericValue> contractTemplates = new ArrayList<>();
        for (String dataResourceId : dataResouceIds) {
            GenericValue contractFile = delegator.makeValue("TblContractAccessory");
            contractFile.setString("contractId", contractId);
            contractFile.setString("dataResourceId", dataResourceId);
            contractTemplates.add(contractFile);
        }
        delegator.storeAll(contractTemplates);
        successResult.put("msg", "添加成功")
    } else {

        GenericValue contract = delegator.findOne("TblContract", UtilMisc.toMap("contractId", context.get("contractId")), false);
        contract.setString("contractId", context.get("contractId"));
        contract.setString("firstPartyName", context.get("firstPartyName"));
        contract.setString("secondPartyName", context.get("secondPartyName"));
        contract.setString("secondPersonName", context.get("secondPersonName"));
        contract.setString("contractName", context.get("contractName"));
        contract.set("startDate", context.get("startDate"));
        contract.set("dateClose", context.get("dateClose"));


        String relatedCaseId = context.get("relatedCase");

        List<String> relatedCaseName = new ArrayList<>();

        //删除原来的关联关系
        List<GenericValue> relateds = EntityQuery.use(delegator).from("TblCaseRelatedContract").where("contractId",context.get("contractId")).queryList();
        if(relateds!=null&&relateds.size()!=0)
        {
            for(GenericValue related:relateds) {
                related.remove();
            }
        }
        if(UtilValidate.isNotEmpty(relatedCaseId)){
            relatedCaseName = createContractCaseLinks(relatedCaseId, contractId);
        }

        contract.put("relateCase", relatedCaseName.toString().replace("[", "").replace("]", ""));

        String charge = context.get("isCharge");
        if (charge != null) {
            charge = "Y";
        } else {
            charge = "N";
        }
        contract.setString("isCharge", charge);
        delegator.store(contract);
        //附件
        delegator.removeByAnd("TblContractAccessory", UtilMisc.toMap("contractId", contractId));

        List<String> dataResouceIds = new ArrayList<>();
        if(UtilValidate.isNotEmpty(files)){
            dataResouceIds = UtilMisc.toListArray(files.split(","));
        }
        List<GenericValue> contractTemplates = new ArrayList<>();
        for (String dataResourceId : dataResouceIds) {
            GenericValue contractFile = delegator.makeValue("TblContractAccessory");
            contractFile.setString("contractId", contractId);
            contractFile.setString("dataResourceId", dataResourceId);
            contractTemplates.add(contractFile);
        }
        delegator.storeAll(contractTemplates);

        successResult.put("msg", "修改成功")
    }
    getManageBetweenTwice(context.secondPartyId, userLogin);
    return successResult;
}

private List<String> createContractCaseLinks(String relatedCaseId, String contractId){
    List<String> relatedCaseName = new ArrayList<>();
    relatedCaseId = relatedCaseId.replace("[", "").replace("]", "").replace(" ", "");
    String[] relatedCaseIds = relatedCaseId.split(",");
    List<GenericValue> relatedCaseList = EntityQuery.use(delegator).from("TblCase").where(EntityCondition.makeCondition("caseId", EntityOperator.IN, UtilMisc.toListArray(relatedCaseIds))).queryList();
    for (GenericValue relatedCase : relatedCaseList) {
        relatedCaseName.add(relatedCase.getString("title"));
        //新增一个关联
        String id = delegator.getNextSeqId("TblCaseRelatedContract");
        GenericValue caseRelatedContract = delegator.makeValue("TblCaseRelatedContract",UtilMisc.toMap("id",id,"contractId",contractId,"caseId", relatedCase.getString("caseId")));
        caseRelatedContract.create();
    }
    return relatedCaseName;
}

//获取需要建立客户管理的两个partyId
public void getManageBetweenTwice(String customerId, GenericValue userLogin) {
    //获取甲方公司主键
    String partyId = context.get("userLogin").get("partyId");
    GenericValue login = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
    String partyTypeId = login.get("partyTypeId");
    if (partyTypeId.equals("PERSON")) {
        GenericValue relationship = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT").queryOne();
        partyId = relationship.get("partyIdFrom");
    }

    //如果custmerId存在,保存相关
    if (UtilValidate.isNotEmpty(customerId)) {
        savePartner(partyId, customerId, userLogin);
        savePartner(customerId, partyId, userLogin);
    }
}

private void savePartner(String partyIdFrom, String partyIdTo, GenericValue userLogin) {
    GenericValue genericValue = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "partyRelationshipTypeId", "ORG_LINK_CONTACT")).queryOne();
    if (genericValue == null) {
        GenericValue genericValue1 = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", partyIdTo)).queryOne();
        Map<String, Object> partner = UtilMisc.toMap("partyId", partyIdFrom, "userLoginId", genericValue1.get("userLoginId"));
        GenericValue genericValuess = EntityQuery.use(delegator).from("TblCustomer").where(partner).queryOne();
        if (UtilValidate.isNotEmpty(genericValuess)) {
            genericValuess.put("status", "Y")
            genericValuess.store();
        } else {
            String id = delegator.getNextSeqId("TblCustomer");
            partner.put("id", id);
            partner.put("status", "Y")
            GenericValue customer = delegator.makeValidValue("TblCustomer", partner);
            delegator.create(customer);
        }
        dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "partyRelationshipTypeId", "ORG_LINK_CONTACT", "userLogin", userLogin));
    }
}

//修改合同获取数据
public Map<String, Object> saveContract() {
    GenericValue map1 = null;
    String contractId = context.get("contractId");
//    String userLoginId=((GenericValue) context.get("userLogin")).get("userLoginId");
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = userLogin.getString("userLoginId");
    String firstPartyName;
    if (contractId != null) {
        map1 = delegator.findByAnd("PartyGroupContract", UtilMisc.toMap("contractId", contractId), null, false).get(0);
    }

    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    List<String> dataResourceId = new ArrayList<>();
    Map<String, Object> map = new HashMap<String, Object>();
    List<GenericValue> files = delegator.findByAnd("TblContractAccessory", UtilMisc.toMap("contractId", contractId), null, false);
    if(UtilValidate.isNotEmpty(files)){
        for (GenericValue file : files) {
            dataResourceId.add(file.getString("dataResourceId"));
        }
    }

    String secondPersonId = map1.getString("secondPersonName");
    String secondPartyGroupId = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", secondPersonId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false).get(0).get("partyIdFrom");
    GenericValue secondPartyGroup = EntityQuery.use(delegator).from("PartyGroup").where("partyId", secondPartyGroupId).queryOne();
    String secondPartyGroupName = secondPartyGroup.getString("groupName");
    String secondPartyPartnerGroupName = secondPartyGroup.getString("partnerGroupName");
    if(UtilValidate.isNotEmpty(secondPartyPartnerGroupName)){
        secondPartyGroupName = secondPartyPartnerGroupName;
    }

    String firstPartyId = map1.getString("createBy");//创建者在保存时转换为了主账号id

    GenericValue firstPartyGroup = delegator.findByAnd("PartyGroup", UtilMisc.toMap("partyId", firstPartyId), null, false).get(0)
    firstPartyName = firstPartyGroup.get("groupName");
    String partnerName = firstPartyGroup.getString("partnerGroupName")
    if(UtilValidate.isNotEmpty(partnerName)){
        firstPartyName = partnerName;
    }

    map.put("dataResourceId", dataResourceId.join(","));
    map.put("contract", map1);
    map.put("firstPartyName", firstPartyName);
    map.put("firstPartyId", firstPartyId);
    map.put("secondPartyId", secondPartyGroupId);
    map.put("secondPartyName", secondPartyGroupName);
    map.put("secondPersonId", secondPersonId);
    successResult.put("data", map);
    return successResult;
}

//删除记录
public Map<String, Object> deleteContract() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contractId = context.get("contractId");
    /*List<GenericValue> relatedCases = delegator.findByAnd("TblCase", UtilMisc.toMap("contract", contractId), null, false);
    for (GenericValue relatedCase : relatedCases) {
        relatedCase.put("contract", null);
    }
    delegator.storeAll(relatedCases);//清空相关case中的合同信息*/
    //现在删除的是中间表中的内容
    List<GenericValue> relatedList = EntityQuery.use(delegator).from("TblCaseRelatedContract").where("contractId",contractId).queryList();
    if(relatedList!=null&&relatedList.size()!=0)
    {
        for (GenericValue related:relatedList)
        {
            related.remove();
        }
    }
    delegator.removeByAnd("TblContract", UtilMisc.toMap("contractId", contractId));
    successResult.put("data", "删除成功");
    return successResult;
}