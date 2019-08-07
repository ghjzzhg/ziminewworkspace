package org.ofbiz.oa;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.model.*;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.ServiceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rextec-15-1 on 2016/1/28.
 */
public class BaiDuYunPush {
    public static final String NOTICE = "notice";
    public static final String MEETING_NOTICE = "meeting_notice";
    public static final String MEETING_SUMMARY = "meeting_summary";
    public static final String VEHICLE = "vehicle";
    public static final String RESOURCE = "resource";
    public static final String TASK = "task";
    public static final String NOTICE_NAME = "公文通知";
    public static final String MEETING_NOTICE_NAME = "会议通知";
    public static final String MEETING_SUMMARY_NAME = "会议纪要";
    public static final String VEHICLE_APPROVE = "车辆审批";
    public static final String RESOURCE_APPROVE = "资源审批";
    public static final String TASK_NAME = "工作流程";
    public static Map<String, Object> sendMessage(Delegator delegator, Map<String, Object> context) {
//        LocalDispatcher dispatcher = ctx.getDispatcher();
//        Delegator delegator = dispatcher.getDelegator();
        Map<String, Object> results = ServiceUtil.returnSuccess();

//        GenericValue userLogin = (GenericValue) context.get("userLogin");
//        TimeZone timeZone = (TimeZone) context.get("timeZone");
//        Locale locale = (Locale) context.get("locale");
        String title = (String) context.get("title");
        String description = (String) context.get("description");
        Map<String, Object> customMsg = (Map<String, Object>) context.get("customMsg");
        String toUser = (String) context.get("toUser");
        String toTag = (String) context.get("toTag");
        Boolean isMessage = (Boolean) context.get("isMessage");
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("title", title);
        msg.put("description", description);
        msg.put("custom_content", customMsg);

        String apiKey = UtilProperties.getPropertyValue("oa", "apiKey");
        String secretKey = UtilProperties.getPropertyValue("oa", "secretKey");

        ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClient channelClient = new BaiduChannelClient(pair);

        try {
            if (UtilValidate.isNotEmpty(toUser)) {
                GenericValue toUserEntity = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", toUser), true);
                if (toUserEntity != null) {
//                    String externalAuthId = toUserEntity.getString("externalAuthId");
//                    String[] split = externalAuthId.split("\\{\\}");

                    // 3. 若要了解交互细节，请注册YunLogHandler类
                    channelClient.setChannelLogHandler(new YunLogHandler() {
                        @Override
                        public void onHandle(YunLogEvent event) {
                            System.out.println(event.getMessage());
                        }
                    });


                    // 4. 创建请求类对象
                    // 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
                    PushUnicastMessageRequest request = new PushUnicastMessageRequest();
                    request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
                    // 4:ios 5:wp
//                    request.setChannelId(Long.parseLong("4479811498879754281"));
//                    request.setUserId("681741885145318659");
                    if (isMessage == null || !isMessage) {
                        request.setMessageType(1);//通知
                    }

                    request.setMessage(JSONObject.fromObject(msg).toString());

                    // 5. 调用pushMessage接口
                    PushUnicastMessageResponse response = channelClient
                            .pushUnicastMessage(request);

                    // 6. 认证推送成功
                    //UtilMessage.logServiceInfo("push amount : " + response.getSuccessAmount(), locale, MODULE);
                }
            } else if (UtilValidate.isNotEmpty(toTag)) {
                PushTagMessageRequest request = new PushTagMessageRequest();
                request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
                // 4:ios 5:wp
                request.setTagName(toTag);
                request.setMessage(JSONObject.fromObject(msg).toString());

                // 5. 调用pushMessage接口
                PushTagMessageResponse response = channelClient
                        .pushTagMessage(request);

                // 6. 认证推送成功
                //UtilMessage.logServiceInfo("push amount : " + response.getSuccessAmount(), locale, MODULE);
            } else {
                PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
                request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
                // 4:ios 5:wp
                request.setMessageType(1);
                request.setMessage(JSONObject.fromObject(msg).toString());

                // 5. 调用pushMessage接口
                PushBroadcastMessageResponse response = channelClient
                        .pushBroadcastMessage(request);

                // 6. 认证推送成功
                //UtilMessage.logServiceInfo("push amount : " + response.getSuccessAmount(), locale, MODULE);

            }
        } catch (Exception e) {
            return null;//UtilMessage.createAndLogServiceError(e, "发送通知失败", locale, MODULE);
        }

        return results;
    }

    //消息通知
    public static void setMessageData(String dataId, Map<String, Object> context,String backType, String messageType, Delegator delegator, String typeName, String description, String title){
        Map<String,Object> contentMap = new HashMap<String, Object>();
        contentMap.put("noticeId",dataId);
        contentMap.put("typeName",typeName);
        contentMap.put("backType",backType);
        contentMap.put("titleName",title);
        contentMap.put("messageType",messageType);

        Map<String,Object> map = new HashMap<String, Object>();
        map.putAll(context);
        map.put("customMsg",contentMap);
        map.put("title",typeName);//消息的标题
        map.put("description",description);
        BaiDuYunPush.sendMessage(delegator,map);
    }
}
