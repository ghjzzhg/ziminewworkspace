import org.apache.commons.collections.map.HashedMap
import org.apache.commons.lang.StringUtils
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.zxdoc.OpenFireRestApi

String caseId = parameters.caseId;

//根据聊天室id获取caseId
String chatRoomId = parameters.chatRoomJID;
if(UtilValidate.isEmpty(chatRoomId)){
    chatRoomId = context.chatRoomId;
}
if(UtilValidate.isNotEmpty(chatRoomId)){
    GenericValue chatRecord = from("TblCaseCooperationRecord").where("chatRoomId", chatRoomId).queryOne();
    if(chatRecord != null){
        caseId = chatRecord.getString("caseId");
        context.chatRoomName = chatRecord.getString("chatRoomName");
    }
    //获取聊天室成员名称映射表
    List<Map<String, Object>> members = new ArrayList<>();
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/chatrooms/" + chatRoomId.split("@")[0];
    Map<String, Object> map = OpenFireRestApi.doExecute("", url, "", "get");
    if(map.get("status") && StringUtils.isNotBlank(map.get("result").toString())) {
        String result = map.get("result").toString();
        Document document = DocumentHelper.parseText(result);
        Element rootElement = document.getRootElement();
        List<Element> membersElements = rootElement.elements("members");
        List<Element> memberElements = membersElements.get(0).elements("member");
        for (Element element : memberElements) {
            Map<String, Object> memberData = new HashedMap();
            members.add(memberData);
            String memberJID = element.getText();
            List<GenericValue> fullList = delegator.findByAnd("FullNameGroupName", UtilMisc.toMap("openFireJid", memberJID), null, false)
            if (UtilValidate.isNotEmpty(fullList)) {
                GenericValue memberInfo = fullList.get(0);//openFireJid不是主键但是不会重复
                memberData.put("name", UtilValidate.isNotEmpty(memberInfo.get("fullName")) ? memberInfo.get("groupName") + "-" + memberInfo.get("fullName") : memberJID);
                memberData.put("fullName", UtilValidate.isNotEmpty(memberInfo.get("fullName")) ? memberInfo.get("fullName") : memberJID);
                memberData.put("groupName", memberInfo.get("groupName"));
                memberData.put("partyId", memberInfo.getString("partyId"));
            }
        }
    }
    context.roomMembers = members;
}

GenericValue companyRole = from("casePartyIdNameDescription").where("caseId", caseId, "roleTypeId", "CASE_ROLE_OWNER").queryOne();
GenericValue userLogin = (GenericValue)context.get("userLogin");
String loginUserPartyId = userLogin.get("partyId");
GenericValue userParty = delegator.findOne("Party", UtilMisc.toMap("partyId", loginUserPartyId), true);

context.isRoot = false;
context.isPartyGroup = true;
String partyTypeId = userParty.get("partyTypeId");
if("PARTY_GROUP".equals(partyTypeId)){
    context.isRoot = true;//主账号
}
String partyGroupId = loginUserPartyId;
context.isManagerParty = true;
context.isyManager = false;//changed by galaxypan@2017-08-28:该参与方是否已有项目经理（多余的判断，不可能出现没有项目经理的情况）
if(!context.isRoot){//合伙人可能以挂靠机构的成员身份加入CASE
    GenericValue userPartyMember = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("partyId",loginUserPartyId,"caseId", caseId)).queryOne();
    if(userPartyMember == null){
        throw new RuntimeException("非CASE成员，无权限!");
    }
    partyGroupId = userPartyMember.getString("groupPartyId");
    context.isPartyGroup = false;
    if("CASE_PERSON_ROLE_MANAGER".equals(userPartyMember.getString("roleTypeId"))){
        context.isManagerParty = false;
    }
}

GenericValue caseManager = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("groupPartyId",partyGroupId,"caseId", caseId,"roleTypeId","CASE_PERSON_ROLE_MANAGER")).queryOne();
if(caseManager != null){
    context.isyManager = true;
}

GenericValue currentUserRole = from("casePartyIdNameDescription").where("caseId", caseId, "partyId", partyGroupId).queryOne();
String roleType = currentUserRole.getString("roleTypeId");//参与角色

GenericValue caseEntity = from("TblCase").where("caseId", caseId).queryOne();
String caseCategory = caseEntity.getString("caseCategory");
//List<String> shareFolderPath = [];

if(UtilValidate.isEmpty(caseCategory)){
    caseCategory = caseEntity.getString("caseTemplate");
}
//根据目录可见参与方设置查询该参与方能够查看的企业目录
/*
List<GenericValue> folders;
if(UtilValidate.isNotEmpty(caseCategory)) {
    folders = EntityQuery.use(delegator).from("TblCaseCategoryFolder").where(EntityCondition.makeCondition("caseCategory", EntityOperator.EQUALS, caseCategory), EntityCondition.makeCondition("roles", EntityOperator.LIKE, "%" + roleType + "%")).queryList();
}else{
    folders = EntityQuery.use(delegator).from("TblCaseShareFolder").where(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId), EntityCondition.makeCondition("roles", EntityOperator.LIKE, "%" + roleType + "%")).queryList();
}

for (GenericValue folder : folders) {
    shareFolderPath.add(folder.getString("folderPath"));
}
*/

context.fileSharePartyId = companyRole.getString("partyId");
//context.filePathList = shareFolderPath.join(",");
context.filePathList = "";

context.caseCreateTime = caseEntity.get("createdStamp");
context.caseFolderId = caseEntity.get("folderId");
if(UtilValidate.isNotEmpty(caseEntity.getString("caseCategory"))){
    GenericValue caseType = from("Enumeration").where("enumId", caseEntity.getString("caseCategory")).queryOne();
    context.caseType = caseType.getString("description");
}

GenericValue companyAddress = from("PostalAddressInfo").where("partyId", context.fileSharePartyId).queryOne();
if(companyAddress != null){
    String postalCode = companyAddress.getString("area");
    if(UtilValidate.isNotEmpty(postalCode)){
        GenericValue geo = from("Geo").where("geoCode", postalCode).queryOne();
        if(geo != null){
            context.area = geo.getString("geoName");
        }
    }
}

context.caseId = caseId;
