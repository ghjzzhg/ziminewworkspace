import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.party.party.PartyHelper

completedTree =  FastList.newInstance();
completedTreeContext =  FastList.newInstance();
existParties =  FastList.newInstance();
subtopLists =  FastList.newInstance();
//internalOrg list
partyRelationships = from("PartyRelationship").where("partyIdFrom", "Company","partyRelationshipTypeId", "GROUP_ROLLUP").filterByDate().queryList();
if (partyRelationships) {
    //root
    partyRoot = from("PartyGroup").where("partyId", "Company").queryOne();
    partyRootMap = FastMap.newInstance();
    partyRootMap.put("partyId", "Company");
    partyRootMap.put("groupName", partyRoot.getString("groupName"));
    partyRootMap.put("grade", 0);
    //child
    for (partyRelationship in partyRelationships) {
        partyGroupMap = [:];
        partyGroupMap.put("partyId", partyRelationship.getString("partyIdTo"));
        partyGroupMap.put("groupName", PartyHelper.getPartyName(delegator, partyRelationship.getString("partyIdTo"), false));
        completedTreeContext.add(partyGroupMap);
        subtopLists.addAll(partyRelationship.getString("partyIdTo"));
    }

    partyRootMap.put("child", completedTreeContext);
    completedTree.add(partyRootMap);
}

String holidayId = parameters.get("holidayId");
if(UtilValidate.isNotEmpty(holidayId)){
    GenericValue holiday = delegator.findOne("TblHoliday",UtilMisc.toMap("holidayId",holidayId),false);
    context.holiday = holiday;
}
context.completedTree = completedTree;
context.subtopLists = subtopLists;

