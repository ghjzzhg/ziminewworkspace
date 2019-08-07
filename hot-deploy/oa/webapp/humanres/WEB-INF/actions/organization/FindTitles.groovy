import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

listTitles=[];
titles = from("Enumeration").where(UtilMisc.toMap("enumTypeId", "POSITION_LEVEL")).orderBy("sequenceId").queryList();
if(titles){
    for (GenericValue title  : titles) {
        listTitles.push([name: title.get("description"), level: title.get("sequenceId").toInteger(), enumId: title.get("enumId")]);
    }
}

context.listTitles = listTitles;