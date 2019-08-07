package org.ofbiz.zxdoc

import net.sf.json.JSONObject
import org.apache.commons.collections.map.HashedMap
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpResponse
import org.apache.http.client.methods.*
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.io.SAXReader
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.content.data.DataFileDescription
import org.ofbiz.content.data.FileManagerFactory
import org.ofbiz.content.data.FileTypeManager
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import javax.xml.rpc.ServiceException
import javax.xml.stream.events.Attribute
import java.sql.Timestamp
import java.text.SimpleDateFormat

//调用REST API
/**
 *
 * @param param参数（主要是xml字符串或json字符串）
 * @param url（请求地址）
 * @param format（xml， json）
 * @param httpMethod（请求方式）
 * @return
 */
private Map<String, Object> doExecute(String param, String url, String format, String httpMethod) {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpRequestBase httpRequestBase = null;
    switch (httpMethod) {
        case "post": httpRequestBase = new HttpPost(url); break;
        case "get": httpRequestBase = new HttpGet(url); break;
        case "put": httpRequestBase = new HttpPut(url); break;
        case "delete": httpRequestBase = new HttpDelete(url); break;
        default: httpRequestBase = null;
    }
    httpRequestBase.setHeader("Authorization", UtilProperties.getPropertyValue("zxdoc", "rest_secret_key"));
    if(StringUtils.isNotBlank(param)){
        StringEntity stringEntity = new StringEntity(param, "utf-8");
        stringEntity.setContentEncoding("UTF-8");
        httpRequestBase.setEntity(stringEntity);
        "json".equals(format) ? stringEntity.setContentType("application/json") : stringEntity.setContentType("application/xml");
    }
    switch (format) {
        case "json":
            httpRequestBase.setHeader(HTTP.CONTENT_TYPE, "application/json");
            break;
        case "xml":
            httpRequestBase.setHeader(HTTP.CONTENT_TYPE, "application/xml");
            break;
        default :
            httpRequestBase.setHeader(HTTP.CONTENT_TYPE, "text/html");
            break;
    }
    HttpResponse response = httpClient.execute(httpRequestBase);
    String result = EntityUtils.toString(response.getEntity(), "utf-8");
    String code = response.getStatusLine().statusCode;
    if ("200".equals(code) || "201".equals(code)) {
        return UtilMisc.toMap("status", true, "result", result != null ? result : "");
    } else {
        return UtilMisc.toMap("status", false, "result", result != null ? result : "");
    }
}
//在创建oa账号时添加openfire账号
public Map<String, Object> createUserInOF() {
    Map data = (HashMap) context.get("data");
    String username = data.get("userLoginId");
    String name = data.get("fullName");
    boolean isUpdate = data.get("update");
    //String password = UUID.randomUUID().toString();
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host", "http://192.168.1.123:9090") + "/plugins/restapi/v1/users" + (isUpdate ? ("/" + username): "");
    //生成随机密码，并加入数据库
    String password = (Integer) (Math.random() * 9 + 1);
    for (int i = 0; i < 5; i++) {
        password += (Integer) (Math.random() * 10);
    }
    GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId",username).distinct().queryOne();
    person.put("openFirePassword",password);
    person.store();
    String param =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<user>\n" +
                    "    <username>" + username + "</username>\n" +
                    (isUpdate ? "": "    <password>" + password + "</password>\n") +
                    "    <name>" + name + "</name>\n" +
                    "    <email></email>\n" +
                    "</user>"
    if (doExecute(param, url, "xml", isUpdate ? "put": "post").get("status")) {
        //TODO addUserToGroup(username, data.get("partyGroupId"));
    }
}

