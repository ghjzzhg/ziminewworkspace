import activiti.ActivitiProcessUtils
import activiti.cmd.ChooseTaskCmd
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.collect.Maps
import org.activiti.bpmn.converter.BpmnXMLConverter
import org.activiti.bpmn.model.BpmnModel
import org.activiti.editor.constants.ModelDataJsonConstants
import org.activiti.editor.language.json.converter.BpmnJsonConverter
import org.activiti.engine.*
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.history.HistoricTaskInstance
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.impl.RepositoryServiceImpl
import org.activiti.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity
import org.activiti.engine.impl.pvm.PvmTransition
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior
import org.activiti.engine.impl.pvm.process.ActivityImpl
import org.activiti.engine.impl.pvm.process.TransitionImpl
import org.activiti.engine.repository.*
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.IdentityLink
import org.activiti.engine.task.IdentityLinkType
import org.activiti.engine.task.Task
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.*
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.party.party.PartyWorker
import org.ofbiz.webapp.event.EventUtil

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import java.nio.ByteBuffer
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.zip.ZipInputStream

public Object processList(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    ManagementService managementService = processEngine.getManagementService();

    String tenantId = request.getAttribute("userTenantId");
    List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();
    //获取部署的流程

    String tableName = managementService.getTableName(ProcessDefinition.class);
    NativeProcessDefinitionQuery nativeQuery = repositoryService.createNativeProcessDefinitionQuery();
    nativeQuery.sql("select * from " + tableName + " where (deployment_id_, version_) in (select deployment_id_, max(version_) from " + tableName + " where tenant_id_ is null or tenant_id_ = '' or tenant_id_ = #{tenant} group by deployment_id_)");
    nativeQuery.parameter("tenant", tenantId);
    List<ProcessDefinition> processDefinitionList = nativeQuery.list();
    ModelQuery modelQuery = repositoryService.createModelQuery();
    if(UtilValidate.isNotEmpty(tenantId)){
        modelQuery = modelQuery.modelTenantId(tenantId);
    }
    for (ProcessDefinition processDefinition : processDefinitionList) {
        String deploymentId = processDefinition.getDeploymentId();
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
        List<Model> models = modelQuery.deploymentId(deployment.id).list();
        String modelId = "";
        if(models.size() > 0){
            modelId = models[0].id;
        }
        objects.add([processDefinition: [id: processDefinition.id,
                                         deploymentId: processDefinition.deploymentId,
                                         name: processDefinition.name,
                                         key: processDefinition.key,
                                         version: processDefinition.version,
                                         suspendStatus: processDefinition.suspended ? '<span style="color:red">挂起</span>' : '正常',
                                         activeToggleTitle: processDefinition.suspended ? '激活' : '挂起'],
                     deployment: [id: deployment.id, deploymentTime: deployment.deploymentTime, category: deployment.category, modelId: modelId]]);


    }
    request.setAttribute("processList", objects);
    return "success";
}

public Object applyList(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    HistoryService historyService = processEngine.getHistoryService();
    TaskService taskService = processEngine.getTaskService();

    String tenantId = request.getAttribute("userTenantId");
    GenericValue loginUser = context.get("userLogin");
    String userId = loginUser.getString("partyId");
    List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();
    //获取部署的流程

    List<HistoricProcessInstance> instanceList = historyService.createHistoricProcessInstanceQuery().startedBy(userId).list();

    for (HistoricProcessInstance instance : instanceList) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionId(instance.getProcessDefinitionId()).singleResult();
        Map<String, Object> instanceTO = new HashMap<>();
        instanceTO.put("processDefinitionId", procDef.getId());
        instanceTO.put("processInstanceId", instance.getId());
        instanceTO.put("name", procDef.getName());
        instanceTO.put("category", procDef.getCategory());
        instanceTO.put("version", procDef.getVersion());
        instanceTO.put("startTime", instance.getStartTime());
        instanceTO.put("endTime", instance.getEndTime());
        instanceTO.put("deleteReason", instance.deleteReason);
        StringBuilder activityNames = new StringBuilder();
        StringBuilder candidates = new StringBuilder();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(instance.getId()).orderByTaskCreateTime().desc().list();

        if (tasks != null && tasks.size() >= 1) {
            for (Task task : tasks) {
                String assignee = task.getAssignee();
                activityNames.append("<div>" + task.getName() + "</div>");
                candidates.append("<div>" + getTaskExecutor(task.getId()).join(" ") + "</div>");
            }
        }
        instanceTO.put("activityNames", activityNames);
        instanceTO.put("candidates", candidates.toString());
        objects.add(instanceTO);
    }
    request.setAttribute("processList", objects);
    return "success";
}

