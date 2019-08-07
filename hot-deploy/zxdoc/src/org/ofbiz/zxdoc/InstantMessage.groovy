package org.ofbiz.zxdoc

import net.sf.json.JSONObject
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import javax.rmi.CORBA.Util

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

/**
 * Created by gf on 2016/11/2.
 * 即时聊天
 */
//创建聊天室

public Map<String, Object> createInstantMessage() {
    GenericValue userLogin = context.get("userLogin");
    String goalId = context.get("goalId");
    String goalName = "";
    String targetOpenfireId = "";
    String isInstant = context.get("isInstant");
    if(goalId!=null) {
        GenericValue user = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("partyId", goalId)).queryOne();
        goalName = user.get("fullName");
        targetOpenfireId = user.get("openFireJid");
    }
    String domain = UtilProperties.getPropertyValue("zxdoc", "jabber_server");
    Map<String, Object> result = ServiceUtil.returnSuccess();
    //即时聊天的caseId设置为00000
    String caseId = "00000";
    //throw new RuntimeException("NOOOOO");
    String url = UtilProperties.getPropertyValue("zxdoc", "rest_host", "http://192.168.1.123:9090") + "/plugins/restapi/v1/chatrooms";
    String roomName = new Date().getTime().toString();//即聊天室的id，唯一标识聊天室的字段
    String fullName = EntityQuery.use(delegator).from("Person").where("partyId",userLogin.get("partyId")).queryOne().get("fullName");
    String naturalName = "咨询：" + fullName + "&" + goalName;
    if(isInstant!=null)
    {
        naturalName = fullName + "&" + goalName;
    }
    //聊天室主题设置为当前用户名+时间戳
    String subject = userLogin.get("userLoginId") + roomName;//聊天室主题
    String chatRoomDescription = userLogin.get("userLoginId") + "的资料库咨询";
    if(isInstant!=null)
    {
        chatRoomDescription = userLogin.get("userLoginId") + "的即时聊天";
    }
    JSONObject chatRoom = new JSONObject();
    chatRoom.put("roomName", roomName);
    chatRoom.put("naturalName", naturalName);
    chatRoom.put("description", chatRoomDescription);
    chatRoom.put("subject", subject);//主题
    chatRoom.put("maxUsers", 30);
    chatRoom.put("persistent", true);
    chatRoom.put("membersOnly", true);//仅对成员开放
    chatRoom.put("publicRoom", false);//列出目录中的房间(这样只对成员可见)

    JSONObject member = new JSONObject();//成员
    Map memberMap = new HashMap();
    String loginUserPartyId = userLogin.get("partyId");
    String partyId = userLogin.get("partyId");
    memberMap.put(partyId,partyId);
    memberMap.put(goalId,goalId);
    List<String> members = new ArrayList();
    for(String memberName: memberMap.keySet()){
        members.add(memberMap.get(memberName) + "@" + domain);
    }
    member.put("member", members);
    chatRoom.put("members", member);

    GenericValue group = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdTo",partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT")).queryOne();
    partyId = group.get("partyIdFrom");
    GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where(UtilMisc.toMap("partyId",partyId)).queryOne();
    String groupMap = partyGroup.get("groupName");
    //将此次协作信息保存到TblCaseCooperationRecord
    List<String> groupNames = new ArrayList<>();
    groupNames.add(groupMap+"");
    String msg = "";
    delegator.create(delegator.makeValidValue("TblCaseCooperationRecord", UtilMisc.toMap(
            "caseId", caseId,
            "chatRoomId", roomName + "@conference." + domain,
            "chatRoomName", naturalName,
            "groupNames", groupNames.toString().replace("[", "").replace("]", ""),
            "groupIds", loginUserPartyId + "," + goalId)));
    if(doExecute(chatRoom.toString(), url, "json", "post").get("status")){
        msg = "创建成功";
    }else{
        msg = "创建失败";
    }
    result.put("data", msg);
    result.put("caseId", "00000");
    result.put("chatRoomJID", roomName + "@conference." + domain);
    result.put("chatRoomName", goalName);
    result.put("members", members);
    result.put("goalName",targetOpenfireId);
    return result;
}

//添加用户进即时聊天,获取公司里的所有除了已经在聊天室的用户
public Map<String, Object> addOtherPeople() {
    Map<String, Object> data = ServiceUtil.returnSuccess();

    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");

    //搜索条件
    String searchName = context.get("data");

    //获取子账户
    List<GenericValue> subs = EntityQuery.use(delegator).from("Person").where(EntityCondition.makeCondition("fullName",EntityOperator.LIKE,"%" + searchName + "%"),EntityCondition.makeCondition("fullName",EntityOperator.NOT_EQUAL,null),EntityCondition.makeCondition("openFireJid",EntityOperator.NOT_EQUAL,null)).queryList();
    List<GenericValue> groupMembers = new ArrayList();
    for (GenericValue sub : subs){
        String subPartyId = sub.get("partyId");
        GenericValue instantMembers = EntityQuery.use(delegator).from("instantMembers").where(UtilMisc.toMap("partyId",subPartyId,"enabled","Y")).queryOne();
        if(instantMembers != null) {
            groupMembers.add(instantMembers);
        }
    }
    //List<GenericValue> groupMembers = delegator.findByAnd("casePartyMemberJid", UtilMisc.toMap("groupPartyId", partyGroupId, "caseId", caseId), null, false);
    //把自己去除
    for(int a = 0; a < groupMembers.size(); a ++){
        if(partyId.equals(groupMembers.get(a).get("partyId"))){
            groupMembers.remove(a);
            break;
        }
    }
    //去除无用的账号
    for(int a = 0; a < groupMembers.size(); a ++){
        String thispartyId = groupMembers.get(a).get("partyId");
        GenericValue user = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId",thispartyId)).queryOne();
        if(user == null)
        {
            groupMembers.remove(a);
        }else
        {
            GenericValue person = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("partyId",thispartyId)).queryOne();
            String open = person.get("openFireJid");
            if(open == null || open == "")
            {
                groupMembers.remove(a);
            }
        }
    }

    List<GenericValue> list = new ArrayList<>();
    //去除现有的成员
    String members = context.get("arr");
    list.addAll(groupMembers);
    //判断当前是否有成员
    if(UtilValidate.isNotEmpty(members)){
        List<String> memberList = new ArrayList<>();
        for (String member : members.split(",")) {
            memberList.add(member)
        }
        for(GenericValue member: groupMembers){
            if(memberList.indexOf(member.get("openFireJid") + "") > 0){
                list.remove(member);
            }
        }

    }

    data.put("members", list);
    return data;
}