private void addUserToGroup(String username, String groupName) {
    String param = "";
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/users/" + username + "/groups/" + groupName;
    doExecute(param, url, "xml", "post");
}
//在oa的group创建时添加openfire的group
public Map<String, Object> createGroupInOF() {
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host", "http://192.168.1.110:9090") + "/plugins/restapi/v1/groups";
    String param = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<group>\n" +
            "    <name>" + context.get("groupName") + "</name>\n" +
            "    <description></description>\n" +
            "</group>";
    doExecute(param, url, "xml", "post");
}
//判断是否已登录openFire
private String isLoginOpenFire(String userLoginId){
    String isLogin = "false";
    Map<String, Object> userSession = doExecute("", UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/sessions/" + userLoginId, "", "get");
    String sessionResult = userSession.get("result") + "";
    println("------------3---------"+sessionResult);
    if(sessionResult.contains("<username>")){
        isLogin = "true";
    }
    return isLogin;
}
//登录xmpp服务器
public Map<String, Object> xmppLogin() {
    Map<String, Object> result = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String chatRoomJID = context.get("chatRoomJID");
    //判断是否可以邀请用户
    String hideMem = context.get("hideMem");
    if(UtilValidate.isNotEmpty(hideMem))
    {
        hideMem = "false";
    }
    String userLoginId = userLogin.get("partyId");
    //TODO 根据userLoginId在oa数据库中找到openfire登录密码
    String httpBase = UtilProperties.getPropertyValue("zxdoc", "http_base", "http://192.168.1.110:7070/http-bind/");
    String jabberServer = UtilProperties.getPropertyValue("zxdoc", "jabber_server", "rextec");
    GenericValue person = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("partyId",userLoginId)).queryOne();
    String openfireNo = "";
    String password = "";
    if(UtilValidate.isNotEmpty(person)){
        openfireNo = person.get("openFireJid");
        password = person.get("openFirePassword")==null?"admin":person.get("openFirePassword");
    }
    try{
        result.put("data", UtilMisc.toMap("userLoginId", userLoginId, "password", password, "httpBase", httpBase, "jabberServer", jabberServer, "chatRoomJID", chatRoomJID, "isLogin", isLoginOpenFire(userLoginId),"hideMem",hideMem,"openfireNo",openfireNo));
    }catch (Exception e){
        result.put("data", new HashMap());
        Debug.logError(e, "xmppLogin");
    }
    return result;
}
//从聊天服务器下线
public Map<String, Object> deleteOpenFireSessions(){
    Map<String, Object> resultMap = new HashMap<>();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String userLoginId = userLogin.get("userLoginId");
    try{
        if(isLoginOpenFire(userLoginId) == "true"){
            resultMap = doExecute("", UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/sessions/" + userLoginId, "", "delete");
        }
    }catch (Exception e){
        Debug.logError(e, "deleteOpenFireSessions");
    }
    return null;
}
//创建聊天室

public Map<String, Object> createChatRoom() {
    String domain = UtilProperties.getPropertyValue("zxdoc", "jabber_server");
    Map<String, Object> result = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    //throw new RuntimeException("NOOOOO");
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host", "http://192.168.1.110:9090") + "/plugins/restapi/v1/chatrooms";
    String roomName = new Date().getTime().toString();//即聊天室的id，唯一标识聊天室的字段
    String naturalName = context.get("chatRoomName");
    String subject = context.get("chatRoomName");//聊天室主题
    String chatRoomDescription = context.get("chatRoomDescription");
    JSONObject chatRoom = new JSONObject();
    chatRoom.put("roomName", roomName);
    chatRoom.put("naturalName", naturalName);
    chatRoom.put("description", UtilValidate.isEmpty(chatRoomDescription) ? subject : chatRoomDescription);
    chatRoom.put("subject", subject);//主题
    chatRoom.put("maxUsers", 30);
    chatRoom.put("persistent", true);
    chatRoom.put("membersOnly", true);//仅对成员开放
    chatRoom.put("publicRoom", false);//列出目录中的房间(这样只对成员可见)

    JSONObject broadcastPresenceRole = new JSONObject();//其 Presence 是 Broadcast 的角色
    List<String> broadcastPresenceRoles = new ArrayList();
    broadcastPresenceRoles.add("moderator");
    broadcastPresenceRoles.add("participant");
    broadcastPresenceRoles.add("visitor");
    broadcastPresenceRole.put("broadcastPresenceRole", broadcastPresenceRoles);
    chatRoom.put("broadcastPresenceRoles", broadcastPresenceRole);

    JSONObject member = new JSONObject();//成员
    Map memberMap = context.get("chatRoomMember");
    List<String> members = new ArrayList();
    for(String memberName: memberMap.keySet()){
        String loginId = memberMap.get(memberName);
        GenericValue person = EntityQuery.use(delegator).from("UserLogin").where("userLoginId",loginId).queryOne();
        members.add(person.get("partyId") + "@" + domain);
    }
    member.put("member", members);
    chatRoom.put("members", member);

    //将此次协作信息保存到TblCaseCooperationRecord
    Map groupMap = context.get("partyGroupName");
    List<String> groupNames = new ArrayList<>();
    List<String> groupIds = new ArrayList<>();
    for(String groups: groupMap.keySet()){
        if(memberMap.keySet().contains(groups)){
            groupNames.add(groupMap.get(groups) + "");
            groupIds.add(groups);
        }
    }

    //获取创建人（登录人）主键，并将该用户设置为群管理员 20161213
    String partyId = context.get("userLogin").get("partyId");
    GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where("partyId",partyId).distinct().queryOne();
    delegator.create(delegator.makeValidValue("TblCaseCooperationRecord", UtilMisc.toMap(
            "administrator",person.get("groupId"),
            "caseId", caseId,
            "chatRoomId", roomName + "@conference." + domain,
            "chatRoomName", naturalName,
            "groupNames", groupNames.join(","),
            "groupIds", groupIds.join(","))));
    if(doExecute(chatRoom.toString(), url, "json", "post").get("status")){
        msg = "创建成功";
    }else{
        msg = "创建失败";
    }
    result.put("data", msg);
    result.put("caseId", caseId);
    result.put("chatRoomJID", roomName + "@conference." + domain);
    result.put("chatRoomName", naturalName);
    if(members.size() > 1){
        members.remove(partyId + "@" + domain);
    }else{
        members = new ArrayList<>();
    }
    result.put("members", members);
    return result;
}

public Map<String, Object> getChatRoomInfo() {
    Map<String, Object> data = new HashMap<>();
    Map<String, Object> returnResult = new HashMap<>();
    Map<String, String> roomInfoMap = new HashMap<>();
    List<Map<String, Object>> members = new ArrayList<>();
    List<String> membersJIDS = new ArrayList<>();
    if(UtilValidate.isNotEmpty(context.userLoginInfo)){
        context.put("userLogin", context.userLoginInfo);
    }
    String groupId = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", context.userLogin.partyId), null, false).get(0).get("partyIdFrom");
    String charRoomJID = context.get("charRoomJID");
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/chatrooms/" + charRoomJID.split("@")[0];
    Map<String, Object> map = doExecute("", url, "", "get");
    if(map.get("status") && StringUtils.isNotBlank(map.get("result").toString())){
        List<Boolean> canRemove = new ArrayList<>();
        String result = map.get("result").toString();
        Document document = DocumentHelper.parseText(result);
        Element rootElement = document.getRootElement();
        List<Element> membersElements = rootElement.elements("members");
        List<Element> memberElements = membersElements.get(0).elements("member");
        Element nameElement = rootElement.elements("naturalName").get(0);//房间名
        Element subjectElement = rootElement.elements("subject").get(0);//主题
        for(Element element : memberElements){
            Map<String, Object> memberData = new HashedMap();
            String memberJID = element.getText();
            membersJIDS.add(memberJID);
           /* List<GenericValue> fullList = delegator.findByAnd("FullNameGroupName", UtilMisc.toMap("openFireJid", memberJID), null, false)*/
            List<GenericValue> fullList = EntityQuery.use(delegator).from("FullNameGroupName").where("openFireJid",memberJID).orderBy("groupName").queryList();
            if(UtilValidate.isNotEmpty(fullList)){
                GenericValue memberInfo = fullList.get(0);//openFireJid不是主键但是不会重复
                memberData.put("name", UtilValidate.isNotEmpty(memberInfo.get("fullName")) ? memberInfo.get("groupName") + "-" +  memberInfo.get("fullName") : memberJID);
                memberData.put("fullName", UtilValidate.isNotEmpty(memberInfo.get("fullName")) ? memberInfo.get("fullName") : memberJID);
                memberData.put("groupName", memberInfo.get("groupName"));
                memberData.put("partyId", memberInfo.getString("partyId"));
                memberData.put("openFireJid",memberInfo.get("openFireJid"));
                members.add(memberData);
                //需求变更：新增群主可以删除(除自己以外的)所有用户 20161213
                String partyId = context.get("userLogin").get("partyId");
                boolean isAdmin = false;
                //群主验证方式：主账户为administrator对应的主键，且为项目经理
                //1.验证主账户是不是管理员
                memberData.put("personType", "成员")
                GenericValue coo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("administrator",groupId,"chatRoomId",charRoomJID).queryOne();
                if(coo!=null) {
                    //2.验证子账户是不是项目经理
                    String caseId = coo.get("caseId");
                    GenericValue member = EntityQuery.use(delegator).from("TblCasePartyMember").where("partyId",partyId,"caseId",caseId,"roleTypeId","CASE_PERSON_ROLE_MANAGER").queryOne();
                    if(member!=null)
                    {
                        isAdmin = true;
//                        if(coo.get("").toString().equals(partyId))

                    }
                }

                //3.如果是群主，设置除自己以外的人都可以删除
                if(isAdmin)
                {
                    if(context.userLogin.partyId.equals(memberInfo.getString("partyId")))
                    {
                        memberData.put("personType","创建人");
                        memberData.put("canRemove", false);
                    }else
                    {
                        memberData.put("canRemove", true)
                    }
                }
                //4.如果不是群主，只能删除自己公司除自己以外的人
                else {
                    if (!groupId.equals(memberInfo.getString("groupId")) || context.userLogin.partyId.equals(memberInfo.getString("partyId"))) {
                        GenericValue tb = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("administrator",memberInfo.get("groupId"),"chatRoomId",charRoomJID).queryOne();
                        if(tb != null){
                            String caseId = tb.get("caseId");
                            GenericValue member = EntityQuery.use(delegator).from("TblCasePartyMember").where("partyId",memberInfo.getString("partyId"),"caseId",caseId,"roleTypeId","CASE_PERSON_ROLE_MANAGER").queryOne();
                            if(member != null){
                                memberData.put("personType","创建人");
                            }
                        }
                        memberData.put("canRemove", false)
                    } else {
                        memberData.put("canRemove", true)
                    }
                }
            }
            //TODO 需要项目经理才能移除？
        }
        sort(members);
        Map<String, Object> oneMap = new HashMap<>();
        for(Map<String, Object> menber : members){
            if(menber.get("personType").toString().equals("创建人")){
                oneMap = menber;
                break;
            }
        }
        if(UtilValidate.isNotEmpty(oneMap)){
            members.remove(oneMap);
            members.add(0, oneMap);
        }
        roomInfoMap.put("roomName", nameElement.getText());
        roomInfoMap.put("roomTheme", subjectElement.getText());
        returnResult.put("roomInfo", roomInfoMap);
        returnResult.put("members", members);
         returnResult.put("membersJIDS", membersJIDS);
        data.put("data", returnResult);
        return data;
    }else {
        sort(members);
        roomInfoMap.put("roomName", "");
        roomInfoMap.put("roomTheme", "");
        returnResult.put("roomInfo", roomInfoMap);
        returnResult.put("members", members);
        returnResult.put("membersJIDS", membersJIDS);
        data.put("data", returnResult);
        return data;
    }
}

/**
 * 针对list中的成员，按照groupName进行排序
 */
public static void sort(List<Map<String, Object>> data) {
    Collections.sort(data, new Comparator<Map>() {
        public int compare(Map o1, Map o2) {
            String a = (String) o1.get("groupName");
            String b = (String) o2.get("groupName");
            // 升序
            return a.compareTo(b);
            // 降序
            //return b.compareTo(a);
        }
    });
}

/**
 * 12.10改动：当前增加邀请其他参与方的项目经理
 * @return
 */
public Map<String, Object> getOtherMembers() {
    Map<String, Object> data = ServiceUtil.returnSuccess();
    String chatRoomJID = context.get("chatRoomJID");
    GenericValue coo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where( UtilMisc.toMap("chatRoomId", chatRoomJID)).distinct().queryOne();
    String caseId = delegator.findByAnd("TblCaseCooperationRecord", UtilMisc.toMap("chatRoomId", chatRoomJID), null, false).get(0).get("caseId");

    String partyId = context.get("userLogin").get("partyId");
    //获取groupId
//    GenericValue group = EntityQuery.use(delegator).from("FullNameGroupName").where("partyId",partyId).distinct().queryOne();

//    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT"), null, false);
//    String partyGroupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
    //changed by galaxypan@2017-08-28:考虑合伙人作为挂靠机构成员加入CASE，通过成员关系获取参与方
    String partyGroupId = "";
    GenericValue userPartyMember = EntityQuery.use(delegator).from("TblCasePartyMember").where("caseId", caseId, "partyId", partyId).queryOne();
    if(userPartyMember == null){
        throw new RuntimeException("非CASE成员,无权限");
    }
    partyGroupId = userPartyMember.getString("groupPartyId");
    //当前参与方类型的成员
    List<GenericValue> groupMembers = delegator.findByAnd("casePartyMemberJid", UtilMisc.toMap("groupPartyId", partyGroupId, "caseId", caseId,"enabled","Y"), null, false);
    //其他类型的项目经理
    //功能优化：现在邀请时应该显示这个人所在参与方  1222
    List<GenericValue> manageMembers = new ArrayList<>();
    if(partyGroupId.equals(coo.get("administrator"))) {
        manageMembers = EntityQuery.use(delegator).from("casePartyMembers").where(EntityCondition.makeCondition("groupPartyId", EntityOperator.NOT_EQUAL, partyGroupId), EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId), EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CASE_PERSON_ROLE_MANAGER"), EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y")).queryList();
    }
    /*if(manageMembers!=null&&manageMembers.size()!=0)
    {
        groupMembers.addAll(manageMembers);
    }*/

    //把自己去除
    for(int a = 0; a < groupMembers.size(); a ++){
        if(partyId.equals(groupMembers.get(a).get("partyId"))){
            groupMembers.remove(a);
            break;
        }
    }
    //去除现有的成员
    String members = context.get("arr");
    //判断当前是否有成员
    if(UtilValidate.isNotEmpty(members)){
        List<String> memberList = new ArrayList<>();
        for (String member : members.split(",")) {
            memberList.add(member)
        }
        List<GenericValue> memlist = new ArrayList<>();
        List<GenericValue> mamlist = new ArrayList<>();
        for(GenericValue member: groupMembers){
            if(memberList.indexOf(member.get("openFireJid") + "") < 0){
                memlist.add(member);
            }
        }
        for (GenericValue manageMember:manageMembers)
        {
            if(memberList.indexOf(manageMember.get("openFireJid") + "") < 0){
                mamlist.add(manageMember);
            }
        }
        data.put("manages",mamlist);
        data.put("members", memlist);
    }
    return data;
}

