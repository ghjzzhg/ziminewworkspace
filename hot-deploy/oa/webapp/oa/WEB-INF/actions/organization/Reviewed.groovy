import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Created by rextec-15-1 on 2016/4/12.
 */

public Map<String,Object> ApproveReceive(){
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

    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    List<EntityCondition> condition = FastList.newInstance();
    condition.add(EntityCondition.makeCondition([EntityCondition.makeCondition("checkResult",EntityOperator.EQUALS,"RECEIVE_RESULT_ONE"),
                                                  EntityCondition.makeCondition("checkResult",EntityOperator.EQUALS,"RECEIVE_RESULT_THREE")], EntityOperator.OR));
    EntityListIterator receiveIterator = EntityQuery.use(delegator)
            .select()
            .from("ReceiveInfo")
            .where(condition)
            .queryIterator();
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    if(null != receiveIterator && receiveIterator.getResultsSizeAfterPartialList() > 0){
        totalCount = receiveIterator.getResultsSizeAfterPartialList();
        pageList = receiveIterator.getPartialList(lowIndex, viewSize);
    }
    for (Map map :pageList){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        map.put("receiveDate",format.format(map.get("receiveDate")));
    }

    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",pageList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex); ;

    successResult.put("data",map);
    return successResult;
}
