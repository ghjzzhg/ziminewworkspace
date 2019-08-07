import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate

String id = parameters.id;
String date = parameters.date;
if(UtilValidate.isEmpty(id)){
    context.event = [
        start: UtilDateTime.toSqlDate(date, "yyyy-MM-dd"),
        end: UtilDateTime.toSqlDate(date + " 23:59:59", "yyyy-MM-dd hh:mm:ss")
    ];
}else{
    context.event = from("TblCalendarEvent").where("id", id).queryOne();
}