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
package org.ofbiz.oa;

import javolution.util.FastList;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.oa.im.EventTO;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkLogEvents {
    public static final String module = WorkLogEvents.class.getName();

    public static String getMySelfSchedule(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String startStr = request.getParameter("start");
        String endStr = request.getParameter("end");
        String type = request.getParameter("type");
        String partyId = request.getParameter("partyId");
        if(UtilValidate.isEmpty(partyId)){
            partyId = userLogin.getString("partyId");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<EventTO> eventTOList = FastList.newInstance();
        try {
            /*List<GenericValue> logSet = delegator.findAll("TblLogSet",false);
            int planValue = 0;
            int logValue = 0;
            if(logSet.size() > 0){
                String planStr = logSet.get(0).getString("planValue");
                String logStr = logSet.get(0).getString("logValue");
                if(UtilValidate.isNotEmpty(planStr)){
                    planValue = Integer.parseInt(planStr);
                }
                if(UtilValidate.isNotEmpty(logStr)){
                    logValue = Integer.parseInt(logStr);
                }
            };
            request.setAttribute("logLimitDate",addDays(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()),-logValue));
            Date limitDate = addDays(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()),-planValue);*/
            Date start = format.parse(startStr);
            Date end = format.parse(endStr);
            Date indexDate = start;
            while (indexDate.compareTo(end) == -1 || indexDate.compareTo(end) == 0){
                List<GenericValue> workLogGenList = delegator.findByAnd("TblWorkLog", UtilMisc.toMap("workLogDate",new java.sql.Date(indexDate.getTime()),"partyId", partyId),null,false);
                GenericValue workLogGen = workLogGenList.size() > 0 ? workLogGenList.get(0) : null;
                String workLogId = "";
                if(UtilValidate.isNotEmpty(workLogGen)){
                    workLogId = workLogGen.getString("workLogId");
                    List<GenericValue> scheduleList = EntityQuery.use(delegator).from("TblSchedule")
                            .where(EntityCondition.makeCondition(
                                    EntityCondition.makeCondition("workLogId", EntityOperator.EQUALS, workLogId),
                                    EntityOperator.AND,
                                    EntityCondition.makeCondition("scheduleStartDatetime",EntityOperator.BETWEEN,UtilMisc.toList(UtilDateTime.getDayStart(UtilDateTime.toTimestamp(indexDate)),UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(indexDate))))
                            )).queryList();
                    for(GenericValue gen : scheduleList){
                        EventTO eventTO = new EventTO();
                        eventTO.setBackgroundColor("green");
                        eventTO.setId(Long.valueOf(workLogId));
                        eventTO.setStart(gen.getTimestamp("scheduleStartDatetime").toString());
                        eventTO.setEnd(gen.getTimestamp("scheduleEndDatetime").toString());
                        eventTO.setTitle(gen.getString("scheduleTitle"));
                        eventTOList.add(eventTO);
                    }
                    GenericValue workLog = EntityQuery.use(delegator).from("TblWorkLog").where(UtilMisc.toMap("workLogId",workLogId)).queryOne();
                    if(null != workLog && workLog.size() > 0 && null != workLog.getString("logTitle") && !"".equals(workLog.getString("logTitle"))){
                        EventTO eventTO = new EventTO();
                        eventTO.setBackgroundColor("gray");
                        eventTO.setId(Long.valueOf(workLogId));
                        eventTO.setStart(format.format(indexDate));
                        eventTO.setEnd(format.format(indexDate));
                        eventTO.setTitle(workLog.getString("logTitle"));
                        eventTOList.add(eventTO);
                    }
                }
               /* int limitCompare = indexDate.compareTo(limitDate);*/
                /*if(!"view".equals(type)){
                    EventTO eventTO = new EventTO();
                    if(UtilValidate.isNotEmpty(workLogId)){
                        eventTO.setId(Long.valueOf(workLogId));
                    }
                    eventTO.setBackgroundColor("#3a87ad");
                    eventTO.setStart(indexDate.toString());
                    eventTO.setTitle("新建日程");
                    eventTO.setClassName("myClass");
                    eventTOList.add(eventTO);
                }*/
                indexDate = addDays(indexDate,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnJson(request,response,eventTOList);
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
                jsonStr = "//" + jsonStr;
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
    public static Date addDays(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }
}