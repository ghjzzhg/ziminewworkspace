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
import net.fortuna.ical4j.model.WeekDay;
import org.hamcrest.generator.FactoryMethod;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
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
import java.awt.peer.CheckboxPeer;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarScheduleEvents {
    public static final String module = CalendarScheduleEvents.class.getName();

    public static String findPersonalSchedule(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String userLoginId = userLogin.get("partyId").toString();
        String staffId = request.getParameter("staffId");
        String date = request.getParameter("date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<EventTO> eventTOList = null;
        try {
            if(UtilValidate.isNotEmpty(date)){
                Map<String,Object> success = dispatcher.runSync("getListOfWorkByStaffAndDate", UtilMisc.toMap("staffId", staffId, "date", format.parse(date), "userLogin", userLogin));
                Map<String,Object> listOfWork = (Map<String,Object>)success.get("returnValue");
                if(UtilValidate.isNotEmpty(listOfWork)){
                    eventTOList = FastList.newInstance();
                    eventTOList.add(listOfWorkToEventTo(listOfWork,date));
                }
            }else {
                Date startDate = format.parse(request.getParameter("start"));
                Date endDate = format.parse(request.getParameter("end"));
                Date nowDate = format.parse(format.format(new Date()));
                GenericValue staff = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", staffId), false);
                Date indexDate = nowDate;
                if (nowDate.before(startDate) || nowDate.after(endDate)) {
                    indexDate = startDate;
                }
                eventTOList = listOfWorkToEventTo(staffId,indexDate,startDate,endDate,dispatcher,userLogin);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }catch (GenericServiceException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnJson(request, response, eventTOList);
    }

    public static List<EventTO> listOfWorkToEventTo(String staffId, Date indexDate,Date startDate, Date endDate, LocalDispatcher dispatcher,GenericValue userLogin){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<EventTO> eventTOList = FastList.newInstance();
        Date itemDate = indexDate;
        try {
            while (!itemDate.after(endDate)){

                Map<String,Object> success = dispatcher.runSync("getListOfWorkByStaffAndDate", UtilMisc.toMap("staffId", staffId, "date", itemDate, "userLogin", userLogin));
                Map<String,Object> listOfWork = (Map<String,Object>)success.get("returnValue");
                eventTOList.add(listOfWorkToEventTo(listOfWork,format.format(itemDate)));
                itemDate = addDays(itemDate,1);
            }
        }catch (GenericServiceException e){

        }
        return eventTOList;
    }

    public static EventTO listOfWorkToEventTo(Map<String,Object> listOfWork,String formatDate){
        EventTO eventTO = new EventTO();
        if(listOfWork != null && listOfWork.size() > 0){
            eventTO.setId(Long.valueOf((String)listOfWork.get("listOfWorkId")));
            eventTO.setTitle((String)listOfWork.get("listOfWorkName"));
            eventTO.setStart(formatDate);
            eventTO.setEnd(formatDate);
        }else {
            eventTO.setId(100L);
            eventTO.setTitle(" ");
            eventTO.setStart(formatDate);
            eventTO.setEnd(formatDate);
        }
        return eventTO;
    }

    public static String returnAutoSchedul(HttpServletRequest request, HttpServletResponse response) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String userLoginId = userLogin.get("partyId").toString();
        String autoWorkScheduleId = request.getParameter("autoWorkScheduleId");
        String workGroups = request.getParameter("groupValues");
        String workOrders = request.getParameter("orderValues");
        Date startDate;
        Date endDate;
        List<Object> eventTOList = FastList.newInstance();
        try {
            startDate = format.parse(request.getParameter("startDate"));
            endDate = format.parse(request.getParameter("endDate"));
            if (UtilValidate.isNotEmpty(workGroups) && UtilValidate.isNotEmpty(workOrders)) {
                String[] workGroupArray = workGroups.split(",");//班组
                String[] workOrdersArray = workOrders.split(",");//班次
                int workGroupSize = workGroupArray.length;
                int workOrderSize = workOrdersArray.length;
                Date indexDate = startDate;

                ArrayList<GenericValue> groupArr = new ArrayList<>();
                for(int i = 0; i < workGroupSize; i++){
                    try {
                        GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", workGroupArray[i]), false);
                        groupArr.add(group);
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<GenericValue> listOfWorkArr = new ArrayList<>();
                for(int i = 0; i < workOrderSize; i++){
                    try {
                        GenericValue listOfWork = delegator.findOne("TblListOfWork", UtilMisc.toMap("listOfWorkId", workOrdersArray[i]), false);
                        listOfWorkArr.add(listOfWork);
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    }
                }
                while (!indexDate.after(endDate)) {
                    for (int i = 0; i < workGroupSize; i++) {//班组循环配对班次
                        GenericValue group = groupArr.get(i);
                        EventTO eventTO = new EventTO();
                        int order = CheckingUtil.getOrder(indexDate,startDate,endDate,workGroupSize,i + 1);
                        if(order > workOrderSize){
                            eventTO.setId(100L);
                            eventTO.setWorkGroup((String) group.get("groupName"));
                            eventTO.setTitle("休息");
                            eventTO.setStart(format.format(indexDate));
                            eventTOList.add(eventTO);
                        }else {
                            GenericValue listOfWork = listOfWorkArr.get(order - 1);
                            eventTO.setId(Long.valueOf(workOrdersArray[order - 1]));
                            eventTO.setWorkGroup((String) group.get("groupName"));
                            eventTO.setTitle((String) listOfWork.get("listOfWorkName"));
                            eventTO.setStart(format.format(indexDate));
                            eventTOList.add(eventTO);
                        }
                    }
                    indexDate = addDays(indexDate, 1);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        EventTO eventTO = new EventTO();
        return returnJson(request, response, eventTOList);
    }

    public static String getGroupSchedule(HttpServletRequest request, HttpServletResponse response) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String userLoginId = userLogin.get("partyId").toString();
        String orderValues = request.getParameter("orderValues");
        int workGroupSize = Integer.parseInt(request.getParameter("workGroupSize"));
        String orderStr = request.getParameter("order");
        List<Object> eventTOList = FastList.newInstance();
        try {
            String groupId = request.getParameter("groupId");                  //班组
            Date startDate = format.parse(request.getParameter("startDate"));//开始时间
            Date endDate = format.parse(request.getParameter("endDate"));    //结束时间
            String[] orderValueArray = orderValues.split(",");          //班次
            int orderValueSize = orderValueArray.length;               //班次数
            int groupOrder = Integer.parseInt(orderStr);                     //班组编号
            GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", groupId), false);
            ArrayList<GenericValue> listOfWorkArr = new ArrayList<>();
            for(int i = 0; i < orderValueArray.length; i ++){
                GenericValue listOfWork = delegator.findOne("TblListOfWork", UtilMisc.toMap("listOfWorkId", orderValueArray[i]), false);
                listOfWorkArr.add(listOfWork);
            }
            Date indexDate = startDate;
            while (!indexDate.after(endDate)) {
                EventTO eventTO = new EventTO();
                int order = CheckingUtil.getOrder(indexDate,startDate,endDate,workGroupSize,groupOrder);
                if (order > orderValueSize){
                    eventTO.setId(100L);
                    eventTO.setWorkGroup((String) group.get("groupName"));
                    eventTO.setTitle("休息");
                    eventTO.setStart(format.format(indexDate));
                    eventTOList.add(eventTO);
                }else {
                    GenericValue listOfWork = listOfWorkArr.get(order -1);
                    eventTO.setId(Long.valueOf(orderValueArray[order -1]));
                    eventTO.setWorkGroup((String) group.get("groupName"));
                    eventTO.setTitle((String) listOfWork.get("listOfWorkName"));
                    eventTO.setStart(format.format(indexDate));
                    eventTOList.add(eventTO);
                }
                indexDate = addDays(indexDate, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        EventTO eventTO = new EventTO();
        return returnJson(request, response, eventTOList);
    }
    public static String getCheckingInStatus(HttpServletRequest request, HttpServletResponse response){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        String staffId = request.getParameter("staffId");
        String startDateStr = request.getParameter("start");
        String endDateStr = request.getParameter("end");
        List<EventTO> eventTOList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(staffId) && UtilValidate.isNotEmpty(startDateStr) && UtilValidate.isNotEmpty(endDateStr)){
            try {
                Date startDate = format.parse(startDateStr);
                Date endDate = format.parse(endDateStr);
                Date indexDate = startDate;
                Date nowDate = format.parse(format.format(new Date()));
                Long item = 100L;
                while (!indexDate.after(endDate)){
                    item ++;
                    EventTO eventTO1 = new EventTO();
                    EventTO eventTO2 = new EventTO();
                    EventTO eventTO3 = new EventTO();
                    Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate", UtilMisc.toMap("staffId", staffId, "date", indexDate, "userLogin", userLogin));
                    GenericValue listOfWork = (GenericValue) successMap.get("returnValue");

                    GenericValue staff = EntityQuery.use(delegator).from("TblStaff").where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, staffId)).queryOne();
                    String directDepart = (String) staff.get("department");
                    List<GenericValue> holidayList = EntityQuery.use(delegator)
                            .from("TblHoliday")
                            .where(EntityCondition.makeCondition(
                                    EntityCondition.makeCondition(
                                            EntityCondition.makeCondition("staff", EntityOperator.EQUALS, staffId),
                                            EntityOperator.OR,
                                            EntityCondition.makeCondition("department", EntityOperator.EQUALS, directDepart)), EntityOperator.AND,
                                    EntityCondition.makeCondition(
                                            EntityCondition.makeCondition("startDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(indexDate.getTime())),
                                            EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(indexDate.getTime()))
                                    )))
                            .orderBy("lastUpdatedStamp DESC")
                            .queryList();
                    if(UtilValidate.isNotEmpty(listOfWork) && listOfWork.getString("listOfWorkId") != null){
                        String listOfWorkType = (String) listOfWork.get("listOfWorkType");
                        String listOfWorkName = (String) listOfWork.get("listOfWorkName");
                        if(listOfWorkType.equals("LIST_OF_WORK_TYPE_02")) {//休息班次
                            eventTO1.setId(item);
                            eventTO1.setStart(format.format(indexDate));
                            eventTO1.setEnd(format.format(indexDate));
                            eventTO1.setTitle("休息");
                            eventTO1.setBackgroundColor("orange");
                            eventTO1.setTextColor("white");
                            eventTO1.setBorderColor("white");
                            eventTO2.setId(item);
                            eventTO2.setStart(format.format(indexDate));
                            eventTO2.setEnd(format.format(indexDate));
                            eventTO2.setTitle("休息");
                            eventTO2.setBackgroundColor("orange");
                            eventTO2.setTextColor("white");
                            eventTO2.setBorderColor("white");

                        }else if(indexDate.compareTo(nowDate) == 1){//游标日期大于当前日期
                            eventTO1.setId(item);
                            eventTO1.setStart(format.format(indexDate));
                            eventTO1.setEnd(format.format(indexDate));
                            eventTO1.setTitle("待签到");
                            eventTO1.setBackgroundColor("gray");
                            eventTO1.setTextColor("white");
                            eventTO1.setBorderColor("white");

                            eventTO2.setId(item);
                            eventTO2.setStart(format.format(indexDate));
                            eventTO2.setEnd(format.format(indexDate));
                            eventTO2.setTitle("待签退");
                            eventTO2.setBackgroundColor("gray");
                            eventTO2.setTextColor("white");
                            eventTO2.setBorderColor("white");
                        }else {
                            try {
                                List<GenericValue> checkingInList = null;
                                if((listOfWork.get("getOffWorkTime").toString()).compareTo(listOfWork.get("toWorkTime").toString())<0){//跨天的班次
                                    java.sql.Date date1 = new java.sql.Date(indexDate.getTime());
                                    Date nextDate = addDays(indexDate,1);
                                    java.sql.Date date2 = new java.sql.Date(nextDate.getTime());
                                    java.sql.Time time = java.sql.Time.valueOf("12:00:00");
                                    checkingInList = EntityQuery.use(delegator)
                                            .from("TblCheckingIn")
                                            .where(EntityCondition.makeCondition(
                                                    EntityCondition.makeCondition("staff",EntityOperator.EQUALS, staffId),
                                                    EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS, date1),
                                                    EntityCondition.makeCondition("checkingInTime",EntityOperator.GREATER_THAN_EQUAL_TO, time),
                                                    EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS, "CHECKING_IN"),
                                                    EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                                            )).queryList();
                                    List<GenericValue> checkingOut = EntityQuery.use(delegator)
                                            .from("TblCheckingIn")
                                            .where(EntityCondition.makeCondition(
                                                    EntityCondition.makeCondition("staff",EntityOperator.EQUALS, staffId),
                                                    EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS, date2),
                                                    EntityCondition.makeCondition("checkingInTime",EntityOperator.LESS_THAN_EQUAL_TO, time),
                                                    EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS, "CHECKING_OUT"),
                                                    EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                                            )).queryList();
                                    checkingInList.addAll(checkingOut);
                                }else{//不跨天的班次
                                    checkingInList = EntityQuery.use(delegator)
                                            .from("TblCheckingIn")
                                            .where(EntityCondition.makeCondition(
                                                    EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staffId),
                                                    EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS,new java.sql.Date(indexDate.getTime())),
                                                    EntityCondition.makeCondition("listOfWorkId",EntityOperator.EQUALS,listOfWork.get("listOfWorkId"))
                                            )).queryList();
                                }
                                /*List<GenericValue> checkingInList = EntityQuery.use(delegator)
                                        .from("TblCheckingIn")
                                        .where(EntityCondition.makeCondition(
                                                EntityCondition.makeCondition("staff", EntityOperator.EQUALS, staffId),
                                                EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS,new java.sql.Date(indexDate.getTime())),
                                                EntityCondition.makeCondition("listOfWorkId", EntityOperator.EQUALS, listOfWork.get("listOfWorkId"))
                                        )).queryList();*/

                                eventTO1.setId(item);
                                eventTO1.setStart(format.format(indexDate));
                                eventTO1.setEnd(format.format(indexDate));

                                eventTO1.setTextColor("white");
                                eventTO1.setBorderColor("white");

                                eventTO2.setId(item);
                                eventTO2.setStart(format.format(indexDate));
                                eventTO2.setEnd(format.format(indexDate));

                                eventTO2.setTextColor("white");
                                eventTO2.setBorderColor("white");

//                                eventTO3.setId(item);
//                                eventTO3.setStart(format.format(indexDate));
//                                eventTO3.setEnd(format.format(indexDate));
//
//                                eventTO3.setTextColor("white");
//                                eventTO3.setBorderColor("white");
                                if(UtilValidate.isNotEmpty(checkingInList)){
                                    int checkingInSize = checkingInList.size();
                                    if(checkingInSize == 1){
                                        GenericValue checkingIn = checkingInList.get(0);
                                        String type = (String) checkingIn.get("checkingInType");
                                        String status = (String) checkingIn.get("checkingInStatus");
                                        if(CheckingInParametersUtil.CHECKING_IN.equals(type)){
                                            if (status.equals("CHECKING_IN_STATUS_NORMAL")){//正常
                                                eventTO1.setBackgroundColor("green");
                                                eventTO1.setTitle(listOfWorkName + ":正常签到");
                                            }else if (status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                                                eventTO1.setBackgroundColor("red");
                                                eventTO1.setTitle(listOfWorkName + ":迟到");
                                            }

                                            if(indexDate.compareTo(nowDate) == 0){
                                                eventTO2.setBackgroundColor("gray");
                                                eventTO2.setTitle("待签退");
                                            }else {
                                                if(UtilValidate.isNotEmpty(holidayList)){
                                                    GenericValue holiday = holidayList.get(0);
                                                    String holidayType = holiday.get("holidayType").toString();
                                                    eventTO3.setId(item);
                                                    eventTO3.setStart(format.format(indexDate));
                                                    eventTO3.setEnd(format.format(indexDate));

                                                    eventTO3.setTextColor("white");
                                                    eventTO3.setBorderColor("white");
                                                    if(holidayType.equals("HOLIDAY_LEAVE")){
                                                        eventTO3.setBackgroundColor("orange");
                                                        eventTO3.setTitle("请假");
                                                    }else{
                                                        eventTO3.setBackgroundColor("orange");
                                                        eventTO3.setTitle("法定节假日");
                                                    }
                                                    eventTO2.setBackgroundColor("blue");
                                                    eventTO2.setTitle("未签退");
                                                    eventTOList.add(eventTO3);
                                                }else{
                                                    eventTO2.setBackgroundColor("blue");
                                                    eventTO2.setTitle("未及时签退");
                                                }
                                            }
                                        }else if(CheckingInParametersUtil.CHECKING_OUT.equals(type)){
                                            if (status.equals("CHECKING_IN_STATUS_NORMAL")){//正常
                                                eventTO2.setBackgroundColor("green");
                                                eventTO2.setTitle(listOfWorkName + ":正常签退");
                                            }else if (status.equals("CHECKING_IN_STATUS_EARLY")) {//早退
                                                eventTO2.setBackgroundColor("red");
                                                eventTO2.setTitle(listOfWorkName + ":早退");
                                            }

                                            if(indexDate.compareTo(nowDate) == 0){
                                                eventTO1.setBackgroundColor("gray");
                                                eventTO1.setTitle("待签到");
                                            }else {
                                                if(UtilValidate.isNotEmpty(holidayList)){
                                                    GenericValue holiday = holidayList.get(0);
                                                    String holidayType = holiday.get("holidayType").toString();
                                                    eventTO3.setId(item);
                                                    eventTO3.setStart(format.format(indexDate));
                                                    eventTO3.setEnd(format.format(indexDate));

                                                    eventTO3.setTextColor("white");
                                                    eventTO3.setBorderColor("white");
                                                    if(holidayType.equals("HOLIDAY_LEAVE")){
                                                        eventTO3.setBackgroundColor("orange");
                                                        eventTO3.setTitle("请假");
                                                    }else{
                                                        eventTO3.setBackgroundColor("orange");
                                                        eventTO3.setTitle("法定节假日");
                                                    }
                                                    eventTO1.setBackgroundColor("blue");
                                                    eventTO1.setTitle("未签到");
                                                    eventTOList.add(eventTO3);
                                                }else{
                                                    eventTO1.setBackgroundColor("blue");
                                                    eventTO1.setTitle("未及时签到");
                                                }
                                            }
                                        }
                                    }else if (checkingInSize == 2){
                                        for(GenericValue value : checkingInList){
                                            String type = (String) value.get("checkingInType");
                                            String status = (String) value.get("checkingInStatus");
                                            if(CheckingInParametersUtil.CHECKING_IN.equals(type)){
                                                if (status.equals("CHECKING_IN_STATUS_NORMAL")){//正常
                                                    eventTO1.setBackgroundColor("green");
                                                    eventTO1.setTitle(listOfWorkName + ":正常签到");
                                                }else if (status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                                                    eventTO1.setBackgroundColor("red");
                                                    eventTO1.setTitle(listOfWorkName + ":迟到");
                                                }
                                            }else if(CheckingInParametersUtil.CHECKING_OUT.equals(type)){
                                                if (status.equals("CHECKING_IN_STATUS_NORMAL")){//正常
                                                    eventTO2.setBackgroundColor("green");
                                                    eventTO2.setTitle(listOfWorkName + ":正常签退");
                                                }else if (status.equals("CHECKING_IN_STATUS_EARLY")) {//早退
                                                    eventTO2.setBackgroundColor("red");
                                                    eventTO2.setTitle(listOfWorkName + ":早退");
                                                }
                                            }
                                        }
                                        if(UtilValidate.isNotEmpty(holidayList)){
                                            GenericValue holiday = holidayList.get(0);
                                            String holidayType = holiday.get("holidayType").toString();
                                            eventTO3.setId(item);
                                            eventTO3.setStart(format.format(indexDate));
                                            eventTO3.setEnd(format.format(indexDate));

                                            eventTO3.setTextColor("white");
                                            eventTO3.setBorderColor("white");
                                            if(holidayType.equals("HOLIDAY_LEAVE")){
                                                eventTO3.setBackgroundColor("orange");
                                                eventTO3.setTitle("请假");
                                            }else{
                                                eventTO3.setBackgroundColor("orange");
                                                eventTO3.setTitle("法定节假日");
                                            }
                                            eventTOList.add(eventTO3);
                                        }

                                    }
                                }else if(UtilValidate.isNotEmpty(holidayList)){
                                    GenericValue holiday = holidayList.get(0);
                                    String holidayType = holiday.get("holidayType").toString();
                                    /*eventTO1.setId(item);
                                    eventTO1.setStart(format.format(indexDate));
                                    eventTO1.setEnd(format.format(indexDate));

                                    eventTO1.setTextColor("white");
                                    eventTO1.setBorderColor("white");
                                    eventTO1.setBackgroundColor("blue");
                                    eventTO1.setTitle("未签到");
                                    eventTO2.setBackgroundColor("blue");
                                    eventTO2.setTitle("未签退");*/
                                    eventTO1.setBackgroundColor("white");
                                    eventTO1.setTitle("未签到签退");
                                    if(holidayType.equals("HOLIDAY_LEAVE")){
                                        eventTO2.setBackgroundColor("orange");
                                        eventTO2.setTitle("请假");
                                    }else{
                                        eventTO2.setBackgroundColor("orange");
                                        eventTO2.setTitle("法定节假日");
                                    }
//                                    eventTOList.add(eventTO3);
                                } else {
                                    eventTO1.setBackgroundColor("blue");
                                    eventTO1.setTitle("未及时签到");
                                    eventTO2.setBackgroundColor("blue");
                                    eventTO2.setTitle("未及时签退");
                                }
                            } catch (GenericEntityException e) {
                                e.printStackTrace();
                            }
                        }
                        eventTOList.add(eventTO1);
                    }else {
                        /*eventTO1.setId(item);
                        eventTO1.setStart(format.format(indexDate));
                        eventTO1.setEnd(format.format(indexDate));
                        eventTO1.setTitle("待安排工作");
                        eventTO1.setBackgroundColor("red");
                        eventTO1.setTextColor("white");
                        eventTO1.setBorderColor("white");*/
                        eventTO2.setId(item);
                        eventTO2.setStart(format.format(indexDate));
                        eventTO2.setEnd(format.format(indexDate));
                        if(getWeekOfDate(indexDate).equals("星期六") || getWeekOfDate(indexDate).equals("星期日")){
                            eventTO2.setTitle("休息");
                            eventTO2.setBackgroundColor("orange");
                        }else {
                            eventTO2.setTitle("");
                            eventTO2.setBackgroundColor("white");
                        }
                        eventTO2.setTextColor("white");
                        eventTO2.setBorderColor("white");
                    }
                    eventTOList.add(eventTO2);
                    indexDate = addDays(indexDate,1);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }catch (GenericServiceException e) {
                e.printStackTrace();
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return returnJson(request,response,eventTOList);
    }

    public static Date addDays(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }
    /**
     * 获取当前日期是星期几
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
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
}