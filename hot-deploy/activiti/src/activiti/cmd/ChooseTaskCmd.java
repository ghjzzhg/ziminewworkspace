package activiti.cmd;

import com.rextec.workflow.RextecNoneEndEventActivityBehavior;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.ofbiz.base.util.UtilValidate;

import java.util.List;
import java.util.Map;

/**
 * 选择指定任务节点跳转
 * Created by galaxypan on 16.1.29.
 */
public class ChooseTaskCmd implements Command<Void> {

    private String taskId;
    private String targetTaskId;

    private String reason;
    private Map<String, Object> variables;

    /**
     *
     * @param taskId
     * @param targetTaskId 如果未指定，默认跳转至任务开始的第一个用户任务
     * @param reason
     * @param variables
     */
    public ChooseTaskCmd(String taskId, String targetTaskId, String reason, Map<String, Object> variables) {
        this.taskId = taskId;
        this.targetTaskId = targetTaskId;
        this.reason = reason;
        this.variables = variables;
    }

    @Override
    public Void execute(CommandContext context) {
        TaskEntity currentTask = context.getTaskEntityManager().findTaskById(taskId);
        ExecutionEntity execution = context.getExecutionEntityManager().findExecutionById(currentTask.getExecutionId());
        ProcessDefinitionImpl processDefinition = execution.getProcessDefinition();
        List<ActivityImpl> activityList = processDefinition.getActivities();
        ActivityImpl curActivity = execution.getActivity();
        PvmActivity destActivity = null;
        for (ActivityImpl ai : activityList) {
            ActivityBehavior activityBehavior = ai.getActivityBehavior();
            if(UtilValidate.isEmpty(targetTaskId)){
                if(activityBehavior instanceof NoneStartEventActivityBehavior){
                    destActivity = ai.getOutgoingTransitions().get(0).getDestination();
                    break;
                }
            }else if(activityBehavior instanceof UserTaskActivityBehavior){
                UserTaskActivityBehavior userTask = (UserTaskActivityBehavior) activityBehavior;
                if(userTask.getTaskDefinition().getKey().equals(targetTaskId)){
                    destActivity = ai;
                    break;
                }
            }else if(activityBehavior instanceof RextecNoneEndEventActivityBehavior){
                RextecNoneEndEventActivityBehavior endEvent = (RextecNoneEndEventActivityBehavior) activityBehavior;
                if(endEvent.getEndEvent().getId().equals(targetTaskId)){
                    destActivity = ai;
                    break;
                }
            }
        }

        if(destActivity != null){
            List<TaskEntity> taskList = execution.getTasks();
            for (TaskEntity task : taskList) {
                if(task.getId().equals(taskId)){
                    task.setVariablesLocal(variables);
                }
                task.fireEvent(TaskListener.EVENTNAME_COMPLETE);//TODO:该任务完成后，其后续任务会被激活吗？
                context.getTaskEntityManager().deleteTask(task, reason, false);
                execution.removeTask(task);
            }
            execution.executeActivity(destActivity);
        }
        return null;
    }
}