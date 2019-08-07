package org.activiti.rest.servlet;

import org.ofbiz.entity.GenericValue;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by galaxypan on 2015/9/1.
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private GenericValue userLogin;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    }

    public GenericValue getUserLogin() {
        return userLogin;
    }
}
