import com.ibm.icu.util.GenderInfo
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery


int viewIndex = 0;
try {
    viewIndex = Integer.parseInt((String) parameters.get("VIEW_INDEX"));
} catch (Exception e) {
    viewIndex = 0;
}
int totalCount = 0;
int viewSize = 3;
try {
    viewSize = Integer.parseInt((String) parameters.get("VIEW_SIZE"));
} catch (Exception e) {
    viewSize = 3;
}
int lowIndex = viewIndex * viewSize + 1;
int highIndex = (viewIndex + 1) * viewSize;

List<GenericValue> salaryMouldList = null;
EntityListIterator from = EntityQuery.use(delegator).from("SalaryBillMouldInfo").orderBy("createdTime DESC").queryIterator();
List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
if(null != from && from.getResultsSizeAfterPartialList() > 0){
    totalCount = from.getResultsSizeAfterPartialList();
    pageList = from.getPartialList(lowIndex, viewSize);
}
salaryMouldList = pageList;

List<GenericValue> usingTemplate = delegator.findByAnd("TblSalaryBillMould",UtilMisc.toMap("useState","USING"));
String content1 = "";
String mouldName1 = "";
if(UtilValidate.isNotEmpty(usingTemplate)){
GenericValue salaryMould = usingTemplate.get(0)
    content1 = salaryMould.get("mouldContent");
    mouldName1 = salaryMould.get("mouldName");
}
String mouldId = parameters.get("mouldId");
GenericValue salaryBillMould = delegator.findByPrimaryKey("TblSalaryBillMould", UtilMisc.toMap("mouldId", mouldId));
context.content1 = content1;
context.mouldName1 = mouldName1;
context.salaryBillMould = salaryBillMould;
context.salaryMouldList = salaryMouldList;
context.viewIndex = viewIndex;
context.highIndex = highIndex;
context.totalCount = totalCount;
context.viewSize = viewSize;
context.lowIndex = lowIndex;
