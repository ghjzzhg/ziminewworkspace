import org.ofbiz.base.util.UtilMisc

context.put("partyReasons", delegator.findByAnd("TicketPartyNames", UtilMisc.toMap("ticketId", parameters.ticketId, "roleTypeId", parameters.roleTypeId), null, false));
context.put("caseId", parameters.caseId);
