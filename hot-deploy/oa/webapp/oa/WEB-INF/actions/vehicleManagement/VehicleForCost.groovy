import freemarker.template.utility.DateUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.jdbc.DatabaseUtil

import java.text.ParseException
import java.text.SimpleDateFormat
List dateList = new ArrayList();
String year,month;
Calendar cal = Calendar.getInstance();
dateForPost = parameters.get("date");
if (UtilValidate.isNotEmpty(dateForPost)){
   year = dateForPost.split("-")[0];
   month = dateForPost.split("-")[1];
}else {
    year = cal.get(Calendar.YEAR).toString();
    month = (cal.get(Calendar.MONTH )+1).toString();
}
java.sql.Date startDate,endDate;
for (int i=1;i<=31;i++){
    date = year + "-" +month+"-"+ i;
    cal.setTime(new Date(getDateByStr2(date).getTime()));
    if (i==1){
        dateString =  getStringDateByDate(getDateByStr2(date));
        startDate = java.sql.Date.valueOf(dateString);
    }
    switch (cal.get(Calendar.DAY_OF_WEEK)){
        case 1:dateList.add("日");break;
        case 2:dateList.add("一");break;
        case 3:dateList.add("二");break;
        case 4:dateList.add("三");break;
        case 5:dateList.add("四");break;
        case 6:dateList.add("五");break;
        case 7:dateList.add("六");break;
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
EntityCondition condition = EntityCondition.makeCondition("happenDate",EntityOperator.BETWEEN,criteria);
sumCostList = delegator.findList("TblVehicleCostDetail",condition,null,null,null,false);
List criteriaFor = new ArrayList();
criteriaFor.add(java.sql.Date.valueOf(year + "-01-01"))
criteriaFor.add(java.sql.Date.valueOf(year + "-12-31"))
EntityCondition conditionFor = EntityCondition.makeCondition("happenDate",EntityOperator.BETWEEN,criteriaFor);
sumCostListForYear = delegator.findList("TblVehicleCostDetailForYear",conditionFor,null,null,null,false);
sumCostListForYearByVehicle = delegator.findList("TblVehicleCostDetailForYearByVehicle",conditionFor,null,null,null,false);
sumCostListForYearByMonth = delegator.findList("TblVehicleCostDetailForYearByMonth",conditionFor,null,null,null,false);


context.dateList = dateList;
context.year = year;
context.sumCostList = sumCostList;
context.sumCostListForYearByVehicle = sumCostListForYearByVehicle;
context.sumCostListForYear = sumCostListForYear;
context.sumCostListForYearByMonth = sumCostListForYearByMonth;
context.month=month;

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
