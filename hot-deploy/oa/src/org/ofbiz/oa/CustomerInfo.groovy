package org.ofbiz.oa

/**
 * Created by rextec-15-1 on 2016/4/9.
 */
import javolution.util.FastList
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.Delegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

public Map<String,Object> saveCustomer(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    Delegator delegator = dctx.getDelegator();
    String msg = "";
    try {
        String customerInfoId = context.get("customerInfoId");
        GenericValue userLogin = context.get("userLogin");
        if (UtilValidate.isNotEmpty(osManagementId)){
            GenericValue NoticeTemplate = delegator.makeValidValue("TblCustomerInfo", "customerInfoId", customerInfoId);
            NoticeTemplate.setNonPKFields(context);
            NoticeTemplate.store();
            msg = "修改成功";
        } else {
            customerInfoId = delegator.getNextSeqId("TblCustomerInfo");
            context.put("customerInfoId",customerInfoId);
            context.put("inputPerson",userLogin.get("partyId"));
            GenericValue createSummary = delegator.makeValidValue("TblCustomerInfo",context);
            createSummary.create();
            msg = "保存成功";
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    Map message =  new HashMap();
    message.put("msg",msg);
    successResult.put("data",message);
    return successResult;
}

public Map<String,Object> searchCustomerInfo(){
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
            .from("TblCustomerInfo")
            .where(conditionList)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != rewordsPunishmentIterator && rewordsPunishmentIterator.getResultsSizeAfterPartialList() > 0){
        totalCount = rewordsPunishmentIterator.getResultsSizeAfterPartialList();
        pageList = rewordsPunishmentIterator.getPartialList(1, 5);
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("aa","aa");

    returnSuccess.put("data",map);
    return successResult;
}