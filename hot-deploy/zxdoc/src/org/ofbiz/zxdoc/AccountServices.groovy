package org.ofbiz.zxdoc

import com.google.gson.*
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.*
import org.ofbiz.content.data.DataServices
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil
import org.ofbiz.webapp.control.LoginWorker
import org.ofbiz.widget.model.HtmlWidget
import org.ofbiz.widget.model.ModelScreen
import org.ofbiz.widget.model.ModelScreenWidget
import org.ofbiz.widget.model.ScreenFactory

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

public Map<String, Object> register() {
    Map success = ServiceUtil.returnSuccess();
    boolean isAdmin = false;
    GenericValue loginUser = (GenericValue) context.get("userLogin");
    //管理员直接添加账户
    if(loginUser != null){
        GenericValue adminSec = null;
        try {
            adminSec = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where("userLoginId", loginUser.getString("userLoginId"), "groupId", "ZXDOC_ADMIN").cache().queryOne();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("管理员操作错误");
        }
        isAdmin = adminSec != null;
    }

    Map maps = new HashMap();
    Map map = new HashMap();
    String groupName = (String) context.get("groupName");
    String partnerGroupName = (String) context.get("partnerGroupName");
    String partnerCategory = (String) context.get("partnerType");
    String partnerUsername = (String) context.get("partnerUsername");
    if("P".equalsIgnoreCase(partnerCategory)){
        partnerGroupName = "";
        //个人合伙人使用挂靠机构名称
        if(UtilValidate.isEmpty(partnerUsername)){
            map.put("result", "false");
            map.put("msg", "个人合伙人请输入个人登录账号");
            success.put("result", map);
            return success;
        }
    }else{
        partnerUsername = "";
    }
    if(groupName.equals(partnerGroupName)){
        partnerGroupName = "";
    }
    String contactName = (String) context.get("contactName");
    String contactNo = (String) context.get("contactNo");
    String email = (String) context.get("email");
    String postCode = (String) context.get("area");
    String address = (String) context.get("address");
    String username = (String) context.get("username");
    String password = (String) context.get("password");
    String groupType = (String) context.get("groupType");
    String webAddress = (String) context.get("webAddress");
    String regNumber = context.get("regNumber") == null ? "未填写" : (String) context.get("regNumber");
    String infoString = context.get("infoString") == null ? "未填写" : (String) context.get("infoString");

    //是否为合伙人
    String isPartner = (String) context.get("isPartner");
    String regPhoto = context.get("regPhoto");
    String code1 = (String) context.get("inputCode");
    String verificationCode = (String) context.get("verificationCode");
    String code = (String) context.get("_CAPTCHA_CODE_");
    if(!isAdmin){
        if(UtilValidate.isNotEmpty(code)){
            String returnString = checkCodes(code1, contactNo, verificationCode,code);
            if(UtilValidate.isNotEmpty(returnString)){
                map.put("result", "false");
                map.put("msg", returnString);
                success.put("result", map);
                return success;
            }
        }else{
            map.put("result", "false");
            map.put("msg", "图片验证码过期，请刷新后重试！");
            success.put("result", map);
            return success;
        }
    }

    if (UtilValidate.isEmpty(regPhoto)) {
        map.put("result", "false");
        map.put("msg", "请上传营业执照");
        success.put("result", map);
        return success;
    }

    boolean parinerFlag = false;
    //查询注册是否为合伙人
    boolean registerAsPartner = isPartner.equals("Y");
    if(registerAsPartner){
        if(UtilValidate.isNotEmpty(partnerGroupName)){
            //机构型合伙人不允许存在同名的其他机构型合伙人
            long partnerWithSameName = EntityQuery.use(delegator).from("PartyGroup").where("partnerGroupName", partnerGroupName).queryCount();
            if(partnerWithSameName > 0){
                map.put("result", "false");
                map.put("msg", "合伙人机构名称已被注册！");
                success.put("result", map);
                return success;
            }
        }

        GenericValue genericValue = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupName), EntityCondition.makeCondition("partnerType",EntityOperator.EQUALS, null)).queryOne();
        if(UtilValidate.isNotEmpty(genericValue)){
            //合伙人的机构类型是否和已注册的合伙机构类型相同
            String roleType = (String) genericValue.get("roleTypeId");
            if(!roleType.equals(groupType)){
                parinerFlag = true;
            }
        }else{
            //如果机构不存在那么查询是否注册的相同名称的合伙人
            List<GenericValue> parinetList = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition("groupName",EntityOperator.EQUALS, groupName),EntityCondition.makeCondition("partnerType",EntityOperator.NOT_EQUAL, null)).queryList();
            if(UtilValidate.isNotEmpty(parinetList)){
                //如果注册的相同名称的合伙人，那么查询合伙人的机构类型
                //changed by galaxypan@2017-08-02:合伙人类型从partyGroup中获取
                GenericValue samplePartner = parinetList.get(0);
                if(!samplePartner.getString("partnerType").equals(groupType)){
                    parinerFlag = true;
                }
            }
        }
    }
    //如果存在相同类型的
    if(parinerFlag){
        map.put("result", "false");
        map.put("msg", "注册机构类型与挂靠机构类型不符！");
        success.put("result", map);
        return success;
    }

    //changed by galaxypan@2017-08-02:合伙人有自己独立的机构名称时需要验证唯一性
    long count = 0;
    if(!registerAsPartner){//非合伙人
        count = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition("groupName",EntityOperator.EQUALS, groupName),EntityCondition.makeCondition("partnerType",EntityOperator.EQUALS, null)).queryCount();
        if (count > 0) {
            map.put("result", "false");
            map.put("msg", groupName + "已被注册");
            success.put("result", map);
            return success;
        }
    }else{//合伙人机构有自己的独立名称时
        if(UtilValidate.isNotEmpty(partnerGroupName) && !groupName.equals(partnerGroupName)){
            count = EntityQuery.use(delegator).from("PartyGroup").where(EntityCondition.makeCondition("groupName",EntityOperator.EQUALS, partnerGroupName)).queryCount();
            if (count > 0) {
                map.put("result", "false");
                map.put("msg", partnerGroupName + "已被注册");
                success.put("result", map);
                return success;
            }
        }
    }

    //登录名唯一性判断
    count = EntityQuery.use(delegator).from("UserLogin").where(EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS, username)).queryCount();
    if(count > 0){
        map.put("result","false");
        map.put("msg",username + "已被注册，请更换一个登录名");
        success.put("result",map);
        return success;
    }

    //以管理员账户登录后才能执行相关service调用
    Map<String, Object> systemAccount = UtilMisc.toMap("userLogin", EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").queryOne());

    //创建PartyGroup
    //如果类型为合伙人，则主账号为虚拟，不可以登录。如果为其他类型，那么可以直接登录
    Map<String, Object> paramMap = new HashMap<String, Object>();
    if (registerAsPartner) {
        paramMap = UtilMisc.toMap("groupName", groupName, "statusId", "PARTY_IDENTIFIED", "partnerGroupName", partnerGroupName, "partnerType", groupType, "partnerCategory", partnerCategory);
    } else {
        paramMap = UtilMisc.toMap("groupName", groupName, "statusId", "PARTY_IDENTIFIED");
    }
    paramMap.putAll(systemAccount);
    Map<String, Object> result = dispatcher.runSync("createPartyGroup", paramMap);
    String partyId = (String) result.get("partyId");
    /*//创建主账号的类型
    if(isPartner.equals("CASE_ROLE_PARTNER"))
    {
        paramMap = UtilMisc.toMap("partyId", partyId, "roleTypeId", isPartner);
        //创建合伙人类型
        Map typePartner = UtilMisc.toMap("partyId",partyId,"partnerType",groupType);
        GenericValue partnerType = delegator.makeValue("TblPartnerType",typePartner);
        partnerType.create();
    }else {
        paramMap = UtilMisc.toMap("partyId", partyId, "roleTypeId", groupType);
    }*/
//changed by galaxypan@2017-08-02:合伙人使用相同的role
    paramMap = UtilMisc.toMap("partyId", partyId, "roleTypeId", groupType);
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPartyRole", paramMap);

    //创建企业机构联系方式
    paramMap = UtilMisc.toMap("partyId", partyId, "emailAddress", email);
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPartyEmailAddress", paramMap);
    paramMap = UtilMisc.toMap("partyId", partyId, "city", "", "address1", address == null ? "" : address, "postalCode", postCode);
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPartyPostalAddress", paramMap);

    //创建联系人信息
    paramMap = UtilMisc.toMap("firstName", contactName, "fullName", contactName);
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPerson", paramMap);
    String contactPartyId = (String) result.get("partyId");
    paramMap = UtilMisc.toMap("partyId", contactPartyId, "contactNumber", contactNo);
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPartyTelecomNumber", paramMap);

    dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechTypeId", "WEB_ADDRESS", "infoString", webAddress, "userLogin", systemAccount.get("userLogin")));

    paramMap = UtilMisc.toMap("partyId", contactPartyId, "roleTypeId", "CONTACT");
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPartyRole", paramMap);

    //建立机构与联系人之间的关系
    paramMap = UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", contactPartyId, "roleTypeIdTo", "CONTACT", "partyRelationshipTypeId", "ORG_CONTACT");
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createPartyRelationship", paramMap);
    String uname = username;
    //changed by galaxypan@2017-08-08:合伙人注册时均创建主账号，个人合伙人可由用户选择是否注册时创建个人登录子账号。
    /*if (registerAsPartner && "P".equalsIgnoreCase(partnerCategory)) {
        uname = "[" + username + "]";
    }*/
    //创建登录账户, 默认不激活，审核通过后激活
    paramMap = UtilMisc.toMap("partyId", partyId, "userLoginId", uname, "currentPassword", password, "currentPasswordVerify", password, "enabled", "N");
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("createUserLogin", paramMap);

    paramMap = UtilMisc.toMap("userLoginId", uname, "groupId", "ZXDOC_UNIDENTIFIED");
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("addUserLoginToSecurityGroup", paramMap);

    //通过审核
    paramMap = UtilMisc.toMap("registerUserLoginId", uname);
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("passRegistration", paramMap);

    GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").queryOne();

    //如果是非合伙人，那么上传的认证信息应该是机构认证信息，如果是合伙人，上传的是子账户认证信息
    //changed by galaxypan@2017-08-02:机构型合伙人上传机构的认证信息
    if (!registerAsPartner || "G".equalsIgnoreCase(partnerCategory)) {
        //存储认证信息
        GenericValue partnerAuth = delegator.makeValidValue("TblAuthentication", UtilMisc.toMap("partyId", partyId, "regNumber", regNumber == null ? " " : regNumber, "bizScope", infoString == null ? " " : infoString));
        delegator.create(partnerAuth);
        if (UtilValidate.isNotEmpty(regPhoto)) {
            //存储上传附件
            String[] dataResouceIds = StringUtils.split(regPhoto, ",");
            for (String dataResourceId : dataResouceIds) {
                String id = delegator.getNextSeqId("TblAuthenticationAttachment");
                GenericValue attachment = delegator.makeValue("TblAuthenticationAttachment");
                attachment.put("id", id);
                attachment.put("partyId", partyId);
                attachment.put("dataResourceId", dataResourceId);
                attachment.create();
            }
        }
    }

    //通过实名认证
    paramMap = UtilMisc.toMap("registerUserLoginId", uname, "isPartner", registerAsPartner && "P".equalsIgnoreCase(partnerCategory));
    paramMap.putAll(systemAccount);
    result = dispatcher.runSync("passQualification", paramMap);
    //如果类型为个人合伙人，为联系人再度创建一个可以登录的子账号
    if (registerAsPartner && "P".equalsIgnoreCase(partnerCategory)) {
        //changed by galaxypan@2017-08-15:个体合伙人联系方式直接关联到联系人
        paramMap = UtilMisc.toMap("partyId", contactPartyId, "emailAddress", email);
        paramMap.putAll(systemAccount);
        result = dispatcher.runSync("createPartyEmailAddress", paramMap);
        paramMap = UtilMisc.toMap("partyId", contactPartyId, "city", "", "address1", address == null ? "" : address, "postalCode", postCode);
        paramMap.putAll(systemAccount);
        result = dispatcher.runSync("createPartyPostalAddress", paramMap);

        //先更新联系人信息
        GenericValue contactPerson = EntityQuery.use(delegator).from("Person").where("partyId", contactPartyId).queryOne();
        contactPerson.setString("openFireJid", partnerUsername + "@" + UtilProperties.getPropertyValue("zxdoc", "jabber_server"));
        contactPerson.store();
        //更新party状态
        contactPerson = EntityQuery.use(delegator).from("Party").where("partyId", contactPartyId).queryOne();
        contactPerson.setString("statusId", "PARTY_IDENTIFIED");
        contactPerson.store();

        String thispartyId = (String) result.get("partyId");

        GenericValue openFire = EntityQuery.use(delegator).from("Person").where("partyId", contactPartyId).queryOne();
        openFire.put("openFireJid",contactPartyId + "@" + UtilProperties.getPropertyValue("zxdoc", "jabber_server"));
        openFire.store();

        //创建登录账户
        paramMap = UtilMisc.toMap("partyId", contactPartyId, "userLoginId", partnerUsername, "enabled", "Y", "currentPassword", password, "currentPasswordVerify", password);
        paramMap.putAll(systemAccount);
        result = dispatcher.runSync("createUserLogin", paramMap);

        //分配securitygroup
        String personSecurityGroup = "ZXDOC_PROVIDER_PERSON";////changed by galaxypan@2017-08-02:合伙人子账户只能是个人
        /*List<GenericValue> roles = from("TblPartnerType").where("partyId", masterPartnerLogin.partyId).queryList();
        List<String> providerRoles = ["CASE_ROLE_ACCOUNTING", "CASE_ROLE_INVESTOR", "CASE_ROLE_STOCK", "CASE_ROLE_LAW", "CASE_ROLE_PARTNER"];
        for (GenericValue role : roles) {
            String roleType = role.getString("partnerType")
            if ("CASE_ROLE_OWNER".equals(roleType)) {
                personSecurityGroup = "ZXDOC_COMPANY_PARTNER";
                break;
            } else if (providerRoles.contains(roleType)) {
                personSecurityGroup = "ZXDOC_PROVIDER_PARTNER";
                break;
            }
        }*/
        if (UtilValidate.isNotEmpty(personSecurityGroup)) {
            paramMap = UtilMisc.toMap("userLoginId", partnerUsername, "groupId", personSecurityGroup);
            paramMap.putAll(systemAccount);
            result = dispatcher.runSync("addUserLoginToSecurityGroup", paramMap);
        }

        //建立合伙人与子账户之间的关系
        paramMap = UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", contactPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT");
        paramMap.putAll(systemAccount);
        result = dispatcher.runSync("createPartyRelationship", paramMap);

        //创建子账户联系方式
        //changed by galaxypan@2017-08-08:个人合伙人联系信息关联到主账号
        /*paramMap = UtilMisc.toMap("partyId", contactPartyId, "emailAddress", email);
        paramMap.putAll(systemAccount);
        result = dispatcher.runSync("createPartyEmailAddress", paramMap);
        if (UtilValidate.isNotEmpty(postCode) || UtilValidate.isNotEmpty(address)) {
            paramMap = UtilMisc.toMap("partyId", thispartyId, "city", "", "address1", address == null ? "" : address, "postalCode", postCode);
            paramMap.putAll(systemAccount);
            result = dispatcher.runSync("createPartyPostalAddress", paramMap);
        }
        paramMap = UtilMisc.toMap("partyId", thispartyId, "contactNumber", contactNo);
        paramMap.putAll(systemAccount);
        result = dispatcher.runSync("createPartyTelecomNumber", paramMap);*/

        GenericValue TblPersonalAuthentication = delegator.makeValidValue("TblPersonalAuthentication", UtilMisc.toMap("partyId", contactPartyId, "idCard", regNumber == null ? " " : regNumber, "qualifiNum", infoString == null ? " " : infoString));
        delegator.create(TblPersonalAuthentication);
        if (UtilValidate.isNotEmpty(regPhoto)) {
            //存储上传附件
            String[] dataResouceIds = StringUtils.split(regPhoto, ",");
            //保存合伙人子账户的认证信息文件
            List<GenericValue> listPersonQualifi = new ArrayList<>();
            for (String dataResourceId : dataResouceIds) {
                GenericValue TblPersonalAuthenticationAtta = delegator.makeValidValue("TblPersonalAuthenticationAtta", UtilMisc.toMap("partyId", contactPartyId, "dataResourceId", dataResourceId));
                listPersonQualifi.add(TblPersonalAuthenticationAtta);
            }
            delegator.storeAll(listPersonQualifi);
        }

        maps.put("fullName", contactName);
        maps.put("userLoginId", contactPartyId);
        maps.put("partyGroupId", partyId);
        maps.put("result", true);
        Map data = new HashMap();
        data.put("data",maps);
        dispatcher.runSync("createUserInOF",data);
        partyId = contactPartyId;
    }
    dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreTarget",partyId,"eventName","SCORE_RULE_REWARD"));
    delegator.removeByAnd("TblPhoneCode",UtilMisc.toMap("phoneNum",contactNo));
    map.put("result", "true");
    success.put("result", map);
    success.put("msg", "成功");
    success.put("data", map);
    return success;
}