public Object participateList(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    HistoryService historyService = processEngine.getHistoryService();
    TaskService taskService = processEngine.getTaskService();

    String tenantId = request.getAttribute("userTenantId");
    GenericValue loginUser = context.get("userLogin");
    String userId = loginUser.getString("partyId");
    List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();

    List<HistoricProcessInstance> participateProcess = new ArrayList<HistoricProcessInstance>();
    List<HistoricTaskInstance> allHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().list();
    for (HistoricTaskInstance instance : allHistoricTaskInstance) {
        String processInstanceId = instance.getProcessInstanceId();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if (list != null && !list.isEmpty()) {
            participateProcess.addAll(list);
        }
    }

    Map<String, HistoricProcessInstance> noDups = new HashMap<>();
    for (HistoricProcessInstance processInstance : participateProcess) {
        noDups.put(processInstance.getId(), processInstance);
    }

    Object[] historyInstances = noDups.values();
    Iterator<HistoricProcessInstance> participateProcessIterator = historyInstances.iterator();
    while (participateProcessIterator.hasNext()) {
        HistoricProcessInstance next = participateProcessIterator.next();
        if (userId.equals(next.getStartUserId())) {
            participateProcessIterator.remove();//自己申请的忽略
        }
    }

    for (HistoricProcessInstance instance : historyInstances) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionId(instance.getProcessDefinitionId()).singleResult();
        Map<String, Object> instanceTO = new HashMap<>();
        instanceTO.put("processDefinitionId", procDef.getId());
        instanceTO.put("processInstanceId", instance.getId());
        instanceTO.put("name", procDef.getName());
        instanceTO.put("category", procDef.getCategory());
        instanceTO.put("version", procDef.getVersion());
        instanceTO.put("startTime", instance.getStartTime());
        instanceTO.put("endTime", instance.getEndTime());
        StringBuilder activityNames = new StringBuilder();
        StringBuilder candidates = new StringBuilder();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(instance.getId()).orderByTaskCreateTime().desc().list();

        if (tasks != null && tasks.size() >= 1) {
            for (Task task : tasks) {
                String assignee = task.getAssignee();
                activityNames.append("<div>" + task.getName() + "</div>");
                candidates.append("<div>" + StringUtils.defaultString(assignee, " ") + "</div>");
            }
        }
        instanceTO.put("activityNames", activityNames);
        instanceTO.put("candidates", candidates);
        objects.add(instanceTO);
    }
    request.setAttribute("processList", objects);
    return "success";
}

public Object taskList(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    HistoryService historyService = processEngine.getHistoryService();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    String tenantId = request.getAttribute("userTenantId");
    GenericValue loginUser = context.get("userLogin");
    String userId = loginUser.getString("partyId");
    List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();

    List<Task> tasks = new ArrayList<Task>();
    List<Task> currentTasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
    tasks.addAll(currentTasks);
    currentTasks = taskService.createTaskQuery().taskCandidateUser(userId).taskUnassigned().orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
    tasks.addAll(currentTasks);


    GenericValue staff = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", userId), true);
    if (staff != null) {
        String department = staff.getString("department");
        if (StringUtils.isNotBlank(department)) {
            currentTasks = taskService.createTaskQuery().taskCandidateGroup(department).taskUnassigned().orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
            tasks.addAll(currentTasks);
        }
    }

    for (Task task : tasks) {
        Map<String, Object> instanceTO = new HashMap<>();
        instanceTO.put("processDefinitionId", task.getProcessDefinitionId());
        instanceTO.put("processInstanceId", task.getProcessInstanceId());
        instanceTO.put("taskId", task.getId());
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();

        String categoryId = processDefinition.getCategory();
        instanceTO.put("category", categoryId);
        if(UtilValidate.isNotEmpty(categoryId)){
            GenericValue category = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", categoryId), true);
            instanceTO.put("categoryName", category == null ? "" : category.getString("description"));
        } else {
            instanceTO.put("categoryName", "");
        }
        instanceTO.put("executionId",task.getExecutionId())
        instanceTO.put("version", processDefinition.getVersion());
        instanceTO.put("name", processDefinition.getName());
        String name = task.getName();
        List<GenericValue> genericValueList = EntityQuery.use(delegator).from("tblActTaskAllotUser").where(UtilMisc.toMap("taskId", task.getId())).orderBy("allotTime").queryList();
        if(UtilValidate.isNotEmpty(genericValueList)){
            name += "   [转办]";
        }
        instanceTO.put("taskName", name);
        instanceTO.put("startTime", task.getCreateTime());
        instanceTO.put("endTime", task.getDueDate() == null ? "无限制" : UtilDateTime.toDateString(task.getDueDate(), "yyyy-MM-dd HH:mm"));
        if(task.getDueDate() != null){
            int freeMinutes = (task.getDueDate().getTime() - task.getCreateTime().getTime())/1000/60;
            int hours = freeMinutes/60;
            freeMinutes = freeMinutes % 60;
            int days = hours/24;
            hours = hours % 24;
            instanceTO.put("freeTime", days + "天 " + hours + "小时 " + freeMinutes + "分钟");
        }

        instanceTO.put("assigned", StringUtils.isNotBlank(task.getAssignee()));
        String owner = task.getOwner();
        if(StringUtils.isNotBlank(owner)){
            owner = delegator.findOne("Person", UtilMisc.toMap("partyId", owner), true).getString("fullName");
            instanceTO.put("assignee", "(代 " + owner + ")");
        }else{
            instanceTO.put("assignee", staff == null ? "" : staff.getRelatedOne("Person").getString("fullName"));
        }
        instanceTO.put("nextViewType", taskService.getVariableLocal(task.getId(), "nextViewType"));
        instanceTO.put("nextView", taskService.getVariableLocal(task.getId(), "nextView"));
        instanceTO.put("candidates", getTaskExecutor(task.getId()).join(" "));
        objects.add(instanceTO);
    }

    request.setAttribute("taskList", objects);
    return "success";
}

