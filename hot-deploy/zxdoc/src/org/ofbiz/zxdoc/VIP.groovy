package org.ofbiz.zxdoc

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2016/10/28.
 */



public Map VIPDue()
{
    String dueTime = 0;
    Map map = new HashMap();
    GenericValue userLogin = context.get("userLogin");
    String createdStamp = userLogin.get("createdStamp");
    //查看该账号为主账号还是子账号
    String type = EntityQuery.use(delegator).from("Party").select("partyTypeId").where(UtilMisc.toMap("partyId",userLogin.get("partyId"))).cache().queryOne().get("partyTypeId");
    String partyId = userLogin.get("partyId");
    //子账户,需要获取主账户信息
    if(type.equals("PERSON") && !partyId.equals("admin") )
    {
        GenericValue relation = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdTo",partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT")).cache().queryOne();
        partyId = relation.get("partyIdFrom");
        userLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId",partyId)).cache().queryOne();
    }
    //检查会员到期表中是否存在数据
    GenericValue vipCue = EntityQuery.use(delegator).from("TblVipCueTime").where(UtilMisc.toMap("partyId",partyId)).cache().queryOne();
    //如果为空，说明该用户没有续费记录，则调取试用期剩余时间；反之说明该用户存在续费记录
    if(vipCue == null)
    {
        createdStamp = userLogin.get("createdStamp");
        //创建时间
        String start = createdStamp.substring(0,10);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        //当前时间
        String now = df.format(new Date());
        Date nowDate = df.parse(now);
        Date startDate = df.parse(start);
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);
        long time2 = cal.getTimeInMillis();
        cal.setTime(startDate);
        long time1 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        int time = Integer.parseInt(String.valueOf(between_days));
        if(time < 180)
        {
            dueTime = (180 - time).toString();
        }
        map.put("vipType","试用期");
    }else
    {
        createdStamp = vipCue.get("CueTime");
        //到期时间
        String end = createdStamp.substring(0,10);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        //当前时间
        String now = df.format(new Date());
        Date nowDate = df.parse(now);
        Date endDate = df.parse(end);
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(endDate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        int time = Integer.parseInt(String.valueOf(between_days));
        dueTime = time.toString();
        if(time<0)
        {
            dueTime = "0";
        }
        map.put("vipType","会员试用期");
    }

    //管理员账户
    if(partyId.equals("admin"))
    {
        map.put("vipType","使用时间");
        dueTime = "永久";
    }
    map.put("data",dueTime);
    return map;
}

public Map getMessageList(){
    Map success = new HashMap();
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.getString("partyId")
    List list = new ArrayList();
    list.add(EntityCondition.makeCondition("partyIds", EntityOperator.LIKE, "%" + partyId + "%"));
    String roomIds = context.get("roomIds");
    if(UtilValidate.isNotEmpty(roomIds)){
        String[] roomIdList = roomIds.split(",");
        list.add(EntityCondition.makeCondition("chatRoomId", EntityOperator.NOT_IN, UtilMisc.toListArray(roomIdList)));
    }
    List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblCaseCooperationRecord").where(list).queryList();
    List<Map<String, Object>> mapList =new ArrayList<>();
    if(UtilValidate.isNotEmpty(genericValueList)){
        for(GenericValue genericValue : genericValueList){
            String username = "";
            if(genericValue.get("caseId").equals("00000")){
                String groupIds = genericValue.get("groupIds");
                String[] groupIdList = groupIds.split(",");
                for(String personId : groupIdList){
                    if(!personId.equals(partyId)){
                        GenericValue person = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId", personId)).queryOne();
                        username = person.get("fullName");
                        break;
                    }
                }
            }else{
                GenericValue caseInfo = EntityQuery.use(delegator).select().from("TblCase").where(UtilMisc.toMap("caseId", genericValue.get("caseId"))).queryOne();
                username = caseInfo.get("title");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("chatRoomId", genericValue.get("chatRoomId"));
            map.put("lastChatTime", genericValue.get("lastChatTime"));
            mapList.add(map);
        }
    }
    success.put("roomList", mapList);

    List<GenericValue> noticeList = EntityQuery.use(delegator).from("TblSiteMsg").where("partyId", partyId, "read", "N", "type", "notice").queryList();

    success.put("noticeList", noticeList);
    return success;
}

public Map remindInfo(){
    Map map = new HashMap();
    String roomIds = context.get("roomIds");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.getString("partyId");
    if(UtilValidate.isNotEmpty(roomIds)){
        String[] ids = roomIds.split(",");
        for(String jid : ids){
            List list = new ArrayList();
            list.add(EntityCondition.makeCondition("chatRoomId", EntityOperator.EQUALS, jid));
            GenericValue chat = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where(list).distinct().queryOne();
            if(UtilValidate.isNotEmpty(chat)){
                String partyIds = chat.get("partyIds");
                if(UtilValidate.isNotEmpty(chat.get("partyIds"))){
                    String partyList = chat.getString("partyIds");
                    if(partyList.indexOf(partyId) < 0){
                        partyIds = chat.get("partyIds") + "," + partyId;
                    }
                }else{
                    partyIds = partyId;
                }
                if(!partyIds.equals(chat.get("partyIds"))){
                    chat.set("partyIds", partyIds);
                    chat.store();
                }
            }
        }
    }
    return map;
}