private String checkCodes(String code1, String phoneNum, verificationCode,String code){
    int captchaCode = 1;
    if(code!=null) {
        String imgCode = "";
        for (int i = 2; i < 8; i++) {
            imgCode = imgCode + "" + code[i];
        }
        if (code1.equals(imgCode)) {
            captchaCode = 0;
        }
    }
    verificationCode = verificationCode == null ? "" : verificationCode;
    int phoneCode = 1;
    List<GenericValue> phoneNumList = EntityQuery.use(delegator).from("TblPhoneCode").where(UtilMisc.toMap("phoneNum",phoneNum)).orderBy("createCodeDate DESC").queryList();
    if(UtilValidate.isNotEmpty(phoneNumList)){
        GenericValue phoneInfo = phoneNumList.get(0);
        Timestamp createCodeDate = phoneInfo.getTimestamp("createCodeDate");
        Timestamp now = new Timestamp((new Date()).getTime());//验证码有效性判断
        if((now.getTime() - createCodeDate.getTime())/1000 <= 60 && phoneInfo.get("code").equals(verificationCode.trim())){
            phoneCode = 0;
        }
    }
//result：0正常，1图片验证码出错，2手机验证码出错，3图片和手机验证码都出错
    String result = "";
    if(captchaCode == 1 && phoneCode == 1){
        result = "短信验证码和图片验证码均出错，请重新获取验证码！";
    }else if(captchaCode == 0 && phoneCode == 1){
        result = "短信验证码出错，请重新申请验证码！";
    }else if(captchaCode == 1 && phoneCode == 0){
        result = "图片验证码出错，请重新获取验证码！";
    }
    return result;
}

