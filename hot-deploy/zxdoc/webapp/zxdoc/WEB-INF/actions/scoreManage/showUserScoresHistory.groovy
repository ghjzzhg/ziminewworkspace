package scoreManage

/**
 * Created by Administrator on 2016/12/1.
 */

import javolution.util.FastList
import net.fortuna.ical4j.model.DateTime
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Created by jiever on 2016/8/29.
 */
String userLoginId = parameters.partyId;
String type = parameters.type;
List<EntityCondition> condList = FastList.newInstance();
condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
if(UtilValidate.isNotEmpty(type))
{
    condList.add(EntityCondition.makeCondition("rule", EntityOperator.EQUALS, type));
}
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("ScoreHistoryParty").where(EntityCondition.makeCondition(condList)), parameters);
if(result!=null) {
    request.setAttribute("draw", result.draw);
    request.setAttribute("recordsTotal", result.recordsTotal);
    request.setAttribute("recordsFiltered", result.recordsFiltered);
    request.setAttribute("data", result.data);
}
