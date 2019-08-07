import javolution.util.FastList
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery

String Id  = parameters.get("Id");
String parentId="";
if(Id==null||Id==""){
    parentId="1000";
}else{
    parentId=Id;
}

int viewIndex = 0;
try {
    viewIndex = Integer.parseInt((String) parameters.get("VIEW_INDEX"));
} catch (Exception e) {
    viewIndex = 0;
}
int totalCount = 0;
int viewSize = 2;
try {
    viewSize = Integer.parseInt((String) parameters.get("VIEW_SIZE"));
} catch (Exception e) {
    viewSize = 2;
}
// 计算当前显示页的最小、最大索引(可能会超出实际条数)
int lowIndex = viewIndex * viewSize + 1;
int highIndex = (viewIndex + 1) * viewSize;

List<EntityCondition> conditionList = FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("parentId", EntityOperator.EQUALS, parentId));
conditionList.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, "1"));
EntityListIterator fileTypes = EntityQuery.use(delegator).from("TblFileType").where(conditionList).queryIterator();
List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
if(null != fileTypes && fileTypes.getResultsSizeAfterPartialList() > 0){
    totalCount = fileTypes.getResultsSizeAfterPartialList();
    pageList = fileTypes.getPartialList(lowIndex, viewSize);
}
ListFileType =  FastList.newInstance();
if (pageList) {
    for(fileType in pageList) {
        ListFileType.add(fileType);
    }
}

//排序
String sortField = parameters.get("sortField");
if(sortField){
    final boolean asc = sortField.startsWith("-");
    sortField = sortField.replaceFirst("-", "");
    if("sn".equals(sortField)){
        if(!asc){
            Collections.reverse(ListFileType);
        }
    }else{
        Collections.sort(ListFileType, new Comparator<Map>() {
            @Override
            int compare(Map o1, Map o2) {
                Comparable f1 = o1.get(sortField);
                f1 = f1 == null ? "" : f1;
                Comparable f2 = o2.get(sortField);
                f2 = f2 == null ? "" : f2;
                return asc ? ((Comparable)f1).compareTo(f2) : ((Comparable)f2).compareTo(f1);
            }
        })
    }
}

context.ListFileType = ListFileType;
context.viewIndex = viewIndex;
context.highIndex = highIndex;
context.totalCount = totalCount;
context.viewSize = viewSize;
context.lowIndex = lowIndex;