public Map<String, Object> addUserToChatRoom() {
    Map<String, Object> data = ServiceUtil.returnSuccess();
    //URLEncoder
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host", "http://192.168.1.110:9090") + "/plugins/restapi/v1/chatrooms/" + context.get("roomName").toString().split("@")[0] + "/members/" + context.get("JID").toString().split("@")[0];
    if(doExecute("", url, "", "post").get("status")){
        data.put("data", "邀请成功，等待对方回应");
    }else{
        throw new ServiceException("邀请失败");
    }
    return data;
}

//需求变更：如果删除的是一个项目经理，应该删除所有该项目经理旗下的所有成员 20161213
public Map<String, Object> removeChatRoomMember(){
    Map<String, Object> data = ServiceUtil.returnSuccess();
    String partyId = context.get("memberJID").toString().split("@")[0];
    String loginId = context.get("userLogin").get("partyId");
    if(!partyId.equals(loginId)) {
        String url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/chatrooms/" + context.get("roomJID").toString().split("@")[0] + "/members/" + context.get("memberJID").toString().split("@")[0];
        doExecute("", url, "", "delete");
        String chatRoomId = context.get("roomJID").toString();
        //1.获取该case协作记录
        GenericValue coo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomId", chatRoomId).queryOne();
        if (coo != null) {
            //2.获取caseId
            String caseId = coo.get("caseId");
            //3.获取该用户在case中的职位
            GenericValue casePartyMember = EntityQuery.use(delegator).from("TblCasePartyMember").where("caseId", caseId, "partyId", partyId).queryOne();
            String roleTypeId = casePartyMember != null ? casePartyMember.get("roleTypeId") : "";
            //4.如果该用户为项目经理，删除其他该公司的成员
            if (roleTypeId == "CASE_PERSON_ROLE_MANAGER") {
                url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/chatrooms/" + context.get("roomJID").toString().split("@")[0];
                String result = doExecute("", url, "json", "get").get("result");
                data.put("xmlString", result);
            }
        }
    }
    data.put("msg", "移除成功");
//    if(doExecute("", url, "", "delete").get("status")){
//        data.put("msg", "移除成功");
//    }else{
//        throw new ServiceException("移除失败");
//    }
    return data;
}