private List<String> getTaskExecutor(String taskId){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<String> candidates = new ArrayList<>();
    List<IdentityLink> links = taskService.getIdentityLinksForTask(taskId);
    for (IdentityLink link : links) {
        if (IdentityLinkType.CANDIDATE.equals(link.getType())) {
            String candidate = link.getUserId();
            if(candidate == null){
                candidate = link.getGroupId();
                if(UtilValidate.isNotEmpty(candidate)){
                    candidate = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", candidate), true).getString("groupName");
                }else{
                    continue;
                }
            }else{
                GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", candidate), true)
                candidate = UtilValidate.isEmpty(person.getString("fullName")) ? person.getString("lastName") + person.getString("firstName") : person.getString("fullName");
            }
            candidates.add(candidate);
        }
    }
    return candidates;
}

public Object processHistory(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    HistoryService historyService = processEngine.getHistoryService();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    String processInstanceId = request.getParameter("processInstanceId");
    String tenantId = request.getAttribute("userTenantId");
    GenericValue loginUser = context.get("userLogin");
    String userId = loginUser.getString("partyId");
    List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();

    HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

    Map<String, Object> instanceTO = new HashMap<>();
    instanceTO.put("taskName", "启动流程");
    instanceTO.put("staffName", StringUtils.isNotBlank(processInstance.getStartUserId()) ? PartyWorker.getPartyName(delegator, processInstance.getStartUserId()): "");
    instanceTO.put("startTime", setDateToString(processInstance.getStartTime()));
    instanceTO.put("endTime", setDateToString(processInstance.getEndTime()));
    objects.add(instanceTO);
    Date onetime = processInstance.getStartTime();
    List<String> taskIdList = new ArrayList<String>();
    List<HistoricActivityInstanceEntity> allotList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).taskAssignee(processInstance.getStartUserId()).orderByHistoricActivityInstanceStartTime().asc().list();
    if(UtilValidate.isNotEmpty(allotList)){
        HistoricActivityInstanceEntity allot = allotList.get(0);
        onetime = processInstance.getStartTime();
        onetime = addObjects(allot.getTaskId(), objects, taskIdList,onetime,allot.getActivityName());
    }
    List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().asc().finished().list();
    Date finashStartTime = null;
    if (list != null && !list.isEmpty()) {
        for (HistoricTaskInstance taskInstance : list) {
            //TODO:并行任务处理
            onetime = addObjects(taskInstance.getId(), objects,taskIdList,onetime,taskInstance.getName());
            instanceTO = new HashMap<>();
            instanceTO.put("taskName", taskInstance.getName());
            instanceTO.put("staffName", StringUtils.isNotBlank(taskInstance.getAssignee()) ? PartyWorker.getPartyName(delegator, taskInstance.getAssignee()): "");
            instanceTO.put("startTime", setDateToString(onetime));
            instanceTO.put("endTime", setDateToString(taskInstance.getEndTime()));
            HistoricVariableInstance var = historyService.createHistoricVariableInstanceQuery().taskId(taskInstance.getId()).variableName("approveComment").singleResult();
            if (var != null) {
                instanceTO.put("approveComment", var.getValue());
            }
            objects.add(instanceTO);
            onetime = taskInstance.getEndTime();
            finashStartTime = taskInstance.getEndTime();
        }
        List<HistoricTaskInstance> allTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().asc().list();
        if(UtilValidate.isNotEmpty(allTaskList)){
            addObjects(allTaskList.get(0).getId(), objects, taskIdList,finashStartTime,allTaskList.get(0).getName());
        }
        String deleteReason = processInstance.getDeleteReason();
        if(!UtilValidate.isEmpty(deleteReason)){
            objects.last().put("approveComment", "流程终止原因: " + deleteReason);
        }
    }

    request.setAttribute("processHistory", objects);
    return "success";
}

public String setDateToString(Date date){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    if(UtilValidate.isNotEmpty(date)){
        return format.format(date);
    }else{
        return "";
    }
}

public Date addObjects(String taskId,List<Map<String, Object>> objects,List<String> taskIdList,Date startTime,String name){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    if(!taskIdList.contains(taskId)) {
        taskIdList.add(taskId)
        List<GenericValue> genericValueList = EntityQuery.use(delegator).from("tblActTaskAllotUser").where(UtilMisc.toMap("taskId", taskId)).orderBy("allotTime").queryList();
        for (GenericValue genericValue : genericValueList) {
            Map map = new HashMap<>();
            map.put("taskName", "[" + name + "]转办给{"+PartyWorker.getPartyName(delegator, genericValue.get("allotUserId").toString())+"}");
            map.put("staffName", PartyWorker.getPartyName(delegator, genericValue.get("userId").toString()));
            String time = setDateToString(format.parse(format.format(genericValue.get("allotTime"))));
            map.put("startTime", setDateToString(startTime));
            map.put("endTime", time);
            map.put("allotStatus", "0");
            objects.add(map);
            startTime = format.parse(time);
        }

    }
    return startTime;
}

public Object convert2Model(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    ManagementService managementService = processEngine.getManagementService();

    String processDefinitionId = request.getParameter("defId");

    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(processDefinitionId).singleResult();
    InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
            processDefinition.getResourceName());
    XMLInputFactory xif = XMLInputFactory.newInstance();
    InputStreamReader isr = new InputStreamReader(bpmnStream, "UTF-8");
    XMLStreamReader xtr = xif.createXMLStreamReader(isr);
    BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

    BpmnJsonConverter converter = new BpmnJsonConverter();
    ObjectNode modelNode = converter.convertToJson(bpmnModel);
    org.activiti.engine.repository.Model modelData = repositoryService.newModel();
    modelData.setKey(processDefinition.getKey());
    modelData.setName(processDefinition.getName() + "[流程抽取]");
    modelData.setCategory(processDefinition.getDeploymentId());

    ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
    modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName() + "[流程抽取]");
    modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
    modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription() == null ? "" : processDefinition.getDescription());
    modelData.setMetaInfo(modelObjectNode.toString());

    String tenantId = request.getAttribute("userTenantId");
    modelData.setTenantId(tenantId);

    repositoryService.saveModel(modelData);

    repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));
    return EventUtil.returnSuccess(request, "流程抽取成功，请至流程模型页面编辑");
