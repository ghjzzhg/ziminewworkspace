import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ModelService

partyId = parameters.get("partyId");
postId = parameters.get("postId");
url = parameters.get("url");
postChangeMap = [:];
if (partyId==""||partyId==null){
    if (postId!=null){
        postChangeMap = delegator.findByPrimaryKey("TblPostChangeDetails",UtilMisc.toMap("postId",postId));
        partyId = postChangeMap.get("partyId");
    }

}else if (postId!=null){
    postChangeMap = delegator.findByPrimaryKey("TblPostChangeDetails",UtilMisc.toMap("postId",postId));
}
resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
resultMap = [:];
if (resultList.size()!=0){
    map = [:];
    map = resultList[0];
    resultMap.put("fullName",map.get("fullName"));
    resultMap.put("workerSn",map.get("workerSn"));
    resultMap.put("partyId",partyId);
    resultMap.put("lastGroup",map.get("departmentName"));
    resultMap.put("lastPost",map.get("occupationName"));
    resultMap.put("lastPosition",map.get("position"));

}
if(postChangeMap.size()!=0&&postChangeMap!=null){
       resultMap.put("postId",postId);
       resultMap.put("lastGroup",postChangeMap.get("departmentName"));
       resultMap.put("lastPost",postChangeMap.get("occupationName"));
       resultMap.put("lastPosition",postChangeMap.get("lastPosition"));
       resultMap.put("newGroup",postChangeMap.get("newGroup"))
       resultMap.put("newPost",postChangeMap.get("newPost"))
       resultMap.put("newPosition",postChangeMap.get("newPosition"));
       resultMap.put("changeType",postChangeMap.get("changeType"))
       resultMap.put("changeDate",postChangeMap.get("changeDate"))
       resultMap.put("changeReason",postChangeMap.get("changeReason"))
       resultMap.put("remarks",postChangeMap.get("remarks"))
}
resultMap.put("url",url);
context.postId = postId;
context.resultMap = resultMap;
