package com.rextec.workflow;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang.ArrayUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;

import java.util.*;

/**
 * Created by galaxypan on 15.10.28.
 */
public class RextecUserTaskCreateListener implements TaskListener {

    private Delegator delegator = DelegatorFactory.getDelegator(null);
    private LocalDispatcher localDispatcher = ServiceContainer.getLocalDispatcher(null, DelegatorFactory.getDelegator(null));

    private RextecUserTaskCreateListener(){

    }

    public static RextecUserTaskCreateListener instance = new RextecUserTaskCreateListener();

    @Override
    public void notify(DelegateTask delegateTask) {
        //获取activityId
        String taskActivityId = delegateTask.getTaskDefinitionKey();
        try {
            //设置任务执行人
            List<String> users = new ArrayList<>();
            List<String> groups = new ArrayList<>();

            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            TaskService taskService = processEngine.getTaskService();
            Map<String, String> candidates = (Map<String, String>) taskService.getVariable(delegateTask.getId(), "workflow_candidates");
            if(candidates != null){//上一步任务执行时用户手动选择人员后 提交
                //处理指定的人员范围
                for (String candidateType : candidates.keySet()) {
                    String candidateStr = candidates.get(candidateType);
                    List<String> candidateStrValue = Arrays.asList(candidateStr.split(","));
                    if("dept_only".equals(candidateType)){
                        groups.addAll(candidateStrValue);
                    }else if("level_only".equals(candidateType)){
                        List<String> levels = new ArrayList<>();
                        for (String s : candidateStrValue) {
                            levels.add("level-" + s);
                        }
                        groups.addAll(levels);
                    }else if("position_only".equals(candidateType)){
                        List<String> posts = new ArrayList<>();
                        for (String s : candidateStrValue) {
                            posts.add("post-" + s);
                        }
                        groups.addAll(posts);
                    }else if("user".equalsIgnoreCase(candidateType)){
                        users.addAll(candidateStrValue);
                    }else{
                        //TODO:其他类型
                    }
                }

                //应用后删除
                taskService.removeVariable(delegateTask.getId(), "workflow_candidates");//TODO:如果是多任务节点，删除后，其他节点的执行人如何处理
            }else if(UtilValidate.isEmpty(delegateTask.getAssignee()) && UtilValidate.isEmpty(delegateTask.getCandidates())){//change by pzq@20160706: 如果有其他监听器设置任务执行人则忽略统一的任务设置
                List<GenericValue> taskUsers = delegator.findByAnd("ActTaskExtUser", UtilMisc.toMap("taskId", taskActivityId, "procDefId", delegateTask.getProcessDefinitionId()), null, true);
                for (GenericValue taskUser : taskUsers) {
                    String userType = taskUser.getString("userType");
                    if("dataScope".equals(userType)){
                        List<GenericValue> userScopes = delegator.findByAnd("TblDataScope", UtilMisc.toMap("entityName", "ActTaskExtUser", "dataId", taskUser.getString("userId")), null, true);
                        for (GenericValue userScope : userScopes) {
                            String scopeType = userScope.getString("scopeType");
                            String scopeValue = userScope.getString("scopeValue");
                            if("SCOPE_DEPT_ONLY".equals(scopeType)){//部门映射成group
                                groups.add(scopeValue);
                            }else if("SCOPE_LEVEL_ONLY".equals(scopeType)){//职级映射成group
                                groups.add("level-" + scopeValue);
                            }else if("SCOPE_POSITION_ONLY".equals(scopeType)){//岗位映射成group
                                groups.add("post-" + scopeValue);
                            }else if("SCOPE_USER".equals(scopeType)){//人员
                                users.add(scopeValue);
                            }else{
                                //TODO:其他类型
                            }
                        }
                    }else{
                        //TODO:其他类型任务责任人设置
                    }
                }

                //如果该任务没有任何执行人设置
                if(users.size() == 0 && groups.size() == 0){
                    //如果还是没有执行人，任务默认到上一步任务执行人
                    String lastExecutor = (String) taskService.getVariable(delegateTask.getId(), "workflow_last_executor");
                    if (UtilValidate.isNotEmpty(lastExecutor)) {
                        users.add(lastExecutor);
                    }
                }
            }

            Iterator<String> ite = users.iterator();
            while (ite.hasNext()){
                if(UtilValidate.isEmpty(ite.next())){
                    ite.remove();
                }
            }
            ite = groups.iterator();
            while (ite.hasNext()){
                if(UtilValidate.isEmpty(ite.next())){
                    ite.remove();
                }
            }

            if(users.size() > 0){
                delegateTask.addCandidateUsers(users);//获选执行人
            }
            if(groups.size() > 0){
                delegateTask.addCandidateGroups(groups);//候选执行组
            }

            GenericValue taskForm = delegator.findOne("ActTaskExtForm", UtilMisc.toMap("taskId", taskActivityId, "procDefId", delegateTask.getProcessDefinitionId()), true);
            if(taskForm != null){
                taskForm = delegator.findOne("ActExtForm", UtilMisc.toMap("formId", taskForm.getString("formId")), true);
                delegateTask.setVariableLocal("nextViewType", taskForm.getString("formType"));
                delegateTask.setVariableLocal("nextView", "USER_SCREEN".equals(taskForm.getString("formType")) ? taskForm.getString("formId") : taskForm.getString("formUrl"));
            }
            Map<String,Object> map = new HashMap<String,Object>();
            BaiDuYunPush.setMessageData(delegateTask.getId(), map, "1",BaiDuYunPush.TASK, delegator, BaiDuYunPush.TASK_NAME, delegateTask.getName(), "工作流程");
        } catch (GenericEntityException e) {
            Debug.logError(e, "获取任务 process instance [" + delegateTask.getProcessInstanceId() + "] task [" + delegateTask.getName() + "]");
        }
    }
}