//删除项目经理公司里的其他用户
public Map<String, Object> RemoveChatRoomTeamMember(){
    Map<String, Object> data = ServiceUtil.returnSuccess();
    String partyId = context.get("memberJID").toString().split("@")[0];
    String parentId = context.get("parentId").toString().split("@")[0];
    String loginId = context.get("userLogin").get("partyId");
    if(!partyId.equals(loginId)) {
        String groupId = "";
        String groupIds = "";
        //获取项目经理所在公司
        GenericValue FullNameGroupName = EntityQuery.use(delegator).from("FullNameGroupName").where("partyId",parentId).queryOne();
        if(FullNameGroupName!=null)
        {
            groupId = FullNameGroupName.get("groupId");
        }
        GenericValue FullNameGroupNames = EntityQuery.use(delegator).from("FullNameGroupName").where("partyId",partyId).queryOne();
        if(FullNameGroupNames!=null)
        {
            groupIds = FullNameGroupNames.get("groupId");
        }
        if(groupId!=""&&groupIds!=""&&groupId.equals(groupIds)) {
            String url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/chatrooms/" + context.get("roomJID").toString().split("@")[0] + "/members/" + context.get("memberJID").toString().split("@")[0];
            doExecute("", url, "", "delete");
            String chatRoomId = context.get("roomJID").toString();
            //1.获取该case协作记录
            GenericValue coo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomId", chatRoomId).queryOne();
            if (coo != null) {
                //2.获取caseId
                String caseId = coo.get("caseId");
                //3.获取该用户在case中的职位
                GenericValue casePartyMember = EntityQuery.use(delegator).from("TblCasePartyMember").where("caseId", caseId, "partyId", partyId).queryOne();
                String roleTypeId = casePartyMember != null ? casePartyMember.get("roleTypeId") : "";
                //4.如果该用户为项目经理，删除其他该公司的成员
                if (roleTypeId == "CASE_PERSON_ROLE_MANAGER") {
                    url = UtilProperties.getPropertyValue("zxdoc", "rest_host") + "/plugins/restapi/v1/chatrooms/" + context.get("roomJID").toString().split("@")[0];
                    String result = doExecute("", url, "json", "get").get("result");
                    data.put("xmlString", result);
                }
            }
        }
    }
    data.put("msg", "移除成功");
//    if(doExecute("", url, "", "delete").get("status")){
//        data.put("msg", "移除成功");
//    }else{
//        throw new ServiceException("移除失败");
//    }
    return data;
}

