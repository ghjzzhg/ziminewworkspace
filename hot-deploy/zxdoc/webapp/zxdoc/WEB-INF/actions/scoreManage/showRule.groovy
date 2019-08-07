package scoreManage

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/12/1.
 */
List<GenericValue> rules = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId", "SCORE_RULE").queryList();
//积分百分比的改动：分为两部分（获得积分与失去积分）
//TODO：失去积分目前只出现在下载中，且积分策略中暂时不能配置，后续如果可以配置失去积分的内容时再考虑
//1.获取partyId:这里考虑是用户登录的我的积分还是管理员查看某个用户的积分记录
String partyId = parameters.partyId == null ? context.get("userLogin").get("partyId") : parameters.partyId;
//2.获取积分变动表中的所有获得积分的总和
Integer scoreTotal = 0;
List<EntityCondition> condition = new ArrayList<>();
condition.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyId));
condition.add(EntityCondition.makeCondition("scoreChange", EntityOperator.GREATER_THAN, 0));
List<GenericValue> scoreHistorys = EntityQuery.use(delegator).from("TblScoreHistory").where(condition).queryList();
for (GenericValue scoreHistory : scoreHistorys) {
    Integer scoreChange = scoreHistory.get("scoreChange");
    scoreTotal += scoreChange;
}
//3.计算每个类型的百分比
List<Map> list = new ArrayList<>();
for (GenericValue rule : rules) {
    Map map = new HashMap();
    map.putAll(rule);
    //如果不存在积分变动，那么所有项的百分比设置为0
    if (scoreTotal == 0) {
        map.put("percentage", 0);
    } else {
        String enumId = rule.get("enumId");
        List<EntityCondition> conditions = new ArrayList<>();
        conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyId));
        conditions.add(EntityCondition.makeCondition("scoreChange", EntityOperator.GREATER_THAN, 0));
        conditions.add(EntityCondition.makeCondition("rule", EntityOperator.EQUALS, enumId));
        List<GenericValue> ruleHistorys = EntityQuery.use(delegator).from("TblScoreHistory").where(conditions).queryList();
        Integer ruleScore = 0;
        for (GenericValue ruleHistory : ruleHistorys) {
            Integer change = ruleHistory.get("scoreChange");
            ruleScore += change;
        }
        if (ruleScore == 0) {
            map.put("percentage", 0);
        } else {
            double result = ruleScore.doubleValue() / scoreTotal;
            BigDecimal b = new BigDecimal(result);
            Integer percent = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            map.put("percentage", percent);
        }
    }
    list.add(map);
}
request.setAttribute("rules", list);