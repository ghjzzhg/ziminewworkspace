package activiti.listener;

import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.beanutils.BeanUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by galaxypan on 2015/1/7.
 */
//@Component("bizStatusUpdate")
//@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class BizStatusUpdate implements TaskListener, ExecutionListener {
    private Logger log = LoggerFactory.getLogger(BizStatusUpdate.class);
//    @Autowired
    private RuntimeService runtimeService;
//    @Autowired
    private TaskService taskService;

    private static BizStatusUpdate instance;

    public static BizStatusUpdate getInstance(){
        if(instance == null){
            instance = new BizStatusUpdate();
        }
        return instance;
    }

    private BizStatusUpdate(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        runtimeService = processEngine.getRuntimeService();
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        updateBizStatus(delegateTask.getProcessInstanceId(), delegateTask.getExecution().getProcessBusinessKey(), delegateTask.getTaskDefinitionKey());
        //任务创建者
//        taskService.setVariable(delegateTask.getId(), "taskCreatedBy", UserUtils.getUser().getId());
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        updateBizStatus(execution.getProcessInstanceId(), execution.getProcessBusinessKey(), execution.getId());
    }

    public void updateBizStatus(String processInstanceId, String businessKey, String statusCode){
        if(statusCode != null && !statusCode.startsWith("sid-"))
        try{
            Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            String bizEntityName = (String) variables.get("bizEntityName");
            String bizEntityKeyField = (String) variables.get("bizEntityKeyField");
            String bizEntityStatusField = (String) variables.get("bizEntityStatusField");
            if(UtilValidate.isNotEmpty(businessKey) && UtilValidate.isNotEmpty(bizEntityKeyField) && UtilValidate.isNotEmpty(bizEntityName) && UtilValidate.isNotEmpty(bizEntityStatusField)){
                try {
                    Delegator delegator = DelegatorFactory.getDelegator(null);
                    GenericValue bizData = delegator.findOne(bizEntityName, UtilMisc.toMap(bizEntityKeyField, businessKey), false);
                    if(bizData != null){
                        bizData.set(bizEntityStatusField, statusCode);
                        bizData.store();
                    }
                } catch (Exception e) {
                    log.error("更新实体状态字段错误", e);
                }
            }
        }catch (Exception e){
            log.error("非状态节点", e);
        }
    }
}
