package com.rextec.workflow;

import activiti.listener.BizStatusUpdate;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by galaxypan on 2015/1/9.
 */
public class RextecNoneStartEventActivityBehavior extends NoneStartEventActivityBehavior {
    private static Logger log = LoggerFactory.getLogger(RextecNoneStartEventActivityBehavior.class);

    private StartEvent startEvent;

    public RextecNoneStartEventActivityBehavior(StartEvent startEvent){
        this.startEvent = startEvent;
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        super.execute(execution);
        try{
            BizStatusUpdate.getInstance().updateBizStatus(execution.getProcessInstanceId(), execution.getProcessBusinessKey(), startEvent.getId());
        }catch (Exception e){
            log.debug("非状态节点", e);
        }
    }
}