//    request.setAttribute("data", "流程抽取成功，请至流程模型页面编辑");
//    request.setAttribute("status", true);
//    return "success";
}

public Object deleteProcess(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();

    String processDefinitionId = request.getParameter("defId");
    repositoryService.deleteDeployment(processDefinitionId, true);
    return EventUtil.returnSuccess(request, "删除成功");
//    request.setAttribute("data", "删除成功");
//    request.setAttribute("status", true);
//    return "success";
}

public Object toggleProcessSuspend(){
    String msg = "";
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String defId = request.getParameter("defId");
        ProcessDefinition definition = repositoryService.getProcessDefinition(defId);
        if(definition.suspended){
            repositoryService.activateProcessDefinitionById(defId, true, null);
            msg = "流程已激活";
        }else{
            repositoryService.suspendProcessDefinitionById(defId, true, null);
            msg = "流程已挂起";
        }

    } catch (Exception e) {
        Debug.logError(e, "流程激活/挂起失败", this.getClass().getName());
        throw new RuntimeException("流程激活/挂起失败");
    }
    return EventUtil.returnSuccess(request, msg);
//    request.setAttribute("data", msg);
//    request.setAttribute("status", true);
//    return "success";
}

public Object passTask(){
    flowTask(true);
}

public Object rejectTask(){
    flowTask(false);
}

public Object flowTask(boolean pass){
    String msg = "操作成功";
    String error = "流程操作失败";
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService = processEngine.getIdentityService();
        HistoryService historyService = processEngine.getHistoryService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        TaskService taskService = processEngine.getTaskService();
        String taskId = request.getParameter("taskId");
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstanceId = task.getProcessInstanceId();

        Map<String, String[]> formProperties = new HashMap<String, String[]>();
        Map<String, String> wfCandidate = new HashMap<>();

        // 从request中读取参数然后转换
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            // fp_的意思是form paremeter
            String keyString = StringUtils.defaultString(key)
            if (keyString.startsWith("fp_")) {
                String name = key.split("_")[1];
                formProperties.put(name, entry.getValue().size() == 1 ? entry.getValue()[0] : entry.getValue());
            }else if (keyString.startsWith("wfCandidates_")) {
                String name = key.split("_")[1];
                wfCandidate.put(name, entry.getValue()[0]);
            }
        }

        String userId = ((GenericValue)context.get("userLogin")).getString("partyId");
        identityService.setAuthenticatedUserId(userId);
        //即使当前执行人为候选人，执行后自动设置为执行人
        taskService.setAssignee(taskId, userId);
        taskService.setVariable(taskId, "workflow_last_executor", userId);//TODO:如果是会签，上一步执行人会覆盖，影响下一步任务默认执行人？

        //设置externalLoginKey便于流程监听器获取登录用户信息
        taskService.setVariable(taskId, "externalLoginKey", request.getParameter("externalLoginKey"));

        //选择路径
        String nextActivityId = (String)request.getParameter("nextWay");
        if(!pass){

            //如果有一条自定义的“驳回”路径则执行该路径
            ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId()); //this can be cached
            List<PvmTransition> transitions = pde.findActivity(task.getTaskDefinitionKey()).getOutgoingTransitions();
            if(UtilValidate.isNotEmpty(transitions)){
                for (PvmTransition transition : transitions) {
                    String actionName = (String) transition.getProperty("name");
                    if("驳回".equals(actionName)){
                        ActivityImpl destination = (ActivityImpl) transition.getDestination();
                        ActivityBehavior activityBehavior = destination.getActivityBehavior();
                        if(activityBehavior instanceof UserTaskActivityBehavior){
                            UserTaskActivityBehavior userTask = (UserTaskActivityBehavior)activityBehavior;
                            nextActivityId = userTask.getTaskDefinition().getKey();
                        }
                        break;
                    }
                }
            }
            //如果没有自定义的驳回路径则查询上一个任务节点
            if(UtilValidate.isEmpty(nextActivityId)){
                List<HistoricTaskInstance> allTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().orderByHistoricTaskInstanceEndTime().asc().list();
                if(UtilValidate.isNotEmpty(allTaskList)) {
                    HistoricTaskInstance historicTaskInstance = null;
                    for (int i = 0; i < allTaskList.size(); i++) {
                        HistoricTaskInstance taskValue = allTaskList.get(i)
                        if (task.getTaskDefinitionKey().equals(taskValue.getTaskDefinitionKey())) {
                            if ((i - 1) < 0) {
                                error = "没有上层流程可驳回！";
                                throw new RuntimeException(error);
                            } else {
                                historicTaskInstance = allTaskList.get(i-1);
                                break;
                            }
                        }else{
                            historicTaskInstance = allTaskList.get(i);
                        }
                    }
                    if (UtilValidate.isNotEmpty(historicTaskInstance)) {
                        if (task.getTaskDefinitionKey().equals(historicTaskInstance.getTaskDefinitionKey())) {
                            List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).finished().orderByHistoricTaskInstanceEndTime().asc().list();
                            if (UtilValidate.isNotEmpty(taskList)) {
                                error = "没有上层流程可驳回！";
                                throw new RuntimeException(error);
                            }
                        }
                        nextActivityId = historicTaskInstance.getTaskDefinitionKey();
                        GenericValue genericValue = EntityQuery.use(delegator).select().from("ActTaskExtUser").where(UtilMisc.toMap("procDefId",historicTaskInstance.getProcessDefinitionId(),"taskId", nextActivityId)).queryOne();
                        String id = genericValue.get("userId");
                        List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblDataScope").where(UtilMisc.toMap("dataId", id)).queryList();
                        String user = "";
                        String dept = "";
                        String level = "";
                        String position = "";
                        for (GenericValue genericValue1 : genericValueList) {
                            String type = genericValue1.get("scopeType");
                            if (type.equals("SCOPE_USER")) {
                                user += genericValue1.get("scopeValue") + ",";
                            } else if (type.equals("SCOPE_DEPT_ONLY")) {
                                dept += genericValue1.get("scopeValue") + ",";
                            } else if (type.equals("SCOPE_LEVEL_ONLY")) {
                                level += genericValue1.get("scopeValue") + ",";
                            } else if (type.equals("SCOPE_POSITION_ONLY")) {
                                position += genericValue1.get("scopeValue") + ",";
                            }
                        }
                        wfCandidate.put("user", user);
                        wfCandidate.put("dept", dept);
                        wfCandidate.put("level", level);
                        wfCandidate.put("position", position);
                        wfCandidate.put("processReject", "true");
                    } else {
                        error = "没有上层流程可驳回！";
                        throw new RuntimeException(error);
                    }
                }else{
                    error = "没有上层流程可驳回！";
                    throw new RuntimeException(error);
                }
            }

        }

        if(UtilValidate.isEmpty(nextActivityId)){//根据流程设置的路径条件自动判定选择哪一条路径
            nextActivityId = setnextActivityIdInfo(task,repositoryService,formProperties);
        }
