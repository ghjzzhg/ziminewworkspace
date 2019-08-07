package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue

import java.sql.Timestamp
import java.text.SimpleDateFormat
    workLogId = parameters.get("workLogId");
    partyId = parameters.get("partyId");
    reviewById = parameters.get("userLogin").get("partyId");
    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    returnMap = delegator.findByPrimaryKey("TblWorkLog",UtilMisc.toMap("workLogId", workLogId));
    workDate = returnMap.get("workDate");
    String date = String.valueOf(workDate).split(" ")[0];
    context.workDate = date;
    context.reviewedById = reviewById;
    context.partyId = partyId;
    context.returnMap = returnMap;
