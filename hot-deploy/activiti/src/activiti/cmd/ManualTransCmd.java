package activiti.cmd;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.TransitionImpl;

/**
 * 用户界面上手动选择需要执行的流程。
 */
public class ManualTransCmd implements Command<Object> {
    private TransitionImpl trans;
    private String processInstanceId;
    private String taskId;

    public ManualTransCmd(TransitionImpl trans, String processInstanceId, String taskId) {
        this.trans = trans;
        this.processInstanceId = processInstanceId;
        this.taskId = taskId;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);

        //完成当前任务
        TaskEntity task = commandContext
                .getTaskEntityManager()
                .findTaskById(taskId);

        task.fireEvent("complete");
        Context.getCommandContext()
                .getTaskEntityManager()
                .deleteTask(task, TaskEntity.DELETE_REASON_COMPLETED, false);

        executionEntity.removeTask(task);
//        executionEntity.signal(null, null);
        executionEntity.take(trans);
        return null;
    }
}
