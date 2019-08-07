import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator

Map<String,Object> data = runService("getLowerDepartments", UtilMisc.toMap("userLogin", userLogin, "departmentId","Company"));
List<Map> departmentList = FastList.newInstance();
if(UtilValidate.isNotEmpty(data.get("data"))){
    for(Map<String,Object> map : data.get("data")){
        Map<String,Object> departmentMap = FastMap.newInstance();
        valueMap = from("PartyGroup").where("partyId", map.get("partyId")).queryOne();
        departmentMap.putAll(map);
        departmentMap.putAll(valueMap);
        departmentList.add(departmentMap)
    }
    context.departmentList =departmentList;
}

/*String noticeId = parameters.get("noticeId");
if(UtilValidate.isNotEmpty(noticeId)){
    GenericValue notice = delegator.findOne("NoticeInfo",UtilMisc.toMap("noticeId",noticeId),false);
    context.notice = notice;
    List<GenericValue> signInPersonList = delegator.findList("TblSignInPerson",EntityCondition.makeCondition("noticeId",EntityOperator.EQUALS,noticeId),null,null,null,false);
    context.signInPerson = signInPersonList;
}*/