//        if(pass){
            if(UtilValidate.isNotEmpty(nextActivityId)){//用户选择执行路径
                ManagementService managementService = processEngine.getManagementService();
                if(UtilValidate.isEmpty(wfCandidate.get("user")) &&
                        UtilValidate.isEmpty(wfCandidate.get("dept")) &&
                        UtilValidate.isEmpty(wfCandidate.get("level")) &&
                        UtilValidate.isEmpty(wfCandidate.get("position"))
                ){//如果页面未指定人员则按任务配置时设置
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("ActTaskExtUser").where(UtilMisc.toMap("procDefId",task.getProcessDefinitionId(),"taskId", nextActivityId)).queryOne();
                    if(genericValue != null){
                        String id = genericValue.get("userId");
                        List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblDataScope").where(UtilMisc.toMap("dataId", id)).queryList();
                        String user = "";
                        String dept = "";
                        String level = "";
                        String position = "";
                        if(UtilValidate.isNotEmpty(genericValueList)) {
                            for (GenericValue genericValue1 : genericValueList) {
                                String type = genericValue1.get("scopeType");
                                if (type.equals("SCOPE_USER")) {
                                    user += genericValue1.get("scopeValue") + ",";
                                } else if (type.equals("SCOPE_DEPT_ONLY")) {
                                    dept += genericValue1.get("scopeValue") + ",";
                                } else if (type.equals("SCOPE_LEVEL_ONLY")) {
                                    level += genericValue1.get("scopeValue") + ",";
                                } else if (type.equals("SCOPE_POSITION_ONLY")) {
                                    position += genericValue1.get("scopeValue") + ",";
                                }
                            }
                            wfCandidate.put("user", user);
                            wfCandidate.put("dept", dept);
                            wfCandidate.put("level", level);
                            wfCandidate.put("position", position);
                        }else{
                            wfCandidate.put("user", userId);
                        }
                    }else{
                        wfCandidate.put("user", userId);
                    }

                }
                //跳转到指定的task
                taskService.setVariable(taskId, "workflow_candidates", wfCandidate);
                String reason = request.getParameter("fp_approveComment");
                managementService.executeCommand(new ChooseTaskCmd(taskId, nextActivityId, reason, formProperties));
            }else{

                //按流程定义自动跳转
                //TODO:如果是会审，某个会审分组驳回该如何操作
                taskService.setVariable(taskId, "workflow_candidates", wfCandidate);//changeby pzq@20160703 自动跳转时如果页面设置了执行人
                taskService.setVariablesLocal(taskId, formProperties);
                Map<String, Object> submitMap = new HashMap<>();
                submitMap.put("pass", pass);//设置判断节点的标志位
                taskService.complete(taskId, submitMap, false);
            }

        //设置任务跳转后新任务的执行期限
        String wfDueDate = (String)request.getParameter("wfDueDate");
        if(UtilValidate.isNotEmpty(wfDueDate)){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dueDate = sf.parse(wfDueDate);

            List<Task> newTasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if(UtilValidate.isNotEmpty(newTasks)){
                for (Task newTask: newTasks) {
                    newTask.setDueDate(dueDate);
                    taskService.saveTask(newTask);
                }
            }
        }

        //TODO:任务通知
        String wfNoticeType = (String)request.getParameter("wfNoticeType");

