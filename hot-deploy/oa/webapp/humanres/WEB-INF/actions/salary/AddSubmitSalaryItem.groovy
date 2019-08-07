import org.ofbiz.base.util.UtilHttp
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate

Map<String, Object> ctx = UtilHttp.getParameterMap(request);
String idForAdd = parameters.get("idForAdd");
List personSalaryList = new ArrayList();
perfExamItem = [:];
int rowCount = UtilHttp.getMultiFormRowCount(ctx);
String year,month,sendId,staffId;
for (int i = 0; i < rowCount; i++) {
personSalaryMap = [:];
String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
 year = (String) ctx.get("year" + suffix);
 month = (String) ctx.get("month" + suffix);
 sendId = (String) ctx.get("sendId" + suffix);
String detailId = (String) ctx.get("detailId" + suffix);
String salary = (String) ctx.get("salary" + suffix);
String remarks = (String) ctx.get("remarks" + suffix);
String type = (String) ctx.get("type" + suffix);
String title = (String) ctx.get("title" + suffix);
String entryId = (String) ctx.get("entryId" + suffix);
 staffId = (String) ctx.get("staffId" + suffix);
String relativeEntry = (String) ctx.get("relativeEntry" + suffix);
String prevValue = (String) ctx.get("prevValue" + suffix);
String typeFor = (String) ctx.get("typeFor" + suffix);
String relativeEntryFor = (String) ctx.get("relativeEntryFor" + suffix);
    if (title.equals("应发工资")&&!UtilValidate.isEmpty(idForAdd)){
            salaryItem = delegator.findByPrimaryKey("SalaryEntryDetail",UtilMisc.toMap("entryId",idForAdd));
        personSalaryMapFor = [:];
        personSalaryMapFor.putAll(salaryItem);
        personSalaryMapFor.put("sendId",sendId);
        personSalaryMapFor.put("month",month);
        personSalaryMapFor.put("year",year);
        personSalaryMapFor.put("staffId",staffId);
            personSalaryList.add(personSalaryMapFor);
    }
    personSalaryMap.put("year",year)
    personSalaryMap.put("month",month)
    personSalaryMap.put("sendId",sendId)
    personSalaryMap.put("detailId",detailId)
    personSalaryMap.put("salary",salary)
    personSalaryMap.put("remarks",remarks)
    personSalaryMap.put("type",type)
    personSalaryMap.put("title",title)
    personSalaryMap.put("entryId",entryId)
    personSalaryMap.put("staffId",staffId)
    personSalaryMap.put("relativeEntry",relativeEntry)
    personSalaryMap.put("prevValue",prevValue)
    personSalaryMap.put("typeFor",typeFor)
    personSalaryMap.put("relativeEntryFor",relativeEntryFor)
personSalaryList.add(personSalaryMap);
}
/*perfExamItem.put("sendId",sendId);*/
context.personSalaryItems = personSalaryList;
context.sendId = sendId;
context.month = month;
context.year = year;
context.staffId = staffId;
context.perfExamItem = perfExamItem;