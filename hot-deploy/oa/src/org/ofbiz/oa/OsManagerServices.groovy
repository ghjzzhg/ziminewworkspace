package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.Delegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityComparisonOperator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityFieldValue
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil
/**
 * Created by galaxypan on 2015/5/21.
 */

/**
 * 创建消耗品时，获取数据
 * @return
 */
public Map<String,Object> createOsManager(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess(); ;
    Map<String,Object> valueMap = new HashMap<String,Object>()
    String osManagementId = context.get("osManagementId");
    GenericValue list = EntityQuery.use(delegator).select().from("ProductList").where(UtilMisc.toMap("osManagementId",osManagementId)).queryOne();
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
    List<GenericValue> productTypeList = EntityQuery.use(delegator).select().from("TblProductType").where(EntityCondition.makeCondition("deletedType",EntityOperator.EQUALS,"N")).queryList();
    if (!UtilValidate.isNotEmpty(productTypeList)){
        productTypeList = new ArrayList<GenericValue>();
    }
    String warehouseId = "";
    if (UtilValidate.isNotEmpty(list)){
        warehouseId = list.get("warehouseId");
    }
    List<GenericValue> locationList = EntityQuery.use(delegator)
            .select()
            .from("TblLocation")
            .where(EntityCondition.makeCondition("warehouseId",EntityOperator.EQUALS,warehouseId))
            .queryList();
    valueMap.put("list",list)
    valueMap.put("unitList",unitList);
    valueMap.put("standardList",standardList);
    valueMap.put("warehouseList",warehouseList);
    valueMap.put("productTypeList",productTypeList);
    valueMap.put("locationList",locationList);
    successResult.put("data",valueMap);
    return successResult;
}

public Map<String, Object> saveOsManager() {
    //返回值集合
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    Delegator delegator = dctx.getDelegator();
    String msg = "";
    try {
        String osManagementId = context.get("osManagementId");
        GenericValue userLogin = context.get("userLogin");
        if (UtilValidate.isNotEmpty(osManagementId)){
            GenericValue NoticeTemplate = delegator.makeValidValue("TblProduct", "osManagementId", osManagementId);
            NoticeTemplate.setNonPKFields(context);
            NoticeTemplate.store();
            msg = "更新成功！";
        } else {
            osManagementId = delegator.getNextSeqId("TblProduct");
            context.put("osManagementId",osManagementId);
            context.put("inputPerson",userLogin.get("partyId"));
            context.put("notReceiveAmount",0);
            context.put("canUseAmount",context.get("repertoryAmount"));
            GenericValue createSummary = delegator.makeValidValue("TblProduct",context);
            createSummary.create();
            msg = "创建成功！";
        }
        Map message =  new HashMap();
        message.put("msg",msg);
        successResult.put("data",message);
    } catch (Exception e) {
        e.printStackTrace();
    }

    return successResult;
}

public Map<String, Object> searchWarehouse() {
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String productTypeId = context.get("productTypeId");
    if (UtilValidate.isNotEmpty(productTypeId)){
        GenericValue rewordsPunishment = EntityQuery.use(delegator)
                .select()
                .from("TblProductTypeInfo")
                .where(EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,productTypeId))
                .queryOne();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("warehouseName",rewordsPunishment.get("warehouseName"));
        map.put("warehouseId",rewordsPunishment.get("warehouseId"));
        map.put("MaximumSafety",rewordsPunishment.get("MaximumSafety"));
        map.put("MinimumSafety",rewordsPunishment.get("MinimumSafety"));
        map.put("unit",rewordsPunishment.get("unitName"));
        List<GenericValue> locationList = EntityQuery.use(delegator)
                .select()
                .from("TblLocation")
                .where(EntityCondition.makeCondition("warehouseId",EntityOperator.EQUALS,rewordsPunishment.get("warehouseId")))
                .queryList();
        map.put("locationList",locationList);
        successResult.put("data",map);
    }
    return successResult;
}

public Map<String, Object> findOsManager() {
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

    Map<String,Object> returnSuccess = ServiceUtil.returnSuccess();
    String suppliesName = context.get("suppliesName");
    String productTypeId = context.get("productTypeId");
    String repertoryWarning = context.get("warning");

    List<EntityCondition> conditionList = FastList.newInstance();
    if (UtilValidate.isNotEmpty(suppliesName)){
        conditionList.add(EntityCondition.makeCondition("suppliesName",EntityOperator.LIKE,"%" + suppliesName+"%"));
    }
    if (UtilValidate.isNotEmpty(productTypeId)){
        conditionList.add(EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,productTypeId));
    }
    if (UtilValidate.isNotEmpty(repertoryWarning)){
        if (repertoryWarning.equals("GREATER_THAN")){
            conditionList.add(EntityCondition.makeCondition("repertoryAmount", EntityComparisonOperator.GREATER_THAN, EntityFieldValue.makeFieldValue("MaximumSafety")));
        } else if(repertoryWarning.equals("LESS_THAN")){
            conditionList.add(EntityCondition.makeCondition("repertoryAmount", EntityComparisonOperator.LESS_THAN, EntityFieldValue.makeFieldValue("MinimumSafety")));
        }
        conditionList.add(EntityCondition.makeCondition("repertoryWarning",EntityOperator.EQUALS,"Y"));
    }
    EntityListIterator rewordsPunishmentIterator = EntityQuery.use(delegator)
            .select()
            .from("ProductList")
            .where(conditionList)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != rewordsPunishmentIterator && rewordsPunishmentIterator.getResultsSizeAfterPartialList() > 0){
        totalCount = rewordsPunishmentIterator.getResultsSizeAfterPartialList();
        pageList = rewordsPunishmentIterator.getPartialList(lowIndex, viewSize);
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("suppliesName",suppliesName);
    map.put("productTypeId",productTypeId);
    map.put("repertoryWarning",repertoryWarning);
    returnSuccess.put("data",map);
    return returnSuccess;
}






