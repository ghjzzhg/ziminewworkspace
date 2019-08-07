package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/12/23.
 */
String caseId = parameters.caseId;
List<GenericValue> caseTicket = EntityQuery.use(delegator).from("TblTicket").where("caseId",caseId).queryList();
if(caseTicket!=null&&caseTicket.size()>0)
{
    request.setAttribute("isTicket","Y");
}else
{
    request.setAttribute("isTicket","N");
}