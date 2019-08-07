import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery


years = [];
months = [];
int yearNow = Calendar.getInstance().get(Calendar.YEAR);
int monthNow = Calendar.getInstance().get(Calendar.MONTH);
int selectedYear=0;
int selectedMonth=0;
perfExamPersons = EntityQuery.use(delegator).select().from("TblPerfExamPerson").where(UtilMisc.toMap("planState","1")).orderBy("startDate ASC").queryList();
if(perfExamPersons.size()!=0){
    GenericValue map =perfExamPersons[0];
    java.sql.Date startDate = map.get("startDate");
    java.util.Date date = new java.util.Date(startDate.getTime());
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    selectedYear = cal.get(Calendar.YEAR);
    selectedMonth = cal.get(Calendar.MONTH);
}
if(selectedYear<yearNow){
    int month = 0 ;
    while (month<monthNow){
        month = month+1;
        months.push([value: month, label: month + '月']);
    }
}else if(selectedYear==yearNow){
    if(selectedMonth<=monthNow){
        /*months.push([value: monthNow+"", label: monthNow + '月']);*/
        while (selectedMonth<monthNow){
            months.push([value: monthNow+"", label: monthNow + '月']);
            monthNow = monthNow - 1;
        }
    }


}
years.push([value: yearNow+"", label: yearNow + '年']);
while (selectedYear<yearNow){
    yearNow = yearNow - 1;
    years.push([value: yearNow+"", label: yearNow + '年']);
}

context.years = years;
context.months = months;
context.evaluateDate = UtilDateTime.nowDateString("yyyy-MM-dd HH:mm:ss");
context.evaluateMonth = Calendar.getInstance().get(Calendar.MONTH);
context.evaluateYear = Calendar.getInstance().get(Calendar.YEAR);


