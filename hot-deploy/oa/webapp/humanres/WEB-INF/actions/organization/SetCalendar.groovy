import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

staffId = parameters.get("staffId");
if(UtilValidate.isNotEmpty(staffId)){
    GenericValue person = delegator.findOne("Person",UtilMisc.toMap("partyId",staffId),false);
    context.person = person;
}
context.staffId = staffId;
