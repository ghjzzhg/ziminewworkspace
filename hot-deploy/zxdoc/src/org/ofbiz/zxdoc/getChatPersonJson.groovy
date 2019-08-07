package org.ofbiz.zxdoc

/**
 * Created by Administrator on 2016/12/19.
 */
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
String personName = parameters.get("personName");
String groupName = parameters.get("groupName");
List criteria = new LinkedList();
List<EntityCondition> condList = FastList.newInstance();
if (UtilValidate.isNotEmpty(personName)) {
    condList.add(EntityCondition.makeCondition("fullName", EntityOperator.LIKE, "%" + personName + "%"));
}
if (UtilValidate.isNotEmpty(groupName)) {
    condList.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + groupName + "%"));
}
condList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,loginId));
condList.add(EntityCondition.makeCondition("openFireJid",EntityOperator.NOT_EQUAL,null));
condList.add(EntityCondition.makeCondition("enabled",EntityOperator.EQUALS,"Y"));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("instantMembers").where(EntityCondition.makeCondition(condList)), parameters);
if(result!=null) {
    request.setAttribute("draw", result.draw);
    request.setAttribute("recordsTotal", result.recordsTotal);
    request.setAttribute("recordsFiltered", result.recordsFiltered);
    request.setAttribute("data", result.data);
}