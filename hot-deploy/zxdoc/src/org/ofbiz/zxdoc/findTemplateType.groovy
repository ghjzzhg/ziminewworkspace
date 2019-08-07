package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/23.
 */
String partyId = context.get("userLogin").get("partyId");
List<GenericValue> caseTemplate = EntityQuery.use(delegator).from("CaseTemplateList").where("partyId",null,"active","Y").distinct().queryList();
//去重
for (int i=0;i<caseTemplate.size();i++)
{
    String templateKeyName = caseTemplate.get(i).get("templateKeyName");
    for (int j=0;j<caseTemplate.size()-i;j++)
    {
        String templateKeyName1 = caseTemplate.get(j).get("templateKeyName");
        if(templateKeyName.equals(templateKeyName1))
        {
            caseTemplate.remove(i);
        }
    }
}
context.caseTemplate = caseTemplate;