public Map<String, Object> whichRoom()
{
    String jid = context.get("jid");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String result = "";
    Map success = ServiceUtil.returnSuccess();
    GenericValue room = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where(UtilMisc.toMap("chatRoomId",jid)).queryOne();
    String caseId = room.get("caseId");
    String partyIds = room.getString("partyIds");
    if(UtilValidate.isNotEmpty(partyIds)){
        String[] partyIdList = partyIds.split(",");
        StringBuilder partys = new StringBuilder();
        for(String party : partyIdList){
            if(!party.equals(partyId)){
                partys.append(party).append(",");
            }
        }
        if(UtilValidate.isNotEmpty(partys)){
            partys.deleteCharAt(partys.length() - 1);
        }
        room.set("partyIds", partys.toString());
        room.store();
    }
    if(caseId.substring(0,1)=="z"||caseId=="00000")
    {
        result = "即时聊天";
    }else
    {
        result = "case聊天";
    }
    success.put("result",result);
    return success;
}

public Map<String, Object> checkIsExist() {
    Map success = ServiceUtil.returnSuccess();
    String goalId = context.get("goalId");
    String goalName = "";
    String partyId = context.get("userLogin").get("partyId");
    String username = "";
    String name ="";
    String fullName = "";
    if(goalId!=null) {
        /*GenericValue user = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", goalId)).queryOne();
        goalName = user.get("userLoginId");*/
        GenericValue person = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("partyId", goalId)).queryOne();
        goalName = person.get("openFireJid");
        name = person.get("fullName");
        GenericValue user = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("partyId", partyId)).queryOne();
        username = user.get("openFireJid");
        fullName = user.get("fullName");
    }
    String chatRoomName = "咨询：" +  fullName + "&" + name;
    GenericValue caseCoo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, "00000"), EntityCondition.makeCondition("groupIds", EntityOperator.LIKE, "%" + goalId + "%"), EntityCondition.makeCondition("groupIds", EntityOperator.LIKE, "%" + partyId + "%")).queryOne();
    if(caseCoo==null || caseCoo.size()==0)
    {
        success.put("data","no");
    }else
    {
        success.put("data","yes");
        success.put("chatRoomName",caseCoo.get("chatRoomName"));
        success.put("chatRoomJID",caseCoo.get("chatRoomId"));
        success.put("goalName",goalName);
        success.put("username",username);
    }
    return success;
}


public Map<String, Object> checkRoomExist() {
    Map success = ServiceUtil.returnSuccess();
    String personId = context.get("personId");
    String partyId = context.get("userLogin").get("partyId");
    GenericValue userLogin = EntityQuery.use(delegator).from("Person").where("partyId",partyId).queryOne();
    String fullName = userLogin.get("fullName");
    String userId = userLogin.get("openFireJid");
    GenericValue personLogin = EntityQuery.use(delegator).from("Person").where("partyId",personId).queryOne();
    String personLoginId = personLogin.get("openFireJid");
    String loginId = personLogin.get("fullName");
    String roomName = fullName + "&" + loginId;
    GenericValue caseCoo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, "00000"), EntityCondition.makeCondition("groupIds", EntityOperator.LIKE, "%" + personId + "%"), EntityCondition.makeCondition("groupIds", EntityOperator.LIKE, "%" + partyId + "%")).queryOne();
//    GenericValue caseCoo = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomName",roomName).queryOne();
    if(caseCoo!=null)
    {
        success.put("data","yes");
        success.put("chatRoomName", loginId);
        success.put("chatRoomJID",caseCoo.get("chatRoomId"));
        success.put("goalName",personLoginId);
        success.put("userId",userId);
    }else
    {
        success.put("data","no");
    }
    return success;
}

/**
 * 获取邀请人
 */
public Map<String, Object> getInviteUsername()
{
    Map success = ServiceUtil.returnSuccess();
    String roomId = context.get("roomId");
    String chatRoomName = context.get("name");
    String inviteId = "";
    GenericValue Room = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomId",roomId).queryOne();
    //针对一个脏数据做的处理
    if(Room!=null) {
        String groupIds = Room.get("groupIds");
        List list = groupIds.split(",");
        String partyId = context.get("userLogin").get("partyId");
        for (String id : list) {
            if (id != partyId) {
                inviteId = id;
            }
        }
    }
    List<GenericValue> persons = EntityQuery.use(delegator).from("Person").where("partyId",inviteId).queryList();
    if(persons!=null&&persons.size()!=0) {
        String inviteName = persons.get(0).get("fullName");
        success.put("inviteName", inviteName);
    }else{
        success.put("inviteName", chatRoomName);
    }
    return success;
}