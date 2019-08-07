import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String partyId = context.get("userLogin").get("partyId");
List<GenericValue> activeProgressTasks = EntityQuery.use(delegator).from("CaseActiveProgressTask").where("managerPartyId", partyId).queryList();

List<Map> activeTasks = new ArrayList<>();
for(GenericValue activeTask : activeProgressTasks){
    Map<String, Object> map = new HashMap<>();
    map.put("caseTitle", activeTask.getString("caseTitle"));
    map.put("taskTitle", activeTask.getString("title"));
    map.put("caseId", activeTask.getString("caseId"));
    map.put("progressId", activeTask.getString("id"));
    activeTasks.add(map);
}
request.setAttribute("activeTasks", activeTasks);