public Map<String, Object> getFullNameByJid() {
    Map<String, Object> data = ServiceUtil.returnSuccess();
    String JID = context.get("JID").toString();
    List fullNameList = delegator.findList("Person", EntityCondition.makeCondition("openFireJid", EntityOperator.EQUALS, JID), UtilMisc.toSet("fullName"), null, null, false);
    String name = fullNameList.size() > 0 && fullNameList.get(0).get("fullName") != null ? fullNameList.get(0).get("fullName") : JID.split("@")[0];
    data.put("name", name);
    return data;
}

public Map<String, Object> upLoadFileTest() {
    Map<String, Object> data = ServiceUtil.returnSuccess();
    String fileId = "TEST_FILE" + delegator.getNextSeqId("DataResource");
    GenericValue testFile = delegator.makeValidValue("DataResource", UtilMisc.toMap("dataResourceId", fileId, "dataResourceName", "姑苏区企业档案表2016-08-04.xls", "objectInfo", "D:\\"));
    delegator.create(testFile);
    data.put("data", UtilMisc.toMap("fileId", fileId, "fileName", "姑苏区企业档案表2016-08-04.xls"));
    return data;
}

public Map<String, Object> addChatFiles() {
    Map<String, Object> data = ServiceUtil.returnSuccess();//根据聊天室id获取caseId
    String chatRoomId = context.chatRoomId;
    GenericValue userLogin = context.userLogin;
    GenericValue chatRecord = from("TblCaseCooperationRecord").where("chatRoomId", chatRoomId).queryOne();
    Map<String, Object> serviceData = new HashMap<>();
    List<String> updatedResourceIds = new ArrayList<>();
    if(chatRecord != null){
        String caseId = chatRecord.getString("caseId");
        GenericValue caseEntity = EntityQuery.use(delegator).from("CaseWithCreatePartyInfo").where("caseId", caseId).queryOne();
        String caseFolderId = caseEntity.getString("folderId");
        String caseOwnerPartyId = caseEntity.getString("ownerGroupId");
        GenericValue caseFolder = EntityQuery.use(delegator).select("folderName", "folderPath").from("TblDirectoryStructure").where("folderId", caseFolderId, "folderType", "1").queryOne();
        String caseFolderPath = caseFolder.getString("folderPath") + caseFolder.getString("folderName");
        String dataResourceIds = context.dataResourceIds;
        String[] ids = dataResourceIds.split(",");
        List<GenericValue> files = new ArrayList<>();
        for (String dataResourceId : ids) {
            boolean isCaseFile = isCaseFile(dataResourceId, caseFolderPath);
            if(!isCaseFile){//不是case文档目录中的，则创建新文件
                dataResourceId = copyFile2CaseFolder(dataResourceId, caseFolderId, caseOwnerPartyId, userLogin);
            }
            GenericValue record = delegator.makeValue("TblCaseCooperationRecordFile");
            record.setString("caseId", caseId);
            record.setString("chatRoomId", chatRoomId);
            record.setString("dataResourceId", dataResourceId);
            files.add(record);
            updatedResourceIds.add(dataResourceId);
        }
        delegator.storeAll(files);
    }
    serviceData.put("fileIds", updatedResourceIds);
    data.put("data", serviceData);
    return data;
}

