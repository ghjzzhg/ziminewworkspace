package org.ofbiz.zxdoc

import javolution.util.FastList
import net.fortuna.ical4j.model.DateTime
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

import java.sql.Timestamp

GenericValue userLogin = context.get("userLogin");
String loginId = userLogin.get("partyId");
GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId",loginId).distinct().queryOne();
if(person!=null) {
    String fullName = person.get("fullName");
    List<EntityCondition> condList = FastList.newInstance();
    condList.add(EntityCondition.makeCondition("groupIds", EntityOperator.LIKE, "%" + loginId + "%"));
    condList.add(EntityCondition.makeCondition("lastChatTime", EntityOperator.NOT_EQUAL, null));
    condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("chatRoomName", EntityOperator.LIKE, "%&" + fullName), EntityOperator.OR, EntityCondition.makeCondition("chatRoomName", EntityOperator.LIKE, fullName + "&%")));
    List<GenericValue> chatList = EntityQuery.use(delegator).from("TblCaseCooperationRecord").where(condList).orderBy("lastUpdatedStamp").queryList();
    List<GenericValue> personList = new ArrayList<>();
    List<GenericValue> top10 = new ArrayList<>();
    for (GenericValue chat : chatList) {
        String partyIds = chat.get("groupIds");
        List party = partyIds.split(",");
        for (String partyId : party) {
            partyId = partyId.substring(0, 1) == " " ? partyId.substring(0, partyId.length()) : partyId;
            if (partyId != loginId) {
                Map map = new HashMap();
                GenericValue personN = EntityQuery.use(delegator).from("FullNameGroupName").where("partyId", partyId).distinct().queryOne();
                map.put("partyId", personN.get("partyId"));
                map.put("groupName", personN.get("groupName"));
                map.put("fullName", personN.get("fullName"));
                map.put("lastChatTime", chat.get("lastChatTime"));
                map.put("openFireJid", personN.get("openFireJid"));
                personList.add(map);
            }
        }
    }
    sort(personList);
//获取前十个数据
    for (int i = 0; i < personList.size(); i++) {
        if (i < 10) {
            top10.add(personList.get(i));
        }
    }
//UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("instantMembers").where(EntityCondition.makeCondition(condList)), parameters);
    if (personList != null) {
        request.setAttribute("draw", top10.size());
        request.setAttribute("recordsTotal", top10.size());
        request.setAttribute("recordsFiltered", top10.size());
        request.setAttribute("data", top10);
    }
}

//按照时间进行倒序
    public void sort(List<Map<String, String>> data) {
        Collections.sort(data, new Comparator<Map>() {
            public int compare(Map o1, Map o2) {
                String a = (String) o1.get("lastChatTime");
                String b = (String) o2.get("lastChatTime");
                // 升序
                //return a.compareTo(b);
                // 降序
                return b.compareTo(a);
            }
        });
    }
