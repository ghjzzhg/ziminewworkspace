import org.ofbiz.base.util.UtilMisc

listEnums=[];
enums = from("Enumeration").where(UtilMisc.toMap("enumTypeId", request.getParameter("enumTypeId"))).orderBy("sequenceId").queryList();
if(enums){
    listEnums = enums;
}

context.listEnums = listEnums;