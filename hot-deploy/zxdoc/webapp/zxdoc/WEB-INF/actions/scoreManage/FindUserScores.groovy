import javolution.util.FastList
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

/**
 * Created by jiever on 2016/8/29.
 */
//这边的username为联系人名，需要考虑企业
String userName =parameters.get("userName");
//登录人的userloginId,需要处理成partyId
String account =parameters.get("account");
List<EntityCondition> condList = FastList.newInstance();
//这边查询条件全部修改为模糊查询
if(userName!=null&&!"".equals(userName)){
    List<EntityCondition> condition = FastList.newInstance();
    condition.add(EntityCondition.makeCondition("fullName",EntityOperator.LIKE,"%"+userName+"%"));
    List<GenericValue> groups = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where(condition).queryList();
    List<String> groupIds = new ArrayList<>();
    for (GenericValue group:groups)
    {
        String partyId = group.get("partyId");
        groupIds.add(partyId);
    }
    condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("userName",EntityOperator.LIKE,"%"+userName+"%"),EntityOperator.OR,EntityCondition.makeCondition("userLoginId",EntityOperator.IN,groupIds)));
}
if(account!=null&&!"".equals(account)){
    List<EntityCondition> condition = FastList.newInstance();
    condition.add(EntityCondition.makeCondition("userLoginId",EntityOperator.LIKE,"%"+account+"%"));
    List<GenericValue> accounts = EntityQuery.use(delegator).from("UserLogin").where(condition).queryList();
    List<String> ids = new ArrayList<>();
    for (GenericValue acc:accounts)
    {
        String partyId = acc.get("partyId");
        ids.add(partyId);
    }
    condList.add(EntityCondition.makeCondition("userLoginId",EntityOperator.IN,ids));
}
condList.add(EntityCondition.makeCondition("userName",EntityOperator.NOT_EQUAL,null));
UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("UserScoreParty").where(EntityCondition.makeCondition(condList)), parameters);
//循环遍历，修复当前登录的主账户不显示名称的bug
List<GenericValue> userScore = new ArrayList<>();
for (GenericValue score:result.data)
{
    Map map = new HashMap();
    String name = score.get("userName");
    //用户名不存在，去查找公账户记录
    if(name==null||name=="")
    {
        String userLoginId = score.get("userLoginId");
        List<GenericValue> groupList = EntityQuery.use(delegator).from("PartyGroupAndUserLoginAndContact").where("partyId",userLoginId).orderBy("fullName DESC").distinct().queryList();
        if(UtilValidate.isNotEmpty(groupList)){
            GenericValue group = groupList.get(0);
            map.put("userLoginId",userLoginId);
            map.put("scoreOn",score.get("scoreOn"));
            map.put("scoreOff",score.get("scoreOff"));
            map.put("scoreNow",score.get("scoreNow"));
            map.put("createdStamp",score.get("createdStamp"));
            map.put("userName",group.get("fullName"));
        }
    }else
    {
        map.putAll(score);
    }
    userScore.add(map);
}
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
request.setAttribute("data", userScore);