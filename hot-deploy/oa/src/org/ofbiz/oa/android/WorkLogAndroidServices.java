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
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.event.EventUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.Object;
import java.lang.String;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
* 移动端 公文通知
* */
public class WorkLogAndroidServices {
    public static final String module = WorkLogAndroidServices.class.getName();
    /*
    *移动端 获取日程列表
    * */
    public static String getWorkLogList(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            String workLogDateStr = request.getParameter("workLogDate");
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            try {
                List<GenericValue> workLogGenList = delegator.findByAnd("TblWorkLog",UtilMisc.toMap("workLogDate",UtilDateTime.toSqlDate(workLogDateStr,"yyyy-MM-dd"),"partyId",userLogin.getString("partyId")),null,false);
                Map<String,Object> valueMap = FastMap.newInstance();
                if(UtilValidate.isNotEmpty(workLogGenList) && workLogGenList.size() > 0){
                    GenericValue workLogGen = workLogGenList.get(0);
                    List<GenericValue> scheduleGenList = delegator.findByAnd("TblSchedule",UtilMisc.toMap("workLogId",workLogGen.getString("workLogId")),null,false);
                    List<Map<String,Object>> scheduleList = new ArrayList<Map<String,Object>>();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (GenericValue genericValue : scheduleGenList){
                        Map<String,Object> map = new HashMap<String, Object>();
                        map.putAll(genericValue);
                        map.put("lastUpdatedStamp",format.format(map.get("lastUpdatedStamp")));
                        map.put("createdTxStamp",format.format(map.get("createdTxStamp")));
                        map.put("createdStamp",format.format(map.get("createdStamp")));
                        map.put("scheduleEndDatetime",format.format(map.get("scheduleEndDatetime")));
                        map.put("lastUpdatedTxStamp",format.format(map.get("lastUpdatedTxStamp")));
                        map.put("scheduleStartDatetime",format.format(map.get("scheduleStartDatetime")));
                        scheduleList.add(map);
                    }
                    valueMap.put("workLog",workLogGen);
                    valueMap.put("scheduleList",scheduleList);
                }
                List<GenericValue> logSetList = delegator.findAll("TblLogSet",false);
                Map<String,Object> logSetMap = FastMap.newInstance();
                if(logSetList.size() > 0){
                    logSetMap.put("logValue", logSetList.get(0).get("logValue"));
                    logSetMap.put("planValue",logSetList.get(0).get("planValue"));
                    logSetMap.put("instructionsValue", logSetList.get(0).get("instructionsValue"));
                }
                valueMap.put("logSet",logSetMap);
                return returnJson(request,response,valueMap);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                return EventUtil.returnError(request,"error");
            }
        }else {
            return EventUtil.returnError(request,"error");
        }
    }
    /*
    * 下属日志列表
    * */
    public static String getUnderlingLogList(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            String workLogDateStr = request.getParameter("workLogDate");
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            try {
                List<GenericValue> workLogGenList = delegator.findByAnd("TblWorkLog",UtilMisc.toMap("workLogDate",UtilDateTime.toSqlDate(workLogDateStr,"yyyy-MM-dd"),"partyId",userLogin.getString("partyId")),null,false);
                Map<String,Object> valueMap = FastMap.newInstance();
                if(UtilValidate.isNotEmpty(workLogGenList) && workLogGenList.size() > 0){
                    GenericValue workLogGen = workLogGenList.get(0);
                    List<GenericValue> scheduleGenList = delegator.findByAnd("TblSchedule",UtilMisc.toMap("workLogId",workLogGen.getString("workLogId")),null,false);
                    valueMap.put("workLog",workLogGen);
                    valueMap.put("scheduleList",scheduleGenList);
                }
                List<GenericValue> logSetList = delegator.findAll("TblLogSet",false);
                Map<String,Object> logSetMap = FastMap.newInstance();
                if(logSetList.size() > 0){
                    logSetMap.put("logValue", logSetList.get(0).get("logValue"));
                    logSetMap.put("planValue",logSetList.get(0).get("planValue"));
                    logSetMap.put("instructionsValue", logSetList.get(0).get("instructionsValue"));
                }
                valueMap.put("logSet",logSetMap);
                return returnJson(request,response,valueMap);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                return EventUtil.returnError(request,"error");
            }
        }else {
            return EventUtil.returnError(request,"error");
        }
    }