//        }
//        else{
//            formProperties.put("pass", pass);//设置判断节点的标志位
//            //TODO:如果是会审，某个会审分组驳回该如何操作
//            taskService.complete(taskId, formProperties, true);
////
//        }
    } catch (Exception e) {
        Debug.logError(e, error, this.getClass().getName());
        throw new RuntimeException(error);
    }
    return EventUtil.returnSuccess(request, msg);
//    request.setAttribute("data", msg);
//    request.setAttribute("status", true);
//    return "success";
}

public String setnextActivityIdInfo(Task task, RepositoryService repositoryService, Map<String, Object> formProperties){
    String nextActivityId = "";
    ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
            .getDeployedProcessDefinition(task.getProcessDefinitionId());
    List<List<TransitionImpl>> activities = pde.getActivities().outgoingTransitions as List<List<TransitionImpl>>;
    String destination = "";
    for (List<PvmTransition> activityList :  activities) {
        for(PvmTransition pvmTransition : activityList){
            if(task.getTaskDefinitionKey().equals(pvmTransition.getSource().getId())){
                destination = pvmTransition.getDestination().getId();
                break;
            }
        }
    }
    String formId = request.getParameter("viewFormId");
    List<GenericValue> chooserList = EntityQuery.use(delegator).select().from("searchFormChooserValue").where(UtilMisc.toMap("fromId",destination,"processDefinitionId",task.getProcessDefinitionId(),"formId",formId)).queryList();
    for(GenericValue genericValue : chooserList){
        Boolean pass = false;
        String chooserId = genericValue.get("chooserId");
        chooserId = chooserId.substring(3,chooserId.length());
        String chooserformValue = formProperties.get(chooserId);
        if(UtilValidate.isNotEmpty(chooserformValue)){
            String operator = genericValue.get("operatorType");
            String type = genericValue.get("type");
            String chooserValue = genericValue.get("value");
            if (type.equals("int")){
                Integer value= Integer.parseInt(chooserformValue);
                Integer formValue= Integer.parseInt(chooserValue);
                if(operator.equals("equals")){
                    pass = (value == formValue);
                }else if(operator.equals("greater")){
                    pass = (value > formValue);
                }else if(operator.equals("less")){
                    pass = (value< formValue);
                }else if(operator.equals("notEquals")){
                    pass = (value != formValue);
                }
            }else{
                if(operator.equals("equals")){
                    pass = chooserValue.equals(chooserformValue)
                }else if(operator.equals("contain")){
                    pass = chooserValue.contains(chooserformValue);
                }else if(operator.equals("notEquals")){
                    pass = !chooserValue.equals(chooserformValue)
                }
            }
        }
        if(pass){
            nextActivityId = genericValue.get("toId");
            break;
        }
    }
    return nextActivityId;
}



public Object backToStart(){
    String msg = "操作成功";
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ManagementService managementService = processEngine.getManagementService();
        TaskService taskService = processEngine.getTaskService();
        String taskId = request.getParameter("taskId");

        String reason = request.getParameter("fp_approveComment");
        if(UtilValidate.isEmpty(reason)){
            reason = "驳回至流程发起者";
        }
        Map<String, Object> formProperties = new HashMap<String, Object>();
        // 从request中读取参数然后转换
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            // fp_的意思是form paremeter
            if (StringUtils.defaultString(key).startsWith("fp_")) {
                String name = key.split("_")[1];
                formProperties.put(name, entry.getValue().size() == 1 ? entry.getValue()[0] : entry.getValue());
            }
        }
        String userId = ((GenericValue)context.get("userLogin")).getString("partyId");
        taskService.setVariable(taskId, "workflow_last_executor", userId)

        //设置externalLoginKey便于流程监听器获取登录用户信息
        taskService.setVariable(taskId, "externalLoginKey", request.getParameter("externalLoginKey"));
        managementService.executeCommand(new ChooseTaskCmd(taskId, null, reason, formProperties));

    } catch (Exception e) {
        Debug.logError(e, "流程操作失败", this.getClass().getName());
        throw new RuntimeException("流程操作失败");
    }
    return EventUtil.returnSuccess(request, msg);
//    request.setAttribute("data", msg);
//    request.setAttribute("status", true);
//    return "success";
}

public Object terminateTask(){
    String msg = "操作成功";
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String processInstanceId = request.getParameter("processInstanceId");
        String reason = request.getParameter("fp_approveComment");
        if(UtilValidate.isEmpty(reason)){
            reason = "终止流程";
        }
        runtimeService.deleteProcessInstance(processInstanceId, reason);

    } catch (Exception e) {
        Debug.logError(e, "流程操作失败", this.getClass().getName());
        throw new RuntimeException("流程操作失败");
    }
    return EventUtil.returnSuccess(request, msg);
//    request.setAttribute("data", msg);
//    request.setAttribute("status", true);
//    return "success";
}

