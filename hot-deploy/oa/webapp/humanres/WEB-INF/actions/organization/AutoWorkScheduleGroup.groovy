import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

partyIdTo = parameters.get("partyIdTo");
if(UtilValidate.isNotEmpty(partyIdTo)){
    List<GenericValue> group = EntityQuery.use(delegator).select().from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom",partyIdTo,"partyRelationshipTypeId", "GROUP_ROLLUP")).queryList();
    List<GenericValue> groupList = FastList.newInstance();
    if(null != group && group.size()){
        for(GenericValue value : group){
            List<GenericValue> depart = delegator.findByAnd("PartyGroup",UtilMisc.toMap("partyId",value.get("partyIdTo")));
            if(UtilValidate.isNotEmpty(depart)){
                groupList.add(depart.get(0));
            }
        }
    }else{
        groupList = new ArrayList<GenericValue>();
    }
    context.groupList = groupList;
}