private String copyFile2CaseFolder(String fileId, String caseFolderId, String caseOwnerPartyId, GenericValue userLogin){
    GenericValue file = EntityQuery.use(delegator).from("DataResource").where(UtilMisc.toMap("dataResourceId", fileId)).queryOne();
    FileTypeManager fileManager = FileManagerFactory.getFileManager(file.getString("dataResourceTypeId"), delegator);
    DataFileDescription fileDescription = new DataFileDescription(file.getString("dataResourceName"), file.get("resourceSize"), file.getString("mimeTypeId"), fileManager.getFileContent(fileId), null, true);
    fileId = fileManager.storeFile(fileDescription, userLogin);//使用新的文件id

    Date date = new Date();
    Timestamp timestamp = new Timestamp(date.getTime());
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTime = format.format(date);

    Map<String, Object> fileMap = new HashMap<>();
    String id = delegator.getNextSeqId("TblFileOwnershipId");
    fileMap.put("id", id);
    fileMap.put("fileId", fileId);
    fileMap.put("folderId", caseFolderId);
    fileMap.put("partyId", caseOwnerPartyId);
    fileMap.put("fileType", "1");
    fileMap.put("filePermissions", "111111");
    fileMap.put("createPartyId", userLogin.get("partyId"));
    fileMap.put("createFileTime", timestamp);
    fileMap.put("fileVersion", dateTime);
    GenericValue newFile = delegator.makeValue("TblFileOwnership",fileMap);
    newFile.create();
    return fileId;
}

private boolean isCaseFile(String fileId, String caseFolderPath){
    GenericValue file = EntityQuery.use(delegator).from("FileAndFolderInfo").where("fileId", fileId, "fileType", "1").queryOne();
    String folderId = file.getString("folderId");
    String parentFolderId = file.getString("parentFolderId");
    if(UtilValidate.isEmpty(parentFolderId)){
        return false;
    }else if("3".equals(parentFolderId)){
        return true;
    }else{
        String folderPath = file.getString("folderPath");
        return folderPath.startsWith(caseFolderPath);
    }
}

