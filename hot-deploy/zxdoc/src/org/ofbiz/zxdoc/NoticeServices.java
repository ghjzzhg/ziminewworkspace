package org.ofbiz.zxdoc;

import org.apache.tomcat.websocket.WsSession;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.JSONConverters;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.*;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.ofbiz.webapp.control.ContextFilter;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;


/**
 * Created by galaxypan on 16.8.30.
 */
public class NoticeServices{

    private static String module = NoticeServices.class.getName();
    private EndpointConfig config;
    private static LocalDispatcher localDispatcher;
    private static Delegator delegator;

    public static Map<String, Object> sendEmailNotice(DispatchContext ctx, Map context) {
        Map<String, Object> results = ServiceUtil.returnSuccess();
        String module = "NoticeServices";
        String templateId = (String) context.get("templateId");
        String toAddress = (String) context.get("toAddress");
        String partyIdTo = (String) context.get("partyIdTo");
        Map bodyParameters = (Map) context.get("bodyParameters");
        List<String> dataResourceIds = (List<String>) context.get("dataResourceIds");

        GenericValue emailTemplateSetting = null;
        try {
            emailTemplateSetting = EntityQuery.use(ctx.getDelegator()).from("EmailTemplateSetting").where("emailTemplateSettingId", templateId).cache().queryOne();
        } catch (GenericEntityException e1) {
            Debug.logError(e1, module);
        }
        if (emailTemplateSetting != null) {
            Map<String, Object> emailCtx = UtilMisc.toMap("userLogin", context.get("userLogin"), "emailTemplateSettingId", templateId, "sendTo", toAddress, "partyIdTo", partyIdTo, "bodyParameters", bodyParameters);
//        Map<String, Object> emailCtx = UtilMisc.toMap("login.username", "demooa", "login.password", "demooa", "emailTemplateSettingId", templateId, "sendTo", toAddress, "partyIdTo", partyIdTo, "bodyParameters", bodyParameters);
            if(UtilValidate.isNotEmpty(dataResourceIds)){
                emailCtx.put("bodyParts", DataServices.getDataResourceAsEmailBodyParts((GenericDelegator) ctx.getDelegator(), dataResourceIds));
            }
            try {
                ctx.getDispatcher().runAsync("sendMailFromTemplateSetting", emailCtx);
            } catch (Exception e) {
                Debug.logWarning("发送邮件失败 " + e, module);
            }
        }
        return results;
    }

    public static Map<String, Object> sendVerificationCode(DispatchContext ctx, Map context) throws GenericEntityException, GenericServiceException {
        Map<String, Object> results = ServiceUtil.returnSuccess();
        Map<String, Object> map = new HashMap<>();
        String phoneNum = context.get("phoneNum").toString();
        String num = "";
        Timestamp now = new Timestamp((new Date()).getTime());
        Delegator delegator = ctx.getDelegator();
        GenericValue oldCode = EntityQuery.use(delegator).from("TblPhoneCode").where("phoneNum", phoneNum).orderBy("-createCodeDate").queryFirst();
        if(oldCode != null){
            Timestamp createCodeDate = oldCode.getTimestamp("createCodeDate");
            if((now.getTime() - createCodeDate.getTime())/1000 > 60){
                //超过一分钟重新获取
                oldCode.remove();
            }else{
                num = oldCode.getString("code");
            }
        }
        if(UtilValidate.isEmpty(num)){
            num = String.valueOf((int)(Math.random()*9000+1000));
            String id = delegator.getNextSeqId("TblPhoneCode");
            map.put("id", id);
            map.put("phoneNum", phoneNum);
            map.put("code", num);
            map.put("createCodeDate", now);
            GenericValue phoneCode = delegator.makeValidValue("TblPhoneCode", map);
            phoneCode.create();
        }

        results.put("phoneNum", phoneNum);
        results.put("phoneCode", num);
//changed by galaxypan@2017-10-21:通过jms异步方式实现web服务器发送短信
        ctx.getDispatcher().runSync("sendVerificationCodePhoneJms", UtilMisc.toMap("phoneNum", phoneNum,"phoneCode", num));
        return results;
    }

    public static Map<String, Object> sendVerificationCodePhone(DispatchContext ctx, Map context){
        Map<String, Object> results = ServiceUtil.returnSuccess();

        //配置是否发送验证码
        String smsDisable = UtilProperties.getPropertyValue("general.properties", "sms.disable");
        if(Boolean.parseBoolean(smsDisable)){
            return results;
        }
        Delegator delegator = ctx.getDelegator();
        PrintWriter out = null;
        BufferedReader ins = null;
        String phoneNum = context.get("phoneNum").toString();
        String num = context.get("phoneCode").toString();

        HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        GetMethod method = new GetMethod();
        try {
            long start = System.currentTimeMillis();
            URI base = new URI("http://sapi.253.com/msg/HttpBatchSendSM", false);
            method.setURI(new URI(base, "HttpBatchSendSM", false));
            method.setQueryString(new NameValuePair[] {
                    new NameValuePair("account", "VIPmaige88"),
                    new NameValuePair("pswd", "W4c6XgI8p"),
                    new NameValuePair("mobile", phoneNum),
                    new NameValuePair("needstatus", "true"),
                    new NameValuePair("msg", "您的注册验证码是：" + num + ".请完成注册"),
                    new NameValuePair("extno", null),
            });
            int result = client.executeMethod(method);
            if (result == HttpStatus.SC_OK) {
                InputStream in = method.getResponseBodyAsStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                URLDecoder.decode(baos.toString(), "UTF-8");
            } else {
                throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
            }
            long time = (System.currentTimeMillis() - start);
            Debug.logError("验证码发送耗时:" + time, "sendVerificationCodePhone");
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return results;
    }

    public static Map<String, Object> sendMessage(DispatchContext dctx, Map context) {
        String userLoginId = (String) context.get("userLoginId");
        String message = (String) context.get("message");
        try {
            Set<Session> targetSessions = WsServices.clients.get(userLoginId);
            if(targetSessions != null){
                for (Session client : targetSessions) {
                    if (client.isOpen()) {
                        client.getBasicRemote().sendText(message);
                    }
                }
            }
            //TODO:持久化
        } catch (IOException e) {
            Debug.logError(e.getMessage(), module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> readSiteMsg(DispatchContext dctx, Map context) {
        String userLoginId = (String) context.get("userLoginId");
        String id = (String) context.get("id");
        Delegator delegator = dctx.getDelegator();
        try {
            GenericValue msg = EntityQuery.use(delegator).from("TblSiteMsg").where("id", id).queryOne();
            msg.set("read", "Y");
            msg.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> createSiteMsg(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        try {
            String id = delegator.getNextSeqId("TblSiteMsg");
            context.put("id", id);
            context.put("read", "N");
            GenericValue siteMsg = delegator.makeValidValue("TblSiteMsg", context);
            siteMsg.create();
            String partyId = (String) context.get("partyId");
            String groupPartyId = (String) context.get("groupPartyId");
            if(UtilValidate.isNotEmpty(partyId)){
                GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId", partyId).queryFirst();
                if(userLogin != null){
                    String message = JSON.from(siteMsg).toString();
                    dctx.getDispatcher().runSync("sendSiteMsg", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"), "message", message));
                }
            }else{//群组消息 TODO：

            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }catch (Exception e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }
}
