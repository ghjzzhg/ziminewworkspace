import org.ofbiz.base.util.UtilMisc

context.positionId = parameters.get("positionId");
positionArray = context.positionId.split(",")
context.partyId = positionArray[0];
context.roleTypeId = positionArray[1];
context.description = delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", context.roleTypeId)).get("description", locale);

Map positionMap = UtilMisc.toMap("userLogin", userLogin, "positionId", context.positionId)
result = runService("getOccupationMembers", positionMap);
/*members = result.data;
recordList = [];
if(members){
    for (Map member  : members) {
        recordList.add([emplyeeName: member.get("partyId"), post: member.get("post")]);
    }
}*/
context.recordList = result.data;