public Map<String, Object> getFileList(){
    String chatRoomId = context.get("chatRoomId");
    String loginId = context.get("userLogin").get("partyId");
    List<GenericValue> dataResourceIds = EntityQuery.use(delegator).from("TblCaseCooperationRecordFile").where("chatRoomId", chatRoomId).select("dataResourceId").queryList();
    List<String> fileIds = new ArrayList<>();
    for (GenericValue row : dataResourceIds) {
        fileIds.add(row.getString("dataResourceId"));
    }
    println("chatRoomId---"+chatRoomId);
    println("loginId---"+loginId);
    println("dataResourceIds---"+dataResourceIds);
    println("fileIds---"+fileIds);
    List<GenericValue> fileLists = EntityQuery.use(delegator).select(UtilMisc.toSet("dataResourceId", "dataResourceName", "lockedBy", "resourceSize")).from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, fileIds)).queryList();
    Map<String, Object> data = ServiceUtil.returnSuccess();
    List<String> nameList = new ArrayList<>();
    List<String> idList = new ArrayList<>();
    List<String> lockList = new ArrayList<>();
    List<String> lockNameList = new ArrayList<>();
    List<String> lockStatusList = new ArrayList<>();
    List<String> versionList = new ArrayList<>();
    List<Boolean> sizeList = new ArrayList<>();
    String fileSizeLimit = UtilProperties.getPropertyValue("zxdoc", "chatFileEditSizeLimitM");
    int fileSizeLimitNumber = 0;
    if(UtilValidate.isEmpty(fileSizeLimit)){
        fileSizeLimitNumber = 5242880;//5M
    }else{
        fileSizeLimitNumber = Integer.parseInt(fileSizeLimit) * 1048576;
    }
    for(GenericValue fileInfo : fileLists){
        nameList.add(fileInfo.get("dataResourceName"));
        idList.add(fileInfo.get("dataResourceId"));
        sizeList.add(fileInfo.getLong("resourceSize") > fileSizeLimitNumber);
        lockList.add(fileInfo.get("lockedBy") == null ? "" : fileInfo.get("lockedBy"));
        String fullName = "";
        /*//判断是否由当前登陆人锁定
        String lockStatus = "unlocked";
        if(UtilValidate.isNotEmpty(fileInfo.get("lockedBy"))){
            if(loginId.equals(fileInfo.get("lockedBy"))){
                lockStatus = "locked"
            }
            //显示是由谁锁定的
            GenericValue genericValue = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("partyId",fileInfo.get("lockedBy"))).queryOne();
            if(UtilValidate.isNotEmpty(genericValue)){
                fullName = genericValue.get("fullName")
            }
        }
        lockStatusList.add(lockStatus);
        lockNameList.add(fullName);*/
        List list = new ArrayList();
        list.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
        list.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, '1'));
        list.add(EntityCondition.makeCondition("fileId", EntityOperator.EQUALS, fileInfo.get("dataResourceId")));
        GenericValue file = EntityQuery.use(delegator).select().from("DataResourceFileList").where(list).queryOne();
        if(UtilValidate.isNotEmpty(file)){
            versionList.add(file.get("fileVersion") != null ? file.get("fileVersion") : "")
        }else{
            versionList.add("")
        }
    }
    data.put("files", UtilMisc.toMap("fileNames", nameList,
                                      "fileId", idList,
                                      "lockedBy", lockList,
                                        /*"lockStatusList",lockStatusList,
                                        "lockNameList", lockNameList,*/
                                        "versionList",versionList, "sizeList", sizeList));
    return data;
}

public Map<String, Object> changeFileStatus(){
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String fileId = context.get("fileId") + "";
    boolean release = UtilValidate.isEmpty(context.get("lockedBy"));//传入空则是解锁
    String lockedBy = context.get("userLogin").get("partyId");//锁定文件只能是当前用户执行，估不需要传递lockedBy参数
    GenericValue file = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", fileId), false);
    String oldLockedBy = file.getString("lockedBy");
    Map<String, Object> data = new HashMap<>();
    if(release){//解锁需判断是否是自己锁定的
        if(UtilValidate.isNotEmpty(oldLockedBy) && !oldLockedBy.equals(lockedBy)){
            //是其他人锁定的
            data.put("changeResult", "无法解锁他人锁定的文件");
        }else{
            data.put("changeResult", "true");
            file.set("lockedBy", "");
            file.store();
        }
    }else{//锁定文件
        if(UtilValidate.isNotEmpty(oldLockedBy) && !oldLockedBy.equals(lockedBy)){
            //已被其他人锁定
            GenericValue lockedByPerson = delegator.findOne("Person", UtilMisc.toMap("partyId", oldLockedBy), true)
            String lockedByPersonName = lockedByPerson == null ? "" : lockedByPerson.getString("fullName");
            data.put("changeResult", "已被 " + lockedByPersonName + " 锁定");
        }else{
            data.put("changeResult", "true");
            file.set("lockedBy", lockedBy);
            file.store();
        }
    }
    success.put("data", data);
    return success;
}

