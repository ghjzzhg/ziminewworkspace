package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2016/12/21.
 */
String jid = parameters.jid;
Date date=new Date();
DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String time = format.format(date);
GenericValue chat = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where("chatRoomId",jid).distinct().queryOne();
if(chat!=null)
{
    chat.put("lastChatTime",time);
    chat.store();
}
request.setAttribute("data","更新成功");