package com.rextec.workflow;

import activiti.listener.BizStatusUpdate;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by galaxypan on 2015/1/7.
 */
public class RextecUserTaskParseHandler extends AbstractBpmnParseHandler<UserTask> {
    private Logger log = LoggerFactory.getLogger(RextecUserTaskParseHandler.class);


    @Override
    protected Class<? extends BaseElement> getHandledType() {
        return UserTask.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
//        super.executeParse(bpmnParse, userTask);//配置为post不能重复执行父类的解析方法

        ActivityImpl activity = findActivity(bpmnParse, userTask.getId());
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        UserTaskActivityBehavior userTaskActivityBehavior = null;
        if(activityBehavior instanceof UserTaskActivityBehavior){
            userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
        }else if(activityBehavior instanceof ParallelMultiInstanceBehavior){
            ParallelMultiInstanceBehavior parallelMultiInstanceBehavior = (ParallelMultiInstanceBehavior) activityBehavior;
            AbstractBpmnActivityBehavior innerActivityBehavior = parallelMultiInstanceBehavior.getInnerActivityBehavior();
            if(innerActivityBehavior instanceof UserTaskActivityBehavior) {
                userTaskActivityBehavior = (UserTaskActivityBehavior) innerActivityBehavior;
            }
        }

        if (userTaskActivityBehavior != null) {
            userTaskActivityBehavior.getTaskDefinition().addTaskListener(TaskListener.EVENTNAME_CREATE, RextecUserTaskCreateListener.instance);
            if(true){//TODO:区分系统流程及用户流程，仅系统流程需要做状态更新配置
                userTaskActivityBehavior.getTaskDefinition().addTaskListener(TaskListener.EVENTNAME_CREATE, (TaskListener) BizStatusUpdate.getInstance());
            }
        }
    }

}
