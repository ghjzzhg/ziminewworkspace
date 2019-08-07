import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil


public Map<String,Object> searchFixedAssets(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    String fixedAssetsCode = context.get("fixedAssetsCode");
    String fixedAssetsName = context.get("fixedAssetsName");
    String fixedAssetsType = context.get("assetType");
    String department = context.get("departmentForSearch");
    String usePerson = context.get("usePersonForSearch");
    String useDepartment = context.get("useDepartmentForSearch");
    String assetStatus = context.get("assetStatus");
    String canLendOut = context.get("canLendOut");

    List<EntityCondition> conditionList = FastList.newInstance();
    if(UtilValidate.isNotEmpty(fixedAssetsCode)){
        conditionList.add(EntityCondition.makeCondition("fixedAssetsCode",EntityOperator.LIKE,"%"+fixedAssetsCode+"%"));
    }
    if(UtilValidate.isNotEmpty(fixedAssetsName)){
        conditionList.add(EntityCondition.makeCondition("fixedAssetsName",EntityOperator.LIKE,"%"+fixedAssetsName+"%"));
    }
    if(UtilValidate.isNotEmpty(fixedAssetsType)){
        conditionList.add(EntityCondition.makeCondition("assetType",EntityOperator.EQUALS,fixedAssetsType));
    }
    if(UtilValidate.isNotEmpty(usePerson)){
        conditionList.add(EntityCondition.makeCondition("usePerson",EntityOperator.EQUALS,usePerson));
    }
    if(UtilValidate.isNotEmpty(useDepartment)){
        conditionList.add(EntityCondition.makeCondition("useDepartment",EntityOperator.EQUALS,useDepartment));
    }
    if(UtilValidate.isNotEmpty(assetStatus)){
        conditionList.add(EntityCondition.makeCondition("assetStatus",EntityOperator.EQUALS,assetStatus));
    }
    if(UtilValidate.isNotEmpty(department)){
        conditionList.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if(UtilValidate.isNotEmpty(canLendOut)){
        conditionList.add(EntityCondition.makeCondition("canLendOut",EntityOperator.EQUALS,canLendOut));
    }
    EntityListIterator fixedAssetsList = null;
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(conditionList.size() > 0){
        fixedAssetsList = EntityQuery.use(delegator).from("FixedAssetsList").where(conditionList).queryIterator();
    }else {
        fixedAssetsList = EntityQuery.use(delegator).from("FixedAssetsList").queryIterator();
    }
    if(null != fixedAssetsList && fixedAssetsList.getResultsSizeAfterPartialList() > 0){
        totalCount = fixedAssetsList.getResultsSizeAfterPartialList();
        pageList = fixedAssetsList.getPartialList(lowIndex, viewSize);
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("fixedAssetsCode",fixedAssetsCode);
    map.put("fixedAssetsName",fixedAssetsName);
    map.put("fixedAssetsType",fixedAssetsType);
    map.put("department",department);
    map.put("usePerson",usePerson);
    map.put("useDepartment",useDepartment);
    map.put("assetStatus",assetStatus);
    map.put("canLendOut",canLendOut);
    successResult.put("data",map)
    return successResult;
}

public Object findAssetPartys(){
    String assetsPartsId = request.getParameter("assetsPartsId");
    if(UtilValidate.isNotEmpty(assetsPartsId)){
        GenericValue assetsParts = delegator.findOne("TblFixedAssetsParts",UtilMisc.toMap("assetsPartsId",assetsPartsId),false);
        request.setAttribute("assetsParts",assetsParts);
    }
    return "success";
}
public Object findFixedAssats(){
    String fixedAssetsId = request.getParameter("fixedAssetsId");
    if(UtilValidate.isNotEmpty(fixedAssetsId)){
        GenericValue fixedAssets = delegator.findOne("TblFixedAssets",UtilMisc.toMap("fixedAssetsId",fixedAssetsId),false);
        request.setAttribute("fixedAssets",fixedAssets);
    }
    return "success";
}