/*
* 保存日程
* */
    public static String saveSchedule(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        Map returnMap = new HashMap();
        Writer out = null;
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String scheduleStartDatetimeStr = request.getParameter("scheduleStartDatetime");
            String scheduleEndDatetimeStr = request.getParameter("scheduleEndDatetime");
            String workLogId = request.getParameter("workLogId");
            String scheduleId = request.getParameter("scheduleId");
            if (UtilValidate.isEmpty(scheduleId)){
                scheduleId = delegator.getNextSeqId("TblSchedule").toString();//获取主键ID
            }
            String scheduleTitle = request.getParameter("scheduleTitle");
            String scheduleContent = request.getParameter("scheduleContent");
            java.sql.Timestamp scheduleStartDatetime = UtilDateTime.toTimestamp(format.parse(scheduleStartDatetimeStr));
            java.sql.Timestamp scheduleEndDatetime = UtilDateTime.toTimestamp(format.parse(scheduleEndDatetimeStr));
            Map<String,Object> valueMap = UtilMisc.toMap("scheduleStartDatetime",scheduleStartDatetime,"scheduleEndDatetime",scheduleEndDatetime,"workLogId",workLogId,"scheduleTitle",scheduleTitle,"scheduleContent",scheduleContent,"scheduleId",scheduleId);

            DynamicViewEntity dynamicView = new DynamicViewEntity();
            dynamicView.addMemberEntity("workLog","TblWorkLog");
            dynamicView.addMemberEntity("schedule","TblSchedule");
            dynamicView.addAliasAll("workLog","",null);
            dynamicView.addAliasAll("schedule","",null);
            dynamicView.addViewLink("workLog","schedule",false,UtilMisc.toList(ModelKeyMap.makeKeyMapList("workLogId")));
            try {
                List<EntityCondition> condition = FastList.newInstance();
                String partyId = userLogin.get("partyId").toString();
                condition.add(EntityCondition.makeCondition("partyId",partyId));
                if (UtilValidate.isNotEmpty(workLogId)) {
                    if(UtilValidate.isNotEmpty(scheduleId)){
                        condition.add(EntityCondition.makeCondition("scheduleId",EntityOperator.NOT_EQUAL,scheduleId));
                        condition.add(EntityCondition.makeCondition("workLogDate",UtilDateTime.toSqlDate(scheduleStartDatetimeStr,"yyyy-MM-dd")));
                        if(!checkDateTime(condition,scheduleStartDatetime,scheduleEndDatetime,dynamicView,delegator)){
                            out = response.getWriter();
                            out.write("success");
                            out.flush();
                        } else {
                            return EventUtil.EVENT_RETURN_ERROR;
                        }
                    }else {
                        if(!checkDateTime(condition,scheduleStartDatetime,scheduleEndDatetime,dynamicView,delegator)){
                            out = response.getWriter();
                            out.write("success");
                            out.flush();
                        } else {
                            return EventUtil.EVENT_RETURN_ERROR;
                        }
                    }

                    long scheduleCount = delegator.findCountByCondition("TblSchedule", EntityCondition.makeCondition(
                            UtilMisc.toMap("scheduleId", scheduleId)), null, null);
                    GenericValue schedule = delegator.makeValidValue("TblSchedule", valueMap);
                    if (scheduleCount > 0) {
                        delegator.store(schedule);
                    } else {
                        delegator.create(schedule);
                    }
                } else {
                    out = response.getWriter();
                    out.write("success");
                    out.flush();
                    workLogId = delegator.getNextSeqId("TblWorkLog");
                    valueMap.put("workLogId",workLogId);
                    delegator.create(delegator.makeValidValue("TblWorkLog", UtilMisc.toMap("workLogId", workLogId, "partyId", userLogin.getString("partyId"), "workLogDate", UtilDateTime.toSqlDate(scheduleStartDatetimeStr, "yyyy-MM-dd"))));
                    delegator.create(delegator.makeValidValue("TblSchedule",valueMap));
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return EventUtil.returnSuccess(request);
        }else {
            return EventUtil.EVENT_RETURN_ERROR;
        }
    }

/*
* 保存日志
* */
    public static String saveWorkLog(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        Map returnMap = new HashMap();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator)request.getAttribute("delegator");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String workLogId = request.getParameter("workLogId");
            String workLogDate = request.getParameter("workLogDate");
            String logTitle = request.getParameter("logTitle");
            String logContent = request.getParameter("logContent");
            Map<String,Object> valueMap = UtilMisc.toMap("workLogId",workLogId,"logTitle",logTitle,"workLogDate",UtilDateTime.toSqlDate(workLogDate,"yyyy-MM-dd"),"partyId",userLogin.getString("partyId"),"logContent",logContent);
            try {
                if (UtilValidate.isNotEmpty(workLogId)) {
                    delegator.store(delegator.makeValidValue("TblWorkLog",valueMap));
                } else {
                    workLogId = delegator.getNextSeqId("TblWorkLog");
                    valueMap.put("workLogId",workLogId);
                    delegator.create(delegator.makeValidValue("TblWorkLog", valueMap));
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
                return EventUtil.returnError(request,"保存失败");
            }
            return EventUtil.returnSuccess(request);
        }else {
            return EventUtil.EVENT_RETURN_ERROR;
        }
    }
    /*
    * 保存审批
    * */
    public static String saveReview(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        Map returnMap = new HashMap();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator) request.getAttribute("delegator");
        String message = LoginWorker.login(request, response);
        if ("success".equals(message)) {
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String workLogId = request.getParameter("workLogId");
            String workLogDate = request.getParameter("workLogDate");
            String partyId = request.getParameter("partyId");
            String reviewContent = request.getParameter("reviewContent");
            Map<String, Object> valueMap = UtilMisc.toMap("workLogId", workLogId, "workLogDate", UtilDateTime.toSqlDate(workLogDate, "yyyy-MM-dd"), "partyId", partyId, "reviewedBy", userLogin.getString("partyId"), "reviewContent", reviewContent);
            try {
                delegator.store(delegator.makeValidValue("TblWorkLog", valueMap));
            } catch (GenericEntityException e) {
                e.printStackTrace();
                return EventUtil.returnError(request, "审批失败");
            }
            return EventUtil.returnSuccess(request);
        } else {
            return EventUtil.EVENT_RETURN_ERROR;
        }
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
                return EventUtil.returnError(request,"error");
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
            return EventUtil.returnSuccess(request,"true");
        } catch (IOException e) {
            Debug.logError(e, module);
            return EventUtil.returnError(request, "error");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /*
    *Author 邓贝贝
    *created 2016/1/6 14:49
    *function 时间戳转换为字符串
    */
    protected static String getString(Timestamp str){
        String timesTamp = "";
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        java.sql.Timestamp timestamp = (java.sql.Timestamp)str;
        Date date = new Date(timestamp.getTime());
        timesTamp = format.format(date);
        return timesTamp;
    }

    /*
*Author 邓贝贝
*created 2016/2/26 13:49
*function  创建或者修改日程时判断是否时间重叠
*/
    public static boolean checkDateTime(List<EntityCondition> condition, Timestamp startDate, Timestamp endDate,DynamicViewEntity dynamicView, Delegator delegator){
        List<GenericValue> sValueList = null;
        try {
            sValueList = EntityQuery.use(delegator).from(dynamicView).where(condition).queryList();
            for(GenericValue value : sValueList){
                Timestamp start = value.getTimestamp("scheduleStartDatetime");
                Timestamp end = value.getTimestamp("scheduleEndDatetime");
                int compare1 = startDate.compareTo(end);
                int compare2 = endDate.compareTo(start);
                if((compare1 != 1) && (compare2 != -1)){
                    return true;
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return false;
    }
}

