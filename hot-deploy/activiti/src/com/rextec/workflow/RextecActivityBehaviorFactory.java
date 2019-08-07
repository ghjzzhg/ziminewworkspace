package com.rextec.workflow;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RextecActivityBehaviorFactory extends DefaultActivityBehaviorFactory {
	private static Logger log = LoggerFactory.getLogger(RextecActivityBehaviorFactory.class);

	//test
	/*public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask, TaskDefinition taskDefinition) {
		log.info("change usertask Behavior : {}", userTask);
		return new CustomUserTaskActivityBehavior(taskDefinition);
	}
*/
	@Override
	public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
		return new RextecNoneStartEventActivityBehavior(startEvent);
	}

	@Override
	public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent) {
		return new RextecNoneEndEventActivityBehavior(endEvent);
	}

	// 同样可以覆盖别的方法,加入其他元素的自定义行为,参考 @see ActivityBehaviorFactory
	// 该类控制执行到某一元素时触发
}
