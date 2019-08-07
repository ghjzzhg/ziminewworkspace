import org.apache.commons.collections.map.HashedMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
String userLoginId = context.get("userLogin").get("partyId");
List<GenericValue> caseCooperationRecords = EntityQuery.use(delegator).select().from("TblCaseCooperationRecord").where(UtilMisc.toMap("caseId", request.getParameter("caseId"))).orderBy("-createdStamp").queryList();
List<Map<String, Object>> result = new ArrayList<>();
String chatRoomIds = "";
for(GenericValue caseCooperationRecord: caseCooperationRecords){
    String chatRoomId = caseCooperationRecord.getString("chatRoomId");
    chatRoomIds = chatRoomIds+chatRoomId+",";
    
}
request.setAttribute("chatRoomIds", chatRoomIds.substring(0,chatRoomIds.length()-1));