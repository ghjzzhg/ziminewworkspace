import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

GenericValue userLogin = (GenericValue) context.get("userLogin");
listOfWorkId = parameters.get("listOfWorkId");
Map maps=new HashMap();
GenericValue listOfWork = delegator.makeValidValue("TblListOfWork",null);
if(UtilValidate.isNotEmpty(listOfWorkId)){
    listOfWork = EntityQuery.use(delegator).select().from("TblListOfWork").where(UtilMisc.toMap("listOfWorkId",listOfWorkId)).queryOne();
    if(UtilValidate.isNotEmpty(listOfWork)){
        String a=listOfWork.get("toWorkTime");
        String b=listOfWork.get("getOffWorkTime");
        maps.putAll(listOfWork);
        String toWorkTime_hour=a.substring(0,2);
        String toWorkTime_minutes=a.substring(3,5);
        int toWorkTime_c_hour=Integer.parseInt(toWorkTime_hour);
        int toWorkTime_c_minutes=Integer.parseInt(toWorkTime_minutes);
        maps.put("toWorkTime_c_hour",toWorkTime_c_hour);
        maps.put("toWorkTime_c_minutes",toWorkTime_c_minutes);
        String getOffWorkTime_hour=b.substring(0,2);
        String getOffWorkTime_minutes=b.substring(3,5);
        int getOffWorkTime_c_hour = Integer.parseInt(getOffWorkTime_hour);
        int getOffWorkTime_c_minutes = Integer.parseInt(getOffWorkTime_minutes);
        maps.put("getOffWorkTime_c_hour",getOffWorkTime_c_hour);
        maps.put("getOffWorkTime_c_minutes",getOffWorkTime_c_minutes);
    }
}else{
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblListOfWork","numName","listOfWorkNumber","prefix","listOfWork","userLogin",userLogin));
    maps.put("listOfWorkNumber",uniqueNumber.get("number"));
}

context.listOfWork = maps;

swipingCardValidList = FastList.newInstance();
map = FastMap.newInstance();
map.put("swipingCardValidBefore",30);
map.put("swipingCardValidAfter",30);
swipingCardValidList.add(map);
context.swipingCardValidList = swipingCardValidList;
