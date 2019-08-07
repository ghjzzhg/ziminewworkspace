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
 * Created by rextec-15-1 on 2016/4/12.
 */

/*public Map<String,Object> saveReceive(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String receiveId = context.get("receiveId");
    Timestamp makeInfoTime = new Timestamp((new Date()).getTime());
    String msg = "";
    try {
        if (!UtilValidate.isNotEmpty(receiveId)){
            Map mapValue = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblReceive","prefix","receive","numName","receiveCode","userLogin",userLogin));;
            String receiveCode = mapValue.get("number");
            receiveId = delegator.getNextSeqId("TblReceive");
            context.put("receiveId",receiveId);
            context.put("makeInfoTime",makeInfoTime);
            context.put("receiveCode",receiveCode);
            GenericValue createSummary = delegator.makeValidValue("TblReceive",context);
            createSummary.create();
            msg = "创建成功！";
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msg",msg);
        map.put("receiveId",receiveId);
        successResult.put("data",map);
    } catch (Exception e){
        e.printStackTrace();
    }
    return successResult;
}*/

public Map<String,Object> searchOutInventory(){
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
    String outInventoryType = context.get("outInventoryType");
    String receiveDepartment = context.get("receiveDepartment");
    String checkResult = context.get("checkResult");
    String receivePerson = context.get("receivePerson");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");
    GenericValue staff = EntityQuery.use(delegator)
            .select()
            .from("TblStaff")
            .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId))
            .queryOne();
    List<EntityCondition> conditionList = FastList.newInstance();
    if (UtilValidate.isNotEmpty(warehouseId)){
        conditionList.add(EntityCondition.makeCondition("warehouseId",EntityOperator.EQUALS,warehouseId));
    }
    if (UtilValidate.isNotEmpty(outInventoryType)){
        conditionList.add(EntityCondition.makeCondition("outInventoryType",EntityOperator.EQUALS,outInventoryType));
    }
    if (UtilValidate.isNotEmpty(receiveDepartment)){
        conditionList.add(EntityCondition.makeCondition("receiveDepartmentId",EntityOperator.EQUALS,receiveDepartment));
    }
    if (UtilValidate.isNotEmpty(checkResult)){
        conditionList.add(EntityCondition.makeCondition("checkResult",EntityOperator.EQUALS,checkResult));
    }/* else {
        conditionList.add(EntityCondition.makeCondition("checkResult",EntityOperator.EQUALS,"RECEIVE_RESULT_FOUR"));
    }*/
    if (UtilValidate.isNotEmpty(receivePerson)){
        conditionList.add(EntityCondition.makeCondition("receivePersonId",EntityOperator.EQUALS,receivePerson));
    }
    EntityListIterator receiveIterator = EntityQuery.use(delegator)
            .select()
            .from("ReceiveInfo")
            .where(conditionList)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != receiveIterator && receiveIterator.getResultsSizeAfterPartialList() > 0){
        totalCount = receiveIterator.getResultsSizeAfterPartialList();
        pageList = receiveIterator.getPartialList(lowIndex, viewSize);
    }
    for (Map map :pageList){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        map.put("receiveDate",format.format(map.get("receiveDate")));
    }
    List<GenericValue> warehouseList = EntityQuery.use(delegator).select().from("TblWarehouseInfo").where().queryList();
    if (!UtilValidate.isNotEmpty(warehouseList)){
        warehouseList = new ArrayList<GenericValue>();
    }
    List<GenericValue> productTypeList = EntityQuery.use(delegator).select().from("TblProductType").where().queryList();
    if (!UtilValidate.isNotEmpty(productTypeList)){
        productTypeList = new ArrayList<GenericValue>();
    }
    List<GenericValue> outInventoryTypeList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","OUTINVENTORY_TYPE")).queryList();
    if (!UtilValidate.isNotEmpty(outInventoryTypeList)){
        outInventoryTypeList = new ArrayList<GenericValue>();
    }
    List<GenericValue> receiveResultList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","RECEIVE_RESULT")).queryList();
    if (!UtilValidate.isNotEmpty(receiveResultList)){
        receiveResultList = new ArrayList<GenericValue>();
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("warehouseList",warehouseList);
    map.put("productTypeList",productTypeList);
    map.put("outInventoryTypeList",outInventoryTypeList);
    map.put("receiveResultList",receiveResultList)
    map.put("warehouseId",warehouseId);
    map.put("outInventoryType",outInventoryType);
    map.put("receiveDepartment",receiveDepartment);
    map.put("checkResult",checkResult);
    map.put("receivePerson",receivePerson);
    map.put("post",staff.get("post"));

    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> searchReceive(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String receiveId = context.get("receiveId");
    GenericValue receive = EntityQuery.use(delegator)
            .select()
            .from("ReceiveInfo")
            .where(EntityCondition.makeCondition("receiveId",EntityOperator.EQUALS,receiveId))
            .queryOne();
    List<GenericValue> productInfoList = EntityQuery.use(delegator)
            .select()
            .from("TblAddProductInfo")
             .where(EntityCondition.makeCondition("receiveId",EntityOperator.EQUALS,receiveId))
             .queryList();
    Map map = new HashMap();
    map.put("list",receive);
    map.put("productInfoList",productInfoList);
    successResult.put("data",map);
    return successResult;
}

/**
 * 审核通过
 * @return
 */
public Map<String,Object> checkAllow(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String receiveId = context.get("receiveId");
    String msg = "";
    if (UtilValidate.isNotEmpty(receiveId)){
        context.put("checkResult","RECEIVE_RESULT_ONE");
        GenericValue createSummary = delegator.makeValidValue("TblReceive",context);
        createSummary.store();
        msg = "操作成功！";
    }
    if (UtilValidate.isNotEmpty(receiveId)){
        List<GenericValue> addProductList = EntityQuery.use(delegator)
                .select()
                .from("TblAddProduct")
                .where(EntityCondition.makeCondition("receiveId",EntityOperator.EQUALS,receiveId))
                .queryList();
        for (GenericValue map : addProductList){
            String osManagementId = map.get("osManagementId");
            GenericValue productInfo = EntityQuery.use(delegator)
                    .select()
                    .from("TblProduct")
                    .where(EntityCondition.makeCondition("osManagementId",EntityOperator.EQUALS,osManagementId))
                    .queryOne();
            int notReceiveAmount = Integer.valueOf(productInfo.get("notReceiveAmount"));
            int canUseAmount = Integer.valueOf(productInfo.get("canUseAmount"))
            int number = Integer.valueOf(map.get("number"));
            notReceiveAmount = notReceiveAmount+number;
            GenericValue product = delegator.makeValidValue("TblProduct",UtilMisc.toMap("osManagementId",osManagementId,"notReceiveAmount",notReceiveAmount,"canUseAmount",canUseAmount-notReceiveAmount));
            product.store();
        }
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}

/**
 * 审核不通过
 * @return
 */
public Map<String,Object> checkRefuse(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String receiveId = context.get("receiveId");
    String msg = "";
    if (UtilValidate.isNotEmpty(receiveId)){
        context.put("checkResult","RECEIVE_RESULT_TWO");
        GenericValue createSummary = delegator.makeValidValue("TblReceive",context);
        createSummary.store();
        msg = "操作成功！";
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}
