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

import javolution.util.FastList;
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
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
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
/*
* 移动端 公文通知
* */
public class BumphNoticeAndroidServices {
    public static final String module = BumphNoticeAndroidServices.class.getName();
    /*
    *移动端 获取公文通知简要信息
    * */
    public static String getBumphNoticeList(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);

        if("success".equals(message)){
            try {
                GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
                String partyId = "";
                Map map = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
                if(!UtilValidate.isEmpty(map)){
                    partyId = (String) map.get("partyId");
                }
                Map<String,Object> partySuccessMap = dispatcher.runSync("verifyViewPermissions",UtilMisc.toMap("entityName","TblNotice","isSelect",false,"partyId",partyId,"userLogin",userLogin));
                HashMap partyInfo = (HashMap)partySuccessMap.get("data");
                List<String> dataId = new ArrayList<String>();
                if(null != partyInfo){
                    List<Map<String,Object>> entityDataList = (List)partyInfo.get("entityDataList");
                    for(Map<String,Object> map1 : entityDataList){
                        if(null != map1.get("dataId")){
                            dataId.add(map1.get("dataId").toString());
                        }
                    }
                }
                List<GenericValue> members2 = EntityQuery.use(delegator).select("noticeId")
                        .from("TblSignInPerson")
                        .where(EntityCondition.makeCondition("staffId", EntityOperator.EQUALS, partyId),
                                EntityCondition.makeCondition("signInPersonType", EntityOperator.EQUALS, "TblNotice"))
                        .distinct()
                        .queryList();
                List conditionList = new ArrayList();
                conditionList.add(EntityCondition.makeCondition("noticeId", EntityOperator.IN, dataId));
                EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("noticeId", EntityOperator.IN, dataId),
                        EntityCondition.makeCondition("releasePerson", EntityOperator.EQUALS, partyId)),
                EntityOperator.OR);
                //判断需要签收的人也有权限查看该消息
                if (members2.size() > 0) {
                    List<String> list = new ArrayList<String>();
                    for (GenericValue genericValue : members2){
                        if (null != genericValue.get("noticeId")){
                            list.add(genericValue.get("noticeId").toString());
                        }
                    }
                    condition = EntityCondition.makeCondition(UtilMisc.toList(
                                    condition,
                                    EntityCondition.makeCondition("noticeId", EntityOperator.IN, list)),
                            EntityOperator.OR);
                }
                List<GenericValue> members = EntityQuery.use(delegator).select("noticeId", "title", "noticeType", "releaseTime", "hasFeedback")
                        .from("TblNotice")
                        .where(condition)
                        .orderBy("-releaseTime")
                        .distinct()
                        .queryList();
                // 获取结果片段
                List<Object> valueList = FastList.newInstance();
                for(GenericValue value : members){
                    Map<String,Object> valueMap = FastMap.newInstance();
                    valueMap.putAll(value);
                    GenericValue genericValue = delegator.findOne("Enumeration",UtilMisc.toMap("enumId",value.getString("noticeType")),false);
                    valueMap.put("noticeTypeDesc",genericValue.getString("description"));
                    valueList.add(valueMap);
                }
                returnJson(request,response,valueList);
            } catch (GenericEntityException e) {
                return "error";
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
    /*
    * 移动端：获取公文通知信息
    * */
    public static String getBumphNoticeById(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            try {
                GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
                String noticeId = request.getParameter("noticeId");
                GenericValue notice = delegator.findOne("NoticeInfo",UtilMisc.toMap("noticeId",noticeId),false);

                Map<String,Object> staffSuccessMap = dispatcher.runSync("getAllDepartmentMember",UtilMisc.toMap("partyId","DevTeam1","userLogin",userLogin));

                Map<String,Object> succesMap = dispatcher.runSync("getDataScope",UtilMisc.toMap("entityName","TblNotice","dataId",noticeId,"userLogin",userLogin));
                List<Map<String,Object>> dataScope = ( List<Map<String,Object>>)succesMap.get("description");
                String dataScopeStr = "";
                if (UtilValidate.isEmpty(dataScope)){
                    dataScopeStr = "全体员工";
                }else {
                    for(Map<String,Object> map : dataScope){
                        dataScopeStr += map.get("name") + " ";
                    }
                }
                List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
                if (notice != null){
                    Map<String,Object> valueMap = FastMap.newInstance();
                    valueMap.putAll(notice);
                    valueMap.put("dataScope",dataScopeStr);
                    listMap.add(valueMap);
                }
                returnJson(request,response,listMap);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
    /*
    * 移动端 获取签收记录
    * */
    public static String getSignInPersonInfo(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            try {
                String noticeId = request.getParameter("noticeId");
                String partId = "";
                Map map1 = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",request.getParameter("USERNAME")));
                if(!UtilValidate.isEmpty(map1)){
                    partId = (String) map1.get("partyId");
                }
                EntityConditionList dateCondition = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("noticeId", EntityOperator.EQUALS, noticeId),
                        EntityCondition.makeCondition("staffId", EntityOperator.EQUALS, partId)
                ), EntityOperator.AND);
                List<GenericValue> members = EntityQuery.use(delegator)
                        .select("singnInPersonId", "departmentName", "fullName", "signInTime", "signInPersonStatus", "stautsDesc", "hasSignInDesc", "hasSignIn", "remark", "staffId")
                        .from("SignInRecord")
                        .where(dateCondition)
                        .distinct()
                        .queryList();
                List<GenericValue> statusList = delegator.findByAnd("Enumeration",UtilMisc.toMap("enumTypeId","NOTICE_STATUS"));
                List<GenericValue> hasSignInList = delegator.findByAnd("Enumeration",UtilMisc.toMap("enumTypeId","NOTICE_SIGNIN"));
                Map valueMap = FastMap.newInstance();
                valueMap.put("value",members);
                valueMap.put("statusList",statusList);
                valueMap.put("hasSignInList",hasSignInList);
                returnJson(request, response, valueMap);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
    public static String getSignInPersonInfoById(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            try {
                String noticeId = request.getParameter("noticeId");
                List members = EntityQuery.use(delegator)
                        .select("groupName","fullName","signInTime","singnInPersonStatus","stautsDesc","hasSignInDesc","hasSignIn")
                        .from("SignInRecord")
                        .where(EntityCondition.makeCondition("noticeId",EntityOperator.EQUALS,noticeId))
                        .orderBy("-releaseTime")
                        .distinct()
                        .queryList();
                returnJson(request,response,members);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
    /*
    * 移动端 获取反馈
    * */
    public static String getFeedback(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            try {
                String noticeId = request.getParameter("noticeId");
                List<GenericValue> members = EntityQuery.use(delegator)
                        .select("feedbackPersonName", "feedbackPersonDeptName", "feedbackTime", "feedbackContext")
                        .from("FeedbackMiddleFeedback")
                        .where(EntityCondition.makeCondition(
                                EntityCondition.makeCondition("feedbackMiddleId", EntityOperator.EQUALS, noticeId),
                                EntityJoinOperator.AND,
                                EntityCondition.makeCondition("feedbackMiddleType", EntityOperator.EQUALS, "TblNotice")
                        ))
                        .orderBy("-feedbackTime")
                        .distinct()
                        .queryList();
                returnJson(request,response,members);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
/*
* 移动端 保存签收信息
* */
    public static String updateSignInPerson(HttpServletRequest request, HttpServletResponse response) {
        String message = LoginWorker.login(request, response);
        Map<String,Object> valueMap = FastMap.newInstance();
        if("success".equals(message)){
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String singnInPersonId = request.getParameter("singnInPersonId");
            String noticeId = request.getParameter("noticeId");
            String staffId = request.getParameter("staffId");
            String signInStatus = request.getParameter("hasSignIn");
            String status = request.getParameter("status");
            String remark = request.getParameter("remark");
            try {
                Map<String,Object> succesMap = dispatcher.runSync("updateSignInRecord",UtilMisc.toMap("singnInPersonId",singnInPersonId,"remark",remark,"signInPersonStatus",status,"signInStatus",signInStatus,"staffId",staffId,"noticeId",noticeId,"userLogin",userLogin));
                valueMap.putAll(succesMap);
            } catch (GenericServiceException e) {
                Debug.logError("保存失败", module);
                valueMap.put("responseMessage","error");
                valueMap.put("msg","反馈失败！");
                returnJson(request,response,valueMap);
                return "error";
            }
        }else {
            valueMap.put("responseMessage","error");
            valueMap.put("msg","请登陆！");
        }
        returnJson(request,response,valueMap);
        return "success";
    }
/*
* 移动端 保存反馈
* */
    public static String saveFeedback(HttpServletRequest request, HttpServletResponse response) {
        String message = LoginWorker.login(request, response);
        Map<String,Object> valueMap = FastMap.newInstance();
        if("success".equals(message)){
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String noticeId = request.getParameter("noticeId");
            String feedbackContext = request.getParameter("feedbackContext");
            try {
                Map<String,Object> succesMap = dispatcher.runSync("saveNoticeFeedback",UtilMisc.toMap("noticeId",noticeId,"feedbackContext",feedbackContext,"userLogin",userLogin));
                valueMap.putAll(succesMap);
            } catch (GenericServiceException e) {
                Debug.logError("保存失败",module);
                valueMap.put("responseMessage","error");
                valueMap.put("msg","反馈失败！");
                returnJson(request,response,valueMap);
                return "error";
            }
        }else {
            valueMap.put("responseMessage","error");
            valueMap.put("msg","请登陆！");
        }
        returnJson(request,response,valueMap);
        return "success";
    }

    public static String returnJson(HttpServletRequest request, HttpServletResponse response, Object toJson) {
        //返回json
        String httpMethod = request.getMethod();
        Writer out = null;
        try {
            JSON json = JSON.from(toJson);
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
            }
            response.setContentType("application/x-json");
            response.setContentLength(jsonStr.getBytes("UTF8").length);
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "success";
    }
}
