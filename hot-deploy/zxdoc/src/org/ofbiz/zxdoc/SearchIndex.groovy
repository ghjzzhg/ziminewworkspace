package org.ofbiz.zxdoc

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/10/28.
 */
public Map searchGroupName()
{
    String groupTips = context.get("groupTips");
    String groupType = context.get("groupType");
    if(groupTips==null)
    {
        groupTips = "-----------------------------";
    }
    //获取结果列表
    List<GenericValue> groups = EntityQuery.use(delegator).from("BasicPartnerInfo").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"),EntityCondition.makeCondition("groupName",EntityOperator.LIKE,"%"+groupTips+"%"),EntityCondition.makeCondition(EntityCondition.makeCondition("partnerType",EntityOperator.EQUALS,groupType),EntityOperator.OR,EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,groupType))).cache().queryList();
    List<GenericValue> group = new ArrayList<GenericValue>();
    //筛选账户类型为合伙人
    for(GenericValue gro : groups)
    {
        /*String partyId = gro.get("partyId");
        List roles = EntityQuery.use(delegator).select("roleTypeId").from("PartyRole").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).cache().queryList();
        List role = new ArrayList();
        for(int i = 0;i<roles.size();i++)
        {
            role.add(roles[i].get("roleTypeId"));
        }
        if(role.contains("CASE_ROLE_PARTNER"))
        {*/
            group.add(gro);
        /*}*/
    }
    List list = new ArrayList();
    //去重
    for ( int i = 0 ; i < group.size() - 1 ; i ++ ) {
        for ( int j = group.size() - 1 ; j > i; j -- ) {
            String name1 = group.get(j).get("groupName");
            String name2 = group.get(i).get("groupName");
            if (name1.equals(name2)) {
                group.remove(j);
            }
        }
    }
    for(GenericValue groupName:group)
    {
        list.add(groupName.get("groupName"));
    }
    Map map = new HashMap();
    map.put("data",list);
    return map;
}
/**
 * //changed by galaxypan@2017-09-03:适应jquery ui autocomplete
 */
public Map searchGroupNameJsonp()
{
    String groupTips = context.get("groupTips");
    String groupType = context.get("groupType");
    if(groupTips==null)
    {
        groupTips = "-----------------------------";
    }
    //获取结果列表
    List<GenericValue> groups = EntityQuery.use(delegator).from("BasicPartnerInfo").select("groupName").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"),EntityCondition.makeCondition("groupName",EntityOperator.LIKE,"%"+groupTips+"%"),EntityCondition.makeCondition(EntityCondition.makeCondition("partnerType",EntityOperator.EQUALS,groupType),EntityOperator.OR,EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,groupType))).distinct().cache().queryList();
    List<Map<String, String>> list = new ArrayList<>();
    for(GenericValue group:groups)
    {
        Map<String, String> option = new HashMap<>();
        String groupName = group.getString("groupName")
        option.put("id", groupName);
        option.put("label", groupName);
        option.put("value", groupName);
        list.add(option);
    }
    Map map = new HashMap();
    map.put("data",list);
    return map;
}