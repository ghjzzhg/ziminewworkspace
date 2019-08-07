import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
import sun.security.x509.GeneralName

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 *添加货品类型时创建的货品类型编号
 */
public Map<String,Object> createPrdouctTypeCode(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    Map map = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblProductType","prefix","productType","numName","productTypeCode","userLogin",userLogin));;
    String productTypeCode = map.get("number");
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
   Map<String,Object> listMap = new HashMap<String,Object>();
    listMap.put("productTypeCode",productTypeCode);
    list.add(listMap);
    Map<String,Object> valueMap = new HashMap<String,Object>()
    List<GenericValue> unitList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","PRODUCT_UNIT")).queryList();
    if(!UtilValidate.isNotEmpty(unitList)){
        unitList = new ArrayList<GenericValue>();
    }
    List<GenericValue> standardList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","PRODUCT_STANDARD")).queryList();
    if (!UtilValidate.isNotEmpty(standardList)){
        standardList = new ArrayList<GenericValue>();
    }
    List<GenericValue> warehouseList = EntityQuery.use(delegator).select().from("TblWarehouseInfo").where().queryList();
    if (!UtilValidate.isNotEmpty(warehouseList)){
        warehouseList = new ArrayList<GenericValue>();
    }
    valueMap.put("list",list);
    valueMap.put("unitList",unitList);
    valueMap.put("standardList",standardList);
    valueMap.put("warehouseList",warehouseList);
    successResult.put("data",valueMap);
    return successResult;
}

/**
 *货品类别信息保存
 */
public Map<String,Object> saveProductType(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String msg = "";
    Map message =  new HashMap();
    try {
        String productTypeId = context.get("productTypeId");
        Timestamp inputTime = new Timestamp((new Date()).getTime());
        if (UtilValidate.isNotEmpty(productTypeId)) {
            GenericValue NoticeTemplate = delegator.makeValidValue("TblProductType", "productTypeId", productTypeId);
            context.put("lastEditPerson", userLogin.get("partyId"));
            context.put("lastEditTime", inputTime);
            NoticeTemplate.setNonPKFields(context);
            NoticeTemplate.store();
            msg = "操作成功！";
        } else {
            productTypeId = delegator.getNextSeqId("TblProductType");
            context.put("productTypeId",productTypeId);
            context.put("inputPerson",userLogin.get("partyId"));
            context.put("inputTime",inputTime);
            context.put("lastEditPerson",userLogin.get("partyId"));
            context.put("lastEditTime",inputTime);
            context.put("deletedType","N");//N为未删除，Y是已删除
            GenericValue createSummary = delegator.makeValidValue("TblProductType",context);
            createSummary.create();
            msg = "操作成功！";
        }
        message.put("msg",msg);
        successResult.put("data",message);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult;
}

/**
 * 货品类别删除
 */
public Map<String,Object> deleteProductTypeInfo(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    try {
        String productTypeId = context.get("productTypeId");
        String deletedType = "Y";
        context.put("deletedType", deletedType);
        String msg = "";
        if(UtilValidate.isNotEmpty(productTypeId)){
            GenericValue ProductTypeTemplate = delegator.makeValidValue("TblProductType","productTypeId",productTypeId);
            ProductTypeTemplate.setNonPKFields(context);
            ProductTypeTemplate.store();
            msg = "删除成功";
        }
        successResult.put("msg",msg);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult
}

/**
 * 货品类别修改
 */
public Map<String,Object> EditProductTypeInfo(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    try {
        String productTypeId = context.get("productTypeId");
        GenericValue InventoryTypes = EntityQuery.use(delegator)
                .select().from("TblProductType")
                .where(EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,productTypeId))
                .queryOne();
        Map<String,Object> map = new HashMap<>();
        map.putAll(InventoryTypes);
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        list.add(map);
        Map<String,Object> valueMap = new HashMap<String,Object>()
        List<GenericValue> unitList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","PRODUCT_UNIT")).queryList();
        if(!UtilValidate.isNotEmpty(unitList)){
            unitList = new ArrayList<GenericValue>();
        }
        List<GenericValue> standardList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","PRODUCT_STANDARD")).queryList();
        if (!UtilValidate.isNotEmpty(standardList)){
            standardList = new ArrayList<GenericValue>();
        }
        List<GenericValue> warehouseList = EntityQuery.use(delegator).select().from("TblWarehouseInfo").where().queryList();
        if (!UtilValidate.isNotEmpty(warehouseList)){
            warehouseList = new ArrayList<GenericValue>();
        }
        valueMap.put("list",list);
        valueMap.put("unitList",unitList);
        valueMap.put("standardList",standardList);
        valueMap.put("warehouseList",warehouseList);
        successResult.put("data",valueMap);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return successResult;
}

/**
 * 货品类别信息查询
 */
public Map<String,Object> searchProductTypeInfo(){
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
    String typeName = context.get("typeName");
    String inputPerson = context.get("inputPerson");
    String standard = context.get("standard");
    String warehouseId = context.get("warehouseId");
    List<EntityCondition> condition = FastList.newInstance();
    if (UtilValidate.isNotEmpty(typeName)){
        condition.add(EntityCondition.makeCondition("typeName", EntityOperator.LIKE, "%" + typeName + "%"))
    }
    if (UtilValidate.isNotEmpty(inputPerson)) {
        condition.add(EntityCondition.makeCondition("inputPerson", EntityOperator.EQUALS, inputPerson));
    }
    if (UtilValidate.isNotEmpty(standard)) {
        condition.add(EntityCondition.makeCondition("standard", EntityOperator.EQUALS, standard));
    }
    if (UtilValidate.isNotEmpty(warehouseId)) {
        condition.add(EntityCondition.makeCondition("warehouseId", EntityOperator.EQUALS, warehouseId));
    }
    EntityListIterator InventoryTypes = EntityQuery.use(delegator)
            .select()
            .from("TblProductTypeInfo")
            .where(condition)
            .queryIterator();
    List<GenericValue> standardList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","PRODUCT_STANDARD")).queryList();
    if (!UtilValidate.isNotEmpty(standardList)){
        standardList = new ArrayList<GenericValue>();
    }
    List<GenericValue> warehouseList = EntityQuery.use(delegator).select().from("TblWarehouseInfo").where().queryList();
    if (!UtilValidate.isNotEmpty(warehouseList)){
        warehouseList = new ArrayList<GenericValue>();
    }
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != InventoryTypes && InventoryTypes.getResultsSizeAfterPartialList() > 0){
        totalCount = InventoryTypes.getResultsSizeAfterPartialList();
        pageList = InventoryTypes.getPartialList(lowIndex, viewSize);
    }
    InventoryTypes.close();
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
    objectMap.put("standardList",standardList);
    objectMap.put("warehouseList",warehouseList);
    successResult.put("data",objectMap);
    return successResult;
}
