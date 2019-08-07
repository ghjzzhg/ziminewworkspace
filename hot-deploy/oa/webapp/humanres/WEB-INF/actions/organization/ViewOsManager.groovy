osManagementId = parameters.get("osManagementId");
/*party = from("PartyGroup").where("partyId", partyId).queryOne();*/
/*osManagement = [:];
osManagement.put("osManagementId", party.getString("osManagementId"));
osManagement.put("suppliesName", party.getString("suppliesName"));*/

//查询负责人
partyRelationships = from("PartyRelationship").where("partyIdFrom", partyId, "partyRelationshipTypeId", "MANAGER").filterByDate().queryList();

if(partyRelationships){
    organization.put("manager", partyRelationships.get(0).get("partyIdTo"));
}

context.organization = organization;