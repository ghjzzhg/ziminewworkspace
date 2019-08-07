/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.oa;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.event.EventUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkPlanEvents {
    public static final String module = WorkPlanEvents.class.getName();
    public static String addWorkPlan(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        boolean beginTrans = true;
        String workPlanId = request.getParameter("workPlanId");
        boolean isUpdate = false;
        if(UtilValidate.isNotEmpty(workPlanId)){
            isUpdate = true;
        }
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");
            String departmentId = request.getParameter("departmentId");
            String title = request.getParameter("title");

            java.sql.Date startTime = UtilDateTime.toSqlDate(request.getParameter("startTime"), UtilDateTime.DATE_FORMAT);
            java.sql.Date completeTime = UtilDateTime.toSqlDate(request.getParameter("completeTime"), UtilDateTime.DATE_FORMAT);

            String noticePerson = request.getParameter("noticePerson");
            List<String> noticePersonList = FastList.newInstance();
            String[] noticePersonStr = noticePerson !=null&& noticePerson.length() > 0 ?noticePerson.split(",") : new String[0];
            for(String str : noticePersonStr){
                noticePersonList.add(str);
            }
            String planDescription = request.getParameter("planDescription");
            String planTypeInput = request.getParameter("planType_input");
            String planType = request.getParameter("planType");
            if(UtilValidate.isEmpty(planType)){
                //TODO 保存类型
                Map<String,Object> planTypeGen = dispatcher.runSync("createPlanType", UtilMisc.toMap("enumTypeId", "WORK_PLAN_Type", "description", planDescription, "userLogin", userLogin));
                planType = (String)planTypeGen.get("enumId");
            }
            String isPerformance = request.getParameter("isPerformance");
            String importanceDegree = request.getParameter("importanceDegree");
            String difficultyDegree = request.getParameter("difficultyDegree");
            String planPerson = request.getParameter("planPerson");
            String projectLeader = request.getParameter("projectLeader");
            String executor = request.getParameter("executor");
            String[] canSeePerson = request.getParameterMap().get("canSeePerson");
            List<String> canSeePersonList = null;
            if(UtilValidate.isNotEmpty(canSeePerson)){
                canSeePersonList = UtilMisc.toListArray(canSeePerson);
            }
            String lastFeedback = request.getParameter("lastFeedback");

            if(beginTrans){
                beginTrans = TransactionUtil.begin();
                Map<String,Object> valueMap = dispatcher.runSync("saveWorkPlanCreate", UtilMisc.toMap("workPlanId", workPlanId, "departmentId", departmentId, "title", title, "startTime", startTime, "completeTime", completeTime,
                        "noticePerson", noticePersonList, "planDescription", planDescription, "planType", planType, "isPerformance", isPerformance, "importanceDegree", importanceDegree,"difficultyDegree",difficultyDegree,"planPerson", planPerson, "projectLeader", projectLeader,
                        "canSeePerson", canSeePersonList,"lastFeedback", lastFeedback,"executor",executor,"userLogin", userLogin));
                workPlanId = (String) valueMap.get("workPlanId");
                /*里程碑*/
                int rowCount = UtilHttp.getMultiFormRowCount(request);
                if(UtilValidate.isNotEmpty(rowCount)){
                    int i=1;
                    for(; i < rowCount; i ++){
                        String milestoneId = request.getParameter("milestoneId" + UtilHttp.MULTI_ROW_DELIMITER + i);
                        String milestoneTime = request.getParameter("milestoneTime" + UtilHttp.MULTI_ROW_DELIMITER + i);
                        String milestoneDescription = request.getParameter("milestoneDescription" + UtilHttp.MULTI_ROW_DELIMITER + i);
                        if (UtilValidate.isEmpty(milestoneTime) && UtilValidate.isEmpty(milestoneDescription)) continue;
                        GenericValue milestone = null;
                        if(UtilValidate.isNotEmpty(milestoneId)){
                            milestone = delegator.makeValidValue("TblMilestone", UtilMisc.toMap("milestoneId",milestoneId,
                                    "milestoneTime",UtilDateTime.toSqlDate(milestoneTime,UtilDateTime.DATE_FORMAT),"milestoneDescription",milestoneDescription,"workPlanId",workPlanId
                            ));
                            milestone.store();
                        }else {
                            milestoneId = delegator.getNextSeqId("TblMilestone");
                            milestone = delegator.makeValidValue("TblMilestone", UtilMisc.toMap("milestoneId",milestoneId,
                                    "milestoneTime",UtilDateTime.toSqlDate(milestoneTime,UtilDateTime.DATE_FORMAT),"milestoneDescription",milestoneDescription,"workPlanId",workPlanId
                            ));
                            milestone.create();
                        }
                    }
                }

            /*任务安排*/
                Enumeration<String> parametersMapName = request.getParameterNames();
                int jobCount = 0;
                while (parametersMapName.hasMoreElements()){
                    String paramStr = parametersMapName.nextElement();
                    String[] paramStrArray  = paramStr.split("_o1_");
                    if(paramStrArray.length > 1) {
                        try {
                            int index = Integer.parseInt(paramStrArray[1]);
                            jobCount = index > jobCount ? index : jobCount;
                        }catch (NumberFormatException e){
                            continue;
                        }
                    }
                }
                List<GenericValue> createPersonWorkList = FastList.newInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String oldExecutorList = request.getParameter("oldExecutorList");
                for(int i = 1;i < jobCount + 1; i++){
                    String jobDescription = request.getParameter("description_o1_" + i);
                    /*java.sql.Timestamp jobStartTime = new Timestamp(format.parse(request.getParameter("startTime_o1_" + i)).getTime());
                    java.sql.Timestamp jobEndTime = new Timestamp(format.parse(request.getParameter("endTime_o1_" + i)).getTime());*/
                    java.sql.Date jobStartTime = UtilDateTime.toSqlDate(request.getParameter("startTime_o1_" + i),UtilDateTime.DATE_FORMAT);
                    java.sql.Date jobEndTime = UtilDateTime.toSqlDate(request.getParameter("endTime_o1_" + i),UtilDateTime.DATE_FORMAT);
                    String jobOfExecutor = request.getParameter("jobOfExecutor_o1_" + i);
                    GenericValue personWork = null;
                    if(!isUpdate){
                        String personWorkId = delegator.getNextSeqId("TblPersonWork");
                        personWork = delegator.makeValidValue("TblPersonWork",UtilMisc.toMap(
                                "personWorkId",personWorkId,"workPlanId",workPlanId,"personId",jobOfExecutor,"startTime",jobStartTime,"completeTime",jobEndTime,"isInvalid","N","personWorkStatus",0.0,"jobDescription",jobDescription));
                    }else {
                        List<GenericValue> personWorkList = delegator.findByAnd("TblPersonWork", UtilMisc.toMap("workPlanId", workPlanId, "personId", jobOfExecutor), null, false);
                        if (personWorkList.size() > 0){
                            personWork = personWorkList.get(0);
                            personWork.put("startTime",jobStartTime);
                            personWork.put("completeTime",jobEndTime);
                            personWork.put("jobDescription",jobDescription);
                        }else {
                            String personWorkId = delegator.getNextSeqId("TblPersonWork");
                            personWork = delegator.makeValidValue("TblPersonWork",UtilMisc.toMap(
                                    "personWorkId",personWorkId,"workPlanId",workPlanId,"personId",jobOfExecutor,"startTime",jobStartTime,"completeTime",jobEndTime,"isInvalid","N","personWorkStatus",0.0,"jobDescription",jobDescription));
                        }
                    }
                    createPersonWorkList.add(personWork);
                }

                if(createPersonWorkList.size() > 0){
                    String[] executorArray = oldExecutorList == null ? new String[0] : oldExecutorList.split(",");
                    for(String str : executorArray){
                        boolean isExist = false;
                        for(GenericValue value : createPersonWorkList){
                            if(str.equals(value.getString("personId"))){
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            delegator.removeByAnd("TblPersonWork",UtilMisc.toMap("workPlanId", workPlanId, "personId",str));
                        }
                    }
                    delegator.storeAll(createPersonWorkList);
                }
                TransactionUtil.commit(beginTrans);
            }
        }catch (Exception e) {
            Debug.logError(e, module);
            try {
                TransactionUtil.rollback();
            } catch (GenericTransactionException e1) {
                e1.printStackTrace();
            }
            return EventUtil.returnError(request, "出现错误");
        }
        return EventUtil.returnSuccess(request, isUpdate ? "修改成功" : "保存成功");
    }

    public static String saveReceive(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        boolean beginTrans = true;
        String receiveId = "";
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String warehouseId = request.getParameter("warehouseId");
            String outInventoryType = request.getParameter("outInventoryType");
            String receivePersonId = request.getParameter("receivePersonId");
            java.sql.Date data = UtilDateTime.toSqlDate(request.getParameter("receiveDate"), UtilDateTime.DATE_FORMAT);
            Timestamp receiveDate = Timestamp.valueOf(sdf.format(data));
            String receiveDepartmentId = request.getParameter("receiveDepartmentId");
            String makeInfoPersonId = request.getParameter("makeInfoPersonId");
            String isCheck = request.getParameter("isCheck");
            String checkJob = request.getParameter("checkJob");
            String receiveNote = request.getParameter("receiveNote");
            String checkResult = "RECEIVE_RESULT_FOUR";
            if (isCheck.equals("N")){
                checkResult = "RECEIVE_RESULT_THREE";
            }

            if(beginTrans){
                receiveId = delegator.getNextSeqId("TblReceive");
                Map mapValue = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblReceive","prefix","receive","numName","receiveCode","userLogin",userLogin));
                String receiveCode = mapValue.get("number").toString();
                GenericValue receive = delegator.makeValidValue("TblReceive", UtilMisc.toMap("receiveId",receiveId,"warehouseId", warehouseId, "outInventoryType", outInventoryType, "receiveDate", receiveDate, "receiveDepartmentId", receiveDepartmentId,
                        "makeInfoPersonId", makeInfoPersonId,"receiveCode",receiveCode, "isCheck", isCheck,"checkResult",checkResult, "checkJob", checkJob, "receivePersonId", receivePersonId, "receiveNote", receiveNote));
                receive.create();
                try {
                    if(UtilValidate.isNotEmpty(request.getParameterMap().get("osManagementId"))) {
                        int rowCount = request.getParameterMap().get("osManagementId").length;
                        List<String> osManagement = Arrays.asList(request.getParameterMap().get("osManagementId"));
                        List<String> numbers = Arrays.asList(request.getParameterMap().get("number"));
                        int i = 0;
                        for(; i < rowCount; i ++){
                            String osManagementId = osManagement.get(i);
                            int number = Integer.valueOf(numbers.get(i));
                            String addProductId = delegator.getNextSeqId("TblAddProduct");
                            GenericValue addProduct = delegator.makeValidValue("TblAddProduct", UtilMisc.toMap("addProductId",addProductId,"receiveId",receiveId,
                                    "osManagementId",osManagementId,"number",number,"receivedAmount",0));
                            addProduct.create();
                        }
                    }
                } catch (Exception e) {
                    receive.remove();
                }

            }
        }catch (Exception e) {
            return EventUtil.returnError(request, "出现错误");
        }
        return EventUtil.returnSuccess(request, "保存成功");
    }

    public static String saveWarehouseInfo(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        try {
            Timestamp inputTime = new Timestamp((new Date()).getTime());
            String warehouseId = request.getParameter("warehouseId");
            String person = userLogin.get("partyId").toString();
            String warehouseCode = request.getParameter("warehouseCode");
            String warehouseName = request.getParameter("warehouseName");
            String warehouseAddress = request.getParameter("warehouseAddress");
            String phone = request.getParameter("phone");
            String linkman = request.getParameter("linkman");
            GenericValue location = null;
            if (UtilValidate.isNotEmpty(warehouseId)) {
                location = delegator.makeValidValue("TblWarehouse", UtilMisc.toMap("warehouseId",warehouseId,"inputPerson", person,"lastEditPerson",person, "warehouseCode", warehouseCode, "warehouseName", warehouseName,
                        "warehouseAddress", warehouseAddress,"phone",phone, "linkman", linkman,"inputTime",inputTime,"lastEditTime",inputTime,"deletedType","N"));
                location.store();
            } else {
                warehouseId = delegator.getNextSeqId("TblWarehouse");
                location = delegator.makeValidValue("TblWarehouse", UtilMisc.toMap("warehouseId",warehouseId,"inputPerson", person,"lastEditPerson",person, "warehouseCode", warehouseCode, "warehouseName", warehouseName,
                        "warehouseAddress", warehouseAddress,"phone",phone, "linkman", linkman,"inputTime",inputTime,"lastEditTime",inputTime,"deletedType","N"));
                location.create();
            }
            try {
                if(UtilValidate.isNotEmpty(request.getParameterMap().get("locationName"))) {
                    int rowCount = request.getParameterMap().get("locationName").length;
                    List<String> locationNames = Arrays.asList(request.getParameterMap().get("locationName"));
                    List<String> locationIds = Arrays.asList(request.getParameterMap().get("locationId"));
                    int i = 0;
                    for(; i < rowCount; i ++){
                        String locationId = "";
                        if (UtilValidate.isNotEmpty(locationIds.get(i))){
                            locationId = locationIds.get(i);
                            String locationName = locationNames.get(i).toString();
                            GenericValue addProduct = delegator.makeValidValue("TblLocation", UtilMisc.toMap("locationId",locationId,"locationName",locationName,"warehouseId",warehouseId));
                            addProduct.store();
                        } else {
                            locationId = delegator.getNextSeqId("TblLocation");
                            String locationName = locationNames.get(i).toString();
                            GenericValue addProduct = delegator.makeValidValue("TblLocation", UtilMisc.toMap("locationId",locationId,"locationName",locationName,"warehouseId",warehouseId));
                            addProduct.create();
                        }
                    }
                }
            } catch (Exception e) {
                location.remove();
            }
        }catch (Exception e) {
            return EventUtil.returnError(request, "出现错误");
        }
        return EventUtil.returnSuccess(request, "保存成功");
    }

    public static String saveDelivery(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String receiveId = "";
        try {
            if(UtilValidate.isNotEmpty(request.getParameterMap().get("addProductId"))) {
                int rowCount = request.getParameterMap().get("addProductId").length;
                List<String> addProductIds = Arrays.asList(request.getParameterMap().get("addProductId"));
                List<String> deliveryAmounts = Arrays.asList(request.getParameterMap().get("deliveryAmount"));
                List<String> receivedAmounts = Arrays.asList(request.getParameterMap().get("receivedAmount"));
                List<String> notReceiveAmounts = Arrays.asList(request.getParameterMap().get("notReceiveAmount"));
                List<String> osManagementIds = Arrays.asList(request.getParameterMap().get("osManagementId"));
                int i = 0;
                for(; i < rowCount; i ++){
                    String addProductId = addProductIds.get(i);
                    String osManagementId = osManagementIds.get(i);
                    int deliveryAmount = Integer.valueOf(deliveryAmounts.get(i));
                    int receivedAmount = Integer.valueOf(receivedAmounts.get(i));
                    int notReceiveAmount = Integer.valueOf(notReceiveAmounts.get(i));
                    GenericValue addProduct = delegator.makeValidValue("TblAddProduct", UtilMisc.toMap("addProductId",addProductId,"receivedAmount",receivedAmount+deliveryAmount));
                    addProduct.store();

                    GenericValue product = delegator.makeValidValue("TblProduct",UtilMisc.toMap("osManagementId",osManagementId,"notReceiveAmount",notReceiveAmount-deliveryAmount));
                    product.store();
                }
            }
        }catch (Exception e) {
            return EventUtil.returnError(request, "出现错误");
        }
        return EventUtil.returnSuccess(request, "保存成功");
    }
}