public Map<String, Object> checkIsManage(){
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String isManage = "no";
    String chatRoomJID = context.get("chatRoomJID");
    GenericValue cooperation = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomId",chatRoomJID).queryOne();
    if(cooperation!=null)
    {
        String caseId = cooperation.get("caseId");
        GenericValue member = EntityQuery.use(delegator).from("TblCasePartyMember").where("caseId",caseId,"partyId",context.get("userLogin").get("partyId")).queryOne();
        if(member!=null)
        {
            String roleTypeId = member.get("roleTypeId");
            isManage = roleTypeId == "CASE_PERSON_ROLE_MANAGER" ? "yes" : "no";
        }
        //检查是否为未处理的协作历史，将旧数据进行更新，增加管理员字段：其中，默认为企业，如果没有企业，优先选择参与方第一个 20161213
        String administrator = cooperation.get("administrator");
        //管理员字段为空，进行处理
        if(administrator==null || administrator=="")
        {
            String memberLists = cooperation.get("groupIds");
            List members = memberLists.split(",");
            for(String memberId :members)
            {
                memberId = (memberId.substring(0,1)==" ")?memberId.substring(1,memberId.length()):memberId;
                //添加管理员
                if(memberId!=""&&memberId!="")
                {
                    GenericValue group = EntityQuery.use(delegator).from("PartyRole").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,memberId),EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%")).queryOne();
                    if(group!=null)
                    {
                        //当该用户为企业用户，设置其为管理员
                        String groupType = group.get("roleTypeId");
                        if(groupType == "CASE_ROLE_OWNER")
                        {
                            administrator = memberId;
                        }
                    }
                }
            }
            //如果参与方中没有企业用户，直接添加第一个公司的项目经理
            if(administrator==null || administrator=="")
            {
                String memberId = members.get(0);
                administrator = memberId;
            }
            cooperation.put("administrator",administrator);
            cooperation.store();
        }
    }
    success.put("data",isManage);
    return success;
}
public Map<String, Object> getFileListCase(){
    String chatRoomId = context.get("chatRoomId");
    String caseId = context.get("caseId");
    String loginId = context.get("userLogin").get("partyId");
    String[] chatRoomIdMany = chatRoomId.split(",");
    List<String> fileIds = new ArrayList<>();
    //8-16  多个case协作room中的文件
    for (int i = 0 ; i < chatRoomIdMany.length ; i++ ) {
    	chatRoomId = chatRoomIdMany[i];
        List<GenericValue> dataResourceIds = EntityQuery.use(delegator).from("TblCaseCooperationRecordFile").where("chatRoomId", chatRoomId).select("dataResourceId").queryList();
        println("dataResourceIds---"+dataResourceIds);
        for (GenericValue row : dataResourceIds) {
	        fileIds.add(row.getString("dataResourceId"));
	    }
    }
    println("fileIds---"+fileIds);
    List<GenericValue> fileLists = EntityQuery.use(delegator).select(UtilMisc.toSet("dataResourceId", "dataResourceName", "lockedBy", "resourceSize")).from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, fileIds)).queryList();
    Map<String, Object> data = new HashMap();
    List<String> nameList = new ArrayList<>();
    List<String> idList = new ArrayList<>();
    List<String> lockList = new ArrayList<>();
    List<String> lockNameList = new ArrayList<>();
    List<String> lockStatusList = new ArrayList<>();
    List<String> versionList = new ArrayList<>();
    List<Boolean> sizeList = new ArrayList<>();
    String fileSizeLimit = UtilProperties.getPropertyValue("zxdoc", "chatFileEditSizeLimitM");
    int fileSizeLimitNumber = 0;
    if(UtilValidate.isEmpty(fileSizeLimit)){
        fileSizeLimitNumber = 5242880;//5M
    }else{
        fileSizeLimitNumber = Integer.parseInt(fileSizeLimit) * 1048576;
    }
    List<GenericValue> fileList = new ArrayList<>();
    for(GenericValue fileInfo : fileLists){
        nameList.add(fileInfo.get("dataResourceName"));
        idList.add(fileInfo.get("dataResourceId"));
        sizeList.add(fileInfo.getLong("resourceSize") > fileSizeLimitNumber);
        lockList.add(fileInfo.get("lockedBy") == null ? "" : fileInfo.get("lockedBy"));
        String fullName = "";
        List list = new ArrayList();
        list.add(EntityCondition.makeCondition("folderType", EntityOperator.EQUALS, "1"));
        list.add(EntityCondition.makeCondition("fileType", EntityOperator.EQUALS, '1'));
        list.add(EntityCondition.makeCondition("fileId", EntityOperator.EQUALS, fileInfo.get("dataResourceId")));
        GenericValue file = EntityQuery.use(delegator).select().from("DataResourceFileList").where(list).queryOne();
        if(UtilValidate.isNotEmpty(file)){
        	Map<String, Object> attributesMap = new HashMap();
        	Map<String, Object> fileMap = new HashMap();
            versionList.add(file.get("fileVersion") != null ? file.get("fileVersion") : "");
            fileMap.put("fileType","file");
            fileMap.put("createFullName",file.getString("fullName"));
            fileMap.put("createTime",file.getString("createFileTime"));
            fileMap.put("fileHistory","");
            fileMap.put("fileId",file.getString("fileId"));
            fileMap.put("fileName",file.getString("fileName"));
            fileMap.put("filePermissions",file.getString("filePermissions"));
            fileMap.put("fileVersion",file.getString("fileVersion"));
            fileMap.put("fullName","CASE文件");
            fileMap.put("partyId",file.getString("partyId"));
            fileMap.put("remarks","");
            attributesMap.put("attributes",fileMap);
            fileList.add(attributesMap);
        }else{
            versionList.add("");
        }
    }
    Map<String, Object> dataMap = new HashMap();
    Map<String, Object> errorMap = new HashMap();
    Map<String, Object> attributesMap1 = new HashMap();
    attributesMap1.put("number","0");
    errorMap.put("attributes",attributesMap1);
    dataMap.put("Error",errorMap);
    Map<String, Object> folderMap = new HashMap();
    Map<String, Object> attributesMap2 = new HashMap();
    attributesMap2.put("parentFolderPermissions","111111");
    folderMap.put("attributes",attributesMap2);
    Map<String, Object> filesMap = new HashMap();
    filesMap.put("folder",folderMap);
    filesMap.put("File",fileList);
    dataMap.put("Files",filesMap);
    data.put("Connector",dataMap);
    println "****************************************";
    println "data:"+data.toString();
    /*data.put("files", UtilMisc.toMap("fileNames", nameList,
                                      "fileId", idList,
                                      "lockedBy", lockList,
                                        "versionList",versionList, "sizeList", sizeList));*/
    return data;
}