/**
 * 通过审核，设置party状态为未认证，同时激活登录账户，允许用户进行登录
 * @return
 */
public Map<String, Object> passRegistration() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = context.get("registerUserLoginId");
    String[] ids = userLoginId.split(",");
    List<GenericValue> userLogins = from("UserLogin").where(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, Arrays.asList(ids))).queryList();
    List<String> partyIds = new ArrayList<>();
    for (GenericValue userLoginObj : userLogins) {

//        Map<String, Object> paramMap = UtilMisc.toMap("userLogin", userLogin, "partyId", userLoginObj.partyId, "statusId", "PARTY_IDENTIFIED");
//        result = runService("setPartyStatus", paramMap);

        //赋予对应的party OWNER角色，在上传附件时需要
        runService("createPartyRole", UtilMisc.toMap("userLogin", userLogin, "partyId", userLoginObj.partyId, "roleTypeId", "OWNER"));
        userLoginObj.setString("enabled", "Y");
        userLoginObj.store();
        partyIds.add(userLoginObj.partyId);
    }

    //发出邮件通知
//    List<GenericValue> emails = select("infoString").from("PartyNameContactMechView").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds), EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")).queryList();
//    List<String> emailAddresses = new ArrayList<>();
//    for (GenericValue email : emails) {
//        emailAddresses.add(email.getString("infoString"));
//    }
//    runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_PASS_REGISTRATION", "toAddress", StringUtil.join(emailAddresses, ","), "dataResourceIds", null));
    return success;
}

