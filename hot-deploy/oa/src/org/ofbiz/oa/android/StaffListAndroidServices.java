package org.ofbiz.oa.android;

import javolution.util.FastMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Created by rextec-15-1 on 2016/2/18.
 */
public class StaffListAndroidServices {
    public static final String module = StaffListAndroidServices.class.getName();
    
    /*
    *Author 邓贝贝
    *created 2016/2/18 15:54
    *function 企业内部联系人
    */

    public static String getStaffList(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);

        if("success".equals(message)){
            try {
                GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
                String department = request.getParameter("departmentId");

                List<GenericValue> circulateGroups = EntityQuery.use(delegator).select().from("StaffListDepartment").where("partyIdFrom", department, "partyRelationshipTypeId", "GROUP_ROLLUP","roleTypeIdFrom","_NA_","roleTypeIdTo","_NA_").cache().filterByDate().queryList();
                List<GenericValue> entityList = EntityQuery.use(delegator).select().from("PersonByGroupId").where(UtilMisc.toMap("department",department)).queryList();
                circulateGroups.addAll(entityList);
                returnJson(request,response,circulateGroups);
            } catch (Exception e) {
                return "error";
            }
        }
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
