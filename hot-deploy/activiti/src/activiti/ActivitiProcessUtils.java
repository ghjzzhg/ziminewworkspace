package activiti;

import com.rextec.workflow.RextecNoneEndEventActivityBehavior;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Collections3;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by galaxypan on 16.1.31.
 */
public class ActivitiProcessUtils {

    public static Map prepareTaskFormData(Map<String, Object> context, String taskId){
        try {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            RepositoryService repositoryService = processEngine.getRepositoryService();
            RuntimeService runtimeService = processEngine.getRuntimeService();
            IdentityService identityService = processEngine.getIdentityService();
            TaskService taskService = processEngine.getTaskService();
            HistoryService historyService = processEngine.getHistoryService();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            context.put("taskName", task.getName());

            String processInstanceId = task.getProcessInstanceId();
            context.put("processInstanceId", processInstanceId);
            context.put("processDefinitionId", task.getProcessDefinitionId());

            List<HistoricTaskInstance> allTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().orderByHistoricTaskInstanceEndTime().asc().list();
            List<Map<String,Object>> hiList = new ArrayList<Map<String,Object>>();
            for(HistoricTaskInstance historicTaskInstance : allTaskList){
                List<HistoricVariableInstance> allHistoryList = historyService.createHistoricVariableInstanceQuery().taskId(historicTaskInstance.getId()).list();
                List<Map<String,Object>> newHistoryList = new ArrayList<Map<String,Object>>();
                Map<String,Object> himap = new HashMap<String,Object>();
                String viewId = "";
                for(HistoricVariableInstance instance : allHistoryList){
                    HistoricVariableInstanceEntity entity = (HistoricVariableInstanceEntity)instance;
                    if(UtilValidate.isNotEmpty(entity.getTaskId()) && !"nextViewType".equals(entity.getName()) && !"nextView".equals(entity.getName())){
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("formName","fp_"+entity.getName());
                        map.put("textValue",entity.getTextValue());
                        newHistoryList.add(map);
                    }else if("nextView".equals(entity.getName())){
                        viewId = entity.getTextValue();
                    }
                }
                himap.put("viewId",viewId);
                himap.put("historyList", newHistoryList);
                hiList.add(himap);
            }
            context.put("allTaskHistoryList",hiList);
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            context.put("businessKey", processInstance.getBusinessKey());//业务id

            //获取下一步所有可以执行的路径
            ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId()); //this can be cached
            context.put("processName", pde.getName());
            context.put("processKey", pde.getKey());//流程标识，客户端可据此判断是哪项业务的流程，结合buinessKey获取对应的数据
            List<PvmTransition> transitions = pde.findActivity(task.getTaskDefinitionKey()).getOutgoingTransitions();
            List<Map<String, Object>> nextWays = new ArrayList<>();
            Map map = new HashMap();
            map.put("flag","0");
            Delegator delegator = (Delegator)context.get("delegator");

            getNextWays(transitions, nextWays, map , delegator);
            List<GenericValue> genericValues = EntityQuery.use(delegator).select().from("TblActExtFormChooserValue").where(UtilMisc.toMap("sourceId",task.getDescription())).queryList();
            if(UtilValidate.isNotEmpty(genericValues)){
                map.put("flag","0");
            }
            context.put("isBranchParameter", map.get("flag"));
            //只有一条路可以走就没必要选择路径了
            if(nextWays.size() > 1){
                context.put("nextWays", nextWays);
            }else if(nextWays.size() == 1){
                context.put("passBtnLabel", nextWays.get(0).get("name"));
            }

            //获取流程中所有的任务节点
            List<Map<String, Object>> allWays = new ArrayList<>();
            List<ActivityImpl> activities = pde.getActivities();
            for (ActivityImpl activity : activities) {
                ActivityBehavior activityBehavior = activity.getActivityBehavior();
                Map<String, Object> way = new HashMap<>();
                if(activityBehavior instanceof UserTaskActivityBehavior){
                    UserTaskActivityBehavior userTask = (UserTaskActivityBehavior) activityBehavior;
                    Expression nameExpression = userTask.getTaskDefinition().getNameExpression();
                    way.put("name", nameExpression == null ? "未知任务名称" : nameExpression.getExpressionText());
                    way.put("activityId", userTask.getTaskDefinition().getKey());
                    allWays.add(way);
                }else if(activityBehavior instanceof RextecNoneEndEventActivityBehavior){
                    RextecNoneEndEventActivityBehavior endEvent = (RextecNoneEndEventActivityBehavior)activityBehavior;
                    EndEvent oriEndEvent = endEvent.getEndEvent();
                    String name = oriEndEvent.getName();
                    way.put("name", UtilValidate.isEmpty(name) ? "结束" : name);
                    way.put("activityId", oriEndEvent.getId());
                    allWays.add(way);
                }
            }
            context.put("allWays", allWays);
            //获取之前提交的表单数据
            //TODO:此处对自定义表单回填数据是否有影响？
            Map<String, Object> formData = new HashMap<>();
            //流程全局变量
            Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
            formData.putAll(processVariables);

            List<HistoricTaskInstance> historyTasks = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().asc().finished().list();
            for (HistoricTaskInstance historyTask : historyTasks) {
                Map<String, Object> multipleTasks = null;
                List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().taskId(historyTask.getId()).list();
                for (HistoricVariableInstance var : variableInstances) {
                    formData.put(var.getVariableName(), var.getValue());
                }
            }
            for (Map.Entry<String, Object> data : formData.entrySet()) {
                context.put("fp_" + data.getKey(), data.getValue());
            }
//
////            JSON json = JSON.from(formData);
//            context.put("formDataJsonStr", formData);按任务执行先后顺序，最后的同名字段覆盖之前的数据。审批意见除外

        } catch (Exception e) {
            Debug.logError(e, "流程任务初始化失败", ActivitiProcessUtils.class.getName());
            throw new RuntimeException("流程任务初始化失败");
        }
        return context;
    }

    private static String formatDate(Object date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static void getNextWays(List<PvmTransition> transitions, List<Map<String, Object>> nextWays,Map<String, Object> map, Delegator delegator) throws GenericEntityException {
        if(transitions == null || transitions.size() == 0){
            return;
        }
        for (PvmTransition transition: transitions) {
            ActivityImpl destination = (ActivityImpl) transition.getDestination();
            ActivityBehavior activityBehavior = destination.getActivityBehavior();
            if(activityBehavior instanceof UserTaskActivityBehavior){
                Map<String, Object> way = new HashMap<>();
                UserTaskActivityBehavior userTask = (UserTaskActivityBehavior)activityBehavior;
                String actionName = (String) transition.getProperty("name");
                if(UtilValidate.isEmpty(actionName)){//显示执行路径名称，如果没有则显示任务名称
                    Expression nameExpression = userTask.getTaskDefinition().getNameExpression();
                    actionName = nameExpression == null ? "未知任务名称" : nameExpression.getExpressionText();
                }
                way.put("name", actionName);
                way.put("activityId", userTask.getTaskDefinition().getKey());
                if(!UtilValidate.isNotEmpty(transition.getProperty("conditionText"))){
                    map.put("flag","1");
                }
                nextWays.add(way);
            }else if(activityBehavior instanceof ExclusiveGatewayActivityBehavior){
                getNextWays(destination.getOutgoingTransitions(), nextWays,map, delegator);
            }else if(activityBehavior instanceof RextecNoneEndEventActivityBehavior){
                Map<String, Object> way = new HashMap<>();
                RextecNoneEndEventActivityBehavior endEvent = (RextecNoneEndEventActivityBehavior)activityBehavior;
                EndEvent oriEndEvent = endEvent.getEndEvent();
                String name = oriEndEvent.getName();
                way.put("name", UtilValidate.isEmpty(name) ? "结束" : name);
                way.put("activityId", oriEndEvent.getId());
                nextWays.add(way);
            }
        }
    }


    public static Map<String, Object> createActForm(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String serviceName = "createActForm";
        ModelService modelService = null;
        try {
            modelService = ctx.getModelService(serviceName);
            Map<String, Object> serviceContext = modelService.makeValid(context, ModelService.IN_PARAM);
            result = ctx.getDispatcher().runSync(serviceName, serviceContext);
            if(ServiceUtil.isSuccess(result) && "USER_SCREEN".equals(context.get("formType"))){
                Object formId = serviceContext.get("formId");
                if(!UtilValidate.isNotEmpty(formId)){
                    formId = result.get("formId");
                }
                Delegator delegator = ctx.getDelegator();
                GenericValue formContent = delegator.makeValidValue("ActExtFormContent", "formId", formId, "content", context.get("content"));
                delegator.create(formContent);
                String chooserList = context.get("hiddenList").toString();
                Object chooserValue = context.get("valueList");
                saveHiddenChooserInfo(chooserList,chooserValue,formId.toString(),delegator,context.get("content").toString());
            }
        } catch (GenericServiceException | GenericEntityException e) {
            Debug.logError(e, ActivitiProcessUtils.class.getName());
        }
        return result;
    }

    public static Map<String, Object> updateActForm(DispatchContext ctx, Map<String,Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String serviceName = "updateActForm";
        ModelService modelService;
        try {
            modelService = ctx.getModelService(serviceName);
            Map<String, Object> serviceContext = modelService.makeValid(context, ModelService.IN_PARAM);
            result = ctx.getDispatcher().runSync(serviceName, serviceContext);
            if(ServiceUtil.isSuccess(result)){
                Object formId = serviceContext.get("formId");
                Delegator delegator = ctx.getDelegator();
                GenericValue formContent = delegator.findOne("ActExtFormContent", UtilMisc.toMap("formId", formId), false);
                if(formContent == null){
                    if("USER_SCREEN".equals(context.get("formType"))){
                        formContent = delegator.makeValidValue("ActExtFormContent", "formId", formId, "content", context.get("content"));
                        delegator.create(formContent);
                    }
                }else{
                    if("USER_SCREEN".equals(context.get("formType"))){
                        formContent.set("content", context.get("content"));
                        delegator.store(formContent);
                    }else{
                        delegator.removeValue(formContent);
                    }
                }
                if(UtilValidate.isNotEmpty(context.get("hiddenList"))){
                    String chooserList = context.get("hiddenList").toString();
                    List<GenericValue> genericValues = EntityQuery.use(delegator).from("TblActExtFormChooserInfo").where(UtilMisc.toMap("formId",formId)).queryList();
                    for(GenericValue genericValue : genericValues){
                        delegator.removeByAnd("TblActExtFormChooserValue",UtilMisc.toMap("chooserId",genericValue.get("id")));
                    }
                    delegator.removeByAnd("TblActExtFormChooserInfo",UtilMisc.toMap("formId",formId));
                    delegator.removeByAnd("TblActChooserInfoValue",UtilMisc.toMap("formId",formId));
                    if(UtilValidate.isNotEmpty(context.get("valueList"))){
                        Object chooserValue = context.get("valueList");
                        Object content = context.get("content");
                        if(UtilValidate.isEmpty(content)){
                            content = "";
                        }
                        saveHiddenChooserInfo(chooserList,chooserValue, formId.toString(),delegator,content.toString());
                    }
                }
            }
        } catch (GenericServiceException | GenericEntityException e) {
            Debug.logError(e, ActivitiProcessUtils.class.getName()) ;
        }
        return result;
    }

    private static void saveHiddenChooserInfo(String chooserList, Object chooserValue, String formId, Delegator delegator,String content) throws GenericEntityException {
        String[] choosers = chooserList.split(";");
        for(String chooserInfo : choosers){
            String id = chooserInfo.substring(0,chooserInfo.indexOf(","));
            if(content.contains(id)){
                String name = chooserInfo.substring(chooserInfo.indexOf(",") + 1, chooserInfo.indexOf("|"));
                String type = chooserInfo.substring(chooserInfo.indexOf("|") + 1, chooserInfo.length());
                Map<String, Object> map = new HashMap<>();
                map.put("id",id);
                map.put("name",name);
                map.put("type",type);
                map.put("formId",formId);
                GenericValue chooserGeneric = delegator.makeValidValue("TblActExtFormChooserInfo", map);
                chooserGeneric.create();
            }
        }
        if(UtilValidate.isNotEmpty(chooserValue)) {
            String[] chooserValueList = chooserValue.toString().split("-");

            for (String valueList : chooserValueList) {
                String chooserId = valueList.substring(0, valueList.indexOf(","));
                String chooserValues = valueList.substring(valueList.indexOf(",") + 1, valueList.length());
                String[] values = chooserValues.split(";");
                for (String value : values) {
                    Map<String, Object> map = new HashMap<>();
                    String id = delegator.getNextSeqId("TblActExtFormChooserValue");
                    map.put("id", id);
                    map.put("chooserId", chooserId);
                    map.put("formId", formId);
                    map.put("chooserKey", value.substring(0, value.indexOf("=")));
                    map.put("chooserValue", value.substring(value.indexOf("=") + 1, value.length()));
                    GenericValue chooValue = delegator.makeValidValue("TblActChooserInfoValue", map);
                    chooValue.create();
                }
            }
        }
    }

    public static Map<String, Object> deleteActForm(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String serviceName = "deleteActForm";
        ModelService modelService = null;
        try {
            Delegator delegator = ctx.getDelegator();
            GenericValue formContent = delegator.findOne("ActExtFormContent", UtilMisc.toMap("formId", context.get("formId")), true);
            if(formContent != null){
                delegator.removeValue(formContent);
            }
            modelService = ctx.getModelService(serviceName);
            Map<String, Object> serviceContext = modelService.makeValid(context, ModelService.IN_PARAM);
            result = ctx.getDispatcher().runSync(serviceName, serviceContext);

        } catch (GenericServiceException | GenericEntityException e) {
            Debug.logError(e, ActivitiProcessUtils.class.getName());
        }
        return result;
    }


    public static Map<String, Object> startProcess(DispatchContext ctx, Map<String, ? extends Object> context){
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            RepositoryService repositoryService = processEngine.getRepositoryService();
            RuntimeService runtimeService = processEngine.getRuntimeService();
            IdentityService identityService = processEngine.getIdentityService();
            TaskService taskService = processEngine.getTaskService();
            String businessKey = (String) context.get("bizKey");
            String workflowKey = (String) context.get("workflowKey");
            String workflowVersion = (String) context.get("workflowVersion");
            if(StringUtils.isNotBlank(businessKey)){//检查该业务实例是否已经有对应的工作流启动
                List<ProcessInstance> existingProcesses = runtimeService.createProcessInstanceQuery().processDefinitionKey(workflowKey).processInstanceBusinessKey(businessKey).list();
                if(Collections3.isNotEmpty(existingProcesses)){
                    throw new RuntimeException("流程已启动，不能重复。");
                }
            }

            ProcessInstance processInstance = null;
            Map<String, Object> processVariables = new HashMap<>();
        /*processVariables.put("businessService", businessService);
        processVariables.put("statusField", statusField);*/
            String tenantId = (String) context.get("userTenantId");
            String userId = ((GenericValue)context.get("userLogin")).getString("partyId");
            identityService.setAuthenticatedUserId(userId);

            //当启动后的首个任务没有指定任务执行者时默认为启动者
            processVariables.put("workflow_last_executor", userId);

            String bizEntityName = (String) context.get("bizEntityName");
            if(UtilValidate.isNotEmpty(bizEntityName)){
                processVariables.put("bizEntityName", bizEntityName);
            }
            String bizEntityStatusField = (String) context.get("bizEntityStatusField");
            if(UtilValidate.isNotEmpty(bizEntityStatusField)){
                processVariables.put("bizEntityStatusField", bizEntityStatusField);
            }
            String bizEntityKeyField = (String) context.get("bizEntityKeyField");
            if(UtilValidate.isNotEmpty(bizEntityKeyField)){
                processVariables.put("bizEntityKeyField", bizEntityKeyField);
            }

            if(StringUtils.isNotBlank(workflowVersion)){//指定版本
                int workflowVersionInt = Integer.parseInt(workflowVersion);
                ProcessDefinition processDefinition = null;
                if (StringUtils.isNotBlank(tenantId)) {
                    processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(workflowKey).processDefinitionVersion(workflowVersionInt).processDefinitionTenantId(tenantId).singleResult();
                }
                if(processDefinition == null){//尝试公共的流程
                    processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(workflowKey).processDefinitionVersion(workflowVersionInt).processDefinitionWithoutTenantId().singleResult();
                }
                if(processDefinition != null){
                    if(StringUtils.isNotBlank(businessKey)){
                        processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), businessKey, processVariables);
                    }else{
                        processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), processVariables);
                    }
                }else{
                    throw new RuntimeException("流程[" + workflowKey +"]版本号[" + workflowVersion + "]不存在");
                }
            }else{//启动最新版本
                try {
                    if(StringUtils.isNotBlank(businessKey)) {
                        processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(workflowKey, businessKey, processVariables, tenantId);
                    }else{
                        processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(workflowKey, processVariables, tenantId);
                    }
                } catch (Exception e) {//流程为公共流程时出错
                    if(StringUtils.isNotBlank(businessKey)) {
                        processInstance = runtimeService.startProcessInstanceByKey(workflowKey, businessKey, processVariables);
                    }else{
                        processInstance = runtimeService.startProcessInstanceByKey(workflowKey, processVariables);
                    }
                }
            }
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            if (task != null) {
                result.put("taskId",task.getId());
            }else{
                result.put("taskId", null);
            }
        } catch (Exception e) {
            Debug.logError(e, "流程启动失败", ActivitiProcessUtils.class.getName());
            throw new RuntimeException("流程启动失败");
        }
        return result;
    }

    public static String searchConstantList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String enumTypeId = request.getParameter("typeId");
        List<GenericValue> typeList = EntityQuery.use(delegator).from("Enumeration").where(UtilMisc.toMap("enumTypeId",enumTypeId)).queryList();
        request.setAttribute("typeList",typeList);
        return "success";
    }

    public static Map<String, Object> searchBusinessStatus(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator =  ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userId = userLogin.get("partyId").toString();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        HistoryService historyService = processEngine.getHistoryService();
        TaskService taskService = processEngine.getTaskService();
        String businessKey = (String) context.get("bizKey");
        String formKey = "";
        String content = "";
        String formId = "";
        String taskId = "";
        String nextViewType = "";
        Map<String,Object> map = new HashMap<String,Object>();
        List<String> businessList = new ArrayList<String>();
        String workflowKey = (String) context.get("workflowKey");
        List<ProcessInstance> existingProcesses = runtimeService.createProcessInstanceQuery().processDefinitionKey(workflowKey).processInstanceBusinessKey(businessKey).list();
        if(UtilValidate.isNotEmpty(existingProcesses)){
            ProcessInstance processInstance = existingProcesses.get(0);
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            taskId = task.getId();

            List<Task> tasks = new ArrayList<Task>();
            List<Task> currentTasks = taskService.createTaskQuery().taskAssignee(userId).processDefinitionKey(workflowKey).orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
            tasks.addAll(currentTasks);
            currentTasks = taskService.createTaskQuery().taskCandidateUser(userId).processDefinitionKey(workflowKey).taskUnassigned().orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
            tasks.addAll(currentTasks);


            GenericValue staff = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", userId), true);
            if (staff != null) {
                String department = staff.getString("department");
                if (StringUtils.isNotBlank(department)) {
                    currentTasks = taskService.createTaskQuery().taskCandidateGroup(department).processDefinitionKey(workflowKey).taskUnassigned().orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
                    tasks.addAll(currentTasks);
                }
            }

            for(Task task1 : tasks){
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task1.getProcessInstanceId()).singleResult();
                String bizKey = pi.getBusinessKey();
                if(UtilValidate.isNotEmpty(bizKey)){
                    businessList.add(bizKey);
                }
            }
            map.put("businessList",UtilValidate.isNotEmpty(businessList) ? businessList : "");

            List<HistoricVariableInstance> allHistoryList = historyService.createHistoricVariableInstanceQuery().taskId(taskId).list();
            for(HistoricVariableInstance instance : allHistoryList){
                if("nextView".equals(instance.getVariableName())){
                    formId = instance.getValue().toString();
                }else if ("nextViewType".equals(instance.getVariableName())){
                    nextViewType = instance.getValue().toString();
                }
            }
            if(UtilValidate.isNotEmpty(formId)){
                GenericValue genericValue = EntityQuery.use(delegator).from("ActExtForm").where(UtilMisc.toMap("formId",formId)).queryOne();
                if(UtilValidate.isNotEmpty(genericValue)){
                    formKey = genericValue.get("formKey").toString();
                    GenericValue contentValue = EntityQuery.use(delegator).from("ActExtFormContent").where(UtilMisc.toMap("formId",formId)).queryOne();
                    content = contentValue.get("content").toString();
                }
            }
        }

        map.put("formKey",UtilValidate.isNotEmpty(formKey) ? formKey : "");
        map.put("taskId",UtilValidate.isNotEmpty(taskId) ? taskId : "");
        map.put("nextViewType",UtilValidate.isNotEmpty(nextViewType) ? nextViewType : "");
        map.put("formId",UtilValidate.isNotEmpty(formId) ? formId : "");
        map.put("formContent",UtilValidate.isNotEmpty(content) ? content : "");
        result.put("data",map);
        return result;
    }

    public static String saveHiddenChooserValue(HttpServletRequest request, HttpServletResponse response){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String msg = "保存成功";
        String processDefinitionId = request.getParameter("processDefinitionId");
        String chooserId = request.getParameter("chooserId");
        String parametersValue = request.getParameter("parametersValue");
        String sourceId = request.getParameter("sourceId");
        String targetId = request.getParameter("targetId");
        String operatorType = request.getParameter("operator");
        String id = request.getParameter("id");
        try {
            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblActExtFormChooserInfo").where(UtilMisc.toMap("id",chooserId)).queryOne();
            if(UtilValidate.isNotEmpty(genericValue)){
                String formId = genericValue.get("formId").toString();
                GenericValue chooserValue = EntityQuery.use(delegator).select().from("TblActExtFormChooserValue").where(UtilMisc.toMap("chooserId",chooserId,"fromId",sourceId,"toId",targetId,"processDefinitionId",processDefinitionId)).queryOne();
                if(UtilValidate.isNotEmpty(chooserValue) || UtilValidate.isNotEmpty(id)){
                    chooserValue = EntityQuery.use(delegator).select().from("TblActExtFormChooserValue").where(UtilMisc.toMap("id",id)).queryOne();
                    chooserValue.put("operatorType",operatorType);
                    chooserValue.put("value",parametersValue);
                    chooserValue.store();
                }else{
                    id = delegator.getNextSeqId("TblActExtFormChooserValue");
                    Map<String,Object> map = new HashMap<>();
                    getWayList(processDefinitionId,sourceId,map);
                    map.put("id",id);
                    map.put("formId",formId);
                    map.put("processDefinitionId",processDefinitionId);
                    map.put("chooserId",chooserId);
                    map.put("fromId",sourceId);
                    map.put("toId",targetId);
                    map.put("operatorType",operatorType);
                    map.put("value",parametersValue);
                    GenericValue workReport = delegator.makeValidValue("TblActExtFormChooserValue", map);
                    workReport.create();
                }
            }else{
                msg = "保存失败";
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        request.setAttribute("msg",msg);
        return "success";
    }

    public static void getWayList(String processDefinitionId,String lineId,Map<String,Object> map){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(processDefinitionId);
        List<ActivityImpl> activities = pde.getActivities();
        List<PvmTransition> pvmTransitions = new ArrayList<>();
        for(ActivityImpl activity : activities){
            pvmTransitions.addAll(activity.getOutgoingTransitions());
        }
        for (PvmTransition activity: pvmTransitions) {
            if(activity.getDestination().getId().equals(lineId)){
                map.put("sourceId", activity.getSource().getId());
                break;
            }
        }
    }


    public static String searchChooseValueList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String chooserId = request.getParameter("chooserId");
        List<GenericValue> chooserValueList = EntityQuery.use(delegator).select().from("TblActChooserInfoValue").where(UtilMisc.toMap("chooserId",chooserId)).queryList();
        request.setAttribute("chooserValueList",chooserValueList);
        return "success";
    }
}