request.setAttribute("counts", from("PartyGroupStatsByRole").where("statusId", "PARTY_UNIDENTIFIED").queryList());