/**
 * 驳回账户审核
 */
public Map<String, Object> rejectRegistration() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = context.get("rejectUserLoginId");
    String[] ids = userLoginId.split(",");
    /*List<GenericValue> userLogins = from("UserLogin").where(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, Arrays.asList(ids))).queryList();
    List<String> partyIds = new ArrayList<>();
    for (GenericValue userLoginObj : userLogins) {
        Map<String, Object> paramMap = UtilMisc.toMap("userLogin", userLogin, "partyId", userLoginObj.partyId, "statusId", "PARTY_REJECT");
        result = runService("setPartyStatus", paramMap);
        userLoginObj.store();
        partyIds.add(userLoginObj.partyId);
    }*/
    for (int i = 0; i < ids.length; i++) {
        Map condition = UtilMisc.toMap("userLoginId", ids[i]);
        GenericValue userlogin = delegator.findByAnd("UserLoginAndPartyDetails", condition).get(0);
        userlogin.put("statusId", "PARTY_REJECT");
        userlogin.store();
    }
    return success;
}

/**
 * 通过实名认证，更新party状态，赋予securitygroup
 * @return
 */
public Map<String, Object> passQualification() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = context.get("registerUserLoginId");
    Boolean isPartner = context.get("isPartner");
    String[] ids = userLoginId.split(",");
    List<GenericValue> userLogins = from("UserLogin").where(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, Arrays.asList(ids))).queryList();
    String companyInitFolder = UtilProperties.getPropertyValue("zxdoc.properties", "companyInitFolder");
    JsonParser parser = new JsonParser();
    List<String> partyIds = new ArrayList<>();
    for (GenericValue userLoginObj : userLogins) {
        Map<String, Object> paramMap = UtilMisc.toMap("userLogin", userLogin, "partyId", userLoginObj.partyId, "statusId", "PARTY_IDENTIFIED");
        result = runService("setPartyStatus", paramMap);

        List<GenericValue> roles = from("PartyRole").where("partyId", userLoginObj.partyId).queryList();
        List<String> providerRoles = ["CASE_ROLE_ACCOUNTING", "CASE_ROLE_INVESTOR", "CASE_ROLE_STOCK", "CASE_ROLE_LAW"];
        boolean isCompany = false;
        for (GenericValue role : roles) {
            String roleType = role.getString("roleTypeId")
            if ("CASE_ROLE_OWNER".equals(roleType)) {
                runService("addUserLoginToSecurityGroup", UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginObj.userLoginId, "groupId", "ZXDOC_COMPANY"));
                isCompany = true;
                break;
            } else if (providerRoles.contains(roleType)) {
                runService("addUserLoginToSecurityGroup", UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginObj.userLoginId, "groupId", "ZXDOC_PROVIDER"));
                break;
            }
        }
        partyIds.add(userLoginObj.partyId);

        if (isCompany) {//企业相关初始化
            //初始化企业文件管理目录
            if (UtilValidate.isNotEmpty(companyInitFolder)) {
                JsonElement elem = parser.parse(companyInitFolder);
                JsonArray folders = elem.getAsJsonArray();
                createFolder(userLoginObj.partyId, null, folders);
            }
        }
    }

    //发出邮件通知
    List<GenericValue> emails = select("infoString").from("PartyNameContactMechView").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds), EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")).queryList();
    List<String> emailAddresses = new ArrayList<>();
    for (GenericValue email : emails) {
        emailAddresses.add(email.getString("infoString"));
    }
    runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_PASS_REGISTRATION", "toAddress", StringUtil.join(emailAddresses, ","), "dataResourceIds", null, "bodyParameters", UtilMisc.toMap("groupUserLoginId", userLoginId, "isPartner", isPartner)));

    return success;
}

