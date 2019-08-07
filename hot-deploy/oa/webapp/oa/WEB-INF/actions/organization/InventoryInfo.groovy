import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat
/**
 * 查看仓库列表信息
 */
public Map<String,Object> InventoryInfoList(){
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

    EntityListIterator InventoryTypes = EntityQuery.use(delegator).select()
            .from("TblWarehouseInfo")
            .where()
            .orderBy("inputTime")
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != InventoryTypes && InventoryTypes.getResultsSizeAfterPartialList() > 0){
        totalCount = InventoryTypes.getResultsSizeAfterPartialList();
        pageList = InventoryTypes.getPartialList(lowIndex, viewSize);
    }

    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Map<String, Object> objectMap = ServiceUtil.returnSuccess();
    objectMap.put("list",pageList)
    objectMap.put("viewIndex",viewIndex);
    objectMap.put("highIndex",highIndex);
    objectMap.put("totalCount",totalCount);
    objectMap.put("viewSize",viewSize);
    objectMap.put("lowIndex",lowIndex);
    successResult.put("data",objectMap);
    return successResult;
}

/**
 *添加仓库信息时创建的仓库编号
 */
public Map<String,Object> createWarehouseInfo(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    Map map = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblWarehouse","prefix","warehouse","numName","warehouseCode","userLogin",userLogin));;
    String warehouseCode = map.get("number");
    successResult.put("data",warehouseCode);
    return successResult;
}

/**
 * 增加加仓库信息
 */
/*
public Map<String,Object> saveWarehouseInfo(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String msg = "";
    Map message =  new HashMap();
    try {
        String warehouseId = context.get("warehouseId");
        Timestamp inputTime = new Timestamp((new Date()).getTime());
        if (UtilValidate.isNotEmpty(warehouseId)) {
            GenericValue NoticeTemplate = delegator.makeValidValue("TblWarehouse", "warehouseId", warehouseId);
            context.put("lastEditPerson", userLogin.get("partyId"));
            context.put("lastEditTime", inputTime);
            NoticeTemplate.setNonPKFields(context);
            NoticeTemplate.store();
            msg = "操作成功！";
        } else {
            warehouseId = delegator.getNextSeqId("TblWarehouse");
            context.put("warehouseId",warehouseId);
            context.put("inputPerson",userLogin.get("partyId"));
            context.put("inputTime",inputTime);
            context.put("lastEditPerson",userLogin.get("partyId"));
            context.put("lastEditTime",inputTime);
            context.put("deletedType","N");//N为未删除，Y是已删除
            GenericValue createSummary = delegator.makeValidValue("TblWarehouse",context);
            createSummary.create();
            msg = "操作成功！";
        }

        message.put("msg",msg);
        successResult.put("data",message);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult;
}*/

/**
 * 仓库消息删除
 */
public Map<String,Object> deleteInventoryInfo(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    try {
        String warehouseId = context.get("warehouseId");
        String deletedType = "Y";
        context.put("deletedType", deletedType);
        String msg = "";
        if(UtilValidate.isNotEmpty(warehouseId)){
            GenericValue NoticeTemplate = delegator.makeValidValue("TblWarehouse","warehouseId",warehouseId);
            NoticeTemplate.setNonPKFields(context);
            NoticeTemplate.store();
            msg = "删除成功";
        }
        successResult.put("msg",msg);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult
}

/**
 * 货位消息删除
 * @return
 */
public Map<String,Object> deleteLocation(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String locationId = context.get("locationId");
    String msg = "";
    try {
        if (UtilValidate.isNotEmpty(locationId)){
            GenericValue location = delegator.makeValidValue("TblLocation","locationId",locationId);
            location.remove();
            msg = "操作成功";
        }
        successResult.put("data",msg);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult;
}

/**
 * 仓库消息修改
 */
public Map<String,Object> editWarehouseInfo(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    try {
        String warehouseId = context.get("warehouseId");
        List<GenericValue> InventoryTypes = EntityQuery.use(delegator)
                .select()
                .from("TblWarehouse")
                .where(EntityCondition.makeCondition("warehouseId",EntityOperator.EQUALS,warehouseId))
                .queryList();
        List<GenericValue> locations = EntityQuery.use(delegator)
                .select()
                .from("TblLocation")
                 .where(EntityCondition.makeCondition("warehouseId",EntityOperator.EQUALS,warehouseId))
                 .queryList();
        Map<String,Object> map = new HashMap<>();
        map.put("list",InventoryTypes);
        map.put("locations",locations);
        successResult.put("data",map);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult;
}

/**
 * 仓库信息查询
 */
public Map<String,Object> searchWarehouseInfo(){
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
    String warehouseName = context.get("warehouseName");
    String linkman = context.get("man");
    List<EntityCondition> condition = FastList.newInstance();
    if (UtilValidate.isNotEmpty(warehouseName)){
        condition.add(EntityCondition.makeCondition("warehouseName", EntityOperator.LIKE, "%" + warehouseName + "%"))
    }
    if (UtilValidate.isNotEmpty(linkman)) {
        condition.add(EntityCondition.makeCondition("linkman", EntityOperator.EQUALS, linkman));
    }
    EntityListIterator InventoryTypes = EntityQuery.use(delegator)
            .select()
            .from("TblWarehouseInfo")
            .where(condition)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != InventoryTypes && InventoryTypes.getResultsSizeAfterPartialList() > 0){
        totalCount = InventoryTypes.getResultsSizeAfterPartialList();
        pageList = InventoryTypes.getPartialList(lowIndex, viewSize);
    }
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for (Map<String,Object> map : pageList) {
        map.put("inputTime",format.format(map.get("inputTime")));
        map.put("lastEditTime",format.format(map.get("lastEditTime")));
    }
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    Map<String, Object> objectMap = ServiceUtil.returnSuccess();
    objectMap.put("list",pageList)
    objectMap.put("viewIndex",viewIndex);
    objectMap.put("highIndex",highIndex);
    objectMap.put("totalCount",totalCount);
    objectMap.put("viewSize",viewSize);
    objectMap.put("lowIndex",lowIndex);
    successResult.put("data",objectMap);
    return successResult;
}


