partyId = parameters.get("partyId");
party = from("PartyGroup").where("partyId", partyId).queryOne();
organization = [:];
organization.put("partyId", party.getString("partyId"));
organization.put("groupName", party.getString("groupName"));

//查询负责人
partyRelationships = from("PartyRelationship").where("partyIdFrom", partyId, "partyRelationshipTypeId", "MANAGER").orderBy("fromDate DESC").queryList();

if(partyRelationships){
    organization.put("manager", partyRelationships.get(0).get("partyIdTo"));
}

context.organization = organization;