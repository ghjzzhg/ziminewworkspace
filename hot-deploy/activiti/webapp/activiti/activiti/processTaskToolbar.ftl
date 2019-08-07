<div style="padding: 5px 5px">
    <a class="btn btn-info activitiPassA" href="#nowhere" onclick="$.workflow.pass('${taskId}','${formId?default("")}','${parameters.submitFormId?default('')}');" title="${passBtnLabel?default("执行")}" oriTitle="${passBtnLabel?default("执行")}">${passBtnLabel?default("执行")}</a>
    <a class="btn btn-cancel activitiRejectA" href="#nowhere" onclick="$.workflow.reject('${taskId}','${formId?default("")}','${parameters.submitFormId?default('')}');" title="驳回">驳回</a>
    <a class="btn btn-cancel activitiBackToStartA" href="#nowhere" onclick="$.workflow.backToStart('${taskId}','${formId?default("")}','${parameters.submitFormId?default('')}');" title="驳回至发起人">驳回至发起人</a>
    <a class="btn btn-danger activitiTerminateA" href="#nowhere" onclick="$.workflow.terminate('${processInstanceId}','${formId?default("")}','${parameters.submitFormId?default('')}');" title="终止">终止</a>
    <a class="btn btn-info" href="#nowhere" onclick="$.workflow.allot('${taskId}');" title="转办">转办</a>
    <#--<a class="btn btn-info" href="#nowhere" onclick="" title="前加签">前加签</a>-->
    <#--<a class="btn btn-info" href="#nowhere" onclick="" title="后加签">后加签</a>-->
    <#--<a class="btn btn-info" href="#nowhere" onclick="" title="打印">打印</a>-->
    <a class="btn btn-info" href="#nowhere" onclick="$.workflow.showProcessHistory('${processInstanceId}','${taskId}')" title="审批历史">审批历史</a>
    <a class="btn btn-info" href="#nowhere" onclick="$.workflow.graphTrace('${processDefinitionId}', '${processInstanceId}')" title="流程图">流程图</a>
</div>
