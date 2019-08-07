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

import javax.swing.text.html.parser.Entity
import java.sql.Date
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

public Map<String,Object> createUnitContract(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    Map map = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblUnitContract","prefix","unitContract","numName","contractNumber","userLogin",userLogin));;
    String contractNumber = map.get("number");
    List<GenericValue> paymentList = EntityQuery.use(delegator)
            .select()
            .from("Enumeration")
            .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PAYMENT_TYPE"))
            .queryList();
    List<GenericValue> contractTypeList = EntityQuery.use(delegator)
            .select()
            .from("Enumeration")
            .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"UNIT_CONTRACT_TYPE"))
            .queryList();
    Map valueMap = new HashMap();
    valueMap.put("contractNumber",contractNumber);
    valueMap.put("paymentList",paymentList);
    valueMap.put("contractTypeList",contractTypeList);
    successResult.put("data",valueMap);
    return successResult;
}

public Map<String, Object> saveUnitContract() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "";
   String unitContractId = context.get("unitContractId");
    if (UtilValidate.isNotEmpty(unitContractId)){
        GenericValue unitContract = delegator.makeValidValue("TblUnitContract",context);
        unitContract.store();


        String fileId = context.get("fileId");
        List<String> newFileList = new ArrayList<String>();
        if(null != fileId && !"".equals(fileId)){
            String[] files = fileId.split(",");
            for(String file : files){
                newFileList.add(file);
            }
        }
        List<String> oldFileList = new ArrayList<String>();
        List<GenericValue> workAccessFile = EntityQuery.use(delegator)
                .select()
                .from("TblFileScope")
                .where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", unitContractId, "entityName", "TblUnitContract")))
                .queryList();
        if(null != workAccessFile && workAccessFile.size() > 0){
            for(GenericValue workAccess : workAccessFile){
                oldFileList.add(workAccess.get("accessoryId").toString());
            }
        }
        saveWorkFile(unitContractId,oldFileList,newFileList);
        msg = "操作成功";
    }else {
        unitContractId = delegator.getNextSeqId("TblUnitContract");
        context.put("unitContractId",unitContractId);
        GenericValue unitContract = delegator.makeValidValue("TblUnitContract",context);
        unitContract.create();

        String fileId = context.get("fileId");
        List<String> newFileList = new ArrayList<String>();
        if(null != fileId && !"".equals(fileId)){
            String[] files = fileId.split(",");
            for(String file : files){
                newFileList.add(file);
            }
        }
        List<String> oldFileList = new ArrayList<String>();
        List<GenericValue> workAccessFile = EntityQuery.use(delegator)
                .select().from("TblFileScope")
                .where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", unitContractId, "entityName", "TblUnitContract")))
                .queryList();
        if(null != workAccessFile && workAccessFile.size() > 0){
            for(GenericValue workAccess : workAccessFile){
                oldFileList.add(workAccess.get("accessoryId").toString());
            }
        }
        saveWorkFile(unitContractId,oldFileList,newFileList);
        msg = "操作成功";
    }
    Map map = new HashMap();
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}

