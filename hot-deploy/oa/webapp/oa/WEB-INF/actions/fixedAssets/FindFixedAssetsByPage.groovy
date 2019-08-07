import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.util.EntityListIterator

int viewSize = GlobalConstant.VIEW_SIZE;
int dataSize = 0;
int viewIndex = 0;
String temp = (String) map.get("viewIndex");
if (UtilValidate.isNotEmpty(temp)) {
    try {
        viewIndex = Integer.valueOf(temp);
    } catch (NumberFormatException e) {
        viewIndex = 0;
        Debug.logError(e.getMessage().toString(), module);
    }
}
int lowIndex = viewIndex * viewSize + 1;
int highIndex = (viewIndex + 1) * viewSize;

List<EntityCondition> condition = makeCondition(map);
EntityListIterator eli = null;
List<GenericValue> product = null;
try {
    eli = delegator.find("test", EntityCondition.makeCondition(condition), null, null, UtilMisc
            .toList(sortBy), null);
    product = eli.getPartialList(lowIndex, viewSize);
    eli.last();
    dataSize = eli.currentIndex();
} catch (GenericEntityException e) {
    Debug.logInfo(e.getMessage().toString(), module);
} finally {
    try {
        if (eli != null) {
            eli.close();
            eli = null;
        }

    } catch (GenericEntityException e) {
        Debug.logError(e.getMessage().toString(), module);
    }
}
if (highIndex > dataSize) {
    highIndex = dataSize;
}