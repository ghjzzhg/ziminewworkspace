package org.ofbiz.zxdoc;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static org.ofbiz.base.lang.JSON.from;
import static org.ofbiz.webapp.event.CoreEvents.runService;

/**
 * Created by Administrator on 2016/11/1.
 */
public class EmailDaily extends NoticeServices{

    public Map<String, Object> myScheduleMethod(DispatchContext dctx, Map<String, ? extends Object> context) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String now = format.format(date);
            Date nowTime = sdf.parse(now);
            Delegator delegator = dctx.getDelegator();
            List<GenericValue> users = EntityQuery.use(delegator).from("UserLoginVipCueTime").where(UtilMisc.toMap("partyTypeId", "PARTY_GROUP")).cache().queryList();
            //临期账户，需要邮件通知
            List<GenericValue> needEmail = new ArrayList<GenericValue>();
            //到期账户，需要邮件通知
            List<GenericValue> needMoney = new ArrayList<GenericValue>();
            Date remain = null;
            for (GenericValue user : users) {
                Date cueTime = (java.sql.Date) user.get("CueTime");
                //如果没有续费记录，说明还在试用期
                if (cueTime == null) {
                    //创建日期
                    Date createdDate = (Date) user.get("createdDate");
                    String created = sdf.format(createdDate);
                    //当前时间和试用期到期时间比较
                    Calendar c = Calendar.getInstance();//获得一个日历的实例
                    Date datetime = null;
                    datetime = sdf.parse(created);//初始日期
                    c.setTime(datetime);//设置日历时间
                    c.add(Calendar.MONTH, 6);
                    //试用期到期时间
                    String remainTime = sdf.format(c.getTime());
                    remain = sdf.parse(remainTime);
                }
                //如果有续费记录，直接比较
                else {
                    //获取到期时间
                    remain = (java.sql.Date) user.get("CueTime");
                    String remainTime = sdf.format(remain);
                }
                //是否已经通知,已经通知的说明已经修改状态，不需要做处理
                String info = (String)user.get("hasInfo");
                if(info != null && !info.equals("1")) {
                    //如果试用期到期时间小于当前时间，则说明已经过期，直接修改状态
                    if (remain.getTime() < nowTime.getTime()) {
                        String userId = (String) user.get("partyId");
                        //修改主账号账号
                        GenericValue party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", userId)).queryOne();
                        party.put("statusId", "PARTY_ENABLED");
                        party.store();
                        GenericValue userlogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", userId)).queryOne();
                        userlogin.put("enabled","N");
                        userlogin.store();
                        //修改子账户状态
                        List<GenericValue> partyIds = EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom", userId)).queryList();
                        for (int i = 0; i < partyIds.size();i++)
                        {
                            userId = (String)partyIds.get(i).get("partyIdTo");
                            party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", userId)).queryOne();
                            if(party!=null) {
                                party.put("statusId", "PARTY_ENABLED");
                                party.store();
                            }
                            userlogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", userId)).queryOne();
                            if(userlogin != null) {
                                userlogin.put("enabled", "N");
                                userlogin.store();
                            }
                        }
                        needMoney.add(user);
                    }
                    //没有过期的话，需要获取剩余几天过期，如果剩余天数小于一个月，邮件通知
                    else {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(nowTime);
                        long time1 = cal.getTimeInMillis();
                        cal.setTime(remain);
                        long time2 = cal.getTimeInMillis();
                        long between_days = (time2 - time1) / (1000 * 3600 * 24);
                        int days = Integer.parseInt(String.valueOf(between_days));
                        if (days == 7) {
                            needEmail.add(user);
                        }
                    }
                }
            }
            //发出邮件通知


            //发送邮件给临期用户
            if(needEmail != null && needEmail.size() != 0) {
                List<String>  hadCue = new ArrayList<String>();
                for (int i = 0; i < needMoney.size() ; i++)
                {
                    hadCue.add((String) needMoney.get(i).get("partyId"));
                }
                List<GenericValue> emails = EntityQuery.use(delegator).from("PartyNameContactMechView").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, hadCue), EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")).queryList();
                List<String> emailAddresses = new ArrayList<String>();
                for (GenericValue email : emails) {
                    emailAddresses.add(email.getString("infoString"));
                }
                Map map = UtilMisc.toMap("bodyParameters", EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_TIME_APPROACHING ", "toAddress", StringUtil.join(emailAddresses, ","), "dataResourceIds", null);
                sendEmailNotice(dctx, map);
            }
            //发送邮件给到期用户
            if(needMoney != null && needMoney.size() != 0) {
                List<String>  hadCue = new ArrayList<String>();
                for (int i = 0; i < needMoney.size() ; i++)
                {
                    hadCue.add((String) needMoney.get(i).get("partyId"));
                }
                List<GenericValue> emailss = EntityQuery.use(delegator).from("PartyNameContactMechView").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, hadCue), EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"),EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)).queryList();
                List<String> emailAddressess = new ArrayList<String>();
                for (GenericValue email : emailss) {
                    emailAddressess.add(email.getString("infoString"));
                }
                Map maps = UtilMisc.toMap("userLogin", EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_HAD_CURTIME ", "toAddress", StringUtil.join(emailAddressess, ","), "dataResourceIds", null);
                sendEmailNotice(dctx, maps);
                //已经通知的修改备注
                for (int i = 0; i < needMoney.size() ; i++)
                {
                    String id = (String) needMoney.get(i).get("partyId");
                    GenericValue vip = EntityQuery.use(delegator).from("TblVipCueTime").where(UtilMisc.toMap("partyId",id)).queryOne();
                    if(vip != null)
                    {
                        vip.put("hasInfo","1");
                        vip.store();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ServiceUtil.returnSuccess();
    }
}
