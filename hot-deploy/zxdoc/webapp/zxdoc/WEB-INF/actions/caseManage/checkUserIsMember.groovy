package caseManage

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/30.
 */
String jid = parameters.jid;
GenericValue chatRoom = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomId",jid).queryOne();
String caseId = chatRoom.get("caseId");
List<GenericValue> caseMembers = EntityQuery.use(delegator).from("TblCasePartyMember").where("caseId",caseId).queryList();
String partyId = context.get("userLogin").get("partyId");
List memberList = new ArrayList();
for (GenericValue caseMember:caseMembers)
{
    memberList.add(caseMember.get("partyId"));
}
if(memberList.contains(partyId))
{
    request.setAttribute("isMember","Y");
}else
{
    request.setAttribute("isMember","N");
}