public void createFolder(String userPartyId, String parentFolderId, JsonArray folders) {
    for (JsonElement ele : folders) {
        JsonObject folder = ele.getAsJsonObject();
        String name = folder.getAsJsonPrimitive("name").getAsString();
        JsonPrimitive permissionJson = folder.getAsJsonPrimitive("permission");
        String permission = permissionJson == null ? "" : permissionJson.getAsString();

        JsonPrimitive descriptionJson = folder.getAsJsonPrimitive("description")
        String description = descriptionJson == null ? "" : descriptionJson.getAsString();
        //调用文件管理模块服务创建目录
        Map<String, Object> result = runService("addUserFolder", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "partyId", userPartyId, "folderName", name, "parentFolderId", parentFolderId, "permissionStr", permission, "description", description))
        String folderId = result.get("folderId");
        if (folder.has("child")) {
            JsonArray subFolders = folder.getAsJsonArray("child");
            createFolder(userPartyId, folderId, subFolders);
        }
    }
}


public Map<String, Object> updateGroupAccount() {
    //changed by galaxypan@2017-08-07:仅用于编辑保存主账号
    Map maps = new HashMap();
    Map success = ServiceUtil.returnSuccess();
    String thisPartyId = context.get("partyId");
    String groupName = context.get("groupName");
    String partnerGroupName = context.get("partnerGroupName");
    String contactNo = context.get("contactNo");
    String email = context.get("email");
    String postCode = context.get("area");
    String address = context.get("address");
    String username = context.get("username");
    String password = context.get("password");
    String category = context.get("category");//类型
    String contactName = context.get("contactName");
//    String isPartner = context.get("isPartner");
    GenericValue userLogin = context.get("userLogin");
    String regNumber = context.get("creditCode") == null ? "未填写" : context.get("creditCode");
    String infoString = context.get("range") == null ? "未填写" : context.get("range");
    Map<String, String> regPhoto = context.get("regPhoto");
    String webAddress = context.get("webAddress");
    Map result;
    List<EntityCondition> con = [];
    con.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, thisPartyId));
    List<GenericValue> emailinfo = EntityQuery.use(delegator).from("EmailAddressInfo").where(con).cache().queryList();
    //以管理员账户登录后才能执行相关service调用
    Map<String, Object> systemAccount = UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne());
    //更新企业机构联系方式
    String Acc = emailinfo[emailinfo.size() - 1].get("emailId");
    //修改邮箱
    Map<String, Object> partyContact = from("PartyContactMech").where("contactMechId", Acc).queryOne();
    Map paramMap = UtilMisc.toMap("contactMechId", Acc, "emailAddress", email, "patyId", thisPartyId);
    paramMap.putAll(systemAccount);
    paramMap.putAll(partyContact);
    result = runService("updatePartyEmailAddress", paramMap);
    //编辑地址
    List<GenericValue> addresses = EntityQuery.use(delegator).from("PostalAddressInfo").where(con).cache().queryList();
    String Add = addresses[addresses.size() - 1].get("postalId");
    GenericValue postalAdress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", Add));
    postalAdress.put("postalCode", postCode);
    postalAdress.put("address1", address == null ? "" : address);
    postalAdress.store();
    //更新联系人信息
    List<GenericValue> perId = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdFrom", thisPartyId,"partyRelationshipTypeId","ORG_CONTACT").cache().queryList();
    GenericValue person = perId[perId.size() - 1];
    String personId = person.get("partyIdTo");
    GenericValue per = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", personId));
    per.put("firstName", contactName);
    per.put("fullName", contactName);
    per.store();
    //更新联系电话
    List<GenericValue> mech = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", personId).cache().queryList();
    String mechId = mech[mech.size() - 1].get("contactMechId");
    GenericValue tel = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", mechId));
    tel.put("contactNumber", contactNo);
    tel.store();
    //更新机构或企业名
    GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", thisPartyId), false);
    String oldGroupName = group.getString("groupName");
    if(UtilValidate.isNotEmpty(groupName)){
        group.put("groupName", groupName);
    }
    group.put("partnerGroupName", partnerGroupName);
    group.store();

    //changed by galaxypan@2017-08-19:如果是更改的主机构名称，需要查询是否有相关的合伙人，同时更新合伙人中关联名称
    if(UtilValidate.isEmpty(group.getString("partnerType"))){
        List<GenericValue> partners = EntityQuery.use(delegator).from("PartyGroup").where(EntityCondition.makeCondition(EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, oldGroupName), EntityCondition.makeCondition("partnerType", EntityOperator.NOT_EQUAL, null))).queryList();
        if(UtilValidate.isNotEmpty(partners)){
            for (GenericValue partner : partners) {
                partner.setString("groupName", groupName);
                partner.store();
            }
        }
    }

    List<GenericValue> UserLoginAndContact = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where("partyId",thisPartyId).distinct().queryList();
    String roleType = "";
    if(UtilValidate.isNotEmpty(UserLoginAndContact)){
        roleType = UserLoginAndContact.get(0).get("roleTypeId");
    }
    //合伙人，应该同时更新主账户和子账户
    //changed by galaxypan@2017-08-07:此处仅更新主账号信息，合伙人子账号信息在子账号更新中实现。
    /*if(roleType=="CASE_ROLE_PARTNER")
    {
        thisPartyId = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdFrom",thisPartyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT").queryOne().get("partyIdTo");
        //更新联系人
        GenericValue pperson = EntityQuery.use(delegator).from("Person").where("partyId",thisPartyId).queryOne();
        pperson.put("firstName", contactName);
        pperson.put("fullName", contactName);
        pperson.store();

        //更新联系电话
        GenericValue ptele = EntityQuery.use(delegator).from("TelNumber").where("partyId",thisPartyId).queryOne();
        String teleId = ptele.get("contactMechId");
        GenericValue ptel = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId",teleId).queryOne();
        ptel.put("contactNumber",contactNo);
        ptel.store();

        //更新省市区
        GenericValue padd = EntityQuery.use(delegator).from("PostalAddressInfo").where("partyId",thisPartyId).queryOne();
        String addId = padd.get("postalId");
        GenericValue paddress = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId",addId).queryOne();
        paddress.put("postalCode",postCode);
        paddress.put("address1",address==null?"":address);
        paddress.store();

        //更新email
        GenericValue psub = EntityQuery.use(delegator).from("SubAccountAndInformation").where("partyId",thisPartyId).queryOne();
        String emailId = psub.get("emailId");
        GenericValue peamil = EntityQuery.use(delegator).from("ContactMech").where("contactMechId",addId).queryOne();
        peamil.put("infoString",email);
        peamil.store();
    }*/
    success.put("msg","成功");
    success.put("data", maps);
    return success;
}
//保存或编辑子账号
public Map<String, Object> saveSubAccounts() {

    Map success = ServiceUtil.returnSuccess();
    String thisPartyId = context.get("partyId");
    String emailId = context.get("emailId");
    String postalId = context.get("postalId");
    String telId = context.get("telId");
    String contactNo = context.get("contactNo");
    String email = context.get("email");
    String postCode = context.get("area");
    String address = context.get("address");
    String username = context.get("username");
    String password = context.get("password");
    String category = context.get("category");//类型
    String contactName = context.get("contactName");
    String groupName = context.get("groupName");
    GenericValue userLogin = context.get("userLogin");
    Map<String, Object> systemAccount = UtilMisc.toMap("userLogin", userLogin);
    if (UtilValidate.isEmpty(thisPartyId)) {
        GenericValue user = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId", username)).queryOne()
        if(UtilValidate.isNotEmpty(user)){
            throw new RuntimeException("登录账户已被占用，请重新命名！");
        }

        //设置机构状态为DISABLE状态，在审核后修改为已认证
        Map<String, Object> paramMap = UtilMisc.toMap("firstName", contactName, "fullName", contactName, "statusId", "PARTY_IDENTIFIED", "openFireJid", username + "@" + UtilProperties.getPropertyValue("zxdoc", "jabber_server"));
        paramMap.putAll(systemAccount);
        Map<String, Object> result = runService("createPerson", paramMap);
        String partyId = result.get("partyId");

        GenericValue openFire = EntityQuery.use(delegator).from("Person").where("partyId",partyId).queryOne();
        openFire.put("openFireJid",partyId + "@" + UtilProperties.getPropertyValue("zxdoc", "jabber_server"));
        openFire.store();

        //创建企业机构联系方式
        paramMap = UtilMisc.toMap("partyId", partyId, "emailAddress", email);
        paramMap.putAll(systemAccount);
        result = runService("createPartyEmailAddress", paramMap);
        if (UtilValidate.isNotEmpty(postCode) || UtilValidate.isNotEmpty(address)) {
            paramMap = UtilMisc.toMap("partyId", partyId, "city", "", "address1", address == null ? "" : address, "postalCode", postCode);
            paramMap.putAll(systemAccount);
            result = runService("createPartyPostalAddress", paramMap);
        }
        paramMap = UtilMisc.toMap("partyId", partyId, "contactNumber", contactNo);
        paramMap.putAll(systemAccount);
        result = runService("createPartyTelecomNumber", paramMap);

        if(groupName==null)
        {
            groupName = userLogin.partyId;
        }
        //建立机构与子账户之间的关系
        paramMap = UtilMisc.toMap("partyIdFrom", groupName, "partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT");
        paramMap.putAll(systemAccount);
        result = runService("createPartyRelationship", paramMap);

        //创建登录账户
        paramMap = UtilMisc.toMap("partyId", partyId, "userLoginId", username, "enabled", "Y", "currentPassword", password, "currentPasswordVerify", password);
        paramMap.putAll(systemAccount);
        result = runService("createUserLogin", paramMap);

        //分配securitygroup
        String personSecurityGroup = null;
        List<GenericValue> roles = from("PartyRole").where("partyId", groupName).queryList();
        List<String> providerRoles = ["CASE_ROLE_ACCOUNTING", "CASE_ROLE_INVESTOR", "CASE_ROLE_STOCK", "CASE_ROLE_LAW"];
        for (GenericValue role : roles) {
            String roleType = role.getString("roleTypeId")
            if ("CASE_ROLE_OWNER".equals(roleType)) {
                personSecurityGroup = "ZXDOC_COMPANY_PERSON";
                break;
            } else if (providerRoles.contains(roleType)) {
                personSecurityGroup = "ZXDOC_PROVIDER_PERSON";
                break;
            }
        }
        if (UtilValidate.isNotEmpty(personSecurityGroup)) {
            paramMap = UtilMisc.toMap("userLoginId", username, "groupId", personSecurityGroup);
            paramMap.putAll(systemAccount);
            result = runService("addUserLoginToSecurityGroup", paramMap);
        }

        //存储认证信息
        GenericValue partnerAuth = delegator.makeValidValue("TblPersonalAuthentication", UtilMisc.toMap("partyId", partyId, "idCard", " ", "qualifiNum", " "));
        delegator.create(partnerAuth);

        //创建openfire聊天账号时需要的参数
        dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreTarget", partyId, "eventName","SCORE_RULE_REWARD"));
        Map map = new HashMap();
        map.put("fullName", contactName);
        map.put("userLoginId", partyId);
        map.put("partyGroupId", context.get("userLogin").get("partyId"));
        map.put("result", true);
        Map data = new HashMap();
        data.put("data",map);
        runService("createUserInOF",data);
        success.put("data", map);
        return success;
    } else {
        //修改联系人
        Map paramMap = UtilMisc.toMap("partyId", thisPartyId);
        GenericValue person = delegator.findByPrimaryKey("Person", paramMap);
        person.put("fullName", contactName);
        person.put("firstName", contactName);
        person.store();

        //修改密码
        if(UtilValidate.isNotEmpty(password)){
            paramMap = UtilMisc.toMap("userLoginId", username, "currentPassword", password, "newPassword", password, "newPasswordVerify", password);
            paramMap.putAll(systemAccount);
            result = runService("updatePassword", paramMap);

        }

        //修改联系电话
        paramMap = UtilMisc.toMap("contactMechId", telId);
        GenericValue tele = delegator.findByPrimaryKey("TelecomNumber", paramMap);
        tele.put("contactNumber", contactNo);
        tele.store();

        //修改email
        paramMap = UtilMisc.toMap("contactMechId", emailId);
        GenericValue emailInfo = delegator.findByPrimaryKey("ContactMech", paramMap);
        emailInfo.put("infoString", email);
        emailInfo.store();

        //编辑地址
        List<EntityCondition> con = [];
        con.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, thisPartyId));
        List<GenericValue> addresses = EntityQuery.use(delegator).from("PostalAddressInfo").where(con).cache().queryList();
        String Add = addresses[addresses.size() - 1].get("postalId");
        GenericValue postalAdress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", Add));
        postalAdress.put("postalCode", postCode);
        postalAdress.put("address1", address == null ? "" : address);
        postalAdress.store();

        Map map = new HashMap();
        success.put("data", map);
        return success;
    }

}

