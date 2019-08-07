import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

includePartyId = parameters.get("includePartyId");
if(includePartyId == null){
    includePartyId = true;
}else{
    includePartyId = Boolean.parseBoolean(includePartyId);
}
occupations = [];
//查询所有岗位
roles = select("partyId", "roleTypeId", "description").from("DepartmentPositionView").queryList();
if(roles){
    roles.each {
        role ->
//            查询上级岗位
            positionCondition = [EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, role.partyId),
                                 EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, role.roleTypeId),
                                 EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "MASTER_POSITION")]
            parentPosition = select("partyIdFrom", "roleTypeIdFrom").from("PartyRelationship").where(positionCondition).cache().queryOne();
            id = includePartyId ? role.partyId + "," + role.roleTypeId : role.roleTypeId;
            code = id;
            pId = parentPosition ? (includePartyId ? parentPosition.partyIdFrom + "," + parentPosition.roleTypeIdFrom : parentPosition.roleTypeIdFrom) : '';
            occupations.add([id: id, code: code, name: role.description, pId: pId]);
    }
}
context.occupations = occupations;
request.setAttribute("data", occupations);