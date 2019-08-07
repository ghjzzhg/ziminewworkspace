import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

GenericValue userLogin = context.get("userLogin");
Map<String,Object> map = dispatcher.runSync("searchTypeQuestion", UtilMisc.<String, Object>toMap("filterParty","true","questionType","","userLogin", userLogin));
Map<String ,Object> data = map.get("data");
context.put("questionList", data.get("result"));
context.put("totalPage", data.get("totalPage"));
context.put("filterParty", "true");
