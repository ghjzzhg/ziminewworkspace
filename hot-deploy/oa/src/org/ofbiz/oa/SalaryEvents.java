package org.ofbiz.oa;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hwt on 2015/9/21.
 */
public class SalaryEvents {
    public static String saveMould(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        String content = (String) request.getAttribute("content");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String mouldId = delegator.getNextSeqId("TblSalaryBillMould").toString();
        GenericValue genericValue = delegator.makeValidValue("TblSalaryBillMould",UtilMisc.toMap("mouldId",mouldId));
        genericValue.setString("mouldContent",content);
        genericValue.create();
        return "success";
    }
    public static String saveSendDetail(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String msg = "保存成功";
        String type = request.getParameter("type");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        int rowCount = UtilHttp.getMultiFormRowCount(ctx);
        Calendar cale =  Calendar.getInstance();
        Integer month = cale.get(Calendar.MONTH);
        String sendId = "";
        String staffId = "";
        double sendMoney = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,cal.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        for (int i = 0; i < rowCount; i++) {
            String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
            String year = (String) ctx.get("year" + suffix);
            if (UtilValidate.isEmpty(sendId)) {
                sendId = (String) ctx.get("sendId" + suffix);
            }
            String detailId = (String) ctx.get("detailId" + suffix);
            String salary = (String) ctx.get("salary" + suffix);
            String remarks = (String) ctx.get("remarks" + suffix);
            String typeFor = (String) ctx.get("type" + suffix);
            String title = (String) ctx.get("title" + suffix);
            String entryId = (String) ctx.get("entryId" + suffix);
            staffId = (String) ctx.get("staffId" + suffix);
            if (UtilValidate.isNotEmpty(salary)) {
                if (typeFor.equals("SEND")) {
                    sendMoney += Double.parseDouble(salary);
                } else if (typeFor.equals("DISCOUNT")) {
                    sendMoney -= Double.parseDouble(salary);
                }
            }
            if (title.equals("基本工资")) {
                int numberSize = CheckingUtil.getAbsenteeismSize(delegator, dispatcher, firstDate, lastDate, staffId, userLogin);
                if (UtilValidate.isEmpty(sendId)) {
                    sendId = delegator.getNextSeqId("TblSalarySend").toString();
                    GenericValue genericValue = delegator.makeValue("TblSalarySend", UtilMisc.toMap("sendId", sendId));
                    genericValue.setString("staffId", staffId);
                    genericValue.setString("month", month.toString());
                    genericValue.setString("year", year);
                    if (type.equals("submit")) {
                        genericValue.setString("state", "未审");
                    } else {
                        genericValue.setString("state", "未发");
                    }
                    try {
                        genericValue.create();
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                        msg = "保存失败";
                        return "error";
                    }
                } else {
                    GenericValue genericValue = null;
                    try {
                        genericValue = delegator.findByPrimaryKey("TblSalarySend", UtilMisc.toMap("sendId", sendId));
                        if (type.equals("submit")) {
                            genericValue.setString("state", "未审");
                        } else {
                            genericValue.setString("state", "未发");
                        }
                        genericValue.store();
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                        msg = "保存失败";
                        return "error";
                    }
                }
                List<GenericValue> checkingInForLate = null;
                try {
                    checkingInForLate = delegator.findByAnd("TblCheckingFor", UtilMisc.toMap("staff", staffId, "checkingInMonth", Long.parseLong(month.toString()), "checkingInStatus", "CHECKING_IN_STATUS_LATE"));
                    List<GenericValue> checkingInForLeaveEarly = delegator.findByAnd("TblCheckingFor", UtilMisc.toMap("staff", staffId, "checkingInMonth", Long.parseLong(month.toString()), "checkingInType", "CHECKING_IN_STATUS_EARLY"));
                    for (Map checkingMap : checkingInForLate) {
                        int minutes = (int) checkingMap.get("minutes");
                        List<GenericValue> attendanceList = delegator.findByAnd("TblSalaryOnAttendance", UtilMisc.toMap("type", "LATE"));
                        for (Map attendanceMap : attendanceList) {
                            int timeStart = Integer.parseInt((String) attendanceMap.get("timeRangeStart"));
                            int timeEnd = Integer.parseInt((String) attendanceMap.get("timeRangeEnd"));
                            int number = Integer.parseInt((String) attendanceMap.get("numberRangeEnd"));
                            String deductType = (String) attendanceMap.get("deductType");
                            int value = Integer.parseInt((String) attendanceMap.get("value"));
                            if (number < attendanceList.size()) {
                                if (minutes >= timeStart && minutes <= timeEnd) {
                                    if (deductType.equals("PERCENT")) {
                                        sendMoney -= sendMoney * (value / 100);
                                    } else if (deductType.equals("MONEY")) {
                                        sendMoney -= value;
                                    }
                                }
                            }
                        }
                    }
                    for (Map checkingMap : checkingInForLeaveEarly) {
                        int minutes = (int) checkingMap.get("minutes");
                        List<GenericValue> attendanceList = delegator.findByAnd("TblSalaryOnAttendance", UtilMisc.toMap("type", "LEAVE_EARLY"));
                        for (Map attendanceMap : attendanceList) {
                            int timeStart = Integer.parseInt((String) attendanceMap.get("timeRangeStart"));
                            int timeEnd = Integer.parseInt((String) attendanceMap.get("timeRangeEnd"));
                            int number = Integer.parseInt((String) attendanceMap.get("numberRangeEnd"));
                            String deductType = (String) attendanceMap.get("deductType");
                            int value = Integer.parseInt((String) attendanceMap.get("value"));
                            if (number < attendanceList.size()) {
                                if (minutes >= timeStart && minutes <= timeEnd) {
                                    if (deductType.equals("PERCENT")) {
                                        sendMoney -= sendMoney * (value / 100);
                                    } else if (deductType.equals("MONEY")) {
                                        sendMoney -= value;
                                    }
                                }
                            }
                        }
                    }
                    List<GenericValue> attendanceList = delegator.findByAnd("TblSalaryOnAttendance", UtilMisc.toMap("type", "ABSENTEEISM"));
                    for (Map attendanceMap : attendanceList) {
                        if (!UtilValidate.isEmpty(attendanceMap.get("numberRangeEnd"))) {
                            int number = Integer.parseInt((String) attendanceMap.get("numberRangeEnd"));
                            String deductType = (String) attendanceMap.get("deductType");
                            int value = Integer.parseInt((String) attendanceMap.get("value"));
                            if (number < numberSize) {
                                for (int j = 0; j < numberSize - number; j++) {
                                    if (deductType.equals("PERCENT")) {
                                        sendMoney -= sendMoney * (value / 100);
                                    } else if (deductType.equals("MONEY")) {
                                        sendMoney -= value;
                                    }
                                }
                            }
                        } else {
                            String deductType = (String) attendanceMap.get("deductType");
                            int value = Integer.parseInt((String) attendanceMap.get("value"));
                            for (int j = 0; j < numberSize; j++) {
                                if (deductType.equals("PERCENT")) {
                                    sendMoney -= sendMoney * (value / 100);
                                } else if (deductType.equals("MONEY")) {
                                    sendMoney -= value;
                                }
                            }
                        }

                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                    msg = "保存失败";
                    return "error";
                }
            }
                if (UtilValidate.isEmpty(detailId)) {
                    detailId = delegator.getNextSeqId("TblSendDetail").toString();
                    GenericValue genericValue = delegator.makeValue("TblSendDetail", UtilMisc.toMap("detailId", detailId));
                    genericValue.setString("sendId", sendId);
                    genericValue.setString("entryId", entryId);
                    if (title.equals("应发工资")) {
                        genericValue.setString("salary", sendMoney + "");
                    } else {
                        genericValue.setString("salary", salary);
                    }
                    genericValue.setString("remarks", remarks);
                    try {
                        genericValue.create();
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                        msg = "保存失败";
                        return "error";

                    }
                } else {
                    GenericValue genericValue = delegator.findByPrimaryKey("TblSendDetail", UtilMisc.toMap("detailId", detailId));
                    genericValue.setString("sendId", sendId);
                    genericValue.setString("entryId", entryId);
                    if (title.equals("应发工资")) {
                        genericValue.setString("salary", sendMoney + "");
                    } else {
                        genericValue.setString("salary", salary);
                    }
                    genericValue.setString("remarks", remarks);
                    genericValue.store();
                }

            }
        request.setAttribute("salary_success",msg);
        return "success";
}
}
