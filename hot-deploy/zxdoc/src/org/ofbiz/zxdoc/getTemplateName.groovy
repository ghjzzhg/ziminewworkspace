package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

/**
 * Created by Administrator on 2016/11/23.
 */

String templateKey = parameters.templateKey;
String partyId = context.get("userLogin").get("partyId");
List<GenericValue> caseTemplate = new ArrayList<>();
if(templateKey!="other")
{
    caseTemplate = EntityQuery.use(delegator).from("TblCaseTemplate").where("templateKey",templateKey,"partyId",null,"active","Y").queryList();
}else
{
    caseTemplate = EntityQuery.use(delegator).from("TblCaseTemplate").where("partyId",partyId,"active","Y").queryList();
}
request.setAttribute("caseTemplate",caseTemplate);