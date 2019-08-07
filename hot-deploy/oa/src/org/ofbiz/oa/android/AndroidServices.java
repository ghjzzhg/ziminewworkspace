/**
 * ****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * *****************************************************************************
 */
package org.ofbiz.oa.android;

import javolution.util.FastMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AndroidServices {
    public static final String module = AndroidServices.class.getName();
    public static String androidLogin(HttpServletRequest request, HttpServletResponse response) {
       String message = LoginWorker.login(request, response);
        Map messageMap = new HashMap();
        if (message.equals("success")){
            messageMap.put("login_passed","true");
        }else if(message.equals("error")) {
            String msg = request.getAttribute("_ERROR_MESSAGE_").toString();
            if (msg.contains("没有找到用户")){
                messageMap.put("errorMessage","没有找到用户");
            }else if(msg.contains("密码不正确")){
                messageMap.put("errorMessage","密码不正确");
            }else if (msg.contains("账户已被禁用")){
                messageMap.put("errorMessage","账户已被禁用");
            }
            messageMap.put("login_passed","false");
        }
        //返回json
        String returnString = jsonFor(messageMap,request,response);
        return returnString;
    }
    public static String getWorkLogList(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        List workLogList = new ArrayList();
        List<GenericValue> logSetList = new ArrayList<GenericValue>();
        Map logSetMap = new HashMap();
        Map logMap = new HashMap();
        try {
            Map loginMap = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(loginMap)){
                String partyId = (String) loginMap.get("partyId");
                workLogList = delegator.findByAnd("TblWorkLog",UtilMisc.toMap("partyId",partyId));
                logSetList = delegator.findAll("TblLogSet",false);
                for(Map map:logSetList){
                    logSetMap.put("logValue",map.get("logValue"));
                    logSetMap.put("planValue",map.get("planValue"));
                    logSetMap.put("instructionsValue", map.get("instructionsValue"));
                }
                logMap.put("workLog",workLogList);
                logMap.put("logSet",logSetMap);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        //返回json
       String returnString = jsonFor(logMap,request,response);
        return returnString;
    }
    public static String saveWorkLog(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        Map returnMap = new HashMap();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String logTitle = request.getParameter("logTitle");
        String logContent = request.getParameter("logContent");
        String workDateString = request.getParameter("workDate");
        Date workDateFor = new SimpleDateFormat("yyyy-MM-dd").parse(workDateString);
        java.sql.Date workDate = new java.sql.Date(workDateFor.getTime());

        try {
            String partyId = "";
            Map loginMap = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(loginMap)){
                 partyId = (String) loginMap.get("partyId");
            }
            List<GenericValue> workLogList = delegator.findByAnd("TblWorkLog",UtilMisc.toMap("partyId",partyId,"workDate",workDate));

            if (UtilValidate.isEmpty(workLogList)){
                String workLogId = delegator.getNextSeqId("TblWorkLog").toString();
                GenericValue genericValue = delegator.makeValue("TblWorkLog",UtilMisc.toMap("workLogId", workLogId));
                genericValue.setString("logTitle",logTitle);
                genericValue.setString("logContent",logContent);
                genericValue.setString("partyId",partyId);
                genericValue.set("workDate",workDate);
                genericValue.create();
                returnMap.put("success","true");
            }else {
                String workLogId = "";
                for(GenericValue map : workLogList){
                    workLogId =  map.get("workLogId").toString();
                }
                GenericValue workLogMap = delegator.findByPrimaryKey("TblWorkLog",UtilMisc.toMap("workLogId",workLogId));
                workLogMap.setString("logTitle",logTitle);
                workLogMap.setString("logContent",logContent);
                workLogMap.setString("partyId",partyId);
                workLogMap.set("workDate",workDate);
                workLogMap.store();
                returnMap.put("success","true");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            returnMap.put("success","false");
        }
        String returnString = jsonFor(returnMap,request,response);
        return returnString;
    }
    public static String saveWorkPlan(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        Map returnMap = new HashMap();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String planTitle = request.getParameter("planTitle");
        String planContent = request.getParameter("planContent");
        String workDateString = request.getParameter("workDate");
        Date workDateFor = new SimpleDateFormat("yyyy-MM-dd").parse(workDateString);
        java.sql.Date workDate = new java.sql.Date(workDateFor.getTime());
        try {
            String partyId = "";
            Map loginMap = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(loginMap)){
                partyId = (String) loginMap.get("partyId");
            }
            List<GenericValue> workLogList = delegator.findByAnd("TblWorkLog", UtilMisc.toMap("partyId", partyId, "workDate", workDate));

            if (UtilValidate.isEmpty(workLogList)){
                String workLogId = delegator.getNextSeqId("TblWorkLog").toString();
                GenericValue genericValue = delegator.makeValue("TblWorkLog",UtilMisc.toMap("workLogId", workLogId));
                genericValue.setString("planTitle",planTitle);
                genericValue.setString("planContent",planContent);
                genericValue.setString("partyId",partyId);
                genericValue.set("workDate",workDate);
                genericValue.create();
                returnMap.put("success","true");
            }else {
                String workLogId = "";
                for(GenericValue map : workLogList){
                    workLogId =  map.get("workLogId").toString();
                }
                GenericValue workLogMap = delegator.findByPrimaryKey("TblWorkLog",UtilMisc.toMap("workLogId",workLogId));
                workLogMap.setString("planTitle",planTitle);
                workLogMap.setString("planContent",planContent);
                workLogMap.setString("partyId",partyId);
                workLogMap.set("workDate",workDate);
                workLogMap.store();
                returnMap.put("success","true");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            returnMap.put("success", "false");
        }
        String returnString = jsonFor(returnMap,request,response);
        return returnString;
    }

    /**
     * 获取车辆预约列表
     * */
    public static String getVehicleOrderListForReview(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String result = "";
        try {
            String partId = "";
            Map map1 = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(map1)){
                partId = (String) map1.get("partyId");
            }
            EntityConditionList dateCondition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("reviewPerson", EntityOperator.EQUALS, partId),
                    EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partId)
            ), EntityOperator.OR);

            EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc.toList(
                            dateCondition,
                            EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "未审核")),
                    EntityOperator.AND);


            List vehicleOrderList = EntityQuery.use(delegator).select("vehicleName", "startDate", "endDate", "groupName", "arrangePerson", "reviewPerson", "reviewPersonName", "arrangePersonName", "reviewState", "orderId")
                    .from("VehicleOrderDetail")
                    .where(conditions)
                    .orderBy("-startDate")
                    .distinct()
                    .queryList();

            List list = new ArrayList();
            for (Object object : vehicleOrderList){
                Map<String,Object> map = new HashMap<String, Object>();
                map.putAll((Map<? extends String, ?>) object);
                map.put("endDate",((Map<? extends String, ?>) object).get("endDate").toString());
                map.put("startDate",((Map<? extends String, ?>) object).get("startDate").toString());
                list.add(map);
            }
             result = jsonFor(list,request,response);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取车辆预约列表详情
     * */
    public static String getVehicleOrderInfoForReview(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String vehicleOrderId = (String)request.getParameter("vehicleOrderId");
        String result = "";
        try {
            List vehicleOrderList = EntityQuery.use(delegator).select("vehicleName", "startDate", "endDate", "groupName", "arrangePerson", "reviewPerson", "reviewPersonName", "arrangePersonName", "reviewState", "orderId")
                    .from("VehicleOrderDetail")
                    .where(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, vehicleOrderId))
                    .orderBy("-startDate")
                    .distinct()
                    .queryList();

            List list = new ArrayList();
            for (Object object : vehicleOrderList){
                Map<String,Object> map = new HashMap<String, Object>();
                map.putAll((Map<? extends String, ?>) object);
                map.put("endDate",((Map<? extends String, ?>) object).get("endDate").toString());
                map.put("startDate",((Map<? extends String, ?>) object).get("startDate").toString());
                list.add(map);
            }
            result = jsonFor(list,request,response);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取资源预约列表
     * */
    public static String getResourceOrderListForReview(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator) request.getAttribute("delegator");
        String result = "";
        try {
            String partId = "";
            Map map1 = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(map1)){
                partId = (String) map1.get("partyId");
            }
            EntityConditionList dateCondition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("auditPersonType", EntityOperator.EQUALS, partId),
                    EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partId)
            ), EntityOperator.OR);
            EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc.toList(
                            dateCondition,
                            EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "未审核")),
                    EntityOperator.AND);
            List vehicleOrderList = EntityQuery.use(delegator).select("source", "resourceId", "startDate", "endDate", "groupName", "arrangePersonType", "auditPersonType", "arrangePerson", "fullName", "reviewState", "orderId")
                    .from("ResourceOrderDetail")
                    .where(conditions)
                    .distinct()
                    .queryList();
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            GenericValue genericValue = null;
            for (Object object : vehicleOrderList){
                Map<String,Object> map = new HashMap<String, Object>();
                map.putAll((Map<? extends String, ?>) object);
                if (((GenericValue) object).get("source").equals("3")){//3表示资源存放在资源表内
                    genericValue = EntityQuery.use(delegator).select("resourceName")
                            .from("TblResourceManagement")
                            .where(EntityCondition.makeCondition("resourceId", EntityOperator.EQUALS, ((GenericValue) object).get("resourceId")))
                            .distinct()
                            .queryOne();
                    if(genericValue != null) {
                        map.put("vehicleName", genericValue.get("resourceName"));
                    }
                }
                if (((GenericValue) object).get("source").equals("1")){//1表示资源存放在固有资产表内
                    genericValue = EntityQuery.use(delegator).select("fixedAssetsName")
                            .from("TblFixedAssets")
                            .where(EntityCondition.makeCondition("fixedAssetsId", EntityOperator.EQUALS, ((GenericValue) object).get("resourceId")))
                            .distinct()
                            .queryOne();
                    if(genericValue != null) {
                        map.put("vehicleName", genericValue.get("fixedAssetsName"));
                    }
                }
                map.put("endDate",((Map<? extends String, ?>) object).get("endDate").toString());
                map.put("startDate",((Map<? extends String, ?>) object).get("startDate").toString());
                map.put("arrangePersonName",((Map<? extends String, ?>) object).get("arrangePerson").toString());
                map.put("reviewPersonName",((Map<? extends String, ?>) object).get("fullName").toString());
                map.put("arrangePerson",((Map<? extends String, ?>) object).get("arrangePersonType").toString());
                map.put("reviewPerson",((Map<? extends String, ?>) object).get("auditPersonType").toString());
                list.add(map);
            }
            result = jsonFor(list, request, response);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获取资源预约列表详情
     * */
    public static String getResourceOrderInfoForReview(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator) request.getAttribute("delegator");
        String result = "";
        try {
            String resourceId = request.getParameter("resourceId");
            List vehicleOrderList = EntityQuery.use(delegator).select("source", "resourceId", "startDate", "endDate", "groupName", "arrangePersonType", "auditPersonType", "arrangePerson", "fullName", "reviewState", "orderId")
                    .from("ResourceOrderDetail")
                    .where(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, resourceId))
                    .distinct()
                    .queryList();
            List list = new ArrayList();
            GenericValue genericValue = null;
            Map<String,Object> map = new HashMap<String, Object>();
            for (Object object : vehicleOrderList){
                map.putAll((Map<? extends String, ?>) object);
                if (((GenericValue) object).get("source").equals("3")){//3表示资源存放在资源表内
                    genericValue = EntityQuery.use(delegator).select("resourceName")
                            .from("TblResourceManagement")
                            .where(EntityCondition.makeCondition("resourceId", EntityOperator.EQUALS, ((GenericValue) object).get("resourceId")))
                            .distinct()
                            .queryOne();
                    if(genericValue != null) {
                        map.put("vehicleName", genericValue.get("resourceName"));
                    }
                }
                if (((GenericValue) object).get("source").equals("1")){//3表示资源存放在固有资产表内
                    genericValue = EntityQuery.use(delegator).select("fixedAssetsName")
                            .from("TblFixedAssets")
                            .where(EntityCondition.makeCondition("fixedAssetsId", EntityOperator.EQUALS, ((GenericValue) object).get("resourceId")))
                            .distinct()
                            .queryOne();
                    if(genericValue != null) {
                        map.put("vehicleName", genericValue.get("fixedAssetsName"));
                    }
                }
                map.put("endDate",((Map<? extends String, ?>) object).get("endDate").toString());
                map.put("startDate",((Map<? extends String, ?>) object).get("startDate").toString());
                map.put("arrangePersonName",((Map<? extends String, ?>) object).get("arrangePerson").toString());
                map.put("reviewPersonName",((Map<? extends String, ?>) object).get("fullName").toString());
                map.put("arrangePerson",((Map<? extends String, ?>) object).get("arrangePersonType").toString());
                map.put("reviewPerson",((Map<? extends String, ?>) object).get("auditPersonType").toString());
                list.add(map);
            }
            result = jsonFor(list, request, response);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获取车辆管理安排列表
     * */
    public static String getVehicleOrderListForArrange(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String result = "";
        try {
            String partId = "";
            Map map1 = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(map1)){
                partId = (String) map1.get("partyId");
            }
            EntityConditionList dateCondition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("arrangePerson", EntityOperator.EQUALS, partId),
                    EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partId)
            ), EntityOperator.OR);

            EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc.toList(
                            dateCondition,
                            EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "已审核")),
                    EntityOperator.AND);
            List vehicleOrderList = EntityQuery.use(delegator).select("vehicleName", "startDate", "endDate", "reviewPerson", "arrangePerson", "groupName", "reviewPersonName", "arrangePersonName", "reviewState", "orderId")
                    .from("VehicleOrderDetail")
                    .where(conditions)
                    .orderBy("-startDate")
                    .distinct()
                    .queryList();

            List list = new ArrayList();
            for (Object object : vehicleOrderList){
                Map<String,Object> map = new HashMap<String, Object>();
                map.putAll((Map<? extends String, ?>) object);
                map.put("endDate",((Map<? extends String, ?>) object).get("endDate").toString());
                map.put("startDate",((Map<? extends String, ?>) object).get("startDate").toString());
                list.add(map);
            }
            result = jsonFor(list,request,response);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  获取资源安排列表
     * */
    public static String getResourceOrderListForArrange(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String result = "";
        try {
            String partId = "";
            Map map1 = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
            if(!UtilValidate.isEmpty(map1)){
                partId = (String) map1.get("partyId");
            }
            EntityConditionList dateCondition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("arrangePersonType", EntityOperator.EQUALS, partId),
                    EntityCondition.makeCondition("orderPerson", EntityOperator.EQUALS, partId)
            ), EntityOperator.OR);

            EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc.toList(
                            dateCondition,
                            EntityCondition.makeCondition("reviewState", EntityOperator.EQUALS, "已审核")),
                    EntityOperator.AND);
            List vehicleOrderList = EntityQuery.use(delegator).select("source", "resourceId", "startDate", "endDate", "groupName", "arrangePersonType", "auditPersonType", "arrangePerson", "fullName", "reviewState", "orderId")
                    .from("ResourceOrderDetail")
                    .where(conditions)
                    .distinct()
                    .queryList();

            //List vehicleOrderList = delegator.findByAnd("ResourceOrderDetail","reviewState","已审核");
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            GenericValue genericValue = null;
            for (Object object : vehicleOrderList){
                Map<String,Object> map = new HashMap<String, Object>();
                map.putAll((Map<? extends String, ?>) object);
                if (((GenericValue) object).get("source").equals("3")){//3表示资源存放在资源表内
                    genericValue = EntityQuery.use(delegator).select("resourceName")
                            .from("TblResourceManagement")
                            .where(EntityCondition.makeCondition("resourceId", EntityOperator.EQUALS, ((GenericValue) object).get("resourceId")))
                            .distinct()
                            .queryOne();
                    if(genericValue != null) {
                        map.put("vehicleName", genericValue.get("resourceName"));
                    }
                }
                if (((GenericValue) object).get("source").equals("1")){//1表示资源存放在固有资产表内
                    genericValue = EntityQuery.use(delegator).select("fixedAssetsName")
                            .from("TblFixedAssets")
                            .where(EntityCondition.makeCondition("fixedAssetsId", EntityOperator.EQUALS, ((GenericValue) object).get("resourceId")))
                            .distinct()
                            .queryOne();
                    if(genericValue != null) {
                        map.put("vehicleName", genericValue.get("fixedAssetsName"));
                    }
                }
                map.put("endDate",((Map<? extends String, ?>) object).get("endDate").toString());
                map.put("startDate",((Map<? extends String, ?>) object).get("startDate").toString());
                map.put("arrangePersonName",((Map<? extends String, ?>) object).get("arrangePerson").toString());
                map.put("reviewPersonName",((Map<? extends String, ?>) object).get("fullName").toString());
                map.put("arrangePerson",((Map<? extends String, ?>) object).get("arrangePersonType").toString());
                map.put("reviewPerson",((Map<? extends String, ?>) object).get("auditPersonType").toString());
                list.add(map);
            }
            result = jsonFor(list,request,response);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 车辆审批结果保存
     * */
    public static String reviewVehicleOrder(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String reviewResult = request.getParameter("reviewResult");
        String reviewRemarks = request.getParameter("reviewRemarks");
        String orderId = request.getParameter("orderId");
        Map map = new HashMap();
        try {
            GenericValue orderMap = delegator.findByPrimaryKey("TblVehicleOrder", UtilMisc.toMap("orderId", orderId));
            if (reviewResult.equals("true")){
                orderMap.setString("reviewState","PERSON_TWO");//已审核
            }else {
                orderMap.setString("reviewState","PERSON_THREE");//已驳回
            }
            orderMap.setString("reviewRemarks",reviewRemarks);
            orderMap.store();
            map.put("success","true");
        } catch (GenericEntityException e) {
            e.printStackTrace();
            map.put("success","false");
        }
        String result = jsonFor(map,request,response);
        return result;
    }
    /**
     * 车辆安排结果 保存
     * */
    public static String arrangeVehicleOrder(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String reviewResult = request.getParameter("reviewResult");
        String arrangeRemarks = request.getParameter("arrangeRemarks");
        String orderId = request.getParameter("orderId");
        Map map = new HashMap();
        try {
            GenericValue orderMap = delegator.findByPrimaryKey("TblVehicleOrder", UtilMisc.toMap("orderId", orderId));
            if (reviewResult.equals("true")){
                orderMap.setString("reviewState","PERSON_FOUR");//已安排
            }
            orderMap.setString("arrangeRemarks",arrangeRemarks);
            orderMap.store();
            map.put("success","true");
        } catch (GenericEntityException e) {
            e.printStackTrace();
            map.put("success","false");
        }
        String result = jsonFor(map,request,response);
        return result;
    }

    /**
     * 资源审批
     * */
    public static String reviewResourceOrder(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String reviewResult = request.getParameter("reviewResult");
        String reviewRemarks = request.getParameter("reviewRemarks");
        String orderId = request.getParameter("orderId");
        Map map = new HashMap();
        try {
            GenericValue orderMap = delegator.findByPrimaryKey("TblResourceOrder", UtilMisc.toMap("orderId", orderId));
            if (reviewResult.equals("true")){
                orderMap.setString("reviewState","PERSON_TWO");//已审核
            }else {
                orderMap.setString("reviewState","PERSON_THREE");//已驳回
            }
            orderMap.setString("reviewRemarks",reviewRemarks);
            orderMap.store();
            map.put("success","true");
        } catch (GenericEntityException e) {
            e.printStackTrace();
            map.put("success","false");
        }
        String result = jsonFor(map,request,response);
        return result;
    }

    /**
     * 资源安排
     * */
    public static String arrangeResourceOrder(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String arrangeRemarks = request.getParameter("arrangeRemarks");
        String orderId = request.getParameter("orderId");
        Map map = new HashMap();
        try {
            GenericValue orderMap = delegator.findByPrimaryKey("TblResourceOrder", UtilMisc.toMap("orderId", orderId));
            orderMap.setString("reviewState","PERSON_FOUR");//已安排
            orderMap.setString("arrangeRemarks",arrangeRemarks);
            orderMap.store();
            map.put("success","true");
        } catch (GenericEntityException e) {
            e.printStackTrace();
            map.put("success","false");
        }
        String result = jsonFor(map,request,response);
        return result;
    }
    public static String jsonFor(Map map, HttpServletRequest request, HttpServletResponse response){
        String httpMethod = request.getMethod();
        Writer out = null;
        try {
            JSON json = JSON.from(map);
            String jsonStr = json.toString();
            if (jsonStr == null) {
                Debug.logError("JSON Object was empty; fatal error!", module);
                return "error";
            }
            if ("GET".equalsIgnoreCase(httpMethod)) {
                Debug.logWarning("for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
                        + "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
                        + "You might need to remove it if you use Ajax GET responses (not recommended)."
                        + "In case, the util.js scrpt is there to help you", module);
                jsonStr = jsonStr;
            }
            response.setContentType("application/x-json");
            response.setContentLength(jsonStr.getBytes("UTF8").length);
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }finally {
            try {
                if(out != null){
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return "success";
    }
    public static String jsonFor(List list, HttpServletRequest request, HttpServletResponse response){
        String httpMethod = request.getMethod();
        Writer out = null;
        try {
            JSON json = JSON.from(list);
            String jsonStr = json.toString();
            if (jsonStr == null) {
                Debug.logError("JSON Object was empty; fatal error!", module);
                return "error";
            }
            if ("GET".equalsIgnoreCase(httpMethod)) {
                Debug.logWarning("for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
                        + "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
                        + "You might need to remove it if you use Ajax GET responses (not recommended)."
                        + "In case, the util.js scrpt is there to help you", module);
                jsonStr = jsonStr;
            }
            response.setContentType("application/x-json");
            response.setContentLength(jsonStr.getBytes("UTF8").length);
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }finally {
            try {
                if(out != null){
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return "success";
    }
}