public Map<String, Object> uploadAvatar() {
    Map success = ServiceUtil.returnSuccess();
    Long attaSize = context.get("_atta_size");
    Map map = new HashMap();
    if(attaSize > 5 * 1024 * 1024) {
        map.put("msg", "图片大小请小于5兆！");
    }else{
        List<String> dataResourceIds = DataServices.storeAllDataResourceInMap(dispatcher, delegator, userLogin, context);
        if (UtilValidate.isNotEmpty(dataResourceIds)) {
            GenericValue userLogin = context.get("userLogin");
            GenericValue party = from("Party").where("partyId", userLogin.getString("partyId")).queryOne();
            String avatar = dataResourceIds.get(0);
            party.setString("avatar", avatar)
            party.store();
            map.put("id", avatar);
            map.put("msg", "更新成功！");
        }
    }
    success.put("data", map);
    return success;
}

//打开续费页面
public Map<String, Object> renewVip() {
    Map success = ServiceUtil.returnSuccess();
    Map map = new HashMap();
    String partyIds = context.get("partyId");
    String partyId = "";
    List partyIdList = partyIds.split(",");
    List userLoginList = new ArrayList();
    for (int i = 0; i < partyIdList.size(); i++) {
        partyId = partyIdList[i];
        GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
        String userLogins = "公司名出错";
        if (partyGroup != null) {
            userLogins = partyGroup.get("groupName");
        }
        userLoginList.add(userLogins);
    }
    success.put("userLogins", userLoginList);
    success.put("partyId", partyIds);
    return success;
}

