package org.ofbiz.zxdoc;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.tomcat.websocket.WsSession;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.JSONConverters;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by galaxypan on 16.8.30.
 */
@ServerEndpoint(value = "/ws/msg", configurator = ServletAwareConfig.class)
public class WsServices {

    private static String module = WsServices.class.getName();
    public static Map<String, Set<Session>> clients = new ConcurrentHashMap<>();
    private EndpointConfig config;
    private static LocalDispatcher localDispatcher;
    private static Delegator delegator;


    @OnMessage
    public void onMessage(Session session, String data, boolean last) {
        try {
            Map<String, Object> result = new JSONConverters.JSONToMap().convert(JSON.from(data));
            String target = (String) result.get("target");
            if(UtilValidate.isNotEmpty(target)){
                Set<Session> targetSessions = clients.get(target);
                if(targetSessions != null){
                    for (Session targetSession : targetSessions) {
                        if (targetSession.isOpen()) {
                            targetSession.getBasicRemote().sendText((String) result.get("msg"));
                        }
                    }
                }
            }
        } catch (ConversionException e) {
            Debug.logError(e, module);
        } catch (IOException e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.config = config;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        GenericValue userLogin = (GenericValue) httpSession.getAttribute("userLogin");
        // Add session to the connected sessions clients set
        if(userLogin != null){
            String userLoginId = userLogin.getString("userLoginId");
            Set<Session> oldSessions = clients.get(userLoginId);
            if(oldSessions != null){
                String sessionId = ((WsSession)session).getHttpSessionId();
                for (Session oldSession : oldSessions) {
                    String oldSessionId = ((WsSession)oldSession).getHttpSessionId();
                    if(oldSession.isOpen() && !oldSessionId.equals(sessionId)){
                        //不同的sessionid登录向对方发送消息
                        try {
                            oldSession.getBasicRemote().sendText("kickedByOtherSession");
                        } catch (IOException e) {
                            Debug.logError(e, module);
                        }finally{
                            try {
                                oldSession.close();
                            } catch (IOException e) {
                                Debug.logError(e, module);
                            }
                        }
                    }
                }
            }else{
                oldSessions = new HashSet<>();
                clients.put(userLoginId, oldSessions);
            }
            oldSessions.add(session);

            //TODO:获取客户持久化的信息批量发送
            /*if(delegator == null){
                synchronized (NoticeServices.class){
                    if(delegator == null){
                        delegator = DelegatorFactory.getDelegator(null);
                        localDispatcher = ContextFilter.makeWebappDispatcher(httpSession.getServletContext(), delegator);
                    }
                }
            }
            try {
                localDispatcher.runAsync("sendSiteMsg", UtilMisc.toMap("userLoginId", userLoginId, "message", "持久化信息"));
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }*/
        }
    }

    @OnClose
    public void onClose(Session session) {
        // Remove session from the connected sessions clients set
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        try {
            GenericValue userLogin = (GenericValue) httpSession.getAttribute("userLogin");
            if (userLogin != null) {
                String userLoginId = userLogin.getString("userLoginId");
                Set<Session> oldSessions = clients.get(userLoginId);
                if (oldSessions != null) {
                    String sessionId = ((WsSession) session).getHttpSessionId();
                    for (Session oldSession : oldSessions) {
                        String oldSessionId = ((WsSession) oldSession).getHttpSessionId();
                        if (oldSessionId.equals(sessionId)) {
                            oldSessions.remove(oldSession);
                            break;
                        }
                    }
                }
            }
        }catch (IllegalStateException e){
            //session 已过期
            Debug.logInfo("session 已过期", module);
        }
    }
}
