
import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.Delegator
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

public Map<String,Object> createCustomerInfo(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String customerInfoId = context.get("customerInfoId");
    GenericValue list = EntityQuery.use(delegator).select().from("TblCustomerInfo").where(UtilMisc.toMap("customerInfoId",customerInfoId)).queryOne();

    List<GenericValue> typeList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","CUSTOMER_TYPE")).queryList();
    if (!UtilValidate.isNotEmpty(typeList)){
        typeList = new ArrayList<GenericValue>();
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",list);
    map.put("typeList",typeList);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> saveCustomer(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    Delegator delegator = dctx.getDelegator();
    String msg = "";
    try {
        String customerInfoId = context.get("customerInfoId");
        GenericValue userLogin = context.get("userLogin");
        if (UtilValidate.isNotEmpty(customerInfoId)){
            GenericValue NoticeTemplate = delegator.makeValidValue("TblCustomerInfo", "customerInfoId", customerInfoId);
            NoticeTemplate.setNonPKFields(context);
            NoticeTemplate.store();
            msg = "更新成功！";
        } else {
            customerInfoId = delegator.getNextSeqId("TblCustomerInfo");
            context.put("customerInfoId",customerInfoId);
            context.put("inputPerson",userLogin.get("partyId"));
            GenericValue createSummary = delegator.makeValidValue("TblCustomerInfo",context);
            createSummary.create();
            msg = "创建成功！";
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    Map message =  new HashMap();
    message.put("msg",msg);
    successResult.put("data",message);
    return successResult;
}

public Map<String,Object> deleteCustomerInfo(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String msg = "";
    String customerInfoId = context.get("customerInfoId");
    if (UtilValidate.isNotEmpty(customerInfoId)){
        try {
            GenericValue NoticeTemplate = delegator.makeValidValue("TblCustomerInfo", "customerInfoId", customerInfoId);
            NoticeTemplate.remove();
            msg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Map message = new HashMap();
    message.put("msg",msg);
    successResult.put("data",message);
    return successResult;
}

public Map<String,Object> searchCustomerInfo(){
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
    String customerType = context.get("customerType");
    String customerName = context.get("customerName");

    List<EntityCondition> conditionList = FastList.newInstance();
    if (UtilValidate.isNotEmpty(customerName)){
        conditionList.add(EntityCondition.makeCondition("customerName",EntityOperator.LIKE,"%" + customerName+"%"));
    }
    if (UtilValidate.isNotEmpty(customerType)){
        conditionList.add(EntityCondition.makeCondition("customerType",EntityOperator.EQUALS,customerType));
    }
    EntityListIterator rewordsPunishmentIterator = EntityQuery.use(delegator)
            .select()
            .from("CustomerInfo")
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
    map.put("customerType",customerType);
    map.put("customerName",customerName);

    successResult.put("data",map);
    return successResult;
}