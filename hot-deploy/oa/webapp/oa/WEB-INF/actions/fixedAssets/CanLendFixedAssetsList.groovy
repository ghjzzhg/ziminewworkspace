import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil

public Map<String,Object> findAssetsBorrow(){
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
    String assetType = context.get("assetType");
    String fixedAssetsManager = context.get("fixedAssetsManager");
    String applyPerson = context.get("applyPerson");
    String department = context.get("department");
    String assetStatus = context.get("assetStatus");
    String lendStatus = context.get("lendStatus");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId").toString();
    List<EntityCondition> conditionList = FastList.newInstance();
    if(UtilValidate.isNotEmpty(fixedAssetsCode)){
        conditionList.add(EntityCondition.makeCondition("fixedAssetsCode",EntityOperator.LIKE,"%"+fixedAssetsCode+"%"));
    }
    if(UtilValidate.isNotEmpty(fixedAssetsName)){
        conditionList.add(EntityCondition.makeCondition("fixedAssetsName",EntityOperator.LIKE,"%"+fixedAssetsName+"%"));
    }
    if(UtilValidate.isNotEmpty(assetType)){
        conditionList.add(EntityCondition.makeCondition("assetType",EntityOperator.EQUALS,assetType));
    }
    if(UtilValidate.isNotEmpty(fixedAssetsManager)){
        conditionList.add(EntityCondition.makeCondition("fixedAssetsManager",EntityOperator.EQUALS,fixedAssetsManager));
    }
    if(UtilValidate.isNotEmpty(applyPerson)){
        conditionList.add(EntityCondition.makeCondition("applyPerson",EntityOperator.EQUALS,applyPerson));
    }
    if(UtilValidate.isNotEmpty(department)){
        conditionList.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if(UtilValidate.isNotEmpty(assetStatus)){
        conditionList.add(EntityCondition.makeCondition("assetStatus",EntityOperator.EQUALS,assetStatus));
    }
    if(UtilValidate.isNotEmpty(lendStatus)){
        conditionList.add(EntityCondition.makeCondition("lendStatus",EntityOperator.EQUALS,lendStatus));
    }
    conditionList.add(EntityCondition.makeCondition("canLendOut",EntityOperator.EQUALS,"Y"));
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    EntityListIterator canLendFixedAssetsList = EntityQuery.use(delegator).from("FixedAssetsView").where(conditionList).queryIterator();
    List<GenericValue> fixeAssectsList;
    if(null != canLendFixedAssetsList && canLendFixedAssetsList.getResultsSizeAfterPartialList() > 0){
        totalCount = canLendFixedAssetsList.getResultsSizeAfterPartialList();
        fixeAssectsList = canLendFixedAssetsList.getPartialList(lowIndex, viewSize);
    }
    String key = EntityUtilProperties.getPropertyValue("oa.properties", "activitiFixedKey", "localhost", delegator);
    for(Map<String,Object> map : fixeAssectsList){
        Map<String,Object> fixeMap = new HashMap<String,Object>();
        fixeMap.putAll(map);
        String id = fixeMap.get("fixedAssetsLendId");
        String flag = "0";
        if(UtilValidate.isNotEmpty(id)) {
            Map<String, Object> businessValue = dispatcher.runSync("searchBusinessStatus", UtilMisc.toMap("bizKey", id, "workflowKey", key, "userLogin", userLogin));
            if ("success".equals(businessValue.get("responseMessage"))) {
                Map<String, Object> dataMap = businessValue.get("data");
                if (UtilValidate.isNotEmpty(dataMap.get("businessList"))) {
                    List<String> busnessIdList = dataMap.get("businessList") as List<String>;
                    for (String busnessMap : busnessIdList) {
                        if (id.equals(busnessMap)) {
                            flag = "1";
                            break;
                        }
                    }
                }
            }
        }
        fixeMap.put("statues",flag);
        pageList.add(fixeMap);
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList)
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("partyId",partyId);
    map.put("fixedAssetsCode",fixedAssetsCode);
    map.put("fixedAssetsName",fixedAssetsName);
    map.put("assetType",assetType);
    map.put("fixedAssetsManager",fixedAssetsManager);
    map.put("applyPerson",applyPerson);
    map.put("department",department);
    map.put("assetStatus",assetStatus);
    map.put("lendStatus",lendStatus);
    successResult.put("data",map);
    return successResult;
}