import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
listOfWorkName = parameters.get("workName");
List<Map<String,Object>> listOfWorkListBase = FastList.newInstance();
List<Map<String,Object>> listOfWorkList = FastList.newInstance();
if(UtilValidate.isNotEmpty(listOfWorkName)){
    listOfWorkListBase = delegator.findList("TblListOfWork",EntityCondition.makeCondition("listOfWorkName",EntityOperator.LIKE,"%"+listOfWorkName+"%"),null,null,null,false);
}else {
    listOfWorkListBase = delegator.findList("TblListOfWork",null,null,null,null,false);
}
Calendar calendar = Calendar.getInstance();
for(GenericValue genericValue : listOfWorkListBase){
    Map<String,Object> map = FastMap.newInstance();
    map.putAll(genericValue);
    java.sql.Time toWorkTime = genericValue.get("toWorkTime");
    int swipingCardValidAfter = genericValue.get("swipingCardValidAfter");
    int swipingCardValidBefore = genericValue.get("swipingCardValidBefore");
    if(UtilValidate.isNotEmpty(toWorkTime)){
        calendar.setTime(toWorkTime);
        calendar.add(Calendar.MINUTE,+swipingCardValidAfter);
        map.put("toWorkSwipingCardOverTime",new java.sql.Time(calendar.getTime().getTime()));
        calendar.setTime(toWorkTime);
        calendar.add(Calendar.MINUTE,-swipingCardValidBefore);
        map.put("toWorkSwipingCardStartTime",new java.sql.Time(calendar.getTime().getTime()));
    }
    java.sql.Time getOffWorkTime = genericValue.get("getOffWorkTime");
    if(UtilValidate.isNotEmpty(getOffWorkTime)){
        calendar.setTime(getOffWorkTime);
        calendar.add(Calendar.MINUTE,+swipingCardValidAfter);
        map.put("getOffWorkTimeSwipingCardOverTime",new java.sql.Time(calendar.getTime().getTime()));
        calendar.setTime(getOffWorkTime);
        calendar.add(Calendar.MINUTE,-swipingCardValidBefore);
        map.put("getOffWorkTimeSwipingCardStartTime",new java.sql.Time(calendar.getTime().getTime()));
    }
    listOfWorkList.add(map);
}
context.listOfWorkList = listOfWorkList;