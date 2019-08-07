import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngines
import org.activiti.engine.TaskService
import org.activiti.engine.task.Task
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

public Map<String, Object> taskAssignedToUser() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String userLoginId = context.get("userLoginId");
    if(UtilValidate.isEmpty(userLoginId)){
        userLoginId = userLogin.getString("userLoginId");
    }
    String userPartyId = context.get("partyId");
    if (UtilValidate.isEmpty(userPartyId)) {
        userPartyId = userLogin.getString("partyId");
    }
    GenericValue staff = from("TblStaff").where("partyId", userPartyId).queryOne();
    List<String> userGroups = new ArrayList<>();
    userGroups.add(staff == null ? "" : staff.getString("department"));
    userGroups.add(staff == null ? "" : "post-" + staff.getString("post"));
    userGroups.add(staff == null ? "" : "level-" + staff.getString("position"));

    String businessKey = context.get("businessKey");
    String workflowKey = context.get("workflowKey");

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(workflowKey).processInstanceBusinessKey(businessKey).or().taskCandidateOrAssigned(userLoginId).taskCandidateOrAssigned(userPartyId).taskCandidateGroupIn(userGroups).endOr().active().list();

    successResult.put("assignedToUser", tasks.size() > 0);
    if(tasks.size() > 0){
        Task task = tasks[0];
        successResult.put("taskId", task.getId());
        successResult.put("taskName", task.getName());
        successResult.put("nextViewType", taskService.getVariableLocal(task.getId(), "nextViewType"));
        successResult.put("nextView", taskService.getVariableLocal(task.getId(), "nextView"));
    }

    return successResult;
}
