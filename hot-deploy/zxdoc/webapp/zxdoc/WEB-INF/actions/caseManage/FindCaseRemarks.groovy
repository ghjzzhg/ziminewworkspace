request.setAttribute("data", from("TblCaseRemark").where("caseId", parameters.caseId, "partyId", userLogin.partyId).orderBy("-createdStamp").queryList());
