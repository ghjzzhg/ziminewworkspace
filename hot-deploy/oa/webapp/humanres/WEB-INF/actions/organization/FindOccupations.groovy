import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

context.occupations = [];
context.partyId = parameters.partyId;
roleCondition = [EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, context.partyId), EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "JOB_POSITION")];
roles = select("partyId", "roleTypeId", "description").from("RoleTypeAndParty").where(roleCondition).queryList();
if(roles){
    roles.each {
        role ->
            partyCondition = [EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "PROVIDE_POSITION"), EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.partyId), EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, role.roleTypeId)]
            context.occupations.add([name: role.description, y: from("PartyRelationship").where(partyCondition).queryCount(), positionId : role.partyId + "," + role.roleTypeId]);
    }
}