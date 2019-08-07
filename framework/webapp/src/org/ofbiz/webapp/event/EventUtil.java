package org.ofbiz.webapp.event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by galaxypan on 15.10.10.
 */
public class EventUtil {

    public static final String EVENT_RETURN_SUCCESS = "success";
    public static final String EVENT_RETURN_FAILURE = "failure";
    public static final String EVENT_RETURN_ERROR = "error";

    public static String returnSuccess(HttpServletRequest request){
        return EVENT_RETURN_SUCCESS;
    }

    public static String returnSuccess(HttpServletRequest request, String message){
        request.setAttribute("_EVENT_MESSAGE_", message);
        return EVENT_RETURN_SUCCESS;
    }

    public static String returnSuccess(HttpServletRequest request, List<String> message){
        request.setAttribute("_EVENT_MESSAGE_LIST_", message);
        return EVENT_RETURN_SUCCESS;
    }

    public static String returnError(HttpServletRequest request, String message){
        request.setAttribute("_ERROR_MESSAGE_", message);
        return EVENT_RETURN_ERROR;
    }

    public static String returnError(HttpServletRequest request, List<String> message){
        request.setAttribute("_ERROR_MESSAGE_LIST_", message);
        return EVENT_RETURN_ERROR;
    }
}
