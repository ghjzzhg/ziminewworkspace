import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

import java.text.Format
import java.text.SimpleDateFormat

GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.get("partyId");
Map departmentMap = dispatcher.runSync("searchPartyInfo",UtilMisc.toMap("partyId",partyId,"userLogin",userLogin));
Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblFileData","numName","documentNumber","prefix","fileData","userLogin",userLogin));
context.number = uniqueNumber.get("number");
Format format = new SimpleDateFormat("yyyyMMddHHmmss");
context.versionNumber = format.format(new Date());
Map map=departmentMap.get("data")
context.department =map.get("groupId");