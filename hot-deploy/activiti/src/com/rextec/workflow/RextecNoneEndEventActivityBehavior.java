package com.rextec.workflow;

import activiti.listener.BizStatusUpdate;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by galaxypan on 2015/1/9.
 */
public class RextecNoneEndEventActivityBehavior extends NoneEndEventActivityBehavior {
    private static Logger log = LoggerFactory.getLogger(RextecNoneEndEventActivityBehavior.class);

    private EndEvent endEvent;

    public RextecNoneEndEventActivityBehavior(EndEvent endEvent){
        this.endEvent = endEvent;
    }

    public EndEvent getEndEvent(){
        return endEvent;
    }
    
    @Override
    public void execute(ActivityExecution execution) throws Exception {
        super.execute(execution);
        try{
            BizStatusUpdate.getInstance().updateBizStatus(execution.getProcessInstanceId(), execution.getProcessBusinessKey(), endEvent.getId());
        }catch (Exception e){
            log.debug("非状态节点", e);
        }
    }
}
