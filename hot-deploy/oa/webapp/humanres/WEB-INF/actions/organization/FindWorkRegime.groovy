import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

String listOfWorkByWeekId = parameters.get("listOfWorkByWeekId");
if(UtilValidate.isNotEmpty(listOfWorkByWeekId)){
    GenericValue listOfWorkByWeek = delegator.findOne("ListOfWorkByWeekInfo",UtilMisc.toMap("listOfWorkByWeekId",listOfWorkByWeekId),false);
    context.listOfWorkByWeek = listOfWorkByWeek;
    List<GenericValue> listOfWorkByWeekList = FastList.newInstance();
    listOfWorkByWeekList.add(listOfWorkByWeek);
    context.listOfWorkByWeekList = listOfWorkByWeekList;
}

