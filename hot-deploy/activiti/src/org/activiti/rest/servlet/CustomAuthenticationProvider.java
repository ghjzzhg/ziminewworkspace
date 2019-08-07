package org.activiti.rest.servlet;

import org.ofbiz.entity.GenericValue;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Created by galaxypan on 2015/9/1.
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    public CustomAuthenticationProvider() {

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WebAuthenticationDetails webWebAuthenticationDetails = (WebAuthenticationDetails)authentication.getDetails();

        if (! (webWebAuthenticationDetails instanceof CustomWebAuthenticationDetails)){
            // ++++++++++++++++++++++++
            // BASIC authentication....
            // ++++++++++++++++++++++++
            UsernamePasswordAuthenticationToken emptyToken = new UsernamePasswordAuthenticationToken(null, null);
            emptyToken.setDetails(null);
            return emptyToken; //return null works, too.
        }

        // ++++++++++++++++++++++++
        // Custom authentication....
        // ++++++++++++++++++++++++
        CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails)webWebAuthenticationDetails;
//        Cookie ssoTokenCookie = ssoTokenWebAuthenticationDetails.getSsoTokenCookie();
        GenericValue userLogin = details.getUserLogin();
        // check if SSO cookie is available
        if (userLogin == null){
            return new UsernamePasswordAuthenticationToken(null, null); //do basic auth.
        }
        String username = userLogin.getString("userLoginId");

        // Create new Authentication object. Name and password can be null (but you can set the values of course).
        // Be careful with your role names!
        // In WebSecurityConfig the role "USER" is automatically prefixed with String "ROLE_", so it is "ROLE_USER" in the end.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(null, null, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_RESTUSER"));
        authRequest.setDetails(details);

        // Don't let spring decide.. you already have made the right decisions. Tell spring you have an authenticated user.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}