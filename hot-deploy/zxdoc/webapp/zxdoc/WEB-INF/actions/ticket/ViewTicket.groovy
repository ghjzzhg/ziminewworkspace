import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

GenericValue ticket = delegator.findOne("TblTicket", UtilMisc.toMap("id", parameters.ticketId), true);
String ticketPartyId = ticket.getString("ticketPartyId");
GenericValue party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", ticketPartyId)).queryOne();
if(UtilValidate.isNotEmpty(party)){
    String ticketPartyName = ""
    if(party.get("partyTypeId").equals("PARTY_GROUP")){
        ticketPartyName = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", ticketPartyId), true).getString("groupName");
    }
    context.ticketPartyName = ticketPartyName;
}
context.ticketInfo = ticket;

//TODO 竞选信息？