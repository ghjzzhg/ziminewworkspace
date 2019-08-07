import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

context.positionId = parameters.get("positionId");
positionArray = context.positionId.split(",")
context.partyId = positionArray[0];
context.roleTypeId = positionArray[1];
context.description = delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", context.roleTypeId)).get("description", locale);
List<GenericValue> oldManagers = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", positionArray[0], "roleTypeIdFrom", positionArray[1], "partyRelationshipTypeId", "MANAGER"));
String managerForEditOccupation = null;
if(UtilValidate.isNotEmpty(oldManagers)){
    managerForEditOccupation = oldManagers.get(0).get("partyIdTo");
}
context.managerForEditOccupation = managerForEditOccupation;
Map positionMap = UtilMisc.toMap("userLogin", userLogin, "positionId", context.positionId)
result = runService("getMasterOccupation", positionMap);
master = result.data;
context.newOccupationMaster = master.get("partyId") + "," + master.get("roleTypeId");
context.newOccupationMasterForEdit = master.get("partyId") + "," + master.get("roleTypeId");
