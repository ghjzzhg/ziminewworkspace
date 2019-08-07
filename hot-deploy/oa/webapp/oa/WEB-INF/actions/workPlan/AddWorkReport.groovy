import com.ibm.icu.util.GenderInfo
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.text.SimpleDateFormat


Locale locale = (Locale) context.get("locale");
Map<String, Object> successResult = ServiceUtil.returnSuccess();
GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.getString("partyId");

SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
String nowTime = format.format(new Date()).toString();
Map map = parameters.get("workReportMap");
String workReportId = map.get("workReportId");
if(null != workReportId && UtilValidate.isNotEmpty(workReportId)){
    context.flag = "update";
}else{
    GenericValue party = EntityQuery.use(delegator).select()
            .from("Person")
            .where(EntityCondition.makeCondition("partyId",partyId))
            .queryOne();
    context.inputPersonName = party.get("fullName");
    context.inputPersonId = partyId;
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblWorkReport","numName","reportNumber","prefix","workReport","userLogin",userLogin));
    context.flag = "add";
    context.reportNumber = uniqueNumber.get("number");
}



