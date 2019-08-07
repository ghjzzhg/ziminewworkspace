package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil
import sun.text.resources.FormatData

import javax.swing.text.html.parser.Entity
import java.sql.Date
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

public Map<String,Object> createProposal(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    Map map = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblProposal","prefix","proposal","numName","proposalNumber","userLogin",userLogin));;
    String proposalNumber = map.get("number");

    List<GenericValue> proposalTypeList = EntityQuery.use(delegator)
            .select()
            .from("Enumeration")
            .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROPOSAL_TYPE"))
            .queryList();
    Map valueMap = new HashMap();
    valueMap.put("proposalNumber",proposalNumber);
    valueMap.put("proposalTypeList",proposalTypeList);
    successResult.put("data",valueMap);
    return successResult;
}

public Map<String, Object> saveProposal() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "";
    GenericValue userLogin = context.get("userLogin");
   String proposalId = context.get("proposalId");
    context.put("submitDate",new Timestamp(System.currentTimeMillis()));
    if (UtilValidate.isNotEmpty(proposalId)){
        context.put("lastEditPerson",userLogin.get("partyId"));
        GenericValue unitContract = delegator.makeValidValue("TblProposal",context);
        unitContract.store();

        String entityName = context.get("canSeePerson_entity_name");
        String deptOnly = context.get("canSeePerson_dept_only");
        String deptLike = context.get("canSeePerson_dept_like");
        String levelOnly = context.get("canSeePerson_level_only");
        String levelLike = context.get("canSeePerson_level_like");
        String positionOnly = context.get("canSeePerson_position_only");
        String positionLike = context.get("canSeePerson_position_like");
        String dataScopeUser = context.get("canSeePerson_user");
        dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName",entityName,"deptOnly",deptOnly,
                "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                "positionLike",positionLike,"userValue",dataScopeUser,"dataId",proposalId,"userLogin",userLogin));

        msg = "操作成功";
    }else {
        proposalId = delegator.getNextSeqId("TblProposal");
        context.put("proposalId",proposalId);
        context.put("lastEditPerson",userLogin.get("partyId"));
        GenericValue unitContract = delegator.makeValidValue("TblProposal",context);
        unitContract.create();

        String entityName = context.get("canSeePerson_entity_name");
        String deptOnly = context.get("canSeePerson_dept_only");
        String deptLike = context.get("canSeePerson_dept_like");
        String levelOnly = context.get("canSeePerson_level_only");
        String levelLike = context.get("canSeePerson_level_like");
        String positionOnly = context.get("canSeePerson_position_only");
        String positionLike = context.get("canSeePerson_position_like");
        String dataScopeUser = context.get("canSeePerson_user");
        dispatcher.runSync("saveDataScope",UtilMisc.toMap("entityName",entityName,"deptOnly",deptOnly,
                "deptLike",deptLike,"levelOnly",levelOnly,"levelLike",levelLike,"positionOnly",positionOnly,
                "positionLike",positionLike,"userValue",dataScopeUser,"dataId",proposalId,"userLogin",userLogin));
        msg = "操作成功";
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> searchProposalList(){

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

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String proposalType = context.get("proposalType");
    Timestamp submitDateStart = (Timestamp)context.get("submitDateStart");
    Timestamp submitDateEnd = (Timestamp)context.get("submitDateEnd");

    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(submitDateStart)&&UtilValidate.isNotEmpty(submitDateEnd)){
        timeCondition.add(submitDateStart);
        timeCondition.add(submitDateEnd);
    }
    List<EntityCondition> condition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(proposalType)){
        condition.add(EntityCondition.makeCondition("proposalType",EntityOperator.EQUALS,proposalType));
    }
    if (UtilValidate.isNotEmpty(timeCondition)){
        condition.add(EntityCondition.makeCondition("submitDate",EntityOperator.BETWEEN,timeCondition));
    }

    EntityListIterator unitContractList = EntityQuery.use(delegator)
            .select()
            .from("ProposalInfo")
            .where(condition)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != unitContractList && unitContractList.getResultsSizeAfterPartialList() > 0){
        totalCount = unitContractList.getResultsSizeAfterPartialList();
        pageList = unitContractList.getPartialList(lowIndex, viewSize);
    }
    unitContractList.close();
    List<GenericValue> proposalTypeList = EntityQuery.use(delegator)
            .select()
            .from("Enumeration")
            .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROPOSAL_TYPE"))
            .queryList();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("proposalTypeList",proposalTypeList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("proposalType",proposalType);
    map.put("submitDateStart",submitDateStart);
    map.put("submitDateEnd",submitDateEnd);
    successResult.put("data",map);
    return successResult;
}
public Map<String,Object> editProposal(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String proposalId = context.get("proposalId");
    if (UtilValidate.isNotEmpty(proposalId)){
        GenericValue proposal = EntityQuery.use(delegator)
                .select()
                .from("TblProposal")
                .where(EntityCondition.makeCondition("proposalId",EntityOperator.EQUALS,proposalId))
                .queryOne();
        List<GenericValue> proposalTypeList = EntityQuery.use(delegator)
                .select()
                .from("Enumeration")
                .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROPOSAL_TYPE"))
                .queryList();
        if (UtilValidate.isNotEmpty(proposal)){
            Map valueMap = new HashMap();
            valueMap.put("proposal",proposal);
            valueMap.put("proposalTypeList",proposalTypeList);
            successResult.put("data",valueMap);
        }
    }
    return successResult;
}

public Map<String,Object> deleteProposal(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String proposalId = context.get("proposalId");
    String msg = "";
    if(UtilValidate.isNotEmpty(proposalId)){
        GenericValue proposal = delegator.makeValidValue("TblProposal","proposalId",proposalId);
        proposal.remove();
        msg = "删除成功！";
    }
    Map map = new HashMap()
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}