public Map<String,Object> searchUnitContract(){

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
    String contractType = context.get("contractType");
    String contractNumber = context.get("contractNumber");
    String contractName = context.get("contractName");
    String department = context.get("department1");
    String customerName = context.get("customerName");
    String balanceOfPaymentsType = context.get("balanceOfPaymentsType");
    Double contractMoneyStart = (Double)context.get("contractMoneyStart");
    Double contractMoneyEnd = (Double)context.get("contractMoneyEnd");
    Timestamp contractStartDate = (Timestamp)context.get("signContractStartDate");
    Timestamp contractEndDate = (Timestamp)context.get("signContractEndDate");

    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(contractStartDate)&&UtilValidate.isNotEmpty(contractEndDate)){
        timeCondition.add(contractStartDate);
        timeCondition.add(contractEndDate);
    }
    List<EntityCondition> condition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(contractName)){
        condition.add(EntityCondition.makeCondition("contractName",EntityOperator.LIKE,"%"+contractName+"%"));
    }
    if (UtilValidate.isNotEmpty(department)){
        condition.add(EntityCondition.makeCondition("department",EntityOperator.EQUALS,department));
    }
    if (UtilValidate.isNotEmpty(contractType)){
        condition.add(EntityCondition.makeCondition("contractType",EntityOperator.EQUALS,contractType));
    }
    if (UtilValidate.isNotEmpty(contractNumber)){
        condition.add(EntityCondition.makeCondition("contractNumber",EntityOperator.LIKE,"%"+contractNumber+"%"));
    }
    if (UtilValidate.isNotEmpty(customerName)){
        condition.add(EntityCondition.makeCondition("customerName",EntityOperator.LIKE,"%"+customerName+"%"));
    }
    if (UtilValidate.isNotEmpty(balanceOfPaymentsType)){
        condition.add(EntityCondition.makeCondition("balanceOfPaymentsType",EntityOperator.EQUALS,balanceOfPaymentsType));
    }
    if (UtilValidate.isNotEmpty(contractMoneyStart)&&UtilValidate.isNotEmpty(contractMoneyEnd)) {
        if (contractMoneyStart <= contractMoneyEnd) {
            condition.add(EntityCondition.makeCondition([EntityCondition.makeCondition("totalMoney", EntityOperator.GREATER_THAN_EQUAL_TO, contractMoneyStart),
                                                     EntityCondition.makeCondition("totalMoney", EntityOperator.LESS_THAN_EQUAL_TO, contractMoneyEnd)],EntityOperator.AND));
         }else {
            condition.add(EntityCondition.makeCondition([EntityCondition.makeCondition("totalMoney", EntityOperator.GREATER_THAN_EQUAL_TO, contractMoneyEnd),
                                                         EntityCondition.makeCondition("totalMoney", EntityOperator.LESS_THAN_EQUAL_TO,contractMoneyStart)],EntityOperator.AND));
        }
    }
    if (UtilValidate.isNotEmpty(timeCondition)){
        condition.add(EntityCondition.makeCondition("signDate",EntityOperator.BETWEEN,timeCondition));
    }

    EntityListIterator unitContractList = EntityQuery.use(delegator)
            .select()
            .from("UnitContractInfo")
            .where(condition)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != unitContractList && unitContractList.getResultsSizeAfterPartialList() > 0){
        totalCount = unitContractList.getResultsSizeAfterPartialList();
        pageList = unitContractList.getPartialList(lowIndex, viewSize);
    }
    unitContractList.close();
    List<Map<String,Object>> valueList = new ArrayList<Map<String,Object>>();
    for (Map map : pageList){
        Map<String,Object> valueMap = new HashMap<String,Object>()
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(new Timestamp(System.currentTimeMillis()));
        Timestamp time = Timestamp.valueOf(str+" 00:00:00");
        Timestamp startDate = Timestamp.valueOf(format.format(map.get("contractStartDate"))+" 00:00:00");
        Timestamp endDate = Timestamp.valueOf(format.format(map.get("contractEndDate"))+" 00:00:00");
        if (time.getTime()<startDate.getTime()){
            valueMap.put("contractState","0");//未生效
        } else if (time.getTime()>endDate.getTime()){
            valueMap.put("contractState","1");//已过期
        } else {
            valueMap.put("contractState","2");//已生效
        }
        map.put("contractStartDate",format.format(map.get("contractStartDate")));
        map.put("contractEndDate",format.format(map.get("contractEndDate")));
        valueMap.putAll(map);
        valueList.add(valueMap);
    }
    List<GenericValue> paymentList = EntityQuery.use(delegator)
            .select()
            .from("Enumeration")
            .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PAYMENT_TYPE"))
            .queryList();
    List<GenericValue> contractTypeList = EntityQuery.use(delegator)
            .select()
            .from("Enumeration")
            .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"UNIT_CONTRACT_TYPE"))
            .queryList();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",valueList);
    map.put("paymentList",paymentList);
    map.put("contractTypeList",contractTypeList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("contractType",contractType);
    map.put("contractNumber",contractNumber);
    map.put("contractName",contractName);
    map.put("department1",department);
    map.put("customerName",customerName);
    map.put("balanceOfPaymentsType",balanceOfPaymentsType);
    map.put("contractMoneyStart",contractMoneyStart);
    map.put("contractMoneyEnd",contractMoneyEnd);
    map.put("signContractStartDate",contractStartDate);
    map.put("signContractEndDate",contractEndDate);
    successResult.put("data",map);
    return successResult;
}
public Map<String,Object> editUnitContract(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String unitContractId = context.get("unitContractId");
    if (UtilValidate.isNotEmpty(unitContractId)){
        GenericValue unitContract = EntityQuery.use(delegator)
                .select()
                .from("TblUnitContract")
                .where(EntityCondition.makeCondition("unitContractId",EntityOperator.EQUALS,unitContractId))
                .queryOne();

        List<GenericValue> paymentList = EntityQuery.use(delegator)
                .select()
                .from("Enumeration")
                .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PAYMENT_TYPE"))
                .queryList();
        List<GenericValue> contractTypeList = EntityQuery.use(delegator)
                .select()
                .from("Enumeration")
                .where(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"UNIT_CONTRACT_TYPE"))
                .queryList();
        if (UtilValidate.isNotEmpty(unitContract)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            unitContract.put("contractStartDate",format.format(unitContract.get("contractStartDate")));
            unitContract.put("contractEndDate",format.format(unitContract.get("contractEndDate")));
            unitContract.put("signDate",format.format(unitContract.get("signDate")));
            Map valueMap = new HashMap();
            valueMap.put("contractNumber",unitContract.get("contractNumber"));
            valueMap.put("paymentList",paymentList);
            valueMap.put("contractTypeList",contractTypeList);
            valueMap.put("unitContract",unitContract);

            //获取附件
            List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", unitContractId, "entityName", "TblUnitContract"))).queryList();
            String fileIds = "";
            if(null != genericValueList && genericValueList.size() > 0){
                for(GenericValue genericValue : genericValueList){
                    fileIds = fileIds + genericValue.get("accessoryId") + ",";
                }
            }
            if(!"".equals(fileIds)){
                fileIds = fileIds.substring(0,fileIds.length() - 1);
                context.put("files",fileIds);
                Map data = runService("searchFileByIds", dispatcher.getDispatchContext().getModelService("searchFileByIds").makeValid(context, ModelService.IN_PARAM));
                Map filemap = (Map)data.get("data");
                valueMap.put("fileIds",filemap.get("fileIds"));
                valueMap.put("fileList",filemap.get("fileList"));
            }

            successResult.put("data",valueMap);
        }
    }
    return successResult;
}