public Object uploadProcess(){
    String msg = "";
    String tenantId = request.getAttribute("userTenantId");
    try {
        ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
        InputStream fileInputStream = file.getInputStream();
        String extension = FilenameUtils.getExtension(fileName);
        DeploymentBuilder deployment = repositoryService.createDeployment();
        if(StringUtils.isNotBlank(tenantId)){
            deployment = deployment.tenantId(tenantId);
        }
        if (extension.equals("zip") || extension.equals("bar")) {
            ZipInputStream zip = new ZipInputStream(fileInputStream);
            deployment.tenantId(tenantId).addZipInputStream(zip).deploy();
        } else if (extension.equals("png")) {
            deployment.tenantId(tenantId).addInputStream(fileName, fileInputStream).deploy();
        } else if (fileName.endsWith("bpmn20.xml")) {
            deployment.tenantId(tenantId).addInputStream(fileName, fileInputStream).deploy();
        } else if (extension.equals("bpmn")) {                /*
                 * bpmn扩展名特殊处理，转换为bpmn20.xml
				 */
            String baseName = FilenameUtils.getBaseName(fileName);
            deployment.tenantId(tenantId).addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
        } else {
            throw new RuntimeException("仅支持.zip .png .bpmn20.xml .bpmn结尾的文件格式");
        }
    } catch (Exception e) {
        logger.error("流程部署错误", e);
    }
    return EventUtil.returnSuccess(request, msg);
//    request.setAttribute("data", msg);
//    request.setAttribute("status", true);
//    return "success";
}

public Object exportProcess(){
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        String defId = request.getParameter("defId");
        String resourceType = request.getParameter("resourceType");

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(defId).singleResult();
        String resourceName = "";
        String fileExt = "";
        String contentType = "";
        String fileName = null;
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
            fileExt = "png";
            contentType = "image/png";
        } else if (resourceType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
            fileExt = "xml";
            contentType = "text/xml";
            fileName = processDefinition.name + "." + "xml";
        }
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

        if (resourceType.equals("image")) {//解决fancybox 显示图片问题
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } else if (resourceType.equals("xml")) {
            UtilHttp.streamContentToBrowser(response, resourceAsStream, resourceAsStream.available(), contentType, fileName);
        }

    } catch (Exception e) {
        Debug.logError(e, "导出流程文件失败", this.getClass().getName());
        throw new RuntimeException("导出流程文件失败");
    }
    return null;
}

public Object formList(){
    List<GenericValue> formList = EntityQuery.use(delegator).from("ActExtForm").orderBy("formId DESC").queryList();
    request.setAttribute("formList", formList);
    return "success";
}

public Object initActForm(){
    String formId = request.getParameter("formId");
    if(UtilValidate.isNotEmpty(formId)){
        GenericValue formRow = delegator.findOne("ActExtForm", UtilMisc.toMap("formId", formId), true);
        Map<String, Object> formData = Maps.newHashMap(formRow);
        request.setAttribute("form", formData);
//        EntityQuery.use(delegator).from("ActExtFormContent").where(UtilMisc.toMap("formId", formId)).queryOne()
        GenericValue content = delegator.findOne("ActExtFormContent", UtilMisc.toMap("formId", formId), true);
        List<GenericValue> chooserList = EntityQuery.use(delegator).from("TblActExtFormChooserInfo").where(UtilMisc.toMap("formId", formId)).queryList();
        String chooserInfo = "";
        String chooserValue = "";
        if(UtilValidate.isNotEmpty(chooserList)) {
            for (GenericValue genericValue : chooserList) {
                chooserInfo += genericValue.get("id") + "," + genericValue.get("name") + "|" + genericValue.get("type") + ";";
                List<GenericValue> chooserValueList = EntityQuery.use(delegator).from("TblActChooserInfoValue").where(UtilMisc.toMap("formId", formId, "chooserId", genericValue.get("id"))).queryList();
                if(UtilValidate.isNotEmpty(chooserValueList)) {
                    String values = "";
                    for (GenericValue chooser : chooserValueList) {
                        values += chooser.get("chooserKey") + "=" + chooser.get("chooserValue") + ";"
                    }
                    chooserValue += genericValue.get("id") + "," + values + "-";
                }
            }
        }
        request.setAttribute("chooserValue",chooserValue)
        request.setAttribute("chooserInfo",chooserInfo)
        if(content != null){
            formData.put("content", content.get("content"));
        }
    }
    return "success";
}

public Object initActTaskExtForm(){
    String taskId = request.getParameter("taskId");
    String procDefId = request.getParameter("procDefId");
    if(UtilValidate.isNotEmpty(taskId) && UtilValidate.isNotEmpty(procDefId)){
        request.setAttribute("taskForm", delegator.findOne("ActTaskExtForm", UtilMisc.toMap("taskId", taskId, "procDefId", procDefId), true));

        List<GenericValue> taskUser = delegator.findByAnd("ActTaskExtUser", UtilMisc.toMap("taskId", taskId, "procDefId", procDefId), null, true);
        if(taskUser != null && taskUser.size() > 0){
            request.setAttribute("userId", taskUser.get(0).getString("userId"));
        }else{//默认创建一个数据范围限定类型的责任人
            String userId = delegator.getNextSeqId("ActTaskExtUser")
            GenericValue user = delegator.makeValidValue("ActTaskExtUser", UtilMisc.toMap("userId", userId, "taskId", taskId, "procDefId", procDefId, "userType", "dataScope"));
            user.create();
            request.setAttribute("userId", userId);
        }

    }
    request.setAttribute("forms", delegator.findAll("ActExtForm", true));

    return "success";
}

public Object initActTaskUser(){
    String procDefId = request.getParameter("procDefId");
    String taskId = request.getParameter("taskId");

    List<GenericValue> users = delegator.findByAnd("ActTaskExtUser", UtilMisc.toMap("procDefId", procDefId, "taskId", taskId));
    request.setAttribute("userList", users);
    return "success";
}

