import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityExpr
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2016/10/12.
 */

/*String time= new Date();
List<EntityCondition> conditions = [];*/

Date date = new Date();
String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
Date datetime = fmt.parse(time);
List<EntityCondition> l = [];
l.add(EntityCondition.makeCondition(EntityCondition.makeCondition("score",EntityOperator.GREATER_THAN,new java.sql.Date(datetime.getTime())),EntityOperator.OR,EntityCondition.makeCondition("score",EntityOperator.EQUALS,new java.sql.Date(datetime.getTime()))));
l.add(EntityCondition.makeCondition(EntityCondition.makeCondition("maxTimes",EntityOperator.LESS_THAN,new java.sql.Date(datetime.getTime())),EntityOperator.OR,EntityCondition.makeCondition("maxTimes",EntityOperator.EQUALS,new java.sql.Date(datetime.getTime()))));
l.add(EntityCondition.makeCondition("hasdelete",EntityOperator.NOT_EQUAL,"0"));
l.add(EntityCondition.makeCondition("lockAdvert",EntityOperator.NOT_EQUAL,"1"));
List<GenericValue> Advertlist = EntityQuery.use(delegator).from("TblAdvert").where(l).queryList();
List<Map<String, Object>> fileData = new ArrayList<>();
for(GenericValue advert:Advertlist)
{
    Map data = new HashMap();
    data.put("description",advert.get("description"));
    data.put("ruleName",advert.get("ruleName"));
    fileData.add(data);
}
request.setAttribute("data", fileData);