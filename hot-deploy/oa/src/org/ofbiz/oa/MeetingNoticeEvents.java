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
import javolution.util.FastMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class MeetingNoticeEvents {
    public static final String module = MeetingNoticeEvents.class.getName();
    public static final String resourceError = "ProductErrorUiLabels";
    public MeetingNoticeEvents() {
    }
    public static String addWorkPlan(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        int rowCount = UtilHttp.getMultiFormRowCount(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String userLoginId = userLogin.get("partyId").toString();
        String workPlanId = request.getParameter("workPlanId");  //计划id
        String participants = ""; //与会人员
//        String isIncludeParticipator = request.getParameter("isIncludeParticipator");
//        if(UtilValidate.isNotEmpty(isIncludeParticipator) && "on".equals(isIncludeParticipator)){//判断计划执行人是否包含与会人员
//            participants = request.getParameter("participants");
//        }
//        participants = projectLeader + "," + participants;
        /*---------权限参数---------------*/
        String noticeDataScope_entity_name = request.getParameter("noticeDataScope_entity_name");
        String noticeDataScope_dept_only = request.getParameter("noticeDataScope_dept_only");
        String noticeDataScope_dept_like = request.getParameter("noticeDataScope_dept_like");
        String noticeDataScope_level_only = request.getParameter("noticeDataScope_level_only");
        String noticeDataScope_level_like = request.getParameter("noticeDataScope_level_like");
        String noticeDataScope_position_only = request.getParameter("noticeDataScope_position_only");
        String noticeDataScope_position_like = request.getParameter("noticeDataScope_position_like");
        String noticeDataScope_user = request.getParameter("noticeDataScope_user");
        /*----------------------end-------------*/
        String planDescription = request.getParameter("meetingTheme"); //工作计划描述
        String title = request.getParameter("title");   //工作计划标题
        java.sql.Date startTime = UtilDateTime.toSqlDate(request.getParameter("startTime"), UtilDateTime.DATE_FORMAT);  //工作计划开始日期
        java.sql.Date completeTime = UtilDateTime.toSqlDate(request.getParameter("completeTime"), UtilDateTime.DATE_FORMAT);  //工作计划完成日期
        String projectLeader = request.getParameter("projectLeader"); //项目主管
        String executor = request.getParameter("executor"); //执行者

//        List<Date> startTimeList = FastList.newInstance();
//        List<Date> endTimeList = FastList.newInstance();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> attrMap = FastMap.newInstance();
        try {
//            for (int i = 1; i < rowCount; i++) {
//                String curSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
//                String personWorkStartTime = request.getParameter("personWorkStartTime" + curSuffix);
//                String personWorkEndTime = request.getParameter("personWorkEndTime" + curSuffix);
//
//                if (UtilValidate.isNotEmpty(personWorkStartTime)) {
//                    startTimeList.add(format.parse(personWorkStartTime));
//                }
//                if (UtilValidate.isNotEmpty(personWorkEndTime)) {
//                    endTimeList.add(format.parse(personWorkEndTime));
//                }
//            }
//            Date minTime = new Date();
//            Date maxTime = new Date();
//            if (UtilValidate.isNotEmpty(startTimeList) && UtilValidate.isNotEmpty(endTimeList)) {
//                int sTListSize = startTimeList.size();
//                minTime = startTimeList.get(0);
//                maxTime = endTimeList.get(0);
//                for (int i = 1; i < sTListSize; i++) {
//                    minTime = (minTime.getTime() < startTimeList.get(i).getTime()) ? minTime : startTimeList.get(i);
//                    maxTime = maxTime.getTime() > endTimeList.get(i).getTime() ? maxTime : endTimeList.get(i);
//                }
//
                String departmentId = "";
                try {
                    GenericValue userLoginDepartment = EntityQuery.use(delegator).select("department").from("TblStaff").where("partyId",userLoginId).queryOne();
                    departmentId = userLoginDepartment.getString("department");
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
                //保存工作计划
                Map<String, Object> successReturn = FastMap.newInstance();
                if(UtilValidate.isNotEmpty(workPlanId)){
                    successReturn = dispatcher.runSync("saveWorkPlanCreate",
                            UtilMisc.toMap("workPlanId",workPlanId,"executor", executor, "canSeePerson", UtilMisc.toList("WP_CS_ONLY_PLAN"),
                                    "title", title, "startTime", startTime,
                                    "completeTime", completeTime,
                                    "noticePerson", UtilMisc.toList("planPerson", "leader"), "planDescription", planDescription,
                                    "planType", "", "isPerformance", "WP_N", "importanceDegree", "WP_DEGREE_ME",
                                    "difficultyDegree", "WP_DEGREE_ME","projectLeader", projectLeader,
                                    "workPlanStatus", new Double(0), "userLogin", userLogin));
                }else {
                    successReturn = dispatcher.runSync("saveWorkPlanCreate",
                            UtilMisc.toMap("executor", executor, "canSeePerson", UtilMisc.toList("WP_CS_ONLY_PLAN"),
                                    "departmentId", departmentId, "title", title, "startTime", startTime,
                                    "completeTime", completeTime,
                                    "noticePerson", UtilMisc.toList("planPerson", "leader"), "planDescription", planDescription,
                                    "planType", "", "isPerformance", "WP_N", "importanceDegree", "WP_DEGREE_ME",
                                    "difficultyDegree", "WP_DEGREE_ME", "planPerson", userLoginId, "projectLeader", projectLeader,
                                    "workPlanStatus", new Double(0), "userLogin", userLogin));
                }
//            }
            workPlanId = successReturn.get("workPlanId") != null ? successReturn.get("workPlanId").toString() : null;
            attrMap.put("workPlanId", workPlanId);//返回json数据

            /*任务安排*/
            boolean isUpdate = false;
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
            String oldExecutorList = request.getParameter("oldExecutorList");
            for(int i = 1;i < jobCount + 1; i++){
                String jobDescription = request.getParameter("description_o1_" + i);
                java.sql.Date jobStartTime = UtilDateTime.toSqlDate(request.getParameter("startTime_o1_" + i), UtilDateTime.DATE_FORMAT);
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
//            List<Map> childWorkPlan = FastList.newInstance();
//            for (int i = 0; i < rowCount; i++) {
//                String curSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
//                String personWorkExecutor = request.getParameter("executor" + curSuffix);
//                String personWorkStartTime = request.getParameter("personWorkStartTime" + curSuffix);
//                String personWorkEndTime = request.getParameter("personWorkEndTime" + curSuffix);
//                String personWorkDescription = request.getParameter("personWorkDescription" + curSuffix);
//                String personWorkId = request.getParameter("personWorkId" + curSuffix);
//                if (UtilValidate.isNotEmpty(personWorkExecutor)
//                        || UtilValidate.isNotEmpty(personWorkStartTime) || UtilValidate.isNotEmpty(personWorkEndTime) || UtilValidate.isNotEmpty(personWorkDescription)) {
//                    Map<String, Object> contextValue = FastMap.newInstance();
//
//                    if (UtilValidate.isNotEmpty(personWorkExecutor)) {
//                        contextValue.put("personId", personWorkExecutor);
//                    }
//
//                    if (UtilValidate.isNotEmpty(personWorkStartTime)) {
//                        contextValue.put("startTime", new Timestamp(format.parse(personWorkStartTime).getTime()));
//                    }
//                    if (UtilValidate.isNotEmpty(personWorkEndTime)) {
//                        contextValue.put("completeTime", new Timestamp(format.parse(personWorkEndTime).getTime()));
//                    }
//
//                    if (UtilValidate.isNotEmpty(personWorkDescription)) {
//                        contextValue.put("description", personWorkDescription);
//                    }
//                    contextValue.put("workPlanId", workPlanId);
//                    contextValue.put("userLogin", userLogin);
//                    if(UtilValidate.isEmpty(personWorkId)){
//                        personWorkId = delegator.getNextSeqId("TblPersonWork");
//                        contextValue.put("personWorkId",personWorkId);
//                        GenericValue personWork = delegator.makeValidValue("TblPersonWork",contextValue);
//                        delegator.create(personWork);
//                    }else {
//                        contextValue.put("personWorkId",personWorkId);
//                        GenericValue personWork = delegator.makeValidValue("TblPersonWork",contextValue);
//                        delegator.store(personWork);
//                    }
//                   /* GenericValue executorGen = delegator.makeValidValue("TblWorkPlanExecutor","workPlanId",workPlanId,"partyId",personWorkExecutor);
//                    delegator.create(executorGen);*/
//                    childWorkPlan.add(UtilMisc.toMap("attrName","personWorkId"+curSuffix,"attrValue",personWorkId));
//                }
//            }
//            attrMap.put("childWorkPlan",childWorkPlan);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        //返回json
        String httpMethod = request.getMethod();
        Writer out;
        try {
            JSON json = JSON.from(attrMap);
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
        }
        return "success";
    }
}
