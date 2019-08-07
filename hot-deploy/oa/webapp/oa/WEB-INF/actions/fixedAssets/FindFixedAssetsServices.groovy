import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
assetsName = parameters.get("assetsName");
groupId = parameters.get("groupId");
fixedAssetsMap = [:];
if (UtilValidate.isNotEmpty(assetsName)){
    fixedAssetsMap.put("assetsName", assetsName);
}
if (UtilValidate.isNotEmpty(groupId)){
    fixedAssetsMap.put("groupId", groupId);
}
if (UtilValidate.isNotEmpty(assetsName)||UtilValidate.isNotEmpty(groupId)){
    /* listFixedAssets = delegator.findByAnd("FixedAssets",fixedAssets);*/
   listFixedAssets = from("FixedAssets").where(fixedAssetsMap).queryList();
}else{
    listFixedAssets = delegator.findList("FixedAssets", null, null, null, null, false);
}
context.listFixedAssets = listFixedAssets;