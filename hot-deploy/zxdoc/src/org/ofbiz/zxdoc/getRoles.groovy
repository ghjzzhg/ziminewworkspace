package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/23.
 */
String id = parameters.id;
GenericValue caseTemplate = EntityQuery.use(delegator).from("TblCaseTemplate").where("id",id).queryOne();
String roles = caseTemplate==null?"":caseTemplate.get("roles");
List role = new ArrayList();
if(roles!=null && roles!="")
{
    if(roles.substring(0,1)=="[") {
        String roleMember = roles.substring(1, roles.length() - 1);
        role = roleMember.split(",");
    }else
    {
        role.add(roles);
    }
}
List list = new ArrayList();
for (int i = 0; i < role.size();i++)
{
    Map map = new HashMap();
    String roleTypeId = role.get(i);
    //处理非第一个元素会首字符为空格的情况
    if(i!=0)
    {
        roleTypeId = roleTypeId.substring(1,roleTypeId.length());
    }
    GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",roleTypeId,"parentTypeId","CASE_ROLE").queryOne();
    String description = roleType.get("description");
    map.put("roleTypeId",roleTypeId);
    map.put("description",description);
    list.add(map);
}
request.setAttribute("list",list);