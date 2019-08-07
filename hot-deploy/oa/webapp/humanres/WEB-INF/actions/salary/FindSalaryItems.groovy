import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil


public Map<String, Object> searchSalaryItems(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
// 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;
    List<GenericValue> salaryItemsList = new ArrayList<>();
    EntityListIterator salaryItemsIterator = EntityQuery.use(delegator).select()
            .from("TblSalaryEntry")
            .where(UtilMisc.toMap("systemType",null))
            .orderBy("entryId")
            .queryIterator();
    if(null != salaryItemsIterator && salaryItemsIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = salaryItemsIterator.getResultsSizeAfterPartialList();
        List<GenericValue> list = salaryItemsIterator.getPartialList(lowIndex, viewSize);
        Iterator<GenericValue> it = list.iterator();
        while (it.hasNext()) {
            GenericValue  salaryItems =it.next();
            salaryItemsList.add(salaryItems);
        }
    }
    salaryItemsIterator.close();
    relativeEntryList = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(UtilMisc.toMap("systemType",null)).queryList();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("salaryItemsList",salaryItemsList);
    viewData.put("relativeEntryList",relativeEntryList);
    successResult.put("data",viewData);
    return successResult;
}