//账户续费
public Map<String, Object> renewAccount() {
    Map success = ServiceUtil.returnSuccess();
    Map map = new HashMap();
    String partyIds = context.get("partyId");
    List partyIdList = partyIds.split(",");
    String partyId = "";
    String times = context.get("times");
    int months = Integer.parseInt(times);
    for (int i = 0; i < partyIdList.size(); i++) {
        partyId = partyIdList[i];
        GenericValue cueTime = delegator.findByPrimaryKey("TblVipCueTime", UtilMisc.toMap("partyId", partyId));

        //获得当前时间
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowtime = format.format(date);
        //没有续费记录，新增续费记录
        if (cueTime == null) {
            //增加到期时间
            Calendar c = Calendar.getInstance();//获得一个日历的实例
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dates = null;
            try {
                dates = sdf.parse(nowtime);//初始日期
            } catch (Exception e) {

            }
            c.setTime(dates);//设置日历时间
            c.add(Calendar.MONTH, months);
            String remainTime = sdf.format(c.getTime());
            Date remain = sdf.parse(remainTime);
            Map con = UtilMisc.toMap("partyId", partyId, "CueTime", new java.sql.Date(remain.getTime()), "hasInfo", "0");
            GenericValue cue = delegator.makeValue("TblVipCueTime", con);
            cue.create();
        }
        //存在续费记录，修改到期时间
        else {
            String time = cueTime.get("CueTime");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date timeCue = sdf.parse(time);
            Date nows = sdf.parse(nowtime);
            //如果到期时间比当前时间小，在当前时间上进行更新
            if (timeCue.getTime() < nows.getTime()) {
                time = nowtime;
            }
            //增加到期时间
            Calendar c = Calendar.getInstance();//获得一个日历的实例
            Date dates = null;
            try {
                dates = sdf.parse(time);//初始日期
            } catch (Exception e) {

            }
            c.setTime(dates);//设置日历时间
            c.add(Calendar.MONTH, months);
            String remainTime = sdf.format(c.getTime());
            Date remain = sdf.parse(remainTime);
            cueTime.put("CueTime", new java.sql.Date(remain.getTime()));
            cueTime.put("hasInfo", "0");
            cueTime.store();
        }

        //登录权限释放
        String userId = partyId;
        //修改主账号账号
        GenericValue party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", userId)).queryOne();
        party.put("statusId", "PARTY_IDENTIFIED");
        party.store();
        GenericValue userlogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", userId)).queryOne();
        userlogin.put("enabled", "Y");
        userlogin.store();
        //修改子账户状态
        List<GenericValue> partylist = EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom", userId)).queryList();
        for (int j = 0; j < partylist.size(); j++) {
            userId = (String) partylist.get(j).get("partyIdTo");
            party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", userId)).queryOne();
            if (party != null) {
                party.put("statusId", "PARTY_IDENTIFIED");
                party.store();
            }
            userlogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", userId)).queryOne();
            if (userlogin != null) {
                userlogin.put("enabled", "Y");
                userlogin.store();
            }
        }
    }


    success.put("data", map);
    return success;
}


