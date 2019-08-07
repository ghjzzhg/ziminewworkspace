import javolution.util.FastList
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

partyId = parameters.get("partyId");
if(UtilValidate.isEmpty(partyId)){
    partyId = "Company";//顶级组织
}
query = from("PartyRelationship").where("partyIdFrom", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP").filterByDate();
partyRelationships = query.queryList();

listSubOrgs =  FastList.newInstance();
if(partyRelationships){
    if (partyRelationships) {
        //child
        for(partyRelationship in partyRelationships) {
            partyIdTo = partyRelationship.getString("partyIdTo");
            partyTo = from("Party").where("partyId", partyIdTo).queryOne();
            if(partyTo.getString("partyTypeId").equals("PERSON")){
                continue;//只显示PartyGroup
            }
            partyGroupMap = [:];
            partyTo = from("PartyGroup").where("partyId", partyIdTo).queryOne();
            partyGroupMap.put("partyId", partyIdTo);
            partyGroupMap.put("partyIdFrom", partyId);
            partyGroupMap.put("groupName", partyTo.get("groupName"));

            GenericValue manager = from("PartyRelationship").where("partyIdFrom", partyIdTo, "partyRelationshipTypeId", "MANAGER").filterByDate().queryOne();
            if(manager != null){
                partyGroupMap.put("manager", from("Person").where("partyId", manager.getString("partyIdTo")).queryOne().getString("fullName"));
            }
            listSubOrgs.add(partyGroupMap);
        }
    }
}

//排序
String sortField = parameters.get("sortField");
if(sortField){
    final boolean asc = sortField.startsWith("-");
    sortField = sortField.replaceFirst("-", "");
    if("sn".equals(sortField)){
        if(!asc){
            Collections.reverse(listSubOrgs);
        }
    }else{
        Collections.sort(listSubOrgs, new Comparator<Map>() {
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

context.listSubOrgs = listSubOrgs;