package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp

public Map<String,Object> findRecord(){
    Map<String,Object> successResult = FastMap.newInstance();
    String noticeId = context.get("noticeId");
    String type = context.get("type");

    List<EntityCondition> conditionList = FastList.newInstance();
    if(UtilValidate.isNotEmpty(noticeId)){
        conditionList.add(EntityCondition.makeCondition("noticeId", EntityOperator.EQUALS, noticeId));
    }
    if(UtilValidate.isNotEmpty(type)){
        conditionList.add(EntityCondition.makeCondition("signInPersonType", EntityOperator.EQUALS, type));
    }

    //------------------------------------------
    EntityListIterator eli = null;
    try {
        // 分页参数
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 5;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 5;
        }

        String sortField = (String) context.get("sortField");
        List<String> orderBy = FastList.newInstance();
        if (UtilValidate.isNotEmpty(sortField)) {
            orderBy.add(sortField);
        }

        // 计算当前显示页的最小、最大索引(可能会超出实际条数)
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;
        //--------------------------------------------------

        eli = EntityQuery.use(delegator)
                .from("SignInPersonInfo")
                .where(EntityCondition.makeCondition(conditionList))
                .orderBy(orderBy)
                .cursorScrollInsensitive()
                .fetchSize(highIndex)
                .queryIterator();
        // 获取结果片段
        List<GenericValue> members = eli.getPartialList(lowIndex, viewSize);
        // 获取实际总条数
        int memberSize = eli.getResultsSizeAfterPartialList();
        if (highIndex > memberSize) {
            highIndex = memberSize;
        }
        successResult.put("returnValue", members);
        successResult.put("viewIndex", viewIndex);
        successResult.put("viewSize", viewSize);
        successResult.put("highIndex", highIndex);
        successResult.put("lowIndex", lowIndex);
        successResult.put("totalCount", memberSize);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询签收记录错误");
    } finally {
        if (eli != null) {
            eli.close();
        }
    }

    return successResult;
}

public Map<String,Object> updateSignInRecord(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String userLoginId = userLogin.get("partyId");
    String singnInPersonId = context.get("singnInPersonId");
    String staffId = context.get("staffId");
    String signInStatus = context.get("signInStatus");
    String status = context.get("signInPersonStatus");
    String remark = context.get("remark");
    if(UtilValidate.isEmpty(remark)){
        remark = "";
    }
    if(UtilValidate.isNotEmpty(singnInPersonId)){
        Map<String,Object> param = FastMap.newInstance();
        param.put("singnInPersonId",singnInPersonId);
        Timestamp nowDate = new Timestamp(new java.util.Date().getTime());
        if(status != "NS_NOT_SEE"){
            param.put("signInPersonStatus",status);
            param.put("hasSignIn","NS_Y");
            param.put("signInTime",nowDate);
        }else if(signInStatus == "NS_N"){
            param.put("hasSignIn",signInStatus);
            param.put("signInPersonStatus",status);
        }else if(signInStatus == "NS_Y" || !UtilValidate.isNotEmpty(signInStatus)){
            param.put("signInPersonStatus",status);
            param.put("hasSignIn","NS_Y");
            param.put("signInTime",nowDate);
        }
        if(userLoginId!=staffId){
            GenericValue person = EntityQuery.use(delegator)
                    .from("Person")
                    .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLoginId))
                    .queryOne();
            String strRemark = person.get("fullName")+"代反馈；"+remark;
            param.put("remark",strRemark);
        }else {
            param.put("remark",remark);
        }
        GenericValue signInPerson = delegator.makeValidValue("TblSignInPerson",param);
        signInPerson.store();
    }
    successResult.put("msg","更新成功！");
    return successResult;
}