public Object processTaskData4Client(){
    String taskId = request.getParameter("taskId");
    Map<String, Object> data = new HashMap<>();
    ActivitiProcessUtils.prepareTaskFormData(data, taskId);
    request.setAttribute("taskData", data);
    return "success";
}

public Object setAllot(){
    String msg = "操作成功";
    GenericValue loginUser = context.get("userLogin");
    String partyId = loginUser.getString("partyId");
    try {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String taskId = request.getParameter("taskId");
        String userId = request.getParameter("userId");
        processEngine.getTaskService().setAssignee(taskId, userId);
        Map<String,Object> map = new HashMap<String,Object>();
        String id = delegator.getNextSeqId("tblActTaskAllotUser").toString();
        map.put("id",id);
        map.put("userId",partyId);
        map.put("taskId",taskId);
        map.put("allotUserId",userId);
        map.put("allotTime",new Timestamp(new Date().getTime()));
        GenericValue allotUser = delegator.makeValidValue("tblActTaskAllotUser", map);
        allotUser.create();
    } catch (Exception e) {
        Debug.logError(e, "流程操作失败", this.getClass().getName());
        throw new RuntimeException("流程操作失败");
    }
    return EventUtil.returnSuccess(request, msg);
}

public Object initLineActTaskExtForm(){
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    String sourceId = request.getParameter("sourceId");
    String targetId = request.getParameter("targetId");
    String processDefinitionId = request.getParameter("processDefinitionId");
    ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
            .getDeployedProcessDefinition(processDefinitionId);
    List<List<TransitionImpl>> activities = pde.getActivities().outgoingTransitions as List<List<TransitionImpl>>;
    Boolean flag = false;
    Map map = new HashMap();
    map.put("flag",flag);
    for (List<PvmTransition> activityList :  activities) {
        if(!Boolean.parseBoolean(map.get("flag").toString())) {
            getWay(activityList,map,targetId,sourceId);
        }else{
            break;
        }
    }
    GenericValue form = EntityQuery.use(delegator).select().from("ActTaskExtForm").where(UtilMisc.toMap("taskId",map.get("taskId"),"procDefId",processDefinitionId)).queryOne();
    if(UtilValidate.isNotEmpty(form)) {
        String formId = form.get("formId");
        List<GenericValue> chooserList = EntityQuery.use(delegator).from("TblActExtFormChooserInfo").where(UtilMisc.toMap("formId", formId)).queryList();
        request.setAttribute("chooserList", chooserList);
        Map<String, Object> chooserMap = new HashMap<>();
        for (GenericValue genericValue : chooserList) {
            String id = genericValue.get("id");
            GenericValue chooserValue = EntityQuery.use(delegator).select().from("TblActExtFormChooserValue").where(UtilMisc.toMap("chooserId", id, "fromId", sourceId, "toId", targetId,"processDefinitionId",processDefinitionId)).queryOne();
            if (UtilValidate.isNotEmpty(chooserValue)) {
                chooserMap.put("id", chooserValue.get("id"));
                chooserMap.put("operatorType", chooserValue.get("operatorType"));
                chooserMap.put("chooserId", chooserValue.get("chooserId"));
                chooserMap.put("value", chooserValue.get("value"));
            }
        }
//        if(UtilValidate.isNotEmpty(chooserMap)) {
            request.setAttribute("chooserMap", chooserMap);
//        }else{
//            request.setAttribute("msg","请选择下个任务节点为用户任务节点的流程线！")
//        }
    }else{
        request.setAttribute("msg","请确认上个节点为用户任务节点的流程线并配置表单信息！")
    }
    return "success";
}

private static void getWay(List<PvmTransition> transitions,Map map, String targetId, String sourceId){
    if(transitions == null || transitions.size() == 0){
        return;
    }
    for (PvmTransition activity: transitions) {
        ActivityImpl destination = (ActivityImpl) activity.getDestination();
        ActivityImpl source = (ActivityImpl) activity.getSource();
        ActivityBehavior activityBehavior = destination.getActivityBehavior();

        ActivityBehavior sourceactivityBehavior = source.getActivityBehavior();
        if(sourceactivityBehavior instanceof UserTaskActivityBehavior && source.getId().equals(sourceId) && destination.getId().equals(targetId)){
            map.put("flag",true);
            map.put("taskId",source.getId());
            break;
        }else if(destination.getId().equals(targetId)){
//                ActivityImpl getsourceId = (ActivityImpl) activity.getSource();
            getsourceId(activity.getSource().getIncomingTransitions(),map, sourceId);
        }else if(activityBehavior instanceof ExclusiveGatewayActivityBehavior){
            getWay(destination.getOutgoingTransitions(), map,targetId,sourceId);
        }
    }
}

private static void getsourceId(List<PvmTransition> transitions,Map map,String sourceId){
    if(UtilValidate.isNotEmpty(transitions)){
        for(PvmTransition activity: transitions){
            ActivityImpl source = (ActivityImpl) activity.getSource();
            ActivityBehavior activityBehavior = source.getActivityBehavior() ;
            if(activityBehavior instanceof UserTaskActivityBehavior && activity.getDestination().getId().equals(sourceId)){
                map.put("flag",true);
                map.put("taskId",activity.getSource().getId());
                break;
            }else {
                if(activity.getSource().getIncomingTransitions().size() > 1){
                    getsourceId(activity.getSource().getIncomingTransitions(), map,sourceId);
                }
            }
        }
    }
}