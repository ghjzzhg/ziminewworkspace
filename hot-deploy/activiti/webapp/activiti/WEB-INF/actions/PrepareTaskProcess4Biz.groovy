import org.activiti.engine.*
import org.activiti.engine.task.Task

String bizKey = request.getParameter("bizKey");
String processKey = request.getParameter("processKey");

ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
RepositoryService repositoryService = processEngine.getRepositoryService();
RuntimeService runtimeService = processEngine.getRuntimeService();
IdentityService identityService = processEngine.getIdentityService();
TaskService taskService = processEngine.getTaskService();
HistoryService historyService = processEngine.getHistoryService();

//根据流程key及业务主键查询对应的任务节点
List<Task> list = taskService.createTaskQuery().processDefinitionKey(processKey).processInstanceBusinessKey(bizKey).list();
if(list != null && list.size() > 0){
    String taskId = list.get(0).getId()
    context.put("taskId", taskId);
    context.put("viewType", taskService.getVariableLocal(taskId, "nextViewType"));
    context.put("view", taskService.getVariableLocal(taskId, "nextView"));
}