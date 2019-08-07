import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

public Map<String,Object> searchInventoryList(){
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

    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String warehouseId = context.get("warehouseId");
    String productTypeId = context.get("productTypeId");
    String suppliesName = context.get("suppliesName");
    List<EntityCondition> condition = FastList.newInstance();
    if (UtilValidate.isNotEmpty(warehouseId)){
        condition.add(EntityCondition.makeCondition("warehouseId", EntityOperator.EQUALS,warehouseId));
    }
    if (UtilValidate.isNotEmpty(productTypeId)){
        condition.add(EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,productTypeId));
    }
    if (UtilValidate.isNotEmpty(suppliesName)){
        condition.add(EntityCondition.makeCondition("suppliesName",EntityOperator.LIKE,"%"+suppliesName+"%"));
    }
    condition.add(EntityCondition.makeCondition("osManagementId",EntityOperator.NOT_EQUAL,null));
    EntityListIterator inventoryIterator = EntityQuery.use(delegator)
            .select()
            .from("InventoryInfo")
            .where(condition)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != inventoryIterator && inventoryIterator.getResultsSizeAfterPartialList() > 0){
        totalCount = inventoryIterator.getResultsSizeAfterPartialList();
        pageList = inventoryIterator.getPartialList(lowIndex, viewSize);
    }
    List<GenericValue> warehouseList = EntityQuery.use(delegator).select().from("TblWarehouseInfo").where().queryList();
    if (!UtilValidate.isNotEmpty(warehouseList)){
        warehouseList = new ArrayList<GenericValue>();
    }
    List<GenericValue> productTypeList = EntityQuery.use(delegator).select().from("TblProductType").where().queryList();
    if (!UtilValidate.isNotEmpty(productTypeList)){
        productTypeList = new ArrayList<GenericValue>();
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("warehouseId",warehouseId);
    map.put("productTypeId",productTypeId);
    map.put("suppliesName",suppliesName);
    map.put("warehouseList",warehouseList);
    map.put("productTypeList",productTypeList);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> EditInputInventory(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String osManagementId = context.get("osManagementId");
    if (UtilValidate.isNotEmpty(osManagementId)){
        GenericValue inventory = EntityQuery.use(delegator)
                .select()
                .from("InventoryInfo")
                .where(EntityCondition.makeCondition("osManagementId",EntityOperator.EQUALS,osManagementId))
                .queryOne();
        String warehouseId = "";
        if (UtilValidate.isNotEmpty(inventory)){
            warehouseId = inventory.get("warehouseId");
        }
        List<GenericValue> locationList = EntityQuery.use(delegator)
                .select()
                .from("TblLocation")
                .where(EntityCondition.makeCondition("warehouseId",EntityOperator.EQUALS,warehouseId))
                .queryList();
        List<GenericValue> inputInventoryTypeList = EntityQuery.use(delegator)
                .select()
                .from("Enumeration")
                .where(UtilMisc.toMap("enumTypeId","INPUTINVENTOEY_TYPE"))
                .queryList();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("list",inventory);
        map.put("locationList",locationList);
        map.put("inputInventoryTypeList",inputInventoryTypeList);
        successResult.put("data",map);
    }
    return successResult;
}

public Map<String,Object> EditOutInventory(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String osManagementId = context.get("osManagementId");
    if (UtilValidate.isNotEmpty(osManagementId)){
        GenericValue inventory = EntityQuery.use(delegator)
                .select()
                .from("InventoryInfo")
                .where(EntityCondition.makeCondition("osManagementId",EntityOperator.EQUALS,osManagementId))
                .queryOne();
        List<GenericValue> outInventoryTypeList = EntityQuery.use(delegator)
                .select()
                .from("Enumeration")
                .where(UtilMisc.toMap("enumTypeId","OUTINVENTORY_TYPE"))
                .queryList();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("list",inventory);
        map.put("outInventoryTypeList",outInventoryTypeList);
        successResult.put("data",map);
    }
    return successResult;
}

public Map<String,Object> saveInputInventory(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String osManagementId = context.get("osManagementId");
    String inputInventoryId = context.get("inputInventoryId");
    String msg = "";
    try {
        if (UtilValidate.isNotEmpty(inputInventoryId)) {
            GenericValue inputInventory = delegator.makeValidValue("TblInputInventory", context);
            inputInventory.store();
        } else {
            inputInventoryId = delegator.getNextSeqId("TblInputInventory");
            context.put("inputInventoryId", inputInventoryId);
            GenericValue inputInventory = delegator.makeValidValue("TblInputInventory", context);
            inputInventory.create();
        }
        String storageLocation = context.get("storageLocation");
        int inputInventoryAmount = Integer.valueOf(context.get("inputInventoryAmount"));
        int repertoryAmount = Integer.valueOf(context.get("repertoryAmount"));
        GenericValue product = delegator.makeValidValue("TblProduct",UtilMisc.toMap("osManagementId",osManagementId,"storageLocation",storageLocation,
                "repertoryAmount",repertoryAmount+inputInventoryAmount));
        product.store();

        msg = "操作成功";
    } catch (Exception e) {
        msg = "操作失败";
        e.printStackTrace();
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> saveOutInventory(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String outInventoryId = context.get("outInventoryId");
    String msg = "";
    try {
        if (UtilValidate.isNotEmpty(outInventoryId)){
            GenericValue outInventory = delegator.makeValidValue("TblOutInventory",context);
            outInventory.store();
        } else {
            outInventoryId = delegator.getNextSeqId("TblOutInventory");
            context.put("outInventoryId",outInventoryId);
            GenericValue outInventory = delegator.makeValidValue("TblOutInventory",context);
            outInventory.create();
        }
        int outInventoryAmount = Integer.valueOf(context.get("outInventoryAmount"));
        int repertoryAmount = Integer.valueOf(context.get("repertoryAmount"));
        GenericValue product = delegator.makeValidValue("TblProduct",UtilMisc.toMap("osManagementId",osManagementId, "repertoryAmount",repertoryAmount-outInventoryAmount));
        product.store();
        msg = "操作成功";
    } catch (Exception e) {
        msg = "操作失败";
        e.printStackTrace();
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}