public Map<String,Object> deleteUnitContract(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String unitContractId = context.get("unitContractId");
    String msg = "";
    if(UtilValidate.isNotEmpty(unitContractId)){
        GenericValue unitContract = delegator.makeValidValue("TblUnitContract","unitContractId",unitContractId);
        unitContract.remove();
        /** 删除附件**/
        List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId",unitContractId, "entityName", "TblUnitContract"))).queryList();
        delegator.removeByAnd("TblFileScope",UtilMisc.toMap("dataId",unitContractId, "entityName", "TblUnitContract"));
        if(UtilValidate.isNotEmpty(genericValueList)){
            for(GenericValue genericValue : genericValueList){
                GenericValue data = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",genericValue.get("accessoryId"))).queryOne();
                if("ATTACHMENT_FILE".equals(data.get("dataResourceTypeId").toString())) {
                    File file = new File(data.get("objectInfo").toString() + data.get("dataResourceName").toString());
                    if (file.exists()) {
                        file.delete();
                    }
                    delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", data.get("dataResourceId")));
                }
            }
        }
        msg = "删除成功！";
    }
    Map map = new HashMap()
    map.put("msg",msg);
    successResult.put("data",map);
    return successResult;
}

/**
 * 保存附件
 * @param workId
 * @param oldFileList
 * @param newFileList
 */
public void saveWorkFile(String workId,List<String> oldFileList,List<String> newFileList){
    for(String fileId:newFileList) {
        if (!oldFileList.contains(fileId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            String fileScopeId = delegator.getNextSeqId("TblFileScope");
            map.put("fileScopeId", fileScopeId);
            map.put("entityName", "TblUnitContract");
            map.put("dataId", workId);
            map.put("accessoryId", fileId);
            GenericValue accessory = delegator.makeValidValue("TblFileScope", map);
            accessory.create();
        }
    }
    for(String fileid:oldFileList){
        if(!newFileList.contains(fileid)) {
            delegator.removeByAnd("TblFileScope", UtilMisc.toMap("entityName", "TblUnitContract", "dataId", workId, "accessoryId", fileid));
            GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileid)).queryOne();
            if (null != fileData && "ATTACHMENT_FILE".equals(fileData.get("dataResourceTypeId").toString())) {
                File file = new File(fileData.get("objectInfo").toString() + fileData.get("dataResourceName").toString());
                if (file.exists()) {
                    file.delete();
                }
                delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileid));
            }
        }
    }
}