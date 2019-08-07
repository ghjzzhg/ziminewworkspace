String ticketStatus = from("TblDdTicket").cache().where("ticketId", parameters.ticketId).queryOne().getString("ticketStatus");
if("TICKET_DRAFT".equals(ticketStatus) || "true".equalsIgnoreCase(parameters.forceEdit)){
    return "success";
}else{
    return "viewTicket";
}