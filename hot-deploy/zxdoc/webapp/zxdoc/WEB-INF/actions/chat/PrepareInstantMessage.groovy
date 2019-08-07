import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

String chatRoomId = context.chatRoomId;
GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.get("partyId");

GenericValue chatRecord = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, "00000"), EntityCondition.makeCondition("chatRoomId", EntityOperator.EQUALS, chatRoomId)).queryOne();
if(chatRecord != null){
    context.chatRoomName = chatRecord.getString("chatRoomName");
    context.joiner = chatRecord.getString("groupIds").replace(partyId, "").replace(",", "").trim();
    GenericValue joinerLogin = EntityQuery.use(delegator).from("Person").where("partyId",context.joiner).queryOne();
    context.joinerName = joinerLogin.getString("fullName");
    GenericValue personLogin = EntityQuery.use(delegator).from("Person").where("partyId",partyId).queryOne();
    context.sponsorName = personLogin.getString("fullName");
    context.sponsorId = partyId;
}
/*
String chatRoomGetUrl = UtilProperties.getPropertyValue("zxdoc", "rest_host", "http://192.168.1.123:9090") + "/plugins/restapi/v1/chatrooms/" + chatRoomId.split('@')[0];
Map<String, Object> restResult = OpenFireRestApi.doExecute(null, chatRoomGetUrl, "json", "get");
if(restResult.get("status")){
    restResult.get("result");
}*/
