import javolution.util.FastList
import net.fortuna.ical4j.model.DateTime
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

import java.sql.Timestamp

/**
 * Created by jiever on 2016/8/29.
 */
String userName = parameters.get("userName");
String account = parameters.get("account");
String startTime = parameters.get("startTime");
String endTime = parameters.get("endTime");
//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

String scoreRule = parameters.get("scoreRule");
List criteria = new LinkedList();
List<EntityCondition> condList = FastList.newInstance();
if (UtilValidate.isNotEmpty(userName)) {
    EntityCondition cond1 = EntityCondition.makeCondition("userName", EntityOperator.LIKE, "%" + userName + "%");
    EntityCondition cond2 = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + userName + "%");
    condList.add(EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2), EntityOperator.OR));
}
if (UtilValidate.isNotEmpty(account)) {
    //修改：目前的userLoginId实际为partyID，导致该字段的查询有问题 0107
    List<GenericValue> logins = EntityQuery.use(delegator).from("UserLogin").where(EntityCondition.makeCondition("userLoginId",EntityOperator.LIKE,"%"+account+"%")).queryList();
    List ids = new ArrayList();
    if(logins!=null)
    {
        for (GenericValue login : logins) {
            String id = login.get("partyId");
            ids.add(id);
        }
        condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, ids));
    }else{
        condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, account));
    }
}
if(UtilValidate.isNotEmpty(startTime)){
    condList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(UtilDateTime.toSqlDate(startTime, "yyyy-MM-dd").getTime())));
//    condition = EntityCondition.makeCondition("planetId", EntityOperator.BETWEEN, UtilMisc.toList("1", "10"));
}
if (UtilValidate.isNotEmpty(endTime)){
    condList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(UtilDateTime.toSqlDate(endTime, "yyyy-MM-dd").getTime())));
}
if (UtilValidate.isNotEmpty(scoreRule)) {
    condList.add(EntityCondition.makeCondition("rule", EntityOperator.EQUALS, scoreRule));
}

UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("ScoreHistoryParty").where(EntityCondition.makeCondition(condList)), parameters);
for (GenericValue user:result.data)
{
    String partyId = user.get("userLoginId");
    GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId",partyId).queryOne();
    String type = party.get("partyTypeId");
    if(type.equals("PARTY_GROUP"))
    {
        String fullName = EntityQuery.use(delegator).from("PartyGroup").where("partyId",partyId).queryOne().get("groupName");
        user.put("userName",fullName);
    }
    //在积分的表机构设计中，userLoginId字段实际上是partyId字段，这里的改动是修复页面上显示错误的bug
    GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId",partyId).queryOne();
    if(userLogin!=null)
    {
        user.put("userLoginId",userLogin.get("userLoginId"));
    }else
    {
        user.put("userLoginId","");
    }
}
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", result.data);