public Map<String, Object> isHideImg() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");
    GenericValue partyType = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", partyId)).queryOne();
    String partyTypeId = partyType.get("partyTypeId");
    boolean result = false;
    //只有子账号才显示图标
    if (partyTypeId.equals("PERSON")) {
        //changed by galaxypan@2017-08-15:只要是子账户均能聊天，不区分是否是合伙人
        result = true;
        //子账户需要判断是不是合伙人，合伙人也不能创建聊天
        /*GenericValue father = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdTo", partyId)).queryOne();
        //如果不存在主账户，该账户为问题账户
        if (father != null) {
            partyId = father.get("partyIdFrom");
            List<GenericValue> group = EntityQuery.use(delegator).from("rolePartyGroups").where(UtilMisc.toMap("partyId", partyId)).queryList();
            String roleTypeId = group.get(0).get("roleTypeId");
            *//*if (!roleTypeId.equals("CASE_ROLE_PARTNER")) {
                result = true;
            }*//*
            result = true;
        }*/
    }

    Map map = new HashMap();
    map.put("result", result);
    success.put("data", map);
    return success;
}

public Map<String, Object> deleteAccount()
{
    String userLoginId = context.get("userLoginId");
    //移除session中的登录账户
    LoginWorker.userSessions.remove(userLoginId);
    GenericValue user = EntityQuery.use(delegator).from("UserLogin").where("userLoginId",userLoginId).queryOne();
    String partyId = user.get("partyId");
    user.put("enabled","S");
    user.store();
    //子账户删除
    List<GenericValue> subLogins = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT")).queryList();
    if(subLogins!=null && subLogins.size()!=0)
    {
        for (GenericValue subLogin:subLogins)
        {
            String subId = subLogin.get("partyIdTo");
            GenericValue sub = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId",subId)).queryOne();
            if (sub!=null) {
                sub.setString("enabled", "S");
                sub.store();
                LoginWorker.userSessions.remove(sub.get("userLoginId"));
            }
        }
    }
    //删除合伙人主账户
    GenericValue partnerLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId","[" + parameters.userLoginId + "]")).queryOne();
    if(partnerLogin!=null) {
        partnerLogin.setString("enabled", "S");
        partnerLogin.store();
        LoginWorker.userSessions.remove(partnerLogin.get("userLoginId"));
    }
    Map success = ServiceUtil.returnSuccess();
    success.put("data","删除成功");
    return success;
}


//管理员新建子账户
public Map<String, Object> saveCompanySubInfo()
{
    Map success = ServiceUtil.returnSuccess();
    String groupName = context.get("groupName");
    String contactName = context.get("contactName");
    String contactNo = context.get("contactNo");
    String area = context.get("area");
    String address = context.get("address");
    String username = context.get("username");
    String password = context.get("password");
    String email = context.get("email");
    runService("saveSubAccounts",UtilMisc.toMap("groupName",groupName,"contactName",contactName,"contactNo",contactNo,"area",area,"address",address,"username",username,"password",password,"email",email));
    return success;
}

//管理员编辑子账户
public Map<String,Object> editCompanySubInfo()
{
    Map success = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    String emailId = context.get("emailId");
    String postalId = context.get("postalId");
    String telId = context.get("telId");
    String contactName = context.get("contactName");
    String contactNo = context.get("contactNo");
    String area = context.get("area");
    String address = context.get("address");
    String email = context.get("email");
    runService("saveSubAccounts",UtilMisc.toMap("partyId",partyId,"emailId",emailId,"postalId",postalId,"telId",telId,"contactName",contactName,"contactNo",contactNo,"area",area,"address",address,"email",email));
    return success;
}

public Map<String, Object> saveEmailInfo()
{
    Map success = ServiceUtil.returnSuccess();
    String emailId = context.get("emailId");
    String info = context.get("info");
    info = info.replace('&lt;','<');
    info = info.replace('&gt;','>');
    GenericValue emailInfo = EntityQuery.use(delegator).from("EmailTemplateSetting").where("emailTemplateSettingId",emailId).queryOne();
    String path = emailInfo.get("bodyScreenLocation");
    ScreenFactory screen = new ScreenFactory();
    String screenName = screen.getScreenNameFromCombined(path);
    String resourceName = screen.getResourceNameFromCombined(path);
    ModelScreen modelScreen = screen.getScreenFromLocation(resourceName,screenName);
    ModelScreenWidget.Section section = modelScreen.section;
    List<ModelScreenWidget> subWidgets = section.subWidgets;
    ModelScreenWidget.PlatformSpecific subWidget = subWidgets.get(0);
    Map<String, ModelScreenWidget> tsubWidgets = subWidget.subWidgets;
    HtmlWidget htmlWidget = tsubWidgets.get("html");
    List<ModelScreenWidget> htmlList = htmlWidget.subWidgets;
    HtmlWidget.HtmlTemplate htmlscreen = htmlList.get(0);
    String location = htmlscreen.getLocation(new HashMap());
    File file = FileUtil.getFile(location);
    FileWriter writer = null;
    String header = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
            "</head>\n" +
            "<body>";
    String end = "</body>\n" +
            "</html>";
    info = header + info +  end;
    try {
        // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件,false表示覆盖的方式写入
        writer = new FileWriter(file, false);
        writer.write(info);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            if(writer != null){
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    success.put("data","编辑成功");
    return success;
}
