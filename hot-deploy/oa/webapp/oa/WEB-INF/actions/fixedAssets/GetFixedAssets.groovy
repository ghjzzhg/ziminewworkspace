import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

String fixedAssetsId = request.getParameter("fixedAssetsId");
if(UtilValidate.isNotEmpty(fixedAssetsId)){
    GenericValue fixedAssets = delegator.findOne("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId),false);
    context.fixedAssetsMap = fixedAssets;
}
Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblFixedAssets", "prefix","fixedAssets", "numName","fixedAssetsCode","userLogin",userLogin));
context.number = uniqueNumber.get("number");
context.fixedAssetsId = fixedAssetsId;
