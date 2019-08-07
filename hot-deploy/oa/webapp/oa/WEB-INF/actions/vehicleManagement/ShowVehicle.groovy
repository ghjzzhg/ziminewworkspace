import javolution.util.FastList
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

import java.text.ParseException
import java.text.SimpleDateFormat

String year,month;
Calendar cal = Calendar.getInstance();
costType=parameters.get("costType");
year = parameters.get("year");
month = parameters.get("month");
vehicleId=parameters.get("vehicleId");
/*costType=parameters.get("costType");*/
java.sql.Date startDate,endDate;
for (int i=1;i<=31;i++){
    date = year + "-" +month+"-"+ i;
    cal.setTime(new Date(getDateByStr2(date).getTime()));
    if (i==1){
        dateString =  getStringDateByDate(getDateByStr2(date));
        startDate = java.sql.Date.valueOf(dateString);
    }
    if(i == cal.getActualMaximum(Calendar.DAY_OF_MONTH)){
        dateString =  getStringDateByDate(getDateByStr2(date));
        endDate = java.sql.Date.valueOf(dateString);
        break;
    }
}
List criteria =  new ArrayList();
criteria.add(startDate)
criteria.add(endDate)
List<EntityCondition> conditionList = FastList.newInstance();
EntityCondition condition = EntityCondition.makeCondition("happenDate",EntityOperator.BETWEEN,criteria);
conditionList.add(condition);
EntityCondition condition2 = EntityCondition.makeCondition("vehicleId",vehicleId);
conditionList.add(condition2);
if (UtilValidate.isNotEmpty(costType)){
    EntityCondition condition3 = EntityCondition.makeCondition("costType",costType);
    conditionList.add(condition3);
}
sumCostList = EntityQuery.use(delegator).from("TblVehicleCost").where(conditionList).queryList();


context.vehicleForDetailsList=sumCostList;

public static Date getDateByStr2(String dd)
{
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    Date date;
    try {
        date = sd.parse(dd);
    } catch (ParseException e) {
        date = null;
        e.printStackTrace();
    }
    return date;
}
public static String getStringDateByDate(Date date)
{
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    String dateString;
    try {
        dateString = sd.format(date);
    } catch (ParseException e) {
        dateString = "";
        e.printStackTrace();
    }
    return dateString;
}