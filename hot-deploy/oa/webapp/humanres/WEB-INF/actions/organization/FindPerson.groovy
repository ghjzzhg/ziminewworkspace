import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.entity.GenericValue

staffList =  FastList.newInstance();
List<GenericValue> staffs = delegator.findAll("TblStaff",false);
for(staff in staffs){
    Map<String,Object> valueMap = FastMap.newInstance();
    valueMap.putAll(staff);
    if(staff.getRelated("Person")){
        valueMap.putAll(staff.getRelated("Person")[0]);
    }
    staffList.add(valueMap);
}
context.staffList = staffList;

