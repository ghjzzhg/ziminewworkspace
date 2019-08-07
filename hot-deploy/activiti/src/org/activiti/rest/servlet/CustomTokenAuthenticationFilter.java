package org.activiti.rest.servlet;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by galaxypan on 2015/9/1.
 */
public class CustomTokenAuthenticationFilter extends GenericFilterBean {
    private AuthenticationManager authenticationManager;

    public CustomTokenAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // check if SSO token is available. If not, pass down to next filter in chain
        try {
            /*Cookie[] cookies = httpRequest.getCookies();
            if (cookies == null){
                chain.doFilter(request, response);
                return;
            }
            Cookie ssoCookie = null;
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("ssoToken"))
                    ssoCookie = cookies[i];
            }
            if (ssoCookie == null){
                chain.doFilter(request, response);
                return;
            }*/

            // SSO token found, now authenticate and afterwards pass down to next filter in chain
            authenticate(httpRequest);
            logger.debug("now the AuthenticationFilter passes down to next filter in chain");
            chain.doFilter(request, response);
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            SecurityContextHolder.clearContext();
            logger.error("Internal authentication service exception", internalAuthenticationServiceException);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        }
    }

    private void authenticate(HttpServletRequest request) throws IOException {
//        System.out.println("+++ authenticateWithSSOToken +++");
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(null, null, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_RESTUSER"));
        authRequest.setDetails(new CustomWebAuthenticationDetails(request));
//        authRequest.setDetails(ssoTokenAuthenticationDetailsSource.buildDetails(request));

        // Delegate authentication to SsoTokenAuthenticationProvider, he will call the SsoTokenAuthenticationProvider <-- because of the configuration in WebSecurityConfig.java
        Authentication authResult = authenticationManager.authenticate(authRequest);
    }
}